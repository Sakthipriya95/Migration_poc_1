/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.differences.timezone;

import java.util.TimeZone;

import com.bosch.caltool.apic.ws.GetPidcDiffsResponseType;
import com.bosch.caltool.apic.ws.ProjectIdCardChangedAttributeType;
import com.bosch.caltool.apic.ws.ProjectIdCardChangedSubVarType;
import com.bosch.caltool.apic.ws.ProjectIdCardChangedVariantsType;
import com.bosch.caltool.apic.ws.timezone.AbstractTimeZone;


/**
 * @author imi2si
 */
public class PidcDiffTypeTimeZone extends AbstractTimeZone {

  private final GetPidcDiffsResponseType histRecord;

  public PidcDiffTypeTimeZone(final TimeZone timeZone, final GetPidcDiffsResponseType histRecord) {
    super(timeZone);
    this.histRecord = histRecord;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void adjustTimeZoneFields() {
    if (this.histRecord != null) {
      this.histRecord.setModifyDate(super.adjustTimeZone(this.histRecord.getModifyDate()));
      adjustAttributes(this.histRecord.getChangedAttributes());
      adjustVariants(this.histRecord.getChangedVariants());
    }
  }

  private void adjustVariants(final ProjectIdCardChangedVariantsType[] variants) {
    if (variants != null) {
      for (ProjectIdCardChangedVariantsType variant : variants) {

        variant.setModifyDate(super.adjustTimeZone(variant.getModifyDate()));
        adjustAttributes(variant.getChangedAttributes());
        adjustSubVariants(variant.getChangedSubVariants());
      }
    }
  }

  private void adjustSubVariants(final ProjectIdCardChangedSubVarType[] variants) {
    if (variants != null) {
      for (ProjectIdCardChangedSubVarType variant : variants) {

        variant.setModifyDate(super.adjustTimeZone(variant.getModifyDate()));
        adjustAttributes(variant.getChangedAttributes());
      }
    }
  }

  private void adjustAttributes(final ProjectIdCardChangedAttributeType[] attributes) {
    if (attributes != null) {
      for (ProjectIdCardChangedAttributeType attribute : attributes) {

        attribute.setModifyDate(super.adjustTimeZone(attribute.getModifyDate()));
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public GetPidcDiffsResponseType getWsResponse() {
    return this.histRecord;
  }
}
