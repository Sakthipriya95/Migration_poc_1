/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.uc.UseCaseLoader;
import com.bosch.caltool.icdm.bo.uc.UseCaseSectionLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrix;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrixVersion;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrix;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixMappingData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;

/**
 * @author BNE4COB
 */
public class FocusMatrixLoader extends AbstractBusinessObject<FocusMatrix, TFocusMatrix> {

  /**
   * string query for the focus matrix
   */
  private static final int STRING_QUERY_SIZE = 40;

  /**
   * @param serviceData Service Data
   */
  public FocusMatrixLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.FOCUS_MATRIX, TFocusMatrix.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected FocusMatrix createDataObject(final TFocusMatrix entity) throws DataException {
    FocusMatrix object = new FocusMatrix();

    setCommonFields(object, entity);

    if (null != entity.getTabvUcpAttr()) {
      object.setUcpaId(entity.getTabvUcpAttr().getUcpaId());
    }
    object.setColorCode(entity.getColorCode());
    object.setComments(entity.getComments());
    object.setLink(entity.getLink());
    if (null != entity.getTabvUseCas()) {
      object.setUseCaseId(entity.getTabvUseCas().getUseCaseId());
    }
    if (null != entity.getTabvUseCaseSection()) {
      object.setSectionId(entity.getTabvUseCaseSection().getSectionId());
    }
    if (null != entity.getTabvAttribute()) {
      object.setAttrId(entity.getTabvAttribute().getAttrId());
    }
    object.setIsDeleted(ApicConstants.CODE_YES.equals(entity.getDeletedFlag()));
    object.setFmVersId(entity.getTFocusMatrixVersion().getFmVersId());

    return object;
  }


  /**
   * @param pidcVersId PIDC Version ID
   * @param model project attribute model
   * @return true, if PIDC Version has focus matrix
   */
  boolean hasFocusMatrixForPidcVersion(final Long pidcVersId, final PidcVersionAttributeModel model) {

    FocusMatrixVersionLoader fmvLdr = new FocusMatrixVersionLoader(getServiceData());

    Long fmVersId = fmvLdr.getWorkingSetVersionId(pidcVersId);
    if (fmVersId == null) {
      return false;
    }

    TFocusMatrixVersion fmVersEntity = fmvLdr.getEntityObject(fmVersId);

    UseCaseLoader ucLdr = new UseCaseLoader(getServiceData());
    UseCaseSectionLoader ucsLdr = new UseCaseSectionLoader(getServiceData());

    for (TFocusMatrix entity : fmVersEntity.getTFocusMatrixs()) {
      if (ApicConstants.CODE_YES.equals(entity.getDeletedFlag())) {
        // FM marked as deleted
        continue;
      }

      PidcVersionAttribute pidcAttr = model.getPidcVersAttr(entity.getTabvAttribute().getAttrId());
      if (isNotApplicableToFM(pidcAttr)) {
        // Condition pidcAttr is null includes deleted attributes, attributes missing due to attr dependency
        continue;
      }
      if (entity.getTabvUseCaseSection() == null) {
        if (ucLdr.isFocusMatrixRelevant(entity.getTabvUseCas().getUseCaseId())) {
          return true;
        }
      }
      else if (ucsLdr.isFocusMatrixRelevant(entity.getTabvUseCaseSection().getSectionId())) {
        return true;
      }
    }

    return false;

  }

  /**
   * @param pidcAttr
   * @return
   */
  private boolean isNotApplicableToFM(final PidcVersionAttribute pidcAttr) {
    return (pidcAttr == null) || !pidcAttr.isFocusMatrixApplicable();
  }

  /**
   * @param fmData
   * @return true if there are focus matrix entries (this check is done during attr mapping)
   * @throws DataException
   */
  public boolean isFMAvailableWhileMapping(final FocusMatrixMappingData fmData) throws DataException {
    return CommonUtils.isNotEmpty(getFocusMatrix(fmData));
  }

  /**
   * Checking the availablity of focus matrix while un mapping the data
   *
   * @param attribute
   * @return true if there are focus matrix entries
   * @throws DataException
   */
  public boolean isFocusMatrixAvailableWhileUnMapping(final FocusMatrixMappingData fmData) throws DataException {
    boolean isFocusMatrixAvailable = false;
    List<FocusMatrix> focusMatrixList = getFocusMatrix(fmData);
    for (FocusMatrix focusMatrixDetails : focusMatrixList) {
      Long ucpaId = focusMatrixDetails.getUcpaId();
      if (null != ucpaId) {
        isFocusMatrixAvailable = true;
        break;
      }
    }
    return isFocusMatrixAvailable;
  }

  /**
   * @param fmData
   * @return
   * @throws DataException
   */
  public List<FocusMatrix> getFocusMatrix(final FocusMatrixMappingData fmData) throws DataException {
    List<FocusMatrix> resultList = new ArrayList<>();
    StringBuilder strQuery = new StringBuilder(STRING_QUERY_SIZE);

    if (null != fmData.getUseCaseSectionId()) {
      strQuery = strQuery.append("SELECT fm FROM TFocusMatrix fm where  fm.tabvUseCaseSection.sectionId = ")
          .append(fmData.getUseCaseSectionId());
    }
    else if (null != fmData.getUseCaseId()) {
      strQuery = strQuery.append("SELECT fm FROM TFocusMatrix fm where  fm.tabvUseCas.useCaseId = ")
          .append(fmData.getUseCaseId());
    }
    else {
      return resultList;
    }
    if (null != fmData.getAttrId()) {
      strQuery.append(" and fm.tabvAttribute.attrId =").append(fmData.getAttrId());
    }
    if (null != fmData.getPidcVrsnId()) {
      strQuery.append(" and fm.tPidcVersion.pidcVersId =").append(fmData.getPidcVrsnId());
    }

    TypedQuery<TFocusMatrix> createQuery = getEntMgr().createQuery(strQuery.toString(), TFocusMatrix.class);

    List<TFocusMatrix> fmList = createQuery.getResultList();
    for (TFocusMatrix dbFm : fmList) {
      FocusMatrix fmDetails = getDataObjectByID(dbFm.getFmId());
      resultList.add(fmDetails);
    }
    return resultList;

  }


}
