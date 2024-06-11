/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.wp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.QuestionnaireLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireRespVersion;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireResponse;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackage;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackageDivision;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;
import com.bosch.caltool.icdm.model.wp.WorkPkg;


/**
 * @author bne4cob
 */
public class WorkPkgLoader extends AbstractBusinessObject<WorkPkg, TWorkpackage> {

  /**
   * @param serviceData service Data
   */
  public WorkPkgLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.WORK_PACKAGE, TWorkpackage.class);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected WorkPkg createDataObject(final TWorkpackage entity) {
    WorkPkg data = new WorkPkg();

    setCommonFields(data, entity);

    String wpNameE = null != entity.getWpNameE() ? entity.getWpNameE().trim() : null;
    String wpNameG = null != entity.getWpNameG() ? entity.getWpNameG().trim() : null;
    data.setName(getLangSpecTxt(wpNameE, wpNameG));
    data.setWpNameEng(wpNameE);
    data.setWpNameGer(wpNameG);

    data.setDescription(getLangSpecTxt(entity.getDescEng(), entity.getDescGer()));
    data.setWpDescEng(entity.getDescEng());
    data.setWpDescGer(entity.getDescGer());

    data.setWpMasterlistId(
        CommonUtils.isNotNull(entity.gettWpmlWpMasterList()) ? entity.gettWpmlWpMasterList().getId() : null);

    data.setDeleted(CommonUtilConstants.CODE_YES.equals(entity.getDeleteFlag()));

    return data;
  }

  /**
   * @return all work packages
   */
  public Set<WorkPkg> findAll() {
    Set<WorkPkg> retSet = new HashSet<>();

    TypedQuery<TWorkpackage> tQuery = getEntMgr().createNamedQuery(TWorkpackage.NQ_FIND_ALL, TWorkpackage.class);

    for (TWorkpackage entity : tQuery.getResultList()) {
      retSet.add(createDataObject(entity));
    }

    return retSet;
  }

  /**
   * Load all the workpackage divisions
   *
   * @return
   * @throws IcdmException
   */
  public ConcurrentMap<AttributeValue, List<WorkPkg>> getMappedWpsDiv() throws IcdmException {
    ConcurrentMap<AttributeValue, List<WorkPkg>> mappedWrkPkgMap = new ConcurrentHashMap<>();

    final TypedQuery<TWorkpackageDivision> typeQuery =
        getEntMgr().createNamedQuery("TWorkpackageDivision.findAll", TWorkpackageDivision.class);
    typeQuery.setHint(ApicConstants.READ_ONLY, "true");
    typeQuery.setHint(ApicConstants.SHARED_CACHE, "true");
    final List<TWorkpackageDivision> resultList = typeQuery.getResultList();
    for (TWorkpackageDivision workpackageDivision : resultList) {
      List<WorkPkg> mappedWps = new ArrayList<>();
      ServiceData serviceData = getServiceData();
      AttributeValue attrValue =
          new AttributeValueLoader(serviceData).getDataObjectByID(workpackageDivision.getTabvAttrValue().getValueId());
      WorkPkg workPkg = createDataObject(workpackageDivision.getTWorkpackage());

      if (mappedWrkPkgMap.get(attrValue) == null) {
        mappedWps.add(workPkg);
        mappedWrkPkgMap.put(attrValue, mappedWps);
      }
      else {
        mappedWps = mappedWrkPkgMap.get(attrValue);
        mappedWps.add(workPkg);
      }
    }
    return mappedWrkPkgMap;
  }

  /**
   * @return the wrkPkgDivisionMap
   * @throws IcdmException
   */
  public ConcurrentMap<Long, WorkPackageDivision> getWrkPkgDetailsMap() throws IcdmException {
    ConcurrentMap<Long, WorkPackageDivision> wrkPkgDetailsMap = new ConcurrentHashMap<>();

    WorkPackageDivisionLoader workPackageDivisionLoader = new WorkPackageDivisionLoader(getServiceData());
    final TypedQuery<TWorkpackageDivision> typeQuery =
        getEntMgr().createNamedQuery("TWorkpackageDivision.findAll", TWorkpackageDivision.class);
    typeQuery.setHint(ApicConstants.READ_ONLY, "true");
    typeQuery.setHint(ApicConstants.SHARED_CACHE, "true");
    final List<TWorkpackageDivision> resultList = typeQuery.getResultList();
    for (TWorkpackageDivision workpackageDivision : resultList) {
      wrkPkgDetailsMap.put(workpackageDivision.getWpDivId(),
          workPackageDivisionLoader.getDataObjectByID(workpackageDivision.getWpDivId()));
    }
    return wrkPkgDetailsMap;
  }


  /**
   * @param division (DGS/BEG/DS)
   * @return Map of Workpackages for the division
   * @throws IcdmException
   */
  public ConcurrentMap<Long, WorkPkg> getIcdmWorkPackageMapForDiv(final AttributeValue division) throws IcdmException {

    ConcurrentMap<Long, WorkPkg> icdmWorkPackageMapForDiv = new ConcurrentHashMap<>();
    // Review 237201
    for (WorkPackageDivision wpDiv : getWrkPkgDetailsMap().values()) {

      if (!CommonUtils.isEqualIgnoreCase(wpDiv.getDeleted(), CommonUtilConstants.CODE_YES) &&
          !getDataObjectByID(wpDiv.getWpId()).isDeleted() && wpDiv.getDivAttrValId().equals(division.getId())) {
        icdmWorkPackageMapForDiv.put(wpDiv.getWpId(), getDataObjectByID(wpDiv.getWpId()));
      }
    }

    return icdmWorkPackageMapForDiv;
  }

  /**
   * Method to get the review qnnaire response list based on Pidc version id
   *
   * @param pidcVerId
   * @return
   * @throws IcdmException
   */
  public SortedSet<WorkPkg> getReviewQnaireRespList(final Long pidcVerId) throws IcdmException {
    WorkPkgLoader workPackageLoader = new WorkPkgLoader(getServiceData());
    SortedSet<WorkPkg> qnaireInUseSet = new TreeSet<>();
    TypedQuery<TRvwQnaireResponse> tQuery =
        getEntMgr().createNamedQuery(TRvwQnaireResponse.GET_BY_PIDCVERSID, TRvwQnaireResponse.class);
    tQuery.setParameter("pidcVersId", pidcVerId);
    for (TRvwQnaireResponse qnaireResponse : tQuery.getResultList()) {
      if (null != getWorkingSetQuestionnaireVersion(qnaireResponse).getTQuestionnaireVersion().getTQuestionnaire()
          .getTWorkpackageDivision()) {
        qnaireInUseSet.add(workPackageLoader.getDataObjectByID(getWorkingSetQuestionnaireVersion(qnaireResponse)
            .getTQuestionnaireVersion().getTQuestionnaire().getTWorkpackageDivision().getTWorkpackage().getWpId()));
      }
    }
    return qnaireInUseSet;
  }

  private TRvwQnaireRespVersion getWorkingSetQuestionnaireVersion(final TRvwQnaireResponse entity) {
    TRvwQnaireRespVersion tRvwQnaireRespVersion = new TRvwQnaireRespVersion();
    for (TRvwQnaireRespVersion tRvwQnaireRespVer : entity.getTRvwQnaireRespVersions()) {
      if (tRvwQnaireRespVersion.getRevNum() == 0l) {
        tRvwQnaireRespVersion = tRvwQnaireRespVer;
      }
    }
    return tRvwQnaireRespVersion;
  }

  /**
   * @param divValId
   * @param inputWps
   * @return
   */
  public Map<Long, String> getWorkRespMap(final Long divValId, final SortedSet<WorkPkg> inputWps) {
    Map<Long, String> workPkgRspMap = new ConcurrentHashMap<>();
    TypedQuery<TWorkpackageDivision> tQuery =
        getEntMgr().createNamedQuery(TWorkpackageDivision.NQ_FIND_BY_DIV_ID, TWorkpackageDivision.class);
    tQuery.setParameter("divValueId", divValId);
    for (TWorkpackageDivision dbWpDiv : tQuery.getResultList()) {
      long wpId = dbWpDiv.getTWorkpackage().getWpId();
      for (WorkPkg icdmWorkPackage : inputWps) {

        if (icdmWorkPackage.getId().equals(wpId)) {
          String resourceCode = "";
          if ((dbWpDiv.getTWpResource() != null) && (dbWpDiv.getTWpResource().getResourceCode() != null)) {
            resourceCode = dbWpDiv.getTWpResource().getResourceCode();
          }
          workPkgRspMap.put(icdmWorkPackage.getId(), resourceCode);
        }
      }
    }
    return workPkgRspMap;
  }


  /**
   * @param questionnireId as input
   * @return workpkg object
   */
  public WorkPkg getWorkPkgbyQnaireID(final Long questionnireId) {
    QuestionnaireLoader questionnaireLoader = new QuestionnaireLoader(getServiceData());
    TWorkpackage tWorkpackage =
        questionnaireLoader.getEntityObject(questionnireId).getTWorkpackageDivision().getTWorkpackage();
    return createDataObject(tWorkpackage);
  }


}
