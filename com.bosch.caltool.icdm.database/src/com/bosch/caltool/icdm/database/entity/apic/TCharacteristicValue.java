package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.OptimisticLocking;


/**
 * The persistent class for the T_CHARACTERISTIC_VALUES database table.
 */
@Entity
@OptimisticLocking(cascade = true)
@Table(name = "T_CHARACTERISTIC_VALUES")
public class TCharacteristicValue implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "CHAR_VAL_ID")
  private long charValId;

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

  @Column(name = "VAL_NAME_ENG")
  private String valNameEng;

  @Column(name = "VAL_NAME_GER")
  private String valNameGer;

  @Column(name = "\"VERSION\"")
  @Version
  private Long version;

  // bi-directional many-to-one association to TabvAttrValue
  @OneToMany(mappedBy = "tCharacteristicValue")
  private List<TabvAttrValue> tabvAttrValues;

  // bi-directional many-to-one association to TCharacteristic
  @ManyToOne
  @JoinColumn(name = "CHAR_ID")
  private TCharacteristic tCharacteristic;


  public TCharacteristicValue() {}

  public long getCharValId() {
    return this.charValId;
  }

  public void setCharValId(final long charValId) {
    this.charValId = charValId;
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

  public String getValNameEng() {
    return this.valNameEng;
  }

  public void setValNameEng(final String valNameEng) {
    this.valNameEng = valNameEng;
  }

  public String getValNameGer() {
    return this.valNameGer;
  }

  public void setValNameGer(final String valNameGer) {
    this.valNameGer = valNameGer;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public List<TabvAttrValue> getTabvAttrValues() {
    return this.tabvAttrValues;
  }

  public void setTabvAttrValues(final List<TabvAttrValue> tabvAttrValues) {
    this.tabvAttrValues = tabvAttrValues;
  }

  public TCharacteristic gettCharacteristic() {
    return this.tCharacteristic;
  }


  public void settCharacteristic(final TCharacteristic tCharacteristic) {
    this.tCharacteristic = tCharacteristic;
  }


}