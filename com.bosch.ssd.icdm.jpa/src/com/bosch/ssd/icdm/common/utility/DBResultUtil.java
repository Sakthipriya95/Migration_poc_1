/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.common.utility;

import java.util.List;

import javax.persistence.Query;

import org.eclipse.persistence.queries.ValueReadQuery;
import org.eclipse.persistence.sessions.Session;

import com.bosch.ssd.icdm.service.utility.ServiceLogAndTransactionUtil;

/**
 * Utility Class that helps in getting the result from the DB Query
 *
 * @author SSN9COB
 */
public final class DBResultUtil {

  private DBResultUtil() {
    // Hidden Constructor
  }

  /**
   * Method that logs the total query time for fetching resultlist
   *
   * @param query query
   * @param namedQuery name
   * @return list
   */
  public static List<Object[]> getQueryObjArrList(final Query query, final String namedQuery) {
    // note the start time of the method in LONG
    long start = System.currentTimeMillis();
    // Fetch the result list from the passed query
    List<Object[]> resultList = query.getResultList();
    // invoke the common util method to log the time taken for each query & pass the time taken
    // by subtracting the start and end time of the result list
    ServiceLogAndTransactionUtil.logTimeTakenForEachDBQuery(namedQuery, "Fetched Size : " + resultList.size(),
        System.currentTimeMillis() - start);
    // return the list
    return resultList;
  }

  /**
   * Method that logs the total query time for fetching resultlist
   *
   * @param query qquery
   * @param namedQuery name
   * @return list
   */
  public static List<Object> getQueryObjList(final Query query, final String namedQuery) {
    // note the start time of the method in LONG
    long start = System.currentTimeMillis();
    // Fetch the result list from the passed query
    List<Object> resultList = query.getResultList();
    // invoke the common util method to log the time taken for each query & pass the time taken
    // by subtracting the start and end time of the result list
    ServiceLogAndTransactionUtil.logTimeTakenForEachDBQuery(namedQuery, "Fetched Size : " + resultList.size(),
        System.currentTimeMillis() - start);
    // return the list
    return resultList;
  }


  /**
   * Method that logs the total query time for fetching result
   *
   * @param query query
   * @param namedQuery name
   * @return object
   */
  public static Object getQuerySingleResult(final Query query, final String namedQuery) {
    // note the start time of the method in LONG
    long start = System.currentTimeMillis();
    // Fetch the single result from the passed query
    Object result = query.getSingleResult();
    // invoke the common util method to log the time taken for each query & pass the time taken
    // by subtracting the start and end time
    ServiceLogAndTransactionUtil.logTimeTakenForEachDBQuery(namedQuery, "Fetched Value : " + result,
        System.currentTimeMillis() - start);
    // return result
    return result;
  }

  /**
   * Method that logs the total query time for executing the query
   *
   * @param session Session
   * @param query Query
   * @param arg Arguments
   * @param procedureName procedure
   * @return object
   */
  public static Object executeSessionQuery(final Session session, final ValueReadQuery query, final List<Object> arg,
      final String procedureName) {
    // note the start time of the method in LONG
    long start = System.currentTimeMillis();
    // execute the session from the passed query
    Object result = session.executeQuery(query, arg);
    // invoke the common util method to log the time taken for each query & pass the time taken
    // by subtracting the start and end time
    ServiceLogAndTransactionUtil.logTimeTakenForEachDBQuery(procedureName, "Fetched Value : " + result,
        System.currentTimeMillis() - start);
    // return the result
    return result;
  }

  /**
   * Method that logs the total query time for executing the query
   *
   * @param query Query
   * @param namedQuery Name
   * @return status
   */
  public static int executeQueryUpdate(final Query query, final String namedQuery) {
    // note the start time of the method in LONG
    long start = System.currentTimeMillis();
    // Execute Update the passed query
    int result = query.executeUpdate();
    // invoke the common util method to log the time taken for each query & pass the time taken
    // by subtracting the start and end time
    ServiceLogAndTransactionUtil.logTimeTakenForEachDBQuery(namedQuery, null, System.currentTimeMillis() - start);
    // return the result
    return result;
  }
}
