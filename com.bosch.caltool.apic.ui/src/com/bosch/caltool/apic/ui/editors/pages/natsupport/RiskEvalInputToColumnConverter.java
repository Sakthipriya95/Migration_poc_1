/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages.natsupport;

import com.bosch.caltool.icdm.client.bo.apic.PidcRMCharacterMapping;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;

/**
 * The Class RiskEvalInputToColumnConverter.
 *
 * @author gge6cob
 */
public class RiskEvalInputToColumnConverter extends AbstractNatInputToColumnConverter {

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getColumnValue(final Object evaluateObj, final int colIndex) {
    Object result = null;
    // PidcRMCharacterMapping represents a row in the table viewer
    if (evaluateObj instanceof PidcRMCharacterMapping) {
      result = getMappingData((PidcRMCharacterMapping) evaluateObj, colIndex);
    }
    return result;
  }

  /**
   * Gets the mapping data.
   *
   * @param evaluateObj the evaluate obj
   * @param colIndex the col index
   * @return the mapping data
   */
  private Object getMappingData(final PidcRMCharacterMapping evaluateObj, final int colIndex) {
    Object result = null;
    switch (colIndex) {
      case 0:// Character
        result = evaluateObj.getProjectCharacter();
        break;
      case 1:// Relevant Flag - Yes
        result = evaluateObj.isRelevantYes();
        break;
      case 2:// Relevant Flag - No
        result = evaluateObj.isRelevantNo();
        break;
      case 3:// Relevant Flag - NA
        result = evaluateObj.isRelevantNA();
        break;
      case 4:// RB Software share -> Refere RiskEvalCellStyleConfig for enabled/disabled states
        result = evaluateObj.isRelevantYes() ? evaluateObj.getRbSoftwareShare() : ApicConstants.EMPTY_STRING;
        break;
      case 5:// RB initial data -> Refere RiskEvalCellStyleConfig for enabled/disabled states
        result = evaluateObj.isRelevantYes() ? evaluateObj.getInputDataByRB() : ApicConstants.EMPTY_STRING;
        break;
      default:
        if (evaluateObj.getRiskImpactMap() != null) {
          result = evaluateObj.getRiskImpactMap().get(colIndex);
        }
        break;
    }
    return result;
  }

}
