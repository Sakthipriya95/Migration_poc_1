/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.a2l.jpa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.caltool.a2l.jpa.bo.A2LGroup;
import com.bosch.caltool.a2l.jpa.bo.WPResponsibility;
import com.bosch.caltool.apic.jpa.bo.PIDCA2l;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.bo.a2l.A2lGroupInput;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;

/**
 * @author mkl2cob
 */
@Deprecated
public class A2LRespGroupParamsInserter {


  /**
   * the response without error
   */
  private static final String OK_RESPONSE = "OK";
  /**
   * a2l file id
   */
  private final Long a2lId;
  /**
   * wp root id
   */
  private final Long wpRootId;
  /**
   * A2LEditorDataProvider
   */
  private final A2LEditorDataProvider a2lEditorDP;
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
  private final PIDCA2l pidcA2l;
  private final A2LDataProvider a2lDataProvider;

  /**
   * Constructor
   *
   * @param a2lId
   * @param wpRootId
   * @param a2lEditorDP
   * @param pidca2l
   * @param typeId
   * @param resolver2
   */
  public A2LRespGroupParamsInserter(final Long a2lId, final Long wpRootId, final A2LEditorDataProvider a2lEditorDP,
      final PIDCA2l pidca2l, final Long typeId, final A2lWPRespResolver resolver2,
      final A2LDataProvider a2lDataProvider) {
    this.a2lId = a2lId;
    this.wpRootId = wpRootId;
    this.a2lEditorDP = a2lEditorDP;
    this.pidcA2l = pidca2l;
    this.pidcA2lId = this.pidcA2l.getID();
    this.typeId = typeId;
    this.resolver = resolver2;
    this.a2lDataProvider = a2lDataProvider;

  }


  /**
   * method which initialises input and calls web service
   *
   * @return
   */
  public boolean callWebServiceForCreation() {

    A2lGroupInput grpInput = new A2lGroupInput();
    grpInput.setA2lID(this.a2lId);
    grpInput.setRootGrpID(this.wpRootId);
    Map<String, String> a2lGrpMap = new HashMap<String, String>();
    Map<String, Characteristic> characteristicsMap = this.a2lEditorDP.getCharacteristicsMap();

    // iterate the group list
    for (A2LGroup a2lgroup : this.a2lEditorDP.getA2LGroupList()) {
      grpInput.getA2lGrplist().add(a2lgroup.getGroupName());
      Set<String> paramNameTypeSet = new HashSet<>();
      // iterate the parameters in the group
      for (String paramName : a2lgroup.getLabelMap().keySet()) {
        // add param name and type with ":" as separator
        paramNameTypeSet.add(paramName + ":" + characteristicsMap.get(paramName).getType());
      }

      // add the GROUP only, if it has parameter
      if (paramNameTypeSet.isEmpty()) {
        getLogger().info(a2lgroup.getGroupName() + " not added since it has no parameter");
      }
      else {
        // create a map entry for this group
        grpInput.getGrpCharParamMap().put(a2lgroup.getGroupName(), paramNameTypeSet);
        // add group name and long name to the map
        a2lGrpMap.put(a2lgroup.getGroupName(), a2lgroup.getGroupLongName());
      }

    }
    grpInput.setA2lGrpMap(a2lGrpMap);
    grpInput.setCurrentUserId(this.a2lDataProvider.getApicDataProvider().getCurrentUser().getUserName());
    grpInput.setDefaultWPRespId(getDefaultWPResp() == null ? null : getDefaultWPResp().getID());
    grpInput.setMappingSource(this.a2lEditorDP.getMappingSourceID());
    grpInput.setPidcA2LId(this.pidcA2lId);
    grpInput.setTypeId(this.typeId);

    // A2LGroupParamServiceClient servClient = new A2LGroupParamServiceClient();
    String respMsg = "";
    // try {
    // respMsg = servClient.insertA2lGrpParams(grpInput);
    // }
    // catch (ApicWebServiceException e) {
    // getLogger().error(e.getMessage(), e);
    // }

    return CommonUtils.isEqual(respMsg, OK_RESPONSE);

  }

  /**
   * @param defaultWpResp
   * @return
   */
  private WPResponsibility getDefaultWPResp() {

    WPResponsibility defaultWpResp = null;
    // get the instance for WP resp default
    for (WPResponsibility wpResp : this.a2lDataProvider.getDataCache().getWpRespMap().values()) {
      if (CommonUtils.isEqual(wpResp.getName(), CDRConstants.WPResponsibilityEnum.RB.getDbVal())) {
        defaultWpResp = wpResp;
      }
    }

    return defaultWpResp;
  }

  /**
   * Logger to be used
   *
   * @return logger
   */
  private ILoggerAdapter getLogger() {
    return ObjectStore.getInstance().getLogger();
  }
}
