/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.uc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author MKL2COB
 */
public class UsecaseTreeGroupModel {


  /**
   * key - usecase group id , value -UseCaseGroup
   */
  private final Map<Long, UseCaseGroup> useCaseGroupMap = new HashMap<>();
  /**
   * key - use case id, value - UseCase
   */
  private final Map<Long, UseCase> usecaseMap = new HashMap<>();

  /**
   * set of root usecase group id's
   */
  private final Set<Long> rootUCGSet = new HashSet<>();
  /**
   * key - usecase group id , value - set of child item id's
   */
  private final Map<Long, Set<Long>> childGroupSetMap = new HashMap<>();

  /**
   * key - usecase group id , value - set of child item id's
   */
  private final Map<Long, Set<Long>> childUsecaseSetMap = new HashMap<>();


  /**
   * @return the useCaseGroupMap
   */
  public Map<Long, UseCaseGroup> getUseCaseGroupMap() {
    return this.useCaseGroupMap;
  }


  /**
   * @return the usecaseMap
   */
  public Map<Long, UseCase> getUsecaseMap() {
    return this.usecaseMap;
  }


  /**
   * @return the childUsecaseSet
   */
  public Map<Long, Set<Long>> getChildUsecaseSetMap() {
    return this.childUsecaseSetMap;
  }


  /**
   * @return the childGroupSet
   */
  public Map<Long, Set<Long>> getChildGroupSetMap() {
    return this.childGroupSetMap;
  }


  /**
   * @return the rootUCGSet
   */
  public Set<Long> getRootUCGSet() {
    return this.rootUCGSet;
  }


}
