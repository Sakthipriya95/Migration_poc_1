package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the T_RM_CATEGORY database table.
 */
@Entity
@Table(name = "T_RM_CATEGORY")
@NamedQuery(name = "TRmCategory.findAll", query = "SELECT t FROM TRmCategory t")
public class TRmCategory implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final String GET_ALL = "TRmCategory.findAll";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "CATEGORY_ID", unique = true, nullable = false)
  private long categoryId;

  @Column(name = "CATEGORY_CODE")
  private String categoryCode;

  @Column(name = "CATEGORY_ENG")
  private String categoryEng;

  @Column(name = "CATEGORY_GER")
  private String categoryGer;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private long version;

  @Column(name = "CAT_TYPE")
  private String catType;

  // bi-directional many-to-one association to TPidcRmProjectCharacter
  @OneToMany(mappedBy = "rbShare")
  private Set<TPidcRmProjectCharacter> TPidcRmProjectCharacters1;

  // bi-directional many-to-one association to TPidcRmProjectCharacter
  @OneToMany(mappedBy = "rbData")
  private Set<TPidcRmProjectCharacter> TPidcRmProjectCharacters2;

  // bi-directional many-to-one association to TRmCategoryMeasure
  @OneToMany(mappedBy = "TRmCategory")
  private Set<TRmCategoryMeasure> TRmCategoryMeasures;

  // bi-directional many-to-one association to TRmCharacterCategoryMatrix
  @OneToMany(mappedBy = "TRmCategory")
  private Set<TRmCharacterCategoryMatrix> TRmCharacterCategoryMatrixs;

  public TRmCategory() {}

  public long getCategoryId() {
    return this.categoryId;
  }

  public void setCategoryId(final long categoryId) {
    this.categoryId = categoryId;
  }

  public String getCategoryEng() {
    return this.categoryEng;
  }

  public void setCategoryEng(final String categoryEng) {
    this.categoryEng = categoryEng;
  }

  public String getCategoryGer() {
    return this.categoryGer;
  }

  public void setCategoryGer(final String categoryGer) {
    this.categoryGer = categoryGer;
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

  public Set<TPidcRmProjectCharacter> getTPidcRmProjectCharacters1() {
    return this.TPidcRmProjectCharacters1;
  }

  public void setTPidcRmProjectCharacters1(final Set<TPidcRmProjectCharacter> TPidcRmProjectCharacters1) {
    this.TPidcRmProjectCharacters1 = TPidcRmProjectCharacters1;
  }

  public TPidcRmProjectCharacter addTPidcRmProjectCharacters1(final TPidcRmProjectCharacter TPidcRmProjectCharacters1) {
    getTPidcRmProjectCharacters1().add(TPidcRmProjectCharacters1);
    TPidcRmProjectCharacters1.setRbShare(this);

    return TPidcRmProjectCharacters1;
  }

  public TPidcRmProjectCharacter removeTPidcRmProjectCharacters1(
      final TPidcRmProjectCharacter TPidcRmProjectCharacters1) {
    getTPidcRmProjectCharacters1().remove(TPidcRmProjectCharacters1);
    TPidcRmProjectCharacters1.setRbShare(null);

    return TPidcRmProjectCharacters1;
  }

  public Set<TPidcRmProjectCharacter> getTPidcRmProjectCharacters2() {
    return this.TPidcRmProjectCharacters2;
  }

  public void setTPidcRmProjectCharacters2(final Set<TPidcRmProjectCharacter> TPidcRmProjectCharacters2) {
    this.TPidcRmProjectCharacters2 = TPidcRmProjectCharacters2;
  }

  public TPidcRmProjectCharacter addTPidcRmProjectCharacters2(final TPidcRmProjectCharacter TPidcRmProjectCharacters2) {
    getTPidcRmProjectCharacters2().add(TPidcRmProjectCharacters2);
    TPidcRmProjectCharacters2.setRbData(this);

    return TPidcRmProjectCharacters2;
  }

  public TPidcRmProjectCharacter removeTPidcRmProjectCharacters2(
      final TPidcRmProjectCharacter TPidcRmProjectCharacters2) {
    getTPidcRmProjectCharacters2().remove(TPidcRmProjectCharacters2);
    TPidcRmProjectCharacters2.setRbData(null);

    return TPidcRmProjectCharacters2;
  }

  public Set<TRmCategoryMeasure> getTRmCategoryMeasures() {
    return this.TRmCategoryMeasures;
  }

  public void setTRmCategoryMeasures(final Set<TRmCategoryMeasure> TRmCategoryMeasures) {
    this.TRmCategoryMeasures = TRmCategoryMeasures;
  }

  public TRmCategoryMeasure addTRmCategoryMeasure(final TRmCategoryMeasure TRmCategoryMeasure) {
    getTRmCategoryMeasures().add(TRmCategoryMeasure);
    TRmCategoryMeasure.setTRmCategory(this);

    return TRmCategoryMeasure;
  }

  public TRmCategoryMeasure removeTRmCategoryMeasure(final TRmCategoryMeasure TRmCategoryMeasure) {
    getTRmCategoryMeasures().remove(TRmCategoryMeasure);
    TRmCategoryMeasure.setTRmCategory(null);

    return TRmCategoryMeasure;
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
    TRmCharacterCategoryMatrix.setTRmCategory(this);

    return TRmCharacterCategoryMatrix;
  }

  public TRmCharacterCategoryMatrix removeTRmCharacterCategoryMatrix(
      final TRmCharacterCategoryMatrix TRmCharacterCategoryMatrix) {
    getTRmCharacterCategoryMatrixs().remove(TRmCharacterCategoryMatrix);
    TRmCharacterCategoryMatrix.setTRmCategory(null);

    return TRmCharacterCategoryMatrix;
  }


  public String getCatType() {
    return this.catType;
  }


  public void setCatType(final String catType) {
    this.catType = catType;
  }


  /**
   * @return the categoryCode
   */
  public String getCategoryCode() {
    return this.categoryCode;
  }


  /**
   * @param categoryCode the categoryCode to set
   */
  public void setCategoryCode(final String categoryCode) {
    this.categoryCode = categoryCode;
  }

}