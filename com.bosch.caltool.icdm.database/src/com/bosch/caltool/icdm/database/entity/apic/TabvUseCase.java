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
import org.eclipse.persistence.annotations.Customizer;
import org.eclipse.persistence.annotations.OptimisticLocking;
import org.eclipse.persistence.annotations.PrivateOwned;


/**
 * The persistent class for the TABV_USE_CASES database table.
 */
@Entity
@OptimisticLocking(cascade = true)
// Icdm-652
@Customizer(OptimisticLockCustomizer.class)
@NamedQueries(value = { @NamedQuery(name = TabvUseCase.GET_ALL_USE_CASES, query = "SELECT uc FROM TabvUseCase uc") })
@Table(name = "TABV_USE_CASES")
public class TabvUseCase implements Serializable {


  /**
   * Named query to get all attribute super groups
   */
  public static final String GET_ALL_USE_CASES = "TabvUseCase.GET_ALL_USE_CASES";

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "USE_CASE_ID", unique = true, nullable = false)
  private long useCaseId;

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

  @Column(name = "FOCUS_MATRIX_YN", length = 1)
  private String focusMatrixRelevant;

  @Column(name = "LAST_CONFIRMATION_DATE")
  private Timestamp lastConfirmationDate;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private Long version;

  // bi-directional many-to-one association to TabvUcpAttr
  @OneToMany(mappedBy = "tabvUseCas", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  // Icdm-652
  @PrivateOwned
  private List<TabvUcpAttr> tabvUcpAttrs;

  // bi-directional many-to-one association to TabvUseCaseGroup
  @ManyToOne(fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  @JoinColumn(name = "GROUP_ID", nullable = false)
  private TabvUseCaseGroup tabvUseCaseGroup;

  // bi-directional many-to-one association to TabvUseCaseSection
  @OneToMany(mappedBy = "tabvUseCas", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  // Icdm-652
  @PrivateOwned
  private List<TabvUseCaseSection> tabvUseCaseSections;


  // bi-directional many-to-one association to TUsecaseFavorite
  @OneToMany(mappedBy = "tabvUseCas")
  private Set<TUsecaseFavorite> TUsecaseFavorites;

  public TabvUseCase() {}

  public long getUseCaseId() {
    return this.useCaseId;
  }

  public void setUseCaseId(final long useCaseId) {
    this.useCaseId = useCaseId;
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

  public List<TabvUcpAttr> getTabvUcpAttrs() {
    return this.tabvUcpAttrs;
  }

  public void setTabvUcpAttrs(final List<TabvUcpAttr> tabvUcpAttrs) {
    this.tabvUcpAttrs = tabvUcpAttrs;
  }

  public TabvUseCaseGroup getTabvUseCaseGroup() {
    return this.tabvUseCaseGroup;
  }

  public void setTabvUseCaseGroup(final TabvUseCaseGroup tabvUseCaseGroup) {
    this.tabvUseCaseGroup = tabvUseCaseGroup;
  }

  public List<TabvUseCaseSection> getTabvUseCaseSections() {
    return this.tabvUseCaseSections;
  }

  public void setTabvUseCaseSections(final List<TabvUseCaseSection> tabvUseCaseSections) {
    this.tabvUseCaseSections = tabvUseCaseSections;
  }

  // public List<TabvAttribute> getTabvAttributes() {
  // return this.tabvAttributes;
  // }
  //
  // public void setTabvAttributes(final List<TabvAttribute> tabvAttributes) {
  // this.tabvAttributes = tabvAttributes;
  // }

  public Set<TUsecaseFavorite> getTUsecaseFavorites() {
    return this.TUsecaseFavorites;
  }

  public void setTUsecaseFavorites(final Set<TUsecaseFavorite> TUsecaseFavorites) {
    this.TUsecaseFavorites = TUsecaseFavorites;
  }


  /**
   * @return the matrixRelevant
   */
  public String getFocusMatrixRelevant() {
    return this.focusMatrixRelevant;
  }


  /**
   * @param matrixRelevant the matrixRelevant to set
   */
  public void setFocusMatrixRelevant(final String matrixRelevant) {
    this.focusMatrixRelevant = matrixRelevant;
  }

  public Timestamp getLastConfirmationDate() {
    return this.lastConfirmationDate;
  }

  public void setLastConfirmationDate(final Timestamp lastConfirmationDate) {
    this.lastConfirmationDate = lastConfirmationDate;
  }

}