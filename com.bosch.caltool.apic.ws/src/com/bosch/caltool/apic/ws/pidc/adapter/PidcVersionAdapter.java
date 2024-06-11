/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.pidc.adapter;


import com.bosch.caltool.apic.ws.PidcVersionType;
import com.bosch.caltool.apic.ws.ProjectIdCardVersInfoType;
import com.bosch.caltool.apic.ws.db.IWebServiceAdapter;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionStatus;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectidcard;
import com.bosch.caltool.icdm.model.apic.ApicConstants.CLEARING_STATUS;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;


/**
 * @author imi2si
 */
public class PidcVersionAdapter extends ProjectIdCardVersInfoType implements IWebServiceAdapter {


  private final PidcVersion pidcVer;
  private final ServiceData serviceData;

  /**
   * @param pidcVer
   * @param serviceData
   * @throws IcdmException
   */
  public PidcVersionAdapter(final PidcVersion pidcVer, final ServiceData serviceData) throws IcdmException {
    this.pidcVer = pidcVer;
    this.serviceData = serviceData;
    adapt();
  }

  /**
   * {@inheritDoc}
   *
   * @throws DataException
   */
  @Override
  public void adapt() throws IcdmException {
    PidcVersionType pidcVersionType = new PidcVersionType();
    setPidcVersionType(pidcVersionType);
    super.setPidcVersion(pidcVersionType);
    super.setName(new PidcLoader(this.serviceData).getDataObjectByID(this.pidcVer.getPidcId()).getName());

    // get the PIDC id from the Active Version
    super.setId(this.pidcVer.getPidcId());
    super.setChangeNumber(this.pidcVer.getProRevId());
    super.setIsDeleted(this.pidcVer.isDeleted());
    super.setCreateUser(this.pidcVer.getCreatedUser());
    super.setCreateDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, this.pidcVer.getCreatedDate()));
    super.setModifyDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, this.pidcVer.getModifiedDate()));
    super.setModifyUser(this.pidcVer.getModifiedUser());
    TabvProjectidcard pidcEntity = new PidcLoader(new ServiceData()).getEntityObject(this.pidcVer.getPidcId());
    super.setClearingStatus(
        CLEARING_STATUS.getClearingStatus(pidcEntity.getTabvAttrValue().getClearingStatus()).getUiText());
    super.setIsCleared(pidcEntity.getTabvAttrValue().getClearingStatus().equals(CLEARING_STATUS.CLEARED.getDBText()));
    super.setChangeNumber(this.pidcVer.getVersion());
  }

  /**
   * @param pidcVersionType pidcVersionType
   * @throws DataException
   */
  private void setPidcVersionType(final PidcVersionType pidcVersionType) throws DataException {
    pidcVersionType.setDescription(this.pidcVer.getDescription());
    pidcVersionType.setDescriptionE(this.pidcVer.getVersDescEng());
    pidcVersionType.setDescriptionG(this.pidcVer.getVersDescGer());
    pidcVersionType.setIsActive(
        Long.compare(new PidcVersionLoader(this.serviceData).getActivePidcVersion(this.pidcVer.getPidcId()).getId(),
            this.pidcVer.getId()) == 0);
    pidcVersionType.setLongName(this.pidcVer.getVersionName());
    pidcVersionType.setPidcVersionId(this.pidcVer.getId());
    pidcVersionType.setProRevId(this.pidcVer.getProRevId());
    pidcVersionType.setVersionStatus(PidcVersionStatus.getStatus(this.pidcVer.getPidStatus()).getUiStatus());
  }
}
