/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcScoutInputAnalyzer.SCOUT_INPUT_TYPE;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectAttr;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.PROJ_ATTR_USED_FLAG;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSearchCondition;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSearchInput;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSearchResponse;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSearchResult;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.general.CommonParamKey;


/**
 * PIDC Scout Busines object
 *
 * @author bne4cob
 */
public class PidcScout extends AbstractSimpleBusinessObject {

  /**
   * Input for PIDC Scout
   */
  private final PidcSearchInput input;

  /**
   * Search results.
   * <p>
   * Key - PIDC Version ID<br>
   * Value - Result object
   */
  private final ConcurrentMap<Long, PidcSearchResult> searchResultMap = new ConcurrentHashMap<>();

  /**
   * Ignored pidc versions for search (as configured in TABV_COMMON_PARAMS) table
   */
  private final Set<Long> ignoredPidcVersSet = new HashSet<>();
  private final Set<Long> booleanAttrIds = new HashSet<>();

  /**
   * PIDC Search
   *
   * @param serviceData Service Data
   * @param input search conditions
   */
  public PidcScout(final ServiceData serviceData, final PidcSearchInput input) {
    super(serviceData);
    this.input = input;
  }

  /**
   * @return search projects
   * @throws IcdmException any exception during search
   */
  public PidcSearchResponse findProjects() throws IcdmException {
    getLogger().info("Pidc Scout started. Search Input - {}", this.input);

    // Converting the used flag value to upper case
    this.input.getSearchConditions().stream().filter(s -> CommonUtils.isNotEmptyString(s.getUsedFlag()))
        .forEach(s -> s.setUsedFlag(s.getUsedFlag().toUpperCase(Locale.ENGLISH)));

    long startTime = System.currentTimeMillis();

    loadPidcVersToIgnore();

    Set<PidcVersion> pidcVersSet = fetchPidcVersions();

    runPreliminarySearch(pidcVersSet);
    runMasterSearch(pidcVersSet);

    getLogger().info("Search completed. Number of PIDC matching search criteria = {}; Time taken = {}",
        this.searchResultMap.size(), System.currentTimeMillis() - startTime);

    PidcSearchResponse response = new PidcSearchResponse();
    response.getResults().addAll(this.searchResultMap.values());
    return response;
  }

  private Set<PidcVersion> fetchPidcVersions() throws DataException {
    PidcScoutInputAnalyzer analyzer = new PidcScoutInputAnalyzer(getServiceData(), this.input);
    analyzer.analyze();
    this.booleanAttrIds.addAll(analyzer.getBooleanAttrIds());
    SCOUT_INPUT_TYPE inputType = analyzer.getInputType();
    Set<PidcSearchCondition> refinedConditions = analyzer.getRefinedConditions();

    Set<PidcVersion> pidcVersSet;
    PidcVersionLoader pidVrsLdr = new PidcVersionLoader(getServiceData());
    if (analyzer.isPidcNameIncluded()) {
      pidcVersSet = pidVrsLdr.getVersionsByName(analyzer.getPidcNameCondition().getAttributeValueIds());
    }
    else if (analyzer.isVarNameIncluded()) {
      PidcSearchCondition con = analyzer.getVarNameCondition();
      pidcVersSet = pidVrsLdr.getVersionsByVarName(con.getUsedFlag(), con.getAttributeValueIds());
      if (con.getAttributeValueIds().isEmpty() && (inputType != null)) {
        pidcVersSet.retainAll(getVersionsByInputType(inputType, refinedConditions));
      }
    }
    else if (analyzer.isSvarNameIncluded()) {
      PidcSearchCondition con = analyzer.getSvarNameCondition();
      pidcVersSet = pidVrsLdr.getVersionsBySubVarName(con.getUsedFlag(), con.getAttributeValueIds());
      if (con.getAttributeValueIds().isEmpty() && (inputType != null)) {
        pidcVersSet.retainAll(getVersionsByInputType(inputType, refinedConditions));
      }
    }
    else {
      pidcVersSet = getVersionsByInputType(inputType, refinedConditions);
    }

    getLogger().debug("Initial count of pidc versions = {}", pidcVersSet.size());

    return pidcVersSet;
  }

  private Set<PidcVersion> getVersionsByInputType(final SCOUT_INPUT_TYPE inputType,
      final Set<PidcSearchCondition> conditions)
      throws DataException {

    Set<PidcVersion> pidcVersSet;
    PidcVersionLoader pidVrsLdr = new PidcVersionLoader(getServiceData());
    Set<Long> dataIdSet = new HashSet<>();
    switch (inputType) {
      case ATTR_IDS:// Used flag yes or no
        addAttrIds(conditions, dataIdSet);
        pidcVersSet = pidVrsLdr.getVersionsByAttribute(dataIdSet);
        break;
      case USED_NO:// Used flag NO
        addAttrIds(conditions, dataIdSet);
        pidcVersSet = pidVrsLdr.getVersionsByUsedFlag(dataIdSet, PROJ_ATTR_USED_FLAG.NO.getDbType());
        break;
      case USED_YES:// Used flag YES
        addAttrIds(conditions, dataIdSet);
        pidcVersSet = pidVrsLdr.getVersionsByUsedFlag(dataIdSet, PROJ_ATTR_USED_FLAG.YES.getDbType());
        break;
      case VALUE_IDS:// Value based
        pidcVersSet = getPidcVersionForValueId(conditions, pidVrsLdr, dataIdSet);
        break;
      case BOOLEAN_ONLY:
        // The user has selected only boolean type attributes
        pidcVersSet = getPidcVersWithBoolConditions(conditions, pidVrsLdr);
        break;
      default:// All versions. Contains used flag not defined, no criteria etc.
        pidcVersSet = new HashSet<>(pidVrsLdr.getAllNonDeletedActiveVersions().values());
        break;
    }
    return pidcVersSet;
  }

  /**
   * @param conditions
   * @param pidVrsLdr
   * @param dataIdSet
   * @return
   * @throws DataException
   */
  private Set<PidcVersion> getPidcVersionForValueId(final Set<PidcSearchCondition> conditions,
      final PidcVersionLoader pidVrsLdr, final Set<Long> dataIdSet)
      throws DataException {
    conditions.forEach(condition -> {
      if (!isBooleanAttr(condition)) {
        dataIdSet.addAll(condition.getAttributeValueIds());
      }
    });
    return pidVrsLdr.getVersionsByValueIds(dataIdSet);

  }


  private Set<PidcVersion> getPidcVersWithBoolConditions(final Set<PidcSearchCondition> conditions,
      final PidcVersionLoader pidVrsLdr)
      throws DataException {
    Set<Long> yesAttrIds = new HashSet<>();
    Set<Long> noAttrIds = new HashSet<>();
    Set<Long> undefinedAttrIds = new HashSet<>();
    conditions.forEach(condition -> fillAttrIdsForBooleanAttrs(yesAttrIds, noAttrIds, undefinedAttrIds, condition));
    if (!undefinedAttrIds.isEmpty()) {
      return new HashSet<>(pidVrsLdr.getAllNonDeletedActiveVersions().values());
    }
    if (!yesAttrIds.isEmpty() && !noAttrIds.isEmpty()) {
      return pidVrsLdr.getPidcWithBooleanValues(yesAttrIds, noAttrIds);
    }
    if (yesAttrIds.isEmpty()) {
      return pidVrsLdr.getVersionsByUsedFlag(noAttrIds, PROJ_ATTR_USED_FLAG.NO.getDbType());
    }
    return pidVrsLdr.getVersionsByUsedFlag(yesAttrIds, PROJ_ATTR_USED_FLAG.YES.getDbType());
  }

  /**
   * @param yesAttrIds
   * @param noAttrIds
   * @param undefinedAttrIds
   * @param condition
   */
  private void fillAttrIdsForBooleanAttrs(final Set<Long> yesAttrIds, final Set<Long> noAttrIds,
      final Set<Long> undefinedAttrIds, final PidcSearchCondition condition) {
    if (condition.getUsedFlag() == null) {
      AttributeValueLoader valueLoader = new AttributeValueLoader(getServiceData());
      condition.getAttributeValueIds().forEach(valueId -> {
        try {
          if (valueLoader.getDataObjectByID(valueId).getName().equals(ApicConstants.BOOLEAN_TRUE_STRING)) {
            yesAttrIds.add(condition.getAttributeId());
          }
          else if (valueLoader.getDataObjectByID(valueId).getName().equals(ApicConstants.BOOLEAN_FALSE_STRING)) {
            noAttrIds.add(condition.getAttributeId());
          }
        }
        catch (DataException exp) {
          getLogger().error(exp.getLocalizedMessage(), exp);
        }
      });
    }
    else {
      if (condition.getUsedFlag().equals(PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType()) ||
          condition.getUsedFlag().equals(PROJ_ATTR_USED_FLAG.NEW_ATTR.getUiType())) {
        undefinedAttrIds.add(condition.getAttributeId());
      }
      if (condition.getUsedFlag().equals(PROJ_ATTR_USED_FLAG.YES.getUiType())) {
        yesAttrIds.add(condition.getAttributeId());
      }
      else {
        noAttrIds.add(condition.getAttributeId());
      }
    }
  }

  /**
   * @param conditions
   * @param dataIdSet
   */
  private void addAttrIds(final Set<PidcSearchCondition> conditions, final Set<Long> dataIdSet) {
    // skip the conditions with boolean type attributes
    conditions.forEach(condition -> {
      if (!isBooleanAttr(condition)) {
        dataIdSet.add(condition.getAttributeId());
      }
    });
  }

  /**
   * @param condition
   * @return true if the condition holds the attribute of boolean type
   * @throws DataException
   */
  boolean isBooleanAttr(final PidcSearchCondition condition) {
    return this.booleanAttrIds.contains(condition.getAttributeId());
  }

  /**
   * Run a sample search to pre-load common data like all attributes etc. before starting the thread based search. This
   * adds the common data to cache that will be reused by other threads.
   */
  private void runPreliminarySearch(final Set<PidcVersion> pidcVersSet) throws IcdmException {
    Iterator<PidcVersion> versItr = pidcVersSet.iterator();

    while (versItr.hasNext()) {
      PidcVersion pidcVers = versItr.next();
      PidcVersionSearch search = createVersionSearchObj(pidcVers, false);

      versItr.remove();

      if (search != null) {
        getLogger().debug("Executing preliminary search with pidc version - {}", pidcVers.getId());

        addResult(search.call());

        // Stop after the first valid search. The remaining search will be performed by runMasterSearch method
        break;
      }

    }
  }

  private void runMasterSearch(final Set<PidcVersion> pidcVersSet) throws DataException {
    // Search in all pidc versions
    getLogger().debug("Executing search on remaining pidc versions ...");

    List<PidcVersionSearch> searchObjList = createVersionSearchObj(pidcVersSet);

    if (!searchObjList.isEmpty()) {
      // If there are items after first search
      getLogger().debug("Number of Pidc Versions to analyze = {}", searchObjList.size());

      ExecutorService executor = Executors.newWorkStealingPool();

      try {
        executor.invokeAll(searchObjList).stream().map(this::searchCall).forEach(this::addResult);
      }
      catch (InterruptedException exp) {
        getLogger().warn("Interrupted!", exp);
        Thread.currentThread().interrupt();
      }

      executor.shutdown();
    }

  }

  private PidcVersionSearch createVersionSearchObj(final PidcVersion pidcVers, final boolean useNewServiceData)
      throws DataException {
    if (!isIgnoredFromSearch(pidcVers.getId()) && canCheck(pidcVers)) {
      return new PidcVersionSearch(getServiceData(), this, pidcVers, useNewServiceData);
    }
    return null;
  }

  private List<PidcVersionSearch> createVersionSearchObj(final Set<PidcVersion> pidcVersSet) throws DataException {
    List<PidcVersionSearch> searchObjList = new ArrayList<>();
    for (PidcVersion pidcVers : pidcVersSet) {
      PidcVersionSearch search = createVersionSearchObj(pidcVers, true);
      if (search != null) {
        searchObjList.add(search);
      }
    }
    return searchObjList;
  }

  private boolean canCheck(final PidcVersion pidcVers) throws DataException {
    PidcVersionLoader pversLdr = new PidcVersionLoader(getServiceData());
    if (pversLdr.isHiddenToCurrentUser(pidcVers.getId())) {
      getLogger().debug("PIDC Version '{}' is hidden to the user ID and will be ignored.", pidcVers.getId());
      return false;
    }
    return true;
  }

  private void addResult(final PidcSearchResult result) {
    if (result != null) {
      getLogger().debug("Search Result found - {}", result);
      this.searchResultMap.put(result.getPidcVersion().getId(), result);
    }
  }

  private PidcSearchResult searchCall(final Future<PidcSearchResult> future) {
    try {
      return future.get();
    }
    catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Initialise the attribute values, to be searched in PIDCs, for PIDC ignore
   *
   * @throws DataException if the configuration parameter is not set correctly
   */
  private void loadPidcVersToIgnore() {

    String paramVal = new CommonParamLoader(getServiceData()).getValue(CommonParamKey.PIDC_SEARCH_EXCLUDE_VAL_LEVEL1);
    if (CommonUtils.isEmptyString(paramVal)) {
      return;
    }

    String[] valIDArr = paramVal.split(",");
    Set<Long> valIdList = new HashSet<>();
    for (String valIDStr : valIDArr) {
      valIdList.add(Long.valueOf(valIDStr.trim()));
    }
    if (!valIdList.isEmpty()) {
      TypedQuery<Long> pidcVrsnIdsQry =
          getServiceData().getEntMgr().createNamedQuery(TabvProjectAttr.GET_PIDCVERSIONIDS_FOR_VALUEID, Long.class);
      pidcVrsnIdsQry.setParameter("valIDs", valIdList);
      this.ignoredPidcVersSet.addAll(pidcVrsnIdsQry.getResultList());
    }

    getLogger().debug("Ignored pidc version count = {}", this.ignoredPidcVersSet.size());

  }

  private boolean isIgnoredFromSearch(final Long pidcVersId) {
    return (pidcVersId != null) && this.ignoredPidcVersSet.contains(pidcVersId);
  }

  /**
   * @return
   */
  PidcSearchInput getSearchInput() {
    return this.input;
  }


  /**
   * @return the booleanAttrIds
   */
  public Set<Long> getBooleanAttrIds() {
    return new HashSet<>(this.booleanAttrIds);
  }


}

