package com.bosch.caltool.icdm.database.entity.cdr;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.OptimisticLocking;

import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;


/**
 * Icdm-1032 - New Entity for Param and Attribute Mapping The persistent class for the T_PARAM_ATTRS database table.
 */

@NamedQueries(value = {
    @NamedQuery(name = TParamAttr.NQ_GET_PARAM_ATTRS, query = "SELECT temp.objName FROM GttObjectName temp,TParamAttr paramAttr  where temp.objName=paramAttr.TParameter.name "),
    @NamedQuery(name = TParamAttr.NQ_GET_PARAM_ATTR_BY_ID, query = "SELECT pattr FROM TParamAttr pattr,GttObjectName temp where temp.id=pattr.TParameter.id"),
    @NamedQuery(name = TParamAttr.NQ_GET_PARAM_ATTR_BY_PARAMLIST, query = "SELECT pattr FROM TParamAttr pattr where pattr.TParameter.id in :paramList"),
    @NamedQuery(name = TParameter.NQ_GET_PARAMS_IN_LIST, query = "SELECT tparam from TParameter tparam where tparam.name IN :paramNameSet"),
    @NamedQuery(name = TParamAttr.NQ_GET_ATTR_ID_BY_PARAM, query = "SELECT temp.objName, pattr.tabvAttribute.attrId from TParamAttr pattr, GttObjectName temp" +
        "      where pattr.TParameter.name = temp.objName and pattr.tabvAttribute.deletedFlag='N'"),
    @NamedQuery(name = TParameter.NQ_GET_PARAM_OBJ_BY_PARAM_NAME, query = "SELECT distinct param FROM TFunction f, TFunctionversion funcver" +
        ",TParameter param,GttObjectName temp  where funcver.defcharname = param.name and funcver.funcname =f.name " +
        "and param.name=temp.objName"),
    @NamedQuery(name = TParameter.NQ_GET_INVALID_LABEL, query = "SELECT temp.objName FROM TParameter p,GttObjectName temp  where " +
        "temp.objName=p.name") })
@Entity
@OptimisticLocking(cascade = true)
@Table(name = "T_PARAM_ATTRS")
public class TParamAttr implements Serializable {


  public static final String NQ_GET_PARAM_ATTRS = "getparamattrs";

  public static final String NQ_GET_PARAM_ATTR_BY_ID = "getparamattrbyid";

  public static final String NQ_GET_PARAM_ATTR_BY_PARAMLIST = "getparamattrbyparamlist";

  public static final String NQ_GET_ATTR_ID_BY_PARAM = "TParamAttr.getAttrIdByParamName";


  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "PARAM_ATTR_ID")
  private long paramAttrId;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private long version;

  // bi-directional many-to-one association to TabvAttribute
  @ManyToOne
  @JoinColumn(name = "ATTR_ID")
  private TabvAttribute tabvAttribute;

  // bi-directional many-to-one association to TParameter
  @ManyToOne
  @JoinColumn(name = "PARAM_ID")
  private TParameter TParameter;


  public TParamAttr() {}

  public long getParamAttrId() {
    return this.paramAttrId;
  }

  public void setParamAttrId(final long paramAttrId) {
    this.paramAttrId = paramAttrId;
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

  public long getVersion() {
    return this.version;
  }

  public void setVersion(final long version) {
    this.version = version;
  }

  public TabvAttribute getTabvAttribute() {
    return this.tabvAttribute;
  }

  public void setTabvAttribute(final TabvAttribute tabvAttribute) {
    this.tabvAttribute = tabvAttribute;
  }

  public TParameter getTParameter() {
    return this.TParameter;
  }

  public void setTParameter(final TParameter TParameter) {
    this.TParameter = TParameter;
  }

}