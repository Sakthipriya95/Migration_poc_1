/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.datamodel.core.cns.ICnsAsyncMessage;
import com.bosch.caltool.dmframework.bo.JPAUtils;
import com.bosch.caltool.dmframework.notification.ChangeNotificationCache;
import com.bosch.caltool.dmframework.notification.ICacheRefresher;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryHandler;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.Language;


/**
 * Class for storing the framework related objects. The store has to be initialised by invoking the method
 * <code>initialise()</code>
 *
 * @author BNE4COB
 */
public final class ObjectStore {

  /**
   * CQN is disabled
   */
  public static final int CQN_MODE_NO = 0;

  /**
   * CQN refresh is triggered from the CQN listener, and runs in the background
   */
  public static final int CQN_MODE_BKGRND = 1;

  /**
   * CQN refresh is triggered from command
   */
  public static final int CQN_MODE_COMMAND = 2;

  /**
   * Connection polling enabled
   */
  public static final String CP_YES = CommonUtilConstants.CODE_YES;

  /**
   * Connection polling disabled
   */
  public static final String CP_NO = CommonUtilConstants.CODE_NO;

  /**
   * Single instance of the class
   */
  private static ObjectStore instance;

  /**
   * Size of the transaction ID queue
   */
  private static final int TRANS_ID_Q_SIZE = 1000;

  /**
   * Base string for DM Framework properties
   */
  private static final String P_DM_BASE = "com.bosch.caltool.dm.common";
  /**
   * Property for application user name
   */
  public static final String P_USER_NAME = P_DM_BASE + ".appuser";

  /**
   * Property for CQN mode
   */
  public static final String P_CQN_MODE = P_DM_BASE + ".cqnmode";

  /**
   * Property for enabling database connection polling. Possible values Y - Yes, N - No
   */
  public static final String P_CONN_POLL_MODE = P_DM_BASE + ".polldbconnection";

  /**
   * Database server
   */
  public static final String P_DB_SERVER = "CommonUtils.DB_SERVER";

  /**
   * Database port
   */
  public static final String P_DB_PORT = "CommonUtils.DB_PORT";

  /**
   * Database instance code
   */
  public static final String P_INS_CODE = "CommonUtils.DB_INS_CODE";

  /**
   * Queue to store transaction ID. Size is specified by constant TRANS_ID_Q_SIZE
   */
  private final Queue<String> transIdQueue = new LinkedBlockingQueue<>(TRANS_ID_Q_SIZE);

  /**
   * Holds the Entity manager factory
   */
  private EntityManagerFactory emf;

  /**
   * Holds the main entity manager
   */
  private EntityManager entMgr;

  /**
   * Change notification cache
   */
  private ChangeNotificationCache cncCache;

  /**
   * Logger for this data model
   */
  private ILoggerAdapter logger;

  /**
   * Logger for JPA
   */
  private ILoggerAdapter jpaLogger;

  /**
   * Defines handler which handles all db transactions
   */
  private TransactionSummaryHandler tranSummHandler;

  /**
   * Set of cache refreshers for handling change notifications
   */
  private final Set<ICacheRefresher> cacheRefresherSet = new HashSet<ICacheRefresher>();

  /**
   * Set of entity types
   */
  private final Set<IEntityType<?, ?>> entityTypeSet = new HashSet<IEntityType<?, ?>>();

  /**
   * Data model properties
   */
  private Properties dmProperties;

  /**
   * Application user name
   */
  private String appUserName;

  /**
   * iCDM PWD
   */
  private String icdmPwd;

  /**
   * Language of the tool
   */
  private Language language = Language.ENGLISH;

  /**
   * CQN mode set to this store
   */
  private int cqnMode = CQN_MODE_NO;

  /**
   * key- string reference , value - EntityManager
   *
   * @deprecated use entity manager from service data
   */
  @Deprecated
  private final Map<String, EntityManager> entityManagerMap = new HashMap<>();

  private Class<?> cnsMessageClass;

  /**
   * Private constructor for singleton implementation
   */
  private ObjectStore() {
    // Nothing to do
  }

  /**
   * Get the single instance
   *
   * @return the instance
   */
  public static ObjectStore getInstance() {
    if (instance == null) { // NOPMD by BNE4COB on 12/12/13 9:41 PM
      instance = new ObjectStore();
    }
    return instance;
  }

  /**
   * Initialise this store. This creates the notifcation cache. Object store should be initialised before creating the
   * database connection.
   *
   * @param dmLoggr Data Model logger
   * @param jpaLoggr JPA Logger
   * @param dmProps data model properties
   */
  public void initialise(final ILoggerAdapter dmLoggr, final ILoggerAdapter jpaLoggr, final Properties dmProps) {
    this.dmProperties = new Properties(dmProps);
    this.cqnMode = Integer.parseInt(dmProps.getProperty(P_CQN_MODE, String.valueOf(CQN_MODE_COMMAND)));
    this.logger = dmLoggr;
    this.jpaLogger = jpaLoggr;
    this.appUserName = dmProps.getProperty(P_USER_NAME).toUpperCase(Locale.getDefault());
    this.cncCache = new ChangeNotificationCache();

    this.tranSummHandler = new TransactionSummaryHandler();

    this.logger.debug("Data model Object store initialised");
  }

  /**
   * Register a new data cache refresher
   *
   * @param dataRefresher cache refresher
   */
  public void registerCacheRefresher(final ICacheRefresher dataRefresher) {
    synchronized (this) {
      this.cacheRefresherSet.add(dataRefresher);
    }
  }

  /**
   * Register the entity types
   *
   * @param entityTypes entity types
   */
  public void registerEntityTypes(final IEntityType<?, ?>[] entityTypes) {
    synchronized (this) {
      if (entityTypes != null) {
        this.entityTypeSet.addAll(Arrays.asList(entityTypes));
      }
    }
  }

  /**
   * Gets the registered entity types
   *
   * @return set of entity types
   */
  public Set<IEntityType<?, ?>> getEntityTypes() {
    synchronized (this) {
      return new HashSet<IEntityType<?, ?>>(this.entityTypeSet);
    }
  }

  /**
   * Get the registered cache refresher objects
   *
   * @return set of cache refreshers
   */
  public Set<ICacheRefresher> getCacheRefreshers() {
    synchronized (this) {
      return new HashSet<ICacheRefresher>(this.cacheRefresherSet);
    }
  }

  /**
   * @return the change notification cache
   */
  public ChangeNotificationCache getChangeNotificationCache() {
    synchronized (this) {
      return this.cncCache;
    }
  }

  /**
   * @return the appUserName
   */
  public String getAppUserName() {
    return this.appUserName;
  }

  /**
   * @return the language
   */
  public Language getLanguage() {
    return this.language;
  }

  /**
   * @param language the language to set
   */
  public void setLanguage(final Language language) {
    this.language = language;
  }

  /**
   * @return the Logger for this data model
   */
  public ILoggerAdapter getLogger() {
    return this.logger;
  }


  /**
   * @return the Logger for JPA
   */
  public ILoggerAdapter getJPALogger() {
    return this.jpaLogger;
  }

  /**
   * @return the CQN mode
   */
  public int getCqnMode() {
    return this.cqnMode;
  }

  /**
   * @return the TransactionSummaryHandler
   */
  public TransactionSummaryHandler getSummaryHandler() {
    return this.tranSummHandler;
  }

  /**
   * Adds the database transaction ID to queue
   *
   * @param transID transaction ID
   */
  public void addTransIDToQueue(final String transID) {
    this.transIdQueue.add(transID);
    if (getLogger().getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
      getLogger().debug("ObjectStore Transaction ID Queue : " + this.transIdQueue);
    }
  }

  /**
   * Checks whether the input transaction ID is a local database transaction
   *
   * @param transID transaction ID
   * @return true/false
   */
  public boolean isLocalTransaction(final String transID) {
    return this.transIdQueue.contains(transID);
  }

  /**
   * Get a data model property
   *
   * @param key key
   * @return property value
   */
  public String getProperty(final String key) {
    return this.dmProperties.getProperty(key);
  }

  /**
   * @param key key
   * @param defValue default property value
   * @return value if present, else default value
   */
  public String getProperty(final String key, final String defValue) {
    return this.dmProperties.getProperty(key, defValue);
  }

  /**
   * @return the entMgr
   * @deprecated old framework
   */
  @Deprecated
  public EntityManager getEntityManager() {
    return this.entMgr;
  }

  /**
   * IMPORTANT : This method should be called only once.
   *
   * @param emf the Entity Manager Factory to set
   */
  public void setEntityManagerFactory(final EntityManagerFactory emf) {
    this.emf = emf;
    this.entMgr = JPAUtils.createEntityManager(emf);
  }

  /**
   * rebuild the em
   *
   * @deprecated old framework
   */
  @Deprecated
  public void rebuild() {
    closeEntMgr(this.entMgr);
    for (EntityManager entManager : this.entityManagerMap.values()) {
      closeEntMgr(entManager);
    }

    this.cncCache = new ChangeNotificationCache();
    this.cacheRefresherSet.clear();
    this.entityTypeSet.clear();
    this.transIdQueue.clear();
    this.entMgr = this.emf.createEntityManager();
  }

  /**
   * @param entManager
   * @deprecated use entity manager handling from service data
   */
  @Deprecated
  private void closeEntMgr(final EntityManager entManager) {
    JPAUtils.closeEntityManager(entManager);
  }

  /**
   * @return the Entity Manager Factory
   */
  public EntityManagerFactory getEntityManagerFactory() {
    return this.emf;
  }

  /**
   * @return the icdmPwd
   */
  public String getIcdmPwd() {
    return this.icdmPwd;
  }

  /**
   * @param icdmPwd the icdmPwd to set
   */
  public void setIcdmPwd(final String icdmPwd) {
    this.icdmPwd = icdmPwd;
  }

  /**
   * Close the database resources stored in the Object store.
   */
  public void closeDatabaseResources() {
    try {
      // logger can be null, if data model is not initialised
      if (CommonUtils.isNotNull(this.logger)) {
        this.logger.debug("Closing database connections...");
      }
      // close entity manager, if initialised
      JPAUtils.closeEntityManager(this.entMgr);

      for (EntityManager entManagr : this.entityManagerMap.values()) {
        closeEntMgr(entManagr);
      }

      // Close entity manager factory, if initialised
      JPAUtils.closeEMF(this.emf);

      if (CommonUtils.isNotNull(this.logger)) {
        this.logger.info("Database connections closed");
      }
    }
    catch (Exception exp) {
      this.logger.error(exp.getMessage(), exp);
    }
  }

  /**
   * @param stringRef String
   * @param entityManager EntityManager
   * @deprecated use entity manager from service data
   */
  @Deprecated
  public void stroreEntityManager(final String stringRef, final EntityManager entityManager) {
    this.entityManagerMap.put(stringRef, entityManager);
  }

  /**
   * @return
   */
  public Class<?> getCnsMessageType() {
    return this.cnsMessageClass;
  }

  /**
   * @param <M>
   * @param clazz
   */
  public <M extends ICnsAsyncMessage> void setCnsMessageType(final Class<M> clazz) {
    this.cnsMessageClass = clazz;

  }
}
