/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.pidcdiff.output;

import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardChangedAttributeType;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardChangedSubVarType;
import com.bosch.caltool.apic.ws.client.output.AbstractStringOutput;


/**
 * @author imi2si
 */
public class PidcDiffSubVarOutput extends AbstractStringOutput {

  private final ProjectIdCardChangedSubVarType subVariantsDiff;

  public PidcDiffSubVarOutput(final ProjectIdCardChangedSubVarType subVariantsDiff) {
    this.subVariantsDiff = subVariantsDiff;
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
    append("Sub Variant ID");
    append("Old Value ID");
    append("New Value ID");
    append("Old Change Number");
    append("New Change Number");
    append("Modify Date");
    append("Modify User");
    lineBreak();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createRows() {
    append(this.subVariantsDiff.getSubVariantID());
    append(this.subVariantsDiff.getOldValueID());
    append(this.subVariantsDiff.getNewValueID());
    append(this.subVariantsDiff.getOldChangeNumber());
    append(this.subVariantsDiff.getNewChangeNumber());
    append(this.subVariantsDiff.getModifyDate());
    append(this.subVariantsDiff.getModifyUser());
    lineBreak();

    createAttrDetails(this.subVariantsDiff.getChangedAttributes());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean outputAvailable() {
    return this.subVariantsDiff != null;
  }

  private void createAttrDetails(final ProjectIdCardChangedAttributeType[] attrDiff) {
    if (attrDiff == null) {
      return;
    }

    PidcDiffAttributeOutput diffAttrOutput;

    for (ProjectIdCardChangedAttributeType entry : attrDiff) {
      diffAttrOutput = new PidcDiffAttributeOutput(entry);
      diffAttrOutput.createOutput();
      appendDetail(diffAttrOutput.getOutput());
      lineBreak();
    }
  }

}
