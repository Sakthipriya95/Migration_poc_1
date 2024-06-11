package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;

/**
 * The persistent class for the TABV_PROJECT_ATTR database table.
 *
 * @author bne4cob
 */
@NamedQueries(value = {

    @NamedQuery(name = TabvProjectAttr.NQ_GET_STRUCT_ATTR_FOR_ALL_VERS, query = "SELECT projAttr FROM TabvProjectAttr projAttr, TabvProjectidcard pidc " +
        "where projAttr.tabvAttribute.attrId IN (select attr.attrId from TabvAttribute attr where attr.attrLevel > 0 ) " +
        "  and projAttr.tPidcVersion.tabvProjectidcard.projectId = pidc.projectId"),

    @NamedQuery(name = TabvProjectAttr.NQ_GET_STRUCT_ATTR_FOR_ALL_NON_ACTIVE_VERS, query = "SELECT projAttr FROM TabvProjectAttr projAttr, TabvProjectidcard pidc " +
        "where projAttr.tabvAttribute.attrId IN (select attr.attrId from TabvAttribute attr where attr.attrLevel > 0 ) " +
        "  and projAttr.tPidcVersion.tabvProjectidcard.projectId = pidc.projectId and pidc.proRevId != projAttr.tPidcVersion.proRevId"),

    @NamedQuery(name = TabvProjectAttr.NQ_GET_STRUCT_ATTR_WITH_PIDC, query = "SELECT projAttr.tabvAttribute.attrId, projAttr.tabvAttrValue.valueId, pidc.projectId FROM TabvProjectAttr projAttr, TabvProjectidcard pidc " +
        "where projAttr.tabvAttribute.attrId IN (select attr.attrId from TabvAttribute attr where attr.attrLevel > 0 ) " +
        "  and projAttr.tPidcVersion.tabvProjectidcard.projectId = pidc.projectId and pidc.proRevId = projAttr.tPidcVersion.proRevId " +
        "  and projAttr.tPidcVersion.tabvProjectidcard.projectId in :pidcIDColl"),
    @NamedQuery(name = TabvProjectAttr.NQ_GET_STRUCT_ATTR_FOR_ALL_ACT_VERS, query = "SELECT projAttr FROM TabvProjectAttr projAttr, TabvProjectidcard pidc " +
        "where projAttr.tabvAttribute.attrId IN (select attr.attrId from TabvAttribute attr where attr.attrLevel > 0 ) " +
        "  and projAttr.tPidcVersion.tabvProjectidcard.projectId = pidc.projectId and pidc.proRevId = projAttr.tPidcVersion.proRevId"),
    @NamedQuery(name = TabvProjectAttr.NQ_GET_STRUCT_ATTR_FOR_ACT_VERS_BY_PIDC, query = "SELECT projAttr FROM TabvProjectAttr projAttr, TabvProjectidcard pidc " +
        "where projAttr.tabvAttribute.attrId IN (select attr.attrId from TabvAttribute attr where attr.attrLevel > 0 ) " +
        "  and projAttr.tPidcVersion.tabvProjectidcard.projectId = pidc.projectId and pidc.proRevId = projAttr.tPidcVersion.proRevId" +
        "  and projAttr.tPidcVersion.tabvProjectidcard.projectId in :pidcIDColl"),
    @NamedQuery(name = TabvProjectAttr.NQ_GET_STRUCT_ATTR_BY_PIDC_VERS_ID, query = "SELECT projAttr FROM TabvProjectAttr projAttr where projAttr.tabvAttribute.attrId IN (select attr.attrId from TabvAttribute attr where attr.attrLevel > 0) " +
        " and projAttr.tPidcVersion.pidcVersId in :pidcVersId"),
    @NamedQuery(name = TabvProjectAttr.GET_PIDCVERSIONIDS_FOR_VALUEID, query = "select projAttr.tPidcVersion.pidcVersId FROM TabvProjectAttr projAttr where projAttr.used='Y' and projAttr.tabvAttrValue.valueId IN :valIDs"),
    @NamedQuery(name = TabvProjectAttr.GET_PROJATTR_FOR_ATTRIBUTEID, query = "SELECT projAttr FROM TabvProjectAttr projAttr where projAttr.tPidcVersion.pidcVersId = :pidcVersId and projAttr.tabvAttribute.attrId = :attrId"),
    @NamedQuery(name = TabvProjectAttr.NQ_FIND_APRJ_PIDC_BY_TEXT_VAL, query = "SELECT prjattr.tPidcVersion.pidcVersId FROM TabvProjectAttr prjattr,TabvAttribute attr ,TabvAttrValue val,TabvProjectidcard proj " +
        "WHERE prjattr.tabvAttribute.attrId=attr.attrId AND prjattr.tPidcVersion.tabvProjectidcard.projectId = proj.projectId " +
        "AND prjattr.tPidcVersion.proRevId=proj.proRevId AND prjattr.tabvAttrValue.valueId=val.valueId " +
        "AND attr.attrId=val.tabvAttribute.attrId AND attr.attrLevel=-20 " +
        "AND (UPPER(val.textvalueEng)=:selAPRJName OR UPPER(val.textvalueGer)=:selAPRJName)"),
    @NamedQuery(name = TabvProjectAttr.NQ_GET_STRUCT_ATTR_FOR_NON_ACT_VERS_BY_PIDC, query = "SELECT projAttr FROM TabvProjectAttr projAttr, TabvProjectidcard pidc " +
        "where projAttr.tabvAttribute.attrId IN (select attr.attrId from TabvAttribute attr where attr.attrLevel > 0 ) " +
        "  and projAttr.tPidcVersion.tabvProjectidcard.projectId = pidc.projectId and pidc.proRevId != projAttr.tPidcVersion.proRevId" +
        "  and projAttr.tPidcVersion.tabvProjectidcard.projectId in :pidcIDColl"),
    @NamedQuery(name = TabvProjectAttr.NQ_GET_STRUCT_ATTR_FOR_ALL_VERS_BY_PIDC, query = "SELECT projAttr FROM TabvProjectAttr projAttr, TabvProjectidcard pidc " +
        "where projAttr.tabvAttribute.attrId IN (select attr.attrId from TabvAttribute attr where attr.attrLevel > 0 ) " +
        "  and projAttr.tPidcVersion.tabvProjectidcard.projectId = pidc.projectId " +
        "  and projAttr.tPidcVersion.tabvProjectidcard.projectId in :pidcIDColl") })


@Entity
@Table(name = "TABV_PROJECT_ATTR")
public class TabvProjectAttr implements Serializable {

  private static final long serialVersionUID = 1L;
  public static final String NQ_GET_STRUCT_ATTR_FOR_ALL_NON_ACTIVE_VERS =
      "TabvProjectAttr.getPidcNonActiveVersionStructureAttrs";
  public static final String NQ_GET_STRUCT_ATTR_FOR_ALL_VERS = "TabvProjectAttr.getPidcVersionStructureAttrs";

  /**
   * Named query to get structure atttributes for the given PIDC IDs
   *
   * @param pidcIDColl collection of PIDC ID
   * @return list of Long array of 3 length. arr[0] - attribute ID; arr[1] - attribute value ID; arr[2] - PIDC ID
   */
  public static final String NQ_GET_STRUCT_ATTR_WITH_PIDC = "TabvProjectAttr.getPidcVersionStructureAttrsForPidcList";


  /**
   * Named query to get structure atttributes for the given pidc version IDs
   *
   * @param pidcVersId collection of PIDC version ID
   */
  public static final String NQ_GET_STRUCT_ATTR_BY_PIDC_VERS_ID = "TabvProjectAttr.getStructAttrByPidcVersId";


  /**
   * Named query to get structure atttributes of all PIDC active versions in the system
   *
   * @return list of TabvProjectAttr
   */
  public static final String NQ_GET_STRUCT_ATTR_FOR_ALL_ACT_VERS = "TabvProjectAttr.getActivePidcVersionStructureAttrs";

  /**
   * Named query to get structure atttributes of all PIDC active versions in the system
   *
   * @param pidcIDColl collection of PIDC ID
   * @return list of TabvProjectAttr
   */
  public static final String NQ_GET_STRUCT_ATTR_FOR_ACT_VERS_BY_PIDC =
      "TabvProjectAttr.getActivePidcVersionStructureAttrsByPidc";
  /**
   * Named query to find Aprj Pidc using text value
   */
  public static final String NQ_FIND_APRJ_PIDC_BY_TEXT_VAL = "TabvAttrValue.NQ_FIND_BY_TEXT_VAL";

  /**
   * Named query to get PIDC Version IDs for a specific value id returns list of PIDC Version ids
   */
  // ICDM-2513, to accept the collection, 'IN' is used in this named query
  public static final String GET_PIDCVERSIONIDS_FOR_VALUEID = "TabvProjectAttr.getPidcVersionIdsListForAValue";
  /**
   * Named query to get PIDC Version IDs for a specific value id returns list of Project Attribute
   */
  public static final String GET_PROJATTR_FOR_ATTRIBUTEID = "TabvProjectAttr.getProjectAttributeByAttributeId";
  /**
   * Named query to get all non active PIDC Versions based on PIDCIDS
   */
  public static final String NQ_GET_STRUCT_ATTR_FOR_NON_ACT_VERS_BY_PIDC =
      "TabvProjectAttr.getNonActivePidcVersionStructureAttrsByPidc";
  /**
   * Named query to get all active and non-active PIDC Versions based on PIDCIDS
   */
  public static final String NQ_GET_STRUCT_ATTR_FOR_ALL_VERS_BY_PIDC =
      "TabvProjectAttr.getAllActivePidcVersionStructureAttrsByPidc";
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "PRJ_ATTR_ID", unique = true, nullable = false, precision = 15)
  private long prjAttrId;

  @Column(nullable = false, length = 1)
  private String used;

  @Column(name = "IS_VARIANT", nullable = false, length = 1)
  private String isVariant;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;

  @Column(name = "PART_NUMBER", length = 255)
  private String partNumber;

  @Column(name = "SPEC_LINK", length = 1000)
  private String specLink;

  @Column(name = "DESCRIPTION", length = 4000)
  private String description;

  @Column(name = "ATTR_HIDDEN_YN", length = 1)
  private String attrHiddenFlag;

  @Column(name = "TRNSFR_VCDM_YN", length = 1)
  private String trnsfrVcdmFlag;

  @Column(name = "FOCUS_MATRIX_YN", length = 1)
  private String focusMatrixYn;

  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;

  @Column(name = "FM_ATTR_REMARK", length = 4000)
  private String fmAttrRemark;


  // bi-directional many-to-one association to TabvAttribute
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ATTR_ID", nullable = false)
  @BatchFetch(value = BatchFetchType.JOIN)
  private TabvAttribute tabvAttribute;

  // bi-directional many-to-one association to TabvAttrValue
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "VALUE_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TabvAttrValue tabvAttrValue;

  // bi-directional many-to-one association to TabvProjectidcard
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PIDC_VERS_ID", nullable = false)
  @BatchFetch(value = BatchFetchType.JOIN)
  private TPidcVersion tPidcVersion;

  public TabvProjectAttr() {}

  public long getPrjAttrId() {
    return this.prjAttrId;
  }

  public void setPrjAttrId(final long prjAttrId) {
    this.prjAttrId = prjAttrId;
  }

  public String getUsed() {
    return this.used;
  }

  public void setUsed(final String used) {
    this.used = used;
  }

  public String getIsVariant() {
    return this.isVariant;
  }

  public void setIsVariant(final String isVariant) {
    this.isVariant = isVariant;
  }

  public TabvAttribute getTabvAttribute() {
    return this.tabvAttribute;
  }

  public void setTabvAttribute(final TabvAttribute tabvAttribute) {
    this.tabvAttribute = tabvAttribute;
  }

  public TabvAttrValue getTabvAttrValue() {
    return this.tabvAttrValue;
  }

  public void setTabvAttrValue(final TabvAttrValue tabvAttrValue) {
    this.tabvAttrValue = tabvAttrValue;
  }

  public TPidcVersion getTPidcVersion() {
    return this.tPidcVersion;
  }

  public void setTPidcVersion(final TPidcVersion tPidcVersion) {
    this.tPidcVersion = tPidcVersion;
  }


  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }

  public String getCreatedUser() {
    return this.createdUser;
  }

  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }

  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public String getModifiedUser() {
    return this.modifiedUser;
  }

  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public String getPartNumber() {
    return this.partNumber;
  }

  public void setPartNumber(final String partNumber) {
    this.partNumber = partNumber;
  }

  public String getSpecLink() {
    return this.specLink;
  }

  public void setSpecLink(final String specLink) {
    this.specLink = specLink;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * @return the attrHiddenFlag
   */
  public String getAttrHiddenFlag() {
    return this.attrHiddenFlag;
  }


  /**
   * @param attrHiddenFlag the attrHiddenFlag to set
   */
  public void setAttrHiddenFlag(final String attrHiddenFlag) {
    this.attrHiddenFlag = attrHiddenFlag;
  }


  /**
   * @return the trnsfrVcdmFlag
   */
  public String getTrnsfrVcdmFlag() {
    return this.trnsfrVcdmFlag;
  }


  /**
   * @param trnsfrVcdmFlag the trnsfrVcdmFlag to set
   */
  public void setTrnsfrVcdmFlag(final String trnsfrVcdmFlag) {
    this.trnsfrVcdmFlag = trnsfrVcdmFlag;
  }

  /**
   * @return the focusMatrixYn
   */
  public String getFocusMatrixYn() {
    return this.focusMatrixYn;
  }

  /**
   * @param focusMatrixYn the focusMatrixYn to set
   */
  public void setFocusMatrixYn(final String focusMatrixYn) {
    this.focusMatrixYn = focusMatrixYn;
  }


  /**
   * @return the fmAttrRemark
   */
  public String getFmAttrRemark() {
    return this.fmAttrRemark;
  }


  /**
   * @param fmAttrRemark the fmAttrRemark to set
   */
  public void setFmAttrRemark(final String fmAttrRemark) {
    this.fmAttrRemark = fmAttrRemark;
  }
}