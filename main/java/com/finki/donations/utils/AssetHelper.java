package com.finki.donations.utils;

import java.lang.reflect.Type;
import java.util.List;

import com.finki.donations.model.AssetWrapper;
import com.finki.donations.model.ItemListModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import lombok.Data;

@Data
public class AssetHelper {
   private AssetWrapper assetWrapper;
   private List<ItemListModel> itemListModels;

  public static AssetWrapper convertJsonToAssetWrapper(String chaincode){
     return new Gson().fromJson(chaincode, AssetWrapper.class);
  };

  public static List<ItemListModel> convertJsonToList(String chaincode){
     Type listType = new TypeToken<List<ItemListModel>>() { }.getType();
     return new Gson().fromJson(chaincode, listType);
  }
}
