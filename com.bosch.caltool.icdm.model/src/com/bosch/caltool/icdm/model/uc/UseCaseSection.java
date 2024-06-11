package com.bosch.caltool.icdm.model.uc;

import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * Usecase Section Model class
 *
 * @author MKL2COB
 */
public class UseCaseSection implements Cloneable, Comparable<UseCaseSection>, IUseCaseItem {

  /**
   *
   */
  private static final long serialVersionUID = 4811236151916054277L;
  /**
   * Section Id
   */
  private Long id;
  /**
   * name
   */
  private String name;
  /**
   * description
   */
  private String description;
  /**
   * Use Case Id
   */
  private Long useCaseId;
  /**
   * Name Eng
   */
  private String nameEng;
  /**
   * Name Ger
   */
  private String nameGer;
  /**
   * Desc Eng
   */
  private String descEng;
  /**
   * Desc Ger
   */
  private String descGer;
  /**
   * Parent Section Id
   */
  private Long parentSectionId;
  /**
   * Created User
   */
  private String createdUser;
  /**
   * Created Date
   */
  private String createdDate;
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
   * Deleted Flag
   */
  private boolean deleted;
  /**
   * Focus Matrix Yn
   */
  private boolean focusMatrixYn;

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
   * @return useCaseId
   */
  public Long getUseCaseId() {
    return this.useCaseId;
  }

  /**
   * @param useCaseId set useCaseId
   */
  public void setUseCaseId(final Long useCaseId) {
    this.useCaseId = useCaseId;
  }

  /**
   * @return nameEng
   */
  public String getNameEng() {
    return this.nameEng;
  }

  /**
   * @param nameEng set nameEng
   */
  public void setNameEng(final String nameEng) {
    this.nameEng = nameEng;
  }

  /**
   * @return nameGer
   */
  public String getNameGer() {
    return this.nameGer;
  }

  /**
   * @param nameGer set nameGer
   */
  public void setNameGer(final String nameGer) {
    this.nameGer = nameGer;
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
   * @return parentSectionId
   */
  public Long getParentSectionId() {
    return this.parentSectionId;
  }

  /**
   * @param parentSectionId set parentSectionId
   */
  public void setParentSectionId(final Long parentSectionId) {
    this.parentSectionId = parentSectionId;
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
  public boolean getFocusMatrixYn() {
    return this.focusMatrixYn;
  }

  /**
   * @param focusMatrixYn set focusMatrixYn
   */
  public void setFocusMatrixYn(final boolean focusMatrixYn) {
    this.focusMatrixYn = focusMatrixYn;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isDeleted() {
    return this.deleted;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDeleted(final boolean deleted) {
    this.deleted = deleted;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UseCaseSection clone() {
    try {
      return (UseCaseSection) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      throw new CloneNotSupportedRuntimeException(excep);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final UseCaseSection other) {
    int compResult = ModelUtil.compare(getName(), other.getName());
    if (compResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      return ModelUtil.compare(getId(), other.getId());
    }
    return compResult;
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
    if (obj.getClass() == this.getClass()) {
      return ModelUtil.isEqual(getId(), ((UseCaseSection) obj).getId());
    }
    return false;
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

}
