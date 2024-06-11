/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.pidc.adapter;

import com.bosch.caltool.apic.ws.ProjectIdCardVariantInfoType;
import com.bosch.caltool.apic.ws.db.IWebServiceAdapter;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;


/**
 * @author imi2si
 */
public class PidVariantAdapter extends ProjectIdCardVariantInfoType implements IWebServiceAdapter {

  private final Long pidcVariantId;
  private PidcVariant pidcVariant = null;
  private final ServiceData serviceData;

  /**
   * @param pidcVariantId
   * @param serviceData
   * @throws IcdmException
   * @throws DataException
   */
  public PidVariantAdapter(final Long pidcVariantId, final ServiceData serviceData) throws IcdmException {
    this.pidcVariantId = pidcVariantId;
    this.pidcVariant = new PidcVariantLoader(serviceData).getDataObjectByID(pidcVariantId);
    this.serviceData = serviceData;
    adapt();
  }

  /**
   * @return
   */
  public boolean isValid() {
    // pidc varaint id and name should not be null.
    return (this.pidcVariantId != null) && (this.pidcVariant.getName() != null);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void adapt() throws IcdmException {
    // if not valid return
    if (!isValid()) {
      return;
    }
    // set id name field value
    super.setId(this.pidcVariant.getId());
    super.setName(this.pidcVariant.getName());
    super.setVersionNumber(new PidcVariantLoader(this.serviceData).getEntityObject(this.pidcVariantId).getTPidcVersion()
        .getProRevId().longValue());
    super.setChangeNumber(this.pidcVariant.getVersion());
    super.setIsDeleted(this.pidcVariant.isDeleted());
    super.setCreateUser(this.pidcVariant.getCreatedUser());
    // Use String to calender to convert date for created amf modified date values
    super.setCreateDate(
        DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, this.pidcVariant.getCreatedDate()));
    super.setModifyDate(
        DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, this.pidcVariant.getModifiedDate()));
    super.setModifyUser(this.pidcVariant.getModifiedUser());
  }
}
