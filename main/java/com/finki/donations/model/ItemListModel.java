package com.finki.donations.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * Custom class which holds only some important properties from {@link Asset}.
 */
@Data
public class ItemListModel {
  @SerializedName("ID")
  @Expose
  private String id;

  @Expose
  private String status;

  @Expose
  private String title;

  @SerializedName("donatorName")
  @Expose
  private String donator;

  @SerializedName("Category")
  @Expose
  private String category;

  @SerializedName("SubCategory")
  @Expose
  private String subCategory;

  @Expose
  private String quantity;
}
