/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.statistics.adapter;

import com.bosch.caltool.apic.ws.AttributeWithValueType;
import com.bosch.caltool.apic.ws.ProjectIdCardVariantType;


/**
 * A concrete implementation for ICDM-web service based statistics.
 *
 * @author imi2si
 */
public class IcdmLevelStatisticsWebServiceAdapter extends AbstractIcdmLevelStatistics {

  @Override
  public final void addVariants(final ProjectIdCardVariantType[] variants) {

    if (variants != null) {
      for (ProjectIdCardVariantType variant : variants) {
        if (variant.getPIdCVariant().getIsDeleted()) {
          continue;
        }
        super.noOfVariants++;
        addAttributes(variant.getAttributes());

      }
    }
  }

  @Override
  public final void addAttributes(final AttributeWithValueType[] attributes) {

    super.levelElements++;

    if (attributes != null) {
      for (AttributeWithValueType entry : attributes) {
        if (entry.getAttribute().getIsDeleted()) {
          continue;
        }
        addUnknownUsedAttr(entry);
        addNotUsedAttr(entry);
        setUsedAttrWithValues(entry);
        setUsedAttrWoValues(entry);
        setNoAttrLowerVariant(entry);
        setNoAttrMandWithVal(entry);
        setNoAttrMandWoVal(entry);
        setNoAttrMandMax(entry);
      }
    }
  }

  /**
   * Increments the number of attributes with unknown usage by 1
   *
   * @param entry the attribute to be analyzed
   */
  private void addUnknownUsedAttr(final AttributeWithValueType entry) {
    if ("???".equals(entry.getUsed())) {
      super.unknownUsedAttr++;
    }
  }

  /**
   * Increments the number of attributes with Usage = No 1
   *
   * @param entry the attribute to be analyzed
   */
  private void addNotUsedAttr(final AttributeWithValueType entry) {
    if ("NO".equals(entry.getUsed())) {
      super.notUsedAttr++;
    }
  }

  /**
   * Increments the number of attributes having entered values with Usage = Yes by 1. Attributes that are maintained on
   * lower levels are not included.
   *
   * @param entry the attribute to be analyzed
   */
  private void setUsedAttrWithValues(final AttributeWithValueType entry) {
    if ("YES".equals(entry.getUsed()) && entry.isValueSpecified() && !entry.getIsVariant()) {
      super.usedAttrWithValues++;
    }
  }

  /**
   * Increments the number of attributes without values with Usage = Yes by 1.
   *
   * @param entry the attribute to be analyzed
   */
  private void setUsedAttrWoValues(final AttributeWithValueType entry) {
    if (isUsedWithoutValue(entry)) {
      super.usedAttrWoValues++;
    }
  }

  /**
   * Increments the number of mandatory attributes with value by 1.
   *
   * @param entry the attribute to be analyzed
   */
  private void setNoAttrMandWithVal(final AttributeWithValueType entry) {
    if (isCompletelyFilledInPidc(entry) && isMandatory(entry)) {
      super.noAttrMandWithVal++;
    }
  }

  private boolean isUsedWithoutValue(final AttributeWithValueType pidcAttribute) {
    return "YES".equals(pidcAttribute.getUsed()) && (!pidcAttribute.isValueSpecified() || pidcAttribute.getIsVariant());
  }

  private boolean isUsedWithValue(final AttributeWithValueType pidcAttribute) {
    return "YES".equals(pidcAttribute.getUsed()) && (pidcAttribute.isValueSpecified() || pidcAttribute.getIsVariant());
  }

  private boolean isNotUsed(final AttributeWithValueType pidcAttribute) {
    return "NO".equals(pidcAttribute.getUsed());
  }

  private boolean isCompletelyFilledInPidc(final AttributeWithValueType attribute) {
    return isUsedWithValue(attribute) || isNotUsed(attribute);
  }

  private boolean isMandatory(final AttributeWithValueType pidcAttribute) {
    return pidcAttribute.getAttribute().getIsMandatory();
  }

  /**
   * Increments the number of mandatory attributes without value by 1.
   *
   * @param entry the attribute to be analyzed
   */
  private void setNoAttrMandWoVal(final AttributeWithValueType entry) {
    if (!entry.isValueSpecified() && entry.getAttribute().getIsMandatory()) {
      super.noAttrMandWoVal++;
    }
  }

  /**
   * Increments the number of max mandatory attributes by 1.
   *
   * @param entry the attribute to be analyzed
   */
  private void setNoAttrMandMax(final AttributeWithValueType entry) {
    if (entry.getAttribute().getIsMandatory()) {
      super.noAttrMandMax++;
    }
  }

  /**
   * Increments the number of attributes which are maintained on the next lower level by 1.
   *
   * @param entry the attribute to be analyzed
   */
  private void setNoAttrLowerVariant(final AttributeWithValueType entry) {
    if (entry.getIsVariant()) {
      this.noAttrLowerVariant++;
    }
  }
}
