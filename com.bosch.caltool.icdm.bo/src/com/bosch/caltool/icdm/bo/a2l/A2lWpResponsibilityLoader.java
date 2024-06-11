package com.bosch.caltool.icdm.bo.a2l;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.common.bo.a2l.ParamWpRespResolver;
import com.bosch.caltool.icdm.common.bo.a2l.ParamWpResponsibility;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lVariantGroup;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWorkPackage;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpDefnVersion;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpParamMapping;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResponsibility;
import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.A2LDetailsStructureModel;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lVarGrpVariantMapping;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefinitionModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.WpRespLabelResponse;
import com.bosch.caltool.icdm.model.a2l.WpRespModel;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * Loader class for A2lWpResponsibility
 *
 * @author pdh2cob
 */
public class A2lWpResponsibilityLoader extends AbstractBusinessObject<A2lWpResponsibility, TA2lWpResponsibility> {

  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public A2lWpResponsibilityLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.A2L_WP_RESPONSIBILITY, TA2lWpResponsibility.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected A2lWpResponsibility createDataObject(final TA2lWpResponsibility entity) throws DataException {
    A2lWpResponsibility object = new A2lWpResponsibility();

    A2lWorkPackageLoader a2lWpLoader = new A2lWorkPackageLoader(getServiceData());
    TA2lWorkPackage dbA2lWp = null;
    if (entity.getA2lWp() != null) {
      dbA2lWp = a2lWpLoader.getEntityObject(entity.getA2lWp().getA2lWpId());
      object.setName(dbA2lWp.getWpName());
      object.setDescription(dbA2lWp.getWpDesc());
      object.setWpNameCust(dbA2lWp.getWpNameCust());
      object.setA2lWpId(entity.getA2lWp().getA2lWpId());
    }
    setCommonFields(object, entity);

    // set wp defn version id
    object.setWpDefnVersId(
        entity.gettA2lWpDefnVersion() == null ? null : entity.gettA2lWpDefnVersion().getWpDefnVersId());

    if (entity.getA2lResponsibility() != null) {
      object.setA2lRespId(entity.getA2lResponsibility().getA2lRespId());
    }
    object.setVariantGrpId(entity.getVariantGroup() == null ? null : entity.getVariantGroup().getA2lVarGrpId());
    object.setVariantGrpName(entity.getVariantGroup() == null ? null : entity.getVariantGroup().getGroupName());
    return object;
  }


  /**
   * @param wpDefnVersId - A2lWpDefinitionVersion id
   * @return Map key - A2lWpResponsibility id, value - A2lWpResponsibility object
   * @throws DataException -
   */
  public A2lWpDefinitionModel getWpRespForWpDefnVers(final Long wpDefnVersId) throws DataException {
    A2lWpDefinitionModel a2lWpDefinitionModel = new A2lWpDefinitionModel();

    // get Wp Defn vers entity
    TA2lWpDefnVersion tWpDefnVers = new A2lWpDefnVersionLoader(getServiceData()).getEntityObject(wpDefnVersId);
    if (tWpDefnVers != null) {
      // set WP Defn version id
      a2lWpDefinitionModel.setSelectedWpDefnVersionId(wpDefnVersId);
      TA2lWpDefnVersion activeA2lWPDefnVersionEntityFromA2l = new A2lWpDefnVersionLoader(getServiceData())
          .getActiveA2lWPDefnVersionEntityFromA2l(tWpDefnVers.getTPidcA2l().getPidcA2lId());
      if (CommonUtils.isNotNull(activeA2lWPDefnVersionEntityFromA2l)) {
        a2lWpDefinitionModel.setActiveWpDefnVersionId(activeA2lWPDefnVersionEntityFromA2l.getWpDefnVersId());
      }

      a2lWpDefinitionModel
          .setParamMappingAllowed(tWpDefnVers.getParamLevelChgAllowedFlag().equals(ApicConstants.CODE_YES));

      // get TA2lWpResponsibility list for given wp defn vers id
      List<TA2lWpResponsibility> wpRespList = tWpDefnVers.getTA2lWpResponsibility();
      if (wpRespList != null) {
        for (TA2lWpResponsibility tA2lWpResponsibility : wpRespList) {
          // add to map
          A2lWpResponsibility a2lWpRespObj = createDataObject(tA2lWpResponsibility);
          a2lWpDefinitionModel.getWpRespMap().put(tA2lWpResponsibility.getWpRespId(), a2lWpRespObj);
        }
        Set<TA2lVariantGroup> ta2lVariantGroups = tWpDefnVers.getTA2lVariantGroups();
        A2lVariantGroupLoader a2lVariantGroupLoader = new A2lVariantGroupLoader(getServiceData());
        A2lVarGrpVariantMappingLoader a2lVarGrpVariantMappingLoader =
            new A2lVarGrpVariantMappingLoader(getServiceData());
        for (TA2lVariantGroup ta2lVariantGroup : ta2lVariantGroups) {
          a2lWpDefinitionModel.getA2lVariantGroupMap().put(ta2lVariantGroup.getA2lVarGrpId(),
              a2lVariantGroupLoader.createDataObject(ta2lVariantGroup));
          Map<Long, A2lVarGrpVariantMapping> a2lvarGrpVariantMapping =
              a2lVarGrpVariantMappingLoader.getA2LVarGrps(ta2lVariantGroup.getA2lVarGrpId());
          a2lvarGrpVariantMapping.values().stream().forEach(varGrpMapping -> a2lWpDefinitionModel.getA2lVarToVarGrpMap()
              .put(varGrpMapping.getVariantId(), varGrpMapping.getA2lVarGroupId()));
        }
      }

    }
    return a2lWpDefinitionModel;
  }

  public Map<Long, A2lWpResponsibility> getA2lWpRespMapForWpDefVers(final Long wpDefnVersId) throws DataException {
    Map<Long, A2lWpResponsibility> wpRespMap = new HashMap<>();
    // get Wp Defn vers entity
    TA2lWpDefnVersion tWpDefnVers = new A2lWpDefnVersionLoader(getServiceData()).getEntityObject(wpDefnVersId);
    if (tWpDefnVers != null) {
      List<TA2lWpResponsibility> wpRespList = tWpDefnVers.getTA2lWpResponsibility();
      if (wpRespList != null) {
        for (TA2lWpResponsibility tA2lWpResponsibility : wpRespList) {
          // add to map
          A2lWpResponsibility a2lWpRespObj = createDataObject(tA2lWpResponsibility);
          wpRespMap.put(tA2lWpResponsibility.getWpRespId(), a2lWpRespObj);
        }
      }
    }
    return wpRespMap;
  }


  /**
   * Gets the all wp resp pal for wp defn vers.
   *
   * @param wpDefnVersId the wp defn vers id
   * @param variantGrpId the variant grp id
   * @return the all wp resp pal for wp defn vers
   * @throws DataException the data exception
   */
  public Map<String, Map<Long, A2lWpResponsibility>> getAllWpRespPalForWpDefnVers(final Long wpDefnVersId,
      final Long variantGrpId)
      throws DataException {
    Map<String, Map<Long, A2lWpResponsibility>> wpRespPalMap = new HashMap<>();
    // get Wp Defn vers entity
    TA2lWpDefnVersion tWpDefnVers = new A2lWpDefnVersionLoader(getServiceData()).getEntityObject(wpDefnVersId);
    if (tWpDefnVers != null) {
      // get TA2lWpResponsibility list for given wp defn vers id
      List<TA2lWpResponsibility> wpRespList = tWpDefnVers.getTA2lWpResponsibility();
      for (TA2lWpResponsibility tA2lWpResponsibility : wpRespList) {
        A2lWorkPackage wpPal =
            new A2lWorkPackageLoader(getServiceData()).getDataObjectByID(tA2lWpResponsibility.getA2lWp().getA2lWpId());
        if ((variantGrpId == null) || ((tA2lWpResponsibility.getVariantGroup() != null) &&
            (variantGrpId.longValue() == tA2lWpResponsibility.getVariantGroup().getA2lVarGrpId()))) {
          if (!wpRespPalMap.containsKey(wpPal.getName().toUpperCase())) {
            wpRespPalMap.put(wpPal.getName().toUpperCase(), new HashMap<>());
          }
          wpRespPalMap.get(wpPal.getName().toUpperCase()).put(tA2lWpResponsibility.getWpRespId(),
              createDataObject(tA2lWpResponsibility));
        }
      }
    }
    return wpRespPalMap;
  }

  /**
   * Gets the default wp resp pal.
   *
   * @param wpDefnVersId the wp defn vers id
   * @return the default wp resp pal
   * @throws DataException the data exception
   */
  public A2lWpResponsibility getDefaultWpRespPal(final Long wpDefnVersId) throws DataException {
    TA2lWpDefnVersion tWpDefnVers = new A2lWpDefnVersionLoader(getServiceData()).getEntityObject(wpDefnVersId);
    List<TA2lWpResponsibility> wpRespList = tWpDefnVers.getTA2lWpResponsibility();
    for (TA2lWpResponsibility tA2lWpResponsibility : wpRespList) {
      A2lWorkPackage wpPal =
          new A2lWorkPackageLoader(getServiceData()).getDataObjectByID(tA2lWpResponsibility.getA2lWp().getA2lWpId());
      if (wpPal.getName().equals(ApicConstants.DEFAULT_A2L_WP_NAME)) {
        return createDataObject(tA2lWpResponsibility);
      }
    }
    return null;
  }


  /**
   * @param pidcA2lId pidcA2lId
   * @param varId varId
   * @return
   * @throws IcdmException IcdmException
   */
  public List<WpRespLabelResponse> getWpResp(final Long pidcA2lId, final Long varId) throws IcdmException {

    A2lWpDefnVersionLoader wpDefVerLoader = new A2lWpDefnVersionLoader(getServiceData());
    A2lVariantGroup a2lVarGrp = null;
    // get the active version
    Map<Long, A2lWpDefnVersion> a2lWpDefVersionMap = wpDefVerLoader.getWPDefnVersionsForPidcA2lId(pidcA2lId);

    TPidcA2l tPidcA2l = new PidcA2lLoader(getServiceData()).getEntityObject(pidcA2lId);
    long pidcVersId = tPidcA2l.getTPidcVersion().getPidcVersId();
    long projectId = tPidcA2l.getTPidcVersion().getTabvProjectidcard().getProjectId();
    A2lWpDefnVersion activeVersion = null;
    for (A2lWpDefnVersion a2lWpDefVersion : a2lWpDefVersionMap.values()) {
      if (a2lWpDefVersion.isActive()) {
        activeVersion = a2lWpDefVersion;
        break;
      }
    }
    // case only if Active version is available.
    if (activeVersion != null) {
      A2LDetailsStructureModel detailsModel = wpDefVerLoader.getDetailsModel(activeVersion.getId(), false);
      A2lVarGrpVariantMapping a2lVarGrpVarMapping = detailsModel.getGroupMappingMap().get(varId);
      if (a2lVarGrpVarMapping != null) {
        a2lVarGrp =
            new A2lVariantGroupLoader(getServiceData()).getDataObjectByID(a2lVarGrpVarMapping.getA2lVarGroupId());
      }

      return getWpRespLabelResponse(a2lVarGrp, activeVersion, pidcVersId, projectId);
    }
    return new ArrayList<>();
  }

  /**
   * Returns a list of wpRespLabelresponse
   *
   * @param varGrpId as input
   * @param wpDefVerId as input
   * @return WpRespLabel Response
   * @throws IcdmException exception
   */
  public List<WpRespLabelResponse> getWpRespForA2LExport(final Long varGrpId, final Long wpDefVerId)
      throws IcdmException {

    A2lWpDefnVersion wpDefnVersion = new A2lWpDefnVersionLoader(getServiceData()).getDataObjectByID(wpDefVerId);

    TPidcA2l tPidcA2l = new PidcA2lLoader(getServiceData()).getEntityObject(wpDefnVersion.getPidcA2lId());
    long pidcVersId = tPidcA2l.getTPidcVersion().getPidcVersId();
    long projectId = tPidcA2l.getTabvProjectidcard().getProjectId();
    A2lVariantGroup a2lVarGrp = null;
    if (null != varGrpId) {
      a2lVarGrp = new A2lVariantGroupLoader(getServiceData()).getDataObjectByID(varGrpId);
    }

    return getWpRespLabelResponse(a2lVarGrp, wpDefnVersion, pidcVersId, projectId);
  }


  /**
   * @param pidcA2lId - pidc a2l id
   * @param variantId - variant id
   * @return Map -> key - param name, value - wprseplabelresponse
   * @throws IcdmException exception
   */
  public Map<String, WpRespLabelResponse> getWpRespLabelMap(final Long pidcA2lId, final Long variantId)
      throws IcdmException {
    Long actVersId = new A2lWpDefnVersionLoader(getServiceData()).getActiveVersion(pidcA2lId).getId();
    Long varGrpId = null;
    if (variantId != null) {
      A2lVariantGroup a2lVariantGroup =
          new A2lVariantGroupLoader(getServiceData()).getVariantGroup(actVersId, variantId);
      varGrpId = (a2lVariantGroup == null) ? null : a2lVariantGroup.getId();
    }
    List<WpRespLabelResponse> labelRespList = getWpRespForA2LExport(varGrpId, actVersId);
    Map<String, WpRespLabelResponse> labelRespMap = new HashMap<>();
    labelRespList.stream().forEach(labelResp -> labelRespMap.put(labelResp.getParamName(), labelResp));
    return labelRespMap;
  }


  /**
   * @param a2lVarGrp
   * @param activeVersion
   * @param projectId
   * @param pidcVersId
   * @param a2lRespModel
   * @return
   * @throws IcdmException
   */
  private List<WpRespLabelResponse> getWpRespLabelResponse(final A2lVariantGroup a2lVarGrp,
      final A2lWpDefnVersion activeVersion, final long pidcVersId, final long projectId)
      throws IcdmException {

    List<WpRespLabelResponse> wpRespLabelResponsesList = new ArrayList<>();
    Map<Long, A2lVariantGroup> a2lVarGrpMap = new HashMap<>();
    Map<Long, A2lWpParamMapping> a2lWParamInfoMap = new HashMap<>();
    Map<Long, A2lWpResponsibility> wpRespMap = new HashMap<>();

    Map<Long, A2lResponsibility> a2lRespMap =
        new A2lResponsibilityLoader(getServiceData()).getByPidc(projectId).getA2lResponsibilityMap();
    Map<Long, A2lWorkPackage> a2lWpMap = new A2lWorkPackageLoader(getServiceData()).getWpByPidcVers(pidcVersId);

    TA2lWpDefnVersion ta2lWpDefnVersion =
        new A2lWpDefnVersionLoader(getServiceData()).getEntityObject(activeVersion.getId());

    List<TA2lWpResponsibility> ta2lWpRespPals = ta2lWpDefnVersion.getTA2lWpResponsibility();


    A2lWpResponsibilityLoader wpResponsibilityLoader = new A2lWpResponsibilityLoader(getServiceData());
    A2lWpParamMappingLoader wpParamMappingLoader = new A2lWpParamMappingLoader(getServiceData());

    // Map of Param Id and A2l WP Param Mapping
    Map<Long, A2lWpParamMapping> wpParamMappingMap = new HashMap<>();

    for (TA2lWpResponsibility tA2lWPRespPal : ta2lWpRespPals) {
      wpRespMap.put(tA2lWPRespPal.getWpRespId(), wpResponsibilityLoader.getDataObjectByID(tA2lWPRespPal.getWpRespId()));
      for (TA2lWpParamMapping tA2lParamMapping : tA2lWPRespPal.getTA2lWpParamMappings()) {
        A2lWpParamMapping wpParamMapping =
            wpParamMappingLoader.getDataObjectByID(tA2lParamMapping.getWpParamMappingId());
        a2lWParamInfoMap.put(tA2lParamMapping.getWpParamMappingId(), wpParamMapping);
        wpParamMappingMap.put(wpParamMapping.getParamId(), wpParamMapping);
      }
    }

    ParamWpRespResolver resolver = new ParamWpRespResolver(a2lVarGrpMap, wpRespMap, a2lWParamInfoMap);

    Map<Long, ParamWpResponsibility> respForParam =
        resolver.getRespForParam(a2lVarGrp != null ? a2lVarGrp.getId() : null);

    for (ParamWpResponsibility paramWpResponsibility : respForParam.values()) {

      WpRespLabelResponse wpRespLabelResponse = new WpRespLabelResponse();
      wpRespLabelResponse.setParamId(paramWpResponsibility.getParamId());
      wpRespLabelResponse.setParamName(paramWpResponsibility.getParamName());
      wpRespLabelResponse
          .setParamDescription(wpParamMappingMap.get(paramWpResponsibility.getParamId()).getDescription());
      WpRespModel wpRespModel = new WpRespModel();
      A2lResponsibility a2lResp = a2lRespMap.get(paramWpResponsibility.getRespId());
      A2lWorkPackage a2lWorkPackage = a2lWpMap.get(paramWpResponsibility.getWpId());

      wpRespModel.setA2lResponsibility(a2lResp);
      wpRespModel.setA2lWpId(a2lWorkPackage.getId());
      wpRespModel.setWpName(a2lWorkPackage.getName());
      wpRespModel.setWpRespName(a2lResp.getName());
      wpRespLabelResponse.setWpRespModel(wpRespModel);
      wpRespLabelResponsesList.add(wpRespLabelResponse);
    }

    return wpRespLabelResponsesList;

  }
}
