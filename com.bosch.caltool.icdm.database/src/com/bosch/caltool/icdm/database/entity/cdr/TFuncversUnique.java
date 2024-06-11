package com.bosch.caltool.icdm.database.entity.cdr;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.annotations.ReadOnly;


/**
 * The persistent class for the T_FUNCVERS_UNIQUE database table.
 */
@NamedQuery(name = TFuncversUnique.NQ_GET_VERS_BY_FUNCNAME, query = "SELECT distinct funcvers FROM TFuncversUnique funcvers where funcvers.upperName=:fname")
@Entity
@ReadOnly
@Table(name = "T_FUNCVERS_UNIQUE")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class TFuncversUnique implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Named query to function versions of the given function
   *
   * @param fname function name
   */
  public static final String NQ_GET_VERS_BY_FUNCNAME = "TFuncversUnique.getVersionsByFuncName";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(unique = true, nullable = false)
  private long id;

  @Column(name = "FUNCNAME", length = 255, nullable = false)
  private String funcname;

  @Column(name = "FUNCVERSION", length = 255, nullable = false)
  private String funcversion;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "FUNCTION_NAME_UPPER", length = 255)
  private String upperName;

  public TFuncversUnique() {}

  public long getId() {
    return this.id;
  }

  public void setId(final long id) {
    this.id = id;
  }


  public String getFuncname() {
    return this.funcname;
  }


  public void setFuncname(final String funcname) {
    this.funcname = funcname;
  }


  public String getFuncversion() {
    return this.funcversion;
  }


  public void setFuncversion(final String funcversion) {
    this.funcversion = funcversion;
  }

  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }


  /**
   * @return the upperName
   */
  public String getUpperName() {
    return this.upperName;
  }


  /**
   * @param upperName the upperName to set
   */
  public void setUpperName(final String upperName) {
    this.upperName = upperName;
  }
}