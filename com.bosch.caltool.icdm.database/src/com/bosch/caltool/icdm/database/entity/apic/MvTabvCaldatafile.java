package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.annotations.ReadOnly;


/**
 * The persistent class for the MV_TABV_CALDATAFILES database table.
 */
@Entity
@ReadOnly
@Table(name = "TABV_CALDATAFILES")
@NamedQueries(value = {
    @NamedQuery(name = MvTabvCaldatafile.GET_A2L_FILE_INFO_ID_BY_DST_ID, query = "SELECT calDataFile.mvTa2lFileinfo.id " +
        "FROM MvTabvCaldatafile calDataFile where " + "calDataFile.mvTabeDataset.easeedstId=:easeedstId") })
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class MvTabvCaldatafile implements Serializable {

  private static final long serialVersionUID = 1L;
  private long id;
  private BigDecimal a2lFileId;
  private String a2lFileName;
  private Date createdate;
  private String createuser;
  private BigDecimal flow5Id;
  private BigDecimal hexFileId;
  private String hexFileName;
  private String notice;
  private BigDecimal revisionId;
  private String seriesdataset;
  private BigDecimal statusId;
  private Date updatedate;
  private String updateuser;
  private MvTa2lFileinfo mvTa2lFileinfo;
  private MvTabeDataset mvTabeDataset;
  /**
   * Named query to get a2l file info id for given vcdm dst id
   */
  public static final String GET_A2L_FILE_INFO_ID_BY_DST_ID = "MvTabvCaldatafile.findA2LFileInfoByDstId";


  public MvTabvCaldatafile() {}


  /**
   * @return id
   */
  @Id
  @Column(unique = true, nullable = false)
  public long getId() {
    return this.id;
  }

  /**
   * @param id Long
   */
  public void setId(final long id) {
    this.id = id;
  }


  /**
   * @return A2l file id
   */
  @Column(name = "A2L_FILE_ID")
  public BigDecimal getA2lFileId() {
    return this.a2lFileId;
  }

  /**
   * @param a2lFileId BigDecimal
   */
  public void setA2lFileId(final BigDecimal a2lFileId) {
    this.a2lFileId = a2lFileId;
  }


  /**
   * @return a2l file name
   */
  @Column(name = "A2L_FILE_NAME", length = 255)
  public String getA2lFileName() {
    return this.a2lFileName;
  }

  /**
   * @param a2lFileName String
   */
  public void setA2lFileName(final String a2lFileName) {
    this.a2lFileName = a2lFileName;
  }


  /**
   * @return created date
   */
  @Temporal(TemporalType.DATE)
  public Date getCreatedate() {
    return this.createdate;
  }

  /**
   * @param createdate Date
   */
  public void setCreatedate(final Date createdate) {
    this.createdate = createdate;
  }


  /**
   * @return created user
   */
  @Column(length = 30)
  public String getCreateuser() {
    return this.createuser;
  }

  /**
   * @param createuser String
   */
  public void setCreateuser(final String createuser) {
    this.createuser = createuser;
  }


  /**
   * @return flow5Id
   */
  @Column(name = "FLOW5_ID")
  public BigDecimal getFlow5Id() {
    return this.flow5Id;
  }

  /**
   * @param flow5Id BigDecimal
   */
  public void setFlow5Id(final BigDecimal flow5Id) {
    this.flow5Id = flow5Id;
  }


  /**
   * @return hex file id
   */
  @Column(name = "HEX_FILE_ID")
  public BigDecimal getHexFileId() {
    return this.hexFileId;
  }

  /**
   * @param hexFileId Big Decimal
   */
  public void setHexFileId(final BigDecimal hexFileId) {
    this.hexFileId = hexFileId;
  }


  /**
   * @return hex file name
   */
  @Column(name = "HEX_FILE_NAME", length = 255)
  public String getHexFileName() {
    return this.hexFileName;
  }

  /**
   * @param hexFileName String
   */
  public void setHexFileName(final String hexFileName) {
    this.hexFileName = hexFileName;
  }


  /**
   * @return notice
   */
  @Column(length = 4000)
  public String getNotice() {
    return this.notice;
  }

  /**
   * @param notice String
   */
  public void setNotice(final String notice) {
    this.notice = notice;
  }


  /**
   * @return revision id
   */
  @Column(name = "REVISION_ID")
  public BigDecimal getRevisionId() {
    return this.revisionId;
  }

  /**
   * @param revisionId BigDecimal
   */
  public void setRevisionId(final BigDecimal revisionId) {
    this.revisionId = revisionId;
  }


  /**
   * @return series dataset
   */
  @Column(nullable = false, length = 1)
  public String getSeriesdataset() {
    return this.seriesdataset;
  }

  /**
   * @param seriesdataset String
   */
  public void setSeriesdataset(final String seriesdataset) {
    this.seriesdataset = seriesdataset;
  }


  /**
   * @return status id
   */
  @Column(name = "STATUS_ID")
  public BigDecimal getStatusId() {
    return this.statusId;
  }

  /**
   * @param statusId BigDecimal
   */
  public void setStatusId(final BigDecimal statusId) {
    this.statusId = statusId;
  }


  /**
   * @return update Date
   */
  @Temporal(TemporalType.DATE)
  public Date getUpdatedate() {
    return this.updatedate;
  }

  /**
   * @param updatedate Date
   */
  public void setUpdatedate(final Date updatedate) {
    this.updatedate = updatedate;
  }


  /**
   * @return updateuser
   */
  @Column(length = 30)
  public String getUpdateuser() {
    return this.updateuser;
  }

  /**
   * @param updateuser update user
   */
  public void setUpdateuser(final String updateuser) {
    this.updateuser = updateuser;
  }


  // bi-directional many-to-one association to MvTa2lFileinfo
  /**
   * @return MvTa2lFileinfo
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "A2L_INFO_ID")
  public MvTa2lFileinfo getMvTa2lFileinfo() {
    return this.mvTa2lFileinfo;
  }

  /**
   * @param mvTa2lFileinfo MvTa2lFileinfo
   */
  public void setMvTa2lFileinfo(final MvTa2lFileinfo mvTa2lFileinfo) {
    this.mvTa2lFileinfo = mvTa2lFileinfo;
  }


  // bi-directional many-to-one association to MvTabeDataset
  /**
   * @return MvTabeDataset
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "EASEEDST_ID")
  public MvTabeDataset getMvTabeDataset() {
    return this.mvTabeDataset;
  }

  /**
   * @param mvTabeDataset MvTabeDataset
   */
  public void setMvTabeDataset(final MvTabeDataset mvTabeDataset) {
    this.mvTabeDataset = mvTabeDataset;
  }

}