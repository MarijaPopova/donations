package com.finki.donations.mapper;

import com.finki.donations.model.Asset;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class AssetMapperForChaincode {
  @SerializedName("id")
  @Expose
  private String id;

  @SerializedName("status")
  private String status;

  @SerializedName("Asset")
  @Expose
  private Asset asset;
}
