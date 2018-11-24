package com.finki.donations.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "hyperledger")
@PropertySource("classpath:application.properties")
public class HyperledgerConfig {
   private String url;
   private String chaincode;
   private String username;
   private String enrollSecret;
}
