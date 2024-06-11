package com.bosch.caltool.icdm.bo.apic.attr;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TPredefinedAttrValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValuesCreationModel;


/**
 * Command class for PredefinedAttrValues
 *
 * @author dmo5cob
 */
public class MultiplePredefinedAttrValuesCommand extends AbstractSimpleCommand {


  /**
   * Input model
   */
  private final PredefinedAttrValuesCreationModel inputData;

  /**
   * Constructor
   *
   * @param input input data
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public MultiplePredefinedAttrValuesCommand(final ServiceData serviceData,
      final PredefinedAttrValuesCreationModel input) throws IcdmException {
    super(serviceData);
    this.inputData = input;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {
    if (!this.inputData.getValuesToBeCreated().isEmpty()) {
      for (PredefinedAttrValue value : this.inputData.getValuesToBeCreated()) {
        PredefinedAttrValueCommand command = new PredefinedAttrValueCommand(getServiceData(), value, false, false);
        executeChildCommand(command);
        value.setId(command.getObjId());
      }

    }
    if (!this.inputData.getValuesToBeDeleted().isEmpty()) {
      for (PredefinedAttrValue val : this.inputData.getValuesToBeDeleted()) {
        PredefinedAttrValueCommand command = new PredefinedAttrValueCommand(getServiceData(), val, false, true);
        executeChildCommand(command);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    if (!this.inputData.getValuesToBeCreated().isEmpty()) {
      for (PredefinedAttrValue value : this.inputData.getValuesToBeCreated()) {
        // Refresh the entity
        PredefinedAttrValueLoader predefinedAttValLoader = new PredefinedAttrValueLoader(getServiceData());
        TPredefinedAttrValue entity = predefinedAttValLoader.getEntityObject(value.getId());
        getEm().refresh(entity);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {

    return true;
  }

}
