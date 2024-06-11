/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import com.bosch.caltool.icdm.client.bo.general.NodeAccessPageDataHandler;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroupNodeAccess;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroupUser;
import com.bosch.caltool.icdm.model.user.NodeAccess;

/**
 * @author apj4cob
 */
public class NodeAccessRightsFilter extends AbstractViewerFilter {


  private final NodeAccessPageDataHandler dataHandler;

  /**
   * @param dataHandler NodeAccessPageDataHandler
   */
  public NodeAccessRightsFilter(final NodeAccessPageDataHandler dataHandler) {

    this.dataHandler = dataHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    boolean result = false;
    if (element instanceof NodeAccess) {
      // If selected element is a BC
      final NodeAccess nodeAccessElement = (NodeAccess) element;

      // Filter the table
      result = matchText(nodeAccessElement.getName()) || matchText(nodeAccessElement.getDescription()) ||
          matchText(this.dataHandler.getUserDepartment(nodeAccessElement.getId()));
    }
    if (element instanceof ActiveDirectoryGroupNodeAccess) {
      // If selected element is a BC
      final ActiveDirectoryGroupNodeAccess nodeAccessElement = (ActiveDirectoryGroupNodeAccess) element;

      // Filter the table
      result = matchText(nodeAccessElement.getAdGroup().getGroupName());
    }
    if (element instanceof ActiveDirectoryGroupUser) {
      // If selected element is a BC
      final ActiveDirectoryGroupUser nodeAccessElement = (ActiveDirectoryGroupUser) element;

      // Filter the table
      result = matchText(nodeAccessElement.getGroupUserName()) || matchText(nodeAccessElement.getGroupUserDept()) ||
          matchText(nodeAccessElement.getUsername());
    }
    return result;
  }
}
