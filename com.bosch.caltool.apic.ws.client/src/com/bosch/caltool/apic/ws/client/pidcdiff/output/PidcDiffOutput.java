/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.pidcdiff.output;

import com.bosch.caltool.apic.ws.client.APICStub.GetPidcDiffsResponseType;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardChangedAttributeType;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardChangedVariantsType;
import com.bosch.caltool.apic.ws.client.output.AbstractStringOutput;


/**
 * @author imi2si
 */
public class PidcDiffOutput extends AbstractStringOutput {

  private final GetPidcDiffsResponseType pidcDiff;

  public PidcDiffOutput(final GetPidcDiffsResponseType pidcDiff) {
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
    append("PIDC ID");
    append("Old Change Number");
    append("New Change Number");
    append("Old PIDC Version Number");
    append("New PIDC Version Number");
    append("Old PIDC Status");
    append("New PIDC Status");
    append("Modify Date");
    append("Modify User");
    lineBreak();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createRows() {
    append(this.pidcDiff.getPidcID());
    append(this.pidcDiff.getOldChangeNumber());
    append(this.pidcDiff.getNewChangeNumber());
    append(this.pidcDiff.getOldPidcVersionNumber());
    append(this.pidcDiff.getNewPidcVersionNumber());
    append(this.pidcDiff.getOldPidcStatus());
    append(this.pidcDiff.getNewPidcStatus());
    append(this.pidcDiff.getModifyDate());
    append(this.pidcDiff.getModifyUser());
    lineBreak();

    createAttrDetails(this.pidcDiff.getChangedAttributes());
    createVarDetails(this.pidcDiff.getChangedVariants());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean outputAvailable() {
    return this.pidcDiff != null;
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

  private void createVarDetails(final ProjectIdCardChangedVariantsType[] varDiff) {
    if (varDiff == null) {
      return;
    }

    PidcDiffVariantsOutput diffVarOutput;

    for (ProjectIdCardChangedVariantsType entry : varDiff) {
      diffVarOutput = new PidcDiffVariantsOutput(entry);
      diffVarOutput.createOutput();
      appendDetail(diffVarOutput.getOutput());
      lineBreak();
    }
  }
}
