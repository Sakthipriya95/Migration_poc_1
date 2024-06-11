/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.ViewVcdmDatasetAll;
import com.bosch.caltool.icdm.model.apic.VcdmDataSet;

/**
 * @author PDH2COB
 */
public class VcdmDataSetAllLoader extends AbstractBusinessObject<VcdmDataSet, ViewVcdmDatasetAll> {

  /**
   * @param serviceData Service Data
   */
  public VcdmDataSetAllLoader(final ServiceData serviceData) {
    super(serviceData, "vCDM Data Set All", ViewVcdmDatasetAll.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected VcdmDataSet createDataObject(final ViewVcdmDatasetAll entity) throws DataException {
    VcdmDataSet data = new VcdmDataSet();

    data.setAprjId(entity.getElementId());
    data.setAprjName(entity.getElementName());
    data.setCalibratedLabels(entity.getCalibratedLabels());
    data.setChangedLabels(entity.getChangedLabels());
    data.setCheckedLabels(entity.getCheckedLabels());
    data.setCompletedLabels(entity.getCompletedLabels());
    data.setCreatedDate(timestamp2Date(entity.getCreDate()));
    data.setCreatedUser(entity.getCreUser());
    data.setEaseedstModDate(timestamp2Date(entity.getEaseedstModDate()));
    data.setEaseedstState(entity.getEaseedstState());
    data.setId(entity.getEaseedstId());
    data.setModifiedDate(timestamp2Date(entity.getModDate()));
    data.setModifiedUser(entity.getModUser());
    data.setNoStateLabels(entity.getNoStateLabels());
    data.setParitionLabels(entity.getPartitionLabel());
    data.setPreLimLabels(entity.getPreLimLabels());
    data.setProductKey(entity.getProdKey());
    data.setProgramKey(entity.getProgKey());
    data.setRevisionNo(entity.getRevision().longValue());
    data.setTotalLabels(entity.getTotalLabels());

    return data;
  }

}
