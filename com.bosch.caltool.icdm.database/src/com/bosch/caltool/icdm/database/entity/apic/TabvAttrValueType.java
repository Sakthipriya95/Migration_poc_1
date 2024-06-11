package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.annotations.ReadOnly;


/**
 * The persistent class for the TABV_ATTR_VALUE_TYPES database table.
 */
@Entity
@Table(name = "TABV_ATTR_VALUE_TYPES")
@ReadOnly
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class TabvAttrValueType implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "VALUE_TYPE_ID", unique = true, nullable = false, precision = 15)
  private long valueTypeId;

  @Column(name = "VALUE_TYPE", nullable = false, length = 25)
  private String valueType;

  // bi-directional many-to-one association to TabvAttribute
  @OneToMany(mappedBy = "tabvAttrValueType")
  private List<TabvAttribute> tabvAttributes;

  public TabvAttrValueType() {}

  public long getValueTypeId() {
    return this.valueTypeId;
  }

  public void setValueTypeId(final long valueTypeId) {
    this.valueTypeId = valueTypeId;
  }

  public String getValueType() {
    return this.valueType;
  }

  public void setValueType(final String valueType) {
    this.valueType = valueType;
  }

  public List<TabvAttribute> getTabvAttributes() {
    return this.tabvAttributes;
  }

  public void setTabvAttributes(final List<TabvAttribute> tabvAttributes) {
    this.tabvAttributes = tabvAttributes;
  }

}