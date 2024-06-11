package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
 * The persistent class for the TABV_ATTR_GROUPS database table.
 */
@NamedQueries(value = { @NamedQuery(name = TabvAttrGroup.GET_ALL, query = "select grp from TabvAttrGroup grp") })
@Entity
@OptimisticLocking(cascade = true)
@Table(name = "TABV_ATTR_GROUPS")
public class TabvAttrGroup implements Serializable {

  private static final long serialVersionUID = 1L;


  public static final String GET_ALL = "TabvAttrGroup.getAll";


  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "GROUP_ID", unique = true, nullable = false, precision = 15)
  private long groupId;

  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 100)
  private String createdUser;

  @Column(name = "DELETED_FLAG", nullable = false, length = 1)
  private String deletedFlag;

  @Column(name = "GROUP_DESC_ENG", length = 255)
  private String groupDescEng;

  @Column(name = "GROUP_DESC_GER", length = 255)
  private String groupDescGer;

  @Column(name = "GROUP_NAME_ENG", nullable = false, length = 100)
  private String groupNameEng;

  @Column(name = "GROUP_NAME_GER", length = 100)
  private String groupNameGer;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 100)
  private String modifiedUser;

  // bi-directional many-to-one association to TabvAttribute
  @OneToMany(mappedBy = "tabvAttrGroup")
  @BatchFetch(value = BatchFetchType.JOIN)
  // Icdm-230
  @PrivateOwned
  private List<TabvAttribute> tabvAttributes;

  // bi-directional many-to-one association to TabvAttrSuperGroup
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "SUPER_GROUP_ID", nullable = false)
  @BatchFetch(value = BatchFetchType.JOIN)
  private TabvAttrSuperGroup tabvAttrSuperGroup;

  public TabvAttrGroup() {}

  public long getGroupId() {
    return this.groupId;
  }

  public void setGroupId(final long groupId) {
    this.groupId = groupId;
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

  public String getGroupDescEng() {
    return this.groupDescEng;
  }

  public void setGroupDescEng(final String groupDescEng) {
    this.groupDescEng = groupDescEng;
  }

  public String getGroupDescGer() {
    return this.groupDescGer;
  }

  public void setGroupDescGer(final String groupDescGer) {
    this.groupDescGer = groupDescGer;
  }

  public String getGroupNameEng() {
    return this.groupNameEng;
  }

  public void setGroupNameEng(final String groupNameEng) {
    this.groupNameEng = groupNameEng;
  }

  public String getGroupNameGer() {
    return this.groupNameGer;
  }

  public void setGroupNameGer(final String groupNameGer) {
    this.groupNameGer = groupNameGer;
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

  public List<TabvAttribute> getTabvAttributes() {
    return this.tabvAttributes;
  }

  public void setTabvAttributes(final List<TabvAttribute> tabvAttributes) {
    this.tabvAttributes = tabvAttributes;
  }

  public void addTabvAttribute(final TabvAttribute tabvAttribute) {
    if (getTabvAttributes() == null) {
      setTabvAttributes(new ArrayList<>());
    }
    getTabvAttributes().add(tabvAttribute);
    tabvAttribute.setTabvAttrGroup(this);
  }

  public TabvAttrSuperGroup getTabvAttrSuperGroup() {
    return this.tabvAttrSuperGroup;
  }

  public void setTabvAttrSuperGroup(final TabvAttrSuperGroup tabvAttrSuperGroup) {
    this.tabvAttrSuperGroup = tabvAttrSuperGroup;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

}