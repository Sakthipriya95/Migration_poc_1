/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.rm;

import java.util.ArrayList;
import java.util.List;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author rgo7cob
 */
public class PidcRmDefinition implements IModel, Comparable<PidcRmDefinition> {


  /**
   *
   */
  private static final long serialVersionUID = 6836499747073313903L;
  private long id;
  private String rmNameEng;
  private String rmNameGer;

  private String rmDescEng;
  private String rmDescGer;

  private String desc;


  private Long pidcVersId;


  private Long version;

  private String name;

  private List<Long> varIds;


  private List<Long> subVarIds;

  private String isVariant;


  /**
   * @return the rmNameEng
   */
  public String getRmNameEng() {
    return this.rmNameEng;
  }

  /**
   * @param rmNameEng the rmNameEng to set
   */
  public void setRmNameEng(final String rmNameEng) {
    this.rmNameEng = rmNameEng;
  }

  /**
   * @return the rmNameGer
   */
  public String getRmNameGer() {
    return this.rmNameGer;
  }

  /**
   * @param rmNameGer the rmNameGer to set
   */
  public void setRmNameGer(final String rmNameGer) {
    this.rmNameGer = rmNameGer;
  }

  /**
   * @return the pidcVersId
   */
  public Long getPidcVersId() {
    return this.pidcVersId;
  }

  /**
   * @param pidcVersId the pidcVersId to set
   */
  public void setPidcVersId(final Long pidcVersId) {
    this.pidcVersId = pidcVersId;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final PidcRmDefinition obj) {
    return ModelUtil.compare(getName(), obj.getName());

  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    // If the object is not saved in the database then adding to set has problems
    if (obj.getClass() == this.getClass()) {
      // Both id and name should be equal
      return ModelUtil.isEqual(getName(), ((PidcRmDefinition) obj).getName()) && ModelUtil.isEqual(getId(), ((PidcRmDefinition) obj).getId());
    }
    return false;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return this.id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long pidcRmId) {
    this.id = pidcRmId;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return this.version;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    this.version = version;

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
   * @return the varIds
   */
  public List<Long> getVarIds() {
    return this.varIds;
  }


  /**
   * @param varIds the varIds to set
   */
  public void setVarIds(final List<Long> varIds) {
    if (varIds != null) {
      this.varIds = new ArrayList<>(varIds);
    }
  }


  /**
   * @return the subVarIds
   */
  public List<Long> getSubVarIds() {
    return this.subVarIds;
  }


  /**
   * @param subVarIds the subVarIds to set
   */
  public void setSubVarIds(final List<Long> subVarIds) {
    if (subVarIds != null) {
      this.subVarIds = new ArrayList<>(subVarIds);
    }
  }


  /**
   * @return the rmDescEng
   */
  public String getRmDescEng() {
    return this.rmDescEng;
  }


  /**
   * @param rmDescEng the rmDescEng to set
   */
  public void setRmDescEng(final String rmDescEng) {
    this.rmDescEng = rmDescEng;
  }


  /**
   * @return the rmDescGer
   */
  public String getRmDescGer() {
    return this.rmDescGer;
  }


  /**
   * @param rmDescGer the rmDescGer to set
   */
  public void setRmDescGer(final String rmDescGer) {
    this.rmDescGer = rmDescGer;
  }


  /**
   * @return the desc
   */
  public String getDesc() {
    return this.desc;
  }


  /**
   * @param desc the desc to set
   */
  public void setDesc(final String desc) {
    this.desc = desc;
  }


  /**
   * @return the isVaraint
   */
  public String getIsVariant() {
    return this.isVariant;
  }


  /**
   * @param isVaraint the isVaraint to set
   */
  public void setIsVariant(final String isVaraint) {
    this.isVariant = isVaraint;
  }

}
