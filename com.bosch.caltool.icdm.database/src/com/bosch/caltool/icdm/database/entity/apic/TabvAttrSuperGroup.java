package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.annotations.OptimisticLocking;
import org.eclipse.persistence.annotations.PrivateOwned;


/**
 * The persistent class for the TABV_ATTR_SUPER_GROUPS database table.
 */
@NamedQueries(value = { @NamedQuery(name = TabvAttrSuperGroup.NQ_GET_ALL_SUPER_GROUPS, query = "SELECT sg FROM TabvAttrSuperGroup sg") })
@Entity
// Icdm-230
@OptimisticLocking(cascade = true)
@Table(name = "TABV_ATTR_SUPER_GROUPS")
public class TabvAttrSuperGroup implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Named query to get all attribute super groups
   * 
   * @return list of TabvAttrSuperGroup
   */
  public static final String NQ_GET_ALL_SUPER_GROUPS = "TabvAttrSuperGroup.getAllSuperGroups";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "SUPER_GROUP_ID", unique = true, nullable = false, precision = 15)
  private long superGroupId;

  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 100)
  private String createdUser;

  @Column(name = "DELETED_FLAG", nullable = false, length = 1)
  private String deletedFlag;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 100)
  private String modifiedUser;

  @Column(name = "SUPER_GROUP_DESC_ENG", length = 255)
  private String superGroupDescEng;

  @Column(name = "SUPER_GROUP_DESC_GER", length = 255)
  private String superGroupDescGer;

  @Column(name = "SUPER_GROUP_NAME_ENG", nullable = false, length = 100)
  private String superGroupNameEng;

  @Column(name = "SUPER_GROUP_NAME_GER", length = 100)
  private String superGroupNameGer;

  // bi-directional many-to-one association to TabvAttrGroup
  @OneToMany(mappedBy = "tabvAttrSuperGroup")
  @BatchFetch(value = BatchFetchType.JOIN)
  // Icdm-230
  @PrivateOwned
  private List<TabvAttrGroup> tabvAttrGroups;

  public TabvAttrSuperGroup() {}

  public long getSuperGroupId() {
    return this.superGroupId;
  }

  public void setSuperGroupId(final long superGroupId) {
    this.superGroupId = superGroupId;
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

  public String getDeletedFlag() {
    return this.deletedFlag;
  }

  public void setDeletedFlag(final String deletedFlag) {
    this.deletedFlag = deletedFlag;
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

  public String getSuperGroupDescEng() {
    return this.superGroupDescEng;
  }

  public void setSuperGroupDescEng(final String superGroupDescEng) {
    this.superGroupDescEng = superGroupDescEng;
  }

  public String getSuperGroupDescGer() {
    return this.superGroupDescGer;
  }

  public void setSuperGroupDescGer(final String superGroupDescGer) {
    this.superGroupDescGer = superGroupDescGer;
  }

  public String getSuperGroupNameEng() {
    return this.superGroupNameEng;
  }

  public void setSuperGroupNameEng(final String superGroupNameEng) {
    this.superGroupNameEng = superGroupNameEng;
  }

  public String getSuperGroupNameGer() {
    return this.superGroupNameGer;
  }

  public void setSuperGroupNameGer(final String superGroupNameGer) {
    this.superGroupNameGer = superGroupNameGer;
  }

  public List<TabvAttrGroup> getTabvAttrGroups() {
    return this.tabvAttrGroups;
  }

  public void setTabvAttrGroups(final List<TabvAttrGroup> tabvAttrGroups) {
    this.tabvAttrGroups = tabvAttrGroups;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

}