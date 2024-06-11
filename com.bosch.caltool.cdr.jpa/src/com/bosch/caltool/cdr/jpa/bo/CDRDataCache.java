/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo; // NOPMD by bne4cob on 10/2/13 10:12 AM

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.apic.jpa.bo.AbstractProjectObject;
import com.bosch.caltool.apic.jpa.bo.AttributeValue;
import com.bosch.caltool.apic.jpa.bo.PIDCVariant;
import com.bosch.caltool.apic.jpa.bo.PIDCVersion;
import com.bosch.caltool.cdr.jpa.bo.review.ReviewRuleSetData;
import com.bosch.caltool.dmframework.bo.AbstractDataCache;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.SsdInterfaceException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;


/**
 * Class to store all data objects related to CDR
 *
 * @author BNE4COB
 */
class CDRDataCache extends AbstractDataCache {

  /**
   * Key for General Questionnaire ID in Common Parameters table
   */
  private static final String KEY_GENERAL_QNAIRE_ID = "GENERAL_QNAIRE_ID";

  /**
   * Lock for Questionnaire
   */
  final Object qNaireSyncLock = new Object();

  /**
   * Defines Map of CDR Result
   */
  private ConcurrentMap<Long, CDRResult> cdrResultMap;


  /**
   * Defines Map of CDR Result
   */
  private ConcurrentMap<Long, CDRSecondaryResult> cdrResSecMap;

  /**
   * Map of Review results against PID Cards
   */
  private ConcurrentMap<Long, Map<String, SortedSet<CDRResult>>> pidcRvwResMap;
  /**
   * Map of Review results against PIDC Variants
   */
  private ConcurrentMap<Long, Map<String, SortedSet<CDRReviewVariant>>> varRvwResMap;

  private Map<Long, Map<Long, Map<String, SortedSet<CDRResult>>>> pidcResMap;

  private Map<Long, Map<Long, Map<String, SortedSet<CDRReviewVariant>>>> pidcResMapWithVar;

  /**
   * Defines Map of CDR Function map
   */
  private ConcurrentMap<Long, CDRResultFunction> cdrResultFuncMap;

  /**
   * Defines Map of CDR Result parameters
   */
  private ConcurrentMap<Long, CDRResultParameter> cdrResultParamMap;

  /**
   * The CDR data provider
   */
  private final CDRDataProvider dataProvider;

  /**
   * Defines Map of CDR Participants
   */
  private ConcurrentMap<Long, CDRParticipant> cdrParticipantMap;

  // iCDM-471
  /**
   * Defines Map of FunctionName(key) and CDFFunction(value)
   */
  private ConcurrentMap<String, CDRFunction> cdrFunctionsMap;

  /**
   * Defines Map of ParamName+Type(key) and CDRFuncParameter(value)
   */
  private ConcurrentMap<String, CDRFuncParameter> cdrFuncParamsMap;

  // ICdm-1032
  /**
   * map for ParameterAttribute
   */
  private ConcurrentMap<Long, ParameterAttribute> paramAttrMap;

  /**
   * Map of parameter dependencies
   */
  private ConcurrentMap<Long, Set<ParameterAttribute>> paramDependencyMap;

  /**
   * Defines Map of CDR Review Attr Values
   */
  // Icdm-1214
  private ConcurrentMap<Long, CDRReviewAttrValue> rvwValueMap;

  // iCDM-1366
  /**
   * All rule sets
   */
  private ConcurrentMap<Long, RuleSet> allRuleSets;
  /**
   * All Rule set params
   */
  private ConcurrentMap<Long, RuleSetParameter> allRuleSetParams;
  /**
   * Rule set param attributes
   */
  private ConcurrentMap<Long, RuleSetParameterAttribute> rsParamAttrMap;
  /**
   * Map of Rule set parameter dependencies
   */
  private ConcurrentMap<Long, Set<RuleSetParameterAttribute>> ruleSetParamDepenMap;

  private final ConcurrentMap<Long, Questionnaire> questionnaireMap = new ConcurrentHashMap<>();

  private final ConcurrentMap<Long, QuestionnaireVersion> allqnaireVersMap = new ConcurrentHashMap<>();

  private final ConcurrentMap<Long, IcdmWorkPackage> icdmWorkPackageMap = new ConcurrentHashMap<>();

  private final ConcurrentMap<Long, WorkPackageDivision> wrkPkgDivisionMap = new ConcurrentHashMap<>();
  /**
   * Key - division name , value - mapped workpackages for that divison
   */
  private final ConcurrentMap<AttributeValue, List<IcdmWorkPackage>> mappedWrkPkgMap = new ConcurrentHashMap<>();

  /**
   * TODO: Check if collection is ok QuestionnaireVersionID -> QuestionID,Question
   */
  private final Map<Long, Question> questionMap = new ConcurrentHashMap<>();

  private final Map<Long, QuestionConfig> questionConfigMap = new ConcurrentHashMap<>();

  /**
   * TODO: Check if collection is ok QuestionnaireVersionID -> QuestionID -->QuestionDepAttrID,QuestionDepenAttr
   */
  private final Map<Long, QuestionDepenAttr> questionDepenAttrMap = new ConcurrentHashMap<>();

  /**
   * TODO: Check if collection is ok QuestionnaireVersionID -> QuestionID -->QuestionDepAttrID -->
   * QuestionDepenAttrValID,QuestionDepenAttrValue
   */
  private final Map<Long, QuestionDepenAttrValue> questionDepenAttrValMap = new ConcurrentHashMap<>();

  // ICDM-2039

  private final Map<Long, ReviewQnaireAnswer> reviewQnaireAnsMap = new ConcurrentHashMap<>();

  // ICDM-2190
  /**
   * Key - qnaire ans open points obj id , value - qnaire ans open points obj
   */
  private final Map<Long, QnaireAnsOpenPoint> qnaireAnsOpenPointMap = new ConcurrentHashMap<>();


  /***
   * Flag to check if all questionnaire are loaded. It can happen that PID tree view Review Questionnaire node is loaded
   * before the FunctionSelection Dialog is opened. In this case only the Questionnaires loaded in the PID Tree View are
   * present in the questionnaireMap. This flag is used to prevent the above case.
   */
  private boolean isAllQnaireLoaded;

  /**
   * Map for variant map
   */
  private Map<Long, CDRReviewVariant> rvwVarMap;

  /**
   * Map that stores variant node details of each pidc version, with questionnaire responses
   * <p>
   * Key - PIDC Version ID <br>
   * Value - Sorted Set of String Array (Length 3), sorted by name <br>
   * arr[0] - CDRConstants.NODE_TYPE_QNAIRE_VARIANTS<br>
   * arr[1] - Variant ID, if variant is available, PIDC Version ID, if ApicConstants.DUMMY_VAR_NODE_NOVAR <br>
   * arr[2] - Variant Name, if variant available, ApicConstants.DUMMY_VAR_NODE_NOVAR if no variant available
   */
  // ICDM-2332
  private final ConcurrentMap<Long, SortedSet<String[]>> qnaireVariantDetMap = new ConcurrentHashMap<>();

  /**
   * Questionnaire responses
   * <p>
   * Key - primary key - response ID <br>
   * value - response BO
   */
  // ICDM-2404
  private final ConcurrentMap<Long, QuestionnaireResponse> qnaireResponseMap = new ConcurrentHashMap<>();


  /**
   * Map storing questionnaire responses attached to a pidc version/pidc variant(if available)
   * <p>
   * Key - PIDCVersion or PIDCVariant<br>
   * Value - Map of Questionnaire responses : Key - 'Questionnaire' ID, Value - Response object
   */
  // ICDM-2404
  private final ConcurrentMap<AbstractProjectObject<?>, ConcurrentMap<Long, QuestionnaireResponse>> projQnaireResMap =
      new ConcurrentHashMap<>();

  /**
   * Map storing general questionnaire versions attached to a pidc version/pidc variant(if available)
   * <p>
   * Key - PIDCVersion or PIDCVariant<br>
   * Value - General Questionnaire version
   */
  // ICDM-2404
  private final ConcurrentMap<AbstractProjectObject<?>, QuestionnaireVersion> projGenQnaireMap =
      new ConcurrentHashMap<>();

  /**
   * Genera Questionnaire, as configured in T_COMMON_PARAMS table
   */
  private Questionnaire genQuestionnaire;
  private boolean icdmWpLoaded;
  private boolean icdmWpDivsLoaded;

  private final Map<Long, CDRResParamSecondary> cdrResParamSecMap = new ConcurrentHashMap<>();

  private final Map<ReviewRuleSetData, CDRSecondaryResult> ruleDataMap = new ConcurrentHashMap<>();

  /**
   * Constructor
   *
   * @param dataProvider data provider
   */
  public CDRDataCache(final CDRDataProvider dataProvider) {
    super();
    this.dataProvider = dataProvider;
    initialize();
  }

  /**
   * Initialize objects
   */
  private void initialize() {
    this.cdrResultMap = new ConcurrentHashMap<Long, CDRResult>();
    this.cdrResSecMap = new ConcurrentHashMap<>();
    this.pidcRvwResMap = new ConcurrentHashMap<Long, Map<String, SortedSet<CDRResult>>>();
    this.varRvwResMap = new ConcurrentHashMap<Long, Map<String, SortedSet<CDRReviewVariant>>>();
    this.cdrResultFuncMap = new ConcurrentHashMap<Long, CDRResultFunction>();
    this.cdrResultParamMap = new ConcurrentHashMap<Long, CDRResultParameter>();
    this.cdrParticipantMap = new ConcurrentHashMap<Long, CDRParticipant>();
    this.allRuleSets = new ConcurrentHashMap<Long, RuleSet>();
    // iCDM-471
    this.cdrFunctionsMap = new ConcurrentHashMap<String, CDRFunction>();
    this.cdrFuncParamsMap = new ConcurrentHashMap<String, CDRFuncParameter>();

    this.paramAttrMap = new ConcurrentHashMap<Long, ParameterAttribute>();
    this.paramDependencyMap = new ConcurrentHashMap<Long, Set<ParameterAttribute>>();
    // Icdm-1214
    this.rvwValueMap = new ConcurrentHashMap<Long, CDRReviewAttrValue>();
    // iCDM-1366
    this.allRuleSetParams = new ConcurrentHashMap<Long, RuleSetParameter>();
    this.rsParamAttrMap = new ConcurrentHashMap<Long, RuleSetParameterAttribute>();
    this.ruleSetParamDepenMap = new ConcurrentHashMap<Long, Set<RuleSetParameterAttribute>>();

    this.pidcResMap = new HashMap<Long, Map<Long, Map<String, SortedSet<CDRResult>>>>();
    this.pidcResMapWithVar = new ConcurrentHashMap();
    this.rvwVarMap = new ConcurrentHashMap<Long, CDRReviewVariant>();
  }


  /**
   * @return the qnaireResponseMap
   */
  public ConcurrentMap<Long, QuestionnaireResponse> getQnaireResponseMap() {
    return this.qnaireResponseMap;
  }


  /**
   * Get data provider
   *
   * @return CDRDataProvider
   */
  protected final CDRDataProvider getDataProvider() {
    return this.dataProvider;
  }

  /**
   * Get the language
   *
   * @return Language
   */
  public final Language getLanguage() {
    return this.dataProvider.getApicDataProvider().getLanguage();
  }

  /**
   * Gets all CDR Results
   *
   * @return map of CDRResults
   */
  protected Map<Long, CDRResult> getAllCDRResults() {
    return this.cdrResultMap;
  }

  /**
   * Gets all CDR Results
   *
   * @return map of CDRResults
   */
  protected Map<Long, CDRSecondaryResult> getAllCDRSecResults() {
    return this.cdrResSecMap;
  }


  /**
   * Get Workpackage map for a pidc
   *
   * @param pidcVerID pidcard
   * @return Map of WPName(key) and SortedSet<CDRResult> (value)
   */
  protected Map<String, SortedSet<CDRResult>> getCDRWorkPackages(final PIDCVersion pidcVerID) {
    Map<String, SortedSet<CDRResult>> retMap = this.pidcRvwResMap.get(pidcVerID.getID());
    if (retMap == null) {
      final SortedSet<CDRResult> cdrResSet = this.dataProvider.getDataLoader().getCDRResults(pidcVerID);
      retMap = this.dataProvider.getDataLoader().resolveWPNames(cdrResSet);
      this.pidcRvwResMap.put(pidcVerID.getID(), retMap);
    }
    return retMap;
  }

  /**
   * Get Workpackage map for a pidc
   *
   * @param pidcVer pidcard
   * @param pidcVariant pidcVariant
   * @return Map of WPName(key) and SortedSet<CDRResult> (value)
   */
  protected Map<Long, Map<String, SortedSet<CDRReviewVariant>>> getWorkPackages(final PIDCVersion pidcVer,
      final PIDCVariant pidcVariant) {
    Map<Long, Map<String, SortedSet<CDRReviewVariant>>> retWithVarMap = this.pidcResMapWithVar.get(pidcVer.getID());
    // if the pidc version is not in the map.
    if ((null == retWithVarMap) || retWithVarMap.isEmpty()) {
      retWithVarMap = new HashMap<Long, Map<String, SortedSet<CDRReviewVariant>>>();
      loadWPForVar(pidcVer, retWithVarMap);
      this.pidcResMapWithVar.put(pidcVer.getID(), retWithVarMap);
    }
    return retWithVarMap;
  }

  /**
   * @param pidcVer pidcVer
   * @param pidcVariant pidcVer
   * @return the map with results having no var
   */
  protected Map<Long, Map<String, SortedSet<CDRResult>>> getWorkPkgWithoutVar(final PIDCVersion pidcVer,
      final PIDCVariant pidcVariant) {
    Map<Long, Map<String, SortedSet<CDRResult>>> retMap = this.pidcResMap.get(pidcVer.getID());
    if (null == retMap) {
      retMap = new HashMap<Long, Map<String, SortedSet<CDRResult>>>();
      loadWPNoVar(pidcVer, retMap);
      this.pidcResMap.put(pidcVer.getID(), retMap);
    }
    return retMap;
  }

  /**
   * @param pidcVer
   * @param retMap
   */
  private void loadWPNoVar(final PIDCVersion pidcVer, final Map<Long, Map<String, SortedSet<CDRResult>>> retMap) {
    if (!pidcVer.hasVariants(false)) {
      SortedSet<CDRResult> cdrResSet = this.dataProvider.getDataLoader().getCDRResults(pidcVer);
      Map<String, SortedSet<CDRResult>> varMap = this.dataProvider.getDataLoader().resolveWPNames(cdrResSet);
      retMap.put(CDRConstants.NO_VARIANT_NODE_ID, varMap);
    }
    SortedSet<CDRResult> cdrResSet = this.dataProvider.getResultsWithoutVar(pidcVer);
    if (!cdrResSet.isEmpty()) {
      Map<String, SortedSet<CDRResult>> varMap = this.dataProvider.getDataLoader().resolveWPNames(cdrResSet);
      retMap.put(CDRConstants.NO_VARIANT_NODE_ID, varMap);
    }

  }

  /**
   * @param pidcVer
   * @param retMap
   */
  private void loadWPForVar(final PIDCVersion pidcVer,
      final Map<Long, Map<String, SortedSet<CDRReviewVariant>>> retMap) {
    // if it has variants , add resultsSet for each variant
    if (pidcVer.hasVariants(false)) {
      for (PIDCVariant var : this.dataProvider.getVariantsWithResults(pidcVer)) {
        Map<String, SortedSet<CDRReviewVariant>> varResMap = getCDRWorkPackages(var, pidcVer, false);
        retMap.put(var.getID(), varResMap);
      }
    }

  }

  /**
   * Get Workpackage map for a pidc variant
   *
   * @param pidcVariant pidc variant
   * @param pidcVer version
   * @return Map of WPName(key) and SortedSet<CDRResult> (value)
   */
  protected Map<String, SortedSet<CDRReviewVariant>> getCDRWorkPackages(final PIDCVariant pidcVariant,
      final PIDCVersion pidcVer, final boolean reload) {
    Map<String, SortedSet<CDRReviewVariant>> retMap = this.varRvwResMap.get(pidcVariant.getID());
    if ((retMap == null) || reload) {
      SortedSet<CDRReviewVariant> cdrRvwSet = this.dataProvider.getReviewVarForVar(pidcVariant.getName(), pidcVer);
      retMap = this.dataProvider.getDataLoader().resolveWPNameForRvwVar(cdrRvwSet);
      if (!retMap.isEmpty()) {
        this.varRvwResMap.put(pidcVariant.getID(), retMap);
      }
    }
    return retMap;
  }

  /**
   * Get Workpackage map for a pidc variant
   *
   * @param pidcVariant pidc variant
   * @param pidcVer version
   * @return Map of WPName(key) and SortedSet<CDRResult> (value)
   */
  protected SortedSet<CDRReviewVariant> getResultsForVarWP(final PIDCVersion pidcVer, final PIDCVariant pidcVaraint,
      final String wpName) {
    Map<Long, Map<String, SortedSet<CDRReviewVariant>>> retMap = getWorkPackages(pidcVer, pidcVaraint);
    Map<String, SortedSet<CDRReviewVariant>> resultsMap;
    if (null == pidcVaraint) {
      resultsMap = retMap.get(CDRConstants.NO_VARIANT_NODE_ID);
    }
    else {
      resultsMap = retMap.get(pidcVaraint.getID());
    }
    if ((resultsMap == null) || resultsMap.isEmpty()) {
      return null;
    }
    return resultsMap.get(wpName);
  }

  /**
   * Get all the results for a pidc version
   *
   * @param pidcVer pidc version
   * @param wPName wp name
   * @return set of cdrreviewvariant
   */
  public SortedSet<CDRReviewVariant> getAllResults(final PIDCVersion pidcVer, final String wPName) {
    Map<Long, Map<String, SortedSet<CDRReviewVariant>>> retMap = getWorkPackages(pidcVer, null);
    Map<String, SortedSet<CDRReviewVariant>> resultsMap;
    SortedSet<CDRReviewVariant> resSet = new TreeSet<CDRReviewVariant>();
    if (pidcVer.hasVariants()) {
      for (PIDCVariant var : pidcVer.getVariantsSet()) {
        resultsMap = retMap.get(var.getID());
        if ((null != resultsMap) && resultsMap.containsKey(wPName)) {
          resSet.addAll(resultsMap.get(wPName));
        }
      }
    }
    else {
      resultsMap = retMap.get(CDRConstants.NO_VARIANT_NODE_ID);
      if ((resultsMap != null) && resultsMap.containsKey(wPName)) {
        resSet.addAll(resultsMap.get(wPName));
      }
    }
    return resSet;
  }

  /**
   * Reload review results
   *
   * @param pidcVersion PIDC version
   */
  protected void reloadPIDCResults(final PIDCVersion pidcVersion) {
    final SortedSet<CDRResult> cdrResSet = this.dataProvider.getDataLoader().getCDRResults(pidcVersion);
    this.pidcRvwResMap.put(pidcVersion.getID(), this.dataProvider.getDataLoader().resolveWPNames(cdrResSet));
  }

  protected void reloadResults(final PIDCVersion pidcVer) {
    Map<Long, Map<String, SortedSet<CDRResult>>> retMap = this.pidcResMap.get(pidcVer.getID());
    if (null == retMap) {
      retMap = new HashMap<>();
      loadWPNoVar(pidcVer, retMap);
    }

    else if (retMap.containsKey(CDRConstants.NO_VARIANT_NODE_ID)) {
      // if variants are newly added then add a seperate entry
      loadWPNoVar(pidcVer, retMap);
    }

    Map<Long, Map<String, SortedSet<CDRReviewVariant>>> reviewVarMap = this.pidcResMapWithVar.get(pidcVer.getID());
    // if the pidc version is not in the map.
    if (null == reviewVarMap) {
      reviewVarMap = new HashMap<Long, Map<String, SortedSet<CDRReviewVariant>>>();
      loadWPForVar(pidcVer, reviewVarMap);
    }
    // if key is null then the pidc does not have variants

    else {
      if (reviewVarMap.containsKey(CDRConstants.NO_VARIANT_NODE_ID)) {
        // if variants are newly added then add a seperate entry
        loadWPForVar(pidcVer, reviewVarMap);
      }
      for (PIDCVariant var : this.dataProvider.getVariantsWithResults(pidcVer)) {
        Map<String, SortedSet<CDRReviewVariant>> newVarResults = getCDRWorkPackages(var, pidcVer, true);
        reviewVarMap.put(var.getID(), newVarResults);
      }
    }
    this.pidcResMap.put(pidcVer.getID(), retMap);
    this.pidcResMapWithVar.put(pidcVer.getID(), reviewVarMap);
  }

  /**
   * @param existingVarID
   * @param pidcVer
   * @return
   */
  private boolean varAlreadyExists(final Long existingVarID, final PIDCVersion pidcVer) {
    for (PIDCVariant var : this.dataProvider.getVariantsWithResults(pidcVer)) {
      if (var.getID().equals(existingVarID)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Get the results for the given pidc and work package
   *
   * @param pidcVer PID Card
   * @param wpName Work Package
   * @return sorted set of results
   */
  protected SortedSet<CDRResult> getResults(final PIDCVersion pidcVer, final String wpName) {
    SortedSet<CDRResult> retSet = getCDRWorkPackages(pidcVer).get(wpName);
    if (retSet == null) {
      retSet = new TreeSet<CDRResult>();
    }
    return retSet;
  }


  /**
   * @param resultId resultId
   * @param fetchFromDB fetchFromDB
   * @return the cdr result from the db for the given pidcVersid
   */
  public CDRResult getCDRResult(final Long resultId, final boolean fetchFromDB) throws DataException {
    CDRResult ret = getCDRResult(resultId);
    if ((ret == null) && fetchFromDB) {
      Long pidcVerID = this.dataProvider.getEntityProvider().getDbCDRResult(resultId).getTPidcA2l().getTPidcVersion()
          .getPidcVersId();

      PIDCVersion pidcVersion = this.dataProvider.getApicDataProvider().getPidcVersion(pidcVerID, true);
      this.dataProvider.reloadPidcVersionResults(pidcVersion);
      ret = getCDRResult(resultId);
    }
    return ret;
  }

  /**
   * @param resultId result id
   * @return CDRResult
   */
  public CDRResult getCDRResult(final Long resultId) {
    return this.cdrResultMap.get(resultId);
  }


  /**
   * @param funcId function id
   * @return CDRResultFunction
   */
  public CDRResultFunction getCDRResultFunction(final Long funcId) {
    return this.cdrResultFuncMap.get(funcId);
  }

  /**
   * @param paramId param id
   * @return CDRResultParameters
   */
  public CDRResultParameter getCDRResultParameter(final Long paramId) {
    return this.cdrResultParamMap.get(paramId);
  }

  /**
   * @return CDR Result Function Map
   */
  protected Map<Long, CDRResultFunction> getCDRResultFunctionMap() {
    return this.cdrResultFuncMap;
  }

  /**
   * Get all CDRResultParameters cached for this session
   *
   * @return CDRResultParameters map
   */
  protected Map<Long, CDRResultParameter> getAllCDRResultParameters() {
    return this.cdrResultParamMap;
  }

  /**
   * @param participantId primary key
   * @return CDRParticipant
   */
  protected CDRParticipant getCDRParticipant(final Long participantId) {
    return this.cdrParticipantMap.get(participantId);
  }

  /**
   * Get all CDRParticipant cached for this session
   *
   * @return CDRParticipant map
   */
  protected Map<Long, CDRParticipant> getAllCDRParticipants() {
    return this.cdrParticipantMap;
  }


  /**
   * Get all CDRParticipant cached for this session
   *
   * @return CDRParticipant map
   */
  protected Map<Long, CDRReviewAttrValue> getAllReviewAttrVal() {
    return this.rvwValueMap;
  }

  /**
   * Get all CDRParticipant cached for this session
   *
   * @return CDRParticipant map
   */
  protected Map<Long, CDRReviewVariant> getAllReviewVariants() {
    return this.rvwVarMap;
  }


  /**
   * // iCDM-1366 Get all Rule sets
   *
   * @param includeDeleted true if the deleted components are to be included
   * @return sorted set of Rule sets
   */
  protected SortedSet<RuleSet> getAllRuleSets(final boolean includeDeleted) {

    if (getAllRuleSetMap().isEmpty()) {
      this.dataProvider.getDataLoader().loadAllRuleSets();
    }

    SortedSet<RuleSet> ruleSetSortSet = new TreeSet<RuleSet>(getAllRuleSetMap().values());

    // Remove the deleted rulesets, if they are not required
    if (!includeDeleted) {
      for (RuleSet ruleSet : getAllRuleSetMap().values()) {
        if (ruleSet.isDeleted()) {
          ruleSetSortSet.remove(ruleSet);
        }
      }
    }
    return ruleSetSortSet;
  }

  /**
   * Gets all Rule sets
   *
   * @return map of Rule sets
   */
  protected Map<Long, RuleSet> getAllRuleSetMap() {
    return this.allRuleSets;
  }

  /**
   * @param rsetId ruleset id
   * @return RuleSet
   */
  public RuleSet getRuleSet(final long rsetId) {
    return this.allRuleSets.get(rsetId);
  }

  /**
   * @param rsetParamId primaryKey
   * @return RuleSetParameter
   */
  public RuleSetParameter getRuleSetParam(final long rsetParamId) {
    return this.allRuleSetParams.get(rsetParamId);
  }


  /**
   * @return map of RuleSetParameterAttribute
   */
  protected Map<Long, RuleSetParameterAttribute> getRuleSetParamAttrMap() {
    return this.rsParamAttrMap;
  }

  /**
   * Add Rule set Param Attribute
   *
   * @param rsParamAttr ruleset param attr
   * @throws SsdInterfaceException
   */
  protected void addRuleSetParamAttr(final RuleSetParameterAttribute rsParamAttr) throws SsdInterfaceException {
    // add to all param attrs
    this.rsParamAttrMap.put(rsParamAttr.getID(), rsParamAttr);
    // also add to corresponding ruleset
    addRuleSetParamDependencyMap(rsParamAttr);
  }

  /**
   * Add rule set param to cache
   *
   * @param rsParam RuleSetParameter
   */

  protected void addRuleSetParameter(final RuleSetParameter rsParam) {
    // add to all rule set params map
    this.allRuleSetParams.put(rsParam.getID(), rsParam);
  }

  /**
   * remove rule set param from cache
   *
   * @param rsParam RuleSetParameter
   */
  protected void removeRuleSetParameter(final RuleSetParameter rsParam) {
    // add to all rule set params map
    this.allRuleSetParams.remove(rsParam);
  }

  /**
   * Get rule set param Dependencies
   *
   * @return the ruleSetParamDepenMap
   */
  protected Map<Long, Set<RuleSetParameterAttribute>> getRuleSetParamDependencyMap() {
    return this.ruleSetParamDepenMap;
  }

  /**
   * Add ruleset param attr to the corresponding rule set
   *
   * @param paramAttr param attr
   * @throws SsdInterfaceException
   */
  protected void addRuleSetParamDependencyMap(final RuleSetParameterAttribute paramAttr) throws SsdInterfaceException {
    Set<RuleSetParameterAttribute> depenSet = this.ruleSetParamDepenMap.get(paramAttr.getParameter().getID());
    if (depenSet == null) {
      depenSet = new TreeSet<RuleSetParameterAttribute>();
    }
    depenSet.add(paramAttr);
    this.ruleSetParamDepenMap.put(paramAttr.getParameter().getID(), depenSet);
  }


  /* iCDM-471 */
  /**
   * Gets CDR functions (those accessed) cached for this session.
   *
   * @return functions map
   */
  protected Map<String, CDRFunction> getAllCDRFunctions() {
    return this.cdrFunctionsMap;
  }

  /**
   * Get CDRFunction obj for the function name.
   *
   * @param funcName function name
   * @return CDRFunction obj
   */
  protected CDRFunction getCDRFunction(final String funcName) {
    // get the function from map.
    CDRFunction cdrFunction = this.cdrFunctionsMap.get(funcName);
    // if null fetch from db with funcname and not relevant to be included. If not assigned param then pass as false
    if (cdrFunction == null) {
      cdrFunction = getDataProvider().getDataLoader().fetchFunctionDetails(funcName,
          !CommonUtils.isEqual(funcName, ApicConstants.NOT_ASSIGNED));
    }
    // If the function is relevant or Not assigned add it to cache.
    if (CommonUtils.isNotNull(cdrFunction) &&
        (cdrFunction.isRelevant() || CommonUtils.isEqual(funcName, ApicConstants.NOT_ASSIGNED))) {
      this.cdrFunctionsMap.put(cdrFunction.getName(), cdrFunction);
    }
    return cdrFunction;
  }


  /**
   * Get CDRFunction obj for the function name.
   *
   * @param funcName function name
   * @return CDRFunction obj
   */
  protected List<CDRFunction> getCDRFunList(final String funcName) {
    List<CDRFunction> cdrFunList = getDataProvider().getDataLoader().getCDRFunList(funcName);
    return cdrFunList;
  }

  /**
   * Get the CDRFunction obj for the functionID
   *
   * @param funcId function id
   * @return CDRFunction
   */
  protected CDRFunction getCDRFunction(final Long funcId) {
    for (CDRFunction cdrFunc : this.cdrFunctionsMap.values()) {
      if (cdrFunc.getID().longValue() == funcId.longValue()) {
        return cdrFunc;
      }
    }
    return null;
  }

  /**
   * Add the CDR function parameters to the cache
   *
   * @param param CDRFuncParameter BO to add
   */
  protected void addCDRFuncParameter(final CDRFuncParameter param) {
    String key = param.getName().toUpperCase(Locale.getDefault());
    // NOTE : key only with name, to support SSD constraint of not considering param type
    this.cdrFuncParamsMap.put(key, param);
  }

  /**
   * Get the CDRFuncParameter for the param name and type
   *
   * @param name name of parameter
   * @param type type of parameter
   * @return the CDR Func parameter BO
   */
  protected CDRFuncParameter getCDRFuncParameter(final String name, final String type) {
    String key = name.toUpperCase(Locale.getDefault());
    // NOTE : key only with name, to support SSD constraint of not considering param type
    return this.cdrFuncParamsMap.get(key);
  }

  /**
   * Icdm-801 Getting function names
   *
   * @param selFuns functions selected for review
   * @return function names in the T functions table
   */
  public Set<String> getCDRFunctionNameSet(final SortedSet<String> selFuns) {
    Set<String> cdrFunSet = new TreeSet<String>();
    List<String> unavialFunInCache = new ArrayList<String>();
    for (String funcName : selFuns) {
      final CDRFunction cdrFunction = this.cdrFunctionsMap.get(funcName);
      if (cdrFunction == null) {
        unavialFunInCache.add(funcName);
      }
      else {
        this.cdrFunctionsMap.put(cdrFunction.getName(), cdrFunction);
        cdrFunSet.add(cdrFunction.getName());

      }
    }

    cdrFunSet.addAll(getDataProvider().getDataLoader().fetchFunctionNames(unavialFunInCache));

    return cdrFunSet;
  }

  /**
   * Icdm-1032
   *
   * @return map of Param Attr
   */
  public Map<Long, ParameterAttribute> getParamAttrMap() {
    return this.paramAttrMap;
  }

  /**
   * @return the paramDependencyMap
   */
  protected Map<Long, Set<ParameterAttribute>> getParamDependencyMap() {
    return this.paramDependencyMap;
  }


  /**
   * Icdm-801 Getting param names
   *
   * @param labelList labelList
   * @return the String in the T_param table
   */
  public Set<String> getLabelNameSet(final List<String> labelList) {
    Set<String> labNameSet = new TreeSet<String>();
    labNameSet.addAll(getDataProvider().getDataLoader().fetchLabelNames(labelList));
    return labNameSet;
  }

  /**
   * Remove ruleset param attr
   *
   * @param paramAttr param attr
   * @throws SsdInterfaceException
   */
  public void removeRuleSetParamAttr(final RuleSetParameterAttribute paramAttr) throws SsdInterfaceException {
    // remove from all param attrs
    this.rsParamAttrMap.remove(paramAttr.getID());
    // also remove from corresponding ruleset
    removeRuleSetParamDependencyMap(paramAttr);

  }

  /**
   * Remove from corresponding ruleset param attr
   *
   * @param paramAttr rs param attr
   * @throws SsdInterfaceException
   */
  protected void removeRuleSetParamDependencyMap(final RuleSetParameterAttribute paramAttr)
      throws SsdInterfaceException {
    Set<RuleSetParameterAttribute> depenSet = this.ruleSetParamDepenMap.get(paramAttr.getParameter().getID());
    if (depenSet != null) {
      depenSet.remove(paramAttr);
    }
    this.ruleSetParamDepenMap.put(paramAttr.getParameter().getID(), depenSet);
  }

  /**
   * @return the pidcRvwResMap
   */
  public Map<String, SortedSet<CDRResult>> getPidcRvwResMapForVersion(final PIDCVersion pidcVersion) {
    if (null == this.pidcRvwResMap.get(pidcVersion.getPidcVersion().getID())) {
      getCDRWorkPackages(pidcVersion);
    }
    return this.pidcRvwResMap.get(pidcVersion.getPidcVersion().getID());
  }

  /**
   * @return the pidcResMap
   */
  public Map<Long, Map<Long, Map<String, SortedSet<CDRResult>>>> getPidcResMap() {
    return this.pidcResMap;
  }

  /**
   * ICDM-2005
   *
   * @param qNaireID ID
   * @return Questionnaire
   */
  public Questionnaire getQuestionnaire(final Long qNaireID) {
    synchronized (this.qNaireSyncLock) {
      return this.questionnaireMap.get(qNaireID);
    }
  }


  /**
   * ICDM-2005
   *
   * @param qNaireVersID ID
   * @return QuestionnaireVersion
   */
  public QuestionnaireVersion getQuestionnaireVersion(final Long qNaireVersID) {
    return this.allqnaireVersMap.get(qNaireVersID);
  }

  /**
   * ICDM-2005
   *
   * @return Map<Long, QuestionnaireVersion>
   */
  public Map<Long, QuestionnaireVersion> getAllQuestionnaireVersionMap() {
    return this.allqnaireVersMap;
  }

  /**
   * ICDM-2005
   *
   * @param questionID ID
   * @return Question
   */
  public Question getQuestion(final Long questionID) {
    return this.questionMap.get(questionID);
  }

  /**
   * ICDM-2005
   *
   * @return Map<Long, Question>
   */
  public Map<Long, Question> getQuestionMap() {
    return this.questionMap;
  }

  /**
   * ICDM-2005
   *
   * @param qConfig ID
   * @return Question
   */
  public QuestionConfig getQuestionConfig(final Long qConfig) {
    return this.questionConfigMap.get(qConfig);
  }

  /**
   * ICDM-2005
   *
   * @return Map<Long, Question>
   */
  public Map<Long, QuestionConfig> getQuestionConfigMap() {
    return this.questionConfigMap;
  }

  /**
   * ICDM-2005
   *
   * @param qDepenAttrID ID
   * @return Question
   */
  public QuestionDepenAttr getQuestionDepenAttribute(final Long qDepenAttrID) {
    return this.questionDepenAttrMap.get(qDepenAttrID);
  }

  /**
   * ICDM-2005
   *
   * @return Map<Long, QuestionDepenAttr>
   */
  public Map<Long, QuestionDepenAttr> getQuestionDepenAttributeMap() {
    return this.questionDepenAttrMap;
  }

  /**
   * ICDM-2005
   *
   * @param qDepenAttrValID ID
   * @return Question
   */
  public QuestionDepenAttrValue getQuestionDepenAttrValue(final Long qDepenAttrValID) {
    return this.questionDepenAttrValMap.get(qDepenAttrValID);
  }


  /**
   * ICDM-2039
   *
   * @param revQnaireAnsID ID
   * @return ReviewQnaireAnswer
   */
  public ReviewQnaireAnswer getReviewQnaireAnswer(final Long revQnaireAnsID) {
    return this.reviewQnaireAnsMap.get(revQnaireAnsID);
  }

  /**
   * ICDM-2039
   *
   * @return Map<Long, ReviewQnaireAnswer>
   */
  public Map<Long, ReviewQnaireAnswer> getReviewQnaireAnsMap() {
    return this.reviewQnaireAnsMap;
  }

  /**
   * ICDM-2190
   *
   * @param revQnaireAnsID id
   * @return QnaireAnsOpenPoint
   */
  public QnaireAnsOpenPoint getQnaireAnsOpenPoint(final Long revQnaireAnsID) {
    return this.qnaireAnsOpenPointMap.get(revQnaireAnsID);
  }

  /**
   * ICDM-2190
   *
   * @return Map<Long, ReviewQnaireAnswer>
   */
  public Map<Long, QnaireAnsOpenPoint> getQnaireAnsOpenPointMap() {
    return this.qnaireAnsOpenPointMap;
  }

  /**
   * ICDM-2005
   *
   * @return Map<Long, QuestionDepenAttr>
   */
  public Map<Long, QuestionDepenAttrValue> getQuestionDepenAttrValueMap() {
    return this.questionDepenAttrValMap;
  }

  /**
   * @return the questionnaireMap
   */
  public ConcurrentMap<Long, Questionnaire> getQuestionnaireMap() {
    return this.questionnaireMap;
  }

  /**
   * @param includeDeleted includeDeleted
   * @return all the questionares
   */
  public SortedSet<Questionnaire> getAllQuestionares(final boolean includeDeleted) {
    if (!this.isAllQnaireLoaded) {
      this.dataProvider.getDataLoader().getAllQuestionares();
      this.isAllQnaireLoaded = true;
    }

    SortedSet<Questionnaire> questionareSet = new TreeSet<Questionnaire>(getQuestionnaireMap().values());

    // Remove the deleted questionnaires, if they are not required
    if (!includeDeleted) {
      for (Questionnaire qNaire : getQuestionnaireMap().values()) {
        if (qNaire.isDeleted()) {
          questionareSet.remove(qNaire);
        }
      }
    }
    return questionareSet;
  }


  /**
   * @param pidcVers PIDC Version
   * @return Value - Sorted set of string Array (Length 3) <br>
   *         arr[0] - CDRConstants.NODE_TYPE_QNAIRE_VARIANTS<br>
   *         arr[1] - Variant ID, if variant is available, PIDC Version ID, if ApicConstants.DUMMY_VAR_NODE_NOVAR <br>
   *         arr[2] - Variant Name, if variant available, ApicConstants.DUMMY_VAR_NODE_NOVAR if no variant available
   */
  // ICDM-2332
  public SortedSet<String[]> getQnaireVariantDet(final PIDCVersion pidcVers) {
    return this.qnaireVariantDetMap.get(pidcVers.getID());
  }

  /**
   * Adds the questionnaire response variant node details to the cache.
   *
   * @param pidcVers PIDC Version
   * @param details sorted set of string array (length 3)
   */
  // ICDM-2332
  public void setQnaireVariantDet(final PIDCVersion pidcVers, final SortedSet<String[]> details) {
    this.qnaireVariantDetMap.put(pidcVers.getID(), details);
  }


  /**
   * @return the icdmWorkPackageMap
   */
  public ConcurrentMap<Long, IcdmWorkPackage> getIcdmWorkPackageMap() {
    if (!this.icdmWpLoaded) {
      getDataProvider().getDataLoader().loadIcdmWorkPackages();
    }
    return this.icdmWorkPackageMap;
  }


  /**
   * @return the wrkPkgDivisionMap
   */
  public ConcurrentMap<Long, WorkPackageDivision> getWrkPkgDivisionMap() {
    if (!this.icdmWpDivsLoaded) {
      this.dataProvider.getDataLoader().loadWpDivsions();
    }
    return this.wrkPkgDivisionMap;
  }

  /**
   * @param wpDivID
   * @return
   */
  public WorkPackageDivision getWorkPackageDiv(final Long wpDivID) {
    WorkPackageDivision wpDiv = getWrkPkgDivisionMap().get(wpDivID);
    if (wpDiv == null) {
      this.dataProvider.getDataLoader().loadWpDivsions();
      wpDiv = getWrkPkgDivisionMap().get(wpDivID);
    }
    return wpDiv;
  }

  /**
   * @param wrkPkgID
   * @return
   */
  public IcdmWorkPackage getIcdmWorkPackage(final Long wrkPkgID) {
    IcdmWorkPackage wrkPkg = getIcdmWorkPackageMap().get(wrkPkgID);
    if (wrkPkg == null) {
      this.dataProvider.getDataLoader().loadIcdmWorkPackages();
      wrkPkg = getIcdmWorkPackageMap().get(wrkPkgID);
    }
    return wrkPkg;
  }

  /**
   * @return the mappedWrkPkgMap
   */
  public ConcurrentMap<AttributeValue, List<IcdmWorkPackage>> getMappedWrkPkgMap() {
    if (!this.icdmWpDivsLoaded) {
      this.dataProvider.getDataLoader().loadWpDivsions();
    }
    return this.mappedWrkPkgMap;
  }


  /**
   * @param respID response ID
   * @return QuestionnaireResponse
   */
  // ICDM-2404
  public QuestionnaireResponse getQuestionnaireResponse(final Long respID) {
    return this.qnaireResponseMap.get(respID);
  }

  /**
   * Add or remove questionnaire response from the global map
   *
   * @param response response object
   * @param add if true, adds the object, else removes it
   */
  // ICDM-2404
  public void addRemoveQnaireResponse(final QuestionnaireResponse response, final boolean add) {
    if (add) {
      this.qnaireResponseMap.put(response.getID(), response);
    }
    else {
      this.qnaireResponseMap.remove(response.getID());
    }
  }

  /**
   * @param projObj project object - PIDC Version or Variant
   * @return the version of General Questionnaire used
   */
  // ICDM-2404
  public QuestionnaireVersion getProjGenQnaireVers(final AbstractProjectObject<?> projObj) {
    return this.projGenQnaireMap.get(projObj);
  }

  /**
   * Set/Remove the general questionnaire version against a project object
   *
   * @param projObj project object - PIDC Version or Variant
   * @param genQnaireVers the version of General Questionnaire used. If null, it will remove the questionnaire version
   */
  // ICDM-2404
  public void setRemoveProjGeneralQnaireVers(final AbstractProjectObject<?> projObj,
      final QuestionnaireVersion genQnaireVers) {
    if (genQnaireVers == null) {
      this.projGenQnaireMap.remove(projObj);
    }
    else {
      this.projGenQnaireMap.put(projObj, genQnaireVers);
    }
  }

  /**
   * Get the sorted set of questionnaire responses added to this project object
   *
   * @param projObj project object - PIDC Version or Variant
   * @return Map of responses. Key - 'Questionnaire' ID, Value - QuestionnaireResponse
   */
  // ICDM-2404
  public Map<Long, QuestionnaireResponse> getProjQnaireResponseMap(final AbstractProjectObject<?> projObj) {
    ConcurrentMap<Long, QuestionnaireResponse> respMap = this.projQnaireResMap.get(projObj);
    if (respMap == null) {
      respMap = new ConcurrentHashMap<>();
      this.projQnaireResMap.put(projObj, respMap);
    }
    return respMap;
  }

  /**
   * Find the questionnaire response for the given questionnaire in the pidc version/ variant
   *
   * @param projObj project object - PIDC Version or Variant
   * @param qnaireID 'Questionnaire' ID
   * @return QuestionnaireResponse, if available. else null
   */
  // ICDM-2404
  public QuestionnaireResponse getProjQnaireResponse(final AbstractProjectObject<?> projObj, final Long qnaireID) {
    return getProjQnaireResponseMap(projObj).get(qnaireID);
  }

  /**
   * Add or remove a questionnaire response from te project - questionnaire response collection
   *
   * @param projObj project object - PIDC Version or Variant
   * @param qnaireResp questionnaire response
   * @param add if true, then add to the collection, else remove from collection
   */
  // ICDM-2404
  public void addRemoveProjQnaireResponse(final AbstractProjectObject<?> projObj,
      final QuestionnaireResponse qnaireResp, final boolean add) {

    Map<Long, QuestionnaireResponse> respMap = getProjQnaireResponseMap(projObj);

    Long qnaireID = qnaireResp.getQNaireVersion().getQuestionnaire().getID();
    if (add) {
      respMap.put(qnaireID, qnaireResp);
    }
    else {
      respMap.remove(qnaireID);
    }
  }

  /**
   * Get the general quesitonnaire configured in iCDM system.
   *
   * @return general questionnaire
   */
  // ICDM-2404
  public Questionnaire getGeneralQuestionnaire() {
    if (this.genQuestionnaire != null) {
      return this.genQuestionnaire;
    }

    getDataProvider().getLogger().debug("Finding general questionnaire");

    String genQnairePropVal = this.dataProvider.getApicDataProvider().getParameterValue(KEY_GENERAL_QNAIRE_ID);
    if (CommonUtils.isEmptyString(genQnairePropVal)) {
      getDataProvider().getLogger().warn("General Questionnaire not defined in system");
      return null;
    }
    long genQnaireID = Long.valueOf(genQnairePropVal);
    this.genQuestionnaire = this.questionnaireMap.get(genQnaireID);
    if (this.genQuestionnaire == null) {
      this.genQuestionnaire = new Questionnaire(getDataProvider(), genQnaireID);
    }

    getDataProvider().getLogger().info("General Questionnaire found : ID={}", genQnaireID);

    return this.genQuestionnaire;
  }

  /**
   * Get the active version of the general questionnaire.
   *
   * @return active version of general questionnaire, null if no general questionnaire or no active version
   */
  // ICDM-2404
  public QuestionnaireVersion getGeneralQnaireActiveVersion() {
    Questionnaire genQnaire = getGeneralQuestionnaire();
    QuestionnaireVersion genQnaireVers = null;

    if (genQnaire != null) {
      genQnaireVers = genQnaire.getActiveVersion();
    }
    if (genQnaireVers == null) {
      getDataProvider().getLogger().warn("No active version found for general questionnaire");
    }
    else {
      getDataProvider().getLogger().debug("General Questionnaire active version : ID={}", genQnaireVers.getID());
    }

    return genQnaireVers;

  }


  /**
   * @param icdmWpLoaded the icdmWpLoaded to set
   */
  protected void setIcdmWpLoaded(final boolean icdmWpLoaded) {
    this.icdmWpLoaded = icdmWpLoaded;
  }


  /**
   * @param icdmWpDivsLoaded the icdmWpDivsLoaded to set
   */
  protected void setIcdmWpDivsLoaded(final boolean icdmWpDivsLoaded) {
    this.icdmWpDivsLoaded = icdmWpDivsLoaded;
  }

  // ICDM-2646
  /**
   * @param division (DGS/BEG/DS)
   * @return Map of Workpackages for the division
   */
  public ConcurrentMap<Long, IcdmWorkPackage> getIcdmWorkPackageMapForDiv(final AttributeValue division) {

    ConcurrentMap<Long, IcdmWorkPackage> icdmWorkPackageMapForDiv = new ConcurrentHashMap<>();
    if (!this.icdmWpDivsLoaded) {
      this.dataProvider.getDataLoader().loadWpDivsions();
    }

    // Review 237201
    for (WorkPackageDivision wpDiv : getWrkPkgDivisionMap().values()) {
      if (!wpDiv.isWpDivDeleteFlag() && !wpDiv.getWorkPackage().isWpDeleteFlag() &&
          wpDiv.getDivision().getID().equals(division.getID())) {
        icdmWorkPackageMapForDiv.put(wpDiv.getWorkPackage().getID(), wpDiv.getWorkPackage());
      }
    }

    return icdmWorkPackageMapForDiv;
  }

  /**
   * @param secResID secResID
   * @return the cdr secondary result map
   */
  public CDRSecondaryResult getCDRResSecondary(final Long secResID) {
    return this.cdrResSecMap.get(secResID);
  }

  /**
   * @return the map of cdrResParamSecondary
   */
  public Map<Long, CDRResParamSecondary> getAllSecRvwPrams() {

    return this.cdrResParamSecMap;
  }

  /**
   * @param entityID entityID
   * @return the CDRResParamSecondary object from cache
   */
  public CDRResParamSecondary getCDRResParamSecondary(final Long entityID) {
    return this.cdrResParamSecMap.get(entityID);
  }

  /**
   * @return the Rule data map.
   */
  public Map<ReviewRuleSetData, CDRSecondaryResult> getRuleDataMap() {
    return this.ruleDataMap;
  }
}