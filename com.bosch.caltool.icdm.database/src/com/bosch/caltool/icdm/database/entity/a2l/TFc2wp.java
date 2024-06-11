package com.bosch.caltool.icdm.database.entity.a2l;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.annotations.ReadOnly;


/**
 * The persistent class for the T_FC2WP database table.
 */
@Entity
@Table(name = "T_FC2WP")
@ReadOnly
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class TFc2wp implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "ID")
  private Long id;

  @Column(name = "CAL_CONTACT1")
  private String calContact1;

  @Column(name = "CAL_CONTACT2")
  private String calContact2;

  private String fc;

  @Column(name = "FC2WP_TYPE")
  private String fc2wpType;


  @Column(name = "WP_GROUP")
  private String wpGroup;

  @Column(name = "WP_NAME_E")
  private String wpNameE;

  @Column(name = "WP_NAME_G")
  private String wpNameG;

  @Column(name = "WP_NUMBER")
  private String wpNumber;

  public TFc2wp() {}

  public String getCalContact1() {
    return this.calContact1;
  }

  public void setCalContact1(final String calContact1) {
    this.calContact1 = calContact1;
  }

  public String getCalContact2() {
    return this.calContact2;
  }

  public void setCalContact2(final String calContact2) {
    this.calContact2 = calContact2;
  }

  public String getFc() {
    return this.fc;
  }

  public void setFc(final String fc) {
    this.fc = fc;
  }

  public String getFc2wpType() {
    return this.fc2wpType;
  }

  public void setFc2wpType(final String fc2wpType) {
    this.fc2wpType = fc2wpType;
  }

  /**
   * @return id
   */
  public Long getId() {
    return this.id;
  }

  /**
   * @param id
   */
  public void setId(final Long id) {
    this.id = id;
  }

  public String getWpGroup() {
    return this.wpGroup;
  }

  public void setWpGroup(final String wpGroup) {
    this.wpGroup = wpGroup;
  }

  public String getWpNameE() {
    return this.wpNameE;
  }

  public void setWpNameE(final String wpNameE) {
    this.wpNameE = wpNameE;
  }

  public String getWpNameG() {
    return this.wpNameG;
  }

  public void setWpNameG(final String wpNameG) {
    this.wpNameG = wpNameG;
  }

  public String getWpNumber() {
    return this.wpNumber;
  }

  public void setWpNumber(final String wpNumber) {
    this.wpNumber = wpNumber;
  }

}