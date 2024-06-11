/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PDH2COB
 */
public class A2lRespMaintenanceData {

  /**
   * Contains object to create After creation, it will contain created object
   */
  private A2lResponsibility a2lRespToCreate;

  private A2lResponsibility a2lRespToUpdate;

  private A2lResponsibility a2lRespUpdated;

  private List<A2lRespBoschGroupUser> boschUsrsCreationList = new ArrayList<>();

  private List<A2lRespBoschGroupUser> boschUsrsDeletionList = new ArrayList<>();


  /**
   * @return the a2lRespToCreate
   */
  public A2lResponsibility getA2lRespToCreate() {
    return this.a2lRespToCreate;
  }


  /**
   * @param a2lRespToCreate the a2lRespToCreate to set
   */
  public void setA2lRespToCreate(final A2lResponsibility a2lRespToCreate) {
    this.a2lRespToCreate = a2lRespToCreate;
  }


  /**
   * @return the a2lRespToUpdate
   */
  public A2lResponsibility getA2lRespToUpdate() {
    return this.a2lRespToUpdate;
  }


  /**
   * @param a2lRespToUpdate the a2lRespToUpdate to set
   */
  public void setA2lRespToUpdate(final A2lResponsibility a2lRespToUpdate) {
    this.a2lRespToUpdate = a2lRespToUpdate;
  }


  /**
   * @return the a2lRespUpdated
   */
  public A2lResponsibility getA2lRespUpdated() {
    return this.a2lRespUpdated;
  }


  /**
   * @param a2lRespUpdated the a2lRespUpdated to set
   */
  public void setA2lRespUpdated(final A2lResponsibility a2lRespUpdated) {
    this.a2lRespUpdated = a2lRespUpdated;
  }


  /**
   * @return the boschUsrsCreationList
   */
  public List<A2lRespBoschGroupUser> getBoschUsrsCreationList() {
    return this.boschUsrsCreationList;
  }


  /**
   * @param boschUsrsCreationList the boschUsrsCreationList to set
   */
  public void setBoschUsrsCreationList(final List<A2lRespBoschGroupUser> boschUsrsCreationList) {
    this.boschUsrsCreationList = boschUsrsCreationList;
  }


  /**
   * @return the boschUsrsDeletionList
   */
  public List<A2lRespBoschGroupUser> getBoschUsrsDeletionList() {
    return this.boschUsrsDeletionList;
  }


  /**
   * @param boschUsrsDeletionList the boschUsrsDeletionList to set
   */
  public void setBoschUsrsDeletionList(final List<A2lRespBoschGroupUser> boschUsrsDeletionList) {
    this.boschUsrsDeletionList = boschUsrsDeletionList;
  }


}
