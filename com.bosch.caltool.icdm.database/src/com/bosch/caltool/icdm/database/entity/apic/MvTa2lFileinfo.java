package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;

/**
 * The persistent class for the MV_TA2L_FILEINFO database table.
 */
@NamedQueries(value = {
    @NamedQuery(name = MvTa2lFileinfo.NQ_GET_PVER_A2L_FILES, query = "  " +
        "SELECT a2l FROM MvTa2lFileinfo a2l                                                   " +
        "where a2l.sdomPverName = :pver                                                       " +
        "  and a2l.sdomPverVariant !=null                                                     " +
        "  and a2l.sdomPverVersid !=null                                                      " +
        "  and a2l.vcdmA2lfileId !=null"),
    @NamedQuery(name = MvTa2lFileinfo.NQ_GET_PVER_VAR_A2L_FILES, query = "                    " +
        "SELECT DISTINCT(a2l.sdomPverVariant) FROM MvTa2lFileinfo a2l                         " +
        "where upper(a2l.sdomPverName)  = :pver                                               " +
        "  and a2l.sdomPverVariant is not null"),
    @NamedQuery(name = MvTa2lFileinfo.GET_A2L_INFO_BY_PVER_INFO, query = "                      " +
        "SELECT afi1 from MvTa2lFileinfo afi1                                                   " +
        "where afi1.sdomPverName = :sdomPverName                                                " +
        "   and afi1.sdomPverVariant= :sdomPverVariant                                          " +
        "   and afi1.sdomPverRevision= :sdomPverRevision                                        "),
    @NamedQuery(name = MvTa2lFileinfo.NQ_GET_BY_VCDM_A2L_FILE_ID, query = "                   " +
        "SELECT a2l                                                                           " +
        "FROM MvTa2lFileinfo a2l                                                              " +
        "where a2l.vcdmA2lfileId = :vcdmA2lFileId                                             ") })
@NamedNativeQueries(value = {
    @NamedNativeQuery(name = MvTa2lFileinfo.NNQ_A2L_FILEINFO_BY_A2L_FILE_ID, query = "        " +
        "select a2l.*                                                                         " +
        "from TA2L_FILEINFO a2l                                                               " +
        "where sdom_pver_versid =                                                             " +
        "      ( select a2l.sdom_pver_versid                                                  " +
        "        from TA2L_FILEINFO a2l, TA2L_VCDM_VERSIONS vcdm                              " +
        "        where vcdm.a2l_checksum = a2l.a2lfilechecksum                                " +
        "           and vcdm.vers_nummer = ?                                                  " +
        "       )                                                                             ", resultClass = MvTa2lFileinfo.class) })
@Entity
@Table(name = "TA2L_FILEINFO")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class MvTa2lFileinfo implements Serializable {

  /**
   * Named query to fetch a2l file info objects based on a2l file id
   */
  public static final String NNQ_A2L_FILEINFO_BY_A2L_FILE_ID = "Ta2lFileinfo.getA2lFileInfoForA2lFileId";

  /**
   * Named query to get all A2L files for a given SDOM PVER
   *
   * @param pver name of SDOM PVER
   * @return list of MvTa2lFileinfo
   */
  public static final String NQ_GET_PVER_A2L_FILES = "MvTa2lFileinfo.getPverA2LFiles";
  /**
   * Named query to get all sdom variants for a given SDOM PVER
   *
   * @param pver name of SDOM PVER
   * @return list of variants
   */
  public static final String NQ_GET_PVER_VAR_A2L_FILES = "MvTa2lFileinfo.getPverVariants";

  /**
   * Name query to get a2l file info by vcdm a2l file id
   */
  public static final String NQ_GET_BY_VCDM_A2L_FILE_ID = "MvTa2lFileinfo.getByVcdmA2lFileId";

  /**
   * Named query to fetch the contents of TA2L_FileInfo for the given vCDM A2L FileID to get the SDOM PVER VersID there
   * can be only one entry for a particular vCDM A2L FileID !
   */
  public static final String GET_A2L_INFO_BY_PVER_INFO = "MvTa2lFileinfo.getA2lInfoByPverInfo";


  private static final long serialVersionUID = 1L;
  private long id;
  private String a2lfilechecksum;
  private BigDecimal a2lfilesize;
  private String asap2version;
  private String filename;
  private String headercomment;
  private String headerprojectno;
  private String headerversion;
  private String projectlongidentifier;
  private String projectname;
  private String sdomPverName;
  private String sdomPverVariant;
  private Long sdomPverVersid;
  private Long vcdmA2lfileId;
  private Long sdomPverRevision;
  private Set<MvTabvCaldatafile> mvTabvCaldatafiles;
  // ICDM-1671
  private Timestamp filedate;
  private Long numCompli;
  private Long numParam;

  public MvTa2lFileinfo() {}


  @Id
  @Column(unique = true, nullable = false, precision = 19)
  public long getId() {
    return this.id;
  }

  public void setId(final long id) {
    this.id = id;
  }


  @Column(length = 255)
  public String getA2lfilechecksum() {
    return this.a2lfilechecksum;
  }

  public void setA2lfilechecksum(final String a2lfilechecksum) {
    this.a2lfilechecksum = a2lfilechecksum;
  }


  @Column(precision = 19)
  public BigDecimal getA2lfilesize() {
    return this.a2lfilesize;
  }

  public void setA2lfilesize(final BigDecimal a2lfilesize) {
    this.a2lfilesize = a2lfilesize;
  }


  @Column(length = 255)
  public String getAsap2version() {
    return this.asap2version;
  }

  public void setAsap2version(final String asap2version) {
    this.asap2version = asap2version;
  }


  @Column(length = 255)
  public String getFilename() {
    return this.filename;
  }

  public void setFilename(final String filename) {
    this.filename = filename;
  }


  @Column(length = 255)
  public String getHeadercomment() {
    return this.headercomment;
  }

  public void setHeadercomment(final String headercomment) {
    this.headercomment = headercomment;
  }


  @Column(length = 255)
  public String getHeaderprojectno() {
    return this.headerprojectno;
  }

  public void setHeaderprojectno(final String headerprojectno) {
    this.headerprojectno = headerprojectno;
  }


  @Column(length = 255)
  public String getHeaderversion() {
    return this.headerversion;
  }

  public void setHeaderversion(final String headerversion) {
    this.headerversion = headerversion;
  }


  @Column(length = 255)
  public String getProjectlongidentifier() {
    return this.projectlongidentifier;
  }

  public void setProjectlongidentifier(final String projectlongidentifier) {
    this.projectlongidentifier = projectlongidentifier;
  }


  @Column(length = 255)
  public String getProjectname() {
    return this.projectname;
  }

  public void setProjectname(final String projectname) {
    this.projectname = projectname;
  }


  @Column(name = "SDOM_PVER_NAME", length = 50)
  public String getSdomPverName() {
    return this.sdomPverName;
  }

  public void setSdomPverName(final String sdomPverName) {
    this.sdomPverName = sdomPverName;
  }


  @Column(name = "SDOM_PVER_VARIANT", length = 32)
  public String getSdomPverVariant() {
    return this.sdomPverVariant;
  }

  public void setSdomPverVariant(final String sdomPverVariant) {
    this.sdomPverVariant = sdomPverVariant;
  }


  @Column(name = "SDOM_PVER_VERSID")
  public Long getSdomPverVersid() {
    return this.sdomPverVersid;
  }

  public void setSdomPverVersid(final Long sdomPverVersid) {
    this.sdomPverVersid = sdomPverVersid;
  }


  @Column(name = "VCDM_A2LFILE_ID")
  public Long getVcdmA2lfileId() {
    return this.vcdmA2lfileId;
  }

  public void setVcdmA2lfileId(final Long vcdmA2lfileId) {
    this.vcdmA2lfileId = vcdmA2lfileId;
  }

  @Column(name = "SDOM_PVER_REVISION")
  public Long getSdomPverRevision() {
    return this.sdomPverRevision;
  }

  public void setSdomPverRevision(final Long sdomPverRevision) {
    this.sdomPverRevision = sdomPverRevision;
  }


  // bi-directional many-to-one association to MvTabvCaldatafile
  @OneToMany(mappedBy = "mvTa2lFileinfo")
  public Set<MvTabvCaldatafile> getMvTabvCaldatafiles() {
    return this.mvTabvCaldatafiles;
  }

  public void setMvTabvCaldatafiles(final Set<MvTabvCaldatafile> mvTabvCaldatafiles) {
    this.mvTabvCaldatafiles = mvTabvCaldatafiles;
  }

  @Column(name = "FILEDATE")
  public Timestamp getFiledate() {
    return this.filedate;
  }

  public void setFiledate(final Timestamp filedate) {
    this.filedate = filedate;
  }

  /**
   * @return the numCompli
   */
  @Column(name = "NUM_COMPLI")
  public Long getNumCompli() {
    return this.numCompli;
  }


  /**
   * @param numCompli the numCompli to set
   */
  public void setNumCompli(final Long numCompli) {
    this.numCompli = numCompli;
  }

  /**
   * @return the numCompli
   */
  @Column(name = "NUM_PARAM")
  public Long getNumParam() {
    return this.numParam;
  }


  /**
   * @param numParam the numParam to set
   */
  public void setNumParam(final Long numParam) {
    this.numParam = numParam;
  }
}