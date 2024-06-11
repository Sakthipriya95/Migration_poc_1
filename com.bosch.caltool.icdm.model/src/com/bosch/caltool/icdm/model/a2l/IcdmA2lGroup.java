package com.bosch.caltool.icdm.model.a2l;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * A2L GROUP Model class
 *
 * @author gge6cob
 */
public class IcdmA2lGroup implements Comparable<IcdmA2lGroup>, IModel {

  /**
   *
   */
  private static final long serialVersionUID = 4974339522764169747L;

  /**
   * enum to declare the sort columns
   */
  public enum SortColumns {
                           /**
                            * Group Name column
                            */
                           SORT_GROUP_NAME,
                           /**
                            * Work Package Name
                            */
                           SORT_GROUP_LONG,
                           /**
                            * Work Package Number
                            */
                           SORT_NUM_REF

  }

  /**
   * Group Id
   */
  private Long id;
  /**
   * Grp Name
   */
  private String grpName;
  /**
   * Grp Long Name
   */
  private String grpLongName;
  /**
   * Wp Root Id
   */
  private Long wpRootId;
  /**
   * A2l Id
   */
  private Long a2lId;
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
   * @return grpName
   */
  public String getGrpName() {
    return this.grpName;
  }

  /**
   * @param grpName set grpName
   */
  public void setGrpName(final String grpName) {
    this.grpName = grpName;
  }

  /**
   * @return grpLongName
   */
  public String getGrpLongName() {
    return this.grpLongName;
  }

  /**
   * @param grpLongName set grpLongName
   */
  public void setGrpLongName(final String grpLongName) {
    this.grpLongName = grpLongName;
  }

  /**
   * @return wpRootId
   */
  public Long getWpRootId() {
    return this.wpRootId;
  }

  /**
   * @param wpRootId set wpRootId
   */
  public void setWpRootId(final Long wpRootId) {
    this.wpRootId = wpRootId;
  }

  /**
   * @return a2lId
   */
  public Long getA2lId() {
    return this.a2lId;
  }

  /**
   * @param a2lId set a2lId
   */
  public void setA2lId(final Long a2lId) {
    this.a2lId = a2lId;
  }

  /**
   * @return createdDate
   */
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * @param createdDate set createdDate
   */
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * @return createdUser
   */
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * @param createdUser set createdUser
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  /**
   * @return modifiedDate
   */
  public String getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * @param modifiedDate set modifiedDate
   */
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  /**
   * @return modifiedUser
   */
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * @param modifiedUser set modifiedUser
   */
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
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    IcdmA2lGroup a2lGroup = (IcdmA2lGroup) obj;
    if ((getGrpName() == null) && (a2lGroup.getGrpName() != null)) {
      return false;
    }
    return ((getGrpName() == null) || (a2lGroup.getGrpName() != null)) ? true
        : getGrpName().equals(a2lGroup.getGrpName());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

  /**
   * Compare to.
   *
   * @param a2lgroup the a 2 lgroup
   * @param sortColumn the sort column
   * @return the int
   */
  public int compareTo(final IcdmA2lGroup a2lgroup, final SortColumns sortColumn) {
    int compareResult;

    switch (sortColumn) {
      case SORT_GROUP_NAME:
        compareResult = ModelUtil.compare(getGrpName(), a2lgroup.getGrpName());
        break;
      case SORT_GROUP_LONG:
        compareResult = ModelUtil.compare(getGrpLongName(), a2lgroup.getGrpLongName());
        break;
      default:
        compareResult = 0;
        break;
    }
    // additional compare column is the name of the system constant
    if (compareResult == 0) {
      // compare result is equal, compare the attribute name
      compareResult = ModelUtil.compare(getGrpName(), a2lgroup.getGrpName());
    }

    return compareResult;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final IcdmA2lGroup o) {
    return ModelUtil.compare(getGrpName(), o.getGrpName());
  }
}
