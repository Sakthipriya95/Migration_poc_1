/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Query;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.common.bo.general.EXTERNAL_LINK_TYPE;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.GttObjectName;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwVariant;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.ReviewResultData;
import com.bosch.caltool.icdm.model.cdr.ReviewVariantModel;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;


/**
 * Loader class for Review Variants
 *
 * @author bru2cob
 */
public class RvwVariantLoader extends AbstractBusinessObject<RvwVariant, TRvwVariant> {

  private static final String PIDC_VER_ID = "pidcverid";


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public RvwVariantLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.CDR_RES_VARIANTS, TRvwVariant.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected RvwVariant createDataObject(final TRvwVariant entity) throws DataException {
    RvwVariant object = new RvwVariant();

    setCommonFields(object, entity);

    object.setVariantName(entity.getTabvProjectVariant().getTabvAttrValue().getTextvalueEng());
    object.setVariantId(entity.getTabvProjectVariant().getVariantId());
    object.setResultId(entity.getTRvwResult().getResultId());
    CDRReviewResult result =
        new CDRReviewResultLoader(getServiceData()).getDataObjectByID(entity.getTRvwResult().getResultId());
    object.setDescription(result.getDescription());

    object.setPrimaryVariantName(result.getPrimaryVariantName());
    object.setPrimaryVariantId(result.getPrimaryVariantId());
    if (object.getVariantId().longValue() != object.getPrimaryVariantId().longValue()) {
      object.setLinkedVariant(true);
    }
    String name = result.getName();
    if (object.isLinkedVariant()) {
      name = CommonUtils.concatenate(result.getName(), "-", object.getVariantName());
    }
    object.setName(name);
    return object;
  }


  /**
   * @param entity trvwvariant object
   * @param resultIdList list of result ids
   * @return Review variant object
   * @throws DataException error while retiving data
   */
  protected ReviewVariantModel createReviewVariantDataObject(final TRvwVariant entity) throws DataException {
    ReviewVariantModel reviewVariant = new ReviewVariantModel();
    reviewVariant.setReviewResultData(new CDRReviewResultLoader(getServiceData()).getReviewResultById(
        entity.getTRvwResult().getResultId(), entity.getTabvProjectVariant().getTabvAttrValue().getTextvalueEng()));
    reviewVariant.setMappedvariant(isMappedVariant(entity));
    reviewVariant.setDeleted(getEntityObject(entity.getRvwVarId()) == null);
    reviewVariant.setRvwVariant(createDataObject(entity));
    return reviewVariant;
  }


  private boolean isMappedVariant(final TRvwVariant entity) throws DataException {
    final Map<Long, RvwVariant> reviewVarMap = getRvwwVarMapForValidation(entity.getTRvwResult().getResultId());
    // If only one Review variant return false
    if (reviewVarMap.size() == 1) {
      return false;
    }
    for (RvwVariant rvwVar : reviewVarMap.values()) {
      // If current date is greater than any of the dates then return true.
      if (ApicUtil.compare(entity.getRvwVarId(), rvwVar.getId()) == 0) {
        return true;
      }
    }
    return false;
  }

  /**
   * Get Review Variants records using ResultId
   *
   * @param entityObject review result*
   * @return Map. Key - id, Value - RvwVariant object
   * @throws DataException error while retrieving data
   */
  public Map<Long, RvwVariant> getByResultObj(final TRvwResult entityObject) throws DataException {
    Map<Long, RvwVariant> objMap = new ConcurrentHashMap<>();
    Set<TRvwVariant> dbObj = entityObject.getTRvwVariants();
    for (TRvwVariant entity : dbObj) {
      objMap.put(entity.getRvwVarId(), createDataObject(entity));
    }
    return objMap;
  }

  /**
   * @param pidcVersId
   * @return
   * @deprecated not used
   */
  @Deprecated
  private List<Long> getResultIdWithParent(final Long pidcVersId) {

    List<Long> resultIdList = new ArrayList<>();

    getEntMgr().getTransaction().begin();
    GttObjectName tempQRvwfunc;

    // Create entities for all the functions
    for (TRvwResult result : new CDRReviewResultLoader(getServiceData()).getCdrResList(pidcVersId)) {
      tempQRvwfunc = new GttObjectName();
      tempQRvwfunc.setId(result.getResultId());
      tempQRvwfunc.setObjName(null);
      getEntMgr().persist(tempQRvwfunc);
    }
    getEntMgr().flush();

    final Query typeQue = getEntMgr().createNativeQuery(
        "SELECT distinct( b.result_id) FROM  t_rvw_results a,  t_rvw_parameters b,  GTT_OBJECT_NAMES temp " +
            "WHERE a.result_id = b.result_id  AND a.result_id = temp.id AND a.DELTA_REVIEW_TYPE = 'P' " +
            "AND b.parent_param_id IS NOT NULL");


    for (Object result : typeQue.getResultList()) {
      resultIdList.add(Long.parseLong(result.toString()));
    }

    getEntMgr().getTransaction().rollback();
    return resultIdList;
  }

  /**
   * @param pidcVersionId pidc version id
   * @return List of ReviewVariant
   * @throws DataException error while retiving data
   * @deprecated not used
   */
  @Deprecated
  public List<ReviewVariantModel> getByPidcVersionId(final long pidcVersionId) throws DataException {

    List<ReviewVariantModel> reviewVariants = new ArrayList<>();
    List<TRvwVariant> rvwVariantList = getEntMgr().createNamedQuery(TRvwVariant.FIND_BY_VERSIONID, TRvwVariant.class)
        .setParameter(PIDC_VER_ID, pidcVersionId).getResultList();
    for (TRvwVariant tRvwVariant : rvwVariantList) {
      reviewVariants.add(createReviewVariantDataObject(tRvwVariant));
    }
    return reviewVariants;
  }


  /**
   * @param objId rvwvariantid
   * @return ReviewVariant object
   * @throws DataException error while retrving data
   */
  public ReviewVariantModel getReviewVariantByObjId(final long objId) throws DataException {
    TRvwVariant rvwVarObj = getEntityObject(objId);
    return createReviewVariantDataObject(rvwVarObj);
  }


  /**
   * Method to fetch the reviewvariantmodel based on review review result id
   *
   * @param cdrResultIds as input
   * @return ReviewVariantModel
   * @throws DataException as exception
   */
  public Set<ReviewVariantModel> getReviewVariantModelSet(final Set<Long> cdrResultIds) throws DataException {
    Set<ReviewVariantModel> reviewVariantModels = new HashSet<>();
    for (Long cdrResultId : cdrResultIds) {
      TRvwResult tRvwResult = new CDRReviewResultLoader(getServiceData()).getEntityObject(cdrResultId);
      if ((null != tRvwResult) && (null != tRvwResult.getTRvwVariants()) && !tRvwResult.getTRvwVariants().isEmpty()) {
        for (TRvwVariant tRvwVariant : tRvwResult.getTRvwVariants()) {
          ReviewVariantModel reviewVariantModel = getReviewVariantByObjId(tRvwVariant.getRvwVarId());
          reviewVariantModels.add(reviewVariantModel);
        }
      }
      else if (null != tRvwResult) {
        ReviewVariantModel reviewVariantModel = new ReviewVariantModel();
        reviewVariantModel.setReviewResultData(
            new CDRReviewResultLoader(getServiceData()).getReviewResultById(tRvwResult.getResultId(), null));
        RvwVariant rvwVariant = new RvwVariant();
        rvwVariant.setName("Dummy Variant for No-Varaint Review Result");
        reviewVariantModel.setRvwVariant(rvwVariant);
        reviewVariantModels.add(reviewVariantModel);
      }

    }
    return reviewVariantModels;
  }

  /**
   * @param resultId result id
   * @return Key - ReviewVariantId and Value - Review Variant Object
   * @throws DataException error while retiving data
   */
  public Map<Long, ReviewVariantModel> getReviewVarMap(final long resultId) throws DataException {
    Map<Long, ReviewVariantModel> reviewVarMap = new HashMap<>();

    CDRReviewResultLoader rvwResLdr = new CDRReviewResultLoader(getServiceData());
    rvwResLdr.validateId(resultId);
    final Set<TRvwVariant> tRvwVars = rvwResLdr.getEntityObject(resultId).getTRvwVariants();
    if (CommonUtils.isNotNull(tRvwVars) && CommonUtils.isNotEmpty(tRvwVars)) {
      for (TRvwVariant rvwVariant : tRvwVars) {
        reviewVarMap.put(rvwVariant.getRvwVarId(), getReviewVariantByObjId(rvwVariant.getRvwVarId()));
      }
    }
    return reviewVarMap;

  }

  /**
   * @param resultId result id
   * @return Key - ReviewVariantId and Value - Review Variant Object
   * @throws DataException error while retiving data
   */
  public Map<Long, RvwVariant> getRvwwVarMapForValidation(final long resultId) throws DataException {
    Map<Long, RvwVariant> reviewVarMap = new HashMap<>();
    final TRvwResult tReviewResult = new CDRReviewResultLoader(getServiceData()).getEntityObject(resultId);
    boolean reviewDeleted = (tReviewResult.getTRvwResult() == null);
    if (!reviewDeleted) {
      final Set<TRvwVariant> tRvwVars = tReviewResult.getTRvwVariants();
      if (CommonUtils.isNotNull(tRvwVars)) {
        for (TRvwVariant rvwVariant : tRvwVars) {
          reviewVarMap.put(rvwVariant.getRvwVarId(), getDataObjectByID(rvwVariant.getRvwVarId()));
        }
      }
    }
    return reviewVarMap;

  }

  /**
   * @param pidcVersId pidc version id
   * @return Key - Workpackage Name and Value - Set of Review Variant Object
   * @throws DataException error while retiving data
   * @deprecated not used
   */
  @Deprecated
  public Map<String, SortedSet<ReviewVariantModel>> getReviewVariantListByPidcVersion(final Long pidcVersId)
      throws DataException {

    SortedSet<ReviewVariantModel> rvwVarSet = new TreeSet<>();
    for (ReviewVariantModel rvwVar : getByPidcVersionId(pidcVersId)) {
      for (Long varId : rvwVar.getReviewResultData().getVariantIds()) {
        if (rvwVar.getRvwVariant().getVariantId().equals(varId)) {
          rvwVarSet.add(rvwVar);
        }
      }
    }
    return resolveWPNameForRvwVar(rvwVarSet);
  }

  /**
   * @param retSet
   * @return
   * @deprecated not used
   */
  @Deprecated
  private Map<String, SortedSet<ReviewVariantModel>> resolveWPNameForRvwVar(
      final SortedSet<ReviewVariantModel> retSet) {

    final List<Long> wpIdList = new ArrayList<>();
    final Map<String, SortedSet<ReviewVariantModel>> wpCDRResults = new ConcurrentHashMap<>();
    if (retSet != null) {
      // Step:1 - Get WP names (group based,config)
      for (ReviewVariantModel cdrRvwVar : retSet) {
        // ICdm-729 new Method created for the Group mapping
        createCdrMapForGrpRvwVar(wpIdList, wpCDRResults, cdrRvwVar);
      }

      // Step:2 - Get WP names (fc2wp type based config)
      // fetch ids not available in cache, this fetch method also adds to cached fc2wp's
      for (ReviewVariantModel cdrRvwVar : retSet) {
        // ICdm-729 new Method created for the Group mapping
        createCdrMapForFcWpRvwVar(wpCDRResults, cdrRvwVar);

      }
    }
    return wpCDRResults;
  }

  /**
   * @param wpIdList
   * @param wpCDRRvwVars
   * @param cdrRvwVar
   * @deprecated not used
   */
  @Deprecated
  private void createCdrMapForGrpRvwVar(final List<Long> wpIdList,
      final Map<String, SortedSet<ReviewVariantModel>> wpCDRRvwVars, final ReviewVariantModel cdrRvwVar) {
    ReviewResultData cdrResult = cdrRvwVar.getReviewResultData();

    if (cdrResult.getFc2wpId() == null) {
      String grpWrkPkgName = CDRConstants.CDR_SOURCE_TYPE.NOT_DEFINED.getTreeDispName();
      if ((cdrResult.getCdrReviewResult().getGrpWorkPkg() != null) &&
          !cdrResult.getCdrReviewResult().getGrpWorkPkg().isEmpty() &&
          (CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE != cdrResult.getCdrSourceType())) {
        grpWrkPkgName = cdrResult.getCdrReviewResult().getGrpWorkPkg();
      }

      if (isLabFunRvwFileValid(cdrResult) || isA2lMonCompFileValid(cdrResult)) {
        grpWrkPkgName = cdrResult.getCdrSourceType().getTreeDispName();
      }
      SortedSet<ReviewVariantModel> cdrRvwVars = wpCDRRvwVars.get(grpWrkPkgName);
      if (cdrRvwVars == null) {
        cdrRvwVars = new TreeSet<>();

        wpCDRRvwVars.put(grpWrkPkgName, cdrRvwVars);
      }

      cdrRvwVars.add(cdrRvwVar);
    }
    else {
      wpIdList.add(cdrResult.getFc2wpId());
    }
  }

  /**
   * @param cdrResult
   */
  private boolean isA2lMonCompFileValid(final ReviewResultData cdrResult) {
    return (CDRConstants.CDR_SOURCE_TYPE.A2L_FILE == cdrResult.getCdrSourceType()) ||
        (CDRConstants.CDR_SOURCE_TYPE.MONICA_FILE == cdrResult.getCdrSourceType()) ||
        (CDRConstants.CDR_SOURCE_TYPE.COMPLI_PARAM == cdrResult.getCdrSourceType());
  }

  /**
   * @param cdrResult
   */
  private boolean isLabFunRvwFileValid(final ReviewResultData cdrResult) {
    return (CDRConstants.CDR_SOURCE_TYPE.LAB_FILE == cdrResult.getCdrSourceType()) ||
        (CDRConstants.CDR_SOURCE_TYPE.FUN_FILE == cdrResult.getCdrSourceType()) ||
        (CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE == cdrResult.getCdrSourceType());
  }

  /**
   * @param wpCDRResults
   * @param cdrResult // ICdm-729 new Method created for the Group mapping
   * @deprecated not used
   */
  @Deprecated
  private void createCdrMapForFcWpRvwVar(final Map<String, SortedSet<ReviewVariantModel>> wpCDRResults,
      final ReviewVariantModel cdrRvwVar) {
    ReviewResultData cdrResult = cdrRvwVar.getReviewResultData();
    if (CommonUtils.isNull(cdrResult.getFc2wpId())) {
      return;
    }

    String fc2WpName = CDRConstants.CDR_SOURCE_TYPE.NOT_DEFINED.getTreeDispName();
    if ((cdrResult.getFc2WorkPkgName() != null) &&
        (CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE != cdrResult.getCdrSourceType())) {
      fc2WpName = cdrResult.getFc2WorkPkgName();
    }
    if (isLabFunRvwFileValid(cdrResult) || isA2lMonCompFileValid(cdrResult)) {
      fc2WpName = cdrResult.getCdrSourceType().getTreeDispName();
    }
    SortedSet<ReviewVariantModel> cdrRvwVaSet = wpCDRResults.get(fc2WpName);
    if (cdrRvwVaSet == null) {
      cdrRvwVaSet = new TreeSet<>();
      wpCDRResults.put(fc2WpName, cdrRvwVaSet);
    }
    cdrRvwVaSet.add(cdrRvwVar);

  }


  /**
   * @param pidcVariantId pidc variant id
   * @param pidcVersId pidc version id
   * @return set of Review Variant
   * @throws DataException error while retiving data
   * @deprecated not used
   */
  @Deprecated
  public Set<ReviewVariantModel> getReviewVariantListByPidcVariant(final Long pidcVariantId, final Long pidcVersId)
      throws DataException {
    SortedSet<ReviewVariantModel> resultSet = new TreeSet<>();
    PidcVariantLoader pidcVariantLoader = new PidcVariantLoader(getServiceData());
    PidcVariant pidcVariant = pidcVariantLoader.getDataObjectByID(pidcVariantId);
    List<ReviewVariantModel> rvwVariantMap = getByPidcVersionId(pidcVersId);
    for (ReviewVariantModel rvwResult : rvwVariantMap) {
      if (rvwResult.getRvwVariant().getVariantName().equals(pidcVariant.getName())) {
        resultSet.add(rvwResult);
      }
    }
    return resultSet;
  }

  /**
   * @param pidcVariantId pidc variant id
   * @param pidcVersId pidc version id
   * @return Key - WpName and Value - SortedSet of Review Variant Object
   * @throws DataException error while retiving data
   * @deprecated not used
   */
  @Deprecated
  public Map<String, SortedSet<ReviewVariantModel>> getReviewVariantMapWithWorkPackage(final Long pidcVariantId,
      final Long pidcVersId)
      throws DataException {
    PidcVariantLoader pidcVariantLoader = new PidcVariantLoader(getServiceData());
    PidcVariant pidcVariant = pidcVariantLoader.getDataObjectByID(pidcVariantId);
    SortedSet<ReviewVariantModel> cdrRvwSet = getReviewVarForVar(pidcVariant.getName(), pidcVersId);
    return resolveWPNameForRvwVar(cdrRvwSet);
  }

  /**
   * @param varName varName
   * @param pidcVerId
   * @return the rvw var set
   * @throws DataException
   * @deprecated not used
   */
  @Deprecated
  private SortedSet<ReviewVariantModel> getReviewVarForVar(final String varName, final Long pidcVerId)
      throws DataException {
    SortedSet<ReviewVariantModel> rvwVarSet = new TreeSet<>();

    for (ReviewVariantModel rvwVar : getByPidcVersionId(pidcVerId)) {
      if ((rvwVar.getRvwVariant().getVariantName()).equalsIgnoreCase(varName)) {
        rvwVarSet.add(rvwVar);
      }
    }

    return rvwVarSet;
  }

  /**
   * Get review variant object for the given CDR result and Variant ID
   *
   * @param resultId CDR result ID
   * @param variantId Variant ID
   * @return review variant
   * @throws DataException exception while getting data
   */
  public RvwVariant getRvwVariantByResultNVarId(final long resultId, final long variantId) throws DataException {
    CDRReviewResultLoader rvwResLdr = new CDRReviewResultLoader(getServiceData());
    rvwResLdr.validateId(resultId);
    new PidcVariantLoader(getServiceData()).validateId(variantId);

    final Set<TRvwVariant> tRvwVars = rvwResLdr.getEntityObject(resultId).getTRvwVariants();
    if (CommonUtils.isNotNull(tRvwVars)) {
      for (TRvwVariant rvwVariant : tRvwVars) {
        if (rvwVariant.getTabvProjectVariant().getVariantId() == variantId) {
          return createDataObject(rvwVariant);
        }
      }
    }

    throw new InvalidInputException(
        "Review variant not found for result ID = " + resultId + " and variant ID = " + variantId);

  }

  /**
   * @param rvwVarId Review Variant ID
   * @return extended name of this Review Result
   * @throws DataException error while retrieving data
   */
  public String getExtendedName(final Long rvwVarId) throws DataException {
    RvwVariant rvwVar = getDataObjectByID(rvwVarId);

    // Use type name as CDR Result
    CDRReviewResultLoader resultLdr = new CDRReviewResultLoader(getServiceData());
    CDRReviewResult result = resultLdr.getDataObjectByID(rvwVar.getResultId());

    return EXTERNAL_LINK_TYPE.CDR_RESULT_VAR.getTypeDisplayText() + ": " + resultLdr.getPidcTreePath(result) +
        rvwVar.getName();
  }


}
