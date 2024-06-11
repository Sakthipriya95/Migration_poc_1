package com.bosch.caltool.icdm.database.entity.a2l;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the T_POWER_TRAIN_TYPE database table.
 */
@Entity
@Table(name = "T_POWER_TRAIN_TYPE")
@NamedQuery(name = TPowerTrainType.NQ_FIND_ALL, query = "SELECT t FROM TPowerTrainType t")
public class TPowerTrainType implements Serializable {

  private static final long serialVersionUID = 1L;

  /** The Constant NQ_FIND_ALL. */
  public static final String NQ_FIND_ALL = "TPowerTrainType.findAll";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "PT_TYPE_ID", unique = true, nullable = false)
  private long ptTypeId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;

  @Column(name = "PT_TYPE", nullable = false, length = 50)
  private String ptType;

  @Column(name = "PT_TYPE_DESC", length = 4000)
  private String ptTypeDesc;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private long version;

  // bi-directional many-to-one association to TFc2wpPtTypeRelv
  @OneToMany(mappedBy = "TPowerTrainType", fetch = FetchType.LAZY)
  private Set<TFc2wpPtTypeRelv> TFc2wpPtTypeRelvs;

  // bi-directional many-to-one association to TFcwpMapPtType
  @OneToMany(mappedBy = "TPowerTrainType", fetch = FetchType.LAZY)
  private Set<TFc2wpMapPtType> TFcwpMapPtTypes;

  public TPowerTrainType() {}

  public long getPtTypeId() {
    return this.ptTypeId;
  }

  public void setPtTypeId(final long ptTypeId) {
    this.ptTypeId = ptTypeId;
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

  public String getPtType() {
    return this.ptType;
  }

  public void setPtType(final String ptType) {
    this.ptType = ptType;
  }

  public String getPtTypeDesc() {
    return this.ptTypeDesc;
  }

  public void setPtTypeDesc(final String ptTypeDesc) {
    this.ptTypeDesc = ptTypeDesc;
  }

  public long getVersion() {
    return this.version;
  }

  public void setVersion(final long version) {
    this.version = version;
  }

  public Set<TFc2wpPtTypeRelv> getTFc2wpPtTypeRelvs() {
    return this.TFc2wpPtTypeRelvs;
  }

  public void setTFc2wpPtTypeRelvs(final Set<TFc2wpPtTypeRelv> TFc2wpPtTypeRelvs) {
    this.TFc2wpPtTypeRelvs = TFc2wpPtTypeRelvs;
  }

  public TFc2wpPtTypeRelv addTFc2wpPtTypeRelv(final TFc2wpPtTypeRelv TFc2wpPtTypeRelv) {
    getTFc2wpPtTypeRelvs().add(TFc2wpPtTypeRelv);
    TFc2wpPtTypeRelv.setTPowerTrainType(this);

    return TFc2wpPtTypeRelv;
  }

  public TFc2wpPtTypeRelv removeTFc2wpPtTypeRelv(final TFc2wpPtTypeRelv TFc2wpPtTypeRelv) {
    getTFc2wpPtTypeRelvs().remove(TFc2wpPtTypeRelv);
    TFc2wpPtTypeRelv.setTPowerTrainType(null);

    return TFc2wpPtTypeRelv;
  }

  public Set<TFc2wpMapPtType> getTFc2wpMapPtTypes() {
    return this.TFcwpMapPtTypes;
  }

  public void setTFc2wpMapPtTypes(final Set<TFc2wpMapPtType> TFcwpMapPtTypes) {
    this.TFcwpMapPtTypes = TFcwpMapPtTypes;
  }

  public TFc2wpMapPtType addTFc2wpMapPtType(final TFc2wpMapPtType TFcwpMapPtType) {
    getTFc2wpMapPtTypes().add(TFcwpMapPtType);
    TFcwpMapPtType.setTPowerTrainType(this);

    return TFcwpMapPtType;
  }

  public TFc2wpMapPtType removeTFc2wpMapPtType(final TFc2wpMapPtType TFcwpMapPtType) {
    getTFc2wpMapPtTypes().remove(TFcwpMapPtType);
    TFcwpMapPtType.setTPowerTrainType(null);

    return TFcwpMapPtType;
  }

}