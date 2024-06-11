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
 * The persistent class for the T_USECASE_FAVORITES database table.
 */
@Entity
@Table(name = "T_USECASE_FAVORITES")
public class TUsecaseFavorite implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "UC_FAV_ID", unique = true, nullable = false, precision = 15)
  private long ucFavId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 100)
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 100)
  private String modifiedUser;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private long version;

  // bi-directional many-to-one association to TabvApicUser
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "USER_ID")
  private TabvApicUser tabvApicUser;

  // bi-directional many-to-one association to TabvProjectidcard
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PROJECT_ID")
  private TabvProjectidcard tabvProjectidcard;

  // bi-directional many-to-one association to TabvUseCas
  @ManyToOne(fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  @JoinColumn(name = "USE_CASE_ID")
  private TabvUseCase tabvUseCas;

  // bi-directional many-to-one association to TabvUseCaseGroup
  @ManyToOne(fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  @JoinColumn(name = "GROUP_ID")
  private TabvUseCaseGroup tabvUseCaseGroup;

  // bi-directional many-to-one association to TabvUseCaseSection
  @ManyToOne(fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  @JoinColumn(name = "SECTION_ID")
  private TabvUseCaseSection tabvUseCaseSection;

  public TUsecaseFavorite() {}

  public long getUcFavId() {
    return this.ucFavId;
  }

  public void setUcFavId(final long ucFavId) {
    this.ucFavId = ucFavId;
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

  public long getVersion() {
    return this.version;
  }

  public void setVersion(final long version) {
    this.version = version;
  }

  public TabvApicUser getTabvApicUser() {
    return this.tabvApicUser;
  }

  public void setTabvApicUser(final TabvApicUser tabvApicUser) {
    this.tabvApicUser = tabvApicUser;
  }

  public TabvProjectidcard getTabvProjectidcard() {
    return this.tabvProjectidcard;
  }

  public void setTabvProjectidcard(final TabvProjectidcard tabvProjectidcard) {
    this.tabvProjectidcard = tabvProjectidcard;
  }

  public TabvUseCase getTabvUseCas() {
    return this.tabvUseCas;
  }

  public void setTabvUseCas(final TabvUseCase tabvUseCas) {
    this.tabvUseCas = tabvUseCas;
  }

  public TabvUseCaseGroup getTabvUseCaseGroup() {
    return this.tabvUseCaseGroup;
  }

  public void setTabvUseCaseGroup(final TabvUseCaseGroup tabvUseCaseGroup) {
    this.tabvUseCaseGroup = tabvUseCaseGroup;
  }

  public TabvUseCaseSection getTabvUseCaseSection() {
    return this.tabvUseCaseSection;
  }

  public void setTabvUseCaseSection(final TabvUseCaseSection tabvUseCaseSection) {
    this.tabvUseCaseSection = tabvUseCaseSection;
  }

}