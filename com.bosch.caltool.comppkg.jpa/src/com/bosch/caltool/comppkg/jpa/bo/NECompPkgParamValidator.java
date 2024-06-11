/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.jpa.bo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.cdr.jpa.bo.CDRFuncParameter;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.bo.caldataimport.ICalDataImportParamDetailsLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.GttParameter;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportData;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.comppkg.CPConstants;


/**
 * Class to load and validate the parameter details of the NE type component package
 *
 * @author bne4cob
 */
@Deprecated
public class NECompPkgParamValidator implements ICalDataImportParamDetailsLoader {

  /**
   * Query string Buffer size
   */
  private static final int SB_QUERY_SIZE = 100;

  /**
   * component package
   */
  private final CompPkg compPkg;

  /**
   * Set of invalid parameters identified
   */
  private final Set<String> invalidParamSet = new HashSet<>();

  /**
   * Properties of parameters retrieved. <br>
   * Key - parameter name <br>
   * Value - Map of &ltproperty, value as string&gt
   */
  private final Map<String, Map<String, String>> paramPropMap = new ConcurrentHashMap<>();

  /**
   * key- parameter name, value- object in temp table
   */
  Map<String, GttParameter> tempObjsMap = new HashMap<>();

  /**
   * Map of param name and type
   */
  private final Map<String, String> paramNameTypeMap = new HashMap<>();

  /**
   * Constructor
   *
   * @param compPkg component package
   */
  public NECompPkgParamValidator(final CompPkg compPkg) {
    this.compPkg = compPkg;
  }

  /**
   * Identify the parameters in the given input, that are not part of the given component package
   *
   * @throws DataException if base component mapping is invalid
   */
  // @Override
  // public void run(final Set<String> paramNameSet, final String funcName) throws DataException {
  //
  // getLogger().debug("Fetching details of input parameters ...");
  //
  // validateBCMapping();
  //
  // EntityManager entMgr = null;
  //
  // try {
  // // Create new Entity Manager for this transaction. To avoid Concurrent Modification exception.
  // entMgr = ObjectStore.getInstance().getEntityManagerFactory().createEntityManager();
  // entMgr.getTransaction().begin();
  //
  // // Insert the input parameters into temp table for easy query
  // populateTempTable(paramNameSet, entMgr);
  // // Fetch the parameter properties
  // findParamProps(entMgr);
  // // Identifiy the invalid parameters in the input
  // findInvalidParams(paramNameSet);
  //
  // // Delete the existing records in this temp table, after data retrieval
  // cleanupTempTable(entMgr);
  //
  // entMgr.getTransaction().commit();
  // }
  // catch (Exception exp) {
  // if (entMgr != null) {
  // entMgr.getTransaction().rollback();
  // }
  //
  // throw exp;
  // }
  // finally {
  // // Close the entity Manager.
  // try {
  // if (entMgr != null) {
  // entMgr.close();
  // }
  // }
  // catch (Exception exp) {
  // getLogger().error(exp.getMessage(), exp);
  // }
  // }
  // // if none of the parameters in the input file is part of the comp pkg then throw error
  // if (this.invalidParamSet.size() == paramNameSet.size()) {
  // throw new DataException(
  // "No parameters in the given input file is part of the component package - " + this.compPkg.getName());
  // }
  //
  // }

  /**
   * Remove all items in invalidParamSet from GttParameter
   *
   * @param entMgr EntityManager
   */
  private void clearInvalidParamsFromTemp(final EntityManager entMgr) {
    for (String invalidParam : this.invalidParamSet) {
      // Using the map<String, GttParameter>, call entMgr.delete(GttParamter)
      entMgr.remove(this.tempObjsMap.get(invalidParam));
    }
    // call entMgr.flush() aftger all delete() statements.
    entMgr.flush();
  }

  /**
   * fetching paramter properties
   *
   * @param entMgr EntityManager
   */
  private void fetchParamProps(final EntityManager entMgr) {
    // Fetch ID, class, Codeword, Name, Type, Bitwise flag using a join query between TParamter, and gttparameter
    // joining "Name, Type"
    StringBuilder queryStr = new StringBuilder(SB_QUERY_SIZE);
    queryStr.append(
        "SELECT param.id FROM t_parameter param,gtt_parameters temp  where temp.param_name=param.name and temp.type=param.ptype");
    final Query query = entMgr.createNativeQuery(queryStr.toString());
    for (Object item : query.getResultList()) {
      // call CdrDataProvider.getcdrfuncparameter(Long) to create the CDRFunctionparameter object
      this.compPkg.getDataProvider().getCdrDataProvider().getCDRFuncParameter(Long.parseLong((String) item));
    }
  }

  /**
   * @throws DataException
   */
  private void validateBCMapping() throws DataException {
    Map<Long, CompPkgBc> bcMap = this.compPkg.getCompPkgBcsMap();

    if (bcMap.isEmpty()) {
      throw new DataException("No base components mapped to the component package " + this.compPkg.getName());
    }

    if (bcMap.size() > CPConstants.NECP_BC_COUNT) {
      throw new DataException(
          "Only ONE base component should be mapped to NE type component package " + this.compPkg.getName());
    }
  }


  /**
   * Fetch the parameter properties
   */
  private void findParamProps(final EntityManager entMgr) {

    getLogger().debug("Fetching properties of valid parameters ...");

    this.paramPropMap.clear();
    final Query query = entMgr.createNativeQuery(paramPropQuery());
    query.setHint(ApicConstants.FETCH_SIZE, "500");

    for (Object item : query.getResultList()) {
      if (item instanceof Object[]) {
        Object[] itemArr = (Object[]) item;

        // Param properties can be null. Cannot be changed to CHM
        Map<String, String> propMap = new HashMap<>();
        propMap.put(CDRConstants.CDIKEY_PARAM_NAME, itemArr[0].toString());
        propMap.put(CDRConstants.CDIKEY_FUNCTION_NAME, itemArr[1].toString());
        CDRFuncParameter cdrFuncParam =
            this.compPkg.getDataProvider().getCdrDataProvider().fetchCDRFuncParam(itemArr[0].toString());
        propMap.put(CDRConstants.CDIKEY_PARAM_CLASS,
            cdrFuncParam.getpClass() == null ? "" : cdrFuncParam.getpClass().getText());
        propMap.put(CDRConstants.CDIKEY_CODE_WORD, cdrFuncParam.getCodeWord());
        this.paramNameTypeMap.put(cdrFuncParam.getName(), cdrFuncParam.getType());
        propMap.put(CDRConstants.CDIKEY_LONG_NAME, cdrFuncParam.getLongName());
        propMap.put(CDRConstants.CDIKEY_CAL_HINT, cdrFuncParam.getParamHint());
        propMap.put(CDRConstants.CDIKEY_BIT_WISE, cdrFuncParam.getBitWiseRule());
        this.paramPropMap.put(itemArr[0].toString(), propMap);
      }
    }

    getLogger().debug("No. of records retrieved = " + this.paramPropMap.size());

  }


  /**
   * Fetches the parameter properties. Transaction is managed outside this method.
   *
   * @param inputParamSet
   * @param entMgr map to which the properties are added (returned)
   * @param invalidParamQuery
   */
  private void findInvalidParams(final Set<String> inputParamSet) {

    getLogger().debug("Finding invalid parameters that are not part of component package - " + this.compPkg.getID());

    this.invalidParamSet.clear();
    this.invalidParamSet.addAll(inputParamSet);
    this.invalidParamSet.removeAll(this.paramPropMap.keySet());

    getLogger().debug("Invalid paramters identified = " + this.invalidParamSet.size());

  }

  /**
   * @param inputParamSet
   * @param entMgr
   */
  private void populateTempTable(final Set<String> inputParamSet, final EntityManager entMgr) {

    cleanupTempTable(entMgr);

    GttParameter tempParam;
    long recID = 1;

    // Create entities for all the parameters
    for (String param : inputParamSet) {
      tempParam = new GttParameter();
      tempParam.setId(recID);
      tempParam.setParamName(param);

      entMgr.persist(tempParam);
      recID++;

      // Also store it to a map<String, GttParameter>
      this.tempObjsMap.put(param, tempParam);
    }

    entMgr.flush();
  }

  /**
   * Cleans the temp tables used
   */
  private void cleanupTempTable(final EntityManager entMgr) {

    getLogger().debug("Clearing temp table ...");

    // Delete the existing records in this temp table, if any
    final Query delQuery = entMgr.createQuery("delete from GttParameter temp");
    delQuery.executeUpdate();
  }


  /**
   * @return the query to fetch the parameter properties(function name)
   */
  private String paramPropQuery() {
    StringBuilder queryStr = new StringBuilder(SB_QUERY_SIZE);

    // Already ensured that one and only one BC is mapped to the component package
    CompPkgBc cpBC = this.compPkg.getCompPkgBcs().first();

    queryStr.append(
        "SELECT DISTINCT funver.defcharname, funver.funcname FROM t_functionversions funver WHERE funver.defcharname IN ( SELECT param_name FROM gtt_parameters )   AND funver.funcname IN ( SELECT DISTINCT fc.name FROM T_Sdom_Bcs bc, T_Sdom_Fcs fc, T_Sdom_fc2bc fc2bc WHERE bc.name  ='")
        .append(cpBC.getName()).append("' AND bc.id  = fc2bc.bc_Id AND fc.id = fc2bc.fc_Id");
    Map<Long, CompPkgBcFc> cpFCMap = cpBC.getCompPkgBcFcsMap();
    if (!cpFCMap.isEmpty()) {
      queryStr.append(" and fc.name in (");
      int fcCounter = 0;
      for (CompPkgBcFc cpFC : cpFCMap.values()) {
        if (fcCounter > 0) {
          queryStr.append(',');
        }

        queryStr.append('\'').append(cpFC.getName()).append('\'');

        fcCounter++;
      }
      queryStr.append(')');

    }
    queryStr.append(')');


    return queryStr.toString();
  }

  /**
   * @return logger logger
   */
  private ILoggerAdapter getLogger() {
    return ObjectStore.getInstance().getLogger();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<String> getInvalidParams() {
    return this.invalidParamSet;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, Map<String, String>> getParamProps() {
    return this.paramPropMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, String> getParamNameType() {
    return this.paramNameTypeMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run(final Set<String> set, final String funcName, final CalDataImportData importData)
      throws IcdmException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, Function> getParamFuncObjMap() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, Parameter> getParamNameObjMap() {
    // TODO Auto-generated method stub
    return null;
  }


}
