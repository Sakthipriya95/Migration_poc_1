package com.bosch.caltool.icdm.bo.apic.cocwp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.uc.UseCaseGroupLoader;
import com.bosch.caltool.icdm.bo.uc.UsecaseFavoriteCommand;
import com.bosch.caltool.icdm.bo.uc.UsecaseFavoriteLoader;
import com.bosch.caltool.icdm.bo.wp.WorkPackageDivisionLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TWpmlWpMasterlist;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCaseGroup;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCaseSection;
import com.bosch.caltool.icdm.model.apic.cocwp.CoCWPUsedFlag;
import com.bosch.caltool.icdm.model.apic.cocwp.PIDCCocWpUpdationInputModel;
import com.bosch.caltool.icdm.model.apic.cocwp.PIDCCocWpUpdationOutputModel;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcSubVarCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVariantCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVersCocWp;
import com.bosch.caltool.icdm.model.uc.UseCaseGroup;
import com.bosch.caltool.icdm.model.uc.UsecaseFavorite;


/**
 * Command class for updating COC WP
 *
 * @author UKT1COB
 */
public class PidcCocWpUpdationCommand extends AbstractSimpleCommand {


  private final PIDCCocWpUpdationInputModel inputData;

  private PIDCCocWpUpdationOutputModel outputModel = new PIDCCocWpUpdationOutputModel();

  private final PidcCocWpUpdationBO pidcCocWpBO = new PidcCocWpUpdationBO(getServiceData());

  private final boolean invokedOnProjUCUpdate;


  /**
   * Constructor
   *
   * @param input input data
   * @param serviceData service Data
   * @param invokedOnProjUCUpdate true if the command is invoked as child command from UsecaseFavoriteCommand
   * @throws IcdmException error when initializing
   */
  public PidcCocWpUpdationCommand(final ServiceData serviceData, final PIDCCocWpUpdationInputModel input,
      final boolean invokedOnProjUCUpdate) throws IcdmException {
    super(serviceData);
    this.inputData = input;
    this.invokedOnProjUCUpdate = invokedOnProjUCUpdate;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {

    if (CommonUtils.isNotEmpty(getInputData().getPidcVersCocWpCreationMap())) {
      this.outputModel.setListOfNewlyCreatedPidcVersCocWpIds(
          createOrUpdPidcVersCocWP(false, getInputData().getPidcVersCocWpCreationMap()));
    }
    if (CommonUtils.isNotEmpty(getInputData().getPidcVersCocWpBeforeUpdate())) {
      this.outputModel.setListOfUpdatedPidcVersCocWpIds(
          createOrUpdPidcVersCocWP(true, getInputData().getPidcVersCocWpBeforeUpdate()));
    }
    if (CommonUtils.isNotEmpty(getInputData().getPidcVarCocWpCreationMap())) {
      this.outputModel.setListOfNewlyCreatedPidcVarCocWpIds(
          createOrUpdPidcVarCocWP(getInputData().getPidcVarCocWpCreationMap(), false));
    }
    if (CommonUtils.isNotEmpty(getInputData().getPidcVarCocWpMapBeforeUpdate())) {
      this.outputModel.setListOfUpdatedPidcVarCocWpIds(
          createOrUpdPidcVarCocWP(getInputData().getPidcVarCocWpMapBeforeUpdate(), true));
    }
    if (CommonUtils.isNotEmpty(getInputData().getPidcVarCocWpDeletionMap())) {
      deletePidcVarCocWP(getInputData().getPidcVarCocWpDeletionMap());
    }
    if (CommonUtils.isNotEmpty(getInputData().getPidcSubVarCocWpCreationMap())) {
      this.outputModel.setListOfNewlyCreatedPidcSubVarCocWpIds(
          createOrUpdPidcSubVarCocWP(getInputData().getPidcSubVarCocWpCreationMap(), false));
    }
    if (CommonUtils.isNotEmpty(getInputData().getPidcSubVarCocWpBeforeUpdateMap())) {
      this.outputModel.setListOfUpdatedPidcSubVarCocWpIds(
          createOrUpdPidcSubVarCocWP(getInputData().getPidcSubVarCocWpBeforeUpdateMap(), true));
    }
    if (CommonUtils.isNotEmpty(getInputData().getPidcSubVarCocWpDeletionMap())) {
      deletePidcSubVarCocWP(getInputData().getPidcSubVarCocWpDeletionMap());
    }
  }

  /**
   * @param wrkPkgDivLoader
   * @param isToUpdate
   * @param inputMap
   * @return
   * @throws IcdmException
   */
  private List<Long> createOrUpdPidcVersCocWP(final boolean isToUpdate, final Map<Long, PidcVersCocWp> inputMap)
      throws IcdmException {

    List<Long> createdOrUpdPidcVersCocWPIdList = new ArrayList<>();

    for (PidcVersCocWp pidcVersCocWP : inputMap.values()) {
      // Invoke command
      PidcVersCocWpCommand pidcVersCocWPCmd = new PidcVersCocWpCommand(getServiceData(), pidcVersCocWP, isToUpdate);
      executeChildCommand(pidcVersCocWPCmd);

      // to update the related entity for PidcVersCocWp
      this.pidcCocWpBO.updatePidcVersCocWpReferenceEntity(pidcVersCocWPCmd.getNewData());
      createdOrUpdPidcVersCocWPIdList.add(pidcVersCocWPCmd.getObjId());


      // if the service is called to update used flag and if this is called as child command of UsecaseFavouriteCommand,
      // then update Project usecase
      if (getInputData().isInvokedOnUsedFlagUpd() && !this.invokedOnProjUCUpdate) {
        updateProjUsecase(pidcVersCocWP);
      }
    }

    return createdOrUpdPidcVersCocWPIdList;
  }


  /**
   * @param pidcCocWpVers
   * @throws IcdmException
   */
  private void updateProjUsecase(final PidcVersCocWp pidcVersCocWP) throws IcdmException {

    Set<Long> setOfUcSecIdToBeUpd = new HashSet<>();

    Long pidcVersCocWpWpmlId = new WorkPackageDivisionLoader(getServiceData())
        .getEntityObject(pidcVersCocWP.getWPDivId()).getTWorkpackage().gettWpmlWpMasterList().getId();

    for (TabvUseCaseSection tabvUc : getListAllUseCaseSections()) {
      TWpmlWpMasterlist ucSecWpml = tabvUc.gettWpmlWpMasterList();
      // check whether the Coc Wp in PIDC level have same wpml_id as usecase section, if yes, then add the section Id
      if (CommonUtils.isNotNull(ucSecWpml) && CommonUtils.isEqual(pidcVersCocWpWpmlId, ucSecWpml.getId())) {
        setOfUcSecIdToBeUpd.add(tabvUc.getSectionId());
      }
    }

    boolean isToDelete = CommonUtils.isEqualIgnoreCase(pidcVersCocWP.getUsedFlag(), CoCWPUsedFlag.NO.getDbType()) ||
        CommonUtils.isEqualIgnoreCase(pidcVersCocWP.getUsedFlag(), CoCWPUsedFlag.NOT_DEFINED.getDbType());
    for (Long uCSecId : setOfUcSecIdToBeUpd) {
      UsecaseFavorite uCFav;

      Long projId =
          new PidcVersionLoader(getServiceData()).getDataObjectByID(pidcVersCocWP.getPidcVersId()).getPidcId();
      Optional<UsecaseFavorite> projFavUC = new UsecaseFavoriteLoader(getServiceData()).getProjFavoriteUseCases(projId)
          .values().stream().filter(ucFavItem -> CommonUtils.isEqual(ucFavItem.getSectionId(), uCSecId)).findFirst();

      Boolean isAlreadyProjUC = projFavUC.isPresent();

      if (isToDelete) {
        if (isAlreadyProjUC.booleanValue()) {

          uCFav = projFavUC.get();

          UsecaseFavoriteCommand cmd = new UsecaseFavoriteCommand(getServiceData(), uCFav, false, isToDelete, true);
          executeChildCommand(cmd);

          this.outputModel.getListOfDelUcFav().add(uCFav);
        }
      }
      else {
        if (!(isAlreadyProjUC.booleanValue())) {
          uCFav = new UsecaseFavorite();
          uCFav.setSectionId(uCSecId);
          uCFav.setProjectId(projId);

          UsecaseFavoriteCommand cmd = new UsecaseFavoriteCommand(getServiceData(), uCFav, false, isToDelete, true);
          executeChildCommand(cmd);

          this.outputModel.getListOfNewlyCreatedUCFav().add(cmd.getNewData());
        }
      }
    }

  }

  /**
   * @param entity
   * @return
   * @throws DataException
   */
  private List<TabvUseCaseSection> getListAllUseCaseSections() throws DataException {

    UseCaseGroupLoader ucGrpLoader = new UseCaseGroupLoader(getServiceData());
    List<TabvUseCaseSection> listOfAllUcSections = new ArrayList<>();

    // loop through all root UcGrps
    for (UseCaseGroup ucGrp : ucGrpLoader.getAll().values()) {
      TabvUseCaseGroup tabvUCGroup = ucGrpLoader.getEntityObject(ucGrp.getId());
      this.pidcCocWpBO.setUcSecFromUcGrp(listOfAllUcSections, tabvUCGroup);
    }
    return listOfAllUcSections;
  }


  /**
   * @param inputMap
   * @return
   * @throws IcdmException
   */
  private List<Long> createOrUpdPidcVarCocWP(final Map<Long, Map<Long, PidcVariantCocWp>> inputMap,
      final boolean isUpdate)
      throws IcdmException {

    List<Long> createdOrUpdPidcVarCocWPIdList = new ArrayList<>();

    // Loop through each variant
    for (Map<Long, PidcVariantCocWp> pidcCocWpVarMap : inputMap.values()) {
      // Loop through each pidcVarCocWP under variant
      for (PidcVariantCocWp pidcCocWpVar : pidcCocWpVarMap.values()) {
        // Invoke command
        PidcVariantCocWpCommand pidcVarCocWpCmd =
            new PidcVariantCocWpCommand(getServiceData(), pidcCocWpVar, isUpdate, false);
        executeChildCommand(pidcVarCocWpCmd);
        // to update the related entity for PidcVarsCocWp
        this.pidcCocWpBO.updatePidcVarsCocWpReferenceEntity(pidcVarCocWpCmd.getNewData(), pidcVarCocWpCmd.getCmdMode());
        createdOrUpdPidcVarCocWPIdList.add(pidcVarCocWpCmd.getObjId());
      }
    }
    return createdOrUpdPidcVarCocWPIdList;
  }

  /**
   * @param pidcVarCocWpToBeDeletedMap
   * @return
   * @throws IcdmException
   */
  private void deletePidcVarCocWP(final Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpToBeDeletedMap)
      throws IcdmException {

    // Loop through each variant
    for (Map<Long, PidcVariantCocWp> pidcCocWpVarMap : pidcVarCocWpToBeDeletedMap.values()) {
      // Loop through each pidcVarCocWP under variant
      for (PidcVariantCocWp pidcCocWpVar : pidcCocWpVarMap.values()) {
        PidcVariantCocWpCommand pidcVarCocWpCmd =
            new PidcVariantCocWpCommand(getServiceData(), pidcCocWpVar, false, true);
        // to update the related entity for PidcVariantCocWp
        this.pidcCocWpBO.updatePidcVarsCocWpReferenceEntity(pidcCocWpVar, pidcVarCocWpCmd.getCmdMode());
        // Invoke command
        executeChildCommand(pidcVarCocWpCmd);
      }
    }
  }

  /**
   * @param inputMap
   * @return
   * @throws IcdmException
   */
  private List<Long> createOrUpdPidcSubVarCocWP(final Map<Long, Map<Long, PidcSubVarCocWp>> inputMap,
      final boolean isUpdate)
      throws IcdmException {

    List<Long> createdOrUpdPidcSubVarCocWPIdList = new ArrayList<>();

    // Loop through each sub-variant
    for (Map<Long, PidcSubVarCocWp> pidcCocWpSubVarMap : inputMap.values()) {
      // Loop through each pidcSubVarCocWP under sub-variant
      for (PidcSubVarCocWp pidcCocWpSubVar : pidcCocWpSubVarMap.values()) {
        // Invoke command
        PidcSubVarCocWpCommand pidcSubVarCocWpCmd =
            new PidcSubVarCocWpCommand(getServiceData(), pidcCocWpSubVar, isUpdate, false);
        executeChildCommand(pidcSubVarCocWpCmd);
        // to update the related entity for PidcSubVariantCocWp
        this.pidcCocWpBO.updatePidcSubVarsCocWpReferenceEntity(pidcSubVarCocWpCmd.getNewData(),
            pidcSubVarCocWpCmd.getCmdMode());
        createdOrUpdPidcSubVarCocWPIdList.add(pidcSubVarCocWpCmd.getObjId());
      }
    }
    return createdOrUpdPidcSubVarCocWPIdList;
  }


  /**
   * @param pidcSubVarCocWpToBeDeletedMap
   * @return
   * @throws IcdmException
   */
  private void deletePidcSubVarCocWP(final Map<Long, Map<Long, PidcSubVarCocWp>> pidcSubVarCocWpToBeDeletedMap)
      throws IcdmException {

    // Loop through each variant
    for (Map<Long, PidcSubVarCocWp> pidcCocWpSubVarMap : pidcSubVarCocWpToBeDeletedMap.values()) {
      // Loop through each pidcSubVarCocWp under variant
      for (PidcSubVarCocWp pidcSubVarCocWp : pidcCocWpSubVarMap.values()) {
        PidcSubVarCocWpCommand pidcSubVarCocWpCmd =
            new PidcSubVarCocWpCommand(getServiceData(), pidcSubVarCocWp, false, true);
        // to update the related entity for PidcSubVariantCocWp
        this.pidcCocWpBO.updatePidcSubVarsCocWpReferenceEntity(pidcSubVarCocWp, pidcSubVarCocWpCmd.getCmdMode());
        // Invoke command
        executeChildCommand(pidcSubVarCocWpCmd);
      }
    }
  }


  /**
   * @return the inputData
   */
  public PIDCCocWpUpdationInputModel getInputData() {
    return this.inputData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // NA
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }


  /**
   * @return the outputModel
   */
  public PIDCCocWpUpdationOutputModel getOutputModel() {
    return this.outputModel;
  }


  /**
   * @param outputModel the outputModel to set
   */
  public void setOutputModel(final PIDCCocWpUpdationOutputModel outputModel) {
    this.outputModel = outputModel;
  }

}
