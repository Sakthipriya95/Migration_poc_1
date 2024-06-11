/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo; // NOPMD by bne4cob on 10/2/13 10:12 AM

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.StringEscapeUtils;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.caltool.dmframework.bo.AbstractDataCache;
import com.bosch.caltool.dmframework.bo.AbstractDataObject;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectidcard;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.ssd.icdm.model.CDRRule;


/**
 * Class to store all data objects related to APIC
 *
 * @author BNE4COB
 */
class DataCache extends AbstractDataCache { // NOPMD by bne4cob on 10/2/13 10:12 AM

  /**
   * Lock for attribute collection, including super groups, groups
   */
  final Object attrSyncLock = new Object();
  /**
   * Lock for Apic Users
   */
  final Object userSyncLock = new Object();
  /**
   * Lock for Link collection
   */
  private final Object linkLock = new Object();

  /**
   * Lock for Unit collection
   */
  private final Object unitLock = new Object();


  /**
   * Lock for Feature collection
   */
  private final Object featureLock = new Object();

  /**
   * Business Objects storage -Attributes
   * <p>
   * Key - Attribute ID<br>
   * Value - Attribute BO
   */
  private final ConcurrentMap<Long, Attribute> attributesMap = new ConcurrentHashMap<>();

  /**
   * Business Objects storage - Sorted Attributes
   */
  private final SortedSet<Attribute> sortedAttributes;

  /**
   * Business Objects storage -Attribute Groups<br>
   * Key - Group ID<br>
   * Value - Group BO
   */
  private final ConcurrentMap<Long, AttrGroup> groupMap = new ConcurrentHashMap<>();

  /**
   * Business Objects storage - Attribute Super Groups<br>
   * Key - ID<br>
   * Value - Super Group BO
   */
  private final ConcurrentMap<Long, AttrSuperGroup> superGroupMap = new ConcurrentHashMap<>();

  /**
   * Business Objects storage -Project Structure Attributes<br>
   * Key - Level<br>
   * Value - Attribute
   */
  private final ConcurrentMap<Long, Attribute> pidcStructAttrMap = new ConcurrentHashMap<>();

  /**
   * Maximum level of structure attributes
   */
  private long pidcStructMaxLvl;// Start with max level as zero

  /**
   * Business Objects storage - Attribute Dependencies<br>
   * Key - ID<br>
   * Value - Attribute Dependency BO
   */
  private final ConcurrentMap<Long, AttrDependency> attrDepMap = new ConcurrentHashMap<>();

  /**
   * Business Objects storage - Attribute Value Dependencies<br>
   * Key - ID<br>
   * Value - Attribute Value Dependency BO
   */
  private final ConcurrentMap<Long, AttrValueDependency> valDepMap = new ConcurrentHashMap<>();

  /**
   * Business Objects storage - Attribute Values<br>
   * Key - ID<br>
   * Value - Attribute Value BO
   */
  private final ConcurrentMap<Long, AttributeValue> attrValuesMap = new ConcurrentHashMap<>();

  /**
   * Business Objects storage - PIDC Attributes<br>
   * Key - ID<br>
   * Value - PIDC Attribute BO
   */
  private final ConcurrentMap<Long, PIDCAttribute> pidcAttrMap = new ConcurrentHashMap<>();

  /**
   * Business Objects storage - PIDC Variant Attributes<br>
   * Key - ID<br>
   * Value - Variant Attribute BO
   */
  private final ConcurrentMap<Long, PIDCAttributeVar> pidcVarAttrMap = new ConcurrentHashMap<>();

  /**
   * Business Objects storage - PIDC Sub Variant Attributes<br>
   * Key - ID<br>
   * Value - Sub Variant Attribute BO
   */
  private final ConcurrentMap<Long, PIDCAttributeSubVar> pidcSubVarAttrMap = new ConcurrentHashMap<>();

  /**
   * Business Objects storage - PIDC Variants<br>
   * Key - ID<br>
   * Value - Variant BO
   */
  private final ConcurrentMap<Long, PIDCVariant> pidcVariantsMap = new ConcurrentHashMap<>();

  /**
   * Business Objects storage - PIDC Sub Variants<br>
   * Key - ID<br>
   * Value - Sub Variant BO
   */
  private final ConcurrentMap<Long, PIDCSubVariant> pidcSubVarMap = new ConcurrentHashMap<>();

  /**
   * Map of all a2l files for SDOM PVERs of PID cards
   * <p>
   * Main Map :<br>
   * Key - PIDC ID; Value - PVER Map(Map of A2L files of PVERs of this PIDC)<br>
   * PVER Map:<br>
   * Key - SDOM PVER Name; Value - A2L File Map : <br>
   * A2L File Map :<br>
   * key - A2L file ID; Value - A2LFile object
   */
  // ICDM-1591
  private final ConcurrentMap<Long, ConcurrentMap<String, ConcurrentMap<Long, A2LFile>>> allPidcPverA2LFileMap =
      new ConcurrentHashMap<>();

  /**
   * only the mapped a2l files
   * <P>
   * Key - PIDC A2L File ID<BR>
   * Value - A2L File Object
   */
  private final ConcurrentMap<Long, A2LFile> allMappedA2LFilesMap = new ConcurrentHashMap<>();

  /**
   * map of all node access rights key:node access id , value:NodeAccessRight object
   */
  private final ConcurrentMap<Long, NodeAccessRight> nodeAccRightsMap = new ConcurrentHashMap<>();

  /**
   * Business Objects storage - Application level access rights<br>
   * Key - ID<br>
   * Value - Application level access right BO
   */
  private final ConcurrentMap<Long, ApicAccessRight> accessRightsMap = new ConcurrentHashMap<>();

  /**
   * Business Objects storage - Users<br>
   * Key - ID<br>
   * Value - User BO
   */
  private final ConcurrentMap<Long, ApicUser> apicUserMap = new ConcurrentHashMap<>();

  /**
   * Business Objects storage - User favourites<br>
   * Key - User<br>
   * Value - Sorted set of favourite PID Cards BO
   */
  private final ConcurrentMap<ApicUser, SortedSet<PIDCard>> apicUserFavorites = new ConcurrentHashMap<>();

  /**
   * List of PIDC Leaf nodes
   */
  private final List<PIDCNode> pidcLeafNodesList;

  /**
   * Attribute root node
   */
  private final AttrRootNode attrRootNode;

  /**
   * PIDC Root node
   */
  private PIDCNode pidcRootNode;

  /**
   * Key - PIDC ID Value - Map of Node ID, Node Object
   */
  private final ConcurrentMap<Long, ConcurrentMap<String, PIDCDetailsNode>> pidDetNodeMap = new ConcurrentHashMap<>();

  /**
   * common parameters and values<br>
   * Key - Parameter Name<br>
   * Value - Parameter value
   */
  private final ConcurrentMap<String, String> commonParams = new ConcurrentHashMap<>();

  /**
   * Business Objects storage - use case groups<br>
   * Key - Primary Key<br>
   * Value - use case group BO
   */
  private final ConcurrentMap<Long, UseCaseGroup> useCaseGroupMap = new ConcurrentHashMap<>();

  /**
   * Business Objects storage - use case groups<br>
   * Key - Primary Key<br>
   * Value - use case group BO
   */
  private final ConcurrentMap<Long, UseCase> useCaseMap = new ConcurrentHashMap<>();

  /**
   * Business Objects storage - use cases<br>
   * Key - Primary Key<br>
   * Value - use case BO
   */
  private final ConcurrentMap<Long, UseCaseSection> useCaseSectionMap = new ConcurrentHashMap<>();

  /**
   * Top level use case groups
   */
  private final Set<UseCaseGroup> topLevelUcgSet;

  // Story 221802
  /**
   * Auditor for MoniCa based reviews
   */
  private ApicUser monicaAuditor;

  /**
   * Apic Data provider
   */
  private final ApicDataProvider dataProvider;

  /**
   * Map of all level attributes
   */
  private final ConcurrentMap<Integer, Attribute> levelAttrMap = new ConcurrentHashMap<>();

  /**
   * Use case root node
   */
  private final UseCaseRootNodeOld useCaseRootNode;

  /**
   * ICDM-1040 Root node for project usecases
   */
  private final ProjFavUcRootNode projUcFavRootNode;

  /**
   * ICDM-1028 Root node for private usecases
   */
  private final UserFavUcRootNode userUcFavRootNode;

  /**
   * Map of node access rights of user . Key : node id , value - NodeAccessRight object
   */
  private final ConcurrentMap<Long, NodeAccessRight> usrNodeAccRghtMap = new ConcurrentHashMap<>();

  /**
   * Business Objects storage - PIDC Det Structure objects
   * <p>
   * Key - ID<br>
   * value - Structure Object
   */
  private final ConcurrentMap<Long, PIDCDetStructure> pidcDetStructMap = new ConcurrentHashMap<>();

  /**
   * Business Objects storage - ICDM Files<br>
   * Key - Primary Key <br>
   * Value - File Object
   */
  private final ConcurrentMap<Long, ConcurrentMap<Long, IcdmFile>> icdmFileMap = new ConcurrentHashMap<>();

  /**
   * key-object id, value- map[link id,link obj] of links for the given obj id
   */
  private final ConcurrentMap<Long, ConcurrentMap<Long, Link>> linksMap = new ConcurrentHashMap<>();


  /**
   * Defines Map of Map for the Top level entity Object
   */
  // icdm-474 Dcn for Top level entity
  private final ConcurrentMap<Long, TopLevelEntity> topLevelEntMap = new ConcurrentHashMap<>();

  // ICDM-1027
  /**
   * Defines map for favourite usecase items of current user
   */
  private final ConcurrentMap<Long, FavUseCaseItem> currentUserUCFavMap = new ConcurrentHashMap<>();

  /**
   * Map <Node ID, Node Object> of all favorite usecase item nodes
   */
  private final ConcurrentMap<Long, FavUseCaseItemNode> currentUsrUcFavNodeMap = new ConcurrentHashMap<>();

  /**
   * Defines Map of Map for the favourite usecase items related to each PIDC
   */
  private final ConcurrentMap<Long, ConcurrentMap<Long, FavUseCaseItem>> projUCFavMap = new ConcurrentHashMap<>();

  /**
   * Key - PIDC ID Value - Map <Node ID, Node Object>
   */
  private final ConcurrentMap<Long, ConcurrentMap<Long, FavUseCaseItemNode>> projUcFavNodeMap =
      new ConcurrentHashMap<>();


  // iCDM-514
  /**
   * Defines map of A2L FileID(key) and A2Lcontents (as value)
   */
  private final ConcurrentMap<Long, A2LFileInfo> a2lFileContents = new ConcurrentHashMap<>();

  // Icdm-513
  /**
   * Defines map of Cdr Units
   */
  private final ConcurrentMap<String, Unit> unitMap = new ConcurrentHashMap<>();

  /**
   * Defines set of focus matrix details - key: fmId (primary key)
   */
  private final Map<Long, FocusMatrixDetails> focusMatrixDetailsMap = new ConcurrentHashMap<>();
  /**
   * Stores focus matrix version. Key - primary key of fucus matrix version
   */
  // ICDM-2569
  private final Map<Long, FocusMatrixVersion> focusMatrixVersionMap = new ConcurrentHashMap<>();

  /**
   * Stores focus matrix version attributes. Key - primary key of fucus matrix version attr
   */
  // ICDM-2569
  private final Map<Long, FocusMatrixVersionAttr> focusMatrixVersAttrMap = new ConcurrentHashMap<>();

  // Icdm-513
  /**
   * Defines map of Featute Values
   */
  private final ConcurrentMap<Long, Feature> featureMap = new ConcurrentHashMap<>();

  // ICDM-959
  /**
   * map which contains nodetype and set of nodeID's which has links
   */
  private final ConcurrentMap<String, Set<Long>> linkNodeIdsMap = new ConcurrentHashMap<>();


  /**
   * iCDM-756 : Defines map of SDOMPver name and its variant names
   */
  private final ConcurrentMap<String, Set<String>> sdomVariantsMap = new ConcurrentHashMap<>();

  /**
   * Defines map of SDOMPver name and its variant names from TA2lFileInfo
   */
  private final ConcurrentMap<String, Set<String>> sdomTA2LVariantsMap = new ConcurrentHashMap<>();
  /**
   * ICDM-933 set which contains objects that are loaded in cache
   */
  private final Set<AbstractDataObject> objLoadedMap;
  /**
   * ICdm-954 Attr Characteristics Map
   */
  private final ConcurrentMap<Long, AttributeCharacteristic> attrCharMap = new ConcurrentHashMap<>();
  /**
   * ICdm-954 Attr Characteristic Value Map
   */
  private final ConcurrentMap<Long, AttributeCharacteristicValue> attrValCharMap = new ConcurrentHashMap<>();

  private final ConcurrentMap<A2LFile, VCDMPST> vCDMPSTMap = new ConcurrentHashMap<>();

  /**
   * Attributes that have mapping to SSD Features
   */
  private Set<Attribute> mappedAttrSet;

  /**
   * ICdm-1066 map for Features with Attr Id as Key
   */
  private final ConcurrentMap<Long, Feature> attrFeaMap = new ConcurrentHashMap<>();
  /**
   * Messages<br>
   * Key - Message Key <br>
   * Value - Message
   */
  private final ConcurrentMap<String, String> messageMap = new ConcurrentHashMap<>();

  /**
   * Defines Map of ParamName+Type(key) and CDRRule(value)
   */
  private final ConcurrentMap<String, List<CDRRule>> cdrRulesMap = new ConcurrentHashMap<>();

  /**
   * Defines rules<param name, rules> for the rule-set id
   */
  private final ConcurrentMap<Long, ConcurrentMap<String, List<CDRRule>>> ruleSetRulesMap = new ConcurrentHashMap<>();

  /**
   * Cache for pidc versions
   */
  private final ConcurrentMap<Long, PIDCVersion> allPidcVersMap = new ConcurrentHashMap<>();

  /**
   * Map of all project ID cards
   */
  private final ConcurrentMap<Long, PIDCard> allPidcMap = new ConcurrentHashMap<>();

  /**
   * Map of PIDC A2L objects
   */
  private final ConcurrentMap<Long, PIDCA2l> allPidcA2lMap = new ConcurrentHashMap<>();

  // ICDM-1557
  /**
   * Use case root node for focus matrix tree
   */
  private final FocusMatrixUseCaseRootNode focusMatrixUCRootNode;

  /**
   * ICDM-1836 map to store MandatoryAttr against each id
   */
  private final ConcurrentMap<Long, MandatoryAttr> mandatoryAttrObjectsMap = new ConcurrentHashMap<>();

  /**
   * ICDM-1836 map to store list of mandatory Attributes against each level attr value id
   */
  private final ConcurrentMap<Long, Set<Attribute>> mandatoryAttrsMap = new ConcurrentHashMap<>();

  /**
   * Icdm-1844 Map for Storing the alias definitions
   */
  private final Map<Long, AliasDefinition> aliasDefMap = new ConcurrentHashMap<>();

  private final Map<Long, AliasDetail> aliasDetailMap = new ConcurrentHashMap();

  /**
   * Mandatory level attribute value ID
   */
  private Long mandatoryLvlAttrValId;


  private final PIDCVersionsExclusionHandler pidcverSionExclusnHandler;

  /**
   * Business Objects storage - Link<br>
   * Key - ID<br>
   * Value - Link BO
   */
  private final ConcurrentMap<Long, Link> linkMap = new ConcurrentHashMap<>();

  /**
   * get the sorted alias defintion
   */

  private final SortedSet<AliasDefinition> sortedAliasDef =
      Collections.synchronizedSortedSet(new TreeSet<AliasDefinition>());

  // new hashmap for ws system
  private final Map<String, String> wsSystemMap = new ConcurrentHashMap<>();

  // ICDM-2354
  /**
   * Set to hold PIDCs which are unlocked
   */
  private final Set<PIDCard> unlockedPidsInSession = new TreeSet<PIDCard>();

  /**
   * ICDM-2296 Predefined Attr Value Map
   */
  private final ConcurrentMap<Long, PredefinedAttrValue> preDefndAttrValMap = new ConcurrentHashMap<>();
  /**
   * ICDM-2296 Predefined Attr Value Validity Map
   */
  private final ConcurrentMap<Long, PredefinedAttrValuesValidity> preDefndValdtyMap = new ConcurrentHashMap<>();


  /**
   * Constructor
   *
   * @param dataProvider data provider
   * @param langSelected selected language
   */
  public DataCache(final ApicDataProvider dataProvider, final Language langSelected) {
    super();

    this.dataProvider = dataProvider;
    ObjectStore.getInstance().setLanguage(langSelected);

    this.sortedAttributes = Collections.synchronizedSortedSet(new TreeSet<Attribute>());

    this.topLevelUcgSet = Collections.synchronizedSet(new HashSet<UseCaseGroup>());
    this.pidcLeafNodesList = Collections.synchronizedList(new ArrayList<PIDCNode>());


    this.attrRootNode = new AttrRootNode(this.dataProvider);
    this.useCaseRootNode = new UseCaseRootNodeOld(this.dataProvider);
    this.focusMatrixUCRootNode = new FocusMatrixUseCaseRootNode(this.dataProvider);
    // Icdm-474 dcn for top level entity

    // ICDM-933
    this.objLoadedMap = Collections.synchronizedSet(new HashSet<AbstractDataObject>());


    // ICDM-1027

    // ICDM-1040
    this.projUcFavRootNode = new ProjFavUcRootNode();
    // ICDM-1028
    this.userUcFavRootNode = new UserFavUcRootNode();

    this.pidcverSionExclusnHandler = new PIDCVersionsExclusionHandler(dataProvider);

  }

  /**
   * @return the map of specific attributes like project name attribute
   */
  protected final Map<Integer, Attribute> getSpecificAttributeMap() {
    synchronized (this.attrSyncLock) {
      return this.levelAttrMap;
    }
  }

  /**
   * Returns the value of the parameter identified by name
   *
   * @param name parameter name
   * @return the parameter value
   */
  public final String getParameterValue(final String name) {
    return this.commonParams.get(name);
  }

  /**
   * Returns the value of the divisions for which questionnaire can be set
   *
   * @param name parameter name
   * @return the parameter value
   */
  public final String getDivisionsForQnaires(final String name) {
    return this.commonParams.get(name);
  }

  /**
   * @return current language
   */
  public final Language getLanguage() {
    return ObjectStore.getInstance().getLanguage();
  }

  /**
   * @param currentLanguage current language
   */
  public final void setLanguage(final Language currentLanguage) {
    ObjectStore.getInstance().setLanguage(currentLanguage);
  }

  /**
   * @return root node
   */
  public final AttrRootNode getAttrRootNode() {
    return this.attrRootNode;
  }

  /**
   * @return root node of pidc tree
   */
  public final PIDCNode getPidcRootNode() {
    return this.pidcRootNode;
  }

  /**
   * Get the version object with the given key. if object is not available in the cache, a new instance is created,
   * after verifying the database
   *
   * @param pidcVersID PIDC's ID
   * @return PIDCVersion
   */
  public final PIDCVersion getPidcVersion(final Long pidcVersID) {
    return this.allPidcVersMap.get(pidcVersID);
  }

  /**
   * Find the PIDC Version with the given ID. If object does not exist, it is retrieved from database.
   *
   * @param pidcVersID version ID
   * @param loadFromDB if true, version is verified with DB
   * @return PIDC Version object
   * @throws DataException if pidcVersID is invalid
   */
  public final PIDCVersion getPidcVersion(final Long pidcVersID, final boolean loadFromDB) throws DataException {
    PIDCVersion version = getPidcVersion(pidcVersID);

    if ((version == null) && loadFromDB) {
      getDataProvider().getLogger().debug("Invoking getPidcVersion from DB for ID : " + pidcVersID);
      version = new PIDCVersion(this.dataProvider, pidcVersID);
      if (version.isValid()) {
        PIDCard pidc = version.getPidc();
        // if version is valid and PIDC is null, the PIDC object is not created yet.
        if (((pidc != null) && pidc.isVersionsLoaded())) {
          // Reset versions, only if it is loaded atlease once
          pidc.resetVersions();
        }
      }
      else {
        getDataProvider().getLogger().warn("Invalid PIDC Version ID : " + pidcVersID);
        // if version is not valid, remove the new object from the data cache.
        this.allPidcVersMap.remove(pidcVersID);
        throw new DataException("Invalid PIDC Version ID : " + pidcVersID);
      }
    }

    return version;
  }

  public final synchronized PIDCA2l getPidcA2l(final Long pidcA2lID) {
    return this.allPidcA2lMap.get(pidcA2lID);
  }

  /**
   * Retrieve the PIDCard with the name defined by the given attribute value.
   *
   * @param pidcName PIDC name attribute value
   * @return PIDC
   */
  public final synchronized PIDCard getPidCard(final AttributeValue pidcName) {
    for (PIDCard pidc : this.allPidcMap.values()) {
      if (pidc.getNameValue().getID().longValue() == pidcName.getValueID().longValue()) {
        return pidc;
      }
    }
    return null;
  }

  /**
   * @return Map that contains all the attributes
   */
  public final Map<Long, Attribute> getAllAttributes() {
    synchronized (this.attrSyncLock) {
      return this.attributesMap;
    }
  }

  /**
   * Returns all attributes in sorted order
   *
   * @return SortedSet<Attribute>
   */
  public final SortedSet<Attribute> getSortedAttributes() {
    synchronized (this.attrSyncLock) {
      this.sortedAttributes.clear();
      this.sortedAttributes.addAll(this.attributesMap.values());
      return this.sortedAttributes;
    }
  }

  /**
   * Returns all attributes in sorted order
   *
   * @param includeDeleted include deleted attributes or not
   * @return SortedSet<Attribute>
   */
  public final SortedSet<Attribute> getSortedAttributes(final boolean includeDeleted) {
    if (includeDeleted) {
      return getSortedAttributes();
    }
    SortedSet<Attribute> undelAttrSet = new TreeSet<>(getSortedAttributes());
    Iterator<Attribute> itr = undelAttrSet.iterator();
    while (itr.hasNext()) {
      if (itr.next().isDeleted()) {
        itr.remove();
      }
    }
    return undelAttrSet;
  }

  /**
   * @return Map of PIDC Attributes
   */
  protected final synchronized Map<Long, PIDCAttribute> getAllPidcAttrMap() {
    return this.pidcAttrMap;
  }

  /**
   * @return Map of PIDC Variant attributes
   */
  protected final synchronized Map<Long, PIDCAttributeVar> getAllPidcVarAttrMap() {
    return this.pidcVarAttrMap;
  }

  /**
   * @return Map of PIDC Sub Variant attributes
   */
  protected final synchronized Map<Long, PIDCAttributeSubVar> getAllPidcSubVarAttrMap() {
    return this.pidcSubVarAttrMap;
  }

  /**
   * @return Map of PIDC structure attributes
   */
  public final Map<Long, Attribute> getPidcStructAttrMap() {
    synchronized (this.attrSyncLock) {
      return this.pidcStructAttrMap;
    }
  }

  /**
   * @return Map of all groups
   */
  public final Map<Long, AttrGroup> getAllGroups() {
    synchronized (this.attrSyncLock) {
      return this.groupMap;
    }
  }

  /**
   * @return Map of all super groups
   */
  public final Map<Long, AttrSuperGroup> getAllSuperGroups() {
    synchronized (this.attrSyncLock) {
      return this.superGroupMap;
    }
  }

  /**
   * @return Map of all Apic users
   */
  protected final Map<Long, ApicUser> getAllApicUsersMap() {

    synchronized (this.userSyncLock) {
      return this.apicUserMap;
    }
  }

  /**
   * @return Map of all Apic Access rights
   */
  protected final synchronized Map<Long, ApicAccessRight> getAllApicAccessRightsMap() {
    return this.accessRightsMap;
  }

  /**
   * @return Map of all Apic users
   */
  public final SortedSet<ApicUser> getAllApicUsers() {
    synchronized (this.userSyncLock) {
      return new TreeSet<ApicUser>(this.apicUserMap.values());
    }
  }

  /**
   * @return Map of all active PIDVersions
   */
  public final synchronized Map<Long, PIDCVersion> getAllActivePIDCVersions() {
    ConcurrentMap<Long, PIDCVersion> retMap = new ConcurrentHashMap<>();
    for (PIDCard pidc : getAllPidcMap().values()) {
      retMap.put(pidc.getActiveVersion().getID(), pidc.getActiveVersion());
    }
    return retMap;
  }

  /**
   * @return Map of all PIDC Variants
   */
  protected final synchronized Map<Long, PIDCVariant> getAllPidcVariants() {
    return this.pidcVariantsMap;
  }

  /**
   * @return Map of all PIDC sub variants
   */
  protected final synchronized Map<Long, PIDCSubVariant> getAllPidcSubVariants() {
    return this.pidcSubVarMap;
  }

  /**
   * Key : node access id , value - NodeAccessRight object
   *
   * @return Map of Node Access Rights
   */
  protected final synchronized Map<Long, NodeAccessRight> getAllNodeAccRights() {
    return this.nodeAccRightsMap;
  }

  // Icdm-346
  /**
   * Map of node access rights of user . Key : node id , value - NodeAccessRight object
   *
   * @return returns the node acccess Rights of a User
   */
  protected final synchronized Map<Long, NodeAccessRight> getUserNodeAccRights() {
    return this.usrNodeAccRghtMap;
  }

  // Icdm-346
  /**
   * @return returns the node acccess Rights of a Node From the User Map
   * @param nodeID Node ID
   */
  protected final NodeAccessRight getNodeAccRights(final Long nodeID) {
    return this.usrNodeAccRghtMap.get(nodeID);
  }

  /**
   * @param attributeID the ID
   * @return Attribute object
   */
  public final Attribute getAttribute(final Long attributeID) {
    synchronized (this.attrSyncLock) {
      return this.attributesMap.get(attributeID);
    }
  }

  /**
   * @param groupID the ID
   * @return Group object
   */
  public final AttrGroup getGroup(final Long groupID) {
    synchronized (this.attrSyncLock) {
      return this.groupMap.get(groupID);
    }
  }

  /**
   * @param superGroupID the ID
   * @return Super group object
   */
  public final AttrSuperGroup getSuperGroup(final Long superGroupID) {
    synchronized (this.attrSyncLock) {
      return this.superGroupMap.get(superGroupID);
    }
  }

  /**
   * @param dependencyID the ID
   * @return Attribute dependency object
   */
  public final AttrDependency getAttrDependency(final Long dependencyID) {
    synchronized (this.attrSyncLock) {
      if (!getAttrDependenciesMap().containsKey(dependencyID)) {
        getAttrDependenciesMap().put(dependencyID, new AttrDependency(this.dataProvider, dependencyID));
      }
      return getAttrDependenciesMap().get(dependencyID);
    }
  }

  /**
   * @param dependencyID the ID
   * @return Attribute value dependency object
   */
  public final AttrValueDependency getValueDependency(final Long dependencyID) {
    AttrValueDependency valueDependency;
    synchronized (this.attrSyncLock) {
      if (this.valDepMap.containsKey(dependencyID)) {
        return this.valDepMap.get(dependencyID);
      }
      valueDependency = new AttrValueDependency(this.dataProvider, dependencyID);
      this.valDepMap.put(dependencyID, valueDependency);
    }
    return valueDependency;

  }

  /**
   * @param linkID id
   * @return Linkobject
   */
  public final Link getLink(final Long linkID) {
    Link link;

    if (this.linkMap.containsKey(linkID)) {
      return this.linkMap.get(linkID);
    }
    link = new Link(this.dataProvider, linkID);
    this.linkMap.put(linkID, link);

    return link;

  }

  /**
   * @param valueID value ID
   * @return Attribute value object
   */
  public final AttributeValue getAttrValue(final Long valueID) {

    synchronized (this.attrSyncLock) {
      // TODO : Import excel parser input null value ID
      if (valueID == null) {
        return null;
      }
      if (!getAllAttrValuesMap().containsKey(valueID)) {
        getAllAttrValuesMap().put(valueID, new AttributeValue(this.dataProvider, valueID));
      }

      return getAllAttrValuesMap().get(valueID);
    }
  }

  /**
   * @param userName user name
   * @return Apic User object
   */
  public final ApicUser getApicUser(final String userName) {

    ApicUser returnValue = null;
    synchronized (this.userSyncLock) {
      // search the user
      for (ApicUser user : this.apicUserMap.values()) {
        if ((user.getUserName() != null) && user.getUserName().equals(userName)) {
          returnValue = user;

          break;
        }
      }

      if ((returnValue == null) && userName.equals(getAppUsername())) {
        returnValue = this.dataProvider.getDataLoader().createApicUser(userName);
      }
    }
    return returnValue;
  }

  /**
   * @return Current user object
   */
  public final ApicUser getCurrentUser() {
    return getApicUser(getAppUsername());
  }

  /**
   * @param pidcAttrID the ID
   * @return PIDC attribute object
   */
  public final synchronized PIDCAttribute getPidcAttribute(final Long pidcAttrID) {
    return this.pidcAttrMap.get(pidcAttrID);
  }

  /**
   * @param pidcVarAttrID the ID
   * @return PIDC variant attribute
   */
  public final synchronized PIDCAttributeVar getPidcVaraintAttr(final Long pidcVarAttrID) {
    return this.pidcVarAttrMap.get(pidcVarAttrID);
  }

  /**
   * @param pidcVariantID the ID
   * @return PIDC variant object
   */
  public final synchronized PIDCVariant getPidcVaraint(final Long pidcVariantID) {

    PIDCVariant pidcVariant = this.pidcVariantsMap.get(pidcVariantID);

    if (pidcVariant == null) {
      pidcVariant = new PIDCVariant(this.dataProvider, pidcVariantID);
    }

    return pidcVariant;
  }

  /**
   * Get PIDC Sub Variant for the given sub-variant-id
   *
   * @param pidcSubVariantID SubVariantID
   * @return PIDCSubVariant
   */
  public final synchronized PIDCSubVariant getPidcSubVaraint(final Long pidcSubVariantID) {

    PIDCSubVariant pidcSubVariant = this.pidcSubVarMap.get(pidcSubVariantID);

    if (pidcSubVariant == null) {
      pidcSubVariant = new PIDCSubVariant(this.dataProvider, pidcSubVariantID);
    }

    return pidcSubVariant;
  }

  /**
   * @return Pidc Struct Max Level for PIDC tree
   */
  public final long getPidcStructMaxLevel() {
    return this.pidcStructMaxLvl;
  }

  /**
   * @param maxLevel maximum level
   */
  protected final void setPidcStructMaxLevel(final long maxLevel) {
    this.pidcStructMaxLvl = maxLevel;
  }

  /**
   * @return PVer Attribute
   */
  public final Attribute getPVerAttribute() {
    synchronized (this.attrSyncLock) {
      return this.levelAttrMap.get(ApicConstants.SDOM_PROJECT_NAME_ATTR);
    }
  }

  /**
   * @return Vcdm Project attribute
   */
  public final Attribute getVCdmAprjAttribute() {
    synchronized (this.attrSyncLock) {
      return this.levelAttrMap.get(ApicConstants.VCDM_APRJ_NAME_ATTR);
    }
  }

  /**
   * @return Variant Coding attribute
   */
  public final Attribute getVariantCodingAttribute() {
    synchronized (this.attrSyncLock) {
      return this.levelAttrMap.get(ApicConstants.VARIANT_CODING_ATTR);
    }
  }

  /**
   * @param apicUser apic user
   * @return sorted set of pidc objects in favourites
   */
  public final synchronized SortedSet<PIDCVersion> getUserPidcFavorites(final ApicUser apicUser) {
    return this.dataProvider.getDataLoader().getUserPidcFavVersions(apicUser);
  }

  /**
   * @return Map of all user's favourite PIDC
   */
  protected final Map<ApicUser, SortedSet<PIDCard>> getApicUserFavorites() {
    return this.apicUserFavorites;
  }

  /**
   * @return the Pidc Name Attribute
   */
  public final Attribute getProjNameAttribute() {
    synchronized (this.attrSyncLock) {
      return this.levelAttrMap.get(ApicConstants.PROJECT_NAME_ATTR);
    }
  }


  /**
   * @return the Variant Name Attribute
   */
  public final Attribute getVarNameAttribute() {
    synchronized (this.attrSyncLock) {
      return this.levelAttrMap.get(ApicConstants.VARIANT_CODE_ATTR);
    }
  }


  /**
   * @return the Sub-Variant Name Attribute
   */
  public final Attribute getSubvarNameAttribute() {
    synchronized (this.attrSyncLock) {
      return this.levelAttrMap.get(ApicConstants.SUB_VARIANT_CODE_ATTR);
    }
  }

  /**
   * @return the list of unused PIDC name attribute values
   */
  public final synchronized List<AttributeValue> getUnusedPidcNames() {
    final List<AttributeValue> retList = new ArrayList<AttributeValue>();
    retList.addAll(getProjNameAttribute().getAttrValues(false));

    TabvProjectidcard dbPidc;
    AttributeValue nameVal;

    for (PIDCard pidc : this.allPidcMap.values()) {
      dbPidc = this.dataProvider.getEntityProvider().getDbPIDC(pidc.getID());
      nameVal = getAttrValue(dbPidc.getTabvAttrValue().getValueId());
      retList.remove(nameVal);
    }
    return retList;
  }

  /**
   * @return the useCaseGroupMap
   */
  public final Map<Long, UseCaseGroup> getAllUseCaseGroupMap() {
    return this.useCaseGroupMap;
  }

  /**
   * Returns the use case group identified by the ID
   *
   * @param ucgID Group ID
   * @return the use case group
   */
  public final UseCaseGroup getUseCaseGroup(final Long ucgID) {
    UseCaseGroup ucg = this.useCaseGroupMap.get(ucgID);
    if (ucg == null) {
      ucg = new UseCaseGroup(this.dataProvider, ucgID);
      this.useCaseGroupMap.put(ucgID, ucg);
    }
    return ucg;
  }

  /**
   * @return the useCaseMap
   */
  public final Map<Long, UseCase> getAllUseCaseMap() {
    return this.useCaseMap;
  }

  /**
   * Returns the use case identified by the ID
   *
   * @param ucID use case ID
   * @return the use case
   */
  public final UseCase getUseCase(final Long ucID) {
    UseCase ucase = this.useCaseMap.get(ucID);
    if (ucase == null) {
      ucase = new UseCase(this.dataProvider, ucID);
      this.useCaseMap.put(ucID, ucase);
    }
    return ucase;
  }

  /**
   * Returns the fm details identified by the ID
   *
   * @param fmID primary key
   * @return the FocusMatrixDetails
   */
  public final FocusMatrixDetails getFocusMatrix(final Long fmID) {
    FocusMatrixDetails fmDetails = this.focusMatrixDetailsMap.get(fmID);
    if (fmDetails == null) {
      fmDetails = new FocusMatrixDetails(this.dataProvider, fmID);
      this.focusMatrixDetailsMap.put(fmID, fmDetails);
    }
    return fmDetails;
  }

  /**
   * Returns the FM Version identified by the ID
   *
   * @param fmVersID Primary Key - FM version ID
   * @return the FocusMatrixVersion
   */
  // ICDM-2569
  public final FocusMatrixVersion getFocusMatrixVersion(final Long fmVersID) {
    return this.focusMatrixVersionMap.get(fmVersID);
  }

  /**
   * Returns the FM Version Attr identified by the ID
   *
   * @param fmVersAttrID Primary Key - FM version attribute ID
   * @return the FocusMatrixVersionAttr
   */
  // ICDM-2569
  public final FocusMatrixVersionAttr getFocusMatrixVersionAttr(final Long fmVersAttrID) {
    return this.focusMatrixVersAttrMap.get(fmVersAttrID);
  }

  /**
   * Add or remove Focus Matrix Version from the global map
   *
   * @param fmVers Focus Matrix Version object
   * @param add if true, adds the object, else removes it
   */
  // ICDM-2569
  public void addRemoveFocusMatrixVersion(final FocusMatrixVersion fmVers, final boolean add) {
    if (add) {
      this.focusMatrixVersionMap.put(fmVers.getID(), fmVers);
    }
    else {
      this.focusMatrixVersionMap.remove(fmVers.getID());
    }
  }

  /**
   * Add or remove Focus Matrix Version Attribute from the global map
   *
   * @param fmVersionAttr Focus Matrix Version object
   * @param add if true, adds the object, else removes it
   */
  // ICDM-2569
  public void addRemoveFocusMatrixVersionAttr(final FocusMatrixVersionAttr fmVersionAttr, final boolean add) {
    if (add) {
      this.focusMatrixVersAttrMap.put(fmVersionAttr.getID(), fmVersionAttr);
    }
    else {
      this.focusMatrixVersAttrMap.remove(fmVersionAttr.getID());
    }

  }

  /**
   * @return the useCaseSectionMap
   */
  public final Map<Long, UseCaseSection> getAllUseCaseSectionMap() {
    return this.useCaseSectionMap;
  }

  /**
   * Returns the use case section identified by the ID
   *
   * @param ucsID section ID
   * @return the use case section
   */
  public final UseCaseSection getUseCaseSection(final Long ucsID) {
    UseCaseSection ucs = this.useCaseSectionMap.get(ucsID);
    if (ucs == null) {
      ucs = new UseCaseSection(this.dataProvider, ucsID);
      this.useCaseSectionMap.put(ucsID, ucs);
    }
    return ucs;
  }

  /**
   * @return the topLevelUcgSet
   */
  public final Set<UseCaseGroup> getTopLevelUCGSet() {
    return this.topLevelUcgSet;
  }

  /**
   * @return the map of common params
   */
  protected final Map<String, String> getCommonParams() {
    return this.commonParams;
  }

  /**
   * @return the attrValuesMap
   */
  public final Map<Long, AttributeValue> getAllAttrValuesMap() {
    return this.attrValuesMap;
  }

  /**
   * @return the attrDependenciesMap
   */
  protected final Map<Long, AttrDependency> getAttrDependenciesMap() {
    return this.attrDepMap;
  }

  /**
   * @param pidcRootNode the pidcRootNode to set
   */
  protected final void setPidcRootNode(final PIDCNode pidcRootNode) {
    this.pidcRootNode = pidcRootNode;
  }

  /**
   * @return the pidcLeafNodesList
   */
  protected final List<PIDCNode> getPidcLeafNodesList() {
    return this.pidcLeafNodesList;
  }

  protected final Map<Long, A2LFile> getMappedA2LFilesMap() {
    return this.allMappedA2LFilesMap;
  }

  protected final A2LFile getMappedA2LFile(final Long pidcA2lFileID, final Long a2lFileID) {
    A2LFile mappedA2lFile = this.allMappedA2LFilesMap.get(pidcA2lFileID);
    if (mappedA2lFile == null) {
      mappedA2lFile = new A2LFile(this.dataProvider, a2lFileID);
      PIDCA2l pidcA2l = new PIDCA2l(this.dataProvider, pidcA2lFileID);
      mappedA2lFile.setPidcA2l(pidcA2l);
      this.allMappedA2LFilesMap.put(pidcA2lFileID, mappedA2lFile);
    }
    return mappedA2lFile;
  }

  /**
   * Get PIDC Sub Variant Attr for the given sub-variant-attr id
   *
   * @param pidcSubVarAttrID sub-variant-attr id
   * @return PIDC Sub Variant Attr
   */
  public final PIDCAttributeSubVar getPidcSubVaraintAttr(final Long pidcSubVarAttrID) {
    return this.pidcSubVarAttrMap.get(pidcSubVarAttrID);
  }

  /**
   * Gets the set of APIC users excluding the users passed as input.
   *
   * @param accessRights accessRights the set of users to exclude (node access objects)
   * @return the collection of APIC users
   */
  public final SortedSet<ApicUser> getValidApicUsers(final SortedSet<NodeAccessRight> accessRights) {

    final SortedSet<ApicUser> retUsers = new TreeSet<ApicUser>();
    retUsers.addAll(getAllApicUsers());

    // Checking for dulicate apic users
    if ((accessRights != null) && !accessRights.isEmpty()) {
      for (NodeAccessRight nodeAccessRight : accessRights) {
        retUsers.remove(nodeAccessRight.getApicUser());
      }
    }

    retUsers.remove(this.dataProvider.getMonicaAuditor());
    return retUsers;
  }

  /**
   * @return the use Case Root Node
   */
  public final UseCaseRootNodeOld getUseCaseRootNode() {
    return this.useCaseRootNode;
  }

  /**
   * @return Map of all PIDCDetStructure
   */
  protected final synchronized Map<Long, PIDCDetStructure> getAllPidcDetStructure() {
    return this.pidcDetStructMap;
  }

  /**
   * Method to get the PIDCDetStructure object for the pdsID
   *
   * @param pdsId pidcDetStructID
   * @return PIDCDetStructure
   */
  public PIDCDetStructure getPidcDetStructure(final Long pdsId) {
    PIDCDetStructure pidcDetStructure = this.pidcDetStructMap.get(pdsId);

    if (pidcDetStructure == null) {
      pidcDetStructure = new PIDCDetStructure(this.dataProvider, pdsId);
    }

    return pidcDetStructure;
  }

  /**
   * @return the apic data provider
   */
  public ApicDataProvider getDataProvider() {
    return this.dataProvider;
  }

  /**
   * Returns all files mapped to a given node
   *
   * @param nodeID node ID
   * @return Map of all Icdm Files
   */
  protected final synchronized Map<Long, IcdmFile> getIcdmFilesOfNode(final Long nodeID) {
    ConcurrentMap<Long, IcdmFile> nodeFileMap = this.icdmFileMap.get(nodeID);
    if (nodeFileMap == null) {
      nodeFileMap = new ConcurrentHashMap<Long, IcdmFile>();
      this.icdmFileMap.put(nodeID, nodeFileMap);
    }
    return nodeFileMap;
  }


  /**
   * Get the ICDM file object for the given file ID and node ID
   *
   * @param nodeID node ID
   * @param fileID file ID
   * @return the ICDM file object Icdm-543
   */
  public final IcdmFile getIcdmFile(final Long nodeID, final Long fileID) {
    IcdmFile file = getIcdmFilesOfNode(nodeID).get(fileID);
    if (file == null) {
      file = new IcdmFile(getDataProvider(), fileID);
      getIcdmFilesOfNode(nodeID).put(fileID, file);
    }
    return file;

  }


  /**
   * @return the Top Level Entity Map // icdm-474 Dcn for Top level entity
   */
  protected Map<Long, TopLevelEntity> getTopEntList() {
    return this.topLevelEntMap;
  }


  /**
   * Icdm-513
   *
   * @return the Unit Map
   */
  protected final ConcurrentMap<String, Unit> getUnitMap() {
    synchronized (this.unitLock) {
      if (this.unitMap.isEmpty()) {
        this.unitMap.putAll(this.dataProvider.getDataLoader().fetchAllUnits());
      }
    }
    return this.unitMap;
  }

  /**
   * @return the units as sorted set
   */
  public final SortedSet<Unit> getUnitSet() {
    return new TreeSet<Unit>(getUnitMap().values());
  }


  /**
   * ICDM-959 fetchs the nodeId's which has links
   *
   * @return map which contains nodetype and corresponding id's which has links
   */
  protected final ConcurrentMap<String, Set<Long>> getLinkNodeIds() {
    synchronized (this.linkLock) {
      if (this.linkNodeIdsMap.isEmpty()) {
        this.linkNodeIdsMap.putAll(this.dataProvider.getDataLoader().fetchLinkNodes());
      }
    }
    return this.linkNodeIdsMap;
  }


  /**
   * ICDM-959 when link is added to object for first time , it is added in map
   *
   * @param nodeID node ID
   * @param nodeType node type in String
   */
  protected void addToLinkNodeIdsMap(final Long nodeID, final String nodeType) {

    if (this.linkNodeIdsMap.get(nodeType) == null) {
      // if object type is not available, new entry is created
      Set<Long> linkNodeSet = new TreeSet<Long>();
      linkNodeSet.add(nodeID);
      this.linkNodeIdsMap.put(nodeType, linkNodeSet);
    }
    else {
      this.linkNodeIdsMap.get(nodeType).add(nodeID);
    }

  }

  /**
   * ICDM-959 when last link for the obj is deleted , it is removed from the map
   *
   * @param node object
   * @param nodeType node type in String
   */
  protected void removeFrmLinkNodeIdsMap(final AbstractDataObject node, final String nodeType) {
    // remove from map only if no links are available for that node
    if (getLinks(node).isEmpty()) {
      Set<Long> linkNodes = this.linkNodeIdsMap.get(nodeType);
      if (null != linkNodes) {
        linkNodes.remove(node.getID());
        // if the nodes for object_type are emtpy , remove the type also from map
        if (linkNodes.isEmpty()) {
          this.linkNodeIdsMap.remove(nodeType);
        }
      }
    }
  }

  /**
   * ICDM-959 Checks whether the node has link or not
   *
   * @param node attr/grp/supergrp/uc/ucs object
   * @return true if node has links
   */
  public boolean hasLink(final AbstractDataObject node) {
    String nodeType = node.getEntityType().getEntityTypeString();
    Map<String, Set<Long>> nodes = getLinkNodeIds();
    boolean flag = false;
    if ((nodes.get(nodeType) != null) && nodes.get(nodeType).contains(node.getID())) {
      flag = true;
    }
    return flag;
  }


  /**
   * Get all a2l file contents map cached for this session.
   *
   * @return the a2l file contents
   */
  // iCDM-514
  protected Map<Long, A2LFileInfo> getA2lFileContentsMap() {
    return this.a2lFileContents;
  }

  /**
   * Get all A2L file objects available for the given pver name in the given PIDC
   *
   * @param pidcID PIDC ID
   * @param pverName PVER Name
   * @param loadFromDb if true, loads the details from database
   * @return Map. Key - A2L file ID, Value - A2L file object
   */
  // ICDM-1591
  public Map<Long, A2LFile> getPverAllA2LMap(final Long pidcID, final String pverName, final boolean loadFromDb) {
    ConcurrentMap<String, ConcurrentMap<Long, A2LFile>> pidcPverMap = this.allPidcPverA2LFileMap.get(pidcID);
    if (pidcPverMap == null) {
      pidcPverMap = new ConcurrentHashMap<>();
      this.allPidcPverA2LFileMap.put(pidcID, pidcPverMap);
    }
    // A2L files are created and stored against PIDC, instead of PIDC version,
    // since there should be only one A2L file per PIDC
    ConcurrentMap<Long, A2LFile> retMap = pidcPverMap.get(pverName);
    if ((retMap == null) && loadFromDb) {
      retMap = new ConcurrentHashMap<>();
      pidcPverMap.put(pverName, retMap);
      retMap.putAll(this.dataProvider.getDataLoader().loadPverA2LFiles(pidcID, pverName));
    }
    return retMap;
  }

  /**
   * Get the virtual nodes for the given PIDC
   *
   * @param pidcID PIDC's ID
   * @return map of virtual nodes, with key=node id, value=node object
   */
  public ConcurrentMap<String, PIDCDetailsNode> getPidcDetNodes(final long pidcID) {
    ConcurrentMap<String, PIDCDetailsNode> nodeMap = this.pidDetNodeMap.get(pidcID);

    if (nodeMap == null) {
      nodeMap = new ConcurrentHashMap<>();
      this.pidDetNodeMap.put(pidcID, nodeMap);
    }

    return nodeMap;
  }

  /**
   * Get the PIDC virtual node
   *
   * @param pidcID PIDC's id
   * @param nodeID node id
   * @return details node
   */
  public PIDCDetailsNode getPidcDetNode(final long pidcID, final String nodeID) {
    return getPidcDetNodes(pidcID).get(nodeID);
  }

  /**
   * Returns all links mapped to a given node
   *
   * @param nodeID node ID
   * @param nodeType node type in String
   * @return Map of all links mapped to the object node
   */
  protected final synchronized ConcurrentMap<Long, Link> getLinks(final Long nodeID, final String nodeType) {
    ConcurrentMap<Long, Link> nodeLinksMap = this.linksMap.get(nodeID);
    if (nodeLinksMap == null) {
      nodeLinksMap = getDataProvider().getDataLoader().fetchNodeLinks(nodeID, nodeType);
      this.linksMap.put(nodeID, nodeLinksMap);
    }
    return nodeLinksMap;
  }


  /**
   * Gets the set of links for a given node id
   *
   * @param node AbstractDataObject
   * @return the collection of links of a given apic object id
   */
  protected final SortedSet<Link> getLinks(final AbstractDataObject node) {
    return new TreeSet<Link>(getLinks(node.getID(), node.getEntityType().getEntityTypeString()).values());
  }

  /**
   * returns unused variant names in a project
   *
   * @param pidcVers PIDCard version
   * @return list of Attribute values
   */
  protected final List<AttributeValue> getUnusedVarForPIDC(final PIDCVersion pidcVers) {
    List<AttributeValue> attrValues = getVarNameAttribute().getAttrValues(false);
    SortedSet<PIDCVariant> variantsSet = pidcVers.getVariantsSet();
    for (PIDCVariant variant : variantsSet) {
      // remove the variant names used in the project
      attrValues.remove(variant.getNameValue());
    }
    return attrValues;
  }

  /**
   * returns unused sub-variant names in a project
   *
   * @param pidcVar PIDCVariant //iCDM-1099
   * @return list of Attribute values
   */
  protected final List<AttributeValue> getUnusedSubVariantNames(final PIDCVariant pidcVar) {
    List<AttributeValue> attrValues = getSubvarNameAttribute().getAttrValues(false);
    // iCDM-1099
    for (PIDCSubVariant subVar : pidcVar.getSubVariantsSet()) {
      // remove the sub variant names in the project
      attrValues.remove(subVar.getNameValue());
    }
    return attrValues;
  }

  /**
   * Get the variants for the SDOM pVer name <br>
   * iCDM-773
   *
   * @param sdomPverName SDOM pver name
   * @return SDOMPverInfo
   */
  protected final synchronized Set<String> getPVERVariants(final String sdomPverName) {
    Set<String> variantsSet = this.sdomVariantsMap.get(sdomPverName);
    // ICDM-2511
    if ((variantsSet == null) || variantsSet.isEmpty()) {
      variantsSet = getDataProvider().getDataLoader().getPVERVariants(sdomPverName);
      this.sdomVariantsMap.put(sdomPverName, variantsSet);
    }
    return variantsSet;
  }

  // ICDM-1456
  /**
   * Get the variants for the SDOM pVer name from MvTa2lFileinfo <br>
   *
   * @param sdomPverName SDOM pver name
   * @return SDOMPverInfo
   */
  protected final synchronized Set<String> getTA2LFilePVERVariants(final String sdomPverName) {
    Set<String> variantsSet = this.sdomTA2LVariantsMap.get(sdomPverName);
    if (variantsSet == null) {
      variantsSet = getDataProvider().getDataLoader().getTA2LFilePVERVars(sdomPverName);
      this.sdomTA2LVariantsMap.put(sdomPverName, variantsSet);
    }
    return variantsSet;
  }

  /**
   * @param dataObj AbstractDataObject
   * @return true if the object is loaded in cache
   */
  protected boolean isObjLoaded(final AbstractDataObject dataObj) {
    return this.objLoadedMap.contains(dataObj);
  }


  /**
   * @param dataObj AbstractDataObject
   */
  protected void setObjLoaded(final AbstractDataObject dataObj) {
    this.objLoadedMap.add(dataObj);
  }

  /**
   * @return the Attr Characteristics Map
   */
  public Map<Long, AttributeCharacteristic> getAllCharMap() {

    return this.attrCharMap;
  }

  /**
   * @return the Char Val Map
   */
  public Map<Long, AttributeCharacteristicValue> getAllCharValMap() {
    return this.attrValCharMap;
  }

  /**
   * @return the psts
   */
  public Map<A2LFile, VCDMPST> getVCDMPstMap() {
    return this.vCDMPSTMap;
  }

  /**
   * @return all the alias definitions from the db. Must not be used outside.So default scope
   */
  Map<Long, AliasDefinition> getAliasDefMap() {
    return this.aliasDefMap;
  }

  /**
   * ICDM-1027
   *
   * @return the current user's favourite uc items
   */
  public Map<Long, FavUseCaseItem> getCurrentUserUCFavMap() {
    return this.currentUserUCFavMap;
  }

  /**
   * ICDM-1027
   *
   * @param projectId PIDC id
   * @return pidc associated favourite usecase items
   */
  public ConcurrentMap<Long, FavUseCaseItem> getPidcUCFavMap(final Long projectId) {
    if (CommonUtils.isNull(this.projUCFavMap.get(projectId))) {
      this.projUCFavMap.put(projectId, new ConcurrentHashMap<Long, FavUseCaseItem>());
    }
    return this.projUCFavMap.get(projectId);
  }

  /**
   * ICDM-1027
   *
   * @return the projUCFavMap
   */
  public ConcurrentMap<Long, ConcurrentMap<Long, FavUseCaseItem>> getProjUCFavMap() {
    return this.projUCFavMap;
  }


  /**
   * ICDM-1040 returns virtual favourite nodes belonging to a PIDC
   *
   * @param pidcID ID of pid card
   * @return map of FavUseCaseItemNode
   */
  public ConcurrentMap<Long, FavUseCaseItemNode> getPidcFavUcNodes(final long pidcID) {
    ConcurrentMap<Long, FavUseCaseItemNode> nodeMap = this.projUcFavNodeMap.get(pidcID);

    if (nodeMap == null) {
      nodeMap = new ConcurrentHashMap<>();
      this.projUcFavNodeMap.put(pidcID, nodeMap);
    }

    return nodeMap;
  }

  /**
   * ICDM-1040
   *
   * @return Map of current user's favorite usecase nodes
   */
  public Map<Long, FavUseCaseItemNode> getCurrentFavUcNodes() {
    return this.currentUsrUcFavNodeMap;
  }

  /**
   * ICDM-1040
   *
   * @param pidcID id of pid card
   * @param nodeID id of fav node
   * @return FavUseCaseItemNode
   */
  public FavUseCaseItemNode getPidcFavUcNode(final long pidcID, final long nodeID) {
    return getPidcFavUcNodes(pidcID).get(nodeID);
  }

  /**
   * ICDM-1040
   *
   * @param nodeId id of fav node
   * @return FavUseCaseItemNode
   */
  public FavUseCaseItemNode getUserFavUcNode(final long nodeId) {
    return this.currentUsrUcFavNodeMap.get(nodeId);
  }

  /**
   * ICDM-1040
   *
   * @return the projUcFavRootNode
   */
  public ProjFavUcRootNode getProjUcFavRootNode() {
    return this.projUcFavRootNode;
  }

  /**
   * ICDM-1027
   *
   * @param favUcID the ID
   * @param pidFav true for project usecase favourite
   * @param objId pidc/apic user id
   * @return FavUseCaseItem
   */
  public final synchronized FavUseCaseItem getFavUcItem(final Long favUcID, final boolean pidFav, final Long objId) {
    FavUseCaseItem favUc;
    if (pidFav) {
      favUc = getPidcUCFavMap(objId).get(favUcID);
    }
    else {
      favUc = getCurrentUserUCFavMap().get(favUcID);
    }

    if (favUc == null) {
      favUc = new FavUseCaseItem(this.dataProvider, favUcID);
      if (pidFav) {
        getPidcUCFavMap(objId).put(favUcID, favUc);
      }
      else {
        getCurrentUserUCFavMap().put(favUcID, favUc);
      }

    }
    return favUc;
  }

  /**
   * ICDM-1028
   *
   * @return UserFavUcRootNode
   */
  public UserFavUcRootNode getUserFavUcRootNode() {
    return this.userUcFavRootNode;
  }

  /**
   * @return the feature Values
   */
  protected Map<Long, Feature> getAllFeatures() {
    synchronized (this.featureLock) {
      if (this.featureMap.isEmpty()) {
        this.featureMap.putAll(this.dataProvider.getDataLoader().fetchAllFeatures());
      }
    }
    return this.featureMap;
  }

  /**
   * @return the mapped Attr Set
   */
  protected Set<Attribute> getMappedAttrSet() {
    if (this.mappedAttrSet == null) {
      this.mappedAttrSet = new TreeSet<Attribute>();
      for (Feature feature : getAllFeatures().values()) {
        final Attribute attribute = getAttribute(feature.getAttributeID());
        if (attribute != null) {
          this.mappedAttrSet.add(attribute);
        }
      }

    }
    return this.mappedAttrSet;
  }

  /**
   * Icdm-1066
   *
   * @return the feature map with attrId key Loaded during Start Up.
   */
  public Map<Long, Feature> getAttrFeaMap() {
    return this.attrFeaMap;
  }


  /**
   * @return
   */
  Map<String, String> getMessageMap() {
    return this.messageMap;
  }

  /**
   * @param grpName grpName
   * @param name name
   * @param varArgs varArgs
   * @return the message String for the Group name and name along with Var args
   */
  public String getMessage(final String grpName, final String name, final Object... varArgs) {
    String message = CommonUtils.checkNull(getMessageMap().get(grpName + ApicConstants.CDR_PARAM_DELIMITER + name));
    message = StringEscapeUtils.unescapeJava(message);
    return MessageFormat.format(message, varArgs);
  }

  /**
   * find the node IDs with write access rights for the current user
   *
   * @param nodeType node type. Use <code>IEntityType.getEntityTypeString()</code> to get type node type
   * @return set of node ID
   */
  // ICDM-1366
  public Set<Long> getWritableNodesByType(final String nodeType) {
    // ApicConstants.FUNC_NODE_TYPE
    final Set<Long> nodeSet = new HashSet<>();
    // find the node IDs with write access rights for the current user
    for (NodeAccessRight node : getUserNodeAccRights().values()) {
      if (nodeType.equals(node.getNodeType()) && node.hasWriteAccess()) {
        nodeSet.add(node.getNodeId());
      }

    }
    return nodeSet;
  }

  /**
   * iCDM-498 Get the CDRule for the param name and type, seperated by delimeter as ':'
   *
   * @param paramName parameter name
   * @return CDR Rule
   */
  protected List<CDRRule> getParamCDRRules(final String paramName) {
    return this.cdrRulesMap.get(paramName.toUpperCase(Locale.getDefault()));
  }

  /**
   * Adds the list of CDR rules of the given parameter to the collection
   *
   * @param paramName parameter's name
   * @param ruleList CDR Rule list
   */
  protected void addParamCDRRules(final String paramName, final List<CDRRule> ruleList) {
    this.cdrRulesMap.put(paramName.toUpperCase(Locale.getDefault()), ruleList);
  }

  /**
   * Adds the CDR Rule to the given parameter's rule collection
   *
   * @param paramName parameter's name
   * @param rule CDR Rule
   */
  protected void addParamCDRRule(final String paramName, final CDRRule rule) {
    List<CDRRule> paramRuleList = getParamCDRRules(paramName);
    if (paramRuleList == null) {
      paramRuleList = new ArrayList<>();
      addParamCDRRules(paramName, paramRuleList);
    }
    paramRuleList.add(rule);
  }

  /**
   * Remove the rule from the cache against the parameter
   *
   * @param rule SSD rule
   */
  protected void removeParamCDRRule(final CDRRule rule) {
    List<CDRRule> ruleList = getParamCDRRules(rule.getParameterName());
    if (ruleList != null) {
      ruleList.remove(rule);
    }
  }

  /**
   * Remove the rule from the cache against the parameter
   *
   * @param rsId RuleSet ID
   * @param rule SSD rule
   */
  protected void removeParamRuleSetRule(final Long rsId, final CDRRule rule) {
    List<CDRRule> ruleList = getCDRRulesForRuleSetParam(rsId, rule.getParameterName());
    if (ruleList != null) {
      // remove from list
      ruleList.remove(rule);
    }
  }

  /**
   * Adds the list of CDR rules to the collection. Supports adding multiple rules of multiple parameters
   *
   * @param ruleMap map of rules and their parameter
   */
  protected void addParamCDRRules(final Map<String, List<CDRRule>> ruleMap) {
    for (Entry<String, List<CDRRule>> parmEntry : ruleMap.entrySet()) {
      addParamCDRRules(parmEntry.getKey().toUpperCase(Locale.getDefault()), parmEntry.getValue());
    }
  }

  /**
   * Get cdrRules for the rule
   *
   * @param rsId ruleset id
   * @return CDRRule list
   */
  protected Map<String, List<CDRRule>> getRuleSetCDRRules(final Long rsId) {
    return this.ruleSetRulesMap.get(rsId);
  }

  /**
   * Add list of rules to rule set param
   *
   * @param rsId rule set id
   * @param rulesMap rules map
   */
  protected void addRuleSetCDRRules(final Long rsId, final Map<String, List<CDRRule>> rulesMap) {
    // convert the key(param name) to upper case and cache it
    ConcurrentMap<String, List<CDRRule>> rsRulesMap = new ConcurrentHashMap<String, List<CDRRule>>();
    for (String param : rulesMap.keySet()) {
      String upperParam = param.toUpperCase(Locale.getDefault());
      rsRulesMap.put(upperParam, rulesMap.get(param));
    }
    // add converted map it to cache
    this.ruleSetRulesMap.put(rsId, rsRulesMap);
  }

  /**
   * Get cdrRules for the given RuleSet and param name
   *
   * @param rsId ruleset id
   * @param paramName param name
   * @return CDRRule list
   */
  protected List<CDRRule> getCDRRulesForRuleSetParam(final Long rsId, final String paramName) {
    List<CDRRule> ruleList = new ArrayList<CDRRule>();
    Map<String, List<CDRRule>> rulesMap = this.ruleSetRulesMap.get(rsId);
    if (rulesMap != null) {
      String key = paramName.toUpperCase(Locale.getDefault());
      ruleList = rulesMap.get(key);
    }
    return ruleList;
  }

  /**
   * Add rule to for the RuleSet parameter
   *
   * @param rsId rule set id
   * @param paramName param name
   * @param rule cdrRule
   */
  protected void addRuleSetCDRRules(final Long rsId, final String paramName, final CDRRule rule) {
    // Convert case of param name
    String key = paramName.toUpperCase(Locale.getDefault());
    // Get the rule map for the ruleSet id
    Map<String, List<CDRRule>> rsRulesMap = getRuleSetCDRRules(rsId);
    if (rsRulesMap == null) {
      rsRulesMap = new ConcurrentHashMap<String, List<CDRRule>>();
    }
    List<CDRRule> ruleList = rsRulesMap.get(key);
    if (ruleList == null) {
      ruleList = new ArrayList<CDRRule>();
    }
    ruleList.add(rule);
    rsRulesMap.put(key, ruleList);
    // add to cache
    addRuleSetCDRRules(rsId, rsRulesMap);
  }

  /**
   * @param projectId PIDCard's ID
   * @return PIDCard BO
   */
  public PIDCard getPidc(final long projectId) {
    return this.allPidcMap.get(projectId);
  }

  /**
   * @return map of all project id cards. Key - PIDC ID; value - PIDC business object
   */
  protected Map<Long, PIDCard> getAllPidcMap() {
    return this.allPidcMap;
  }

  /**
   * @return the focusMatrixUCRootNode
   */
  public FocusMatrixUseCaseRootNode getFocusMatrixUCRootNode() {
    return this.focusMatrixUCRootNode;
  }

  /**
   * @param allPIDCAttrs : map of all pidc attrs
   * @return the set of attrs which can be marked as relevant for focus matrix. Visible attrs which are not deleted and
   *         do not belong to the attribute class "doc" and "sthr" are considered. <br>
   *         Key - Attribute ID <br>
   *         Value - Attribute
   */
  protected Map<Long, Attribute> getFocusMatrixApplicableAttrMap(final Map<Long, PIDCAttribute> allPIDCAttrs) {

    List<String> excludeAttrClassesList = getExcludeAttrClassesList();

    ConcurrentMap<Long, Attribute> fMatrixAttrMap = new ConcurrentHashMap<>();

    for (PIDCAttribute projAttr : allPIDCAttrs.values()) {
      if (excludeAttrClassesList.contains(projAttr.getAttribute().getCharStr()) || !projAttr.isVisible() ||
          projAttr.getAttribute().isDeleted()) {
        continue;
      }
      fMatrixAttrMap.put(projAttr.getAttribute().getID(), projAttr.getAttribute());
    }

    return fMatrixAttrMap;
  }

  /**
   * @return the list of attr char classes which are excluded from focus matrix
   */
  protected List<String> getExcludeAttrClassesList() {
    // ICDM-1611
    List<String> excludeAttrClassesList = new ArrayList<String>();

    for (AttributeCharacteristic attrClass : this.attrCharMap.values()) {

      if (CommonUtils.isEqual(attrClass.getFocusMatrixYN(), ApicConstants.CODE_NO)) {
        excludeAttrClassesList.add(attrClass.getNameEng());
      }

    }
    return excludeAttrClassesList;
  }

  /**
   * @return Map of all project versions. Key - Pidc version ID; value - Pidc Version BO
   */
  public Map<Long, PIDCVersion> getAllPidcVersionMap() {
    return this.allPidcVersMap;
  }

  /**
   * @return map of all PIDC A2L mappings
   */
  public Map<Long, PIDCA2l> getAllPidcA2lMap() {
    return this.allPidcA2lMap;
  }


  /**
   * @return the focusMatrixDetailsMap
   */
  protected Map<Long, FocusMatrixDetails> getFocusMatrixDetailsMap() {
    return this.focusMatrixDetailsMap;
  }

  /**
   * @return the mandatoryAttrObjectsMap
   */
  public ConcurrentMap<Long, MandatoryAttr> getMandatoryAttrObjectsMap() {
    return this.mandatoryAttrObjectsMap;
  }

  /**
   * @return the mandatoryAttrsMap
   */
  public ConcurrentMap<Long, Set<Attribute>> getMandatoryAttrsMap() {
    return this.mandatoryAttrsMap;
  }

  /**
   * @param entityID Long
   * @return ApicObject
   */
  public ApicObject getMandatoryAttr(final Long entityID) {
    return this.mandatoryAttrObjectsMap.get(entityID);
  }

  /**
   * @return the mandatoryLvlAttrValId
   */
  public Long getMandatoryLvlAttrValId() {
    if (this.mandatoryLvlAttrValId == null) {
      String lvlAttrID = getParameterValue(ApicConstants.MANDATORY_LEVEL_ATTR);
      this.mandatoryLvlAttrValId = Long.parseLong(lvlAttrID);
    }
    return this.mandatoryLvlAttrValId;
  }

  /**
   * @return the sorted alias defintions
   */
  public SortedSet<AliasDefinition> getSortedAliasDef() {
    this.sortedAliasDef.clear();
    this.sortedAliasDef.addAll(this.aliasDefMap.values());
    return this.sortedAliasDef;
  }


  /**
   * @return the pidcverSionLoader
   */
  public PIDCVersionsExclusionHandler getPidcVrsnExclusnHandler() {
    return this.pidcverSionExclusnHandler;
  }

  /**
   * @return the web service system map.
   */
  public Map<String, String> getWSSystemMap() {
    return this.wsSystemMap;
  }

  /**
   * @param cdrRules cdrRules list to be removed
   */
  protected void removeParamCDRRules(final List<CDRRule> cdrRules) {
    for (CDRRule rule : cdrRules) {
      List<CDRRule> ruleList = getParamCDRRules(rule.getParameterName());
      if (ruleList != null) {
        ruleList.remove(rule);
      }
    }
  }

  /**
   * Remove the rule from the cache against the parameter
   *
   * @param rsId RuleSet ID
   * @param cdrRules cdrRules list to be removed
   */
  protected void removeParamRuleSetRules(final Long rsId, final List<CDRRule> cdrRules) {
    List<CDRRule> cdrRulesCopy = new ArrayList<>();
    cdrRulesCopy.addAll(cdrRules);
    for (CDRRule rule : cdrRulesCopy) {
      List<CDRRule> ruleList = getCDRRulesForRuleSetParam(rsId, rule.getParameterName());
      if (ruleList != null) {
        // remove from list
        ruleList.remove(rule);
      }
    }
  }

  // ICDM-2354
  /**
   * @return the unlockedPidsInSession
   */
  public Set<PIDCard> getUnlockedPidsInSession() {
    return this.unlockedPidsInSession;
  }

  // ICDM-2354
  /**
   * @param pidc PIDCard
   * @return the isUnlockedInSession
   */
  public boolean isUnlockedInSession(final PIDCard pidc) {
    return getUnlockedPidsInSession().contains(pidc);
  }


  /**
   * @return the PredefinedAttrValue Map
   */
  public Map<Long, PredefinedAttrValue> getPredAttrValMap() {
    return this.preDefndAttrValMap;
  }

  /**
   * @return the PredefinedValidity Map
   */
  public Map<Long, PredefinedAttrValuesValidity> getPredAttrValValidityMap() {
    return this.preDefndValdtyMap;
  }


  /**
   * @param aliasDetailID
   * @return the aliasDetailMap
   */
  public AliasDetail getAliasDetail(final Long aliasDetailID) {
    return this.aliasDetailMap.get(aliasDetailID);
  }

  /**
   * @return
   */
  public Map<Long, AliasDetail> getAliasDetailMap() {
    return this.aliasDetailMap;
  }

  // Story 221802
  /**
   * @return the monicaAuditor
   */
  public ApicUser getMonicaAuditor() {
    return this.monicaAuditor;
  }

  /**
   * @param monicaAuditor the monicaAuditor to set
   */
  public void setMonicaAuditor(final ApicUser monicaAuditor) {
    this.monicaAuditor = monicaAuditor;
  }


  /**
   * @return the linkMap
   */
  public ConcurrentMap<Long, Link> getLinkMap() {
    return this.linkMap;
  }


}