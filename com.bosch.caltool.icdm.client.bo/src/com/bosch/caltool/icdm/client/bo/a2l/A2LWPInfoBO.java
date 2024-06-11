/*
 * \ * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.a2l;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.bo.a2l.A2lResponsibilityCommon;
import com.bosch.caltool.icdm.common.bo.a2l.ParamWpRespResolver;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponentFunctions;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponents;
import com.bosch.caltool.icdm.model.a2l.A2LDetailsStructureModel;
import com.bosch.caltool.icdm.model.a2l.A2lRespMaintenanceData;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibilityModel;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefinitionModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMappingModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMappingUpdateModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.model.util.ModelUtil;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lResponsibilityServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWorkPackageServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpDefinitionVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpParamMappingServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpResponsibilityServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.UserServiceClient;

/**
 * The Class A2LWPInfoBO.
 *
 * @author apj4cob
 */
public class A2LWPInfoBO {

  /**
   * Constant to indicate WP not inherited
   */
  private static final String NO = "NO";

  /**
   * Constant to indicate WP inherited
   */
  private static final String YES = "YES";

  /** A2LFileInfoBO of loaded a2l file in editor. */
  private final A2LFileInfoBO a2lFileInfoBo;

  /** Model with data for selected A2lWpDefinitionVersion. */
  private A2lWpDefinitionModel a2lWpDefnModel = new A2lWpDefinitionModel();

  /** Model with data for active A2lWpDefinitionVersion. */
  private A2lWpDefinitionModel activeA2lWpDefnModel = new A2lWpDefinitionModel();

  /** key - A2lWpDefinitionVersion id, value - A2lWpDefinitionVersion object. */
  private Map<Long, A2lWpDefnVersion> a2lWpDefnVersMap;


  /**
   * A2LRespModel with pidc level resp and user details
   */
  private A2lResponsibilityModel a2lResponsibilityModel;

  /** Model with data for A2l Param mapping with Wp-Resp details. */
  private A2lWpParamMappingModel a2lWpParamMappingModel;

  /** Map of parameter id and A2LWpParamInfo ;row object for wp Label assignment page. */
  private final Map<Long, A2LWpParamInfo> a2lWParamInfoMap = new HashMap<>();

  /** Sorted Set values of a2lWParamInfoMap */
  private final SortedSet<A2LWpParamInfo> a2lWParamInfoSet = new TreeSet<>();

  /** Map of Function name and BC name. */

  private final Map<String, String> a2lFuncBCMap = new HashMap<>();

  /** pidc a2l id. */
  private final PidcA2LBO pidcA2lBo;

  /** bo to handle all validations. */
  private final A2lWPValidationBO a2lWpValidationBO;
  /**
   * a2l details structure model instance
   */
  private A2LDetailsStructureModel detailsStrucModel;

  /**
   * selected a2l variant group
   */
  private A2lVariantGroup selectedA2lVarGroup;

  /** to check default Level is selected in a2l details page */
  private boolean isDefaultLevel;

  private boolean isNotAssignedVarGrp;

  private int currentPage = 3;
  /**
   * Map of A2lWorkPackage objects which are mapped to the PidcVersion . Key - A2l Work Package id, Value -
   * A2lWorkPackage
   */
  private Map<Long, A2lWorkPackage> wpMapppedToPidcVers = new HashMap<>();
  /**
   * Key - Variant Group id, value Set of A2LWpParamInfo objects assigned to the var grp
   */
  private final Map<Long, Map<Long, A2LWpParamInfo>> a2lWParamInfoForVarGrp = new HashMap<>();


  /**
   * Key - Variant Group id, value Set of A2LWpParamInfo objects
   */
  private final Map<Long, Map<Long, A2LWpParamInfo>> virtualRecordsMap = new HashMap<>();

  private ParamWpRespResolver paramWpRespResolver;

  private boolean reconstructA2lWpParamMapApplicable = true;


  /**
   * Instantiates a new a 2 LWP info BO.
   *
   * @param a2lfileInfoBo A2LFileInfoBO
   * @param pidcA2lBo the pidc A 2 l bo
   */
  public A2LWPInfoBO(final A2LFileInfoBO a2lfileInfoBo, final PidcA2LBO pidcA2lBo) {
    super();
    this.a2lFileInfoBo = a2lfileInfoBo;
    this.pidcA2lBo = pidcA2lBo;
    this.a2lWpValidationBO = new A2lWPValidationBO(this);
  }

  /**
   * load WP Defn versions for a2l.
   *
   * @return key - A2lWpDefinitionVersion id, value - A2lWpDefinitionVersion object
   */
  public Map<Long, A2lWpDefnVersion> loadWPDefnVersionsForA2l() {
    try {
      setA2lWpDefnVersMap(
          new A2lWpDefinitionVersionServiceClient().getWPDefnVersForPidcA2l(this.pidcA2lBo.getPidcA2lId()));
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return getA2lWpDefnVersMap();
  }


  /**
   * method to load the workpackages that are mapped to a pidcversion
   */
  public void loadWpMappedToPidcVers() {
    try {
      setWpMapppedToPidcVers(
          new A2lWorkPackageServiceClient().getWpByPidcVers(this.pidcA2lBo.getPidcVersion().getId()));
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * load PIDC WP Resp for pidc.
   *
   * @return A2lResponsibilityModel object
   */
  public A2lResponsibilityModel loadRespForPidc() {
    try {
      // to check if A2lResponsibilityModel is already loaded
      if (CommonUtils.isNull(this.a2lResponsibilityModel)) {
        setA2lResponsibilityModel(
            new A2lResponsibilityServiceClient().getByPidc(this.pidcA2lBo.getPidcA2l().getProjectId()));
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return getA2lResponsibilityModel();
  }

  /**
   * Method to intialize both defn and param model based on version selection
   *
   * @param selectedA2lWpDefnVersId as Input
   * @param forceReloadA2lWpDef force reload a2l wp definition version
   * @param forceReloadParamModel force reload a2l wp param mappings
   */
  public void initializeModelBasedOnWpDefVers(final Long selectedA2lWpDefnVersId, final boolean forceReloadA2lWpDef,
      final boolean forceReloadParamModel) {
    loadRespForPidc();
    loadA2lWpDefnModel(selectedA2lWpDefnVersId, forceReloadA2lWpDef);
    loadActiveA2lWpDefnModel();
    loadA2lWpParamModel(selectedA2lWpDefnVersId, forceReloadParamModel);
    setSelectedA2lVarGroup(null);
  }


  /**
   * Load A 2 l wp defn model.
   *
   * @param selectedA2lWpDefnVersId - selected wp defnvers id
   * @param forceReload
   */
  private void loadA2lWpDefnModel(final Long selectedA2lWpDefnVersId, final boolean forceReload) {
    Long a2lWpDefVersId = null;
    try {
      // first time , choose working set as selected a2l wp defn version
      if (selectedA2lWpDefnVersId == null) {
        A2lWpDefnVersion wrkingSet = getWorkingSet();
        if (wrkingSet != null) {
          a2lWpDefVersId = wrkingSet.getId();
        }
      }
      else {
        a2lWpDefVersId = selectedA2lWpDefnVersId;
      }
      if (a2lWpDefVersId != null) {
        boolean isAlreadyLoaded =
            (this.a2lWpDefnModel != null) && (this.a2lWpDefnModel.getSelectedWpDefnVersionId() != null) &&
                (this.a2lWpDefnModel.getSelectedWpDefnVersionId().longValue() == a2lWpDefVersId.longValue());
        if (!isAlreadyLoaded || forceReload) {
          setA2lWpDefnModel(new A2lWpResponsibilityServiceClient().getA2lWpRespForWpDefnVers(a2lWpDefVersId));
          constructA2lWpDefRespMap(getA2lWpDefnModel());
        }
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  private void loadActiveA2lWpDefnModel() {
    try {
      A2lWpDefnVersion activeVers = getActiveVers();
      setActiveA2lWpDefnModel(new A2lWpResponsibilityServiceClient().getA2lWpRespForWpDefnVers(activeVers.getId()));
      constructA2lWpDefRespMap(getActiveA2lWpDefnModel());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * Load A 2 l wp param model.
   *
   * @param selectedA2lWpDefnVersId - selected wp defnvers id
   * @param fetchParamModel
   */
  private void loadA2lWpParamModel(final Long selectedA2lWpDefnVersId, final boolean fetchParamModel) {
    Long a2lWpDefVersId = null;

    try {
      // first time , choose working set as selected a2l wp defn version
      if (selectedA2lWpDefnVersId == null) {
        A2lWpDefnVersion wrkingSet = getWorkingSet();
        if (wrkingSet != null) {
          a2lWpDefVersId = wrkingSet.getId();
        }
      }
      else {
        a2lWpDefVersId = selectedA2lWpDefnVersId;
      }

      // load model only if not already loaded
      if ((a2lWpDefVersId != null)) {

        boolean isAlreadyLoaded = (this.a2lWpParamMappingModel != null) &&
            (this.a2lWpParamMappingModel.getSelectedWpDefnVersionId().longValue() == a2lWpDefVersId.longValue());
        if (!isAlreadyLoaded || fetchParamModel) {
          this.a2lWpParamMappingModel = new A2lWpParamMappingServiceClient().getAllByWpDefVersId(a2lWpDefVersId);
          loadA2lWpParamMappingDet();
          constructA2lWpParamRespMap();
          formVirtualRecords();
        }
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }


  /**
   * @param a2lWpParamMappingModel
   */
  public void constructA2lWpParamRespMap() {

    for (A2LWpParamInfo a2lWpParamInfo : this.a2lWParamInfoMap.values()) {
      if (isA2lAndWpRespAvailInParamInfo(a2lWpParamInfo)) {
        A2lResponsibility a2lResp =
            getA2lResponsibilityModel().getA2lResponsibilityMap().get(a2lWpParamInfo.getA2lRespId());
        A2lWpResponsibility a2lWpResponsibility = getA2lWpDefnModel().getWpRespMap().get(a2lWpParamInfo.getWpRespId());
        if ((a2lWpResponsibility != null) && (a2lResp != null)) {
          constructParamIdMap(this.a2lWpParamMappingModel.getParamIdWithWpAndRespMap(), a2lWpParamInfo,
              a2lWpResponsibility,
              getRespName(this.a2lWpParamMappingModel.getA2lWPResponsibleMap(), a2lResp, a2lWpResponsibility));
        }
      }
    }
  }


  /**
   * @param paramInfo
   * @return A2lWpParamMapping from map or create virtual record and return
   */
  public A2lWpParamMapping getA2lWpParamMappingObject(final A2LWpParamInfo paramInfo) {
    if (paramInfo.getA2lWpParamMappingId() != null) {
      return getA2lWpParamMappingModel().getA2lWpParamMapping().get(paramInfo.getA2lWpParamMappingId()).clone();
    }

    A2lWpParamMapping a2lWpParamMapping = new A2lWpParamMapping();
    a2lWpParamMapping.setParamId(paramInfo.getParamId());
    a2lWpParamMapping.setName(paramInfo.getParamName());
    a2lWpParamMapping.setWpRespId(paramInfo.getWpRespId());
    a2lWpParamMapping.setWpDefnVersionId(getA2lWpDefnModel().getWpRespMap().get(paramInfo.getWpRespId()).getId());
    a2lWpParamMapping.setWpNameCust(paramInfo.getWpNameCust());
    a2lWpParamMapping.setWpRespInherited(paramInfo.isWpRespInherited());
    a2lWpParamMapping.setWpNameCustInherited(paramInfo.isWpNameInherited());
    a2lWpParamMapping.setParA2lRespId(paramInfo.getA2lRespId());

    return a2lWpParamMapping;
  }


  /**
   * @param wpRespMap
   * @param a2lResp
   * @param a2lWpResponsibility
   * @param respName
   * @return
   */
  public String getRespName(final Map<String, SortedSet<A2lWpResponsibility>> wpRespMap,
      final A2lResponsibility a2lResp, final A2lWpResponsibility a2lWpResponsibility) {
    String respName = a2lResp.getName();
    A2lWpResponsibility wpResponsibility = a2lWpResponsibility.clone();
    if (wpRespMap.containsKey(respName)) {
      wpResponsibility.setMappedWpRespName(respName);
      wpRespMap.get(respName).add(wpResponsibility);
    }
    else {
      wpResponsibility.setMappedWpRespName(respName);
      SortedSet<A2lWpResponsibility> a2lWpRespPals = new TreeSet<>();
      a2lWpRespPals.add(wpResponsibility);
      wpRespMap.put(respName, a2lWpRespPals);
    }
    return respName;
  }

  /**
   * @param paramIdWithWpAndRespMap
   * @param a2lWpParamInfo
   * @param a2lWpResponsibility
   * @param respName
   */
  public void constructParamIdMap(final Map<Long, Map<Long, String>> paramIdWithWpAndRespMap,
      final A2LWpParamInfo a2lWpParamInfo, final A2lWpResponsibility a2lWpResponsibility, final String respName) {
    // For Construction of param id map based on Wp
    fillParamAndRespMap(a2lWpParamInfo, a2lWpResponsibility);
    if (paramIdWithWpAndRespMap.containsKey(a2lWpParamInfo.getParamId())) {
      paramIdWithWpAndRespMap.get(a2lWpParamInfo.getParamId()).put(a2lWpResponsibility.getId(), respName);
    }
    else {
      Map<Long, String> wpIdRespMap = new HashMap<>();
      wpIdRespMap.put(a2lWpResponsibility.getId(), respName);
      paramIdWithWpAndRespMap.put(a2lWpParamInfo.getParamId(), wpIdRespMap);
    }
  }

  /**
   * @param a2lWpParamInfo
   * @param a2lWpResponsibility
   */
  private void fillParamAndRespMap(final A2LWpParamInfo a2lWpParamInfo, final A2lWpResponsibility a2lWpResponsibility) {
    // loaded for showing param count based on the responsibility from outline selection in Data Review Report
    Set<Long> paramIdSet;
    if (null == this.a2lWpParamMappingModel.getParamAndRespPalMap().get(a2lWpResponsibility.getId())) {
      paramIdSet = new HashSet<>();
    }
    else {
      paramIdSet = this.a2lWpParamMappingModel.getParamAndRespPalMap().get(a2lWpResponsibility.getId());
    }
    paramIdSet.add(a2lWpParamInfo.getParamId());
    this.a2lWpParamMappingModel.getParamAndRespPalMap().put(a2lWpResponsibility.getId(), paramIdSet);
  }


  /**
   * @param a2lWpDefinitionModel
   */
  public void constructA2lWpDefRespMap(final A2lWpDefinitionModel a2lWpDefinitionModel) {
    Map<String, SortedSet<A2lWpResponsibility>> wpRespMap = new HashMap<>();
    Map<String, Set<A2lWpResponsibility>> a2lWpRespNodeMergedMap = new HashMap<>();

    for (A2lWpResponsibility wpRespPal : a2lWpDefinitionModel.getWpRespMap().values()) {
      if ((wpRespPal.getA2lRespId() != null) &&
          getA2lResponsibilityModel().getA2lResponsibilityMap().containsKey(wpRespPal.getA2lRespId())) {
        A2lResponsibility a2lResp = getA2lResponsibilityModel().getA2lResponsibilityMap().get(wpRespPal.getA2lRespId());
        if (a2lResp != null) {
          getRespName(wpRespMap, a2lResp, wpRespPal);
        }
      }
      fillA2lWpRespMergedMap(a2lWpRespNodeMergedMap, wpRespPal);
    }
    a2lWpDefinitionModel.setA2lWpRespNodeMergedMap(a2lWpRespNodeMergedMap);
    a2lWpDefinitionModel.setA2lWPResponsibleMap(wpRespMap);
  }

  /**
   * @param a2lWpRespNodeMergedMap
   * @param wpRespPal
   */
  private void fillA2lWpRespMergedMap(final Map<String, Set<A2lWpResponsibility>> a2lWpRespNodeMergedMap,
      final A2lWpResponsibility wpRespPal) {
    if (a2lWpRespNodeMergedMap.containsKey(wpRespPal.getName())) {
      a2lWpRespNodeMergedMap.get(wpRespPal.getName()).add(wpRespPal);
    }
    else {
      Set<A2lWpResponsibility> wpRespPalSet = new HashSet<>();
      wpRespPalSet.add(wpRespPal);
      a2lWpRespNodeMergedMap.put(wpRespPal.getName(), wpRespPalSet);
    }
  }


  /**
   * @param paramId parameter id
   * @return true if parameter has records in variant group level (in DB)
   */
  public boolean paramHasVgLevelRecords(final Long paramId) {
    if ((getParamWpRespResolver() != null) &&
        CommonUtils.isNotEmpty(getParamWpRespResolver().getParamMappingAtVarLevel()) &&
        (getParamWpRespResolver().getParamMappingAtVarLevel().size() > 1)) {
      for (Long varGrpId : getA2lWpDefnModel().getA2lVariantGroupMap().keySet()) {
        if (CommonUtils.isNotEmpty(getParamWpRespResolver().getParamMappingAtVarLevel().get(varGrpId)) &&
            getParamWpRespResolver().getParamMappingAtVarLevel().get(varGrpId).keySet().contains(paramId)) {
          return true;
        }
      }
    }
    return false;
  }


  /**
   * Gets the working set.
   *
   * @return A2lWpDefinitionVersion for which version is 0 - working set
   */
  public A2lWpDefnVersion getWorkingSet() {
    for (A2lWpDefnVersion a2lWpDefinitionVersion : getA2lWpDefnVersMap().values()) {
      if (a2lWpDefinitionVersion.getVersionNumber() == 0L) {
        return a2lWpDefinitionVersion;
      }
    }
    return null;
  }

  /**
   * @return A2lWpDefinitionVersion
   */
  public A2lWpDefnVersion getActiveVers() {
    for (A2lWpDefnVersion a2lWpDefinitionVersion : getA2lWpDefnVersMap().values()) {
      if (a2lWpDefinitionVersion.isActive()) {
        return a2lWpDefinitionVersion;
      }

    }
    return null;
  }

  /**
   * @return the working set id
   */
  public Long getWorkingSetId() {
    A2lWpDefnVersion workingSet = getWorkingSet();
    return workingSet == null ? null : workingSet.getId();
  }


  /**
   * Checks if is editable.
   *
   * @return true if editable
   */
  public boolean isEditable() {
    return isWPInfoModifiable() && isWorkingSet(getA2lWpDefnModel().getSelectedWpDefnVersionId());
  }

  /**
   * @return true , if user has access rights to import from groups and is working set
   */
  public boolean canImportGroups() {
    return hasAccessToImportFromGrps() && isWorkingSet(getA2lWpDefnModel().getSelectedWpDefnVersionId());
  }

  /**
   * @return true if 1. user has only read access on pidc , allow import only when there are no manual changes in a2l (
   *         only default wp in working set and one active version 2. if user has apric write or owner access to pidc
   */
  private boolean hasAccessToImportFromGrps() {
    NodeAccess currentUserAccessRights = null;
    CurrentUserBO currentUserBO = new CurrentUserBO();
    try {
      if (currentUserBO.hasApicWriteAccess()) {
        return true;
      }
      currentUserAccessRights = currentUserBO.getNodeAccessRight(this.pidcA2lBo.getPidcVersion().getPidcId());
      if (currentUserAccessRights != null) {

        return (currentUserAccessRights.isRead() && (getA2lWpDefnModel().getWpRespMap().size() == 1) &&
            (getA2lWpDefnVersMap().size() == 2)) || (currentUserAccessRights.isOwner());
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return false;
  }

  public boolean isCustomizable(final A2lWpResponsibility selobject) {
    if (getSelectedA2lVarGroup() == null) {
      return false;
    }
    else if (selobject.getVariantGrpId() != null) {
      return isEditable() && (!selobject.getVariantGrpId().equals(getSelectedA2lVarGroup().getId()));
    }
    else {
      return isEditable();
    }

  }

  /**
   * return true , if variant group is matching or wp resp is in pidc level
   *
   * @param a2lWpResponsibility A2lWpResponsibility
   * @return true if row object matches selection in a2l details view
   */
  public boolean isMappedToSelectedVarGrp(final A2lWpResponsibility a2lWpResponsibility) {
    if (getSelectedA2lVarGroup() != null) {
      return getSelectedA2lVarGroup().getId().equals(a2lWpResponsibility.getVariantGrpId());
    }
    // check while selecting Default variant group in details view
    return a2lWpResponsibility.getVariantGrpId() == null;
  }

  /**
   * @param wpParamInfoObj A2LWpParamInfo
   * @return true if row object matches selection in a2l details view
   */
  public boolean isParamMappedToSelectedVarGrp(final A2LWpParamInfo wpParamInfoObj) {
    A2lWpResponsibility a2lWpResponsibility = getWpRespPalForA2lWpMapping(wpParamInfoObj);
    if (null != a2lWpResponsibility) {
      return isMappedToSelectedVarGrp(a2lWpResponsibility);
    }
    return false;
  }

  /**
   * Checks if is a 2 l wp param map modifiable.
   *
   * @return true if editable
   */
  public boolean isA2lWpParamMapModifiable() {
    if (getA2lWpParamMappingModel() != null) {
      return isParamLevelMappingAllowed() && isWPInfoModifiable() &&
          isWorkingSet(getA2lWpParamMappingModel().getSelectedWpDefnVersionId());
    }
    return false;
  }

  /**
   * @return true if the user has access rights to modify the param and selected WpDefnVers is working set
   */
  public boolean isParamEditAllowed() {
    if (getA2lWpParamMappingModel() != null) {
      return isWPInfoModifiable() && isWorkingSet(getA2lWpParamMappingModel().getSelectedWpDefnVersionId());
    }
    return false;
  }

  /**
   * @param wpResp A2lWpResponsibility
   * @return RespType display name
   */
  public String getRespTypeName(final A2lWpResponsibility wpResp) {
    WpRespType respType = getRespType(wpResp);
    return respType == null ? "" : respType.getDispName();
  }

  /**
   * Get the responsibility type string for the given responsibility
   *
   * @param a2lResp A2lResponsibility
   * @return String type display name
   */
  public String getRespTypeName(final A2lResponsibility a2lResp) {
    WpRespType type = A2lResponsibilityCommon.getRespType(a2lResp);
    return type == null ? "" : type.getDispName();
  }

  /**
   * Get the responsibility type for the given WP responsibility record
   *
   * @param wpResp A2lWpResponsibility
   * @return WpRespEnum type
   */
  public WpRespType getRespType(final A2lWpResponsibility wpResp) {
    A2lResponsibility a2lResp = this.a2lResponsibilityModel.getA2lResponsibilityMap().get(wpResp.getA2lRespId());
    return A2lResponsibilityCommon.getRespType(a2lResp);
  }

  /**
   * Get the responsibility type for the given WP responsibility record
   *
   * @param a2lRespId Long
   * @return WpRespEnum type
   */
  public WpRespType getRespType(final Long a2lRespId) {
    A2lResponsibility a2lResp = this.a2lResponsibilityModel.getA2lResponsibilityMap().get(a2lRespId);
    return A2lResponsibilityCommon.getRespType(a2lResp);
  }

  /**
   * @param selWpResp A2lWpResponsibility
   * @return variant group name
   */
  public String getVarGrpName(final A2lWpResponsibility selWpResp) {
    Map<Long, A2lVariantGroup> a2lVariantGrpMap = getDetailsStrucModel().getA2lVariantGrpMap();
    if ((selWpResp.getVariantGrpId() != null) && (a2lVariantGrpMap != null) &&
        (a2lVariantGrpMap.get(selWpResp.getVariantGrpId()) != null)) {
      return a2lVariantGrpMap.get(selWpResp.getVariantGrpId()).getName();
    }
    return "";
  }

  /**
   * @return can show AvailWps
   */
  public boolean canShowAvailWps() {
    return isParamEditAllowed();
  }

  /**
   * Flag to check whether edit is allowed in WP Label Assignment Page
   *
   * @return boolean
   */
  public boolean isParamLevelMappingAllowed() {
    if (null != getA2lWpDefnModel()) {
      return getA2lWpDefnModel().isParamMappingAllowed();
    }
    return false;
  }

  /**
   * Checks if is working set.
   *
   * @param a2lWpDefVersId - PK of A2lWpDefinitionVersion
   * @return true if a2lwpDefnvers id is working set
   */
  public boolean isWorkingSet(final Long a2lWpDefVersId) {
    return (a2lWpDefVersId != null) && (getA2lWpDefnVersMap().get(a2lWpDefVersId).getVersionNumber() == 0L);
  }

  /**
   * Checks if is WP info modifiable.
   *
   * @return true if wp edit is possible (if pidc owner)
   */
  public boolean isWPInfoModifiable() {
    NodeAccess currentUserAccessRights = null;
    try {
      currentUserAccessRights = new CurrentUserBO().getNodeAccessRight(this.pidcA2lBo.getPidcVersion().getPidcId());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return (null != currentUserAccessRights) && currentUserAccessRights.isOwner() &&
        this.pidcA2lBo.getPidcA2l().isActive();
  }


  /**
   * Form a map To set BC Name in row object.
   */
  public void loadFuncBcMap() {
    for (A2LBaseComponents a2lBc : this.a2lFileInfoBo.getA2lBCInfo()) {
      for (A2LBaseComponentFunctions func : a2lBc.getFunctionMap().values()) {
        this.a2lFuncBCMap.put(func.getName(), a2lBc.getBcName());

      }
    }
  }

  /**
   * Initialise A 2 LW param info.
   *
   * @param paramMap A2L Parameter Map
   */
  public void initialiseA2LWParamInfo(final Map<String, A2LParameter> paramMap) {
    for (A2LParameter a2lParam : paramMap.values()) {
      A2LWpParamInfo a2lWParamInfo = new A2LWpParamInfo(a2lParam);
      a2lWParamInfo.setBcName(this.a2lFuncBCMap.get(a2lWParamInfo.getFuncName()));
      if ((a2lParam.getParamId() == null) || (a2lParam.getParamId() == 0L)) {
        CDMLogger.getInstance().debug("Id not found for the A2l Parameter : " + a2lParam.getName());
      }
      else {
        this.a2lWParamInfoMap.put(a2lParam.getParamId(), a2lWParamInfo);
        this.a2lWParamInfoSet.add(a2lWParamInfo);
      }
    }
  }

  /**
   * To set A2lWpParamMApping in row object.
   */
  private void loadA2lWpParamMappingDet() {
    if (CommonUtils.isNotNull(this.a2lWpParamMappingModel)) {
      for (A2lWpParamMapping a2lWpParamMapping : this.a2lWpParamMappingModel.getA2lWpParamMapping().values()) {
        if (CommonUtils.isNotNull(a2lWpParamMapping)) {
          A2LWpParamInfo a2lWpParamInfo = this.a2lWParamInfoMap.get(a2lWpParamMapping.getParamId());
          setMappingDetInRow(a2lWpParamMapping, a2lWpParamInfo);
        }
      }
    }
  }

  /**
   * @param a2lWpParamMapping A2lWpParamMapping
   * @param a2lWpParamInfo A2LWpParamInfo
   */
  public void setMappingDetInRow(final A2lWpParamMapping a2lWpParamMapping, final A2LWpParamInfo a2lWpParamInfo) {
    A2lWpResponsibility a2lWpResp = this.a2lWpDefnModel.getWpRespMap().get(a2lWpParamMapping.getWpRespId());
    if (CommonUtils.isNotNull(a2lWpParamInfo) && CommonUtils.isNotNull(a2lWpResp) &&
        (a2lWpResp.getVariantGrpId() == null)) {

      a2lWpParamInfo.setA2lWpParamMappingId(a2lWpParamMapping.getId());
      a2lWpParamInfo.setWpRespId(a2lWpParamMapping.getWpRespId());
      a2lWpParamInfo.setWpNameInherited(a2lWpParamMapping.isWpNameCustInherited());
      a2lWpParamInfo.setWpRespInherited(a2lWpParamMapping.isWpRespInherited());
      a2lWpParamInfo.setVariantGroupId(a2lWpResp.getVariantGrpId());
      if (a2lWpParamMapping.isWpNameCustInherited()) {
        a2lWpParamInfo.setWpNameCust(a2lWpResp.getWpNameCust());
        a2lWpParamInfo.setA2lRespId(a2lWpResp.getA2lRespId());
      }
      else {
        a2lWpParamInfo.setWpNameCust(a2lWpParamMapping.getWpNameCust());
        a2lWpParamInfo.setA2lRespId(a2lWpParamMapping.getParA2lRespId());
      }
      setWpRespUser(a2lWpParamInfo, a2lWpParamMapping);
    }
  }


  /**
   * @param a2lWpParamInfo
   * @param a2lWpParamMapping
   */
  private void setWpRespUser(final A2LWpParamInfo a2lWpParamInfo, final A2lWpParamMapping a2lWpParamMapping) {
    if (a2lWpParamMapping.isWpRespInherited()) {
      A2lWpResponsibility a2lResp = this.a2lWpDefnModel.getWpRespMap().get(a2lWpParamMapping.getWpRespId());
      a2lWpParamInfo.setA2lRespId(a2lResp.getA2lRespId());
    }
    else {
      a2lWpParamInfo.setA2lRespId(a2lWpParamMapping.getParA2lRespId());
    }

  }

  /**
   * @param wpResp a2l Wp Responsibility
   * @return full name
   */
  public String getLFullName(final A2lWpResponsibility wpResp) {
    if (wpResp.getA2lRespId() != null) {
      A2lResponsibility a2lResp = getA2lResponsibilityModel().getA2lResponsibilityMap().get(wpResp.getA2lRespId());
      return getLFullName(a2lResp);
    }

    return "";
  }

  /**
   * @param a2lResp as Input
   * @return full name of customer
   */
  public String getLFullName(final A2lResponsibility a2lResp) {
    return a2lResp.getName();
  }


  /**
   * Gets the a 2 l file info bo.
   *
   * @return the a2lfileInfoBo
   */
  public A2LFileInfoBO getA2lFileInfoBo() {
    return this.a2lFileInfoBo;
  }

  /**
   * Gets the a 2 l wp defn model.
   *
   * @return the a2lWpDefnModel
   */
  public A2lWpDefinitionModel getA2lWpDefnModel() {
    return this.a2lWpDefnModel;
  }


  /**
   * Sets the a 2 l wp defn model.
   *
   * @param a2lWpDefnModel the a2lWpDefnModel to set
   */
  public void setA2lWpDefnModel(final A2lWpDefinitionModel a2lWpDefnModel) {
    this.a2lWpDefnModel = a2lWpDefnModel;
  }


  /**
   * Gets the a 2 l wp defn vers map.
   *
   * @return the a2lWpDefnVersMap
   */
  public Map<Long, A2lWpDefnVersion> getA2lWpDefnVersMap() {
    return this.a2lWpDefnVersMap;
  }


  /**
   * Sets the A 2 l wp defn vers map.
   *
   * @param a2lWpDefnVersMap the a2lWpDefnVersMap to set
   */
  public void setA2lWpDefnVersMap(final Map<Long, A2lWpDefnVersion> a2lWpDefnVersMap) {
    this.a2lWpDefnVersMap = a2lWpDefnVersMap;
  }

  /**
   * Gets the a 2 l wp param mapping model.
   *
   * @return the a2lWpParamMappingModel
   */
  public A2lWpParamMappingModel getA2lWpParamMappingModel() {
    return this.a2lWpParamMappingModel;
  }

  /**
   * Sets the a 2 l wp param mapping model.
   *
   * @param a2lWpParamMappingModel the a2lWpParamMappingModel to set
   */
  public void setA2lWpParamMappingModel(final A2lWpParamMappingModel a2lWpParamMappingModel) {
    this.a2lWpParamMappingModel = a2lWpParamMappingModel;
  }


  /**
   * Gets the a 2 l W param info map.
   *
   * @return the a2lWParamInfoMap
   */
  public Map<Long, A2LWpParamInfo> getA2lWParamInfoMap() {
    return this.a2lWParamInfoMap;
  }

  /**
   * Gets the a 2 l func BC map.
   *
   * @return the a2lFuncBCMap
   */
  public Map<String, String> getA2lFuncBCMap() {
    return this.a2lFuncBCMap;
  }

  /**
   * Gets the pidc A 2 l bo.
   *
   * @return the pidcA2lBo
   */
  public PidcA2LBO getPidcA2lBo() {
    return this.pidcA2lBo;
  }

  /**
   * Gets the a 2 l wp validation BO.
   *
   * @return the a2lWpValidationBO
   */
  public A2lWPValidationBO getA2lWpValidationBO() {
    return this.a2lWpValidationBO;
  }

  /**
   * The Enum SortColumns.
   *
   * @author apj4cob
   */
  public enum SortColumns {

                           /** Sort Based On the Parameter Name. */
                           SORT_PARAM_NAME,

                           /** Sort Based on Function name. */
                           SORT_FUNC_NAME,

                           /** Sort Based on Function Version. */
                           SORT_FUNC_VERSION,

                           /** Sort Based on BC Name. */
                           SORT_BC_NAME,

                           /** Sort Based on Workpackage Name. */
                           SORT_WP_NAME,

                           /** Sort Based on Responsibility name. */
                           SORT_RESP_NAME,

                           /** Sort Based on Name at Customer. */
                           SORT_WP_CUST_NAME,

                           /** Sort Based on Compliance Flag. */
                           SORT_PARAM_TYPE_COMPLIANCE,

                           /** Sort Based on Responsibility Type. */
                           SORT_RESP_TYPE,

                           /** Sort Based on Inherited from WP. */
                           SORT_WP_INHERITED,

                           /** Sort Based on Created Date. */
                           SORT_CREATED_DATE,

                           /** Sort Based on Modified Date. */
                           SORT_MODIFIED_DATE,

                           /** Sort Based on Variant Group. */
                           SORT_VARIANT_GROUP,

  }

  /**
   * Compare to.
   *
   * @param a2lWpParam1 param1
   * @param a2lWpParam2 param2
   * @param sortColumn sortColumn
   * @return the Compare Int
   */
  public int compareTo(final A2LWpParamInfo a2lWpParam1, final A2LWpParamInfo a2lWpParam2,
      final SortColumns sortColumn) {
    int compareResult;
    switch (sortColumn) {
      case SORT_PARAM_TYPE_COMPLIANCE:
        compareResult = ModelUtil.compare(a2lWpParam1.isComplianceParam(), a2lWpParam2.isComplianceParam());
        break;
      // ICDM-2439
      case SORT_PARAM_NAME:
        // use compare method for Strings
        compareResult = ModelUtil.compare(a2lWpParam1.getParamName(), a2lWpParam2.getParamName());
        break;

      case SORT_FUNC_NAME:
        compareResult = ModelUtil.compare(a2lWpParam1.getFuncName(), a2lWpParam2.getFuncName());
        break;

      case SORT_FUNC_VERSION:
        compareResult = ModelUtil.compare(a2lWpParam1.getFunctionVer(), a2lWpParam2.getFunctionVer());
        break;

      case SORT_BC_NAME:
        compareResult = ModelUtil.compare(a2lWpParam1.getBcName(), a2lWpParam2.getBcName());
        break;

      case SORT_WP_NAME:
        // use compare method for Strings
        compareResult = ModelUtil.compare(getWPName(a2lWpParam1), getWPName(a2lWpParam2));
        break;
      case SORT_WP_INHERITED:
        compareResult = ModelUtil.compare(getWpRespInherited(a2lWpParam1), getWpRespInherited(a2lWpParam2));
        break;
      /* icdm-469 */
      case SORT_RESP_TYPE:
        compareResult = ModelUtil.compare(getWpRespType(a2lWpParam1), getWpRespType(a2lWpParam2));
        break;
      case SORT_RESP_NAME:
        compareResult = ModelUtil.compare(getWpRespUser(a2lWpParam1), getWpRespUser(a2lWpParam2));
        break;
      case SORT_WP_CUST_NAME:
        compareResult = ModelUtil.compare(getWpNameCust(a2lWpParam1), getWpNameCust(a2lWpParam2));
        break;
      case SORT_VARIANT_GROUP:
        // to sort in descending order, that is to display all wp resp that are mapped in var grp first
        compareResult = ModelUtil.compare(getVariantGrpId(a2lWpParam2), getVariantGrpId(a2lWpParam1));
        break;
      default:
        compareResult = 0;
        break;
    }
    // additional compare column is the name of the characteristic
    if (compareResult == 0) {
      // compare result is equal, compare the parameter name
      compareResult = ModelUtil.compare(a2lWpParam1.getParamName(), a2lWpParam2.getParamName());
    }
    return compareResult;
  }

  /**
   * @param a2lWpParam
   * @return
   */
  private Long getVariantGrpId(final A2LWpParamInfo a2lWpParam) {
    A2lWpResponsibility a2lWpResponsibility = getWpRespPalForA2lWpMapping(a2lWpParam);
    if (null != a2lWpResponsibility) {
      return a2lWpResponsibility.getVariantGrpId();
    }
    return null;
  }

  /**
   * Gets the wp resp pal for A 2 l wp mapping.
   *
   * @param a2lWpParam A2LWpParamInfo
   * @return a2lWpResponsibility
   */
  public A2lWpResponsibility getWpRespPalForA2lWpMapping(final A2LWpParamInfo a2lWpParam) {
    return (null != this.a2lWpDefnModel) ? this.a2lWpDefnModel.getWpRespMap().get(a2lWpParam.getWpRespId()) : null;
  }

  /**
   * Gets the Workpackage Name
   *
   * @param a2lWpParamInfo A2LWpParamInfo
   * @return String
   */
  public String getWPName(final A2LWpParamInfo a2lWpParamInfo) {
    A2lWpResponsibility a2lWpResponsibility = getWpRespPalForA2lWpMapping(a2lWpParamInfo);
    if (null != a2lWpResponsibility) {
      return a2lWpResponsibility.getName();
    }
    return "";
  }

  /**
   * Gets the wp responsible name.
   *
   * @param a2lWpParamInfo A2LWpParamInfo
   * @return String
   */
  public String getWpRespUser(final A2LWpParamInfo a2lWpParamInfo) {
    A2lResponsibility a2lResp = getWpResp(a2lWpParamInfo);
    if (CommonUtils.isNotNull(a2lResp)) {
      return a2lResp.getName();
    }

    return "";

  }

  /**
   * @param a2lWpParamInfo
   * @return
   */
  public A2lResponsibility getWpResp(final A2LWpParamInfo a2lWpParamInfo) {
    if (a2lWpParamInfo.isWpRespInherited()) {
      A2lWpResponsibility wpRespPal = getWpRespPalForA2lWpMapping(a2lWpParamInfo);
      if ((null != wpRespPal) && (null != wpRespPal.getA2lRespId())) {
        return getA2lResponsibilityModel().getA2lResponsibilityMap().get(wpRespPal.getA2lRespId());
      }
    }
    else {
      if (null != a2lWpParamInfo.getA2lRespId()) {
        return getA2lResponsibilityModel().getA2lResponsibilityMap().get(a2lWpParamInfo.getA2lRespId());
      }
    }

    return null;
  }

  /**
   * Resp name to be used in export
   *
   * @param a2lWpParamInfo a2lWpParamInfo
   * @return Resp name
   */
  public String getWpRespAliasName(final A2LWpParamInfo a2lWpParamInfo) {
    A2lResponsibility a2lResp = getWpResp(a2lWpParamInfo);
    if (CommonUtils.isNotNull(a2lResp)) {
      return a2lResp.getAliasName();
    }

    return "";
  }

  /**
   * Gets the responsibility type
   *
   * @param a2lWpParamInfo A2LWpParamInfo
   * @return String
   */
  public String getWpRespType(final A2LWpParamInfo a2lWpParamInfo) {
    A2lResponsibility a2lResp = null;

    if (a2lWpParamInfo.isWpRespInherited()) {
      A2lWpResponsibility wpRespPal = getWpRespPalForA2lWpMapping(a2lWpParamInfo);
      if (wpRespPal != null) {
        a2lResp = getA2lResponsibilityModel().getA2lResponsibilityMap().get(wpRespPal.getA2lRespId());
      }
    }
    else {
      a2lResp = getA2lResponsibilityModel().getA2lResponsibilityMap().get(a2lWpParamInfo.getA2lRespId());
    }

    return getRespTypeName(a2lResp);
  }

  /**
   * Gets the Wp Name at Customer
   *
   * @param a2lWpParamInfo A2LWpParamInfo
   * @return String
   */
  public String getWpNameCust(final A2LWpParamInfo a2lWpParamInfo) {
    A2lWpParamMapping a2lWpParamMapping = (null != getA2lWpParamMappingModel())
        ? getA2lWpParamMappingModel().getA2lWpParamMapping().get(a2lWpParamInfo.getA2lWpParamMappingId()) : null;
    if ((null != a2lWpParamMapping)) {
      A2lWpResponsibility a2lWpResponsibility = getWpRespPalForA2lWpMapping(a2lWpParamInfo);
      return a2lWpParamMapping.isWpNameCustInherited() ? getWpNameAtCustFromPal(a2lWpResponsibility)
          : a2lWpParamMapping.getWpNameCust();
    }
    A2lWpResponsibility a2lWpResponsibility = getA2lWpDefnModel().getWpRespMap().get(a2lWpParamInfo.getWpRespId());
    if (null != a2lWpResponsibility) {
      return a2lWpResponsibility.getWpNameCust();
    }
    return "";
  }

  /**
   * @param a2lWpResponsibility
   * @return
   */
  private String getWpNameAtCustFromPal(final A2lWpResponsibility a2lWpResponsibility) {
    return (null != a2lWpResponsibility) ? a2lWpResponsibility.getWpNameCust() : "";
  }

  /**
   * @param a2lWpParamInfo A2LWpParamInfo
   * @return String
   */
  public String getWpRespInherited(final A2LWpParamInfo a2lWpParamInfo) {
    // No mapping return empty string
    if (null == a2lWpParamInfo.getWpRespId()) {
      return "";
    }
    if (a2lWpParamInfo.isWpRespInherited()) {
      return YES;
    }
    return NO;
  }

  /**
   * Take over for function.
   *
   * @param functionName the function name
   * @param rowObject the row object
   */
  public void takeOverForFunction(final String functionName, final A2LWpParamInfo rowObject) {
    Set<Long> tempParamIdSet = this.a2lFileInfoBo.getParamIdByFunction(functionName);
    takeOverForFilteredItems(rowObject, tempParamIdSet);
  }

  /**
   * Take over for filtered items.
   *
   * @param rowObject the row object
   * @param paramIdSet the param id set
   */
  public void takeOverForFilteredItems(final A2LWpParamInfo rowObject, final Set<Long> paramIdSet) {
    Set<A2lWpParamMapping> a2lWpMappingSetToTakeOverCreate = new HashSet<>();
    Map<Long, A2lWpParamMapping> a2lWpMappingListToTakeOverUpdate = new HashMap<>();

    paramIdSet.forEach(paramid -> {
      A2LWpParamInfo a2lWpParamInfo = null;

      if (getSelectedA2lVarGroup() != null) {
        a2lWpParamInfo = getVirtualRecordsMap().get(getSelectedA2lVarGroup().getId()).get(paramid);
      }
      if (a2lWpParamInfo == null) {
        a2lWpParamInfo = getA2lWParamInfoMap().get(paramid);
      }
      A2lWpParamMapping paramMapping = getA2lWpParamMappingObject(a2lWpParamInfo);

      // update
      A2lWpParamMapping paramObject = paramMapping.clone();
      paramObject.setWpRespId(rowObject.getWpRespId());
      paramObject.setParA2lRespId(rowObject.getA2lRespId());
      paramObject.setWpNameCust(rowObject.getWpNameCust());
      paramObject.setWpNameCustInherited(rowObject.isWpNameInherited());
      paramObject.setWpRespInherited(rowObject.isWpRespInherited());

      if ((paramMapping.getId() != null) && isA2lVarGrpValid(a2lWpParamInfo)) {

        a2lWpMappingListToTakeOverUpdate.put(paramObject.getId(), paramObject);
      }
      else {
        // this is when a default level mapping record is part of take over functionality
        // then existing fields must be set to null and a new var grp level record must be created
        if (paramObject.getId() != null) {
          paramObject.setId(null);
          paramObject.setCreatedDate(null);
          paramObject.setCreatedUser(null);
          paramObject.setModifiedDate(null);
          paramObject.setModifiedUser(null);
          paramObject.setVersion(null);
        }
        a2lWpMappingSetToTakeOverCreate.add(paramObject);
      }
    });

    try {

      if (CommonUtils.isNotEmpty(a2lWpMappingListToTakeOverUpdate) ||
          CommonUtils.isNotEmpty(a2lWpMappingSetToTakeOverCreate)) {

        A2lWpParamMappingServiceClient client = new A2lWpParamMappingServiceClient();
        A2lWpParamMappingUpdateModel updateModel = new A2lWpParamMappingUpdateModel();

        updateModel.getA2lWpParamMappingToBeCreated().addAll(a2lWpMappingSetToTakeOverCreate);
        updateModel.getA2lWpParamMappingToBeUpdated().putAll(a2lWpMappingListToTakeOverUpdate);

        client.updateA2lWpParamMapping(updateModel, getPidcA2lBo().getPidcA2l());
      }

    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param a2lWpParamInfo
   * @return
   */
  private boolean isA2lVarGrpValid(final A2LWpParamInfo a2lWpParamInfo) {
    return isA2lVarGrpNull(a2lWpParamInfo) || isA2lVarGrpAvailable(a2lWpParamInfo);
  }

  /**
   * @param a2lWpParamInfo
   * @return
   */
  private boolean isA2lVarGrpAvailable(final A2LWpParamInfo a2lWpParamInfo) {
    return (getSelectedA2lVarGroup() != null) && (a2lWpParamInfo.getVariantGroupId() != null);
  }

  /**
   * @param a2lWpParamInfo
   * @return
   */
  private boolean isA2lVarGrpNull(final A2LWpParamInfo a2lWpParamInfo) {
    return (getSelectedA2lVarGroup() == null) && (a2lWpParamInfo.getVariantGroupId() == null);
  }


  /**
   * @param detailsStrucModel detailsStrucModel
   */
  public void setDetailsStrucModel(final A2LDetailsStructureModel detailsStrucModel) {
    this.detailsStrucModel = detailsStrucModel;

  }

  /**
   * @return the detailsStrucModel
   */
  public A2LDetailsStructureModel getDetailsStrucModel() {
    return this.detailsStrucModel;
  }

  /**
   * @return the selectedA2lVarGroup
   */
  public A2lVariantGroup getSelectedA2lVarGroup() {
    return this.selectedA2lVarGroup;
  }


  /**
   * @param selectedA2lVarGroup the selectedA2lVarGroup to set
   */
  public void setSelectedA2lVarGroup(final A2lVariantGroup selectedA2lVarGroup) {
    this.selectedA2lVarGroup = selectedA2lVarGroup;
  }


  /**
   * @return the a2lRespModel
   */
  public A2lResponsibilityModel getA2lResponsibilityModel() {
    return this.a2lResponsibilityModel;
  }


  /**
   * @param a2lRespModel the a2lRespModel to set
   */
  public void setA2lResponsibilityModel(final A2lResponsibilityModel a2lRespModel) {
    this.a2lResponsibilityModel = a2lRespModel;
  }


  /**
   * @return the currentPage
   */
  public int getCurrentPage() {
    return this.currentPage;
  }


  /**
   * @param currentPage the currentPage to set
   */
  public void setCurrentPage(final int currentPage) {
    this.currentPage = currentPage;
  }

  /**
   * @return the isNotAssignedVarGrp
   */
  public boolean isNotAssignedVarGrp() {
    return this.isNotAssignedVarGrp;
  }


  /**
   * @param isNotAssignedVarGrp the isNotAssignedVarGrp to set
   */
  public void setNotAssignedVarGrp(final boolean isNotAssignedVarGrp) {
    this.isNotAssignedVarGrp = isNotAssignedVarGrp;
  }


  /**
   * @param respToCreate - A2lResponsibility to be created
   * @return creaetd A2lResponsibility
   */
  public A2lResponsibility createA2lResp(final A2lResponsibility respToCreate) {
    try {
      A2lResponsibility a2lRespToRefresh = new A2lResponsibilityServiceClient().create(respToCreate);
      refreshA2lResp(a2lRespToRefresh);
      return a2lRespToRefresh;
    }

    catch (ApicWebServiceException exp) {
      if (exp.getLocalizedMessage().contains("User is deleted")) {
        CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), Activator.PLUGIN_ID);
      }
      else {
        CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      }
    }

    return null;
  }

  /**
   * @param a2lResp - A2lResp from dialog
   */
  private void refreshA2lResp(final A2lResponsibility a2lResp) {
    if (this.a2lResponsibilityModel.getA2lResponsibilityMap().containsKey(a2lResp.getId())) {
      this.a2lResponsibilityModel.getA2lResponsibilityMap().remove(a2lResp.getId());
    }
    this.a2lResponsibilityModel.getA2lResponsibilityMap().put(a2lResp.getId(), a2lResp);
    if ((a2lResp.getUserId() != null) && !this.a2lResponsibilityModel.getUserMap().containsKey(a2lResp.getUserId())) {
      User user = null;
      try {
        user = new UserServiceClient().getApicUserById(a2lResp.getUserId());
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }
      if (user != null) {
        this.a2lResponsibilityModel.getUserMap().put(user.getId(), user);
      }
    }

  }


  /**
   * @param respToEdit - A2lResp to be edited
   * @return updated A2lResp
   */
  public A2lResponsibility editA2lResp(final A2lResponsibility respToEdit) {
    try {
      List<A2lResponsibility> respList = new ArrayList<>();
      respList.add(respToEdit);
      Map<Long, A2lResponsibility> updatedMap = new A2lResponsibilityServiceClient().update(respList);
      A2lResponsibility a2lRespToRefresh = updatedMap.get(respToEdit.getId());
      refreshA2lResp(a2lRespToRefresh);
      return a2lRespToRefresh;
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
    return null;
  }

  /**
   * @param a2lWpResponsibility - to be edited
   * @return A2lWpResponsibility
   */
  public A2lWpResponsibility updateA2lWpRespPal(final A2lWpResponsibility a2lWpResponsibility) {
    A2lWpResponsibility wpResponsibility = new A2lWpResponsibility();
    A2lWpResponsibilityServiceClient client = new A2lWpResponsibilityServiceClient();
    try {
      List<A2lWpResponsibility> a2lWpRespPalList = new ArrayList<>();
      a2lWpRespPalList.add(a2lWpResponsibility);
      Set<A2lWpResponsibility> updateA2lWPResp = client.update(a2lWpRespPalList, this.pidcA2lBo.getPidcA2l());
      wpResponsibility = updateA2lWPResp.iterator().next();
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return wpResponsibility;
  }


  /**
   * Method to update A2lWpParamMapping
   *
   * @param selectedA2lWpParamInfoList A2LWpParamInfo list
   * @param selectedA2lWpResponsibility A2lWpResponsibility
   * @param onlyWpResp - if true, method is called from "Set Workpackage" action, where Wp Resp is directly set without
   *          modifications if false, method is called from "Edit" action where Wp Resp may be modified (wp name cust,
   *          resp, etc)
   */
  public void updateWpParamMapping(final List<A2LWpParamInfo> selectedA2lWpParamInfoList,
      final A2lWpResponsibility selectedA2lWpResponsibility, final boolean onlyWpResp) {

    A2lWpParamMappingUpdateModel a2lWpParamMappingUpdateModel = new A2lWpParamMappingUpdateModel();

    createCreateList(selectedA2lWpParamInfoList, selectedA2lWpResponsibility, a2lWpParamMappingUpdateModel, onlyWpResp);
    createUpdateList(selectedA2lWpParamInfoList, selectedA2lWpResponsibility, a2lWpParamMappingUpdateModel, onlyWpResp);
    createDeleteList(selectedA2lWpParamInfoList, selectedA2lWpResponsibility, a2lWpParamMappingUpdateModel);
    updateMappings(a2lWpParamMappingUpdateModel);

  }


  /**
   * @param a2lWpParamMappingUpdateModel model to update
   */
  public void updateMappings(final A2lWpParamMappingUpdateModel a2lWpParamMappingUpdateModel) {
    A2lWpParamMappingServiceClient client = new A2lWpParamMappingServiceClient();
    try {
      client.updateA2lWpParamMapping(a2lWpParamMappingUpdateModel, getPidcA2lBo().getPidcA2l());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * method for updating A2lparammapping model
   *
   * @param a2lWpParam A2lParamMapping
   */
  public void updateWpParam(final A2lWpParamMapping a2lWpParam) {
    A2lWpParamMappingServiceClient client = new A2lWpParamMappingServiceClient();
    A2lWpParamMappingUpdateModel updateModel = new A2lWpParamMappingUpdateModel();
    try {

      updateModel.getA2lWpParamMappingToBeUpdated().put(a2lWpParam.getId(), a2lWpParam);
      client.updateA2lWpParamMapping(updateModel, getPidcA2lBo().getPidcA2l());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param selectedA2lWpParamInfo A2LWpParamInfo
   * @param selectedA2lWpResponsibility A2lWpResponsibility
   * @return true if A2lwpParamMapping needs to be created
   */
  public boolean isCreate(final A2LWpParamInfo selectedA2lWpParamInfo,
      final A2lWpResponsibility selectedA2lWpResponsibility) {
    // if param mapping id == null, then it is a virtual record, hence create = true
    // if var grp id of selected wp is different than the wp param mapping's assigned vg id, then create = true

    return !selectedA2lWpResponsibility.getName().equals(ApicConstants.DEFAULT_LEVEL_WP) &&
        ((selectedA2lWpParamInfo.getA2lWpParamMappingId() == null) || CommonUtils
            .isNotEqual(selectedA2lWpParamInfo.getVariantGroupId(), selectedA2lWpResponsibility.getVariantGrpId()));


  }

  /**
   * @param selectedA2lWpParamInfo A2LWpParamInfo
   * @param selectedA2lWpResponsibility A2lWpResponsibility
   * @return true if A2lwpParamMapping needs to be updated
   */
  public boolean isUpdate(final A2LWpParamInfo selectedA2lWpParamInfo,
      final A2lWpResponsibility selectedA2lWpResponsibility) {
    return (!selectedA2lWpResponsibility.getName().equals(ApicConstants.DEFAULT_LEVEL_WP)) &&
        (selectedA2lWpParamInfo.getA2lWpParamMappingId() != null) &&
        // if var grp is not present, ie pidc level wp resp updation or if
        // selected wp resp is from same var grp as selected param info
        (CommonUtils.isEqual(selectedA2lWpParamInfo.getVariantGroupId(),
            selectedA2lWpResponsibility.getVariantGrpId()));
  }

  /**
   * @param selectedA2lWpParamInfo A2LWpParamInfo
   * @param selectedA2lWpResponsibility A2lWpResponsibility
   * @return true if A2lwpParamMapping needs to be deleted
   */
  public boolean isDelete(final A2LWpParamInfo selectedA2lWpParamInfo,
      final A2lWpResponsibility selectedA2lWpResponsibility) {
    return (selectedA2lWpParamInfo.getA2lWpParamMappingId() != null) &&
        (selectedA2lWpParamInfo.getVariantGroupId() != null) && (selectedA2lWpResponsibility.getVariantGrpId() == null);
  }


  /**
   * @param selectedA2lWpParamInfo
   * @param selectedA2lWpResponsibility
   * @return
   */
  private A2lWpParamMapping getUpdatedWpParamMapping(final A2LWpParamInfo selectedA2lWpParamInfo,
      final A2lWpResponsibility selectedA2lWpResponsibility, final boolean onlyWpResp, final boolean isCreate) {
    A2lWpParamMapping a2lWpParamClone;

    if (selectedA2lWpParamInfo.getA2lWpParamMappingId() == null) {
      a2lWpParamClone = new A2lWpParamMapping();
    }
    else {
      a2lWpParamClone = getA2lWpParamMappingModel().getA2lWpParamMapping()
          .get(selectedA2lWpParamInfo.getA2lWpParamMappingId()).clone();
    }

    a2lWpParamClone.setParamId(selectedA2lWpParamInfo.getParamId());
    a2lWpParamClone.setWpRespId(selectedA2lWpResponsibility.getId());

    if (onlyWpResp && !isCreate) {
      a2lWpParamClone.setWpNameCustInherited(true);
      a2lWpParamClone.setWpRespInherited(true);
    }
    else {
      a2lWpParamClone.setWpNameCustInherited(selectedA2lWpParamInfo.isWpNameInherited());
      a2lWpParamClone.setWpRespInherited(selectedA2lWpParamInfo.isWpRespInherited());
    }

    if (a2lWpParamClone.isWpNameCustInherited()) {
      a2lWpParamClone.setWpNameCust(selectedA2lWpResponsibility.getWpNameCust());
    }
    else {
      a2lWpParamClone.setWpNameCust(selectedA2lWpParamInfo.getWpNameCust());
    }


    if (a2lWpParamClone.isWpRespInherited() || onlyWpResp) {
      a2lWpParamClone.setParA2lRespId(selectedA2lWpResponsibility.getA2lRespId());
    }
    else if (!a2lWpParamClone.isWpRespInherited()) {
      a2lWpParamClone.setParA2lRespId(selectedA2lWpParamInfo.getA2lRespId());
    }


    return a2lWpParamClone;
  }


  /**
   * Method to fill A2lWpParamMapping objects that need to be updated
   *
   * @param selectedA2lWpParamInfoList
   * @param selectedA2lWpResponsibility
   * @param updateModel with objects to be updated
   * @param onlyWpResp
   */
  public void createUpdateList(final List<A2LWpParamInfo> selectedA2lWpParamInfoList,
      final A2lWpResponsibility selectedA2lWpResponsibility, final A2lWpParamMappingUpdateModel updateModel,
      final boolean onlyWpResp) {

    for (A2LWpParamInfo a2lWpParamInfo : selectedA2lWpParamInfoList) {
      // update operation happens only for records existing in DB
      if (isUpdate(a2lWpParamInfo, selectedA2lWpResponsibility)) {
        A2lWpParamMapping a2lWpParamMappingWithWpResp =
            getUpdatedWpParamMapping(a2lWpParamInfo, selectedA2lWpResponsibility, onlyWpResp, false);
        updateModel.getA2lWpParamMappingToBeUpdated().put(a2lWpParamMappingWithWpResp.getId(),
            a2lWpParamMappingWithWpResp);

      }
    }
  }


  /**
   * Method to fill A2lWpParamMapping objects to be created
   *
   * @param selectedA2lWpParamInfoList A2LWpParamInfo list
   * @param selectedA2lWpResponsibility A2lWpResponsibility
   * @param updateModel with create list
   * @param onlyWpResp
   */
  public void createCreateList(final List<A2LWpParamInfo> selectedA2lWpParamInfoList,
      final A2lWpResponsibility selectedA2lWpResponsibility, final A2lWpParamMappingUpdateModel updateModel,
      final boolean onlyWpResp) {

    for (A2LWpParamInfo a2lWpParamInfo : selectedA2lWpParamInfoList) {
      if (isCreate(a2lWpParamInfo, selectedA2lWpResponsibility)) {
        A2lWpParamMapping a2lWpParamMappingWithWpResp =
            getUpdatedWpParamMapping(a2lWpParamInfo, selectedA2lWpResponsibility, onlyWpResp, true);
        updateModel.getA2lWpParamMappingToBeCreated().add(a2lWpParamMappingWithWpResp);
      }

    }

  }


  /**
   * Method to fill A2lWpParamMapping objects to be deleted
   *
   * @param selectedA2lWpParamInfoList A2LWpParamInfo list
   * @param selectedA2lWpResponsibility A2lWpResponsibility
   * @param updateModel with delete list
   */
  public void createDeleteList(final List<A2LWpParamInfo> selectedA2lWpParamInfoList,
      final A2lWpResponsibility selectedA2lWpResponsibility, final A2lWpParamMappingUpdateModel updateModel) {

    for (A2LWpParamInfo a2lWpParamInfo : selectedA2lWpParamInfoList) {
      if (isDelete(a2lWpParamInfo, selectedA2lWpResponsibility)) {
        A2lWpParamMapping a2lWpParamMappingToDelete =
            getA2lWpParamMappingModel().getA2lWpParamMapping().get(a2lWpParamInfo.getA2lWpParamMappingId());
        updateModel.getA2lWpParamMappingToBeDeleted().put(a2lWpParamMappingToDelete.getId(), a2lWpParamMappingToDelete);
      }
    }
  }


  /**
   * Gets the pidc id.
   *
   * @return the pidc id
   */
  public Long getPidcId() {
    return this.pidcA2lBo.getPidcVersion().getPidcId();
  }

  /**
   * Creates the bosch responsible.
   *
   * @param user the user
   * @return the pidc wp resp
   */
  public A2lResponsibility createBoschResponsible(final User user) {
    A2lResponsibility respToCreate = new A2lResponsibility();
    respToCreate.setProjectId(getPidcId());
    if (user != null) {
      respToCreate.setUserId(user.getId());
    }
    respToCreate.setRespType(WpRespType.RB.getCode());
    respToCreate.setLFirstName(null);
    respToCreate.setLLastName(null);
    respToCreate.setLDepartment(null);
    A2lResponsibility existing = checkIfExists(respToCreate);
    if (existing == null) {
      existing = createA2lResp(respToCreate);
    }
    return existing;
  }

  /**
   * Creates the bosch responsible.
   *
   * @param user the user
   * @return the pidc wp resp
   */
  public A2lResponsibility createDefaultResponsible(final WpRespType wpRespType) {
    A2lResponsibility respToCreate = new A2lResponsibility();
    respToCreate.setProjectId(getPidcId());
    respToCreate.setRespType(wpRespType.getCode());
    respToCreate.setLFirstName(null);
    respToCreate.setLLastName(null);
    respToCreate.setLDepartment(wpRespType.getAliasBase());
    respToCreate.setAliasName(wpRespType.getAliasBase());
    A2lResponsibility existing = checkIfExists(respToCreate);
    if (existing == null) {
      existing = createA2lResp(respToCreate);
    }
    return existing;
  }

  /**
   * Check if exists.
   *
   * @param respToCreate the resp to create
   * @return true, if successful
   */
  public A2lResponsibility checkIfExists(final A2lResponsibility respToCreate) {
    Map<Long, A2lResponsibility> existingMap = getA2lResponsibilityModel().getA2lResponsibilityMap();
    for (A2lResponsibility obj : existingMap.values()) {
      if (A2lResponsibilityCommon.isSame(obj, respToCreate)) {
        return obj;
      }
    }
    return null;
  }

  /**
   * @param selectedBoschUserId - User ID of the Bosch user selected
   * @return true if the selected user is already present in A2lWpRespModel and is deleted
   */
  public boolean isDeletedResp(final Long selectedBoschUserId) {
    if (getA2lResponsibilityModel().getUserMap().containsKey(selectedBoschUserId)) {
      for (A2lResponsibility a2lResponsibility : getA2lResponsibilityModel().getA2lResponsibilityMap().values()) {
        if ((a2lResponsibility.getUserId() != null) && a2lResponsibility.getUserId().equals(selectedBoschUserId) &&
            a2lResponsibility.isDeleted()) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * @return the wpMapppedToPidcVers
   */
  public Map<Long, A2lWorkPackage> getAllWpMapppedToPidcVers() {
    return this.wpMapppedToPidcVers;
  }


  /**
   * @param wpMapppedToPidcVers the wpMapppedToPidcVers to set
   */
  public void setWpMapppedToPidcVers(final Map<Long, A2lWorkPackage> wpMapppedToPidcVers) {
    this.wpMapppedToPidcVers = wpMapppedToPidcVers;
  }


  /**
   * @param selA2lParamList selected a2lparammapping list in parameter assignment page
   * @return a collection of A2lWpResponsibility based on Variant Group selection
   */
  public List<A2lWpResponsibility> getWpsByVarGrpSelection(final List<A2lWpParamMapping> selA2lParamList) {

    List<A2lWpResponsibility> wpRespList = new ArrayList<>();
    Collection<A2lWpResponsibility> allA2lWpResps = getA2lWpDefnModel().getWpRespMap().values();
    if (this.selectedA2lVarGroup == null) {
      for (A2lWpParamMapping a2lWpParamMapping : selA2lParamList) {
        List<A2lWpResponsibility> filteredRespList =
            allA2lWpResps.stream().filter(wpResp -> !wpResp.getId().equals(a2lWpParamMapping.getWpRespId()) &&
                !isMappedAtVarGrpLvl(wpResp, a2lWpParamMapping)).collect(Collectors.toCollection(ArrayList::new));

        wpRespList.addAll(filteredRespList.stream().filter(wpResp -> !wpRespList.contains(wpResp))
            .collect(Collectors.toCollection(ArrayList::new)));


      }

    }
    else {
      for (A2lWpParamMapping a2lWpParamMapping : selA2lParamList) {

        List<A2lWpResponsibility> filteredRespList = allA2lWpResps.stream()
            .filter(a2lWpResp -> this.selectedA2lVarGroup.getId().equals(a2lWpResp.getVariantGrpId()) && !a2lWpResp
                .getName().equals(getA2lWpDefnModel().getWpRespMap().get(a2lWpParamMapping.getWpRespId()).getName()))
            .collect(Collectors.toCollection(ArrayList::new));

        wpRespList.addAll(filteredRespList.stream().filter(wpResp -> !wpRespList.contains(wpResp))
            .collect(Collectors.toCollection(ArrayList::new)));

      }

      // add dummy records only for actual records
      if (isDummyRecRequired(selA2lParamList)) {
        A2lWpResponsibility dummyWpResp = new A2lWpResponsibility();
        dummyWpResp.setName(ApicConstants.DEFAULT_LEVEL_WP);
        wpRespList.add(dummyWpResp);
      }
    }
    return wpRespList;
  }

  /**
   * @param selA2lParamList
   * @param dummyRecReq
   * @return
   */
  private boolean isDummyRecRequired(final List<A2lWpParamMapping> selA2lParamList) {
    for (A2lWpParamMapping a2lWpParamMapping : selA2lParamList) {
      Map<Long, A2lWpParamMapping> a2lWpParamMap =
          this.paramWpRespResolver.getParamMappingAtVarLevel().get(this.selectedA2lVarGroup.getId());
      if ((a2lWpParamMapping.getId() != null) && (a2lWpParamMap != null) &&
          (a2lWpParamMap.get(a2lWpParamMapping.getParamId()) != null)) {
        return true;
      }
    }
    return false;
  }

  private boolean isMappedAtVarGrpLvl(final A2lWpResponsibility wpResp, final A2lWpParamMapping selA2lParam) {
    // check whether the parameter is already assigned to the same WP in the VarGrp level
    if (wpResp.getVariantGrpId() == null) {
      return false;
    }
    if (this.virtualRecordsMap.isEmpty()) {
      formVirtualRecords();
    }
    Map<Long, A2LWpParamInfo> varGrpLevelMappings = this.virtualRecordsMap.get(wpResp.getVariantGrpId());
    A2LWpParamInfo a2lWpParamInfo =
        varGrpLevelMappings == null ? null : varGrpLevelMappings.get(selA2lParam.getParamId());
    return null != a2lWpParamInfo;
  }

  /**
   * @return set of Name at customer values of the workpackages in the PidcVersion
   */
  public SortedSet<String> getNameAtCustomerSet() {
    return this.wpMapppedToPidcVers.values().stream().map(A2lWorkPackage::getNameCustomer).filter(Objects::nonNull)
        .collect(Collectors.toCollection(() -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER)));
  }

  /**
   * @return workpackages that can be assinged to a variant group level -WP definition page
   */
  public Map<Long, A2lWorkPackage> getWpsForVarGrpLevel() {
    Collection<A2lWpResponsibility> allMappings = getA2lWpDefnModel().getWpRespMap().values();
    Map<Long, A2lWorkPackage> wpMap = new HashMap<>();
    wpMap.putAll(this.wpMapppedToPidcVers);
    for (A2lWpResponsibility a2lWpResponsibility : allMappings) {
      if (((a2lWpResponsibility.getVariantGrpId() != null) &&
          (Long.compare(a2lWpResponsibility.getVariantGrpId(), getSelectedA2lVarGroup().getId()) == 0) &&
          wpMap.containsKey(a2lWpResponsibility.getA2lWpId())) ||
          a2lWpResponsibility.getName().equals(ApicConstants.DEFAULT_A2L_WP_NAME)) {
        wpMap.remove(a2lWpResponsibility.getA2lWpId());
      }
    }
    return wpMap;
  }

  /**
   * @return workpackages that can be assinged to default level -WP definition page
   */
  public Map<Long, A2lWorkPackage> getWpsForDefaultLevel() {
    Collection<A2lWpResponsibility> allMappings = getA2lWpDefnModel().getWpRespMap().values();
    Map<Long, A2lWorkPackage> wpMap = new HashMap<>();
    wpMap.putAll(this.wpMapppedToPidcVers);
    for (A2lWpResponsibility respPal : allMappings) {
      // remove if mapping is already available at Default level
      if ((respPal.getVariantGrpId() == null) && wpMap.containsKey(respPal.getA2lWpId())) {
        wpMap.remove(respPal.getA2lWpId());
      }
    }
    return wpMap;
  }

  /**
   */
  public void formVirtualRecords() {
    this.paramWpRespResolver = new ParamWpRespResolver(getA2lWpDefnModel().getA2lVariantGroupMap(),
        getA2lWpDefnModel().getWpRespMap(), getA2lWpParamMappingModel().getA2lWpParamMapping());
    this.paramWpRespResolver.fillParamRespMaps();
    constructVirtualRecords();
  }

  /**
   * @param varGrpParamRespMap
   */
  private void constructVirtualRecords() {
    Collection<A2lWpResponsibility> a2lWpResps = getA2lWpDefnModel().getWpRespMap().values();
    Map<Long, Set<Long>> paramsWithRespModified = this.paramWpRespResolver.getParamsWithRespModified();

    fillVirtualRecordsMap(a2lWpResps, paramsWithRespModified);

    replaceVarGrpLevelMappings();
    loadParamInfoForVarGrp();
    this.virtualRecordsMap.values().stream()
        .forEach(paramInfoMap -> paramInfoMap.values().stream().forEach(paramInfo -> {
          // For Outline filter
          A2lResponsibility a2lResp;
          A2lWpResponsibility a2lWpResponsibility = getA2lWpDefnModel().getWpRespMap().get(paramInfo.getWpRespId());

          if (paramInfo.isWpRespInherited()) {
            a2lResp = getA2lResponsibilityModel().getA2lResponsibilityMap().get(a2lWpResponsibility.getA2lRespId());
          }
          else {
            a2lResp = getA2lResponsibilityModel().getA2lResponsibilityMap().get(paramInfo.getA2lRespId());
          }
          if (isA2lAndWpRespAvailInParamInfo(paramInfo) && (a2lWpResponsibility != null) && (a2lResp != null)) {
            constructParamIdMap(this.a2lWpParamMappingModel.getParamIdWithWpAndRespMap(), paramInfo,
                a2lWpResponsibility,
                getRespName(this.a2lWpParamMappingModel.getA2lWPResponsibleMap(), a2lResp, a2lWpResponsibility));
          }
        }));
  }

  /**
   * @param a2lWpResps
   * @param paramsWithRespModified
   */
  private void fillVirtualRecordsMap(final Collection<A2lWpResponsibility> a2lWpResps,
      final Map<Long, Set<Long>> paramsWithRespModified) {

    if (!this.virtualRecordsMap.isEmpty()) {
      this.virtualRecordsMap.clear();
    }

    for (Map.Entry<Long, Set<Long>> entry : paramsWithRespModified.entrySet()) {
      Long varGrpId = entry.getKey();
      Set<Long> paramIdSet = entry.getValue();
      Map<Long, A2LWpParamInfo> paramMap = new HashMap<>();
      // For the virtual records
      for (Long paramId : paramIdSet) {
        // if Data Review report generated from Resp or wp node , then only param for WP and Resp will be avaialble
        if (!this.a2lFileInfoBo.isDataRvwRprtFrmA2LRespWP() || (getA2lWParamInfoMap().containsKey(paramId))) {
          paramMap.put(paramId, createVirtualRecord(paramId, varGrpId, a2lWpResps));
        }
      }

      this.virtualRecordsMap.put(varGrpId, paramMap);
    }
  }

  /**
   * @param paramInfo
   * @return
   */
  private boolean isA2lAndWpRespAvailInParamInfo(final A2LWpParamInfo paramInfo) {
    return (paramInfo.getA2lRespId() != null) && (paramInfo.getWpRespId() != null) &&
        getA2lResponsibilityModel().getA2lResponsibilityMap().containsKey(paramInfo.getA2lRespId());
  }

  /**
   * @param resolver
   * @param a2lWpResps
   */
  private void replaceVarGrpLevelMappings() {

    for (Long varGrpId : getA2lWpDefnModel().getA2lVariantGroupMap().keySet()) {

      Map<Long, A2lWpParamMapping> mappingAtVarGrpLevel =
          this.paramWpRespResolver.getParamMappingAtVarLevel().get(varGrpId);
      if (CommonUtils.isNotNull(mappingAtVarGrpLevel)) {
        fillParamAtVarGrpLvl(varGrpId, mappingAtVarGrpLevel);
      }
    }
  }

  /**
   * @param varGrpId
   * @param mappingAtVarGrpLevel
   */
  private void fillParamAtVarGrpLvl(final Long varGrpId, final Map<Long, A2lWpParamMapping> mappingAtVarGrpLevel) {
    for (A2lWpParamMapping mapping : mappingAtVarGrpLevel.values()) {
      // set mapping id to the record , since it would not be added in the previous loop
      if (!this.a2lFileInfoBo.isDataRvwRprtFrmA2LRespWP() ||
          (this.a2lFileInfoBo.getA2lParamMap().containsKey(mapping.getName()))) {
        A2LWpParamInfo virtualRecord = setDetails(mapping);
        Long paramId = virtualRecord.getParamId();
        fillParamsAtVarGrpLevel(varGrpId, paramId, virtualRecord);
      }
    }
  }


  /**
   * @param mapping A2lWpParamMapping
   * @return paramIfo
   */
  public A2LWpParamInfo setDetails(final A2lWpParamMapping mapping) {
    A2LParameter a2lParam = this.a2lFileInfoBo.getA2lParamByName(mapping.getName());
    A2LWpParamInfo paramIfo = new A2LWpParamInfo(a2lParam);
    A2lWpResponsibility a2lWpResponsibility = getA2lWpDefnModel().getWpRespMap().get(mapping.getWpRespId());
    if (mapping.isWpRespInherited()) {
      paramIfo.setA2lRespId(a2lWpResponsibility.getA2lRespId());
    }
    else {
      paramIfo.setA2lRespId(mapping.getParA2lRespId());
    }
    paramIfo.setVariantGroupId(a2lWpResponsibility.getVariantGrpId());
    paramIfo.setA2lWpParamMappingId(mapping.getId());
    paramIfo.setBcName(this.a2lFuncBCMap.get(paramIfo.getFuncName()));
    paramIfo.setLABParam(a2lParam.isLABParam());
    paramIfo.setWpRespInherited(mapping.isWpRespInherited());
    paramIfo.setWpNameInherited(mapping.isWpNameCustInherited());
    paramIfo.setWpRespId(mapping.getWpRespId());
    return paramIfo;
  }

  /**
   * @param varGrpId
   * @param paramMap
   */
  private void fillParamsAtVarGrpLevel(final Long varGrpId, final Long paramId, final A2LWpParamInfo virtualRecord) {
    if (this.virtualRecordsMap.containsKey(varGrpId)) {
      this.virtualRecordsMap.get(varGrpId).put(paramId, virtualRecord);
    }
    else {
      Map<Long, A2LWpParamInfo> paramMap = new HashMap<>();
      paramMap.put(paramId, virtualRecord);
      this.virtualRecordsMap.put(varGrpId, paramMap);
    }
  }


  /**
   * @param paramId
   * @param varGrpId
   * @param a2lWpResps
   * @return
   */
  private A2LWpParamInfo createVirtualRecord(final Long paramId, final Long varGrpId,
      final Collection<A2lWpResponsibility> a2lWpResps) {

    A2LWpParamInfo paramInfo = getA2lWParamInfoMap().get(paramId).clone();
    A2lWpParamMapping a2lWpParamMapping =
        getA2lWpParamMappingModel().getA2lWpParamMapping().get(paramInfo.getA2lWpParamMappingId());
    paramInfo.setVariantGroupId(varGrpId);
    paramInfo.setA2lWpParamMappingId(null);
    paramInfo.setA2lRespId(this.paramWpRespResolver.getVarGrpParamRespMap().get(varGrpId).get(paramId).getRespId());
    for (A2lWpResponsibility a2lWpResp : a2lWpResps) {
      if ((a2lWpParamMapping != null) && (a2lWpResp.getVariantGrpId() != null) &&
          a2lWpResp.getVariantGrpId().equals(varGrpId) && a2lWpResp.getA2lWpId()
              .equals(this.paramWpRespResolver.getVarGrpParamRespMap().get(varGrpId).get(paramId).getWpId())) {
        paramInfo.setWpRespId(a2lWpResp.getId());

        if (a2lWpParamMapping.isWpNameCustInherited()) {
          paramInfo.setWpNameCust(this.wpMapppedToPidcVers.get(a2lWpResp.getA2lWpId()).getNameCustomer());
        }
        else {
          paramInfo.setWpNameCust(a2lWpParamMapping.getWpNameCust());
        }
      }
    }

    return paramInfo;
  }


  /**
   * @return the virtualRecordsMap
   */
  public Map<Long, Map<Long, A2LWpParamInfo>> getVirtualRecordsMap() {
    return this.virtualRecordsMap;
  }


  /**
   * @return the paramWpRespResolver
   */
  public ParamWpRespResolver getParamWpRespResolver() {
    return this.paramWpRespResolver;
  }


  /**
   */
  private void loadParamInfoForVarGrp() {
    for (A2lVariantGroup varGrp : getA2lWpDefnModel().getA2lVariantGroupMap().values()) {
      Map<Long, A2LWpParamInfo> a2lWpParamInfoMap = new HashMap<>();
      for (A2LWpParamInfo paramInfo : getA2lWParamInfoMap().values()) {
        // virtual records && DB records for the var grpchecking
        if (this.virtualRecordsMap.containsKey(varGrp.getId()) &&
            this.virtualRecordsMap.get(varGrp.getId()).containsKey(paramInfo.getParamId())) {

          a2lWpParamInfoMap.put(paramInfo.getParamId(),
              this.virtualRecordsMap.get(varGrp.getId()).get(paramInfo.getParamId()));
        }
        else {
          a2lWpParamInfoMap.put(paramInfo.getParamId(), paramInfo);
        }
      }
      this.a2lWParamInfoForVarGrp.put(varGrp.getId(), a2lWpParamInfoMap);
    }
  }

  /**
   * @return the a2lWParamInfoForVarGrp
   */
  public Map<Long, Map<Long, A2LWpParamInfo>> getA2lWParamInfoForVarGrp() {
    return this.a2lWParamInfoForVarGrp;
  }

  /**
   * @return the Set of A2LWpParamInfo to be exported
   */
  public Set<A2LWpParamInfo> loadExcelData() {
    if (this.paramWpRespResolver == null) {
      formVirtualRecords();
    }
    if (this.selectedA2lVarGroup != null) {
      return new TreeSet<>(this.a2lWParamInfoForVarGrp.get(this.selectedA2lVarGroup.getId()).values());
    }
    return new TreeSet<>(getA2lWParamInfoMap().values());
  }

  /**
   * @return the isDefaultLevel
   */
  public boolean isDefaultLevel() {
    return this.isDefaultLevel;
  }

  /**
   * @param isDefaultLevel the isDefaultLevel to set
   */
  public void setDefaultLevel(final boolean isDefaultLevel) {
    this.isDefaultLevel = isDefaultLevel;
  }


  /**
   * @return the refreshApplicable
   */
  public boolean isReconstructA2lWpParamMapApplicable() {
    return this.reconstructA2lWpParamMapApplicable;
  }


  /**
   * @param refreshApplicable the refreshApplicable to set
   */
  public void setReconstructA2lWpParamMapApplicable(final boolean reconstructA2lWpParamMap) {
    this.reconstructA2lWpParamMapApplicable = reconstructA2lWpParamMap;
  }


  /**
   * @return the a2lWParamInfoSet
   */
  public SortedSet<A2LWpParamInfo> getA2lWParamInfoSet() {
    return this.a2lWParamInfoSet;
  }

  /**
   * Forms a Map of RespType and A2lResponsibility from available Work Package - A2lResponsibility Mapping
   *
   * @return map of Key - String of Resposibility Tyep and Value - Set<A2lResponsibility> of Resposibility
   */
  public Map<String, Set<A2lResponsibility>> getRespTypeAndRespMap() {

    Set<String> wpRespSet = getA2lWpDefnModel().getA2lWPResponsibleMap().keySet();
    return createRespTypeAndRespMap(wpRespSet);
  }

  /**
   * @param wpRespSet
   * @return
   */
  private Map<String, Set<A2lResponsibility>> createRespTypeAndRespMap(final Set<String> wpRespSet) {
    Map<String, Set<A2lResponsibility>> respTypeAndRespMap = new TreeMap<>();
    wpRespSet.forEach(respName -> {
      for (A2lResponsibility a2lResponsibility : getA2lResponsibilityModel().getA2lResponsibilityMap().values()) {
        if (a2lResponsibility.getName().equals(respName)) {
          String key = WpRespType.getType(a2lResponsibility.getRespType()).getDispName();
          if (respTypeAndRespMap.get(key) == null) {
            Set<A2lResponsibility> value = new HashSet<>();
            value.add(a2lResponsibility);
            respTypeAndRespMap.put(key, value);
          }
          else {
            Set<A2lResponsibility> value = respTypeAndRespMap.get(key);
            value.add(a2lResponsibility);
            respTypeAndRespMap.put(key, value);
          }
        }
      }
    });

    return respTypeAndRespMap;
  }

  /**
   * Check if exists.
   *
   * @param respToCreate the resp to create
   * @return true, if successful
   */
  public boolean checkIfRespExists(final A2lResponsibility respToCreate) {
    Map<Long, A2lResponsibility> existingMap = getA2lResponsibilityModel().getA2lResponsibilityMap();
    for (A2lResponsibility obj : existingMap.values()) {
      if (A2lResponsibilityCommon.isSame(obj, respToCreate)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Check if Department is already mapped to a2l responsibility
   *
   * @param respToEdit the resp to edit
   * @return object if successful
   */
  public boolean checkIfDepartmentIdExists(final A2lResponsibility respToEdit) {
    Map<Long, A2lResponsibility> existingMap = getA2lResponsibilityModel().getA2lResponsibilityMap();
    if (respToEdit.getId() != null) {
//    edit the existing department
      for (A2lResponsibility obj : existingMap.values()) {
        if (CommonUtils.isNotEqual(respToEdit.getId(), obj.getId()) && (obj.getLDepartment() != null) &&
            respToEdit.getLDepartment().equalsIgnoreCase(obj.getLDepartment())) {
          return true;
        }
      }
    }
    else {
//    create a new department
      for (A2lResponsibility obj : existingMap.values()) {
        if ((obj.getLDepartment() != null) && respToEdit.getLDepartment().equalsIgnoreCase(obj.getLDepartment())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * @param a2lRespUpdationData a2l resp and boschGrpUsers bosch group users to be created/updated
   * @return created bosch group users
   */
  public A2lRespMaintenanceData createUpdateA2lRespWithBoschGrpUsers(final A2lRespMaintenanceData a2lRespUpdationData) {

    try {
      return new A2lResponsibilityServiceClient().maintainA2lResp(a2lRespUpdationData);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return null;
  }

  /**
   * Forms a Map of RespType and A2lResponsibility from available Work Package - A2lResponsibility Mapping
   *
   * @return map of Key - String of Resposibility Tyep and Value - Set<A2lResponsibility> of Resposibility
   */
  public Map<String, Set<A2lResponsibility>> getRespTypeAndRespMapForWpParam() {
    A2lWpParamMappingModel a2lWpParamMapModel = getA2lWpParamMappingModel();
    Set<String> wpRespSet = new HashSet<>();
    if (CommonUtils.isNotNull(a2lWpParamMapModel)) {
      wpRespSet = a2lWpParamMapModel.getA2lWPResponsibleMap().keySet();
    }
    return createRespTypeAndRespMap(wpRespSet);
  }

  /**
   * Forms a Map of RespType and A2lResponsibility from available Work Package - A2lResponsibility Mapping
   *
   * @param a2lWPResMapBasedOnVarGroup A2lWPResMapBasedOnVarGroup
   * @return map of Key - String of Resposibility Tyep and Value - Set<A2lResponsibility> of Resposibility
   */
  public Map<String, Set<A2lResponsibility>> getRespTypeAndRespMapWithVarGroups(
      final Map<String, SortedSet<A2lWpResponsibility>> a2lWPResMapBasedOnVarGroup) {
    Set<String> wpRespSet = a2lWPResMapBasedOnVarGroup.keySet();
    return createRespTypeAndRespMap(wpRespSet);
  }


  /**
   * Forms a Map of RespType and A2lResponsibility for available A2lResponsibility Names
   *
   * @param respNameSet Responsibility Name Set
   * @return map of Key - String of Resposibility Tyep and Value - Set<A2lResponsibility> of Resposibility
   */
  public Map<String, Set<A2lResponsibility>> getRespTypeAndRespMapFromRespNameSet(final Set<String> respNameSet) {
    return createRespTypeAndRespMap(respNameSet);
  }


  /**
   * @return the activeA2lWpDefnModel
   */
  public A2lWpDefinitionModel getActiveA2lWpDefnModel() {
    return this.activeA2lWpDefnModel;
  }


  /**
   * @param activeA2lWpDefnModel the activeA2lWpDefnModel to set
   */
  public void setActiveA2lWpDefnModel(final A2lWpDefinitionModel activeA2lWpDefnModel) {
    this.activeA2lWpDefnModel = activeA2lWpDefnModel;
  }

  /**
   * @return true, if Resp is available for the parameters
   */
  public boolean isRespAvailableInParamLevel() {
    // If Resp available in default Level, return true
    // Else check in Variant group level
    boolean isRespAvailable = checkForRespAvailability(getA2lWParamInfoMap());
    if (!isRespAvailable && CommonUtils.isNotEmpty(getA2lWParamInfoForVarGrp())) {
      for (Map<Long, A2LWpParamInfo> paramInfoMapForVarGrp : getA2lWParamInfoForVarGrp().values()) {
        if (CommonUtils.isNotEmpty(paramInfoMapForVarGrp) && checkForRespAvailability(paramInfoMapForVarGrp)) {
          isRespAvailable = true;
          break;
        }
      }
    }
    return isRespAvailable;
  }

  /**
   * Method to check Responsibility is available either in WP level or Param level
   *
   * @param paramInfoMap
   */
  private boolean checkForRespAvailability(final Map<Long, A2LWpParamInfo> paramInfoMap) {
    for (A2LWpParamInfo paramInfo : paramInfoMap.values()) {
      if (CommonUtils.isNotEqual(getWpRespUser(paramInfo), WpRespType.RB.getDispName())) {
        return true;
      }
    }
    return false;
  }

}