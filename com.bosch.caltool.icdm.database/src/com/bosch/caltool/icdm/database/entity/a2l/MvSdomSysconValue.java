package com.bosch.caltool.icdm.database.entity.a2l;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.annotations.ReadOnly;


/**
 * The persistent class for the MV_SDOM_SYSCON_VALUES database table.
 */
@Entity
@Table(name = "MV_SDOM_SYSCON_VALUES")
@ReadOnly
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
@NamedQuery(name = MvSdomSysconValue.GET_ALL, query = "SELECT t.syskonWert, t.syskonWertBeschreibung, t.mvSdomSyscon.syskonName FROM MvSdomSysconValue t")
public class MvSdomSysconValue implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Query to get all system constant values
   */
  public static final String GET_ALL = "GetAll";

  @Id
  @Column(name = "SYSKON_WERT_ID")
  private long syskonWertId;

  @Column(name = "SYSKON_NAME")
  private String syskonName;

  @Column(name = "SYSKON_VERSION")
  private BigDecimal syskonVersion;

  @Column(name = "SYSKON_WERT")
  private String syskonWert;

  @Column(name = "SYSKON_WERT_BESCHREIBUNG")
  private String syskonWertBeschreibung;

  @Column(name = "SYSKON_WERT_LABEL")
  private String syskonWertLabel;

  // bi-directional many-to-one association to MvSdomSyscon
  @ManyToOne
  @JoinColumn(name = "SYSKON_NUMMER")
  private MvSdomSyscon mvSdomSyscon;

  public MvSdomSysconValue() {}

  public long getSyskonWertId() {
    return this.syskonWertId;
  }

  public void setSyskonWertId(final long syskonWertId) {
    this.syskonWertId = syskonWertId;
  }

  public String getSyskonName() {
    return this.syskonName;
  }

  public void setSyskonName(final String syskonName) {
    this.syskonName = syskonName;
  }

  public BigDecimal getSyskonVersion() {
    return this.syskonVersion;
  }

  public void setSyskonVersion(final BigDecimal syskonVersion) {
    this.syskonVersion = syskonVersion;
  }

  public String getSyskonWert() {
    return this.syskonWert;
  }

  public void setSyskonWert(final String syskonWert) {
    this.syskonWert = syskonWert;
  }

  public String getSyskonWertBeschreibung() {
    return this.syskonWertBeschreibung;
  }

  public void setSyskonWertBeschreibung(final String syskonWertBeschreibung) {
    this.syskonWertBeschreibung = syskonWertBeschreibung;
  }

  public String getSyskonWertLabel() {
    return this.syskonWertLabel;
  }

  public void setSyskonWertLabel(final String syskonWertLabel) {
    this.syskonWertLabel = syskonWertLabel;
  }

  public MvSdomSyscon getMvSdomSyscon() {
    return this.mvSdomSyscon;
  }

  public void setMvSdomSyscon(final MvSdomSyscon mvSdomSyscon) {
    this.mvSdomSyscon = mvSdomSyscon;
  }

}