package com.finki.donations.model;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * Custom class which holds only some important properties from {@link Asset}.
 */
@Data
public class ItemListModel {
  @Expose
  private String id;
  @Expose
  private String status;
  @Expose
  private String title;
  @Expose
  private String donator;
  @Expose
  private String category;
  @Expose
  private String subCategory;
}
