/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.jpa.bo; // NOPMD by bne4cob on 10/2/13 10:12 AM

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.apic.jpa.bo.ApicUser;
import com.bosch.caltool.apic.jpa.bo.NodeAccessRight;
import com.bosch.caltool.dmframework.bo.AbstractDataCache;
import com.bosch.caltool.icdm.common.util.Language;


/**
 * Class to store all data objects related to Comp package
 */
class CPDataCache extends AbstractDataCache { // NOPMD by bne4cob on 10/2/13 10:12 AM

  /**
   * The CDR data provider
   */
  private final CPDataProvider dataProvider;

  /**
   * Defines Map of all Coponent packages
   */
  private Map<Long, CompPkg> allCompPkgs;

  /**
   * Map of all comp Bc's
   */
  private Map<Long, CompPkgBc> allCompPkgBcs;

  /**
   * Map of all comp Bc Fc's
   */
  private Map<Long, CompPkgBcFc> allCompPkgBcFcs;

  /**
   * Map of all DISTINCT BC's (in T_SDOM_BCS)
   */
  private Map<String, SdomBC> allBcs;

  /**
   * Map of all Fc's cached for the BC key
   */
  private Map<String, Set<SdomFC>> bcsFC;

  /**
   * Map of all DISTINCT FC's used in current session (from T_SDOM_FCS)
   */
  private Map<String, SdomFC> allFcs;

  /**
   * Map that stores all Business objects of CompPkgRuleAttr
   */
  private final Map<Long, CompPkgRuleAttr> allCpRuleAttrMap = new ConcurrentHashMap<>();

  /**
   * Constructor
   * 
   * @param dataProvider data provider
   */
  public CPDataCache(final CPDataProvider dataProvider) {
    super();
    this.dataProvider = dataProvider;
    initialize();
  }

  /**
   * Initialize objects
   */
  private void initialize() {
    this.allCompPkgs = Collections.synchronizedMap(new HashMap<Long, CompPkg>());
    this.allCompPkgBcs = Collections.synchronizedMap(new HashMap<Long, CompPkgBc>());
    this.allCompPkgBcFcs = Collections.synchronizedMap(new HashMap<Long, CompPkgBcFc>());
    this.allBcs = Collections.synchronizedMap(new HashMap<String, SdomBC>());
    this.allFcs = Collections.synchronizedMap(new HashMap<String, SdomFC>());
    this.bcsFC = Collections.synchronizedMap(new HashMap<String, Set<SdomFC>>());
  }

  /**
   * Get all Component packages
   * 
   * @param includeDeleted true if the deleted components are to be included
   * @return sorted set of component packages
   */
  public SortedSet<CompPkg> getAllCompPkgs(final boolean includeDeleted) {
    if (getAllCompPkgsMap().isEmpty()) {
      this.dataProvider.getDataLoader().loadAllCompPkgs();
    }
    if (includeDeleted) {
      return new TreeSet<CompPkg>(getAllCompPkgsMap().values());
    }
    SortedSet<CompPkg> cmpPkgSet = new TreeSet<CompPkg>();
    for (CompPkg cmpPkg : getAllCompPkgsMap().values()) {
      if (!cmpPkg.isDeleted()) {
        cmpPkgSet.add(cmpPkg);
      }
    }
    return cmpPkgSet;
  }

  /**
   * Gets all Component packages
   * 
   * @return map of Component packages
   */
  protected Map<Long, CompPkg> getAllCompPkgsMap() {
    return this.allCompPkgs;
  }

  /**
   * Gets all Component packages
   * 
   * @return map of Component packages
   */
  protected Map<Long, CompPkgBc> getAllCompPkgBcs() {
    return this.allCompPkgBcs;
  }

  /**
   * Gets all Component packages
   * 
   * @return map of Component packages
   */
  protected Map<Long, CompPkgBcFc> getAllCompPkgBcFcs() {
    return this.allCompPkgBcFcs;
  }

  /**
   * Get data provider
   * 
   * @return CPDataProvider
   */
  protected final CPDataProvider getDataProvider() {
    return this.dataProvider;
  }

  /**
   * @param compPkgId primaryKey
   * @return CompPkg
   */
  protected final synchronized CompPkg getCompPkg(final Long compPkgId) { // NOPMD by adn1cob on 6/30/14 10:17 AM
    return getAllCompPkgsMap().get(compPkgId);
  }

  /**
   * @param compPkgBcId primaryKey
   * @return CompPkgBc
   */
  protected final synchronized CompPkgBc getCompPkgBc(final Long compPkgBcId) { // NOPMD by adn1cob on 6/30/14 10:17 AM
    return getAllCompPkgBcs().get(compPkgBcId);
  }

  /**
   * @param compPkgBcFcId primaryKey
   * @return CompPkgBcFc
   */
  protected final synchronized CompPkgBcFc getCompPkgBcFc(final Long compPkgBcFcId) { // NOPMD by adn1cob on 6/30/14
                                                                                      // 10:17 AM
    return getAllCompPkgBcFcs().get(compPkgBcFcId);
  }

  /**
   * @param bcName bcName
   * @return SDOMBc obj
   */
  protected final synchronized SdomBC getBC(final String bcName) { // NOPMD by adn1cob on 6/30/14 10:17 AM
    return getAllBcs().get(bcName);
  }

  /**
   * @param objID key
   * @return CompPkgRuleAttr obj
   */
  protected final CompPkgRuleAttr getCompPkgRuleAttr(final Long objID) {
    return this.allCpRuleAttrMap.get(objID);
  }

  /**
   * @return map of bcs
   */
  protected Map<String, SdomBC> getAllBcs() {
    return this.allBcs;
  }

  /**
   * @param fcName bcName
   * @return SdomFC obj
   */
  protected final synchronized SdomFC getFC(final String fcName) { // NOPMD by adn1cob on 6/30/14 10:17 AM
    return getAllFcs().get(fcName);
  }

  /**
   * @return map of bcs
   */
  protected Map<String, SdomFC> getAllFcs() {
    return this.allFcs;
  }

  /**
   * @param bcName bcName
   * @return Set of FC names
   */
  protected Set<SdomFC> getFcs(final String bcName) {
    return this.bcsFC.get(bcName);
  }

  /**
   * @return Set of FCs used in the session
   */
  protected Map<String, Set<SdomFC>> getAllBCFcs() {
    return this.bcsFC;
  }

  /**
   * Get the language
   * 
   * @return Language
   */
  public final Language getLanguage() {
    return this.dataProvider.getApicDataProvider().getLanguage();
  }


  /**
   * @return returns the node acccess Rights of a Node From the User Map
   * @param nodeAccessID Node Access ID
   */
  protected final NodeAccessRight getNodeAccRight(final Long nodeAccessID) {
    return this.dataProvider.getApicDataProvider().getNodeAccRight(nodeAccessID);
  }

  /**
   * @return the logged in user
   */
  public ApicUser getCurrentUser() {
    return this.dataProvider.getApicDataProvider().getCurrentUser();
  }

  /**
   * @return ConcurrentHashMap of CompPkgRuleAttr objects
   */
  protected Map<Long, CompPkgRuleAttr> getAllCompPkgRuleAttrs() {
    return this.allCpRuleAttrMap;
  }

}