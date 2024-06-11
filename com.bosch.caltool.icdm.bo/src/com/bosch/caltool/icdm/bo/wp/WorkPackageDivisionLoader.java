/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.wp;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackage;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackageDivision;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;


/**
 * @author bne4cob
 */
public class WorkPackageDivisionLoader extends AbstractBusinessObject<WorkPackageDivision, TWorkpackageDivision> {


  /**
   * Name format of workpackage division
   */
  private static final String WP_DIV_NAME_FORMAT = "{0} - {1}";

  /**
   * @param serviceData service Data
   */
  public WorkPackageDivisionLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.WORKPACKAGE_DIVISION, TWorkpackageDivision.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected WorkPackageDivision createDataObject(final TWorkpackageDivision dbWpDiv) throws DataException {
    WorkPackageDivision wpDet = new WorkPackageDivision();

    setCommonFields(wpDet, dbWpDiv);

    TWorkpackage dbWp = dbWpDiv.getTWorkpackage();
    wpDet.setWpId(dbWp.getWpId());
    wpDet.setWpName(getLangSpecTxt(dbWp.getWpNameE(), dbWp.getWpNameG()));
    wpDet.setWpDesc(getLangSpecTxt(dbWp.getDescEng(), dbWp.getDescGer()));
    UserLoader userldr = new UserLoader(getServiceData());
    if (dbWpDiv.getTabvApicContactUser() != null) {
      wpDet.setContactPersonId(dbWpDiv.getTabvApicContactUser().getUserId());
      wpDet.setCntctPersonDispNm(
          (userldr.getDataObjectByID(dbWpDiv.getTabvApicContactUser().getUserId())).getDescription());
    }
    if (dbWpDiv.getTabvApicSecondContactUser() != null) {
      wpDet.setContactPersonSecondId(dbWpDiv.getTabvApicSecondContactUser().getUserId());
      wpDet.setCntctPersonSecDispNm(
          (userldr.getDataObjectByID(dbWpDiv.getTabvApicSecondContactUser().getUserId())).getDescription());
    }
    wpDet.setWpIdMcr(dbWpDiv.getWpIdMcr());

    if (dbWpDiv.getTWpResource() != null) {
      wpDet.setWpGroup(dbWpDiv.getTWpResource().getResourceCode());
      wpDet.setWpResource(dbWpDiv.getTWpResource().getResourceCode());
      wpDet.setWpResId(dbWpDiv.getTWpResource().getWpResId());
    }
    TabvAttrValue dbDivNameVal = dbWpDiv.getTabvAttrValue();
    wpDet.setDivAttrValId(dbDivNameVal.getValueId());
    wpDet.setIccRelevantFlag(dbWpDiv.getIccRelevantFlag());

    wpDet.setDivName(getLangSpecTxt(dbDivNameVal.getTextvalueEng(), dbDivNameVal.getTextvalueGer()));
    wpDet.setDeleted(dbWpDiv.getDeleteFlag());

    wpDet.setName(buildName(wpDet.getWpName(), wpDet.getDivName()));
    wpDet.setDescription(getLangSpecTxt(dbWp.getDescEng(), dbWp.getDescGer()));

    wpDet.setWpdComment(dbWpDiv.getWpdComment());

    wpDet.setCrpObdRelevantFlag(dbWpDiv.getCrpObdRelevantFlag());
    wpDet.setCrpObdComment(dbWpDiv.getCrpObdComment());
    wpDet.setCrpEmissionRelevantFlag(dbWpDiv.getCrpEmissionRelevantFlag());
    wpDet.setCrpEmissionComment(dbWpDiv.getCrpEmissionComment());
    wpDet.setCrpSoundRelevantFlag(dbWpDiv.getCrpSoundRelevantFlag());
    wpDet.setCrpSoundComment(dbWpDiv.getCrpSoundComment());

    return wpDet;
  }

  /**
   * @param divId Division ID(Attribute value ID)
   * @return set of WorkPackageDetails
   * @throws DataException when object cannot be created
   */
  private Set<WorkPackageDivision> getWorkPackageDivByDivID(final Long divId, final String iccRelevantFlag)
      throws DataException {
    Set<WorkPackageDivision> retSet = new HashSet<>();
    TypedQuery<TWorkpackageDivision> tQuery;
    String nqFindByDivId = TWorkpackageDivision.NQ_FIND_BY_DIV_ID;
    if (ApicConstants.CODE_YES.equals(iccRelevantFlag)) {
      nqFindByDivId = TWorkpackageDivision.NQ_FIND_BY_DIV_ID_WITH_ICC_RELEVANT;
    }
    else if (ApicConstants.CODE_NO.equals(iccRelevantFlag)) {
      nqFindByDivId = TWorkpackageDivision.NQ_FIND_BY_DIV_ID_WITHOUT_ICC_RELEVANT;
    }
    tQuery = getEntMgr().createNamedQuery(nqFindByDivId, TWorkpackageDivision.class);
    tQuery.setParameter("divValueId", divId);

    for (TWorkpackageDivision dbWpDiv : tQuery.getResultList()) {
      retSet.add(createDataObject(dbWpDiv));
    }
    return retSet;
  }

  /**
   * @param divId Division ID(Attribute value ID)
   * @param includeDeleted deleted flag
   * @param iccRelevantFlag pass true if only icc relevant wp are needed, pass null if all wps need to be fetched
   * @return set of WorkPackageDetails
   * @throws DataException when object cannot be created
   */
  public Set<WorkPackageDivision> getWorkPackageDivByDivID(final Long divId, final boolean includeDeleted,
      final String iccRelevantFlag)
      throws DataException {
    Set<WorkPackageDivision> retSet = new HashSet<>();

    if (includeDeleted) {
      getAllWorkpackageDivisions(divId, retSet, iccRelevantFlag);
    }
    else {
      retSet = getWorkPackageDivByDivID(divId, iccRelevantFlag);
    }
    return retSet;
  }

  /**
   * @param divId
   * @param retSet
   * @param iccRelevantFlag
   * @throws DataException
   */
  private void getAllWorkpackageDivisions(final Long divId, final Set<WorkPackageDivision> retSet,
      final String iccRelevantFlag)
      throws DataException {
    String nqFindAllByDivId = TWorkpackageDivision.NQ_FIND_ALL_BY_DIV_ID;
    if (ApicConstants.CODE_YES.equals(iccRelevantFlag)) {
      nqFindAllByDivId = TWorkpackageDivision.NQ_FIND_ALL_BY_DIV_ID_WITH_ICC_RELEVANT;
    }
    else if (ApicConstants.CODE_NO.equals(iccRelevantFlag)) {
      nqFindAllByDivId = TWorkpackageDivision.NQ_FIND_ALL_BY_DIV_ID_WITHOUT_ICC_RELEVANT;
    }
    TypedQuery<TWorkpackageDivision> tQuery =
        getEntMgr().createNamedQuery(nqFindAllByDivId, TWorkpackageDivision.class);
    tQuery.setParameter("divValueId", divId);

    for (TWorkpackageDivision dbWpDiv : tQuery.getResultList()) {
      retSet.add(createDataObject(dbWpDiv));
    }
  }


  /**
   * @param wpId Workpackage ID
   * @return set of WorkPackageDetails
   * @throws DataException when object cannot be created
   */
  public Set<WorkPackageDivision> getWorkPackageDivByWpID(final Long wpId) throws DataException {
    Set<WorkPackageDivision> retSet = new HashSet<>();

    TypedQuery<TWorkpackage> tQuery = getEntMgr().createNamedQuery(TWorkpackage.NQ_FIND_BY_WP_ID, TWorkpackage.class);
    tQuery.setParameter("wpId", wpId);

    List<TWorkpackage> workpackage = tQuery.getResultList();

    if (CommonUtils.isNotEmpty(workpackage)) {
      TWorkpackage dbWorkPackage = workpackage.get(0);
      for (TWorkpackageDivision dbWpDiv : dbWorkPackage.getTWorkpackageDivisions()) {
        retSet.add(createDataObject(dbWpDiv));
      }
    }
    return retSet;
  }

  private static String buildName(final String wpName, final String divName) {
    return MessageFormat.format(WP_DIV_NAME_FORMAT, wpName, divName);
  }

}
