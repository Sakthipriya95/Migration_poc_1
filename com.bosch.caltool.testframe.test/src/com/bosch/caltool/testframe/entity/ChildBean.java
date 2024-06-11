/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.testframe.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * @author bne4cob
 */
@Entity
@Table(name = "TEST_CHILD_TABLE")
public class ChildBean implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TEMP_GEN")
  @Column(name = "CHILD_ID", precision = 15)
  private long id;

  @Column(name = "CHILD_NAME", nullable = false, length = 50)
  private String childName;

  // bi-directional many-to-one association to ParentBean
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PAR_ID", nullable = false)
  private ParentBean parent;

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
   * @return the childName
   */
  public String getChildName() {
    return this.childName;
  }

  /**
   * @param childName the childName to set
   */
  public void setChildName(final String childName) {
    this.childName = childName;
  }

  /**
   * @return the parent
   */
  public ParentBean getParent() {
    return this.parent;
  }

  /**
   * @param parent the parent to set
   */
  public void setParent(final ParentBean parent) {
    this.parent = parent;
  }

}
