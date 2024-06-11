/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.emr;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrCategory;
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrColumn;
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrColumnValue;
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrEmissionStandard;
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrFile;
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrFileData;
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrMeasureUnit;
import com.bosch.caltool.icdm.model.emr.EmrFileData;

/**
 * @author bru2cob
 */
public class EmrFileDataCommand extends AbstractCommand<EmrFileData, EmrFileDataLoader> {

  /**
   * @param serviceData service Data
   * @param inputDef input Definition
   * @throws IcdmException error when initializing
   */
  protected EmrFileDataCommand(final ServiceData serviceData, final EmrFileData inputDef, final boolean isCreate)
      throws IcdmException {
    super(serviceData, inputDef, new EmrFileDataLoader(serviceData),
        isCreate ? COMMAND_MODE.CREATE : COMMAND_MODE.DELETE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TEmrFileData fileData = new TEmrFileData();
    fileData.setFuelTypeNumber(getInputData().getFuelTypeNumber());
    fileData.setValueNum(getInputData().getValueNum());
    fileData.setValueText(getInputData().getValueText());

    TEmrCategory tEmrCat = new EmrCategoryLoader(getServiceData()).getEntityObject(getInputData().getCategoryId());
    fileData.setTEmrCategory(tEmrCat);
    tEmrCat.addTEmrFileData(fileData);


    TEmrColumn tEmrColumn = new EmrColumnLoader(getServiceData()).getEntityObject(getInputData().getColId());
    fileData.setTEmrColumn(tEmrColumn);
    tEmrColumn.addTEmrFileData(fileData);


    TEmrColumnValue tEmrColVal =
        new EmrColumnValueLoader(getServiceData()).getEntityObject(getInputData().getColValId());
    if (null != tEmrColVal) {
      fileData.setTEmrColumnValue(tEmrColVal);
      tEmrColVal.addTEmrFileData(fileData);
    }


    EmrEmissionStandardLoader emnStdLoader = new EmrEmissionStandardLoader(getServiceData());
    TEmrEmissionStandard tEmrEmsStdProc = emnStdLoader.getEntityObject(getInputData().getEmissionStdProcedureId());
    fileData.setTEmrEmissionStandardProcedure(tEmrEmsStdProc);
    emnStdLoader.getEntityObject(getInputData().getEmissionStdProcedureId()).addTEmrFileData1(fileData);


    TEmrEmissionStandard tEmrEmsStdTestCase = emnStdLoader.getEntityObject(getInputData().getEmissionStdTestcaseId());
    if (null != tEmrEmsStdTestCase) {
      fileData.setTEmrEmissionStandardTestcase(tEmrEmsStdTestCase);
      emnStdLoader.getEntityObject(getInputData().getEmissionStdTestcaseId()).addTEmrFileData2(fileData);
    }


    TEmrMeasureUnit tEmrMeasureUnit =
        new EmrMeasureUnitLoader(getServiceData()).getEntityObject(getInputData().getMeasureUnitId());
    if (null != tEmrMeasureUnit) {
      fileData.setTEmrMeasureUnit(tEmrMeasureUnit);
      tEmrMeasureUnit.addTEmrFileData(fileData);
    }


    TEmrFile tEmrFile = new EmrFileLoader(getServiceData()).getEntityObject(getInputData().getFileId());
    fileData.setTEmrFile(tEmrFile);
    tEmrFile.addTEmrFileData(fileData);


    setUserDetails(COMMAND_MODE.CREATE, fileData);

    persistEntity(fileData);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    TEmrFileData entity = new EmrFileDataLoader(getServiceData()).getEntityObject(getInputData().getId());

    TEmrCategory emrCat = new EmrCategoryLoader(getServiceData()).getEntityObject(entity.getTEmrCategory().getCatId());
    emrCat.getTEmrFileData().remove(entity);

    TEmrColumn emrColumn = new EmrColumnLoader(getServiceData()).getEntityObject(entity.getTEmrColumn().getColId());
    emrColumn.getTEmrFileData().remove(entity);

    if (null != entity.getTEmrColumnValue()) {
      TEmrColumnValue emrColVal =
          new EmrColumnValueLoader(getServiceData()).getEntityObject(entity.getTEmrColumnValue().getColValueId());
      emrColVal.getTEmrFileData().remove(entity);
    }

    TEmrEmissionStandard emrEmissionStd1 = new EmrEmissionStandardLoader(getServiceData())
        .getEntityObject(entity.getTEmrEmissionStandardProcedure().getEmsId());
    emrEmissionStd1.getTEmrFileData1().remove(entity);
    emrEmissionStd1.getTEmrFileData2().remove(entity);

    if (null != entity.getTEmrEmissionStandardTestcase()) {
      TEmrEmissionStandard emrEmissionStd2 = new EmrEmissionStandardLoader(getServiceData())
          .getEntityObject(entity.getTEmrEmissionStandardTestcase().getEmsId());
      if (null != emrEmissionStd2) {
        emrEmissionStd2.getTEmrFileData1().remove(entity);
        emrEmissionStd2.getTEmrFileData2().remove(entity);
      }
    }

    TEmrFile emrFile = new EmrFileLoader(getServiceData()).getEntityObject(entity.getTEmrFile().getEmrFileId());
    emrFile.getTEmrFileData().remove(entity);

    if (null != entity.getTEmrMeasureUnit()) {
      TEmrMeasureUnit emrMu =
          new EmrMeasureUnitLoader(getServiceData()).getEntityObject(entity.getTEmrMeasureUnit().getMuId());
      if (null != emrMu) {
        emrMu.getTEmrFileData().remove(entity);
      }
    }
    getEm().remove(entity);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // TODO Auto-generated method stub

  }

}
