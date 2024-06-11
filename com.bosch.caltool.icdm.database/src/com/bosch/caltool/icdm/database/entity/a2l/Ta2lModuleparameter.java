package com.bosch.caltool.icdm.database.entity.a2l;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.annotations.ReadOnly;


/**
 * The persistent class for the TA2L_MODULEPARAMETER database table.
 */
@Entity
@Table(name = "TA2L_MODULEPARAMETER")
@ReadOnly
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class Ta2lModuleparameter implements Serializable {

  private static final long serialVersionUID = 1L;

  private String addrepk;

  private String cputype;

  private String customer;

  private String customerno;

  private String ecu;

  private String ecucalibrationoffset;

  private String epk;

  @Id
  private BigDecimal id;

  private String noofinterfaces;

  private String phoneno;

  private String supplier;

  private String username;

  @Column(name = "\"VERSION\"")
  private String version;

  public Ta2lModuleparameter() {}

  public String getAddrepk() {
    return this.addrepk;
  }

  public void setAddrepk(final String addrepk) {
    this.addrepk = addrepk;
  }

  public String getCputype() {
    return this.cputype;
  }

  public void setCputype(final String cputype) {
    this.cputype = cputype;
  }

  public String getCustomer() {
    return this.customer;
  }

  public void setCustomer(final String customer) {
    this.customer = customer;
  }

  public String getCustomerno() {
    return this.customerno;
  }

  public void setCustomerno(final String customerno) {
    this.customerno = customerno;
  }

  public String getEcu() {
    return this.ecu;
  }

  public void setEcu(final String ecu) {
    this.ecu = ecu;
  }

  public String getEcucalibrationoffset() {
    return this.ecucalibrationoffset;
  }

  public void setEcucalibrationoffset(final String ecucalibrationoffset) {
    this.ecucalibrationoffset = ecucalibrationoffset;
  }

  public String getEpk() {
    return this.epk;
  }

  public void setEpk(final String epk) {
    this.epk = epk;
  }

  public BigDecimal getId() {
    return this.id;
  }

  public void setId(final BigDecimal id) {
    this.id = id;
  }

  public String getNoofinterfaces() {
    return this.noofinterfaces;
  }

  public void setNoofinterfaces(final String noofinterfaces) {
    this.noofinterfaces = noofinterfaces;
  }

  public String getPhoneno() {
    return this.phoneno;
  }

  public void setPhoneno(final String phoneno) {
    this.phoneno = phoneno;
  }

  public String getSupplier() {
    return this.supplier;
  }

  public void setSupplier(final String supplier) {
    this.supplier = supplier;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

  public String getVersion() {
    return this.version;
  }

  public void setVersion(final String version) {
    this.version = version;
  }

}