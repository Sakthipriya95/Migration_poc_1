package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * WpArchival Model class
 *
 * @author msp5cob
 */
public class WpArchival implements Comparable<WpArchival>, IModel {

  /**
   * Generated Serial Version UID
   */
  private static final long serialVersionUID = 2128203793256361867L;
  /**
   * WP Archival Id
   */
  private Long id;
  /**
   * Baseline Name
   */
  private String baselineName;
  /**
   * Pidc Vers Id
   */
  private Long pidcVersId;
  /**
   * Pidc Vers Fullname
   */
  private String pidcVersFullname;
  /**
   * Pidc A2l Id
   */
  private Long pidcA2lId;
  /**
   * A2l Filename
   */
  private String a2lFilename;
  /**
   * Variant Id
   */
  private Long variantId;
  /**
   * Variant Name
   */
  private String variantName;
  /**
   * Responsibility Id
   */
  private Long respId;
  /**
   * Responsibility Name
   */
  private String respName;
  /**
   * Wp Id
   */
  private Long wpId;
  /**
   * Wp Name
   */
  private String wpName;
  /**
   * Wp Defn Vers Name
   */
  private Long wpDefnVersId;
  /**
   * Wp Defn Vers Name
   */
  private String wpDefnVersName;

  /**
   * Creation Date
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
   * File Archival Status
   */
  private String fileArchivalStatus;


  /**
   * @return the baselineName
   */
  public String getBaselineName() {
    return this.baselineName;
  }


  /**
   * @param baselineName the baselineName to set
   */
  public void setBaselineName(final String baselineName) {
    this.baselineName = baselineName;
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
   * @return the pidcVersFullname
   */
  public String getPidcVersFullname() {
    return this.pidcVersFullname;
  }


  /**
   * @param pidcVersFullname the pidcVersFullname to set
   */
  public void setPidcVersFullname(final String pidcVersFullname) {
    this.pidcVersFullname = pidcVersFullname;
  }


  /**
   * @return the pidcA2lId
   */
  public Long getPidcA2lId() {
    return this.pidcA2lId;
  }


  /**
   * @param pidcA2lId the pidcA2lId to set
   */
  public void setPidcA2lId(final Long pidcA2lId) {
    this.pidcA2lId = pidcA2lId;
  }


  /**
   * @return the a2lFilename
   */
  public String getA2lFilename() {
    return this.a2lFilename;
  }


  /**
   * @param a2lFilename the a2lFilename to set
   */
  public void setA2lFilename(final String a2lFilename) {
    this.a2lFilename = a2lFilename;
  }


  /**
   * @return the variantId
   */
  public Long getVariantId() {
    return this.variantId;
  }


  /**
   * @param variantId the variantId to set
   */
  public void setVariantId(final Long variantId) {
    this.variantId = variantId;
  }


  /**
   * @return the variantName
   */
  public String getVariantName() {
    return this.variantName;
  }


  /**
   * @param variantName the variantName to set
   */
  public void setVariantName(final String variantName) {
    this.variantName = variantName;
  }


  /**
   * @return the respId
   */
  public Long getRespId() {
    return this.respId;
  }


  /**
   * @param respId the respId to set
   */
  public void setRespId(final Long respId) {
    this.respId = respId;
  }


  /**
   * @return the respName
   */
  public String getRespName() {
    return this.respName;
  }


  /**
   * @param respName the respName to set
   */
  public void setRespName(final String respName) {
    this.respName = respName;
  }


  /**
   * @return the wpId
   */
  public Long getWpId() {
    return this.wpId;
  }


  /**
   * @param wpId the wpId to set
   */
  public void setWpId(final Long wpId) {
    this.wpId = wpId;
  }


  /**
   * @return the wpName
   */
  public String getWpName() {
    return this.wpName;
  }


  /**
   * @param wpName the wpName to set
   */
  public void setWpName(final String wpName) {
    this.wpName = wpName;
  }


  /**
   * @return the wpDefnVersId
   */
  public Long getWpDefnVersId() {
    return this.wpDefnVersId;
  }


  /**
   * @param wpDefnVersId the wpDefnVersId to set
   */
  public void setWpDefnVersId(final Long wpDefnVersId) {
    this.wpDefnVersId = wpDefnVersId;
  }


  /**
   * @return the wpDefnVersName
   */
  public String getWpDefnVersName() {
    return this.wpDefnVersName;
  }


  /**
   * @param wpDefnVersName the wpDefnVersName to set
   */
  public void setWpDefnVersName(final String wpDefnVersName) {
    this.wpDefnVersName = wpDefnVersName;
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
   * @return the version
   */
  @Override
  public Long getVersion() {
    return this.version;
  }


  /**
   * @param version the version to set
   */
  @Override
  public void setVersion(final Long version) {
    this.version = version;
  }

  /**
   * @return the fileArchivalStatus
   */
  public String getFileArchivalStatus() {
    return this.fileArchivalStatus;
  }


  /**
   * @param fileArchivalStatus the fileArchivalStatus to set
   */
  public void setFileArchivalStatus(final String fileArchivalStatus) {
    this.fileArchivalStatus = fileArchivalStatus;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final WpArchival object) {
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((WpArchival) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }


}
