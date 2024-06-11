package com.bosch.caltool.icdm.database.entity.cdr.cdfx;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.bosch.caltool.icdm.database.entity.a2l.TA2lResponsibility;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWorkPackage;


/**
 * The persistent class for the T_CDFX_DELVRY_WP_RESP database table.
 */
@Entity
@Table(name = "T_CDFX_DELVRY_WP_RESP")
public class TCDFxDelvryWpResp implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "CDFX_DEL_WP_RESP_ID")
  private long cdfxDelWpRespId;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "RESP_ID")
  private TA2lResponsibility resp;


  @Column(name = "\"VERSION\"")
  @Version
  private long version;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "WP_ID")
  private TA2lWorkPackage wp;

  // bi-directional many-to-one association to TCDFxDelivery
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "CDFX_DELIVERY_ID")
  private TCDFxDelivery tCdfxDelivery;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @OneToMany(mappedBy = "tCDFxDelvryWpResp", fetch = FetchType.LAZY)
  @JoinColumn(name = "CDFX_DLVRY_WP_RESP_ID")
  private List<TCDFxDeliveryParam> tCDFxDeliveryParams;

  public TCDFxDelvryWpResp() {}


  /**
   * @return the cdfxDelWpRespId
   */
  public long getCdfxDelWpRespId() {
    return this.cdfxDelWpRespId;
  }


  /**
   * @param cdfxDelWpRespId the cdfxDelWpRespId to set
   */
  public void setCdfxDelWpRespId(final long cdfxDelWpRespId) {
    this.cdfxDelWpRespId = cdfxDelWpRespId;
  }


  /**
   * @return the resp
   */
  public TA2lResponsibility getResp() {
    return this.resp;
  }


  /**
   * @param resp the resp to set
   */
  public void setResp(final TA2lResponsibility resp) {
    this.resp = resp;
  }


  /**
   * @return the version
   */
  public Long getVersion() {
    return this.version;
  }


  /**
   * @param version the version to set
   */
  public void setVersion(final Long version) {
    this.version = version;
  }


  /**
   * @return the wp
   */
  public TA2lWorkPackage getWp() {
    return this.wp;
  }


  /**
   * @param wp the wp to set
   */
  public void setWp(final TA2lWorkPackage wp) {
    this.wp = wp;
  }


  /**
   * @return the tCdfxDelivery
   */
  public TCDFxDelivery getTCdfxDelivery() {
    return this.tCdfxDelivery;
  }


  /**
   * @param tCdfxDelivery the tCdfxDelivery to set
   */
  public void setTCdfxDelivery(final TCDFxDelivery tCdfxDelivery) {
    this.tCdfxDelivery = tCdfxDelivery;
  }


  /**
   * @return the createdDate
   */
  public Timestamp getCreatedDate() {
    return this.createdDate;
  }


  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }


  /**
   * @return the createdUser
   */
  public String getCreatedUser() {
    return this.createdUser;
  }


  /**
   * @param createdUser the createdUser to set
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }


  /**
   * @return the modifiedDate
   */
  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }


  /**
   * @param modifiedDate the modifiedDate to set
   */
  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }


  /**
   * @return the modifiedUser
   */
  public String getModifiedUser() {
    return this.modifiedUser;
  }


  /**
   * @param modifiedUser the modifiedUser to set
   */
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }


  /**
   * @return the tCDFxDeliveryParams
   */
  public List<TCDFxDeliveryParam> getTCDFxDeliveryParams() {
    return this.tCDFxDeliveryParams;
  }


  /**
   * @param tCDFxDeliveryParams the tCDFxDeliveryParams to set
   */
  public void setTCDFxDeliveryParams(final List<TCDFxDeliveryParam> tCDFxDeliveryParams) {
    this.tCDFxDeliveryParams = tCDFxDeliveryParams;
  }

  /**
   * @param tCDFxDeliveryParam the tCDFxDeliveryParam to add
   * @return updated tCDFxDeliveryParam
   */
  public TCDFxDeliveryParam addTCDFxDeliveryParam(final TCDFxDeliveryParam tCDFxDeliveryParam) {
    getTCDFxDeliveryParams().add(tCDFxDeliveryParam);
    tCDFxDeliveryParam.settCDFxDelvryWpResp(this);

    return tCDFxDeliveryParam;
  }

  /**
   * @param tCDFxDeliveryParam the tCDFxDeliveryParam to remove
   * @return removed tCDFxDeliveryParam
   */
  public TCDFxDeliveryParam removeTCDFxDeliveryParam(final TCDFxDeliveryParam tCDFxDeliveryParam) {
    getTCDFxDeliveryParams().remove(tCDFxDeliveryParam);
    return tCDFxDeliveryParam;
  }


}