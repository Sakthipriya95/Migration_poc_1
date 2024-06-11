/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo; // NOPMD by bne4cob on 10/2/13 9:38 AM This is a wrapper class

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentMap;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.caltool.apic.jpa.bo.AbstractProjectObject;
import com.bosch.caltool.apic.jpa.bo.ApicDataProvider;
import com.bosch.caltool.apic.jpa.bo.AttributeValue;
import com.bosch.caltool.apic.jpa.bo.NodeAccessRight;
import com.bosch.caltool.apic.jpa.bo.PIDCA2l;
import com.bosch.caltool.apic.jpa.bo.PIDCVariant;
import com.bosch.caltool.apic.jpa.bo.PIDCVersion;
import com.bosch.caltool.apic.jpa.rules.bo.IParameter;
import com.bosch.caltool.apic.jpa.rules.bo.IParameterCollection;
import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.icdm.common.exception.SsdInterfaceException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TParameter;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.ssd.icdm.model.CDRRule;

/**
 * CDRDataProvider provides data related to CDR
 */
public class CDRDataProvider extends AbstractDataProvider {

  /**
   * No variant Node
   */
  private static final String NO_VARIANT = ApicConstants.DUMMY_VAR_NODE_NOVAR;

  /**
   * array size of special nodes in pidc tree
   */
  private static final int TREENODE_ARRAY_SIZE = 3;

  /**
   * The CDR data loader instance
   */
  private final CDRDataLoader dataLoader;

  /**
   * The CDR data cache
   */
  private final CDRDataCache dataCache;

  /**
   * The entity provider
   */
  private final CDREntityProvider entityProvider;

  /**
   * The APIC data provider instance
   */
  private final ApicDataProvider apicDataProvider;

  /**
   * This constructor is called when entity manager and laguage are provided from outside.
   *
   * @param apicDataProvider the apic data provider
   */
  public CDRDataProvider(final ApicDataProvider apicDataProvider) {

    super();

    this.apicDataProvider = apicDataProvider;

    this.entityProvider = new CDREntityProvider(getLogger(), CDREntityType.values());
    this.dataCache = new CDRDataCache(this);

    this.dataLoader = new CDRDataLoader(this, this.dataCache, this.entityProvider, getLogger());
    this.dataLoader.fetchStartupData();

  }

  /**
   * @return the dataLoader
   */
  @Override
  protected CDRDataLoader getDataLoader() {
    return this.dataLoader;
  }

  /**
   * @return the dataCache
   */
  @Override
  protected CDRDataCache getDataCache() {
    return this.dataCache;
  }

  /**
   * @return the entityProvider
   */
  @Override
  protected CDREntityProvider getEntityProvider() {
    return this.entityProvider;
  }

  /**
   * @return the apicDataProvider
   */
  protected ApicDataProvider getApicDataProvider() {
    return this.apicDataProvider;
  }

  /**
   * @return the logger
   */
  @Override
  protected final ILoggerAdapter getLogger() {
    return super.getLogger();
  }

  /**
   * @param pidcVer pidcard version for refresh Icdm-881 Create a new method to call the Refresh for DCN Delete of a
   *          result
   */
  public void reloadPidcVersionResults(final PIDCVersion pidcVer) {
    getDataCache().reloadPIDCResults(pidcVer);
  }

  /**
   * @param pidcVer pidcard version for refresh Icdm-881 Create a new method to call the Refresh for DCN Delete of a
   *          result
   */
  public void reloadPidcResults(final PIDCVersion pidcVer) {
    getDataCache().reloadResults(pidcVer);
  }

  /**
   * Add new Changed Data to the cache. Access to this method is synchronized
   *
   * @param changedData the changed data
   */
  public void addChangedData(final Map<Long, ChangedData> changedData) {
    ObjectStore.getInstance().getChangeNotificationCache().addChangedData(changedData);
  }

  /**
   * Get CDR Result
   *
   * @param entityID id
   * @return CDRObject
   */
  public CDRResult getCDRResult(final Long entityID) {
    return this.dataCache.getCDRResult(entityID);
  }


  /**
   * Get CDR Result
   *
   * @param entityID id
   * @return CDRObject
   */
  public CDRSecondaryResult getCDRResSecondary(final Long entityID) {
    return this.dataCache.getCDRResSecondary(entityID);
  }


  /**
   * Get CDR Result
   *
   * @param entityID id
   * @return CDRObject
   */
  public CDRResParamSecondary getCDRResParamSecondary(final Long entityID) {
    return this.dataCache.getCDRResParamSecondary(entityID);
  }

  /**
   * Get CDR Result Function
   *
   * @param entityID id
   * @return CDRObject
   */
  public CDRResultFunction getCDRResFunction(final Long entityID) {
    return this.dataCache.getCDRResultFunction(entityID);
  }

  /**
   * Returns whether the pidc has CDR results
   *
   * @param pidcVersID PID Card
   * @return true/false
   */
  public boolean hasCDRResults(final PIDCVersion pidcVersID) {
    return !this.dataCache.getCDRWorkPackages(pidcVersID).isEmpty();
  }


  /**
   * Returns whether the a2lFile has CDR results
   *
   * @param pidcA2l pidc a2l instance
   * @return true/false
   */
  public boolean hasCDRResults(final PIDCA2l pidcA2l) {
    return !this.dataLoader.getCDRResults(pidcA2l).isEmpty();
  }

  /**
   * Get CDR Result Parameter
   *
   * @param entityID id
   * @return CDRObject
   */
  public CDRResultParameter getCDRResParameter(final Long entityID) {
    return this.dataCache.getCDRResultParameter(entityID);
  }

  /**
   * Returns the Review Results for a given parameter ID.
   *
   * @param paramId the ID of the parameter
   * @return a set of Review Results
   */
  public SortedSet<CDRResultParameter> getCDRParamResults(final long paramId) {
    return this.dataLoader.getCDRParamResults(paramId);
  }

  /**
   * Get Work Packages reviewed for the PIDC version
   *
   * @param pidcVer pidCard version
   * @return work packages as sorted set of string array with size '3' Items are sorted based on the WP names. Array
   *         elements are as follows : item [0] - "WP"; item [1] - Pidc ID; item [2] - Name of work package
   */
  public SortedSet<String[]> getCDRWorkPackages(final PIDCVersion pidcVer) {
    final SortedSet<String[]> retSet = new TreeSet<String[]>(new Comparator<String[]>() {

      @Override
      public int compare(final String[] item1, final String[] item2) {
        return ApicUtil.compare(item1[2], item2[2]);
      }
    });

    String[] item;
    for (String wpName : this.dataCache.getCDRWorkPackages(pidcVer).keySet()) {
      item = new String[ApicConstants.STRING_ARR_INDEX_THREE]; // NOPMD Instantiation is required here
      item[ApicConstants.STRING_ARR_INDEX_ZERO] = CDRConstants.NODE_TYPE_WRK_PKG;
      item[ApicConstants.STRING_ARR_INDEX_ONE] = String.valueOf(pidcVer.getID());
      item[ApicConstants.STRING_ARR_INDEX_TWO] = wpName;

      retSet.add(item);
    }

    return retSet;
  }


  /**
   * Returns the workpages for a variant in PIDC
   *
   * @param pidcVer selc pidc version
   * @param pidcVar selc pidc variant
   * @return set of wp's
   */
  public SortedSet<String[]> getCDRWorkPackages(final PIDCVersion pidcVer, final PIDCVariant pidcVar) {
    Map<Long, Map<String, SortedSet<CDRReviewVariant>>> workPackages = this.dataCache.getWorkPackages(pidcVer, pidcVar);
    String[] item;
    SortedSet<String[]> retSet = new TreeSet<String[]>(new Comparator<String[]>() {

      @Override
      public int compare(final String[] item1, final String[] item2) {
        return ApicUtil.compare(item1[ApicConstants.COLUMN_INDEX_2], item2[ApicConstants.COLUMN_INDEX_2]);
      }
    });
    if (null == pidcVar) {
      Map<Long, Map<String, SortedSet<CDRResult>>> workPkgWithoutVar =
          this.dataCache.getWorkPkgWithoutVar(pidcVer, pidcVar);
      Map<String, SortedSet<CDRResult>> wpSet = workPkgWithoutVar.get(CDRConstants.NO_VARIANT_NODE_ID);
      if (CommonUtils.isNotEmpty(wpSet)) {
        for (String wpName : wpSet.keySet()) {
          item = new String[TREENODE_ARRAY_SIZE]; // NOPMD Instantiation is required here
          item[ApicConstants.COLUMN_INDEX_0] = CDRConstants.NODE_TYPE_WRK_PKG;
          item[ApicConstants.COLUMN_INDEX_1] = String.valueOf(pidcVer.getID());
          item[ApicConstants.COLUMN_INDEX_2] = wpName;

          retSet.add(item);
        }
      }
    }
    else {
      Map<String, SortedSet<CDRReviewVariant>> wpSet = workPackages.get(pidcVar.getVariantID());
      if (CommonUtils.isNotEmpty(wpSet)) {
        for (String wpName : wpSet.keySet()) {
          item = new String[TREENODE_ARRAY_SIZE]; // NOPMD Instantiation is required here
          item[ApicConstants.COLUMN_INDEX_0] = CDRConstants.NODE_TYPE_WRK_PKG;
          item[ApicConstants.COLUMN_INDEX_1] = String.valueOf(pidcVar.getID());
          item[ApicConstants.COLUMN_INDEX_2] = wpName;

          retSet.add(item);
        }
      }
    }
    return retSet;
  }

  /**
   * Gets the cdr work packages
   *
   * @param pidcVariant
   * @param pidcVer
   * @param reload
   * @return
   */
  // ICDM-1998
  public Map<String, SortedSet<CDRReviewVariant>> getCDRWorkPackages(final PIDCVariant pidcVariant,
      final PIDCVersion pidcVer, final boolean reload) {
    return this.dataCache.getCDRWorkPackages(pidcVariant, pidcVer, false);
  }

  /**
   * @param pidcVer pidcVer
   * @param pidcVariant pidcVariant
   * @return the results without varaint
   */
  public Map<Long, Map<String, SortedSet<CDRResult>>> getWorkPkgWithoutVar(final PIDCVersion pidcVer,
      final PIDCVariant pidcVariant) {
    return this.dataCache.getWorkPkgWithoutVar(pidcVer, pidcVariant);

  }

  /**
   * Get variants reviewed for the PIDC version
   *
   * @param pidcVer pidCard version
   * @return work packages as sorted set of string array with size '3' Items are sorted based on the WP names. Array
   *         elements are as follows : item [0] - "Variants"; item [1] - Pidc ID; item [2] - Name of variant
   */
  public SortedSet<String[]> getCDRVariants(final PIDCVersion pidcVer) {
    final SortedSet<String[]> retSet = new TreeSet<String[]>(new Comparator<String[]>() {

      @Override
      public int compare(final String[] item1, final String[] item2) {
        return ApicUtil.compare(item1[ApicConstants.COLUMN_INDEX_2], item2[ApicConstants.COLUMN_INDEX_2]);
      }
    });

    String[] item;
    if (pidcVer.hasVariants(false)) {
      for (PIDCVariant variant : getVariantsWithResults(pidcVer)) {
        item = new String[TREENODE_ARRAY_SIZE]; // NOPMD Instantiation is required here
        item[ApicConstants.COLUMN_INDEX_0] = CDRConstants.NODE_TYPE_VARIANTS;
        item[ApicConstants.COLUMN_INDEX_1] = String.valueOf(variant.getID());
        item[ApicConstants.COLUMN_INDEX_2] = variant.getName();
        retSet.add(item);
      }
      if (!getResultsWithoutVar(pidcVer).isEmpty()) {
        item = new String[TREENODE_ARRAY_SIZE]; // NOPMD Instantiation is required here
        item[ApicConstants.COLUMN_INDEX_0] = CDRConstants.NODE_TYPE_VARIANTS;
        item[ApicConstants.COLUMN_INDEX_1] = String.valueOf(pidcVer.getID());
        item[ApicConstants.COLUMN_INDEX_2] = NO_VARIANT;
        retSet.add(item);
      }
    }
    else {
      item = new String[TREENODE_ARRAY_SIZE]; // NOPMD Instantiation is required here
      item[ApicConstants.COLUMN_INDEX_0] = CDRConstants.NODE_TYPE_VARIANTS;
      item[ApicConstants.COLUMN_INDEX_1] = String.valueOf(pidcVer.getID());
      item[ApicConstants.COLUMN_INDEX_2] = NO_VARIANT;

      retSet.add(item);
    }
    return retSet;
  }

  /**
   * Get questionnaire variants reviewed for the PIDC version
   *
   * @param pidcVers pidCard version
   * @param refresh if true, retrieves the data from DB again
   * @return variant nodes as sorted set of string array with size '3' Items are sorted based on the names. Array
   *         elements are as follows : item [0] - "Q_Variants"; item [1] - Variant/Version ID; item [2] - Name of
   *         variant/'No Variant'
   */
  public SortedSet<String[]> getQNaireVariants(final PIDCVersion pidcVers, final boolean refresh) {
    return getDataLoader().getQNaireVariants(pidcVers, refresh);
  }

  /**
   * Get the variants which has results
   *
   * @param pidcVer pidc version
   * @return set of variants
   */
  public SortedSet<PIDCVariant> getVariantsWithResults(final PIDCVersion pidcVer) {
    SortedSet<PIDCVariant> variantSet = new TreeSet<PIDCVariant>();
    for (CDRResult cdrResult : this.dataLoader.getCDRResults(pidcVer)) {
      if (null != cdrResult.getReviewVarMap()) {
        for (CDRReviewVariant rvwVar : cdrResult.getReviewVarMap().values()) {
          variantSet.add(rvwVar.getVariant());
        }
      }
    }
    return variantSet;
  }

  /**
   * This is a new class which has a {@link QuestionnaireVersion} mapped with {@link Question}s and
   * {@link ReviewQnaireAnswer}s
   *
   * @param pidcVer the given pidc version
   * @param pidcVarName Variant name
   * @return SortedSet, the set of QuestionnaireVersion
   */
  // iCDM-1982
  public SortedSet<QuestionnaireResponse> getQuestionnaireVersionWithResults(final PIDCVersion pidcVer,
      final String pidcVarName) {
    // ICDM-2404
    SortedSet<QuestionnaireResponse> qnaireRespSet = new TreeSet<>();
    AbstractProjectObject<?> projObj = null;
    if (CommonUtils.isEqual(pidcVarName, NO_VARIANT)) {
      projObj = pidcVer;
    }
    else {
      for (PIDCVariant variant : pidcVer.getVariantsSet(false)) {
        if (CommonUtils.isEqual(pidcVarName, variant.getName())) {
          projObj = variant;
          break;
        }
      }
    }
    if (projObj != null) {
      qnaireRespSet.addAll(getDataCache().getProjQnaireResponseMap(projObj).values());
    }
    return qnaireRespSet;
  }

  /**
   * Get questionnaire responses added to this project object
   *
   * @param projObj project object - PIDC Version or Variant
   * @return Map of responses. Key - 'Questionnaire' ID, Value - QuestionnaireResponse
   */
  // ICDM-2404
  public Map<Long, QuestionnaireResponse> getProjQnaireResponseMap(final AbstractProjectObject<?> projObj) {

    return getDataCache().getProjQnaireResponseMap(projObj);
  }

  /**
   * Get the results for the selc variants
   *
   * @param varName
   * @param pidcVer
   * @return
   */
  public SortedSet<CDRResult> getResultsForVar(final String varName, final PIDCVersion pidcVer) {
    SortedSet<CDRResult> resultSet = new TreeSet<CDRResult>();
    for (CDRResult cdrResult : this.dataLoader.getCDRResults(pidcVer)) {
      if ((null != cdrResult.getVariant()) && (cdrResult.getVariant().getName().equalsIgnoreCase(varName))) {
        resultSet.add(cdrResult);
      }
    }
    return resultSet;
  }

  /**
   * @param varName varName
   * @param pidcVer pidcVer
   * @return the rvw var set
   */
  public SortedSet<CDRReviewVariant> getReviewVarForVar(final String varName, final PIDCVersion pidcVer) {
    SortedSet<CDRReviewVariant> rvwVarSet = new TreeSet<CDRReviewVariant>();
    for (CDRReviewVariant rvwVar : this.dataLoader.getCDRReviewVar(pidcVer)) {
      if (rvwVar.getVariant().getName().equals(varName)) {
        rvwVarSet.add(rvwVar);
      }
    }

    return rvwVarSet;
  }

  /**
   * Get CDRResults reviewed for the WPName and PIDC
   *
   * @param pidcVer PIDCard version
   * @param WPName wpName
   * @return CDRResult set
   */
  public SortedSet<CDRReviewVariant> getCDRResults(final PIDCVersion pidcVer, final String WPName) {
    return this.dataCache.getAllResults(pidcVer, WPName);
  }

  /**
   * Get CDRResults reviewed for the WPName and PIDC
   *
   * @param pidcVer PIDCard version
   * @param wpName wpName
   * @return CDRResult set
   */
  public SortedSet<CDRReviewVariant> getResultsForVarWp(final PIDCVersion pidcVer, final PIDCVariant pidcVaraint,
      final String wpName) {
    return this.dataCache.getResultsForVarWP(pidcVer, pidcVaraint, wpName);
  }


  /**
   * @param entityID Cdr Review Attr Val Id
   * @return the Cdr Review Attr Value
   */
  public AbstractCdrObject getCdrRvwAttrVal(final Long entityID) {

    return this.dataCache.getAllReviewAttrVal().get(entityID);
  }


  /**
   * @param entityID Cdr Review Attr Val Id
   * @return the Cdr Review Attr Value
   */
  public AbstractCdrObject getCdrRvwVar(final Long entityID) {
    return this.dataCache.getAllReviewVariants().get(entityID);
  }

  /**
   * // iCDM-1366 Get all Project specific rule sets
   *
   * @param includeDeleted true if the deleted rule sets need to be included
   * @return sorted set of rule sets
   */
  public SortedSet<RuleSet> getAllRuleSets(final boolean includeDeleted) {
    return this.dataCache.getAllRuleSets(includeDeleted);
  }

  /**
   * Get the RuleSEt
   *
   * @param rsId entity id
   * @return RuleSet
   */
  public RuleSet getRuleSet(final Long rsId) {
    return this.dataCache.getRuleSet(rsId);
  }

  /**
   * @param rsetParamId primaryKey
   * @return RuleSetParameter
   */
  public RuleSetParameter getRuleSetParam(final long rsetParamId) {
    return this.dataCache.getRuleSetParam(rsetParamId);
  }


  /**
   * Get CDRFunction obj for the function Id
   *
   * @param funcId function Id
   * @return CDRFunction if obj available in cache, else NULL
   */
  protected CDRFunction getCDRFunction(final Long funcId) {
    return this.dataCache.getCDRFunction(funcId);
  }

  /**
   * Get the CDRFuncParameter for the param name and type, seperated by delimeter as ':'
   *
   * @param paramName parameter name
   * @param paramType parameter type
   * @return CDRFuncParameter
   */
  public CDRFuncParameter getCDRFuncParameter(final String paramName, final String paramType) {
    return this.dataCache.getCDRFuncParameter(paramName, paramType);
  }


  /**
   * Get Parameter (CDRFuncParameter, RuleSetParameter...)
   *
   * @param parent Param collection (CDRFunction, RuleSet..)
   * @param rule cdrRule
   * @return IParameter
   * @throws SsdInterfaceException
   */
  public IParameter<?> getParameter(final IParameterCollection parent, final CDRRule rule)
      throws SsdInterfaceException {
    return parent.getParameter(rule.getParameterName(), rule.getValueType());
  }

  /**
   * Get CDRFunctions which can be modified for the current user
   *
   * @return CDRFunction set
   */
  // ICDM-501
  public SortedSet<CDRFunction> getAllModifiableCDRFunctions() {
    final Set<Long> funcIdList =
        getApicDataProvider().getWritableNodesByType(CDREntityType.CDR_FUNCTION.getEntityTypeString());
    // fetch functions for the func id's with access rights for the current user
    return this.dataLoader.fetchCDRFunctions(funcIdList);
  }

  /* iCDM-471 */
  /**
   * Get CDRFunction obj for the function name. Initial method call to retrive the CDR Function details to open in CDR
   * Function param editor
   *
   * @param funcName function name
   * @return CDRFunction obj
   */
  public CDRFunction getCDRFunction(final String funcName) {
    return this.dataCache.getCDRFunction(funcName);
  }

  /* iCDM-471 */
  /**
   * Get CDRFunction obj for the function name. Initial method call to retrive the CDR Function details to open in CDR
   * Function param editor
   *
   * @param funcName function name
   * @return CDRFunction obj
   */
  public List<CDRFunction> getCDRFunList(final String funcName) {
    return this.dataCache.getCDRFunList(funcName);
  }

  /**
   * ICDM-1324 Required for CDR-Rule Importer (Plausibel-Importer)
   *
   * @return the Function name Set
   * @throws SsdInterfaceException
   */
  public Map<String, CDRFuncParameter> getCDRFuncParameters(final String paramName) throws SsdInterfaceException {
    return this.dataLoader.fetchCDRParameters(paramName);
  }

  /**
   * Icdm-801 Performance improvement using a single Query for fetching function names Get the CDR functions for a Set
   * of functions in the UI
   *
   * @param selFuns selFuns
   * @return the Function name Set
   */
  public Set<String> getCDRFunctionNameSet(final SortedSet<String> selFuns) {
    return this.dataCache.getCDRFunctionNameSet(selFuns);
  }

  /**
   * Get CDRfunctions starting with the search string
   *
   * @param startsWith Function Name starting with
   * @return CDRFunction set
   */
  // ICDM-501
  public SortedSet<CDRFunction> searchCDRFunctions(final String startsWith) {
    return this.dataLoader.searchCDRFunctions(startsWith);
  }

  /**
   * Fetch the DB func PARAMETER of Calibration data review
   *
   * @param paramName parameter name
   * @param paramType parameter type
   * @return obj
   */
  // iCDM-471
  public AbstractCdrObject getFunctionParameter(final String paramName, final String paramType) {
    return getCDRFuncParameter(paramName, paramType);
  }

  /**
   * Return the CDR Parameter for the paramID
   *
   * @param paramId dbParameterId
   * @return CDRFuncParameter
   */
  // iCDM-519
  public CDRFuncParameter getCDRFuncParameter(final long paramId) {
    // prepare param key to get from cache
    final String paramName = getEntityProvider().getDbFunctionParameter(paramId).getName();
    final String paramType = getEntityProvider().getDbFunctionParameter(paramId).getPtype();
    // check if available in cache
    CDRFuncParameter cdrFuncParam = this.dataCache.getCDRFuncParameter(paramName, paramType);
    // if not create and also add to cache
    if (cdrFuncParam == null) {
      cdrFuncParam = new CDRFuncParameter(this, paramId);
      this.dataCache.addCDRFuncParameter(cdrFuncParam);
    }
    return cdrFuncParam;
  }

  /**
   * Fetches the attr dependecies for a parameter
   *
   * @param paramSet parm set
   * @return map
   */
  public Map<Long, Set<ParameterAttribute>> fetchParameterAttrDepn(final Set<CDRFuncParameter> paramSet) {
    return this.dataLoader.fetchParameterAttrDepn(paramSet);
  }

  /**
   * Fetches the attr dependecies for a parameter // ICDM-1584
   *
   * @param paramSet parm set
   * @param functionName FunctionName
   * @return map
   */
  public Map<Long, Set<ParameterAttribute>> fetchParameterAttrDepn(final Set<CDRFuncParameter> paramSet,
      final String functionName, final String funcVer) {
    return this.dataLoader.fetchParameterAttrDepn(paramSet, functionName, funcVer);
  }


  /**
   * @param paramAttrID paramAttrID
   * @return the ParamAttrObject from the map using the key as ParamAttrID
   */
  public ParameterAttribute getParamAttr(final Long paramAttrID) {
    return this.dataCache.getParamAttrMap().get(paramAttrID);
  }

  /**
   * @param param CDRFuncParameter or RuleSetParameter
   * @return the paramDependencyMap
   */
  public Set getParamDependencyAttrs(final IParameter<?> param) {
    if (param instanceof RuleSetParameter) {
      return this.dataCache.getRuleSetParamDependencyMap().get(param.getID());
    }
    return this.dataCache.getParamDependencyMap().get(param.getID());
  }

  /**
   * Returns the parameter identified by name
   *
   * @param name parameter name
   * @return the parameter value
   */
  public final List<TParameter> getParameter(final String name) {
    return this.dataLoader.getParameter(name);
  }

  /**
   * Returns the parameter identified by name
   *
   * @param paramId parameter name
   * @return List of TParameter entity
   */
  public final List<TParameter> getParameter(final Long paramId) {
    return this.dataLoader.getParameter(paramId);
  }

  /**
   * Icdm-801 Performance optimization using single Query for fetching Param names
   *
   * @param labelList labelList
   * @return the Label name from T Parameter table
   */
  public Set<String> getLabelNameSet(final List<String> labelList) {
    return this.dataCache.getLabelNameSet(labelList);
  }

  /**
   * Icdm-1315- new Method to get all Var coded Param Id's
   *
   * @param firstName base param name first name
   * @param lastName base param name last name
   * @return the list of variant coded parameters
   */
  public List<Long> fetchAllVarCodedParam(final String firstName, final String lastName) {
    return this.dataLoader.fetchAllVarCodedParam(firstName, lastName);

  }

  /**
   * @param ruleAttrId rsParamAttrID
   * @return RuleSetParameterAttribute
   */
  public RuleSetParameterAttribute getRuleSetParamAttr(final long ruleAttrId) {
    return this.dataCache.getRuleSetParamAttrMap().get(ruleAttrId);
  }

  /**
   * @param paramName paramName
   * @return CDRFunction
   */
  public List<CDRFunction> fetchFunctions(final String paramName) {
    return this.dataLoader.fetchFunctions(paramName);

  }

  /**
   * @param paramName aramName
   * @return the CDRFuncParameter object for the Parameter
   */
  public CDRFuncParameter fetchCDRFuncParam(final String paramName) {
    // Icdm-1379- show the rules editor
    return this.dataLoader.fetchCdrFuncParam(paramName);
  }

  /**
   * ICDM-1380
   *
   * @param funcNameStr String function name
   * @param paramNameStr String parameter name
   * @param ruleSetId Long Rule set Id
   * @return List<Map<CDRFuncParameter, CDRFunction>> List of map with key as parameter and value as function
   */
  public List<List<Object>> searchParameters(final String paramNameStr, final String funcNameStr,
      final Long ruleSetId) {
    return this.dataLoader.searchParameters(paramNameStr, funcNameStr, ruleSetId);

  }

  /**
   * @param baseParamSet baseParamSet
   * @return set of parmeter names retrieved
   */
  public Set<String> fetchParamsWithDepen(final Set<String> baseParamSet) {
    // Icdm-1255 - Review for varaint coded
    return this.dataLoader.fetchParamsWithDep(baseParamSet);
  }

  /**
   * @param paramNameList list of parameter names
   * @param sortWithID if true, sorted only using ID, otherwise uses default sorting for parameters
   * @return sorted set of the func param objects for the names
   */
  public SortedSet<CDRFuncParameter> fetchFuncParams(final List<String> paramNameList, final boolean sortWithID) {
    return this.dataLoader.fetchFuncParams(paramNameList, sortWithID);
  }

  // ICDM-1476
  /**
   * Fetch CDRFuncParameter objects with the parameter names. The objects are also added to the cache
   *
   * @param paramNameList list of parameter names
   * @return Set of CDRFuncParameter
   */
  public Set<CDRFuncParameter> fetchFuncParams(final Collection<String> paramNameList) {
    return this.dataLoader.fetchFuncParams(paramNameList);
  }

  /**
   * New method to get the Func param map from Char collection
   *
   * @param paramNameList paramNameList
   * @return the map of Func param with name as Key
   */
  public Map<String, CDRFuncParameter> fetchFuncParamMap(final Collection<Characteristic> paramNameList) {
    return this.dataLoader.fetchFuncParamMap(paramNameList);
  }

  /**
   * @param pidcVer sel pidc ver
   * @return returns results reviewed without variant
   */
  public SortedSet<CDRResult> getResultsWithoutVar(final PIDCVersion pidcVer) {
    SortedSet<CDRResult> resultWithoutVarSet = new TreeSet<CDRResult>();
    for (CDRResult cdrResult : this.dataLoader.getCDRResults(pidcVer)) {
      if (null == cdrResult.getVariant()) {
        resultWithoutVarSet.add(cdrResult);
      }
    }
    return resultWithoutVarSet;
  }

  /**
   * @param pidcVer sel pidc ver
   * @return returns results reviewed without variant
   */
  // iCDM-1982
  public SortedSet<CDRResult> getQNaireRvwResultsWithoutVariant(final PIDCVersion pidcVer) {
//    SortedSet<CDRResult> resultWithoutVarSet = new TreeSet<CDRResult>();
//    for (CDRResult cdrResult : this.dataLoader.getCDRResults(pidcVer)) {
//      if (null == cdrResult.getVariant()) {
//        if (!cdrResult.getDispReviewQNaires().isEmpty()) {
//          resultWithoutVarSet.add(cdrResult);
//        }
//      }
//    }
//    return resultWithoutVarSet;
    return new TreeSet<>();
  }


  /**
   * @param paramMap paramMap
   * @throws SsdInterfaceException
   */
  public void fetchRules(final Map<String, CDRFuncParameter> paramMap) throws SsdInterfaceException {
    List<String> paramNames = new ArrayList<>();

    for (CDRFuncParameter params : paramMap.values()) {
      paramNames.add(params.getName());
    }
    Map<String, List<CDRRule>> rulesMap = this.apicDataProvider.getSsdServiceHandler().readReviewRule(paramNames);
    if (!rulesMap.isEmpty()) {
      this.apicDataProvider.addParamCDRRules(rulesMap);
    }

  }

  /**
   * @return the pidcRvwResMap
   */
  public Map<String, SortedSet<CDRResult>> getPidcRvwResMapForVersion(final PIDCVersion pidcVersion) {
    return this.dataCache.getPidcRvwResMapForVersion(pidcVersion);
  }

  /**
   * @param pidcVersion PIDCVersion
   * @return map with var id as key, and another map with wp name and set of cdr results as value
   */
  public Map<Long, Map<String, SortedSet<CDRResult>>> getPIDCVarResults(final PIDCVersion pidcVersion) {
    return this.dataCache.getPidcResMap().get(pidcVersion.getID());
  }

  /**
   * @param wpDivID wpDivID
   * @return workpackage divsion
   */
  public WorkPackageDivision getWorkPackageDivision(final Long wpDivID) {
    return this.dataCache.getWorkPackageDiv(wpDivID);
  }

  /**
   * @param wrkPkgID wrkPkgID
   * @return icdm workpackage
   */
  public IcdmWorkPackage getIcdmWorkPackage(final Long wrkPkgID) {
    return this.dataCache.getIcdmWorkPackage(wrkPkgID);
  }

  /**
   * @return all workpackages in icdm
   */
  public ConcurrentMap<Long, IcdmWorkPackage> getIcdmWorkPackageMap() {
    return this.dataCache.getIcdmWorkPackageMap();
  }

  // ICDM-2646
  /**
   * @param division (DGS/BEG/DS)
   * @return all workpackages in icdm
   */
  public ConcurrentMap<Long, IcdmWorkPackage> getIcdmWorkPackageMapForDiv(final AttributeValue division) {
    return this.dataCache.getIcdmWorkPackageMapForDiv(division);
  }

  /**
   * @return workpackages mapped to division
   */
  public ConcurrentMap<Long, WorkPackageDivision> getWrkPkgDivisionMap() {
    return this.dataCache.getWrkPkgDivisionMap();
  }

  /**
   * @return wp list belonging to each division
   */
  public ConcurrentMap<AttributeValue, List<IcdmWorkPackage>> getMappedWrkPkgMap() {
    return this.dataCache.getMappedWrkPkgMap();
  }


  /**
   * ICDM-2005
   *
   * @param qNaireID ID
   * @return Questionnaire business object
   */
  public Questionnaire getQuestionnaire(final Long qNaireID) {
    return this.dataCache.getQuestionnaire(qNaireID);
  }

  /**
   * ICDM-2005
   *
   * @param qNaireVersID ID
   * @return QuestionnaireVersion business object
   */
  public QuestionnaireVersion getQuestionnaireVersion(final Long qNaireVersID) {
    return this.dataCache.getQuestionnaireVersion(qNaireVersID);
  }

  /**
   * ICDM-2005
   *
   * @param questionID ID
   * @return QuestionnaireVersion business object
   */
  public Question getQuestion(final Long questionID) {
    return this.dataCache.getQuestion(questionID);
  }

  /**
   * ICDM-2005
   *
   * @param qConfigID ID
   * @return QuestionnaireVersion business object
   */
  public QuestionConfig getQuestionConfig(final Long qConfigID) {
    return this.dataCache.getQuestionConfig(qConfigID);
  }

  /**
   * ICDM-2005
   *
   * @param qDepenAttrID ID
   * @return QuestionDepenAttr business object
   */
  public QuestionDepenAttr getQuestionDepenAttribute(final Long qDepenAttrID) {
    return this.dataCache.getQuestionDepenAttribute(qDepenAttrID);
  }

  /**
   * ICDM-2005
   *
   * @param qDepenAttrValID ID
   * @return QuestionDepenAttrValue business object
   */
  public QuestionDepenAttrValue getQuestionDepenAttrValue(final Long qDepenAttrValID) {
    return this.dataCache.getQuestionDepenAttrValue(qDepenAttrValID);
  }


  /**
   * @param includeDeleted includeDeleted
   * @return all the Modifibale Questionare fo the user.
   */
  public SortedSet<Questionnaire> getAllQuestionares(final boolean includeDeleted) {
    return this.dataCache.getAllQuestionares(includeDeleted);
  }

  /**
   * @return the questionnaireMap
   */
  public ConcurrentMap<Long, Questionnaire> getQuestionnaireMap() {
    return this.dataCache.getQuestionnaireMap();
  }


  /**
   * ICDM-2039
   *
   * @param revQnaireAnsID ID
   * @return ReviewQnaireAnswer business object
   */
  public ReviewQnaireAnswer getReviewQnaireAnswer(final Long revQnaireAnsID) {
    return this.dataCache.getReviewQnaireAnswer(revQnaireAnsID);
  }

  /**
   * ICDM-2190
   *
   * @param revQnaireAnsID ID
   * @return ReviewQnaireAnswer business object
   */
  public QnaireAnsOpenPoint getQnaireAnsOpenPoints(final Long revQnaireAnsID) {
    return this.dataCache.getQnaireAnsOpenPoint(revQnaireAnsID);
  }

  /**
   * @param pidcVariant
   * @param pidcVer
   * @return
   */
  public SortedSet<CDRResult> getMappedResultVar(final PIDCVariant pidcVariant, final PIDCVersion pidcVer) {
    SortedSet<CDRResult> resultSet = new TreeSet<CDRResult>();
    for (CDRResult cdrResult : this.dataLoader.getCDRResults(pidcVer)) {
      for (CDRReviewVariant cdrRvwVar : cdrResult.getReviewVarMap().values()) {
        if (cdrRvwVar.getVariant().getName().equals(pidcVariant.getName())) {
          resultSet.add(cdrResult);
        }
      }
    }
    return resultSet;
  }

  /**
   * @param pidcVariant
   * @param pidcVer
   * @return
   */
  // ICDM-2434
  public SortedSet<CDRReviewVariant> getMappedResultCdrRvwVariant(final PIDCVariant pidcVariant,
      final PIDCVersion pidcVer) {
    SortedSet<CDRReviewVariant> resultSet = new TreeSet<CDRReviewVariant>();
    for (CDRResult cdrResult : this.dataLoader.getCDRResults(pidcVer)) {
      for (CDRReviewVariant cdrRvwVar : cdrResult.getReviewVarMap().values()) {
        if (cdrRvwVar.getVariant().getName().equals(pidcVariant.getName())) {
          resultSet.add(cdrRvwVar);
        }
      }
    }
    return resultSet;
  }


  /**
   * Checks whether the logged in user has privileges to export CDFx files from Data review report, review result etc.
   *
   * @param pidcVers PIDC Version
   * @return true if usre has privilges
   */
  public boolean canExportCDFx(final PIDCVersion pidcVers) {
    boolean status = false;
    if (this.apicDataProvider.getCurrentUser().hasApicWriteAccess()) {
      status = true;
    }
    else if (pidcVers != null) {
      NodeAccessRight curUserAccRight = pidcVers.getPidc().getCurrentUserAccessRights();
      if ((curUserAccRight != null) && (curUserAccRight.hasReadAccess() || curUserAccRight.isOwner())) {
        status = true;
      }
    }
    return status;
  }

  /**
   * @param respID response ID
   * @return QuestionnaireResponse
   */
  // ICDM-2404
  public QuestionnaireResponse getQuestionnaireResponse(final Long respID) {
    return this.dataCache.getQuestionnaireResponse(respID);
  }

  /**
   * @param wpDivsWithQs
   * @param qsWithoutActiveVersions
   */
  public SortedSet<QuestionnaireVersion> getQuestionnaireVersions(final List<Long> wpDivsWithQs,
      final SortedSet<Questionnaire> qsWithoutActiveVersions) {
    return this.dataLoader.getQuestionnaireVersions(wpDivsWithQs, qsWithoutActiveVersions);

  }

  /**
   * @param divValId
   * @param inputWps
   */
  public Map<IcdmWorkPackage, String> getWorkPkgResp(final Long divValId, final SortedSet<IcdmWorkPackage> inputWps) {
    return this.dataLoader.getWorkRespMap(divValId, inputWps);

  }
}