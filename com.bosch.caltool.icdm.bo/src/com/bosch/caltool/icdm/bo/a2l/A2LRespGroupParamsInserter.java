/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2LGroup;
import com.bosch.caltool.icdm.model.a2l.WpResp;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;

/**
 * @author gge6cob
 */
public class A2LRespGroupParamsInserter extends AbstractSimpleBusinessObject {

  /**
   * a2l file id
   */
  private final Long a2lId;
  /**
   * wp root id
   */
  private final Long wpRootId;
  /**
   * pidc a2l id
   */
  private final Long pidcA2lId;
  /**
   * wp type id
   */
  private final Long typeId;
  /**
   *
   */
  private final A2lWPRespResolver resolver;
  private final PidcA2l pidcA2l;
  private final Long grpMappingId;

  /**
   * Constructor.
   *
   * @param serviceData Service Data
   * @param a2lId the a 2 l id
   * @param wpRootId the wp root id
   * @param pidca2l the pidca 2 l
   * @param typeId the type id
   * @param grpMappingId group mapping id
   * @param resolver2 the resolver 2
   */
  public A2LRespGroupParamsInserter(final ServiceData serviceData, final Long a2lId, final Long wpRootId,
      final PidcA2l pidca2l, final Long typeId, final Long grpMappingId, final A2lWPRespResolver resolver2) {
    super(serviceData);
    this.a2lId = a2lId;
    this.wpRootId = wpRootId;
    this.pidcA2l = pidca2l;
    this.pidcA2lId = this.pidcA2l.getId();
    this.typeId = typeId;
    this.grpMappingId = grpMappingId;
    this.resolver = resolver2;
  }


  /**
   * method which initialises input and calls web service.
   *
   * @param a2lGroupList the a 2 l group list
   * @param characteristicsMap the characteristics map
   * @param wpRespMap WP Resp Map
   * @return true, if successful
   * @throws IcdmException error while calling webservice
   */
  public boolean insertResp(final List<A2LGroup> a2lGroupList, final Map<String, Characteristic> characteristicsMap,
      final Map<Long, WpResp> wpRespMap)
      throws IcdmException {

    A2lGroupInput grpInput = new A2lGroupInput();
    grpInput.setA2lID(this.a2lId);
    grpInput.setRootGrpID(this.wpRootId);
    Map<String, String> a2lGrpMap = new HashMap<>();
    // iterate the group list
    for (A2LGroup a2lgroup : a2lGroupList) {
      grpInput.getA2lGrplist().add(a2lgroup.getGroupName());
      Set<String> paramNameTypeSet = new HashSet<>();
      // iterate the parameters in the group
      for (String paramName : a2lgroup.getLabelMap().keySet()) {
        // add param name and type with ":" as separator
        if (null != characteristicsMap.get(paramName)) {
          paramNameTypeSet.add(paramName + ":" + characteristicsMap.get(paramName).getType());
        }
      }
      // add the GROUP only, if it has parameter
      if (paramNameTypeSet.isEmpty()) {
        getLogger().debug(a2lgroup.getGroupName() + " not added since it has no parameter");
      }
      else {
        // create a map entry for this group
        grpInput.getGrpCharParamMap().put(a2lgroup.getGroupName(), paramNameTypeSet);
        // add group name and long name to the map
        a2lGrpMap.put(a2lgroup.getGroupName(), a2lgroup.getGroupLongName());
      }
    }
    grpInput.setA2lGrpMap(a2lGrpMap);
    grpInput.setCurrentUserId(getServiceData().getUsername());
    WpResp wpResp = getDefaultWPResp(wpRespMap);
    grpInput.setDefaultWPRespId(wpResp == null ? null : wpResp.getId());
    grpInput.setMappingSource(this.resolver.getMappingSourceId());
    grpInput.setPidcA2LId(this.pidcA2lId);
    grpInput.setTypeId(this.typeId);
    grpInput.setGroupMappingId(this.grpMappingId);
    try (ServiceData sdCmd = new ServiceData()) {
      // Copies the main service data details to the new one
      getServiceData().copyTo(sdCmd, false);
      getLogger().debug("Starting master command to insert group structure....");
      A2lGroupParamCreationMasterCommand masterCmd = new A2lGroupParamCreationMasterCommand(sdCmd, grpInput);
      sdCmd.getCommandExecutor().execute(masterCmd);
      getLogger().debug("Master Command to insert group structure executed successfully");
      return true;
    }
  }

  /**
   * @param defaultWpResp
   * @return
   * @throws ApicWebServiceException
   */
  private WpResp getDefaultWPResp(final Map<Long, WpResp> wpRespMap) {
    WpResp defaultWpResp = null;
    // get the instance for WP resp default
    for (WpResp wpResp : wpRespMap.values()) {
      if (CommonUtils.isEqual(wpResp.getRespName(), WpRespType.RB.getCode())) {
        defaultWpResp = wpResp;
      }
    }
    return defaultWpResp;
  }

}
