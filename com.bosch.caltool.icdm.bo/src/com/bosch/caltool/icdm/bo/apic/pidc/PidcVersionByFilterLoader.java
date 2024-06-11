/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.user.NodeAccessLevel;
import com.bosch.caltool.icdm.bo.user.NodeAccessLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionInfo;
import com.bosch.caltool.icdm.model.user.NodeAccessDetails;

/**
 * @author TRL1COB
 */
public class PidcVersionByFilterLoader extends AbstractSimpleBusinessObject {

  /**
   * Maximum count of values that can be provided in the 'IN' clause of an SQL Query
   */
  private static final int SQL_MAX_LIMIT_FOR_IN_CLAUSE = 999;


  /**
   * @param serviceData Service data
   */
  public PidcVersionByFilterLoader(final ServiceData serviceData) {
    super(serviceData);
  }


  /**
   * @param userNtIdSet Set of user names whose values are to be filtered
   * @param accessType Type of access for the provided username
   * @param pidcIdSet Set of PIDC ids whose values are to be filtered
   * @param pidcVersIdSet Set of PIDC version ids whose values are to be filtered
   * @param pidcName PIDC name which needs to be filtered
   * @param activeFlag Possible values - Y, N, ALL
   * @return Map of PidcVersionInfo. Key - Pidc Version ID, value - PidcVersionInfo
   * @throws DataException - Exception while retrieving data
   */
  public Map<Long, PidcVersionInfo> getPidcVersionInfo(final Set<String> userNtIdSet, final String accessType,
      final Set<Long> pidcIdSet, final Set<Long> pidcVersIdSet, final String pidcName, final String activeFlag)
      throws DataException {

    getLogger().info(
        "PidcVersionByFilterLoader.getPidcVersionInfo() - inputs : User NTIDs - {}, Access Type - {}, PIDC IDs - {}, PIDC Version IDs - {}, PIDC Name - {}",
        userNtIdSet, accessType, pidcIdSet, pidcVersIdSet, pidcName);

    Set<Long> filteredPidcIds = new HashSet<>();
    boolean proceedWithPidcFilters = true;

    if (CommonUtils.isNotEmpty(userNtIdSet)) {
      // Fetching PIDC IDs for which the user NT ids have specified access
      for (String userId : userNtIdSet) {
        filteredPidcIds
            .addAll(fetchPidcIdsFromNodeAccessDetails(userId, NodeAccessLevel.getType(accessType), pidcIdSet));
      }
      if (filteredPidcIds.isEmpty()) {
        getLogger().info("No Pidc IDs found for the User NT ID(s): {} with access type {}", userNtIdSet, accessType);
        proceedWithPidcFilters = false;
      }
    }

    // To filter PIDC IDs when only PidcName is given in query params and other values are not provided.This is done
    // so that pidcname filter gets applied without fetching all values
    else if (CommonUtils.isNotEmptyString(pidcName) && CommonUtils.isNullOrEmpty(pidcIdSet) &&
        CommonUtils.isNullOrEmpty(pidcVersIdSet) && CommonUtils.isEmptyString(activeFlag)) {
      filteredPidcIds.addAll(new PidcLoader(getServiceData()).getPidcIdByName(pidcName));
      if (filteredPidcIds.isEmpty()) {
        getLogger().info("No matching pattern found for pidc name: {}", pidcName);
        proceedWithPidcFilters = false;
      }
    }

    Map<Long, PidcVersionInfo> retMap = new HashMap<>();
    if (proceedWithPidcFilters) {
      retMap = filterPidcVersionInfo(pidcIdSet, pidcVersIdSet, pidcName, filteredPidcIds, activeFlag);
    }

    getLogger().debug("PidcVersionByFilterLoader.getPidcVersionInfo() - fetching PidcVersionInfo completed. Count: {}",
        retMap.size());

    return retMap;

  }


  /**
   * To filter Pidc version Info based on pidcIds, PidcVersionIds and PidcName
   *
   * @param pidcIdSet
   * @param pidcVersIdSet
   * @param pidcName
   * @param retMap
   * @param filteredPidcIds
   * @return
   * @throws DataException
   */
  private Map<Long, PidcVersionInfo> filterPidcVersionInfo(final Set<Long> pidcIdSet, final Set<Long> pidcVersIdSet,
      final String pidcName, final Set<Long> filteredPidcIds, final String activeFlag)
      throws DataException {

    Map<Long, PidcVersionInfo> retMap = new HashMap<>();
    PidcVersionLoader pidcVersLdr = new PidcVersionLoader(getServiceData());


    // To filter based on PIDC version ids
    if (CommonUtils.isNotEmpty(pidcVersIdSet)) {

      retMap = filterBasedOnPidcVersionIds(pidcIdSet, pidcVersIdSet, filteredPidcIds, activeFlag);
    }

    // To filter based on PIDC ids
    else if (CommonUtils.isNotEmpty(pidcIdSet)) {

      retMap = filterBasedOnPidcIds(pidcIdSet, filteredPidcIds, activeFlag);
    }

    // To filter when only userids and access type are provided or if only pidc name is provided
    else if (CommonUtils.isNotEmpty(filteredPidcIds)) {

      for (Set<Long> childPidcSet : CommonUtils.splitSet(filteredPidcIds, SQL_MAX_LIMIT_FOR_IN_CLAUSE)) {
        retMap.putAll(pidcVersLdr.getPidcVersionInfoByPidcId(childPidcSet, activeFlag));
      }
    }


    else if (CommonUtils.isNotEmptyString(activeFlag)) {
      retMap = pidcVersLdr.getPidcVersionInfo(activeFlag);
    }
    else {
      retMap = pidcVersLdr.getAllActiveVersionsWithStructureAttributes();
    }

    // To filter based on PIDC name in addition to other params
    if (CommonUtils.isNotEmptyString(pidcName)) {
      retMap = filterBasedOnPidcName(pidcName, retMap);
    }


    return retMap;
  }


  /**
   * @param pidcIdSet
   * @param pidcVersIdSet
   * @param filteredPidcIds
   * @return
   * @throws DataException
   */
  private Map<Long, PidcVersionInfo> filterBasedOnPidcVersionIds(final Set<Long> pidcIdSet,
      final Set<Long> pidcVersIdSet, final Set<Long> filteredPidcIds, final String activeFlag)
      throws DataException {
    Map<Long, PidcVersionInfo> retMap;
    if (CommonUtils.isNotEmpty(pidcIdSet) && CommonUtils.isNullOrEmpty(filteredPidcIds)) {
      retMap = filterByPidcVersionIds(pidcVersIdSet, pidcIdSet, activeFlag);
    }
    else {
      retMap = filterByPidcVersionIds(pidcVersIdSet, filteredPidcIds, activeFlag);
    }
    return retMap;
  }

  /**
   * @param pidcName
   * @param retMap
   * @return
   */
  private Map<Long, PidcVersionInfo> filterBasedOnPidcName(final String pidcName,
      final Map<Long, PidcVersionInfo> retMap) {
    // To filter based on PIDC Name
    return retMap.entrySet().stream()
        .filter(versInfo -> versInfo.getValue().getPidc().getName().toLowerCase().contains(pidcName.toLowerCase()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }


  /**
   * @param pidcVersIdSet
   * @param filteredPidcIds
   * @return
   * @throws DataException
   */
  private Map<Long, PidcVersionInfo> filterByPidcVersionIds(final Set<Long> pidcVersIdSet,
      final Set<Long> filteredPidcIds, final String activeFlag)
      throws DataException {

    Map<Long, PidcVersionInfo> retMap;
    PidcVersionLoader pidcVersLdr = new PidcVersionLoader(getServiceData());
    Set<Long> filteredPidcVersIds = new HashSet<>();
    for (Long pidcVersID : pidcVersIdSet) {
      if (CommonUtils.isNotEmpty(filteredPidcIds)) {
        Long pidcId = pidcVersLdr.getDataObjectByID(pidcVersID).getPidcId();
        if (filteredPidcIds.contains(pidcId)) {
          filteredPidcVersIds.add(pidcVersID);
        }
      }
      else {
        filteredPidcVersIds.add(pidcVersID);
      }
    }

    if (CommonUtils.isNotEmpty(filteredPidcVersIds)) {
      retMap = pidcVersLdr.getPidcVersionInfo((filteredPidcVersIds), activeFlag);
    }
    else {
      retMap = new HashMap<>();
    }
    return retMap;
  }


  /**
   * @param pidcIdSet
   * @param pidcVersIdSet
   * @param filteredPidcIds
   * @return
   * @throws DataException
   */
  private Map<Long, PidcVersionInfo> filterBasedOnPidcIds(final Set<Long> pidcIdSet, final Set<Long> filteredPidcIds,
      final String activeFlag)
      throws DataException {

    Map<Long, PidcVersionInfo> retMap;
    PidcVersionLoader pidcVersLdr = new PidcVersionLoader(getServiceData());
    if (CommonUtils.isNotEmpty(filteredPidcIds)) {
      Set<Long> pidcIdsToBeFetched = pidcIdSet.stream().filter(filteredPidcIds::contains).collect(Collectors.toSet());
      retMap = pidcVersLdr.getPidcVersionInfoByPidcId(pidcIdsToBeFetched, activeFlag);
    }
    else {
      retMap = pidcVersLdr.getPidcVersionInfoByPidcId(pidcIdSet, activeFlag);
    }
    return retMap;
  }


  /**
   * @param userId
   * @param nodeAccessLevel
   * @param pidcIdSet
   * @return
   * @throws DataException
   */
  private Set<Long> fetchPidcIdsFromNodeAccessDetails(final String userId, final NodeAccessLevel nodeAccessLevel,
      final Set<Long> pidcIdSet)
      throws DataException {
    NodeAccessLoader nodeAccessLoader = new NodeAccessLoader(getServiceData());
    NodeAccessDetails nodeAccessDetails =
        nodeAccessLoader.getAllNodeAccessByNode(ApicConstants.PIDC_NODE_TYPE, userId, nodeAccessLevel, pidcIdSet);
    return nodeAccessDetails.getNodeAccessMap().keySet();

  }


}
