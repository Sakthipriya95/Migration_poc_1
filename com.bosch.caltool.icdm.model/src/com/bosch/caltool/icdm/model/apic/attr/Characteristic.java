package com.bosch.caltool.icdm.model.apic.attr;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * Characteristic Model class
 *
 * @author dmo5cob
 */
public class Characteristic implements Comparable<Characteristic>, IDataObject {

  /**
   *
   */
  private static final long serialVersionUID = 3852687446359805160L;
  /**
   * Char Id
   */
  private Long id;
  /**
   * Char Name Eng
   */
  private String name;
  /**
   * Char Name Eng
   */
  private String charNameEng;
  /**
   * Char Name Ger
   */
  private String charNameGer;
  /**
   * Char Desc
   */
  private String description;
  /**
   * Desc Eng
   */
  private String descEng;
  /**
   * Desc Ger
   */
  private String descGer;
  /**
   * Created Date
   */
  private String createdDate;
  /**
   * Created User
   */
  private String createdUser;
  /**
   * Modified Date
   */
  private String modifiedDate;
  /**
   * Modified User
   */
  private String modifiedUser;
  /**
   * Version
   */
  private Long version;
  /**
   * Focus Matrix Yn
   */
  private String focusMatrixYn;

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
  public void setId(final Long id) {
    this.id = id;
  }

  /**
   * @return charNameEng
   */
  public String getCharNameEng() {
    return this.charNameEng;
  }

  /**
   * @param charNameEng set charNameEng
   */
  public void setCharNameEng(final String charNameEng) {
    this.charNameEng = charNameEng;
  }

  /**
   * @return charNameGer
   */
  public String getCharNameGer() {
    return this.charNameGer;
  }

  /**
   * @param charNameGer set charNameGer
   */
  public void setCharNameGer(final String charNameGer) {
    this.charNameGer = charNameGer;
  }


  /**
   * @return the description
   */
  @Override
  public String getDescription() {
    return this.description;
  }


  /**
   * @param description the description to set
   */
  @Override
  public void setDescription(final String description) {
    this.description = description;
  }

  /**
   * @return descEng
   */
  public String getDescEng() {
    return this.descEng;
  }

  /**
   * @param descEng set descEng
   */
  public void setDescEng(final String descEng) {
    this.descEng = descEng;
  }

  /**
   * @return descGer
   */
  public String getDescGer() {
    return this.descGer;
  }

  /**
   * @param descGer set descGer
   */
  public void setDescGer(final String descGer) {
    this.descGer = descGer;
  }

  /**
   * @return createdDate
   */
  @Override
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * @param createdDate set createdDate
   */
  @Override
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * @return createdUser
   */
  @Override
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * @param createdUser set createdUser
   */
  @Override
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  /**
   * @return modifiedDate
   */
  @Override
  public String getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * @param modifiedDate set modifiedDate
   */
  @Override
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  /**
   * @return modifiedUser
   */
  @Override
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * @param modifiedUser set modifiedUser
   */
  @Override
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
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
   * @return focusMatrixYn
   */
  public String getFocusMatrixYn() {
    return this.focusMatrixYn;
  }

  /**
   * @param focusMatrixYn set focusMatrixYn
   */
  public void setFocusMatrixYn(final String focusMatrixYn) {
    this.focusMatrixYn = focusMatrixYn;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final Characteristic object) {
    return ModelUtil.compare(getId(), object.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {

    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((Characteristic) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }


  /**
   * @return the name
   */
  @Override
  public String getName() {
    return this.name;
  }


  /**
   * @param name the name to set
   */
  @Override
  public void setName(final String name) {
    this.name = name;
  }

}
