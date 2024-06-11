package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.annotations.ReadOnly;


/**
 * The persistent class for the TA2L_VCDM_VERSIONS database table.
 */
@Entity
@ReadOnly
@Table(name = "TA2L_VCDM_VERSIONS")
@NamedQueries(value = { @NamedQuery(name = MvTa2lVcdmVersion.NQ_FIND_VCDM_VERSION, query = "SELECT t FROM MvTa2lVcdmVersion t where t.a2lChecksum = :a2lCSum") })
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class MvTa2lVcdmVersion implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Named query to find VCDM version number for the passed in {@link MvTa2lFileinfo} checksum.</br>Returns a list of
   * TA2L_VCDM_VERSIONS entities
   */
  public static final String NQ_FIND_VCDM_VERSION = "Ta2lVcdmVersion.findVCDMVersion";

  @Id
  @Column(name = "VERS_NUMMER")
  private BigDecimal versNummer;

  @Column(name = "A2L_CHECKSUM")
  private String a2lChecksum;

  private BigDecimal filesize;

  @Column(name = "ORIGINAL_DATE")
  private Timestamp originalDate;

  @Column(name = "ORIGINAL_FILE")
  private String originalFile;

  private String pst;

  @Column(name = "VCDM_CHECKSUM")
  private String vcdmChecksum;

  @Column(name = "VCDM_COMMENT")
  private String vcdmComment;

  public MvTa2lVcdmVersion() {}

  public String getA2lChecksum() {
    return this.a2lChecksum;
  }

  public void setA2lChecksum(final String a2lChecksum) {
    this.a2lChecksum = a2lChecksum;
  }

  public BigDecimal getFilesize() {
    return this.filesize;
  }

  public void setFilesize(final BigDecimal filesize) {
    this.filesize = filesize;
  }

  public Timestamp getOriginalDate() {
    return this.originalDate;
  }

  public void setOriginalDate(final Timestamp originalDate) {
    this.originalDate = originalDate;
  }


  public String getOriginalFile() {
    return this.originalFile;
  }

  public void setOriginalFile(final String originalFile) {
    this.originalFile = originalFile;
  }

  public String getPst() {
    return this.pst;
  }

  public void setPst(final String pst) {
    this.pst = pst;
  }

  public String getVcdmChecksum() {
    return this.vcdmChecksum;
  }

  public void setVcdmChecksum(final String vcdmChecksum) {
    this.vcdmChecksum = vcdmChecksum;
  }

  public String getVcdmComment() {
    return this.vcdmComment;
  }

  public void setVcdmComment(final String vcdmComment) {
    this.vcdmComment = vcdmComment;
  }

  public BigDecimal getVersNummer() {
    return this.versNummer;
  }

  public void setVersNummer(final BigDecimal versNummer) {
    this.versNummer = versNummer;
  }

}