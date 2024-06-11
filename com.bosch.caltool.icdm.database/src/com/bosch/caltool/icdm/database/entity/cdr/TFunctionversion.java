package com.bosch.caltool.icdm.database.entity.cdr;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.annotations.ReadOnly;


/**
 * The persistent class for the T_FUNCTIONVERSIONS database table.
 */
@Entity
@ReadOnly
@Table(name = "T_FUNCTIONVERSIONS")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class TFunctionversion implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "T_FUNCTIONVERSIONS_ID_GENERATOR", sequenceName = "SEQ_ID")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "T_FUNCTIONVERSIONS_ID_GENERATOR")
  @Column(unique = true, nullable = false)
  private long id;

  @Column(length = 255)
  private String defcharname;

  @Column(nullable = false, length = 255)
  private String funcname;

  @Column(length = 255)
  private String funcversion;

  @Column(name = "FUNCTION_NAME_UPPER", length = 255)
  private String funcNameUpper;


  @Column(name = "\"VERSION\"")
  private Long version;

  public TFunctionversion() {}

  public long getId() {
    return this.id;
  }

  public void setId(final long id) {
    this.id = id;
  }

  public String getDefcharname() {
    return this.defcharname;
  }

  public void setDefcharname(final String defcharname) {
    this.defcharname = defcharname;
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

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  /**
   * @return the funcNameUpper
   */
  public String getFuncNameUpper() {
    return this.funcNameUpper;
  }

  /**
   * @param funcNameUpper the funcNameUpper to set
   */
  public void setFuncNameUpper(final String funcNameUpper) {
    this.funcNameUpper = funcNameUpper;
  }
}