/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.bosch.caltool.apic.ws.client.APICStub.Attribute;
import com.bosch.caltool.apic.ws.client.APICStub.AttributeValue;
import com.bosch.caltool.apic.ws.client.APICStub.AttributeWithValueType;
import com.bosch.caltool.apic.ws.client.APICStub.GetPidcFavouritesResponse;
import com.bosch.caltool.apic.ws.client.APICStub.LevelAttrInfo;
import com.bosch.caltool.apic.ws.client.APICStub.PidcActiveVersionResponseType;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardAllVersInfoType;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardInfoType;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardType;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardVariantType;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardWithVersion;
import com.bosch.caltool.apic.ws.client.output.AbstractStringOutput;
import com.bosch.caltool.apic.ws.client.output.StringPidcOutput;


public class TestPidc extends AbstractSoapClientTest {

  /**
   * PIDC : X_Test_002_P866_EA288
   */
  private static final long DETAIL_PIDC_ID = 2747L;

  private final APICWebServiceClient stub = new APICWebServiceClient();

  @Test
  public void testAllPidc() throws Exception {

    ProjectIdCardInfoType[] allPidc = this.stub.getAllPidc();

    assertNotNull(allPidc);
    assertFalse("AllPidc:Response not empty", allPidc.length == 0);

    LOG.info("Number of Project id card in the db {}", allPidc.length);


    AbstractStringOutput output = new StringPidcOutput(allPidc);
    output.setNoOfRowsToInclude(5);
    showOutput(output);

  }


  /**
   * @throws Exception
   */
  @Test
  public void testAllPidcVersions() throws Exception {
    ProjectIdCardAllVersInfoType[] allPidcVersion = this.stub.getAllPidcVersion();

    assertNotNull(allPidcVersion);
    assertFalse("getAllPidcVersion:Response not empty", allPidcVersion.length == 0);

    LOG.info("Number of Project id versions in the db {}", allPidcVersion.length);
  }


  @Test
  public void testSpecficPidc() throws Exception {

    ProjectIdCardType pidcDetails = getPidcDetails();
    LOG.info("Pidc name {}", pidcDetails.getProjectIdCardDetails().getName());

    LOG.info("Pidc attributes Size {}", pidcDetails.getAttributes().length);
    assertTrue(pidcDetails.getAttributes().length > 0);

  }


  /**
   * @return
   * @throws Exception
   */
  private ProjectIdCardType getPidcDetails() throws Exception {
    ProjectIdCardInfoType[] allPidc = this.stub.getAllPidc();

    assertNotNull("Response not null", allPidc);
    assertFalse("allPidc:Response not empty", allPidc.length == 0);

    ProjectIdCardType pidcDetails = this.stub.getPidcDetails(allPidc[0].getId());

    assertNotNull(pidcDetails);
    return pidcDetails;
  }


  @Test
  public void testPidcAttributes() throws Exception {
    ProjectIdCardType pidcDetails = getPidcDetails();

    assertNotNull(pidcDetails);
    assertNotNull(pidcDetails.getAttributes());

    int counter = 0;
    for (AttributeWithValueType attrValType : pidcDetails.getAttributes()) {
      if (counter >= 5) {
        break;
      }
      LOG.info("Attribute name:{}", attrValType.getAttribute().getNameE());
      counter++;
    }
  }

  @Test
  public void testLevelAttrs() throws Exception {
    ProjectIdCardType pidcDetails = getPidcDetails();

    assertNotNull(pidcDetails);
    assertNotNull(pidcDetails.getProjectIdCardDetails());
    assertNotNull(pidcDetails.getProjectIdCardDetails().getLevelAttrInfoList());

    for (LevelAttrInfo levelAttr : pidcDetails.getProjectIdCardDetails().getLevelAttrInfoList()) {
      LOG.info("level attr id :{}", levelAttr.getLevelAttrId());
      LOG.info(levelAttr.getLevelName());
    }
  }

  /**
   * @param allPidc
   * @throws Exception
   */
  @Test
  public void testSpecificPidcVersion() throws Exception {

    // ProjectIdCardAllVersInfoType[] allPidcVersion = this.stub.getAllPidcVersion();

    // assertNotNull(allPidcVersion);

    // if (allPidcVersion.length == 0) {
    // throw new Exception("No Pidc version present");

    // }
    // LOG.info("Number of Project id versions in the db {}" , allPidcVersion.length);
    // PidcVersionType[] pidcVersions = allPidcVersion[0].getPidcVersions();

    ProjectIdCardWithVersion pidcForVersion = this.stub.getPidcForVersion(774418071l);

    assertNotNull(pidcForVersion);

    LOG.info("Project id card name {}", pidcForVersion.getProjectIdCardDetails().getName());
  }


  @Test
  public void activeid() throws Exception {

    ProjectIdCardInfoType[] allPidc = this.stub.getAllPidc();
    PidcActiveVersionResponseType pidcActiveVersionId = this.stub.getPidcActiveVersionId(allPidc[0].getId());

    assertNotNull("Response not null", pidcActiveVersionId);
    assertFalse("Get active Project id cards failure", (pidcActiveVersionId.getPidcVersionId() == 0L));

    LOG.info("\nactive id : {}", pidcActiveVersionId.getPidcVersionId());

  }


  /**
   * Comprehensive retrieval of a pidc
   *
   * @throws java.lang.Exception error from service
   */
  @Test
  public void testGetProjIdCard() throws java.lang.Exception {

    StringBuilder logInfo;

    LOG.info("---------------------- ALL Project Id card details ------------------------");
    ProjectIdCardType response = this.stub.getPidcDetails(DETAIL_PIDC_ID);
    assertNotNull("PIDC reponse not null", response);

    LOG.info("------------------------ Project Id card details --------------------------");
    String pidcName = response.getProjectIdCardDetails().getName();
    if ((pidcName != null) && !pidcName.equals("")) {
      LOG.info("Project Id card name: {}", pidcName);
      LOG.info("Project Id card Id: {}", response.getProjectIdCardDetails().getId());
      LOG.info("Project Id card created date: {}",
          response.getProjectIdCardDetails().getCreateDate().getTime().toString());
      LOG.info("Project Id card is deleted : {}", response.getProjectIdCardDetails().getIsDeleted());
      // Get project id card modified date
      if (response.getProjectIdCardDetails().getModifyDate() != null) {
        LOG.info("Project Id card modified date: {}",
            response.getProjectIdCardDetails().getModifyDate().getTime().toString());
      }
      else {
        LOG.info("No modification on this project id card");
      }
    }
    else {
      LOG.info("no project Id card details found");
    }
    LOG.info("-------------------- End of project Id card details --------------------");

    LOG.info("------------------------Project Id card attributes details------------------------");
    if (response.isAttributesSpecified()) {
      LOG.info("{} project id card attributes found", response.getAttributes().length);
      AttributeWithValueType[] projIdCardAttrs = response.getAttributes();
      for (AttributeWithValueType attributeWithValueType : projIdCardAttrs) {
        Attribute projIdCardAttr = attributeWithValueType.getAttribute();

        logInfo = new StringBuilder();
        logInfo.append(" Attribute ").append(projIdCardAttr.getNameE()).append("(used: ")
            .append(attributeWithValueType.getUsed()).append(")");
        if (attributeWithValueType.isValueSpecified()) {
          AttributeValue projIdCardAttrVal = attributeWithValueType.getValue();
          logInfo.append(" = ").append(projIdCardAttrVal.getValueE());
        }
        else {
          if (attributeWithValueType.getIsVariant()) {
            logInfo.append(" = <VARIANT>");
          }
          else {
            logInfo.append(" = <NOT SET>");
          }
        }
        LOG.info(logInfo.toString());
      }
    }
    else {
      LOG.info("no project id card attributes found");
    }
    LOG.info("---------------------End of project Id card attributes details---------------------");

    LOG.info("---------------------Project Id card variant details---------------------");
    if (response.isVariantsSpecified()) {

      LOG.info(response.getVariants().length + " variants found");

      // Get all project id card variants
      ProjectIdCardVariantType[] variants = response.getVariants();

      for (ProjectIdCardVariantType projectIdCardVariantType : variants) {
        AttributeWithValueType[] variantAttrs = projectIdCardVariantType.getAttributes();

        LOG.info("variant:    {}", projectIdCardVariantType.getPIdCVariant().getName());
        LOG.info("variant ID: {}", projectIdCardVariantType.getPIdCVariant().getId());
        LOG.info("version:    {}", projectIdCardVariantType.getPIdCVariant().getVersionNumber());
        LOG.info("is deleted: {}", projectIdCardVariantType.getPIdCVariant().getIsDeleted());
        LOG.info("created by: {}", projectIdCardVariantType.getPIdCVariant().getCreateUser());
        LOG.info("created at: {}", projectIdCardVariantType.getPIdCVariant().getCreateDate().getTime().toString());

        for (AttributeWithValueType attributeWithValueType : variantAttrs) {
          Attribute variantAttr = attributeWithValueType.getAttribute();

          logInfo = new StringBuilder();

          logInfo.append(" Attribute ").append(variantAttr.getNameE()).append("(used: ")
              .append(attributeWithValueType.getUsed()).append(")");
          if (attributeWithValueType.isValueSpecified()) {
            AttributeValue attrVal = attributeWithValueType.getValue();
            logInfo.append(" = ").append(attrVal.getValueE());
          }
          else {
            if (attributeWithValueType.getIsVariant()) {
              logInfo.append(" = <SUB-VARIANT>");
            }
            else {
              logInfo.append(" = <NOT SET>");
            }
          }
          LOG.info(logInfo.toString());
        }
      }
    }
    else {
      LOG.info("no variants found");
    }
    LOG.info("---------------------End of project Id card variant details---------------------");

    assertNotNull(response);

  }

  /**
   * Get Pidc Favourites detail for iCDM user
   *
   * @throws Exception in ws call
   */
  @Test
  public void testGetPidcFavouritesiCDMUser() throws Exception {
    LOG.info("---------------------- Get Pidc Favourites for iCDM user------------------------");
    GetPidcFavouritesResponse pidcFavourites = this.stub.getPidcFavourites("BNE4COB");
    assertNotNull("Response not null", pidcFavourites);
  }

  /**
   * Pidc favourites details for non iCDM user is empty
   *
   * @throws Exception in ws call
   */
  @Test
  public void testGetPidcFavouritesNoniCDMUser() throws Exception {
    LOG.info("---------------------- Get Pidc Favourites for non iCDM user------------------------");
    GetPidcFavouritesResponse pidcFavourites = this.stub.getPidcFavourites("REL3SI");
    assertNotNull("Response not null", pidcFavourites);
    assertNull("Response will be empty for non iCDM user", pidcFavourites.getPidcFavouriteResponse());
  }

}
