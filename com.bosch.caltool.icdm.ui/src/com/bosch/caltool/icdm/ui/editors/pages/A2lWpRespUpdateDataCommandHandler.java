/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.pages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.command.UpdateDataCommand;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.event.CellVisualChangeEvent;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.util.IUIConstants;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpResponsibilityServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.CustomFilterGridLayer;


/**
 * @author pdh2cob
 */
public class A2lWpRespUpdateDataCommandHandler extends AbstractLayerCommandHandler<UpdateDataCommand> {

  /**
   * The {@link DataLayer} on which the data model updates should be executed.
   */
  private final CustomFilterGridLayer gridLayer;
  private final PidcA2l pidcA2l;


  /**
   * @param customFilterGridLayer The {@link DataLayer} on which the data model updates should be executed.
   * @param pidcA2l PidcA2l
   */
  public A2lWpRespUpdateDataCommandHandler(final CustomFilterGridLayer customFilterGridLayer, final PidcA2l pidcA2l) {
    this.gridLayer = customFilterGridLayer;
    this.pidcA2l = pidcA2l;
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

      if ((columnPosition == IUIConstants.COLUMN_INDEX_WP) || (columnPosition == IUIConstants.COLUMN_INDEX_WP_CUST)) {
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
    A2lWpResponsibility wpResp = ((A2lWpResponsibility) rowObject).clone();

    switch (colIndex) {
      case IUIConstants.COLUMN_INDEX_WP:
        wpResp.setName((String) newValue);
        break;
      case IUIConstants.COLUMN_INDEX_WP_CUST:
        wpResp.setWpNameCust((String) newValue);
        break;
      default:
        break;
    }

    try {
      List<A2lWpResponsibility> updateList = new ArrayList<>();
      updateList.add(wpResp);
      new A2lWpResponsibilityServiceClient().update(updateList, this.pidcA2l);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }

  }

}
