package com.finki.donations.model;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Item {

  @SerializedName("product-id")
  @Expose
  private String productId;

  @SerializedName("name")
  @Expose
  private String name;

  @SerializedName("image")
  @Expose
  private String image;

  @SerializedName("created-on")
  @Expose
  private String createdOn;

  @SerializedName("quantity")
  @Expose
  private String quantity;

  @SerializedName("description")
  @Expose
  private String description;

  @SerializedName("category")
  @Expose
  private String category;

  @SerializedName("sub-category")
  @Expose
  private String subCategory;
}
