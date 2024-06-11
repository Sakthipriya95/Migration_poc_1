package com.bosch.caltool.icdm.bo.a2l;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lVarGrpVariantMapping;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lVarGrpVariantMapping;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;


/**
 * Command class for A2lVarGrpVarMapping
 *
 * @author pdh2cob
 */
public class A2lVarGrpVarMappingCommand
    extends AbstractCommand<A2lVarGrpVariantMapping, A2lVarGrpVariantMappingLoader> {

  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public A2lVarGrpVarMappingCommand(final ServiceData serviceData, final A2lVarGrpVariantMapping input,
      final boolean isUpdate, final boolean isDelete) throws IcdmException {
    super(serviceData, input, new A2lVarGrpVariantMappingLoader(serviceData), resolveCommandModeA(isDelete, isUpdate));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    // create the entity
    TA2lVarGrpVariantMapping entity = new TA2lVarGrpVariantMapping();

    setValuesToEntity(entity);

    setUserDetails(COMMAND_MODE.CREATE, entity);
    updatePidcA2l(entity);
    persistEntity(entity);
  }


  private void setValuesToEntity(final TA2lVarGrpVariantMapping entity) {
    // set the variant details to entity
    entity
        .setTabvProjectVariant(new PidcVariantLoader(getServiceData()).getEntityObject(getInputData().getVariantId()));

    new A2lVariantGroupLoader(getServiceData()).getEntityObject(getInputData().getA2lVarGroupId())
        .addTA2lVarGrpVarMapping(entity);


  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    A2lVarGrpVariantMappingLoader loader = new A2lVarGrpVariantMappingLoader(getServiceData());
    TA2lVarGrpVariantMapping entity = loader.getEntityObject(getInputData().getId());

    setValuesToEntity(entity);

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    TA2lVarGrpVariantMapping entity =
        new A2lVarGrpVariantMappingLoader(getServiceData()).getEntityObject(getInputData().getId());
    updatePidcA2l(entity);

    TA2lVariantGroup dbVarGrp =
        new A2lVariantGroupLoader(getServiceData()).getEntityObject(getInputData().getA2lVarGroupId());
    dbVarGrp.removeTA2lVarGrpVarMapping(entity);

    getEm().remove(entity);
  }

  /**
   * Update the pidc working set modified flag , if there is new change
   *
   * @param entity
   */
  private void updatePidcA2l(final TA2lVarGrpVariantMapping entity) throws IcdmException {
    PidcA2lLoader pidcA2lLoader = new PidcA2lLoader(getServiceData());
    PidcA2l pidcA2l = pidcA2lLoader
        .getDataObjectByID(entity.getTA2lVariantGroup().gettA2lWpDefnVersion().getTPidcA2l().getPidcA2lId());
    if (!pidcA2l.isWorkingSetModified()) {
      pidcA2l.setWorkingSetModified(true);
      PidcA2lCommand pidcA2lCmd = new PidcA2lCommand(getServiceData(), pidcA2l, true, true, false);
      executeChildCommand(pidcA2lCmd);
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
    // TODO Auto-generated method stub
  }

}
