/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.pidc.accessright.adapter;

import com.bosch.caltool.apic.ws.PidcAccessRightResponseType;
import com.bosch.caltool.apic.ws.db.IWebServiceAdapter;
import com.bosch.caltool.icdm.model.user.LdapUserInfo;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.User;


/**
 * An adapter for a NodeAccessRight object and a UserInfo object. Based on these two objects, a
 * PidcAccessRightResponseType object which is used by the web-service as response object is created. <b>Usage notes</b>
 * :
 * <ul>
 * <li>Class is used in {@link com.bosch.caltool.apic.ws.pidc.accessright.AbstractAccessRight#createWsResponse
 * AbstractAccessRight.createWsResponse }</li>
 * <li>Create an instance of this class</li>
 * <li>Pass a logger, a NodeAccessRight object and a UserInfo object.</li>
 * <li>Call the adapt. This will fill the attributes of the web service response object (the attributes of class
 * PidcAccessRightResponseType)</li>
 * <li>Use this object as response object. Because it is a sub-class of PidcAccessRightResponseType, it can be added to
 * the web service response.</li></li>
 * </ul>
 *
 * @author imi2si
 * @since 1.18.0
 */
@SuppressWarnings("serial")
public class AccessRightAdapter extends PidcAccessRightResponseType implements IWebServiceAdapter {

  private final transient NodeAccess nodeAccess;
  private final transient LdapUserInfo userInfo;
  private final transient long pidcId;
  private final transient User user;


  /**
   * Standard constructor for the adapter.
   *
   * @param nodeAccess the NodeAccessRight objects from which the access rights are extracted.
   * @param user User
   * @param userInfo the UserInfo object containg name and department for a windows user.
   * @param pidcId the PIDC-ID for which this object is created.
   */
  public AccessRightAdapter(final NodeAccess nodeAccess, final User user, final LdapUserInfo userInfo,
      final long pidcId) {
    this.nodeAccess = nodeAccess;
    this.user = user;
    this.userInfo = userInfo;
    this.pidcId = pidcId;
    adapt();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void adapt() {
    super.setUserName(this.user.getName());
    this.setDepartment();
    super.setPidcId(this.pidcId);
    super.setWrite(this.nodeAccess.isWrite());
    super.setOwner(this.nodeAccess.isOwner());
    super.setGrant(this.nodeAccess.isGrant());
  }

  /**
   * Sets the department of the user. The userInfo object might be null, if there's no LDAP information for a user. If
   * there are no information for a user, null is entered as User-Info-
   */
  private void setDepartment() {
    if (this.userInfo != null) {
      super.setDepartment(this.userInfo.getDepartment());
    }
  }
}
