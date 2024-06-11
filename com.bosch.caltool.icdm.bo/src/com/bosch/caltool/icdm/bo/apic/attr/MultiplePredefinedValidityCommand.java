package com.bosch.caltool.icdm.bo.apic.attr;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedValidity;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedValidityCreationModel;


/**
 * Command class for PredefinedValidity
 *
 * @author dmo5cob
 */
public class MultiplePredefinedValidityCommand extends AbstractSimpleCommand {


  /**
   * Input model
   */
  private final PredefinedValidityCreationModel inputData;

  /**
   * Constructor
   *
   * @param input input data
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public MultiplePredefinedValidityCommand(final ServiceData serviceData, final PredefinedValidityCreationModel input)
      throws IcdmException {
    super(serviceData);
    this.inputData = input;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {
    if (!this.inputData.getValidityToBeCreated().isEmpty()) {
      for (PredefinedValidity validity : this.inputData.getValidityToBeCreated()) {
        PredefinedValidityCommand command = new PredefinedValidityCommand(getServiceData(), validity, false, false);
        executeChildCommand(command);
        validity.setId(command.getObjId());
      }

    }
    if (!this.inputData.getValidityToBeDeleted().isEmpty()) {
      for (PredefinedValidity validity : this.inputData.getValidityToBeDeleted()) {
        PredefinedValidityCommand command = new PredefinedValidityCommand(getServiceData(), validity, false, true);
        executeChildCommand(command);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {

    return true;
  }

}
