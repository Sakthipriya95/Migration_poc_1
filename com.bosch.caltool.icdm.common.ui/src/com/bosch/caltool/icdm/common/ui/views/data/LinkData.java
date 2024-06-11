/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.views.data;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.RuleLinks;
import com.bosch.caltool.icdm.model.general.Link;


/**
 * ICDM-1502 Class to store details of link
 *
 * @author mkl2cob
 */
public class LinkData implements Comparable<LinkData> {

  /**
   * nodeId
   */
  private Long nodeId;
  /**
   * nodeType
   */
  private String nodeType;
  /**
   * operation type whether it is add/edit/delete characterised by the first letter
   */
  private char oprType;

  /**
   * Link object
   */
  private Link linkObj;

  /**
   * Rule Link object
   */
  private RuleLinks ruleLinkObj;

  // old & new values
  private String newLink;


  private String newDescEng;

  private String newDescGer;
  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;

  /**
   * Constructor
   *
   * @param linkObj Link
   */
  public LinkData(final Object linkObj) {
    if (linkObj instanceof Link) {
      this.linkObj = (Link) linkObj;
      if (CommonUtils.isNotNull(this.linkObj)) {
        this.newLink = this.linkObj.getLinkUrl();
        this.newDescEng = this.linkObj.getDescriptionEng();
        this.newDescGer = this.linkObj.getDescriptionGer();
      }
    }
    else if (linkObj instanceof RuleLinks) {
      this.ruleLinkObj = (RuleLinks) linkObj;
      if (CommonUtils.isNotNull(this.ruleLinkObj)) {
        this.newLink = this.ruleLinkObj.getLink();
        this.newDescEng = this.ruleLinkObj.getDescEng();
        this.newDescGer = this.ruleLinkObj.getDescGer();
      }
    }
  }


  /**
   * @return the linkObj
   */
  public Link getLinkObj() {
    return this.linkObj;
  }

  /**
   * @return the newLink
   */
  public String getNewLink() {
    return this.newLink;
  }

  /**
   * @param linkObj to set
   */
  public void setLinkObj(final Link linkObj) {
    this.linkObj = linkObj;
  }


  /**
   * @return the ruleLinkObj
   */
  public RuleLinks getRuleLinkObj() {
    return this.ruleLinkObj;
  }


  /**
   * @param ruleLinkObj the ruleLinkObj to set
   */
  public void setRuleLinkObj(final RuleLinks ruleLinkObj) {
    this.ruleLinkObj = ruleLinkObj;
  }


  /**
   * @param newLink the newLink to set
   */
  public void setNewLink(final String newLink) {
    this.newLink = newLink;
  }


  /**
   * @return the newDescEng
   */
  public String getNewDescEng() {
    return this.newDescEng;
  }


  /**
   * @param newDescEng the newDescEng to set
   */
  public void setNewDescEng(final String newDescEng) {
    this.newDescEng = newDescEng;
  }


  /**
   * @return the newDescGer
   */
  public String getNewDescGer() {
    return this.newDescGer;
  }


  /**
   * @param newDescGer the newDescGer to set
   */
  public void setNewDescGer(final String newDescGer) {
    this.newDescGer = newDescGer;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final LinkData arg0) {
    return ApicUtil.compare(getNewDescEng(), arg0.getNewDescEng());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASH_CODE_PRIME_31 * result) + ((getNewDescEng() == null) ? 0 : getNewDescEng().hashCode());
    return result;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    LinkData other = (LinkData) obj;
    return CommonUtils.isEqual(getNewDescEng(), other.getNewDescEng());
  }

  /**
   * @return the oprType
   */
  public char getOprType() {
    return this.oprType;
  }


  /**
   * @param oprType the oprType to set
   */
  public void setOprType(final char oprType) {
    this.oprType = oprType;
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


}
