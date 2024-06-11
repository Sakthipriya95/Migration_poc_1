package com.bosch.caltool.icdm.bo.cdr;

import java.util.ArrayList;
import java.util.List;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.cdr.TDaDataAssessment;
import com.bosch.caltool.icdm.database.entity.cdr.TDaFile;
import com.bosch.caltool.icdm.model.cdr.DaFile;


/**
 * Command class for DaFile
 *
 * @author say8cob
 */
public class DaFileCommand extends AbstractCommand<DaFile, DaFileLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public DaFileCommand(final ServiceData serviceData, final DaFile input) throws IcdmException {
    super(serviceData, input, new DaFileLoader(serviceData), COMMAND_MODE.CREATE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TDaFile entity = new TDaFile();

    TDaDataAssessment tDaDataAssessment =
        new DaDataAssessmentLoader(getServiceData()).getEntityObject(getInputData().getDataAssessmentId());
    entity.setTDaDataAssessment(tDaDataAssessment);
    entity.setFileName(getInputData().getFileName());
    entity.setFileData(getInputData().getFileData());

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);

    List<TDaFile> tDaFiles = tDaDataAssessment.getTDaFiles();
    if (tDaFiles == null) {
      tDaFiles = new ArrayList<>();
      tDaDataAssessment.setTDaFiles(tDaFiles);
    }
    tDaFiles.add(entity);
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
