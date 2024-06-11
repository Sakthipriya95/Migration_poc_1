package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;


/**
 * The persistent class for the TABV_ATTR_DEPENDENCIES database table.
 */

@Entity
@Table(name = "TABV_ATTR_DEPENDENCIES")
public class TabvAttrDependency implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "DEPEN_ID", unique = true, nullable = false, precision = 15)
  private long depenId;

  @Column(name = "DELETED_FLAG", nullable = false, length = 1)
  private String deletedFlag;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;

  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;
  // bi-directional many-to-one association to TabvAttribute
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ATTR_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TabvAttribute tabvAttribute;

  // bi-directional many-to-one association to TabvAttribute
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "DEPEN_ATTR_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TabvAttribute tabvAttributeD;

  // bi-directional many-to-one association to TabvAttrValue
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "VALUE_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TabvAttrValue tabvAttrValue;

  // bi-directional many-to-one association to TabvAttrValue
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "DEPEN_VALUE_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TabvAttrValue tabvAttrValueD;

  @Column(name = "CHANGE_COMMENT", length = 4000)
  private String changeComment;

  public TabvAttrDependency() {}

  /**
   * @return the changeComment
   */
  public String getChangeComment() {
    return this.changeComment;
  }


  /**
   * @param changeComment the changeComment to set
   */
  public void setChangeComment(final String changeComment) {
    this.changeComment = changeComment;
  }

  public long getDepenId() {
    return this.depenId;
  }

  public void setDepenId(final long depenId) {
    this.depenId = depenId;
  }

  public String getDeletedFlag() {
    return this.deletedFlag;
  }

  public void setDeletedFlag(final String deletedFlag) {
    this.deletedFlag = deletedFlag;
  }

  public TabvAttribute getTabvAttribute() {
    return this.tabvAttribute;
  }

  public void setTabvAttribute(final TabvAttribute tabvAttribute) {
    this.tabvAttribute = tabvAttribute;
  }

  public TabvAttribute getTabvAttributeD() {
    return this.tabvAttributeD;
  }

  public void setTabvAttributeD(final TabvAttribute tabvAttributeD) {
    this.tabvAttributeD = tabvAttributeD;
  }

  public TabvAttrValue getTabvAttrValue() {
    return this.tabvAttrValue;
  }

  public void setTabvAttrValue(final TabvAttrValue tabvAttrValue) {
    this.tabvAttrValue = tabvAttrValue;
  }

  public TabvAttrValue getTabvAttrValueD() {
    return this.tabvAttrValueD;
  }

  public void setTabvAttrValueD(final TabvAttrValue tabvAttrValueD) {
    this.tabvAttrValueD = tabvAttrValueD;
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