package com.finki.donations.model;

import lombok.Data;

@Data
public class Item {
  private String productId;
  private String name;
  private String image;
  private String createdOn;
  private String quantity;
  private String description;
  private String category;
  private String subCategory;
}
