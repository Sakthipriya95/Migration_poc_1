package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.annotations.ReadOnly;


/**
 * The persistent class for the MV_SDOM_PVER database table.
 */
@Entity
@Table(name = "MV_SDOM_PVER")
@ReadOnly
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
@NamedQueries(value = {
    @NamedQuery(name = MvSdomPver.NQ_GET_ALL_PVER_NAMES, query = "select distinct pver.elName from MvSdomPver pver"),
    @NamedQuery(name = MvSdomPver.NQ_GET_PVER_VARS, query = "select distinct pver.variante from MvSdomPver pver where upper(pver.elName) = :pver"),
    @NamedQuery(name = MvSdomPver.NQ_GET_PVER_VAR_VERSIONS, query = "select distinct pver.revision from MvSdomPver pver where upper(pver.elName) = :pver and upper(pver.variante) = :pverVariant"),
    @NamedQuery(name = MvSdomPver.NQ_GET_PVER_VERS_NUM, query = "select pver.versNummer from MvSdomPver pver where upper(pver.elName) = :pver and upper(pver.variante) = :pverVariant and pver.revision = :pverRevision") })
public class MvSdomPver implements Serializable {

  /**
   * Named query to all PVER names
   */
  public static final String NQ_GET_ALL_PVER_NAMES = "MvSdomPver.getAllPverNames";

  /**
   * Named query to fetch pver variants for given pver
   */
  public static final String NQ_GET_PVER_VARS = "MvSdomPver.getPverVariants";

  /**
   * Named query to fetch pver varinat versions for given pver and pver variant
   */
  public static final String NQ_GET_PVER_VAR_VERSIONS = "MvSdomPver.getPverVarVersions";

  private static final long serialVersionUID = 1L;

  public static final String NQ_GET_PVER_VERS_NUM = "MvSdomPver.getPverVersNum";

  @Id
  @Column(name = "VERS_NUMMER")
  private Long versNummer;

  @Temporal(TemporalType.DATE)
  @Column(name = "AENDER_DATUM")
  private Date aenderDatum;

  @Column(name = "AENDER_USER")
  private String aenderUser;

  @Temporal(TemporalType.DATE)
  @Column(name = "CREATE_DATUM")
  private Date createDatum;

  @Column(name = "CREATE_USER")
  private String createUser;

  @Column(name = "EL_NAME")
  private String elName;

  @Column(name = "EL_NAME_ORIG")
  private String elNameOrig;

  @Column(name = "REVISION")
  private Long revision;

  @Column(name = "VARIANTE")
  private String variante;

  @Column(name = "ZUSTAND")
  private String zustand;

  public MvSdomPver() {}

  public Date getAenderDatum() {
    return this.aenderDatum;
  }

  public void setAenderDatum(final Date aenderDatum) {
    this.aenderDatum = aenderDatum;
  }

  public String getAenderUser() {
    return this.aenderUser;
  }

  public void setAenderUser(final String aenderUser) {
    this.aenderUser = aenderUser;
  }

  public Date getCreateDatum() {
    return this.createDatum;
  }

  public void setCreateDatum(final Date createDatum) {
    this.createDatum = createDatum;
  }

  public String getCreateUser() {
    return this.createUser;
  }

  public void setCreateUser(final String createUser) {
    this.createUser = createUser;
  }

  public String getElName() {
    return this.elName;
  }

  public void setElName(final String elName) {
    this.elName = elName;
  }

  public String getElNameOrig() {
    return this.elNameOrig;
  }

  public void setElNameOrig(final String elNameOrig) {
    this.elNameOrig = elNameOrig;
  }

  public Long getRevision() {
    return this.revision;
  }

  public void setRevision(final Long revision) {
    this.revision = revision;
  }

  public String getVariante() {
    return this.variante;
  }

  public void setVariante(final String variante) {
    this.variante = variante;
  }

  public Long getVersNummer() {
    return this.versNummer;
  }

  public void setVersNummer(final Long versNummer) {
    this.versNummer = versNummer;
  }

  public String getZustand() {
    return this.zustand;
  }

  public void setZustand(final String zustand) {
    this.zustand = zustand;
  }

}