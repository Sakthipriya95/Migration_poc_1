/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.views.providers;

import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.icdm.client.bo.general.NodeAccessPageDataHandler;
import com.bosch.caltool.icdm.common.ui.propertysource.AbstractDataObjectPropertySource;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.model.user.NodeAccess;


/**
 * @author rvu1cob
 */
public class AccessRightsPagePropertiesViewSource extends AbstractDataObjectPropertySource<NodeAccess> {


  /**
   * Property for user name
   */
  private static final String USER_NAME = "User Name";

  /**
   * Property for user id
   */
  private static final String USER_ID = "User id";

  /**
   * Property for user department
   */
  private static final String DEPARTMENT = "Department";

  /**
   * Property for write
   */
  private static final String WRITE = "Write";

  /**
   * Property for grant
   */
  private static final String GRANT = "Grant";

  /**
   * Property for owner
   */
  private static final String OWNER = "Owner";

  /**
   * Selected node access
   */
  private final NodeAccess nodeAccess;

  /**
   * instance of NodeAccessPageDataHandler
   */
  private final NodeAccessPageDataHandler nodeAccessDataHandler;


  /**
   * @param nodeAccess
   */
  @SuppressWarnings("javadoc")
  public AccessRightsPagePropertiesViewSource(final NodeAccess nodeAccess,
      final NodeAccessPageDataHandler nodeAccessDataHandler) {
    super(nodeAccess);
    this.nodeAccess = nodeAccess;
    this.nodeAccessDataHandler = nodeAccessDataHandler;
  }


  /**
   * @param descField - property description
   * @return property value
   */


  public String getPropertyValue(final String descField) {
    String result = "";
    switch (descField) {
      case USER_NAME:
        if (this.nodeAccess.getUserId() != null) {
          result = this.nodeAccessDataHandler.getUserFullName(this.nodeAccess.getId());
        }
        break;

      case USER_ID:
        if (this.nodeAccess.getUserId() != null) {
          result = this.nodeAccessDataHandler.getUserName(this.nodeAccess.getId());
        }
        break;

      case DEPARTMENT:
        if (this.nodeAccess.getUserId() != null) {
          result = this.nodeAccessDataHandler.getUserDepartment(this.nodeAccess.getId());
        }
        break;

      case WRITE:
        result = this.nodeAccess.isWrite() ? IMessageConstants.YES : IMessageConstants.NO_CONSTANT;
        break;

      case GRANT:
        result = this.nodeAccess.isGrant() ? IMessageConstants.YES : IMessageConstants.NO_CONSTANT;
        break;

      case OWNER:
        result = this.nodeAccess.isOwner() ? IMessageConstants.YES : IMessageConstants.NO_CONSTANT;
        break;

      case PROP_TITLE:
        result = getTitle();
        break;

      default:
        break;
    }

    return result;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected Object getStrPropertyValue(final String propKey) {
    String result = getPropertyValue(propKey);
    return result == null ? "" : result;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected String[] getDescFields() {
    return new String[] { USER_NAME, USER_ID, DEPARTMENT, WRITE, GRANT, OWNER, };

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getTitle() {
    final String titleName = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()
        .getEditorSite().getPart().getTitleToolTip();
    return "USER : " + this.nodeAccessDataHandler.getUserFullName(this.nodeAccess.getId()) + " [ " + titleName + " ] ";

  }

}
