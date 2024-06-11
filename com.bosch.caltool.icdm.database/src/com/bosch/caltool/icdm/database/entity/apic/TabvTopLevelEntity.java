package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.OptimisticLocking;


/**
 * icdm-470 The persistent class for the TABV_TOP_LEVEL_ENTITIES database table.
 */
@Entity
@OptimisticLocking(cascade = true)
@Table(name = "TABV_TOP_LEVEL_ENTITIES")
public class TabvTopLevelEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "ENT_ID")
  private long entId;

  @Column(name = "ENTITY_NAME")
  private String entityName;

  @Column(name = "LAST_MOD_DATE", nullable = false)
  private Timestamp lastModDate;

  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;

  /**
   * Empty Constsructor
   */
  public TabvTopLevelEntity() {/* Empty Constructor */}

  /**
   * @return the Top level Entity Id
   */
  public long getEntId() {
    return this.entId;
  }

  /**
   * @param entId Entity Id
   */
  public void setEntId(final long entId) {
    this.entId = entId;
  }


  /**
   * @return the Entity Name
   */
  public String getEntityName() {
    return this.entityName;
  }

  /**
   * @param entityName entityName
   */
  public void setEntityName(final String entityName) {
    this.entityName = entityName;
  }

  /**
   * @return the Last Modified Date
   */
  public Timestamp getLastModDate() {
    return this.lastModDate;
  }

  /**
   * @param lastModDate lastModDate
   */
  public void setLastModDate(final Timestamp lastModDate) {
    this.lastModDate = lastModDate;
  }

  /**
   * @return the version
   */
  public Long getVersion() {
    return this.version;
  }

  /**
   * @param version version
   */
  public void setVersion(final Long version) {
    this.version = version;
  }

}