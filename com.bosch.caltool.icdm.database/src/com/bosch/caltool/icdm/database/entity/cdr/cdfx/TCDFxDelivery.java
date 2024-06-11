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

import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpDefnVersion;
import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;


/**
 * The persistent class for the T_CDFX_DELIVERY database table.
 */
@Entity
@Table(name = "T_CDFX_DELIVERY")
public class TCDFxDelivery implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "CDFX_DELIVERY_ID")
  private long cdfxDeliveryId;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PIDC_A2L_ID")
  private TPidcA2l pidcA2l;

  @Column(name = "READINESS_YN")
  private String readinessYn;

  @Column(name = "\"SCOPE\"")
  private String scope;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "VARIANT_ID")
  private TabvProjectVariant variant;

  @Column(name = "\"VERSION\"")
  @Version
  private Long version;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "WP_DEFN_VERS_ID")
  private TA2lWpDefnVersion wpDefnVersion;

  // bi-directional many-to-one association to TCDFxDelWpResp
  @OneToMany(mappedBy = "tCdfxDelivery")
  private List<TCDFxDelvryWpResp> tCDFxDelvryWpResps;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  public TCDFxDelivery() {}

  public long getCdfxDeliveryId() {
    return this.cdfxDeliveryId;
  }

  public void setCdfxDeliveryId(final long cdfxDeliveryId) {
    this.cdfxDeliveryId = cdfxDeliveryId;
  }

  public String getCreatedUser() {
    return this.createdUser;
  }

  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  public String getModifiedUser() {
    return this.modifiedUser;
  }

  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  public String getReadinessYn() {
    return this.readinessYn;
  }

  public void setReadinessYn(final String readinessYn) {
    this.readinessYn = readinessYn;
  }

  public String getScope() {
    return this.scope;
  }

  public void setScope(final String scope) {
    this.scope = scope;
  }


  /**
   * @return the pidcA2l
   */
  public TPidcA2l getPidcA2l() {
    return this.pidcA2l;
  }


  /**
   * @param pidcA2l the pidcA2l to set
   */
  public void setPidcA2l(final TPidcA2l pidcA2l) {
    this.pidcA2l = pidcA2l;
  }


  /**
   * @return the variant
   */
  public TabvProjectVariant getVariant() {
    return this.variant;
  }


  /**
   * @param variant the variant to set
   */
  public void setVariant(final TabvProjectVariant variant) {
    this.variant = variant;
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
   * @return the wpDefnVersion
   */
  public TA2lWpDefnVersion getWpDefnVersion() {
    return this.wpDefnVersion;
  }


  /**
   * @param wpDefnVersion the wpDefnVersion to set
   */
  public void setWpDefnVersion(final TA2lWpDefnVersion wpDefnVersion) {
    this.wpDefnVersion = wpDefnVersion;
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

  public List<TCDFxDelvryWpResp> getTCDFxDelvryWpResps() {
    return this.tCDFxDelvryWpResps;
  }

  public void setTCDFxDelvryWpResps(final List<TCDFxDelvryWpResp> tCDFxDelvryWpResps) {
    this.tCDFxDelvryWpResps = tCDFxDelvryWpResps;
  }

  public TCDFxDelvryWpResp addTCDFxDelvryWpResp(final TCDFxDelvryWpResp tCDFxDelvryWpResp) {
    getTCDFxDelvryWpResps().add(tCDFxDelvryWpResp);
    tCDFxDelvryWpResp.setTCdfxDelivery(this);

    return tCDFxDelvryWpResp;
  }

  public TCDFxDelvryWpResp removeTCDFxDelvryWpResp(final TCDFxDelvryWpResp tCDFxDelvryWpResp) {
    getTCDFxDelvryWpResps().remove(tCDFxDelvryWpResp);
    tCDFxDelvryWpResp.setTCdfxDelivery(null);

    return tCDFxDelvryWpResp;
  }

}