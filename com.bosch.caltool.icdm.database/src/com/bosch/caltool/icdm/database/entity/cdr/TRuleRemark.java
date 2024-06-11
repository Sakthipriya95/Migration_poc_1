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

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.TypeConverter;
import org.eclipse.persistence.platform.database.oracle.NClob;


/**
 * The persistent class for the T_RULE_REMARKS database table.
 */
@Entity
@Table(name = "T_RULE_REMARKS")
@NamedQueries(value = {
    @NamedQuery(name = TRuleRemark.NQ_GET_BY_RULE_ID, query = "SELECT t FROM TRuleRemark t where t.ruleId = :ruleId"),
    @NamedQuery(name = TRuleRemark.NQ_GET_BY_RULE_REV_ID, query = "SELECT t FROM TRuleRemark t where t.ruleId = :ruleId and t.revId = :revId "),
    @NamedQuery(name = TRuleRemark.NQ_GET_BY_RULE_ID_GTT, query = "Select tRuleRemark                              " +
        "from TRuleRemark tRuleRemark, GttRuleRemark tempRuleRemark                                                " +
        "where tRuleRemark.ruleId = tempRuleRemark.ruleId and tRuleRemark.revId = tempRuleRemark.revId             ") })

public class TRuleRemark implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Get remarks by rule-id, rev-id
   */
  public static final String NQ_GET_BY_RULE_REV_ID = "TRuleRemark.findByRuleRevId";

  /**
   * Get remarks by rule-id, rev-id using temp table (many records)
   */
  public static final String NQ_GET_BY_RULE_ID_GTT = "TRuleRemark.findByRuleIdGtt";

  /**
   * Get remarks by rule ID
   */
  public static final String NQ_GET_BY_RULE_ID = "TRuleRemark.findByRuleId";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "RULE_REMARK_ID")
  private long ruleRemarkId;

  @Column(name = "RULE_ID")
  private Long ruleId;

  @Column(name = "REV_ID")
  private Long revId;

  @TypeConverter(name = "nclobStringConverter", dataType = NClob.class, objectType = String.class)
  @Convert("nclobStringConverter")
  @Column(name = "REMARK")
  private String remark;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "\"VERSION\"")
  private Long version;

  public TRuleRemark() {}

  public Long getRuleRemarkId() {
    return this.ruleRemarkId;
  }

  public void setRuleRemarkId(final long ruleRemarkId) {
    this.ruleRemarkId = ruleRemarkId;
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

  public String getRemark() {
    return this.remark;
  }

  public void setRemark(final String remark) {
    this.remark = remark;
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

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

}