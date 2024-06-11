/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.bosch.caltool.apic.ws.client.APICStub.AttributeWithValueType;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardType;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardVariantType;


/**
 * @author imi2si
 */
public class PidcAttributeTest extends AbstractSoapClientTest {

  private final APICWebServiceClient stub = new APICWebServiceClient();

  /**
   * @throws Exception service error
   */
  @Test
  public void attrPnVarShouldBeFilled() throws Exception {
    ProjectIdCardVariantType variant = getVariant(getPidc(690567L), 768398138L);
    AttributeWithValueType attribute = getAttributeWithValue(variant.getAttributes(), 1527L);

    assertEquals("Verify part number", "B 258 080 XXX", attribute.getPartNumber());
    assertEquals("Verify spec link",
        "\\\\bosch.com\\dfsrb\\DfsDE\\DIV\\GS\\07\\Line\\SA3\\SCI\\P_Malaysia\\03_Proton\\01_NFE_variants\\05-Components\\Projektblatt_28_11_12.pdf",
        attribute.getSpecLink());
    assertEquals("Verify additional comment", "XXX (different length of wires)", attribute.getDescription());
  }

  /**
   * @throws Exception service error
   */
  @Test
  public void attrPnSubVarShouldBeFilled() throws Exception {
    ProjectIdCardVariantType variant = getSubVariant(getPidc(1116516467L), 10743681030L);
    AttributeWithValueType attribute = getAttributeWithValue(variant.getAttributes(), 84L);

    assertEquals("Verify part number at sub variant attribute level", "Pn Sub-Variant", attribute.getPartNumber());
    assertEquals("Verify spec link at sub variant attribute level", "http://127.0.0.1/", attribute.getSpecLink());
    assertEquals("Verify additional comment at sub variant attribute level", "Comment Sub-Variant",
        attribute.getDescription());
  }


  private ProjectIdCardType getPidc(final Long pidcId) throws Exception {
    return this.stub.getPidcDetails(pidcId);
  }

  private ProjectIdCardVariantType getVariant(final ProjectIdCardType pidc, final long variantId) {
    if (pidc.isVariantsSpecified()) {
      for (ProjectIdCardVariantType variant : pidc.getVariants()) {
        if (variant.getPIdCVariant().getId() == variantId) {
          return variant;
        }
      }
    }
    return null;
  }

  private ProjectIdCardVariantType getSubVariant(final ProjectIdCardType pidc, final long subVariantId) {
    if (pidc.isVariantsSpecified()) {
      for (ProjectIdCardVariantType variant : pidc.getVariants()) {
        ProjectIdCardVariantType subVariant = getSubVariant(variant, subVariantId);

        if ((subVariant != null) && (subVariant.getPIdCVariant().getId() == subVariantId)) {
          return subVariant;
        }
      }
    }
    return null;
  }

  private ProjectIdCardVariantType getSubVariant(final ProjectIdCardVariantType variant, final long subVariantId) {
    if (variant.isSubVariantsSpecified()) {
      for (ProjectIdCardVariantType subVariant : variant.getSubVariants()) {
        if (subVariant.getPIdCVariant().getId() == subVariantId) {
          return subVariant;
        }
      }
    }
    return null;
  }

  private AttributeWithValueType getAttributeWithValue(final AttributeWithValueType[] attributes,
      final long attributeId) {
    for (AttributeWithValueType attribute : attributes) {
      if (attribute.getAttribute().getId() == attributeId) {
        return attribute;
      }
    }
    return null;
  }

}
