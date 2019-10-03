package com.finki.donations.mapper;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import com.finki.donations.model.Asset;
import com.finki.donations.model.AssetForChaincode;
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
  public static AssetMapperForChaincode convertJsonToAssetWrapper(String chaincode){
     return new Gson().fromJson(chaincode, AssetMapperForChaincode.class);
  }

  public void setAssetForView(AssetMapperForChaincode assetWrapper) {
    AssetWrapper asset = new AssetWrapper();
    asset.setAsset(assetWrapper.getAsset());
    asset.setId(assetWrapper.getId());
    asset.setStatus(assetWrapper.getStatus());
    this.assetWrapper = asset;
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
  public static AssetWrapper createAssetWrapper(String asset){
    AssetForChaincode createdAsset = new Gson().fromJson(asset, AssetForChaincode.class);
    AssetWrapper assetWrapper = new AssetWrapper();
    // create specific uuid
    UUID assetId = UUID.randomUUID();
    assetWrapper.setId(assetId.toString());
    String todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    createdAsset.getItem().setCreatedOn(todayDate);
    UUID productId = UUID.randomUUID();
    createdAsset.getItem().setProductId(productId.toString());
    assetWrapper.setAsset(createdAsset.convertToAsset());
    return assetWrapper;
  }
}
