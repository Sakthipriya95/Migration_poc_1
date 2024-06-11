package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.annotations.ReadOnly;


/**
 * The persistent class for the TABV_COMMON_PARAMS database table.
 */
@NamedQueries(value = { @NamedQuery(name = TabvCommonParam.NQ_GET_ALL, query = "SELECT cmp FROM TabvCommonParam cmp") })
@Entity
@Table(name = "TABV_COMMON_PARAMS")
@ReadOnly
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class TabvCommonParam implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Named query to get all TabvCommonParam from database
   *
   * @return list of TabvCommonParam
   */
  public static final String NQ_GET_ALL = "TabvCommonParam.getAll";

  @Id
  @Column(name = "PARAM_ID")
  private String paramId;

  @Column(name = "PARAM_DESC")
  private String paramDesc;

  @Column(name = "PARAM_VALUE")
  private String paramValue;

  @Column(name = "\"VERSION\"")
  @Version
  private Long version;

  public TabvCommonParam() {}

  public String getParamId() {
    return this.paramId;
  }

  public void setParamId(final String paramId) {
    this.paramId = paramId;
  }

  public String getParamDesc() {
    return this.paramDesc;
  }

  public void setParamDesc(final String paramDesc) {
    this.paramDesc = paramDesc;
  }

  public String getParamValue() {
    return this.paramValue;
  }

  public void setParamValue(final String paramValue) {
    this.paramValue = paramValue;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

}