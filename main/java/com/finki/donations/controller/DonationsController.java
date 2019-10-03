package com.finki.donations.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.finki.donations.model.Asset;
import com.finki.donations.model.AssetWrapper;
import com.finki.donations.model.Item;
import com.finki.donations.model.ItemListModel;
import com.finki.donations.service.DonationsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.grapebaba.hyperledger.fabric.models.RpcResponse;

/**
 * Rest controller for Donations.
 */
@Slf4j
@RestController
@RequestMapping(value = "/")
@RequiredArgsConstructor
public class DonationsController {

  private final DonationsService donationsService;

  /**
   * Get the asset by id .
   *
   * @param id id for the asset
   * @return ViewWrapper view model for certificate
   */
  @RequestMapping(value = "getAsset", method = RequestMethod.GET)
  public ResponseEntity getAssetById(@RequestParam String id) {
    AssetWrapper assetWrapper = donationsService.getAssetById(id);
    if (assetWrapper == null) {
      return new ResponseEntity<>("This document doesn't exist", HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(assetWrapper, HttpStatus.OK);
  }

  /**
   * Get the asset by id .
   *
   * @param id id for the asset
   * @return ViewWrapper view model for certificate
   */
  @RequestMapping(value = "getItem", method = RequestMethod.GET)
  public ResponseEntity getItemById(@RequestParam String id) {
    AssetWrapper assetWrapper = donationsService.getAssetById(id);

    if (assetWrapper == null || assetWrapper.getAsset() == null) {
      return new ResponseEntity<>("This document doesn't exist", HttpStatus.NOT_FOUND);
    }
    Item item = assetWrapper.getAsset().getItem();
    return new ResponseEntity<>(item, HttpStatus.OK);
  }

  /**
   * Get all items.
   *
   * @return list of custom list item model {@link ItemListModel}
   */
  @RequestMapping(value = "getAllItems", method = RequestMethod.GET)
  public ResponseEntity getAllItems() {
    List<ItemListModel> allItems = donationsService.getAllItems();
    return new ResponseEntity<>(allItems, HttpStatus.OK);
  }

  /**
   * Get all items.
   *
   * @return list of custom list item model {@link ItemListModel}
   */
  @RequestMapping(value = "getAllItemsForDonatorId", method = RequestMethod.GET)
  public ResponseEntity getAllItemsForUserId(@RequestParam String id) {
    List<ItemListModel> allItems = donationsService.getAllItemsByDonatorId(id);
    return new ResponseEntity<>(allItems, HttpStatus.OK);
  }

  /** Creates new Asset and stores on blockchain.
   * @param asset asset to be created.
   * @return new {@link AssetWrapper} if it's stored on blockchain, otherwise return proper http status.
   */
  @RequestMapping(value = "createAsset", method = RequestMethod.POST)
  public ResponseEntity createNewAsset(@RequestParam String asset){
    AssetWrapper assetWrapper = donationsService.createAssetWrapper(asset);
    if(assetWrapper != null) {
      return new ResponseEntity<>("it's ok", HttpStatus.OK);
    } else {
      return new ResponseEntity<>("Problem with saving the asset on blockchain", HttpStatus.SERVICE_UNAVAILABLE);
    }
  }

  /**
   * Updates donator on a current asset.
   *
   * @param assetWrapper asset that should be updated.
   * @return  updated {@link AssetWrapper} if it's stored on blockchain, otherwise return proper http status.
   */
  @RequestMapping(value = "updateDonatorForAsset", method = RequestMethod.POST)
  public ResponseEntity updateDonatorForAsset(@RequestBody AssetWrapper assetWrapper){
    AssetWrapper updatedAssetWrapper = donationsService.updateDonator(assetWrapper);
    if(assetWrapper != null) {
      return new ResponseEntity<>(updatedAssetWrapper, HttpStatus.OK);
    } else {
      return new ResponseEntity<>("Problem with saving the asset on blockchain", HttpStatus.SERVICE_UNAVAILABLE);
    }
  }

  /**
   * Updates donator on a current asset.
   *
   * @param assetWrapper asset that should be updated.
   * @return  updated {@link AssetWrapper} if it's stored on blockchain, otherwise return proper http status.
   */
  @RequestMapping(value = "updateItemForAsset", method = RequestMethod.POST)
  public ResponseEntity updateItemForAsset(@RequestParam String assetWrapper){
    AssetWrapper updatedAssetWrapper = donationsService.updateItem(assetWrapper);
    if(assetWrapper != null) {
      return new ResponseEntity<>(updatedAssetWrapper, HttpStatus.OK);
    } else {
      return new ResponseEntity<>("Problem with saving the asset on blockchain", HttpStatus.SERVICE_UNAVAILABLE);
    }
  }

  /**
   * Verify the donation.
   *
   * @param assetId id of the asset.
   * @param quantity the quantity that should be donated.
   * @return  updated {@link AssetWrapper} if it's stored on blockchain, otherwise return proper http status.
   */
  @RequestMapping(value = "verifyDonation", method = RequestMethod.POST)
  public ResponseEntity verifyDonation(@RequestParam String assetId, @RequestParam String quantity){
    RpcResponse rpcResponse = donationsService.verifyDonation(assetId, quantity);
    String status = rpcResponse.getStatus();
    if(status.equals("OK")) {
      AssetWrapper assetById = donationsService.getAssetById(assetId);
      return new ResponseEntity<>(assetById, HttpStatus.OK);
    } else {
      return new ResponseEntity<>("Invalid quantity", HttpStatus.BAD_REQUEST);
    }
  }
}
