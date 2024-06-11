/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.a2lcomparison;

import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWpParamInfo;
import com.bosch.caltool.icdm.client.bo.a2l.A2lParamCompareRowObject;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.CustomFilterGridLayer;

/**
 * @author bru2cob
 */
class A2lCompareNatTableToolTip extends DefaultToolTip {


  /** The nat table obj. */
  private final NatTable natTableObj;
  CustomFilterGridLayer a2lCompFilterGridLayer;

  /**
   * Instantiates a new simple nat table tool tip.
   *
   * @param natTable the nat table
   * @param a2lCompFilterGridLayer
   */
  public A2lCompareNatTableToolTip(final NatTable natTable, final CustomFilterGridLayer a2lCompFilterGridLayer) {
    super(natTable, ToolTip.NO_RECREATE, false);
    this.natTableObj = natTable;
    this.a2lCompFilterGridLayer = a2lCompFilterGridLayer;
  }

  /**
   * {@inheritDoc}
   */
  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.window.ToolTip#getToolTipArea(org.eclipse.swt.widgets.Event) Implementation here means the
   * tooltip is not redrawn unless mouse hover moves outside of the current cell (the combination of ToolTip.NO_RECREATE
   * style and override of this method).
   */
  @Override
  protected Object getToolTipArea(final Event event) {
    int col = this.natTableObj.getColumnPositionByX(event.x);
    int row = this.natTableObj.getRowPositionByY(event.y);

    return new Point(col, row);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getText(final Event event) {
    int col = this.natTableObj.getColumnPositionByX(event.x);
    int row = this.natTableObj.getRowPositionByY(event.y);
    ILayerCell cellByPosition = this.natTableObj.getCellByPosition(col, row);
    Object rowObject = A2lCompareNatTableToolTip.this.a2lCompFilterGridLayer.getBodyDataProvider()
        .getRowObject(cellByPosition.getRowIndex());
    if ((rowObject instanceof A2lParamCompareRowObject) && (col == 1)) {
      CommonDataBO dataBo = new CommonDataBO();
      A2lParamCompareRowObject compareRowObject = (A2lParamCompareRowObject) rowObject;
      int colIndex;
      // size gives dynamic cols + 3 static cols = total cols
      int totalColCount = compareRowObject.getA2lColumnDataMapper().getColumnIndexA2lWpDefVersIdMap().size() + 3;
      A2LWpParamInfo a2lParamInfoBo = null;
      for (colIndex = 3; colIndex < totalColCount; colIndex = colIndex + 6) {
        a2lParamInfoBo = compareRowObject.getParamInfo(colIndex);
        if (a2lParamInfoBo != null) {
          break;
        }
      }
      String blackListToolTip = null;

      try {
        blackListToolTip = dataBo.getMessage(CDRConstants.PARAM, CDRConstants.BLACK_LIST_TOOLTIP);
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      }
      StringBuilder toolTip = new StringBuilder();
      if (a2lParamInfoBo != null) {
        frameToolTipString(a2lParamInfoBo, blackListToolTip, toolTip);
      }

      if (toolTip.length() > 0) {
        return toolTip.substring(0, toolTip.length() - 1);
      }
    }
    return (String) cellByPosition.getDataValue();
  }

  /**
   * @param a2lParamInfoBo
   * @param blackListToolTip
   * @param toolTip
   */
  private void frameToolTipString(A2LWpParamInfo a2lParamInfoBo, String blackListToolTip, StringBuilder toolTip) {
    if (a2lParamInfoBo.isComplianceParam()) {
      toolTip.append(ApicConstants.COMPLIANCE_PARAM).append('\n');
    }
    if (a2lParamInfoBo.isBlackList()) {
      toolTip.append(blackListToolTip).append('\n');
    }
    if (a2lParamInfoBo.isReadOnly()) {
      toolTip.append(ApicConstants.READ_ONLY_PARAM).append('\n');
    }
    if (a2lParamInfoBo.isQssdParameter()) {
      toolTip.append(ApicConstants.QSSD_PARAM).append('\n');
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean shouldCreateToolTip(final Event event) {
    int col = this.natTableObj.getColumnPositionByX(event.x);
    int row = this.natTableObj.getRowPositionByY(event.y);
    ILayerCell cellByPosition = this.natTableObj.getCellByPosition(col, row);

    if (cellByPosition == null) {
      return false;
    }

    Object rowObject = A2lCompareNatTableToolTip.this.a2lCompFilterGridLayer.getBodyDataProvider()
        .getRowObject(cellByPosition.getRowIndex());
    if (rowObject instanceof A2lParamCompareRowObject) {
      A2lParamCompareRowObject compareRowObject = (A2lParamCompareRowObject) rowObject;
      int colIndex;
      // size gives dynamic cols + 3 static cols = total cols
      int totalColCount = compareRowObject.getA2lColumnDataMapper().getColumnIndexA2lWpDefVersIdMap().size() + 3;
      A2LWpParamInfo paramInfo = null;
      for (colIndex = 3; colIndex < totalColCount; colIndex = colIndex + 6) {
        paramInfo = compareRowObject.getParamInfo(colIndex);
        if (paramInfo != null) {
          break;
        }
      }
      if ((col == 1) && (paramInfo != null) && isParamTypeInList(paramInfo)) {
        return true;
      }
    }
    if (!(cellByPosition.getDataValue() instanceof String)) {
      return false;
    }
    String cellValue = (String) cellByPosition.getDataValue();
    if (CommonUtils.isEmptyString(cellValue)) {
      return false;
    }
    Rectangle currentBounds = cellByPosition.getBounds();
    cellByPosition.getLayer().getPreferredWidth();

    GC gcObj = new GC(this.natTableObj);
    Point size = gcObj.stringExtent(cellValue);

    return currentBounds.width < size.x;
  }

  /**
   * @param paramInfo
   * @return
   */
  private boolean isParamTypeInList(A2LWpParamInfo paramInfo) {
    return paramInfo.isBlackList() || (paramInfo.isReadOnly()) ||
        paramInfo.isComplianceParam() || paramInfo.isQssdParameter();
  }

}
