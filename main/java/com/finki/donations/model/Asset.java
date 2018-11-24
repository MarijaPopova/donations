package com.finki.donations.model;

import lombok.Data;

@Data
public class Asset {
  private Item item;
  private Donator donator;
}
