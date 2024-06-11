package com.bosch.caltool.icdm.bo.a2l;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lVarGrpVariantMapping;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lVariantGroup;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpDefnVersion;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpParamMapping;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResponsibility;
import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.A2LDetailsStructureModel;
import com.bosch.caltool.icdm.model.a2l.A2lVarGrpVariantMapping;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.ReviewInfoWpDefDetails;


/**
 * Loader class for A2lWpDefinitionVersion
 *
 * @author pdh2cob
 */
public class A2lWpDefnVersionLoader extends AbstractBusinessObject<A2lWpDefnVersion, TA2lWpDefnVersion> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public A2lWpDefnVersionLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.A2L_WP_DEFN_VERSION, TA2lWpDefnVersion.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected A2lWpDefnVersion createDataObject(final TA2lWpDefnVersion entity) throws DataException {
    A2lWpDefnVersion object = new A2lWpDefnVersion();

    setCommonFields(object, entity);

    object.setVersionNumber(entity.getVersionNumber());
    object.setVersionName(entity.getVersionName());
    object.setDescription(entity.getVersionDesc());
    object.setActive(yOrNToBoolean(entity.getIsActive()));

    // Working set version has version number '0'
    object.setWorkingSet(entity.getVersionNumber() == 0);

    // set pidc a2l id
    object.setPidcA2lId(entity.getTPidcA2l() == null ? null : entity.getTPidcA2l().getPidcA2lId());
    // set flag to indicate if parameter to wp mapping is allowed
    object.setParamLevelChgAllowedFlag(yOrNToBoolean(entity.getParamLevelChgAllowedFlag()));
    // flag to indicate whether the version is active anytime
    object.setAnytimeActiveVersion(yOrNToBoolean(entity.getIsAnytimeActiveFlag()));
    object.setName(object.isWorkingSet() ? ApicConstants.WORKING_SET_NAME
        : object.getVersionNumber() + " : " + object.getVersionName());
    object.setVcdmPvdId(entity.getVcdmPvdId());
    object.setVcdmPrdId(entity.getVcdmPrdId());

    return object;
  }


  /**
   * @param pidcA2lId -
   * @return map key- A2lWpDefinitionVersion id, value - A2lWpDefinitionVersion object
   * @throws DataException -
   */
  public Map<Long, A2lWpDefnVersion> getWPDefnVersionsForPidcA2lId(final Long pidcA2lId) throws DataException {
    Map<Long, A2lWpDefnVersion> objMap = new ConcurrentHashMap<>();
    TPidcA2l tPidcA2l = new PidcA2lLoader(getServiceData()).getEntityObject(pidcA2lId);
    if (tPidcA2l != null) {
      List<TA2lWpDefnVersion> wpDefnList = tPidcA2l.gettA2lWpDefnVersions();
      A2lWpDefnVersionLoader loader = new A2lWpDefnVersionLoader(getServiceData());
      for (TA2lWpDefnVersion ta2lWpDefinitionVersion : wpDefnList) {
        objMap.put(ta2lWpDefinitionVersion.getWpDefnVersId(),
            loader.getDataObjectByID(ta2lWpDefinitionVersion.getWpDefnVersId()));
      }
    }
    return objMap;
  }


  /**
   * Gets the working set.
   *
   * @return A2lWpDefinitionVersion for which version is 0 - working set
   */
  public A2lWpDefnVersion getWorkingSet(final Map<Long, A2lWpDefnVersion> wpDefnVersMap) {
    for (A2lWpDefnVersion a2lWpDefinitionVersion : wpDefnVersMap.values()) {
      if (a2lWpDefinitionVersion.getVersionNumber() == 0L) {
        return a2lWpDefinitionVersion;
      }
    }
    return null;
  }


  /**
   * @param a2lWpDefVerId as WpDefnitionVersion
   * @param includeDeletedVar as boolean value
   * @return A2LDetailsStructureModel
   * @throws IcdmException as exception
   */
  public A2LDetailsStructureModel getDetailsModel(final Long a2lWpDefVerId, final boolean includeDeletedVar)
      throws IcdmException {

    A2LDetailsStructureModel detailsModel = new A2LDetailsStructureModel();
    Map<Long, List<PidcVariant>> mappedVariantsMap = new HashMap<>();
    A2lWpDefnVersionLoader wpDefVerLoader = new A2lWpDefnVersionLoader(getServiceData());
    A2lWpDefnVersion wpDefVer = wpDefVerLoader.getDataObjectByID(a2lWpDefVerId);

    detailsModel.setWpDefVersion(wpDefVer);
    Long pidcA2lId = wpDefVer.getPidcA2lId();
    PidcVariantLoader variantLoader = new PidcVariantLoader(getServiceData());
    Map<Long, PidcVariant> variantsMap = variantLoader.getA2lMappedVariants(pidcA2lId, includeDeletedVar);
    detailsModel.setA2lMappedVariantsMap(variantsMap);

    SortedSet<PidcVariant> unmappedVarsSet = new TreeSet<>();
    unmappedVarsSet.addAll(variantsMap.values());

    A2lVariantGroupLoader varGrpLoader = new A2lVariantGroupLoader(getServiceData());
    Map<Long, A2lVariantGroup> a2lVarGrpMap = varGrpLoader.getA2LVarGrps(a2lWpDefVerId);
    detailsModel.setA2lVariantGrpMap(a2lVarGrpMap);

    PidcVariantLoader varLoader = new PidcVariantLoader(getServiceData());
    A2lVarGrpVariantMappingLoader varGrpMapLoader = new A2lVarGrpVariantMappingLoader(getServiceData());
    Map<Long, A2lVarGrpVariantMapping> pidcA2lVarGroupMappingMap = new HashMap<>();

    for (Long a2lVarGrpId : a2lVarGrpMap.keySet()) {
      Map<Long, A2lVarGrpVariantMapping> varGrpMappingMap = varGrpMapLoader.getA2LVarGrps(a2lVarGrpId);


      for (A2lVarGrpVariantMapping varMapping : varGrpMappingMap.values()) {
        pidcA2lVarGroupMappingMap.put(varMapping.getVariantId(), varMapping);
        if (variantsMap.containsKey(varMapping.getVariantId())) {
          List<PidcVariant> mappedVarList = mappedVariantsMap.get(a2lVarGrpId);
          if (mappedVarList == null) {
            mappedVarList = new ArrayList<>();
          }
          PidcVariant mappedVar = varLoader.getDataObjectByID(varMapping.getVariantId());
          mappedVarList.add(mappedVar);
          unmappedVarsSet.remove(mappedVar);
          mappedVariantsMap.put(a2lVarGrpId, mappedVarList);
        }
      }
    }
    detailsModel.setUnmappedVariants(unmappedVarsSet);
    detailsModel.setMappedVariantsMap(mappedVariantsMap);
    detailsModel.getGroupMappingMap().putAll(pidcA2lVarGroupMappingMap);

    return detailsModel;

  }

  /**
   * 489480 - Show WP definition version and variant group name in review result editor
   *
   * @param resultId review result id
   * @param variantId variant id
   * @return {@link ReviewInfoWpDefDetails} obj
   * @throws DataException Exception
   */
  public ReviewInfoWpDefDetails getWpDefnDetails(final Long wpDefnVersId, final Long variantId) throws DataException {
    ReviewInfoWpDefDetails rvwInfoWpDefDetails = new ReviewInfoWpDefDetails();
    if (null != wpDefnVersId) {
      TA2lWpDefnVersion tA2lWpDefnVersion = getEntityObject(wpDefnVersId);
      rvwInfoWpDefDetails.setWpDefVersName(createDataObject(tA2lWpDefnVersion).getName());
      String variantGrpName = getVariantGrpName(variantId, tA2lWpDefnVersion);
      rvwInfoWpDefDetails.setVariantGrpname(variantGrpName);
    }
    return rvwInfoWpDefDetails;
  }

  /**
   * @param pidcA2lId pidc a2l id
   * @return active version
   * @throws DataException exception
   */
  public A2lWpDefnVersion getActiveVersion(final Long pidcA2lId) throws DataException {

    Map<Long, A2lWpDefnVersion> wpDefnVersMap =
        new A2lWpDefnVersionLoader(getServiceData()).getWPDefnVersionsForPidcA2lId(pidcA2lId);

    for (A2lWpDefnVersion wpDefnVers : wpDefnVersMap.values()) {
      if (wpDefnVers.isActive()) {
        return wpDefnVers;
      }
    }
    return null;
  }

  /**
   * @param pidcA2lId pidc a2l id
   * @return workingset version
   * @throws DataException exception
   */
  public A2lWpDefnVersion getWorkingSetVersion(final Long pidcA2lId) throws DataException {
    // Fetch all the wp definition versions
    Map<Long, A2lWpDefnVersion> wpDefnVersMap = getWPDefnVersionsForPidcA2lId(pidcA2lId);
    // Fetch wp definition working set version
    return getWorkingSet(wpDefnVersMap);
  }

  /**
   * @param Long pidcA2lId
   * @return TA2lWpDefnVersion
   */
  public TA2lWpDefnVersion getActiveA2lWPDefnVersionEntityFromA2l(final Long pidcA2lId) {
    TPidcA2l tPidcA2l = new PidcA2lLoader(getServiceData()).getEntityObject(pidcA2lId);
    for (TA2lWpDefnVersion ta2lWpDefnVersion : tPidcA2l.gettA2lWpDefnVersions()) {
      if (yOrNToBoolean(ta2lWpDefnVersion.getIsActive())) {
        return ta2lWpDefnVersion;
      }
    }
    return null;
  }

  /**
   * @param variantId
   * @param rvwInfoWpDefDetails
   * @param tA2lWpDefnVersion
   */
  private String getVariantGrpName(final Long variantId, final TA2lWpDefnVersion tA2lWpDefnVersion) {
    String variantGrpName = null;
    Set<TA2lVariantGroup> ta2lVariantGroups = tA2lWpDefnVersion.getTA2lVariantGroups();
    if ((null != variantId) && (null != ta2lVariantGroups)) {
      for (TA2lVariantGroup ta2lVariantGroup : ta2lVariantGroups) {
        for (TA2lVarGrpVariantMapping varGrpVarmapping : ta2lVariantGroup.getTA2lVarGrpVariantMappings()) {
          if (variantId.equals(varGrpVarmapping.getTabvProjectVariant().getVariantId())) {
            variantGrpName = ta2lVariantGroup.getGroupName();
            break;
          }
        }
      }
    }
    return variantGrpName;
  }

  /**
   * While creating a new wp definition version, needs to check whether label has assignments to default wp resp. If it
   * has default wp resp assignments then this method returns true;
   *
   * @param pidcA2lId pidc A2l Id
   * @return isDefaultWpRespLabelAssignmentExist
   * @throws DataException exception in ws
   */
  public boolean isDefaultWpRespLabelAssignmentExist(final Long pidcA2lId) throws DataException {
    boolean result = false;
    Map<Long, A2lWpDefnVersion> wpDefnVersMap = getWPDefnVersionsForPidcA2lId(pidcA2lId);
    A2lWpDefnVersion workingSet = getWorkingSet(wpDefnVersMap);

    A2lWpResponsibilityLoader a2lWpResponsibilityLoader = new A2lWpResponsibilityLoader(getServiceData());
    A2lWpResponsibility defaultWpResp = a2lWpResponsibilityLoader.getDefaultWpRespPal(workingSet.getId());

    TA2lWpResponsibility tA2lWpResponsibility = a2lWpResponsibilityLoader.getEntityObject(defaultWpResp.getId());

    // Check default wp resp has param mapping
    if (CommonUtils.isNotEmpty(tA2lWpResponsibility.getTA2lWpParamMappings())) {
      for (TA2lWpParamMapping tA2lWpParamMapping : tA2lWpResponsibility.getTA2lWpParamMappings()) {
        // Check default a2l wp resp param mapping has manual overridden responsibility
        if (CommonUtils.isNull(tA2lWpParamMapping.getTA2lResponsibility())) {
          result = true;
          break;
        }
      }
    }
    return result;
  }
}
