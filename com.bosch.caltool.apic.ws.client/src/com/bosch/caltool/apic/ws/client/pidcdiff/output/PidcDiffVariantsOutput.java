/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.pidcdiff.output;

import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardChangedAttributeType;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardChangedSubVarType;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardChangedVariantsType;
import com.bosch.caltool.apic.ws.client.output.AbstractStringOutput;


/**
 * @author imi2si
 */
public class PidcDiffVariantsOutput extends AbstractStringOutput {

  private final ProjectIdCardChangedVariantsType variantsDiff;

  public PidcDiffVariantsOutput(final ProjectIdCardChangedVariantsType variantsDiff) {
    this.variantsDiff = variantsDiff;
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
    append("Variant ID");
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
    append(this.variantsDiff.getVariantID());
    append(this.variantsDiff.getOldValueID());
    append(this.variantsDiff.getNewValueID());
    append(this.variantsDiff.getOldChangeNumber());
    append(this.variantsDiff.getNewChangeNumber());
    append(this.variantsDiff.getModifyDate());
    append(this.variantsDiff.getModifyUser());
    lineBreak();

    createAttrDetails(this.variantsDiff.getChangedAttributes());
    createSVarDetails(this.variantsDiff.getChangedSubVariants());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean outputAvailable() {
    return this.variantsDiff != null;
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

  private void createSVarDetails(final ProjectIdCardChangedSubVarType[] varDiff) {
    if (varDiff == null) {
      return;
    }

    PidcDiffSubVarOutput diffVarOutput;

    for (ProjectIdCardChangedSubVarType entry : varDiff) {
      diffVarOutput = new PidcDiffSubVarOutput(entry);
      diffVarOutput.createOutput();
      appendDetail(diffVarOutput.getOutput());
      lineBreak();
    }
  }
}
