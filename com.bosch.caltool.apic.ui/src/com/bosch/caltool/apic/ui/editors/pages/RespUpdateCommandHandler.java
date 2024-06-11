/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.command.UpdateDataCommand;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.event.CellVisualChangeEvent;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lResponsibilityServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.CustomFilterGridLayer;

/**
 * @author apj4cob
 */
public class RespUpdateCommandHandler extends AbstractLayerCommandHandler<UpdateDataCommand> {

  /**
   * The {@link DataLayer} on which the data model updates should be executed.
   */
  private final CustomFilterGridLayer gridLayer;
  private MessageDialog infoMessageDialog;
  private final ResponsibilityPage responsibilityPage;

  /**
   * @param customFilterGridLayer
   * @param responsibilityPage
   */
  public RespUpdateCommandHandler(final CustomFilterGridLayer customFilterGridLayer,
      final ResponsibilityPage responsibilityPage) {
    this.gridLayer = customFilterGridLayer;
    this.responsibilityPage = responsibilityPage;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<UpdateDataCommand> getCommandClass() {
    return UpdateDataCommand.class;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean doCommand(final UpdateDataCommand command) {
    try {

      int rowPosition = command.getRowPosition();

      int columnPosition = command.getColumnPosition();
      if ((columnPosition == ResponsibilityPage.COLUMN_FIRST_NAME) ||
          (columnPosition == ResponsibilityPage.COLUMN_LAST_NAME) ||
          (columnPosition == ResponsibilityPage.COLUMN_DEPT_NAME) ||
          (columnPosition == ResponsibilityPage.COLUMN_RESP_TYPE)) {
        final IDataProvider dataProvider = this.gridLayer.getBodyDataProvider();
        Object newValue = command.getNewValue();
        dataProvider.setDataValue(columnPosition, rowPosition, newValue);
        this.gridLayer.fireLayerEvent(new CellVisualChangeEvent(this.gridLayer, columnPosition, rowPosition));
        boolean isAllowed = false;
        final IStructuredSelection selection =
            (IStructuredSelection) this.responsibilityPage.getSelectionProvider().getSelection();
        if (selection.size() >= 1) {
          if (columnPosition == ResponsibilityPage.COLUMN_RESP_TYPE) {
            for (WpRespType respType : WpRespType.values()) {
              if (newValue.toString().equalsIgnoreCase(respType.getDispName())) {
                newValue = respType.getDispName();
                isAllowed = true;
                break;
              }
            }
          }
          if ((columnPosition != ResponsibilityPage.COLUMN_RESP_TYPE) || isAllowed) {
            // send the entire selection to avoid multiple service calls
            updateValue(newValue, columnPosition, selection);
          }
          else {
            showErrorDialog("Responsibility type " + newValue + " does not exist!");
          }
          // clear the selection to avoid the update again or showing the error dialog multiple times
          this.responsibilityPage.getSelectionProvider().setSelection(StructuredSelection.EMPTY);
        }
      }
      return true;
    }
    catch (UnsupportedOperationException e) {
      CDMLogger.getInstance().errorDialog("Failed to update value to: " + command.getNewValue(), e,
          com.bosch.caltool.apic.ui.Activator.PLUGIN_ID);

      return false;
    }
  }

  /**
   * @param string
   */
  private void showErrorDialog(final String msg) {
    if ((null != this.infoMessageDialog) && (null != this.infoMessageDialog.getShell()) &&
        !this.infoMessageDialog.getShell().isDisposed()) {
      return;
    }
    CDMLogger.getInstance().error(msg, Activator.PLUGIN_ID);
    Shell infoShell = new Shell();
    this.infoMessageDialog =
        new MessageDialog(infoShell, "Information", null, msg, MessageDialog.INFORMATION, new String[] { "OK" }, 0);
    this.infoMessageDialog.open();
  }

  /**
   * Method to update edited value in DB
   *
   * @param newValue
   * @param rowObject
   * @param selection
   */
  private void updateValue(final Object newValue, final int colIndex, final IStructuredSelection selection) {
    List<A2lResponsibility> respList = new ArrayList<>(selection.toList());
    List<A2lResponsibility> updateList = new ArrayList<>();

    for (A2lResponsibility a2lResp : respList) {
      A2lResponsibility resp = a2lResp.clone();

      switch (colIndex) {
        case ResponsibilityPage.COLUMN_RESP_TYPE:
          setRespType(newValue, resp);
          break;
        case ResponsibilityPage.COLUMN_FIRST_NAME:
          resp.setLFirstName((String) newValue);
          break;
        case ResponsibilityPage.COLUMN_LAST_NAME:
          resp.setLLastName((String) newValue);
          break;
        case ResponsibilityPage.COLUMN_DEPT_NAME:
          setDeptDetails(newValue, resp);
          break;
        default:
          break;
      }
      updateList.add(resp);
    }

    try {
      new A2lResponsibilityServiceClient().update(updateList);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, com.bosch.caltool.apic.ui.Activator.PLUGIN_ID);
    }

  }

  /**
   * @param newValue
   * @param resp
   */
  private void setRespType(final Object newValue, final A2lResponsibility resp) {
    if (null != newValue) {
      WpRespType newRespType = WpRespType.getTypeFromUI((String) newValue);
      WpRespType oldRespType = WpRespType.getType(resp.getRespType());
      if (!oldRespType.getDispName().equals(newRespType.getDispName())) {
        resp.setRespType(newRespType.getCode());
        if ((newRespType == WpRespType.RB) && CommonUtils.isEmptyString(resp.getLDepartment())) {
          resp.setLDepartment(resp.getAliasName());
        }
      }
    }
  }

  /**
   * @param newValue
   * @param resp
   */
  private void setDeptDetails(final Object newValue, final A2lResponsibility resp) {
    String deptName = (String) newValue;
    resp.setLDepartment(deptName);
  }
}