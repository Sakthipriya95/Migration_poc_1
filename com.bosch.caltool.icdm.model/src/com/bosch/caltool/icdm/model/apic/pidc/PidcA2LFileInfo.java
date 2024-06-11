/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author bru2cob
 */
public class PidcA2LFileInfo {

  /**
   * sdom pver name
   */
  private String pverName;
  /**
   * sdom pver variant name
   */
  private String pverVariant;
  /**
   * a2l name
   */
  private String fileName;
  /**
   * a2l created date
   */
  private Date createdDate;
  /**
   * Date when a2l mapped to a version
   */
  private Date assignedDate;
  /**
   * a2l file id
   */
  private Long a2lFileID;

  /**
   * pidc a2l file id
   */
  private Long pidcA2lFileId;
  /**
   * List of variant id's belonging to a2l
   */
  private List<Long> variantIDList = new ArrayList<>();


  /**
   * @return the a2lFileID
   */
  public Long getA2lFileID() {
    return this.a2lFileID;
  }


  /**
   * @param a2lFileID the a2lFileID to set
   */
  public void setA2lFileID(final Long a2lFileID) {
    this.a2lFileID = a2lFileID;
  }


  /**
   * @return the variantIDList
   */
  public List<Long> getVariantIDList() {
    return this.variantIDList;
  }


  /**
   * @param variantIDList the variantIDList to set
   */
  public void setVariantIDList(final List<Long> variantIDList) {
    this.variantIDList = variantIDList == null ? null : new ArrayList<>(variantIDList);
  }


  /**
   * @return the pverName
   */
  public String getPverName() {
    return this.pverName;
  }


  /**
   * @param pverName the pverName to set
   */
  public void setPverName(final String pverName) {
    this.pverName = pverName;
  }


  /**
   * @return the pverVariant
   */
  public String getPverVariant() {
    return this.pverVariant;
  }


  /**
   * @param pverVariant the pverVariant to set
   */
  public void setPverVariant(final String pverVariant) {
    this.pverVariant = pverVariant;
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
   * @return the createdDate
   */
  public Date getCreatedDate() {
    return this.createdDate;
  }


  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(final Date createdDate) {
    this.createdDate = createdDate;
  }


  /**
   * @return the assignedDate
   */
  public Date getAssignedDate() {
    return this.assignedDate;
  }


  /**
   * @param assignedDate the assignedDate to set
   */
  public void setAssignedDate(final Date assignedDate) {
    this.assignedDate = assignedDate;
  }


  /**
   * @return the pidcA2lFileId
   */
  public Long getPidcA2lFileId() {
    return this.pidcA2lFileId;
  }


  /**
   * @param pidcA2lFileId the pidcA2lFileId to set
   */
  public void setPidcA2lFileId(final Long pidcA2lFileId) {
    this.pidcA2lFileId = pidcA2lFileId;
  }


}
