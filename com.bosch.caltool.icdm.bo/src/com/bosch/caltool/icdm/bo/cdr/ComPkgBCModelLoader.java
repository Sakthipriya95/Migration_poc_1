/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.ref.concrete.DefCharacteristic;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.database.entity.apic.GttEaseeElements;
import com.bosch.ssd.icdm.model.ComPkgBcModel;

/**
 * Business class to fetch the parse the a2l file byte array and fetch the compli-parameter from the a2l file model
 *
 * @author svj7cob
 */
public class ComPkgBCModelLoader extends AbstractSimpleBusinessObject {

  /**
   *
   */
  private static final String PROCEDURE_SQLCODE_ERR = "Sqlcode: -20100";

  private final A2LFileInfo a2lFileInfo;
  private final Set<String> compliParamSet;

  private final Map<String, Function> compliFcs = new HashMap<>();
  private final Map<String, String> paramFuncMap = new HashMap<>();


  /**
   * Class Instance
   *
   * @param serviceData initialise the service transaction
   * @param a2lFileInfo a2lFileInfo
   * @param compliParamSet compliParamSet
   */
  public ComPkgBCModelLoader(final ServiceData serviceData, final A2LFileInfo a2lFileInfo,
      final Set<String> compliParamSet) {

    super(serviceData);

    this.a2lFileInfo = a2lFileInfo;
    // get the compli params
    this.compliParamSet = new HashSet<>(compliParamSet);
    // fetch the Compli Fc's
    fetchCompliFcs();
  }


  /**
   * @param pverName pverName
   * @param pverVariant pverVariant
   * @param pverRevision pverRevision
   * @return list of gtt elements
   * @throws IcdmException data loading error
   */
  public Map<String, ComPkgBcModel> getBCs(final String pverName, final String pverVariant, final String pverRevision)
      throws IcdmException {

    getLogger().debug("Fetching BC model started. Inputs : PVER = {}, PVER variant = {}, PVER Revision = {}", pverName,
        pverVariant, pverRevision);

    EntityManager entMgr = getServiceData().getEntMgr();
    entMgr.getTransaction().begin();

    Map<Long, GttEaseeElements> bcElementMap = new HashMap<>();
    Map<String, GttEaseeElements> fcElementMap = new HashMap<>();

    Long deLibBcID = null;

    try {
      // procedure call. Results will be available in temp table GTT_EASEE_ELEMENTS
      final Query stmtFetchPverBc = entMgr.createNativeQuery("{CALL Get_PVER_BC (?1, ?2, ?3) }");

      stmtFetchPverBc.setParameter(1, pverName);
      stmtFetchPverBc.setParameter(2, pverVariant);
      stmtFetchPverBc.setParameter(3, pverRevision);
      stmtFetchPverBc.executeUpdate();

      for (GttEaseeElements gttEaseeElement : fetchEaseeElts(entMgr)) {
        Long thisLibBcId = categorizeToBcFcElement(gttEaseeElement, bcElementMap, fcElementMap);
        if (thisLibBcId != null) {
          deLibBcID = thisLibBcId;
        }
      }
    }
    catch (PersistenceException exp) {
      throw createIcdmException(exp);
    }

    // identify missing FCs (FC without BC mapping)
    for (Function function : this.compliFcs.values()) {
      String compliFCName = (function).getName();

      if (!fcElementMap.containsKey(compliFCName) && compliFCName.contains("DELib_") && (deLibBcID != null)) {
        // FC is missing in SDOM and FC is a DELib FC
        // add a dummy FC element which is mapped to DELib BC
        GttEaseeElements dummyFc = new GttEaseeElements();
        dummyFc.setContainerId(deLibBcID);
        dummyFc.setElementClass("FC");
        dummyFc.setElementName(compliFCName);

        fcElementMap.put(dummyFc.getElementName(), dummyFc);
      }

    }

    Map<String, ComPkgBcModel> bcModelMap = createBcModel(bcElementMap, fcElementMap);

    entMgr.getTransaction().rollback();

    // Log results
    logUnmappedParams(fcElementMap);
    logBCs(bcModelMap);
    getLogger().debug("Fetching BC model completed. BCs found = {}", bcModelMap.size());

    return bcModelMap;
  }


  /**
   * fetch Compli Fc's
   */
  private void fetchCompliFcs() {
    // Get all functions
    Map<String, Function> allModulesFunctions = this.a2lFileInfo.getAllModulesFunctions();
    for (Function function : allModulesFunctions.values()) {
      // get the Def characteristics
      List<DefCharacteristic> defCharRefList = function.getDefCharRefList();
      if (defCharRefList != null) {
        // Fetch Fc's
        fillCompliFcs(function, defCharRefList);
      }
    }
  }


  /**
   * @param function
   * @param defCharRefList
   */
  private void fillCompliFcs(final Function function, final List<DefCharacteristic> defCharRefList) {
    for (DefCharacteristic defCharacteristic : defCharRefList) {

      for (String paramName : this.compliParamSet) {
        if (paramName.equals(defCharacteristic.getName())) {
          this.compliFcs.put(function.getName(), function);
          this.paramFuncMap.put(function.getName(), paramName);
        }
      }
    }
  }

  /**
   * Categorizethe input easee element to BC/FC element based on the element class.
   *
   * @param inputGttEaseeElement input
   * @param bcElementMap output
   * @param fcElementMap output
   * @return deLibBcID if the input element class is BC/BC-MO and element name is DELib
   */
  private Long categorizeToBcFcElement(final GttEaseeElements inputGttEaseeElement,
      final Map<Long, GttEaseeElements> bcElementMap, final Map<String, GttEaseeElements> fcElementMap) {

    Long deLibBcID = null;

    // add bc elements
    if (("BC".equals(inputGttEaseeElement.getElementClass())) ||
        ("BC_MO".equals(inputGttEaseeElement.getElementClass()))) {

      bcElementMap.put(Long.valueOf(inputGttEaseeElement.getId()), inputGttEaseeElement);
      // return the DELib BC, will be used later
      if ("DELib".equals(inputGttEaseeElement.getElementName())) {
        deLibBcID = Long.valueOf(inputGttEaseeElement.getId());
      }
    }
    // add FC elements
    else if ("FC".equals(inputGttEaseeElement.getElementClass()) &&
        this.compliFcs.containsKey(inputGttEaseeElement.getElementName())) {
      fcElementMap.put(inputGttEaseeElement.getElementName(), inputGttEaseeElement);

    }
    // add FC-ARA elements
    else if (("FC-ARA".equals(inputGttEaseeElement.getElementClass())) ||
        ("FC-ARB".equals(inputGttEaseeElement.getElementClass()))) {

      categorizeToFcArElement(inputGttEaseeElement, fcElementMap);
    }

    return deLibBcID;
  }


  private void categorizeToFcArElement(final GttEaseeElements inputGttEaseeElement,
      final Map<String, GttEaseeElements> fcElementMap) {

    if (this.compliFcs.containsKey(inputGttEaseeElement.getElementName())) {
      // FC-ARA / FC-ARB matches the FC in A2L file
      fcElementMap.put(inputGttEaseeElement.getElementName(), inputGttEaseeElement);
    }
    else {
      // check, if FC-ARA matches when inserting "_FCT"
      // SDOM: ICCtl_ShutOff => A2L: ICCtl_FCT_ShutOff
      if (inputGttEaseeElement.getElementName().contains("_")) {
        int firstUnderscore = inputGttEaseeElement.getElementName().indexOf('_');

        String modifiedFC = inputGttEaseeElement.getElementName().substring(0, firstUnderscore) + "_FCT" +
            inputGttEaseeElement.getElementName().substring(firstUnderscore);

        if (this.compliFcs.containsKey(modifiedFC)) {
          GttEaseeElements modifiedElement = new GttEaseeElements();
          modifiedElement.setContainerId(inputGttEaseeElement.getContainerId());
          modifiedElement.setElementClass(inputGttEaseeElement.getElementClass());
          modifiedElement.setElementName(inputGttEaseeElement.getElementName());
          modifiedElement.setElementVariant(inputGttEaseeElement.getElementVariant());
          modifiedElement.setElementRevision(inputGttEaseeElement.getElementRevision());
          modifiedElement.setId(inputGttEaseeElement.getId());

          fcElementMap.put(modifiedFC, modifiedElement);
        }
      }
    }
  }


  /**
   * @param retSet
   * @param bcElements
   * @param fcElements
   * @return
   */
  private Map<String, ComPkgBcModel> createBcModel(final Map<Long, GttEaseeElements> bcElements,
      final Map<String, GttEaseeElements> fcElements) {

    Map<String, ComPkgBcModel> retMap = new HashMap<>();

    GttEaseeElements bcElement;
    for (GttEaseeElements fcElement : fcElements.values()) {
      bcElement = bcElements.get(Long.valueOf(fcElement.getContainerId()));
      if (bcElement == null) {
        getLogger().debug("  BC for FC not in model. FC Element container id = {}", fcElement.getContainerId());
        // BC for FC not in model
      }
      else {
        ComPkgBcModel model = new ComPkgBcModel();
        model.setBcName(bcElement.getElementName());
        model.setBvVersion(bcElement.getElementVariant());
        model.setLevel(new BigDecimal(1));

        retMap.put(bcElement.getElementName(), model);
      }
    }

    return retMap;
  }


  /**
   * @param entMgr entity manager
   * @return list of gtt elements
   */
  private List<GttEaseeElements> fetchEaseeElts(final EntityManager entMgr) {
    final TypedQuery<GttEaseeElements> qDbA2LFiles =
        entMgr.createNamedQuery(GttEaseeElements.NQ_GET_GTT_EASEE_ELTS, GttEaseeElements.class);
    return qDbA2LFiles.getResultList();
  }

  /**
   * @param fcElements fcElements
   * @param paramFuncMap paramFuncMap
   */
  private void logUnmappedParams(final Map<String, GttEaseeElements> fcElements) {
    List<String> unmappedParams = new ArrayList<>();

    for (Entry<String, String> funcParam : this.paramFuncMap.entrySet()) {
      if (!fcElements.containsKey(funcParam.getKey())) {
        unmappedParams.add(funcParam.getValue());
      }
    }

    if (!unmappedParams.isEmpty()) {
      getLogger()
          .error("Parameter(s) without BC mapping : " + unmappedParams.stream().collect(Collectors.joining(", ")));
    }
  }

  /**
   * @param bcModelMap
   */
  private void logBCs(final Map<String, ComPkgBcModel> bcModelMap) {
    if (!bcModelMap.isEmpty()) {
      getLogger().debug(" BC Retrieved :");
    }

    for (Entry<String, ComPkgBcModel> enty : bcModelMap.entrySet()) {
      ComPkgBcModel model = enty.getValue();
      getLogger().debug("  Name = {}, vers = {}, lvl = {}", enty.getKey(), model.getBvVersion(), model.getLevel());
    }
  }

  /**
   * Convert the input exception to iCDM exception and throw
   */
  private IcdmException createIcdmException(final PersistenceException exp) {
    int indexOf = exp.getMessage().indexOf("Sqlcode");
    String sqlCode = exp.getMessage().substring(indexOf, indexOf + 15);

    // error code for normal error from procedure is 20100.
    if (sqlCode.equalsIgnoreCase(PROCEDURE_SQLCODE_ERR)) {
      return new InvalidInputException("COMPLI_REVIEW.BC_MISSING", exp, exp.getMessage());
    }

    // New error code defined for BC mapping failures
    return new IcdmException("BASE_COMP.BC_FETCH_ERROR_DB", exp, exp.getMessage());
  }
}

