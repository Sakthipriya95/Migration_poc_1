/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.pidcdiff.output;

import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardChangedAttributeType;
import com.bosch.caltool.apic.ws.client.output.AbstractStringOutput;


/**
 * @author imi2si
 */
public class PidcDiffAttributeOutput extends AbstractStringOutput {

  private final ProjectIdCardChangedAttributeType attrDiff;

  public PidcDiffAttributeOutput(final ProjectIdCardChangedAttributeType attrDiff) {
    this.attrDiff = attrDiff;
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
    append("Attribute ID");
    append("Old Value ID");
    append("New Value ID");
    append("Old Used");
    append("New Used");
    append("Old Part Number");
    append("New Part Number");
    append("Old Spec Link");
    append("New Spec Link");
    append("Old Description");
    append("New Description");
    append("Old Value ID Clearing Status");
    append("New Value ID Clearing Status");
    append("Change Number");
    append("Modify Date");
    append("Modify User");

    lineBreak();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createRows() {
    append(this.attrDiff.getAttrID());
    append(this.attrDiff.getOldValueID());
    append(this.attrDiff.getNewValueID());
    append(this.attrDiff.getOldUsed());
    append(this.attrDiff.getNewUsed());
    append(this.attrDiff.getOldPartNumber());
    append(this.attrDiff.getNewPartNumber());
    append(this.attrDiff.getOldSpecLink());
    append(this.attrDiff.getNewSpecLink());
    append(this.attrDiff.getOldDescription());
    append(this.attrDiff.getNewDescription());
    append(this.attrDiff.getOldValueIdClearingStatus());
    append(this.attrDiff.getNewValueIdClearingStatus());
    append(this.attrDiff.getChangeNumber());
    append(this.attrDiff.getModifyDate());
    append(this.attrDiff.getModifyUser());
    lineBreak();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean outputAvailable() {
    return this.attrDiff != null;
  }
}
