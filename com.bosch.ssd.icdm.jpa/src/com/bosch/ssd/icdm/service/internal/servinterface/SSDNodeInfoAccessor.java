/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.service.internal.servinterface;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.PersistenceProvider;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.pwdservice.exception.PasswordNotFoundException;
import com.bosch.ssd.icdm.common.utility.DBConnectionRetrievalUtil;
import com.bosch.ssd.icdm.constants.SSDiCDMInterfaceConstants;
import com.bosch.ssd.icdm.exception.ExceptionUtils;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException.SSDiCDMInterfaceErrorCodes;
import com.bosch.ssd.icdm.logger.SSDiCDMInterfaceLogger;
import com.bosch.ssd.icdm.service.utility.DBQueryUtils;

/**
 * Service Class to handle Entity Manager Creation for all transactions
 *
 * @author SSN9COB
 */
public class SSDNodeInfoAccessor {

  private EntityManager currentEntityManager;

  private final String loginEnum;
  private final String schemaUserName;
  private final String schemaPassword;

  private BigDecimal compliNodeId;
  private BigDecimal compPkgNodeId;
  private BigDecimal reviewRuleNodeId;

  private DBQueryUtils dbQueryUtils;
  private String userName;

  /**
   * Set the Username & Password and create the connection : To be used for Interface test methods
   *
   * @param userName Login User
   * @param password SSD Password of the logging in user
   * @param loginEnum login DB INstance
   */
  protected SSDNodeInfoAccessor(final String userName, final String password, final String loginEnum) {
    this.schemaUserName = userName;
    this.schemaPassword = password;
    this.currentEntityManager = null;
    this.loginEnum = loginEnum;
  }

  /**
   * Set the entity manager and node id : To be used by iCDM as they create the connection and pass the entity manager
   *
   * @param userName Login User
   * @param em Entity Manager
   */
  protected SSDNodeInfoAccessor(final String userName, final EntityManager em) {
    this.schemaUserName = userName;
    this.schemaPassword = null;
    this.loginEnum = null;
    this.currentEntityManager = em;
    this.dbQueryUtils = new DBQueryUtils(this.currentEntityManager, getSchemaUserName());
  }

  /**
   * Create the connection for the specified User Credentials against the configured DB instance from Properties file
   *
   * @throws SSDiCDMInterfaceException Exception
   */
  private void createConnection() throws SSDiCDMInterfaceException {
    try {
      HashMap<String, Object> props = getDBProperties();
      props.put(SSDiCDMInterfaceConstants.DATABASE_JDBC_PERSISTENCE_USER_PROPERTY, getSchemaUserName());
      props.put(SSDiCDMInterfaceConstants.DATABASE_JDBC_PERSISTENCE_PSWD_PROPERTY, this.schemaPassword);
      props.put(PersistenceUnitProperties.CLASSLOADER, this.getClass().getClassLoader());
      EntityManagerFactory emf =
          new PersistenceProvider().createEntityManagerFactory(SSDiCDMInterfaceConstants.PLUGIN_ID, props);
      this.currentEntityManager = emf.createEntityManager();
      this.dbQueryUtils = new DBQueryUtils(this.currentEntityManager, getSchemaUserName());
      SSDiCDMInterfaceLogger.logMessage(
          "Logged in User: " + props.get(SSDiCDMInterfaceConstants.DATABASE_JDBC_PERSISTENCE_USER_PROPERTY),
          ILoggerAdapter.LEVEL_INFO, null);
    }
    // IOException for File Input Stream, PasswordNotFound for PwdWebService & PersistenceException for DB Entity
    // Manager
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION,true);
    }
  }


  /**
   * Get the DB Proprties with ConnectionString for the Entity Manager from the properties file
   *
   * @param logger logger needed to log message
   * @throws PasswordNotFoundException password not found
   */
  private HashMap<String, Object> getDBProperties() throws PasswordNotFoundException {


    String dbUrl = SSDiCDMInterfaceConstants.DATABASE_CONNECTION_JDBC_PREFIX + DBConnectionRetrievalUtil.getInstance()
        .getConnStringAndDecrypt(this.loginEnum, SSDiCDMInterfaceLogger.getLogger()); // Test
    HashMap<String, Object> props = new HashMap<>();
    props.put(SSDiCDMInterfaceConstants.DATABASE_JDBC_PERSISTENCE_URL_PROPERTY, dbUrl);
    SSDiCDMInterfaceLogger.logMessage("DB connection String: " + this.loginEnum, ILoggerAdapter.LEVEL_INFO, null);
    return props;
  }

  /**
   * Returns the Entity Manager for the current session
   *
   * @return the currentEntityManager
   * @throws SSDiCDMInterfaceException exception
   */
  public EntityManager getCurrentEntityManager() throws SSDiCDMInterfaceException {
    // If no connection available, create a new connection
    if (Objects.isNull(this.currentEntityManager)) {
      createConnection();
    }
    return this.currentEntityManager;
  }

  /**
   * At end of each session close the entity manager & Manager Factory
   *
   * @throws SSDiCDMInterfaceException exception
   */
  public void closeEntityManager() throws SSDiCDMInterfaceException {
    if (getCurrentEntityManager().isOpen()) {
      getCurrentEntityManager().getEntityManagerFactory().close();
    }
  }

  /**
   * @return the schemaUserName
   */
  public String getSchemaUserName() {
    return this.schemaUserName;
  }

  /**
   * @return the compliNodeId
   */
  public BigDecimal getCompliNodeId() {
    return this.compliNodeId;
  }

  /**
   * @return the compPkgNodeId
   */
  public BigDecimal getCompPkgNodeId() {
    return this.compPkgNodeId;
  }

  /**
   * @return the reviewRuleNodeId
   */
  public BigDecimal getReviewRuleNodeId() {
    return this.reviewRuleNodeId;
  }

  /**
   * @return the dbQueryUtils
   */
  public DBQueryUtils getDbQueryUtils() {
    return this.dbQueryUtils;
  }

  /**
   * @return the userName
   */
  public String getUserName() {
    return this.userName;
  }

  /**
   * @param userName the userName to set
   */
  public void setUserName(final String userName) {
    this.userName = userName;
  }

  /**
   * @param compliNodeId the compliNodeId to set
   */
  public void setCompliNodeId(final BigDecimal compliNodeId) {
    this.compliNodeId = compliNodeId;
  }


  /**
   * @param compPkgNodeId the compPkgNodeId to set
   */
  public void setCompPkgNodeId(final BigDecimal compPkgNodeId) {
    this.compPkgNodeId = compPkgNodeId;
  }


  /**
   * @param reviewRuleNodeId the reviewRuleNodeId to set
   */
  public void setReviewRuleNodeId(final BigDecimal reviewRuleNodeId) {
    this.reviewRuleNodeId = reviewRuleNodeId;
  }

}
