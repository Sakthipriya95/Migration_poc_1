/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages.natsupport;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.editors.pages.ResponsibilityPage;
import com.bosch.caltool.icdm.common.ui.dialogs.CreateEditA2lRespDialog;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * The listener interface for receiving mouseEvent events. The class that is interested in processing a mouseEvent event
 * implements this interface, and the object created with that class is registered with a component using the
 * component's <code>addMouseEventListener<code> method. When the mouseEvent event occurs, that object's appropriate
 * method is invoked.
 *
 * @author dmo5cob
 */
public class RespNatTableMouseListener implements MouseListener {

  /**
   * Instance of responsibilty page
   */
  private final ResponsibilityPage responsibilityPage;

  /**
   * @param responsibilityPage ResponsibilityPage
   */
  public RespNatTableMouseListener(final ResponsibilityPage responsibilityPage) {
    this.responsibilityPage = responsibilityPage;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mouseUp(final MouseEvent mouseevent) {
    // NA

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mouseDown(final MouseEvent mouseevent) {
    // set selected col position
    this.responsibilityPage.setSelectedColPostn(LayerUtil.convertColumnPosition(this.responsibilityPage.getNATTable(),
        this.responsibilityPage.getNATTable().getColumnPositionByX(mouseevent.x),
        this.responsibilityPage.getCustomFilterGridLayer().getColumnHeaderDataLayer()));
    // set selected row position
    this.responsibilityPage.setSelectedRowPostn(LayerUtil.convertRowPosition(this.responsibilityPage.getNATTable(),
        this.responsibilityPage.getNATTable().getRowPositionByY(mouseevent.y),
        this.responsibilityPage.getCustomFilterGridLayer().getBodyDataLayer()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mouseDoubleClick(final MouseEvent mouseevent) {
    // get the column position
    int col = this.responsibilityPage.getNATTable().getColumnPositionByX(mouseevent.x);
    // get the row position
    int row = this.responsibilityPage.getNATTable().getRowPositionByY(mouseevent.y);

    ILayerCell cellByPosition = this.responsibilityPage.getNATTable().getCellByPosition(col, row);
    int columnIndex = cellByPosition.getColumnIndex();
    // get the selected object
    IStructuredSelection selection =
        (IStructuredSelection) this.responsibilityPage.getSelectionProvider().getSelection();
    if ((null != selection) && (null != selection.getFirstElement())) {
      A2lResponsibility selA2lResp = (A2lResponsibility) selection.getFirstElement();
      try {
        openingEditDialogue(columnIndex, selA2lResp);
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * @param columnIndex
   * @param selA2lResp
   * @throws ApicWebServiceException
   */
  private void openingEditDialogue(final int columnIndex, final A2lResponsibility selA2lResp)
      throws ApicWebServiceException {
    // check access rights and open the edit a2l resp dialog
    if (this.responsibilityPage.getWorkPkgResponsibilityBO().canEditResp(selA2lResp)) {
      if (isDoubleClickApplicable(columnIndex, selA2lResp)) {
        CreateEditA2lRespDialog createDialog = new CreateEditA2lRespDialog(Display.getCurrent().getActiveShell(),
            this.responsibilityPage.getWorkPkgResponsibilityBO(), selA2lResp, true);
        createDialog.open();
      }
    }
    else {
      CDMLogger.getInstance().warn("Edit Not Allowed!", Activator.PLUGIN_ID);
    }
  }

  /**
   * @param selA2lResp A2lResponsibility
   * @param columnIndex int
   * @return true if double click is applicable
   */
  private boolean isDoubleClickApplicable(final int columnIndex, final A2lResponsibility a2lResp) {
    boolean isDoubleClickApplicable = true;
    switch (columnIndex) {
      case ResponsibilityPage.COLUMN_RESP_TYPE:
        isDoubleClickApplicable = false;
        break;
      case ResponsibilityPage.COLUMN_FIRST_NAME:
        if (WpRespType.getType(a2lResp.getRespType()) != WpRespType.RB) {
          isDoubleClickApplicable = false;
        }
        break;
      case ResponsibilityPage.COLUMN_LAST_NAME:
        if (WpRespType.getType(a2lResp.getRespType()) != WpRespType.RB) {
          isDoubleClickApplicable = false;
        }
        break;
      case ResponsibilityPage.COLUMN_DEPT_NAME:
        isDoubleClickApplicable = false;
        break;
      default:
        break;
    }
    return isDoubleClickApplicable;
  }
}
