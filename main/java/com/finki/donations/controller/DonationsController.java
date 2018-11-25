package com.finki.donations.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.finki.donations.model.AssetWrapper;
import com.finki.donations.service.DonationsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Rest controller for Donations.
 */
@Slf4j
@RestController
@RequestMapping(value = "/")
@RequiredArgsConstructor
public class DonationsController {

  private final DonationsService donationsService;

  /**
   * Get the document by id .
   *
   * @param id id for the certificate
   * @return ViewWrapper view model for certificate
   */
  @RequestMapping(value = "getAsset", method = RequestMethod.GET)
  public ResponseEntity getAssetById(@RequestParam String id) {
    AssetWrapper assetWrapper = donationsService.getAssetById(id);
    if (assetWrapper == null) {
      return new ResponseEntity<>("This document doesn't exist", HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(assetWrapper, HttpStatus.OK);
  }
}
