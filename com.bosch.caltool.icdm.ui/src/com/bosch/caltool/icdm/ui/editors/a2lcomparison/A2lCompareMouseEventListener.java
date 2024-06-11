/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.a2lcomparison;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWpParamInfo;
import com.bosch.caltool.icdm.client.bo.a2l.A2lParamColumnDataMapper;
import com.bosch.caltool.icdm.client.bo.a2l.A2lParamCompareRowObject;
import com.bosch.caltool.icdm.ui.dialogs.UpdateWpParamDetailsDialog;
import com.bosch.caltool.icdm.ui.util.IUIConstants;
import com.bosch.caltool.nattable.CustomFilterGridLayer;

/**
 * @author bru2cob
 */
class A2lCompareMouseEventListener implements MouseListener {

  /**
   * column headers row count
   */
  private static final int COL_HEADER_POSITION = 3;
  /** The nat table obj. */
  private final NatTable natTable;

  /** CustomeFilterGridLayer Obj **/
  CustomFilterGridLayer a2lCompFilterGridLayer;

  /**
   * Instantiates a new simple nat table tool tip.
   *
   * @param natTable the nat table
   * @param a2lCompFilterGridLayer
   */
  public A2lCompareMouseEventListener(final NatTable natTable, final CustomFilterGridLayer a2lCompFilterGridLayer) {
    super();
    this.natTable = natTable;
    this.a2lCompFilterGridLayer = a2lCompFilterGridLayer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mouseDoubleClick(final MouseEvent arg0) {
    // Not yet Implemented
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mouseDown(final MouseEvent mouseevent) {
    CustomFilterGridLayer customFilterGridLayer =
        (CustomFilterGridLayer) A2lCompareMouseEventListener.this.natTable.getLayer();
    final SelectionLayer selectionLayer = customFilterGridLayer.getBodyLayer().getSelectionLayer();
    int rowNumber = A2lCompareMouseEventListener.this.natTable.getRowPositionByY(mouseevent.y);
    int rowPosition = LayerUtil.convertRowPosition(A2lCompareMouseEventListener.this.natTable,
        A2lCompareMouseEventListener.this.natTable.getRowPositionByY(mouseevent.y), selectionLayer);
    int columnPosition = LayerUtil.convertColumnPosition(A2lCompareMouseEventListener.this.natTable,
        A2lCompareMouseEventListener.this.natTable.getColumnPositionByX(mouseevent.x),
        customFilterGridLayer.getDummyDataLayer());
    if (rowPosition >= 0) {
      Object rowObject =
          A2lCompareMouseEventListener.this.a2lCompFilterGridLayer.getBodyDataProvider().getRowObject(rowPosition);
      if ((rowNumber > COL_HEADER_POSITION) && (rowObject instanceof A2lParamCompareRowObject)) {
        A2lParamCompareRowObject compareRowObject = (A2lParamCompareRowObject) rowObject;
        A2lParamColumnDataMapper columnDataMapper = compareRowObject.getA2lColumnDataMapper();
        String columnType = columnDataMapper.getColumnIndexFlagMap().get(columnPosition);

        if ((columnType != null) &&
            (IUIConstants.WORK_PACKAGE.equals(columnType) || IUIConstants.RESPONSIBILTY.equals(columnType))) {
          A2LWpParamInfo paramInfo = compareRowObject.getParamInfo(columnPosition);
          A2LWPInfoBO a2LWPInfoBO = compareRowObject.getA2lWpInfoBO(columnPosition);
          openEditPar2WPDialogIfParamEditAllowed(paramInfo, a2LWPInfoBO);
        }
      }
    }
  }

  /**
   * Method to check if Edit is allowed for the parameter and open edit dialog if allowed
   *
   * @param paramInfo
   * @param a2LWPInfoBO
   */
  private void openEditPar2WPDialogIfParamEditAllowed(final A2LWpParamInfo paramInfo, final A2LWPInfoBO a2LWPInfoBO) {
    if (a2LWPInfoBO.isParamEditAllowed()) {
      // Open EditPar2WpDialog
      UpdateWpParamDetailsDialog editPar2WPDialog =
          new UpdateWpParamDetailsDialog(Display.getCurrent().getActiveShell(), paramInfo,
              a2LWPInfoBO.getA2lWpParamMappingModel().getSelectedWpDefnVersionId(), a2LWPInfoBO);
      editPar2WPDialog.open();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mouseUp(final MouseEvent arg0) {
    // Not yet Implemented

  }


}
