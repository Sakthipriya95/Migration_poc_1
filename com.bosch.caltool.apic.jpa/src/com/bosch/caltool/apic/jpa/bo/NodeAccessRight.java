package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicNodeAccess;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

public class NodeAccessRight extends ApicObject implements Comparable<NodeAccessRight> {

  /**
   * Columns for advanced sorting (see method compareTo(NodeAccessRight nodeAccessRight2, SortColumns sortColumn))
   *
   * @author he2fe
   */
  public enum SortColumns {
                           /**
                            * Write Access
                            */
                           SORT_WRITE_ACCESS,
                           /**
                            * Grant Option
                            */
                           SORT_GRANT_OPTION,
                           /**
                            * Owner
                            */
                           SORT_OWNER,
                           /**
                            * User name
                            */
                           SORT_USER_NAME,
                           /**
                            * First Name
                            */
                           SORT_FIRST_NAME,
                           /**
                            * Last Name
                            */
                           SORT_LAST_NAME,
                           /**
                            * Full Name
                            */
                           SORT_FULL_NAME,
                           /**
                            * Department
                            */
                           SORT_DEPARTMENT
  }


  /**
   * ICDM-1469 Node ID
   */
  private static final String FLD_NODE_ID = "Node ID";

  private static final String READ_ACCESS = "Read Access";

  private static final String WRITE_ACCESS = "Write Access";

  private static final String GRANT_ACCESS = "Grant Access";

  private static final String OWNER_ACCESS = "Owner Access";


  /**
   * the one and only constructor the unique ID of the node access right in TabV_APIC_AccessRights
   *
   * @param apicDataProvider
   * @param nodeaccessId
   */
  public NodeAccessRight(final ApicDataProvider apicDataProvider, final Long nodeaccessId) {

    super(apicDataProvider, nodeaccessId);

    getDataCache().getAllNodeAccRights().put(nodeaccessId, this);
  }

  /**
   * Check, if the user ID is valid The ID is valid if the related database entity is available
   *
   * @return TRUE if the userID is valid
   */
  private boolean nodeaccessIdValid() {
    return getEntityProvider().getDbNodeAccess(getID()) != null;
  }


  /**
   * Get the name of the user
   *
   * @return The user name
   */
  public String getUserName() {
    if (nodeaccessIdValid()) {
      return getEntityProvider().getDbNodeAccess(getID()).getTabvApicUser().getUsername();
    }
    return null;
  }

  /**
   * Get the ApicUser object
   *
   * @return The ApicUser
   */
  public ApicUser getApicUser() {
    if (nodeaccessIdValid()) {
      return getDataCache().getApicUser(getEntityProvider().getDbNodeAccess(getID()).getTabvApicUser().getUsername());
    }
    return null;
  }

  /**
   * Get the ID of the node access right
   *
   * @return The ID
   */
  public Long getNodeAccessID() {
    return getID();
  }

  /**
   * Get the nodeID of the node access right
   *
   * @return The nodeId
   */
  public Long getNodeId() {
    if (nodeaccessIdValid()) {
      return getEntityProvider().getDbNodeAccess(getID()).getNodeId();
    }
    return null;
  }

  /**
   * Get the type of the node access right
   *
   * @return The type
   */
  public String getNodeType() {
    if (nodeaccessIdValid()) {
      return getEntityProvider().getDbNodeAccess(getID()).getNodeType();
    }
    return null;
  }

  /**
   * @return true if the user is the owner
   */
  public boolean isOwner() {
    if (nodeaccessIdValid()) {
      return ApicConstants.YES.equals(getEntityProvider().getDbNodeAccess(getID()).getOwner());
    }
    return false;
  }

  /**
   * @return true if the user has read access
   */
  public boolean hasReadAccess() {
    if (nodeaccessIdValid()) {
      return ApicConstants.YES.equals(getEntityProvider().getDbNodeAccess(getID()).getReadright());
    }
    return false;
  }

  /**
   * @return true if the user has write access
   */
  public boolean hasWriteAccess() {
    if (nodeaccessIdValid()) {
      return ApicConstants.YES.equals(getEntityProvider().getDbNodeAccess(getID()).getWriteright());
    }
    return false;
  }

  /**
   * @return true if the user has grant access
   */
  public boolean hasGrantOption() {
    if (nodeaccessIdValid()) {
      return ApicConstants.YES.equals(getEntityProvider().getDbNodeAccess(getID()).getGrantright());
    }
    return false;
  }

  /**
   * Get the creation date of the User
   *
   * @return The date when the user has been created in the database
   */
  @Override
  public Calendar getCreatedDate() {
    if (nodeaccessIdValid()) {
      return ApicUtil.timestamp2calendar(getEntityProvider().getDbNodeAccess(getID()).getCreatedDate());
    }
    return null;
  }


  /**
   * Get the ID of the user who has created the User
   *
   * @return The ID of the user who has created the User
   */
  @Override
  public String getCreatedUser() {
    if (nodeaccessIdValid()) {
      return getEntityProvider().getDbNodeAccess(getID()).getCreatedUser();
    }
    return "";
  }

  /**
   * Get the date when the User has been modified the last time
   *
   * @return The date when the User has been modified the last time
   */
  @Override
  public Calendar getModifiedDate() {
    if (nodeaccessIdValid()) {
      return ApicUtil.timestamp2calendar(getEntityProvider().getDbNodeAccess(getID()).getModifiedDate());
    }
    return null;
  }

  /**
   * Get the user who has modified the User the last time
   *
   * @return The user who has modified the User the last time
   */
  @Override
  public String getModifiedUser() {
    if (nodeaccessIdValid()) {
      return getEntityProvider().getDbNodeAccess(getID()).getModifiedUser();
    }
    return "";
  }

  /**
   * {@inheritDoc} refreshes the cache object
   */
  @Override
  public void refresh() {

    TabvApicNodeAccess dbNodeAccess = getEntityProvider().getDbNodeAccess(getID());

    getEntityProvider().refreshCacheObject(dbNodeAccess);

  }

  /**
   * Compares this node access right with another node access right based on column to sort
   *
   * @param nodeAccessRight2 the second node access right
   * @param sortColumn column selected for sorting
   * @return the int value based on String.compare() method
   */
  public int compareTo(final NodeAccessRight nodeAccessRight2, final SortColumns sortColumn) {
    int compareResult;

    switch (sortColumn) {
      case SORT_WRITE_ACCESS:
        // compare the user WriteAccess flag
        compareResult = ApicUtil.compareBoolean(hasWriteAccess(), nodeAccessRight2.hasWriteAccess());
        break;

      case SORT_GRANT_OPTION:
        // compare the user GrantOption flag
        compareResult = ApicUtil.compareBoolean(hasGrantOption(), nodeAccessRight2.hasGrantOption());
        break;

      case SORT_OWNER:
        // compare the user Owner flag
        compareResult = ApicUtil.compareBoolean(isOwner(), nodeAccessRight2.isOwner());
        break;

      case SORT_USER_NAME:
        // compare the user IDs
        compareResult = getApicUser().compareTo(nodeAccessRight2.getApicUser(), ApicUser.SortColumns.SORT_USER_NAME);
        break;

      case SORT_FIRST_NAME:
        // compare the first names
        compareResult = getApicUser().compareTo(nodeAccessRight2.getApicUser(), ApicUser.SortColumns.SORT_FIRST_NAME);
        break;

      case SORT_LAST_NAME:
        // compare the last names
        compareResult = getApicUser().compareTo(nodeAccessRight2.getApicUser(), ApicUser.SortColumns.SORT_LAST_NAME);
        break;

      case SORT_FULL_NAME:
        // compare the last names
        compareResult = getApicUser().compareTo(nodeAccessRight2.getApicUser(), ApicUser.SortColumns.SORT_FULL_NAME);
        break;

      case SORT_DEPARTMENT:
        // compare the departments
        compareResult = getApicUser().compareTo(nodeAccessRight2.getApicUser(), ApicUser.SortColumns.SORT_DEPARTMENT);
        break;


      default:
        compareResult = 0;
        break;
    }

    if (compareResult == 0) {
      // compare result is equal, compare the users full name
      compareResult = getApicUser().compareTo(nodeAccessRight2.getApicUser(), ApicUser.SortColumns.SORT_FULL_NAME);

    }

    return compareResult;
  }

  /**
   * {@inheritDoc} return compare result of two node access rights
   */
  @Override
  public int compareTo(final NodeAccessRight arg0) {

    return ApicUtil.compare(getUserName(), arg0.getUserName());
  }

  /**
   * {@inheritDoc} return the version id
   */
  @Override
  public Long getVersion() {
    if (nodeaccessIdValid()) {
      return getEntityProvider().getDbNodeAccess(getID()).getVersion();
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getUserName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    // Not applicable
    return null;
  }

  /**
   * @return summary of data
   */
  @Override
  public Map<String, String> getObjectDetails() {
    ConcurrentMap<String, String> summaryMap = new ConcurrentHashMap<String, String>();

    // node id
    summaryMap.put(FLD_NODE_ID, getNodeId().toString());
    // read, write, grant & owner access
    summaryMap.put(READ_ACCESS, Boolean.toString(hasReadAccess()));
    summaryMap.put(WRITE_ACCESS, Boolean.toString(hasWriteAccess()));
    summaryMap.put(GRANT_ACCESS, Boolean.toString(hasGrantOption()));
    summaryMap.put(OWNER_ACCESS, Boolean.toString(isOwner()));

    return summaryMap;
  }

}
