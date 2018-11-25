package com.finki.donations.model;

import javax.validation.constraints.NotEmpty;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * Model class for storing the Asset from chaincode.
 */
@Data
public class Asset {
  @SerializedName("donator")
  @Expose
  private Donator donator;

  @SerializedName("item")
  @Expose
  private Item item;
}
