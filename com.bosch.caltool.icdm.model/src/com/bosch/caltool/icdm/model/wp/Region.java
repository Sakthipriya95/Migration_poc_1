package com.bosch.caltool.icdm.model.wp;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * Region Model class
 *
 * @author apj4cob
 */
public class Region implements Comparable<Region>, IModel {

  /**
   * Serial UID
   */
  private final static long serialVersionUID = 56786335340498L;
  /**
   * Region Id
   */
  private Long id;
  /**
   * Region Code
   */
  private String regionCode;
  /**
   * Region Name
   */
  private String regionName;
  /**
   * Region Name Eng
   */
  private String regionNameEng;
  /**
   * Region Name Ger
   */
  private String regionNameGer;
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
   * @return regionCode
   */
  public String getRegionCode() {
    return this.regionCode;
  }

  /**
   * @param regionCode set regionCode
   */
  public void setRegionCode(final String regionCode) {
    this.regionCode = regionCode;
  }


  /**
   * @return String
   */
  public String getRegionName() {
    return this.regionName;
  }


  /**
   * @param regionName String
   */
  public void setRegionName(final String regionName) {
    this.regionName = regionName;
  }

  /**
   * @return regionNameEng
   */
  public String getRegionNameEng() {
    return this.regionNameEng;
  }

  /**
   * @param regionNameEng set regionNameEng
   */
  public void setRegionNameEng(final String regionNameEng) {
    this.regionNameEng = regionNameEng;
  }

  /**
   * @return regionNameGer
   */
  public String getRegionNameGer() {
    return this.regionNameGer;
  }

  /**
   * @param regionNameGer set regionNameGer
   */
  public void setRegionNameGer(final String regionNameGer) {
    this.regionNameGer = regionNameGer;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    this.version = version;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final Region object) {
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

    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((Region) obj).getId());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return this.version;
  }

  /**
   * @return the createdDate
   */
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * @return the createdUser
   */
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * @param createdUser the createdUser to set
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  /**
   * @return the modifiedDate
   */
  public String getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * @param modifiedDate the modifiedDate to set
   */
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  /**
   * @return the modifiedUser
   */
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * @param modifiedUser the modifiedUser to set
   */
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

}
