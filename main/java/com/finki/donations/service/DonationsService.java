package com.finki.donations.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.grapebaba.hyperledger.fabric.models.ChaincodeOpPayload;
import me.grapebaba.hyperledger.fabric.models.RpcResponse;

import org.springframework.stereotype.Service;

import com.finki.donations.model.AssetWrapper;
import com.finki.donations.model.ChaincodeMethodNames;
import com.finki.donations.model.Donator;
import com.finki.donations.model.Item;
import com.finki.donations.model.ItemListModel;
import com.finki.donations.model.MethodType;
import com.finki.donations.mapper.AssetMapper;
import com.finki.donations.utils.TransactionResolver;
import com.google.gson.Gson;

/**
 * Service for communication with hyperledger.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DonationsService {

  private final TransactionResolver transactionResolver;

  /**
   * Get asset by id from hyperledger
   *
   * @param id for the asset.
   * @return {@link AssetWrapper}
   */
  public AssetWrapper getAssetById(String id) {
    log.info("Get asset by ID : {}", id);
    ChaincodeOpPayload chaincodePayload = transactionResolver
      .createChaincodePayload(MethodType.QUERY, ChaincodeMethodNames.READ, Collections.singletonList(id));
    return transactionResolver.getAssetFromChaincode(chaincodePayload);
  }

  /**
   * Create new asset wrapper.
   *
   * @param asset asset to be saved on hyperledger.
   * @return {@link AssetWrapper}
   */
  public AssetWrapper createAssetWrapper(String asset) {
    log.info("Start creating item");
    AssetWrapper assetWrapper = AssetMapper.createAssetWrapper(asset);
    ChaincodeOpPayload chaincodePayload = transactionResolver.createChaincodePayload(
      MethodType.INVOKE, ChaincodeMethodNames.INIT_ITEM, prepareArgumentsForCreatingAsset(assetWrapper));
    RpcResponse transactionInfo = transactionResolver.getTransactionInfo(chaincodePayload);
    String transactionInfoStatus = transactionInfo.getStatus();
    return "OK".equals(transactionInfoStatus) ? assetWrapper : null;
  }

  private List<String> prepareArgumentsForCreatingAsset(AssetWrapper assetWrapper){
    Item item = assetWrapper.getAsset().getItem();
    Donator donator = assetWrapper.getAsset().getDonator();
    return Arrays.asList(assetWrapper.getId(),
      item.getProductId(),
      item.getQuantity(),
      item.getCreatedOn(),
      item.getName(),
      donator.getGivenName(),
      donator.getUserId(),
      item.getCategory());
  }

  /**
   * Update {@link Donator} on hyperledger for given {@link AssetWrapper}.
   *
   * @param assetWrapper {@link AssetWrapper}
   */
  public AssetWrapper updateDonator(AssetWrapper assetWrapper) {
    log.info("Start updating donator");
    ChaincodeOpPayload chaincodePayload = transactionResolver.createChaincodePayload(
      MethodType.INVOKE, ChaincodeMethodNames.UPDATE_DONATOR, prepareArgumentsToUpdateDonator(assetWrapper));
    RpcResponse transactionInfo = transactionResolver.getTransactionInfo(chaincodePayload);
    String transactionInfoStatus = transactionInfo.getStatus();
    return "OK".equals(transactionInfoStatus) ? assetWrapper : null;
  }

  private List<String> prepareArgumentsToUpdateDonator(AssetWrapper assetWrapper){
    Donator donator = assetWrapper.getAsset().getDonator();
    return Arrays.asList(assetWrapper.getId(),
      donator.getEmail(), donator.getImage(), donator.getEmail(),
      donator.getTelephone(), donator.getGivenName());
  }

  /**
   * Update {@link AssetWrapper}.
   *
   * @param assetWrapper document to be updated.
   */
  public AssetWrapper updateItem(String assetWrapper) {
    log.info("Start updating item");
    AssetWrapper wrapper = new Gson().fromJson(assetWrapper, AssetWrapper.class);
    List<String> argumentsToUpdateItem = prepareArgumentsToUpdateItem(wrapper);

    ChaincodeOpPayload chaincodePayload = transactionResolver.createChaincodePayload(
      MethodType.INVOKE, ChaincodeMethodNames.UPDATE_ITEM, argumentsToUpdateItem);
    RpcResponse transactionInfo = transactionResolver.getTransactionInfo(chaincodePayload);
    String transactionInfoStatus = transactionInfo.getStatus();
    return "OK".equals(transactionInfoStatus) ? wrapper : null;
  }

  private List<String> prepareArgumentsToUpdateItem(AssetWrapper assetWrapper){
    Item item = assetWrapper.getAsset().getItem();
    return Arrays.asList(assetWrapper.getId(),
      item.getName(),
      item.getCategory(),
      item.getSubCategory(),
      item.getImage(),
      item.getDescription(),
      item.getQuantity());
  }

  /**
   * Gets all {@link ItemListModel}.
   *
   * @return list from {@link ItemListModel}
   */
  public List<ItemListModel> getAllItems() {
    log.info("list all items");
    ChaincodeOpPayload chaincodePayload = transactionResolver.createChaincodePayload(
      MethodType.QUERY, ChaincodeMethodNames.GET_ALL_ITEMS, Collections.emptyList());
    return transactionResolver.getListOfItemsFromChaincode(chaincodePayload);
  }

  /**
   * Gets all {@link ItemListModel}.
   *
   * @return list from {@link ItemListModel}
   */
  public List<ItemListModel> getAllItemsByDonatorId(String donatorId) {
    log.info("list all items for donator with id {}", donatorId);
    ChaincodeOpPayload chaincodePayload = transactionResolver.createChaincodePayload(MethodType.QUERY,
      ChaincodeMethodNames.GET_All_ITEMS_FOR_USER, Collections.singletonList(donatorId));
    return transactionResolver.getListOfItemsFromChaincode(chaincodePayload);
  }

  /**
   * Transfer the item. Updating the item quantity. Verifies the donation.
   *
   * @param assetId id on the asset.
   * @param donatedQuantity wanted quantity to be donated.
   * @return {@link AssetWrapper}
   */
  public RpcResponse verifyDonation(String assetId, String donatedQuantity) {
    log.info("Start the donation");
    ChaincodeOpPayload chaincodePayload = transactionResolver.createChaincodePayload(
      MethodType.INVOKE, ChaincodeMethodNames.VERIFY_DONATION, Arrays.asList(assetId, donatedQuantity));
    return transactionResolver.getTransactionInfo(chaincodePayload);
  }
}

