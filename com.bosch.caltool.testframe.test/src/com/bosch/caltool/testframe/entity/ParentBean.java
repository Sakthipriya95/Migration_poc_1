/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.testframe.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;


/**
 * @author bne4cob
 */
@Entity
@Table(name = "TEST_PARENT_TABLE")
public class ParentBean implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "SEQ_TEMP_GEN", sequenceName = "SEQ_TEMP", allocationSize = 10)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TEMP_GEN")
  @Column(name = "PAR_ID", unique = true, nullable = false)
  private long id;

  @Column(name = "PAR_NAME", nullable = false, length = 50)
  private String name;

  @Column(name = "PAR_ADDR", length = 200)
  private String address;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  // bi-directional many-to-one association to TabvAttrGroup
  @OneToMany(mappedBy = "parent")
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<ChildBean> childBeans;

  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private long version;

  /**
   * @return the id
   */
  public long getId() {
    return this.id;
  }

  /**
   * @param id the id to set
   */
  public void setId(final long id) {
    this.id = id;
  }

  /**
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * @param name the name to set
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * @return the address
   */
  public String getAddress() {
    return this.address;
  }

  /**
   * @param address the address to set
   */
  public void setAddress(final String address) {
    this.address = address;
  }

  /**
   * @return the createdDate
   */
  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * @return the childBeans
   */
  public List<ChildBean> getChildBeans() {
    return this.childBeans;
  }

  /**
   * @param childBeans the childBeans to set
   */
  public void setChildBeans(final List<ChildBean> childBeans) {
    this.childBeans = childBeans;
  }

  /**
   * @return the version
   */
  public long getVersion() {
    return this.version;
  }

  /**
   * @param version the version to set
   */
  public void setVersion(final long version) {
    this.version = version;
  }

}
