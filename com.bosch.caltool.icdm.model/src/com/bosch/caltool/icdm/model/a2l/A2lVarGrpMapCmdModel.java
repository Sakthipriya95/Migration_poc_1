/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rgo7cob
 */
public class A2lVarGrpMapCmdModel {

  /**
   * a2l var mapping object to be created
   */
  private List<A2lVarGrpVariantMapping> mappingTobeCreated = new ArrayList<>();

  /**
   * a2l var mapping object to be updated
   */
  private List<A2lVarGrpVariantMapping> mappingTobeUpdated = new ArrayList<>();

  /**
   * a2l var mapping object to be deleted
   */
  private List<A2lVarGrpVariantMapping> mappingTobeDeleted = new ArrayList<>();

  /*  *//**
         * a2l var mapping object created
         */
  private List<A2lVarGrpVariantMapping> mappingCreated = new ArrayList<>();

  private List<A2lVarGrpVariantMapping> mappingDeleted = new ArrayList<>();


  /**
   * @return the mappingTobeCreated
   */
  public List<A2lVarGrpVariantMapping> getMappingTobeCreated() {
    return this.mappingTobeCreated;
  }


  /**
   * @param mappingTobeCreated the mappingTobeCreated to set
   */
  public void setMappingTobeCreated(final List<A2lVarGrpVariantMapping> mappingTobeCreated) {
    this.mappingTobeCreated = mappingTobeCreated;
  }


  /**
   * @return the mappingTobeUpdated
   */
  public List<A2lVarGrpVariantMapping> getMappingTobeUpdated() {
    return this.mappingTobeUpdated;
  }


  /**
   * @param mappingTobeUpdated the mappingTobeUpdated to set
   */
  public void setMappingTobeUpdated(final List<A2lVarGrpVariantMapping> mappingTobeUpdated) {
    this.mappingTobeUpdated = mappingTobeUpdated;
  }


  /**
   * @return the mappingTobeDeleted
   */
  public List<A2lVarGrpVariantMapping> getMappingTobeDeleted() {
    return this.mappingTobeDeleted;
  }


  /**
   * @param mappingTobeDeleted the mappingTobeDeleted to set
   */
  public void setMappingTobeDeleted(final List<A2lVarGrpVariantMapping> mappingTobeDeleted) {
    this.mappingTobeDeleted = mappingTobeDeleted;
  }


  /**
   * @return the mappingCreated
   */
  public List<A2lVarGrpVariantMapping> getMappingCreated() {
    return this.mappingCreated;
  }


  /**
   * @return the mappingDeleted
   */
  public List<A2lVarGrpVariantMapping> getMappingDeleted() {
    return this.mappingDeleted;
  }


  /**
   * @param mappingCreated the mappingCreated to set
   */
  public void setMappingCreated(final List<A2lVarGrpVariantMapping> mappingCreated) {
    this.mappingCreated = mappingCreated;
  }


  /**
   * @param mappingDeleted the mappingDeleted to set
   */
  public void setMappingDeleted(final List<A2lVarGrpVariantMapping> mappingDeleted) {
    this.mappingDeleted = mappingDeleted;
  }


}
