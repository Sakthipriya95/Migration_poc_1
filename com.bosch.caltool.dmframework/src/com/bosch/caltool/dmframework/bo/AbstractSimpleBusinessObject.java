/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.bo;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.EntityManager;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.DateUtil;

/**
 * @author bne4cob
 */
public abstract class AbstractSimpleBusinessObject {

  /**
   * Service data
   */
  private final ServiceData serviceData;


  /**
   * @param serviceData ServiceData
   */
  protected AbstractSimpleBusinessObject(final ServiceData serviceData) {
    this.serviceData = serviceData;
  }

  /**
   * @return the inputData
   */
  protected ServiceData getServiceData() {
    return this.serviceData;
  }

  /**
   * @return entity manager to be used
   */
  protected final EntityManager getEntMgr() {
    return this.serviceData.getEntMgr();
  }

  /**
   * @return temporary entity manager
   */
  protected final EntityManager getNewEntMgr() {
    return JPAUtils.createEntityManager(ObjectStore.getInstance().getEntityManagerFactory());
  }

  /**
   * Starts the transaction, if not started already, in the given Entity Manager
   *
   * @param entMgr Entity Manager
   */
  protected final void beginTransaction(final EntityManager entMgr) {
    JPAUtils.beginTransaction(entMgr);
  }

  /**
   * If the given entity manager is 'not null' and transaction is active, rolls back the transaction
   *
   * @param entMgr Entity Manager
   */
  protected final void rollbackTransaction(final EntityManager entMgr) {
    JPAUtils.rollbackTransaction(entMgr);
  }

  /**
   * If the given entity manager is 'not null' and transaction is active, commits the transaction.
   *
   * @param entMgr Entity Manager
   */
  protected final void commitTransaction(final EntityManager entMgr) {
    JPAUtils.commitTransaction(entMgr);
  }

  /**
   * If the given entity manager is 'not null' and transaction is active, rolls back the transaction and closes it.
   *
   * @param entMgr Entity Manager
   */
  protected final void rollbackTransactionAndCloseEm(final EntityManager entMgr) {
    JPAUtils.rollbackTransactionAndCloseEm(entMgr);
  }

  /**
   * If the given entity manager is 'not null' and transaction is active, commits the transaction and closes it.
   *
   * @param entMgr Entity Manager
   */
  protected final void commitTransactionAndCloseEm(final EntityManager entMgr) {
    JPAUtils.commitTransactionAndCloseEm(entMgr);
  }

  /**
   * Closes the given entity manager, if EM is 'not null' and 'open'
   *
   * @param entMgr Entity Manager
   */
  protected final void closeEntityManager(final EntityManager entMgr) {
    JPAUtils.closeEntityManager(entMgr);
  }

  /**
   * Util method to get text based on the input language
   *
   * @param engTxt text in English
   * @param gerTxt text in German
   * @param defaultStr default text if both English and German text are null
   * @return language specific text
   */
  protected final String getLangSpecTxt(final String engTxt, final String gerTxt, final String defaultStr) {
    return ApicUtil.getLangSpecTxt(getServiceData().getLanguageObj(), engTxt, gerTxt, defaultStr);
  }

  /**
   * Util method to get text based on the input language
   *
   * @param engTxt text in English
   * @param gerTxt text in German
   * @return language specific text. <code>null</code> if both English and German text are null
   */
  protected final String getLangSpecTxt(final String engTxt, final String gerTxt) {
    return ApicUtil.getLangSpecTxt(getServiceData().getLanguageObj(), engTxt, gerTxt, null);
  }

  /**
   * Convert SQL time to date, with timezone set in service data
   *
   * @param sqlTime sql time
   * @return date. If input is null, returns null
   */
  protected final Date timestamp2Date(final Timestamp sqlTime) {
    return DateUtil.timestamp2Date(sqlTime, getServiceData().getTimezone());
  }

  /**
   * @param sqlTime Timestamp
   * @param dateFormat pattern
   * @return String from Timestamp
   */
  protected final String timestamp2String(final Timestamp sqlTime, final String dateFormat) {
    return DateUtil.formatDateToString(timestamp2Date(sqlTime), dateFormat);
  }

  /**
   * @param sqlTime Timestamp
   * @return String from Timestamp
   */
  protected final String timestamp2String(final Timestamp sqlTime) {
    return timestamp2String(sqlTime, DateFormat.DATE_FORMAT_15);
  }


  /**
   * @return Logger
   */
  protected ILoggerAdapter getLogger() {
    return ObjectStore.getInstance().getLogger();
  }

  /**
   * Convert Y or N to boolean
   *
   * @param yOrN Y or N
   * @return true if input is Y, false otherwise
   */
  protected final boolean yOrNToBoolean(final String yOrN) {
    return CommonUtils.getBooleanType(yOrN);
  }

}
