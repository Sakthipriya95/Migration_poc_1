/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.fc2wp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.BaseComponentLoader;
import com.bosch.caltool.icdm.bo.a2l.PTTypeLoader;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.bo.wp.WorkPackageDivisionLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.DateUtil;
import com.bosch.caltool.icdm.database.entity.a2l.TBaseComponent;
import com.bosch.caltool.icdm.database.entity.a2l.TFc2wpDefVersion;
import com.bosch.caltool.icdm.database.entity.a2l.TFc2wpDefinition;
import com.bosch.caltool.icdm.database.entity.a2l.TFc2wpMapPtType;
import com.bosch.caltool.icdm.database.entity.a2l.TFc2wpMapping;
import com.bosch.caltool.icdm.database.entity.a2l.TPowerTrainType;
import com.bosch.caltool.icdm.database.entity.apic.GttObjectName;
import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackageDivision;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicUser;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.RvwFuncDetails;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMapping;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMappingWithDetails;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPVersion;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;

/**
 * @author bne4cob
 */
public class FC2WPMappingLoader extends AbstractBusinessObject<FC2WPMapping, TFc2wpMapping> {

  /**
   *
   */
  private static final String FCWP_VER_ID = "fcwpVerId";

  /**
   * @param inputData ServiceData
   */
  public FC2WPMappingLoader(final ServiceData inputData) {
    super(inputData, MODEL_TYPE.FC2WP_MAPPING, TFc2wpMapping.class);
  }

  /**
   * Get all FC2WP mappings for the given version ID
   *
   * @param fc2wpVersID fc2wp Vers ID
   * @return FC2WPVersMapping
   * @throws DataException when data object not found
   */
  public FC2WPMappingWithDetails getFC2WPMapping(final Long fc2wpVersID) throws DataException {

    FC2WPMappingWithDetails ret = new FC2WPMappingWithDetails();

    TFc2wpDefVersion dbVers = (new FC2WPVersionLoader(getServiceData())).getEntityObject(fc2wpVersID);
    for (TFc2wpMapping dbFcwpMapping : dbVers.getTFc2wpMappings()) {
      FC2WPMapping fcwp = createDataObject(ret, dbFcwpMapping);
      ret.getFc2wpMappingMap().put(fcwp.getFunctionName(), fcwp);
    }

    return ret;
  }

  /**
   * @param fcwpMapId primary key
   * @return FC2WPVersMapping
   * @throws DataException when data object not found
   */
  public FC2WPMappingWithDetails getDataObjectType2ByID(final Long fcwpMapId) throws DataException {
    FC2WPMappingWithDetails ret = new FC2WPMappingWithDetails();

    FC2WPMapping fcwp = createDataObject(ret, getEntityObject(fcwpMapId));

    ret.getFc2wpMappingMap().put(fcwp.getFunctionName(), fcwp);

    return ret;
  }

  /**
   * @param mappingIdSet Set of mapping ids
   * @return FC2WPVersMapping
   * @throws DataException when data object not found
   */
  public FC2WPMappingWithDetails getDataObjectType2ByID(final Set<Long> mappingIdSet) throws DataException {
    FC2WPMappingWithDetails ret = new FC2WPMappingWithDetails();

    for (Long mappingId : mappingIdSet) {
      FC2WPMapping fcwp = createDataObject(ret, getEntityObject(mappingId));
      ret.getFc2wpMappingMap().put(fcwp.getFunctionName(), fcwp);
    }
    return ret;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected FC2WPMapping createDataObject(final TFc2wpMapping entity) throws DataException {
    return createDataObject(null, entity);
  }

  /**
   * @param ret
   * @param dbFcwpMapping
   * @return
   * @throws DataException
   */
  private FC2WPMapping createDataObject(final FC2WPMappingWithDetails ret, final TFc2wpMapping dbFcwpMapping)
      throws DataException {
    FC2WPMapping fcwp = new FC2WPMapping();

    setCommonFields(fcwp, dbFcwpMapping);

    fcwp.setName(dbFcwpMapping.getTFunction().getName());
    fcwp.setDescription(dbFcwpMapping.getTFunction().getLongname());

    fcwp.setFcwpVerId(dbFcwpMapping.getTFc2wpDefVersion().getFcwpVerId());
    fcwp.setFunctionId(dbFcwpMapping.getTFunction().getId());
    fcwp.setFunctionName(fcwp.getName());
    fcwp.setFunctionDesc(fcwp.getDescription());

    fcwp.setAgreeWithCoc(ApicConstants.CODE_YES.equals(dbFcwpMapping.getAgreedWithCocFlag()));
    if (dbFcwpMapping.getAgreedDate() != null) {
      fcwp.setAgreeWithCocDate(DateUtil.timestamp2Date(dbFcwpMapping.getAgreedDate(), DateFormat.DFLT_DATE_FORMAT));
    }
    fcwp.setAgreeWithCocRespUserId(loadUser(ret, dbFcwpMapping.getTabvApicRespForAgrUser()));

    fcwp.setBcID(loadBc(ret, dbFcwpMapping));

    fcwp.setComments(dbFcwpMapping.getComments());

    // If value in db is null, then it is considered as Y
    fcwp.setUseWpDef(!ApicConstants.CODE_NO.equals(dbFcwpMapping.getUseWpDefaultsFlag()));

    fcwp.setContactPersonId(loadUser(ret, dbFcwpMapping.getTabvApicContactUser()));
    fcwp.setContactPersonSecondId(loadUser(ret, dbFcwpMapping.getTabvApicSecondContactUser()));

    fcwp.setWpDivId(loadWpDivDetails(ret, dbFcwpMapping));

    fcwp.setUsedInIcdm(ApicConstants.CODE_YES.equals(dbFcwpMapping.getInIcdmA2lFlag()));
    fcwp.setFcInSdom(ApicConstants.CODE_YES.equals(dbFcwpMapping.getIsFcInSdomFlag()));
    fcwp.setDeleted(ApicConstants.CODE_YES.equals(dbFcwpMapping.getDeleteFlag()));

    fcwp.getPtTypeSet().addAll(loadPtTypes(ret, dbFcwpMapping));

    fcwp.setFcWithParams(yOrNToBoolean(dbFcwpMapping.getFcWithParams()));
    fcwp.setFc2wpInfo(dbFcwpMapping.getFcWpInfo());

    return fcwp;
  }

  private Long loadBc(final FC2WPMappingWithDetails ret, final TFc2wpMapping dbFcwpMapping) throws DataException {
    Long bcId = null;

    TBaseComponent dbBc = dbFcwpMapping.getTBaseComponent();
    if (dbBc != null) {
      bcId = dbBc.getBcId();
      if ((ret != null) && !ret.getBcMap().containsKey(bcId)) {
        ret.getBcMap().put(bcId, new BaseComponentLoader(getServiceData()).getDataObjectByID(bcId));
      }
    }
    return bcId;
  }

  /**
   * @throws DataException
   */
  private Set<Long> loadPtTypes(final FC2WPMappingWithDetails ret, final TFc2wpMapping dbFcwpMapping)
      throws DataException {
    Set<Long> ptTypeIDSet = new HashSet<>();

    TPowerTrainType dbPtType;
    long ptTypeId;

    if (dbFcwpMapping.getTFc2wpMapPtTypes() != null) {
      PTTypeLoader ptTypeLoader = new PTTypeLoader(getServiceData());

      for (TFc2wpMapPtType dbPtTypeMaping : dbFcwpMapping.getTFc2wpMapPtTypes()) {

        dbPtType = dbPtTypeMaping.getTPowerTrainType();
        ptTypeId = dbPtType.getPtTypeId();
        ptTypeIDSet.add(ptTypeId);

        if ((ret != null) && !ret.getPtTypeMap().containsKey(ptTypeId)) {
          ret.getPtTypeMap().put(ptTypeId, ptTypeLoader.getDataObjectByID(ptTypeId));
        }
      }
    }

    return ptTypeIDSet;

  }

  private Long loadWpDivDetails(final FC2WPMappingWithDetails ret, final TFc2wpMapping dbFcwpMapping)
      throws DataException {
    Long wpDivID = null;

    TWorkpackageDivision dbWpDiv = dbFcwpMapping.getTWorkpackageDivision();
    if (dbWpDiv != null) {

      wpDivID = dbWpDiv.getWpDivId();

      if ((ret != null) && !ret.getWpDetMap().containsKey(wpDivID)) {
        WorkPackageDivision wpDiv = (new WorkPackageDivisionLoader(getServiceData())).getDataObjectByID(wpDivID);

        ret.getWpDetMap().put(wpDivID, wpDiv);

        loadUser(ret, wpDiv.getContactPersonId());
        loadUser(ret, wpDiv.getContactPersonSecondId());
      }
    }
    return wpDivID;
  }

  private void loadUser(final FC2WPMappingWithDetails ret, final Long userId) throws DataException {
    UserLoader loader = new UserLoader(getServiceData());
    loadUser(ret, loader.getEntityObject(userId));
  }

  private Long loadUser(final FC2WPMappingWithDetails ret, final TabvApicUser dbUser) throws DataException {
    Long userId = null;

    if (dbUser != null) {
      userId = dbUser.getUserId();
      if ((ret != null) && !ret.getUserMap().containsKey(userId)) {
        ret.getUserMap().put(userId, new UserLoader(getServiceData()).getDataObjectByID(userId));
      }
    }

    return userId;
  }

  /**
   * Find FC to WP mappings for the given 'PIDC A2L ID'
   *
   * @param pidcA2LId PIDC A2L ID
   * @return FC2WPVersMapping
   * @throws DataException if active version of FC2WP not available
   */
  public FC2WPMappingWithDetails findByPidcA2lId(final Long pidcA2LId) throws DataException {
    TPidcA2l dbPidcA2l = getEntMgr().find(TPidcA2l.class, pidcA2LId);

    long pidcVersId = dbPidcA2l.getTPidcVersion().getPidcVersId();

    // FC2WP attribute fetch
    String paramVal = (new CommonParamLoader(getServiceData())).getValue(CommonParamKey.WP_TYPE_ATTR_ID);
    Long wpTypeAttrID = Long.valueOf(paramVal);

    TypedQuery<Long> qryFcwpVers =
        getEntMgr().createNamedQuery(TFc2wpDefVersion.NQ_GET_FC2WP_VERS_ID_BY_PIDC_N_ATTR, Long.class);
    qryFcwpVers.setParameter("attrId", wpTypeAttrID);
    qryFcwpVers.setParameter("pidcVersId", pidcVersId);
    List<Long> resList = qryFcwpVers.getResultList();
    if (resList.isEmpty()) {
      // Attribute value not set
      // FC2WP definition does not have active version etc.
      throw new DataException("FC2WP mapping incomplete for the project");
    }

    Long fcwpVersId = resList.get(0);
    Long a2lId = dbPidcA2l.getMvTa2lFileinfo().getId();


    return doFindMapping(a2lId, fcwpVersId);
  }

  /**
   * Find FC2WP mapping for the given A2L, FC2WP name and division
   *
   * @param a2lId a2l file Id
   * @param divValId Division ID
   * @param fc2wpName Name Vlaue id of FC2WP definition
   * @return FC2WPVersMapping
   * @throws IcdmException Exception
   */
  public FC2WPMappingWithDetails findByA2LId(final Long a2lId, final Long divValId, final String fc2wpName)
      throws IcdmException {

    FC2WPVersion activeVers = new FC2WPVersionLoader(getServiceData()).getActiveVersionByValueID(fc2wpName, divValId);
    return doFindMapping(a2lId, activeVers.getId());
  }

  /**
   * @param activeVersId active Version Id
   * @return FC2WPVersMapping
   * @throws DataException if active version of FC2WP not available
   */
  private FC2WPMappingWithDetails doFindMapping(final Long a2lFileId, final Long activeVersId) throws DataException {

    getEntMgr().getTransaction().begin();

    Query nStmt = getEntMgr().createNamedQuery(GttObjectName.NNS_INS_TEMP_TABLE_A2L_FUNS);
    nStmt.setParameter(1, a2lFileId);
    nStmt.executeUpdate();

    TypedQuery<TFc2wpMapping> mapQry =
        getEntMgr().createNamedQuery(TFc2wpMapping.NQ_GET_FC2WP_MAP_BY_VERS_FOR_TEMP, TFc2wpMapping.class);
    mapQry.setParameter(FCWP_VER_ID, activeVersId);

    FC2WPMappingWithDetails ret = new FC2WPMappingWithDetails();

    for (TFc2wpMapping entity : mapQry.getResultList()) {
      FC2WPMapping fcwp = createDataObject(ret, entity);
      ret.getFc2wpMappingMap().put(fcwp.getFunctionName(), fcwp);
    }

    getEntMgr().getTransaction().rollback();

    return ret;
  }

  /**
   * @param rvwFuncDetails Model to hold Division Id & list of functions used in data review
   * @return FC2WPVersMapping
   * @throws IcdmException Exception
   */
  public FC2WPMappingWithDetails getQFc2WpMappingByDivId(final RvwFuncDetails rvwFuncDetails) throws IcdmException {

    TypedQuery<TFc2wpDefinition> tQuery =
        getEntMgr().createNamedQuery(TFc2wpDefinition.NQ_FIND_QNAIRE_FC2WPDEF_BY_DIV_ID, TFc2wpDefinition.class);
    tQuery.setParameter("divValueId", rvwFuncDetails.getDivId());

    List<TFc2wpDefinition> resultList = tQuery.getResultList();
    if ((resultList != null) && (!resultList.isEmpty())) {
      TFc2wpDefinition fc2wpDef = resultList.get(0);
      FC2WPVersionLoader versLoader = new FC2WPVersionLoader(getServiceData());
      FC2WPVersion activeVers;
      try {
        activeVers = versLoader.getActiveVersionByValueID(fc2wpDef.getFc2wpName(), rvwFuncDetails.getDivId());
      }
      catch (DataException e) {
        throw new DataException("Active version does not exist for the given inputs", e);
      }
      return findQFC2WPMapping(activeVers.getId(), rvwFuncDetails);
    }
    return null;
  }

  /**
   * @param rvwFuncDetails Model to hold Division Id & list of functions used in data review
   * @param activeVersId Active version Id
   * @return FC2WPVersMapping
   * @throws DataException error from service call
   */
  private FC2WPMappingWithDetails findQFC2WPMapping(final Long activeVersId, final RvwFuncDetails rvwFuncDetails)
      throws DataException {

    getEntMgr().getTransaction().begin();
    GttObjectName tempQRvwfunc;
    long id = 1;

    // Create entities for all the functions
    for (String funcName : rvwFuncDetails.getFuncSet()) {
      tempQRvwfunc = new GttObjectName();
      tempQRvwfunc.setId(id);
      tempQRvwfunc.setObjName(funcName.toUpperCase(Locale.getDefault()));
      getEntMgr().persist(tempQRvwfunc);
      id++;
    }
    getEntMgr().flush();

    TypedQuery<TFc2wpMapping> mapQry =
        getEntMgr().createNamedQuery(TFc2wpMapping.NQ_GET_FC2WP_MAP_BY_VERS_FOR_TEMP, TFc2wpMapping.class);
    mapQry.setParameter(FCWP_VER_ID, activeVersId);

    FC2WPMappingWithDetails ret = new FC2WPMappingWithDetails();

    for (TFc2wpMapping entity : mapQry.getResultList()) {
      FC2WPMapping fcwp = createDataObject(ret, entity);
      ret.getFc2wpMappingMap().put(fcwp.getFunctionName(), fcwp);
    }

    getEntMgr().getTransaction().rollback();
    return ret;
  }

  /**
   * Gets the wp name from FC 2 WP.
   *
   * @param a2lFileId the a 2 l file id
   * @param activeVersId active Version Id
   * @return FC2WPVersMapping
   */
  public Set<String> getWpNameFromFC2WP(final Long a2lFileId, final Long activeVersId) {

    getEntMgr().getTransaction().begin();

    Query nStmt = getEntMgr().createNamedQuery(GttObjectName.NNS_INS_TEMP_TABLE_A2L_FUNS);
    nStmt.setParameter(1, a2lFileId);
    nStmt.executeUpdate();

    TypedQuery<String> mapQry =
        getEntMgr().createNamedQuery(TFc2wpMapping.NQ_GET_DISTINCT_WP_BY_VERS_FOR_TEMP, String.class);
    mapQry.setParameter(FCWP_VER_ID, activeVersId);

    Set<String> ret = new HashSet<>();
    for (String wpNameE : mapQry.getResultList()) {
      ret.add(wpNameE);
    }
    getEntMgr().getTransaction().rollback();
    return ret;
  }

  /**
   * Gets the fc 2 wp mapping by A 2 l N version.
   *
   * @param a2lFileId the a 2 l file id
   * @param activeVersId the active vers id
   * @return the fc 2 wp by A 2 l N version
   * @throws DataException the data exception
   */
  public Map<String, WorkPackageDivision> getFc2WpByA2lNVersion(final Long a2lFileId, final Long activeVersId)
      throws DataException {
    Map<String, WorkPackageDivision> ret = new HashMap<>();
    try (ServiceData sdata = new ServiceData()) {
      getServiceData().copyTo(sdata, true);

      sdata.getEntMgr().getTransaction().begin();

      Query nStmt = sdata.getEntMgr().createNamedQuery(GttObjectName.NNS_INS_TEMP_TABLE_A2L_FUNS);
      nStmt.setParameter(1, a2lFileId);
      nStmt.executeUpdate();

      TypedQuery<TFc2wpMapping> mapQry =
          sdata.getEntMgr().createNamedQuery(TFc2wpMapping.NQ_GET_FC2WP_MAP_BY_VERS_FOR_TEMP, TFc2wpMapping.class);
      mapQry.setParameter(FCWP_VER_ID, activeVersId);

      WorkPackageDivisionLoader wpDivLdr = new WorkPackageDivisionLoader(getServiceData());
      for (TFc2wpMapping entity : mapQry.getResultList()) {
        FC2WPMapping fcwp = createDataObject(entity);
        if (!fcwp.isDeleted()) {
          WorkPackageDivision wpDiv = wpDivLdr.getDataObjectByID(fcwp.getWpDivId());
          ret.put(fcwp.getFunctionName(), wpDiv);
        }
      }
      sdata.getEntMgr().getTransaction().rollback();
    }
    return ret;
  }
}
