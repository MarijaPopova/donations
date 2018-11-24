package com.finki.donations.utils;

import me.grapebaba.hyperledger.fabric.ErrorResolver;
import me.grapebaba.hyperledger.fabric.Fabric;
import me.grapebaba.hyperledger.fabric.Hyperledger;
import me.grapebaba.hyperledger.fabric.models.ChaincodeID;
import me.grapebaba.hyperledger.fabric.models.ChaincodeInput;
import me.grapebaba.hyperledger.fabric.models.ChaincodeOpPayload;
import me.grapebaba.hyperledger.fabric.models.ChaincodeOpResult;
import me.grapebaba.hyperledger.fabric.models.ChaincodeSpec;
import me.grapebaba.hyperledger.fabric.models.Error;
import me.grapebaba.hyperledger.fabric.models.Transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import rx.Observable;
import rx.functions.Func1;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import com.finki.donations.config.HyperledgerConfig;
import com.finki.donations.model.AssetWrapper;
import com.finki.donations.model.ItemListModel;

/**
 * Helper class for transactions.
 */
@Component
public final class TransactionResolver {

  private static final String jsonrpc = "2.0";
  private HyperledgerConfig hyperledgerConfig;
  private Fabric fabric;

  public TransactionResolver(HyperledgerConfig hyperledgerConfig){
    this.hyperledgerConfig = hyperledgerConfig;
  };

  @PostConstruct
  public void setupFabricUrl(){
    this.fabric = Hyperledger.fabric(hyperledgerConfig.getUrl());
  };

  private static Logger logger = LoggerFactory.getLogger(TransactionResolver.class);

  /**
   * Get info for transactions.
   *
   * @param payload payload for transaction
   */
  public void getTransactionInfo(ChaincodeOpPayload payload) {
    fabric.chaincode(payload)
      .flatMap((Func1<ChaincodeOpResult, Observable<Transaction>>) chaincodeOpResult -> {
        logger.info(chaincodeOpResult.getResult().getMessage());
        try {
          TimeUnit.SECONDS.sleep(3L);
        } catch (InterruptedException e) {
          logger.error(e.getMessage());
        }
        return fabric.getTransaction(chaincodeOpResult.getResult().getMessage());
      })
      .subscribe(transaction -> logger.info("Transaction is : {}", transaction), throwable -> {
        Error error = ErrorResolver.resolve(throwable, Error.class);
        logger.error("Error : {}", error.getError());
      });
  }

  public ChaincodeOpPayload createChaincodePayload(String typeOfMethodInvocation,
    String methodName,
    Collection<String> arguments) {
    return ChaincodeOpPayload.builder()
      .jsonrpc(jsonrpc)
      .id(1)
      .method(typeOfMethodInvocation)
      .params(
        ChaincodeSpec.builder()
          .chaincodeID(
            ChaincodeID.builder()
              .name(hyperledgerConfig.getUsername())
              .build())
          .ctorMsg(
            ChaincodeInput.builder()
              .function(methodName)
              .args(arguments)
              .build())
          .secureContext(hyperledgerConfig.getEnrollSecret())
          .type(ChaincodeSpec.Type.GOLANG)
          .build())
      .build();
  }

  public AssetWrapper getAssetFromChaincode(ChaincodeOpPayload chaincodePayload) {
    AssetHelper assetHelper = new AssetHelper();
    fabric.chaincode(chaincodePayload)
      .subscribe(chaincodeOpResult -> {
        logger.info(chaincodeOpResult.toString());
        AssetWrapper assetWrapper = AssetHelper.convertJsonToAssetWrapper(
          chaincodeOpResult.getResult().getMessage()
        );
        assetHelper.setAssetWrapper(assetWrapper);

      }, throwable -> {
        Error error = ErrorResolver.resolve(throwable, Error.class);
        logger.error("Error during query function : {} ", error.getError());
      });
    return assetHelper.getAssetWrapper();
  }

  public List<ItemListModel> getListOfItemsFromChaincode(ChaincodeOpPayload chaincodePayload){
    AssetHelper assetHelper = new AssetHelper();
        fabric.chaincode(chaincodePayload)
      .subscribe(chaincodeOpResult -> {
        logger.info(chaincodeOpResult.toString());
        List<ItemListModel> itemListModels = AssetHelper.convertJsonToList(
          chaincodeOpResult
            .getResult()
            .getMessage()
        );
        assetHelper.setItemListModels(itemListModels);
      }, throwable -> {
        Error error = ErrorResolver.resolve(throwable, Error.class);
        logger.error("Error : " + error.getError());
      });
    return assetHelper.getItemListModels();
  }
}