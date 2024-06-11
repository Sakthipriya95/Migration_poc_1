/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.statistics.output;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.bosch.caltool.icdm.statistics.adapter.AbstractIcdmLevelStatistics;
import com.bosch.caltool.icdm.statistics.adapter.AbstractIcdmMetaData;
import com.bosch.caltool.icdm.statistics.adapter.AbstractIcdmStatistics;
import com.bosch.caltool.icdm.statistics.adapter.AbstractIcdmStatistics.PidcLevel;


/**
 * @author imi2si
 */
public class StringStatisticOutput extends AbstractStatisticOutput {

  private static final int COL_LENGTH = 35;

  StringBuffer output = new StringBuffer();

  public StringStatisticOutput(final AbstractIcdmMetaData icdmMetaData, final AbstractIcdmStatistics... statistics) {
    super(icdmMetaData, statistics);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createOutput() throws IOException, FileNotFoundException {
    for (AbstractIcdmStatistics statistic : this.statistics) {
      this.output.append(String.format("%1$100s", "").replace(" ", "*") + "\n");
      this.output.append(createColumn("PIDC-Name: ") + statistic.getPidcName() + "\n");

      for (int loop = 0; loop < getNumOfTreeAtt(); loop++) {
        this.output
            .append(createColumn("Level " + (loop + 1) + ": ") + statistic.getPidcTreeAttribute(loop + 1) + "\n");
      }

      this.output.append(createColumn("PIDC-Created on: ") + statistic.getPidcCreateDateString() + "\n");
      this.output.append(createColumn("PIDC-Modifyd on: ") + statistic.getPidcModifyDateString() + "\n");
      this.output.append(createColumn("Number of Attributes: ") + statistic.getPidcNoOfAttributes() + "\n");
      this.output.append(createColumn("Number of Mandatory Attr.: ") + statistic.getPidcNoOfMandAttr() + "\n");
      this.output.append(createColumn("Percentage of Mandatory Attr.: ") + statistic.getPidcPercMandAttr() + "\n");
      this.output.append(createColumn("Number of Variants: ") + statistic.getPidcNoOfVariants() + "\n");
      this.output.append(createColumn("Number of Sub-Variants: ") + statistic.getPidcNoOfSubVariants() + "\n");

      // Iterate through the levels
      for (PidcLevel level : PidcLevel.values()) {
        AbstractIcdmLevelStatistics levelStat = statistic.getLevelStatistics(level);
        this.output.append(createColumn(level.toString() + " - ???: ") + levelStat.getUnknownUsedAttr() + "\n");
        this.output.append(createColumn(level.toString() + " - No: ") + levelStat.getNotUsedAttr() + "\n");
        this.output.append(createColumn(level.toString() + " - Yes (total): ") +
            (levelStat.getUsedAttrWoValues() + levelStat.getUsedAttrWithValues()) + "\n");
        this.output.append(createColumn(level.toString() + " - Yes (with value): ") +
            levelStat.getUsedAttrWithValues() + "\n");
        this.output
            .append(createColumn(level.toString() + " - <Variant>: ") + levelStat.getNoAttrLowerVariant() + "\n");
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getOutput() {
    // TODO Auto-generated method stub
    return this.output.toString();
  }

  private String createColumn(final String text) {
    if (text.length() < COL_LENGTH) {
      String format = "%1$" + (COL_LENGTH - text.length()) + "s";
      return text + String.format(format, "");
    }

    return text;
  }

}
