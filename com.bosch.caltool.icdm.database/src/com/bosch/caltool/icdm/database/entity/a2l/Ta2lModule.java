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
 * The persistent class for the TA2L_MODULES database table.
 */
@Entity
@Table(name = "TA2L_MODULES")
@ReadOnly
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class Ta2lModule implements Serializable {

  private static final long serialVersionUID = 1L;

  @Column(name = "FILE_ID")
  private BigDecimal fileId;

  private String longidentifier;

  @Column(name = "MODPAR_ID")
  private BigDecimal modparId;
  @Id
  @Column(name = "MODULE_ID")
  private BigDecimal moduleId;

  private String name;

  private BigDecimal sizeofasciis;

  private BigDecimal sizeofaxispts;

  private BigDecimal sizeofcuboids;

  private BigDecimal sizeofcurves;

  private BigDecimal sizeofmaps;

  private BigDecimal sizeofothertypes;

  private BigDecimal sizeofvalueblocks;

  private BigDecimal sizeofvalues;

  public Ta2lModule() {}

  public BigDecimal getFileId() {
    return this.fileId;
  }

  public void setFileId(final BigDecimal fileId) {
    this.fileId = fileId;
  }

  public String getLongidentifier() {
    return this.longidentifier;
  }

  public void setLongidentifier(final String longidentifier) {
    this.longidentifier = longidentifier;
  }

  public BigDecimal getModparId() {
    return this.modparId;
  }

  public void setModparId(final BigDecimal modparId) {
    this.modparId = modparId;
  }

  public BigDecimal getModuleId() {
    return this.moduleId;
  }

  public void setModuleId(final BigDecimal moduleId) {
    this.moduleId = moduleId;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public BigDecimal getSizeofasciis() {
    return this.sizeofasciis;
  }

  public void setSizeofasciis(final BigDecimal sizeofasciis) {
    this.sizeofasciis = sizeofasciis;
  }

  public BigDecimal getSizeofaxispts() {
    return this.sizeofaxispts;
  }

  public void setSizeofaxispts(final BigDecimal sizeofaxispts) {
    this.sizeofaxispts = sizeofaxispts;
  }

  public BigDecimal getSizeofcuboids() {
    return this.sizeofcuboids;
  }

  public void setSizeofcuboids(final BigDecimal sizeofcuboids) {
    this.sizeofcuboids = sizeofcuboids;
  }

  public BigDecimal getSizeofcurves() {
    return this.sizeofcurves;
  }

  public void setSizeofcurves(final BigDecimal sizeofcurves) {
    this.sizeofcurves = sizeofcurves;
  }

  public BigDecimal getSizeofmaps() {
    return this.sizeofmaps;
  }

  public void setSizeofmaps(final BigDecimal sizeofmaps) {
    this.sizeofmaps = sizeofmaps;
  }

  public BigDecimal getSizeofothertypes() {
    return this.sizeofothertypes;
  }

  public void setSizeofothertypes(final BigDecimal sizeofothertypes) {
    this.sizeofothertypes = sizeofothertypes;
  }

  public BigDecimal getSizeofvalueblocks() {
    return this.sizeofvalueblocks;
  }

  public void setSizeofvalueblocks(final BigDecimal sizeofvalueblocks) {
    this.sizeofvalueblocks = sizeofvalueblocks;
  }

  public BigDecimal getSizeofvalues() {
    return this.sizeofvalues;
  }

  public void setSizeofvalues(final BigDecimal sizeofvalues) {
    this.sizeofvalues = sizeofvalues;
  }

}