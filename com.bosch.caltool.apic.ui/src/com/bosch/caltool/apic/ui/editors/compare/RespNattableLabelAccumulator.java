/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.compare;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.editors.pages.ResponsibilityPage;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.common.bo.a2l.A2lResponsibilityCommon;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author apj4cob
 */
public class RespNattableLabelAccumulator extends ColumnOverrideLabelAccumulator {

  /**
   * Instance variable which holds a reference to bodyDataLayer
   */
  private final ILayer layer;
  /**
   * Constant for first name
   */
  private static final String CONFIG_LABEL_FIRST_NAME = "FIRST_NAME";
  /**
   * Constant for last name
   */
  private static final String CONFIG_LABEL_LAST_NAME = "LAST_NAME";
  /**
   * Constant for department
   */
  private static final String CONFIG_LABEL_DEPT = "DEPARTMENT";
  /**
   * Constant for resp type
   */
  private static final String CONFIG_RESP_TYPE = "RESP_TYPE";
  /**
   * Constant for resp type read only
   */
  private static final String CONFIG_RESP_TYPE_READ_ONLY = "RESP_TYPE_READONLY";

  /**
   * pidc editor input
   */
  private final ResponsibilityPage responsibilityPage;


  /**
   * @param bodyDataLayer ILayer instance
   * @param responsibilityPage ResponsibilityPage
   */

  public RespNattableLabelAccumulator(final ILayer bodyDataLayer, final ResponsibilityPage responsibilityPage) {
    super(bodyDataLayer);
    this.layer = bodyDataLayer;
    this.responsibilityPage = responsibilityPage;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accumulateConfigLabels(final LabelStack configLabels, final int columnPosition, final int rowPosition) {
    // get the row object out of the dataprovider
    @SuppressWarnings("unchecked")
    IRowDataProvider<A2lResponsibility> bodyDataProvider =
        (IRowDataProvider<A2lResponsibility>) ((DataLayer) this.layer).getDataProvider();
    Object rowObject = bodyDataProvider.getRowObject(rowPosition);
    // get the selected row object
    A2lResponsibility a2lResp = (A2lResponsibility) rowObject;

    // check whether icdm qnaire config attr value is BEG
    boolean isRespBEG = this.responsibilityPage.getWorkPkgResponsibilityBO().isBEGResp(a2lResp);

    if ((A2lResponsibilityCommon.isDefaultResponsibility(a2lResp) || isRespBEG) && (columnPosition != 6)) {
      // if the responsibility is default , show the row in grey foreground
      configLabels.addLabel(ApicUiConstants.CONFIG_LABEL_DISABLE);
    }
    int columnIndex = this.layer.getColumnIndexByPosition(columnPosition);
    List<String> overrides = getOverrides(Integer.valueOf(columnIndex));
    if (overrides != null) {
      for (String configLabel : overrides) {
        configLabels.addLabel(configLabel);
      }
    }
    switch (columnPosition) {
      case 0:
        addConfigLabelForRespType(configLabels, a2lResp);
        break;
      case 2:
        if (!a2lResp.getRespType().equals(WpRespType.RB.getCode())) {
          // first name should not be editable for RB type
          configLabels.addLabel(CONFIG_LABEL_FIRST_NAME);
        }
        break;
      case 3:
        if (!a2lResp.getRespType().equals(WpRespType.RB.getCode())) {
          // last name should not be editable for RB type
          configLabels.addLabel(CONFIG_LABEL_LAST_NAME);
        }
        break;
      case 4:
        configLabels.addLabel(CONFIG_LABEL_DEPT);
        break;
      // deleted column
      case 6:
        addConfigForDelete(configLabels, a2lResp, isRespBEG);
        break;
      default:
        break;
    }
  }

  /**
   * @param a2lResp
   * @param configLabels
   * @param isRespBEG
   */
  private void addConfigForDelete(final LabelStack configLabels, final A2lResponsibility a2lResp,
      final boolean isRespBEG) {
    // add empty label for default responsibility
    if (A2lResponsibilityCommon.isDefaultResponsibility(a2lResp) || isRespBEG) {
      configLabels.addLabel(ApicUiConstants.EMPTY_LABEL);
    }
    // label for non editable
    else if (this.responsibilityPage.getWorkPkgResponsibilityBO().isEditNotApplicable(a2lResp)) {
      configLabels.addLabel(ApicUiConstants.CONFIG_LABEL_NOT_DELETABLE);
    }
    // label for editable
    else {
      configLabels.addLabel(ApicUiConstants.CHECK_BOX_EDITOR_CNG_LBL);
      configLabels.addLabel(ApicUiConstants.CHECK_BOX_CONFIG_LABEL);
    }
  }

  /**
   * @param configLabels
   * @param a2lResp
   */
  private void addConfigLabelForRespType(final LabelStack configLabels, final A2lResponsibility a2lResp) {
    try {
      if (this.responsibilityPage.getWorkPkgResponsibilityBO().canEditResp(a2lResp)) {
        configLabels.addLabel(CONFIG_RESP_TYPE);
      }
      else {
        configLabels.addLabel(CONFIG_RESP_TYPE_READ_ONLY);
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }
}
