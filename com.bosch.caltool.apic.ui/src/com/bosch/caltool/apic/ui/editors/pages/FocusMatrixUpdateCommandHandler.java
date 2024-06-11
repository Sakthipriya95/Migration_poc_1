/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;

import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.command.UpdateDataCommand;
import org.eclipse.nebula.widgets.nattable.layer.event.CellVisualChangeEvent;

import com.bosch.caltool.apic.ui.util.PIDCPageEditUtil;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixAttributeClientBO;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixDataHandler;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.ProjectAttributesUpdationModel;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.nattable.CustomFilterGridLayer;

/**
 * @author mkl2cob
 */
public class FocusMatrixUpdateCommandHandler extends AbstractLayerCommandHandler<UpdateDataCommand> {

//  REMARK_COL_NUMBER
  private static final int REMARK_COL_NUMBER = 3;
//  data handler instance
  private final FocusMatrixDataHandler dataHandler;
//  ucFilterGridLayer instance
  private final CustomFilterGridLayer<FocusMatrixAttributeClientBO> ucFilterGridLayer;


  /**
   * @param dataHandler FocusMatrixDataHandler
   * @param ucFilterGridLayer
   */
  public FocusMatrixUpdateCommandHandler(final FocusMatrixDataHandler dataHandler,
      final CustomFilterGridLayer<FocusMatrixAttributeClientBO> ucFilterGridLayer) {
    this.dataHandler = dataHandler;
    this.ucFilterGridLayer = ucFilterGridLayer;
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
//    get grid position and old, new values to update
    int rowPosition = this.ucFilterGridLayer.getRowIndexByPosition(command.getRowPosition());
    int columnPosition = this.ucFilterGridLayer.getColumnIndexByPosition(command.getColumnPosition());

    final IDataProvider dataProvider = this.ucFilterGridLayer.getBodyDataProvider();
    Object oldValue = dataProvider.getDataValue(columnPosition, rowPosition);
    Object newValue = command.getNewValue();

    if (columnPosition == (REMARK_COL_NUMBER)) {
      // for remark column
      if (((oldValue != null) && !CommonUtils.isEmptyString(oldValue.toString())) ? !oldValue.equals(newValue)
          : ((null != newValue) && !CommonUtils.isEmptyString(newValue.toString()))) {
        // if the old value and new value are not the same

        // set the new value to the nat table
        dataProvider.setDataValue(columnPosition, rowPosition, newValue);
        this.ucFilterGridLayer
            .fireLayerEvent(new CellVisualChangeEvent(this.ucFilterGridLayer, columnPosition, rowPosition));

        Object rowObject = this.ucFilterGridLayer.getBodyDataProvider().getRowObject(rowPosition);
        if (rowObject instanceof FocusMatrixAttributeClientBO) {
          // get the proj attr
          FocusMatrixAttributeClientBO fmAttr = (FocusMatrixAttributeClientBO) rowObject;
          PidcVersionAttribute pidcVersionAttribute =
              this.dataHandler.getPidcDataHandler().getPidcVersAttrMap().get(fmAttr.getAttribute().getId());
          PidcVersionAttribute projAttrClone = pidcVersionAttribute.clone();
          projAttrClone.setFmAttrRemark(null == newValue ? "" : newValue.toString());

          // call the project attribute updation model
          ProjectAttributesUpdationModel updationModel = new ProjectAttributesUpdationModel();
          updationModel.setPidcVersion(this.dataHandler.getPidcVersion());
          PIDCPageEditUtil editUtil = new PIDCPageEditUtil(this.dataHandler.getPidcVersionBO());
          editUtil.updateProjectAttribute(projAttrClone, updationModel);


        }

      }
    }
    return true;
  }

}
