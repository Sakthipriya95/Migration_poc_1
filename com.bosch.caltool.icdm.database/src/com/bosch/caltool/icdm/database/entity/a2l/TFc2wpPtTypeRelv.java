package com.bosch.caltool.icdm.database.entity.a2l;

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
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;

import com.bosch.caltool.dmframework.entity.IEntity;


/**
 * The persistent class for the T_FC2WP_PT_TYPE_RELV database table.
 */
@Entity
@Table(name = "T_FC2WP_PT_TYPE_RELV")
@NamedQuery(name = "TFc2wpPtTypeRelv.findAll", query = "SELECT t FROM TFc2wpPtTypeRelv t")
public class TFc2wpPtTypeRelv implements IEntity, Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "FCWP_PT_TYPE_RELV_ID", unique = true, nullable = false)
  private long fcwpPtTypeRelvId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private long version;

  // bi-directional many-to-one association to TFc2wpDefinition
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "FCWP_DEF_ID", nullable = false)
  private TFc2wpDefinition TFc2wpDefinition;

  // bi-directional many-to-one association to TPowerTrainType
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PT_TYPE_ID", nullable = false)
  @BatchFetch(value = BatchFetchType.JOIN)
  private TPowerTrainType TPowerTrainType;

  public TFc2wpPtTypeRelv() {}

  public long getFcwpPtTypeRelvId() {
    return this.fcwpPtTypeRelvId;
  }

  public void setFcwpPtTypeRelvId(final long fcwpPtTypeRelvId) {
    this.fcwpPtTypeRelvId = fcwpPtTypeRelvId;
  }

  @Override
  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }

  @Override
  public String getCreatedUser() {
    return this.createdUser;
  }

  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  @Override
  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }

  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  @Override
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  @Override
  public long getVersion() {
    return this.version;
  }

  public void setVersion(final long version) {
    this.version = version;
  }

  public TFc2wpDefinition getTFc2wpDefinition() {
    return this.TFc2wpDefinition;
  }

  public void setTFc2wpDefinition(final TFc2wpDefinition TFc2wpDefinition) {
    this.TFc2wpDefinition = TFc2wpDefinition;
  }

  public TPowerTrainType getTPowerTrainType() {
    return this.TPowerTrainType;
  }

  public void setTPowerTrainType(final TPowerTrainType TPowerTrainType) {
    this.TPowerTrainType = TPowerTrainType;
  }

}