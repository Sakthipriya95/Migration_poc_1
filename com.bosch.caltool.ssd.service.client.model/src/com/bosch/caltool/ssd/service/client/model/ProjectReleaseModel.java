/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.ssd.service.client.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author QRK1COB
 *
 */
public class ProjectReleaseModel {

  private final Map<String, Object> properties = new HashMap<>();

  private String creDate;

  private String creUser;

  private String description;

  private String errors;

  private String globalChk;

  private String globalSsdheader;

  private Date modDate;

  private String modUser;

  private Long proRelId;

  private Long proRevId;

  private Integer relId;

  private String relType;


  /**
   * @return the creDate
   */
  public String getCreDate() {
    return this.creDate;
  }


  /**
   * @param creDate the creDate to set
   */
  public void setCreDate(final String creDate) {
    String datePart = creDate.split(" ")[0];
    this.creDate = datePart;
  }


  /**
   * @return the creUser
   */
  public String getCreUser() {
    return this.creUser;
  }


  /**
   * @param creUser the creUser to set
   */
  public void setCreUser(final String creUser) {
    this.creUser = creUser;
  }


  /**
   * @return the description
   */
  public String getDescription() {
    return this.description;
  }


  /**
   * @param description the description to set
   */
  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * @return the errors
   */
  public String getErrors() {
    return this.errors;
  }


  /**
   * @param errors the errors to set
   */
  public void setErrors(final String errors) {
    this.errors = errors;
  }


  /**
   * @return the globalChk
   */
  public String getGlobalChk() {
    return this.globalChk;
  }


  /**
   * @param globalChk the globalChk to set
   */
  public void setGlobalChk(final String globalChk) {
    this.globalChk = globalChk;
  }


  /**
   * @return the globalSsdheader
   */
  public String getGlobalSsdheader() {
    return this.globalSsdheader;
  }


  /**
   * @param globalSsdheader the globalSsdheader to set
   */
  public void setGlobalSsdheader(final String globalSsdheader) {
    this.globalSsdheader = globalSsdheader;
  }


  /**
   * @return the modDate
   */
  public Date getModDate() {
    return this.modDate;
  }


  /**
   * @param modDate the modDate to set
   */
  public void setModDate(final Date modDate) {
    this.modDate = modDate;
  }


  /**
   * @return the modUser
   */
  public String getModUser() {
    return this.modUser;
  }


  /**
   * @param modUser the modUser to set
   */
  public void setModUser(final String modUser) {
    this.modUser = modUser;
  }


  /**
   * @return the proRelId
   */
  public Long getProRelId() {
    return this.proRelId;
  }


  /**
   * @param proRelId the proRelId to set
   */
  public void setProRelId(final Long proRelId) {
    this.proRelId = proRelId;
  }


  /**
   * @return the proRevId
   */
  public Long getProRevId() {
    return this.proRevId;
  }


  /**
   * @param proRevId the proRevId to set
   */
  public void setProRevId(final Long proRevId) {
    this.proRevId = proRevId;
  }


  /**
   * @return the relId
   */
  public Integer getRelId() {
    return this.relId;
  }


  /**
   * @param relId the relId to set
   */
  public void setRelId(final Integer relId) {
    this.relId = relId;
  }


  /**
   * @return the relType
   */
  public String getRelType() {
    return this.relType;
  }


  /**
   * @param relType the relType to set
   */
  public void setRelType(final String relType) {
    this.relType = relType;
  }

  /**
   * @param key key
   * @param value value
   */
  public void setProperty(final String key, final Object value) {
    if (value != null) {
      switch (key) {
        case "creDate":
          setCreDate((String) value);
          break;
        case "creUser":
          setCreUser((String) value);
          break;
        case "description":
          setDescription((String) value);
          break;
        case "errors":
          setErrors((String) value);
          break;
        case "globalChk":
          setGlobalChk((String) value);
          break;
        case "globalSsdheader":
          setGlobalSsdheader((String) value);
          break;
        case "modDate":
          setModDate((Date) value);
          break;
        case "modUser":
          setModUser((String) value);
          break;
        case "proRelId":
          setProRelId((Long) value);
          break;
        case "proRevId":
          setProRevId((Long) value);
          break;
        case "relId":
          setRelId((Integer) value);
          break;
        case "relType":
          setRelType((String) value);
          break;
        // Add more cases for other properties if needed
        default:
          break;
      }
    }
  }

  /**
   * @param key key
   * @return value
   */
  public String getStringProperty(final String key) {
    Object value = this.properties.get(key);
    return (value instanceof String) ? (String) value : null;
  }

  /**
   * @param key key
   * @return value
   */
  public Long getLongProperty(final String key) {
    Object value = this.properties.get(key);
    return (value instanceof Long) ? (Long) value : null;
  }

  /**
   * @param key key
   * @return value
   */
  public Integer getIntegerProperty(final String key) {
    Object value = this.properties.get(key);
    return (value instanceof Integer) ? (Integer) value : null;
  }

}