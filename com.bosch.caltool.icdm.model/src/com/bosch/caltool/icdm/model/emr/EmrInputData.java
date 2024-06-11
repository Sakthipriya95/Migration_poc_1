/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.emr;


import java.util.ArrayList;
import java.util.List;

/**
 * @author TRL1COB
 */
public class EmrInputData {

  private Long pidcVersId;

  private String fileName;

  private String fileDescription;

  private List<Long> pidcVariantIdList;

  private List<CodexResults> codexResultsList = new ArrayList<>();


  /**
   * @return the pidcVersId
   */
  public Long getPidcVersId() {
    return this.pidcVersId;
  }


  /**
   * @param pidcVersId the pidcVersId to set
   */
  public void setPidcVersId(final Long pidcVersId) {
    this.pidcVersId = pidcVersId;
  }


  /**
   * @return the fileName
   */
  public String getFileName() {
    return this.fileName;
  }


  /**
   * @param fileName the fileName to set
   */
  public void setFileName(final String fileName) {
    this.fileName = fileName;
  }


  /**
   * @return the fileDescription
   */
  public String getFileDescription() {
    return this.fileDescription;
  }


  /**
   * @param fileDescription the fileDescription to set
   */
  public void setFileDescription(final String fileDescription) {
    this.fileDescription = fileDescription;
  }


  /**
   * @return the pidcVariantIdList
   */
  public List<Long> getPidcVariantIdList() {
    return this.pidcVariantIdList;
  }


  /**
   * @param pidcVariantIdList the pidcVariantIdList to set
   */
  public void setPidcVariantIdList(final List<Long> pidcVariantIdList) {
    this.pidcVariantIdList = pidcVariantIdList;
  }


  /**
   * @return the codexResultsList
   */
  public List<CodexResults> getCodexResultsList() {
    return this.codexResultsList;
  }

  /**
   * @param codexResultsList the codexResultsList to set
   */
  public void setCodexResultsList(final List<CodexResults> codexResultsList) {
    this.codexResultsList = codexResultsList;
  }


}
