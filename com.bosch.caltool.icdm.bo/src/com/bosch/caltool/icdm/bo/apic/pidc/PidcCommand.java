/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.AliasDefLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.DateUtil;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectidcard;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;

/**
 * @author dja7cob
 */
public class PidcCommand extends AbstractCommand<Pidc, PidcLoader> {

  /**
   * @param serviceData ServiceData
   * @param inputData PIDCVersion
   * @param update boolean
   * @throws IcdmException Exception
   */
  public PidcCommand(final ServiceData serviceData, final Pidc input, final boolean isUpdate, final boolean isDelete)
      throws IcdmException {
    super(serviceData, input, new PidcLoader(serviceData), resolveCommandModeA(isDelete, isUpdate));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TabvProjectidcard entity = new TabvProjectidcard();
    setValuesToEntity(entity);

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * @param entity
   */
  private void setValuesToEntity(final TabvProjectidcard entity) {
    Pidc newPidc = getInputData();
    entity.setTabvAttrValue((new AttributeValueLoader(getServiceData())).getEntityObject(newPidc.getNameValueId()));
    if (null != newPidc.getAliasDefId()) {
      entity.setTaliasDefinition((new AliasDefLoader(getServiceData())).getEntityObject(newPidc.getAliasDefId()));
    }
    // Default Value for InclRvwOfOldVers is Y
    entity.setInclRvwOfOldVers(ApicConstants.CODE_YES);
    entity.setProRevId(Long.valueOf(1));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {

    PidcLoader pidcLoader = new PidcLoader(getServiceData());
    TabvProjectidcard dbPidcToUpd = pidcLoader.getEntityObject(getInputData().getId());
    if (isObjectChanged(getInputData().getProRevId(), getOldData().getProRevId())) {
      dbPidcToUpd.setProRevId(getInputData().getProRevId());
    }
    if (isObjectChanged(getInputData().getVcdmTransferDate(), getOldData().getVcdmTransferDate())) {
      dbPidcToUpd.setAprjId(getInputData().getAprjId());
      dbPidcToUpd.setVcdmTransferDate(DateUtil.getCurrentUtcTime());
      dbPidcToUpd.setVcdmTransferUser(getInputData().getVcdmTransferUser());
    }
    dbPidcToUpd.setInclRvwOfOldVers(booleanToYorN(getInputData().isInclRvwOfOldVers()));
    setUserDetails(COMMAND_MODE.UPDATE, dbPidcToUpd);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    // NA

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // NA

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // NA

  }

}
