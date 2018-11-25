package com.finki.donations.model;

import javax.validation.constraints.NotEmpty;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * Model for storing the data for donator.
 */
@Data
public class Donator {
  @SerializedName("user-id")
  @Expose
  private String userId;

  @SerializedName("given-name")
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
