package com.bosch.caltool.icdm.database.entity.comppkg;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.OptimisticLocking;

import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;


/**
 * The persistent class for the T_CP_RULE_ATTRS database table.
 */
@Entity
@OptimisticLocking(cascade = true)
@Table(name = "T_CP_RULE_ATTRS")
@NamedQuery(name = "TCpRuleAttr.findAll", query = "SELECT t FROM TCpRuleAttr t")
public class TCpRuleAttr implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMP_SEQ_GENERATOR")
  @Column(name = "CP_RULE_ATTR_ID", unique = true, nullable = false)
  private long cpRuleAttrId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private long version;

  // bi-directional many-to-one association to TabvAttribute
  @ManyToOne
  @JoinColumn(name = "ATTR_ID", nullable = false)
  private TabvAttribute tabvAttribute;

  // bi-directional many-to-one association to TCompPkg
  @ManyToOne
  @JoinColumn(name = "COMP_PKG_ID", nullable = false)
  private TCompPkg TCompPkg;

  public TCpRuleAttr() {}

  public long getCpRuleAttrId() {
    return this.cpRuleAttrId;
  }

  public void setCpRuleAttrId(final long cpRuleAttrId) {
    this.cpRuleAttrId = cpRuleAttrId;
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

  public TCompPkg getTCompPkg() {
    return this.TCompPkg;
  }

  public void setTCompPkg(final TCompPkg TCompPkg) {
    this.TCompPkg = TCompPkg;
  }

}