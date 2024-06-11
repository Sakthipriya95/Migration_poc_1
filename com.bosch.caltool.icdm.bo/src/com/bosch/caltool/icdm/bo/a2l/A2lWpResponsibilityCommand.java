package com.bosch.caltool.icdm.bo.a2l;

import java.util.ArrayList;
import java.util.List;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lResponsibility;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lVariantGroup;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWorkPackage;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpDefnVersion;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;


/**
 * Command class for A2lWpResponsibility
 *
 * @author pdh2cob
 */
public class A2lWpResponsibilityCommand extends AbstractCommand<A2lWpResponsibility, A2lWpResponsibilityLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public A2lWpResponsibilityCommand(final ServiceData serviceData, final A2lWpResponsibility input,
      final boolean isUpdate, final boolean isDelete) throws IcdmException {
    super(serviceData, input, new A2lWpResponsibilityLoader(serviceData), resolveCommandModeA(isDelete, isUpdate));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TA2lWpResponsibility entity = new TA2lWpResponsibility();

    setValuesToEntity(entity);
    setUserDetails(COMMAND_MODE.CREATE, entity);
    updateWorkingSetStatusForPidcA2l(entity);
    persistEntity(entity);
  }

  /**
   * @param entity
   */
  private void setValuesToEntity(final TA2lWpResponsibility entity) {

    A2lWpDefnVersionLoader wpDefnLoader = new A2lWpDefnVersionLoader(getServiceData());
    TA2lWpDefnVersion wpDefnVersEntity = wpDefnLoader.getEntityObject(getInputData().getWpDefnVersId());
    entity.settA2lWpDefnVersion(wpDefnVersEntity);

    entity.setA2lWp(new A2lWorkPackageLoader(getServiceData()).getEntityObject(getInputData().getA2lWpId()));

    A2lResponsibilityLoader a2lRespLoader = new A2lResponsibilityLoader(getServiceData());
    TA2lResponsibility a2lRespEntity = a2lRespLoader.getEntityObject(getInputData().getA2lRespId());
    entity.setA2lResponsibility(a2lRespEntity);

    // set variant group entity
    if (getInputData().getVariantGrpId() != null) {
      TA2lVariantGroup varGrpEntity =
          new A2lVariantGroupLoader(getServiceData()).getEntityObject(getInputData().getVariantGrpId());
      entity.setVariantGroup(varGrpEntity);
      // set Ta2lvariantGroup entity's referenced list
      if (null == varGrpEntity.gettA2lWpRespList()) {
        List<TA2lWpResponsibility> wpResponsibilities = new ArrayList<>();
        wpResponsibilities.add(entity);
        varGrpEntity.settA2lWpRespList(wpResponsibilities);
      }
      else {
        varGrpEntity.gettA2lWpRespList().add(entity);
      }
    }

    // set TA2lWpDefinitionVersion entity's referenced list
    List<TA2lWpResponsibility> ta2lWpRespPals = wpDefnVersEntity.getTA2lWpResponsibility();
    if (ta2lWpRespPals == null) {
      ta2lWpRespPals = new ArrayList<>();
    }
    ta2lWpRespPals.add(entity);
    wpDefnVersEntity.setTA2lWpResponsibility(ta2lWpRespPals);
    // set ta2l wp pal
    A2lWorkPackageLoader a2lWpLoader = new A2lWorkPackageLoader(getServiceData());
    TA2lWorkPackage dbA2lWp = a2lWpLoader.getEntityObject(getInputData().getA2lWpId());
    if (dbA2lWp != null) {
      List<TA2lWpResponsibility> a2lWpRespPalList = dbA2lWp.getA2lWpRespPalList();
      if (a2lWpRespPalList == null) {
        a2lWpRespPalList = new ArrayList<>();
      }
      a2lWpRespPalList.add(entity);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    A2lWpResponsibilityLoader loader = new A2lWpResponsibilityLoader(getServiceData());
    TA2lWpResponsibility entity = loader.getEntityObject(getInputData().getId());

    setValuesToEntity(entity);

    updateReferencingEntities(entity, false);
    updateWorkingSetStatusForPidcA2l(entity);
    setUserDetails(COMMAND_MODE.UPDATE, entity);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    A2lWpResponsibilityLoader loader = new A2lWpResponsibilityLoader(getServiceData());
    TA2lWpResponsibility entity = loader.getEntityObject(getInputData().getId());
    updateReferencingEntities(entity, true);
    updateWorkingSetStatusForPidcA2l(entity);
    getEm().remove(entity);
  }

  /**
   * Update the pidc working set modified flag , if there is new change
   *
   * @param entity
   */
  private void updateWorkingSetStatusForPidcA2l(final TA2lWpResponsibility entity) throws IcdmException {
    PidcA2lLoader pidcA2lLoader = new PidcA2lLoader(getServiceData());

    PidcA2l pidcA2l = pidcA2lLoader.getDataObjectByID(entity.gettA2lWpDefnVersion().getTPidcA2l().getPidcA2lId());
    A2lWpDefnVersion wpDefnVersion =
        new A2lWpDefnVersionLoader(getServiceData()).getDataObjectByID(entity.gettA2lWpDefnVersion().getWpDefnVersId());
    if (!pidcA2l.isWorkingSetModified() && wpDefnVersion.isWorkingSet()) {
      pidcA2l.setWorkingSetModified(true);
      PidcA2lCommand pidcA2lCmd = new PidcA2lCommand(getServiceData(), pidcA2l, true, true, false);
      executeChildCommand(pidcA2lCmd);
    }
  }

  /**
   * @param entity
   * @param isDelete - false means update operation
   */
  private void updateReferencingEntities(final TA2lWpResponsibility entity, final boolean isDelete) {

    // update A2lWpDefinitionVersion details
    A2lWpDefnVersionLoader loader = new A2lWpDefnVersionLoader(getServiceData());

    TA2lWpDefnVersion wpDefnVersionEntity = loader.getEntityObject(getInputData().getWpDefnVersId());
    updateA2lWpDefVersnDetails(entity, isDelete, wpDefnVersionEntity);

    // Update Variant Group details
    updateVariantGrpDetails(entity, isDelete);

    // update t pidc wp resp details
    updatePidcWpRespDetails(entity, isDelete);

    // update a2lwp pal details
    A2lWorkPackageLoader a2lWpLoader = new A2lWorkPackageLoader(getServiceData());
    TA2lWorkPackage tWpPal = a2lWpLoader.getEntityObject(getInputData().getA2lWpId());
    updateA2lWpPalDetails(entity, isDelete, tWpPal);
  }

  /**
   * @param entity
   * @param isDelete
   * @param wpDefnVersionEntity
   */
  private void updateA2lWpDefVersnDetails(final TA2lWpResponsibility entity, final boolean isDelete,
      final TA2lWpDefnVersion wpDefnVersionEntity) {
    wpDefnVersionEntity.getTA2lWpResponsibility().remove(entity);
    if (!isDelete) {
      wpDefnVersionEntity.getTA2lWpResponsibility().add(entity);
    }
  }

  /**
   * @param entity
   * @param isDelete
   */
  private void updateVariantGrpDetails(final TA2lWpResponsibility entity, final boolean isDelete) {
    if (getInputData().getVariantGrpId() != null) {
      A2lVariantGroupLoader varGrpLoader = new A2lVariantGroupLoader(getServiceData());
      TA2lVariantGroup varGrpEntity = varGrpLoader.getEntityObject(getInputData().getVariantGrpId());
      if (varGrpEntity.gettA2lWpRespList() != null) {
        varGrpEntity.gettA2lWpRespList().remove(entity);
      }
      if (!isDelete) {
        if (varGrpEntity.gettA2lWpRespList() == null) {
          varGrpEntity.settA2lWpRespList(new ArrayList<>());
        }
        varGrpEntity.gettA2lWpRespList().add(entity);
      }
    }
  }

  /**
   * @param entity
   * @param isDelete
   */
  private void updatePidcWpRespDetails(final TA2lWpResponsibility entity, final boolean isDelete) {
    if (getInputData().getA2lRespId() != null) {
      A2lResponsibilityLoader pidWpResploader = new A2lResponsibilityLoader(getServiceData());
      TA2lResponsibility pidWpRespEntity = pidWpResploader.getEntityObject(getInputData().getA2lRespId());
      if (pidWpRespEntity.getWpRespPalList() != null) {
        pidWpRespEntity.getWpRespPalList().remove(entity);
      }
      if (!isDelete) {
        if (pidWpRespEntity.getWpRespPalList() == null) {
          pidWpRespEntity.setWpRespPalList(new ArrayList<>());
        }
        pidWpRespEntity.getWpRespPalList().add(entity);
      }
    }
  }

  /**
   * @param entity
   * @param isDelete
   * @param tWpPal
   */
  private void updateA2lWpPalDetails(final TA2lWpResponsibility entity, final boolean isDelete,
      final TA2lWorkPackage tWpPal) {
    if (tWpPal != null) {
      tWpPal.getA2lWpRespPalList().remove(entity);
      if (!isDelete) {
        tWpPal.getA2lWpRespPalList().add(entity);
      }
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // to do

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
    // No implementation
  }

}
