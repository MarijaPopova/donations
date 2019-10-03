package com.finki.donations.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * Model for storing the data for donator.
 */
@Data
public class DonatorForChaincode {
  @SerializedName("userId")
  @Expose
  private String userId;

  @SerializedName("givenName")
  @Expose
  private String givenName;

  @SerializedName("telephone")
  @Expose
  private String telephone;

  @SerializedName("image")
  @Expose
  private String image;

  @SerializedName("email")
  @Expose
  private String email;
}
