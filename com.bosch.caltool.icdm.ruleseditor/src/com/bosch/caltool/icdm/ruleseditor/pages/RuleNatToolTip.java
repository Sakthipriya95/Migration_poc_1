/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.pages;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.cell.CellDisplayConversionUtils;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.tooltip.NatTableContentTooltip;
import org.eclipse.swt.widgets.Event;

import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.ruleseditor.Activator;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author bru2cob
 */
public class RuleNatToolTip extends NatTableContentTooltip {


  private final ParamNatTable paramListPage;

  /**
   * @param natTable
   * @param tooltipRegions
   * @param paramListPage
   * @param strings
   * @param paramNatTable
   */
  public RuleNatToolTip(final NatTable natTable, final String[] tooltipRegions, final ParamNatTable paramListPage) {
    super(natTable, tooltipRegions);
    this.paramListPage = paramListPage;
  }

  /**
   * {@inheritDoc} Overriding the GetText method for Changing the Contants.
   */
  @Override
  protected String getText(final Event event) {
    int col = this.natTable.getColumnPositionByX(event.x);
    int row = this.natTable.getRowPositionByY(event.y);


    ILayerCell cell = this.natTable.getCellByPosition(col, row);
    if (cell != null) {
      // if the registered cell painter is the PasswordCellPainter, there will be no tooltip
      ICellPainter painter = this.natTable.getConfigRegistry().getConfigAttribute(CellConfigAttributes.CELL_PAINTER,
          DisplayMode.NORMAL, cell.getConfigLabels().getLabels());
      if (isVisibleContentPainter(painter)) {
        String tooltipValue = CellDisplayConversionUtils.convertDataType(cell, this.natTable.getConfigRegistry());

        // skip tooltips for filter row
        if (!cell.getConfigLabels().hasLabel(GridRegion.FILTER_ROW)) {
          Object rowObj =
              this.paramListPage.getCustomFilterGridLayer().getBodyDataProvider().getRowObject(cell.getRowIndex());
          if (rowObj instanceof IParameter) {
            IParameter param = (IParameter) rowObj;
            if (col == 1) {
              StringBuilder modifiedToolTip = new StringBuilder();
              String blackListTooltip = null;
              if (this.paramListPage.getParameterDataProvider().isComplianceParam(param)) {
                modifiedToolTip.append(ApicConstants.COMPLIANCE_PARAM).append("\n");
              }
              if (param.isBlackList()) {
                CommonDataBO dataBo = new CommonDataBO();
                try {
                  blackListTooltip = dataBo.getMessage(CDRConstants.PARAM, CDRConstants.BLACK_LIST_TOOLTIP);
                  modifiedToolTip.append(blackListTooltip).append("\n");
                }
                catch (ApicWebServiceException exp) {
                  CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
                }
              }
              if (this.paramListPage.getParameterDataProvider().isQssdParam(param)) {
                modifiedToolTip.append(ApicConstants.QSSD_PARAM).append("\n");
              }
              return modifiedToolTip.toString();
            }
          }
          return tooltipValue;
        }
      }
    }
    return null;
  }
}
