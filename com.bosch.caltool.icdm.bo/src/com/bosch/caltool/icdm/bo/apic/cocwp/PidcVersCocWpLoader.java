package com.bosch.caltool.icdm.bo.apic.cocwp;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcSubVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.wp.WorkPackageDivisionLoader;
import com.bosch.caltool.icdm.bo.wp.WpmlWpMasterlistLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.DataNotFoundException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackageDivision;
import com.bosch.caltool.icdm.database.entity.apic.TWpmlWpMasterlist;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectSubVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.database.entity.apic.cocwp.TPidcSubVarCocWp;
import com.bosch.caltool.icdm.database.entity.apic.cocwp.TPidcVariantCocWp;
import com.bosch.caltool.icdm.database.entity.apic.cocwp.TPidcVersCocWp;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.cocwp.PIDCCocWpUpdationInputModel;
import com.bosch.caltool.icdm.model.apic.cocwp.PIDCCocWpUpdationModel;
import com.bosch.caltool.icdm.model.apic.cocwp.PIDCCocWpUpdationOutputModel;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcCocWpExternalAPIData;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcSubVarCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVariantCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVersCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVersCocWpData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;
import com.bosch.caltool.icdm.model.wp.WpmlWpMasterlist;


/**
 * Loader class for PidcVersCocWp
 *
 * @author UKT1COB
 */
public class PidcVersCocWpLoader extends AbstractBusinessObject<PidcVersCocWp, TPidcVersCocWp> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public PidcVersCocWpLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.PIDC_VERS_COC_WP, TPidcVersCocWp.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected PidcVersCocWp createDataObject(final TPidcVersCocWp entity) throws DataException {
    PidcVersCocWp pidcVersCocWp = new PidcVersCocWp();

    setCommonFields(pidcVersCocWp, entity);

    pidcVersCocWp.setPidcVersId(entity.getTPidcVersion().getPidcVersId());

    WorkPackageDivision wrkPckgDiv =
        new WorkPackageDivisionLoader(getServiceData()).getDataObjectByID(entity.getTWrkpkgdiv().getWpDivId());
    pidcVersCocWp.setWPDivId(wrkPckgDiv.getId());
    pidcVersCocWp.setDeleted(yOrNToBoolean(wrkPckgDiv.getDeleted()));

    pidcVersCocWp.setName(wrkPckgDiv.getWpName());
    pidcVersCocWp.setDescription(wrkPckgDiv.getWpDesc());
    pidcVersCocWp.setUsedFlag(entity.getUsedFlag());
    pidcVersCocWp.setAtChildLevel(yOrNToBoolean(entity.getIsAtChildLevel()));

    return pidcVersCocWp;
  }

  public PidcCocWpExternalAPIData getCocWpForExtAPI(final Long pidcVersId) throws IcdmException {

    PidcVersionLoader pidcVersLoader = new PidcVersionLoader(getServiceData());

    if (pidcVersLoader.isHiddenToCurrentUser(pidcVersId)) {
      throw new UnAuthorizedAccessException(
          "The PIDC Version with ID " + pidcVersId + " is not accessible to current user");
    }

    PidcCocWpExternalAPIData pidcCoCWPExtAPIdata = new PidcCocWpExternalAPIData();

    // Set all Common Fields
    setPidcVersCocWpDataFields(pidcVersId, pidcCoCWPExtAPIdata, true);

    // Set PidcVersion
    pidcCoCWPExtAPIdata.setPidcVersion(pidcVersLoader.getDataObjectByID(pidcVersId));

    // Set Pidc Variant Map
    pidcCoCWPExtAPIdata.setProjectVariantMap(new PidcVariantLoader(getServiceData()).getVariants(pidcVersId, true));

    // Set Pidc Sub Variant Map
    pidcCoCWPExtAPIdata
        .setProjectSubVariantMap(new PidcSubVariantLoader(getServiceData()).getSubVariantsForVersion(pidcVersId, true));

    // set WPMLMasterList Map
    setWpmlMasterListMap(pidcCoCWPExtAPIdata);

    return pidcCoCWPExtAPIdata;
  }

  /**
   * Get all CoC WP details
   *
   * @param pidcVersId pidc version Id
   * @return pidcVersCoCWPdata wrapper
   * @throws IcdmException exception while invoking the method
   */
  public PidcVersCocWpData getAllCocWpByPidcVersId(final Long pidcVersId) throws IcdmException {

    PidcVersCocWpData pidcVersCoCWPdata = new PidcVersCocWpData();

    setPidcVersCocWpDataFields(pidcVersId, pidcVersCoCWPdata, false);

    return pidcVersCoCWPdata;
  }

  /**
   * @param pidcVersId
   * @param pidcVersCoCWPdata
   * @param isForExternalAPI
   * @throws InvalidInputException
   * @throws IcdmException
   * @throws DataNotFoundException
   * @throws DataException
   */
  private void setPidcVersCocWpDataFields(final Long pidcVersId, final PidcVersCocWpData pidcVersCoCWPdata,
      final boolean isForExternalAPI)
      throws IcdmException {

    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
    TPidcVersion pidcVers = pidcVersionLoader.getEntityObject(pidcVersId);

    // checking the input pidc Vers Id is valid
    pidcVersionLoader.validateId(pidcVersId);

    // set Division Value Id
    Long divValId = pidcVersionLoader.getDivIdByPidcVersId(pidcVersId);
    if (divValId == null) {
      throw new DataNotFoundException(
          "Division attribute value is not filled for the PIDC version '" + pidcVersId + "'");
    }

    // Set WorkPackage Div Map - Key - WP div Id , Value - WorkpackageDivision
    setPidcVersCocWp(pidcVersId, pidcVersCoCWPdata, divValId, pidcVers, isForExternalAPI);

    // set DefinedPidcVarCocWp Map
    setDefinedPidcVarCocWp(pidcVersCoCWPdata, pidcVers);

    // set DefinedPidcSubVarCocWp Map
    setDefinedPidcSubVarCocWP(pidcVersCoCWPdata, pidcVers);
  }

  /**
   * @param pidcVersCoCWPdata
   * @param wrkPkgLoader
   * @throws DataException
   */
  private void setWpmlMasterListMap(final PidcCocWpExternalAPIData pidcCoCWPExtAPIdata) throws DataException {

    WpmlWpMasterlistLoader wpMasterListLoader = new WpmlWpMasterlistLoader(getServiceData());
    Map<Long, WpmlWpMasterlist> pidcVersWpmlMasterListMap = new HashMap<>();
    WorkPackageDivisionLoader wrkPkgLoader = new WorkPackageDivisionLoader(getServiceData());

    for (PidcVersCocWp pidcVersCocWpObj : pidcCoCWPExtAPIdata.getPidcVersCocWpMap().values()) {
      Long wpDivId = pidcVersCocWpObj.getWPDivId();
      TWpmlWpMasterlist gettWpmlWpMasterList =
          wrkPkgLoader.getEntityObject(wpDivId).getTWorkpackage().gettWpmlWpMasterList();
      if (CommonUtils.isNotNull(gettWpmlWpMasterList)) {
        long wpMasterListId = gettWpmlWpMasterList.getId();
        WpmlWpMasterlist dataObjectByID = wpMasterListLoader.getDataObjectByID(wpMasterListId);
        pidcVersWpmlMasterListMap.put(wpDivId, dataObjectByID);
      }

    }

    pidcCoCWPExtAPIdata.setWpmlMasterListMap(pidcVersWpmlMasterListMap);
  }


  /**
   * @param pidcVersId
   * @param pidcVersCoCWPdata
   * @param pidcVers
   * @return
   * @throws DataException
   */
  private Map<Long, PidcVersCocWp> getDefinedPidcVersCocWpMap(final TPidcVersion pidcVers) throws DataException {

    Map<Long, PidcVersCocWp> definedPidcVersCocWpMap = new HashMap<>();
    for (TPidcVersCocWp tpidcVersCocWp : pidcVers.gettPidcVersCocWp()) {
      definedPidcVersCocWpMap.put(tpidcVersCocWp.getTWrkpkgdiv().getWpDivId(),
          getDataObjectByID(tpidcVersCocWp.getPidcVersCocWpId()));
    }

    return definedPidcVersCocWpMap;
  }

  /**
   * @param pidcVersId
   * @param pidcVersCoCWPdata
   * @param divValId
   * @param pidcVers
   * @param isForExternalAPI
   * @param definedPidcVersCocWpMap
   * @param wrkPkgLoader
   * @throws DataException
   */
  private void setPidcVersCocWp(final Long pidcVersId, final PidcVersCocWpData pidcVersCoCWPdata, final Long divValId,
      final TPidcVersion pidcVers, final boolean isForExternalAPI)
      throws DataException {

    // Creating map that contains data for UI with virtual and defined values
    TypedQuery<TWorkpackageDivision> wrkPkgDivQuery =
        getEntMgr().createNamedQuery(TWorkpackageDivision.NQ_FIND_BY_DIV_AND_MASTERLIST_ID, TWorkpackageDivision.class);
    wrkPkgDivQuery.setParameter("divValueId", divValId);

    // Set WrkPkgDivMap
    Map<Long, WorkPackageDivision> wrkPkgDivMap = new HashMap<>();
    Map<Long, PidcVersCocWp> wpDivPidcVersCocWpMap = new HashMap<>();
    WorkPackageDivisionLoader wrkPkgLoader = new WorkPackageDivisionLoader(getServiceData());
    // set DefinedPidcVersCocWp Map
    Map<Long, PidcVersCocWp> definedPidcVersCocWpMap = getDefinedPidcVersCocWpMap(pidcVers);

    for (TWorkpackageDivision tWrkPkgDiv : wrkPkgDivQuery.getResultList()) {
      WorkPackageDivision wrkPkgDiv = wrkPkgLoader.getDataObjectByID(tWrkPkgDiv.getWpDivId());
      Long wrkPkgDivId = wrkPkgDiv.getId();
      wrkPkgDivMap.put(wrkPkgDivId, wrkPkgDiv);

      if (!isForExternalAPI) {
        pidcVersCoCWPdata.setWrkPkgDivMap(wrkPkgDivMap);
      }
      // set PidcVersCocWp
      if (CommonUtils.isNotEmpty(definedPidcVersCocWpMap) && definedPidcVersCocWpMap.containsKey(wrkPkgDivId)) {
        wpDivPidcVersCocWpMap.put(wrkPkgDivId, definedPidcVersCocWpMap.get(wrkPkgDivId));
      }
      else {
        PidcVersCocWp pidcVersCocWp = new PidcVersCocWp();
        pidcVersCocWp.setPidcVersId(pidcVersId);
        pidcVersCocWp.setWPDivId(wrkPkgDiv.getId());
        pidcVersCocWp.setName(wrkPkgDiv.getWpName());
        pidcVersCocWp.setDescription(wrkPkgDiv.getWpDesc());
        pidcVersCocWp.setAtChildLevel(false);
        pidcVersCocWp.setDeleted(CommonUtils.getBooleanType(wrkPkgDiv.getDeleted()));
        wpDivPidcVersCocWpMap.put(wrkPkgDivId, pidcVersCocWp);
      }
    }

    pidcVersCoCWPdata.setPidcVersCocWpMap(wpDivPidcVersCocWpMap);
  }


  /**
   * @param pidcVersCoCWPdata
   * @param pidcVers
   * @throws DataException
   */
  private void setDefinedPidcVarCocWp(final PidcVersCocWpData pidcVersCoCWPdata, final TPidcVersion pidcVers)
      throws DataException {

    PidcVariantCocWpLoader varLoader = new PidcVariantCocWpLoader(getServiceData());

    Map<Long, Map<Long, PidcVariantCocWp>> definedPidcVarCocWpMap = new HashMap<>();
    for (TabvProjectVariant pidcVar : pidcVers.getTabvProjectVariants()) {
      for (TPidcVariantCocWp tpidcVarCocWp : pidcVar.gettPidcVarCocWp()) {
        Long varId = tpidcVarCocWp.getTabvprojvar().getVariantId();
        if (definedPidcVarCocWpMap.containsKey(varId)) {
          definedPidcVarCocWpMap.get(varId).put(tpidcVarCocWp.getTwrkpkgdiv().getWpDivId(),
              varLoader.createDataObject(tpidcVarCocWp));
        }
        else {
          Map<Long, PidcVariantCocWp> varCocWpMap = new HashMap<>();
          varCocWpMap.put(tpidcVarCocWp.getTwrkpkgdiv().getWpDivId(), varLoader.createDataObject(tpidcVarCocWp));
          definedPidcVarCocWpMap.put(varId, varCocWpMap);
        }
      }
    }

    pidcVersCoCWPdata.setPidcVarCocWpMap(definedPidcVarCocWpMap);
  }

  /**
   * @param pidcVersId
   * @param pidcVersCoCWPdata
   * @throws DataException
   */
  private void setDefinedPidcSubVarCocWP(final PidcVersCocWpData pidcVersCoCWPdata, final TPidcVersion pidcVers)
      throws DataException {

    PidcSubVarCocWpLoader subVarLoader = new PidcSubVarCocWpLoader(getServiceData());

    Map<Long, Map<Long, PidcSubVarCocWp>> definedPidcSubVarCocWpMap = new HashMap<>();
    for (TabvProjectSubVariant tabvProjSubVar : pidcVers.getTabvProjectSubVariants()) {
      for (TPidcSubVarCocWp tpidcSubVarCocWp : tabvProjSubVar.gettPidcSubVarCocWp()) {
        Long subvarId = tpidcSubVarCocWp.getTabvprojsubvar().getSubVariantId();
        if (definedPidcSubVarCocWpMap.containsKey(subvarId)) {
          definedPidcSubVarCocWpMap.get(subvarId).put(tpidcSubVarCocWp.getTwrkpkgdiv().getWpDivId(),
              subVarLoader.createDataObject(tpidcSubVarCocWp));
        }
        else {
          Map<Long, PidcSubVarCocWp> subvarCocWpMap = new HashMap<>();
          subvarCocWpMap.put(tpidcSubVarCocWp.getTwrkpkgdiv().getWpDivId(),
              subVarLoader.createDataObject(tpidcSubVarCocWp));
          definedPidcSubVarCocWpMap.put(subvarId, subvarCocWpMap);
        }
      }
    }

    pidcVersCoCWPdata.setPidcSubVarCocWpMap(definedPidcSubVarCocWpMap);
  }

  /**
   * @param outputModel
   * @param pidcCocWPUpdInputModel
   * @return
   * @throws DataException
   */
  public PIDCCocWpUpdationModel createUpdationModel(final PIDCCocWpUpdationOutputModel outputModel,
      final PIDCCocWpUpdationInputModel pidcCocWPUpdInputModel)
      throws DataException {

    PIDCCocWpUpdationModel pidcCocWpUpdationModel = new PIDCCocWpUpdationModel();

    // setPidcVersCocWpCreationMap
    pidcCocWpUpdationModel.setPidcVersCocWpCreationMap(
        getCreatedOrUpdatedVersCocWpMap(outputModel.getListOfNewlyCreatedPidcVersCocWpIds()));

    // setPidcVersCocWpBeforeUpdate
    pidcCocWpUpdationModel.setPidcVersCocWpBeforeUpdate(pidcCocWPUpdInputModel.getPidcVersCocWpBeforeUpdate());

    // setPidcVersCocWpAfterUpdate
    pidcCocWpUpdationModel
        .setPidcVersCocWpAfterUpdate(getCreatedOrUpdatedVersCocWpMap(outputModel.getListOfUpdatedPidcVersCocWpIds()));

    // setPidcVarCocWpCreationMap
    pidcCocWpUpdationModel.setPidcVarCocWpCreationMap(
        getCreatedOrUpdatedPidcVarCocWpMap(outputModel.getListOfNewlyCreatedPidcVarCocWpIds()));

    // setPidcVarCocWpMapBeforeUpdate
    pidcCocWpUpdationModel.setPidcVarCocWpMapBeforeUpdate(pidcCocWPUpdInputModel.getPidcVarCocWpMapBeforeUpdate());

    // setPidcVarCocWpAfterUpdate
    pidcCocWpUpdationModel.setPidcVarCocWpMapAfterUpdate(
        getCreatedOrUpdatedPidcVarCocWpMap(outputModel.getListOfUpdatedPidcVarCocWpIds()));

    // setPidcVarCocWpDeletionMap
    pidcCocWpUpdationModel.setPidcVarCocWpDeletionMap(pidcCocWPUpdInputModel.getPidcVarCocWpDeletionMap());

    // setPidcSubVarCocWpCreationMap
    pidcCocWpUpdationModel.setPidcSubVarCocWpCreationMap(
        getCreatedOrUpdatedPidcSubVarCocWpMap(outputModel.getListOfNewlyCreatedPidcSubVarCocWpIds()));

    // setPidcSubVarCocWpBeforeUpdateMap
    pidcCocWpUpdationModel
        .setPidcSubVarCocWpBeforeUpdateMap(pidcCocWPUpdInputModel.getPidcSubVarCocWpBeforeUpdateMap());

    // setPidcSubVarCocWpAfterUpdateMap
    pidcCocWpUpdationModel.setPidcSubVarCocWpAfterUpdateMap(
        getCreatedOrUpdatedPidcSubVarCocWpMap(outputModel.getListOfUpdatedPidcSubVarCocWpIds()));

    // setPidcSubVarCocWpDeletionMap
    pidcCocWpUpdationModel.setPidcSubVarCocWpDeletionMap(pidcCocWPUpdInputModel.getPidcSubVarCocWpDeletionMap());

    pidcCocWpUpdationModel.setPidcVersion(getCreateOrUpdatedPidcVersion(pidcCocWpUpdationModel));
    pidcCocWpUpdationModel.setPidcVariantMap(getCreateOrUpdatePidcVarMap(pidcCocWpUpdationModel));
    pidcCocWpUpdationModel.setPidcSubVariantMap(getCreateOrUpdatePidcSubVarMap(pidcCocWpUpdationModel));
    pidcCocWpUpdationModel.setListOfDelUcFav(outputModel.getListOfDelUcFav());
    pidcCocWpUpdationModel.setListOfNewlyCreatedUcFav(outputModel.getListOfNewlyCreatedUCFav());

    return pidcCocWpUpdationModel;
  }


  private PidcVersion getCreateOrUpdatedPidcVersion(final PIDCCocWpUpdationModel pidcCocWpUpdationModel)
      throws DataException {
    PidcVersion pidcVersion = null;
    Long pidcVerId = 0l;
    Optional<Entry<Long, PidcVersCocWp>> pidcVersCocCreationFindFirst =
        pidcCocWpUpdationModel.getPidcVersCocWpCreationMap().entrySet().stream().findFirst();
    Optional<Entry<Long, PidcVersCocWp>> pidcVersCocUpdateFindFirst =
        pidcCocWpUpdationModel.getPidcVersCocWpBeforeUpdate().entrySet().stream().findFirst();

    Optional<Entry<Long, Map<Long, PidcVariantCocWp>>> pidcVarCocCreationFindFirst =
        pidcCocWpUpdationModel.getPidcVarCocWpCreationMap().entrySet().stream().findFirst();
    Optional<Entry<Long, Map<Long, PidcVariantCocWp>>> pidcVarCocUpdateFindFirst =
        pidcCocWpUpdationModel.getPidcVarCocWpMapBeforeUpdate().entrySet().stream().findFirst();

    Optional<Entry<Long, Map<Long, PidcSubVarCocWp>>> pidcVarSubCocCreationFindFirst =
        pidcCocWpUpdationModel.getPidcSubVarCocWpCreationMap().entrySet().stream().findFirst();
    Optional<Entry<Long, Map<Long, PidcSubVarCocWp>>> pidcVarSubCocUpdateFindFirst =
        pidcCocWpUpdationModel.getPidcSubVarCocWpBeforeUpdateMap().entrySet().stream().findFirst();


    if (pidcVersCocCreationFindFirst.isPresent()) {
      pidcVerId = pidcVersCocCreationFindFirst.get().getValue().getPidcVersId();
    }
    if (pidcVersCocUpdateFindFirst.isPresent() && (pidcVerId.equals(0l))) {
      pidcVerId = pidcVersCocUpdateFindFirst.get().getValue().getPidcVersId();
    }

    if (pidcVarCocCreationFindFirst.isPresent()) {
      Long pidcVarId = pidcVarCocCreationFindFirst.get().getKey();
      pidcVerId = new PidcVariantLoader(getServiceData()).getDataObjectByID(pidcVarId).getPidcVersionId();
    }
    if (pidcVarCocUpdateFindFirst.isPresent()) {
      Long pidcVarId = pidcVarCocUpdateFindFirst.get().getKey();
      pidcVerId = new PidcVariantLoader(getServiceData()).getDataObjectByID(pidcVarId).getPidcVersionId();
    }

    if (pidcVarSubCocCreationFindFirst.isPresent()) {
      Long pidcVarSubId = pidcVarSubCocCreationFindFirst.get().getKey();
      pidcVerId = new PidcSubVariantLoader(getServiceData()).getDataObjectByID(pidcVarSubId).getPidcVersionId();
    }
    if (pidcVarSubCocUpdateFindFirst.isPresent()) {
      Long pidcVarSubId = pidcVarSubCocUpdateFindFirst.get().getKey();
      pidcVerId = new PidcSubVariantLoader(getServiceData()).getDataObjectByID(pidcVarSubId).getPidcVersionId();
    }

    if (!pidcVerId.equals(0l)) {
      pidcVersion = new PidcVersionLoader(getServiceData()).getDataObjectByID(pidcVerId);
    }
    return pidcVersion;
  }

  private Map<Long, PidcVariant> getCreateOrUpdatePidcVarMap(final PIDCCocWpUpdationModel pidcCocWpUpdationModel)
      throws DataException {
    PidcVariantLoader pidcVariantLoader = new PidcVariantLoader(getServiceData());
    Map<Long, PidcVariant> pidcVarMap = new HashMap<>();
    for (Long pidcVarId : pidcCocWpUpdationModel.getPidcVarCocWpCreationMap().keySet()) {
      pidcVarMap.put(pidcVarId, pidcVariantLoader.getDataObjectByID(pidcVarId));
    }
    for (Long pidcVarId : pidcCocWpUpdationModel.getPidcVarCocWpMapAfterUpdate().keySet()) {
      pidcVarMap.put(pidcVarId, pidcVariantLoader.getDataObjectByID(pidcVarId));
    }
    for (Long pidcVarId : pidcCocWpUpdationModel.getPidcVarCocWpDeletionMap().keySet()) {
      pidcVarMap.put(pidcVarId, pidcVariantLoader.getDataObjectByID(pidcVarId));
    }
    return pidcVarMap;
  }


  private Map<Long, PidcSubVariant> getCreateOrUpdatePidcSubVarMap(final PIDCCocWpUpdationModel pidcCocWpUpdationModel)
      throws DataException {
    PidcSubVariantLoader pidcSubVariantLoader = new PidcSubVariantLoader(getServiceData());
    Map<Long, PidcSubVariant> pidcSubVarMap = new HashMap<>();
    for (Long pidcSubVarId : pidcCocWpUpdationModel.getPidcSubVarCocWpCreationMap().keySet()) {
      pidcSubVarMap.put(pidcSubVarId, pidcSubVariantLoader.getDataObjectByID(pidcSubVarId));
    }
    for (Long pidcSubVarId : pidcCocWpUpdationModel.getPidcSubVarCocWpAfterUpdateMap().keySet()) {
      pidcSubVarMap.put(pidcSubVarId, pidcSubVariantLoader.getDataObjectByID(pidcSubVarId));
    }
    for (Long pidcSubVarId : pidcCocWpUpdationModel.getPidcSubVarCocWpDeletionMap().keySet()) {
      pidcSubVarMap.put(pidcSubVarId, pidcSubVariantLoader.getDataObjectByID(pidcSubVarId));
    }
    return pidcSubVarMap;
  }

  /**
   * @param inputlist
   * @param pidcVersCocWpLoader
   * @return
   * @throws DataException
   */
  private Map<Long, PidcVersCocWp> getCreatedOrUpdatedVersCocWpMap(final List<Long> inputlist) throws DataException {

    PidcVersCocWpLoader pidcVersCocWpLoader = new PidcVersCocWpLoader(getServiceData());

    Map<Long, PidcVersCocWp> pidcVersCocWpCreatedOrUpdatedMap = new HashMap<>();
    for (Long CreatedOrUpdatedPidcVersCocWpId : inputlist) {

      PidcVersCocWp pidcVersCocWp = pidcVersCocWpLoader.getDataObjectByID(CreatedOrUpdatedPidcVersCocWpId);
      pidcVersCocWpCreatedOrUpdatedMap.put(pidcVersCocWp.getWPDivId(), pidcVersCocWp);
    }
    return pidcVersCocWpCreatedOrUpdatedMap;
  }

  /**
   * @param inputList of pidcVarCocWp ids
   * @return
   * @throws DataException
   */
  private Map<Long, Map<Long, PidcVariantCocWp>> getCreatedOrUpdatedPidcVarCocWpMap(final List<Long> inputList)
      throws DataException {
    PidcVariantCocWpLoader pidcVariantCocWpLoader = new PidcVariantCocWpLoader(getServiceData());
    Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpCreatOrUpdMap = new HashMap<>();
    Long varId = null;
    for (Long createdOrUpdidcVarCocWpId : inputList) {
      Map<Long, PidcVariantCocWp> pidcVarCocWpWpDivIdMap = new HashMap<>();
      PidcVariantCocWp pidcVarCocWp = pidcVariantCocWpLoader.getDataObjectByID(createdOrUpdidcVarCocWpId);
      pidcVarCocWpWpDivIdMap.put(pidcVarCocWp.getWPDivId(), pidcVarCocWp);

      varId = pidcVarCocWp.getPidcVariantId();
      if (pidcVarCocWpCreatOrUpdMap.containsKey(varId)) {
        pidcVarCocWpCreatOrUpdMap.get(varId).putAll(pidcVarCocWpWpDivIdMap);
      }
      else {
        pidcVarCocWpCreatOrUpdMap.put(varId, pidcVarCocWpWpDivIdMap);
      }
    }
    return pidcVarCocWpCreatOrUpdMap;
  }

  /**
   * @param inputList
   * @return
   * @throws DataException
   */
  private Map<Long, Map<Long, PidcSubVarCocWp>> getCreatedOrUpdatedPidcSubVarCocWpMap(final List<Long> inputList)
      throws DataException {
    Map<Long, Map<Long, PidcSubVarCocWp>> pidcSubVarCocWpCreatOrUpdMap = new HashMap<>();
    Long subVarId = null;
    for (Long createdOrUpdidcVarCocWpId : inputList) {
      Map<Long, PidcSubVarCocWp> pidcSubVarCocWpWpDivIdMap = new HashMap<>();
      PidcSubVarCocWp pidcSubVarCocWp =
          new PidcSubVarCocWpLoader(getServiceData()).getDataObjectByID(createdOrUpdidcVarCocWpId);
      pidcSubVarCocWpWpDivIdMap.put(pidcSubVarCocWp.getWPDivId(), pidcSubVarCocWp);

      subVarId = pidcSubVarCocWp.getPidcSubVarId();
      if (pidcSubVarCocWpCreatOrUpdMap.containsKey(subVarId)) {
        pidcSubVarCocWpCreatOrUpdMap.get(subVarId).putAll(pidcSubVarCocWpWpDivIdMap);
      }
      else {
        pidcSubVarCocWpCreatOrUpdMap.put(subVarId, pidcSubVarCocWpWpDivIdMap);
      }
    }
    return pidcSubVarCocWpCreatOrUpdMap;
  }

}
