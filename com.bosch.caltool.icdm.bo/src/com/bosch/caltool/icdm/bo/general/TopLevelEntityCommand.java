/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.general;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TabvTopLevelEntity;


/**
 * @author bne4cob
 */
public class TopLevelEntityCommand extends AbstractCommand<TopLevelEntityInternal, TopLevelEntityLoader> {

  /**
   * Constructor to update a top level entity. This also create an 'APIC_READ' system access to the user
   *
   * @param serviceData Service Data
   * @param entityId top level entity's ID
   * @throws IcdmException any error
   */
  public TopLevelEntityCommand(final ServiceData serviceData, final Long entityId) throws IcdmException {
    super(serviceData, (new TopLevelEntityLoader(serviceData)).getDataObjectByID(entityId),
        new TopLevelEntityLoader(serviceData), COMMAND_MODE.UPDATE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    // Not Required
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {

    final TabvTopLevelEntity modifiedtopLevel =
        new TopLevelEntityLoader(getServiceData()).getEntityObject(getInputData().getId());

    // Just update the last modified date
    modifiedtopLevel.setLastModDate(getCurrentTime());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    // Not Required
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
    // Always true
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    // No actions
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // No actions, only for internal purposes
  }

  /**
   * {@inheritDoc}
   * <p>
   * TOP level entity is NOT relevant for CNS events.
   */
  @Override
  protected boolean isRelevantForCns() {
    return false;
  }

}
