package com.bosch.caltool.icdm.database.entity.general;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;


/**
 * The persistent class for the T_MESSAGES database table.
 */
@Entity
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
@Table(name = "TABV_COMMON_PARAMS")
public class TabvCommonParams implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "PARAM_ID")
  private String paramId;

  // @Column(name = "PARAM_NAME")
  // private String paramName;

  @Column(name = "PARAM_DESC")
  private String paramDesc;

  @Column(name = "PARAM_VALUE")
  private String paramValue;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private long version;

  /**
   * Constructor
   */
  public TabvCommonParams() {}

  /**
   * @return the paramId
   */
  public String getParamId() {
    return this.paramId;
  }

  /**
   * @param paramId the paramId to set
   */
  public void setParamId(final String paramId) {
    this.paramId = paramId;
  }


  // /**
  // * @return the paramName
  // */
  // public String getParamName() {
  // return this.paramName;
  // }
  //
  //
  // /**
  // * @param paramName the paramName to set
  // */
  // public void setParamName(final String paramName) {
  // this.paramName = paramName;
  // }


  /**
   * @return the paramDesc
   */
  public String getParamDesc() {
    return this.paramDesc;
  }


  /**
   * @param paramDesc the paramDesc to set
   */
  public void setParamDesc(final String paramDesc) {
    this.paramDesc = paramDesc;
  }


  /**
   * @return the paramValue
   */
  public String getParamValue() {
    return this.paramValue;
  }


  /**
   * @param paramValue the paramValue to set
   */
  public void setParamValue(final String paramValue) {
    this.paramValue = paramValue;
  }


  /**
   * @return the version
   */
  public long getVersion() {
    return this.version;
  }

  /**
   * @param version the version to set
   */
  public void setVersion(final long version) {
    this.version = version;
  }

}