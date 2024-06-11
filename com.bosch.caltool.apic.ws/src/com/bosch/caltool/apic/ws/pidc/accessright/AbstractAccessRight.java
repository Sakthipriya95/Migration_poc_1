/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.pidc.accessright;

import java.util.Locale;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.apic.ws.PidcAccessRight;
import com.bosch.caltool.apic.ws.PidcAccessRightResponse;
import com.bosch.caltool.apic.ws.PidcAccessRightType;
import com.bosch.caltool.apic.ws.PidcAccessRights;
import com.bosch.caltool.apic.ws.db.IWebServiceResponse;
import com.bosch.caltool.apic.ws.pidc.accessright.adapter.AccessRightAdapter;
import com.bosch.caltool.icdm.model.user.LdapUserInfo;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.NodeAccessDetailsExt;
import com.bosch.caltool.icdm.model.user.User;

/**
 * Abstract class for generating a web service reponse for PIDC access rights. The web service request, which contains
 * the requested PIDC-IDs and the constraints (Show owner/grant/write) and a logger object are passed. <b>Usage
 * notes</b>:
 * <ul>
 * <li>Use this class in the web-service service class</li>
 * <li>Create an instance of this class like AbstractAccessRight object = new <DetailObject,
 * e.G.>IcdmPidcAccessRight(ILoggerAdapter,PidcAccessRight).</li>
 * <li>Pass a logger and a the webservice request as parameter to the constructor</li>
 * <li>Call the method createWsResponse(). This will fetch the access rights depending on the constraints in the web
 * service request object</li>
 * <li>Call the method getWsResponse(). This returns the object that be be returned to the web service client.</li></li>
 * </ul>
 *
 * @author imi2si
 * @since 1.18.0
 */
public abstract class AbstractAccessRight implements IWebServiceResponse {

  /**
   * The webservice request, that contains the Pidcs and constraints of the client for the server
   */
  protected final PidcAccessRightType wsRequestType;
  /**
   * The logger of the class
   */
  protected final ILoggerAdapter logger;
  /**
   * The webservice response operation
   */
  private final PidcAccessRightResponse wsResponse = new PidcAccessRightResponse();
  /**
   * The webservice response type, that conatins an array of the access rights
   */
  private final PidcAccessRights wsResponseType = new PidcAccessRights();

  /**
   * Constructor for this class which excepted a logger and the web-service request object
   *
   * @param logger the logger object for the class
   * @param pidcAccessRight the web-service client request object
   */
  public AbstractAccessRight(final ILoggerAdapter logger, final PidcAccessRight pidcAccessRight) {
    this.logger = logger;
    this.wsRequestType = pidcAccessRight.getPidcAccessRight();
    this.wsResponse.setPidcAccessRightResponse(this.wsResponseType);
  }

  /**
   * Fetches and returns the access rights for the passed PIDC-IDs
   *
   * @param pidcs the PIDCS for which the access rights should be returned
   * @return a Map, conataining the NodeAccessRight as Key and UserInfo ans value
   */
  protected abstract NodeAccessDetailsExt getAccesRights(long[] pidcs);

  /**
   * {@inheritDoc}
   */
  @Override
  public void createWsResponse() {
    final NodeAccessDetailsExt accessRights = getAccesRights(getRequestetPidcIds());

    accessRights.getNodeAccessMap().values()
        .forEach(accSet -> accSet.forEach(acc -> addAccessRights(acc, accessRights.getUserMap().get(acc.getUserId()),
            accessRights.getUserInfoMap().get(acc.getUserId()), acc.getNodeId())));

  }


  private void addAccessRights(final NodeAccess nodeAccess, final User user, final LdapUserInfo userInfo,
      final long pidcId) {
    this.wsResponseType.addAccessRights(new AccessRightAdapter(nodeAccess, user, userInfo, pidcId));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getWsResponse() {
    return this.wsResponse;
  }


  /**
   * Returns either the PIDC-IDs the user requested, or in case the request has no PIDC-IDs, all existing PIDC-IDs in
   * the system are retruned.
   */
  private long[] getRequestetPidcIds() {
    return this.wsRequestType.isPidcIdSpecified() ? this.wsRequestType.getPidcId() : new long[0];
  }


  /**
   * @return user name
   */
  protected String getUserName() {
    // toUpperCase added, because the comparison with the user Info's user name is already available in uppercase
    return this.wsRequestType.getUserName() == null ? null
        : this.wsRequestType.getUserName().toUpperCase(Locale.getDefault());
  }

}
