/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.LabelList;
import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.module.util.A2LDataConstants.LabelType;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2LGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpObj;
import com.bosch.caltool.icdm.model.general.CommonParamKey;

// ICDM-1789 Extracted the responsibility loading logic to a separate file.
/**
 * Find responsibility of labels(parameters)
 *
 * @author bne4cob
 */
public final class ParamResponsibilityLoader extends AbstractSimpleBusinessObject {

  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public ParamResponsibilityLoader(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * Fetch responsiblity information into paramRespMap based on mapping source
   *
   * @param pidcA2lId PIDC A2L ID
   * @return Map : Key - parameter name; value - responsibility(work package / group)
   * @throws IcdmException error while retrieving data
   * @deprecated not used
   */
  @Deprecated
  public Map<String, String> getRespInfo(final Long pidcA2lId) throws IcdmException {
    A2lWpMapping pidcA2lWpMapping = new PidcA2lLoader(getServiceData()).getWorkpackageMapping(pidcA2lId);
    return getRespInfo(pidcA2lId, pidcA2lWpMapping);
  }

  /**
   * Fetch responsiblity information into paramRespMap based on mapping source, when A2L WP mapping is already available
   *
   * @param pidcA2lId PIDC A2L ID
   * @param pidcA2lWpMapping A2L wp mapping
   * @return Map : Key - parameter name; value - responsibility(work package / group)
   * @throws IcdmException error while retrieving data
   */
  public Map<String, String> getRespInfo(final Long pidcA2lId, final A2lWpMapping pidcA2lWpMapping)
      throws IcdmException {

    ConcurrentMap<String, String> paramRespMap = new ConcurrentHashMap<>();
    Long respMappingSrc = pidcA2lWpMapping == null ? null : pidcA2lWpMapping.getMappingSourceId();

    if (respMappingSrc != null) {
      if (CommonUtils.isEqual(respMappingSrc,
          Long.valueOf(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.GROUP_MAPPING_ID)))) {
        loadRespFromGroupMapping(paramRespMap, pidcA2lWpMapping);
      }
      else {
        loadRespFc2Wp(paramRespMap, pidcA2lWpMapping, pidcA2lId);
      }
    }
    return paramRespMap;
  }

  /**
   * Load responsibility if mapping type is 'group'
   *
   * @param paramRespMap responsibility map
   */
  private void loadRespFromGroupMapping(final ConcurrentMap<String, String> paramRespMap,
      final A2lWpMapping pidcA2lWpMapping) {
    // in case of group maaping, make group name as responsibility
    for (A2LGroup a2lGroup : pidcA2lWpMapping.getA2lGroupList()) {
      if (!a2lGroup.getLabelMap().isEmpty()) {
        addParamResp(a2lGroup, paramRespMap);
      }
    }
  }

  /**
   * Add responsibility from a2l groups
   *
   * @param paramRespMap responsibility map
   * @param a2lGroup A2L Group input
   */
  private void addParamResp(final A2LGroup a2lGroup, final ConcurrentMap<String, String> paramRespMap) {
    final Map<String, String> labelMap = a2lGroup.getLabelMap();
    for (String labelName : labelMap.values()) {
      paramRespMap.put(labelName, a2lGroup.getGroupName());
    }
  }

  /**
   * Load responsibility if mapping type is fc - wp
   *
   * @param paramRespMap responsibility map
   * @param pidcA2lId
   * @throws IcdmException error while retrieving data
   */
  private void loadRespFc2Wp(final ConcurrentMap<String, String> paramRespMap, final A2lWpMapping pidcA2lWpMappingp,
      final Long pidcA2lId)
      throws IcdmException {
    // in case of workpackages, make workpackage name as responsibility
    A2LFileInfo a2lFileInfo =
        new A2LFileInfoProvider(getServiceData()).fetchA2LFileInfo(new A2LFileInfoLoader(getServiceData())
            .getDataObjectByID(new PidcA2lLoader(getServiceData()).getDataObjectByID(pidcA2lId).getA2lFileId()));
    for (A2lWpObj workPckg : pidcA2lWpMappingp.getWorkPackageList()) {
      addRespInfoFromWP(paramRespMap, a2lFileInfo, workPckg);
    }
  }

  /**
   * Adds responsibility info to the responsibility map for workpackage types
   *
   * @param paramRespMap responsibility map
   * @param a2lFileInfo A2L file details
   * @param workPckg work package
   */
  private void addRespInfoFromWP(final ConcurrentMap<String, String> paramRespMap, final A2LFileInfo a2lFileInfo,
      final A2lWpObj workPckg) {
    for (String funcName : workPckg.getFunctionMap().values()) {
      Function function = a2lFileInfo.getAllModulesFunctions().get(funcName);
      LabelList labelList = function.getLabelList(LabelType.DEF_CHARACTERISTIC);
      if (null != labelList) {
        for (String labelName : labelList) {
          paramRespMap.put(labelName, workPckg.getWpName());
        }
      }
    }
  }

}
