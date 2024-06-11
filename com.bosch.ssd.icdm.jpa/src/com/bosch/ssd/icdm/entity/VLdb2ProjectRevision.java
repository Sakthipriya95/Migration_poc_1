package com.bosch.ssd.icdm.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;


/**
 * The persistent class for the V_LDB2_PROJECT_REVISION database table.
 *
 * @author SSN9COB
 */
@Entity
@Table(name = "V_LDB2_PROJECT_REVISION")
@NamedQueries({
    /**
     * Find All Query
     * 
     * @author SSN9COB
     */
    @NamedQuery(name = "VLdb2ProjectRevision.findAll", query = "SELECT v FROM VLdb2ProjectRevision v"),

    /**
     * Find Max Rev id query
     * 
     * @author SSN9COB
     */
    @NamedQuery(name = "VLdb2ProjectRevision.findMaxRevId", query = "SELECT COALESCE(MAX(r.revId), 0) " +
        "FROM VLdb2ProjectRevision r " +
        "WHERE r.versId = :versId", hints = { @QueryHint(name = QueryHints.REFRESH, value = HintValues.TRUE) }),

    /**
     * Get QSSD Labels
     * 
     * @author SSN9COB
     */
    @NamedQuery(name = "VLdb2ProjectRevision.getQSSDLabels", query = "select labellist.label from VLdb2ProjectLabelsList labellist where  labellist.proRevId = " +
        "(select revision.proRevId from VLdb2ProjectRevision revision where revision.versId =" +
        " (select swVer.versId from VLdb2SwVer swVer where swVer.villaSwversId =" +
        "(select tree.objectId from VLdb2ObjectTree tree where tree.nodeId = :nodeid)) and revision.validList ='Y') "),

    /**
     * Get Pr0 Rev ID
     * 
     * @author SSN9COB
     */
    @NamedQuery(name = "VLdb2ProjectRevision.getProRevID", query = "select revision.proRevId from VLdb2ProjectRevision revision where revision.versId =" +
        " (select swVer.versId from VLdb2SwVer swVer where swVer.villaSwversId =" +
        "(select tree.objectId from VLdb2ObjectTree tree where tree.nodeId = :nodeid)) and revision.validList ='Y'") })

@NamedNativeQuery(name = "VLdb2ProjectRevision.versId", query = "SELECT vers_id FROM V_Ldb2_Sw_Vers where " +
    " VILLA_SWVERS_ID = (select object_id from v_ldb2_object_tree where node_id= ?)")


@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class VLdb2ProjectRevision implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * asapFile
   */
  @Column(name = "ASAP_FILE")
  private String asapFile;

  /**
   * Description
   */
  private String description;

  /*
   * Hide
   */
  private String hide = "N";

  /**
   * Pro Rev Id
   */
  @Id
  @GeneratedValue(generator = "ldb2_all")
  @Column(name = "PRO_REV_ID")
  private BigDecimal proRevId;

  /**
   * Rev Id
   */
  @Column(name = "REV_ID")
  private BigDecimal revId;

  /**
   * Valid List
   */
  @Column(name = "VALID_LIST")
  private String validList = "N";

  /**
   * Vers ID
   */
  @Column(name = "VERS_ID")
  private BigDecimal versId;

  /**
   *
   */
  public VLdb2ProjectRevision() {
    // constructor
  }

  /**
   * @return file
   */
  public String getAsapFile() {
    return this.asapFile;
  }

  /**
   * @param asapFile file
   */
  public void setAsapFile(final String asapFile) {
    this.asapFile = asapFile;
  }

  /**
   * @return description
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * @param description description
   */
  public void setDescription(final String description) {
    this.description = description;
  }

  /**
   * @return hide
   */
  public String getHide() {
    return this.hide;
  }

  /**
   * @param hide hide
   */
  public void setHide(final String hide) {
    this.hide = hide;
  }

  /**
   * @return rev id
   */
  public BigDecimal getProRevId() {
    return this.proRevId;
  }

  /**
   * @param proRevId -pro rev id
   */
  public void setProRevId(final BigDecimal proRevId) {
    this.proRevId = proRevId;
  }

  /**
   * @return rev id
   */
  public BigDecimal getRevId() {
    return this.revId;
  }

  /**
   * @param revId rev id
   */
  public void setRevId(final BigDecimal revId) {
    this.revId = revId;
  }

  /**
   * @return validList
   */
  public String getValidList() {
    return this.validList;
  }

  /**
   * @param validList valid List
   */
  public void setValidList(final String validList) {
    this.validList = validList;
  }

  /**
   * @return version id
   */
  public BigDecimal getVersId() {
    return this.versId;
  }

  /**
   * @param versId version id
   */
  public void setVersId(final BigDecimal versId) {
    this.versId = versId;
  }

}