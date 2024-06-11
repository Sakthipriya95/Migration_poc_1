/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.cocwp;

import java.util.ArrayList;
import java.util.List;

import com.bosch.caltool.icdm.model.uc.UsecaseFavorite;

/**
 * @author UKT1COB
 */
public class PIDCCocWpUpdationOutputModel {


  private List<Long> listOfNewlyCreatedPidcVersCocWpIds = new ArrayList<>();

  private List<Long> listOfUpdatedPidcVersCocWpIds = new ArrayList<>();

  private List<Long> listOfNewlyCreatedPidcVarCocWpIds = new ArrayList<>();

  private List<Long> listOfUpdatedPidcVarCocWpIds = new ArrayList<>();

  private List<Long> listOfDeletedPidcVarCocWpIds = new ArrayList<>();

  private List<Long> listOfNewlyCreatedPidcSubVarCocWpIds = new ArrayList<>();

  private List<Long> listOfUpdatedPidcSubVarCocWpIds = new ArrayList<>();

  private List<Long> listOfDeletedPidcSubVarCocWpIds = new ArrayList<>();
  /**
   * List of newly created Usecase section
   */
  private List<UsecaseFavorite> listOfNewlyCreatedUCFav = new ArrayList<>();
  /**
   * List of Deleted Usecase section
   */
  private List<UsecaseFavorite> listOfDelUcFav = new ArrayList<>();


  /**
   * @return the listOfNewlyCreatedPidcVersCocWpIds
   */
  public List<Long> getListOfNewlyCreatedPidcVersCocWpIds() {
    return this.listOfNewlyCreatedPidcVersCocWpIds;
  }


  /**
   * @param listOfNewlyCreatedPidcVersCocWpIds the listOfNewlyCreatedPidcVersCocWpIds to set
   */
  public void setListOfNewlyCreatedPidcVersCocWpIds(final List<Long> listOfNewlyCreatedPidcVersCocWpIds) {
    this.listOfNewlyCreatedPidcVersCocWpIds = listOfNewlyCreatedPidcVersCocWpIds;
  }


  /**
   * @return the listOfUpdatedPidcVersCocWpIds
   */
  public List<Long> getListOfUpdatedPidcVersCocWpIds() {
    return this.listOfUpdatedPidcVersCocWpIds;
  }


  /**
   * @param listOfUpdatedPidcVersCocWpIds the listOfUpdatedPidcVersCocWpIds to set
   */
  public void setListOfUpdatedPidcVersCocWpIds(final List<Long> listOfUpdatedPidcVersCocWpIds) {
    this.listOfUpdatedPidcVersCocWpIds = listOfUpdatedPidcVersCocWpIds;
  }


  /**
   * @return the listOfNewlyCreatedPidcVarCocWpIds
   */
  public List<Long> getListOfNewlyCreatedPidcVarCocWpIds() {
    return this.listOfNewlyCreatedPidcVarCocWpIds;
  }


  /**
   * @param listOfNewlyCreatedPidcVarCocWpIds the listOfNewlyCreatedPidcVarCocWpIds to set
   */
  public void setListOfNewlyCreatedPidcVarCocWpIds(final List<Long> listOfNewlyCreatedPidcVarCocWpIds) {
    this.listOfNewlyCreatedPidcVarCocWpIds = listOfNewlyCreatedPidcVarCocWpIds;
  }


  /**
   * @return the listOfUpdatedPidcVarCocWpIds
   */
  public List<Long> getListOfUpdatedPidcVarCocWpIds() {
    return this.listOfUpdatedPidcVarCocWpIds;
  }


  /**
   * @param listOfUpdatedPidcVarCocWpIds the listOfUpdatedPidcVarCocWpIds to set
   */
  public void setListOfUpdatedPidcVarCocWpIds(final List<Long> listOfUpdatedPidcVarCocWpIds) {
    this.listOfUpdatedPidcVarCocWpIds = listOfUpdatedPidcVarCocWpIds;
  }


  /**
   * @return the listOfDeletedPidcVarCocWpIds
   */
  public List<Long> getListOfDeletedPidcVarCocWpIds() {
    return this.listOfDeletedPidcVarCocWpIds;
  }


  /**
   * @param listOfDeletedPidcVarCocWpIds the listOfDeletedPidcVarCocWpIds to set
   */
  public void setListOfDeletedPidcVarCocWpIds(final List<Long> listOfDeletedPidcVarCocWpIds) {
    this.listOfDeletedPidcVarCocWpIds = listOfDeletedPidcVarCocWpIds;
  }


  /**
   * @return the listOfNewlyCreatedPidcSubVarCocWpIds
   */
  public List<Long> getListOfNewlyCreatedPidcSubVarCocWpIds() {
    return this.listOfNewlyCreatedPidcSubVarCocWpIds;
  }


  /**
   * @param listOfNewlyCreatedPidcSubVarCocWpIds the listOfNewlyCreatedPidcSubVarCocWpIds to set
   */
  public void setListOfNewlyCreatedPidcSubVarCocWpIds(final List<Long> listOfNewlyCreatedPidcSubVarCocWpIds) {
    this.listOfNewlyCreatedPidcSubVarCocWpIds = listOfNewlyCreatedPidcSubVarCocWpIds;
  }


  /**
   * @return the listOfUpdatedPidcSubVarCocWpIds
   */
  public List<Long> getListOfUpdatedPidcSubVarCocWpIds() {
    return this.listOfUpdatedPidcSubVarCocWpIds;
  }


  /**
   * @param listOfUpdatedPidcSubVarCocWpIds the listOfUpdatedPidcSubVarCocWpIds to set
   */
  public void setListOfUpdatedPidcSubVarCocWpIds(final List<Long> listOfUpdatedPidcSubVarCocWpIds) {
    this.listOfUpdatedPidcSubVarCocWpIds = listOfUpdatedPidcSubVarCocWpIds;
  }


  /**
   * @return the listOfDeletedPidcSubVarCocWpIds
   */
  public List<Long> getListOfDeletedPidcSubVarCocWpIds() {
    return this.listOfDeletedPidcSubVarCocWpIds;
  }


  /**
   * @param listOfDeletedPidcSubVarCocWpIds the listOfDeletedPidcSubVarCocWpIds to set
   */
  public void setListOfDeletedPidcSubVarCocWpIds(final List<Long> listOfDeletedPidcSubVarCocWpIds) {
    this.listOfDeletedPidcSubVarCocWpIds = listOfDeletedPidcSubVarCocWpIds;
  }


  /**
   * @return the listOfNewlyCreatedUCFav
   */
  public List<UsecaseFavorite> getListOfNewlyCreatedUCFav() {
    return this.listOfNewlyCreatedUCFav;
  }


  /**
   * @param listOfNewlyCreatedUCFav the listOfNewlyCreatedUCFav to set
   */
  public void setListOfNewlyCreatedUCFav(final List<UsecaseFavorite> listOfNewlyCreatedUCFav) {
    this.listOfNewlyCreatedUCFav = listOfNewlyCreatedUCFav;
  }


  /**
   * @return the listOfDelUcFav
   */
  public List<UsecaseFavorite> getListOfDelUcFav() {
    return this.listOfDelUcFav;
  }


  /**
   * @param listOfDelUcFav the listOfDelUcFav to set
   */
  public void setListOfDelUcFav(final List<UsecaseFavorite> listOfDelUcFav) {
    this.listOfDelUcFav = listOfDelUcFav;
  }


}

