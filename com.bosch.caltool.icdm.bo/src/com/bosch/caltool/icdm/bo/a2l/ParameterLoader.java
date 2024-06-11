/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.cdr.WorkPackageStatusHandler;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpDefnVersion;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResponsibility;
import com.bosch.caltool.icdm.database.entity.apic.GttFuncparam;
import com.bosch.caltool.icdm.database.entity.apic.GttObjectName;
import com.bosch.caltool.icdm.database.entity.cdr.TFunction;
import com.bosch.caltool.icdm.database.entity.cdr.TParameter;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWPRespModel;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.FunctionParamProperties;
import com.bosch.caltool.icdm.model.a2l.ParamProperties;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.a2l.ParameterClass;
import com.bosch.caltool.icdm.model.a2l.WpRespLabelResponse;
import com.bosch.caltool.icdm.model.a2l.WpRespModel;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRReportData;
import com.bosch.caltool.icdm.model.general.CommonParamKey;

/**
 * @author svj7cob
 */
public class ParameterLoader extends AbstractBusinessObject<Parameter, TParameter> {

  /**
   * Session data key
   */
  private static final String SESSKEY_ALL_COMPLI_PARAMS = "ALL_COMPLI_PARAMS";
  /**
   * Session data key
   */
  private static final String SESSKEY_ALL_QSSD_PARAMS = "ALL_QSSD_PARAMS";
  /**
   * Session data key for black list parameters
   */
  private static final String SESSKEY_ALL_BLACK_LIST_PARAMS = "ALL_BLACK_LIST_PARAMS";
  /**
   * Version length
   */
  private static final int VERSION_LENGTH = 3;
  /**
   * String builder initial size
   */
  private static final int PARAM_BUFFER_SIZE = 180;
  /**
   * Query result's output column index for property 'Parameter Name'
   */
  private static final int QRIDX_PARAM_NAME = 0;
  /**
   * Query result's output column index for property 'Parameter Class'
   */
  private static final int QRIDX_PCLASS = 1;
  /**
   * Query result's output column index for property 'Codeword YN'
   */
  private static final int QRIDX_CWYN = 2;

  /**
   * Query result's output column index for ssdclass 'Codeword YN'
   */
  private static final int QRIDX_SSDCLASS = 3;

  /**
   * Query result's output column index for ssdclass 'Parameter id'
   */
  private static final int QRIDX_ID = 6;

  /**
   * Query result's output column index for property 'QSSD flag'
   */
  private static final int QRIDX_QSSD = 5;

  /**
   * Query result's output column index for ssdclass 'black list'
   */
  private static final int QRIDX_BLACKLIST = 4;

  /**
   * @param servData Service Data
   */
  public ParameterLoader(final ServiceData servData) {
    super(servData, MODEL_TYPE.CDR_FUNC_PARAM, TParameter.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Parameter createDataObject(final TParameter entity) throws DataException {
    Parameter data = new Parameter();

    setCommonFields(data, entity);

    data.setName(entity.getName());
    data.setDescription(getLangSpecTxt(entity.getLongname(), entity.getLongnameGer()));
    data.setSsdClass(entity.getSsdClass());
    data.setCodeWord(entity.getIscodeword());
    data.setCustPrm(entity.getIscustprm());

    data.setLongName(entity.getLongname());
    data.setLongNameGer(entity.getLongnameGer());
    data.setParamHint(entity.getHint());
    data.setType(entity.getPtype());

    ParameterClass paramClass = ParameterClass.getParamClass(entity.getPclass());
    data.setpClassText(paramClass == null ? "" : paramClass.getText());

    data.setIsBitWise(entity.getIsbitwise());
    data.setBlackList(yOrNToBoolean(entity.getIsBlackListLabel()));
    data.setQssdFlag(yOrNToBoolean(entity.getQssdFlag()));

    return data;
  }

  /**
   * Gets all the compliance parameters with the configured ssd class
   *
   * @return the Map of compli parameters. Key - upper case parameter name, Value - ssd class
   */
  public Map<String, String> getAllCompliParams() {
    Object data = getServiceData().retrieveData(getClass(), SESSKEY_ALL_COMPLI_PARAMS);
    if (data == null) {
      data = internalLoadCompliParamsFromDb();
      getServiceData().storeData(getClass(), SESSKEY_ALL_COMPLI_PARAMS, data);
    }
    return (Map<String, String>) data;
  }


  /**
   * @return Map of qssd parameters. key - param name , value - ptype
   */
  public Map<String, String> getQssdParams() {
    Object data = getServiceData().retrieveData(getClass(), SESSKEY_ALL_QSSD_PARAMS);
    if (data == null) {
      data = internalLoadQssdParamsFromDb();
      getServiceData().storeData(getClass(), SESSKEY_ALL_QSSD_PARAMS, data);
    }
    return (Map<String, String>) data;
  }

  /**
   * @return Map of qssd parameters. key - param name , value - ptype
   */
  public Set<String> getBlackListParams() {
    Object data = getServiceData().retrieveData(getClass(), SESSKEY_ALL_BLACK_LIST_PARAMS);
    if (data == null) {
      data = loadBlackListParamsFromDb();
      getServiceData().storeData(getClass(), SESSKEY_ALL_BLACK_LIST_PARAMS, data);
    }
    return (Set<String>) data;
  }

  /**
   * @return
   */
  private Set<String> loadBlackListParamsFromDb() {
    getLogger().debug("Fetching Black-List parameters from DB...");

    final TypedQuery<String> typeQuery = getEntMgr().createNamedQuery(TParameter.NQ_GET_BLACK_LIST_PARAM, String.class);

    Set<String> blackListParams = new HashSet<>();
    for (String paramName : typeQuery.getResultList()) {
      blackListParams.add(paramName);
    }

    getLogger().info("No. of Black List parameters retrieved : {}", blackListParams.size());

    return blackListParams;
  }

  /**
   * Checks whether the given parameter name is of type Qssd
   *
   * @param paramName parameter name
   * @return if compliance, returns true if input is qssd parameter
   */
  public boolean isQssdParameter(final String paramName) {
    return getQssdParams().containsKey(paramName);
  }

  /**
   * Get the ssd class of this qssd parameter. Null, if not a qssd parameter
   *
   * @param paramName parameter name
   * @return if Qssd, returns the ssd class. if not qssd parameter, returns null
   */
  public String getQssdParameterClass(final String paramName) {
    return getQssdParams().get(paramName);
  }

  /**
   * @return
   */
  private Map<String, String> internalLoadQssdParamsFromDb() {
    getLogger().debug("Fetching QSSD parameters from DB...");

    final TypedQuery<Object[]> typeQuery = getEntMgr().createNamedQuery(TParameter.NQ_GET_QSSD_PARAM, Object[].class);
    Map<String, String> qssdParamsMap = new HashMap<>();
    for (Object[] parameter : typeQuery.getResultList()) {
      qssdParamsMap.put((String) parameter[0], (String) parameter[1]);
    }

    getLogger().info("No. of QSSD parameters retrieved : {}", qssdParamsMap.size());

    return qssdParamsMap;
  }

  /**
   * Get compliance type parameters
   *
   * @return Map of compliance parameters. Key - parameter name, value - parameter type
   */
  public Map<String, String> getCompliParamWithType() {
    Map<String, String> compliParamMap = new HashMap<>();
    TypedQuery<TParameter> query = getCompliParamsQuery();

    for (TParameter tparam : query.getResultList()) {
      compliParamMap.put(tparam.getName(), tparam.getPtype());
    }

    return compliParamMap;
  }


  /**
   * Checks whether the given parameter name is of type compliance
   *
   * @param paramName parameter name
   * @return if compliance, returns the ssd class. if not compliance, returns null
   */
  public boolean isCompliParameter(final String paramName) {
    return getAllCompliParams().containsKey(paramName.toUpperCase(Locale.getDefault()));
  }

  /**
   * Get the ssd class of this compliance parameter. Null, if not a compliance parameter
   *
   * @param paramName parameter name
   * @return if compliance, returns the ssd class. if not compliance parameter, returns null
   */
  public String getCompliParameterClass(final String paramName) {
    return getAllCompliParams().get(paramName.toUpperCase(Locale.getDefault()));
  }

  private Map<String, String> internalLoadCompliParamsFromDb() {
    getLogger().debug("Fetching compliance parameters from DB...");

    TypedQuery<TParameter> query = getCompliParamsQuery();
    List<TParameter> resultList = query.getResultList();

    Map<String, String> compliParamMap = new HashMap<>();
    for (TParameter tparameter : resultList) {
      compliParamMap.put(tparameter.getName().toUpperCase(), tparameter.getSsdClass());
    }

    getLogger().info("No. of compliance parameters retrieved : {}", compliParamMap.size());

    return compliParamMap;
  }

  /**
   * @return
   */
  private TypedQuery<TParameter> getCompliParamsQuery() {
    String propVal = new CommonParamLoader(getServiceData()).getValue(CommonParamKey.COMPLI_CLASS_TYPE);
    Set<String> ssdClassSet = new HashSet<>(Arrays.asList(propVal.split(",")));

    TypedQuery<TParameter> query =
        getServiceData().getEntMgr().createNamedQuery(TParameter.NQ_GET_COMPLI_PARAMS, TParameter.class);
    query.setParameter("ssdClassSet", ssdClassSet);

    return query;
  }


  /**
   * @param paramNames parameters
   * @return the param obj Map. Key - parameter name, value - Parameter object
   * @throws DataException data retrieval error
   */
  public Map<String, Parameter> getParamMapByParamNames(final List<String> paramNames) throws DataException {
    getLogger().info("Fetching parameters by parameter names... Input size = {}", paramNames.size());

    EntityManager entMgr = getNewEntMgr();

    Map<String, Parameter> paramMap = new HashMap<>();

    try {
      beginTransaction(entMgr);

      GttObjectName tempRec;
      long id = 1;

      // Create entities for all the params
      for (String paramName : paramNames) {
        tempRec = new GttObjectName();
        tempRec.setId(id);
        tempRec.setObjName(paramName.trim());
        entMgr.persist(tempRec);
        id++;
      }
      entMgr.flush();

      // Query joining the tparam and gtt table
      TypedQuery<TParameter> query = entMgr.createNamedQuery(TParameter.NQ_GET_PARAMS_BY_PARAM_NAMES, TParameter.class);

      List<TParameter> retList = query.getResultList();

      if ((retList != null) && (!retList.isEmpty())) {
        for (TParameter tParameter : retList) {
          Parameter param = createDataObject(tParameter);
          paramMap.put(param.getName(), param);
        }
      }

      rollbackTransaction(entMgr);

    }
    finally {
      closeEntityManager(entMgr);
    }

    getLogger().info("Parameters fetched = {}", paramMap.size());

    return paramMap;
  }


  /**
   * Identifies the invalid params (not in DB) from the given list
   *
   * @param paramNames list of parameter names
   * @return list of unavailable params
   * @throws DataException data retrieval error
   */
  public List<String> getInvalidParams(final List<String> paramNames) {
    getLogger().debug("Fetching invalid Parameters from the input list, size = {}", paramNames.size());

    TypedQuery<TParameter> query = getEntMgr().createNamedQuery(TParameter.NQ_GET_PARAMS_IN_LIST, TParameter.class);
    query.setParameter("paramNameSet", paramNames);

    List<TParameter> resultList = query.getResultList();

    List<String> paramDbList = new ArrayList<>();
    for (TParameter param : resultList) {
      paramDbList.add(param.getName().trim());
    }

    List<String> invalidParamsList = new ArrayList<>();
    for (String string : paramNames) {
      if (!paramDbList.contains(string)) {
        invalidParamsList.add(string);
      }
    }

    getLogger().debug("Invalid Parameters = {}", invalidParamsList.size());

    return invalidParamsList;
  }

  /**
   * @param paramIdSet param id set
   * @return the param map
   * @throws DataException DataException
   */
  public Map<Long, Parameter> getParamsByIds(final Set<Long> paramIdSet) throws DataException {
    return getDataObjectByID(paramIdSet);
  }


  /**
   * @param funcName funcName
   * @param version version
   * @param byVariant by function variant
   * @param paramNameSet set of parameters from imported file
   * @return the Param Rules Output
   * @throws DataException DataException
   */
  public Map<String, Parameter> getParamsMap(final String funcName, final String version, final Boolean byVariant,
      final Set<String> paramNameSet)
      throws DataException {

    getLogger().debug("Fetching params - funcName={}, version={}, byVariant={} ...", funcName, version, byVariant);

    String query = getQueryForParamMap(funcName, version, byVariant);

    return getResultParams(query, paramNameSet);
  }

  /**
   * @param funcName
   * @param version
   * @param byVariant
   * @return
   */
  private String getQueryForParamMap(final String funcName, final String version, final Boolean byVariant) {
    final StringBuilder query = new StringBuilder(PARAM_BUFFER_SIZE);

    // Build query string
    query.append(
        "SELECT DISTINCT  param from TFunctionversion funcVer, TParameter param  where   funcVer.defcharname = param.name and funcVer.funcNameUpper = '")
        .append(funcName.toUpperCase(Locale.GERMAN)).append('\'');
    if (version != null) {
      queryForVersion(version, byVariant, query);
    }

    return query.toString();
  }

  /**
   * @param funcNameSet as input
   * @return the Param count
   */
  public Long getFunctionsParamCount(final Set<String> funcNameSet) {
    getLogger().debug("ParameterLoader.getFunctionsParamCount() - Inputs = {}", funcNameSet.size());

    beginTransaction(getEntMgr());

    GttObjectName tempRec;
    long id = 1;

    // Create entities for all the functions
    for (String funcName : funcNameSet) {
      tempRec = new GttObjectName();
      tempRec.setId(id);
      tempRec.setObjName(funcName);
      getEntMgr().persist(tempRec);
      id++;
    }
    getEntMgr().flush();

    final TypedQuery<Long> typeQuery =
        getEntMgr().createNamedQuery(TParameter.NQ_GET_PARAM_COUNT_BY_FUNCNAMESET, Long.class);
    final Long paramCount = typeQuery.getSingleResult();

    rollbackTransaction(getEntMgr());

    getLogger().debug("ParameterLoader.getFunctionsParamCount() - paramCount = {}", paramCount);

    return paramCount;

  }

  /**
   * @param version
   * @param byVariant
   * @param query
   */
  private void queryForVersion(final String version, final Boolean byVariant, final StringBuilder query) {
    if (byVariant == null) {
      query.append(" and funcVer.funcversion = '" + version + '\'');
    }
    // by funtion variants
    else if (byVariant.booleanValue()) {
      String variant;
      // Check if the version has both major and minor versions, and dot is available
      if ((version.split("\\.").length == VERSION_LENGTH) && (version.lastIndexOf('.') != -1)) {
        variant = version.substring(0, version.lastIndexOf('.'));
      }
      else {
        variant = version;
      }
      query.append(" and (funcVer.funcversion like '").append(variant).append("' or funcVer.funcversion like '")
          .append(variant).append(".%' )");
    }
    else { // by alternative
      String alternative;
      // check if dot is available
      if (version.indexOf('.') == -1) {
        alternative = version;
      }
      else {
        alternative = version.substring(0, version.indexOf('.'));
      }
      query.append(" and (funcVer.funcversion like '").append(alternative).append("' or funcVer.funcversion like '")
          .append(alternative).append(".%' )");
    }
  }

  /**
   * Get Result parameters
   *
   * @param query query to execute
   * @param paramNameSet
   * @param funcName function name
   * @param version version
   * @return CDRFuncParameters
   * @throws DataException
   */
  private Map<String, Parameter> getResultParams(final String query, final Set<String> paramNameSet)
      throws DataException {

    final Map<String, Parameter> paramMap = new ConcurrentHashMap<>();

    final TypedQuery<TParameter> typeQuery = getEntMgr().createQuery(query, TParameter.class);
    typeQuery.setHint(ApicConstants.FETCH_SIZE, "15000");
    final List<TParameter> resultList = typeQuery.getResultList();

    List<TParameter> resultListFiltered;
    if (null == paramNameSet) {
      resultListFiltered = resultList;
    }
    else {
      resultListFiltered =
          resultList.stream().filter(tparam -> paramNameSet.contains(tparam.getName())).collect(Collectors.toList());
    }

    for (TParameter dbParam : resultListFiltered) {
      Parameter cdrParam = createDataObject(dbParam);
      // add param to the return set for this function
      paramMap.put(dbParam.getName(), cdrParam);
    }

    getLogger().debug("Parameters retrieved = {}", paramMap.size());

    return paramMap;
  }

  /**
   * Get Params By Name and type
   *
   * @param paramName parameter Name
   * @param paramType parameter Type
   * @return the parameter object
   * @throws DataException data retreval error
   */
  public Parameter getParamByName(final String paramName, final String paramType) throws DataException {
    getLogger().debug("Get param object : input : name - {}, type - {}", paramName, paramType);

    TypedQuery<TParameter> tQuery = getEntMgr().createNamedQuery(TParameter.NQ_GET_PARAM_BY_NAME, TParameter.class);
    tQuery.setParameter("name", paramName);
    tQuery.setParameter("type", paramType);

    List<TParameter> dbParams = tQuery.getResultList();
    Parameter param = dbParams.isEmpty() ? null : createDataObject(dbParams.get(0));
    getLogger().debug("Get param by name and type completed. Found = {}", (param != null));

    return param;
  }


  /**
   * @param paramName parameter Name
   * @return parameter object
   * @throws DataException data retreval error
   */
  public Parameter getParamOnlyByName(final String paramName) throws DataException {
    getLogger().debug("Get param object : input : name - {}", paramName);

    TypedQuery<TParameter> tQuery =
        getEntMgr().createNamedQuery(TParameter.NQ_GET_PARAM_ONLY_BY_NAME, TParameter.class);
    tQuery.setParameter("name", paramName);

    List<TParameter> dbParams = tQuery.getResultList();
    Parameter param = dbParams.isEmpty() ? null : createDataObject(dbParams.get(0));
    getLogger().debug("Get param by name completed. Found = {}", (param != null));

    return param;
  }

  /**
   * @param paramNameSet paramNameSet
   * @param a2lCharMap A2L char map
   * @return map of param objects
   * @throws DataException DataException
   */
  public Map<String, Parameter> getParamsByName(final Set<String> paramNameSet,
      final Map<String, Characteristic> a2lCharMap)
      throws DataException {

    getLogger().debug("Get param objects by name, input size - {}", paramNameSet.size());

    beginTransaction(getEntMgr());

    GttObjectName tempRec;
    long id = 1;

    // Create entities for all the functions
    for (String paramName : paramNameSet) {
      tempRec = new GttObjectName();
      tempRec.setId(id);
      tempRec.setObjName(paramName);
      getEntMgr().persist(tempRec);
      id++;
    }
    getEntMgr().flush();

    final TypedQuery<TParameter> typeQuery =
        getEntMgr().createNamedQuery(TParameter.NQ_GET_PARAMS_BY_NAME, TParameter.class);
    final List<TParameter> paramList = typeQuery.getResultList();

    Map<String, Parameter> retParamMap = new HashMap<>();
    for (TParameter entity : paramList) {
      Parameter param = createDataObject(entity);

      if (a2lCharMap == null) {
        retParamMap.put(param.getName(), param);
      }
      else {
        // Match the param type with A2L's
        Characteristic characteristic = a2lCharMap.get(param.getName());
        if (characteristic.getType().equals(param.getType())) {
          retParamMap.put(param.getName(), param);
        }
      }
    }

    rollbackTransaction(getEntMgr());

    getLogger().debug("Params found size = {}", retParamMap.size());

    return retParamMap;
  }

  /**
   * @param paramNameSet paramNameSet
   * @return map of param objects
   * @throws DataException DataException
   */
  public Map<String, Parameter> getCompPkgParamsByName(final Set<String> paramNameSet) throws DataException {

    getLogger().debug("Get CompPkg param objects by name, input size - {}", paramNameSet.size());

    beginTransaction(getEntMgr());

    GttObjectName tempRec;
    long id = 1;

    // Create entities for all the functions
    for (String paramName : paramNameSet) {
      tempRec = new GttObjectName();
      tempRec.setId(id);
      tempRec.setObjName(paramName);
      getEntMgr().persist(tempRec);
      id++;
    }
    getEntMgr().flush();

    final TypedQuery<TParameter> typeQuery =
        getEntMgr().createNamedQuery(TParameter.NQ_GET_PARAMS_BY_NAME, TParameter.class);
    final List<TParameter> paramList = typeQuery.getResultList();

    Map<String, Parameter> retParamMap = new HashMap<>();

    for (TParameter entity : paramList) {
      Parameter param = createDataObject(entity);
      retParamMap.put(param.getName(), param);
    }

    rollbackTransaction(getEntMgr());

    getLogger().debug("CompPkg Params found, size = {}", retParamMap.size());

    return retParamMap;

  }


  /**
   * Get the additional parameter properties of the characteristics in A2L file from iCDM database
   *
   * @param a2lFileID A2L File ID
   * @return propMap properties map. Key - parameter name, Value - property object
   */
  public Map<String, ParamProperties> fetchAllA2lParamProps(final Long a2lFileID) {
    getLogger().debug("Fetching parameter attributes of A2L file : {}", a2lFileID);

    final Map<String, ParamProperties> propMap = new HashMap<>();
    final Query query = getAllA2lParamPropQuery(a2lFileID);

    // get the class and code Word of each parameter.
    fillParamPropMap(propMap, query.getResultList());

    getLogger().debug("Parameter properties fetched : {}", propMap.size());

    return propMap;
  }


  /**
   * Get the additional parameter properties of the characteristics in A2L file from iCDM database
   *
   * @param cdrRprtData CDR Report Data Model
   * @return propMap properties map. Key - parameter name, Value - property object
   * @throws IcdmException
   */
  public Map<String, ParamProperties> fetchA2lWpRespParamProps(final CDRReportData cdrRprtData) throws IcdmException {

    getLogger().debug("Fetching parameter attributes of A2L file : {}", cdrRprtData.getA2lFileId());

    List<Object[]> resultList = new ArrayList<>();
    final Map<String, ParamProperties> propMap = new HashMap<>();
    final Query query = getAllA2lParamPropQuery(cdrRprtData.getA2lFileId());
    List<Object[]> paramPropObjList = query.getResultList();

    if (CommonUtils.isNotNull(cdrRprtData.getA2lRespId())) {
      // Filter the resultant List to get only data for parameters with selected WP and Resp
      List<Long> paramListWithSelRespAndWp =
          new ParameterLoader(getServiceData()).getParamListByA2lRespAndWP(cdrRprtData);
      for (Object[] obj : paramPropObjList) {
        if (paramListWithSelRespAndWp.contains(Long.valueOf(obj[QRIDX_ID].toString()))) {
          resultList.add(obj);
        }
      }
    }
    else {
      // If the report generation call is from A2l, then pass the complete list
      resultList.addAll(paramPropObjList);
    }

    // get the class and code Word of each parameter.
    fillParamPropMap(propMap, resultList);

    getLogger().debug("Parameter properties fetched : {}", propMap.size());

    return propMap;
  }


  /**
   * @param a2lFileID
   * @return
   */
  private Query getAllA2lParamPropQuery(final Long a2lFileID) {
    final Query query = getEntMgr().createNamedQuery(TParameter.NNQ_GET_ALL_A2L_PARAM_PROPS);
    query.setParameter(1, a2lFileID);
    query.setHint(ApicConstants.FETCH_SIZE, "2000");
    return query;
  }


  /**
   * Get List of param Id with selected Resp and selected WP id for active WP defn vers id
   *
   * @param cdrRprtData
   * @return List of param Id with Selected A2l Resp Id, a2l Wp Id and active wpdefn vers ID
   * @throws IcdmException
   */
  public List<Long> getParamListByA2lRespAndWP(final CDRReportData cdrRprtData) throws IcdmException {

    List<Long> paramIdList = new ArrayList<>();
    // active a2lwpdefversion
    TA2lWpDefnVersion activeA2lWpDefnVersion =
        new A2lWpDefnVersionLoader(getServiceData()).getActiveA2lWPDefnVersionEntityFromA2l(cdrRprtData.getPidcA2lId());
    // Query to get all WP RESP ID for the active WP Defn Vers of selected A2l
    Set<TA2lWpResponsibility> ta2lWpResponsibilities = new HashSet<>(activeA2lWpDefnVersion.getTA2lWpResponsibility());

    WorkPackageStatusHandler workPackageStatusHandler = new WorkPackageStatusHandler(getServiceData());
    // fetch the matching variant group for the pidcVaraint
    A2lVariantGroup a2lVarGrp =
        workPackageStatusHandler.fetchVariantGroup(activeA2lWpDefnVersion, cdrRprtData.getVarId());

    // fetching a2lwpRespModel based on validated wp resp
    Set<A2lWPRespModel> wpRespLabelResponse =
        workPackageStatusHandler.getWpRespLabelResponse(a2lVarGrp, ta2lWpResponsibilities);

    List<WpRespLabelResponse> wpRespLabResponse =
        new A2lWpResponsibilityLoader(getServiceData()).getWpResp(cdrRprtData.getPidcA2lId(), cdrRprtData.getVarId());

    Map<WpRespModel, List<Long>> resolveWpRespLabels = workPackageStatusHandler.resolveWpRespLabels(wpRespLabResponse);

    for (A2lWPRespModel a2lWPRespModel : wpRespLabelResponse) {
      if ((CommonUtils.isEqual(a2lWPRespModel.getA2lRespId(), cdrRprtData.getA2lRespId()) &&
          CommonUtils.isEqual(a2lWPRespModel.getA2lWpId(), cdrRprtData.getA2lWpId())) ||
          (CommonUtils.isEqual(a2lWPRespModel.getA2lRespId(), cdrRprtData.getA2lRespId()) &&
              CommonUtils.isNull(cdrRprtData.getA2lWpId()))) {
        List<Long> paramList =
            getWpRespParamList(resolveWpRespLabels, a2lWPRespModel.getA2lRespId(), a2lWPRespModel.getA2lWpId());
        paramIdList.addAll(paramList);
      }
    }
    return paramIdList;
  }


  /**
   * @param resolveWpRespLabels
   * @param a2lRespId
   * @param a2lWpId
   * @param wpRespModelVal
   * @return
   */
  private List<Long> getWpRespParamList(final Map<WpRespModel, List<Long>> resolveWpRespLabels, final Long a2lRespId,
      final Long a2lWpId) {
    List<Long> paramList = new ArrayList<>();
    for (Entry<WpRespModel, List<Long>> wpRespLabelEntrySet : resolveWpRespLabels.entrySet()) {
      WpRespModel wpRespModel = wpRespLabelEntrySet.getKey();
      if (a2lWpId.equals(wpRespModel.getA2lWpId()) && a2lRespId.equals(wpRespModel.getA2lResponsibility().getId())) {
        paramList = wpRespLabelEntrySet.getValue();
        break;
      }
    }
    return paramList;
  }


  /**
   * @param propMap
   * @param resultList
   */
  private void fillParamPropMap(final Map<String, ParamProperties> propMap, final List<Object[]> resultList) {
    String paramName;
    String paramId;
    ParamProperties paramProps;
    for (Object[] resArr : resultList) {
      paramName = String.valueOf(resArr[QRIDX_PARAM_NAME]);
      paramId = String.valueOf(resArr[QRIDX_ID]);

      paramProps = new ParamProperties();

      paramProps.setId(Long.valueOf(paramId));
      // Set value if PClass is not '-'
      if ((resArr[QRIDX_PCLASS] != null) && !String.valueOf(resArr[QRIDX_PCLASS]).equals("-")) {
        paramProps.setPClass(String.valueOf(resArr[QRIDX_PCLASS]));
      }

      paramProps.setCodeWord(yOrNToBoolean(String.valueOf(resArr[QRIDX_CWYN])));

      // Set value if SSD class is not 'NOT_IN_SSD'
      if ((resArr[QRIDX_SSDCLASS] != null) && !String.valueOf(resArr[QRIDX_SSDCLASS]).equals("NOT_IN_SSD")) {
        paramProps.setSsdClass(String.valueOf(resArr[QRIDX_SSDCLASS]));
      }

      paramProps.setBlackList(yOrNToBoolean(String.valueOf(resArr[QRIDX_BLACKLIST])));
      paramProps.setQssdParameter(yOrNToBoolean(String.valueOf(resArr[QRIDX_QSSD])));

      propMap.put(paramName, paramProps);
    }
  }

  /**
   * @param labelNameSet Label Name
   * @return list of functions
   */
  private Set<String> getMismatchLabelListfromTable(final Set<String> labelNameSet) {
    beginTransaction(getEntMgr());

    GttObjectName tempRec;
    long id = 1;

    // Create entities for all the functions
    for (String label : labelNameSet) {
      tempRec = new GttObjectName();
      tempRec.setId(id);
      tempRec.setObjName(label.trim());
      getEntMgr().persist(tempRec);
      id++;
    }
    getEntMgr().flush();

    final Query typeQuery = getEntMgr().createNamedQuery(TParameter.NQ_GET_INVALID_LABEL, Object.class);
    Set<String> resultList = new HashSet<>();
    List<String> result = typeQuery.getResultList();
    for (String entity : result) {
      resultList.add(entity);
    }

    rollbackTransaction(getEntMgr());

    labelNameSet.removeAll(resultList);

    return labelNameSet;
  }

  /**
   * @param labelSet set of labels
   * @param a2lLabelset set of labels in a2l file
   * @return invalid label
   */
  public Set<String> getMismatchLabelList(final Set<String> labelSet, final Set<String> a2lLabelset) {
    Set<String> invalidLabelsInTable = getMismatchLabelListfromTable(labelSet);
    Set<String> invalidLabels = invalidLabelInList(labelSet, a2lLabelset);

    for (String label : invalidLabelsInTable) {
      invalidLabels.add(label);
    }
    return invalidLabels;
  }

  private Set<String> invalidLabelInList(final Set<String> labelSet, final Set<String> a2lLabelset) {
    Set<String> invalidLabelA2L = new HashSet<>();
    for (String label : labelSet) {
      if (!a2lLabelset.contains(label)) {
        invalidLabelA2L.add(label);
      }
    }
    return invalidLabelA2L;
  }


  /**
   * Method is used to get a list of Parameter using the paramName list
   *
   * @param paramNameList list of Prameter Name
   * @return list of functions
   * @throws DataException data retreval error
   */
  public List<Parameter> getParamObjListByParamName(final List<String> paramNameList) throws DataException {
    beginTransaction(getEntMgr());

    GttObjectName tempRec;
    long id = 1;

    // Create entities for all the functions
    for (String paramName : paramNameList) {
      tempRec = new GttObjectName();
      tempRec.setId(id);
      tempRec.setObjName(paramName.trim());
      getEntMgr().persist(tempRec);
      id++;
    }
    getEntMgr().flush();

    final TypedQuery<TParameter> typeQuery =
        getEntMgr().createNamedQuery(TParameter.NQ_GET_PARAM_OBJ_BY_PARAM_NAME, TParameter.class);

    List<TParameter> resultList = typeQuery.getResultList();

    List<Parameter> paramList = new ArrayList<>();

    for (TParameter tParameter : resultList) {
      paramList.add(createDataObject(tParameter));
    }

    rollbackTransaction(getEntMgr());

    return paramList;
  }


  /**
   * Fetch cdr parameters and functions into maps
   *
   * @param paramNameSet parameter Names
   * @return ParameterPropOutput model
   */
  // Task 246879
  public ParameterPropOutput fetchCDRParamsAndFuncs(final Set<String> paramNameSet) {
    getLogger().debug("Fetching CDR Params And Funcs, input size : {}", paramNameSet.size());

    beginTransaction(getEntMgr());

    ParameterPropOutput output = new ParameterPropOutput();

    try {
      // Delete the existing records in this temp table, if any
      final Query delQuery = getEntMgr().createQuery("delete from GttFuncparam temp");
      delQuery.executeUpdate();
      GttFuncparam tempfuncParam;
      long recID = 1;

      // param name insertion into temporary table
      for (String paramName : paramNameSet) {
        // insert records into temporary table GttFuncparam
        tempfuncParam = new GttFuncparam();
        tempfuncParam.setId(recID);
        tempfuncParam.setParamName(paramName);

        getEntMgr().persist(tempfuncParam);
        recID++;
      }

      getEntMgr().flush();

      final Query query = getEntMgr().createNamedQuery(TParameter.NQ_PARAMS_N_FUNCTIONS_BY_TEMP_TABLE);
      query.setHint(ApicConstants.FETCH_SIZE, "2000");

      // get the parameters list
      List dbRows = query.getResultList();
      for (Object rowObj : dbRows) {
        addParamFuncResultToParamPropOutput(output, rowObj);
      }
      delQuery.executeUpdate();
    }
    catch (Exception ex) {
      getLogger().error(ex.getMessage(), ex);
    }
    finally {
      rollbackTransaction(getEntMgr());
    }

    getLogger().debug(
        "Fetching CDR Params And Funcs finished. ParamNameObjMap size = {}, ParamFuncObjMap size = {}, ParamNameTypeMap size = {}, ParamPropMap size = {}",
        output.getParamNameObjMap().size(), output.getParamFuncObjMap().size(), output.getParamNameTypeMap().size(),
        output.getParamPropMap().size());

    return output;

  }

  private void addParamFuncResultToParamPropOutput(final ParameterPropOutput output, final Object rowObj)
      throws DataException {
    if (rowObj instanceof Object[]) {

      Object[] rowObjArr = (Object[]) rowObj;

      if (rowObjArr[0] instanceof TParameter) {
        TParameter tParam = (TParameter) rowObjArr[0];
        // generate the CDRFuncParameter object

        Parameter cdrFuncParam = createDataObject(tParam);

        String paramName = tParam.getName();


        output.getParamNameObjMap().put(paramName, cdrFuncParam);


        if (rowObjArr[1] instanceof TFunction) {
          TFunction tFunc = (TFunction) rowObjArr[1];
          // generate CDRFunction object


          final Function cdrFunction = new FunctionLoader(getServiceData()).createDataObject(tFunc);
          output.getParamFuncObjMap().put(paramName, cdrFunction);

          output.getParamNameTypeMap().put(paramName, cdrFuncParam.getType());
          // Param properties can be null. Cannot be changed to CHM
          Map<String, String> propMap = new HashMap<>();
          propMap.put(CDRConstants.CDIKEY_PARAM_NAME, cdrFuncParam.getName());
          propMap.put(CDRConstants.CDIKEY_FUNCTION_NAME, cdrFunction.getName());
          propMap.put(CDRConstants.CDIKEY_PARAM_CLASS, cdrFuncParam.getpClassText());
          propMap.put(CDRConstants.CDIKEY_CODE_WORD, cdrFuncParam.getCodeWord());
          propMap.put(CDRConstants.CDIKEY_LONG_NAME, cdrFuncParam.getLongName());
          propMap.put(CDRConstants.CDIKEY_CAL_HINT, cdrFuncParam.getParamHint());

          String isParamBitWise = cdrFuncParam.getIsBitWise();
          propMap.put(CDRConstants.CDIKEY_BIT_WISE,
              CommonUtils.isNotNull(isParamBitWise) && yOrNToBoolean(isParamBitWise) ? "Yes" : "No");

          output.getParamPropMap().put(paramName, propMap);
        }
      }
    }
  }

  /**
   * @param paramName paramName
   * @param funcName funcName
   * @param ruleSetId ruleSetId
   * @return FunctionParamProperties List
   */
  public SortedSet<FunctionParamProperties> getSearchParameters(final String paramName, final String funcName,
      final Long ruleSetId) {

    String funcNameLike = CommonUtils.convertStringForDbQuery(funcName) + "%";
    String paramNameLike = CommonUtils.convertStringForDbQuery(paramName) + "%";

    getLogger().debug("Fetching params not in ruleset '{}' : param-name like '{}', func-name like '{}'", ruleSetId,
        paramNameLike, funcNameLike);

    Query createQuery = getEntMgr().createNamedQuery(TParameter.NQ_GET_SEARCH_PARAM, TParameter.class);
    createQuery.setParameter("funcName", funcNameLike.toUpperCase(Locale.ENGLISH));
    createQuery.setParameter("paramName", paramNameLike.toUpperCase(Locale.ENGLISH));
    createQuery.setParameter("rset_id", ruleSetId);

    CommonParamLoader loader = new CommonParamLoader(getServiceData());
    createQuery.setMaxResults(Integer.parseInt(loader.getValue(CommonParamKey.DB_SEARCH_MAX_RESULT_SIZE)));

    createQuery.setHint(ApicConstants.READ_ONLY, "true");
    createQuery.setHint(ApicConstants.SHARED_CACHE, "true");

    final List<Object> resultList = createQuery.getResultList();

    Object[] dbParamPropsArr;
    SortedSet<FunctionParamProperties> paramFuncSet = new TreeSet<>();

    for (Object row : resultList) {
      dbParamPropsArr = (Object[]) row;

      FunctionParamProperties funcParamProp = new FunctionParamProperties();

      funcParamProp.setFunctionName(dbParamPropsArr[0].toString());
      funcParamProp.setParamId(Long.parseLong(dbParamPropsArr[1].toString()));
      funcParamProp.setParamName(dbParamPropsArr[2].toString());
      if (dbParamPropsArr[3] != null) {
        funcParamProp.setParamLongName(dbParamPropsArr[3].toString());
      }
      if (dbParamPropsArr[4] != null) {
        funcParamProp.setParamLongNameGer(dbParamPropsArr[4].toString());
      }
      funcParamProp.setParamType(dbParamPropsArr[5].toString());
      funcParamProp.setFuncId(Long.parseLong(dbParamPropsArr[6].toString()));

      paramFuncSet.add(funcParamProp);
    }

    getLogger().debug("Params retrieved = {}", paramFuncSet.size());

    return paramFuncSet;
  }

  /**
   * Get all parameter objects, with the given name.
   *
   * @param paramName parameter name
   * @return Map. Key - param Id; value - parameter
   * @throws DataException error while retrieving data
   */
  // TODO : Can this be merged with getParamOnlyByName() ? Hint : queries are different, but necessary?
  public Map<Long, Parameter> getParamByNameOnly(final String paramName) throws DataException {

    final TypedQuery<Object[]> query =
        getEntMgr().createNamedQuery(TParameter.NQ_GET_PARAM_BY_NAME_ONLY, Object[].class);
    query.setParameter("name", paramName);

    final Map<Long, Parameter> retMap = new HashMap<>();

    Parameter cdrParam;
    for (Object[] resObj : query.getResultList()) {
      TParameter entity = (TParameter) resObj[1];
      if (!retMap.containsKey(entity.getId())) {
        cdrParam = createDataObject(entity);
        // add param to the return set for this function
        retMap.put(cdrParam.getId(), cdrParam);
      }
    }

    return retMap;
  }

  /**
   * @param firstName param firstname
   * @param lastName param lastname
   * @return param Id List
   */
  public List<Long> fetchAllVarCodedParam(final String firstName, final String lastName) {
    getLogger().debug("Fetch all the parameters for the base parameter {}", firstName);

    // Enable case in-sensitive search
    String query = "SELECT param from TParameter param where UPPER(param.name) like '" +
        firstName.toUpperCase(Locale.GERMAN) + "[" + "%" + lastName.toUpperCase(Locale.GERMAN);
    if (lastName.length() > 0) {
      query = CommonUtils.concatenate(query, "%'");
    }
    else {
      query = CommonUtils.concatenate(query, "'");
    }

    final TypedQuery<TParameter> typeQuery = getEntMgr().createQuery(query, TParameter.class);
    typeQuery.setHint(ApicConstants.READ_ONLY, "true");
    typeQuery.setHint(ApicConstants.FETCH_SIZE, "2000");
    typeQuery.setHint(ApicConstants.SHARED_CACHE, "true");

    final List<TParameter> resultList = typeQuery.getResultList();

    List<Long> paramIds = new ArrayList<>();
    for (TParameter dbParam : resultList) {
      paramIds.add(dbParam.getId());
    }

    getLogger().debug("No of Variant coded parameters fetched {}", paramIds.size());

    return paramIds;
  }

  /**
   * @param a2lFileID a2l file id
   * @return set of read only parameter in a2l file
   */
  public Set<Long> fetchReadOnlyParams(final Long a2lFileID) {
    Set<Long> readOnlyParamIDs = new HashSet<>();

    getLogger().debug("Fetching read only parameters in a2l File : {}", a2lFileID);

    final Query query = getEntMgr().createNamedQuery(TParameter.NNQ_GET_READONLY_PARAMS_ID_BY_A2L_ID);
    query.setParameter(1, a2lFileID);

    for (Object val : query.getResultList()) {
      BigDecimal value = (BigDecimal) val;
      readOnlyParamIDs.add(value.longValue());
    }

    return readOnlyParamIDs;
  }

}
