package com.bosch.caltool.icdm.model.a2l;

import java.math.BigDecimal;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * A2L FILE INFO Model class
 *
 * @author apj4cob
 */
public class A2LFile implements Comparable<A2LFile>, IModel {

  /**
   *
   */
  private static final long serialVersionUID = -4795924963455896242L;
  /**
   * Id
   */
  private Long id;
  /**
   * Asap2version
   */
  private String asap2version;
  /**
   * A2lfilesize
   */
  private BigDecimal a2lfilesize;
  /**
   * A2lfilechecksum
   */
  private String a2lfilechecksum;
  /**
   * Headerversion
   */
  private String headerversion;
  /**
   * Projectlongidentifier
   */
  private String projectlongidentifier;
  /**
   * Filename
   */
  private String filename;
  /**
   * Headercomment
   */
  private String headercomment;
  /**
   * Headerprojectno
   */
  private String headerprojectno;
  /**
   * Projectname
   */
  private String projectname;
  /**
   * Sdom Pver Name
   */
  private String sdomPverName;
  /**
   * Sdom Pver Variant
   */
  private String sdomPverVariant;
  /**
   * Sdom Pver Versid
   */
  private Long sdomPverVersid;
  /**
   * Vcdm A2lfile Id
   */
  private Long vcdmA2lfileId;
  /**
   * Sdom Pver Revision
   */
  private Long sdomPverRevision;
  /**
   * Filedate
   */
  private String filedate;
  /**
   * Num Compli
   */
  private Long numCompli;
  /**
   * Num Param
   */
  private Long numParam;

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return this.id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long id) {
    this.id = id;
  }

  /**
   * @return asap2version
   */
  public String getAsap2version() {
    return this.asap2version;
  }

  /**
   * @param asap2version set asap2version
   */
  public void setAsap2version(final String asap2version) {
    this.asap2version = asap2version;
  }

  /**
   * @return a2lfilesize
   */
  public BigDecimal getA2lfilesize() {
    return this.a2lfilesize;
  }

  /**
   * @param a2lfilesize set a2lfilesize
   */
  public void setA2lfilesize(final BigDecimal a2lfilesize) {
    this.a2lfilesize = a2lfilesize;
  }

  /**
   * @return a2lfilechecksum
   */
  public String getA2lfilechecksum() {
    return this.a2lfilechecksum;
  }

  /**
   * @param a2lfilechecksum set a2lfilechecksum
   */
  public void setA2lfilechecksum(final String a2lfilechecksum) {
    this.a2lfilechecksum = a2lfilechecksum;
  }

  /**
   * @return headerversion
   */
  public String getHeaderversion() {
    return this.headerversion;
  }

  /**
   * @param headerversion set headerversion
   */
  public void setHeaderversion(final String headerversion) {
    this.headerversion = headerversion;
  }

  /**
   * @return projectlongidentifier
   */
  public String getProjectlongidentifier() {
    return this.projectlongidentifier;
  }

  /**
   * @param projectlongidentifier set projectlongidentifier
   */
  public void setProjectlongidentifier(final String projectlongidentifier) {
    this.projectlongidentifier = projectlongidentifier;
  }

  /**
   * @return filename
   */
  public String getFilename() {
    return this.filename;
  }

  /**
   * @param filename set filename
   */
  public void setFilename(final String filename) {
    this.filename = filename;
  }

  /**
   * @return headercomment
   */
  public String getHeadercomment() {
    return this.headercomment;
  }

  /**
   * @param headercomment set headercomment
   */
  public void setHeadercomment(final String headercomment) {
    this.headercomment = headercomment;
  }

  /**
   * @return headerprojectno
   */
  public String getHeaderprojectno() {
    return this.headerprojectno;
  }

  /**
   * @param headerprojectno set headerprojectno
   */
  public void setHeaderprojectno(final String headerprojectno) {
    this.headerprojectno = headerprojectno;
  }

  /**
   * @return projectname
   */
  public String getProjectname() {
    return this.projectname;
  }

  /**
   * @param projectname set projectname
   */
  public void setProjectname(final String projectname) {
    this.projectname = projectname;
  }

  /**
   * @return sdomPverName
   */
  public String getSdomPverName() {
    return this.sdomPverName;
  }

  /**
   * @param sdomPverName set sdomPverName
   */
  public void setSdomPverName(final String sdomPverName) {
    this.sdomPverName = sdomPverName;
  }

  /**
   * @return sdomPverVariant
   */
  public String getSdomPverVariant() {
    return this.sdomPverVariant;
  }

  /**
   * @param sdomPverVariant set sdomPverVariant
   */
  public void setSdomPverVariant(final String sdomPverVariant) {
    this.sdomPverVariant = sdomPverVariant;
  }

  /**
   * @return sdomPverVersid
   */
  public Long getSdomPverVersid() {
    return this.sdomPverVersid;
  }

  /**
   * @param sdomPverVersid set sdomPverVersid
   */
  public void setSdomPverVersid(final Long sdomPverVersid) {
    this.sdomPverVersid = sdomPverVersid;
  }

  /**
   * @return vcdmA2lfileId
   */
  public Long getVcdmA2lfileId() {
    return this.vcdmA2lfileId;
  }

  /**
   * @param vcdmA2lfileId set vcdmA2lfileId
   */
  public void setVcdmA2lfileId(final Long vcdmA2lfileId) {
    this.vcdmA2lfileId = vcdmA2lfileId;
  }

  /**
   * @return sdomPverRevision
   */
  public Long getSdomPverRevision() {
    return this.sdomPverRevision;
  }

  /**
   * @param sdomPverRevision set sdomPverRevision
   */
  public void setSdomPverRevision(final Long sdomPverRevision) {
    this.sdomPverRevision = sdomPverRevision;
  }

  /**
   * @return filedate
   */
  public String getFiledate() {
    return this.filedate;
  }

  /**
   * @param filedate set filedate
   */
  public void setFiledate(final String filedate) {
    this.filedate = filedate;
  }

  /**
   * @return numCompli
   */
  public Long getNumCompli() {
    return this.numCompli;
  }

  /**
   * @param numCompli set numCompli
   */
  public void setNumCompli(final Long numCompli) {
    this.numCompli = numCompli;
  }

  /**
   * @return numParam
   */
  public Long getNumParam() {
    return this.numParam;
  }

  /**
   * @param numParam set numParam
   */
  public void setNumParam(final Long numParam) {
    this.numParam = numParam;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2LFile object) {
    return ModelUtil.compare(getId(), object.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    return ModelUtil.isEqual(getId(), ((A2LFile) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    // Not yet Implemented

  }

}
