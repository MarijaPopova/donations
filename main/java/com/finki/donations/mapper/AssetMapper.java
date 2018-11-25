package com.finki.donations.mapper;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import com.finki.donations.model.Asset;
import com.finki.donations.model.AssetWrapper;
import com.finki.donations.model.ItemListModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import lombok.Data;

/**
 *
 */
@Data
public class AssetMapper {
   private AssetWrapper assetWrapper;
   private List<ItemListModel> itemListModels;

  /**
   * Converts chaincode to pojo {@link AssetWrapper}.
   * @param chaincode which has to be converted
   * @return {@link AssetWrapper}
   */
  public static AssetWrapper convertJsonToAssetWrapper(String chaincode){
     return new Gson().fromJson(chaincode, AssetWrapper.class);
  }

  /**
   * Converts chaincode to pojo {@link ItemListModel}.
   * @param chaincode which has to be converted
   * @return {@link AssetWrapper}
   */
  public static List<ItemListModel> convertJsonToList(String chaincode){
     Type listType = new TypeToken<List<ItemListModel>>() { }.getType();
     return new Gson().fromJson(chaincode, listType);
  }

  /**
   * Creates {@link AssetWrapper} to store on hyperledger
   * @param asset asset that should be saved
   * @return {@link AssetWrapper which is saved}
   */
  public static AssetWrapper createAssetWrapper(Asset asset){
    AssetWrapper assetWrapper = new AssetWrapper();
    // create specific uuid
    UUID assetId = UUID.randomUUID();
    assetWrapper.setId(assetId.toString());
    String todayDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    asset.getItem().setCreatedOn(todayDate);
    assetWrapper.setAsset(asset);
    return assetWrapper;
  }
}
