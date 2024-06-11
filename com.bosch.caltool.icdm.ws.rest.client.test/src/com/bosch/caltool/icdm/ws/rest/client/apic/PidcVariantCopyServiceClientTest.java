/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVarPasteOutput;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantData;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author hnu1cob
 */
public class PidcVariantCopyServiceClientTest extends AbstractRestClientTest {


  /**
   * Test method for {@link PidcVariantCopyServiceClient#create(PidcVariantData)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreate() throws ApicWebServiceException {

    PidcVariantCopyServiceClient pidcVarClient = new PidcVariantCopyServiceClient();
    PidcVariantData pidcVarCreationData = new PidcVariantData();

    AttributeValue varNameAttrValue = new AttributeValue();
    varNameAttrValue.setTextValueEng("Junit_Variant_" + getRunId());
    varNameAttrValue.setDescriptionEng("Junit_Variant_" + getRunId());
    varNameAttrValue.setClearingStatus("Y");
    varNameAttrValue.setAttributeId(126L);

    pidcVarCreationData.setSrcPidcVar(new PidcVariantServiceClient().get(1508649080l));
    pidcVarCreationData.setVarNameAttrValue(varNameAttrValue);
    pidcVarCreationData.setPidcVersion(new PidcVersionServiceClient().getById(790699170L));

    PidcVarPasteOutput pidcVarData = pidcVarClient.create(pidcVarCreationData);
    PidcVariant createdVariant = pidcVarData.getPastedVariant();

    // validate
    assertNotNull("Object is not null", pidcVarData);
    assertEquals("Pidc Version is equal", new PidcVersionServiceClient().getById(790699170L),
        pidcVarData.getPidcVersion());
    assertEquals("Variant name is equal", "Junit_Variant_" + getRunId(), createdVariant.getName());

    Map<Long, PidcVariantAttribute> attrsInSrc =
        new PidcVariantAttributeServiceClient().getVarAttrsForVariant(1508649080l);
    Map<Long, PidcVariantAttribute> attrsInDest =
        new PidcVariantAttributeServiceClient().getVarAttrsForVariant(createdVariant.getId());

    assertEquals("Number of Variant Attributes are equal", attrsInSrc.size(), attrsInDest.size());

    Map<Long, Long> attrValMap = new HashMap<>();
    for (PidcVariantAttribute attr : attrsInSrc.values()) {
      attrValMap.put(attr.getAttrId(), attr.getValueId());
    }
    Map<Long, Long> attrValMapDest = new HashMap<>();
    for (PidcVariantAttribute attr : attrsInDest.values()) {
      attrValMapDest.put(attr.getAttrId(), attr.getValueId());
    }
    assertEquals("Variant Attributes are equal", attrValMap, attrValMapDest);
  }

  /**
   * Test method for {@link PidcVariantCopyServiceClient#update(PidcVariantData)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testUpdate() throws ApicWebServiceException {

    PidcVariantCopyServiceClient pidcVarClient = new PidcVariantCopyServiceClient();
    PidcVariantData pidcVarCreationData = new PidcVariantData();
    pidcVarCreationData.setSrcPidcVar(new PidcVariantServiceClient().get(1027853967L));
    pidcVarCreationData.setPidcVersion(new PidcVersionServiceClient().getById(790699170L));
    pidcVarCreationData.setDestPidcVar(new PidcVariantServiceClient().get(1508649080L));

    PidcVarPasteOutput pidcVarData = pidcVarClient.update(pidcVarCreationData);
    // validate
    assertNotNull("Object is not null", pidcVarData);
    assertEquals("Pidc Version is equal", new PidcVersionServiceClient().getById(790699170L),
        pidcVarData.getPidcVersion());
    assertEquals("Variant name is equal", "V1_EU6A_Copy", pidcVarData.getPastedVariant().getName());

    Map<Long, PidcVariantAttribute> attrsInSrc =
        new PidcVariantAttributeServiceClient().getVarAttrsForVariant(1027853967L);
    Map<Long, PidcVariantAttribute> attrsInDest =
        new PidcVariantAttributeServiceClient().getVarAttrsForVariant(1508649080L);

    assertEquals("Number of Variant Attributes are equal", attrsInSrc.size(), attrsInDest.size());

    List<PidcVariantAttribute> valAlreadyExists = pidcVarData.getValAlreadyExists();
    List<Long> attrList = new ArrayList<Long>();
    for (PidcVariantAttribute attr : valAlreadyExists) {
      attrList.add(attr.getAttrId());
    }

    Map<Long, Long> attrValToUpdate = new HashMap<>();
    for (PidcVariantAttribute attr : attrsInSrc.values()) {
      if (!attrList.contains(attr.getAttrId())) {
        attrValToUpdate.put(attr.getAttrId(), attr.getValueId());
      }
    }
    Map<Long, Long> attrValUpdated = new HashMap<>();
    for (PidcVariantAttribute attr : attrsInDest.values()) {
      if (!attrList.contains(attr.getAttrId())) {
        attrValUpdated.put(attr.getAttrId(), attr.getValueId());
      }
    }
    assertEquals("Variant Attributes are equal", attrValToUpdate, attrValUpdated);
  }

}
