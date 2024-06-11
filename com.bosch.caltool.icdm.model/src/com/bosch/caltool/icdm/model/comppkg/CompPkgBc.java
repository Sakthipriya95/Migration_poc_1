package com.bosch.caltool.icdm.model.comppkg;

import java.util.Set;
import java.util.TreeSet;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * TCompPkgBc Model class
 *
 * @author say8cob
 */
public class CompPkgBc implements Comparable<CompPkgBc>, IDataObject {

  /**
   *
   */
  private static final long serialVersionUID = 8304982174101249645L;
  /**
   * Comp Bc Id
   */
  private Long id;
  /**
   * Comp Pkg Id
   */
  private Long compPkgId;
  /**
   * Bc Name
   */
  private String bcName;
  /**
   * Bc Seq No
   */
  private Long bcSeqNo;
  /**
   * Created User
   */
  private String createdUser;
  /**
   * Created Date
   */
  private String createdDate;
  /**
   * Modified User
   */
  private String modifiedUser;
  /**
   * Modified Date
   */
  private String modifiedDate;
  /**
   * Version
   */
  private Long version;

  private Set<CompPkgFc> fcList;

  private String description;

  private CompPackage compPkgObj;


  /**
   * @return the compPkgObj
   */
  public CompPackage getCompPkgObj() {
    return this.compPkgObj;
  }


  /**
   * @param compPkgObj the compPkgObj to set
   */
  public void setCompPkgObj(final CompPackage compPkgObj) {
    this.compPkgObj = compPkgObj;
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
   * @return compPkgId
   */
  public Long getCompPkgId() {
    return this.compPkgId;
  }

  /**
   * @param compPkgId set compPkgId
   */
  public void setCompPkgId(final Long compPkgId) {
    this.compPkgId = compPkgId;
  }

  /**
   * @return bcName
   */
  public String getBcName() {
    return this.bcName;
  }

  /**
   * @param bcName set bcName
   */
  public void setBcName(final String bcName) {
    this.bcName = bcName;
  }

  /**
   * @return bcSeqNo
   */
  public Long getBcSeqNo() {
    return this.bcSeqNo;
  }

  /**
   * @param bcSeqNo set bcSeqNo
   */
  public void setBcSeqNo(final Long bcSeqNo) {
    this.bcSeqNo = bcSeqNo;
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
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CompPkgBc object) {
    return ModelUtil.compare(getBcSeqNo(), object.getBcSeqNo());
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(this.id, ((CompPkgBc) obj).id);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }


  /**
   * @return the fcList
   */
  public Set<CompPkgFc> getFcList() {
    return this.fcList;
  }


  /**
   * @param fcList the fcList to set
   */
  public void setFcList(final Set<CompPkgFc> fcList) {
    this.fcList = fcList == null ? null : new TreeSet<>();
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
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return this.bcName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(final String name) {
    this.bcName = name;

  }
}
