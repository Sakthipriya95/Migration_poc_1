package com.bosch.caltool.icdm.database.entity.a2l;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;


/**
 * The persistent class for the TVCDM_PST_CONT database table.
 */
@Entity
@Table(name = "TVCDM_PST_CONT")
@NamedQuery(name = "TvcdmPstCont.findAll", query = "SELECT t FROM TvcdmPstCont t")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class TvcdmPstCont implements Serializable {

  private static final long serialVersionUID = 1L;

  @Column(name = "FILE_ID")
  private Long fileId;

  @Column(name = "FILE_NAME")
  private String fileName;
  @Id
  private Long id;

  @Column(name = "PST_ID")
  private Long pstId;

  @Column(name = "VCDM_CLASS")
  private String vcdmClass;

  @Column(name = "VCDM_NAME")
  private String vcdmName;

  @Column(name = "VCDM_REVISION")
  private Long vcdmRevision;

  @Column(name = "VCDM_VARIANT")
  private String vcdmVariant;

  @ManyToOne
  @JoinColumn(name = "PST_ID", nullable = false, insertable = false, updatable = false)
  private TvcdmPst TvcdmPst;


  public TvcdmPstCont() {}

  public Long getFileId() {
    return this.fileId;
  }

  public void setFileId(final Long fileId) {
    this.fileId = fileId;
  }

  public String getFileName() {
    return this.fileName;
  }

  public void setFileName(final String fileName) {
    this.fileName = fileName;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Long getPstId() {
    return this.pstId;
  }

  public void setPstId(final Long pstId) {
    this.pstId = pstId;
  }

  public String getVcdmClass() {
    return this.vcdmClass;
  }

  public void setVcdmClass(final String vcdmClass) {
    this.vcdmClass = vcdmClass;
  }

  public String getVcdmName() {
    return this.vcdmName;
  }

  public void setVcdmName(final String vcdmName) {
    this.vcdmName = vcdmName;
  }

  public Long getVcdmRevision() {
    return this.vcdmRevision;
  }

  public void setVcdmRevision(final Long vcdmRevision) {
    this.vcdmRevision = vcdmRevision;
  }

  public String getVcdmVariant() {
    return this.vcdmVariant;
  }

  public void setVcdmVariant(final String vcdmVariant) {
    this.vcdmVariant = vcdmVariant;
  }


  /**
   * @return the tvcdmPst
   */
  public TvcdmPst getTvcdmPst() {
    return this.TvcdmPst;
  }


  /**
   * @param tvcdmPst the tvcdmPst to set
   */
  public void setTvcdmPst(final TvcdmPst tvcdmPst) {
    this.TvcdmPst = tvcdmPst;
  }

}