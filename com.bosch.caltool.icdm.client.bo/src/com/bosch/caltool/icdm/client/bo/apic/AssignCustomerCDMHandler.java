/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants.PROJ_ATTR_USED_FLAG;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVariantAttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dja7cob
 */
public class AssignCustomerCDMHandler {

  private final PidcVersionBO pidcVersionBo;

  /**
   * @param pidcVersionBO
   */
  public AssignCustomerCDMHandler(final PidcVersionBO pidcVersionBO) {
    this.pidcVersionBo = pidcVersionBO;
  }

  /**
   * @param newlyCreatedVar
   * @param varCustCDMAttr2
   */
  public void setValueForVarCustCDMAttr(final PidcVariant newlyCreatedVar, final String varNameEng,
      final String varNameGer, final PidcVariantAttribute varCustCDMAttr) {

    // Get the Variant in customer CDM system attribute
    AttributeValue valueToBeSet = null;
    Attribute custCDMAttr = this.pidcVersionBo.getPidcDataHandler().getAttributeMap().get(varCustCDMAttr.getAttrId());
    for (AttributeValue valu : new AttributeClientBO(custCDMAttr).getAttrValues()) {
      if ((null != valu.getNameRaw()) && valu.getNameRaw().equals(varNameEng)) {
        valueToBeSet = valu;
      }
    }
    PidcVariantAttributeServiceClient varAttrSrvcClient = new PidcVariantAttributeServiceClient();
    if (null == valueToBeSet) {
      AttributeValueServiceClient attrValSrvc = new AttributeValueServiceClient();
      AttributeValue attrVal = new AttributeValue();
      attrVal.setTextValueEng(null == varNameEng ? newlyCreatedVar.getName() : varNameEng);
      attrVal.setTextValueGer(null == varNameEng ? newlyCreatedVar.getName() : varNameEng);
      attrVal.setDescriptionEng(null == varNameEng ? newlyCreatedVar.getDescription() : varNameEng);
      attrVal.setDescriptionGer(null == varNameGer ? newlyCreatedVar.getDescription() : varNameGer);
      attrVal.setAttributeId(custCDMAttr.getId());
      attrVal.setCharacteristicValueId(null);
      attrVal.setChangeComment("");
      try {
        AttributeValue newAttrVal = attrValSrvc.create(attrVal);
        varCustCDMAttr.setUsedFlag(PROJ_ATTR_USED_FLAG.YES.getDbType());
        varCustCDMAttr.setValue(newAttrVal.getName());
        varCustCDMAttr.setValueId(newAttrVal.getId());
        varCustCDMAttr.setValueType(newAttrVal.getValueType());
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
    else {
      varCustCDMAttr.setUsedFlag(PROJ_ATTR_USED_FLAG.YES.getDbType());
      varCustCDMAttr.setValue(valueToBeSet.getName());
      varCustCDMAttr.setValueId(valueToBeSet.getId());
      varCustCDMAttr.setValueType(valueToBeSet.getValueType());
    }
    try {
      PidcVariantAttribute varAttr = varAttrSrvcClient.update(varCustCDMAttr);
      PidcVariantBO varHandler = new PidcVariantBO(this.pidcVersionBo.getPidcVersion(), newlyCreatedVar,
          this.pidcVersionBo.getPidcDataHandler());

      varHandler.getAttributesAll().put(custCDMAttr.getId(), varAttr);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param custCDMAttr
   */
  public void resetAlreadyExistingAssgmt(final PidcVariant variant, final Attribute custCDMAttr) {
    PidcVariantBO varHandler =
        new PidcVariantBO(this.pidcVersionBo.getPidcVersion(), variant, this.pidcVersionBo.getPidcDataHandler());
    PidcVariantAttribute varCustCDMAttr = varHandler.getAttributesAll().get(custCDMAttr.getId());
    invokeUpdateCommand(varCustCDMAttr);
  }

  /**
   * @param varCustCDMAttr
   */
  public void invokeUpdateCommand(final PidcVariantAttribute varCustCDMAttr) {
    PidcVariantAttributeServiceClient varAttrSrvc = new PidcVariantAttributeServiceClient();
    varCustCDMAttr.setValueId(null);
    varCustCDMAttr.setPartNumber("");
    varCustCDMAttr.setSpecLink("");
    varCustCDMAttr.setDescription("");
    try {
      varAttrSrvc.update(varCustCDMAttr);
      System.out.println();
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }


}
