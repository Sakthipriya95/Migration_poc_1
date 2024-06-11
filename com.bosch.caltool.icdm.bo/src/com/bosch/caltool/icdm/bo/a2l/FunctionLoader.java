/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.bo.user.NodeAccessLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.GttObjectName;
import com.bosch.caltool.icdm.database.entity.cdr.TFunction;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.cdr.MonicaReviewData;
import com.bosch.caltool.icdm.model.cdr.RvwFunctionModel;
import com.bosch.caltool.icdm.model.user.NodeAccess;

/**
 * @author dja7cob Fetch list of functions
 */
public class FunctionLoader extends AbstractBusinessObject<Function, TFunction> {

  /**
   * @param serviceData serviceData
   */
  public FunctionLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.CDR_FUNCTION, TFunction.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Function createDataObject(final TFunction dbFunc) throws DataException {
    Function function = new Function();
    function.setId(dbFunc.getId());
    function.setName(dbFunc.getName());
    function.setLongName(dbFunc.getLongname());

    function.setBigFunc(dbFunc.getBigFunction());
    function.setIsCustFunc(dbFunc.getIscustfunc());
    function.setUpperName(dbFunc.getUpperName());
    function.setRelevantName(dbFunc.getRelevantName());
    function.setVersion(dbFunc.getVersion());
    setCommonFields(function, dbFunc);
    return function;
  }


  /**
   * @param searchString string to search functions
   * @return set of FunctionDetails
   * @throws DataException DataException
   */
  public Set<Function> getSearchFunctions(String searchString) throws DataException {
    Set<Function> retSet = new HashSet<>();

    if (searchString.contains("*")) {
      searchString = searchString.replaceAll("\\*", "\\%");
    }

    TypedQuery<TFunction> tQuery = getEntMgr().createNamedQuery(TFunction.NQ_SEARCH_FUNC, TFunction.class);
    tQuery.setParameter("searchstring", searchString.toUpperCase(Locale.GERMAN) + "%");

    List<TFunction> dbFunctions = tQuery.getResultList();

    for (TFunction dbFunc : dbFunctions) {
      retSet.add(createDataObject(dbFunc));
    }
    return retSet;
  }

  /**
   * @return the param Collection set
   * @throws DataException DataException
   */
  private Map<String, Function> getAllUserFunctions() throws IcdmException {
    NodeAccessLoader loader = new NodeAccessLoader(getServiceData());
    Set<Long> funcIds = new HashSet<>();
    Map<Long, NodeAccess> allNodeAcces = loader.getAllNodeAccessForCurrentUser();
    for (NodeAccess node : allNodeAcces.values()) {
      if (("FUNCTION").equals(node.getNodeType()) && node.isWrite()) {
        funcIds.add(node.getNodeId());
      }

    }
    return fetchCDRFunctions(funcIds);
  }

  /**
   * @return the sorted Functions
   * @throws DataException DataException
   */
  public SortedSet<Function> getSortedFunctions() throws IcdmException {
    return new TreeSet<>(getAllUserFunctions().values());
  }

  /**
   * @param paramName
   * @return
   * @throws DataException
   */
  public SortedSet<Function> getFunctionsByparamName(final String paramName) throws DataException {
    SortedSet<Function> functions = new TreeSet<>();
    TypedQuery<TFunction> typeQuery = getEntMgr().createNamedQuery(TFunction.NQ_GET_FUNC_BY_PARAMNAME, TFunction.class);
    typeQuery.setParameter("paramName", paramName);
    final List<TFunction> resultList = typeQuery.getResultList();
    for (TFunction dbFunc : resultList) {
      functions.add(createDataObject(dbFunc));
    }
    return functions;
  }

  /**
   * Fetch all CDR Functions for the functionID list
   *
   * @param funcIdSet Set of func id's
   * @return sorted set of CDR function
   * @throws DataException
   */
  // iCDM-501
  private Map<String, Function> fetchCDRFunctions(final Set<Long> funcIdSet) throws IcdmException {
    return getFuncMap(funcIdSet);
  }

  /**
   * @param funcIdSet
   * @param funcSet
   * @throws DataException
   */
  private Map<String, Function> getFuncMap(final Set<Long> funcIdSet) throws IcdmException {


    Map<String, Function> funcMap = new HashMap<>();
    // Empty list would cause error in executing query
    if ((funcIdSet == null) || funcIdSet.isEmpty()) {
      return funcMap;
    }
    EntityManager entityManager = null;
    try {
      entityManager = ObjectStore.getInstance().getEntityManagerFactory().createEntityManager();
      entityManager.getTransaction().begin();
      GttObjectName tempFuncId;
      // Create entities for all the function id
      for (Long funcId : funcIdSet) {
        tempFuncId = new GttObjectName();
        tempFuncId.setId(funcId);
        entityManager.persist(tempFuncId);
      }
      entityManager.flush();

      // Run the Querry to use GTT table instead of in cluase
      TypedQuery<TFunction> typeQuery = entityManager.createNamedQuery(TFunction.NQ_GET_FUNC_BY_ID, TFunction.class);


      final List<TFunction> resultList = typeQuery.getResultList();
      for (TFunction dbFunc : resultList) {
        funcMap.put(dbFunc.getName(), createDataObject(dbFunc));
      }
      entityManager.getTransaction().rollback();
    }
    catch (Exception exp) {
      throw new IcdmException(exp.getMessage(), exp);
    }
    finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }

    return funcMap;
  }


  /**
   * @param funcNameSet funcNameSet
   * @return the functions
   * @throws DataException DataException
   */
  public Map<String, Function> getFunctionsByName(final List<String> funcNameSet) throws DataException {
    Map<String, Function> funcMap = new HashMap<>();
    getEntMgr().getTransaction().begin();
    GttObjectName tempQRvwfunc;
    long id = 1;

    // Create entities for all the functions
    for (String functionName : funcNameSet) {
      tempQRvwfunc = new GttObjectName();
      tempQRvwfunc.setId(id);
      tempQRvwfunc.setObjName(functionName.trim());
      getEntMgr().persist(tempQRvwfunc);
      id++;
    }
    getEntMgr().flush();

    TypedQuery<TFunction> tQuery = getEntMgr().createNamedQuery(TFunction.NQ_FUNC_IN_GTT_LIST, TFunction.class);

    final List<TFunction> resultList = tQuery.getResultList();
    for (TFunction dbFunc : resultList) {
      funcMap.put(dbFunc.getName(), createDataObject(dbFunc));
    }

    getEntMgr().getTransaction().rollback();
    return funcMap;
  }


  /**
   * @param funcNameSet funcNameSet
   * @return the functions
   * @throws DataException DataException
   */
  public Map<String, Function> getFunctionsByName(final Set<String> funcNameSet, final boolean hasUnassiged)
      throws DataException {
    Map<String, Function> funcMap = new HashMap<>();

    getEntMgr().getTransaction().begin();
    GttObjectName tempQRvwfunc;
    long id = 1;

    // Create entities for all the functions
    for (String functionName : funcNameSet) {
      tempQRvwfunc = new GttObjectName();
      tempQRvwfunc.setId(id);
      tempQRvwfunc.setObjName(functionName.trim());
      getEntMgr().persist(tempQRvwfunc);
      id++;
    }
    getEntMgr().flush();
    String query = "SELECT fnc FROM TFunction fnc,GttObjectName temp  where temp.objName = fnc.name";
    if (hasUnassiged) {
      query = query + " or fnc.name='<NOT_ASSIGNED>'";
    }
    final TypedQuery<TFunction> typeQuery = getEntMgr().createQuery(query, TFunction.class);
    final List<TFunction> resultList = typeQuery.getResultList();
    for (TFunction dbFunc : resultList) {
      funcMap.put(dbFunc.getName(), createDataObject(dbFunc));
    }
    getEntMgr().getTransaction().rollback();
    return funcMap;
  }

  /**
   * @param funcMap - function names from UI
   * @param originalFuncList
   * @return list of invalid function names
   */
  public List<String> getInvalidFunctions(final Map<String, String> funcMap) {


    getEntMgr().getTransaction().begin();
    GttObjectName tempQRvwfunc;
    long id = 1;

    // Create entities for all the functions
    for (String functionName : funcMap.values()) {
      tempQRvwfunc = new GttObjectName();
      tempQRvwfunc.setId(id);
      tempQRvwfunc.setObjName(functionName.trim());
      getEntMgr().persist(tempQRvwfunc);
      id++;
    }
    getEntMgr().flush();

    TypedQuery<TFunction> tQuery = getEntMgr().createNamedQuery(TFunction.NQ_FUNC_IN_GTT_LIST, TFunction.class);

    List<TFunction> dbFunctions = tQuery.getResultList();

    List<String> invalidFunctions = new ArrayList<>();

    List<String> dbFuncNames = new ArrayList<>();


    for (TFunction func : dbFunctions) {
      dbFuncNames.add(func.getName().trim().toUpperCase());
    }

    for (Entry<String, String> funcEntrySet : funcMap.entrySet()) {
      if (!dbFuncNames.contains(funcEntrySet.getValue())) {
        invalidFunctions.add(funcEntrySet.getKey());
      }
    }

    getEntMgr().getTransaction().rollback();

    return invalidFunctions;
  }


  /**
   * Method is used to get a function param map using the paramName list
   *
   * @param map list of Prameter Name
   * @return map of RvwFunction key with Set of parameters
   */
  public Map<RvwFunctionModel, Set<String>> getFunctionParamMapByParamList(
      final List<MonicaReviewData> monicaReviewDatas) {

    getEntMgr().getTransaction().begin();
    GttObjectName tempQRvwfunc;
    long id = 1;

    // Create entities for all the functions
    for (MonicaReviewData monicaReviewData : monicaReviewDatas) {
      tempQRvwfunc = new GttObjectName();
      tempQRvwfunc.setId(id);
      tempQRvwfunc.setObjName(monicaReviewData.getLabel());
      getEntMgr().persist(tempQRvwfunc);
      id++;
    }
    getEntMgr().flush();

    final Query typeQuery = getEntMgr().createNamedQuery(TFunction.NQ_GET_FUNC_BY_GTT_PARAM_NAME, Object.class);

    List<Object[]> resultList = typeQuery.getResultList();

    getEntMgr().getTransaction().rollback();
    getEntMgr().close();

    Map<RvwFunctionModel, Set<String>> rvwFuncMap = new HashMap<>();
    resultList.forEach(obj -> {
      RvwFunctionModel rvwFunc = new RvwFunctionModel();
      rvwFunc.setFuncId((Long) obj[0]);
      rvwFunc.setFuncName((String) obj[1]);
      if (rvwFuncMap.containsKey(rvwFunc)) {
        rvwFuncMap.get(rvwFunc).add((String) obj[2]);
      }
      else {
        Set<String> paramSet = new HashSet<>();
        paramSet.add((String) obj[2]);
        rvwFuncMap.put(rvwFunc, paramSet);
      }
    });

    return rvwFuncMap;
  }

  /**
   * @param funcNameSet Function Name
   * @return list of functions
   */
  private Set<String> getMismatchFunctListfromTable(final Set<String> funcNameSet) {

    getEntMgr().getTransaction().begin();
    GttObjectName tempQRvwfunc;
    long id = 1;

    // Create entities for all the functions
    for (String function : funcNameSet) {
      tempQRvwfunc = new GttObjectName();
      tempQRvwfunc.setId(id);
      tempQRvwfunc.setObjName(function.trim());
      getEntMgr().persist(tempQRvwfunc);
      id++;
    }
    getEntMgr().flush();

    final Query typeQuery = getEntMgr().createNamedQuery(TFunction.NQ_GET_INVALID_FUNC, Object.class);
    Set<String> resultList = new HashSet<>();
    List<String> result = typeQuery.getResultList();
    for (String entity : result) {
      resultList.add(entity);
    }
    getEntMgr().getTransaction().rollback();
    funcNameSet.removeAll(resultList);
    return funcNameSet;
  }

  /**
   * @param functionSet set of functions
   * @param a2lFunctionset set of function in a2l file
   * @return invalid function
   */
  public Set<String> getMismatchFunctList(final Set<String> functionSet, final Set<String> a2lFunctionset) {
    Set<String> invalidFuncInTable = getMismatchFunctListfromTable(functionSet);
    Set<String> invalidFunction = invalidFunctionInList(functionSet, a2lFunctionset);
    for (String function : invalidFuncInTable) {
      invalidFunction.add(function);
    }
    return invalidFunction;
  }

  private Set<String> invalidFunctionInList(final Set<String> functionSet, final Set<String> a2lFunctionset) {
    Set<String> invalidFunctionA2L = new HashSet<>();
    for (String func : functionSet) {
      if (!a2lFunctionset.contains(func)) {
        invalidFunctionA2L.add(func);
      }
    }
    return invalidFunctionA2L;
  }

  /**
   * @param funcName functionName
   * @return Function
   * @throws DataException Exception
   */
  public Function getFunctionsByName(final String funcName) throws DataException {
    List<String> funcNameList = new ArrayList<>();
    funcNameList.add(funcName);
    Collection<Function> functions = getFunctionsByName(funcNameList).values();
    return CommonUtils.isNotEmpty(functions) ? functions.iterator().next() : null;
  }
}
