/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.io.FilenameUtils;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.apic.jpa.PidcFavourite;
import com.bosch.caltool.dmframework.bo.AbstractDataLoader;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.database.entity.a2l.TvcdmPst;
import com.bosch.caltool.icdm.database.entity.apic.MvTa2lFileinfo;
import com.bosch.caltool.icdm.database.entity.apic.MvTa2lVcdmVersion;
import com.bosch.caltool.icdm.database.entity.apic.MvTabeDataset;
import com.bosch.caltool.icdm.database.entity.apic.MvVcdmDatasetsWorkpkgStat;
import com.bosch.caltool.icdm.database.entity.apic.TAliasDefinition;
import com.bosch.caltool.icdm.database.entity.apic.TAlternateAttr;
import com.bosch.caltool.icdm.database.entity.apic.TCharacteristic;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrix;
import com.bosch.caltool.icdm.database.entity.apic.TLink;
import com.bosch.caltool.icdm.database.entity.apic.TMandatoryAttr;
import com.bosch.caltool.icdm.database.entity.apic.TMessage;
import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;
import com.bosch.caltool.icdm.database.entity.apic.TPidcChangeHistory;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TSsdFeature;
import com.bosch.caltool.icdm.database.entity.apic.TUnit;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicAccessRight;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicNodeAccess;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicUser;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrDependency;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrGroup;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrSuperGroup;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.database.entity.apic.TabvCommonParam;
import com.bosch.caltool.icdm.database.entity.apic.TabvIcdmFile;
import com.bosch.caltool.icdm.database.entity.apic.TabvPidFavorite;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectAttr;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectSubVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectidcard;
import com.bosch.caltool.icdm.database.entity.apic.TabvTopLevelEntity;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCase;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCaseGroup;
import com.bosch.caltool.icdm.database.entity.general.TWsSystem;
import com.bosch.caltool.icdm.model.a2l.VCDMA2LFileDetail;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * This class loads the data from the database, and stores in the DataCache class. The entity manager is provided by
 * EntityProvider instance
 *
 * @author BNE4COB
 */
class DataLoader extends AbstractDataLoader {

  /**
   * string query for the focus matrix
   */
  private static final int STRING_QUERY_SIZE = 40;

  /**
   * Apic data provider instance
   */
  private final ApicDataProvider dataProvider;

  /**
   * Query parameter 1
   */
  private static final int QUERY_PARAM_1 = 1;

  /**
   * Query parameter 2
   */
  private static final int QUERY_PARAM_2 = 2;

  /**
   * Constructor
   *
   * @param dataProvider the apic data provider
   */
  DataLoader(final ApicDataProvider dataProvider) {
    super();

    this.dataProvider = dataProvider;
    ObjectStore.getInstance().registerCacheRefresher(new ApicDataCacheRefresher(dataProvider));
  }

  /**
   * Fetch start up data
   */
  protected final void fetchStartupData() {
    int stage = 0;

    if (canLoadData(ApicDataProvider.P_LOAD_USRS_START, stage, ApicConstants.START_DATA_FETCH_STAGE0)) {
      fetchUsers();
      stage = 1;
    }

    if (canLoadData(ApicDataProvider.P_LOAD_CPRM_START, stage, ApicConstants.START_DATA_FETCH_STAGE0)) {
      fetchCommonParams();
      stage = ApicConstants.START_DATA_FETCH_STAGE2;
    }

    // Fetch attributes, pidc structure, pidc. Load levels are also updated inside
    stage = fetchAllAttrPIDC(stage);

    if (canLoadData(ApicDataProvider.P_LOAD_UC_START, stage, ApicConstants.START_DATA_FETCH_STAGE2)) {
      fetchUCGroups(null);
      stage = ApicConstants.START_DATA_FETCH_STAGE6;
    }

    // Icdm-346
    if (canLoadData(ApicDataProvider.P_LOAD_UNDE_START, stage, ApicConstants.START_DATA_FETCH_STAGE2)) {
      fetchUserNodeAccess();
      stage = ApicConstants.START_DATA_FETCH_STAGE7;
    }

    // Icdm-346
    if (canLoadData(ApicDataProvider.P_LOAD_MTPL_START, stage, ApicConstants.START_DATA_FETCH_STAGE7)) {
      fetchIcdmMailTemplate();
      stage = ApicConstants.START_DATA_FETCH_STAGE8;
    }

    // Icdm-474 dcn for top level entity
    if (canLoadData(ApicDataProvider.P_LOAD_TPNT_START, stage, ApicConstants.START_DATA_FETCH_STAGE8)) {
      fetchTopLevelEntities();
      stage = ApicConstants.START_DATA_FETCH_STAGE9;
    }

    // Icdm-954 load the characteristics
    if (canLoadData(ApicDataProvider.P_LOAD_CHAR_START, stage, ApicConstants.START_DATA_FETCH_STAGE9)) {
      fetchCharacteristics();
      stage = ApicConstants.START_DATA_FETCH_STAGE10;
    }
    // ICDM-1043 load the tip of the day file
    if (canLoadData(ApicDataProvider.P_LOAD_TIP_OF_DAY_START, stage, ApicConstants.START_DATA_FETCH_STAGE10)) {
      // fetchWelcomePage();
      stage = ApicConstants.START_DATA_FETCH_STAGE11;
    }
    // load the messages
    if (canLoadData(ApicDataProvider.P_LOAD_MESSAGES_START, stage, ApicConstants.START_DATA_FETCH_STAGE11)) {
      fetchMessages();
      fetchWSSystems();
    }
  }


  /**
   * @param entityManager entityManager
   */
  public void fetchAliasDefinitions(final EntityManager entityManager) {
    getLogger().debug("fetching alias defintion...");
    final TypedQuery<TAliasDefinition> query =
        entityManager.createNamedQuery(TAliasDefinition.GET_ALL, TAliasDefinition.class);
    final List<TAliasDefinition> aliasDefList = query.getResultList();
    for (TAliasDefinition dbAliasDef : aliasDefList) {
      AliasDefinition aliasDefinition = new AliasDefinition(this.dataProvider, dbAliasDef.getAdId());
      getDataCache().getAliasDefMap().put(dbAliasDef.getAdId(), aliasDefinition);
    }
    getLogger().debug("Alias Defintions fetched from database: " + getDataCache().getAliasDefMap().size());
  }

  /**
   * fetch the tool tips from the Data base Map key Group name +
   */
  protected void fetchMessages() {
    getLogger().debug("fetching tool tips from database ...");

    final TypedQuery<TMessage> query =
        getEntityProvider().getEm().createQuery("SELECT mess FROM TMessage mess", TMessage.class);
    final List<TMessage> dbParamList = query.getResultList();

    for (TMessage dbParam : dbParamList) {
      if ((this.dataProvider.getLanguage() == Language.GERMAN) && (dbParam.getMessageTextGer() != null)) {
        getDataCache().getMessageMap().put(
            CommonUtils.concatenate(dbParam.getGroupName(), ApicConstants.CDR_PARAM_DELIMITER, dbParam.getName()),
            dbParam.getMessageTextGer());
      }
      else {
        getDataCache().getMessageMap().put(
            CommonUtils.concatenate(dbParam.getGroupName(), ApicConstants.CDR_PARAM_DELIMITER, dbParam.getName()),
            dbParam.getMessageText());
      }
    }

    getLogger().debug("Tool Tips fetched from database: " + getDataCache().getMessageMap().size());

  }

  /**
   * fetch the tool tips from the Data base Map key Group name +
   */
  protected void fetchWSSystems() {
    getLogger().debug("fetching Web service systemsfrom database ...");

    final TypedQuery<TWsSystem> query =
        getEntityProvider().getEm().createNamedQuery(TWsSystem.ALL_T_WS_SYSTEM, TWsSystem.class);
    final List<TWsSystem> dbParamList = query.getResultList();

    for (TWsSystem dbSystem : dbParamList) {

      String systemToken = dbSystem.getSystemToken();
      String systemType = dbSystem.getSystemType();

      getDataCache().getWSSystemMap().put(systemToken, systemType);
      if (CommonUtilConstants.ICDM_SYSTEM_PASSWRD_KEY.equals(systemType)) {
        ObjectStore.getInstance().setIcdmPwd(systemToken);
      }

    }

    getLogger().debug("Web service systems fecthed from DB: " + getDataCache().getWSSystemMap().size());

  }


  /**
   * @param stage
   * @return the Current Stage
   */
  private int fetchAllAttrPIDC(final int stage) {
    int currStage = stage;
    if (canLoadData(ApicDataProvider.P_LOAD_ATTR_START, currStage, ApicConstants.START_DATA_FETCH_STAGE2)) {
      if (CODE_YES
          .equals(ObjectStore.getInstance().getProperty(ApicDataProvider.P_LOAD_LVL_ATTR_ONLY_START, CODE_NO))) {
        fetchProjStructAttributes();
      }
      else {
        fetchAllAttributes(getEntityProvider().getEm(), null);
      }
      currStage = ApicConstants.START_DATA_FETCH_STAGE3;
    }
    if (canLoadData(ApicDataProvider.P_LOAD_PSTR_START, currStage, ApicConstants.START_DATA_FETCH_STAGE3)) {
      fetchPidcStructure();
      currStage = ApicConstants.START_DATA_FETCH_STAGE4;
    }

    if (canLoadData(ApicDataProvider.P_LOAD_PIDC_START, currStage, ApicConstants.START_DATA_FETCH_STAGE4)) {
      fetchPidc(null);
      currStage = ApicConstants.START_DATA_FETCH_STAGE5;
    }
    return currStage;
  }

  /**
   * ICDM-1836
   *
   * @param entityManager EntityManager
   */
  public void fetchMandatoryAttributes(final EntityManager entityManager) {

    getLogger().debug("fetching madatory attributes...");

    final TypedQuery<TMandatoryAttr> query =
        entityManager.createNamedQuery(TMandatoryAttr.GET_ALL, TMandatoryAttr.class);
    final List<TMandatoryAttr> mandatoryAttrList = query.getResultList();

    for (TMandatoryAttr dbMandatoryAttr : mandatoryAttrList) {
      new MandatoryAttr(this.dataProvider, dbMandatoryAttr.getTabvAttrValue().getValueId(), dbMandatoryAttr.getMaId());
      Set<Attribute> mandAttrSet =
          getDataCache().getMandatoryAttrsMap().get(dbMandatoryAttr.getTabvAttrValue().getValueId());
      if (null == mandAttrSet) {
        mandAttrSet = new HashSet<>();
        // fill the set of mandatory attributes against each value id
        mandAttrSet.add(getDataCache().getAttribute(dbMandatoryAttr.getTabvAttribute().getAttrId()));
        getDataCache().getMandatoryAttrsMap().put(dbMandatoryAttr.getTabvAttrValue().getValueId(), mandAttrSet);
      }
      else {
        // fill the set of mandatory attributes against each value id
        mandAttrSet.add(getDataCache().getAttribute(dbMandatoryAttr.getTabvAttribute().getAttrId()));
      }
    }

    getLogger()
        .debug("Mandatory attributes fetched from database: " + getDataCache().getMandatoryAttrObjectsMap().size());


  }

  /**
   * Icdm-513 Fetch all the Units From the T_UNITS Table
   *
   * @return map of units
   */
  protected final ConcurrentMap<String, Unit> fetchAllUnits() {
    getLogger().debug("fetching All Cdr Units from database ...");

    final ConcurrentMap<String, Unit> unitMap = new ConcurrentHashMap<String, Unit>();

    final TypedQuery<TUnit> query = getEntityProvider().getEm().createQuery("SELECT unit FROM TUnit unit", TUnit.class);
    final List<TUnit> dbUnitList = query.getResultList();
    for (TUnit dbParam : dbUnitList) {
      unitMap.put(dbParam.getUnit(), new Unit(dbParam.getUnit()));
    }

    getLogger().debug("Units fetched from database: " + unitMap.size());

    return unitMap;
  }


  /**
   * Icdm-474 dcn for top level entity Fetch the Top Level Entities
   */
  private void fetchTopLevelEntities() {
    getLogger().debug("fetching Top Level Entities from database ...");

    final TypedQuery<TabvTopLevelEntity> query =
        getEntityProvider().getEm().createQuery("SELECT top FROM TabvTopLevelEntity top", TabvTopLevelEntity.class);

    final List<TabvTopLevelEntity> dbTopEntityList = query.getResultList();
    for (TabvTopLevelEntity dbParam : dbTopEntityList) {
      getDataCache().getTopEntList().put(dbParam.getEntId(), new TopLevelEntity(this.dataProvider, dbParam.getEntId()));
    }

    getLogger().debug("Top level Entities fetched from database: " + getDataCache().getCommonParams().size());
    getLogger().debug(" ");
  }


  /**
   * Icdm-954 fetch the Characteristics
   */
  private void fetchCharacteristics() {
    getLogger().debug("fetching Characteristics from database ...");

    final TypedQuery<TCharacteristic> query =
        getEntityProvider().getEm().createQuery("SELECT char FROM TCharacteristic char", TCharacteristic.class);

    final List<TCharacteristic> dbTopEntityList = query.getResultList();
    for (TCharacteristic dbParam : dbTopEntityList) {
      getDataCache().getAllCharMap().put(dbParam.getCharId(),
          new AttributeCharacteristic(this.dataProvider, dbParam.getCharId()));
    }

    getLogger().debug("Characteristics fetched from database: " + getDataCache().getAllCharMap().size());
    getLogger().debug(" ");
  }

  /**
   * Fetches the hotline mail template from Database
   */
  private void fetchIcdmMailTemplate() {
    getLogger().debug("fetching hotLine mail template from database ...");

    new IcdmFile(this.dataProvider,
        Long.valueOf(this.dataProvider.getParameterValue(ApicConstants.ICDM_HOTLINE_FILE_ID)));
    getLogger().debug("hotLine mail template fetched from database ");
  }

  /**
   * Fetches the welcome page file from Database
   */
  // ICDM-1043, ICDM-1566
  @Deprecated
  public void fetchWelcomePage() {
    getLogger().debug("fetching welcome page from database ...");
    IcdmFile welcomePageFiles = new IcdmFile(this.dataProvider,
        Long.valueOf(this.dataProvider.getParameterValue(ApicConstants.KEY_WELCOME_FILE_ID)));
    getLogger().debug("welcome page fetched from database ");
    try {
      String dirPath =
          CommonUtils.concatenate(CommonUtils.getSystemUserTempDirPath(), File.separator, ApicConstants.WELCOME_PAGE);
      CommonUtils.deleteFile(dirPath);
      dirPath = CommonUtils.concatenate(dirPath, File.separator);
      Map<String, byte[]> files = welcomePageFiles.getFiles();

      /*
       * ICDM-1566 Allow tooltips for the menu points on the iCDM startpage dynamically
       */
      createWelcomeIcdmFiles(dirPath, files);
    }
    catch (IOException e) {
      getLogger().error(e.getMessage(), e);
    }
  }


  /**
   * create web resources like html, css, images etc
   *
   * @param dirPath
   * @param files
   */
  // ICDM-1566
  private void createWelcomeIcdmFiles(final String dirPath, final Map<String, byte[]> files) throws IOException {
    Set<String> keySet = files.keySet();
    for (String key : keySet) {
      if (!CommonUtils.isEmptyString(FilenameUtils.getExtension(key))) {// check if path has file
        byte[] fileBytes = files.get(key);
        CommonUtils.createFile(dirPath.concat(key), fileBytes); // creating file in temp path
        // (C:\..\AppData\Local\Temp\welcome_page\)
      }
    }
  }

  /**
   * Fetch the common parameters from database
   */
  private void fetchCommonParams() {
    getLogger().debug("fetching common params from database ...");

    final TypedQuery<TabvCommonParam> query =
        getEntityProvider().getEm().createQuery("SELECT cmp FROM TabvCommonParam cmp", TabvCommonParam.class);
    final List<TabvCommonParam> dbParamList = query.getResultList();

    for (TabvCommonParam dbParam : dbParamList) {
      getDataCache().getCommonParams().put(dbParam.getParamId(), dbParam.getParamValue());
    }

    getLogger().debug("common params fetched from database: " + getDataCache().getCommonParams().size());
  }

  /**
   * Fetch all users
   */
  private void fetchUsers() {
    if (CODE_YES.equals(ObjectStore.getInstance().getProperty(ApicDataProvider.P_LOAD_CUR_USER_ONLY_START, CODE_NO))) {
      fetchCurrentUser();
    }
    else {
      fetchAllUsers(getEntityProvider().getEm());
    }
  }

  /**
   * Fetch the current user from DB
   */
  private void fetchCurrentUser() {
    getLogger().debug("fetching current user from database ...");

    synchronized (getDataCache().userSyncLock) {
      final TypedQuery<TabvApicUser> qDbUser =
          getEntityProvider().getEm().createNamedQuery(TabvApicUser.NQ_GET_APIC_USER_BY_NAME, TabvApicUser.class);
      qDbUser.setParameter("username", ObjectStore.getInstance().getAppUserName());
      final List<TabvApicUser> dbUsers = qDbUser.getResultList();

      Long userID;
      for (TabvApicUser dbUser : dbUsers) {
        userID = dbUser.getUserId();
        // Story 221802 - skip the dummy user created for auditor of MoniCa reviews
        if (!getDataCache().getAllApicUsersMap().containsKey(userID)) {
          getDataCache().getAllApicUsersMap().put(dbUser.getUserId(), new ApicUser(this.dataProvider, userID));
        }
      }
      getLogger().debug("users retrieved from database: " + dbUsers.size());
      getLogger().debug("Total users in cache: " + getDataCache().getAllApicUsersMap().size());

    }
  }

  /**
   * Fetch all users
   *
   * @param entMgr
   */
  void fetchAllUsers(final EntityManager entMgr) {

    getLogger().debug("fetching users from database ...");

    synchronized (getDataCache().userSyncLock) {
      final TypedQuery<TabvApicUser> qDbUser =
          getEntityProvider().getEm().createNamedQuery(TabvApicUser.NQ_GET_ALL_APIC_USERS, TabvApicUser.class);
      final List<TabvApicUser> dbUsers = qDbUser.getResultList();

      Long userID;
      for (TabvApicUser dbUser : dbUsers) {
        userID = dbUser.getUserId();
        // Story 221802 - skip the dummy user created for auditor of MoniCa reviews
        if (!getDataCache().getAllApicUsersMap().containsKey(userID)) {
          getDataCache().getAllApicUsersMap().put(dbUser.getUserId(), new ApicUser(this.dataProvider, userID));
        }

        // Story 221802
        if (userID == -1) {
          getDataCache().setMonicaAuditor(new ApicUser(this.dataProvider, userID));
        }
      }
      getLogger().debug("users retrieved from database: " + dbUsers.size());
      getLogger().debug("Total users in cache: " + getDataCache().getAllApicUsersMap().size());

    }
  }


  /**
   * Fetch project structure attributes and their details
   */
  private void fetchProjStructAttributes() {

    if (CODE_NO.equals(ObjectStore.getInstance().getProperty(ApicDataProvider.P_LOAD_LVL_ATTR_ONLY_START, CODE_NO))) {
      return;
    }

    getLogger().debug("fetching pidc structure attributes from database ...");

    final TypedQuery<TabvAttribute> qDbProjStrAttr =
        getEntityProvider().getEm().createNamedQuery(TabvAttribute.NQ_GET_PROJ_STRUCT_ATTRS, TabvAttribute.class);
    final List<TabvAttribute> dbProjStrAttr = qDbProjStrAttr.getResultList();


    fetchAttributes(dbProjStrAttr, null);

    getLogger().debug("fetching pidc structure attributes from database completed");
  }

  /**
   * Fetch all attributes, super groups, attribute dependencies
   *
   * @param entMgr EntityManager
   * @param changedDataMap change data map, if called from DCN cache refresher
   */
  protected void fetchAllAttributes(final EntityManager entMgr, final Map<Long, ChangedData> changedDataMap) {
    getLogger().debug("fetching group structure and all attributes from database ...");

    synchronized (getDataCache().attrSyncLock) {
      final TypedQuery<TabvAttrSuperGroup> qDbSuperGroups =
          entMgr.createNamedQuery(TabvAttrSuperGroup.NQ_GET_ALL_SUPER_GROUPS, TabvAttrSuperGroup.class);
      final List<TabvAttrSuperGroup> dbSuperGroups = qDbSuperGroups.getResultList();

      Long superGroupID;
      for (TabvAttrSuperGroup dbSuperGroup : dbSuperGroups) {
        superGroupID = dbSuperGroup.getSuperGroupId();
        if (getDataCache().getAllSuperGroups().containsKey(superGroupID)) {
          continue;
        }
        fetchGroups(dbSuperGroup, null);
        getDataCache().getAllSuperGroups().put(superGroupID, new AttrSuperGroup(this.dataProvider, superGroupID));

        if (changedDataMap != null) {
          changedDataMap.put(superGroupID,
              new ChangedData(ChangeType.INSERT, superGroupID, TabvAttrSuperGroup.class, DisplayEventSource.DATABASE));
        }

      }

    }

    getLogger().debug("super groups fetched from database: " + getDataCache().getAllSuperGroups().size());
    getLogger().debug("groups fetched from database: " + getDataCache().getAllGroups().size());
    getLogger().debug("attributes fetched from database: " + getDataCache().getAllAttributes().size());
  }


  /**
   * Fetch groups of the super group
   *
   * @param dbSuperGroup super group entity
   * @param changedDataMap changed data map, if applicable
   */
  protected void fetchGroups(final TabvAttrSuperGroup dbSuperGroup, final Map<Long, ChangedData> changedDataMap) {

    Long groupID;

    for (TabvAttrGroup dbGroup : dbSuperGroup.getTabvAttrGroups()) {
      groupID = Long.valueOf(dbGroup.getGroupId());

      if (getDataCache().getAllGroups().containsKey(groupID)) {
        continue;
      }

      getDataCache().getAllGroups().put(groupID, new AttrGroup(this.dataProvider, groupID));
      if (changedDataMap != null) {
        changedDataMap.put(groupID,
            new ChangedData(ChangeType.INSERT, groupID, TabvAttrGroup.class, DisplayEventSource.DATABASE));
      }

      fetchAttributes(dbGroup, changedDataMap);

    }
  }

  /**
   * Fetch attributes of the group
   *
   * @param dbGroup group entity
   * @param changedDataMap changed data map, if applicable
   */
  protected void fetchAttributes(final TabvAttrGroup dbGroup, final Map<Long, ChangedData> changedDataMap) {
    fetchAttributes(dbGroup.getTabvAttributes(), changedDataMap);
  }

  /**
   * Fetch attributes of the group
   *
   * @param dbGroup group entity
   * @param changedDataMap changed data map, if applicable
   */
  private void fetchAttributes(final List<TabvAttribute> tabvAttributesList,
      final Map<Long, ChangedData> changedDataMap) {

    Long attributeID;

    long attrLevel;

    for (TabvAttribute dbAttribute : tabvAttributesList) {
      attributeID = Long.valueOf(dbAttribute.getAttrId());

      if (getDataCache().getAllAttributes().containsKey(attributeID)) {
        continue;
      }

      getDataCache().getAllAttributes().put(attributeID, new Attribute(this.dataProvider, attributeID));
      if (changedDataMap != null) {
        changedDataMap.put(attributeID,
            new ChangedData(ChangeType.INSERT, attributeID, TabvAttribute.class, DisplayEventSource.DATABASE));
      }

      fetchAttrValues(dbAttribute, changedDataMap);

      fetchAttrDependencies(dbAttribute, changedDataMap);

      if (dbAttribute.getAttrLevel() == null) {
        attrLevel = 0;
      }
      else {
        attrLevel = dbAttribute.getAttrLevel().longValue();
      }

      if (attrLevel > 0) {

        getDataCache().setPidcStructMaxLevel(Math.max(getDataCache().getPidcStructMaxLevel(), attrLevel));

        if (!getDataCache().getPidcStructAttrMap().containsKey(dbAttribute.getAttrLevel().longValue())) {
          getDataCache().getPidcStructAttrMap().put(dbAttribute.getAttrLevel().longValue(),
              getDataCache().getAttribute(attributeID));
        }
      }
      else if (attrLevel == ApicConstants.SDOM_PROJECT_NAME_ATTR) {
        getDataCache().getSpecificAttributeMap().put(ApicConstants.SDOM_PROJECT_NAME_ATTR,
            getDataCache().getAllAttributes().get(attributeID));
      }
      else if (attrLevel == ApicConstants.VCDM_APRJ_NAME_ATTR) {
        getDataCache().getSpecificAttributeMap().put(ApicConstants.VCDM_APRJ_NAME_ATTR,
            getDataCache().getAllAttributes().get(attributeID));
      }
      else if (attrLevel == ApicConstants.PROJECT_NAME_ATTR) {
        getDataCache().getSpecificAttributeMap().put(ApicConstants.PROJECT_NAME_ATTR,
            getDataCache().getAllAttributes().get(attributeID));
      }
      else if (attrLevel == ApicConstants.VARIANT_CODE_ATTR) {
        getDataCache().getSpecificAttributeMap().put(ApicConstants.VARIANT_CODE_ATTR,
            getDataCache().getAllAttributes().get(attributeID));
      }
      else if (attrLevel == ApicConstants.SUB_VARIANT_CODE_ATTR) {
        getDataCache().getSpecificAttributeMap().put(ApicConstants.SUB_VARIANT_CODE_ATTR,
            getDataCache().getAllAttributes().get(attributeID));
      }
      else if (attrLevel == ApicConstants.VARIANT_CODING_ATTR) {
        getDataCache().getSpecificAttributeMap().put(ApicConstants.VARIANT_CODING_ATTR,
            getDataCache().getAllAttributes().get(attributeID));
      }
    }
  }

  /**
   * @param dbAttribute
   * @param changedDataMap
   */
  private void fetchAttrValues(final TabvAttribute dbAttribute, final Map<Long, ChangedData> changedDataMap) {
    for (TabvAttrValue dbValue : dbAttribute.getTabvAttrValues()) {
      Long valueID = dbValue.getValueId();
      if (getDataCache().getAllAttrValuesMap().containsKey(valueID)) {
        continue;
      }

      // Create new attribute value object(internally)
      getDataCache().getAttrValue(valueID);

      if (changedDataMap != null) {
        changedDataMap.put(valueID,
            new ChangedData(ChangeType.INSERT, valueID, TabvAttrValue.class, DisplayEventSource.DATABASE));
      }
    }

  }

  /**
   * Fetch dependencies of the attribute
   *
   * @param dbAttribute attribute entity
   * @param changedDataMap changed data map, if applicable
   */
  protected synchronized void fetchAttrDependencies(final TabvAttribute dbAttribute,
      final Map<Long, ChangedData> changedDataMap) {
    Long dependencyID;
    Long valueID;

    for (TabvAttrDependency dbAttrDependency : dbAttribute.getTabvAttrDependencies()) {
      dependencyID = dbAttrDependency.getDepenId();

      if (!getDataCache().getAttrDependenciesMap().containsKey(dependencyID)) {
        getDataCache().getAttrDependenciesMap().put(dependencyID, new AttrDependency(this.dataProvider, dependencyID));
        if (changedDataMap != null) {
          changedDataMap.put(dependencyID,
              new ChangedData(ChangeType.INSERT, dependencyID, TabvAttrDependency.class, DisplayEventSource.DATABASE));
        }
      }

      // get the value if the valueID is not NULL
      if (dbAttrDependency.getTabvAttrValueD() != null) {
        valueID = dbAttrDependency.getTabvAttrValueD().getValueId();

        if (!getDataCache().getAllAttrValuesMap().containsKey(valueID)) {
          getDataCache().getAllAttrValuesMap().put(valueID, new AttributeValue(this.dataProvider, valueID));
          if (changedDataMap != null) {
            changedDataMap.put(valueID,
                new ChangedData(ChangeType.INSERT, valueID, TabvAttrValue.class, DisplayEventSource.DATABASE));
          }
        }
      }

    }

  }

  /**
   * Fetch PIDC structure
   */
  private void fetchPidcStructure() {

    final int rootNodeLevel = ApicConstants.PIDC_ROOT_LEVEL;

    getLogger().debug("fetching Project-ID-Card structure from database ...");

    ConcurrentMap<Long, Set<AttributeValue>> lvlAttrValMap = getLevelAttrValues();

    getDataCache().setPidcRootNode(new PIDCNode(this.dataProvider, rootNodeLevel, null,
        getDataCache().getPidcStructAttrMap().get(rootNodeLevel + 1L), null));


    getDataCache().getPidcRootNode()
        .addNodeChildren(getPidcNodeChildren(rootNodeLevel + 1, getDataCache().getPidcRootNode(), lvlAttrValMap));


    getLogger().debug("Project-ID-Card structure fetched from database");

  }

  /**
   * @param attrIDSet
   * @return
   */
  private ConcurrentMap<Long, Set<AttributeValue>> getLevelAttrValues() {

    ConcurrentMap<Long, Set<AttributeValue>> retMap = new ConcurrentHashMap<>();

    for (Entry<Long, Attribute> strAttrEntry : getDataCache().getPidcStructAttrMap().entrySet()) {
      Long attrID = strAttrEntry.getKey();
      Attribute attr = strAttrEntry.getValue();

      retMap.put(attrID, new HashSet<>(attr.getAttrValues()));
    }

    return retMap;
  }

  /**
   * PIDC node's children
   *
   * @param level node level
   * @param parentNode parent node
   * @return list of child nodes
   */
  private synchronized List<PIDCNode> getPidcNodeChildren(final int level, final PIDCNode parentNode,
      final ConcurrentMap<Long, Set<AttributeValue>> lvlAttrValMap) {

    PIDCNode currentNode;
    boolean addNode;

    final List<PIDCNode> nodesList = new Vector<PIDCNode>();

    final Attribute levelAttribute = getDataCache().getPidcStructAttrMap().get(Long.valueOf(level));

    Collection<AttributeValue> valCollection = lvlAttrValMap.get(levelAttribute.getID());
    if (valCollection == null) {
      valCollection = levelAttribute.getAttrValues();
    }

    for (AttributeValue levelAttrValue : valCollection) {
      addNode = true;
      currentNode = new PIDCNode(this.dataProvider, level, parentNode, levelAttribute, levelAttrValue);

      // Check if the node is already added to the tree.
      for (PIDCNode node : parentNode.getNodeChildren()) {
        if (currentNode.compareTo(node) == 0) {
          currentNode = node;
          addNode = false;
          break;
        }
      }

      if (addNode) {
        nodesList.add(currentNode);
      }
      if (level < getDataCache().getPidcStructMaxLevel()) {
        currentNode.addNodeChildren(getPidcNodeChildren(level + 1, currentNode, lvlAttrValMap));
      }
      else {
        if (addNode) {
          getDataCache().getPidcLeafNodesList().add(currentNode);
        }
      }
    }

    return nodesList;
  }

  /**
   * Fetch all PIDCs
   *
   * @param changedDataMap changed data map, if invoked from DCN cache refresher
   */
  protected void fetchPidc(final Map<Long, ChangedData> changedDataMap) {

    getLogger().debug("fetching all Project-ID-Cards from database ...");

    final TypedQuery<TabvProjectAttr> qProjStrucAttrs = getEntityProvider().getEm()
        .createNamedQuery(TabvProjectAttr.NQ_GET_STRUCT_ATTR_FOR_ALL_ACT_VERS, TabvProjectAttr.class);


    List<TabvProjectAttr> dbProjAttrList = qProjStrucAttrs.getResultList();
    long pidcStructMaxLevel = getDataCache().getPidcStructMaxLevel();

    Long activePidcVersionID;
    Long pidcID;
    PIDCVersion activeVersion;
    TPidcVersion dbActPidcVers;

    for (TabvProjectAttr dbProjAttr : dbProjAttrList) {
      dbActPidcVers = dbProjAttr.getTPidcVersion();
      activePidcVersionID = dbActPidcVers.getPidcVersId();

      activeVersion = getDataCache().getPidcVersion(activePidcVersionID);

      if (activeVersion == null) {
        activeVersion = new PIDCVersion(this.dataProvider, activePidcVersionID);
      }

      pidcID = dbActPidcVers.getTabvProjectidcard().getProjectId();

      PIDCard pidc = getDataCache().getPidc(pidcID);
      if (pidc == null) {
        pidc = new PIDCard(this.dataProvider, pidcID, activeVersion);
        if (changedDataMap != null) {
          changedDataMap.put(pidcID,
              new ChangedData(ChangeType.INSERT, pidcID, TabvProjectidcard.class, DisplayEventSource.DATABASE));
        }
      }

      pidc.addStructureAttrValue(dbProjAttr.getTabvAttribute().getAttrId(), dbProjAttr.getTabvAttrValue().getValueId());
    }


    assignPIDCToPIDCNodes(pidcStructMaxLevel);

    getLogger().debug("Project-ID-Cards fetched from database: " + getDataCache().getAllPidcMap().size());

  }

  /**
   * assign PIDCs to PIDCNodes
   *
   * @param pidcStructMaxLevel long
   */
  private void assignPIDCToPIDCNodes(final long pidcStructMaxLevel) {
    // 221715
    for (PIDCard pidcCard : getDataCache().getAllPidcMap().values()) {

      if (pidcCard.getLeafNode() != null) {
        // PIDC already assigned to a node
        continue;
      }

      // Find the right leaf node, using PIDC's attribute values, to put the PIDC to the leaf
      PIDCNode node = getDataCache().getPidcRootNode();
      for (long level = 1L; level <= pidcStructMaxLevel; level++) {
        Attribute levelAttr = getDataCache().getPidcStructAttrMap().get(level);
        long structValID = pidcCard.getStructureAttrValueIDs().get(levelAttr.getID());
        node = node.getChildNode(structValID);
      }

      // Assign the identified node to PIDC
      node.getPidCardMap().put(pidcCard.getID(), pidcCard);
      pidcCard.setLeafNode(node);
    }
  }

  /**
   * Get the apic user
   *
   * @param userName user name (NT ID)
   * @return user object
   */
  public synchronized ApicUser createApicUser(final String userName) {

    getLogger().info("User " + userName + " not found in iCDM, adding user details");

    ApicUser returnValue = null;

    // user not found in the list of users => create a new user and access right
    // Note : This is not handled using commands, as this should not be un-done

    final TabvApicUser dbApicUser = new TabvApicUser();
    dbApicUser.setUsername(userName);
    dbApicUser.setCreatedDate(ApicUtil.getCurrentUtcTime());
    dbApicUser.setCreatedUser(getDataCache().getAppUsername());
    dbApicUser.setVersion(Long.valueOf(1));

    TabvApicAccessRight dbAccessRight;
    dbAccessRight = new TabvApicAccessRight();
    dbAccessRight.setTabvApicUser(dbApicUser);
    dbAccessRight.setAccessRight(ApicConstants.APIC_READ_ACCESS);
    dbAccessRight.setModuleName(ApicConstants.APIC_MODULE_NAME);
    dbAccessRight.setCreatedDate(ApicUtil.getCurrentUtcTime());
    dbAccessRight.setCreatedUser(getDataCache().getAppUsername());
    dbAccessRight.setVersion(Long.valueOf(1));

    getEntityProvider().startTransaction();

    try {
      getEntityProvider().registerNewEntity(dbApicUser);
      getEntityProvider().registerNewEntity(dbAccessRight);
      getEntityProvider().commitChanges();
      // create a new ApicUser
      // (will be added automatically to apicUserMap)
      returnValue = new ApicUser(this.dataProvider, dbApicUser.getUserId());

      // create the new AccessRight
      // (will be added automatically to accessRightsMap)
      new ApicAccessRight(this.dataProvider, dbAccessRight.getAccessrightId());
    }
    finally {
      getEntityProvider().endTransaction();
    }
    getLogger().debug("User added to iCDM");

    return returnValue;
  }

  public synchronized SortedSet<PIDCVersion> getUserPidcFavVersions(final ApicUser apicUser) {

    final SortedSet<PIDCVersion> pidcVersionList = new TreeSet<PIDCVersion>();
    SortedSet<PIDCard> pidcList = getUserPIDCFavSet(apicUser);

    for (PIDCard pidc : pidcList) {
      if (!pidc.getActiveVersion().isHidden()) {// ICDM-2182
        pidcVersionList.add(pidc.getActiveVersion());
      }
    }

    return pidcVersionList;

  }

  /**
   * @return the sorted set of all the pid favorites.For all users for web service.
   */
  public SortedSet<PidcFavourite> getAllPidcFavorites() {
    // Create Sorted set of pidc
    final SortedSet<PidcFavourite> pidcList = new TreeSet<>();
    String queryStr = "SELECT uf FROM TabvPidFavorite uf ";

    // get all the db pidc favourites
    final TypedQuery<TabvPidFavorite> qDbUserFavorites =
        getEntityProvider().getEm().createQuery(queryStr, TabvPidFavorite.class);

    final List<TabvPidFavorite> dbUserFavorites = qDbUserFavorites.getResultList();
    // get the data from the result list and return the pidc set
    for (TabvPidFavorite dbUserFavorite : dbUserFavorites) {
      PIDCard pidc = getDataCache().getPidc(dbUserFavorite.getTabvProjectidcard().getProjectId());
      // PidcFavourite favObj = new PidcFavourite(pidc, dbUserFavorite.getUserId());
      // pidcList.add(favObj);
    }
    return pidcList;
  }

  /**
   * @param apicUser ApicUser
   * @return SortedSet<PIDCard>
   */
  SortedSet<PIDCard> getUserPIDCFavSet(final ApicUser apicUser) {
    SortedSet<PIDCard> pidcList;
    if (getDataCache().getApicUserFavorites().containsKey(apicUser)) {
      pidcList = getDataCache().getApicUserFavorites().get(apicUser);
    }
    else {
      pidcList = new TreeSet<PIDCard>();
      for (TabvPidFavorite userFavorite : getDbPidcFavorites(apicUser)) {
        PIDCard pidc = getDataCache().getPidc(userFavorite.getTabvProjectidcard().getProjectId());
        pidcList.add(pidc);
      }
      getDataCache().getApicUserFavorites().put(apicUser, pidcList);
    }
    return pidcList;
  }

  private synchronized Collection<TabvPidFavorite> getDbPidcFavorites(final ApicUser apicUser) {
    getLogger().debug("fetching user favorites from database ...");

    final Collection<TabvPidFavorite> favoritesList = new ArrayList<TabvPidFavorite>();
    final TypedQuery<TabvPidFavorite> qDbUserFavorites =
        getEntityProvider().getEm().createQuery("SELECT uf FROM TabvPidFavorite uf " + "  where upper(uf.userId) = '" +
            apicUser.getUserName().toUpperCase() + "'", TabvPidFavorite.class);

    final List<TabvPidFavorite> dbUserFavorites = qDbUserFavorites.getResultList();
    for (TabvPidFavorite dbUserFavorite : dbUserFavorites) {
      favoritesList.add(dbUserFavorite);
    }

    getLogger().debug("user favorites from database : " + favoritesList.size());

    return favoritesList;
  }


  /**
   * Add a PIDC to the users favorites list
   *
   * @param apicUser The user to which the PIDC should be added as a favorite
   * @param newFavorite the PIDC which should be added as a favorite
   * @return TRUE, if the PIDC has been added as a favorite FALSE, if the PIDC was still a favorite
   */
  protected synchronized boolean addUserPidcFavorite(final ApicUser apicUser, final PIDCVersion newFavorite) {

    final SortedSet<PIDCard> usersPidcList = getUserPIDCFavSet(apicUser);

    if (usersPidcList.contains(newFavorite.getPidc())) {
      // PIDC is still a favorite
      // => do nothing
      return false;
    }


    final TabvPidFavorite dbNewFavorite = new TabvPidFavorite();

    final TPidcVersion dbNewFavoritePidc = getEntityProvider().getDbPIDCVersion(newFavorite.getID());

    dbNewFavorite.setTabvProjectidcard(dbNewFavoritePidc.getTabvProjectidcard());

    // dbNewFavorite.setUserId(apicUser.getUserName().toUpperCase());
    dbNewFavorite.setCreatedDate(ApicUtil.getCurrentUtcTime());

    getEntityProvider().registerNewEntity(dbNewFavorite);

    usersPidcList.add(newFavorite.getPidc());

    return true;


  }

  /**
   * Remove a PIDC from the users list of favorite PIDC
   *
   * @param apicUser The user from which the PIDC should be removed
   * @param pidcVersion The PIDC which should be removed
   * @return TRUE, if the PIDC has been removed FALSE, if the PIDC was not a favorite of the user
   */
  protected synchronized boolean removeUserPidcFavorite(final ApicUser apicUser, final PIDCVersion pidcVersion) {

    final Collection<PIDCard> usersPidcList = getUserPIDCFavSet(apicUser);

    if (usersPidcList.contains(pidcVersion.getPidc())) {

      final Collection<TabvPidFavorite> userFavorites = getDbPidcFavorites(apicUser);

      for (TabvPidFavorite userFavorite : userFavorites) {
        if (userFavorite.getTabvProjectidcard().getProjectId() == pidcVersion.getPidc().getID()) {
          getEntityProvider().deleteEntity(userFavorite);
          usersPidcList.remove(pidcVersion.getPidc());
          break;
        }

      }

      return true;
    }
    // PIDC is not a favorite
    // => do nothing
    return false;

  }

  /**
   * Load the A2L files of the given PVER in this pidc
   *
   * @param pidcID PIDC ID
   * @param pverName PVER name
   * @return all A2Lfiles
   */
  public synchronized Map<Long, A2LFile> loadPverA2LFiles(final Long pidcID, final String pverName) {

    getLogger().debug("fetching A2LFiles from database for PVER " + pverName + "...");

    // Get the cached a2l file map for the given pver in PIDC, without loading from DB.
    // Note loadFromDB should be false. If set to true, this will create a cyclic call.
    Map<Long, A2LFile> cachedA2LFilesMap = getDataCache().getPverAllA2LMap(pidcID, pverName, false);

    final TypedQuery<MvTa2lFileinfo> qDbA2LFiles =
        getEntityProvider().getEm().createNamedQuery(MvTa2lFileinfo.NQ_GET_PVER_A2L_FILES, MvTa2lFileinfo.class);
    qDbA2LFiles.setParameter("pver", pverName);

    final List<MvTa2lFileinfo> dbA2LFiles = qDbA2LFiles.getResultList();

    Long a2lFileID;
    A2LFile file;
    final ConcurrentMap<Long, A2LFile> retMap = new ConcurrentHashMap<>();

    for (MvTa2lFileinfo dbA2LFile : dbA2LFiles) {
      a2lFileID = dbA2LFile.getId();
      // If the A2LFile object is already created, reuse it. Else create a new one.
      file = cachedA2LFilesMap == null ? null : cachedA2LFilesMap.get(a2lFileID);
      if (file == null) {
        file = new A2LFile(this.dataProvider, a2lFileID);
      }
      retMap.put(a2lFileID, file);
    }

    getLogger().debug("A2L files fetched from database: " + retMap.size());

    return retMap;
  }

  /**
   * This method returns a Sorted Set of VCDMA2LFileDetails based on the a2lFileCheckSum
   *
   * @param a2lFileCheckSum of TABVA2LINFO
   * @return SortedSet<VCDMA2LFileDetail>
   */
  @Deprecated
  public SortedSet<VCDMA2LFileDetail> getVCDMA2LFileDetails(final String a2lFileCheckSum) {

    getLogger().debug("fetching vCDM A2LFile details from database ...");

    final TypedQuery<MvTa2lVcdmVersion> vcdmVersionQuery =
        getEntityProvider().getEm().createNamedQuery(MvTa2lVcdmVersion.NQ_FIND_VCDM_VERSION, MvTa2lVcdmVersion.class);
    vcdmVersionQuery.setParameter("a2lCSum", a2lFileCheckSum);

    final List<MvTa2lVcdmVersion> mvVCDMVersions = vcdmVersionQuery.getResultList();

    int size = mvVCDMVersions != null ? mvVCDMVersions.size() : 0;

    SortedSet<VCDMA2LFileDetail> vcdma2lFileDetails = new TreeSet<>();
    for (MvTa2lVcdmVersion mvTa2lVcdmVersion : mvVCDMVersions) {
      VCDMA2LFileDetail vcdma2lFileDetail = new VCDMA2LFileDetail();
      vcdma2lFileDetail.setOriginalFileName(mvTa2lVcdmVersion.getOriginalFile());
      Date date = ApicUtil.timestamp2Date(mvTa2lVcdmVersion.getOriginalDate(), TimeZone.getDefault().getID());
      vcdma2lFileDetail.setOriginalDate(DateFormat.formatDateToString(date, DateFormat.DATE_FORMAT_15));
      vcdma2lFileDetail.setPst(mvTa2lVcdmVersion.getPst());
      vcdma2lFileDetails.add(vcdma2lFileDetail);
    }

    getLogger().debug("VCDMVersions fetched from database: " + size + " for the a2lFileCheckSum " + a2lFileCheckSum);

    return vcdma2lFileDetails;
  }

  /**
   * This method returns a2l files mapped to sdom pver of specifc pidc version
   *
   * @param pVERName PVER name
   * @param pidcVersID PIDC ID
   * @return pidc version- sdom pver's A2Lfiles
   */
  public synchronized SortedSet<A2LFile> getMappedA2LFiles(final String pVERName, final Long pidcVersID) {
    // TODO : Move this method to PidcVersion class

    // load all the a2l files
    final SortedSet<A2LFile> mappedA2lFiles = new TreeSet<A2LFile>();

    getLogger().debug(
        "fetching mapped A2LFiles from database for PVER : " + pVERName + ", pidvVersion " + pidcVersID + " ...");

    final List<TPidcA2l> dbA2LFiles = getEntityProvider().getDbPIDCVersion(pidcVersID).getTabvPidcA2ls();

    Long a2lFileID;

    for (TPidcA2l dbA2LFile : dbA2LFiles) {

      a2lFileID = dbA2LFile.getMvTa2lFileinfo().getId();
      A2LFile mappedA2lFile = getDataCache().getMappedA2LFilesMap().get(dbA2LFile.getPidcA2lId());
      if (mappedA2lFile == null) {
        mappedA2lFile = new A2LFile(this.dataProvider, a2lFileID);
        PIDCA2l pidcA2l = new PIDCA2l(this.dataProvider, dbA2LFile.getPidcA2lId());
        mappedA2lFile.setPidcA2l(pidcA2l);
        getDataCache().getMappedA2LFilesMap().put(pidcA2l.getID(), mappedA2lFile);
      }
      if (dbA2LFile.getSdomPverName().equals(pVERName)) {
        mappedA2lFiles.add(mappedA2lFile);
      }
    }

    getLogger().debug("Mapped A2L files fetched from database: " + mappedA2lFiles.size());
    return mappedA2lFiles;
  }

  /**
   * icdm-253
   *
   * @param nodeID primary key
   * @return Node access rights
   */
  public synchronized SortedSet<NodeAccessRight> getNodeAccessRights(final Long nodeID) {
    final SortedSet<NodeAccessRight> resultSet = new TreeSet<NodeAccessRight>();
    final TypedQuery<TabvApicNodeAccess> query = getEntityProvider().getEm().createQuery(
        "SELECT nacc FROM TabvApicNodeAccess nacc where nacc.nodeId = " + nodeID, TabvApicNodeAccess.class);


    final List<TabvApicNodeAccess> dbNodeAccesRights = query.getResultList();

    Long nodeAccessID;

    for (TabvApicNodeAccess dbNodeAccessRight : dbNodeAccesRights) {
      nodeAccessID = Long.valueOf(dbNodeAccessRight.getNodeaccessId());
      NodeAccessRight nodeRights = getDataCache().getAllNodeAccRights().get(nodeAccessID);
      if (nodeRights == null) {
        nodeRights = new NodeAccessRight(this.dataProvider, nodeAccessID);
      }

      resultSet.add(nodeRights);
    }

    getLogger().debug("access rights fetched from database: " + resultSet.size());

    return resultSet;
  }


  /**
   * Icdm-346 Fetch User specific Node Access
   */
  private void fetchUserNodeAccess() {
    getLogger().debug("fetching access rights for current user ...");
    final Long userID = this.dataProvider.getCurrentUser().getUserID();
    final TypedQuery<TabvApicNodeAccess> qDbNodeAcc = getEntityProvider().getEm().createQuery(
        "SELECT nacc FROM TabvApicNodeAccess nacc where nacc.tabvApicUser.userId= " + userID, TabvApicNodeAccess.class);

    final List<TabvApicNodeAccess> dbNodeAcc = qDbNodeAcc.getResultList();
    Long nodeID;

    for (TabvApicNodeAccess dbNodeAccessRight : dbNodeAcc) {
      nodeID = Long.valueOf(dbNodeAccessRight.getNodeId());
      if (!getDataCache().getUserNodeAccRights().containsKey(nodeID)) {
        getDataCache().getUserNodeAccRights().put(nodeID,
            new NodeAccessRight(this.dataProvider, dbNodeAccessRight.getNodeaccessId()));
      }
    }

    getLogger().debug("access rights for current user : " + getDataCache().getUserNodeAccRights().size());

  }


  /**
   * Checks if the attribute is a structure attribute, if so the structure is updated.
   *
   * @param attrID attribute ID
   */
  protected final void fetchPidcStructure(final Long attrID) {
    for (Attribute attr : getDataCache().getPidcStructAttrMap().values()) {
      if (attrID.equals(attr.getAttributeID())) {
        ConcurrentMap<Long, Set<AttributeValue>> lvlAttrValMap = new ConcurrentHashMap<>();
        lvlAttrValMap.put(attrID, new HashSet<AttributeValue>(attr.getAttrValues()));
        getDataCache().getPidcRootNode().addNodeChildren(
            getPidcNodeChildren(ApicConstants.PIDC_ROOT_LEVEL + 1, getDataCache().getPidcRootNode(), lvlAttrValMap));
        break;
      }
    }
  }

  /**
   * fetching all usecase groups
   *
   * @param changedDataMap change data map, if invoked from DCN cache refresher
   */
  protected void fetchUCGroups(final Map<Long, ChangedData> changedDataMap) {
    getLogger().debug("fetching use case items from database ...");

    final TypedQuery<TabvUseCaseGroup> query =
        getEntityProvider().getEm().createQuery("select ucg from TabvUseCaseGroup ucg", TabvUseCaseGroup.class);
    final List<TabvUseCaseGroup> ucGrpList = query.getResultList();
    UseCaseGroup ucg;
    Long groupID;
    for (TabvUseCaseGroup dbUCGrup : ucGrpList) {
      groupID = dbUCGrup.getGroupId();
      ucg = getDataCache().getAllUseCaseGroupMap().get(groupID);
      if (ucg != null) {
        continue;
      }
      ucg = new UseCaseGroup(this.dataProvider, dbUCGrup.getGroupId());
      getDataCache().getAllUseCaseGroupMap().put(dbUCGrup.getGroupId(), ucg);
      if (changedDataMap != null) {
        changedDataMap.put(groupID,
            new ChangedData(ChangeType.INSERT, groupID, TabvUseCaseGroup.class, DisplayEventSource.DATABASE));
      }
      if (dbUCGrup.getTabvUseCaseGroup() == null) {
        getDataCache().getTopLevelUCGSet().add(ucg);
      }

      if (dbUCGrup.getTabvUseCases() != null) {
        fetchUseCases(dbUCGrup.getTabvUseCases());
      }

    }
    getLogger().debug("use case groups fetched from database: " + getDataCache().getAllUseCaseGroupMap().size());
    getLogger().debug("top level use case groups: " + getDataCache().getTopLevelUCGSet().size());
    getLogger().debug("use cases: " + getDataCache().getAllUseCaseMap().size());

  }


  /**
   * Fetching all usecases
   *
   * @param tabvUseCases
   */
  private void fetchUseCases(final List<TabvUseCase> tabvUseCases) {
    for (TabvUseCase ucase : tabvUseCases) {
      getDataCache().getAllUseCaseMap().put(ucase.getUseCaseId(), new UseCase(this.dataProvider, ucase.getUseCaseId()));
    }
  }

  /**
   * Fetch the Icdm files added to the given node ID and type
   *
   * @param nodeID node ID
   * @param nodeType type of node
   * @return the map of ICDM files
   */
  public Map<Long, IcdmFile> fetchIcdmFiles(final Long nodeID, final IcdmFile.NodeType nodeType) {

    final String query = "SELECT icfle from TabvIcdmFile icfle where icfle.nodeid = '" + nodeID +
        "' and icfle.nodeType = '" + nodeType.getDbNodeType() + "'";

    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    final TypedQuery<TabvIcdmFile> typeQuery = entMgr.createQuery(query, TabvIcdmFile.class);
    final List<TabvIcdmFile> fileList = typeQuery.getResultList();

    IcdmFile file;

    for (TabvIcdmFile dbFile : fileList) {
      file = this.dataProvider.getDataCache().getIcdmFile(nodeID, dbFile.getFileId());
      if (file == null) {
        new IcdmFile(this.dataProvider, dbFile.getFileId());
      }

    }

    return this.dataProvider.getDataCache().getIcdmFilesOfNode(nodeID);

  }

  /**
   * Fetch all features for the feature id list
   *
   * @param featureIdList List of feature id's
   * @return sorted set of Feature
   */
  protected Map<Long, Feature> fetchFeatures(final List<Long> featureIdList) {

    final Map<Long, Feature> featureSet = new ConcurrentHashMap<Long, Feature>();
    // Empty list would cause error in executing query
    if ((featureIdList == null) || featureIdList.isEmpty()) {
      return featureSet;
    }

    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();

    final TypedQuery<TSsdFeature> typeQuery = entMgr.createQuery(
        "SELECT feature from TSsdFeature feature where feature.featureId IN :featureIDs ", TSsdFeature.class);
    typeQuery.setHint(ApicConstants.READ_ONLY, "true");
    typeQuery.setHint(ApicConstants.FETCH_SIZE, "2000");
    typeQuery.setHint(ApicConstants.SHARED_CACHE, "true");
    typeQuery.setParameter("featureIDs", featureIdList);

    final List<TSsdFeature> resultList = typeQuery.getResultList();
    Feature featureObj;
    for (TSsdFeature dbFeature : resultList) {
      featureObj = new Feature(this.dataProvider, dbFeature.getFeatureId());
      featureSet.put(dbFeature.getFeatureId(), featureObj);
    }
    return featureSet;
  }


  /**
   * Fetch all Parameter NAMES of this function or with function version
   *
   * @param funcName function name
   * @param version version
   * @return Set of parameter names
   */
  protected SortedSet<String> fetchParameterNames(final String funcName, final String version) {

    String query = CommonUtils.concatenate(
        "SELECT DISTINCT(funcVer.defcharname) from TFunctionversion funcVer where funcVer.funcname = '", funcName,
        '\'');
    if (version != null) {
      query = CommonUtils.concatenate(query, " and funcVer.funcversion = '", version, '\'');
    }

    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    final TypedQuery<String> typeQuery = entMgr.createQuery(query, String.class);
    final List<String> paramList = typeQuery.getResultList();

    return new TreeSet<String>(paramList);
  }

  /**
   * @param selAPRJName selcted aprjname
   * @return set of PID Cards
   */
  public Set<PIDCVersion> getAprjPIDCs(final String selAPRJName) {


    final String query = CommonUtils.concatenate(
        "SELECT prjattr.tPidcVersion.pidcVersId from TabvProjectAttr prjattr,TabvAttribute attr,TabvAttrValue val,TabvProjectidcard proj where ",
        "prjattr.tabvAttribute.attrId=attr.attrId and prjattr.tabvAttrValue.valueId=val.valueId and attr.attrId=val.tabvAttribute.attrId and ",
        "attr.attrLevel=-20 and (UPPER(val.textvalueEng)='", selAPRJName.toUpperCase(Locale.getDefault()),
        "' or UPPER(val.textvalueGer)='", selAPRJName.toUpperCase(Locale.getDefault()),
        "') and prjattr.tPidcVersion.tabvProjectidcard.projectId = proj.projectId and proj.proRevId = prjattr.tPidcVersion.proRevId");


    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    final TypedQuery<Long> typeQuery = entMgr.createQuery(query, Long.class);

    final List<Long> resultList = typeQuery.getResultList();

    final Set<PIDCVersion> pidCards = new HashSet<PIDCVersion>();
    if (!resultList.isEmpty()) {
      PIDCVersion pidcVer;
      for (Long pidID : resultList) {
        pidcVer = getDataCache().getPidcVersion(pidID);
        pidCards.add(pidcVer);
      }
    }

    return pidCards;
  }

  /**
   * Fetches the PIDC change history // iCDM-672
   *
   * @param fromVersion : version
   * @param pidcVersId : project id card version id
   * @param pidcId :pidcId
   * @param fromVersionWeb
   * @param attrID
   * @return Sorted set of PidcChangeHistory objects
   */
  protected SortedSet<PidcChangeHistory> fetchPIDCChangeHistory(final int fromVersion, final long pidcVersId,
      final long pidcId, final boolean fromVersionWeb, final Set<Long> attrID) {
    final SortedSet<PidcChangeHistory> pidcHistoryResultSet = new TreeSet<PidcChangeHistory>();
    String query;
    // From Old Request
    if ((pidcVersId <= 0) && !fromVersionWeb) {
      query = CommonUtils.concatenate("SELECT hist from TPidcChangeHistory hist where  hist.pidcId  = '", pidcId,
          "' and hist.pidcVersion >= ", fromVersion - 1);
    }
    // New Request with Version ID.
    else if (fromVersionWeb && (pidcVersId > 0)) {
      query = CommonUtils.concatenate(
          "SELECT hist from TPidcChangeHistory hist where hist.pidcVerVersion is not null and hist.pidcVersId  = '",
          pidcVersId, "' and hist.pidcVerVersion >= ", fromVersion - 1);
    }
    // New Request without Version ID
    else {
      query = CommonUtils.concatenate(
          "SELECT hist from TPidcChangeHistory hist where hist.pidcVerVersion is not null and hist.pidcId  = '", pidcId,
          "' and hist.pidcVersion >= ", fromVersion - 1);
    }

    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();

    final TypedQuery<TPidcChangeHistory> typeQuery = entMgr.createQuery(query, TPidcChangeHistory.class);
    typeQuery.setHint(ApicConstants.READ_ONLY, "true");
    typeQuery.setHint(ApicConstants.FETCH_SIZE, "2000");
    final List<TPidcChangeHistory> resultList = typeQuery.getResultList();
    PidcChangeHistory pidcChangeHist;
    for (TPidcChangeHistory dbHist : resultList) {
      if (CommonUtils.isNotEmpty(attrID)) {
        if (attrID.contains(dbHist.getAttrId()) || attrID.contains(dbHist.getVarId()) ||
            attrID.contains(dbHist.getSvarId())) {
          continue;
        }
      }
      pidcChangeHist = new PidcChangeHistory(dbHist);
      pidcHistoryResultSet.add(pidcChangeHist);
    }
    return pidcHistoryResultSet;
  }

  /**
   * ICDM-740 to check whether the level attrs value is used or not
   *
   * @param selectedVals value of the attr
   * @return boolean
   */
  protected boolean getAttrValState(final List<AttributeValue> selectedVals) {
    boolean attrUsedFlag = false;
    List<Long> valueIds = new ArrayList<Long>();
    for (AttributeValue val : selectedVals) {
      valueIds.add(val.getValueID());
    }
    String query = "SELECT val.prjAttrId from TabvProjectAttr val where val.tabvAttrValue.valueId in :valIDs";
    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();

    final TypedQuery<Long> typeQuery = entMgr.createQuery(query, Long.class);
    typeQuery.setParameter("valIDs", valueIds);
    if (!typeQuery.setMaxResults(1).getResultList().isEmpty()) {
      attrUsedFlag = true;
    }
    return attrUsedFlag;
  }

  /**
   * iCDM-756 <br>
   * Gets the SDOM PVer object for the sdom name and variant
   *
   * @param sdomPverName sdom name
   * @param variant variant name
   * @return SDOMPver set
   */
  protected SortedSet<Long> getPVERVarRevisions(final String sdomPverName, final String variant) {
    getLogger().debug("fetching PVER details for sdom pver name " + sdomPverName + " and variant " + variant + "...");
    // build query for fetching revisions for pvername and variant
    final String query =
        "select DISTINCT(pver.revision) from MvSdomPver pver where upper(pver.elName) = ?1 and upper(pver.variante) = ?2";
    // fetch the data
    final TypedQuery<Long> typeQuery = getEntityProvider().getEm().createQuery(query, Long.class);
    // ICDM-2511
    typeQuery.setParameter(QUERY_PARAM_1, sdomPverName.toUpperCase(Locale.getDefault()));
    typeQuery.setParameter(QUERY_PARAM_2, variant.toUpperCase(Locale.getDefault()));
    typeQuery.setHint(ApicConstants.READ_ONLY, "true");
    // Get the results
    final List<Long> resultList = typeQuery.getResultList();
    SortedSet<Long> pVerVarRevs = new TreeSet<Long>();
    for (Long resObj : resultList) {
      pVerVarRevs.add(resObj);
    }
    getLogger().debug("No of revisions available for SDOM Pver " + sdomPverName + " and variant" + variant + " is " +
        pVerVarRevs.size());
    return pVerVarRevs;
  }

  // ICDM-1456
  /**
   * Check whether the sdom pver name already exists
   *
   * @param sdomPverName sdom pver name
   * @return true if sdomname exists in MVSdomPver
   */
  protected boolean checkPverNameExists(final String sdomPverName) {
    getLogger().debug("fetching PVER details for sdom pver name " + sdomPverName);
    // build query for fetching the sdompver name
    final String query = "select pver.elName from MvSdomPver pver where upper(pver.elName) = ?1";
    // fetch the data
    final TypedQuery<Long> typeQuery = getEntityProvider().getEm().createQuery(query, Long.class);
    typeQuery.setParameter(1, sdomPverName.toUpperCase(Locale.getDefault()));
    typeQuery.setHint(ApicConstants.READ_ONLY, "true");
    // Get the results
    final List<Long> resultList = typeQuery.getResultList();
    return !resultList.isEmpty();
  }

  /**
   * iCDM-756 <br>
   * Fetches PVer variants for the SDOM PVer name
   *
   * @param sdomPverName sdom name
   * @return SDOMPver object
   */
  protected SortedSet<String> getPVERVariants(final String sdomPverName) {
    getLogger().debug("fetching PVER details for sdom pver name '" + sdomPverName + "' ...");
    // build query for fetching variants for pvername
    // ICDM-2511
    final String query = "select DISTINCT(pver.variante) from MvSdomPver pver where upper(pver.elName) = ?1";
    // fetch the data
    final TypedQuery<String> typeQuery = getEntityProvider().getEm().createQuery(query, String.class);
    typeQuery.setParameter(1, sdomPverName.toUpperCase(Locale.getDefault()));
    typeQuery.setHint(ApicConstants.READ_ONLY, "true");
    // Get the results
    final List<String> resultList = typeQuery.getResultList();
    SortedSet<String> pVerVaraints = new TreeSet<String>();
    for (String resObj : resultList) {
      pVerVaraints.add(resObj);
    }
    getLogger().debug("No of variants available for SDOM Pver " + sdomPverName + "-" + pVerVaraints.size());
    return pVerVaraints;
  }

  // ICDM-1456
  /**
   * Fetches PVer variants for the SDOM PVer name
   *
   * @param sdomPverName sdom name
   * @return SDOMPver object
   */
  protected SortedSet<String> getTA2LFilePVERVars(final String sdomPverName) {
    getLogger().debug("fetching PVER details for sdom pver name from MvTa2lFileinfo '" + sdomPverName + "' ...");
    // build query for fetching variants for pvername
    final String query =
        "select DISTINCT(a2l.sdomPverVariant) from MvTa2lFileinfo a2l where upper(a2l.sdomPverName) = ?1 and a2l.sdomPverVariant is not null";
    // fetch the data
    final TypedQuery<String> typeQuery = getEntityProvider().getEm().createQuery(query, String.class);
    // ICDM-2511
    typeQuery.setParameter(1, sdomPverName.toUpperCase(Locale.getDefault()));
    typeQuery.setHint(ApicConstants.READ_ONLY, "true");
    // Get the results
    final List<String> resultList = typeQuery.getResultList();
    SortedSet<String> pVerVaraints = new TreeSet<String>();
    pVerVaraints.addAll(resultList);

    getLogger().debug(
        "No of variants available for SDOM Pver from MvTa2lFileinfo " + sdomPverName + "-" + pVerVaraints.size());
    return pVerVaraints;
  }

  /**
   * Get the pVER id for the sdomPver name, variant, revision
   *
   * @param sdomPverName sdom pver name
   * @param variant variant name
   * @param revision revision
   * @return Long sdom pver vers ID
   */
  protected Long getPVERId(final String sdomPverName, final String variant, final Long revision) {
    getLogger().debug("fetching PVER ID for sdom pver name " + sdomPverName + " and variant " + variant +
        " and revision " + revision + "...");
    // build query for fetching pver id for pvername and variant and revision
    final String query =
        "select pver.versNummer from MvSdomPver pver where upper(pver.elName) = ?1 and upper(pver.variante) = ?2 and pver.revision = '" +
            revision + "'";
    // fetch the data
    final TypedQuery<Long> typeQuery = getEntityProvider().getEm().createQuery(query, Long.class);
    // ICDM-2511
    typeQuery.setParameter(QUERY_PARAM_1, sdomPverName.toUpperCase(Locale.getDefault()));
    typeQuery.setParameter(QUERY_PARAM_2, variant.toUpperCase(Locale.getDefault()));
    typeQuery.setHint(ApicConstants.READ_ONLY, "true");
    // Get the results
    final List<Long> resultList = typeQuery.getResultList();
    Long pverID = 0L;
    if ((resultList != null) && !resultList.isEmpty()) {
      pverID = resultList.get(0);
    }
    getLogger().debug("Sdom pver id available for SDOM Pver " + sdomPverName + " and variant" + variant +
        " and revision " + revision + " is " + pverID);
    return pverID;
  }

  /**
   * Fetch the links mapped to the given node ID
   *
   * @param nodeID node ID
   * @param nodeType Object type in string
   * @return the map of links
   */
  public ConcurrentMap<Long, Link> fetchNodeLinks(final Long nodeID, final String nodeType) {

    final String query =
        "SELECT link from TLink link where link.nodeId = '" + nodeID + "' and upper(link.nodeType) = ?1";

    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    final TypedQuery<TLink> typeQuery = entMgr.createQuery(query, TLink.class);
    // ICDM-2511
    typeQuery.setParameter(1, nodeType.toUpperCase(Locale.getDefault()));
    final List<TLink> linkList = typeQuery.getResultList();

    ConcurrentMap<Long, Link> linkMap = new ConcurrentHashMap<Long, Link>();

    for (TLink dbLink : linkList) {
      linkMap.put(dbLink.getLinkId(), new Link(this.dataProvider, dbLink.getLinkId()));
    }

    return linkMap;

  }

  /**
   * ICDM-959 fetch the nodes which has links
   *
   * @return map which contains nodetype and corresponding id's which has links
   */
  public ConcurrentMap<String, Set<Long>> fetchLinkNodes() {

    getLogger().debug("fetching nodes which has links...");

    final Query qDbSuperGroups = getEntityProvider().getEm()
        .createQuery("SELECT link.nodeType,link.nodeId FROM TLink link group by link.nodeType,link.nodeId");
    @SuppressWarnings("unchecked")
    final List<Object[]> resultList = qDbSuperGroups.getResultList();
    ConcurrentMap<String, Set<Long>> nodeMap = new ConcurrentHashMap<String, Set<Long>>();
    String nodeType;
    Long nodeID;
    for (Object[] resObj : resultList) {
      nodeType = (String) resObj[0];
      nodeID = (Long) resObj[1];
      if (nodeMap.get(nodeType) == null) {
        Set<Long> newTypeSet = new TreeSet<Long>();
        newTypeSet.add(nodeID);
        nodeMap.put(nodeType, newTypeSet);
      }
      else {
        Set<Long> typeSet = nodeMap.get(nodeType);
        typeSet.add(nodeID);
        nodeMap.put(nodeType, typeSet);
      }

    }
    return nodeMap;
  }


  /**
   * @param a2lFileId a2l file id
   * @return A2LFile
   */
  public A2LFile getA2LInfo(final Long a2lFileId) {
    // build query to ckeck a2l file info already exists
    final String query = "select a2l from MvTa2lFileinfo a2l where a2l.id= '" + a2lFileId + "'";
    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    final TypedQuery<MvTa2lFileinfo> typeQuery = entMgr.createQuery(query, MvTa2lFileinfo.class);
    final List<MvTa2lFileinfo> linkList = typeQuery.getResultList();
    A2LFile a2lFile = null;
    // Build BO for the result set
    if ((linkList != null) && !linkList.isEmpty()) {
      MvTa2lFileinfo dbA2l = linkList.get(0);
      a2lFile = new A2LFile(this.dataProvider, dbA2l.getId());
    }
    return a2lFile;
  }

  /**
   * Returns the DSTS using the same SDOM PVER VersionID as the vcdmA2lFileId passed as a parameter
   *
   * @param vcdmA2lFileID Long
   * @return VCDMApplicationProjects
   */
  public List<VCDMApplicationProject> getDataSets(final Long vcdmA2lFileID) {

    // the entity manager to execute the queries
    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();

    // query to fetch the contents of TA2L_FileInfo for the given vCDM A2L FileID to get the SDOM PVER VersID
    // there can be only one entry for a particular vCDM A2L FileID !
    final String qsSdomPverVersID = "select afi1 from MvTa2lFileinfo afi1 where afi1.vcdmA2lfileId = :vcdmA2lfileId";

    final TypedQuery<MvTa2lFileinfo> qSdomPverVersID = entMgr.createQuery(qsSdomPverVersID, MvTa2lFileinfo.class);
    // set the parameter
    qSdomPverVersID.setParameter("vcdmA2lfileId", BigDecimal.valueOf(vcdmA2lFileID));
    // execute the query
    final List<MvTa2lFileinfo> fileInfos = qSdomPverVersID.getResultList();

    // query to get all vCDM DST using A2L with a special SDOM PVER VersID
    final String qsDst =
        "select dsts from MvTabvCaldatafile calFiles join MvTabeDataset dsts on calFiles.mvTabeDataset = dsts " +
            "      join MvTa2lFileinfo afi on calFiles.mvTa2lFileinfo = afi" +
            " where afi.sdomPverVersid = :sdomPverVersid ";


    final TypedQuery<MvTabeDataset> qDst = entMgr.createQuery(qsDst, MvTabeDataset.class);
    // set the parameter from the previous query
    qDst.setParameter("sdomPverVersid", fileInfos.get(0).getSdomPverVersid());
    // get the DSTs
    final List<MvTabeDataset> datasets = qDst.getResultList();

    return convertDSTSTOModel(datasets);
  }

  /**
   * Returns the DSTS using the same SDOM PVER VersionID as the vcdmA2lFileId passed as a parameter
   *
   * @param sdomPverName pver name
   * @param sdomPverVar pver variant name
   * @param sdomPverRevision pver variant revision
   * @return VCDMApplicationProjects
   */
  public List<VCDMApplicationProject> getDataSets(final String sdomPverName, final String sdomPverVar,
      final String sdomPverRevision) {

    // the entity manager to execute the queries
    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();

    // query to fetch the contents of TA2L_FileInfo for the given vCDM A2L FileID to get the SDOM PVER VersID
    // there can be only one entry for a particular vCDM A2L FileID !
    final String qsSdomPverVersID =
        "select afi1 from MvTa2lFileinfo afi1 where afi1.sdomPverName = :sdomPverName and afi1.sdomPverVariant= :sdomPverVariant and afi1.sdomPverRevision= :sdomPverRevision";

    final TypedQuery<MvTa2lFileinfo> qSdomPverVersID = entMgr.createQuery(qsSdomPverVersID, MvTa2lFileinfo.class);
    // set the parameter
    qSdomPverVersID.setParameter("sdomPverName", sdomPverName);
    qSdomPverVersID.setParameter("sdomPverVariant", sdomPverVar);
    qSdomPverVersID.setParameter("sdomPverRevision", Long.valueOf(sdomPverRevision));
    // execute the query
    final List<MvTa2lFileinfo> fileInfos = qSdomPverVersID.getResultList();

    // query to get all vCDM DST using A2L with a special SDOM PVER VersID
    final String qsDst =
        "select dsts from MvTabvCaldatafile calFiles join MvTabeDataset dsts on calFiles.mvTabeDataset = dsts " +
            "      join MvTa2lFileinfo afi on calFiles.mvTa2lFileinfo = afi" +
            " where afi.sdomPverVersid = :sdomPverVersid ";

    if (!fileInfos.isEmpty()) {
      final TypedQuery<MvTabeDataset> qDst = entMgr.createQuery(qsDst, MvTabeDataset.class);
      // set the parameter from the previous query
      qDst.setParameter("sdomPverVersid", fileInfos.get(0).getSdomPverVersid());
      // get the DSTs
      List<MvTabeDataset> datasets = qDst.getResultList();
      return convertDSTSTOModel(datasets);
    }
    return null;
  }


  /**
   * @param datasets MvTabeDataset
   * @return VCDMApplicationProjects
   */
  private List<VCDMApplicationProject> convertDSTSTOModel(final List<MvTabeDataset> datasets) {
    Map<String, VCDMApplicationProject> aprjMap = new ConcurrentHashMap<>();

    for (MvTabeDataset mvTabeDataset : datasets) {

      VCDMApplicationProject vcdmApplicationProject = aprjMap.get(mvTabeDataset.getElementName());
      if (vcdmApplicationProject == null) {
        vcdmApplicationProject = new VCDMApplicationProject();
        vcdmApplicationProject.setAprjName(mvTabeDataset.getElementName());
        aprjMap.put(mvTabeDataset.getElementName(), vcdmApplicationProject);
      }

      VCDMProductKey vcdmVariant = vcdmApplicationProject.getVcdmVariants().get(mvTabeDataset.getProdKey());
      if (vcdmVariant == null) {
        vcdmVariant = new VCDMProductKey();
        vcdmVariant.setVariantName(mvTabeDataset.getProdKey());
        vcdmApplicationProject.getVcdmVariants().put(mvTabeDataset.getProdKey(), vcdmVariant);
      }

      VCDMProgramkey vcdmProgramkey = vcdmVariant.getProgramKeys().get(mvTabeDataset.getProgKey());
      if (vcdmProgramkey == null) {
        vcdmProgramkey = new VCDMProgramkey();
        vcdmProgramkey.setProgramKeyName(mvTabeDataset.getProgKey());
        vcdmVariant.getProgramKeys().put(mvTabeDataset.getProgKey(), vcdmProgramkey);
      }

      VCDMDSTRevision vcdmdstRevision = vcdmProgramkey.getvCDMDSTRevisions().get(mvTabeDataset.getRevision());
      if (vcdmdstRevision == null) {
        vcdmdstRevision = new VCDMDSTRevision();
        vcdmdstRevision.setDstID(mvTabeDataset.getEaseedstId());
        vcdmdstRevision.setRevisionNo(mvTabeDataset.getRevision());
        vcdmdstRevision.setCreatedDate(mvTabeDataset.getCreDate());
        vcdmdstRevision.setVersionName(mvTabeDataset.getVersionName());
        vcdmProgramkey.getvCDMDSTRevisions().put(mvTabeDataset.getRevision(), vcdmdstRevision);
      }
    }
    return new ArrayList<>(aprjMap.values());
  }

  /**
   * @param variant PIDCVariant
   * @return true if variant is used in some other pidc
   */
  public boolean isVarNameUsed(final PIDCVariant variant) {

    final String query = "SELECT var.variantId from TabvProjectVariant var where var.tabvAttrValue.valueId = '" +
        variant.getNameValue().getValueID() + "' and var.variantId != '" + variant.getVariantID() + "'";

    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    final TypedQuery<TabvProjectVariant> typeQuery = entMgr.createQuery(query, TabvProjectVariant.class);
    final List<TabvProjectVariant> varList = typeQuery.setMaxResults(1).getResultList();

    return !varList.isEmpty();

  }

  /**
   * @param subVariant PIDCSubVariant
   * @return true if variant is used in some other pidc
   */
  public boolean isSubVarNameUsed(final PIDCSubVariant subVariant) {

    final String query = "SELECT var.subVariantId from TabvProjectSubVariant var where var.tabvAttrValue.valueId = '" +
        subVariant.getNameValue().getValueID() + "' and var.subVariantId != '" + subVariant.getSubVariantID() + "'";

    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    final TypedQuery<TabvProjectSubVariant> typeQuery = entMgr.createQuery(query, TabvProjectSubVariant.class);
    final List<TabvProjectSubVariant> subVarList = typeQuery.setMaxResults(1).getResultList();

    return !subVarList.isEmpty();

  }


  /**
   * iCDM-890 <br>
   * Fetch the PIDC names with the owners - for which the attribute value is used in ProjectId card
   *
   * @param valueId AttrValue ID to search
   * @return Map of Owner and PIDC names in which the valID is used
   */
  protected Map<String, Map<String, Map<String, Long>>> getPidcUsersUsingAttrValue(final Long valueId) {
    // Get of PIDC names along with owners where the attr value_id is used
    final String query =
        " select au.username,vrsn.project_id,vrsn.vers_name,vrsn.pidc_vers_id FROM tabv_apic_node_access na , tabv_apic_users au , " +
            " (select distinct(pa.pidc_vers_id) from tabv_project_attr pa where ( pa.value_id =  ?  ) " + " union " +
            " select distinct(va.pidc_vers_id) from tabv_variants_attr va where ( va.value_id = ? ) " + " union " +
            " select distinct(sa.pidc_vers_id) from tabv_proj_sub_variants_attr sa where (sa.value_id = ?  )) pidcVrsn, t_pidc_version vrsn" +
            " where vrsn.pidc_vers_id =  pidcVrsn.pidc_vers_id  " +
            " and na.node_id = vrsn.project_id  and na.owner = 'Y' " + " and na.user_id = au.user_id ";

    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    final Query nativeQuery = entMgr.createNativeQuery(query);
    nativeQuery.setParameter(1, valueId);
    nativeQuery.setParameter(2, valueId);
    nativeQuery.setParameter(3, valueId);

    @SuppressWarnings("unchecked")
    final List<Object[]> resultList = nativeQuery.getResultList();

    // Collect the pidcs and users
    Map<String, Map<String, Map<String, Long>>> userPidcMap = new ConcurrentHashMap<>();
    String userName, pidcVersName;
    Long pid;
    Long pidcVerId;
    for (Object[] resObj : resultList) {
      // first obj is user name
      userName = (String) resObj[ApicConstants.COLUMN_INDEX_0];
      // second obj is pidc id
      pid = ((BigDecimal) resObj[ApicConstants.COLUMN_INDEX_1]).longValue();
      // third obj is pidc version name
      pidcVersName = (String) resObj[ApicConstants.COLUMN_INDEX_2];
      // fourth object is PIDC Version Id
      pidcVerId = ((BigDecimal) resObj[ApicConstants.COLUMN_INDEX_3]).longValue();
      PIDCard pidCard = getDataCache().getPidc(pid);
      if (pidCard != null) {
        if (userPidcMap.get(userName) == null) {
          ConcurrentMap<String, Map<String, Long>> newPidcMap = new ConcurrentHashMap<>();
          ConcurrentMap<String, Long> pidcVerNamIdMap = new ConcurrentHashMap<>();
          pidcVerNamIdMap.put(pidcVersName, pidcVerId);
          newPidcMap.put(pidCard.getName(), pidcVerNamIdMap);
          userPidcMap.put(userName, newPidcMap);
        }
        else {
          Map<String, Map<String, Long>> currUserPidcMap = userPidcMap.get(userName);
          Map<String, Long> pidcVerNameIdMap = currUserPidcMap.get(pidCard.getName());
          addToPidcVerMap(pidcVersName, pidcVerId, pidCard, currUserPidcMap, pidcVerNameIdMap);
        }
      }
    }
    return userPidcMap;
  }

  /**
   * @param pidcVersName
   * @param pidcVerId
   * @param pidCard
   * @param currUserPidcMap
   * @param pidcVerNameIdMap
   */
  private void addToPidcVerMap(final String pidcVersName, final Long pidcVerId, final PIDCard pidCard,
      final Map<String, Map<String, Long>> currUserPidcMap, final Map<String, Long> pidcVerNameIdMap) {
    if (pidcVerNameIdMap == null) {
      ConcurrentMap<String, Long> pidcVerNameId = new ConcurrentHashMap<>();
      pidcVerNameId.put(pidcVersName, pidcVerId);
      currUserPidcMap.put(pidCard.getName(), pidcVerNameId);
    }
    else {
      pidcVerNameIdMap.put(pidcVersName, pidcVerId);
    }
  }

  /**
   * @return the logger
   */
  private ILoggerAdapter getLogger() {
    return this.dataProvider.getLogger();
  }


  /**
   * @return the data Cache
   */
  private DataCache getDataCache() {
    return this.dataProvider.getDataCache();
  }


  /**
   * @return the Entity Provider
   */
  private EntityProvider getEntityProvider() {
    return this.dataProvider.getEntityProvider();
  }

  /**
   * @return the feature map
   */
  protected ConcurrentMap<Long, Feature> fetchAllFeatures() {

    getLogger().debug("fetching All Features from database ...");

    final ConcurrentMap<Long, Feature> featureMap = new ConcurrentHashMap<Long, Feature>();

    final TypedQuery<TSsdFeature> query =
        getEntityProvider().getEm().createQuery("SELECT feature FROM TSsdFeature feature", TSsdFeature.class);
    final List<TSsdFeature> dbFtreList = query.getResultList();
    for (TSsdFeature dbFeature : dbFtreList) {
      if (dbFeature.getTabvAttribute() != null) {
        final Feature feature = new Feature(this.dataProvider, dbFeature.getFeatureId());
        featureMap.put(dbFeature.getFeatureId(), feature);
        // Icdm-1066 - add the Obj to the new map
        this.dataProvider.getDataCache().getAttrFeaMap().put(feature.getAttributeID(), feature);
      }
    }

    // Load the feature values, by fetching the values of the first feature in the map
    for (Entry<Long, Feature> entry : featureMap.entrySet()) {
      Feature feature = entry.getValue();
      feature.getFeatureValues();
      break;
    }

    getLogger().debug("Features fetched from database: " + featureMap.size() +
        this.dataProvider.getDataCache().getAttrFeaMap().size());

    return featureMap;
  }

  /**
   * @return all the pidc version available in tabcProjAttr Table
   */
  public List<PIDCVersion> getAllPIDCVersions() {

    Set<PIDCVersion> retSet = new HashSet<>();
    ConcurrentMap<Long, PIDCard> newPidcMap = new ConcurrentHashMap<>();

    TypedQuery<TPidcVersion> versQry =
        getEntityProvider().getEm().createNamedQuery(TPidcVersion.NQ_GET_ALL_PIDC_VERSIONS, TPidcVersion.class);
    final List<TPidcVersion> dbPidcVerList = versQry.getResultList();

    for (TPidcVersion dbPidcVers : dbPidcVerList) {
      PIDCVersion pidcVers = getDataCache().getPidcVersion(dbPidcVers.getPidcVersId());
      if (pidcVers == null) {
        pidcVers = new PIDCVersion(this.dataProvider, dbPidcVers.getPidcVersId());
        Long pidcID = dbPidcVers.getTabvProjectidcard().getProjectId();
        PIDCard pidc = getDataCache().getPidc(pidcID);
        if (pidc == null) {
          pidc = new PIDCard(this.dataProvider, pidcID, null);
          newPidcMap.put(pidcID, pidc);
        }
      }
      retSet.add(pidcVers);
    }

    // Set project structure to new project ID cards
    if (!newPidcMap.isEmpty()) {
      TypedQuery<Long[]> strAttrQry =
          getEntityProvider().getEm().createNamedQuery(TabvProjectAttr.NQ_GET_STRUCT_ATTR_WITH_PIDC, Long[].class);
      strAttrQry.setParameter("pidcIDColl", newPidcMap.keySet());
      final List<Long[]> dbProjAttrList = strAttrQry.getResultList();
      Long attrID;
      Long valID;
      Long pidcID;
      PIDCard pidc;
      for (Long[] dbStructAttrValID : dbProjAttrList) {
        attrID = dbStructAttrValID[0];
        valID = dbStructAttrValID[0];
        pidcID = dbStructAttrValID[0];

        pidc = newPidcMap.get(pidcID);
        pidc.addStructureAttrValue(attrID, valID);
        // TODO : set leaf pidc node to this pidc
        // TODO : add this pidc to correct pidc leaf node

      }
    }

    getLogger().debug("Project-ID-Card versions fetched: " + retSet.size());
    getLogger().debug("New Project-ID-Cards fetched from database: " + newPidcMap.size());

    // TODO change return type to set??
    return new ArrayList<>(retSet);

  }

  /**
   * @param useCaseId Usecase id
   * @param useCaseSectionId section id
   * @param attrId attr id
   * @param pidcVrsnId pidc version id
   * @return unique id of focus matrix table
   */
  public List<FocusMatrixDetails> getFocusMatrix(final Long useCaseId, final Long useCaseSectionId, final Long attrId,
      final Long pidcVrsnId) {
    List<FocusMatrixDetails> resultList = new ArrayList<>();
    StringBuilder strQuery = new StringBuilder(STRING_QUERY_SIZE);

    if (null != useCaseSectionId) {
      strQuery = strQuery.append("SELECT fm FROM TFocusMatrix fm where  fm.tabvUseCaseSection.sectionId = ")
          .append(useCaseSectionId);
    }
    else if (null != useCaseId) {
      strQuery = strQuery.append("SELECT fm FROM TFocusMatrix fm where  fm.tabvUseCas.useCaseId = ").append(useCaseId);
    }
    else {
      return resultList;
    }
    if (null != attrId) {
      strQuery.append(" and fm.tabvAttribute.attrId =").append(attrId);
    }
    if (null != pidcVrsnId) {
      strQuery.append(" and fm.tPidcVersion.pidcVersId =").append(pidcVrsnId);
    }

    TypedQuery<TFocusMatrix> createQuery =
        getEntityProvider().getEm().createQuery(strQuery.toString(), TFocusMatrix.class);

    List<TFocusMatrix> fmList = createQuery.getResultList();
    for (TFocusMatrix dbFm : fmList) {
      FocusMatrixDetails fmDetails = getDataCache().getFocusMatrix(dbFm.getFmId());
      resultList.add(fmDetails);
    }
    return resultList;

  }

  /**
   * @param aprjName aprjName
   * @param timePeriod timePeriod
   * @return the list of Data sets
   * @deprecated Use VcdmDataSetLoader instead
   */
  // Icdm-1485 - New method for getting the Label Lists
  @Deprecated
  public List<VcdmDataSet> getLabelSets(final String aprjName, final int timePeriod) {
    List<VcdmDataSet> resultList = new ArrayList<>();

    String query = "Select * from TABE_DATASETS where upper(ELEMENT_NAME) =? ";
    if (timePeriod > 0) {
      query = CommonUtils.concatenate(query, "and trunc(EASEEDST_MOD_DATE) > trunc(sysdate) - ?");
    }
    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();

    final Query nativeQuery = entMgr.createNativeQuery(query, MvTabeDataset.class);
    nativeQuery.setParameter(QUERY_PARAM_1, aprjName);
    if (timePeriod > 0) {
      nativeQuery.setParameter(QUERY_PARAM_2, timePeriod);
    }


    for (MvTabeDataset mvTabeDataset : (List<MvTabeDataset>) nativeQuery.getResultList()) {
      VcdmDataSet data = new VcdmDataSet(this.dataProvider, mvTabeDataset.getEaseedstId());
      resultList.add(data);
    }
    return resultList;
  }

  /**
   * @param aprjName the aprj name attribute value id
   * @param timePeriod the timeperiod in milliseconds
   * @return the list of VcdmDatasetsWorkpkgStat
   * @deprecated Use VcdmDataSetWPStatsLoader instead
   */
  // ICDM-2469
  @Deprecated
  public List<VcdmDatasetsWorkpkgStat> getVcdmLabelStatisticsforWp(final String aprjName, final int timePeriod,
      final long easeeDstId) {
    List<VcdmDatasetsWorkpkgStat> returnVcdmDataSetList = new ArrayList<>();
    String query = "SELECT * FROM V_VCDM_DATASETS_WORKPKG_STAT where APRJ_NAME= ? ";
    // 221731
    if (easeeDstId > 0) {
      query = CommonUtils.concatenate(query, " AND EASEEDST_ID= ", easeeDstId);
    }
    if (timePeriod > 0) {
      query = CommonUtils.concatenate(query, " AND TRUNC(EASEEDST_MOD_DATE) > TRUNC(sysdate) - ? ");
    }

    // initialising the entity manager
    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();


    final Query nativeQuery = entMgr.createNativeQuery(query, MvVcdmDatasetsWorkpkgStat.class);
    nativeQuery.setParameter(QUERY_PARAM_1, aprjName);
    if (timePeriod > 0) {
      nativeQuery.setParameter(QUERY_PARAM_2, timePeriod);
    }

    @SuppressWarnings("unchecked")
    List<MvVcdmDatasetsWorkpkgStat> vcdmDatasetsWorkpkgList = nativeQuery.getResultList();
    for (MvVcdmDatasetsWorkpkgStat vcdmDatasetsWorkpkgData : vcdmDatasetsWorkpkgList) {
      VcdmDatasetsWorkpkgStat vcdmDataSetObject =
          new VcdmDatasetsWorkpkgStat(this.dataProvider, vcdmDatasetsWorkpkgData.getId());
      returnVcdmDataSetList.add(vcdmDataSetObject);
    }
    return returnVcdmDataSetList;
  }

  /**
   * Checks whether the eadm name already exists for different atttribute
   *
   * @param eadmName eadmName
   * @param modifiedDbAttrID modifiedDbAttrID
   * @return true if name exists
   */
  // ICDM-1560
  public boolean checkEADMNameExists(final String eadmName, final Long modifiedDbAttrID) {
    final TypedQuery<Long[]> query =
        getEntityProvider().getEm().createNamedQuery(TabvAttribute.CHECK_EADM_NAME, Long[].class);
    query.setParameter("eadmName", eadmName);
    query.setParameter("attrID", modifiedDbAttrID);
    return !query.getResultList().isEmpty();
  }


  /**
   * Load use case attribute mappings
   */
  // ICDM-1123
  public void loadUcItemAttrMapping() {
    getLogger().debug("Loading use case sections and attribute mappings...");
    Attribute refAttr = getDataCache().getProjNameAttribute();
    for (UseCaseGroup ucg : getDataCache().getAllUseCaseGroupMap().values()) {
      ucg.isMapped(refAttr);
    }
    getLogger().debug("Loading use case sections and attribute mappings completed.");
  }

  /**
   * @param a2lFileID projectID
   * @param projectID projectID
   * @return the pidc A2L File
   */
  public PIDCA2l getPidcA2L(final Long a2lFileID, final Long projectID) {
    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    final Query typeQuery = entMgr.createNamedQuery(TPidcA2l.GET_PIDC_A2L, TPidcA2l.class);
    typeQuery.setParameter(QUERY_PARAM_1, a2lFileID);
    typeQuery.setParameter(QUERY_PARAM_2, projectID);
    final List<Object[]> linkList = typeQuery.getResultList();
    PIDCA2l pidcA2l = null;
    // Build BO for the result set
    if ((linkList != null) && !linkList.isEmpty()) {
      Object[] dbA2l = linkList.get(0);
      pidcA2l = new PIDCA2l(this.dataProvider, ((BigDecimal) dbA2l[0]).longValue());
    }
    return pidcA2l;
  }

  /**
   * Fetches the mail template file for Deletion of Attribute Value from Databas
   *
   * @return Mail Template
   */
  // ICDM-2217 / ICDM-2300
  public String fetchDelAttrValMailTemplate() {

    String mailTemplate = "";
    IcdmFile delAttrValMailTemplateFile = new IcdmFile(this.dataProvider,
        Long.valueOf(this.dataProvider.getParameterValue(ApicConstants.KEY_DEL_ATTRVAL_MAIL_TEMPLATE)));
    try {

      Map<String, byte[]> files = delAttrValMailTemplateFile.getFiles();
      mailTemplate = new String(files.get(ApicConstants.DEL_ATTRVAL_MAIL_TEMPLATE));
    }
    catch (IOException e) {
      getLogger().error(e.getMessage(), e);
    }
    return mailTemplate;
  }


  /**
   * @param versId the pidc version Id
   * @param valueId the value of attribute 'Division'
   * @return true if the given the pidc has already assigned the given value id
   */
  // ICDM-2379
  public boolean hasProjectAttributeUsedInReview(final Long versId, final Long valueId) {
    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
//    TypedQuery<Long> attrCheckQuery =
//        entMgr.createNamedQuery(TRvwQnaireResponse.NQ_GET_DIVISION_ATTR_VALUE_USED, Long.class);
//    attrCheckQuery.setParameter("pidcVersId", versId);
//    attrCheckQuery.setParameter("divValueId", valueId);
//
//    return attrCheckQuery.getSingleResult() > 0L;
    return false;
  }


  /**
   * @param elementID variant id
   * @return the pidc variant
   */
  public PIDCVariant getPidcVariantAndVersion(final Long elementID) {
    PIDCVariant pidcVariant = null;
    final String query =
        "SELECT projVar.tPidcVersion from TabvProjectVariant projVar where projVar.variantId = '" + elementID + "'";
    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    final TypedQuery<TPidcVersion> typeQuery = entMgr.createQuery(query, TPidcVersion.class);
    final TPidcVersion tpidcVersion = typeQuery.getSingleResult();

    if (tpidcVersion != null) {
      PIDCVersion version = new PIDCVersion(this.dataProvider, tpidcVersion.getPidcVersId());
      getDataCache().getAllPidcVersionMap().put(tpidcVersion.getPidcVersId(), version);

      pidcVariant = version.getVariantsMap().get(elementID);
    }
    return pidcVariant;
  }

  // Story 221726
  /**
   * @return alternateAttrMap
   */
  public ConcurrentHashMap<Long, Long> getAlternateAttrs() {

    ConcurrentHashMap<Long, Long> alternateAttrMap = new ConcurrentHashMap<>();
    final String query = "SELECT t FROM TAlternateAttr t";
    final EntityManager entMgr = this.dataProvider.getEntityProvider().getEm();
    final TypedQuery<TAlternateAttr> typeQuery = entMgr.createQuery(query, TAlternateAttr.class);
    final List<TAlternateAttr> tAlternateAttrList = typeQuery.getResultList();

    for (TAlternateAttr alternateAttr : tAlternateAttrList) {
      alternateAttrMap.put(alternateAttr.getAttrId().longValue(), alternateAttr.getAlternateAttrId().longValue());
    }
    return alternateAttrMap;
  }

  /**
   * Fetch the vCDM psts based on SDOM PVER name, variant and revision
   *
   * @param a2lFile A2LFile
   * @return SortedSet<vCDMPST>
   */
  public VCDMPST fetchvCDMPST(final A2LFile a2lFile) {
    try {
      getLogger().debug("fetching VCDM PSTs...");
      final TypedQuery<TvcdmPst> query =
          getEntityProvider().getEm().createNamedQuery(TvcdmPst.NQ_GET_PST_BY_A2LFILE_INFO_ID, TvcdmPst.class);
      query.setParameter("a2lInfoId", a2lFile.getID());

      List<TvcdmPst> dbPST = query.getResultList();
      if (dbPST.isEmpty()) {
        return null;
      }
      VCDMPST vcdmPST = new VCDMPST(this.dataProvider, dbPST.get(0).getPstId().longValue());
      getDataCache().getVCDMPstMap().put(a2lFile, vcdmPST);

      return vcdmPST;
    }
    catch (NoResultException e) {
      getLogger().error(e.getMessage(), e);
      return null;
    }
  }
}

