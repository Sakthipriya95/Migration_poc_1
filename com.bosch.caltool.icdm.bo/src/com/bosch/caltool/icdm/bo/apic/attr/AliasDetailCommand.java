/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.attr;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.AliasDefLoader;
import com.bosch.caltool.icdm.bo.apic.AliasDetailLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TAliasDetail;
import com.bosch.caltool.icdm.model.apic.AliasDetail;


/**
 * Command class for Alias Detail
 *
 * @author bne4cob
 */
public class AliasDetailCommand extends AbstractCommand<AliasDetail, AliasDetailLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public AliasDetailCommand(final ServiceData serviceData, final AliasDetail input, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, input, new AliasDetailLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TAliasDetail entity = new TAliasDetail();

    new AliasDefLoader(getServiceData()).getEntityObject(getInputData().getAdId()).addTAliasDetail(entity);

    if (getInputData().getAttrId() != null) {
      new AttributeLoader(getServiceData()).getEntityObject(getInputData().getAttrId()).addTAliasDetail(entity);
    }

    if (getInputData().getValueId() != null) {
      new AttributeValueLoader(getServiceData()).getEntityObject(getInputData().getValueId()).addTAliasDetail(entity);
    }

    entity.setAliasName(getInputData().getAliasName());

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    AliasDetailLoader loader = new AliasDetailLoader(getServiceData());
    TAliasDetail entity = loader.getEntityObject(getInputData().getId());

    entity.setAliasName(getInputData().getAliasName());

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    AliasDetailLoader loader = new AliasDetailLoader(getServiceData());
    TAliasDetail entity = loader.getEntityObject(getInputData().getId());

    new AliasDefLoader(getServiceData()).getEntityObject(getInputData().getAdId()).removeTAliasDetail(entity);

    if (getInputData().getAttrId() != null) {
      new AttributeLoader(getServiceData()).getEntityObject(getInputData().getAttrId()).removeTAliasDetail(entity);
    }

    if (getInputData().getValueId() != null) {
      new AttributeValueLoader(getServiceData()).getEntityObject(getInputData().getValueId())
          .removeTAliasDetail(entity);
    }

    getEm().remove(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // No actions
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
    // No actions
  }

}
