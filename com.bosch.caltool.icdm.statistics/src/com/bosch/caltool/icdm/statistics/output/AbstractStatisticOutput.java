/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.statistics.output;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.bosch.caltool.icdm.statistics.adapter.AbstractIcdmMetaData;
import com.bosch.caltool.icdm.statistics.adapter.AbstractIcdmStatistics;


/**
 * @author imi2si
 */
public abstract class AbstractStatisticOutput {

  private int numberOfTreeAttributes;

  protected AbstractIcdmStatistics[] statistics;

  protected AbstractIcdmMetaData icdmMetaData;

  public AbstractStatisticOutput(final AbstractIcdmMetaData icdmMetaData, final AbstractIcdmStatistics... statistics) {
    this.icdmMetaData = icdmMetaData;
    setInput(statistics);
    calculateNumOfTreeAtt();
  }


  public void setInput(final AbstractIcdmStatistics... statistics) {
    // TODO Auto-generated method stub
    this.statistics = statistics;
  }

// Icdm-1112 Throwing the Exception
  public abstract void createOutput() throws IOException, FileNotFoundException;


  public abstract Object getOutput();

  protected int getNumOfTreeAtt() {
    return this.numberOfTreeAttributes;
  }

  private void calculateNumOfTreeAtt() {
    for (AbstractIcdmStatistics statistic : this.statistics) {
      this.numberOfTreeAttributes =
          statistic.getPidcNoOfTreeAttributes() > this.numberOfTreeAttributes ? statistic.getPidcNoOfTreeAttributes()
              : this.numberOfTreeAttributes;
    }
  }

}
