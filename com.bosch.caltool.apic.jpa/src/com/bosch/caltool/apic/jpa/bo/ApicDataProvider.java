/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo; // NOPMD by bne4cob on 10/2/13 9:38 AM This is a wrapper class

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.caltool.apic.jpa.PidcFavourite;
import com.bosch.caltool.dmframework.bo.AbstractDataObject;
import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryHandler;
import com.bosch.caltool.icdm.bo.cdr.SSDServiceHandler;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.ssd.icdm.model.CDRRule;


/**
 * ApicDataProvider contains all entity and business objects loaded at the start of the application
 *
 * @author hef2fe
 * @version 1.0
 * @created 08-Feb-2013 14:03:33
 */
public class ApicDataProvider extends AbstractDataProvider {


  /**
   * Base text for all parameter keys in this module
   */
  private static final String P_APIC_BASE = "com.bosch.caltool.apic.";

  /**
   * Parameter key to enable loading of users during startup
   */
  public static final String P_LOAD_USRS_START = P_APIC_BASE + "loadusers";

  /**
   * Parameter key to enable loading of current user alone during startup
   */
  public static final String P_LOAD_CUR_USER_ONLY_START = P_APIC_BASE + "loadCurrentUserOnly";
  /**
   * Parameter key to enable loading of attributes during startup
   */
  public static final String P_LOAD_ATTR_START = P_APIC_BASE + "loadattrs";

  /**
   * Parameter key to enable loading of level attributes alone during startup.
   */
  public static final String P_LOAD_LVL_ATTR_ONLY_START = P_APIC_BASE + "loadlvlattrsonly";

  /**
   * Parameter key to enable loading of PIDC structure during startup
   */
  public static final String P_LOAD_PSTR_START = P_APIC_BASE + "loadpstructure";

  /**
   * Parameter key to enable loading of PIDC during startup
   */
  public static final String P_LOAD_PIDC_START = P_APIC_BASE + "loadpidc";

  /**
   * Parameter key to enable loading of common parameters during startup
   */
  public static final String P_LOAD_CPRM_START = P_APIC_BASE + "loadcommonparams";

  /**
   * Parameter key to enable loading of use cases during startup
   */
  public static final String P_LOAD_UC_START = P_APIC_BASE + "loaduc";

  /**
   * Parameter key to enable loading of user node access during startup
   */
  public static final String P_LOAD_UNDE_START = P_APIC_BASE + "loadusernodes";

  /**
   * Parameter key to enable loading of iCDM mail templates during startup
   */
  public static final String P_LOAD_MTPL_START = P_APIC_BASE + "loadmailtemplate";

  /**
   * Parameter key to enable loading of top level entities during startup
   */
  public static final String P_LOAD_TPNT_START = P_APIC_BASE + "loadtopentities";

  /**
   * Icdm-954 Parameter key to enable loading of Characteristics
   */
  public static final String P_LOAD_CHAR_START = P_APIC_BASE + "loadCharacteristics";

  /**
   * Icdm-954 Parameter key to enable loading of Characteristics
   */
  public static final String P_LOAD_VAL_START = P_APIC_BASE + "loadCharacteristicValues";
  /**
   * Icdm-1043 key to enable loading tip of the day file
   */
  public static final String P_LOAD_TIP_OF_DAY_START = P_APIC_BASE + "loadTipOfTheDay";
  /**
   * key to enable loading messages
   */
  public static final String P_LOAD_MESSAGES_START = P_APIC_BASE + "loadMessages";


  /**
   * Logger name for parser logger
   */
  public static final String LOGGR_PARSER_NAME = "com.bosch.caltool.icdm.logger.ParserLogger";

  /**
   * Logger name for SSDLogger
   */
  public static final String LOGGR_SSDLOGGR_NAME = "com.bosch.caltool.icdm.logger.SSDLogger";

  /**
   * Data loader
   */
  private final DataLoader dataLoader;

  /**
   * Data cache
   */
  private final DataCache dataCache;

  /**
   * Entity provider
   */
  private final EntityProvider entityProvider;

  /**
   * SSD Service handler
   */
  private final SSDServiceHandler ssdServiceHandler = null;

  /**
   * Rule Cache to store rules
   */
  private final RuleCache ruleCache = new RuleCache();

  /**
   * This constructor is called when entity manager is to be created internally. The object store is also initialised.
   *
   * @param dmLogger logger
   * @param dbUsername db Username
   * @param dbPassword db Password
   * @param appUsername app Username
   */
  public ApicDataProvider(final ILoggerAdapter dmLogger, final String dbUsername, final String dbPassword,
      final String appUsername) {

    super();

    Properties dmProps = new Properties();
    dmProps.setProperty(ObjectStore.P_USER_NAME, dbUsername);
    dmProps.setProperty(ObjectStore.P_CQN_MODE, String.valueOf(ObjectStore.CQN_MODE_NO));

    ObjectStore.getInstance().initialise(dmLogger, dmLogger, dmProps);

    this.entityProvider = new EntityProvider(dmLogger, dbUsername, dbPassword, EntityType.values());
    this.dataCache = new DataCache(this, Language.ENGLISH);

    this.dataLoader = new DataLoader(this);
    this.dataLoader.fetchStartupData();

    final String nodeId = getParameterValue(ApicConstants.SSD_NODE_ID);
    final String cmpPkgNodeId = getParameterValue(ApicConstants.SSD_COMP_PKG_NODE_ID);
    final String cmpliParamNodeId = getParameterValue(ApicConstants.SSD_COMPLI_PARAM_NODE_ID);
    // this.ssdServiceHandler = new SSDServiceHandler(appUsername, getLogger(), getLogger(), new BigDecimal(nodeId),
    // new BigDecimal(cmpPkgNodeId), this, new BigDecimal(cmpliParamNodeId));
  }

  /**
   * This constructor is called when entity manager provided from outside. Language is set to English. The object store
   * is also initialised.
   *
   * @param dmLogger logger
   * @param emf entity manager Factory
   * @param appUsername app Username
   */
  public ApicDataProvider(final ILoggerAdapter dmLogger, final EntityManagerFactory emf, final String appUsername) {

    super();

    Properties dmProps = new Properties();
    dmProps.setProperty(ObjectStore.P_USER_NAME, appUsername);
    dmProps.setProperty(ObjectStore.P_CQN_MODE, String.valueOf(ObjectStore.CQN_MODE_NO));

    ObjectStore.getInstance().initialise(dmLogger, dmLogger, dmProps);

    this.entityProvider = new EntityProvider(getLogger(), emf, EntityType.values());
    this.dataCache = new DataCache(this, Language.ENGLISH);

    this.dataLoader = new DataLoader(this);
    this.dataLoader.fetchStartupData();

    final String nodeId = getParameterValue(ApicConstants.SSD_NODE_ID);
    final String cmpPkgNodeId = getParameterValue(ApicConstants.SSD_COMP_PKG_NODE_ID);
    final String cmpliParamNodeId = getParameterValue(ApicConstants.SSD_COMPLI_PARAM_NODE_ID);
    // this.ssdServiceHandler = new SSDServiceHandler(appUsername, getLogger(), getLogger(), new BigDecimal(nodeId),
    // new BigDecimal(cmpPkgNodeId), this, new BigDecimal(cmpliParamNodeId));


  }

  /**
   * This constructor is called when entity manager provided from outside. Language is set to English. The object store
   * is also initialised.
   *
   * @param dmLogger logger
   * @param emf entity manager Factory
   * @param dmProps data model properties
   */
  public ApicDataProvider(final ILoggerAdapter dmLogger, final EntityManagerFactory emf, final Properties dmProps) {

    super();

    ObjectStore.getInstance().initialise(dmLogger, dmLogger, dmProps);

    this.entityProvider = new EntityProvider(getLogger(), emf, EntityType.values());
    this.dataCache = new DataCache(this, Language.ENGLISH);

    this.dataLoader = new DataLoader(this);
    this.dataLoader.fetchStartupData();

    final String nodeId = getParameterValue(ApicConstants.SSD_NODE_ID);
    final String cmpPkgNodeId = getParameterValue(ApicConstants.SSD_COMP_PKG_NODE_ID);
    final String cmpliParamNodeId = getParameterValue(ApicConstants.SSD_COMPLI_PARAM_NODE_ID);
    // this.ssdServiceHandler = new SSDServiceHandler(ObjectStore.getInstance().getAppUserName(), getLogger(),
    // getLogger(),
    // new BigDecimal(nodeId), new BigDecimal(cmpPkgNodeId), this, new BigDecimal(cmpliParamNodeId));

  }

  /**
   * This constructor is called when entity manager and laguage are provided from outside.
   *
   * @param loggerMap map of loggers to be used. Key - name of logger. Value - Logger
   * @param emf entity manager Factory
   * @param appUsername app Username
   * @param langSelected language Selected
   */
  public ApicDataProvider(final Map<String, ILoggerAdapter> loggerMap, final EntityManagerFactory emf,
      final String appUsername, final Language langSelected) {

    super();

    this.entityProvider = new EntityProvider(getLogger(), emf, EntityType.values());
    this.dataCache = new DataCache(this, langSelected);

    this.dataLoader = new DataLoader(this);
    this.dataLoader.fetchStartupData();

    final String nodeId = getParameterValue(ApicConstants.SSD_NODE_ID);
    final String cmpPkgNodeId = getParameterValue(ApicConstants.SSD_COMP_PKG_NODE_ID);
    final String cmpliParamNodeId = getParameterValue(ApicConstants.SSD_COMPLI_PARAM_NODE_ID);

    ILoggerAdapter parserLogger = CommonUtils.checkNull(loggerMap.get(LOGGR_PARSER_NAME), getLogger());
    ILoggerAdapter ssdLogger = CommonUtils.checkNull(loggerMap.get(LOGGR_SSDLOGGR_NAME), getLogger());

    // this.ssdServiceHandler = new SSDServiceHandler(appUsername, ssdLogger, parserLogger, new BigDecimal(nodeId),
    // new BigDecimal(cmpPkgNodeId), this, new BigDecimal(cmpliParamNodeId));

  }

  /**
   * Returns the value of the parameter identified by name
   *
   * @param name parameter name
   * @return the parameter value
   */
  public final String getParameterValue(final String name) {
    return this.dataCache.getParameterValue(name);
  }

  /**
   * Returns the value of the divisions for which questionnaire can be set
   *
   * @param name parameter name
   * @return the parameter value
   */
  public final String getDivisionsForQnaires(final String name) {
    return this.dataCache.getDivisionsForQnaires(name);
  }

  /**
   * @return the application user name. This is the login user's NT ID
   */
  public String getAppUsername() {
    return this.dataCache.getAppUsername();
  }

  /**
   * @return current language
   */
  public Language getLanguage() {
    return this.dataCache.getLanguage();
  }

  /**
   * Set the current language
   *
   * @param currentLanguage current language
   */
  public void setLanguage(final Language currentLanguage) {
    this.dataCache.setLanguage(currentLanguage);
  }

  /**
   * @return AttrRootNode
   */
  public AttrRootNode getAttrRootNode() {
    return this.dataCache.getAttrRootNode();
  }

  /**
   * @return PidcRootNode
   */
  public PIDCNode getPidcRootNode() {
    return this.dataCache.getPidcRootNode();
  }

  /**
   * @param pidcID PIDC's ID
   * @return the PIDC business object
   */
  public PIDCard getPidc(final Long pidcID) {
    return this.dataCache.getPidc(pidcID);
  }

  /**
   * Retrieve the PIDCard with the name defined by the given attribute value
   *
   * @param pidcName PIDC name attribute value
   * @return PIDCard
   */
  public PIDCard getPidc(final AttributeValue pidcName) {
    return this.dataCache.getPidCard(pidcName);
  }

  /**
   * Return PIDCard version with the given version ID
   *
   * @param pidcVersID version ID
   * @return PIDCVersion
   */
  public PIDCVersion getPidcVersion(final Long pidcVersID) {
    return this.dataCache.getPidcVersion(pidcVersID);
  }

  /**
   * Find the PIDC Version with the given ID. If object does not exist, it is retrieved from database.
   *
   * @param pidcVersID version ID
   * @param loadFromDB if true, version is verified with DB
   * @return PIDC Version object
   * @throws DataException if pidcVersID is invalid
   */
  public PIDCVersion getPidcVersion(final Long pidcVersID, final boolean loadFromDB) throws DataException {
    return getDataCache().getPidcVersion(pidcVersID, loadFromDB);
  }

  /**
   * @return all attributes
   */
  public Map<Long, Attribute> getAllAttributes() {
    return this.dataCache.getAllAttributes();
  }

  /**
   * Returns all attributes in sorted order
   *
   * @return SortedSet<Attribute>
   */
  public SortedSet<Attribute> getSortedAttributes() {
    return this.dataCache.getSortedAttributes();
  }

  /**
   * @return the sorted set of alias definitions.
   */
  public SortedSet<AliasDefinition> getSortedAliasDef() {
    if (this.dataCache.getSortedAliasDef().isEmpty()) {
      loadAliasDefinitions();
    }
    return this.dataCache.getSortedAliasDef();
  }

  /**
   * Returns all attributes in sorted order
   *
   * @param includeDeleted include deleted attributes or not
   * @return SortedSet<Attribute>
   */
  public final SortedSet<Attribute> getSortedAttributes(final boolean includeDeleted) {
    return this.dataCache.getSortedAttributes(includeDeleted);
  }

  /**
   * @return structure attributes for PIDC
   */
  public Map<Long, Attribute> getPidcStructAttrMap() {
    return this.dataCache.getPidcStructAttrMap();
  }

  /**
   * @return all groups
   */
  public Map<Long, AttrGroup> getAllGroups() {
    return this.dataCache.getAllGroups();
  }

  /**
   * @return all super groups
   */
  public Map<Long, AttrSuperGroup> getAllSuperGroups() {
    return this.dataCache.getAllSuperGroups();
  }

  /**
   * @return all APIC users
   */
  @Deprecated
  public SortedSet<ApicUser> getAllApicUsers() {
    return this.dataCache.getAllApicUsers();
  }

  /**
   * @return all Active PIDC Versions
   */
  public Map<Long, PIDCVersion> getAllActivePIDCVersions() {
    return this.dataCache.getAllActivePIDCVersions();
  }


  /**
   * @return all PIDCs
   */
  public List<PIDCVersion> getAllPIDCVersions() {
    return this.dataLoader.getAllPIDCVersions();
  }

  /**
   * @param attributeID attribute ID
   * @return attribute business object
   */
  public Attribute getAttribute(final Long attributeID) {
    return this.dataCache.getAttribute(attributeID);
  }

  /**
   * @param groupID group ID
   * @return group business object
   */
  public AttrGroup getGroup(final Long groupID) {
    return this.dataCache.getGroup(groupID);
  }

  /**
   * @param superGroupID super Group ID
   * @return Super Group business object
   */
  public AttrSuperGroup getSuperGroup(final Long superGroupID) {
    return this.dataCache.getSuperGroup(superGroupID);
  }

  /**
   * @param dependencyID Attr dependency ID
   * @return Attr dependency business object
   */
  public AttrDependency getAttrDependency(final Long dependencyID) {
    return this.dataCache.getAttrDependency(dependencyID);
  }

  /**
   * @param dependencyID AttrValueDependency ID
   * @return AttrValueDependency business object
   */
  public AttrValueDependency getValueDependency(final Long dependencyID) {
    return this.dataCache.getValueDependency(dependencyID);
  }

  /**
   * @param valueID Attribute Value ID
   * @return Attribute Value business object
   */
  public AttributeValue getAttrValue(final Long valueID) {
    return this.dataCache.getAttrValue(valueID);
  }

  /**
   * @param userName user Name
   * @return group business object
   */
  @Deprecated
  public ApicUser getApicUser(final String userName) {
    return this.dataCache.getApicUser(userName);
  }


  /**
   * @return the current user
   */
  @Deprecated
  public ApicUser getCurrentUser() {
    return this.dataCache.getCurrentUser();
  }

  /**
   * @param pidcAttrID PIDC Attribute ID
   * @return PIDC Attribute business object
   */
  public PIDCAttribute getPidcAttribute(final Long pidcAttrID) {
    return this.dataCache.getPidcAttribute(pidcAttrID);
  }

  /**
   * @param pidcVarAttrID PIDC Attribute Var ID
   * @return PIDC Attribute Var business object
   */
  public PIDCAttributeVar getPidcVaraintAttr(final Long pidcVarAttrID) {
    return this.dataCache.getPidcVaraintAttr(pidcVarAttrID);
  }

  /**
   * @param pidcVariantID PIDC Variant ID
   * @return PIDC Variant business object
   */
  public PIDCVariant getPidcVaraint(final Long pidcVariantID) {
    return this.dataCache.getPidcVaraint(pidcVariantID);
  }

  /**
   * Get PIDC Sub Variant for the given sub-variant-id
   *
   * @param pidcSubVariantID Sub Variant ID
   * @return PIDCSubVariant business object
   */
  public PIDCSubVariant getPidcSubVaraint(final Long pidcSubVariantID) {
    return this.dataCache.getPidcSubVaraint(pidcSubVariantID);
  }

  /**
   * Get PIDC Sub Variant Attr for the given sub-variant-attr id
   *
   * @param pidcSubVarAttrID Sub Variant attr ID
   * @return PIDCAttribute Sub Var business object
   */
  public PIDCAttributeSubVar getPidcSubVaraintAttr(final Long pidcSubVarAttrID) {
    return this.dataCache.getPidcSubVaraintAttr(pidcSubVarAttrID);
  }

  /**
   * @return maximum structure level
   */
  public long getPidcStructMaxLevel() {
    return this.dataCache.getPidcStructMaxLevel();
  }

  /**
   * @return PVER attribute
   */
  public Attribute getPVerAttribute() {
    return this.dataCache.getPVerAttribute();
  }

  /**
   * @return vCDM Aprj Attribute
   */
  public Attribute getVCdmAprjAttribute() {
    return this.dataCache.getVCdmAprjAttribute();
  }

  /**
   * @return Variant coding Attribute
   */
  public Attribute getVariantCodingAttribute() {
    return this.dataCache.getVariantCodingAttribute();
  }

  /**
   * @param apicUser user
   * @return user's PIDC favorites
   */
  public SortedSet<PIDCVersion> getUserPidcFavorites(final ApicUser apicUser) {
    return this.dataCache.getUserPidcFavorites(apicUser);
  }

  /**
   * @return all the PIDC favorites
   */
  public SortedSet<PidcFavourite> getAllPidcFavorites() {
    return this.dataLoader.getAllPidcFavorites();
  }

  // icdm-253
  /**
   * @param nodeID nodeID
   * @return access rights of the given node
   */
  @Deprecated
  public SortedSet<NodeAccessRight> getNodeAccessRights(final Long nodeID) {
    return this.dataLoader.getNodeAccessRights(nodeID);
  }

  /**
   * Get the Node access right for the node access id
   *
   * @param nodeAccessID access id
   * @return NodeAccessRight for the node access id
   */
  @Deprecated
  public NodeAccessRight getNodeAccRight(final Long nodeAccessID) {
    return this.dataCache.getNodeAccRights(nodeAccessID);
  }


  /**
   * @return the Pidc Name Attribute
   */
  public Attribute getProjNameAttribute() {
    return this.dataCache.getProjNameAttribute();
  }


  /**
   * @return the Variant Name Attribute
   */
  public Attribute getVarNameAttribute() {
    return this.dataCache.getVarNameAttribute();
  }


  /**
   * @return the Sub-Variant Name Attribute
   */
  public Attribute getSubvarNameAttribute() {
    return this.dataCache.getSubvarNameAttribute();
  }

  /**
   * @return the list of unused PIDC name attribute values
   */
  public List<AttributeValue> getUnusedPidcNames() {
    return this.dataCache.getUnusedPidcNames();
  }

  /**
   * Returns the use case group identified by the ID
   *
   * @param ucgID Group ID
   * @return the use case group
   */
  public UseCaseGroup getUseCaseGroup(final Long ucgID) {
    return this.dataCache.getUseCaseGroup(ucgID);
  }

  /**
   * Returns the use case identified by the ID
   *
   * @param ucID use case ID
   * @return the use case
   */
  public UseCase getUseCase(final Long ucID) {
    return this.dataCache.getUseCase(ucID);
  }

  /**
   * Returns the use case section identified by the ID
   *
   * @param ucsID section ID
   * @return the use case section
   */
  public UseCaseSection getUseCaseSection(final Long ucsID) {
    return this.dataCache.getUseCaseSection(ucsID);
  }

  /**
   * @return the top Level UCG Set
   */
  public Set<UseCaseGroup> getTopLevelUCGSet() {
    return this.dataCache.getTopLevelUCGSet();
  }

  /**
   * @param pdsId PIDDetStruct id
   * @return the PIDCDetStructure bo
   */
  public PIDCDetStructure getPidcDetStructure(final Long pdsId) {
    return this.dataCache.getPidcDetStructure(pdsId);
  }


  /**
   * @return the dataLoader
   */
  @Override
  protected DataLoader getDataLoader() {
    return this.dataLoader;
  }

  /**
   * @return the dataCache
   */
  @Override
  protected DataCache getDataCache() {
    return this.dataCache;
  }

  /**
   * @return the entityProvider
   */
  @Override
  protected EntityProvider getEntityProvider() {
    return this.entityProvider;
  }

  /**
   * @return the logger
   */
  @Override
  protected final ILoggerAdapter getLogger() {
    return super.getLogger();
  }

  /**
   * {@inheritDoc}
   *
   * @return
   */
  public TransactionSummaryHandler getSumHandler() {
    return getSummaryHandler();
  }

  /**
   * Gets the set of APIC users excluding the users passed as input.
   *
   * @param accessRights accessRights the set of users to exclude (node access objects)
   * @return the collection of APIC users
   */
  public SortedSet<ApicUser> getValidApicUsers(final SortedSet<NodeAccessRight> accessRights) {
    return this.dataCache.getValidApicUsers(accessRights);
  }

  /**
   * @return the use Case Root Node
   */
  public UseCaseRootNodeOld getUseCaseRootNode() {
    return this.dataCache.getUseCaseRootNode();
  }


  /**
   * @return the map of Systems with key as System token
   */
  public Map<String, String> getWSSystemMap() {
    if (CommonUtils.isNullOrEmpty(this.dataCache.getWSSystemMap())) {
      this.dataLoader.fetchWSSystems();
    }
    return this.dataCache.getWSSystemMap();
  }

  /**
   * @return the use Case Root Node for focus matrix tree
   */
  public FocusMatrixUseCaseRootNode getFocusMatrixUseCaseRootNode() {
    return this.dataCache.getFocusMatrixUCRootNode();
  }

  /**
   * Add new Changed Data to the cache. Access to this method is synchronized
   *
   * @param changedData the changed data
   */
  public void addChangedData(final Map<Long, ChangedData> changedData) {
    ObjectStore.getInstance().getChangeNotificationCache().addChangedData(changedData);
  }

  /**
   * Get the ICDM file object for the given file ID and node ID
   *
   * @param nodeID node ID
   * @param fileID file ID
   * @return the ICDM file object
   */
  public final IcdmFile getIcdmFile(final Long nodeID, final Long fileID) {
    return this.dataCache.getIcdmFile(nodeID, fileID);
  }

  /**
   * Fetch the Icdm files added to the given node ID and type
   *
   * @param nodeID node ID
   * @param nodeType type of node
   * @return the map of ICDM files
   */
  public Map<Long, IcdmFile> getIcdmFiles(final Long nodeID, final IcdmFile.NodeType nodeType) {
    return this.dataLoader.fetchIcdmFiles(nodeID, nodeType);
  }


  /**
   * Icdm-513
   *
   * @return the Units
   */
  public SortedSet<Unit> getUnits() {
    return this.dataCache.getUnitSet();
  }


  /**
   * ICDM-959 Checks whether the node has link or not
   *
   * @param node attr/grp/supergrp/uc/ucs object
   * @return true if node has links
   */
  @Deprecated
  public boolean hasLink(final AbstractDataObject node) {
    return this.dataCache.hasLink(node);
  }


  /**
   * ICDM-959
   *
   * @return nodeIds with links
   */
  @Deprecated
  public Map<String, Set<Long>> getLinkNodeIds() {
    return this.dataCache.getLinkNodeIds();
  }


  /**
   * Get Features
   *
   * @param featureIdList list of ids
   * @return Map having key feature id and feature obj as value
   */

  public Map<Long, Feature> getFeatures(final List<Long> featureIdList) {
    return this.dataLoader.fetchFeatures(featureIdList);
  }


  /**
   * @param a2lFileId vCDM a2l file id
   * @return A2LFile object
   */
  public A2LFile getA2LInfo(final Long a2lFileId) {
    return this.dataLoader.getA2LInfo(a2lFileId);
  }

  @Deprecated
  /**
   * Returns the DSTS using this vcdmA2lFileId
   *
   * @param vcdmA2lFileId vCDM a2l file id
   * @return VCDMApplicationProjects
   */
  public List<VCDMApplicationProject> getDataSets(final Long vcdmA2lFileId) {
    return this.dataLoader.getDataSets(vcdmA2lFileId);
  }

  /**
   * Returns the DSTS using this vcdmA2lFileId
   * @param sdomPverName pver name
   * @param sdomPverVar pver variant name
   * @param sdomPverRevision pver variant revision
   *
   * @return VCDMApplicationProjects
   */
  public List<VCDMApplicationProject> getDataSets(final String sdomPverName, final String sdomPverVar,
      final String sdomPverRevision) {
    return this.dataLoader.getDataSets(sdomPverName, sdomPverVar, sdomPverRevision);
  }

  /**
   * Get PIDCChange History
   *
   * @param fromVersion :version
   * @param pidcVersId : pidcVersId
   * @param pidcId : pidcId
   * @return SortedSet of PidcChangeHistory
   */
  // ICDM-672
  public SortedSet<PidcChangeHistory> fetchPIDCChangeHistory(final int fromVersion, final long pidcVersId,
      final long pidcId, final boolean fromVersionWeb, final Set<Long> attrID) {
    return this.dataLoader.fetchPIDCChangeHistory(fromVersion, pidcVersId, pidcId, fromVersionWeb, attrID);
  }

  /**
   * Get the A2L file contents, cached against the A2L file ID.
   *
   * @return map of A2LFileInfo
   */
  // iCDM-514
  public Map<Long, A2LFileInfo> getA2lFileContentsMap() {
    return this.dataCache.getA2lFileContentsMap();
  }


  /**
   * Get the a2l files which are mapped
   *
   * @return
   */
  public final Map<Long, A2LFile> getMappedA2LFilesMap() {
    return this.dataCache.getMappedA2LFilesMap();
  }


  /**
   * @param pidcA2lFileID PIDC A2l FileID in ICDM
   * @param a2lFileId A2lFileID in VCDM (from view)
   * @return A2LFile TODO: Refactor such that a2lFileID can be retrieved from pidcA2lFileID
   */
  // iCDM-519
  public final A2LFile getA2lFile(final Long pidcA2lFileID, final Long a2lFileId) {
    return getDataCache().getMappedA2LFile(pidcA2lFileID, a2lFileId);
  }

  /**
   * @return the ssdServiceHandler
   */
  public SSDServiceHandler getSsdServiceHandler() {
    return this.ssdServiceHandler;
  }

  /**
   * get the pidcards with aprj attribute having selAPRJName
   *
   * @param selAPRJName selc aprj name
   * @return pid cards
   */
  public Set<PIDCVersion> getAprjPIDCs(final String selAPRJName) {
    return this.dataLoader.getAprjPIDCs(selAPRJName);
  }

  /**
   * icdm-740 returns whether the level attributes values are used or not
   *
   * @param selectedVals selected level attr values
   * @return true/false
   */
  public boolean getAttrValState(final List<AttributeValue> selectedVals) {
    return this.dataLoader.getAttrValState(selectedVals);
  }


  /**
   * iCDM-756 <br>
   * Gets the SDOM PVer object for the sdom name and variant
   *
   * @param sdomPverName sdom name
   * @param variant variant name
   * @return SDOMPver set
   */
  public SortedSet<Long> getPVERVarRevisions(final String sdomPverName, final String variant) {
    return this.dataLoader.getPVERVarRevisions(sdomPverName, variant);
  }

  // ICDM-1456
  /**
   * Checks whether the sdom pver name exists in mvsdompver
   *
   * @param sdomPverName pver name
   * @return true is pvername exists in the table
   */
  public boolean checkPverName(final String sdomPverName) {
    return this.dataLoader.checkPverNameExists(sdomPverName);
  }

  /**
   * iCDM-756 <br>
   * Fetches PVer variants for the SDOM PVer name
   *
   * @param sdomPverName sdom name
   * @return SDOMPver object
   */
  public Set<String> getPVERVariants(final String sdomPverName) {
    return this.dataCache.getPVERVariants(sdomPverName);
  }

  // ICDM-1456
  /**
   * Fetches PVer variants for the SDOM PVer name from MvTA2lFileInfo
   *
   * @param sdomPverName sdom name
   * @return SDOMPver object
   */
  public Set<String> getTA2LFilePVERVariants(final String sdomPverName) {
    return this.dataCache.getTA2LFilePVERVariants(sdomPverName);
  }

  /**
   * ICDM-763 returns the Link for given node
   *
   * @param abstractDataObj node
   * @return Set of links
   */
  @Deprecated
  public final SortedSet<Link> getLinks(final AbstractDataObject abstractDataObj) {
    return this.dataCache.getLinks(abstractDataObj);
  }

  /**
   * returns unused variant names in a project
   *
   * @param pidcVers PIDCard version
   * @return list of Attribute values
   */
  public final List<AttributeValue> getUnusedVarForPIDC(final PIDCVersion pidcVers) {
    return this.dataCache.getUnusedVarForPIDC(pidcVers);
  }

  /**
   * returns unused sub-variant names in a project
   *
   * @param pidcVar PIDCVariant //iCDM-1099
   * @return list of Attribute values
   */
  public final List<AttributeValue> getUnusedSubVariantNames(final PIDCVariant pidcVar) {
    return this.dataCache.getUnusedSubVariantNames(pidcVar); // iCDM-1099
  }


  /**
   * Get the SDOM PVER VERS NUMM for the SDOM name, variant and revision
   *
   * @param pverName sdom pvaer name
   * @param varName variant name
   * @param varRev variant rev
   * @return PverID
   */
  public Long getPVERId(final String pverName, final String varName, final Long varRev) {
    return this.dataLoader.getPVERId(pverName, varName, varRev);
  }

  /**
   * @param entityID charID
   * @return the Characteristic object
   */
  public AttributeCharacteristic getCharacteristic(final Long entityID) {

    return this.dataCache.getAllCharMap().get(entityID);
  }

  /**
   * @param entityID charValID
   * @return the CharacteristicValue Object
   */
  public AttributeCharacteristicValue getCharacteristicValue(final Long entityID) {

    return this.dataCache.getAllCharValMap().get(entityID);
  }

  /**
   * @param dataObj AbstractDataObject
   * @return true if the object is loaded in cache
   */
  public boolean isObjLoaded(final AbstractDataObject dataObj) {
    return this.dataCache.isObjLoaded(dataObj);
  }


  /**
   * @param dataObj AbstractDataObject
   */
  public void setObjLoaded(final AbstractDataObject dataObj) {
    this.dataCache.setObjLoaded(dataObj);
  }


  /**
   * ICdm-955 get all the Char Map
   *
   * @return all Characteristics
   */
  public SortedSet<AttributeCharacteristic> getAllCharSet() {
    return new TreeSet<AttributeCharacteristic>(this.dataCache.getAllCharMap().values());
  }

  /**
   * Fetch the vCDM psts based on SDOM PVER name, variant and revision
   *
   * @param a2lFile A2LFile
   * @return SortedSet<vCDMPST>
   */
  public VCDMPST fetchVcdmPSTs(final A2LFile a2lFile) {
    VCDMPST vCDMPST = getDataCache().getVCDMPstMap().get(a2lFile);
    if (null == vCDMPST) {
      return this.dataLoader.fetchvCDMPST(a2lFile);
    }
    return vCDMPST;
  }

  /**
   * @param grpName message group
   * @param name message key
   * @param inputValues inputs parameter values
   * @return the ParamAttrObject from the map using the key as ParamAttrID
   */
  public String getMessage(final String grpName, final String name, final Object... inputValues) {
    return this.dataCache.getMessage(grpName, name, inputValues);
  }

  /**
   * ICDM-1040
   *
   * @return the project use Case Root Node
   */
  public ProjFavUcRootNode getProjFavUcRootNode() {
    return this.dataCache.getProjUcFavRootNode();
  }

  /**
   * icdm-1028
   *
   * @return UserFavUcRootNode
   */
  public UserFavUcRootNode getUserFavUcRootNode() {
    return this.dataCache.getUserFavUcRootNode();
  }

  /**
   * @return all the features available in t_SSD features table
   */
  public Map<Long, Feature> getAllFeatures() {
    return this.dataCache.getAllFeatures();
  }

  /**
   * Icdm-1066
   *
   * @return all the features available in t_SSD features table with Attr Id as key
   */
  public Map<Long, Feature> getFeaturesWithAttrKey() {
    return this.dataCache.getAttrFeaMap();
  }


  /**
   * @return the mapped attributes between icdm and SSD.
   */
  public Set<Attribute> getMappedAttrs() {
    return this.dataCache.getMappedAttrSet();
  }


  /**
   * Get the node IDs with write access rights for the current user
   *
   * @param nodeType node type. Use <code>IEntityType.getEntityTypeString()</code> to get type node type
   * @return set of node ID
   */
  // ICDM-1366
  public Set<Long> getWritableNodesByType(final String nodeType) {
    return this.dataCache.getWritableNodesByType(nodeType);
  }

  /**
   * Adds the list of CDR rules to the collection. Supports adding multiple rules of multiple parameters
   *
   * @param ruleMap map of rules and their parameter
   */
  public void addParamCDRRules(final Map<String, List<CDRRule>> ruleMap) {
    this.dataCache.addParamCDRRules(ruleMap);
  }


  /**
   * iCDM-498 Get the CDRule for the param name and type, seperated by delimeter as ':'
   *
   * @param paramName parameter name
   * @return CDR Rule
   */
  public List<CDRRule> getParamCDRRules(final String paramName) {
    return this.dataCache.getParamCDRRules(paramName);
  }


  /**
   * Add rules to rule set
   *
   * @param rsId ruleSet id
   * @param rulesMap rulesMap
   */
  public void addCDRRulesToRuleSet(final Long rsId, final Map<String, List<CDRRule>> rulesMap) {
    getDataCache().addRuleSetCDRRules(rsId, rulesMap);
  }

  /**
   * Get rules for the Rule Set parameter
   *
   * @param rsId ruleSet id
   * @param paramName paramName
   * @return list of rules
   */
  public List<CDRRule> getCDRRulesForRuleSetParam(final Long rsId, final String paramName) {
    return getDataCache().getCDRRulesForRuleSetParam(rsId, paramName);
  }

  /**
   * Get rules for the parameter in the given node parameter
   *
   * @param ssdNode SSD Node
   * @param icdmNode ruleSet id
   * @param paramName paramName
   * @param paramType type of parameter
   * @return list of rules
   */
  public List<CDRRule> getRulesForParam(final Long ssdNode, final long icdmNode, final String paramName,
      final String paramType) {
    return getRuleCache().getRulesForParam(ssdNode, icdmNode, paramName);
  }

  /**
   * @param pidcA2lID Long
   * @return PIDCA2l
   */
  public PIDCA2l getPidcA2l(final Long pidcA2lID) {
    return getDataCache().getPidcA2l(pidcA2lID);
  }

  /**
   * Rule cache, that stores all rules, retrieved to iCDM
   * <p>
   * IMPORTANT : Should NOT be invoked outside the datamodel.
   *
   * @return RuleCache instance
   */
  public final RuleCache getRuleCache() {
    return this.ruleCache;

  }

  /**
   * @param useCaseId Usecaseid
   * @param sectionId Section id
   * @param attrId Attr Id
   * @param pidcVrsnId pidc version id
   * @return unique id of focus matrix table
   */
  public List<FocusMatrixDetails> getFocusMatrix(final Long useCaseId, final Long sectionId, final Long attrId,
      final Long pidcVrsnId) {
    return this.dataLoader.getFocusMatrix(useCaseId, sectionId, attrId, pidcVrsnId);
  }

  /**
   * @param aprjName aprjName
   * @param timePeriod timePeriod
   * @return List of MvTabeDataset
   * @deprecated Use VcdmDataSetLoader instead
   */
  @Deprecated
  public List<VcdmDataSet> getLabelSets(final String aprjName, final int timePeriod) {
    return this.dataLoader.getLabelSets(aprjName, timePeriod);
  }

  /**
   * @param aprjName the aprj-id
   * @param timePeriod the time period
   * @return List of VcdmDatasetsWorkpkgStat
   * @deprecated Use VcdmDataSetWPStatsLoader instead
   */
  // ICDM-2469
  @Deprecated
  public List<VcdmDatasetsWorkpkgStat> getVcdmLabelStatisticsforWp(final String aprjName, final int timePeriod,
      final long easeeDstId) {
    // 221731 - adding easeeDstId parameter in the below call
    return this.dataLoader.getVcdmLabelStatisticsforWp(aprjName, timePeriod, easeeDstId);
  }


  /**
   * Load all attributes, super groups, groups, values, and dependencies from database
   */
  public void loadAllAttrStructure() {
    EntityManager entMgr = ObjectStore.getInstance().getEntityManagerFactory().createEntityManager();
    try {
      this.dataLoader.fetchAllAttributes(entMgr, null);
      // ICDM-1836
      this.dataLoader.fetchMandatoryAttributes(entMgr);
    }
    finally {
      if (entMgr != null) {
        entMgr.close();
      }
    }
  }

  /**
   * Load all apic users to cache
   */
  public void loadAllApicUsers() {
    EntityManager entMgr = ObjectStore.getInstance().getEntityManagerFactory().createEntityManager();
    try {
      this.dataLoader.fetchAllUsers(entMgr);
    }
    finally {
      if (entMgr != null) {
        entMgr.close();
      }
    }
  }

  /**
   * Get apic user by ID
   *
   * @param userId user Id
   * @return ApicUser
   */
  public ApicUser getApicUserById(final Long userId) {
    return this.dataCache.getAllApicUsersMap().get(userId);
  }


  /**
   * load all the alias defintions
   */
  private void loadAliasDefinitions() {
    EntityManager entMgr = ObjectStore.getInstance().getEntityManagerFactory().createEntityManager();
    try {
      this.dataLoader.fetchAliasDefinitions(entMgr);
    }
    finally {
      if (entMgr != null) {
        entMgr.close();
      }
    }
  }

  /**
   * @return the map of all text attributes
   */
  public Map<Long, Attribute> getAllTextAttributes() {
    Map<Long, Attribute> textattributes = new ConcurrentHashMap<Long, Attribute>();
    Map<Long, Attribute> allAttributes = getAllAttributes();
    for (Attribute attr : allAttributes.values()) {
      if (attr.getValueType() == AttributeValueType.TEXT) {
        textattributes.put(attr.getID(), attr);
      }
    }
    return textattributes;
  }

  /**
   * @return the sorted Text Attributes
   */
  public SortedSet<Attribute> getSortedTxtAttrs() {
    return new TreeSet<>(getAllTextAttributes().values());
  }

  /**
   * @param definition definition
   * @return non alias attr map
   */
  public Map<Long, Attribute> getNotAttrAlias(final AliasDefinition definition) {
    Map<Long, Attribute> nonAttrAliasMap = new ConcurrentHashMap<Long, Attribute>();
    Map<Long, AliasDetail> aliasDetAttrMap = definition.getAliasDetAttrMap();
    Map<Long, Attribute> allTextAttributes = getAllTextAttributes();
    for (Attribute textAttr : allTextAttributes.values()) {
      if (!aliasDetAttrMap.containsKey(textAttr.getID())) {
        nonAttrAliasMap.put(textAttr.getID(), textAttr);
      }
    }
    return nonAttrAliasMap;
  }

  /**
   * @param definition definition
   * @return non alias attr map
   */
  public Map<Long, Attribute> getNotValAlias(final AliasDefinition definition) {
    Map<Long, Attribute> nonValAliasMap = new ConcurrentHashMap<Long, Attribute>();
    Map<Long, AliasDetail> aliasDetAttrValMap = definition.getAliasDetAttrValMap();
    Map<Long, Attribute> allTextAttributes = getAllTextAttributes();
    for (Attribute textAttr : allTextAttributes.values()) {
      if (!aliasDetAttrValMap.containsKey(textAttr.getID())) {
        nonValAliasMap.put(textAttr.getID(), textAttr);
      }
    }
    return nonValAliasMap;
  }

  /**
   * Load the use case items' attribute mappings
   */
  // ICDM-1123
  public void loadUseCaseItemAttributeMapping() {
    this.dataLoader.loadUcItemAttrMapping();
  }

  /**
   * @param a2lFileID a2lFileID
   * @param projectID projectID
   * @return the pidc A2l
   */
  public PIDCA2l getPidcA2L(final Long a2lFileID, final Long projectID) {
    return this.dataLoader.getPidcA2L(a2lFileID, projectID);

  }

  /**
   * @returns the list of attr char classes which are excluded from focus matrix
   */
  public List<String> getExcludeAttrClassesList() {
    return this.dataCache.getExcludeAttrClassesList();
  }

  /**
   * @return the delAttrMailDataProvider
   */
  public String getDelAttrMailDataProvider() {
    return this.dataLoader.fetchDelAttrValMailTemplate();
  }


  /**
   * @param session
   */
  private boolean isWebServiceValidUser(final String passWord) {
    if (CommonUtils.isNotNull(passWord)) {
      String systemType = getWSSystemMap().get(passWord);
      if (CommonUtils.isNotNull(systemType)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param userName userName
   * @param passWord passWord
   * @return the user
   */
  public ApicUser getApicUser(final String userName, final String passWord) {
    ApicUser user = null;

    if ((userName != null) && isWebServiceValidUser(passWord)) {
      user = getApicUser(userName.toUpperCase(Locale.GERMAN));
    }
    return user;
  }


  /**
   * @param systemName example ICDM
   * @return the icdm web service system password
   */
  public String getSystemPassword(final String systemName) {
    String passWrd = "";
    for (Entry<String, String> wsSys : getWSSystemMap().entrySet()) {
      if (wsSys.getValue().equals(systemName)) {
        passWrd = wsSys.getKey();
      }
    }
    return passWrd;
  }

  /**
   * returns true, if pidc division attribute is used in the review questionnaire
   *
   * @param versId
   * @param valueId
   * @return
   */
  // ICDM-2493
  public boolean hasProjectAttributeUsedInReview(final Long versId, final Long valueId) {
    return getDataLoader().hasProjectAttributeUsedInReview(versId, valueId);
  }


  /**
   * @param elementID elementID
   * @return the pidc variant by loading the pidc version
   */
  public PIDCVariant getPidcVariantAndVersion(final Long elementID) {
    return getDataLoader().getPidcVariantAndVersion(elementID);
  }

  /**
   * @param id primary key
   * @return the PredefinedAttrValue Object
   */
  public PredefinedAttrValue getPredfnAttrValue(final Long id) {
    return this.dataCache.getPredAttrValMap().get(id);
  }

  /**
   * @param validityId primary key
   * @return the PredefinedAttrValueValidity Object
   */
  public PredefinedAttrValuesValidity getPredfnValidity(final Long validityId) {
    return this.dataCache.getPredAttrValValidityMap().get(validityId);
  }

  // ICDM-2593 Get all level attributes (attribute level is not null)
  /**
   * @return specific attributes (level attributes) for PIDC
   */
  public Map<Integer, Attribute> getPidcSpecificAttrMap() {
    return this.dataCache.getSpecificAttributeMap();
  }

  /**
   * @return map of all PIDC A2L mappings
   */
  public Map<Long, PIDCA2l> getAllPidcA2lMap() {
    return this.dataCache.getAllPidcA2lMap();
  }

  // Story 221726
  /**
   * @return
   */
  public ConcurrentHashMap<Long, Long> getAllAlternateAttrs() {
    return getDataLoader().getAlternateAttrs();
  }

  // Story 221802
  public ApicUser getMonicaAuditor() {
    return this.dataCache.getMonicaAuditor();
  }
}
