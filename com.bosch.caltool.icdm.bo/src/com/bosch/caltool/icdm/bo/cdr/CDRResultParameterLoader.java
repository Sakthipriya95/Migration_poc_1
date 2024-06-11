/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2lResponsibilityLoader;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionStatus;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TParameter;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParameter;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.CLEARING_STATUS;
import com.bosch.caltool.icdm.model.apic.ApicConstants.READY_FOR_SERIES;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.LevelAttrInfo;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionType;
import com.bosch.caltool.icdm.model.apic.pidc.ProjectIdCardVariantInfoType;
import com.bosch.caltool.icdm.model.apic.pidc.ProjectIdCardVersinfoType;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.cdr.MonicaReviewData;
import com.bosch.caltool.icdm.model.cdr.ParameterReviewResult;
import com.bosch.caltool.icdm.model.cdr.ReviewParamResponse;
import com.bosch.caltool.icdm.model.cdr.ReviewResultType;
import com.bosch.caltool.icdm.model.cdr.ReviewResultsType;


/**
 * Loader class for CDR Result Parameter
 *
 * @author BRU2COB
 */
public class CDRResultParameterLoader extends AbstractBusinessObject<CDRResultParameter, TRvwParameter> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public CDRResultParameterLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.CDR_RES_PARAMETER, TRvwParameter.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected CDRResultParameter createDataObject(final TRvwParameter entity) throws DataException {
    CDRResultParameter object = new CDRResultParameter();

    setCommonFields(object, entity);

    object.setParamId(entity.getTParameter().getId());
    object.setName(entity.getTParameter().getName());
    object
        .setDescription(getLangSpecTxt(entity.getTParameter().getLongname(), entity.getTParameter().getLongnameGer()));
    object.setpType(entity.getTParameter().getPtype());
    object.setResultId(entity.getTRvwResult().getResultId());
    object.setRvwFunId(entity.getTRvwFunction().getRvwFunId());
    object.setFuncName(entity.getTRvwFunction().getTFunction().getName());
    object.setRvwMethod(entity.getRvwMethod());
    object.setLowerLimit(entity.getLowerLimit());
    object.setUpperLimit(entity.getUpperLimit());
    object.setMaturityLvl(entity.getMaturityLvl());
    object.setRefValue(entity.getRefValue());
    object.setRefUnit(entity.getRefUnit());
    object.setHint(entity.getHint());
    object.setParamHint(entity.getTParameter().getHint());
    object.setCheckedValue(entity.getCheckedValue());
    object.setResult(entity.getResult());
    object.setRvwComment(entity.getRvwComment());
    object.setCheckUnit(entity.getCheckUnit());
    object.setChangeFlag(Long.valueOf(entity.getChangeFlag()));
    object.setMatchRefFlag(entity.getMatchRefFlag());
    object.setRvwFileId(entity.getTRvwFile() == null ? null : entity.getTRvwFile().getRvwFileId());
    object.setBitwiseLimit(entity.getBitwiseLimit());
    object.setIsbitwise(entity.getIsbitwise());
    object.setParentParamId(entity.getTRvwParameter() == null ? null : entity.getTRvwParameter().getRvwParamId());
    object.setReviewScore(entity.getReviewScore());
    object.setCompliResult(entity.getCompliResult());
    object.setQssdResult(entity.getQssdResult());
    object.setQssdLabObjId(entity.getQssdLabObjId() == null ? null : BigDecimal.valueOf(entity.getQssdLabObjId()));
    object.setQssdRevId(entity.getQssdRevId() == null ? null : BigDecimal.valueOf(entity.getQssdRevId()));
    object.setLabObjId(entity.getLabObjId() == null ? null : BigDecimal.valueOf(entity.getLabObjId()));
    object.setRevId(entity.getRevId() == null ? null : BigDecimal.valueOf(entity.getRevId()));
    object
        .setCompliLabObjId(entity.getCompliLabObjId() == null ? null : BigDecimal.valueOf(entity.getCompliLabObjId()));
    object.setCompliRevId(entity.getCompliRevId() == null ? null : BigDecimal.valueOf(entity.getCompliRevId()));
    object.setSecondaryResult(entity.getSecondaryResult());
    object.setSecondaryResultState(entity.getSecondaryResultState());
    object.setSrResult(entity.getSrResult());
    object.setSrErrorDetails(entity.getSrErrorDetails());
    object.setSrAcceptedFlag(entity.getSrAcceptedFlag());
    object.setSrAcceptedUser(entity.getSrAcceptedUser());
    object.setSrAcceptedDate(timestamp2String(entity.getSrAcceptedDate()));
    object.setRvwWpRespId(null == entity.gettRvwWpResp() ? null : entity.gettRvwWpResp().getRvwWpRespId());
    object.setArcReleasedFlag(yOrNToBoolean(entity.getArcReleasedFlag()));
    return object;
  }


  /**
   * Get CDR Result Parameter records using ResultId
   *
   * @param entityObject result entity
   * @return Map. Key - id, Value - CDRResultParameter object
   * @throws DataException error while retrieving data
   */
  public Map<Long, CDRResultParameter> getByResultObj(final TRvwResult entityObject) throws DataException {
    Map<Long, CDRResultParameter> objMap = new ConcurrentHashMap<>();

    Set<TRvwParameter> dbObj = entityObject.getTRvwParameters();
    if (dbObj != null) {
      for (TRvwParameter entity : dbObj) {
        objMap.put(entity.getRvwParamId(), createDataObject(entity));
      }
    }
    return objMap;

  }

  /**
   * Get CDR Result Parameter records using ResultId
   *
   * @param entityObject result entity
   * @return Map. Key - param id, Value - CDRResultParameter object
   * @throws DataException error while retrieving data
   */
  public Map<Long, CDRResultParameter> getParamsByResultObj(final TRvwResult entityObject) throws DataException {
    Map<Long, CDRResultParameter> objMap = new ConcurrentHashMap<>();

    Set<TRvwParameter> dbObj = entityObject.getTRvwParameters();
    if (dbObj != null) {
      for (TRvwParameter entity : dbObj) {
        objMap.put(entity.getTParameter().getId(), createDataObject(entity));
      }
    }
    return objMap;

  }

  /**
   * @param paramIds - set of parameter Id
   * @return ParameterReviewResult object
   * @throws IcdmException Error during fetching data
   * @throws IOException
   * @throws ClassNotFoundException
   */
  public ParameterReviewResult[] getParameterReviewResult(final List<Long> paramIds)
      throws ClassNotFoundException, IOException, IcdmException {
    ParameterReviewResult[] ret = new ParameterReviewResult[paramIds.size()];
    int count = 0;
    for (Long paramId : paramIds) {
      ParameterReviewResult parameterReviewResult = new ParameterReviewResult();
      TParameter tParameter = new ParameterLoader(getServiceData()).getEntityObject(paramId);
      setParameterDetails(parameterReviewResult, tParameter);
      ret[count] = parameterReviewResult;
      count++;
    }
    return ret;
  }

  /**
   * @param ret
   * @param tParameter
   * @throws IcdmException
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private void setParameterDetails(final ParameterReviewResult ret, final TParameter tParameter)
      throws ClassNotFoundException, IOException, IcdmException {
    ret.setParameterId(tParameter.getId());
    ret.setParameterName(tParameter.getName());
    ret.setParameterLongName(tParameter.getLongname());
    ret.setReviewDetails(createReviewResultsList(tParameter));
  }

  /**
   * @param tParameter
   * @return
   * @throws IOException
   * @throws ClassNotFoundException
   * @throws IcdmException
   */
  private ReviewResultsType[] createReviewResultsList(final TParameter tParameter)
      throws ClassNotFoundException, IOException, IcdmException {

    SortedSet<CDRResultParameter> reviewDataResponse = getReviewDataResponse(tParameter.getId());
    ReviewResultsType[] reviewDetails = new ReviewResultsType[reviewDataResponse.size()];
    CDRReviewResultLoader rvwResultLoader = new CDRReviewResultLoader(getServiceData());
    int count = 0;
    for (CDRResultParameter cDRResultParameter : reviewDataResponse) {
      ReviewResultsType reviewResultsType = new ReviewResultsType();
      reviewResultsType.setReviewId(cDRResultParameter.getResultId());
      reviewResultsType.setParamName(getEntityObject(cDRResultParameter.getId()).getTParameter().getName());
      reviewResultsType.setResult(CDRConstants.RESULT_FLAG.getType(cDRResultParameter.getResult()).getUiType());
      reviewResultsType.setComment(cDRResultParameter.getRvwComment());
      reviewResultsType.setUnit(cDRResultParameter.getCheckUnit());
      CDRReviewResult cdrRvwResult;
      cdrRvwResult = rvwResultLoader.getDataObjectByID(cDRResultParameter.getResultId());
      reviewResultsType.setProjectIdCardVersInfoType(getProjectIdCardInfoType(cdrRvwResult));
      if (null != cdrRvwResult.getPrimaryVariantId()) {
        reviewResultsType.setProjectIdCardVariantInfoType(getProjectIdCardVariantInfoType(cdrRvwResult));
      }
      reviewResultsType.setReviewName(cdrRvwResult.getName());

      reviewResultsType.setCheckValue(cDRResultParameter.getCheckedValue());
      reviewResultsType.setCheckValStr(getCheckedValueString(cDRResultParameter));

      reviewResultsType.setReviewStatus(CDRConstants.REVIEW_STATUS.getType(cdrRvwResult.getRvwStatus()).getUiType());
      reviewResultsType.setReviewMethod(READY_FOR_SERIES.getType(cDRResultParameter.getRvwMethod()).getUiType());
      reviewResultsType.setReviewType(CDRConstants.REVIEW_TYPE.getType(cdrRvwResult.getReviewType()).getUiType());
      reviewResultsType.setReviewDate(cdrRvwResult.getCreatedDate());
      reviewResultsType.setReviewDescription(cdrRvwResult.getDescription());
      reviewDetails[count] = reviewResultsType;
      count++;
    }
    return reviewDetails;
  }

  /**
   * @param cdrRvwResult
   * @return
   * @throws IcdmException
   */
  private ProjectIdCardVariantInfoType getProjectIdCardVariantInfoType(final CDRReviewResult cdrRvwResult)
      throws IcdmException {
    ProjectIdCardVariantInfoType projectIdCardVariantInfoType = new ProjectIdCardVariantInfoType();
    PidcVariantLoader pidcVariantLoader = new PidcVariantLoader(getServiceData());
    PidcVariant pidcVariant = pidcVariantLoader.getDataObjectByID(cdrRvwResult.getPrimaryVariantId());
    projectIdCardVariantInfoType.setId(pidcVariant.getId());
    projectIdCardVariantInfoType.setName(pidcVariant.getName());
    projectIdCardVariantInfoType
        .setVersionNumber(pidcVariantLoader.getEntityObject(pidcVariant.getId()).getTPidcVersion().getProRevId());
    projectIdCardVariantInfoType.setChangeNumber(pidcVariant.getVersion());
    projectIdCardVariantInfoType.setDeleted(pidcVariant.isDeleted());
    projectIdCardVariantInfoType.setCreatedDate(pidcVariant.getCreatedDate());
    projectIdCardVariantInfoType.setCreatedUser(pidcVariant.getCreatedUser());
    projectIdCardVariantInfoType.setModifyDate(pidcVariant.getModifiedDate());
    projectIdCardVariantInfoType.setModifyUser(pidcVariant.getModifiedUser());
    return projectIdCardVariantInfoType;
  }

  /**
   * @param cdrRvwResult
   * @return
   * @throws DataException
   */
  private ProjectIdCardVersinfoType getProjectIdCardInfoType(final CDRReviewResult cdrRvwResult) throws DataException {
    ProjectIdCardVersinfoType ret = new ProjectIdCardVersinfoType();

    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
    PidcVersion pidcVersion = pidcVersionLoader.getDataObjectByID(cdrRvwResult.getPidcVersionId());
    PidcVersionType pidcVersionType = new PidcVersionType();
    pidcVersionType.setActive(Long.compare(pidcVersionLoader.getActivePidcVersion(pidcVersion.getPidcId()).getId(),
        pidcVersion.getId()) == 0);
    pidcVersionType.setChangeNumber(pidcVersion.getProRevId());
    pidcVersionType.setDescription(pidcVersion.getDescription());
    pidcVersionType.setDescriptionE(pidcVersion.getVersDescEng());
    pidcVersionType.setDescriptionG(pidcVersion.getVersDescGer());
    pidcVersionType.setLongName(pidcVersion.getVersionName());
    pidcVersionType.setPidcVersionId(pidcVersion.getId());
    pidcVersionType.setProRevId(pidcVersion.getProRevId());
    pidcVersionType.setVersionStatus(PidcVersionStatus.getStatus(pidcVersion.getPidStatus()).getUiStatus());
    ret.setPidcVersionType(pidcVersionType);

    Pidc pidc = new PidcLoader(getServiceData()).getDataObjectByID(pidcVersion.getPidcId());
    ret.setName(pidc.getName());
    ret.setId(pidcVersion.getPidcId());
    ret.setChangeNumber(pidcVersion.getVersion());
    ret.setDeleted(pidcVersion.isDeleted());
    ret.setCreateDate(pidcVersion.getCreatedDate());
    ret.setCreatedUser(pidcVersion.getCreatedUser());
    ret.setModifyUser(pidcVersion.getModifiedUser());
    ret.setModifyDate(pidcVersion.getModifiedDate());
    ret.setLevelAttrInfoList(getLevelInfo(pidcVersion));
    ret.setClearingStatus(CLEARING_STATUS.getClearingStatus(pidc.getClearingStatus()).getUiText());
    ret.setCleared(pidc.getClearingStatus().equals(CLEARING_STATUS.CLEARED.getDBText()));
    return ret;
  }

  private LevelAttrInfo[] getLevelInfo(final PidcVersion pidcVersion) throws DataException {

    PidcVersion activePidcVersion =
        new PidcVersionLoader(getServiceData()).getActivePidcVersion(pidcVersion.getPidcId());
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    AttributeValueLoader attrValLoader = new AttributeValueLoader(getServiceData());
    Map<Long, PidcVersionAttribute> structureAttributes =
        new PidcVersionAttributeLoader(getServiceData()).getStructureAttributes(activePidcVersion.getId());
    LevelAttrInfo[] levelAttrInfos = new LevelAttrInfo[structureAttributes.size()];
    int count = 0;
    for (PidcVersionAttribute pidcVerAttr : structureAttributes.values()) {
      Attribute attr = attrLoader.getDataObjectByID(pidcVerAttr.getAttrId());
      LevelAttrInfo levelAttrInfo = new LevelAttrInfo();
      levelAttrInfo.setLevelAttrId(pidcVerAttr.getAttrId());
      if (attr.getId() != null) {
        Long valueId =
            structureAttributes.get(attrLoader.getDataObjectByID(pidcVerAttr.getAttrId()).getLevel()).getValueId();
        levelAttrInfo.setLevelAttrValueId(valueId);
        levelAttrInfo.setLevelName(attrValLoader.getDataObjectByID(valueId).getName());
      }
      else {
        levelAttrInfo.setLevelAttrValueId(-1L);
        levelAttrInfo.setLevelName("NULL");
      }
      levelAttrInfo.setLevelNo(attr.getLevel());
      levelAttrInfos[count] = levelAttrInfo;
      count++;
    }
    return levelAttrInfos;
  }


  /**
   * Get CDR Result Parameter records using ResultId
   *
   * @param entityObject result entity
   * @return Map. Key - id, Value - CDRResultParameter object
   */
  public boolean hasParent(final TRvwResult entityObject) {
    Set<TRvwParameter> dbObj = entityObject.getTRvwParameters();
    for (TRvwParameter entity : dbObj) {
      if (entity.getTRvwParameter() == null) {
        return false;
      }
    }
    return true;

  }


  /**
   * @param paramId paramId
   * @return the review results
   * @throws DataException DataException
   */
  public SortedSet<CDRResultParameter> getReviewDataResponse(final long paramId) throws DataException {

    List<DATA_REVIEW_SCORE> checkedValues = DATA_REVIEW_SCORE.getCheckedValues();
    List<String> checkedTypes = new ArrayList<>();
    for (DATA_REVIEW_SCORE score : checkedValues) {
      checkedTypes.add(score.getDbType());
    }

    final TypedQuery<TRvwParameter> typeQuery =
        getEntMgr().createNamedQuery(TRvwParameter.NQ_GET_REVIEW_DATA_RESPONSE, TRvwParameter.class);
    typeQuery.setHint(ApicConstants.FETCH_SIZE, "100");
    typeQuery.setParameter("paramId", paramId);
    typeQuery.setParameter("checkedTypes", checkedTypes);
    final List<TRvwParameter> resultList = typeQuery.getResultList();

    final SortedSet<CDRResultParameter> retSet = new TreeSet<>();
    for (TRvwParameter dbRes : resultList) {


      CDRResultParameter cdrResultParam = createDataObject(dbRes);

      retSet.add(cdrResultParam);
    }
    return retSet;

  }

  /**
   * Method to fetch the list of param mapped to Wp and Resp
   *
   * @param resultId as input
   * @return Map<Long, Map<Long, String>> key - Param Id and value - ( Key - WP Id and value - RESP Name)
   * @throws DataException
   */
  public Map<Long, Map<Long, String>> getParamMappedToWpAndResp(final long resultId) throws DataException {
    Map<Long, Map<Long, String>> paramIdMappedToWpAndResp = new HashMap<>();
    TypedQuery<Object[]> rvwParamQuery =
        getEntMgr().createNamedQuery(TRvwParameter.NNQ_GET_PARAM_MAPPED_TO_WPANDRESP, Object[].class);
    rvwParamQuery.setParameter(1, resultId);
    final List<Object[]> objectList = rvwParamQuery.getResultList();
    for (Object[] object : objectList) {
      long rvwParamId = ((BigDecimal) object[0]).longValue();
      long wpRespId = ((BigDecimal) object[1]).longValue();
      long a2lRespId = ((BigDecimal) object[2]).longValue();
      A2lResponsibility responsibility = new A2lResponsibilityLoader(getServiceData()).getDataObjectByID(a2lRespId);
      String respName = responsibility.getName();

      if (paramIdMappedToWpAndResp.containsKey(rvwParamId)) {
        paramIdMappedToWpAndResp.get(rvwParamId).put(wpRespId, respName);
      }
      else {
        Map<Long, String> wpAndRespMap = new HashMap<>();
        wpAndRespMap.put(wpRespId, respName);
        paramIdMappedToWpAndResp.put(rvwParamId, wpAndRespMap);
      }
    }
    return paramIdMappedToWpAndResp;
  }

  public Map<String, ReviewParamResponse> getParamReviewRsponse(final List<String> paramNames,
      final List<Long> varCodedParamIdList)
      throws DataException {

    getLogger().info("Fetching Review Data for parameters -" + paramNames);

    Map<String, ReviewParamResponse> reviewRespMap = new HashMap<>();
    ParameterLoader paramLoader = new ParameterLoader(getServiceData());
    List<Parameter> paramMap = paramLoader.getParamObjListByParamName(paramNames);

    List<Long> paramIds = new ArrayList<>();
    paramMap.stream().forEach(parameter -> paramIds.add(parameter.getId()));

    if (CommonUtils.isNotEmpty(varCodedParamIdList)) {
      paramIds.addAll(varCodedParamIdList);
    }
    /* Adding it to a Set to avoid duplicates if any */
    Set<Long> expectedItemsSet = new HashSet<>(paramIds);

    for (Long paramId : expectedItemsSet) {
      Parameter paramObj = paramLoader.getDataObjectByID(paramId);
      ReviewParamResponse response = new ReviewParamResponse();
      response.setParamId(paramId);
      response.setLongName(paramObj.getLongName());
      response.setParamName(paramObj.getName());
      List<ReviewResultType> reviewResultType = createReviewResultList(paramId);
      response.setReviewResultType(reviewResultType);
      reviewRespMap.put(paramObj.getName(), response);
    }
    getLogger().info("Fetching of Review Data Completed. No of paramaters fetched - " + reviewRespMap.size());

    return reviewRespMap;
  }

  /**
   * @param paramId
   * @throws DataException
   */
  private List<ReviewResultType> createReviewResultList(final Long paramId) throws DataException {

    List<ReviewResultType> reviewResultTypeList = new ArrayList<>();
    SortedSet<CDRResultParameter> cdrResParamSet = getReviewDataResponse(paramId);
    for (CDRResultParameter cdrResultParameter : cdrResParamSet) {
      ReviewResultType type = new ReviewResultType();
      type.setParamName(cdrResultParameter.getName());
      CDRReviewResult cdrResult =
          new CDRReviewResultLoader(getServiceData()).getDataObjectByID(cdrResultParameter.getResultId());


      type.setReviewName(getResultName(cdrResult));
      type.setReviewId(cdrResultParameter.getResultId());

      if (cdrResult.getPrimaryVariantId() != null) {
        PidcVariant pidcVar =
            new PidcVariantLoader(getServiceData()).getDataObjectByID(cdrResult.getPrimaryVariantId());
        type.setPidcVar(pidcVar);
      }

      PidcA2l pidcA2l = new PidcA2lLoader(getServiceData()).getDataObjectByID(cdrResult.getPidcA2lId());
      PidcVersion pidVersion = new PidcVersionLoader(getServiceData()).getDataObjectByID(pidcA2l.getPidcVersId());

      pidcA2l.getSdomPverVarName();
      type.setPidcversion(pidVersion);
      type.setResult(CDRConstants.RESULT_FLAG.getType(cdrResultParameter.getResult()).getUiType());
      type.setComment(cdrResultParameter.getRvwComment());
      byte[] checkedValue = cdrResultParameter.getCheckedValue();
      type.setCheckValue(checkedValue);
      CalData calDataObj;
      try {
        calDataObj = CalDataUtil.getCalDataObj(checkedValue);
        if (calDataObj != null) {
          type.setCheckValStr(calDataObj.getCalDataPhy().getSimpleDisplayValue());
        }
      }
      catch (ClassNotFoundException | IOException exp) {
        throw new DataException(exp.getMessage(), exp);
      }
      type.setUnit(cdrResultParameter.getCheckUnit());
      type.setReviewDate(cdrResult.getCreatedDate());
      type.setReviewType(cdrResult.getReviewType());
      type.setReviewMethod(cdrResultParameter.getRvwMethod());

      String statusUiValue = CDRConstants.REVIEW_STATUS.getType(cdrResult.getRvwStatus()).getUiType();
      type.setReviewStatus(statusUiValue);
      type.setReviewDescription(cdrResult.getDescription());
      reviewResultTypeList.add(type);
    }
    getLogger().info(" Review Result List created with size - " + reviewResultTypeList.size());

    return reviewResultTypeList;

  }

  /**
   * @param cdrResult
   */
  private String getResultName(final CDRReviewResult cdrResult) {
    final StringBuilder resultName = new StringBuilder(cdrResult.getCreatedDate());
    resultName.append(" - ");
    resultName.append(cdrResult.getSdomPverVarName());
    resultName.append(" - ");
    resultName.append(cdrResult.getPrimaryVariantName());
    resultName.append(" - ");
    resultName.append(cdrResult.getDescription());
    return resultName.toString();

  }

  /**
   * @param paramObjList
   * @param monicaObjectList
   * @return
   * @deprecated not used
   */
  @Deprecated
  private Map<Parameter, MonicaReviewData> getParamObjMap(final List<Parameter> paramObjList,
      final List<MonicaReviewData> monicaObjectList) {
    Map<Parameter, MonicaReviewData> monicaReviewMap = new HashMap<>();
    for (MonicaReviewData monicaReviewData : monicaObjectList) {

      for (Parameter parameter : paramObjList) {
        if (monicaReviewData.getLabel().equals(parameter.getName())) {
          monicaReviewMap.put(parameter, monicaReviewData);
        }

      }

    }
    return monicaReviewMap;

  }

  /**
   * Return the string representation of checked value
   *
   * @param cDRResultParameter
   * @return String
   * @throws IOException
   * @throws ClassNotFoundException
   */
  public String getCheckedValueString(final CDRResultParameter cDRResultParameter)
      throws ClassNotFoundException, IOException {

    if (cDRResultParameter.getCheckedValue() != null) {
      return getCheckedValueObj(cDRResultParameter).getCalDataPhy().getSimpleDisplayValue();
    }
    return "";
  }

  /**
   * Method to get CheckedValue object
   *
   * @param cDRResultParameter
   * @return actual review output
   * @throws IOException
   * @throws ClassNotFoundException
   */
  public CalData getCheckedValueObj(final CDRResultParameter cDRResultParameter)
      throws ClassNotFoundException, IOException {
    return getCDPObj(cDRResultParameter.getCheckedValue());
  }

  /**
   * Method to convert byte array to CaldataPhy object
   *
   * @return actual review output
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private CalData getCDPObj(final byte[] dbData) throws ClassNotFoundException, IOException {
    return CalDataUtil.getCalDataObj(dbData);
  }

}
