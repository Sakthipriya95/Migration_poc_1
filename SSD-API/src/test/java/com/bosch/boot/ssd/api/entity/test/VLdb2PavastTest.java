/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.entity.test;


import java.math.BigDecimal;
import java.util.Date;

/**
 * @author TUD1COB
 */
import org.junit.Assert;
import org.junit.Test;

import com.bosch.boot.ssd.api.entity.VLdb2Pavast;

/**
 *
 */
public class VLdb2PavastTest {

  /**
   * Test the setters and Getters
   */
  @Test
  public void testSettersAndGetters() {
    // Create an instance of VLdb2Pavast
    VLdb2Pavast vLdb2Pavast = new VLdb2Pavast();

    // Set values using setters
    BigDecimal labId = BigDecimal.valueOf(1);
    vLdb2Pavast.setLabId(labId);

    String label = "Test Label";
    vLdb2Pavast.setLabel(label);

    String typ = "Test Type";
    vLdb2Pavast.setTyp(typ);

    String subtyp = "Test Subtype";
    vLdb2Pavast.setSubtyp(subtyp);

    String category = "Test Category";
    vLdb2Pavast.setCategory(category);

    String paramterType = "Test Parameter Type";
    vLdb2Pavast.setParamterType(paramterType);

    String function = "Test Function";
    vLdb2Pavast.setFunction(function);

    String ssdClass = "Test SSD Class";
    vLdb2Pavast.setSsdClass(ssdClass);

    String edc16Label = "Test EDC16 Label";
    vLdb2Pavast.setEdc16Label(edc16Label);

    String fileName = "Test File Name";
    vLdb2Pavast.setFileName(fileName);

    String remarks = "Test Remarks";
    vLdb2Pavast.setRemarks(remarks);

    String longnameE = "Test Longname E";
    vLdb2Pavast.setLongnameE(longnameE);

    String longnameF = "Test Longname F";
    vLdb2Pavast.setLongnameF(longnameF);

    String longnameG = "Test Longname G";
    vLdb2Pavast.setLongnameG(longnameG);

    String longnameI = "Test Longname I";
    vLdb2Pavast.setLongnameI(longnameI);

    String active = "Test Active";
    vLdb2Pavast.setActive(active);

    String newEdc17Label = "Test New EDC17 Label";
    vLdb2Pavast.setNewEdc17Label(newEdc17Label);

    String oldEdc17Label = "Test Old EDC17 Label";
    vLdb2Pavast.setOldEdc17Label(oldEdc17Label);

    String creDate = "Test Creation Date";
    vLdb2Pavast.setCreDate(creDate);

    String upperLabel = "Test Upper Label";
    vLdb2Pavast.setUpperLabel(upperLabel);

    Date modClassDate = new Date();
    vLdb2Pavast.setModClassDate(modClassDate);

    String revDescr = "Test Revision Description";
    vLdb2Pavast.setRevDescr(revDescr);

    String state = "Test State";
    vLdb2Pavast.setState(state);

    BigDecimal refId = BigDecimal.valueOf(2);
    vLdb2Pavast.setRefId(refId);

    String dgsLabel = "Test DGS Label";
    vLdb2Pavast.setDgsLabel(dgsLabel);

    // Test getters to verify if values are correctly set
    Assert.assertEquals(labId, vLdb2Pavast.getLabId());
    Assert.assertEquals(label, vLdb2Pavast.getLabel());
    Assert.assertEquals(typ, vLdb2Pavast.getTyp());
    Assert.assertEquals(subtyp, vLdb2Pavast.getSubtyp());
    Assert.assertEquals(category, vLdb2Pavast.getCategory());
    Assert.assertEquals(paramterType, vLdb2Pavast.getParamterType());
    Assert.assertEquals(function, vLdb2Pavast.getFunction());
    Assert.assertEquals(ssdClass, vLdb2Pavast.getSsdClass());
    Assert.assertEquals(edc16Label, vLdb2Pavast.getEdc16Label());
    Assert.assertEquals(fileName, vLdb2Pavast.getFileName());
    Assert.assertEquals(remarks, vLdb2Pavast.getRemarks());
    Assert.assertEquals(longnameE, vLdb2Pavast.getLongnameE());
    Assert.assertEquals(longnameF, vLdb2Pavast.getLongnameF());
    Assert.assertEquals(longnameG, vLdb2Pavast.getLongnameG());
    Assert.assertEquals(longnameI, vLdb2Pavast.getLongnameI());
    Assert.assertEquals(active, vLdb2Pavast.getActive());
    Assert.assertEquals(newEdc17Label, vLdb2Pavast.getNewEdc17Label());
    Assert.assertEquals(oldEdc17Label, vLdb2Pavast.getOldEdc17Label());
    Assert.assertEquals(creDate, vLdb2Pavast.getCreDate());
    Assert.assertEquals(upperLabel, vLdb2Pavast.getUpperLabel());
    Assert.assertEquals(modClassDate, vLdb2Pavast.getModClassDate());
    Assert.assertEquals(revDescr, vLdb2Pavast.getRevDescr());
    Assert.assertEquals(state, vLdb2Pavast.getState());
    Assert.assertEquals(refId, vLdb2Pavast.getRefId());
    Assert.assertEquals(dgsLabel, vLdb2Pavast.getDgsLabel());
  }
}
