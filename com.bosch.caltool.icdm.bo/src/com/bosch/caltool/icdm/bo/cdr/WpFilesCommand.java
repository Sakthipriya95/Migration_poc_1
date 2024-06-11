package com.bosch.caltool.icdm.bo.cdr;

import java.util.ArrayList;
import java.util.List;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.cdr.TWpArchival;
import com.bosch.caltool.icdm.database.entity.cdr.TWpFiles;
import com.bosch.caltool.icdm.model.cdr.WpFiles;


/**
 * Command class for WpFiles
 *
 * @author msp5cob
 */
public class WpFilesCommand extends AbstractCommand<WpFiles, WpFilesLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public WpFilesCommand(final ServiceData serviceData, final WpFiles input) throws IcdmException {
    super(serviceData, input, new WpFilesLoader(serviceData), COMMAND_MODE.CREATE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TWpFiles entity = new TWpFiles();

    TWpArchival tWpArchival = new WpArchivalLoader(getServiceData()).getEntityObject(getInputData().getWpArchivalId());

    entity.setTWpArchival(tWpArchival);
    entity.setFileName(getInputData().getFileName());
    entity.setFileData(getInputData().getFileData());

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);

    List<TWpFiles> tWpFilesList = tWpArchival.getTWpFiles();
    if (tWpFilesList == null) {
      tWpFilesList = new ArrayList<>();
      tWpArchival.setTWpFiles(tWpFilesList);
    }
    tWpFilesList.add(entity);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    // Not Applicable
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
