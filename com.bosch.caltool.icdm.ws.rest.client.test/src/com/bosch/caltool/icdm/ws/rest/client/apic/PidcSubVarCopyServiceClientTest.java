/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVarPasteOutput;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantData;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author and4cob
 */
public class PidcSubVarCopyServiceClientTest extends AbstractRestClientTest {


  /**
   * Test method for {@link PidcSubVarCopyServiceClient#create(PidcSubVariantData)},
   * {@link PidcSubVarCopyServiceClient#update(PidcSubVariantData)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testCreateUpdate() throws ApicWebServiceException {
    PidcSubVarCopyServiceClient servClient = new PidcSubVarCopyServiceClient();

    PidcSubVariantData pidcSubVariantData = new PidcSubVariantData();

    PidcSubVariant copiedSVar = (new PidcSubVariantServiceClient()).get(2132395423L);
    // creation of AttributeValue object
    AttributeValue obj = new AttributeValue();
    obj.setAttributeId(126l);
    obj.setDescriptionEng("Junit_Subvariant_" + getRunId());
    obj.setTextValueEng("Junit_Subvariant_" + getRunId());
    obj.setBoolvalue(null);
    obj.setOtherValue(null);
    obj.setNumValue(null);
    obj.setDateValue(null);
    obj.setDeleted(false);
    obj.setClearingStatus("R");
    obj.setChangeComment(null);
    obj.setCharacteristicValueId(null);

    AttributeValueServiceClient attrValServiceClient = new AttributeValueServiceClient();
    AttributeValue attrValObj = attrValServiceClient.create(obj);
    AttributeValue selValue = attrValServiceClient.getById(attrValObj.getId());

    pidcSubVariantData.setSrcPidcSubVar(copiedSVar);
    pidcSubVariantData.setSubvarNameAttrValue(selValue);
    pidcSubVariantData.setPidcVariantId(copiedSVar.getPidcVariantId());
    pidcSubVariantData.setPidcVersionId(copiedSVar.getPidcVersionId());

    // invoke create
    PidcSubVarPasteOutput createdObj = servClient.create(pidcSubVariantData);
    assertNotNull("Created obj is not null", createdObj);
    // validate create
    assertNotNull("Created obj is not null", createdObj);
    assertEquals("Pasted sub variant name is equal", "Junit_Subvariant_" + getRunId(),
        createdObj.getPastedSubVariant().getName());


    PidcSubVariant destSVar = (new PidcSubVariantServiceClient()).get(2132395418L);
    pidcSubVariantData.setDestPidcSubVar(destSVar);
    // invoke update
    PidcSubVarPasteOutput updatedObj = servClient.update(pidcSubVariantData);
    // validate update
    assertNotNull("Updated Object is not null", updatedObj);
    assertEquals("Pasted sub variant name is equal", "02_VW216_SUV_85kW_200Nm_DQ200_EU6AG_OPF",
        updatedObj.getPastedSubVariant().getName());
    Map<Long, PidcSubVariantAttribute> retMap =
        (new PidcSubVariantAttributeServiceClient()).getSubVarAttrForSubVar(2132395418L);
    assertFalse("Response should not be null or empty", ((retMap == null) || retMap.isEmpty()));
    PidcSubVariantAttribute pidcSubVariantAttribute = retMap.get(84L);
    testPidcSubVariantAttribute(pidcSubVariantAttribute);
  }

  /**
   * @param pidcSubVariantAttribute
   */
  private void testPidcSubVariantAttribute(final PidcSubVariantAttribute pidcSubVariantAttribute) {
    assertEquals("Variant Id is equal", Long.valueOf(1584778176), pidcSubVariantAttribute.getVariantId());
    assertEquals("CreatedUser is equal", "AND4COB", pidcSubVariantAttribute.getCreatedUser());
    assertNotNull("CreatedDate is not null", pidcSubVariantAttribute.getCreatedDate());
    assertEquals("Value id is equal", Long.valueOf(1011078045), pidcSubVariantAttribute.getValueId());
    assertEquals("Value is equal", "AT", pidcSubVariantAttribute.getValue());
  }

}
