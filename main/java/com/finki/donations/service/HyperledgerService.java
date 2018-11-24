package com.finki.donations.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import me.grapebaba.hyperledger.fabric.models.ChaincodeOpPayload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.finki.donations.model.Asset;
import com.finki.donations.model.AssetWrapper;
import com.finki.donations.model.ChaincodeMethodNames;
import com.finki.donations.model.Donator;
import com.finki.donations.model.ItemListModel;
import com.finki.donations.model.MethodType;
import com.finki.donations.utils.AssetHelper;
import com.finki.donations.utils.TransactionResolver;

import static java.util.Arrays.asList;

/**
 * Service for communication with hyperledger.
 */
@Service
@RequiredArgsConstructor
public class HyperledgerService {

  private final Logger logger = LoggerFactory.getLogger(HyperledgerService.class);
  private final TransactionResolver transactionResolver;

  /**
   * Get asset by id from hyperledger
   *
   * @param id for the asset.
   * @return {@link AssetWrapper}
   */
  public AssetWrapper getAssetById(String id) {
    logger.info("Get asset by ID : {}", id);
    final AssetHelper assetHelper = new AssetHelper();
    ChaincodeOpPayload chaincodePayload = transactionResolver
      .createChaincodePayload(MethodType.QUERY, ChaincodeMethodNames.READ, Collections.singletonList(id));
    transactionResolver.getAssetFromChaincode(chaincodePayload);
    return assetHelper.getAssetWrapper();
  }

  /**
   * Create new asset wrapper.
   *
   * @param asset asset to be saved on hyperledger.
   * @return {@link AssetWrapper}
   */
  public AssetWrapper createAssetWrapper(Asset asset) {
    logger.info("Start creating item");
    AssetWrapper assetWrapper = new AssetWrapper();
    assetWrapper.setAsset(asset);
    UUID assetId = UUID.randomUUID();
    assetWrapper.setId(assetId.toString());
    asset.getItem().setCreatedOn(LocalDateTime.now().toString());
    ChaincodeOpPayload chaincodePayload = transactionResolver.createChaincodePayload(
      MethodType.INVOKE, ChaincodeMethodNames.INIT_ITEM, Collections.singletonList(assetId.toString()));
    transactionResolver.getTransactionInfo(chaincodePayload);
    return assetWrapper;
  }

  public void updateDonator(AssetWrapper assetWrapper) {
    logger.info("Start updating donator");
    Donator donator = assetWrapper.getAsset().getDonator();
    ChaincodeOpPayload chaincodePayload = transactionResolver.createChaincodePayload(MethodType.INVOKE,
      ChaincodeMethodNames.UPDATE_DONATOR, asList(assetWrapper.getId(),
        donator.getEmail(), donator.getImage(), donator.getEmail(),
        donator.getTelephone(), donator.getGivenName()));
    transactionResolver.getTransactionInfo(chaincodePayload);
  }

  /**
   * Update {@link AssetWrapper}.
   *
   * @param assetWrapper document to be updated.
   */
  public void updateAssetWrapper(AssetWrapper assetWrapper) {
  }

  /**
   * List all {@link ItemListModel}.
   *
   * @return list from {@link ItemListModel}
   */
  public List<ItemListModel> listAllItems() {

    logger.info("list all items");
    final AssetHelper documentHelper = new AssetHelper();
    ChaincodeOpPayload chaincodePayload = transactionResolver.createChaincodePayload(MethodType.QUERY,
      ChaincodeMethodNames.GET_ALL_ITEMS, Collections.emptyList());
     return transactionResolver.getListOfItemsFromChaincode(chaincodePayload);
  }
  /**
   * Donation
   *
   * @param assetWrapper asset to be donated.
   * @return {@link AssetWrapper}
   */
  public AssetWrapper donation(AssetWrapper assetWrapper) {
    logger.info("Start the donation");
    final AssetHelper assetHelper = new AssetHelper();
    assetHelper.setAssetWrapper(assetWrapper);
    ChaincodeOpPayload chaincodePayload = transactionResolver.createChaincodePayload(
      MethodType.INVOKE, ChaincodeMethodNames.VERIFY_DONATION, asList(assetWrapper.getId(), assetWrapper.getAsset().getItem().getQuantity()));
    transactionResolver.getTransactionInfo(chaincodePayload);
    return assetHelper.getAssetWrapper();
  }
}

