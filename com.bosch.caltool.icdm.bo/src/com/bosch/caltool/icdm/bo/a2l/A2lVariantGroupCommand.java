package com.bosch.caltool.icdm.bo.a2l;

import java.util.ArrayList;
import java.util.List;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lVarGrpVariantMapping;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lVariantGroup;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpDefnVersion;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpParamMapping;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lVarGrpVariantMapping;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;


/**
 * Command class for A2lVariantGroup
 *
 * @author pdh2cob
 */
public class A2lVariantGroupCommand extends AbstractCommand<A2lVariantGroup, A2lVariantGroupLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public A2lVariantGroupCommand(final ServiceData serviceData, final A2lVariantGroup input, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, input, new A2lVariantGroupLoader(serviceData), resolveCommandModeA(isDelete, isUpdate));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TA2lVariantGroup entity = new TA2lVariantGroup();

    setValuesToEntity(entity);

    setUserDetails(COMMAND_MODE.CREATE, entity);
    updatePidcA2l(entity);
    persistEntity(entity);
  }

  /**
   * @param entity
   */
  private void setValuesToEntity(final TA2lVariantGroup entity) {
    entity.setGroupName(getInputData().getName());
    entity.setGroupDesc(getInputData().getDescription());

    new A2lWpDefnVersionLoader(getServiceData()).getEntityObject(getInputData().getWpDefnVersId())
        .addTA2lVariantGroup(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    A2lVariantGroupLoader loader = new A2lVariantGroupLoader(getServiceData());
    TA2lVariantGroup entity = loader.getEntityObject(getInputData().getId());

    setValuesToEntity(entity);
    updatePidcA2l(entity);
    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    A2lVariantGroupLoader loader = new A2lVariantGroupLoader(getServiceData());
    TA2lVariantGroup entity = loader.getEntityObject(getInputData().getId());


    List<TA2lVarGrpVariantMapping> ta2lVarGrpVarMappings = entity.getTA2lVarGrpVariantMappings();
    List<A2lVarGrpVariantMapping> a2lVarGrpMappingList = new ArrayList<>();
    for (TA2lVarGrpVariantMapping ta2lVarGrpVarMapping : ta2lVarGrpVarMappings) {
      A2lVarGrpVariantMapping a2lVarGpMapping = new A2lVarGrpVariantMappingLoader(getServiceData())
          .getDataObjectByID(ta2lVarGrpVarMapping.getA2lVarGrpVarId());
      a2lVarGrpMappingList.add(a2lVarGpMapping);

    }

    for (A2lVarGrpVariantMapping a2lVarGpMapping : a2lVarGrpMappingList) {

      A2lVarGrpVarMappingCommand varGroupMapCommand =
          new A2lVarGrpVarMappingCommand(getServiceData(), a2lVarGpMapping, false, true);
      executeChildCommand(varGroupMapCommand);

    }

    deleteWpRespAndMapping(entity);
    updatePidcA2l(entity);

    TA2lWpDefnVersion dbDefVers =
        new A2lWpDefnVersionLoader(getServiceData()).getEntityObject(getInputData().getWpDefnVersId());
    dbDefVers.removeTA2lVariantGroup(entity);

    getEm().remove(entity);

  }

  /**
   * @param entity
   * @throws DataException
   * @throws IcdmException
   */
  private void deleteWpRespAndMapping(final TA2lVariantGroup entity) throws DataException, IcdmException {

    List<A2lWpResponsibility> a2lWpRespList = new java.util.ArrayList<>();
    List<A2lWpParamMapping> mappingList = new ArrayList<>();

    for (TA2lWpResponsibility ta2lWpResponsibility : entity.gettA2lWpRespList()) {
      A2lWpResponsibilityLoader respLoader = new A2lWpResponsibilityLoader(getServiceData());
      List<TA2lWpParamMapping> ta2lWpParamMappings = ta2lWpResponsibility.getTA2lWpParamMappings();

      for (TA2lWpParamMapping ta2lWpParamMapping : ta2lWpParamMappings) {
        A2lWpParamMappingLoader mappingLoader = new A2lWpParamMappingLoader(getServiceData());
        A2lWpParamMapping mappingObj = mappingLoader.getDataObjectByID(ta2lWpParamMapping.getWpParamMappingId());
        mappingList.add(mappingObj);


      }
      A2lWpResponsibility a2lWpResp = respLoader.getDataObjectByID(ta2lWpResponsibility.getWpRespId());
      a2lWpRespList.add(a2lWpResp);
    }

    for (A2lWpParamMapping mappingObj : mappingList) {
      A2lWpParamMappingCommand command = new A2lWpParamMappingCommand(getServiceData(), mappingObj, false, true);
      executeChildCommand(command);

    }

    for (A2lWpResponsibility a2lWpResp : a2lWpRespList) {
      A2lWpResponsibilityCommand command = new A2lWpResponsibilityCommand(getServiceData(), a2lWpResp, false, true);
      executeChildCommand(command);

    }
  }

  /**
   * Update the pidc working set modified flag , if there is new change
   *
   * @param entity
   */
  private void updatePidcA2l(final TA2lVariantGroup entity) throws IcdmException {
    PidcA2lLoader pidcA2lLoader = new PidcA2lLoader(getServiceData());
    PidcA2l pidcA2l = pidcA2lLoader.getDataObjectByID(entity.gettA2lWpDefnVersion().getTPidcA2l().getPidcA2lId());
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
