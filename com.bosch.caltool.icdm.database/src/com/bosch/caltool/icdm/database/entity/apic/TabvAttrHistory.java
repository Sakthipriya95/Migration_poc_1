package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the TABV_ATTR_HISTORY database table.
 */
@Entity
@Table(name = "TABV_ATTR_HISTORY")
public class TabvAttrHistory implements Serializable {

  private static final long serialVersionUID = 1L;
  private long id;
  private BigDecimal attrId;
  private BigDecimal attrLevel;
  private String boolvalue;
  private Timestamp createdDate;
  private String createdUser;
  private Timestamp datevalue;
  private String deletedFlag;
  private BigDecimal depenAttrId;
  private BigDecimal depenId;
  private BigDecimal depenValueId;
  private String descEng;
  private String descGer;
  private String grantedUser;
  private String grantright;
  private BigDecimal groupId;
  private Timestamp modifiedDate;
  private String modifiedUser;
  private String nameEng;
  private String nameGer;
  private BigDecimal nodeId;
  private String normalizedFlag;
  private BigDecimal numvalue;
  private Timestamp operationDate;
  private String operationFlag;
  private String operationUser;
  private String othervalue;
  private String owner;
  private BigDecimal prjAttrId;
  private BigDecimal projectId;
  private String readright;
  private BigDecimal revId;
  private String singleMultipleVal;
  private BigDecimal superGroupId;
  private String tableName;
  private String textvalueEng;
  private String textvalueGer;
  private String units;
  private String userName;
  private String valueDescEng;
  private String valueDescGer;
  private BigDecimal valueId;
  private BigDecimal valueTypeId;
  private BigDecimal varAttrId;
  private BigDecimal variantId;
  private String writeright;

  public TabvAttrHistory() {}


  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(unique = true, nullable = false, precision = 15)
  public long getId() {
    return this.id;
  }

  public void setId(final long id) {
    this.id = id;
  }


  @Column(name = "ATTR_ID", precision = 15)
  public BigDecimal getAttrId() {
    return this.attrId;
  }

  public void setAttrId(final BigDecimal attrId) {
    this.attrId = attrId;
  }


  @Column(name = "ATTR_LEVEL", precision = 2)
  public BigDecimal getAttrLevel() {
    return this.attrLevel;
  }

  public void setAttrLevel(final BigDecimal attrLevel) {
    this.attrLevel = attrLevel;
  }


  @Column(length = 1)
  public String getBoolvalue() {
    return this.boolvalue;
  }

  public void setBoolvalue(final String boolvalue) {
    this.boolvalue = boolvalue;
  }


  @Column(name = "CREATED_DATE")
  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }


  @Column(name = "CREATED_USER", length = 100)
  public String getCreatedUser() {
    return this.createdUser;
  }

  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }


  public Timestamp getDatevalue() {
    return this.datevalue;
  }

  public void setDatevalue(final Timestamp datevalue) {
    this.datevalue = datevalue;
  }


  @Column(name = "DELETED_FLAG", length = 1)
  public String getDeletedFlag() {
    return this.deletedFlag;
  }

  public void setDeletedFlag(final String deletedFlag) {
    this.deletedFlag = deletedFlag;
  }


  @Column(name = "DEPEN_ATTR_ID", precision = 15)
  public BigDecimal getDepenAttrId() {
    return this.depenAttrId;
  }

  public void setDepenAttrId(final BigDecimal depenAttrId) {
    this.depenAttrId = depenAttrId;
  }


  @Column(name = "DEPEN_ID", precision = 15)
  public BigDecimal getDepenId() {
    return this.depenId;
  }

  public void setDepenId(final BigDecimal depenId) {
    this.depenId = depenId;
  }


  @Column(name = "DEPEN_VALUE_ID", precision = 15)
  public BigDecimal getDepenValueId() {
    return this.depenValueId;
  }

  public void setDepenValueId(final BigDecimal depenValueId) {
    this.depenValueId = depenValueId;
  }


  @Column(name = "DESC_ENG", length = 4000)
  public String getDescEng() {
    return this.descEng;
  }

  public void setDescEng(final String descEng) {
    this.descEng = descEng;
  }


  @Column(name = "DESC_GER", length = 4000)
  public String getDescGer() {
    return this.descGer;
  }

  public void setDescGer(final String descGer) {
    this.descGer = descGer;
  }


  @Column(name = "GRANTED_USER", length = 15)
  public String getGrantedUser() {
    return this.grantedUser;
  }

  public void setGrantedUser(final String grantedUser) {
    this.grantedUser = grantedUser;
  }


  @Column(length = 1)
  public String getGrantright() {
    return this.grantright;
  }

  public void setGrantright(final String grantright) {
    this.grantright = grantright;
  }


  @Column(name = "GROUP_ID", precision = 15)
  public BigDecimal getGroupId() {
    return this.groupId;
  }

  public void setGroupId(final BigDecimal groupId) {
    this.groupId = groupId;
  }


  @Column(name = "MODIFIED_DATE")
  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }

  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }


  @Column(name = "MODIFIED_USER", length = 100)
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }


  @Column(name = "NAME_ENG", length = 100)
  public String getNameEng() {
    return this.nameEng;
  }

  public void setNameEng(final String nameEng) {
    this.nameEng = nameEng;
  }


  @Column(name = "NAME_GER", length = 100)
  public String getNameGer() {
    return this.nameGer;
  }

  public void setNameGer(final String nameGer) {
    this.nameGer = nameGer;
  }


  @Column(name = "NODE_ID", precision = 15)
  public BigDecimal getNodeId() {
    return this.nodeId;
  }

  public void setNodeId(final BigDecimal nodeId) {
    this.nodeId = nodeId;
  }


  @Column(name = "NORMALIZED_FLAG", length = 1)
  public String getNormalizedFlag() {
    return this.normalizedFlag;
  }

  public void setNormalizedFlag(final String normalizedFlag) {
    this.normalizedFlag = normalizedFlag;
  }


  @Column(precision = 15)
  public BigDecimal getNumvalue() {
    return this.numvalue;
  }

  public void setNumvalue(final BigDecimal numvalue) {
    this.numvalue = numvalue;
  }


  @Column(name = "OPERATION_DATE", nullable = false)
  public Timestamp getOperationDate() {
    return this.operationDate;
  }

  public void setOperationDate(final Timestamp operationDate) {
    this.operationDate = operationDate;
  }


  @Column(name = "OPERATION_FLAG", nullable = false, length = 1)
  public String getOperationFlag() {
    return this.operationFlag;
  }

  public void setOperationFlag(final String operationFlag) {
    this.operationFlag = operationFlag;
  }


  @Column(name = "OPERATION_USER", nullable = false, length = 100)
  public String getOperationUser() {
    return this.operationUser;
  }

  public void setOperationUser(final String operationUser) {
    this.operationUser = operationUser;
  }


  @Column(length = 255)
  public String getOthervalue() {
    return this.othervalue;
  }

  public void setOthervalue(final String othervalue) {
    this.othervalue = othervalue;
  }


  @Column(length = 1)
  public String getOwner() {
    return this.owner;
  }

  public void setOwner(final String owner) {
    this.owner = owner;
  }


  @Column(name = "PRJ_ATTR_ID", precision = 15)
  public BigDecimal getPrjAttrId() {
    return this.prjAttrId;
  }

  public void setPrjAttrId(final BigDecimal prjAttrId) {
    this.prjAttrId = prjAttrId;
  }


  @Column(name = "PROJECT_ID", precision = 15)
  public BigDecimal getProjectId() {
    return this.projectId;
  }

  public void setProjectId(final BigDecimal projectId) {
    this.projectId = projectId;
  }


  @Column(length = 1)
  public String getReadright() {
    return this.readright;
  }

  public void setReadright(final String readright) {
    this.readright = readright;
  }


  @Column(name = "REV_ID", precision = 15)
  public BigDecimal getRevId() {
    return this.revId;
  }

  public void setRevId(final BigDecimal revId) {
    this.revId = revId;
  }


  @Column(name = "SINGLE_MULTIPLE_VAL", length = 1)
  public String getSingleMultipleVal() {
    return this.singleMultipleVal;
  }

  public void setSingleMultipleVal(final String singleMultipleVal) {
    this.singleMultipleVal = singleMultipleVal;
  }


  @Column(name = "SUPER_GROUP_ID", precision = 15)
  public BigDecimal getSuperGroupId() {
    return this.superGroupId;
  }

  public void setSuperGroupId(final BigDecimal superGroupId) {
    this.superGroupId = superGroupId;
  }


  @Column(name = "TABLE_NAME", nullable = false, length = 100)
  public String getTableName() {
    return this.tableName;
  }

  public void setTableName(final String tableName) {
    this.tableName = tableName;
  }


  @Column(name = "TEXTVALUE_ENG", length = 100)
  public String getTextvalueEng() {
    return this.textvalueEng;
  }

  public void setTextvalueEng(final String textvalueEng) {
    this.textvalueEng = textvalueEng;
  }


  @Column(name = "TEXTVALUE_GER", length = 100)
  public String getTextvalueGer() {
    return this.textvalueGer;
  }

  public void setTextvalueGer(final String textvalueGer) {
    this.textvalueGer = textvalueGer;
  }


  @Column(length = 30)
  public String getUnits() {
    return this.units;
  }

  public void setUnits(final String units) {
    this.units = units;
  }


  @Column(name = "USER_NAME", length = 15)
  public String getUserName() {
    return this.userName;
  }

  public void setUserName(final String userName) {
    this.userName = userName;
  }


  @Column(name = "VALUE_DESC_ENG", length = 4000)
  public String getValueDescEng() {
    return this.valueDescEng;
  }

  public void setValueDescEng(final String valueDescEng) {
    this.valueDescEng = valueDescEng;
  }


  @Column(name = "VALUE_DESC_GER", length = 4000)
  public String getValueDescGer() {
    return this.valueDescGer;
  }

  public void setValueDescGer(final String valueDescGer) {
    this.valueDescGer = valueDescGer;
  }


  @Column(name = "VALUE_ID", precision = 15)
  public BigDecimal getValueId() {
    return this.valueId;
  }

  public void setValueId(final BigDecimal valueId) {
    this.valueId = valueId;
  }


  @Column(name = "VALUE_TYPE_ID", precision = 15)
  public BigDecimal getValueTypeId() {
    return this.valueTypeId;
  }

  public void setValueTypeId(final BigDecimal valueTypeId) {
    this.valueTypeId = valueTypeId;
  }


  @Column(name = "VAR_ATTR_ID", precision = 15)
  public BigDecimal getVarAttrId() {
    return this.varAttrId;
  }

  public void setVarAttrId(final BigDecimal varAttrId) {
    this.varAttrId = varAttrId;
  }


  @Column(name = "VARIANT_ID", precision = 15)
  public BigDecimal getVariantId() {
    return this.variantId;
  }

  public void setVariantId(final BigDecimal variantId) {
    this.variantId = variantId;
  }


  @Column(length = 1)
  public String getWriteright() {
    return this.writeright;
  }

  public void setWriteright(final String writeright) {
    this.writeright = writeright;
  }

}