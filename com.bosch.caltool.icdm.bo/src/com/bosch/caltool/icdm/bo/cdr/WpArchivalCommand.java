package com.bosch.caltool.icdm.bo.cdr;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.cdr.TWpArchival;
import com.bosch.caltool.icdm.model.cdr.WpArchival;


/**
 * Command class for WpArchival
 *
 * @author msp5cob
 */
public class WpArchivalCommand extends AbstractCommand<WpArchival, WpArchivalLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param serviceData service Data
   * @param isUpdate Flag to indicate update/create
   * @throws IcdmException error when initializing
   */
  public WpArchivalCommand(final ServiceData serviceData, final WpArchival input, final boolean isUpdate)
      throws IcdmException {
    super(serviceData, input, new WpArchivalLoader(serviceData), resolveCommandModeU(isUpdate));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TWpArchival entity = new TWpArchival();

    entity.setBaselineName(getInputData().getBaselineName());
    entity.setPidcVersId(getInputData().getPidcVersId());
    entity.setPidcVersFullname(getInputData().getPidcVersFullname());
    entity.setPidcA2lId(getInputData().getPidcA2lId());
    entity.setA2lFilename(getInputData().getA2lFilename());
    entity.setVariantId(getInputData().getVariantId());
    entity.setVariantName(getInputData().getVariantName());
    entity.setRespId(getInputData().getRespId());
    entity.setRespName(getInputData().getRespName());
    entity.setWpId(getInputData().getWpId());
    entity.setWpName(getInputData().getWpName());
    entity.setWpDefnVersId(getInputData().getWpDefnVersId());
    entity.setWpDefnVersName(getInputData().getWpDefnVersName());
    entity.setFileArchivalStatus(getInputData().getFileArchivalStatus());

    setUserDetails(COMMAND_MODE.CREATE, entity);

    // Persist the entity to get Wp Archival id
    persistEntity(entity);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    WpArchivalLoader wpArchivalLoader = new WpArchivalLoader(getServiceData());
    TWpArchival entity = wpArchivalLoader.getEntityObject(getInputData().getId());

    if (isObjectChanged(getOldData().getFileArchivalStatus(), getInputData().getFileArchivalStatus())) {
      entity.setFileArchivalStatus(getInputData().getFileArchivalStatus());
    }

    getLogger().debug("Updating the file archival status into t_wp_archival table");
    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    // Not Applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // Not Applicable
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
    // Not Applicable
  }


}
