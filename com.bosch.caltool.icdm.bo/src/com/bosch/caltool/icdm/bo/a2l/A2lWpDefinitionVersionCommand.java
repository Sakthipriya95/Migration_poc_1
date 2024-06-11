package com.bosch.caltool.icdm.bo.a2l;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpDefnVersion;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpParamMapping;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResponsibility;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResponsibilityStatus;
import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;
import com.bosch.caltool.icdm.model.a2l.A2LDetailsStructureModel;
import com.bosch.caltool.icdm.model.a2l.A2lVarGrpVariantMapping;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibilityStatus;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.WP_RESP_STATUS_TYPE;


/**
 * Command class for A2lWpDefinitionVersion
 *
 * @author pdh2cob
 */
public class A2lWpDefinitionVersionCommand extends AbstractCommand<A2lWpDefnVersion, A2lWpDefnVersionLoader> {

  private boolean isWpForCopyMappings;
  /**
   * Used for copy command to copy the mappings from old working set
   */
  private A2lWpDefnVersion oldWpDefWS;
  private boolean newVersionProc;
  private boolean isUpdateFinishedStatus;
  private long prevWpDefActiveVers;
  private long newWpDefActiveVers;
  /**
   * A2l WP Definition Version from which WP Finished Status should be taken over
   */
  private A2lWpDefnVersion wpDefnVersForFinStatusTakeOver;

  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @param isWpForCopyMappings true if command is called from take par2wp from another a2l
   * @throws IcdmException error when initializing
   */
  public A2lWpDefinitionVersionCommand(final ServiceData serviceData, final A2lWpDefnVersion input,
      final boolean isUpdate, final boolean isDelete, final boolean isWpForCopyMappings) throws IcdmException {
    super(serviceData, input, new A2lWpDefnVersionLoader(serviceData), resolveCommandModeA(isDelete, isUpdate));
    this.isWpForCopyMappings = isWpForCopyMappings;
  }


  /**
   * Constructor for updating finished status of A2lWp Definition version
   *
   * @param serviceData service Data
   * @param prevWpDefActiveVers old wpDefActive version
   * @param newWpDefActiveVers new wpDefActive version
   * @throws IcdmException error when initializing
   */
  public A2lWpDefinitionVersionCommand(final ServiceData serviceData, final long prevWpDefActiveVers,
      final A2lWpDefnVersion newWpDefActiveVers) throws IcdmException {
    super(serviceData, newWpDefActiveVers, new A2lWpDefnVersionLoader(serviceData), COMMAND_MODE.UPDATE);
    this.isUpdateFinishedStatus = true;
    this.prevWpDefActiveVers = prevWpDefActiveVers;
    this.newWpDefActiveVers = newWpDefActiveVers.getId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TA2lWpDefnVersion entity = new TA2lWpDefnVersion();
    setValuesToEntity(entity, true);
    setUserDetails(COMMAND_MODE.CREATE, entity);
    persistEntity(entity);
    getEm().flush();
    refreshEntity();

    // when par2wp is to be taken from another a2l , create only wp def version without the var groups and mappings
    if (!this.isWpForCopyMappings) {
      createNewA2lWpDefinitionVersion(entity);
    }

    updatePidcA2l(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    // For updating finished status only
    if (this.isUpdateFinishedStatus) {
      updateWpStatusForNewActiveWPDefVers();
    }
    else {
      TA2lWpDefnVersion entity = new A2lWpDefnVersionLoader(getServiceData()).getEntityObject(getInputData().getId());
      // for copy mappings from a2l update , set the master refresh applicable
      setMasterRefreshApplicable(this.isWpForCopyMappings);
      setValuesToEntity(entity, false);
      setUserDetails(COMMAND_MODE.UPDATE, entity);
      persistEntity(entity);
    }
  }

  /**
   * Update the pidc working set modified flag , if there is new change
   *
   * @param entity
   */
  private void updatePidcA2l(final TA2lWpDefnVersion entity) throws IcdmException {
    PidcA2lLoader pidcA2lLoader = new PidcA2lLoader(getServiceData());
    PidcA2l pidcA2l = pidcA2lLoader.getDataObjectByID(entity.getTPidcA2l().getPidcA2lId());
    if (pidcA2l.isWorkingSetModified()) {
      pidcA2l.setWorkingSetModified(false);
      PidcA2lCommand pidcA2lCmd = new PidcA2lCommand(getServiceData(), pidcA2l, true, true, false);
      executeChildCommand(pidcA2lCmd);
    }
  }

  /**
   * Method to set values to entity object
   *
   * @param entity
   */
  private void setValuesToEntity(final TA2lWpDefnVersion entity, final boolean isCreate) {
    TPidcA2l tPidcA2l = new PidcA2lLoader(getServiceData()).getEntityObject(getInputData().getPidcA2lId());
    if (this.isWpForCopyMappings) {
      entity.setVersionNumber(getInputData().getVersionNumber());
    }
    else {
      if (isCreate) {
        if (getInputData().isWorkingSet()) {
          entity.setVersionNumber(0L);
        }
        else {
          // Version number is the next available integer.
          long versionNumber = tPidcA2l.gettA2lWpDefnVersions().size();
          entity.setVersionNumber(versionNumber);
        }
      }
    }
    if (isCreate) {
      // Also setting child to parent
      tPidcA2l.gettA2lWpDefnVersions().add(entity);

    }

    entity.setVersionName(getInputData().getVersionName());
    entity.setVersionDesc(getInputData().getDescription());
    entity.setIsActive(booleanToYorN(getInputData().isActive()));
    if (isCreate || getInputData().isActive()) {
      entity.setIsAnytimeActiveFlag(entity.getIsActive());
    }
    // set pidc a2l entity object
    entity.setTPidcA2l(tPidcA2l);

    entity.setParamLevelChgAllowedFlag(booleanToYorN(getInputData().isParamLevelChgAllowedFlag()));


  }


  /**
   * Create variant groups, var grp mappings, resp pal, param mapping objects
   *
   * @param ta2lWpDefnVers created TA2lWpDefinitionVersion
   * @throws IcdmException IcdmException
   */
  public void createNewA2lWpDefinitionVersion(final TA2lWpDefnVersion ta2lWpDefnVers) throws IcdmException {

    A2lWpDefnVersionLoader a2lWpDefinitionVersionLoader = new A2lWpDefnVersionLoader(getServiceData());
    A2lWpDefnVersion a2lWpDefnVers = a2lWpDefinitionVersionLoader.getDataObjectByID(ta2lWpDefnVers.getWpDefnVersId());
    Map<Long, A2lWpDefnVersion> wpDefnVersMap =
        a2lWpDefinitionVersionLoader.getWPDefnVersionsForPidcA2lId(a2lWpDefnVers.getPidcA2lId());

    // in case of copy 'overwrite only default mapping' we need to create a copy of working set
    A2lWpDefnVersion workingSet =
        null == this.oldWpDefWS ? a2lWpDefinitionVersionLoader.getWorkingSet(wpDefnVersMap) : this.oldWpDefWS;

    if (this.newVersionProc) {
      final Query query = getEm().createNamedQuery(TA2lWpDefnVersion.NNS_INSERT_A2L_WPDETAILS);
      query.setParameter(1, workingSet.getId());
      query.setParameter(2, a2lWpDefnVers.getId());
      query.setParameter(3, a2lWpDefnVers.getCreatedUser());
      query.executeUpdate();
      refreshEntity();
    }
    else {
      // create a2l variant groups
      Map<String, A2lVariantGroup> newVarGrps = createNewA2lVarGroups(a2lWpDefnVers, workingSet);

      // create var grp mappings
      A2lVariantGroupLoader varGrpLoader = new A2lVariantGroupLoader(getServiceData());
      Map<Long, A2lVariantGroup> workingSetVarGrpMap = varGrpLoader.getA2LVarGrps(workingSet.getId());
      copyA2lVarGrpVarMapping(workingSetVarGrpMap, newVarGrps);

      // create a2l wp resp pal
      Map<Long, A2lWpResponsibility> newA2lWpRespPalMap = createA2lWpRespPal(workingSet, a2lWpDefnVers, newVarGrps);

      // create a2l param mapping
      createA2lWpParamMapping(workingSet, newA2lWpRespPalMap);
    }

    if (!a2lWpDefnVers.isWorkingSet()) {
      final Query query = getEm().createNamedQuery(TA2lWpDefnVersion.PRC_INSERT_WP_FINISHED_STATUS);
      A2lWpDefnVersion srcWpDefnVers = getWpDefnVersForFinStatusTakeOver();
      query.setParameter(1, CommonUtils.isNotNull(srcWpDefnVers) ? srcWpDefnVers.getId() : null);// Src Wp Defn
                                                                                                 // Version
                                                                                                 // Id
      query.setParameter(2, a2lWpDefnVers.getId());// New Wp defn version Id
      query.executeUpdate();
      getEm().refresh(ta2lWpDefnVers.getTPidcA2l().getTabvProjectidcard());
    }
  }


  /**
   * @param a2lWpDefnVersion
   * @param workingSet
   * @throws IcdmException
   * @throws DataException
   */
  private void createA2lWpRespStatusForNewWpDefVers(final A2lWpDefnVersion a2lWpDefnVersion) throws IcdmException {
    A2LDetailsStructureModel detailsModel =
        new A2lWpDefnVersionLoader(getServiceData()).getDetailsModel(a2lWpDefnVersion.getId(), false);

    Map<Long, Set<Long>> wpRespInheritedRespMap = getwpParamMapByInheritedFlag(a2lWpDefnVersion.getId(), "N");
    Map<Long, A2lWpResponsibility> srcA2lWpRespMapForWpDefVers = new HashMap<>();
    if (CommonUtils.isNotNull(getWpDefnVersForFinStatusTakeOver())) {
      srcA2lWpRespMapForWpDefVers = new A2lWpResponsibilityLoader(getServiceData())
          .getA2lWpRespMapForWpDefVers(getWpDefnVersForFinStatusTakeOver().getId());
    }
    Map<Long, A2lWpResponsibility> destA2lWpRespMapForWpDefVers =
        new A2lWpResponsibilityLoader(getServiceData()).getA2lWpRespMapForWpDefVers(a2lWpDefnVersion.getId());
    for (Entry<Long, A2lWpResponsibility> a2lWPRespEntrySet : destA2lWpRespMapForWpDefVers.entrySet()) {
      // create entry in wp resp status table for the newly created workpackage responsibility based on the variant
      // group information
      A2lWpResponsibility a2lWpResponsibility = a2lWPRespEntrySet.getValue();
      Set<A2lWpResponsibilityStatus> srcA2lWpRespStatusSet =
          findMatchingWpRespStatusSetForWpResp(srcA2lWpRespMapForWpDefVers, a2lWpResponsibility);
      createA2lWpRespStatusBasedOnValidation(detailsModel, a2lWpResponsibility,
          getSrcVarParA2lRespStatusMap(srcA2lWpRespStatusSet), wpRespInheritedRespMap);
    }

  }

  /*
   * Create Map with Variant Id as key and value as <Map of a2lRespId as Key and A2lWpResponsibilityStatus as value>
   */
  private Map<Long, Map<Long, A2lWpResponsibilityStatus>> getSrcVarParA2lRespStatusMap(
      final Set<A2lWpResponsibilityStatus> srcA2lWpRespStatusSet) {

    Map<Long, Map<Long, A2lWpResponsibilityStatus>> srcVarParA2lRespStatusMap = new HashMap<>();

    for (A2lWpResponsibilityStatus a2lWpResponsibilityStatus : srcA2lWpRespStatusSet) {
      Long variantId = a2lWpResponsibilityStatus.getVariantId() != null ? a2lWpResponsibilityStatus.getVariantId()
          : ApicConstants.NO_VARIANT_ID;

      if (srcVarParA2lRespStatusMap.containsKey(variantId)) {
        Map<Long, A2lWpResponsibilityStatus> parA2lRespStatusMap = srcVarParA2lRespStatusMap.get(variantId);
        parA2lRespStatusMap.put(a2lWpResponsibilityStatus.getA2lRespId(), a2lWpResponsibilityStatus);
        srcVarParA2lRespStatusMap.put(variantId, parA2lRespStatusMap);
      }
      else {
        Map<Long, A2lWpResponsibilityStatus> parA2lRespStatusMap = new HashMap<>();
        parA2lRespStatusMap.put(a2lWpResponsibilityStatus.getA2lRespId(), a2lWpResponsibilityStatus);
        srcVarParA2lRespStatusMap.put(variantId, parA2lRespStatusMap);
      }
    }

    return srcVarParA2lRespStatusMap;
  }


  /**
   * @param detailsModel
   * @param a2lWpResponsibility
   * @param map
   * @param wpRespInheritedRespMap
   * @throws IcdmException
   */
  private void createA2lWpRespStatusBasedOnValidation(final A2LDetailsStructureModel detailsModel,
      final A2lWpResponsibility a2lWpResponsibility,
      final Map<Long, Map<Long, A2lWpResponsibilityStatus>> srcVarParA2lRespStatusMap,
      final Map<Long, Set<Long>> wpRespInheritedRespMap)
      throws IcdmException {
    if (CommonUtils.isNotEmpty(detailsModel.getA2lMappedVariantsMap())) {
      if (CommonUtils.isNotNull(a2lWpResponsibility.getVariantGrpId()) &&
          detailsModel.getMappedVariantsMap().containsKey(a2lWpResponsibility.getVariantGrpId())) {
        List<PidcVariant> pidcVariants = detailsModel.getMappedVariantsMap().get(a2lWpResponsibility.getVariantGrpId());
        for (PidcVariant pidcVariant : pidcVariants) {
          createA2LWpRespStatus(a2lWpResponsibility, pidcVariant, srcVarParA2lRespStatusMap, wpRespInheritedRespMap);
        }
      }
      else if (CommonUtils.isNull(a2lWpResponsibility.getVariantGrpId())) {
        for (PidcVariant pidcVariant : detailsModel.getUnmappedVariants()) {
          createA2LWpRespStatus(a2lWpResponsibility, pidcVariant, srcVarParA2lRespStatusMap, wpRespInheritedRespMap);
        }
      }
    }
    else {
      createA2LWpRespStatus(a2lWpResponsibility, null, srcVarParA2lRespStatusMap, wpRespInheritedRespMap);
    }
  }


  /**
   * @param a2lWpResponsibilityStatusLoader
   * @param srcA2lWpRespMapForWpDefVers
   * @param a2lWpResponsibility
   * @param srcA2lWpRespStatusSet
   * @return
   * @throws DataException
   */
  private Set<A2lWpResponsibilityStatus> findMatchingWpRespStatusSetForWpResp(
      final Map<Long, A2lWpResponsibility> srcA2lWpRespMapForWpDefVers, final A2lWpResponsibility a2lWpResponsibility)
      throws DataException {
    A2lWpResponsibilityStatusLoader a2lWpResponsibilityStatusLoader =
        new A2lWpResponsibilityStatusLoader(getServiceData());

    Set<A2lWpResponsibilityStatus> srcA2lWpRespStatusSet = new HashSet<>();

    for (Entry<Long, A2lWpResponsibility> srcA2lWPRespEntrySet : srcA2lWpRespMapForWpDefVers.entrySet()) {
      A2lWpResponsibility srcA2lWpResponsibility = srcA2lWPRespEntrySet.getValue();
      boolean isA2lWPRespStatusAvailable =
          a2lWpResponsibilityStatusLoader.isA2lWPRespStatusAvailable(srcA2lWpResponsibility.getId());
      if (isA2lWPRespStatusAvailable &&
          CommonUtils.isEqual(srcA2lWpResponsibility.getA2lRespId(), a2lWpResponsibility.getA2lRespId()) &&
          CommonUtils.isEqual(srcA2lWpResponsibility.getA2lWpId(), a2lWpResponsibility.getA2lWpId()) &&
          CommonUtils.isEqual(srcA2lWpResponsibility.getVariantGrpName(), a2lWpResponsibility.getVariantGrpName())) {
        srcA2lWpRespStatusSet =
            a2lWpResponsibilityStatusLoader.getA2lWpRespStatusBasedOnWPRespId(srcA2lWpResponsibility.getId());
        // TODO confirm
        break;
      }
    }
    return srcA2lWpRespStatusSet;
  }


  /**
  *
  */
  private void createA2lWpParamMapping(final A2lWpDefnVersion workingSet,
      final Map<Long, A2lWpResponsibility> newA2lWpRespPalMap)
      throws IcdmException {
    A2lWpParamMappingLoader a2lWpParamMappingLdr = new A2lWpParamMappingLoader(getServiceData());
    A2lWpResponsibilityLoader respPalLdr = new A2lWpResponsibilityLoader(getServiceData());


    Map<Long, A2lWpParamMapping> existingMapping = a2lWpParamMappingLdr.getMappingByWpDefVersId(workingSet.getId());
    for (A2lWpParamMapping wpParMapping : existingMapping.values()) {

      A2lWpParamMapping wpParamMapping = new A2lWpParamMapping();
      // Parameter details
      wpParamMapping.setParamId(wpParMapping.getParamId());
      wpParamMapping.setName(wpParMapping.getName());
      wpParamMapping.setDescription(wpParMapping.getDescription());
      // set workpackage responsibility id

      Map<Long, A2lWpResponsibility> existingWps = respPalLdr.getWpRespForWpDefnVers(workingSet.getId()).getWpRespMap();
      A2lWpResponsibility a2lWpResponsibility = getNewA2lWpRespPal(wpParMapping, existingWps, newA2lWpRespPalMap);
      if (a2lWpResponsibility != null) {
        wpParamMapping.setWpRespId(a2lWpResponsibility.getId());
      }
      // set WP name at customer
      wpParamMapping.setWpNameCust(wpParMapping.getWpNameCust());
      // Inherit flags
      wpParamMapping.setWpNameCustInherited(wpParMapping.isWpNameCustInherited());
      wpParamMapping.setWpRespInherited(wpParMapping.isWpRespInherited());
      wpParamMapping.setParA2lRespId(wpParMapping.getParA2lRespId());
      A2lWpParamMappingCommand wpParMapCmd =
          new A2lWpParamMappingCommand(getServiceData(), wpParamMapping, false, false);
      executeChildCommand(wpParMapCmd);
    }

  }


  /**
   * @param wpParamMapping
   * @param existingWps
   * @param newRespPalMap
   * @return A2lWpResponsibility that is to be set to a2lwpParamMapping object
   * @throws DataException
   */
  private A2lWpResponsibility getNewA2lWpRespPal(final A2lWpParamMapping wpParamMapping,
      final Map<Long, A2lWpResponsibility> existingWps, final Map<Long, A2lWpResponsibility> newRespPalMap)
      throws DataException {
    A2lWpResponsibility existingWpRespPal = existingWps.get(wpParamMapping.getWpRespId());
    A2lVariantGroup existingVarGrp = null;

    // if variant group is mapped to wpresp pal, get var grp object
    if (existingWpRespPal.getVariantGrpId() != null) {
      existingVarGrp =
          new A2lVariantGroupLoader(getServiceData()).getDataObjectByID(existingWpRespPal.getVariantGrpId());
    }
    for (A2lWpResponsibility a2lWpResponsibility : newRespPalMap.values()) {
      A2lVariantGroup newVarGrp = null;
      if (a2lWpResponsibility.getVariantGrpId() != null) {
        newVarGrp =
            new A2lVariantGroupLoader(getServiceData()).getDataObjectByID(a2lWpResponsibility.getVariantGrpId());
      }

      // if wp name is equal, check for var grp..
      // reason : same wp name can be present in 2 var grps
      if (a2lWpResponsibility.getName().equals(existingWpRespPal.getName()) &&
          compareExistingAndNewVarGrp(existingWpRespPal, existingVarGrp, a2lWpResponsibility, newVarGrp)) {
        return a2lWpResponsibility;
      }

    }


    return null;

  }

  /**
   * @param existingWpRespPal
   * @param existingVarGrp
   * @param a2lWpResponsibility
   * @param newVarGrp
   * @return
   */
  private boolean compareExistingAndNewVarGrp(final A2lWpResponsibility existingWpRespPal,
      final A2lVariantGroup existingVarGrp, final A2lWpResponsibility a2lWpResponsibility,
      final A2lVariantGroup newVarGrp) {
    return isVarGrpNameEqual(existingVarGrp, newVarGrp) || isVarGrpIdNull(existingWpRespPal, a2lWpResponsibility);
  }

  /**
   * @param existingVarGrp
   * @param newVarGrp
   * @return
   */
  private boolean isVarGrpNameEqual(final A2lVariantGroup existingVarGrp, final A2lVariantGroup newVarGrp) {
    return (null != existingVarGrp) && (null != newVarGrp) && newVarGrp.getName().equals(existingVarGrp.getName());
  }

  /**
   * @param existingWpRespPal
   * @param a2lWpResponsibility
   * @return
   */
  private boolean isVarGrpIdNull(final A2lWpResponsibility existingWpRespPal,
      final A2lWpResponsibility a2lWpResponsibility) {
    return (null == existingWpRespPal.getVariantGrpId()) && (null == a2lWpResponsibility.getVariantGrpId());
  }


  /**
   * Create a2l wp resp pal objects using existing objects from working set
   *
   * @throws IcdmException
   */
  private Map<Long, A2lWpResponsibility> createA2lWpRespPal(final A2lWpDefnVersion workingSet,
      final A2lWpDefnVersion newA2lWpDefinitionVersion, final Map<String, A2lVariantGroup> newVarGrps)
      throws IcdmException {
    Map<Long, A2lWpResponsibility> newA2lWpRespPalMap = new HashMap<>();
    A2lWpResponsibilityLoader respPalLdr = new A2lWpResponsibilityLoader(getServiceData());
    A2lVariantGroupLoader varGrpLoader = new A2lVariantGroupLoader(getServiceData());

    Map<Long, A2lWpResponsibility> existingWps = respPalLdr.getWpRespForWpDefnVers(workingSet.getId()).getWpRespMap();

    for (A2lWpResponsibility wpRespPal : existingWps.values()) {
      A2lWpResponsibility newA2lRespPal = new A2lWpResponsibility();

      newA2lRespPal.setA2lWpId(wpRespPal.getA2lWpId());
      newA2lRespPal.setWpDefnVersId(newA2lWpDefinitionVersion.getId());
      newA2lRespPal.setWpNameCust(wpRespPal.getWpNameCust());
      newA2lRespPal.setA2lRespId(wpRespPal.getA2lRespId());
      if (wpRespPal.getVariantGrpId() != null) {
        String varGrpName = varGrpLoader.getDataObjectByID(wpRespPal.getVariantGrpId()).getName();
        newA2lRespPal.setVariantGrpId(newVarGrps.get(varGrpName).getId());
      }
      A2lWpResponsibilityCommand cmd = new A2lWpResponsibilityCommand(getServiceData(), newA2lRespPal, false, false);
      executeChildCommand(cmd);
      newA2lWpRespPalMap.put(wpRespPal.getId(), cmd.getNewData());
    }
    return newA2lWpRespPalMap;
  }


  /**
   * @param a2lWpResponsibility
   * @param pidcVariant
   * @param map
   * @param wpRespInheritedRespMap
   * @throws IcdmException
   */
  private void createA2LWpRespStatus(final A2lWpResponsibility a2lWpResponsibility, final PidcVariant pidcVariant,
      final Map<Long, Map<Long, A2lWpResponsibilityStatus>> srcVarParA2lRespStatusMap,
      final Map<Long, Set<Long>> wpRespInheritedRespMap)
      throws IcdmException {

    Long varId = (null != pidcVariant) ? pidcVariant.getId() : ApicConstants.NO_VARIANT_ID;
    Long wpRespId = a2lWpResponsibility.getId();

    // If WPResp contains Customized Responsibility
    if (wpRespInheritedRespMap.containsKey(wpRespId)) {
      createA2LWpRespStatusForCustomizedResp(varId, a2lWpResponsibility, srcVarParA2lRespStatusMap,
          wpRespInheritedRespMap.get(wpRespId));
    }
    else {
      String wpRespStatus = WP_RESP_STATUS_TYPE.NOT_FINISHED.getDbType();

      if (srcVarParA2lRespStatusMap.containsKey(varId)) {
        // Getting the Wp Resp Status if available from source
        Map<Long, A2lWpResponsibilityStatus> parA2lRespStatusMap = srcVarParA2lRespStatusMap.get(varId);

        if (parA2lRespStatusMap.containsKey(a2lWpResponsibility.getA2lRespId())) {
          wpRespStatus = parA2lRespStatusMap.get(a2lWpResponsibility.getA2lRespId()).getWpRespFinStatus();
        }
      }
      createA2lWpRespStatusEntry(varId, wpRespId, wpRespStatus, null);
    }
  }

  private void createA2LWpRespStatusForCustomizedResp(final Long varId, final A2lWpResponsibility a2lWpResponsibility,
      final Map<Long, Map<Long, A2lWpResponsibilityStatus>> srcVarParA2lRespStatusMap, final Set<Long> palA2lRespIdSet)
      throws IcdmException {

    String wpRespStatus = WP_RESP_STATUS_TYPE.NOT_FINISHED.getDbType();
    Map<Long, A2lWpResponsibilityStatus> parA2lRespStatusMap = new HashMap<>();
    // Getting the Value for current Variant
    if (srcVarParA2lRespStatusMap.containsKey(varId)) {
      parA2lRespStatusMap = srcVarParA2lRespStatusMap.get(varId);
    }
    // A2lWpResponsibiltyStatus table entry for Original WP/resp (Wp/Resp in 'WP' page of A2l editor) with a2l Resp Id
    // as null
    if (parA2lRespStatusMap.containsKey(a2lWpResponsibility.getA2lRespId())) {
      // Getting the Wp Resp Status if available from source
      wpRespStatus = parA2lRespStatusMap.get(a2lWpResponsibility.getA2lRespId()).getWpRespFinStatus();
    }
    createA2lWpRespStatusEntry(varId, a2lWpResponsibility.getId(), wpRespStatus, null);

    // Adding A2lWpResponsibiltyStatus table entry for each customized Responsibility under Original WP-Resp
    for (Long palA2lRespId : palA2lRespIdSet) {
      String wpRespStatusForCustResp = WP_RESP_STATUS_TYPE.NOT_FINISHED.getDbType();
      // Looping through each customized responsibility
      if (parA2lRespStatusMap.containsKey(palA2lRespId)) {
        // Getting the Wp Resp Status if available from source
        wpRespStatusForCustResp = parA2lRespStatusMap.get(palA2lRespId).getWpRespFinStatus();
      }
      createA2lWpRespStatusEntry(varId, a2lWpResponsibility.getId(), wpRespStatusForCustResp, palA2lRespId);
    }
  }


  /**
   * @param varId
   * @param wpRespId
   * @param a2lWpRespFinStatus
   * @param palA2lRespId
   * @throws IcdmException
   */
  private void createA2lWpRespStatusEntry(final Long varId, final Long wpRespId, final String a2lWpRespFinStatus,
      final Long palA2lRespId)
      throws IcdmException {
    Long varIdFoCreate = (varId != -1) ? varId : null;
    A2lWpResponsibilityStatus a2lWpRespStatusToBeCreated = new A2lWpResponsibilityStatus();
    a2lWpRespStatusToBeCreated.setVariantId(varIdFoCreate);
    a2lWpRespStatusToBeCreated.setWpRespId(wpRespId);
    a2lWpRespStatusToBeCreated.setWpRespFinStatus(a2lWpRespFinStatus);
    a2lWpRespStatusToBeCreated.setA2lRespId(palA2lRespId);
    A2lWpResponsibilityStatusCommand a2lWpResponsibilityStatusCommand =
        new A2lWpResponsibilityStatusCommand(getServiceData(), a2lWpRespStatusToBeCreated, false, false);
    executeChildCommand(a2lWpResponsibilityStatusCommand);
  }


  /**
   * @throws IcdmException
   */
  private void copyA2lVarGrpVarMapping(final Map<Long, A2lVariantGroup> sourceVarGrps,
      final Map<String, A2lVariantGroup> newVarGrps)
      throws IcdmException {
    A2lVarGrpVariantMappingLoader varGrpVarMapLoader = new A2lVarGrpVariantMappingLoader(getServiceData());
    for (A2lVariantGroup varGrp : sourceVarGrps.values()) {
      Map<Long, A2lVarGrpVariantMapping> varGrpVarMapingMap = varGrpVarMapLoader.getA2LVarGrps(varGrp.getId());
      for (A2lVarGrpVariantMapping varGrpVarMap : varGrpVarMapingMap.values()) {
        A2lVarGrpVariantMapping newVarGrpVarMap = new A2lVarGrpVariantMapping();
        // set variant group id
        newVarGrpVarMap.setA2lVarGroupId(newVarGrps.get(varGrp.getName()).getId());
        // set variant id
        newVarGrpVarMap.setVariantId(varGrpVarMap.getVariantId());
        newVarGrpVarMap.setName(varGrpVarMap.getName());
        A2lVarGrpVarMappingCommand varGrpVarMapCmd =
            new A2lVarGrpVarMappingCommand(getServiceData(), newVarGrpVarMap, false, false);
        executeChildCommand(varGrpVarMapCmd);
      }
    }

  }

  /**
  *
  */
  private Map<String, A2lVariantGroup> createNewA2lVarGroups(final A2lWpDefnVersion newA2lWpDefinitionVersion,
      final A2lWpDefnVersion workingSet)
      throws IcdmException {
    final Map<String, A2lVariantGroup> newVarGrpMap = new HashMap<>();
    A2lVariantGroupLoader varGrpLoader = new A2lVariantGroupLoader(getServiceData());

    Map<Long, A2lVariantGroup> varGrpMap = varGrpLoader.getA2LVarGrps(workingSet.getId());
    if (varGrpMap != null) {
      for (A2lVariantGroup varGrp : varGrpMap.values()) {

        A2lVariantGroup newVarGrp = new A2lVariantGroup();
        newVarGrp.setName(varGrp.getName());
        newVarGrp.setDescription(varGrp.getDescription());
        // set workpackage defn version id
        newVarGrp.setWpDefnVersId(newA2lWpDefinitionVersion.getId());
        A2lVariantGroupCommand varGrpCmd = new A2lVariantGroupCommand(getServiceData(), newVarGrp, false, false);
        executeChildCommand(varGrpCmd);
        A2lVariantGroup newarGrp = varGrpCmd.getNewData();
        newVarGrpMap.put(newarGrp.getName(), newarGrp);
      }
    }
    return newVarGrpMap;
  }

  /**
   * Updated the finished status of WP - Resp in new Active WpDef version
   */
  public void updateWpStatusForNewActiveWPDefVers() {
    final Query query = getEm().createNamedQuery(TA2lWpDefnVersion.PRC_UPDATE_A2L_FINISHED);
    query.setParameter(1, this.prevWpDefActiveVers);
    query.setParameter(2, this.newWpDefActiveVers);
    query.executeUpdate();
  }

  /*
   * This method finds Parameters with customized responsiblities configured and Returns < Map of WpRespId as Key and
   * Set of parA2lRespIds as Value >
   */
  private Map<Long, Set<Long>> getwpParamMapByInheritedFlag(final long wpDefVersId, final String wpRespInheritedFlag)
      throws DataException {
    Map<Long, Set<Long>> wpRespInheritedRespMap = new HashMap<>();
    TypedQuery<TA2lWpParamMapping> queryForParamMappings = getEm().createNamedQuery(
        TA2lWpParamMapping.GET_WP_PARAM_MAPPING_FOR_WP_DEFN_VERS_INHERIT_FLAG, TA2lWpParamMapping.class);
    queryForParamMappings.setParameter("wpDefnVersId", wpDefVersId);
    queryForParamMappings.setParameter("wpRespInheritedFlag", wpRespInheritedFlag);
    for (TA2lWpParamMapping tA2lWpParamMapping : queryForParamMappings.getResultList()) {
      A2lWpParamMapping paramMapping =
          new A2lWpParamMappingLoader(getServiceData()).createDataObject(tA2lWpParamMapping);
      long key = paramMapping.getWpRespId();
      if (wpRespInheritedRespMap.containsKey(key)) {
        Set<Long> parA2lRespSet = wpRespInheritedRespMap.get(key);
        parA2lRespSet.add(paramMapping.getParA2lRespId());
        wpRespInheritedRespMap.put(key, parA2lRespSet);
      }
      else {
        Set<Long> parA2lRespSet = new HashSet<>();
        parA2lRespSet.add(paramMapping.getParA2lRespId());
        wpRespInheritedRespMap.put(key, parA2lRespSet);
      }
    }

    return wpRespInheritedRespMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    A2lWpDefnVersionLoader loader = new A2lWpDefnVersionLoader(getServiceData());
    TA2lWpDefnVersion entity = loader.getEntityObject(getInputData().getId());

    getEm().remove(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    if ((getCmdMode() == COMMAND_MODE.CREATE) || (getCmdMode() == COMMAND_MODE.UPDATE)) {
      refreshEntity();
    }
  }

  /**
   * Used for copy command
   */
  public void refreshEntity() {
    // clear the cache and load the entity from db
    getEm().getEntityManagerFactory().getCache().evictAll();

    // Refresh the entity after the creation, as the child records are created via stored procedure
    A2lWpDefnVersionLoader ldr = new A2lWpDefnVersionLoader(getServiceData());
    TA2lWpDefnVersion entity = ldr.getEntityObject(getObjId());
    getEm().refresh(entity);

    TPidcA2l tPidcA2l = new PidcA2lLoader(getServiceData()).getEntityObject(entity.getTPidcA2l().getPidcA2lId());
    getEm().refresh(tPidcA2l);
    
    // refresh a2l wp parameter mappings as after creating wp def version newly created mappings in wp def version are
    // not fetched
    for (TA2lWpResponsibility a2lWpRespEntity : entity.getTA2lWpResponsibility()) {
      getEm().refresh(a2lWpRespEntity);
      for (TA2lWpResponsibilityStatus a2lWprespStatus : a2lWpRespEntity.gettA2lWPRespStatus()) {
        getEm().refresh(a2lWprespStatus);
      }
    }
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
    // Implementation not provided
  }

  /**
   * @return the oldWpDefWS
   */
  public A2lWpDefnVersion getOldWpDefWS() {
    return this.oldWpDefWS;
  }


  /**
   * @param oldWpDefWS the oldWpDefWS to set
   */
  public void setOldWpDefWS(final A2lWpDefnVersion oldWpDefWS) {
    this.oldWpDefWS = oldWpDefWS;
  }

  /**
   * @param newVersionProc newVersionProc
   */
  public void setNewVersionProc(final boolean newVersionProc) {
    this.newVersionProc = newVersionProc;

  }


  /**
   * @return the wpDefnVersForFinStatusTakeOver
   */
  public A2lWpDefnVersion getWpDefnVersForFinStatusTakeOver() {
    return this.wpDefnVersForFinStatusTakeOver;
  }


  /**
   * @param wpDefnVersForFinStatusTakeOver the wpDefnVersForFinStatusTakeOver to set
   */
  public void setWpDefnVersForFinStatusTakeOver(final A2lWpDefnVersion wpDefnVersForFinStatusTakeOver) {
    this.wpDefnVersForFinStatusTakeOver = wpDefnVersForFinStatusTakeOver;
  }
}
