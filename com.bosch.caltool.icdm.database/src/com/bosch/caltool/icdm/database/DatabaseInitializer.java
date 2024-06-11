/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.database;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.PasswordServiceWrapper;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.security.Decryptor;

/**
 * @author bne4cob
 */
// ICDM-2475
public class DatabaseInitializer {

  /**
   * Logger
   */
  private final ILoggerAdapter loggerAdapter;

  /**
   * Constructor
   *
   * @param logger logger
   */
  public DatabaseInitializer(final ILoggerAdapter logger) {
    this.loggerAdapter = logger;
  }

  /**
   * Initialise database connection
   *
   * @return EntityManagerFactory created
   * @throws IcdmException exception connecting to DB
   */
  public EntityManagerFactory connect() throws IcdmException {

    this.loggerAdapter.debug("Initialising database connection...");

    final Map<String, Object> props = getJpaProperties();

    EntityManagerFactory emf;


    if (CommonUtils.isStartedFromWebService()) {
      // The webservice doesn't need the OSGI Persistence Provider
      emf = Persistence.createEntityManagerFactory(DBConstants.ICDM_JPA_PERSISTANCE_NAME, props);
    }
    else {
      // provide the Classloader of the plug-in. otherwise the persistence.xml file can not be found
      props.put(PersistenceUnitProperties.CLASSLOADER, this.getClass().getClassLoader());

      // use the OSGI PersistenceProvider in an OSGI environment
      final org.eclipse.persistence.jpa.PersistenceProvider persProvider =
          new org.eclipse.persistence.jpa.PersistenceProvider();

      // create the EntityManagerFactory for the Persistence unit and properties
      emf = persProvider.createEntityManagerFactory(DBConstants.ICDM_JPA_PERSISTANCE_NAME, props);
    }

    if (emf == null) {
      throw new DataException(DBConstants.AUTH_FAILED_INVALID_CRED);
    }

    this.loggerAdapter.debug("database is connected now");

    return emf;

  }

  /**
   * Method to read database properties
   *
   * @return the Map
   * @throws DataException JPA_PROPERTIES_MISSING
   */
  private Map<String, Object> getJpaProperties() throws DataException {

    // Read db-attributes from properties file Available in
    // com.bosch.caltool.icdm.common.util.messages.Messages.properties
    final String dbURL = Messages.getString("CommonUtils.DB_URL");
    final String dbUser = Messages.getString("CommonUtils.DB_USER");

    // Get the passord from the WEb service
    String passwordKey = Messages.getString(CommonUtilConstants.COMMON_UTILS_DB_USER_PASS);

    final PasswordServiceWrapper passWordWrapper = new PasswordServiceWrapper(this.loggerAdapter);
    final String dbPass = Decryptor.getInstance().decrypt(passWordWrapper.getPassword(passwordKey), this.loggerAdapter);

    // Validate if values in properties are available
    if (CommonUtils.isEmptyString(dbURL) || CommonUtils.isEmptyString(dbUser) || CommonUtils.isEmptyString(dbPass)) {
      throw new DataException(DBConstants.JPA_PROPERTIES_MISSING);
    }

    final ConcurrentMap<String, Object> props = new ConcurrentHashMap<>();

    props.put(DBConstants.JPA_JDBC_USER, dbUser);
    props.put(DBConstants.JPA_JDBC_PASS, dbPass);
    props.put(DBConstants.JPA_JDBC_URL, dbURL);
    props.put(DBConstants.JPA_JDBC_DRIVER, DBConstants.JPA_ORA_DRIVER);

    /* Log file path is configured here */
    props.put(DBConstants.JPA_LOGGING, DBConstants.JPA_LOG_PATH);


    return props;
  }
}
