package com.bosch.caltool.icdm.bo.uc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.cocwp.PidcCocWpUpdationBO;
import com.bosch.caltool.icdm.bo.apic.cocwp.PidcCocWpUpdationCommand;
import com.bosch.caltool.icdm.bo.apic.cocwp.PidcVersCocWpLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.MultipleFocusMatrixCommand;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.bo.wp.WorkPackageDivisionLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TUsecaseFavorite;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackageDivision;
import com.bosch.caltool.icdm.database.entity.apic.TWpmlWpMasterlist;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicUser;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectidcard;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCase;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCaseGroup;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCaseSection;
import com.bosch.caltool.icdm.model.apic.cocwp.CoCWPUsedFlag;
import com.bosch.caltool.icdm.model.apic.cocwp.PIDCCocWpUpdationInputModel;
import com.bosch.caltool.icdm.model.apic.cocwp.PIDCCocWpUpdationModel;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcSubVarCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVariantCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVersCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVersCocWpData;
import com.bosch.caltool.icdm.model.uc.UsecaseFavorite;


/**
 * Command class for UsecaseFavorite
 *
 * @author dmo5cob
 */
public class UsecaseFavoriteCommand extends AbstractCommand<UsecaseFavorite, UsecaseFavoriteLoader> {


  private PIDCCocWpUpdationModel pidcVersCoCWPUpdateModel = new PIDCCocWpUpdationModel();
  private final boolean invokedOnCoCWPUpdate;

  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @param invokedOnCoCWPUpdate true if the command is invoked as child command from PidcVersCoCWPCommand
   * @throws IcdmException error when initializing
   */
  public UsecaseFavoriteCommand(final ServiceData serviceData, final UsecaseFavorite input, final boolean isUpdate,
      final boolean isDelete, final boolean invokedOnCoCWPUpdate) throws IcdmException {
    super(serviceData, input, new UsecaseFavoriteLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : isUpdate(isUpdate));
    this.invokedOnCoCWPUpdate = invokedOnCoCWPUpdate;
  }

  /**
   * @param isUpdate
   * @return
   */
  private static COMMAND_MODE isUpdate(final boolean isUpdate) {
    return isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TUsecaseFavorite entity = new TUsecaseFavorite();

    entity.setTabvUseCaseGroup(new UseCaseGroupLoader(getServiceData()).getEntityObject(getInputData().getGroupId()));
    entity.setTabvUseCas(new UseCaseLoader(getServiceData()).getEntityObject(getInputData().getUseCaseId()));
    entity.setTabvUseCaseSection(
        new UseCaseSectionLoader(getServiceData()).getEntityObject(getInputData().getSectionId()));
    if (CommonUtils.isNotNull(getInputData().getUserId())) {
      entity.setTabvApicUser(new UserLoader(getServiceData()).getEntityObject(getInputData().getUserId()));
    }
    if (CommonUtils.isNotNull(getInputData().getProjectId())) {
      entity.setTabvProjectidcard(new PidcLoader(getServiceData()).getEntityObject(getInputData().getProjectId()));
    }
    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);

    if (CommonUtils.isNotNull(getInputData().getUserId())) {
      TabvApicUser userEntity = new UserLoader(getServiceData()).getEntityObject(getInputData().getUserId());

      Set<TUsecaseFavorite> ucFavs = userEntity.getTUsecaseFavorites();
      if (null == ucFavs) {
        ucFavs = new HashSet<>();
      }
      ucFavs.add(entity);
    }
    if (CommonUtils.isNotNull(getInputData().getProjectId())) {

      TabvProjectidcard projEntity = new PidcLoader(getServiceData()).getEntityObject(getInputData().getProjectId());

      Set<TUsecaseFavorite> projfavs = projEntity.getTUsecaseFavorites();
      if (null == projfavs) {
        projfavs = new HashSet<>();
      }
      projfavs.add(entity);
    }

    updateFocusMatrixEntitiesForProj(entity);

    // if service call is for create or paste pidc then no need to update Coc Wp used flag
    // if division attribute is not set, then no need to update CoC WP on adding usecase to project usecase
    if (!this.invokedOnCoCWPUpdate && isProjUsecase() && isDivAttrAvailable()) {
      // On adding UsecaseFavorite object as Proj usecase then the CoC WP with same wpml_masterlist_id uc sec will be
      // marked as 'Y'
      updateCoCWpUsedFlag(entity, CoCWPUsedFlag.YES.getDbType());
    }
  }


  /**
   * @param entity
   * @throws IcdmException
   */
  private void updateFocusMatrixEntitiesForProj(final TUsecaseFavorite entity) throws IcdmException {
    // skip private usecases
    if (entity.getTabvApicUser() == null) {
      Map<Long, UsecaseFavorite> favMap = new HashMap<>();
      favMap.put(entity.getUcFavId(), new UsecaseFavoriteLoader(getServiceData()).createDataObject(entity));
      MultipleFocusMatrixCommand cmd = new MultipleFocusMatrixCommand(getServiceData(), favMap, entity);
      executeChildCommand(cmd);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    // NA
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    UsecaseFavoriteLoader loader = new UsecaseFavoriteLoader(getServiceData());
    TUsecaseFavorite entity = loader.getEntityObject(getInputData().getId());

    getEm().remove(entity);

    if (CommonUtils.isNotNull(getInputData().getUserId())) {
      TabvApicUser userEntity = new UserLoader(getServiceData()).getEntityObject(getInputData().getUserId());
      Set<TUsecaseFavorite> ucFavs = userEntity.getTUsecaseFavorites();
      if ((null != ucFavs) && ucFavs.contains(entity)) {
        ucFavs.remove(entity);
      }
    }
    if (CommonUtils.isNotNull(getInputData().getProjectId())) {
      TabvProjectidcard projEntity = new PidcLoader(getServiceData()).getEntityObject(getInputData().getProjectId());
      Set<TUsecaseFavorite> projfavs = projEntity.getTUsecaseFavorites();
      if ((null != projfavs) && projfavs.contains(entity)) {
        projfavs.remove(entity);
      }
    }

    // if it is private usecase and if division attribute is not set, then no need to update CoC WP on adding usecase to
    // project usecase
    if (!this.invokedOnCoCWPUpdate && isProjUsecase() && isDivAttrAvailable()) {
      // When user remove UsecaseFavorite object from Project use case then the CoC WP with wpml_masterlist_id same as
      // in
      // usecase section under usecase ('s used flag) will be marked as 'N'
      updateCoCWpUsedFlag(entity, CoCWPUsedFlag.NO.getDbType());
    }
  }

  private boolean isProjUsecase() {
    return CommonUtils.isNotNull(getInputData().getProjectId()) && CommonUtils.isNull(getInputData().getUserId());
  }

  private boolean isDivAttrAvailable() throws IcdmException {
    Long pidcVersId = getInputData().getPidcVersId();
    return CommonUtils.isNotNull(pidcVersId) &&
        CommonUtils.isNotNull(new PidcVersionLoader(getServiceData()).getDivIdByPidcVersId(pidcVersId));
  }

  /**
   * @param entity
   * @param usedFlagToBeUpdated
   * @throws IcdmException
   */
  private void updateCoCWpUsedFlag(final TUsecaseFavorite entity, final String usedFlagToBeUpdated)
      throws IcdmException {

    List<TabvUseCaseSection> listOfUCAddedAsProjFav = getListOfUCAddedAsProjFav(entity);

    executeUpdPidcVersCocWPUsedFlag(listOfUCAddedAsProjFav, new PidcVersCocWpLoader(getServiceData()),
        usedFlagToBeUpdated);
  }

  /**
   * @param entity
   * @return
   */
  private List<TabvUseCaseSection> getListOfUCAddedAsProjFav(final TUsecaseFavorite entity) {

    List<TabvUseCaseSection> listOfUCAddedAsProjFav = new ArrayList<>();
    PidcCocWpUpdationBO pidcCocWpBO = new PidcCocWpUpdationBO(getServiceData());

    TabvUseCaseGroup tabvUseCaseGroup = entity.getTabvUseCaseGroup();
    TabvUseCase tabvUseCas = entity.getTabvUseCas();
    TabvUseCaseSection tabvUseCaseSection = entity.getTabvUseCaseSection();

    // If the added UsecaseFavorite object is Usecase Group, then add all sections under all usecase under usecase
    // Group
    if (CommonUtils.isNotNull(tabvUseCaseGroup)) {
      pidcCocWpBO.setUcSecFromUcGrp(listOfUCAddedAsProjFav, tabvUseCaseGroup);
    }
    else if (CommonUtils.isNotNull(tabvUseCas)) {
      // If the added UsecaseFavorite object is Usecase, then add all sections under usecase
      for (TabvUseCaseSection ucSec : tabvUseCas.getTabvUseCaseSections()) {
        pidcCocWpBO.setUcSecFromUcSec(listOfUCAddedAsProjFav, ucSec);
      }
    }
    else if (CommonUtils.isNotNull(tabvUseCaseSection)) {

      // If the added UsecaseFavorite object is Usecase section, then add itself and all child sections under usecase
      // section
      listOfUCAddedAsProjFav.add(tabvUseCaseSection);
      for (TabvUseCaseSection ucSec : tabvUseCaseSection.getTabvUseCaseSections()) {
        pidcCocWpBO.setUcSecFromUcSec(listOfUCAddedAsProjFav, ucSec);
      }
    }

    return listOfUCAddedAsProjFav;
  }

  /**
   * @param useCaseSections
   * @param pidcVersCocWpLoader
   * @param usedFlagToBeUpdated
   * @throws IcdmException
   */
  private void executeUpdPidcVersCocWPUsedFlag(final List<TabvUseCaseSection> useCaseSections,
      final PidcVersCocWpLoader pidcVersCocWpLoader, final String usedFlagToBeUpdated)
      throws IcdmException {

    // Map contain list of pidcVersCoCWP whose used flag to be updated to 'Y'/'N' on adding/removing related usecase
    // section to
    // project usecase
    Map<Long, PidcVersCocWp> pidcVersCocWpUpdateMap = new HashMap<>();
    Map<Long, PidcVersCocWp> pidcVersCocWpCreateMap = new HashMap<>();
    Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpDeletionMap = new HashMap<>();
    Map<Long, Map<Long, PidcSubVarCocWp>> pidcSubVarCocWpDeletionMap = new HashMap<>();

    for (TabvUseCaseSection ucSec : useCaseSections) {
      Long inputPidcVersId = getInputData().getPidcVersId();
      PidcVersCocWpData pidcVersCocWpData =
          pidcVersCocWpLoader.getAllCocWpByPidcVersId(CommonUtils.isNotNull(inputPidcVersId) ? inputPidcVersId
              : new PidcVersionLoader(getServiceData()).getActivePidcVersion(getInputData().getProjectId()).getId());

      for (PidcVersCocWp pidcVersCocWP : pidcVersCocWpData.getPidcVersCocWpMap().values()) {

        TWorkpackageDivision twrkPkgDiv =
            new WorkPackageDivisionLoader(getServiceData()).getEntityObject(pidcVersCocWP.getWPDivId());
        TWpmlWpMasterlist ucSecWpml = ucSec.gettWpmlWpMasterList();

        // check whether the Coc Wp in PIDC level have same wpml_id as usecase section
        if (CommonUtils.isNotNull(ucSecWpml) &&
            CommonUtils.isEqual(twrkPkgDiv.getTWorkpackage().gettWpmlWpMasterList().getId(), ucSecWpml.getId())) {

          PidcVersCocWp updatedPidcVersCocWP = new PidcVersCocWp();
          CommonUtils.shallowCopy(updatedPidcVersCocWP, pidcVersCocWP);
          updatedPidcVersCocWP.setUsedFlag(usedFlagToBeUpdated);

          // If Used flag is to be updated to 'N', check whether it is in child level and add that child record for
          // deleting
          if ((CommonUtils.isEqual(usedFlagToBeUpdated, CoCWPUsedFlag.NO.getDbType())) &&
              pidcVersCocWP.isAtChildLevel()) {

            // Remove the child records
            updatedPidcVersCocWP.setAtChildLevel(false);

            setChildRecToBeDeleted(pidcVarCocWpDeletionMap, pidcSubVarCocWpDeletionMap, pidcVersCocWpData,
                pidcVersCocWP);
          }

          // If the Coc Wp is a virtual one in UI, then create it else update it
          if (CommonUtils.isNull(pidcVersCocWP.getId())) {
            pidcVersCocWpCreateMap.put(pidcVersCocWP.getWPDivId(), updatedPidcVersCocWP);
          }
          else {
            pidcVersCocWpUpdateMap.put(pidcVersCocWP.getWPDivId(), updatedPidcVersCocWP);
          }
        }
      }
    }

    executePidcCoCWPUpdCmd(pidcVersCocWpLoader, pidcVersCocWpCreateMap, pidcVersCocWpUpdateMap, pidcVarCocWpDeletionMap,
        pidcSubVarCocWpDeletionMap);
  }


  /**
   * @param usedFlagToBeUpdated
   * @param pidcVarCocWpDeletionMap
   * @param pidcSubVarCocWpDeletionMap
   * @param pidcVersCocWpData
   * @param pidcVersCocWP
   */
  private void setChildRecToBeDeleted(final Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpDeletionMap,
      final Map<Long, Map<Long, PidcSubVarCocWp>> pidcSubVarCocWpDeletionMap, final PidcVersCocWpData pidcVersCocWpData,
      final PidcVersCocWp pidcVersCocWP) {

    // loop through variants
    for (Entry<Long, Map<Long, PidcVariantCocWp>> varNPidcVarCocWpMap : pidcVersCocWpData.getPidcVarCocWpMap()
        .entrySet()) {

      PidcVariantCocWp pidcVarCocWp = varNPidcVarCocWpMap.getValue().get(pidcVersCocWP.getWPDivId());

      Map<Long, PidcVariantCocWp> pidcVarCocWpMap = new HashMap<>();
      pidcVarCocWpMap.put(pidcVersCocWP.getWPDivId(), pidcVarCocWp);
      pidcVarCocWpDeletionMap.put(varNPidcVarCocWpMap.getKey(), pidcVarCocWpMap);

      if (pidcVarCocWp.isAtChildLevel()) {
        // loop through sub-variants
        for (Entry<Long, Map<Long, PidcSubVarCocWp>> subvarNPidcSubVarCocWpMap : pidcVersCocWpData
            .getPidcSubVarCocWpMap().entrySet()) {
          PidcSubVarCocWp pidcSubVarCocWp = subvarNPidcSubVarCocWpMap.getValue().get(pidcVersCocWP.getWPDivId());

          Map<Long, PidcSubVarCocWp> pidcSubVarCocWpMap = new HashMap<>();
          pidcSubVarCocWpMap.put(pidcVersCocWP.getWPDivId(), pidcSubVarCocWp);
          pidcSubVarCocWpDeletionMap.put(subvarNPidcSubVarCocWpMap.getKey(), pidcSubVarCocWpMap);
        }
      }
    }
  }

  /**
   * @param pidcVersCocWpLoader
   * @param pidcVersCocWpUpdateMap
   * @param pidcVersCocWpCreateMap
   * @param pidcVarCocWpDeletionMap
   * @param pidcSubVarCocWpDeletionMap
   * @throws IcdmException
   */
  private void executePidcCoCWPUpdCmd(final PidcVersCocWpLoader pidcVersCocWpLoader,
      final Map<Long, PidcVersCocWp> pidcVersCocWpCreateMap, final Map<Long, PidcVersCocWp> pidcVersCocWpUpdateMap,
      final Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpDeletionMap,
      final Map<Long, Map<Long, PidcSubVarCocWp>> pidcSubVarCocWpDeletionMap)
      throws IcdmException {

    if (CommonUtils.isNotEmpty(pidcVersCocWpUpdateMap) || CommonUtils.isNotEmpty(pidcVersCocWpCreateMap)) {
      // Set Input Model
      PIDCCocWpUpdationInputModel pidcCocWPUpdInputModel = new PIDCCocWpUpdationInputModel();
      pidcCocWPUpdInputModel.setPidcVersCocWpCreationMap(pidcVersCocWpCreateMap);
      pidcCocWPUpdInputModel.setPidcVersCocWpBeforeUpdate(pidcVersCocWpUpdateMap);
      pidcCocWPUpdInputModel.setPidcVarCocWpDeletionMap(pidcVarCocWpDeletionMap);
      pidcCocWPUpdInputModel.setPidcSubVarCocWpDeletionMap(pidcSubVarCocWpDeletionMap);

      // execute child command to update pidc vers used flag to 'Y'/'N'
      PidcCocWpUpdationCommand updateCmd = new PidcCocWpUpdationCommand(getServiceData(), pidcCocWPUpdInputModel, true);
      executeChildCommand(updateCmd);

      setPidcVersCoCWPUpdateModel(
          pidcVersCocWpLoader.createUpdationModel(updateCmd.getOutputModel(), pidcCocWPUpdInputModel));
    }
  }


  /**
   * @return the pidcVersCoCWPUpdateModel
   */
  public PIDCCocWpUpdationModel getPidcVersCoCWPUpdateModel() {
    return this.pidcVersCoCWPUpdateModel;
  }

  /**
   * @param pidcVersCoCWPUpdateModel the pidcVersCoCWPUpdateModel to set
   */
  public void setPidcVersCoCWPUpdateModel(final PIDCCocWpUpdationModel pidcVersCoCWPUpdateModel) {
    this.pidcVersCoCWPUpdateModel = pidcVersCoCWPUpdateModel;
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
    // NA
  }

}
