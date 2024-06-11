package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

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
 * The persistent class for the TABV_USE_CASE_GROUPS database table.
 */
@Entity
@OptimisticLocking(cascade = true)
@NamedQueries(value = { @NamedQuery(name = TabvUseCaseGroup.GET_ALL, query = "SELECT ug FROM TabvUseCaseGroup ug") })
@Table(name = "TABV_USE_CASE_GROUPS")
public class TabvUseCaseGroup implements Serializable {

  /**
   * Named query to get all attribute super groups
   */
  public static final String GET_ALL = "TabvUseCaseGroup.getAll";

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "GROUP_ID", unique = true, nullable = false)
  private long groupId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "DELETED_FLAG", length = 1)
  private String deletedFlag;

  @Column(name = "DESC_ENG", nullable = false, length = 255)
  private String descEng;

  @Column(name = "DESC_GER", length = 255)
  private String descGer;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;

  @Column(name = "NAME_ENG", nullable = false, length = 100)
  private String nameEng;

  @Column(name = "NAME_GER", length = 100)
  private String nameGer;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private Long version;

  // bi-directional many-to-one association to TabvUseCase
  @OneToMany(mappedBy = "tabvUseCaseGroup")
  @BatchFetch(value = BatchFetchType.JOIN)
  // Icdm-652
  @PrivateOwned
  private List<TabvUseCase> tabvUseCases;

  // bi-directional many-to-one association to TabvUseCaseGroup
  @ManyToOne(fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  @JoinColumn(name = "PARENT_GROUP_ID")
  // Icdm-652
  @PrivateOwned
  private TabvUseCaseGroup tabvUseCaseGroup;

  // bi-directional many-to-one association to TabvUseCaseGroup
  @OneToMany(mappedBy = "tabvUseCaseGroup")
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TabvUseCaseGroup> tabvUseCaseGroups;

  // bi-directional many-to-one association to TUsecaseFavorite
  @OneToMany(mappedBy = "tabvUseCaseGroup")
  private Set<TUsecaseFavorite> TUsecaseFavorites;

  public TabvUseCaseGroup() {}

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

  public String getDescEng() {
    return this.descEng;
  }

  public void setDescEng(final String descEng) {
    this.descEng = descEng;
  }

  public String getDescGer() {
    return this.descGer;
  }

  public void setDescGer(final String descGer) {
    this.descGer = descGer;
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

  public String getNameEng() {
    return this.nameEng;
  }

  public void setNameEng(final String nameEng) {
    this.nameEng = nameEng;
  }

  public String getNameGer() {
    return this.nameGer;
  }

  public void setNameGer(final String nameGer) {
    this.nameGer = nameGer;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public List<TabvUseCase> getTabvUseCases() {
    return this.tabvUseCases;
  }

  public void setTabvUseCases(final List<TabvUseCase> tabvUseCases) {
    this.tabvUseCases = tabvUseCases;
  }

  public TabvUseCaseGroup getTabvUseCaseGroup() {
    return this.tabvUseCaseGroup;
  }

  public void setTabvUseCaseGroup(final TabvUseCaseGroup tabvUseCaseGroup) {
    this.tabvUseCaseGroup = tabvUseCaseGroup;
  }

  public List<TabvUseCaseGroup> getTabvUseCaseGroups() {
    return this.tabvUseCaseGroups;
  }

  public void setTabvUseCaseGroups(final List<TabvUseCaseGroup> tabvUseCaseGroups) {
    this.tabvUseCaseGroups = tabvUseCaseGroups;
  }

  public Set<TUsecaseFavorite> getTUsecaseFavorites() {
    return this.TUsecaseFavorites;
  }

  public void setTUsecaseFavorites(final Set<TUsecaseFavorite> TUsecaseFavorites) {
    this.TUsecaseFavorites = TUsecaseFavorites;
  }

}