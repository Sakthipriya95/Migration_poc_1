/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.pidcdiff.output;

import com.bosch.caltool.apic.ws.client.APICStub.AttrDiffType;
import com.bosch.caltool.apic.ws.client.output.AbstractStringOutput;


/**
 * @author imi2si
 */
public class PidcDiffAttrReportOutput extends AbstractStringOutput {

  private final AttrDiffType[] pidcDiff;

  public PidcDiffAttrReportOutput(final AttrDiffType[] pidcDiff) {
    this.pidcDiff = pidcDiff;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getOutput() {
    return this.output.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createHeader() {
    append("PIDC Version");
    append("PIDC ID");
    append("Variant ID");
    append("Sub-Variant ID");
    append("Level");
    append("Attribute English");
    append("Attribute German");
    append("Changed Item");
    append("Old Value");
    append("Old Value English");
    append("Old Value German");
    append("New Value");
    append("New Value English");
    append("New Value German");
    append("Modified By");
    append("Modified On");
    append("Change No.");
    append("Attribute Change");
    lineBreak();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createRows() {
    for (AttrDiffType entry : this.pidcDiff) {
      append(entry.getPidcVersion());
      append(entry.getPidcId());
      append(entry.getVariantId());
      append(entry.getSubVariantId());
      append(entry.getLevel());
      append(entry.getAttribute().getNameE());
      append(entry.getAttribute().getNameG());
      append(entry.getChangedItem());
      append(entry.getOldValue());
      append(entry.getOldAttributeValue() != null ? entry.getOldAttributeValue().getValueE() : null);
      append(entry.getOldAttributeValue() != null ? entry.getOldAttributeValue().getValueG() : null);
      append(entry.getNewValue());
      append(entry.getNewAttributeValue() != null ? entry.getNewAttributeValue().getValueE() : null);
      append(entry.getNewAttributeValue() != null ? entry.getNewAttributeValue().getValueG() : null);
      append(entry.getModifiedName());
      append(entry.getModifiedOn());
      append(entry.getVersionId());
      append(entry.getAttributeChange());

      lineBreak();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean outputAvailable() {
    return this.pidcDiff != null;
  }
}