package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;


/**
 * The persistent class for the GTT_PARAMETERS database table.
 */
@Entity
@Table(name = "GTT_PARAMETERS")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class GttParameter implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(unique = true, nullable = false)
  private long id;

  @Column(name = "PARAM_NAME", length = 255)
  private String paramName;

  @Column(name = "\"TYPE\"", length = 30)
  private String type;

  @Column(name = "GROUP_ID")
  private Long groupId;


  public GttParameter() {}

  public long getId() {
    return this.id;
  }

  public void setId(final long id) {
    this.id = id;
  }

  public String getParamName() {
    return this.paramName;
  }

  public void setParamName(final String paramName) {
    this.paramName = paramName;
  }

  public String getType() {
    return this.type;
  }

  public void setType(final String type) {
    this.type = type;
  }


  public Long getGroupId() {
    return this.groupId;
  }

  public void setGroupId(final Long groupId) {
    this.groupId = groupId;
  }
}