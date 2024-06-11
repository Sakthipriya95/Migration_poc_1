/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.pidc.accessright;

import java.util.HashSet;
import java.util.Set;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.apic.ws.PidcAccessRight;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.user.NodeAccessLevel;
import com.bosch.caltool.icdm.bo.user.NodeAccessLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.model.user.NodeAccessDetailsExt;


/**
 * Generates a web service reponse for PIDC access rights. The web service request, which contains the requested
 * PIDC-IDs and the constraints (Show owner/grant/write), a logger object and the database connection are passed.
 * <b>Usage notes</b>:
 * <ul>
 * <li>Use this class in the web-service service class</li>
 * <li>Create an instance of this class like AbstractAccessRight object = new
 * IcdmPidcAccessRight(ILoggerAdapter,PidcAccessRight,ApicDataProvider).</li>
 * <li>Pass a logger, the webservice request and the data provider as parameter to the constructor.</li>
 * <li>Call the method createWsResponse(). This will fetch the access rights depending on the constraints in the web
 * service request object</li>
 * <li>Call the method getWsResponse(). This returns the object that be be returned to the web service client.</li></li>
 * </ul>
 *
 * @author imi2si
 * @since 1.18.0
 */
public class IcdmPidcAccessRight extends AbstractAccessRight {


  /**
   * Enum for relevant constraints, there boolean values and int values (which come from the web-service request)
   *
   * @author imi2si
   */
  public enum PidcAccessRightsTyp {

                                   /**
                                    * Access right should be considered for response if value is true.
                                    */
                                   ACCESS_TRUE("User has access", true, 1),
                                   /**
                                    * Access right should be considered for response if value is false.
                                    */
                                   ACCESS_FALSE("User has noaccess", false, 0),
                                   /**
                                    * All access rights should be considered. Note: The boolean represenattion has
                                    * another meaning as for ACCESS_TRUE and ACCESS_FALSE. See implementation details in
                                    * method checkForRelevance().
                                    */
                                   ALL("All access rights", true, 2);

    /**
     * Returns based on a given int code (from the clients request) the right PidcAccessRightsTyp object
     *
     * @param code the constraint code for which the ENUM object should be returned
     * @return the PidcAccessRightsTyp object. One of the types ACCESS_TRUE, ACCESS_FALSE or ALL
     */
    public static PidcAccessRightsTyp getTypeByCode(final int code) {
      for (PidcAccessRightsTyp entry : PidcAccessRightsTyp.values()) {
        if (entry.getCode() == code) {
          return entry; // NOPMD
        }
      }

      // ALL is the default if no values is passed from the client
      return ALL;
    }

    /**
     * Descpription of the type
     */
    private String description;
    /**
     * boolean representation of the type (needed to determine if a value is relevant. Based on the code passed from the
     * client)
     */
    private boolean show;
    /**
     * int code of the type (from the client)
     */
    private int code;

    /**
     * private Constructor for the ENUM.
     *
     * @param description the descpription of the type
     * @param show the boolean represenation of the type
     * @param code the int representation of the type
     */
    private PidcAccessRightsTyp(final String description, final boolean show, final int code) {
      this.description = description;
      this.show = show;
      this.code = code;
    }

    /**
     * Returns the int code of the type.
     *
     * @return the int code of the type
     */
    public int getCode() {
      return this.code;
    }

    /**
     * Returns the bollean mapped to the int code.
     *
     * @return true or false
     */
    public boolean getShow() { // NOPMD
      return this.show;
    }

    /**
     * @return the description
     */
    public String getDescription() {
      return this.description;
    }
  }

  private final ServiceData serviceData;

  /**
   * @param logger the logger object for the class
   * @param pidcAccessRight the web-service client request object
   * @param serviceData Service Data
   */
  public IcdmPidcAccessRight(final ILoggerAdapter logger, final PidcAccessRight pidcAccessRight,
      final ServiceData serviceData) {
    super(logger, pidcAccessRight);
    this.serviceData = serviceData;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected final NodeAccessDetailsExt getAccesRights(final long[] pidcs) {

    NodeAccessLevel reqLevel = getRequiredAccessLevel();

    Set<Long> pidcIdSet = new HashSet<>();
    if (pidcs != null) {
      for (long pidcId : pidcs) {
        pidcIdSet.add(pidcId);
      }
    }

    NodeAccessLoader loader = new NodeAccessLoader(this.serviceData);
    try {
      return loader.getAllNodeAccessByNodeExt("PIDC", getUserName(), reqLevel, pidcIdSet);
    }
    catch (DataException e) {
      this.logger.error(e.getMessage(), e);
    }

    // Return empty object, in case of exceptions
    return new NodeAccessDetailsExt();
  }


  private NodeAccessLevel getRequiredAccessLevel() {
    if (checkFlag(this.wsRequestType.isShowWriteSpecified(), this.wsRequestType.getShowWrite())) {
      return NodeAccessLevel.WRITE;
    }
    if (checkFlag(this.wsRequestType.isShowGrantSpecified(), this.wsRequestType.getShowGrant())) {
      return NodeAccessLevel.GRANT;
    }
    if (checkFlag(this.wsRequestType.isShowOwnerSpecified(), this.wsRequestType.getShowOwner())) {
      return NodeAccessLevel.OWNER;
    }
    return null;

  }

  private boolean checkFlag(final boolean accessSpecified, final int accessCode) {
    return accessSpecified && (PidcAccessRightsTyp.getTypeByCode(accessCode) == PidcAccessRightsTyp.ACCESS_TRUE);
  }

}
