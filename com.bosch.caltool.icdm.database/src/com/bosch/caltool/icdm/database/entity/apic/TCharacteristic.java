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


/**
 * The persistent class for the T_CHARACTERISTICS database table.
 */
@Entity
@OptimisticLocking(cascade = true)
@Table(name = "T_CHARACTERISTICS")
@NamedQueries(value = { @NamedQuery(name = TCharacteristic.NQ_FIND_ALL, query = "SELECT t FROM TCharacteristic t") })
public class TCharacteristic implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Named Query to find all characteistics
   */
  public static final String NQ_FIND_ALL = "TCharacteristic.findAll";


  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "CHAR_ID")
  private long charId;

  @Column(name = "CHAR_NAME_ENG")
  private String charNameEng;

  @Column(name = "CHAR_NAME_GER")
  private String charNameGer;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "DESC_ENG")
  private String descEng;

  @Column(name = "DESC_GER")
  private String descGer;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "FOCUS_MATRIX_YN", length = 1)
  private String focusMatrixYN;

  @Column(name = "VERSION")
  @Version
  private Long version;

  // bi-directional many-to-one association to TabvAttribute
  @OneToMany(mappedBy = "tCharacteristic")
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TabvAttribute> tabvAttributes;

  // bi-directional many-to-one association to TCharacteristicValue
  @OneToMany(mappedBy = "tCharacteristic")
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TCharacteristicValue> tCharacteristicValues;


  public TCharacteristic() {}

  public long getCharId() {
    return this.charId;
  }

  public void setCharId(final long charId) {
    this.charId = charId;
  }

  public String getCharNameEng() {
    return this.charNameEng;
  }

  public void setCharNameEng(final String charNameEng) {
    this.charNameEng = charNameEng;
  }

  public String getCharNameGer() {
    return this.charNameGer;
  }

  public void setCharNameGer(final String charNameGer) {
    this.charNameGer = charNameGer;
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

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public List<TabvAttribute> getTabvAttributes() {
    return this.tabvAttributes;
  }

  public void setTabvAttributes(final List<TabvAttribute> tabvAttributes) {
    this.tabvAttributes = tabvAttributes;
  }

  /**
   * @return the tCharacteristicValues
   */
  public List<TCharacteristicValue> gettCharacteristicValues() {
    return this.tCharacteristicValues;
  }


  /**
   * @param tCharacteristicValues the tCharacteristicValues to set
   */
  public void settCharacteristicValues(final List<TCharacteristicValue> tCharacteristicValues) {
    this.tCharacteristicValues = tCharacteristicValues;
  }

  /**
   * @return the focusMatrixYN
   */
  public String getFocusMatrixYN() {
    return this.focusMatrixYN;
  }


  /**
   * @param focusMatrixYN the focusMatrixYN to set
   */
  public void setFocusMatrixYN(final String focusMatrixYN) {
    this.focusMatrixYN = focusMatrixYN;
  }
}