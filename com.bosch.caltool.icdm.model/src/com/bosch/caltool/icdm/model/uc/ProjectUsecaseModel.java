/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.uc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author PDH2COB
 */
public class ProjectUsecaseModel {

  /**
   * This set is mainly used for refresh applicability in PIDC Editor. Contains IDs of those items higher in hierarchy
   * level like parent group, parent section parent usecase etc
   */
  private Set<Long> parentUsecaseItemIdSet = new HashSet<>();

  /**
   * This set is mainly used for refresh applicability in PIDC Editor. Contains IDs of project usecase group, usecase,
   * usecase section.
   */
  private Set<Long> projectUsecaseIdSet = new HashSet<>();

  /**
   * Set of attribute ids that are mapped to project usecases
   */
  private Set<Long> projectUsecaseAttrIdSet = new HashSet<>();

  /**
   * Set of attribute ids that are mapped to usecase and quotation relevant
   */
  private Set<Long> quotationRelevantUcAttrIdSet = new HashSet<>();

  /**
   * Key - Usecase Id/ Usecase Section Id Value - Set of Attr ids
   */
  private Map<Long, Set<Long>> quotationRelevantUcAttrIdMap = new HashMap<>();


  /**
   * @return the projectUsecaseIdSet
   */
  public Set<Long> getProjectUsecaseIdSet() {
    return this.projectUsecaseIdSet;
  }


  /**
   * @return the projectUsecaseAttrIdSet
   */
  public Set<Long> getProjectUsecaseAttrIdSet() {
    return this.projectUsecaseAttrIdSet;
  }


  /**
   * @param projectUsecaseAttrIdSet the projectUsecaseAttrIdSet to set
   */
  public void setProjectUsecaseAttrIdSet(final Set<Long> projectUsecaseAttrIdSet) {
    this.projectUsecaseAttrIdSet = projectUsecaseAttrIdSet;
  }


  /**
   * @param projectUsecaseIdSet the projectUsecaseIdSet to set
   */
  public void setProjectUsecaseIdSet(final Set<Long> projectUsecaseIdSet) {
    this.projectUsecaseIdSet = projectUsecaseIdSet;
  }


  /**
   * @return the parentUsecaseItemIdSet
   */
  public Set<Long> getParentUsecaseItemIdSet() {
    return this.parentUsecaseItemIdSet;
  }


  /**
   * @param parentUsecaseItemIdSet the parentUsecaseItemIdSet to set
   */
  public void setParentUsecaseItemIdSet(final Set<Long> parentUsecaseItemIdSet) {
    this.parentUsecaseItemIdSet = parentUsecaseItemIdSet;
  }


  /**
   * @return the quotationRelevantUcAttrIdSet
   */
  public Set<Long> getQuotationRelevantUcAttrIdSet() {
    return this.quotationRelevantUcAttrIdSet;
  }


  /**
   * @param quotationRelevantUcAttrIdSet the quotationRelevantUcAttrIdSet to set
   */
  public void setQuotationRelevantUcAttrIdSet(final Set<Long> quotationRelevantUcAttrIdSet) {
    this.quotationRelevantUcAttrIdSet = quotationRelevantUcAttrIdSet;
  }


  /**
   * @return the quotationRelevantUcAttrIdMap
   */
  public Map<Long, Set<Long>> getQuotationRelevantUcAttrIdMap() {
    return this.quotationRelevantUcAttrIdMap;
  }


  /**
   * @param quotationRelevantUcAttrIdMap the quotationRelevantUcAttrIdMap to set
   */
  public void setQuotationRelevantUcAttrIdMap(final Map<Long, Set<Long>> quotationRelevantUcAttrIdMap) {
    this.quotationRelevantUcAttrIdMap = quotationRelevantUcAttrIdMap;
  }


}
