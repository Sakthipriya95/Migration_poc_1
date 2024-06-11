/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */

package com.bosch.caltool.apic.ws.client.serviceclient;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.apic.ws.client.APICStub.Attribute;
import com.bosch.caltool.apic.ws.client.APICStub.AttributeValue;
import com.bosch.caltool.apic.ws.client.APICStub.GetPidcDiffsResponseType;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardChangedAttributeType;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardChangedSubVarType;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardChangedVariantsType;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardType;
import com.bosch.caltool.apic.ws.client.APICStub.ValueList;

/**
 * @author imi2si
 */
public class TestClientGetPIDCDiffs extends AbstractSoapClientTest {


  private final Map<Long, AttributeValue> ValuesMap = new HashMap<Long, AttributeValue>();

  private final APICWebServiceClient stub = new APICWebServiceClient();

  /**
   * @throws Exception service error
   */
  @Test
  public void runTest() throws Exception {
    // PIDC : Fire 1.4L ME17.3.0
    Long pidcID = 699017L;
    int attributeID = -1; // -1 for all
    long oldVersionID = 0;
    long newVersionID = -1;


    ProjectIdCardType projectCard = this.stub.getPidcDetails(pidcID);

    int i;
    int i2;

    LOG.info("========= Attributes =========================");

    i = 0;

    Attribute[] attributes = this.stub.getAllAttributes();

    Map<Long, Attribute> attributesMap = new HashMap<>();

    for (Attribute attribute : attributes) {

      attributesMap.put(attribute.getId(), attribute);

      if (i < 3) {
        LOG.info(++i + " : " + attribute.getNameE() + " : " + attribute.getNameG() + " : TypeID = " +
            attribute.getTypeID() + " : Version = " + attribute.getChangeNumber());
      }
    }

    LOG.info("======= Attributevalues ======================");

    long[] attrIDs;

    attrIDs = new long[attributesMap.size()];
    i = 0;
    for (Object element : attributesMap.keySet()) {
      Long attrID = (Long) element;
      attrIDs[i++] = attrID;

    }

    ValueList[] values = this.stub.getAttrValues(attrIDs);
    int logCounter = 0;
    for (ValueList value2 : values) {

      i = 0;

      if (logCounter < 3) {
        LOG.info("Attribute: " + value2.getAttribute().getNameE());
      }

      if (value2.isValuesSpecified() && (value2.getValues().length > 0)) {

        for (AttributeValue value : value2.getValues()) {

          this.ValuesMap.put(value.getValueID(), value);

          if (value.getIsDeleted()) {
            /*
             * LOG.info("  " + ++i + " : DELETED! : " + value.getValueE() + " : " + value.getValueG() + " : Version = "
             * + value.getChangeNumber());
             */
          }
          else {
            /*
             * LOG.info("  " + ++i + " : " + value.getValueE() + " : " + value.getValueG() + " : Version = " +
             * value.getChangeNumber());
             */
          }

        }

      }
      else {
        // LOG.info(" " + " no values found ");
      }

      logCounter++;

    }

    LOG.info("=========== PIDC Differences ===================");

    GetPidcDiffsResponseType pidcDiffs =
        this.stub.getPidcDiffs(projectCard.getProjectIdCardDetails().getId(), oldVersionID, newVersionID)
            .getGetPidcDiffsResponse();
    assertNotNull(pidcDiffs);

    LOG.info("Differences for PIDC " + projectCard.getProjectIdCardDetails().getName());

    LOG.info("  old changeNumber: " + pidcDiffs.getOldChangeNumber());
    LOG.info("  new changeNumber: " + pidcDiffs.getNewChangeNumber());

    LOG.info("  old version: " + pidcDiffs.getOldPidcVersionNumber());
    LOG.info("  new version: " + pidcDiffs.getNewPidcVersionNumber());

    LOG.info("  old status: " + pidcDiffs.getOldPidcStatus());
    LOG.info("  new status: " + pidcDiffs.getNewPidcStatus());

    LOG.info("  last modification date: " + pidcDiffs.getModifyDate().getTime());
    LOG.info("  last modification user: " + pidcDiffs.getModifyUser());

    getChangedAttributes(attributesMap, pidcDiffs.getChangedAttributes(), attributeID);

    if (pidcDiffs.isChangedVariantsSpecified()) {
      getChangedVariants(attributesMap, pidcDiffs.getChangedVariants(), attributeID);
    }
    else {
      LOG.info("no variants defined for PIDC!");
    }

  }

  /**
   * @param attributesMap
   * @param changedVariants
   */
  private void getChangedVariants(final Map<Long, Attribute> attributesMap,
      final ProjectIdCardChangedVariantsType[] changedVariants, final int attributeID) {

    LOG.info("  Changed Variants: " + changedVariants.length);
    int i = 0;
    for (ProjectIdCardChangedVariantsType changedVariant : changedVariants) {

      if (i > 2) {
        break;
      }
      LOG.info("    Variant: " + changedVariant.getVariantID());

      if (changedVariant.isOldValueIDSpecified()) {
        LOG.info("      oldValueID: " + changedVariant.getOldValueID());
      }
      else {
        LOG.info("      oldValueID: " + "NULL");
      }
      LOG.info("      newValueID: " + changedVariant.getNewValueID());

      LOG.info("      oldChangeNumber: " + changedVariant.getOldChangeNumber());
      LOG.info("      newChangeNumber: " + changedVariant.getNewChangeNumber());

      LOG.info("      Modify User: " + changedVariant.getModifyUser());
      LOG.info("      Modify Date: " + changedVariant.getModifyDate().getTime());

      LOG.info(" Attributes for Variant " + changedVariant.getNewValueID());
      getChangedAttributes(attributesMap, changedVariant.getChangedAttributes(), attributeID);

      if (changedVariant.isChangedSubVariantsSpecified()) {
        getChangedSubVariants(attributesMap, changedVariant.getChangedSubVariants(), attributeID);
      }
      else {
        LOG.info("no sub variants defined for PIDC!");
      }


      LOG.info("    =============================================");
      i++;
    }

    LOG.info("  =============================================");
  }

  /**
   * @param attributesMap
   * @param changedVariants
   */
  private void getChangedSubVariants(final Map<Long, Attribute> attributesMap,
      final ProjectIdCardChangedSubVarType[] changedVariants, final int attributeID) {

    LOG.info("  Changed SubVariants: " + changedVariants.length);

    int i = 0;
    for (ProjectIdCardChangedSubVarType changedVariant : changedVariants) {
      if (i > 3) {
        break;
      }
      LOG.info("    Variant: " + changedVariant.getSubVariantID());

      if (changedVariant.isOldValueIDSpecified()) {
        LOG.info("      oldValueID: " + changedVariant.getOldValueID());
      }
      else {
        LOG.info("      oldValueID: " + "NULL");
      }
      LOG.info("      newValueID: " + changedVariant.getNewValueID());

      LOG.info("      oldChangeNumber: " + changedVariant.getOldChangeNumber());
      LOG.info("      newChangeNumber: " + changedVariant.getNewChangeNumber());

      LOG.info("      Modify User: " + changedVariant.getModifyUser());
      LOG.info("      Modify Date: " + changedVariant.getModifyDate().getTime());

      getChangedAttributes(attributesMap, changedVariant.getChangedAttributes(), attributeID);

      LOG.info("    =============================================");
      i++;
    }

    LOG.info("  =============================================");
  }


  /**
   * @param attributesMap
   * @param changedAttributes
   */
  private void getChangedAttributes(final Map<Long, Attribute> attributesMap,
      final ProjectIdCardChangedAttributeType[] changedAttributes, final int attributeID) {
    if (changedAttributes != null) {

      LOG.info("  Changed Attributes: " + changedAttributes.length);
      int i = 0;
      for (ProjectIdCardChangedAttributeType changedAttr : changedAttributes) {
        if (i > 2) {
          break;
        }

        if ((attributeID >= 0) && (attributeID != changedAttr.getAttrID())) {
          continue;
        }


        LOG.info("    Changed Attribute: " + attributesMap.get(changedAttr.getAttrID()).getNameE());
        LOG.info("    Changed Attribute-ID: " + changedAttr.getAttrID());

        LOG.info("      oldUsed: " + changedAttr.getOldUsed());
        LOG.info("      newUsed: " + changedAttr.getNewUsed());

        if (changedAttr.isOldValueIDSpecified()) {
          LOG.info("      oldValueID: " + changedAttr.getOldValueID() + " :: " +
              ((changedAttr.getOldValueID() > 0) && (this.ValuesMap.get(changedAttr.getOldValueID()) != null)
                  ? this.ValuesMap.get(changedAttr.getOldValueID()).getValueE() : ""));
        }
        else {
          LOG.info("      oldValueID: " + "NULL");
        }

        if (changedAttr.isNewValueIDSpecified()) {
          LOG.info("      newValueID: " + changedAttr.getNewValueID() + " :: " +
              ((changedAttr.getNewValueID() > 0) && (this.ValuesMap.get(changedAttr.getNewValueID()) != null)
                  ? this.ValuesMap.get(changedAttr.getNewValueID()).getValueE() : ""));
        }
        else {
          LOG.info("      newValueID: " + "NULL");
        }

        if (changedAttr.isOldPartNumberSpecified()) {
          LOG.info("      oldPartNumber: " + changedAttr.getOldPartNumber());
        }
        else {
          LOG.info("      oldPartNumber: " + "NULL");
        }

        if (changedAttr.isNewPartNumberSpecified()) {
          LOG.info("      newPartNumber: " + changedAttr.getNewPartNumber());
        }
        else {
          LOG.info("      newPartNumber: " + "NULL");
        }

        if (changedAttr.isOldSpecLinkSpecified()) {
          LOG.info("      oldSpecLink: " + changedAttr.getOldSpecLink());
        }
        else {
          LOG.info("      oldSpecLink: " + "NULL");
        }

        if (changedAttr.isNewSpecLinkSpecified()) {
          LOG.info("      newSpecLink: " + changedAttr.getNewSpecLink());
        }
        else {
          LOG.info("      newSpecLink: " + "NULL");
        }

        if (changedAttr.isOldDescriptionSpecified()) {
          LOG.info("      oldDescription: " + changedAttr.getOldDescription());
        }
        else {
          LOG.info("      oldDescription: " + "NULL");
        }

        if (changedAttr.isNewDescriptionSpecified()) {
          LOG.info("      newDescription: " + changedAttr.getNewDescription());
        }
        else {
          LOG.info("      newDescription: " + "NULL");
        }

        LOG.info("      modification date: " + changedAttr.getModifyDate().getTime());
        LOG.info("      modification user: " + changedAttr.getModifyUser());

        LOG.info("      changeNumber: " + changedAttr.getChangeNumber());

        LOG.info("      =============================================");
        i++;
      }
    }
    else {

      LOG.info("    No Changed Attributes! ");

      LOG.info("      =============================================");
    }
  }

  //
  // private String getValue(final AttributeWithValueType attrWithValue) {
  //
  // if (attrWithValue.getIsVariant()) {
  // return "<VARIANT>";
  // }
  //
  // if (attrWithValue.isValueSpecified()) {
  // return attrWithValue.getValue().getValueE();
  // }
  //
  // return "";
  //
  // }

}
