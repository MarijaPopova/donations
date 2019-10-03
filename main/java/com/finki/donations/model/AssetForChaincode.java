package com.finki.donations.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * Model class for storing the Asset from chaincode.
 */
@Data
public class AssetForChaincode {
  @SerializedName("donator")
  @Expose
  private DonatorForChaincode donator;

  @SerializedName("item")
  @Expose
  private Item item;

  public Asset convertToAsset(){
    Asset asset = new Asset();
    asset.setItem(this.item);
    Donator donator = new Donator();
    String userId = this.donator.getUserId();
    donator.setUserId(userId);
    String givenName = this.donator.getGivenName();
    donator.setGivenName(givenName);
    String email = this.donator.getEmail();
    donator.setEmail(email);
    String telephone = this.donator.getTelephone();
    donator.setTelephone(telephone);
    String image = this.donator.getImage();
    donator.setImage(image);
    asset.setDonator(donator);

    return asset;
  }
}
