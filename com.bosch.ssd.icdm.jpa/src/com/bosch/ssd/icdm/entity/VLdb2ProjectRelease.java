package com.bosch.ssd.icdm.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.annotations.Direction;
import org.eclipse.persistence.annotations.NamedStoredProcedureQueries;
import org.eclipse.persistence.annotations.NamedStoredProcedureQuery;
import org.eclipse.persistence.annotations.StoredProcedureParameter;


/**
 * The persistent class for the V_LDB2_PROJECT_RELEASE database table.
 *
 * @author SSN9COB
 */
@Entity
@Table(name = "V_LDB2_PROJECT_RELEASE")
@NamedQuery(name = "VLdb2ProjectRelease.findAll", query = "SELECT v FROM VLdb2ProjectRelease v")
@NamedNativeQueries({
    /*
     * @NamedNativeQuery(name="VLdb2ProjectRelease.findValidList",
     * query="select rev.pro_rev_id from V_Ldb2_Project_Revision rev where rev.vers_id in "+
     * " (select swver.vers_id from v_ldb2_sw_vers swver where swver.villa_swvers_id in "+
     * " (select object_id from v_ldb2_object_tree where node_id=?)) and rev.valid_list='Y' "),
     */
    /**
     * Ger Feature vale for provided release
     *
     * @author SSN9COB
     */
    @NamedNativeQuery(name = "VLdb2ProjectRelease.getFeaValForRelease",
        // "select obj.feature_id,fea.feature_text,obj.value_id,val.value_text "+
        query = "select distinct obj.feature_id,fea.feature_text,obj.value_id,val.value_text " +
            "from v_ldb2_temp_con_release obj,v_ldb2_features fea, v_ldb2_values val" + "where obj.pro_rel_id=?" +
            "and obj.feature_id=fea.feature_id and obj.value_id=val.value_id  and fea.feature_id=val.feature_id" +
            "and val.val_id > 0" + "order by fea.feature_text,val.value_text" + "union" +
            "select distinct fea.feature_id,fea.feature_text,val.value_id,val.value_text" +
            "from v_ldb2_features fea, v_ldb2_values val" +
            "where fea.feature_id=val.feature_id and fea.feature_id in ( select  obj.feature_id" +
            "from v_ldb2_temp_con_release obj" + "where obj.pro_rel_id=?  and OBJ.VAL_ID <0) and val.val_id >0"),
    // ALM-261509
    /**
     * Get complete Fea val for release
     *
     * @author SSN9COB
     */
    @NamedNativeQuery(name = "VLdb2ProjectRelease.getFeaValCompleteForRelease",
        // "select obj.feature_id,fea.feature_text,obj.value_id,val.value_text "+
        query = "select distinct obj.feature_id,fea.feature_text,val.value_id,val.value_text" + // ALM-277858
            " from v_ldb2_temp_con_release obj,v_ldb2_features fea, v_ldb2_values val" + " where obj.pro_rel_id=? " +
            " and obj.feature_id=fea.feature_id  and fea.feature_id=val.feature_id  " +
            " order by fea.feature_text,val.value_text") })
/**
 * Stored PRocedure query calls
 *
 * @author SSN9COB
 */
@NamedStoredProcedureQueries({
    /**
     * Invoke icdm config release wrapper
     *
     * @author SSN9COB
     */
    @NamedStoredProcedureQuery(name = "VLdb2ProjectRelease.wrapper_configRelease", procedureName = "wrapper_icdmconfigRelease", parameters = {
        @StoredProcedureParameter(queryParameter = "revisionId", name = "revisionId", type = BigDecimal.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "releaseId", name = "releaseId", type = BigDecimal.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "result", name = "result", type = String.class, direction = Direction.OUT) }),
    // ALM-305433
    /**
     * Invoke icdm compli release wrapper
     *
     * @author SSN9COB
     */
    @NamedStoredProcedureQuery(name = "VLdb2ProjectRelease.wrapper_configReleaseCompli", procedureName = "wrapper_icdmconfigCompliRel", parameters = {
        @StoredProcedureParameter(queryParameter = "revisionId", name = "revisionId", type = BigDecimal.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "releaseId", name = "releaseId", type = BigDecimal.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "result", name = "result", type = String.class, direction = Direction.OUT) }),

  @NamedStoredProcedureQuery(name = "VLdb2ProjectRelease.pr_check_nodes_for_release", procedureName = "k5esk_ldb2.pr_assign_nodes_non_sdom_rel", parameters = {
        @StoredProcedureParameter(queryParameter = "pn_pro_rev_id", name = "pn_pro_rev_id", type = BigDecimal.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "pn_pro_rel_id", name = "pn_pro_rel_id", type = BigDecimal.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "primary_id", name = "primary_id", type = String.class, direction = Direction.OUT) }),
          
    /**
     * invoke find valud list procedure
     *
     * @author SSN9COB
     */
    @NamedStoredProcedureQuery(name = "VLdb2ProjectRelease.findValidList", procedureName = "FINDVALIDLIST", parameters = {
        @StoredProcedureParameter(queryParameter = "P_NODE_ID", name = "P_NODE_ID", type = BigDecimal.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "Pro_rev_id", name = "Pro_rev_id", type = BigDecimal.class, direction = Direction.OUT) }) })


@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class VLdb2ProjectRelease implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Created date
   */
  @Temporal(TemporalType.DATE)
  @Column(name = "CRE_DATE")
  private Date creDate;

  /**
   * Created user
   */
  @Column(name = "CRE_USER")
  private String creUser;

  /**
   * Description
   */
  private String description;

  /**
   * Global Check
   */
  @Column(name = "GLOBAL_CHK")
  private String globalChk;

  /**
   * Global SSD Header
   */
  @Column(name = "GLOBAL_SSDHEADER")
  private String globalSsdheader;

  /**
   * Modified date
   */
  @Temporal(TemporalType.DATE)
  @Column(name = "MOD_DATE")
  private Date modDate;

  /**
   * Modified date
   */
  @Column(name = "MOD_USER")
  private String modUser;

  /**
   * PRORELID
   */
  @Id
  @GeneratedValue(generator = "ldb2_all")
  @Column(name = "PRO_REL_ID")
  private BigDecimal proRelId;

  /**
   * PROREVID
   */
  @Column(name = "PRO_REV_ID")
  private BigDecimal proRevId;

  /**
   * RELID
   */
  @Column(name = "REL_ID")
  private BigDecimal relId;

  /**
   * REL TYPE
   */
  @Column(name = "REL_TYP")
  private String relTyp;

  /**
   *
   */
  public VLdb2ProjectRelease() {
    // constructor
  }

  /**
   * @return date
   */
  public Date getCreDate() {
    return (Date) this.creDate.clone();
  }

  /**
   * @param creDate date
   */
  public void setCreDate(final Date creDate) {
    this.creDate = (Date) creDate.clone();
  }

  /**
   * @return user
   */
  public String getCreUser() {
    return this.creUser;
  }

  /**
   * @param creUser user
   */
  public void setCreUser(final String creUser) {
    this.creUser = creUser;
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
   * @return globalChk
   */
  public String getGlobalChk() {
    return this.globalChk;
  }

  /**
   * @param globalChk globalChk
   */
  public void setGlobalChk(final String globalChk) {
    this.globalChk = globalChk;
  }

  /**
   * @return headaer
   */
  public String getGlobalSsdheader() {
    return this.globalSsdheader;
  }

  /**
   * @param globalSsdheader globalSsdheader
   */
  public void setGlobalSsdheader(final String globalSsdheader) {
    this.globalSsdheader = globalSsdheader;
  }

  /**
   * @return date
   */
  public Date getModDate() {
    return (Date) this.modDate.clone();
  }

  /**
   * @param modDate mod date
   */
  public void setModDate(final Date modDate) {
    this.modDate = (Date) modDate.clone();
  }

  /**
   * @return modUser
   */
  public String getModUser() {
    return this.modUser;
  }

  /**
   * @param modUser modUser
   */
  public void setModUser(final String modUser) {
    this.modUser = modUser;
  }

  /**
   * @return proRelId
   */
  public BigDecimal getProRelId() {
    return this.proRelId;
  }

  /**
   * @param proRelId proRelId
   */
  public void setProRelId(final BigDecimal proRelId) {
    this.proRelId = proRelId;
  }

  /**
   * @return proRevId
   */
  public BigDecimal getProRevId() {
    return this.proRevId;
  }

  /**
   * @param proRevId proRevId
   */
  public void setProRevId(final BigDecimal proRevId) {
    this.proRevId = proRevId;
  }

  /**
   * @return relId
   */
  public BigDecimal getRelId() {
    return this.relId;
  }

  /**
   * @param relId relId
   */
  public void setRelId(final BigDecimal relId) {
    this.relId = relId;
  }

  /**
   * @return relTyp
   */
  public String getRelTyp() {
    return this.relTyp;
  }

  /**
   * @param relTyp relTyp
   */
  public void setRelTyp(final String relTyp) {
    this.relTyp = relTyp;
  }

}