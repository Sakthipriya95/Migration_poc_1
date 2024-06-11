/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrix;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrixVersion;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrixVersionAttr;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrix;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersion;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersionAttr;


/**
 * @author BNE4COB
 */
public class FocusMatrixVersionLoader extends AbstractBusinessObject<FocusMatrixVersion, TFocusMatrixVersion> {

  /**
   * @param serviceData Service Data
   */
  public FocusMatrixVersionLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.FOCUS_MATRIX_VERSION, TFocusMatrixVersion.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected FocusMatrixVersion createDataObject(final TFocusMatrixVersion entity) throws DataException {
    FocusMatrixVersion object = new FocusMatrixVersion();

    setCommonFields(object, entity);

    object.setPidcVersId(entity.getTPidcVersion().getPidcVersId());
    object.setName(entity.getName());
    object.setRevNum(entity.getRevNumber());
    object.setStatus(entity.getStatus());
    if (null != entity.getReviewedUser()) {
      object.setReviewedUser(entity.getReviewedUser().getUserId());
    }
    object.setReviewedDate(timestamp2String(entity.getReviewedDate()));
    object.setLink(entity.getLink());
    object.setRemark(entity.getRemark());
    object.setRvwStatus(entity.getRvwStatus());

    return object;
  }


  /**
   * @param pidcVersionId PIDC Version ID
   * @return working set version id, or null if no working set version
   */
  public Long getWorkingSetVersionId(final Long pidcVersionId) {
    Long retId = null;
    for (TFocusMatrixVersion entity : new PidcVersionLoader(getServiceData()).getEntityObject(pidcVersionId)
        .getTFocusMatrixVersions()) {
      if (entity.getRevNumber() == 0L) {
        retId = entity.getFmVersId();
        break;
      }
    }
    return retId;
  }

  /**
   * @param fmVersionId Long
   * @return Map<Long, TFocusMatrix>
   * @throws DataException Exception while creating data object
   */
  public Map<Long, FocusMatrix> getFocusMatrixForVersion(final Long fmVersionId) throws DataException {
    Map<Long, FocusMatrix> focusMatrixMap = new HashMap<Long, FocusMatrix>();
    TFocusMatrixVersion fmVersion = getEntityObject(fmVersionId);
    Set<TFocusMatrix> tFocusMatrixs = fmVersion.getTFocusMatrixs();
    FocusMatrixLoader focusMatrixLoader = new FocusMatrixLoader(getServiceData());
    for (TFocusMatrix tFocusMatrix : tFocusMatrixs) {
      focusMatrixMap.put(tFocusMatrix.getFmId(), focusMatrixLoader.getDataObjectByID(tFocusMatrix.getFmId()));
    }
    return focusMatrixMap;

  }

  /**
   * @param fmVersionId Long
   * @return Map<Long, FocusMatrixVersionAttr>
   * @throws DataException exception while creating data object
   */
  public Map<Long, FocusMatrixVersionAttr> getFocusMatrixAttrForVersion(final Long fmVersionId) throws DataException {
    Map<Long, FocusMatrixVersionAttr> focusMatrixMap = new HashMap<Long, FocusMatrixVersionAttr>();
    TFocusMatrixVersion fmVersion = getEntityObject(fmVersionId);
    Set<TFocusMatrixVersionAttr> tFocusMatrixVersionAttr = fmVersion.getTFocusMatrixVersionAttrs();
    FocusMatrixVersionAttrLoader focusMatrixLoader = new FocusMatrixVersionAttrLoader(getServiceData());
    for (TFocusMatrixVersionAttr tFocusMatrixAttr : tFocusMatrixVersionAttr) {
      focusMatrixMap.put(tFocusMatrixAttr.getFmvAttrId(),
          focusMatrixLoader.getDataObjectByID(tFocusMatrixAttr.getFmvAttrId()));
    }
    return focusMatrixMap;
  }

}
