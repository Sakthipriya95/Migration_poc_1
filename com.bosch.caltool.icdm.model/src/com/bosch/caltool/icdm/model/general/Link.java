/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.general;

import java.util.Date;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * @author bne4cob
 */
public class Link implements IModel, Comparable<Link> {

  /**
   *
   */
  private static final long serialVersionUID = 185700417081125828L;

  private Long id;

  private Long nodeId;
  private String nodeType;

  private String description;
  private String descriptionEng;
  private String descriptionGer;

  private String linkUrl;

  private Date createdDate;
  private String createdUser;
  private Date modifiedDate;
  private String modifiedUser;

  private Long version;

  private Long attributeValueId;


  /**
   * @return the attributeValueId
   */
  public Long getAttributeValueId() {
    return this.attributeValueId;
  }


  /**
   * @param attributeValueId the attributeValueId to set
   */
  public void setAttributeValueId(final Long attributeValueId) {
    this.attributeValueId = attributeValueId;
  }


  /**
   * @return the id
   */
  @Override
  public Long getId() {
    return this.id;
  }


  /**
   * @param id the id to set
   */
  @Override
  public void setId(final Long id) {
    this.id = id;
  }


  /**
   * @return the nodeId
   */
  public Long getNodeId() {
    return this.nodeId;
  }


  /**
   * @param nodeId the nodeId to set
   */
  public void setNodeId(final Long nodeId) {
    this.nodeId = nodeId;
  }


  /**
   * @return the nodeType
   */
  public String getNodeType() {
    return this.nodeType;
  }


  /**
   * @param nodeType the nodeType to set
   */
  public void setNodeType(final String nodeType) {
    this.nodeType = nodeType;
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
   * @return the descriptionEng
   */
  public String getDescriptionEng() {
    return this.descriptionEng;
  }


  /**
   * @param descriptionEng the descriptionEng to set
   */
  public void setDescriptionEng(final String descriptionEng) {
    this.descriptionEng = descriptionEng;
  }


  /**
   * @return the descriptionGer
   */
  public String getDescriptionGer() {
    return this.descriptionGer;
  }


  /**
   * @param descriptionGer the descriptionGer to set
   */
  public void setDescriptionGer(final String descriptionGer) {
    this.descriptionGer = descriptionGer;
  }


  /**
   * @return the linkUrl
   */
  public String getLinkUrl() {
    return this.linkUrl;
  }


  /**
   * @param linkUrl the linkUrl to set
   */
  public void setLinkUrl(final String linkUrl) {
    this.linkUrl = linkUrl;
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
   * @return the createdUser
   */
  public String getCreatedUser() {
    return this.createdUser;
  }


  /**
   * @param createdUser the createdUser to set
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }


  /**
   * @return the modifiedDate
   */
  public Date getModifiedDate() {
    return this.modifiedDate;
  }


  /**
   * @param modifiedDate the modifiedDate to set
   */
  public void setModifiedDate(final Date modifiedDate) {
    this.modifiedDate = modifiedDate;
  }


  /**
   * @return the modifiedUser
   */
  public String getModifiedUser() {
    return this.modifiedUser;
  }


  /**
   * @param modifiedUser the modifiedUser to set
   */
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }


  /**
   * @return the version
   */
  @Override
  public Long getVersion() {
    return this.version;
  }


  /**
   * @param version the version to set
   */
  @Override
  public void setVersion(final Long version) {
    this.version = version;
  }

  /**
   * returns compare result of two links
   * <p>
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final Link arg0) {
    int compareResult = ModelUtil.compare(getDescription(), arg0.getDescription());
    if (compareResult == 0) {
      compareResult = ModelUtil.compare(getId(), arg0.getId());
    }
    return compareResult;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Link clone() {
    Link qNaireAns = null;
    try {
      qNaireAns = (Link) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      // TODO
    }
    return qNaireAns;
  }
}
