/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.fc2wp;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.PTType;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPDef;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMappingWithDetails;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPRelvPTType;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPVersion;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.NodeAccessDetails;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FC2WPMappingServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FC2WPRelevantPTTypeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FC2WPVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.PTTypeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.NodeAccessServiceClient;


/**
 * @author APJ4COB
 */
public class FC2WPDefBO {

  /**
   * FC2WPVersion
   */
  private final FC2WPVersion fc2wpVersion;
  private Set<FC2WPVersion> allVersions;
  private FC2WPMappingWithDetails fc2wpMappingWithDetails;
  private final CurrentUserBO currentUser = new CurrentUserBO();
  private NodeAccessDetails nodeAccessWithUserInfo;
  private final Map<Long, FC2WPMappingWithDetails> fc2wpVerMappingForCompEditor = new LinkedHashMap<>();
  private final List<FC2WPMappingWithDetails> fc2wpMappingWithDetailsList = new ArrayList<>();
  private final List<FC2WPVersion> fc2wpVersionList = new ArrayList<>();
  private final List<FC2WPVersion> selfc2wpVersionList = new ArrayList<>();
  private Map<FC2WPVersion, FC2WPDef> fc2wpDefMap = new ConcurrentHashMap<>();
  private SortedSet<FC2WPRelvPTType> relPTTypeData;
  private Set<PTType> allPtTypeSet;

  /**
   * Constructor
   *
   * @param fc2wpVersion Object ID
   */
  public FC2WPDefBO(final FC2WPVersion fc2wpVersion) {
    this.fc2wpVersion = fc2wpVersion;
    initializeFields();
  }

  /**
  *
  */
  private void initializeFields() {
    loadAllVersionForDef();
    loadFC2WPMapping();
    loadRelevantPTTypeData(this.fc2wpVersion.getFcwpDefId());
    loadAllPtTypes();
  }

  /**
   *
   */
  private void loadAllPtTypes() {
    try {
      setAllPtTypeSet(new PTTypeServiceClient().getAllPTtypes());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error("Error loading All PT-types for FC-WP Data. " + exp.getMessage(), exp,
          Activator.PLUGIN_ID);
    }
  }

  /**
   *
   */
  public void loadFC2WPMapping() {
    try {
      FC2WPMappingServiceClient mapClient = new FC2WPMappingServiceClient();
      // get the fc2wp version mapping
      this.fc2wpMappingWithDetails = mapClient.getFC2WPMappingByVersion(this.fc2wpVersion.getId());
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().error("Error in loading FC2WP mapping list for the version : " + ex.getMessage(), ex,
          Activator.PLUGIN_ID);
    }
  }

  /**
   * @return the allVersions
   */
  public Set<FC2WPVersion> getAllVersions() {
    return this.allVersions;
  }

  /**
   * @param selFc2WpVerList List<FC2WPMappingWithDetails>
   * @return List<FC2WPMappingWithDetails>
   */
  public Map<Long, FC2WPMappingWithDetails> getMappingDetailsForCompareEditor(
      final List<FC2WPVersion> selFc2WpVerList) {
    this.fc2wpVerMappingForCompEditor.clear();
    this.selfc2wpVersionList.clear();
    // get the fc2wp version mapping
    FC2WPMappingServiceClient mapClient = new FC2WPMappingServiceClient();
    try {
      for (FC2WPVersion ver : selFc2WpVerList) {
        this.selfc2wpVersionList.add(ver);
        if (ver.equals(this.fc2wpVersion)) {
          this.fc2wpVerMappingForCompEditor.put(ver.getId(), this.fc2wpMappingWithDetails);
        }
        else {
          FC2WPMappingWithDetails compFc2wpVersMapping = mapClient.getFC2WPMappingByVersion(ver.getId());
          this.fc2wpVerMappingForCompEditor.put(ver.getId(), compFc2wpVersMapping);
        }
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error("Error in loading FC2WP mapping list for the version : " + exp.getMessage(), exp,
          Activator.PLUGIN_ID);
    }
    return this.fc2wpVerMappingForCompEditor;

  }

  /**
   * @return List<FC2WPMappingWithDetails>
   */
  public List<FC2WPMappingWithDetails> getFC2WPMappingWithDetailsList() {
    this.fc2wpMappingWithDetailsList.clear();
    if (CommonUtils.isNotEmpty(this.fc2wpVerMappingForCompEditor)) {
      for (FC2WPMappingWithDetails mappingDetails : this.fc2wpVerMappingForCompEditor.values()) {
        this.fc2wpMappingWithDetailsList.add(mappingDetails);
      }
    }
    else {
      this.fc2wpMappingWithDetailsList.add(this.fc2wpMappingWithDetails);
    }
    return this.fc2wpMappingWithDetailsList;
  }


  /**
   * @return the fc2wpVerMappingForCompEditor
   */
  public Map<Long, FC2WPMappingWithDetails> getFc2wpVerMappingForCompEditor() {
    return this.fc2wpVerMappingForCompEditor;
  }

  /**
   * @return the fc2wpVersionList
   */
  public List<FC2WPVersion> getFc2wpVersionList() {
    this.fc2wpVersionList.clear();
    if (CommonUtils.isNotEmpty(this.fc2wpVerMappingForCompEditor)) {
      this.fc2wpVersionList.addAll(this.selfc2wpVersionList);
    }
    else {
      this.fc2wpVersionList.add(this.fc2wpVersion);
    }
    return this.fc2wpVersionList;
  }

  /**
   * @return the fc2wpDefMap
   */
  public Map<FC2WPVersion, FC2WPDef> getFc2wpDefMap() {
    return this.fc2wpDefMap;
  }

  /**
   * @param fc2wpDefMap the fc2wpDefMap to set
   */
  public void setFc2wpDefMap(final Map<FC2WPVersion, FC2WPDef> fc2wpDefMap) {
    this.fc2wpDefMap = fc2wpDefMap;
  }

  /**
   * @param fc2wpDefID Long
   */
  public void loadRelevantPTTypeData(final Long fc2wpDefID) {

    Set<FC2WPRelvPTType> ptTypeSet;
    SortedSet<FC2WPRelvPTType> sortedPTtypes = null;

    final FC2WPRelevantPTTypeServiceClient fc2wpDefClient = new FC2WPRelevantPTTypeServiceClient();
    try {
      // Load the contents using webservice calls
      ptTypeSet = fc2wpDefClient.getRelevantPTTypes(fc2wpDefID);

      // Sort the set
      if (CommonUtils.isNotEmpty(ptTypeSet)) {
        sortedPTtypes = new TreeSet<>(getComparator());
        sortedPTtypes.addAll(ptTypeSet);
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(
          "Error loading Relevant PT-types for FC2WP Data for Definition. " + exp.getMessage(), exp,
          Activator.PLUGIN_ID);
    }
    this.relPTTypeData = sortedPTtypes;
  }

  /**
   * @return Comparator<FC2WPRelvPTType>
   */
  public Comparator<FC2WPRelvPTType> getComparator() {
    return (final FC2WPRelvPTType e1, final FC2WPRelvPTType e2) -> e1.getPtTypeName().compareTo(e2.getPtTypeName());
  }

  /**
   * Gets the all versions.
   */
  public void loadAllVersionForDef() {

    CDMLogger.getInstance().debug("Starting to load all versions using APIC web server... ");

    // create a webservice client
    final FC2WPVersionServiceClient client = new FC2WPVersionServiceClient();
    Long defID = 0L;
    try {
      defID = this.fc2wpVersion.getFcwpDefId();
      // Load the contents using webservice calls
      this.allVersions = client.getVersionsByDefID(defID);
      CDMLogger.getInstance()
          .debug("All Versions for FC2WP Data loaded for Def:" + defID + " with versions :" + this.allVersions.size());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error("Error loading All Versions for FC2WP Data for Definition : " + exp.getMessage(),
          exp, Activator.PLUGIN_ID);
    }

  }

  /**
   * Webservice call to update active flag.
   *
   * @param versn the new active version WS
   * @param isActive boolean
   * @return the FC2WP version
   */
  public FC2WPVersion setActiveVersServiceCall(final FC2WPVersion versn, final boolean isActive) {
    FC2WPVersion retVers = null;
    CDMLogger.getInstance().debug("Updating active FC2WP version using APIC web server... ");

    // create a webservice client
    final FC2WPVersionServiceClient client = new FC2WPVersionServiceClient();
    try {
      versn.setActive(isActive);
      retVers = client.update(versn);
      CDMLogger.getInstance().debug("FC2WP version :" + versn.getId() + " is set to Active");
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error("Error in setting FC2WP version as active : " + exp.getMessage(), exp,
          Activator.PLUGIN_ID);
    }

    return retVers;
  }


  /**
   * @return the fc2wpVersMapping
   */
  public FC2WPMappingWithDetails getFc2wpMappingWithDetails() {
    return this.fc2wpMappingWithDetails;
  }

  /**
   * To enable/disable toolbar buttons(add/edit) in nat form page
   *
   * @return is edit allowed
   */

  public boolean isModifiable() {
    try {
      return this.currentUser.hasNodeWriteAccess(this.fc2wpVersion.getFcwpDefId()) && this.fc2wpVersion.isWorkingSet();
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
      return false;
    }
  }

  /**
   * to get all the users having access right for fc2wp node;this will be used to populate FC2WPContactPersonDialog Get
   * a sorted set of the ApicUser AccessRights
   *
   * @return access rights
   */
  //
  public SortedSet<NodeAccess> getAccessRights() {
    long nodeId = this.fc2wpVersion.getFcwpDefId();
    SortedSet<NodeAccess> nodeAccessSet = new TreeSet<>();
    try {
      NodeAccessDetails result = new NodeAccessServiceClient().getNodeAccessDetailsByNode(MODEL_TYPE.FC2WP_DEF, nodeId);
      setNodeAccessWithUserInfo(result);
      if (CommonUtils.isNotEmpty(result.getNodeAccessMap())) {
        nodeAccessSet.addAll(result.getNodeAccessMap().get(nodeId));
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error("Error while loading node access data - " + e.getMessage(), e);
    }
    return nodeAccessSet;
  }

  /**
   * @return long
   */
  public long getFC2WPDefId() {
    return this.fc2wpVersion.getFcwpDefId();
  }

  /**
   * @return the fc2wpVersion
   */
  public FC2WPVersion getFc2wpVersion() {
    return this.fc2wpVersion;
  }


  /**
   * @return the relPTTypeData
   */
  public SortedSet<FC2WPRelvPTType> getRelPTTypeData() {
    return this.relPTTypeData;
  }


  /**
   * @param relPTTypeData the relPTTypeData to set
   */
  public void setRelPTTypeData(final SortedSet<FC2WPRelvPTType> relPTTypeData) {
    this.relPTTypeData = relPTTypeData;
  }

  /**
   * to enable/disable buttons in pt type dialog or give permission to modify pt type Returns whether the logged in user
   * has privilege to modify access rights to this Project ID Card.
   *
   * @return <code>true</code> if current user can modify the access rights.
   */
  //
  public boolean canModifyAccessRights() {
    try {
      return this.currentUser.hasApicWriteAccess() ||
          this.currentUser.hasNodeGrantAccess(this.fc2wpVersion.getFcwpDefId());
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, com.bosch.caltool.icdm.client.bo.Activator.PLUGIN_ID);
      return false;
    }
  }


  /**
   * to enable toolbar buttons add/edit in versions page Returns whether the logged in user is owner.
   *
   * @return <code>true</code> if current user can modify the access rights.
   */

  public boolean isOwner() {
    try {
      return this.currentUser.hasNodeOwnerAccess(this.fc2wpVersion.getFcwpDefId());
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, com.bosch.caltool.icdm.client.bo.Activator.PLUGIN_ID);
      return false;
    }
  }


  /**
   * @return the nodeAccessWithUserInfo
   */
  public NodeAccessDetails getNodeAccessWithUserInfo() {
    return this.nodeAccessWithUserInfo;
  }


  /**
   * @param nodeAccessWithUserInfo the nodeAccessWithUserInfo to set
   */
  public void setNodeAccessWithUserInfo(final NodeAccessDetails nodeAccessWithUserInfo) {
    this.nodeAccessWithUserInfo = nodeAccessWithUserInfo;
  }

  /**
   * @param selFC2WPDefId Long
   * @return SortedSet<FC2WPRelvPTType>
   */
  public SortedSet<FC2WPRelvPTType> getRelevantPTTypeDataForCompEditor(final Long selFC2WPDefId) {
    Set<FC2WPRelvPTType> ptTypeSet;
    SortedSet<FC2WPRelvPTType> sortedPTtypes = null;

    final FC2WPRelevantPTTypeServiceClient fc2wpDefClient = new FC2WPRelevantPTTypeServiceClient();
    try {
      // Load the contents using webservice calls
      ptTypeSet = fc2wpDefClient.getRelevantPTTypes(selFC2WPDefId);

      // Sort the set
      if (CommonUtils.isNotEmpty(ptTypeSet)) {
        sortedPTtypes = new TreeSet<>(getComparator());
        sortedPTtypes.addAll(ptTypeSet);
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(
          "Error loading Relevant PT-types for FC2WP Data for Definition. " + exp.getMessage(), exp,
          Activator.PLUGIN_ID);
    }
    return sortedPTtypes;
  }

  /**
   * @return the allPtTypeSet
   */
  public Set<PTType> getAllPtTypeSet() {
    return this.allPtTypeSet;
  }

  /**
   * @param allPtTypeSet the allPtTypeSet to set
   */
  public void setAllPtTypeSet(final Set<PTType> allPtTypeSet) {
    this.allPtTypeSet = allPtTypeSet;
  }

}

