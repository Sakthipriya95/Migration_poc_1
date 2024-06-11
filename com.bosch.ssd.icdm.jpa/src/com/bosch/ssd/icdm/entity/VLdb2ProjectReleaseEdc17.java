package com.bosch.ssd.icdm.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;

import com.bosch.ssd.icdm.constants.JPAQueryConstants;


/**
 * The persistent class for the V_LDB2_PROJECT_RELEASE_EDC17 database table.
 *
 * @author SSN9COB
 */
@Entity
@Table(name = "V_LDB2_PROJECT_RELEASE_EDC17")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
@NamedNativeQueries({
    /**
     * Get EDC17 Release by project software version
     *
     * @author SSN9COB
     */
    @NamedNativeQuery(name = JPAQueryConstants.V_LDB2_PROJECT_RELEASE_EDC17_GET_RELEASE_BY_SW_VERSION, query = "select SR.PRO_REL_ID,LL.REV_ID||'.'||SR.REL_ID as Release,LL.DESCRIPTION labelList_DESC,SR.DESCRIPTION as Release_DESC, " +
        "SR.CRE_DATE as cerated_Date,SR.CRE_USER as cerated_user, SR.REL_TYPE as External_Release ,SR.ERRORS as Errors, " +
        "SR.GLOBAL_CHK as Global_ByPass,BR.buggy_Date as Buggy_Date " + "from V_LDB2_SW_VERS SV " +
        "INNER JOIN V_LDB2_PROJECT_REVISION LL ON SV.VERS_ID=LL.VERS_ID " +
        "INNER JOIN V_LDB2_PROJECT_RELEASE_EDC17 SR ON LL.PRO_REV_ID=SR.PRO_REV_ID " +
        "LEFT OUTER  JOIN (select max(buggy_Date) as buggy_date,PRO_REL_ID from V_LDB2_RELEASE_BUGGY  " +
        "GROUP BY PRO_REL_ID ) BR ON SR.PRO_REL_ID=BR.PRO_REL_ID " + "WHERE SV.VILLA_SWVERS_ID=? " +
        "order by REV_ID,Rel_ID,SR.PRO_REV_ID"),
    /**
     * Get feature value
     *
     * @author SSN9COB
     */
    @NamedNativeQuery(name = JPAQueryConstants.V_LDB2_PROJECT_RELEASE_EDC17_FEATURE_VALUE, query = "Select b.feature_Id,b.feature_Text ,c.value_Id, c.value_Text from " +
        "V_LDB2_PRJ_REL_COMP a ,V_LDB2_FEATURES b, V_LDB2_VALUES c where a.pro_Rel_Id=? and " +
        " a.feature_Id=b.feature_Id and a.value_Id=c.value_Id order by b.feature_Text ") })

public class VLdb2ProjectReleaseEdc17 implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Created date
   */
  @Temporal(TemporalType.DATE)
  @Column(name = "CRE_DATE")
  private Date creDate;

  /**
   * Created USer
   */
  @Column(name = "CRE_USER")
  private String creUser;

  /**
   * Description
   */
  private String description;

  /**
   * Errors
   */
  private String errors;

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
   * Modified Date
   */
  @Temporal(TemporalType.DATE)
  @Column(name = "MOD_DATE")
  private Date modDate;

  /**
   * Modified User
   */
  @Column(name = "MOD_USER")
  private String modUser;

  /**
   * Pro Rel ID
   */
  @Id
  @Column(name = "PRO_REL_ID")
  private BigDecimal proRelId;

  /**
   * Pro Rev ID
   */
  @Column(name = "PRO_REV_ID")
  private BigDecimal proRevId;

  /**
   * Rel ID
   */
  @Column(name = "REL_ID")
  private BigDecimal relId;

  /**
   * Rel Type
   */
  @Column(name = "REL_TYPE")
  private String relType;

  /**
   * Constructor
   */
  public VLdb2ProjectReleaseEdc17() {
    // constructor
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
   * @return errors
   */
  public String getErrors() {
    return this.errors;
  }

  /**
   * @param errors errors
   */
  public void setErrors(final String errors) {
    this.errors = errors;
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
   * @return globalSsdheader
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
   * @return modDate
   */
  public Date getModDate() {
    return (Date) this.modDate.clone();
  }

  /**
   * @param modDate modDate
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
   * @return relId relId
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
   * @return relType
   */
  public String getRelType() {
    return this.relType;
  }

  /**
   * @param relType relType
   */
  public void setRelType(final String relType) {
    this.relType = relType;
  }


  /**
   * @return the creDate
   */
  public Date getCreDate() {
    return (Date) this.creDate.clone();
  }


  /**
   * @param creDate the creDate to set
   */
  public void setCreDate(final Date creDate) {
    this.creDate = (Date) creDate.clone();
  }

}