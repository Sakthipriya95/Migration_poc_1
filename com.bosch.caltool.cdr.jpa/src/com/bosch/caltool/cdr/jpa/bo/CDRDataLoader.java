/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */

package com.bosch.caltool.cdr.jpa.bo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
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

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.caltool.a2l.jpa.bo.FCToWP;
import com.bosch.caltool.apic.jpa.bo.AbstractProjectObject;
import com.bosch.caltool.apic.jpa.bo.AttributeValue;
import com.bosch.caltool.apic.jpa.bo.PIDCA2l;
import com.bosch.caltool.apic.jpa.bo.PIDCVariant;
import com.bosch.caltool.apic.jpa.bo.PIDCVersion;
import com.bosch.caltool.dmframework.bo.AbstractDataLoader;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayChangeEvent;
import com.bosch.caltool.dmframework.notification.ICacheRefresher;
import com.bosch.caltool.icdm.common.exception.SsdInterfaceException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TFc2wp;
import com.bosch.caltool.icdm.database.entity.apic.GttObjectName;
import com.bosch.caltool.icdm.database.entity.apic.GttParameter;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionnaire;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionnaireVersion;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireResponse;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackage;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackageDivision;
import com.bosch.caltool.icdm.database.entity.cdr.TFunction;
import com.bosch.caltool.icdm.database.entity.cdr.TFuncversUnique;
import com.bosch.caltool.icdm.database.entity.cdr.TParamAttr;
import com.bosch.caltool.icdm.database.entity.cdr.TParameter;
import com.bosch.caltool.icdm.database.entity.cdr.TRuleSet;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParameter;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwVariant;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.ssd.icdm.model.CDRRule;


/**
 * This class loads the data from the database, and stores in the DataCache class. The entity manager is provided by
 * EntityProvider instance
 *
 * @author BNE4COB
 */
class CDRDataLoader extends AbstractDataLoader implements ICacheRefresher {

  /**
   * Array size of PIDC tree node, (special nodes, not associated with business objects)
   */
  private static final int TREENODE_ARRAY_SIZE = 3;

  /**
   * Version length
   */
  private static final int VERSION_LENGTH = 3;
  /**
   * String builder initial size
   */
  private static final int PARAM_BUFFER_SIZE = 180;
  /**
   * The logger instance
   */
  protected ILoggerAdapter logger;
  /**
   * CDR data cache instance
   */
  private final CDRDataCache dataCache;
  /**
   * CDR entity provider instance
   */
  private final CDREntityProvider entProvider;
  /**
   * CDR data provider
   */
  private final CDRDataProvider dataProvider;

  /**
   * new constant for Delete Global temp
   */
  private static final String DELETE_GLB_TEMP_QUERY = "delete from GttObjectName temp";


  /**
   * Constructor
   *
   * @param dataProvider the cdr data provider
   * @param cache the data cache instance
   * @param entProvider entity provider instance
   * @param logger logger
   */
  CDRDataLoader(final CDRDataProvider dataProvider, final CDRDataCache cache, final CDREntityProvider entProvider,
      final ILoggerAdapter logger) {

    super();

    this.logger = logger;
    this.dataCache = cache;
    this.entProvider = entProvider;
    this.dataProvider = dataProvider;
    ObjectStore.getInstance().registerCacheRefresher(this);
  }

  /**
   * Fetch start up data
   */
  protected final void fetchStartupData() {
    // Nothing to load at startup

  }

  /**
   * Get the review results for a given PIDC
   *
   * @param pidcVer Project ID card version
   * @return the sorted set of CDR results
   */
  public SortedSet<CDRResult> getCDRResults(final PIDCVersion pidcVer) {
    // TODO use entity relationship instead of db query
    final String query =
        "select rvwRes from TRvwResult rvwRes where rvwRes.TPidcA2l.TPidcVersion.pidcVersId  = " + pidcVer.getID();
    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    final TypedQuery<TRvwResult> typeQuery = entMgr.createQuery(query, TRvwResult.class);
    typeQuery.setHint(ApicConstants.FETCH_SIZE, "100");
    final List<TRvwResult> resultList = typeQuery.getResultList();

    CDRResult cdrRes;
    final SortedSet<CDRResult> retSet = new TreeSet<CDRResult>();
    for (TRvwResult dbRes : resultList) {
      cdrRes = this.dataCache.getCDRResult(dbRes.getResultId());
      if (cdrRes == null) {
        cdrRes = new CDRResult(this.dataProvider, dbRes.getResultId()); // NOPMD by bne4cob on 7/8/14 9:36 AM
      }
      retSet.add(cdrRes);
    }
    return retSet;
  }

  /**
   * Get the review results for a given A2l file
   *
   * @param pidcA2l pidca2l instance
   * @return the sorted set of CDR results
   */
  public SortedSet<CDRResult> getCDRResults(final PIDCA2l pidcA2l) {
    final String query = "select rvwRes from TRvwResult rvwRes where rvwRes.TPidcA2l.pidcA2lId  = " + pidcA2l.getID();
    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    final TypedQuery<TRvwResult> typeQuery = entMgr.createQuery(query, TRvwResult.class);
    typeQuery.setHint(ApicConstants.FETCH_SIZE, "100");
    final List<TRvwResult> resultList = typeQuery.getResultList();

    CDRResult cdrRes;
    final SortedSet<CDRResult> retSet = new TreeSet<CDRResult>();
    for (TRvwResult dbRes : resultList) {
      cdrRes = this.dataCache.getCDRResult(dbRes.getResultId());
      if (cdrRes == null) {
        cdrRes = new CDRResult(this.dataProvider, dbRes.getResultId());
      }
      retSet.add(cdrRes);
    }
    return retSet;
  }

  /**
   * Get the review results for a given Parameter
   *
   * @param paramId parameter id
   * @return the sorted set of CDR results
   */
  public SortedSet<CDRResultParameter> getCDRParamResults(final long paramId) {

    List<DATA_REVIEW_SCORE> reviewedValues = DATA_REVIEW_SCORE.getReviewedValues();
    List<String> reviwedTypes = new ArrayList<>();
    for (DATA_REVIEW_SCORE score : reviewedValues) {
      reviwedTypes.add(score.getDbType());
    }

    final String query =
        "select rvwRes from TRvwParameter rvwRes join rvwRes.TParameter param join rvwRes.TRvwResult result where  result.reviewType != 'T' and  rvwRes.reviewScore  IN :reviewedTypes  and param.id  = " +
            paramId;
    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();

    final TypedQuery<TRvwParameter> typeQuery = entMgr.createQuery(query, TRvwParameter.class);
    typeQuery.setHint(ApicConstants.FETCH_SIZE, "100");
    typeQuery.setParameter("reviewedTypes", reviwedTypes);
    final List<TRvwParameter> resultList = typeQuery.getResultList();

    final SortedSet<CDRResultParameter> retSet = new TreeSet<CDRResultParameter>();
    for (TRvwParameter dbRes : resultList) {

      CDRResult cdrResult = this.dataCache.getCDRResult(dbRes.getTRvwResult().getResultId());
      if (cdrResult == null) {
        cdrResult = new CDRResult(this.dataProvider, dbRes.getTRvwResult().getResultId()); // NOPMD by bne4cob on
        // 1/29/14 5:34 PM
        this.dataCache.getAllCDRResults().put(dbRes.getTRvwResult().getResultId(), cdrResult);
      }

      CDRResultParameter cdrResultParam = this.dataCache.getCDRResultParameter(dbRes.getRvwParamId());
      if (cdrResultParam == null) {
        cdrResultParam = new CDRResultParameter(this.dataProvider, dbRes.getRvwParamId()); // NOPMD by bne4cob on
        this.dataCache.getAllCDRResultParameters().put(dbRes.getRvwParamId(), cdrResultParam);
      }
      retSet.add(cdrResultParam);
    }
    return retSet;
  }


  /**
   * Fetch fc2wp for the wpId's
   *
   * @param wpIdSet set of wp id's
   * @return FC2Wp map
   */
  private Map<Long, FCToWP> fetchFcToWpFromDB(final Set<Long> wpIdSet) {

    final Map<Long, FCToWP> fc2wpMap = new HashMap<Long, FCToWP>();
    // Empty list would cause error in executing query
    if (wpIdSet.isEmpty()) {
      return fc2wpMap;
    }

    this.logger.debug("fetching FC2WP from database");

    // Create query with list of wp ids
    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    // Execute the query with required hints
    final TypedQuery<TFc2wp> typeQuery =
        entMgr.createQuery("SELECT fc2wp FROM TFc2wp fc2wp where fc2wp.id IN :wpIDs ", TFc2wp.class);
    typeQuery.setHint(ApicConstants.READ_ONLY, "true");
    typeQuery.setHint(ApicConstants.FETCH_SIZE, "100");

    typeQuery.setParameter("wpIDs", wpIdSet);
    // Add objects to map
    final List<TFc2wp> resultList = typeQuery.getResultList();
    FCToWP fc2wp;
    for (TFc2wp dbFC2WP : resultList) {
      fc2wp =
          new FCToWP(this.dataProvider.getApicDataProvider().getLanguage(), dbFC2WP.getId(), dbFC2WP.getCalContact1(), // NOPMD
              // by
              // bne4cob
              // on
              // 7/8/14
              // 9:36
              // AM
              dbFC2WP.getCalContact2(), dbFC2WP.getFc2wpType(), dbFC2WP.getWpGroup(), dbFC2WP.getWpNameE(),
              dbFC2WP.getWpNameG(), dbFC2WP.getWpNumber(), dbFC2WP.getFc());
      fc2wpMap.put(dbFC2WP.getId(), fc2wp);
    }
    return fc2wpMap;
  }

  /**
   * Resolve WP names for the Wp Ids
   *
   * @param retSet CDRResult
   * @return Map of WPName(key) and SortedSet<CDRResult> (value)
   */
  protected Map<String, SortedSet<CDRResult>> resolveWPNames(final SortedSet<CDRResult> retSet) {
    final List<Long> wpIdList = new ArrayList<Long>();
    final Map<String, SortedSet<CDRResult>> wpCDRResults = new HashMap<String, SortedSet<CDRResult>>();
    if (retSet != null) {
      // Step:1 - Get WP names (group based,config)
      for (CDRResult cdrResult : retSet) {
        // ICdm-729 new Method created for the Group mapping
        createCdrMapForGrp(wpIdList, wpCDRResults, cdrResult);
      }

      // filter the wpId's already available in cache
      final Set<Long> wpIds2Fetch = new HashSet<Long>();
      for (Long fc2wp : wpIdList) {
        wpIds2Fetch.add(fc2wp);
      }

      // Step:2 - Get WP names (fc2wp type based config)
      // fetch ids not available in cache, this fetch method also adds to cached fc2wp's
      for (CDRResult cdrResult : retSet) {
        // ICdm-729 new Method created for the Group mapping
        createCdrMapForFcWp(wpCDRResults, cdrResult);

      }
    }

    return wpCDRResults;
  }


  protected Map<String, SortedSet<CDRReviewVariant>> resolveWPNameForRvwVar(final SortedSet<CDRReviewVariant> retSet) {

    final List<Long> wpIdList = new ArrayList<Long>();
    final Map<String, SortedSet<CDRReviewVariant>> wpCDRResults = new ConcurrentHashMap<>();
    if (retSet != null) {
      // Step:1 - Get WP names (group based,config)
      for (CDRReviewVariant cdrRvwVar : retSet) {
        // ICdm-729 new Method created for the Group mapping
        createCdrMapForGrpRvwVar(wpIdList, wpCDRResults, cdrRvwVar);
      }

      // filter the wpId's already available in cache
      final Set<Long> wpIds2Fetch = new HashSet<Long>();
      for (Long fc2wp : wpIdList) {
        wpIds2Fetch.add(fc2wp);
      }

      // Step:2 - Get WP names (fc2wp type based config)
      // fetch ids not available in cache, this fetch method also adds to cached fc2wp's
      for (CDRReviewVariant cdrRvwVar : retSet) {
        // ICdm-729 new Method created for the Group mapping
        createCdrMapForFcWpRvwVar(wpCDRResults, cdrRvwVar);

      }
    }

    return wpCDRResults;
  }

  /**
   * @param wpIdList
   * @param wpCDRRvwVars
   * @param cdrResult // ICdm-729 new Method created for the Group mapping
   */
  private void createCdrMapForGrpRvwVar(final List<Long> wpIdList,
      final Map<String, SortedSet<CDRReviewVariant>> wpCDRRvwVars, final CDRReviewVariant cdrRvwVar) {
    CDRResult cdrResult = cdrRvwVar.getResult();


    if (cdrResult.getFC2WPID() == null) {
      String grpWrkPkgName = CDRConstants.CDR_SOURCE_TYPE.NOT_DEFINED.getTreeDispName();
      if ((cdrResult.getGroupWorkPackageName() != null) && !cdrResult.getGroupWorkPackageName().isEmpty() &&
          (CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE != cdrResult.getCDRSourceType())) {
        grpWrkPkgName = cdrResult.getGroupWorkPackageName();
      }

      if ((CDRConstants.CDR_SOURCE_TYPE.LAB_FILE == cdrResult.getCDRSourceType()) ||
          (CDRConstants.CDR_SOURCE_TYPE.FUN_FILE == cdrResult.getCDRSourceType()) ||
          (CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE == cdrResult.getCDRSourceType()) ||
          (CDRConstants.CDR_SOURCE_TYPE.A2L_FILE == cdrResult.getCDRSourceType()) ||
          (CDRConstants.CDR_SOURCE_TYPE.MONICA_FILE == cdrResult.getCDRSourceType()) ||
          (CDRConstants.CDR_SOURCE_TYPE.COMPLI_PARAM == cdrResult.getCDRSourceType())) {
        grpWrkPkgName = cdrResult.getCDRSourceType().getTreeDispName();
      }
      SortedSet<CDRReviewVariant> cdrRvwVars = wpCDRRvwVars.get(grpWrkPkgName);
      if (cdrRvwVars == null) {
        cdrRvwVars = new TreeSet<CDRReviewVariant>();

        wpCDRRvwVars.put(grpWrkPkgName, cdrRvwVars);
      }

      cdrRvwVars.add(cdrRvwVar);
    }
    else {
      wpIdList.add(cdrResult.getFC2WPID());
    }
  }

  /**
   * @param wpCDRResults
   * @param cdrResult // ICdm-729 new Method created for the Group mapping
   */
  private void createCdrMapForFcWp(final Map<String, SortedSet<CDRResult>> wpCDRResults, final CDRResult cdrResult) {
    if (CommonUtils.isNull(cdrResult.getFC2WPID())) {
      return;
    }

    String fc2WpName = CDRConstants.CDR_SOURCE_TYPE.NOT_DEFINED.getTreeDispName();
    if ((cdrResult.getFc2WpName() != null) &&
        (CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE != cdrResult.getCDRSourceType())) {
      fc2WpName = cdrResult.getFc2WpName();
    }
    if ((CDRConstants.CDR_SOURCE_TYPE.LAB_FILE == cdrResult.getCDRSourceType()) ||
        (CDRConstants.CDR_SOURCE_TYPE.FUN_FILE == cdrResult.getCDRSourceType()) ||
        (CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE == cdrResult.getCDRSourceType()) ||
        (CDRConstants.CDR_SOURCE_TYPE.A2L_FILE == cdrResult.getCDRSourceType()) ||
        (CDRConstants.CDR_SOURCE_TYPE.MONICA_FILE == cdrResult.getCDRSourceType()) ||
        (CDRConstants.CDR_SOURCE_TYPE.COMPLI_PARAM == cdrResult.getCDRSourceType())) {
      fc2WpName = cdrResult.getCDRSourceType().getTreeDispName();
    }
    SortedSet<CDRResult> cdrResultSet = wpCDRResults.get(fc2WpName);
    if (cdrResultSet == null) {
      cdrResultSet = new TreeSet<CDRResult>();
      wpCDRResults.put(fc2WpName, cdrResultSet);
    }
    cdrResultSet.add(cdrResult);

  }


  /**
   * @param wpCDRResults
   * @param cdrResult // ICdm-729 new Method created for the Group mapping
   */
  private void createCdrMapForFcWpRvwVar(final Map<String, SortedSet<CDRReviewVariant>> wpCDRResults,
      final CDRReviewVariant cdrRvwVar) {
    CDRResult cdrResult = cdrRvwVar.getResult();
    if (CommonUtils.isNull(cdrResult.getFC2WPID())) {
      return;
    }

    String fc2WpName = CDRConstants.CDR_SOURCE_TYPE.NOT_DEFINED.getTreeDispName();
    if ((cdrResult.getFc2WpName() != null) &&
        (CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE != cdrResult.getCDRSourceType())) {
      fc2WpName = cdrResult.getFc2WpName();
    }
    if ((CDRConstants.CDR_SOURCE_TYPE.LAB_FILE == cdrResult.getCDRSourceType()) ||
        ((CDRConstants.CDR_SOURCE_TYPE.FUN_FILE == cdrResult.getCDRSourceType()) ||
            (CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE == cdrResult.getCDRSourceType()) ||
            (CDRConstants.CDR_SOURCE_TYPE.A2L_FILE == cdrResult.getCDRSourceType())) ||
        (CDRConstants.CDR_SOURCE_TYPE.MONICA_FILE == cdrResult.getCDRSourceType()) ||
        (CDRConstants.CDR_SOURCE_TYPE.COMPLI_PARAM == cdrResult.getCDRSourceType())) {
      fc2WpName = cdrResult.getCDRSourceType().getTreeDispName();
    }
    SortedSet<CDRReviewVariant> cdrRvwVaSet = wpCDRResults.get(fc2WpName);
    if (cdrRvwVaSet == null) {
      cdrRvwVaSet = new TreeSet<CDRReviewVariant>();
      wpCDRResults.put(fc2WpName, cdrRvwVaSet);
    }
    cdrRvwVaSet.add(cdrRvwVar);

  }

  /**
   * @param wpIdList
   * @param wpCDRResults
   * @param cdrResult // ICdm-729 new Method created for the Group mapping
   */
  private void createCdrMapForGrp(final List<Long> wpIdList, final Map<String, SortedSet<CDRResult>> wpCDRResults,
      final CDRResult cdrResult) {
    if (cdrResult.getFC2WPID() == null) {
      String grpWrkPkgName = CDRConstants.CDR_SOURCE_TYPE.NOT_DEFINED.getTreeDispName();
      if ((cdrResult.getGroupWorkPackageName() != null) && !cdrResult.getGroupWorkPackageName().isEmpty() &&
          (CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE != cdrResult.getCDRSourceType())) {
        grpWrkPkgName = cdrResult.getGroupWorkPackageName();
      }

      if ((CDRConstants.CDR_SOURCE_TYPE.LAB_FILE == cdrResult.getCDRSourceType()) ||
          (CDRConstants.CDR_SOURCE_TYPE.FUN_FILE == cdrResult.getCDRSourceType()) ||
          (CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE == cdrResult.getCDRSourceType()) ||
          (CDRConstants.CDR_SOURCE_TYPE.A2L_FILE == cdrResult.getCDRSourceType()) ||
          (CDRConstants.CDR_SOURCE_TYPE.MONICA_FILE == cdrResult.getCDRSourceType()) ||
          (CDRConstants.CDR_SOURCE_TYPE.COMPLI_PARAM == cdrResult.getCDRSourceType())) {
        grpWrkPkgName = cdrResult.getCDRSourceType().getTreeDispName();
      }
      SortedSet<CDRResult> cdrResultSet = wpCDRResults.get(grpWrkPkgName);
      if (cdrResultSet == null) {
        cdrResultSet = new TreeSet<CDRResult>();

        wpCDRResults.put(grpWrkPkgName, cdrResultSet);
      }

      cdrResultSet.add(cdrResult);
    }
    else {
      wpIdList.add(cdrResult.getFC2WPID());
    }
  }

  /**
   * // iCDM-1366 <br>
   * Get all project specific rule sets
   */
  protected void loadAllRuleSets() {
    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    TypedQuery<TRuleSet> query = entMgr.createNamedQuery("TRuleSet.findAll", TRuleSet.class);
    List<TRuleSet> resultList = query.getResultList();
    // iterate the result list
    RuleSet ruleSet;
    for (TRuleSet dbRuleSet : resultList) {
      ruleSet = this.dataCache.getRuleSet(dbRuleSet.getRsetId());
      if (ruleSet == null) {
        ruleSet = new RuleSet(this.dataProvider, dbRuleSet.getRsetId()); // NOPMD by adn1cob on 6/30/14 10:17 AM
        // add it to all rule sets
        this.dataCache.getAllRuleSetMap().put(dbRuleSet.getRsetId(), ruleSet);
      }
    }
  }

  @Override
  public void refreshDataCache(final DisplayChangeEvent event) {
    if (event.isEmpty()) {
      return;
    }

    this.logger.debug("Refreshing data cache for the display change event ID : " + event.getEventID());

    final SortedSet<ChangedData> changedDataSet = new TreeSet<ChangedData>(event.getChangedData().values());
    CDREntityType itemType;

    for (ChangedData item : changedDataSet) {
      if (!(item.getEntityType() instanceof CDREntityType)) {
        continue;
      }
      if (getLogger().getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
        getLogger().debug("Processing Entity - id :" + item.getPrimaryKey() + "; type :" + item.getEntityType() +
            "; change type :" + item.getChangeType());
      }
      itemType = (CDREntityType) item.getEntityType();
      if (itemType == null) {
        // TODO create other entity types
        this.logger.warn("Entity :" + item + " skipped as entity not defined in  CDR EntityType");
        event.getChangedData().remove(Long.valueOf(item.getPrimaryKey()));
        continue;
      }
      // Capture the details of old object
      final AbstractCdrObject cdrObj = itemType.getDataObject(this.dataProvider, item.getPrimaryKey());

      // Icdm-881 For the Deleted Object When Trying to Redo Removing from the Cache
      if ((cdrObj == null) ||
          ((item.getChangeType() == ChangeType.DELETE) && (item.getEntityType() != CDREntityType.CDR_RESULT))) {
        this.logger.warn("CDR object :" + item + " skipped as CDR object not found");
        event.getChangedData().remove(Long.valueOf(item.getPrimaryKey()));
        continue;
      }

      if (item.getChangeType() == ChangeType.UPDATE) {
        item.setOldDataDetails(cdrObj.getObjectDetails());
      }

      refreshObject(event, itemType, item, cdrObj);
    }

  }

  /**
   * @param paramName paramName
   * @return the Cdr func Param Object. gets the CDR func param for the Parameter name
   */
  public CDRFuncParameter fetchCdrFuncParam(final String paramName) {
    // Icdm-1379 - Show Rules Editor
    CDRFuncParameter cdrFuncParam = null;
    List<TParameter> parameter = getParameter(paramName);
    if ((parameter != null) && !parameter.isEmpty()) {
      cdrFuncParam = new CDRFuncParameter(this.dataProvider, parameter.get(0).getId());
    }
    return cdrFuncParam;
  }

  /**
   * Refresh the entity and the DM object
   *
   * @param event DCE
   * @param itemType item type
   * @param item item
   * @param cdrObj DM object
   */
  private void refreshObject(final DisplayChangeEvent event, final CDREntityType itemType, final ChangedData item,
      final AbstractCdrObject cdrObj) {
    // Refresh the entity in the entity manager and data model
    final Object entity = this.entProvider.getEm().find(item.getEntityClass(), item.getPrimaryKey());

    // TODO Refresh Data Objects

    if (item.getChangeType() == ChangeType.UPDATE) {
      // Refresh is not executed for delete as it will throw a EntityNotFound exception. Inserted records are not
      // identified by EclipseLink directly
      // TODO check these conditions for Undo operations

      final long oldVersion = itemType.getVersion(this.dataProvider, item.getPrimaryKey());
      this.entProvider.refreshCacheObject(entity);
      final long newVersion = itemType.getVersion(this.dataProvider, item.getPrimaryKey());
      if (oldVersion == newVersion) {
        event.getChangedData().remove(Long.valueOf(item.getPrimaryKey()));
        return;
      }
    }
    // Icdm-881 Extra Check for null
    if (cdrObj != null) {
      cdrObj.refresh();
    }
  }

  /**
   * Fetch the list of parameters with the given paramId
   *
   * @param paramId paramId
   * @return list of TParameter
   */
  public List<TParameter> getParameter(final Long paramId) {
    String query = "SELECT p FROM TParameter p where p.id = " + paramId;
    return fetchParameter(query);
  }


  /**
   * Fetch the list of parameters with the given name
   *
   * @param paramName Name
   * @return list of TParameter
   */
  public List<TParameter> getParameter(final String paramName) {
    String query = "SELECT p FROM TParameter p where p.name = '" + paramName + "'";
    return fetchParameter(query);
  }

  private List<TParameter> fetchParameter(final String statement) {
    getLogger().debug("fetching parameters from database ...");

    final TypedQuery<TParameter> qParameters = getEntityProvider().getEm().createQuery(statement, TParameter.class);
    List<TParameter> dbParams = qParameters.getResultList();

    getLogger().debug("parameters from database : " + dbParams.size());

    return dbParams;
  }

  /**
   * Fetch function details for the function name, This method is to be protected and only called from DataCache
   *
   * @param funcName function name
   * @param includeRel
   * @return CDRFunction
   */
  /* iCDM-471 */
  protected CDRFunction fetchFunctionDetails(final String funcName, final boolean includeRel) {

    String query = "SELECT fnc.id,fnc.name from TFunction fnc where  fnc.upperName = '" +
        funcName.toUpperCase(Locale.GERMAN) + "'";

    if (includeRel) {
      query = query + " and fnc.relevantName='Y'";
    }

    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    final Query typeQuery = entMgr.createQuery(query);
    typeQuery.setHint(ApicConstants.READ_ONLY, "true");

    int idIndex = 0;

    int nameIndex = 1;

    final Object[] funcInfo = (Object[]) typeQuery.getSingleResult();
    final Long funcId = (Long) funcInfo[idIndex];
    final String name = (String) funcInfo[nameIndex];
    final CDRFunction cdrFunction = new CDRFunction(this.dataProvider, funcId, name);
    this.dataProvider.getDataCache().getAllCDRFunctions().put(name, cdrFunction);

    return cdrFunction;
  }

  /**
   * Fetch function details for the param name, This method is to be protected and only called from DataCache
   *
   * @param paramName function name
   * @return CDRFunction
   */
  /* iCDM-471 */
  protected List<CDRFunction> fetchFunctions(final String paramName) {

    List<CDRFunction> funcList = new ArrayList<>();

    final String query =
        "SELECT distinct (tFunction) from TFunctionversion funcVer,TFunction tFunction where tFunction.relevantName='Y' and tFunction.upperName=funcVer.funcNameUpper and funcVer.defcharname= '" +
            paramName + "'";

    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    final TypedQuery<TFunction> typeQuery = entMgr.createQuery(query, TFunction.class);
    typeQuery.setHint(ApicConstants.READ_ONLY, "true");
    typeQuery.setHint(ApicConstants.FETCH_SIZE, "2000");
    final List<TFunction> resultList = typeQuery.getResultList();

    for (TFunction tFunction : resultList) {
      final CDRFunction cdrFunction = new CDRFunction(this.dataProvider, tFunction.getId(), tFunction.getName());
      funcList.add(cdrFunction);
      this.dataProvider.getDataCache().getAllCDRFunctions().put(paramName, cdrFunction);
    }

    return funcList;
  }

  /**
   * Fetch all versions of the CDR fuction
   *
   * @param funcName function name
   * @return map of function versions
   */
  protected Map<String, CDRFuncVersion> fetchFunctionVersions(final String funcName) {

    final Map<String, CDRFuncVersion> versionsMap = new ConcurrentHashMap<String, CDRFuncVersion>();

    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    final TypedQuery<TFuncversUnique> typeQuery =
        entMgr.createNamedQuery(TFuncversUnique.NQ_GET_VERS_BY_FUNCNAME, TFuncversUnique.class);

    typeQuery.setParameter("fname", funcName.toUpperCase(Locale.GERMAN));

    typeQuery.setHint(ApicConstants.FETCH_SIZE, "2000");
    typeQuery.setHint(ApicConstants.SHARED_CACHE, "true");

    final List<TFuncversUnique> funcVerList = typeQuery.getResultList();

    CDRFunction function = this.dataProvider.getDataCache().getAllCDRFunctions().get(funcName);
    CDRFuncVersion funcVersion;
    for (TFuncversUnique dbFuncVer : funcVerList) {
      funcVersion = new CDRFuncVersion(function, dbFuncVer.getFuncversion());
      versionsMap.put(dbFuncVer.getFuncversion(), funcVersion);
    }
    return versionsMap;
  }

  /**
   * Fetch all parameters of this function or function version
   *
   * @param funcName function name
   * @param version version
   * @return param map
   * @throws SsdInterfaceException
   */
  protected Map<String, CDRFuncParameter> fetchParameters(final String funcName, final String version)
      throws SsdInterfaceException {
    // Build query string
    String query = CommonUtils.concatenate(
        "SELECT DISTINCT(funcVer.defcharname), param from TFunctionversion funcVer, TParameter param  where  funcVer.defcharname = param.name and funcVer.funcNameUpper = '",
        funcName.toUpperCase(Locale.GERMAN), '\'');
    if (version != null) {
      query = CommonUtils.concatenate(query, " and funcVer.funcversion = '", version, '\'');
    }
    return getResultParams(query);
  }

  /**
   * ICDM-1324 Required for CDR-Rule Importer (Plausibel-Importer)
   *
   * @return param map
   * @throws SsdInterfaceException
   */
  protected Map<String, CDRFuncParameter> fetchCDRParameters(final String paramName) throws SsdInterfaceException {

    final String query =
        "SELECT DISTINCT(funcVer.funcname), param from TFunctionversion funcVer, TParameter param  where funcVer.defcharname = param.name and param.name = " +
            "'" + paramName + "'";
    final Map<String, CDRFuncParameter> paramMap = new ConcurrentHashMap<String, CDRFuncParameter>();
    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    final TypedQuery<Object[]> typeQuery = entMgr.createQuery(query, Object[].class);
    typeQuery.setHint(ApicConstants.FETCH_SIZE, "15000");
    final List<Object[]> resultList = typeQuery.getResultList();

    String functionName = null;
    TParameter dbParam;
    CDRFuncParameter cdrParam;

    for (Object[] resObj : resultList) {
      // first obj is param name
      functionName = (String) resObj[0];
      // second obj is param entity
      dbParam = (TParameter) resObj[1];
      // Check if this parameter and type is already available in collection of all params
      cdrParam = getDataCache().getCDRFuncParameter(dbParam.getName(), dbParam.getPtype());
      // if not found, create the obj
      if (cdrParam == null) {
        cdrParam = new CDRFuncParameter(this.dataProvider, dbParam.getId());
        // add it to all params map
        this.dataProvider.getDataCache().addCDRFuncParameter(cdrParam);
      }
      // add param to the return set for this function
      paramMap.put(functionName, cdrParam);
    }
    // Fetch the Rules for all the Params
    fetchRules(functionName, null);


    // Build query string
    return paramMap;
  }

  /**
   * Fetch all rules from SSD of this function or function version
   *
   * @param funcName function name
   * @param version version
   * @throws SsdInterfaceException
   */
  public void fetchRules(final String funcName, final String version) throws SsdInterfaceException {

    final Map<String, List<CDRRule>> rulesMap =
        this.dataProvider.getApicDataProvider().getSsdServiceHandler().readReviewRule(funcName, version);
    if (!rulesMap.isEmpty()) {
      this.dataProvider.getApicDataProvider().addParamCDRRules(rulesMap);
    }
  }

  /**
   * @param paramNames param names for fetching the rules
   * @throws SsdInterfaceException
   */
  public void fetchRules(final Set<String> paramNames) throws SsdInterfaceException {
    List<String> paramList = new ArrayList<>(paramNames);
    final Map<String, List<CDRRule>> rulesMap =
        this.dataProvider.getApicDataProvider().getSsdServiceHandler().readReviewRule(paramList);
    if (!rulesMap.isEmpty()) {
      this.dataProvider.getApicDataProvider().addParamCDRRules(rulesMap);
    }
  }

  // iCDM-845
  /**
   * Fetch all parameters of this function VARIANT or function ALTERNATIVE
   *
   * @param funcName function name
   * @param version version
   * @param byVariant 'TRUE' to search by variant, 'FALSE' to search for alternative
   * @return CDRFuncParameters
   * @throws SsdInterfaceException
   */
  protected Map<String, CDRFuncParameter> fetchParameters(final String funcName, final String version,
      final boolean byVariant)
      throws SsdInterfaceException {
    final StringBuilder query = new StringBuilder(PARAM_BUFFER_SIZE);
    // Build query string
    query.append(
        "SELECT DISTINCT(funcVer.defcharname), param from TFunctionversion funcVer, TParameter param  where   funcVer.defcharname = param.name and funcVer.funcNameUpper = '")
        .append(funcName.toUpperCase(Locale.GERMAN)).append('\'');
    if (version != null) {
      if (byVariant) {
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
    return getResultParams(query.toString());
  }

  /**
   * Get Result parameters
   *
   * @param query query to execute
   * @param funcName function name
   * @param version version
   * @return CDRFuncParameters
   * @throws SsdInterfaceException
   */
  private Map<String, CDRFuncParameter> getResultParams(final String query) throws SsdInterfaceException {
    final Map<String, CDRFuncParameter> paramMap = new ConcurrentHashMap<String, CDRFuncParameter>();
    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    final TypedQuery<Object[]> typeQuery = entMgr.createQuery(query, Object[].class);
    typeQuery.setHint(ApicConstants.FETCH_SIZE, "15000");
    final List<Object[]> resultList = typeQuery.getResultList();

    String paramName;
    TParameter dbParam;
    CDRFuncParameter cdrParam;

    for (Object[] resObj : resultList) {
      // first obj is param name
      paramName = (String) resObj[0];
      // second obj is param entity
      dbParam = (TParameter) resObj[1];
      // Check if this parameter and type is already available in collection of all params
      cdrParam = getDataCache().getCDRFuncParameter(paramName, dbParam.getPtype());
      // if not found, create the obj
      if (cdrParam == null) {
        cdrParam = new CDRFuncParameter(this.dataProvider, dbParam.getId());
        // add it to all params map
        this.dataProvider.getDataCache().addCDRFuncParameter(cdrParam);
      }
      // add param to the return set for this function
      paramMap.put(dbParam.getName(), cdrParam);
    }
    // Consolidate the function versions

    fetchRules(paramMap.keySet());


    return paramMap;
  }

  /**
   * Fetch all CDR Functions for the functionID list
   *
   * @param funcIdSet Set of func id's
   * @return sorted set of CDR function
   */
  // iCDM-501
  protected SortedSet<CDRFunction> fetchCDRFunctions(final Set<Long> funcIdSet) {

    final SortedSet<CDRFunction> funcSet = new TreeSet<CDRFunction>();
    // Empty list would cause error in executing query
    if ((funcIdSet == null) || funcIdSet.isEmpty()) {
      return funcSet;
    }
    // TODO: to check if there a limit for IN clause.. Normally JPA itself would handle this case..?

    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();

    final TypedQuery<TFunction> typeQuery =
        entMgr.createQuery("SELECT fnc from TFunction fnc where fnc.id IN :funcIDs ", TFunction.class);
    typeQuery.setHint(ApicConstants.READ_ONLY, "true");
    typeQuery.setHint(ApicConstants.FETCH_SIZE, "2000");
    typeQuery.setHint(ApicConstants.SHARED_CACHE, "true");
    typeQuery.setParameter("funcIDs", funcIdSet);

    final List<TFunction> resultList = typeQuery.getResultList();
    CDRFunction cdrFunction;
    for (TFunction dbFunc : resultList) {
      cdrFunction = this.dataProvider.getDataCache().getAllCDRFunctions().get(dbFunc.getName());
      if (cdrFunction == null) {
        cdrFunction = new CDRFunction(this.dataProvider, dbFunc.getId(), dbFunc.getName());
        this.dataProvider.getDataCache().getAllCDRFunctions().put(dbFunc.getName(), cdrFunction);
      }
      funcSet.add(cdrFunction);
    }
    return funcSet;
  }

  /**
   * Fetch all CDR functions for the search text (function name starting with)
   *
   * @param startsWith function name starting with
   * @return CDRFunctions set
   */
  // iCDM-501
  protected SortedSet<CDRFunction> searchCDRFunctions(final String startsWith) {
    final SortedSet<CDRFunction> searchResultSet = new TreeSet<CDRFunction>();
    String searchText = startsWith;
    // ICDM-853 replace wildcard chars with % for querying
    if (searchText.contains("*")) {
      searchText = searchText.replaceAll("\\*", "\\%");
    }
    getLogger().debug("The function name to be fetched from db " + searchText);
    // Enable case in-sensitive search
    final String query = "SELECT fnc from TFunction fnc where fnc.relevantName='Y' and fnc.upperName like '" +
        searchText.toUpperCase(Locale.GERMAN) + "%'";

    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();

    final TypedQuery<TFunction> typeQuery = entMgr.createQuery(query, TFunction.class);
    typeQuery.setHint(ApicConstants.READ_ONLY, "true");
    typeQuery.setHint(ApicConstants.FETCH_SIZE, "2000");
    typeQuery.setHint(ApicConstants.SHARED_CACHE, "true");
    final List<TFunction> resultList = typeQuery.getResultList();
    CDRFunction cdrFunction;
    for (TFunction dbFunc : resultList) {
      cdrFunction = this.dataProvider.getDataCache().getAllCDRFunctions().get(dbFunc.getName());
      if (cdrFunction == null) {
        cdrFunction = new CDRFunction(this.dataProvider, dbFunc.getId(), dbFunc.getName());
        this.dataProvider.getDataCache().getAllCDRFunctions().put(dbFunc.getName(), cdrFunction);
      }
      searchResultSet.add(cdrFunction);
    }
    getLogger().debug("No of functions fetched " + searchResultSet.size());
    return searchResultSet;
  }

  /**
   * fetch the attr dependency to a parameter
   *
   * @param paramSet parameters
   * @return map which contains paramter attr for a particular parameter
   */

  protected Map<Long, Set<ParameterAttribute>> fetchParameterAttrDepn(final Set<CDRFuncParameter> paramSet) {


    getLogger().debug("fetching attr dependencies for a parameter");
    Map<Long, String> cdrFuncParamMap = new ConcurrentHashMap<Long, String>();
    for (CDRFuncParameter cdrFuncParameter : paramSet) {
      cdrFuncParamMap.put(cdrFuncParameter.getID(), cdrFuncParameter.getName());
    }
    if (!cdrFuncParamMap.isEmpty()) {
      if (cdrFuncParamMap.size() <= ApicConstants.JPA_IN_CLAUSE_LIMIT) {
        final Query qDbattrDepn = getEntityProvider().getEm().createQuery(
            "SELECT pattr FROM TParamAttr pattr where pattr.TParameter.id in :paramList", TParamAttr.class);
        qDbattrDepn.setHint(ApicConstants.FETCH_SIZE, "500");
        qDbattrDepn.setHint(ApicConstants.READ_ONLY, "true");
        qDbattrDepn.setParameter("paramList", cdrFuncParamMap.keySet());
        @SuppressWarnings("unchecked")
        final List<TParamAttr> resultList = qDbattrDepn.getResultList();

        setParamDepns(resultList);
      } // For more than 1000 Values we need the temp table
      else {
        try {
          this.dataProvider.getEntityProvider().startTransaction();
          GttObjectName tempParam;
          // Delete the existing records in this temp table, if any
          final Query delQuery = this.dataProvider.getEntityProvider().getEm().createQuery(DELETE_GLB_TEMP_QUERY);
          delQuery.executeUpdate();

          // Create entities for all the functions
          for (Entry<Long, String> cdrFuncParamMapEntry : cdrFuncParamMap.entrySet()) {
            tempParam = new GttObjectName();
            tempParam.setId(cdrFuncParamMapEntry.getKey());
            tempParam.setObjName(cdrFuncParamMapEntry.getValue());
            this.dataProvider.getEntityProvider().registerNewEntity(tempParam);
          }

          this.dataProvider.getEntityProvider().getEm().flush();
          final TypedQuery<TParamAttr> qDbattrDepn = getEntityProvider().getEm().createQuery(
              "SELECT pattr FROM TParamAttr pattr,GttObjectName temp where temp.id=pattr.TParameter.id",
              TParamAttr.class);
          qDbattrDepn.setHint(ApicConstants.FETCH_SIZE, "2000");

          final List<TParamAttr> resultList = qDbattrDepn.getResultList();
          setParamDepns(resultList);

          delQuery.executeUpdate();

          this.dataProvider.getEntityProvider().commitChanges();
        }
        catch (Exception ex) {
          getLogger().error(ex.getMessage(), ex);
        }
        finally {
          this.dataProvider.getEntityProvider().commitChanges();
          this.dataProvider.getEntityProvider().endTransaction();
        }

      }
    }
    return this.dataProvider.getDataCache().getParamDependencyMap();
  }

  // ICDM-1584
  /**
   * This method uses IN clause in select query when the number of parameters <= 1000. Otherwise, TParamAttr is joined
   * with TParameter and TFunctionversion to fetch the Parameter attributes.</br>
   * </br>
   * The use of JOIN in query improves the performance for functions with large number of parameters(eg:DFES).But for
   * smaller functions, the IN clause shows better performance. In the JOIN query, TFunctionversion table which even
   * though contain a huge amount of data is used because this provides better performance.</br>
   * <b>For eg:</b> while fetching the parameter attribute dependencies for DFES function which contains around 3 lakh
   * parameters the JOIN query prevents insertion and deletion of 3 lakh entries by not using TempTable as is done in
   * the previous method
   *
   * @param paramSet Set<CDRFuncParameter>
   * @param functionName String
   * @return Map<Long, Set<ParameterAttribute>>
   */
  protected Map<Long, Set<ParameterAttribute>> fetchParameterAttrDepn(final Set<CDRFuncParameter> paramSet,
      final String functionName, final String funcVer) {
    getLogger().debug("fetching attr dependencies for a parameter");

    Map<Long, String> cdrFuncParamMap = new ConcurrentHashMap<Long, String>();
    for (CDRFuncParameter cdrFuncParameter : paramSet) {
      cdrFuncParamMap.put(cdrFuncParameter.getID(), cdrFuncParameter.getName());
    }
    if (!cdrFuncParamMap.isEmpty()) {
      if (cdrFuncParamMap.size() <= ApicConstants.JPA_IN_CLAUSE_LIMIT) {
        final Query qDbattrDepn = getEntityProvider().getEm().createQuery(
            "SELECT pattr FROM TParamAttr pattr where pattr.TParameter.id in :paramList", TParamAttr.class);
        qDbattrDepn.setHint(ApicConstants.FETCH_SIZE, "500");
        qDbattrDepn.setHint(ApicConstants.READ_ONLY, "true");
        qDbattrDepn.setParameter("paramList", cdrFuncParamMap.keySet());
        @SuppressWarnings("unchecked")
        final List<TParamAttr> resultList = qDbattrDepn.getResultList();

        setParamDepns(resultList);
      } // For more than 1000 parameters JOIN query is used
      else {
        String query =
            "SELECT distinct(pattr) FROM TParamAttr pattr join pattr.TParameter param,TFunctionversion funver" +
                " where pattr.TParameter.name = funver.defcharname and funver.funcNameUpper = :functionName";
        if (funcVer != null) {
          query = query + " and funver.funcversion=:funcVer";
        }
        final Query qDbattrDepn = getEntityProvider().getEm().createQuery(query, TParamAttr.class);
        qDbattrDepn.setHint(ApicConstants.FETCH_SIZE, "500");
        qDbattrDepn.setHint(ApicConstants.READ_ONLY, "true");
        qDbattrDepn.setParameter("functionName", functionName.toUpperCase(Locale.GERMAN));
        if (funcVer != null) {
          qDbattrDepn.setParameter("funcVer", funcVer);
        }
        @SuppressWarnings("unchecked")
        final List<TParamAttr> resultList = qDbattrDepn.getResultList();
        setParamDepns(resultList);
      }
    }
    return this.dataProvider.getDataCache().getParamDependencyMap();
  }

  /**
   * @param resultList
   */
  private void setParamDepns(final List<TParamAttr> resultList) {
    ParameterAttribute paramAttr;

    for (TParamAttr dbPAttr : resultList) {
      paramAttr = this.dataProvider.getDataCache().getParamAttrMap().get(dbPAttr.getParamAttrId());
      if (paramAttr == null) {
        paramAttr = new ParameterAttribute(this.dataProvider, dbPAttr.getParamAttrId());
        this.dataProvider.getDataCache().getParamAttrMap().put(dbPAttr.getParamAttrId(), paramAttr);
      }
      if (this.dataProvider.getDataCache().getParamDependencyMap().containsKey(paramAttr.getParameter().getID())) {
        Set<ParameterAttribute> setOfAttrsFrmMap =
            this.dataProvider.getDataCache().getParamDependencyMap().get(paramAttr.getParameter().getID());
        setOfAttrsFrmMap.add(paramAttr);
        this.dataProvider.getDataCache().getParamDependencyMap().put(paramAttr.getParameter().getID(),
            setOfAttrsFrmMap);
      }
      else {
        Set<ParameterAttribute> setOfAttrs = new TreeSet<ParameterAttribute>();
        setOfAttrs.add(paramAttr);
        this.dataProvider.getDataCache().getParamDependencyMap().put(paramAttr.getParameter().getID(), setOfAttrs);
      }
    }
  }

  /**
   * Icdm-801 Query for fetching the Function names and adding it to cache
   *
   * @param unavialFuns unavialFuns
   * @return the FunctionSet
   */
  public Set<String> fetchFunctionNames(final List<String> unavialFuns) {
    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();

    Set<String> cdrFuncSet = new TreeSet<String>();
    if ((!unavialFuns.isEmpty()) && (unavialFuns.size() <= ApicConstants.JPA_IN_CLAUSE_LIMIT)) {
      final TypedQuery<TFunction> typeQuery =
          entMgr.createQuery("SELECT fnc FROM TFunction fnc  where fnc.name in :funList", TFunction.class);
      typeQuery.setHint(ApicConstants.FETCH_SIZE, "500");
      typeQuery.setHint(ApicConstants.READ_ONLY, "true");
      typeQuery.setParameter("funList", unavialFuns);

      final List<TFunction> funcList = typeQuery.getResultList();
      fillFunctionNames(cdrFuncSet, funcList);
    }
    else if (!unavialFuns.isEmpty()) {
      try {
        this.dataProvider.getEntityProvider().startTransaction();
        GttObjectName tempParam;
        long recID = 1;
        // Delete the existing records in this temp table, if any
        final Query delQuery = entMgr.createQuery(DELETE_GLB_TEMP_QUERY);
        delQuery.executeUpdate();

        // Create entities for all the functions
        for (String fun : unavialFuns) {
          tempParam = new GttObjectName();
          tempParam.setId(recID);
          tempParam.setObjName(fun);
          this.dataProvider.getEntityProvider().registerNewEntity(tempParam);
          recID++;
        }

        entMgr.flush();


        final TypedQuery<TFunction> typeQuery = entMgr.createQuery(
            "SELECT fnc FROM TFunction fnc,GttObjectName temp  where temp.objName=fnc.name", TFunction.class);
        typeQuery.setHint(ApicConstants.FETCH_SIZE, "2000");

        final List<TFunction> funcList = typeQuery.getResultList();
        fillFunctionNames(cdrFuncSet, funcList);
        delQuery.executeUpdate();
        this.dataProvider.getEntityProvider().commitChanges();
      }
      catch (Exception ex) {
        getLogger().error(ex.getMessage(), ex);
      }
      finally {
        this.dataProvider.getEntityProvider().commitChanges();
        this.dataProvider.getEntityProvider().endTransaction();
      }

    }
    return cdrFuncSet;

  }

  /**
   * Icdm-801 Query for fetching the Function names and adding it to cache
   *
   * @param cdrFuncSet
   * @param funcList
   */
  private void fillFunctionNames(final Set<String> cdrFuncSet, final List<TFunction> funcList) {
    for (TFunction func : funcList) {
      final CDRFunction cdrFunction = new CDRFunction(this.dataProvider, func.getId(), func.getName());
      this.dataProvider.getDataCache().getAllCDRFunctions().put(func.getName(), cdrFunction);
      cdrFuncSet.add(cdrFunction.getName());
    }
  }

  /**
   * Icdm-801 Query for fetching the param names and adding it to cache
   *
   * @param labelList param names
   * @return the label name list
   */
  public Set<String> fetchLabelNames(final List<String> labelList) {
    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    Set<String> labelNameSet = new TreeSet<String>();
    // Here the Inclause can handle upto 1000 values
    if ((!labelList.isEmpty()) && (labelList.size() <= ApicConstants.JPA_IN_CLAUSE_LIMIT)) {
      final TypedQuery<TParameter> typeQuery =
          entMgr.createQuery("SELECT param FROM TParameter param  where param.name in :labList", TParameter.class);
      typeQuery.setHint(ApicConstants.FETCH_SIZE, "500");
      typeQuery.setHint(ApicConstants.READ_ONLY, "true");
      typeQuery.setParameter("labList", labelList);

      final List<TParameter> paramList = typeQuery.getResultList();
      fillLabelNames(labelNameSet, paramList);
    }
    // For more than 1000 Values we need the temp table
    else if (!labelList.isEmpty()) {

      try {
        this.dataProvider.getEntityProvider().startTransaction();
        GttObjectName tempParam;
        long recID = 1;
        // Delete the existing records in this temp table, if any
        final Query delQuery = entMgr.createQuery(DELETE_GLB_TEMP_QUERY);
        delQuery.executeUpdate();

        // Create entities for all the functions
        for (String label : labelList) {
          tempParam = new GttObjectName();
          tempParam.setId(recID);
          tempParam.setObjName(label);
          this.dataProvider.getEntityProvider().registerNewEntity(tempParam);
          recID++;
        }

        entMgr.flush();
        final TypedQuery<TParameter> typeQuery = entMgr.createQuery(
            "SELECT param FROM TParameter param,GttObjectName temp  where temp.objName=param.name", TParameter.class);
        typeQuery.setHint(ApicConstants.FETCH_SIZE, "2000");

        final List<TParameter> paramList = typeQuery.getResultList();
        fillLabelNames(labelNameSet, paramList);

        delQuery.executeUpdate();

        this.dataProvider.getEntityProvider().commitChanges();
      }
      catch (Exception ex) {
        getLogger().error(ex.getMessage(), ex);
      }
      finally {
        this.dataProvider.getEntityProvider().commitChanges();
        this.dataProvider.getEntityProvider().endTransaction();
      }
    }
    return labelNameSet;
  }


  /**
   * Icdm-1451 Query for fetching the Cdr Func Parametr and adding it to the cache.
   *
   * @param labelList param names
   * @param sortWithID if true, sorted only using ID, otherwise uses default sorting for parameters
   * @return the label name list
   */
  public SortedSet<CDRFuncParameter> fetchFuncParams(final List<String> labelList, final boolean sortWithID) {
    SortedSet<CDRFuncParameter> funcParamSet = new TreeSet<>(new Comparator<CDRFuncParameter>() {

      @Override
      public int compare(final CDRFuncParameter param1, final CDRFuncParameter param2) {
        if (sortWithID) {
          return ApicUtil.compare(param1.getID(), param2.getID());
        }
        // Default sorting
        return ApicUtil.compare(param1, param2);
      }
    });

    for (TParameter tParameter : fetchTParameters(labelList)) {
      CDRFuncParameter funcParam = new CDRFuncParameter(this.dataProvider, tParameter.getId());
      funcParamSet.add(funcParam);
      this.dataCache.addCDRFuncParameter(funcParam);
    }

    return funcParamSet;
  }

  /**
   * Fetch TParameter with the parameter names
   *
   * @param paramNameColln list of names
   * @return TParameter list
   */
  private List<TParameter> fetchTParameters(final Collection<String> paramNameColln) {
    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    List<TParameter> retList = new ArrayList<>();

    try {
      this.dataProvider.getEntityProvider().startTransaction();
      GttObjectName tempParam;
      long recID = 1;
      // Delete the existing records in this temp table, if any
      final Query delQuery = entMgr.createQuery(DELETE_GLB_TEMP_QUERY);
      delQuery.executeUpdate();

      // Create entities for all the functions
      for (String paramName : paramNameColln) {
        tempParam = new GttObjectName();
        tempParam.setId(recID);
        tempParam.setObjName(paramName);
        this.dataProvider.getEntityProvider().registerNewEntity(tempParam);
        recID++;
      }

      entMgr.flush();

      final TypedQuery<TParameter> typeQuery = entMgr.createQuery(
          "SELECT param FROM TParameter param,GttObjectName temp  where temp.objName=param.name", TParameter.class);
      typeQuery.setHint(ApicConstants.FETCH_SIZE, "2000");

      // Retrieved entities are added to another collection, to ensure that they are fetched.
      retList.addAll(typeQuery.getResultList());

      delQuery.executeUpdate();
    }
    catch (Exception exp) {
      getLogger().error(exp.getMessage(), exp);
    }
    finally {
      this.dataProvider.getEntityProvider().commitChanges();
      this.dataProvider.getEntityProvider().endTransaction();
    }

    return retList;
  }

  /**
   * Fetch CDRFuncParameter with the parameter names
   *
   * @param paramNameList list of parameter names
   * @return Set of CDRFuncParameter
   */
  public Set<CDRFuncParameter> fetchFuncParams(final Collection<String> paramNameList) {
    Set<CDRFuncParameter> funcParamSet = new HashSet<>();
    for (TParameter tParameter : fetchTParameters(paramNameList)) {
      CDRFuncParameter funcParam = new CDRFuncParameter(this.dataProvider, tParameter.getId());
      funcParamSet.add(funcParam);
      this.dataCache.addCDRFuncParameter(funcParam);
    }
    return funcParamSet;
  }

  /**
   * Icdm-801 Query for fetching the param names and adding it to cache
   *
   * @param labelNameSet
   * @param paramList
   */
  private void fillLabelNames(final Set<String> labelNameSet, final List<TParameter> paramList) {
    for (TParameter dbParam : paramList) {
      final CDRFuncParameter cdrParam = new CDRFuncParameter(this.dataProvider, dbParam.getId());
      labelNameSet.add(cdrParam.getName());
    }
  }

  /**
   * Icdm-1315- Query to get all Varaint coded Params
   *
   * @param firstName base param name
   * @param lastName lastName
   * @return the List of param Id's for the base Parameter
   */
  public List<Long> fetchAllVarCodedParam(final String firstName, final String lastName) {

    List<Long> paramIds = new ArrayList<>();

    getLogger().debug("Fetch all the parameters for the base parameter  " + firstName);
    // Enable case in-sensitive search
    String query = "SELECT param from TParameter param where UPPER(param.name) like '" +
        firstName.toUpperCase(Locale.GERMAN) + "[" + "%" + lastName.toUpperCase(Locale.GERMAN);
    if (lastName.length() > 0) {
      query = CommonUtils.concatenate(query, "%'");
    }
    else {
      query = CommonUtils.concatenate(query, "'");
    }

    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();

    final TypedQuery<TParameter> typeQuery = entMgr.createQuery(query, TParameter.class);
    typeQuery.setHint(ApicConstants.READ_ONLY, "true");
    typeQuery.setHint(ApicConstants.FETCH_SIZE, "2000");
    typeQuery.setHint(ApicConstants.SHARED_CACHE, "true");
    final List<TParameter> resultList = typeQuery.getResultList();
    for (TParameter dbParam : resultList) {
      paramIds.add(dbParam.getId());
    }
    getLogger().debug("No of Variant coded parameters fetched " + paramIds.size());
    return paramIds;

  }


  /**
   * @return the logger
   */
  private ILoggerAdapter getLogger() {
    return this.dataProvider.getLogger();
  }

  /**
   * @return the data Cache
   */
  private CDRDataCache getDataCache() {
    return this.dataProvider.getDataCache();
  }


  /**
   * @return the Entity Provider
   */
  private CDREntityProvider getEntityProvider() {
    return this.dataProvider.getEntityProvider();
  }

  /**
   * Fetch rule set rules
   *
   * @param paramNames list of params
   * @param ssdNodeId ssdNodeId
   * @return List<CDRRule>
   * @throws SsdInterfaceException
   */
  protected Map<String, List<CDRRule>> fetchRuleSetRules(final List<String> paramNames, final Long ssdNodeId)
      throws SsdInterfaceException {
    return this.dataProvider.getApicDataProvider().getSsdServiceHandler().readReviewRule(paramNames, ssdNodeId);
  }

  /**
   * @param paramNameStr String parameter name
   * @param funcNameStr String function name
   * @param ruleSetId Long RuleSet Id
   * @return List<Map<CDRFuncParameter, CDRFunction>> List of map with key as parameter and value as function
   */
  public List<List<Object>> searchParameters(final String paramNameStr, final String funcNameStr,
      final Long ruleSetId) {
    // ICDM-1380
    List<List<Object>> paramFuncList = new ArrayList<>();
    // create strings that start with the given characters
    String funcNameLike = CommonUtils.convertStringForDbQuery(funcNameStr) + "%";


    String paramNameLike = CommonUtils.convertStringForDbQuery(paramNameStr) + "%";
    getLogger().debug(
        "Fetch all the parameters which contains " + paramNameStr + " and function name which contains " + funcNameStr);

    String query =
        "select DISTINCT fun.name, param.id, param.name, param.longname,param.longname_ger,param.ptype from TFunction fun, TFunctionversion funver, TParameter param where fun.name = funver.funcname and param.name = funver.defcharname and UPPER(fun.name) like :funcName and UPPER(param.name) like :paramName and param.name not like '%[%' and param.id not in (select rule_set_params.TParameter.id from TRuleSetParam rule_set_params where rule_set_params.TRuleSet.rsetId = :rset_id)";


    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();

    Query createQuery = entMgr.createQuery(query);
    createQuery.setParameter("funcName", funcNameLike.toUpperCase(Locale.ENGLISH));
    createQuery.setParameter("paramName", paramNameLike.toUpperCase(Locale.ENGLISH));
    createQuery.setParameter("rset_id", ruleSetId);
    createQuery.setMaxResults(Integer
        .parseInt(this.dataProvider.getApicDataProvider().getParameterValue(ApicConstants.DB_SEARCH_MAX_RESULT_SIZE)));
    createQuery.setHint(ApicConstants.READ_ONLY, "true");
    createQuery.setHint(ApicConstants.SHARED_CACHE, "true");
    final List<Object> resultList = createQuery.getResultList();
    List<Object> singleParamList;
    Object[] values;

    for (Object dbParam : resultList) {
      values = (Object[]) dbParam;
      singleParamList = new ArrayList<Object>();
      for (Object value : values) {
        singleParamList.add(value);
      }
      paramFuncList.add(singleParamList);
    }
    return paramFuncList;
  }

  /**
   * Method to get the parameter names which has dependencies
   *
   * @param baseParamSet baseParamSet
   * @return the param name list which have dep
   */
  public Set<String> fetchParamsWithDep(final Set<String> baseParamSet) {
    // Icdm-1255- Variant coded Parameter
    Set<String> paramNameWithDep = new HashSet<>();
    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    try {
      this.dataProvider.getEntityProvider().startTransaction();
      GttObjectName tempParam;
      long recID = 1;
      // Delete the existing records in this temp table, if any
      final Query delQuery = entMgr.createQuery(DELETE_GLB_TEMP_QUERY);
      delQuery.executeUpdate();

      // Create entities for all the functions
      for (String label : baseParamSet) {
        tempParam = new GttObjectName();
        tempParam.setId(recID);
        tempParam.setObjName(label);
        this.dataProvider.getEntityProvider().registerNewEntity(tempParam);
        recID++;
      }

      entMgr.flush();
      final TypedQuery<String> typeQuery = entMgr.createQuery(
          "SELECT temp.objName FROM GttObjectName temp,TParamAttr paramAttr  where temp.objName=paramAttr.TParameter.name ",
          String.class);
      typeQuery.setHint(ApicConstants.FETCH_SIZE, "2000");


      paramNameWithDep.addAll(typeQuery.getResultList());


      delQuery.executeUpdate();

      this.dataProvider.getEntityProvider().commitChanges();
    }
    catch (Exception ex) {
      getLogger().error(ex.getMessage(), ex);
    }
    finally {
      this.dataProvider.getEntityProvider().commitChanges();
      this.dataProvider.getEntityProvider().endTransaction();
    }
    return paramNameWithDep;
  }

  /**
   * Method to create the Func Param map for a collection of Char objects
   *
   * @param paramNameList paramNameList
   * @return the map of Func param
   */
  public Map<String, CDRFuncParameter> fetchFuncParamMap(final Collection<Characteristic> paramNameList) {
    Map<String, CDRFuncParameter> funcParamMap = new ConcurrentHashMap<>();
    String key;
    for (TParameter tParameter : fetchFunctionParams(paramNameList)) {
      CDRFuncParameter funcParam = new CDRFuncParameter(this.dataProvider, tParameter.getId());
      // create the key using the Func Param name and type
      key = CommonUtils.concatenate(funcParam.getName(), funcParam.getType());
      funcParamMap.put(key, funcParam);
      this.dataCache.addCDRFuncParameter(funcParam);
    }
    return funcParamMap;
  }

  /**
   * @param paramNameList
   * @return
   */
  private List<TParameter> fetchFunctionParams(final Collection<Characteristic> paramNameList) {
    GttParameter tempParam;
    long recID = 1;
    List<TParameter> resultList = null;
    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    try {
      this.dataProvider.getEntityProvider().startTransaction();
      // Delete the existing records in this temp table, if any
      final Query delQuery = entMgr.createQuery("delete from GttParameter temp");
      delQuery.executeUpdate();

      // Create entities for all the parameters
      for (Characteristic chr : paramNameList) {
        tempParam = new GttParameter();
        tempParam.setId(recID);
        tempParam.setParamName(chr.getName());
        tempParam.setType(chr.getType());
        entMgr.persist(tempParam);
        recID++;
      }

      entMgr.flush();

      // Execute the select Query using the GTT.
      final TypedQuery<TParameter> query = entMgr.createQuery(
          "SELECT param FROM TParameter param,GttParameter temp  where temp.paramName=param.name and temp.type=param.ptype",
          TParameter.class);
      query.setHint(ApicConstants.FETCH_SIZE, "2000");

      // get the result set
      resultList = query.getResultList();


      // Delete the existing records in this temp table, after data retrieval
      delQuery.executeUpdate();
    }
    catch (Exception exp) {
      getLogger().error(exp.getMessage(), exp);
    }
    finally {
      this.dataProvider.getEntityProvider().commitChanges();
      this.dataProvider.getEntityProvider().endTransaction();
    }
    return resultList;
  }

  /**
   * @return the Questionares .
   */
  public SortedSet<Questionnaire> getAllQuestionares() {

    final SortedSet<Questionnaire> quesSet = new TreeSet<Questionnaire>();

    // TODO: to check if there a limit for IN clause.. Normally JPA itself would handle this case..?

    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();

    final TypedQuery<TQuestionnaire> typeQuery =
        entMgr.createNamedQuery("TQuestionnaire.findAll", TQuestionnaire.class);
    typeQuery.setHint(ApicConstants.READ_ONLY, "true");
    typeQuery.setHint(ApicConstants.FETCH_SIZE, "2000");
    typeQuery.setHint(ApicConstants.SHARED_CACHE, "true");

    final List<TQuestionnaire> resultList = typeQuery.getResultList();
    Questionnaire questionnaire;
    for (TQuestionnaire dbquestionnaire : resultList) {
      questionnaire = this.dataProvider.getDataCache().getQuestionnaireMap().get(dbquestionnaire.getQnaireId());
      if (questionnaire == null) {
        questionnaire = new Questionnaire(this.dataProvider, dbquestionnaire.getQnaireId());
        this.dataProvider.getDataCache().getQuestionnaireMap().put(dbquestionnaire.getQnaireId(), questionnaire);
      }
      quesSet.add(questionnaire);
    }
    return quesSet;
  }

  /**
   * @param pidcVer pidcVer
   * @return the sorted set of rvw varaint for a pidc version
   */
  public List<CDRReviewVariant> getCDRReviewVar(final PIDCVersion pidcVer) {
    final String query =
        "select rvwVar from TRvwVariant rvwVar where rvwVar.TRvwResult.TPidcA2l.TPidcVersion.pidcVersId  = " +
            pidcVer.getID();
    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    final TypedQuery<TRvwVariant> typeQuery = entMgr.createQuery(query, TRvwVariant.class);
    typeQuery.setHint(ApicConstants.FETCH_SIZE, "100");
    final List<TRvwVariant> resultList = typeQuery.getResultList();

    CDRReviewVariant cdrRvwVar;
    final List<CDRReviewVariant> retSet = new ArrayList<CDRReviewVariant>();
    for (TRvwVariant dbRes : resultList) {
      cdrRvwVar = new CDRReviewVariant(this.dataProvider, dbRes.getRvwVarId());
      retSet.add(cdrRvwVar);
    }
    return retSet;

  }

  /**
   * Load all the workpackage divisions
   */
  public void loadWpDivsions() {
    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    final TypedQuery<TWorkpackageDivision> typeQuery =
        entMgr.createNamedQuery("TWorkpackageDivision.findAll", TWorkpackageDivision.class);
    typeQuery.setHint(ApicConstants.READ_ONLY, "true");
    typeQuery.setHint(ApicConstants.SHARED_CACHE, "true");

    final List<TWorkpackageDivision> resultList = typeQuery.getResultList();
    getDataCache().setIcdmWpDivsLoaded(true);
    WorkPackageDivision wpDivision;
    for (TWorkpackageDivision dbIcdmWorkPackage : resultList) {
      wpDivision = new WorkPackageDivision(this.dataProvider, dbIcdmWorkPackage.getWpDivId());
      addToMappedWpMap(wpDivision);
    }
  }

  /**
   * @param wpDivision
   */
  public void addToMappedWpMap(final WorkPackageDivision wpDivision) {
    AttributeValue division = wpDivision.getDivision();
    List<IcdmWorkPackage> mappedWps = this.dataProvider.getDataCache().getMappedWrkPkgMap().get(division);
    if (mappedWps == null) {
      mappedWps = new ArrayList<IcdmWorkPackage>();
      mappedWps.add(wpDivision.getWorkPackage());
      this.dataProvider.getDataCache().getMappedWrkPkgMap().put(division, mappedWps);
    }
    else {
      mappedWps.add(wpDivision.getWorkPackage());
    }
  }

  /**
   * Load all the workpackages
   */
  public void loadIcdmWorkPackages() {

    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    final TypedQuery<TWorkpackage> typeQuery = entMgr.createNamedQuery("TWorkpackage.findAll", TWorkpackage.class);
    typeQuery.setHint(ApicConstants.READ_ONLY, "true");
    typeQuery.setHint(ApicConstants.SHARED_CACHE, "true");

    final List<TWorkpackage> resultList = typeQuery.getResultList();
    getDataCache().setIcdmWpLoaded(true);
    for (TWorkpackage dbIcdmWorkPackage : resultList) {
      new IcdmWorkPackage(this.dataProvider, dbIcdmWorkPackage.getWpId());
    }
  }

  /*
   * @return details of quesiotnnaire s attached to variant
   */
  /**
   * Get the details of variants with questionnaire responses attached
   *
   * @param pidcVers PIDC Version
   * @param refresh if true, refreshes the data
   * @return details of variants
   */
  // ICDM-2404
  public SortedSet<String[]> getQNaireVariants(final PIDCVersion pidcVers, final boolean refresh) {

    SortedSet<String[]> qnaireVarNodeSet = getDataCache().getQnaireVariantDet(pidcVers);
    if ((qnaireVarNodeSet == null) || refresh) {
      qnaireVarNodeSet = new TreeSet<String[]>(new Comparator<String[]>() {

        /**
         * {@inheritDoc}
         */
        @Override
        public int compare(final String[] item1, final String[] item2) {
          return ApicUtil.compare(item1[ApicConstants.COLUMN_INDEX_2], item2[ApicConstants.COLUMN_INDEX_2]);
        }
      });

      loadAllQnaireResponses(pidcVers);

      buildVirtualQnaireVariantNodes(pidcVers, qnaireVarNodeSet);

      // ICDM-2332
      getDataCache().setQnaireVariantDet(pidcVers, qnaireVarNodeSet);
    }
    return qnaireVarNodeSet;
  }

  /**
   * Creates the q.naire variant nodes
   *
   * @param pidcVers PIDC Version
   * @param retSet SortedSet of String[]
   */
  private void buildVirtualQnaireVariantNodes(final PIDCVersion pidcVers, final SortedSet<String[]> qnaireVarNodeSet) {
    String[] item;
    if (pidcVers.hasVariants(true)) {
      // ICDM-2160
      for (Entry<Long, PIDCVariant> entry : pidcVers.getVariantsMap(false).entrySet()) {
        if (!getDataCache().getProjQnaireResponseMap(entry.getValue()).isEmpty()) {
          item = new String[TREENODE_ARRAY_SIZE]; // NOPMD Instantiation is required here
          item[ApicConstants.COLUMN_INDEX_0] = CDRConstants.NODE_TYPE_QNAIRE_VARIANTS;
          item[ApicConstants.COLUMN_INDEX_1] = String.valueOf(entry.getKey());
          item[ApicConstants.COLUMN_INDEX_2] = entry.getValue().getName();
          qnaireVarNodeSet.add(item);
        }
      }
    }
    // iCDM-1982
    if (!getDataCache().getProjQnaireResponseMap(pidcVers).isEmpty()) {
      item = new String[TREENODE_ARRAY_SIZE]; // NOPMD Instantiation is required here
      item[ApicConstants.COLUMN_INDEX_0] = CDRConstants.NODE_TYPE_QNAIRE_VARIANTS;
      item[ApicConstants.COLUMN_INDEX_1] = String.valueOf(pidcVers.getID());
      item[ApicConstants.COLUMN_INDEX_2] = ApicConstants.DUMMY_VAR_NODE_NOVAR;
      qnaireVarNodeSet.add(item);
    }
  }

  /**
   * Load all questionnaire responses in the given pidc version
   *
   * @param pidcVers pidc version
   */
  // ICDM-2404
  private void loadAllQnaireResponses(final PIDCVersion pidcVers) {

    getLogger().debug("Loading all questionionnaire responses for PIDC Version {} ...", pidcVers.getID());

//    Set<TRvwQnaireResponse> dbResponseSet =
//        getEntityProvider().getDbPIDCVersion(pidcVers.getID()).getTRvwQnaireResponses();

    AbstractProjectObject<?> projObj;
    QuestionnaireResponse resp;

//    for (TRvwQnaireResponse dbResponse : dbResponseSet) {
//      if (dbResponse.getTabvProjectVariant() == null) {
//        // Project version level response
//        projObj = pidcVers;
//      }
//      else {
//        // Variant
//        projObj = pidcVers.getVariantsMap().get(dbResponse.getTabvProjectVariant().getVariantId());
//      }
//
//      // Special handling of general questionnaires
//      if (isGeneralType(dbResponse.getTQuestionnaireVersion().getTQuestionnaire().getQnaireId())) {
//        setProjGeneralQnaireVersion(dbResponse, projObj);
//        continue;
//      }
//
//      // Normal quesitonnaires responses
//      Long respID = dbResponse.getQnaireRespId();
//      resp = getDataCache().getQuestionnaireResponse(respID);
//      if (resp == null) {
//        resp = new QuestionnaireResponse(this.dataProvider, respID);
//        getDataCache().addRemoveProjQnaireResponse(projObj, resp, true);
//      }
//
//    }
//    getLogger().debug("Loading questionnaire responses for PIDC Version = {} completed. No.of responses = {}",
//        pidcVers.getID(), dbResponseSet.size());
  }

  /**
   * checks whether the given qusestionnaire id belongs to the general questionnaire
   *
   * @param qnaireID Questionnnaire ID
   * @return true, if this is a general questionnaire
   */
  // ICDM-2404
  private boolean isGeneralType(final Long qnaireID) {
    Questionnaire genQnaire = this.dataProvider.getDataCache().getGeneralQuestionnaire();
    return genQnaire == null ? false : CommonUtils.isEqual(genQnaire.getID(), qnaireID);
  }

  /**
   * Set the general questionnaire version of the project, from the questionnaire response(general type)
   *
   * @param dbResponse database object
   * @param projObj project object
   */
  // ICDM-2404
  private void setProjGeneralQnaireVersion(final TRvwQnaireResponse dbResponse,
      final AbstractProjectObject<?> projObj) {
//    Long genQnaireVersID = dbResponse.getTQuestionnaireVersion().getQnaireVersId();
//    QuestionnaireVersion genQnaireVersion = getDataCache().getQuestionnaireVersion(genQnaireVersID);
//    if (genQnaireVersion == null) {
//      genQnaireVersion = new QuestionnaireVersion(this.dataProvider, genQnaireVersID);
//    }
//    getDataCache().setRemoveProjGeneralQnaireVers(projObj, genQnaireVersion);
  }

  /**
   * @param wpDivsWithQs
   * @param qsWithoutActiveVersions
   * @return
   */
  public SortedSet<QuestionnaireVersion> getQuestionnaireVersions(final List<Long> wpDivsWithQs,
      final SortedSet<Questionnaire> qsWithoutActiveVersions) {
    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    final TypedQuery<TQuestionnaireVersion> typeQuery =
        entMgr.createNamedQuery(TQuestionnaireVersion.NQ_GET_QNVERS_FOR_WPDIV, TQuestionnaireVersion.class);

    typeQuery.setHint(ApicConstants.FETCH_SIZE, "100");
    typeQuery.setParameter("wpDivIds", wpDivsWithQs);

    final List<TQuestionnaireVersion> qNaireVersions = typeQuery.getResultList();

    SortedSet<QuestionnaireVersion> existingQuestVersions = new TreeSet<QuestionnaireVersion>();
    QuestionnaireVersion qNaireVerBO;
    for (TQuestionnaireVersion qNaireVersion : qNaireVersions) {
      qNaireVerBO = getDataCache().getQuestionnaireVersion(qNaireVersion.getQnaireVersId());
      if (qNaireVerBO == null) {
        qNaireVerBO = new QuestionnaireVersion(this.dataProvider, qNaireVersion.getQnaireVersId());
      }
      if (qNaireVerBO.getQuestionnaire().getAllVersions().size() == 1) {
        if (!qNaireVerBO.isActiveVersion()) {
          qsWithoutActiveVersions.add(qNaireVerBO.getQuestionnaire());
        }
      }
      else if (qNaireVerBO.isActiveVersion()) {
        existingQuestVersions.add(qNaireVerBO);
      }

    }
    return existingQuestVersions;
  }

  /**
   * @param funcName
   * @param fetchNonRel
   * @return
   */
  public List<CDRFunction> getCDRFunList(final String funcName) {

    List<CDRFunction> funcList = new ArrayList<>();
    final String query = "SELECT fnc.id,fnc.name from TFunction fnc where    fnc.upperName = '" +
        funcName.toUpperCase(Locale.GERMAN) + "'";

    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    final Query typeQuery = entMgr.createQuery(query);
    typeQuery.setHint(ApicConstants.READ_ONLY, "true");

    int idIndex = 0;

    int nameIndex = 1;

    final List<Object[]> funcInfo = typeQuery.getResultList();

    for (Object[] obj : funcInfo) {
      final Long funcId = (Long) obj[idIndex];
      final String name = (String) obj[nameIndex];
      final CDRFunction cdrFunction = new CDRFunction(this.dataProvider, funcId, name);
      this.dataProvider.getDataCache().getAllCDRFunctions().put(name, cdrFunction);
      funcList.add(cdrFunction);
    }


    return funcList;
  }

  /**
   * @param divValId divValId
   * @param inputWps inputWps
   * @return the map of work package and value as resp name
   */
  public Map<IcdmWorkPackage, String> getWorkRespMap(final Long divValId, final SortedSet<IcdmWorkPackage> inputWps) {
    Map<IcdmWorkPackage, String> workPkgRspMap = new ConcurrentHashMap<>();
    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    TypedQuery<TWorkpackageDivision> tQuery =
        entMgr.createNamedQuery(TWorkpackageDivision.NQ_FIND_BY_DIV_ID, TWorkpackageDivision.class);
    tQuery.setParameter("divValueId", divValId);
    for (TWorkpackageDivision dbWpDiv : tQuery.getResultList()) {
      long wpId = dbWpDiv.getTWorkpackage().getWpId();
      for (IcdmWorkPackage icdmWorkPackage : inputWps) {

        if (icdmWorkPackage.getID().equals(wpId)) {
          String resourceCode = "";
          if ((dbWpDiv.getTWpResource() != null) && (dbWpDiv.getTWpResource().getResourceCode() != null)) {
            resourceCode = dbWpDiv.getTWpResource().getResourceCode();
          }
          workPkgRspMap.put(icdmWorkPackage, resourceCode);
        }
      }

    }

    return workPkgRspMap;

  }
}

