package com.finki.donations.model;

import javax.validation.constraints.NotEmpty;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * Model for wrapping the asset stored on hyperledger.
 */
@Data
public class AssetWrapper {

  @SerializedName("id")
  @Expose
  private String id;

  @SerializedName("status")
  private String status;

  @SerializedName("Asset")
  @Expose
  private Asset asset;
}
