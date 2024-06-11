/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic.pidc;

import java.util.ArrayList;
import java.util.List;

import com.bosch.caltool.icdm.model.apic.AliasDef;
import com.bosch.caltool.icdm.model.uc.UsecaseFavorite;


/**
 * @author dja7cob
 */
public class PIDCCreationWizardData {

  /**
   * pidc name English
   */
  private String nameEng;

  /**
   * pidc english desc
   */
  private String descEng;

  /**
   * pidc german desc
   */
  private String descGer;


  /**
   * AliasDefinition
   */
  private AliasDef selectedAliasDef;

  /**
   * version name
   */
  private String versionName;

  /**
   * version description english
   */
  private String versionDescEng;

  /**
   * version description German
   */
  private String versionDescGer;

  /**
   * List of usecase selected
   */
  private final List<UsecaseFavorite> selUcFavList = new ArrayList<>();

  /**
   * @return the nameEng
   */
  public String getNameEng() {
    return this.nameEng;
  }

  /**
   * @param nameEng the nameEng to set
   */
  public void setNameEng(final String nameEng) {
    this.nameEng = nameEng;
  }


  /**
   * @return the descEng
   */
  public String getDescEng() {
    return this.descEng;
  }

  /**
   * @param descEng the descEng to set
   */
  public void setDescEng(final String descEng) {
    this.descEng = descEng;
  }

  /**
   * @return the descGer
   */
  public String getDescGer() {
    return this.descGer;
  }

  /**
   * @param descGer the descGer to set
   */
  public void setDescGer(final String descGer) {
    this.descGer = descGer;
  }


  /**
   * @param selectedAliasDef Alias Definition
   */
  public void setAliasDefinition(final AliasDef selectedAliasDef) {
    this.selectedAliasDef = selectedAliasDef;
  }

  /**
   * @return AliasDe finition
   */
  public AliasDef getAliasDefinition() {
    return this.selectedAliasDef;
  }

  /**
   * @return Version Name
   */
  public String getVersionName() {
    return this.versionName;
  }

  /**
   * @return Version Desc Eng
   */
  public String getVersionDescEng() {
    return this.versionDescEng;
  }

  /**
   * @return Version Desc Ger
   */
  public String getVersionDescGer() {
    return this.versionDescGer;
  }


  /**
   * @param versionDescGer the versionDescGer to set
   */
  public void setVersionDescGer(final String versionDescGer) {
    this.versionDescGer = versionDescGer;
  }

  /**
   * @param text Version Name
   */
  public void setVersionName(final String text) {
    this.versionName = text;
  }

  /**
   * @param text Version Desc Eng
   */
  public void setVersionDescEng(final String text) {
    this.versionDescEng = text;
  }

  /**
   * @return the selUcFavSet
   */
  public List<UsecaseFavorite> getSelUcFavList() {
    return this.selUcFavList;
  }

}
