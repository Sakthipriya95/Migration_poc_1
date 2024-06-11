package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the T_RM_PROJECT_CHARACTER database table.
 */
@Entity
@Table(name = "T_RM_PROJECT_CHARACTER")
@NamedQuery(name = "TRmProjectCharacter.findAll", query = "SELECT t FROM TRmProjectCharacter t")
public class TRmProjectCharacter implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final String GET_ALL = "TRmProjectCharacter.findAll";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "PRJ_CHARACTER_ID", unique = true, nullable = false)
  private long prjCharacterId;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "DELETED_FLAG")
  private String deletedFlag;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "PROJECT_CHARACTER_ENG")
  private String projectCharacterEng;

  @Column(name = "PROJECT_CHARACTER_GER")
  private String projectCharacterGer;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private long version;

  // bi-directional many-to-one association to TPidcRmProjectCharacter
  @OneToMany(mappedBy = "TRmProjectCharacter")
  private Set<TPidcRmProjectCharacter> TPidcRmProjectCharacters;

  // bi-directional many-to-one association to TRmCharacterCategoryMatrix
  @OneToMany(mappedBy = "TRmProjectCharacter")
  private Set<TRmCharacterCategoryMatrix> TRmCharacterCategoryMatrixs;

  // bi-directional many-to-one association to TRmProjectCharacter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PARENT_PRJ_CHARACTER_ID")
  private TRmProjectCharacter TRmProjectCharacter;

  // bi-directional many-to-one association to TRmProjectCharacter
  @OneToMany(mappedBy = "TRmProjectCharacter")
  private Set<TRmProjectCharacter> TRmProjectCharacters;

  public TRmProjectCharacter() {}

  public long getPrjCharacterId() {
    return this.prjCharacterId;
  }

  public void setPrjCharacterId(final long prjCharacterId) {
    this.prjCharacterId = prjCharacterId;
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

  public String getProjectCharacterEng() {
    return this.projectCharacterEng;
  }

  public void setProjectCharacterEng(final String projectCharacterEng) {
    this.projectCharacterEng = projectCharacterEng;
  }

  public String getProjectCharacterGer() {
    return this.projectCharacterGer;
  }

  public void setProjectCharacterGer(final String projectCharacterGer) {
    this.projectCharacterGer = projectCharacterGer;
  }

  /**
   * @return
   */
  public long getVersion() {
    return this.version;
  }

  public void setVersion(final long version) {
    this.version = version;
  }

  public Set<TPidcRmProjectCharacter> getTPidcRmProjectCharacters() {
    return this.TPidcRmProjectCharacters;
  }

  public void setTPidcRmProjectCharacters(final Set<TPidcRmProjectCharacter> TPidcRmProjectCharacters) {
    this.TPidcRmProjectCharacters = TPidcRmProjectCharacters;
  }

  public TPidcRmProjectCharacter addTPidcRmProjectCharacter(final TPidcRmProjectCharacter TPidcRmProjectCharacter) {
    getTPidcRmProjectCharacters().add(TPidcRmProjectCharacter);
    TPidcRmProjectCharacter.setTRmProjectCharacter(this);

    return TPidcRmProjectCharacter;
  }

  public TPidcRmProjectCharacter removeTPidcRmProjectCharacter(final TPidcRmProjectCharacter TPidcRmProjectCharacter) {
    getTPidcRmProjectCharacters().remove(TPidcRmProjectCharacter);
    TPidcRmProjectCharacter.setTRmProjectCharacter(null);

    return TPidcRmProjectCharacter;
  }

  public Set<TRmCharacterCategoryMatrix> getTRmCharacterCategoryMatrixs() {
    return this.TRmCharacterCategoryMatrixs;
  }

  public void setTRmCharacterCategoryMatrixs(final Set<TRmCharacterCategoryMatrix> TRmCharacterCategoryMatrixs) {
    this.TRmCharacterCategoryMatrixs = TRmCharacterCategoryMatrixs;
  }

  public TRmCharacterCategoryMatrix addTRmCharacterCategoryMatrix(
      final TRmCharacterCategoryMatrix TRmCharacterCategoryMatrix) {
    getTRmCharacterCategoryMatrixs().add(TRmCharacterCategoryMatrix);
    TRmCharacterCategoryMatrix.setTRmProjectCharacter(this);

    return TRmCharacterCategoryMatrix;
  }

  public TRmCharacterCategoryMatrix removeTRmCharacterCategoryMatrix(
      final TRmCharacterCategoryMatrix TRmCharacterCategoryMatrix) {
    getTRmCharacterCategoryMatrixs().remove(TRmCharacterCategoryMatrix);
    TRmCharacterCategoryMatrix.setTRmProjectCharacter(null);

    return TRmCharacterCategoryMatrix;
  }

  public TRmProjectCharacter getTRmProjectCharacter() {
    return this.TRmProjectCharacter;
  }

  public void setTRmProjectCharacter(final TRmProjectCharacter TRmProjectCharacter) {
    this.TRmProjectCharacter = TRmProjectCharacter;
  }

  public Set<TRmProjectCharacter> getTRmProjectCharacters() {
    return this.TRmProjectCharacters;
  }

  public void setTRmProjectCharacters(final Set<TRmProjectCharacter> TRmProjectCharacters) {
    this.TRmProjectCharacters = TRmProjectCharacters;
  }

  public TRmProjectCharacter addTRmProjectCharacter(final TRmProjectCharacter TRmProjectCharacter) {
    getTRmProjectCharacters().add(TRmProjectCharacter);
    TRmProjectCharacter.setTRmProjectCharacter(this);

    return TRmProjectCharacter;
  }

  public TRmProjectCharacter removeTRmProjectCharacter(final TRmProjectCharacter TRmProjectCharacter) {
    getTRmProjectCharacters().remove(TRmProjectCharacter);
    TRmProjectCharacter.setTRmProjectCharacter(null);

    return TRmProjectCharacter;
  }

}