/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.caltool.icdm.common.exception.SsdInterfaceException;
import com.bosch.caltool.icdm.database.entity.apic.GttFuncparam;
import com.bosch.caltool.icdm.database.entity.apic.GttObjectName;
import com.bosch.caltool.icdm.database.entity.cdr.TFunction;
import com.bosch.caltool.icdm.database.entity.cdr.TParameter;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.CDRRulesWithFile;
import com.bosch.ssd.icdm.model.FeatureValueModel;
import com.bosch.ssd.icdm.model.SSDRelease;


/**
 * New data loader having method to create the Function set and Param set from a list of functions and params as input
 *
 * @author rgo7cob
 */
public class CdrReviewDataLoader {

  /**
   * param name index position in query
   */
  private static final int INDEX_OF_PARAM_NAME = 1;
  /**
   * function name index position in query
   */
  private static final int INDEX_OF_FUN_NAME = 2;
  /**
   * cdr data provider
   */
  private final CDRDataProvider dataProvider;

  /**
   * apicDataProvider
   *
   * @param dataProvider apicDataProvider
   */
  public CdrReviewDataLoader(final CDRDataProvider dataProvider) {
    super();
    this.dataProvider = dataProvider;

  }


  /**
   * Create CDRFunction objects for a given set of function names
   *
   * @param funNames funNames
   * @param b
   * @return the Functions set
   */
  public SortedSet<CDRFunction> fetchFunctions(final Set<String> funNames, final boolean hasUnassiged) {

    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    final SortedSet<CDRFunction> cdrFuncSet = new TreeSet<CDRFunction>();

    final Set<String> uncachedFunctions = uncachedFunctions(funNames, cdrFuncSet);


    if (!uncachedFunctions.isEmpty() && (uncachedFunctions.size() <= ApicConstants.JPA_IN_CLAUSE_LIMIT)) {
      String query = "SELECT fnc FROM TFunction fnc  where fnc.name in :funList ";
      if (hasUnassiged) {
        query = query + " or fnc.name='<NOT_ASSIGNED>'";
      }
      final TypedQuery<TFunction> typeQuery = entMgr.createQuery(query, TFunction.class);
      typeQuery.setHint(ApicConstants.FETCH_SIZE, "500");
      typeQuery.setHint(ApicConstants.READ_ONLY, "true");
      typeQuery.setParameter("funList", uncachedFunctions);

      final List<TFunction> funcList = typeQuery.getResultList();

      fillFunctions(cdrFuncSet, funcList);
    }
    else if (uncachedFunctions.size() >= ApicConstants.JPA_IN_CLAUSE_LIMIT) {
      try {
        this.dataProvider.getEntityProvider().startTransaction();
        GttObjectName tempParam;
        long recID = 1;
        // Delete the existing records in this temp table, if any
        final Query delQuery = entMgr.createQuery("delete from GttObjectName temp");
        delQuery.executeUpdate();

        // Create entities for all the functions
        for (String fun : uncachedFunctions) {
          tempParam = new GttObjectName();
          tempParam.setId(recID);
          tempParam.setObjName(fun);
          this.dataProvider.getEntityProvider().registerNewEntity(tempParam);
          recID++;
        }

        entMgr.flush();

        String query = "SELECT fnc FROM TFunction fnc  where fnc.name in :funList ";
        if (hasUnassiged) {
          query = query + " or fnc.name='<NOT_ASSIGNED>'";
        }
        final TypedQuery<TFunction> typeQuery = entMgr.createQuery(query, TFunction.class);
        typeQuery.setHint(ApicConstants.FETCH_SIZE, "2000");

        final List<TFunction> funcList = typeQuery.getResultList();

        fillFunctions(cdrFuncSet, funcList);

        delQuery.executeUpdate();
      }
      finally {
        this.dataProvider.getEntityProvider().commitChanges();
        this.dataProvider.getEntityProvider().endTransaction();
      }

    }
    else {
      for (String funcName : funNames) {
        CDRFunction cdrFunction = this.dataProvider.getDataCache().getAllCDRFunctions().get(funcName);
        cdrFuncSet.add(cdrFunction);
      }
    }
    return cdrFuncSet;
  }


  /**
   * return the set of uncached functions
   *
   * @param funNames
   * @param cdrFuncSet
   * @return
   */
  private Set<String> uncachedFunctions(final Set<String> funNames, final SortedSet<CDRFunction> cdrFuncSet) {

    final Set<String> uncachedFunctions = new TreeSet<String>();
    for (String funName : funNames) {
      if (this.dataProvider.getDataCache().getAllCDRFunctions().get(funName) == null) {
        uncachedFunctions.add(funName);
      }
      else {
        cdrFuncSet.add(this.dataProvider.getDataCache().getAllCDRFunctions().get(funName));
      }
    }
    return uncachedFunctions;
  }


  /**
   * create the functions and add it to the cache
   *
   * @param cdrFuncSet
   * @param funcList
   */
  private void fillFunctions(final Set<CDRFunction> cdrFuncSet, final List<TFunction> funcList) {
    for (TFunction func : funcList) {
      final CDRFunction cdrFunction = new CDRFunction(this.dataProvider, func.getId(), func.getName());
      this.dataProvider.getDataCache().getAllCDRFunctions().put(func.getName(), cdrFunction);
      cdrFuncSet.add(cdrFunction);
    }
  }


  /**
   * create CDRFuncParam Objects
   *
   * @param charSet charSet
   * @return the Map of CDRFuncParams
   */
  public Map<Long, Set<CDRFuncParameter>> fetchFuncParams(final Set<Characteristic> charSet) {

    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    final Map<Long, Set<CDRFuncParameter>> rvwFunParamMap = new ConcurrentHashMap<>();
    try {
      this.dataProvider.getEntityProvider().startTransaction();
      GttFuncparam tempfuncParam;
      long recID = 1;
      // Delete the existing records in this temp table, if any
      final Query delQuery = entMgr.createQuery("delete from GttFuncparam temp");
      delQuery.executeUpdate();

      // Create entities for all the functions
      for (Characteristic character : charSet) {
        tempfuncParam = new GttFuncparam();
        tempfuncParam.setId(recID);
        tempfuncParam.setFunName(character.getDefFunction().getName());
        tempfuncParam.setParamName(character.getName());
        tempfuncParam.setType(character.getType());
        this.dataProvider.getEntityProvider().registerNewEntity(tempfuncParam);
        recID++;
      }

      entMgr.flush();


      loadCDRParams(entMgr, rvwFunParamMap);
      delQuery.executeUpdate();
    }
    finally {
      this.dataProvider.getEntityProvider().commitChanges();
      this.dataProvider.getEntityProvider().endTransaction();
    }
    return rvwFunParamMap;
  }


  /**
   * @param entMgr
   * @param rvwFunParamMap
   */
  private void loadCDRParams(final EntityManager entMgr, final Map<Long, Set<CDRFuncParameter>> rvwFunParamMap) {

    final String query =
        "SELECT DISTINCT(funcVer.defcharname), param,temp.funName from TFunctionversion funcVer, TParameter param,GttFuncparam temp  " +
            " where funcVer.defcharname = param.name and temp.paramName=param.name and " +
            " temp.funName=funcVer.funcname and param.ptype=temp.type";


    final TypedQuery<Object[]> typeQuery = entMgr.createQuery(query, Object[].class);
    typeQuery.setHint(ApicConstants.FETCH_SIZE, "15000");

    final List<Object[]> resultList = typeQuery.getResultList();

    TParameter dbParam;
    String funName;
    CDRFuncParameter cdrParam;

    for (Object[] resObj : resultList) {
      // second obj is param entity
      dbParam = (TParameter) resObj[INDEX_OF_PARAM_NAME];
      // Function name
      funName = (String) resObj[INDEX_OF_FUN_NAME];
      // Check if this parameter and type is already available in collection of all params
      // if not found, create the obj
      cdrParam = new CDRFuncParameter(this.dataProvider, dbParam.getId());
      final CDRFunction cdrFunction = this.dataProvider.getDataCache().getAllCDRFunctions().get(funName);

      Set<CDRFuncParameter> funcParamSet = rvwFunParamMap.get(cdrFunction.getID());
      if (funcParamSet == null) {
        funcParamSet = new TreeSet<>();
        rvwFunParamMap.put(cdrFunction.getID(), funcParamSet);
      }
      funcParamSet.add(cdrParam);
      // add it to all params map
      this.dataProvider.getDataCache().addCDRFuncParameter(cdrParam);
      // add param to the return set for this function

    }
  }

  /**
   * ICDM-1066- Data model Changes
   *
   * @param labels set of labels
   * @param fetureValModelSet featureValModel Set
   * @return the Cdr Rules with the File path
   * @throws SsdInterfaceException
   */
  public CDRRulesWithFile createSSDRulesFileWithDep(final Set<String> labels,
      final Set<FeatureValueModel> fetureValModelSet)
      throws SsdInterfaceException {
    // get the cdr rules with the file path for the normal rules
    final CDRRulesWithFile cdrRuleWithFile =
        this.dataProvider.getApicDataProvider().getSsdServiceHandler().readRulesandGetSSDFileDependency(
            new ArrayList<>(labels), new ArrayList<FeatureValueModel>(fetureValModelSet), null);
    // return the rules
    return cdrRuleWithFile;
  }

  /**
   * ICDM-1066- Data model Changes
   *
   * @param labels set of labels
   * @param fetureValModelSet featureValModel Set
   * @param nodeId nodeId
   * @return the Cdr Rules with the File path
   * @throws SsdInterfaceException
   */
  public CDRRulesWithFile createSSDRulesFileWithDepForRuleSet(final Set<String> labels,
      final Set<FeatureValueModel> fetureValModelSet, final Long nodeId)
      throws SsdInterfaceException {
    // get the cdr rules with the file path for the rule set
    final CDRRulesWithFile cdrRuleWithFile =
        this.dataProvider.getApicDataProvider().getSsdServiceHandler().readRulesandGetSSDFileDpndyForNode(
            new ArrayList<>(labels), new ArrayList<FeatureValueModel>(fetureValModelSet), nodeId, null);
    // return the rules
    return cdrRuleWithFile;
  }


  /**
   * @param ssdSoftwareVersionID
   * @return the list of ssd releases
   */
  public List<SSDRelease> getSSDRelesesBySwVersionId(final Long ssdSoftwareVersionID) throws Exception {
    return this.dataProvider.getApicDataProvider().getSsdServiceHandler()
        .getSSDRelesesBySwVersionId(ssdSoftwareVersionID);
  }


  /**
   * @param releaseId releaseId
   * @return the rule list
   */
  public List<CDRRule> readRuleForRelease(final BigDecimal releaseId) throws Exception {

    return this.dataProvider.getApicDataProvider().getSsdServiceHandler().readRuleForRelease(releaseId);
  }


  /**
   * @param path path
   * @param releaseId releaseId
   * @return the report path.
   */
  public String getReleaseReportsByReleaseId(final String path, final BigDecimal releaseId) throws Exception {
    return this.dataProvider.getApicDataProvider().getSsdServiceHandler().getReleaseReportsByReleaseId(path, releaseId);
  }


}
