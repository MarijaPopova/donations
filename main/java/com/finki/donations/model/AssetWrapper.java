package com.finki.donations.model;

import lombok.Data;

@Data
public class AssetWrapper {
  private String id;
  private String status;
  private Asset asset;
}
