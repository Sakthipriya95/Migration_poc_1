/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;

import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.command.UpdateDataCommand;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.event.CellVisualChangeEvent;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWorkPackageServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.CustomFilterGridLayer;

/**
 * @author elm1cob
 */
public class WorkPkgsUpdateCmndHandler extends AbstractLayerCommandHandler<UpdateDataCommand> {

  /**
   * The {@link DataLayer} on which the data model updates should be executed.
   */
  private final CustomFilterGridLayer gridLayer;

  /**
   * @param customFilterGridLayer The {@link DataLayer} on which the data model updates should be executed.
   */
  public WorkPkgsUpdateCmndHandler(final CustomFilterGridLayer customFilterGridLayer) {
    this.gridLayer = customFilterGridLayer;
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

      final IDataProvider dataProvider = this.gridLayer.getBodyDataProvider();
      Object oldValue = dataProvider.getDataValue(columnPosition, rowPosition);
      Object newValue = command.getNewValue();

      if (((oldValue != null) && !CommonUtils.isEmptyString(oldValue.toString())) ? !oldValue.equals(newValue)
          : ((null != newValue) && !CommonUtils.isEmptyString(newValue.toString()))) {
        dataProvider.setDataValue(columnPosition, rowPosition, newValue);
        this.gridLayer.fireLayerEvent(new CellVisualChangeEvent(this.gridLayer, columnPosition, rowPosition));
        Object rowObject = this.gridLayer.getBodyDataProvider().getRowObject(rowPosition);
        updateValue(newValue, rowObject, columnPosition);

      }


      return true;
    }
    catch (UnsupportedOperationException e) {
      CDMLogger.getInstance().errorDialog("Failed to update value to: " + command.getNewValue(), e,
          Activator.PLUGIN_ID);

      return false;
    }
  }

  /**
   * Method to update edited value in DB
   *
   * @param newValue
   * @param rowObject
   */
  private void updateValue(final Object newValue, final Object rowObject, final int colIndex) {
    A2lWorkPackage workPkg = ((A2lWorkPackage) rowObject).clone();

    switch (colIndex) {
      case ApicUiConstants.COLUMN_INDEX_0:
        workPkg.setName((String) newValue);
        break;
      case ApicUiConstants.COLUMN_INDEX_1:
        workPkg.setDescription((String) newValue);
        break;
      case ApicUiConstants.COLUMN_INDEX_2:
        workPkg.setNameCustomer((String) newValue);
        break;
      default:
        break;
    }

    try {
      new A2lWorkPackageServiceClient().update(workPkg);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }

  }
}
