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
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVarPasteOutput;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantData;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author ukt1cob
 */
public class PidcVarNSubVarCopyServiceClientTest extends AbstractRestClientTest {


  /**
   * Test method for {@link PidcVarNSubVarCopyServiceClient#create(PidcVariantData)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreate() throws ApicWebServiceException {

    PidcVarNSubVarCopyServiceClient pidcVarNSubVarCopyServiceClient = new PidcVarNSubVarCopyServiceClient();
    PidcVariantData pidcVarData = new PidcVariantData();

    AttributeValue varNameAttrValue = new AttributeValue();
    String variantName = "PidcVarNSubVarCopy_Junit_Var_" + getRunId();
    varNameAttrValue.setTextValueEng(variantName);
    varNameAttrValue.setDescriptionEng(variantName);
    varNameAttrValue.setClearingStatus("Y");
    varNameAttrValue.setAttributeId(126L);

    pidcVarData.setSrcPidcVar(new PidcVariantServiceClient().get(1508649080l));
    pidcVarData.setPidcVersion(new PidcVersionServiceClient().getById(790699170L));
    pidcVarData.setVarNameAttrValue(varNameAttrValue);

    // subvarset
    PidcSubVariant srcSubVar1 = (new PidcSubVariantServiceClient()).get(2132395423L);
    SortedSet<PidcSubVariant> srcSubVarset = new TreeSet<>();
    srcSubVarset.add(srcSubVar1);
    pidcVarData.setSrcSubVarSet(srcSubVarset);

    PidcVarPasteOutput pidcVarOutputData = pidcVarNSubVarCopyServiceClient.create(pidcVarData);
    PidcVariant createdVariant = pidcVarOutputData.getPastedVariant();

    // validate
    assertNotNull("Object is not null", pidcVarOutputData);
    assertEquals("Pidc Version is equal", new PidcVersionServiceClient().getById(790699170L),
        pidcVarOutputData.getPidcVersion());
    assertEquals("Variant name is equal", variantName, createdVariant.getName());

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
   * Test method for {@link PidcVarNSubVarCopyServiceClient#update(PidcVariantData)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testUpdate() throws ApicWebServiceException {

    PidcVarNSubVarCopyServiceClient pidcVarClient = new PidcVarNSubVarCopyServiceClient();
    PidcVariantData pidcVarData = new PidcVariantData();
    pidcVarData.setSrcPidcVar(new PidcVariantServiceClient().get(1027853967L));
    pidcVarData.setPidcVersion(new PidcVersionServiceClient().getById(790699170L));
    pidcVarData.setDestPidcVar(new PidcVariantServiceClient().get(1508649080L));

    // subvarset
    PidcSubVariant srcSubVar1 = (new PidcSubVariantServiceClient()).get(2132395423L);
    SortedSet<PidcSubVariant> srcSubVarset = new TreeSet<>();
    srcSubVarset.add(srcSubVar1);
    pidcVarData.setSrcSubVarSet(srcSubVarset);

    PidcVarPasteOutput pidcVarOutputData = pidcVarClient.update(pidcVarData);

    // validate
    assertNotNull("Object is not null", pidcVarOutputData);
    assertEquals("Pidc Version is equal", new PidcVersionServiceClient().getById(790699170L),
        pidcVarOutputData.getPidcVersion());
    assertEquals("Variant name is equal", "V1_EU6A_Copy", pidcVarOutputData.getPastedVariant().getName());

    Map<Long, PidcVariantAttribute> attrsInSrc =
        new PidcVariantAttributeServiceClient().getVarAttrsForVariant(1027853967L);
    Map<Long, PidcVariantAttribute> attrsInDest =
        new PidcVariantAttributeServiceClient().getVarAttrsForVariant(1508649080L);

    assertEquals("Number of Variant Attributes are equal", attrsInSrc.size(), attrsInDest.size());

    List<PidcVariantAttribute> valAlreadyExists = pidcVarOutputData.getValAlreadyExists();
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
