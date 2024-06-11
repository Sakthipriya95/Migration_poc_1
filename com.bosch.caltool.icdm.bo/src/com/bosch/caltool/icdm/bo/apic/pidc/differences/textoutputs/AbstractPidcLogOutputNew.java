/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.differences.textoutputs;

import java.util.HashMap;
import java.util.Map;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcSubVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.attribute.adapter.AttributeAdapterRest;
import com.bosch.caltool.icdm.bo.apic.pidc.attribute.adapter.AttributeValueAdapterRest;
import com.bosch.caltool.icdm.bo.apic.pidc.difference.IWebServiceResponse;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.pidc.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDiffsResponseType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.user.User;


/**
 * @author imi2si
 */
public abstract class AbstractPidcLogOutputNew implements IWebServiceResponse {

  protected final PidcDiffsResponseType diff;
  protected final ILoggerAdapter logger;
  protected final String language;
  protected final ServiceData serviceData;
  private final Map<String, User> userMap = new HashMap<>();

  /**
   * @param logger
   * @param attrDataProvider
   * @param diff
   * @param language
   */
  public AbstractPidcLogOutputNew(final ILoggerAdapter logger, final ServiceData serviceData,
      final PidcDiffsResponseType diff, final String language) {
    this.diff = diff;
    this.logger = logger;
    this.language = language;
    this.serviceData = serviceData;
  }

  /**
   * {@inheritDoc}
   *
   * @throws IcdmException
   */
  @Override
  public abstract void createWsResponse() throws IcdmException;


  /**
   * @param attributeID
   * @return
   * @throws IcdmException
   * @throws DataException
   */
  public Attribute getAttribute(final Long attributeID) throws IcdmException {
    AttributeLoader attrLoader = new AttributeLoader(this.serviceData);
    return new AttributeAdapterRest(this.logger, attrLoader.getDataObjectByID(attributeID));
  }

  public boolean isAttributeExisting(final Long attributeID) {
    try {

      return getAttribute(attributeID).getNameEng() != null;
    }
    catch (Exception e) {
      this.logger.error(e.getMessage(), e);
      return false;
    }
  }

  /**
   * @param valueID
   * @return
   * @throws IcdmException
   * @throws DataException
   * @see com.bosch.caltool.apic.jpa.bo.ApicDataProvider#getAttrValue(java.lang.Long)
   */
  public AttributeValue getAttrValue(final Long valueID) throws IcdmException {
    AttributeValueLoader attrValueLoader = new AttributeValueLoader(this.serviceData);
    // Value IDs 0 and -1 are dummys and can't be queried from the DB
    this.logger.debug("Getting attribute record for attribute ID " + valueID);
    return ((null != valueID) && (valueID > 0))
        ? new AttributeValueAdapterRest(this.logger, attrValueLoader.getDataObjectByID(valueID)) : null;
  }

  /**
   * @param pidcVariantID
   * @return
   * @throws DataException
   * @see com.bosch.caltool.apic.jpa.bo.ApicDataProvider#getPidcVaraint(java.lang.Long)
   */
  public PidcVariant getPidcVaraint(final Long pidcVariantID) throws DataException {
    PidcVariantLoader pidcVariantLoader = new PidcVariantLoader(this.serviceData);
    return pidcVariantLoader.getDataObjectByID(pidcVariantID);
  }

  public boolean isVarExisting(final Long pidcVariantID) {
    try {
      return getPidcVaraint(pidcVariantID).getName() != null;
    }
    catch (Exception exp) {
      this.logger.error(exp.getMessage(), exp);
      return false;
    }
  }

  /**
   * @param pidcSubVariantID
   * @return
   * @throws DataException
   * @see com.bosch.caltool.apic.jpa.bo.ApicDataProvider#getPidcSubVaraint(java.lang.Long)
   */
  public PidcSubVariant getPidcSubVaraint(final Long pidcSubVariantID) throws DataException {
    PidcSubVariantLoader subVarLoader = new PidcSubVariantLoader(this.serviceData);
    return subVarLoader.getDataObjectByID(pidcSubVariantID);
  }

  public boolean isSubVarExisting(final Long pidcSubVariantID) {
    try {
      return getPidcSubVaraint(pidcSubVariantID).getName() != null;
    }
    catch (Exception e) {
      this.logger.error(e.getMessage(), e);
      return false;
    }
  }

  protected String getUserName(final String userName) throws DataException {
    if (!this.userMap.containsKey(userName)) {
      try {
        this.userMap.put(userName, new UserLoader(this.serviceData).getDataObjectByUserName(userName));
      }
      catch (DataException e) {
        this.logger.error(e.getMessage(), e);
        this.userMap.put(userName, null);
      }
    }

    User user = this.userMap.get(userName);
    return null != user ? user.getDescription() : userName;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int prime = 31;
    int result = 1;
    result = (prime * result) + ((this.diff == null) ? 0 : this.diff.hashCode());
    return result;
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
    AbstractPidcLogOutputNew other = (AbstractPidcLogOutputNew) obj;
    if (this.diff == null) {
      if (other.diff != null) {
        return false;
      }
    }
    else if (!this.diff.equals(other.diff)) {
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "AbstractOutput";
  }
}
