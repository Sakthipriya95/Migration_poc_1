/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.calcomp.externallink.ILinkableObject;
import com.bosch.caltool.apic.jpa.bo.AttributeValue.CLEARING_STATUS;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TAliasDefinition;
import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TUsecaseFavorite;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * @author jvi6cob
 */
public class PIDCard extends ApicObject implements Comparable<PIDCard>, ILinkableObject {

  /**
   * PIDC Revision key
   */
  public static final String FLD_PIDC_PRO_REV_ID = "PIDC_REVISION";

  /**
   * Map of structure attribute and values
   * <p>
   * Key - attribute ID<br>
   * Value - value ID
   */
  private final ConcurrentMap<Long, Long> structAttrValueIDsMap = new ConcurrentHashMap<>();

  // ICDM-1040
  /**
   * Manager instance for project usecases
   */
  private final UseCaseFavNodesMgr ucFavMgr = new UseCaseFavNodesMgr(this);

  /**
   * Map of PIDC Versions
   * <p>
   * Key - PRO REV ID<br>
   * Value - PIDC Version
   */
  private final ConcurrentMap<Long, PIDCVersion> allVersionMap = new ConcurrentHashMap<>();

  /**
   * if true, versions are loaded atleast once.
   */
  private boolean versionsLoaded;

  /**
   * Leaf PIDC node
   */
  private PIDCNode leafNode;

  /**
   * Version lock object
   */
  private final Object versLock = new Object();

  /**
   * PIDCard Constructor
   *
   * @param apicDataProvider the dataprovider
   * @param pidcID pidcID
   * @param activeVersion active version of this PIDC
   */
  public PIDCard(final ApicDataProvider apicDataProvider, final Long pidcID, final PIDCVersion activeVersion) {
    super(apicDataProvider, pidcID);
    if (activeVersion != null) {
      // During PIDC Creation, active version is not available yet
      this.allVersionMap.put(activeVersion.getProRevId(), activeVersion);
    }
    getDataCache().getAllPidcMap().put(pidcID, this);

  }


  /**
   * Add the structure attr value for the PIDC tree
   *
   * @param attrID attribute ID
   * @param valueID value ID
   */
  void addStructureAttrValue(final Long attrID, final Long valueID) {
    this.structAttrValueIDsMap.put(attrID, valueID);
  }

  /**
   * @return Map of Attribute values for the PIDC tree
   */
  Map<Long, Long> getStructureAttrValueIDs() {
    return this.structAttrValueIDsMap;
  }

  /**
   * @return ID of PIDC
   * @deprecated use {@link #getID()} instead
   */
  @Deprecated
  public Long getPidcId() {
    return getID();
  }

  /**
   * @return PRO rev ID of the active version
   */
  protected Long getProRevID() {
    return getEntityProvider().getDbPIDC(getID()).getProRevId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return getEntityProvider().getDbPIDC(getID()).getVersion();
  }

  /**
   * @return PIDC name
   * @deprecated use {@link #getName()} instead
   */
  @Deprecated
  public String getPidcName() {
    return getName();
  }

  /**
   * @return English name of PIDC
   */
  public String getPidcNameEng() {
    return getEntityProvider().getDbPIDC(getID()).getTabvAttrValue().getTextvalueEng();
  }

  /**
   * @return German name of PIDC
   */
  public String getPidcNameGer() {
    return getEntityProvider().getDbPIDC(getID()).getTabvAttrValue().getTextvalueGer();
  }

  /**
   * @return English description of PIDC
   */
  public String getPidcDescEng() {
    return getEntityProvider().getDbPIDC(getID()).getTabvAttrValue().getValueDescEng();
  }

  /**
   * @return German description of PIDC
   */
  public String getPidcDescGer() {
    return getEntityProvider().getDbPIDC(getID()).getTabvAttrValue().getValueDescGer();
  }

  /**
   * @return true if the PIDC is deleted
   */
  public boolean isDeleted() {
    return getEntityProvider().getDbPIDC(getID()).getTabvAttrValue().getDeletedFlag().equals(ApicConstants.YES);
  }

  /**
   * @return Created Date
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbPIDC(getID()).getCreatedDate());
  }

  /**
   * @return Modified date
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbPIDC(getID()).getModifiedDate());
  }

  /**
   * @return Created User
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbPIDC(getID()).getCreatedUser();
  }

  /**
   * @return Modified User
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbPIDC(getID()).getModifiedUser();
  }

  /**
   * @return the active version of the pidc
   */
  public PIDCVersion getActiveVersion() {
    PIDCVersion activeVersion = null;
    Long proRevID = getProRevID();
    synchronized (this.versLock) {
      if (!isVersionsLoaded()) {
        // If all versions are not loaded yet, only active version will be available in the version map
        activeVersion = this.allVersionMap.get(proRevID);
      }
      // If the previous step did not work, then get the active version from the all versions map.(for e.g. PIDC object
      // created uring PIDC Creation)
      // Also, if versions are loaded already, then version SHOULD be picked up using this method, as the map might
      // have been reset with a resetVersions() call.
      if (activeVersion == null) {
        getLogger().debug("Retrieving active version for PIDC : {}", getID());

        activeVersion = getAllVersionsMapInternal().get(proRevID);

        getLogger().debug("Active version for PIDC {} is {}", getID(), activeVersion.getID());
      }
    }
    return activeVersion;
  }


  /**
   * Fetch map of all versions. If map is reset or empty, then versions are loaded to the map
   *
   * @return Map of PIDC Versions
   *         <p>
   *         Key - PRO REV ID<br>
   *         Value - PIDC Version
   */
  private Map<Long, PIDCVersion> getAllVersionsMapInternal() {

    synchronized (this.versLock) {
      // Case 1: Initially, allVersionMap will contain the active version only. But versionsLoaded will be 'false'
      // Case 2: After reset, allVersionMap will be empty
      if (this.allVersionMap.isEmpty() || !isVersionsLoaded()) {
        getLogger().debug("Loading all versions of PIDC : {}", getID());

        PIDCVersion pidcVersion;
        for (TPidcVersion tPidcVersion : getEntityProvider().getDbPIDC(getID()).getTPidcVersions()) {
          pidcVersion = getDataCache().getPidcVersion(tPidcVersion.getPidcVersId());
          if (pidcVersion == null) {
            pidcVersion = new PIDCVersion((ApicDataProvider) getDataProvider(), tPidcVersion.getPidcVersId());
          }
          // Note : Key is PRO REV ID
          this.allVersionMap.put(pidcVersion.getProRevId(), pidcVersion);
        }
        this.versionsLoaded = true;

        getLogger().debug("Total versions count = {}", this.allVersionMap.size());
      }

    }
    return this.allVersionMap;
  }

  /**
   * @return all versions of this PIDC
   */
  public SortedSet<PIDCVersion> getAllVersions() {
    return new TreeSet<>(getAllVersionsMapInternal().values());
  }

  /**
   * @return all versions of this PIDC as a Map
   */
  protected Map<Long, PIDCVersion> getAllVersionsMap() {
    ConcurrentMap<Long, PIDCVersion> allVerMap = new ConcurrentHashMap<>();
    for (PIDCVersion vers : getAllVersionsMapInternal().values()) {
      allVerMap.put(vers.getID(), vers);
    }
    return allVerMap;
  }

  /**
   * @return true, if versions are loaded atleast once
   */
  boolean isVersionsLoaded() {
    return this.versionsLoaded;
  }

  /**
   * Get a sorted set of the PIDCs AccessRights
   *
   * @return access rights
   */
  public SortedSet<NodeAccessRight> getAccessRights() {
    return getDataLoader().getNodeAccessRights(getID());
  }

  /**
   * @return all versions other than active version
   */
  public SortedSet<PIDCVersion> getOtherVersions() {
    SortedSet<PIDCVersion> retSet = new TreeSet<>(getAllVersionsMapInternal().values());
    retSet.remove(getActiveVersion());
    return retSet;
  }

  /**
   * @return all versions excluding the hidden versions and active version
   */
  public SortedSet<PIDCVersion> getOtherVisibleVersions() {
    SortedSet<PIDCVersion> excludedList = new TreeSet<>();
    for (PIDCVersion versionObj : getOtherVersions()) {
      if (!versionObj.isHidden()) {
        // add only version which are not hidden
        excludedList.add(versionObj);
      }
    }
    return excludedList;
  }

  /**
   * Returns the corresponding attrValue linked for the PIDC's name
   *
   * @return AttributeValue
   */
  public AttributeValue getNameValue() {
    return getDataCache().getAttrValue(getEntityProvider().getDbPIDC(getID()).getTabvAttrValue().getValueId());
  }


  /**
   * ICDM-1107
   *
   * @return tooltip with name & description
   */
  @Override
  public String getToolTip() {
    return "PIDC : " + getName() + "\nDescription : " + getDescription();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    String returnValue;

    switch (getDataCache().getLanguage()) {
      case ENGLISH:
        returnValue = getEntityProvider().getDbPIDC(getID()).getTabvAttrValue().getValueDescEng();
        break;
      case GERMAN:
        returnValue = caseGerDesc();
        break;

      default:
        returnValue = "INVALID LANGUAGE!";
    }

    return returnValue;
  }


  /**
   * @return
   */
  private String caseGerDesc() {
    String returnValue;
    returnValue = getEntityProvider().getDbPIDC(getID()).getTabvAttrValue().getValueDescGer();
    if (returnValue == null) {
      returnValue = getEntityProvider().getDbPIDC(getID()).getTabvAttrValue().getValueDescEng();
    }
    return returnValue;
  }


  /**
   * @return APRJ ID
   */
  public Long getAPRJID() {
    return getEntityProvider().getDbPIDC(getID()).getAprjId();
  }

  /**
   * @return Last VCDM TransferUser
   */
  public String getLastVCDMTransferUser() {
    return getEntityProvider().getDbPIDC(getID()).getVcdmTransferUser();
  }

  /**
   * @return Created Date
   */
  public Calendar getLastVCDMTransferDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbPIDC(getID()).getVcdmTransferDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final PIDCard pidcard) {
    return ApicUtil.compare(getName(), pidcard.getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    String returnValue;

    switch (getDataCache().getLanguage()) {
      case ENGLISH:
        returnValue = getEntityProvider().getDbPIDC(getID()).getTabvAttrValue().getTextvalueEng();
        break;
      case GERMAN:
        returnValue = caseGerName();
        break;

      default:
        returnValue = "INVALID LANGUAGE!";
    }

    return returnValue;
  }


  /**
   * @return
   */
  private String caseGerName() {
    String returnValue;
    returnValue = getEntityProvider().getDbPIDC(getID()).getTabvAttrValue().getTextvalueGer();
    if (CommonUtils.isEmptyString(returnValue)) {
      returnValue = getEntityProvider().getDbPIDC(getID()).getTabvAttrValue().getTextvalueEng();
    }
    return returnValue;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return EntityType.PIDC;
  }

  /**
   * Get the current users access right on this PIDC
   *
   * @return The NodeAccessRight of the current user If the user has no special access rights return NULL
   */
  public NodeAccessRight getCurrentUserAccessRights() {
    // Icdm-346
    return getDataCache().getNodeAccRights(getID());
  }

  /**
   * Returns whether the logged in user has privilege to modify access rights to this Project ID Card.
   *
   * @return <code>true</code> if current user can modify the access rights.
   */
  public boolean canModifyAccessRights() {
    // ICDM-1007
    // ICDM-2354
    if (getDataCache().getCurrentUser().hasApicWriteAccess()) {
      return true;
    }
    NodeAccessRight curUserAccRight = getCurrentUserAccessRights();
    if ((curUserAccRight != null) && curUserAccRight.hasGrantOption()) {
      return true;
    }
    return false;
  }

  /**
   * icdm-275
   *
   * @return boolean if the user has the access to change the owner flag
   */
  public boolean canModifyOwnerRights() {
    // ICDM-2354
    final NodeAccessRight curUserAccRight = getCurrentUserAccessRights();
    if (((curUserAccRight != null) && curUserAccRight.isOwner()) ||
        getDataCache().getCurrentUser().hasApicWriteAccess()) {
      return true;
    }

    return false;
  }

  /**
   * ICDM-1040 Gets the root level nodes of this project ID card.
   *
   * @return PIDC Nodes at the root level
   */
  public SortedSet<FavUseCaseItemNode> getRootUcFavNodes() {
    // fetch the fav uc items
    getFavoriteUCMap();
    return this.ucFavMgr.getRootVirtualNodes();
  }

  /**
   * ICDM-1040 Refresh the nodes. To be used if attribute values are changed.
   *
   * @param favUcItem FavUseCaseItem
   */
  public void refreshFavNodes(final FavUseCaseItem favUcItem) {
    this.ucFavMgr.refreshNodes(favUcItem);

  }

  /**
   * reset the virtual structure by clearing the root nodes
   */
  public void resetFavNodes() {
    this.ucFavMgr.clearRootNodes();
  }

  /**
   * ICDM-1027
   *
   * @return Map of favourite usecase items for this project id card
   */
  public Map<Long, FavUseCaseItem> getFavoriteUCMap() {
    Map<Long, FavUseCaseItem> favUcMap = getDataCache().getPidcUCFavMap(getID());
    if (favUcMap.isEmpty()) {
      for (TUsecaseFavorite pidcFavUcItem : getEntityProvider().getDbPIDC(getID()).getTUsecaseFavorites()) {
        favUcMap.put(pidcFavUcItem.getUcFavId(),
            getDataCache().getFavUcItem(pidcFavUcItem.getUcFavId(), true, getID()));
      }
    }

    return favUcMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, String> getObjectDetails() {
    ConcurrentMap<String, String> summaryMap = new ConcurrentHashMap<String, String>();
    summaryMap.put(FLD_PIDC_PRO_REV_ID, String.valueOf(getProRevID()));
    // TODO add all fields of the entity
    return summaryMap;
  }

  /**
   * Reset the version fields, so that the next get version retrieval fills with latest changes.
   */
  void resetVersions() {
    getLogger().debug("Resetting versions for PIDC : {}", getID());
    synchronized (this.versLock) {
      this.allVersionMap.clear();
    }
  }

  /**
   * @return PIDC Clearing Status
   */
  public CLEARING_STATUS getClearingStatus() {
    return CLEARING_STATUS
        .getClearingStatus(getEntityProvider().getDbPIDC(getID()).getTabvAttrValue().getClearingStatus());
  }

  /**
   * @return whether the value is cleared
   */
  public boolean isCleared() {
    // Icdm-830 Data Model Changes
    return getClearingStatus() == CLEARING_STATUS.CLEARED;
  }


  /**
   * @return the parent leaf node in the PIDC tree
   */
  public PIDCNode getLeafNode() {
    return this.leafNode;
  }

  /**
   * @param leafNode set the parent leaf node in the PIDC tree
   */
  protected void setLeafNode(final PIDCNode leafNode) {
    this.leafNode = leafNode;
  }

  /**
   * Returns pidc node name for specified level
   *
   * @param attrName level attr name
   * @return node name of pidc corresponding to level
   */
  public String getNodeName(final String attrName) {
    PIDCNode nodeObj = getLeafNode();
    while (nodeObj != null) {
      if (nodeObj.getNodeAttr().getName().equalsIgnoreCase(attrName)) {
        return nodeObj.getNodeName();
      }
      nodeObj = nodeObj.getParent();
    }
    return "";
  }

  /**
   * @return get all PIDca2l objects created defined for this pidc ID
   */
  protected Map<Long, PIDCA2l> getDefinedPidcA2lMap() {
    ConcurrentMap<Long, PIDCA2l> retMap = new ConcurrentHashMap<>();
    PIDCA2l pidcA2l;
    Long primaryKey;
    for (TPidcA2l dbPidcA2l : getEntityProvider().getDbPIDC(getID()).getTabvPidcA2ls()) {
      primaryKey = dbPidcA2l.getPidcA2lId();
      pidcA2l = getDataCache().getAllPidcA2lMap().get(primaryKey);
      if (pidcA2l == null) {
        pidcA2l = new PIDCA2l(getDataCache().getDataProvider(), primaryKey);
      }
      retMap.put(pidcA2l.getID(), pidcA2l);
    }
    this.versionsLoaded = true;
    return retMap;
  }

  /**
   * Get the input data
   *
   * @return map of a2l files
   */
  public Map<Long, A2LFile> getAllA2LFiles() {
    // get the sdom pver's mapped to each version
    Set<SdomPver> sdomPvers = new HashSet<SdomPver>();
    for (PIDCVersion pidcVer : getAllVersions()) {
      sdomPvers.addAll(pidcVer.getPVerSet());
    }

    // get the a2l files mapped to that pver name from tabvA2lFileInfo
    ConcurrentMap<Long, A2LFile> a2lFileMap = new ConcurrentHashMap<Long, A2LFile>();
    for (SdomPver pver : sdomPvers) {
      a2lFileMap.putAll(pver.getAllA2LFileMap());
    }

    for (PIDCA2l pidcA2l : getDefinedPidcA2lMap().values()) {
      A2LFile existingA2LFile = a2lFileMap.get(pidcA2l.getA2LId());
      if (existingA2LFile != null) {
        existingA2LFile.setPidcA2l(pidcA2l);
        getDataCache().getMappedA2LFilesMap().put(pidcA2l.getID(), existingA2LFile);
      }
    }

    return a2lFileMap;
  }


  /**
   * @return the alias definition for the Project id card
   */
  public AliasDefinition getAliasDefinition() {
    TAliasDefinition taliasDefinition = getEntityProvider().getDbPIDC(getID()).getTaliasDefinition();
    if (taliasDefinition != null) {
      return getDataCache().getAliasDefMap().get(taliasDefinition.getAdId());
    }
    return null;
  }

  // ICDM-2354
  /**
   * @return the isUnlockedInSession
   */
  public boolean isUnlockedInSession() {
    return getDataCache().isUnlockedInSession(this);
  }

  // ICDM-2354
  /**
  */
  public void addUnlockedPidcInSession() {
    getDataCache().getUnlockedPidsInSession().add(this);
  }

  // ICDM-2354
  /**
  *
  */
  public void removeUnlockedPidcInSession() {
    getDataCache().getUnlockedPidsInSession().remove(this);
  }

  /**
   * @return true, if user can download artifacts
   */
  public boolean canDownloadArtifacts() {
    final NodeAccessRight currentUserRight = getCurrentUserAccessRights();
    return getDataCache().getDataProvider().getCurrentUser().hasApicWriteAccess() ||
        ((currentUserRight != null) && currentUserRight.hasReadAccess());
  }
}
