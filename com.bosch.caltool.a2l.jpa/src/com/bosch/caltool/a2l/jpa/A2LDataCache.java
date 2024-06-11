/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.a2l.jpa;

import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.a2l.jpa.bo.A2LResponsibility;
import com.bosch.caltool.a2l.jpa.bo.A2LWpResponsibility;
import com.bosch.caltool.a2l.jpa.bo.ICDMA2LGroup;
import com.bosch.caltool.a2l.jpa.bo.WPResponsibility;
import com.bosch.caltool.dmframework.bo.AbstractDataCache;
import com.bosch.caltool.icdm.common.util.Language;


/**
 * Class to store all data objects related to A2lEditor
 *
 * @author dmo5cob
 */
@Deprecated
public class A2LDataCache extends AbstractDataCache {


  private final A2LDataProvider dataProvider;

  private final Map<Long, A2LResponsibility> a2lRespMap = new ConcurrentHashMap<>();

  private final Map<Long, ICDMA2LGroup> a2lGrpMap = new ConcurrentHashMap<>();

  private final Map<Long, WPResponsibility> wpRespMap = new ConcurrentHashMap<>();


  private final Map<Long, SortedSet<A2LWpResponsibility>> a2lWpRespMap = new ConcurrentHashMap<>();

  /**
   * Constructor
   *
   * @param dataProvider data provider
   */
  public A2LDataCache(final A2LDataProvider dataProvider) {
    super();
    this.dataProvider = dataProvider;
  }


  /**
   * @return the wpRespMap
   */
  public Map<Long, WPResponsibility> getWpRespMap() {
    return this.wpRespMap;
  }

  /**
   * @return current language
   */
  public final Language getLanguage() {
    return this.dataProvider.getApicDataProvider().getLanguage();
  }


  /**
   * @return the dataProvider
   */
  public A2LDataProvider getDataProvider() {
    return this.dataProvider;
  }


  /**
   * @return the a2lRespMap
   */
  public Map<Long, A2LResponsibility> getA2lRespMap() {
    return this.a2lRespMap;
  }


  /**
   * @return the a2lGrpMap
   */
  public Map<Long, ICDMA2LGroup> getA2lGrpMap() {
    return this.a2lGrpMap;
  }


  /**
   * @return the a2l Wp Resp map
   */
  public Map<Long, SortedSet<A2LWpResponsibility>> getA2lWpRespMap() {
    return this.a2lWpRespMap;
  }


}