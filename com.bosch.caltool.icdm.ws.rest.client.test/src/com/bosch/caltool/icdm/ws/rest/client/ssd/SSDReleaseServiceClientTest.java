/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.ssd;

import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.bosch.caltool.icdm.model.ssd.FeatureValueICDMModel;
import com.bosch.caltool.icdm.model.ssd.SSDFeatureICDMAttrModel;
import com.bosch.caltool.icdm.model.ssd.SSDReleaseIcdmModel;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dmr1cob
 */
public class SSDReleaseServiceClientTest extends AbstractRestClientTest {

  /**
   *
   */
  private static final long SSD_SOFTWARE_VERSION_ID = 284163L;

  /**
   * Test method for {@link com.bosch.caltool.icdm.ws.rest.client.ssd.SSDReleaseServiceClient#getSSDReleaseList(long)}
   */
  @Test
  public void testGetSSDReleaseList() {
    SSDReleaseServiceClient ssdReleaseServiceClient = new SSDReleaseServiceClient();
    try {
      List<SSDReleaseIcdmModel> ssdReleaseList = ssdReleaseServiceClient.getSSDReleaseList(SSD_SOFTWARE_VERSION_ID);
      assertNotNull("SSD releases list should not be  null", ssdReleaseList);
      testoutputssdRelease(ssdReleaseList);
    }
    catch (ApicWebServiceException e) {
      e.printStackTrace();
    }
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.ws.rest.client.ssd.SSDReleaseServiceClient#getSSDFeatureICDMAttrModelList(List)}
   *
   * @throws ApicWebServiceException Exception
   */
  @Test
  public void testGetSSDFeatureICDMAttrModelList() throws ApicWebServiceException {
    SSDReleaseServiceClient ssdReleaseServiceClient = new SSDReleaseServiceClient();
    FeatureValueICDMModel featureValueICDMModelOne = new FeatureValueICDMModel();
    featureValueICDMModelOne.setFeatureId(new BigDecimal(10003));
    featureValueICDMModelOne.setFeatureText("AFS (Hotfilm air mass sensor)");
    featureValueICDMModelOne.setValueId(new BigDecimal(2935));
    featureValueICDMModelOne.setValueText("Bosch digital sensor");
    FeatureValueICDMModel featureValueICDMModelTwo = new FeatureValueICDMModel();
    featureValueICDMModelTwo.setFeatureId(new BigDecimal(10004));
    featureValueICDMModelTwo.setFeatureText("APP (Accelerator pedal)");
    featureValueICDMModelTwo.setValueId(new BigDecimal(2001));
    featureValueICDMModelTwo.setValueText("PQ35_EDC17");
    List<FeatureValueICDMModel> dependencyList = new ArrayList<FeatureValueICDMModel>();
    dependencyList.add(featureValueICDMModelOne);
    dependencyList.add(featureValueICDMModelTwo);

    List<SSDFeatureICDMAttrModel> ssdFeatureICDMAttrModel =
        ssdReleaseServiceClient.getSSDFeatureICDMAttrModelList(dependencyList);
    assertNotNull("SSDFeatureICDMAttrModel list should not be  null", ssdFeatureICDMAttrModel);
    testoutputSSDFeatureICDMAttrModel(ssdFeatureICDMAttrModel);
  }


  /**
   * @param ssdReleaseList
   */
  private void testoutputssdRelease(final List<SSDReleaseIcdmModel> ssdReleaseList) {
    assertNotNull("SSD releases list should not be  null", ssdReleaseList);

    for (SSDReleaseIcdmModel ssdRelease : ssdReleaseList) {
      assertNotNull("SSD releases cannot be null", ssdRelease.getRelease());
      assertNotNull("SSD releases date cannot be null", ssdRelease.getReleaseDate());
      assertNotNull("SSD releases descrption  cannot be null", ssdRelease.getReleaseDesc());
      assertNotNull("SSD releases ID  cannot be null", ssdRelease.getReleaseId());
    }

  }

  /**
   * @param SSDFeatureICDMAttrModel List
   */
  private void testoutputSSDFeatureICDMAttrModel(final List<SSDFeatureICDMAttrModel> ssdFeatureICDMAttrModelList) {
    assertNotNull("SSDFeatureICDMAttrModel list should not be  null", ssdFeatureICDMAttrModelList);

    for (SSDFeatureICDMAttrModel ssdFeatureICDMAttrModel : ssdFeatureICDMAttrModelList) {
      assertNotNull("Attribute Model cannot be null", ssdFeatureICDMAttrModel.getAttrValModel());
      assertNotNull("Feature Value Model cannot be null", ssdFeatureICDMAttrModel.getFeaValModel());
    }

  }
}
