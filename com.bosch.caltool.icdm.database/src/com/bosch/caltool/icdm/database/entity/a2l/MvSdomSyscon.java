package com.bosch.caltool.icdm.database.entity.a2l;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.annotations.ReadOnly;


/**
 * The persistent class for the MV_SDOM_SYSCON database table.
 */
@Entity
@Table(name = "MV_SDOM_SYSCON")
@ReadOnly
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
@NamedQueries(value = {
    @NamedQuery(name = MvSdomSyscon.GET_SYSCON_LIST, query = "SELECT t FROM MvSdomSyscon t where t.syskonName in :sysconlist"),
    @NamedQuery(name = MvSdomSyscon.GET_ALL, query = "SELECT t FROM MvSdomSyscon t")})

public class MvSdomSyscon implements Serializable {

  private static final long serialVersionUID = 1L;
  /**
   * Query to get all system constants
   */
  public static final String GET_ALL = "findAll";
  /**
   * Query to get all system constants
   */
  public static final String GET_SYSCON_LIST = "getsysconlist";

  @Id
  @Column(name = "SYSKON_NUMMER")
  private long syskonNummer;

  @Temporal(TemporalType.DATE)
  @Column(name = "AENDER_DATUM")
  private Date aenderDatum;

  @Column(name = "AENDER_USER")
  private String aenderUser;

  private String beschreibung;

  @Column(name = "BEZ_DE")
  private String bezDe;

  @Column(name = "BEZ_EN")
  private String bezEn;

  @Column(name = "BEZ_FR")
  private String bezFr;

  @Column(name = "BEZ_IT")
  private String bezIt;

  @Column(name = "CLEARING_REQUEST_ID")
  private String clearingRequestId;

  @Column(name = "CODE_SYNTAX")
  private String codeSyntax;

  @Column(name = "CRQ_COMMENT")
  private String crqComment;

  @Column(name = "CRQ_NUMBER")
  private String crqNumber;

  @Column(name = "CRQ_SYSTEM")
  private String crqSystem;

  private String dependencies;

  @Column(name = "EASEE_LINK")
  private BigDecimal easeeLink;

  @Column(name = "FILE_LINK")
  private String fileLink;

  private String gueltigkeit;

  @Column(name = "GUELTIGKEIT_BEZ")
  private String gueltigkeitBez;

  @Column(name = "LAB_KLASSE")
  private String labKlasse;

  @Column(name = "LABEL_ID")
  private BigDecimal labelId;

  @Column(name = "MASTER_APPROVED")
  private String masterApproved;

  @Column(name = "MASTER_CLASS")
  private BigDecimal masterClass;

  @Column(name = "MASTER_EL_NUMMER")
  private BigDecimal masterElNummer;

  @Column(name = "MASTER_ELEMENT")
  private String masterElement;

  @Column(name = "MAX_WERT")
  private String maxWert;

  @Column(name = "MIN_WERT")
  private String minWert;

  @Column(name = "SYSKON_CALACCESS")
  private String syskonCalaccess;

  @Column(name = "SYSKON_KATEGORIE")
  private String syskonKategorie;

  @Column(name = "SYSKON_LONGNAME")
  private String syskonLongname;

  @Column(name = "SYSKON_MODUS")
  private String syskonModus;

  @Column(name = "SYSKON_NAME")
  private String syskonName;

  @Column(name = "SYSKON_TYP")
  private String syskonTyp;

  @Column(name = "SYSKON_VERSION")
  private BigDecimal syskonVersion;

  @Column(name = "TECHNICAL_RESP_ELEMENT_NR")
  private BigDecimal technicalRespElementNr;

  @Column(name = "TECHNICAL_RESPONSIBILITY")
  private String technicalResponsibility;

  @Column(name = "WERT_BERECHNUNG")
  private String wertBerechnung;

  // bi-directional many-to-one association to MvSdomSysconValue
  @OneToMany(mappedBy = "mvSdomSyscon")
  private Set<MvSdomSysconValue> mvSdomSysconValues;

  public MvSdomSyscon() {}

  public long getSyskonNummer() {
    return this.syskonNummer;
  }

  public void setSyskonNummer(final long syskonNummer) {
    this.syskonNummer = syskonNummer;
  }

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

  public String getBeschreibung() {
    return this.beschreibung;
  }

  public void setBeschreibung(final String beschreibung) {
    this.beschreibung = beschreibung;
  }

  public String getBezDe() {
    return this.bezDe;
  }

  public void setBezDe(final String bezDe) {
    this.bezDe = bezDe;
  }

  public String getBezEn() {
    return this.bezEn;
  }

  public void setBezEn(final String bezEn) {
    this.bezEn = bezEn;
  }

  public String getBezFr() {
    return this.bezFr;
  }

  public void setBezFr(final String bezFr) {
    this.bezFr = bezFr;
  }

  public String getBezIt() {
    return this.bezIt;
  }

  public void setBezIt(final String bezIt) {
    this.bezIt = bezIt;
  }

  public String getClearingRequestId() {
    return this.clearingRequestId;
  }

  public void setClearingRequestId(final String clearingRequestId) {
    this.clearingRequestId = clearingRequestId;
  }

  public String getCodeSyntax() {
    return this.codeSyntax;
  }

  public void setCodeSyntax(final String codeSyntax) {
    this.codeSyntax = codeSyntax;
  }

  public String getCrqComment() {
    return this.crqComment;
  }

  public void setCrqComment(final String crqComment) {
    this.crqComment = crqComment;
  }

  public String getCrqNumber() {
    return this.crqNumber;
  }

  public void setCrqNumber(final String crqNumber) {
    this.crqNumber = crqNumber;
  }

  public String getCrqSystem() {
    return this.crqSystem;
  }

  public void setCrqSystem(final String crqSystem) {
    this.crqSystem = crqSystem;
  }

  public String getDependencies() {
    return this.dependencies;
  }

  public void setDependencies(final String dependencies) {
    this.dependencies = dependencies;
  }

  public BigDecimal getEaseeLink() {
    return this.easeeLink;
  }

  public void setEaseeLink(final BigDecimal easeeLink) {
    this.easeeLink = easeeLink;
  }

  public String getFileLink() {
    return this.fileLink;
  }

  public void setFileLink(final String fileLink) {
    this.fileLink = fileLink;
  }

  public String getGueltigkeit() {
    return this.gueltigkeit;
  }

  public void setGueltigkeit(final String gueltigkeit) {
    this.gueltigkeit = gueltigkeit;
  }

  public String getGueltigkeitBez() {
    return this.gueltigkeitBez;
  }

  public void setGueltigkeitBez(final String gueltigkeitBez) {
    this.gueltigkeitBez = gueltigkeitBez;
  }

  public String getLabKlasse() {
    return this.labKlasse;
  }

  public void setLabKlasse(final String labKlasse) {
    this.labKlasse = labKlasse;
  }

  public BigDecimal getLabelId() {
    return this.labelId;
  }

  public void setLabelId(final BigDecimal labelId) {
    this.labelId = labelId;
  }

  public String getMasterApproved() {
    return this.masterApproved;
  }

  public void setMasterApproved(final String masterApproved) {
    this.masterApproved = masterApproved;
  }

  public BigDecimal getMasterClass() {
    return this.masterClass;
  }

  public void setMasterClass(final BigDecimal masterClass) {
    this.masterClass = masterClass;
  }

  public BigDecimal getMasterElNummer() {
    return this.masterElNummer;
  }

  public void setMasterElNummer(final BigDecimal masterElNummer) {
    this.masterElNummer = masterElNummer;
  }

  public String getMasterElement() {
    return this.masterElement;
  }

  public void setMasterElement(final String masterElement) {
    this.masterElement = masterElement;
  }

  public String getMaxWert() {
    return this.maxWert;
  }

  public void setMaxWert(final String maxWert) {
    this.maxWert = maxWert;
  }

  public String getMinWert() {
    return this.minWert;
  }

  public void setMinWert(final String minWert) {
    this.minWert = minWert;
  }

  public String getSyskonCalaccess() {
    return this.syskonCalaccess;
  }

  public void setSyskonCalaccess(final String syskonCalaccess) {
    this.syskonCalaccess = syskonCalaccess;
  }

  public String getSyskonKategorie() {
    return this.syskonKategorie;
  }

  public void setSyskonKategorie(final String syskonKategorie) {
    this.syskonKategorie = syskonKategorie;
  }

  public String getSyskonLongname() {
    return this.syskonLongname;
  }

  public void setSyskonLongname(final String syskonLongname) {
    this.syskonLongname = syskonLongname;
  }

  public String getSyskonModus() {
    return this.syskonModus;
  }

  public void setSyskonModus(final String syskonModus) {
    this.syskonModus = syskonModus;
  }

  public String getSyskonName() {
    return this.syskonName;
  }

  public void setSyskonName(final String syskonName) {
    this.syskonName = syskonName;
  }

  public String getSyskonTyp() {
    return this.syskonTyp;
  }

  public void setSyskonTyp(final String syskonTyp) {
    this.syskonTyp = syskonTyp;
  }

  public BigDecimal getSyskonVersion() {
    return this.syskonVersion;
  }

  public void setSyskonVersion(final BigDecimal syskonVersion) {
    this.syskonVersion = syskonVersion;
  }

  public BigDecimal getTechnicalRespElementNr() {
    return this.technicalRespElementNr;
  }

  public void setTechnicalRespElementNr(final BigDecimal technicalRespElementNr) {
    this.technicalRespElementNr = technicalRespElementNr;
  }

  public String getTechnicalResponsibility() {
    return this.technicalResponsibility;
  }

  public void setTechnicalResponsibility(final String technicalResponsibility) {
    this.technicalResponsibility = technicalResponsibility;
  }

  public String getWertBerechnung() {
    return this.wertBerechnung;
  }

  public void setWertBerechnung(final String wertBerechnung) {
    this.wertBerechnung = wertBerechnung;
  }

  public Set<MvSdomSysconValue> getMvSdomSysconValues() {
    return this.mvSdomSysconValues;
  }

  public void setMvSdomSysconValues(final Set<MvSdomSysconValue> mvSdomSysconValues) {
    this.mvSdomSysconValues = mvSdomSysconValues;
  }

}