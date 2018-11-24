package com.finki.donations.model;

import lombok.Data;

@Data
public class Donator {
  private String userId;
  private String givenName;
  private String telephone;
  private String image;
  private String email;
}
