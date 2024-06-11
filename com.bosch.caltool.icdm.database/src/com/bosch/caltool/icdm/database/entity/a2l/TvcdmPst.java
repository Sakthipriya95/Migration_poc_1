package com.bosch.caltool.icdm.database.entity.a2l;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;


/**
 * The persistent class for the TVCDM_PST database table.
 */
@Entity
@Table(name = "TVCDM_PST")

@NamedQueries(value = {
    @NamedQuery(name = "TvcdmPst.findAll", query = "SELECT t FROM TvcdmPst t"),
    @NamedQuery(name = TvcdmPst.NQ_GET_PST_BY_A2LFILE_INFO_ID, query = "select pst from TvcdmPst pst where pst.a2lInfoId =:a2lInfoId and pst.pstForIcdm ='Y' ") })
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class TvcdmPst implements Serializable {

  /**
   * Named query to get the pst info based on a2l_info_id
   */
  public static final String NQ_GET_PST_BY_A2LFILE_INFO_ID = "TvcdmPst.getPstByA2LFileInfoId";
  private static final long serialVersionUID = 1L;

  @Column(name = "A2L_INFO_ID")
  private Long a2lInfoId;

  @Temporal(TemporalType.DATE)
  @Column(name = "CRE_DATE")
  private Date creDate;

  @Column(name = "CRE_USER")
  private String creUser;

  @Temporal(TemporalType.DATE)
  @Column(name = "MOD_DATE")
  private Date modDate;

  @Column(name = "MOD_USER")
  private String modUser;
  @Id
  @Column(name = "PST_ID")
  private Long pstId;

  @Column(name = "PST_NAME")
  private String pstName;

  @Column(name = "PST_REVISION")
  private Long pstRevision;

  @Column(name = "PST_VARIANT")
  private String pstVariant;

  @Column(name = "PVER_NAME")
  private String pverName;

  @Column(name = "PVER_REVISION")
  private Long pverRevision;

  @Column(name = "PVER_VARIANTE")
  private String pverVariante;

  @Temporal(TemporalType.DATE)
  @Column(name = "VCDM_CRE_DATE")
  private Date vcdmCreDate;

  @Column(name = "PST_FOR_ICDM", length = 1)
  private String pstForIcdm;

  public TvcdmPst() {}

  @OneToMany(mappedBy = "TvcdmPst", fetch = FetchType.LAZY)
  private Set<TvcdmPstCont> TvcdmPstCont;


  public Long getA2lInfoId() {
    return this.a2lInfoId;
  }

  public void setA2lInfoId(final Long a2lInfoId) {
    this.a2lInfoId = a2lInfoId;
  }

  public Date getCreDate() {
    return this.creDate;
  }

  public void setCreDate(final Date creDate) {
    this.creDate = creDate;
  }

  public String getCreUser() {
    return this.creUser;
  }

  public void setCreUser(final String creUser) {
    this.creUser = creUser;
  }

  public Date getModDate() {
    return this.modDate;
  }

  public void setModDate(final Date modDate) {
    this.modDate = modDate;
  }

  public String getModUser() {
    return this.modUser;
  }

  public void setModUser(final String modUser) {
    this.modUser = modUser;
  }

  public Long getPstId() {
    return this.pstId;
  }

  public void setPstId(final Long pstId) {
    this.pstId = pstId;
  }

  public String getPstName() {
    return this.pstName;
  }

  public void setPstName(final String pstName) {
    this.pstName = pstName;
  }

  public Long getPstRevision() {
    return this.pstRevision;
  }

  public void setPstRevision(final Long pstRevision) {
    this.pstRevision = pstRevision;
  }

  public String getPstVariant() {
    return this.pstVariant;
  }

  public void setPstVariant(final String pstVariant) {
    this.pstVariant = pstVariant;
  }

  public String getPverName() {
    return this.pverName;
  }

  public void setPverName(final String pverName) {
    this.pverName = pverName;
  }

  public Long getPverRevision() {
    return this.pverRevision;
  }

  public void setPverRevision(final Long pverRevision) {
    this.pverRevision = pverRevision;
  }

  public String getPverVariante() {
    return this.pverVariante;
  }

  public void setPverVariante(final String pverVariante) {
    this.pverVariante = pverVariante;
  }

  public Date getVcdmCreDate() {
    return this.vcdmCreDate;
  }

  public void setVcdmCreDate(final Date vcdmCreDate) {
    this.vcdmCreDate = vcdmCreDate;
  }


  /**
   * @return the tvcdmPstCont
   */
  public Set<TvcdmPstCont> getTvcdmPstCont() {
    return this.TvcdmPstCont;
  }


  /**
   * @param tvcdmPstCont the tvcdmPstCont to set
   */
  public void setTvcdmPstCont(final Set<TvcdmPstCont> tvcdmPstCont) {
    this.TvcdmPstCont = tvcdmPstCont;
  }


  /**
   * @return the pstForIcdm
   */
  public String getPstForIcdm() {
    return this.pstForIcdm;
  }


  /**
   * @param pstForIcdm the pstForIcdm to set
   */
  public void setPstForIcdm(final String pstForIcdm) {
    this.pstForIcdm = pstForIcdm;
  }

}