/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.database.entity.cdr;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the T_RULE_LINKS database table.
 */
@Entity
@Table(name = "T_RULE_LINKS")
@NamedQueries(value = {
    @NamedQuery(name = TRuleLinks.NQ_GET_BY_RULE_ID, query = "SELECT t FROM TRuleLinks t where t.ruleId = :ruleId"),
    @NamedQuery(name = TRuleLinks.NQ_GET_BY_RULE_REV_ID, query = "SELECT t FROM TRuleLinks t where t.ruleId = :ruleId and t.revId = :revId "),
    @NamedQuery(name = TRuleLinks.NQ_GET_BY_RULE_ID_GTT, query = "Select tRuleLink                             " +
        "from TRuleLinks tRuleLink, GttRuleLinks tempRuleLink                                                " +
        "where tRuleLink.ruleId = tempRuleLink.ruleId and tRuleLink.revId = tempRuleLink.revId             ") })
public class TRuleLinks implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Get Links by rule-id, rev-id
   */
  public static final String NQ_GET_BY_RULE_REV_ID = "TRuleLinks.findByRuleRevId";

  /**
   * Get Links by rule ID
   */
  public static final String NQ_GET_BY_RULE_ID = "TRuleLinks.findByRuleId";

  /**
   * Get Links by rule-id, rev-id using temp table (many records)
   */
  public static final String NQ_GET_BY_RULE_ID_GTT = "TRuleLinks.findByRuleIdGtt";


  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "RULE_LINK_ID")
  private long ruleLinkId;

  @Column(name = "RULE_ID")
  private Long ruleId;

  @Column(name = "REV_ID")
  private Long revId;

  @Column(name = "Link", nullable = false, length = 1000)
  private String link;

  @Column(name = "DESC_ENG", nullable = false, length = 2000)
  private String descEng;

  @Column(name = "DESC_GER", length = 2000)
  private String descGer;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Version
  @Column(name = "\"VERSION\"")
  private Long version;

  public TRuleLinks() {}


  /**
   * @return the ruleLinkId
   */
  public long getRuleLinkId() {
    return this.ruleLinkId;
  }


  /**
   * @param ruleLinkId the ruleLinkId to set
   */
  public void setRuleLinkId(final long ruleLinkId) {
    this.ruleLinkId = ruleLinkId;
  }

  public Long getRevId() {
    return this.revId;
  }

  public void setRevId(final Long revId) {
    this.revId = revId;
  }

  public Long getRuleId() {
    return this.ruleId;
  }

  public void setRuleId(final Long ruleId) {
    this.ruleId = ruleId;
  }


  /**
   * @return the link
   */
  public String getLink() {
    return this.link;
  }


  /**
   * @param link the link to set
   */
  public void setLink(final String link) {
    this.link = link;
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


  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }

  public String getCreatedUser() {
    return this.createdUser;
  }

  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }

  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public String getModifiedUser() {
    return this.modifiedUser;
  }

  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

}
