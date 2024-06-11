/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireRespVersionLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireResponseLoader;
import com.bosch.caltool.icdm.common.bo.a2l.ParamWpRespResolver;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpDefnVersion;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpParamMapping;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResponsibility;
import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;
import com.bosch.caltool.icdm.model.a2l.A2LDetailsStructureModel;
import com.bosch.caltool.icdm.model.a2l.A2LStructInternalModel;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lVarGrpVariantMapping;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWPRespModel;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibilityStatus;
import com.bosch.caltool.icdm.model.a2l.PidcA2lTreeStructureModel;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.CdrReportQnaireRespWrapper;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;

/**
 * @author say8cob
 */
public class PidcA2lTreeStructureHandler extends AbstractSimpleBusinessObject {


  /**
   * @param serviceData as input
   */
  public PidcA2lTreeStructureHandler(final ServiceData serviceData) {
    super(serviceData);
  }


  /**
   * Method to fetch the PidcA2lTreeStructureModel
   *
   * @param pidcA2lId as input
   * @return PidcA2lTreeStructureModel
   * @throws IcdmException as exception
   */
  public PidcA2lTreeStructureModel getPidcA2lTreeStruct(final Long pidcA2lId) throws IcdmException {

    PidcA2lTreeStructureModel pidcA2lTreeStructureModel = new PidcA2lTreeStructureModel();

    // Key-Variant Id Value - PidcVariant
    Map<Long, PidcVariant> pidcVarIdVarMap = new HashMap<>();
    // key - variant id , Value - Map of <resp id, Map<wp id , Set<QnaireRespId>>>>
    Map<Long, Map<Long, Map<Long, Set<Long>>>> varRespWpQniareMap = new HashMap<>();

    // key - workpackage id , value - A2LWorkpackage
    Map<Long, A2lWorkPackage> a2lWpMap = new HashMap<>();

    // key - responsibility id , value - A2lResponsibility
    Map<Long, A2lResponsibility> a2lRespMap = new HashMap<>();

    // key - Qnaire Resp id , value - RvwQnaireResponse
    Map<Long, RvwQnaireResponse> rvwQnaireRespMap = new HashMap<>();

    // key - Qnaire Resp id , value - RvwQnaireRespVersion - Working Set
    Map<Long, RvwQnaireRespVersion> rvwQnaireRespVersMap = new HashMap<>();

    /**
     * To get the status of a2l wp responsibility combination;key - pidc variant id,value-map of key- responsibility id
     * ,value-map of key-workpackage id and WP Finished status as String
     */
    Map<Long, Map<Long, Map<Long, String>>> wpRespStatusMap = new HashMap<>();
    /**
     * To get the a2l wp responsibility combination at a variant group level in the active a2l wp def version;key - pidc
     * variant id,value-map of key- responsibility id ,value-map of key-workpackage id and A2lWpRespModel
     */
    Map<Long, Map<Long, Map<Long, A2lWPRespModel>>> respWpA2lWpRespModel = new HashMap<>();

    TPidcA2l tPidcA2l = new PidcA2lLoader(getServiceData()).getEntityObject(pidcA2lId);

    A2lWpDefnVersionLoader wpDefVerLoader = new A2lWpDefnVersionLoader(getServiceData());

    PidcVariantLoader pidcVariantLoader = new PidcVariantLoader(getServiceData());

    A2lVariantGroup a2lVarGrp;

    Long pidcVarId;

    // get the active version
    Map<Long, A2lWpDefnVersion> a2lWpDefVersionMap = wpDefVerLoader.getWPDefnVersionsForPidcA2lId(pidcA2lId);

    long pidcVersId = tPidcA2l.getTPidcVersion().getPidcVersId();
    // to fetch the active wp definition version
    A2lWpDefnVersion activeVersion = fetchActiveWpDefVersion(a2lWpDefVersionMap);
    // case only if Active version is available.
    if (null != activeVersion) {
      if (pidcVariantLoader.hasVariants(pidcVersId, true)) {
        A2LDetailsStructureModel detailsModel = wpDefVerLoader.getDetailsModel(activeVersion.getId(), true);

        for (Entry<Long, PidcVariant> pidcVarEntry : detailsModel.getA2lMappedVariantsMap().entrySet()) {
          a2lVarGrp = null;
          pidcVarId = pidcVarEntry.getValue().getId();
          if (pidcVariantLoader.hasReviewForA2lAndVariant(pidcA2lId, pidcVarId)) {
            A2lVarGrpVariantMapping a2lVarGrpVariantMapping = detailsModel.getGroupMappingMap().get(pidcVarId);
            a2lVarGrp = fetchVarGrpData(a2lVarGrp, a2lVarGrpVariantMapping);
            A2LStructInternalModel a2lStructInternalModel =
                getA2lStructureInternalModel(a2lVarGrp, pidcVarId, pidcVersId, activeVersion);
            fillAllA2lStructRelatedMap(pidcVarIdVarMap, varRespWpQniareMap, wpRespStatusMap, respWpA2lWpRespModel,
                a2lWpMap, a2lRespMap, rvwQnaireRespMap, rvwQnaireRespVersMap, a2lStructInternalModel);
          }
        }
      }
      else {
        // For No-Variant Case
        a2lVarGrp = null;
        pidcVarId = null;
        A2LStructInternalModel a2lStructInternalModel =
            getA2lStructureInternalModel(a2lVarGrp, pidcVarId, pidcVersId, activeVersion);

        fillAllA2lStructRelatedMap(pidcVarIdVarMap, varRespWpQniareMap, wpRespStatusMap, respWpA2lWpRespModel, a2lWpMap,
            a2lRespMap, rvwQnaireRespMap, rvwQnaireRespVersMap, a2lStructInternalModel);
      }
    }

    pidcA2lTreeStructureModel.setPidcVariantMap(pidcVarIdVarMap);
    pidcA2lTreeStructureModel.setA2lRespMap(a2lRespMap);
    pidcA2lTreeStructureModel.setA2lWpMap(a2lWpMap);
    pidcA2lTreeStructureModel.setRvwQnaireRespMap(rvwQnaireRespMap);
    pidcA2lTreeStructureModel.setRvwQnaireRespVersMap(rvwQnaireRespVersMap);
    pidcA2lTreeStructureModel.setVarRespWpQniareMap(varRespWpQniareMap);
    pidcA2lTreeStructureModel.setWpRespStatusMap(wpRespStatusMap);
    pidcA2lTreeStructureModel.setRespWPA2lWpRespModelMap(respWpA2lWpRespModel);

    return pidcA2lTreeStructureModel;
  }


  /**
   * @param a2lVarGrp
   * @param a2lVarGrpVariantMapping
   * @return
   * @throws DataException
   */
  private A2lVariantGroup fetchVarGrpData(A2lVariantGroup a2lVarGrp,
      final A2lVarGrpVariantMapping a2lVarGrpVariantMapping)
      throws DataException {
    if (CommonUtils.isNotNull(a2lVarGrpVariantMapping)) {
      a2lVarGrp =
          new A2lVariantGroupLoader(getServiceData()).getDataObjectByID(a2lVarGrpVariantMapping.getA2lVarGroupId());
    }
    return a2lVarGrp;
  }


  /**
   * @param a2lWpDefVersionMap
   * @return
   */
  private A2lWpDefnVersion fetchActiveWpDefVersion(final Map<Long, A2lWpDefnVersion> a2lWpDefVersionMap) {
    A2lWpDefnVersion activeVersion = null;
    for (A2lWpDefnVersion a2lWpDefVersion : a2lWpDefVersionMap.values()) {
      if (a2lWpDefVersion.isActive()) {
        activeVersion = a2lWpDefVersion;
        break;
      }
    }
    return activeVersion;
  }


  /**
   * @param a2lVarGrp
   * @param pidcVarId
   * @param pidcVersId
   * @param projectId
   * @param activeVersion
   * @return
   */
  private A2LStructInternalModel getA2lStructureInternalModel(final A2lVariantGroup a2lVarGrp, final Long pidcVarId,
      final long pidcVersId, final A2lWpDefnVersion activeVersion) {
    A2LStructInternalModel a2lStructInternalModel = new A2LStructInternalModel();
    a2lStructInternalModel.setA2lVarGrp(a2lVarGrp);
    a2lStructInternalModel.setActiveVersion(activeVersion);
    a2lStructInternalModel.setPidcVarId(pidcVarId);
    a2lStructInternalModel.setPidcVersId(pidcVersId);
    return a2lStructInternalModel;
  }


  /**
   * @param pidcVarIdVarMap
   * @param varRespWpQniareMap
   * @param pidcVarIDRespIDWpIDStatusMap
   * @param a2lWpMap
   * @param a2lRespMap
   * @param rvwQnaireRespMap
   * @param rvwQnaireRespVersMap
   * @param a2lStructInternalModel
   * @throws IcdmException
   */
  private void fillAllA2lStructRelatedMap(final Map<Long, PidcVariant> pidcVarIdVarMap,
      final Map<Long, Map<Long, Map<Long, Set<Long>>>> varRespWpQniareMap,
      final Map<Long, Map<Long, Map<Long, String>>> pidcVarIDRespIDWpIDStatusMap,
      final Map<Long, Map<Long, Map<Long, A2lWPRespModel>>> pidcVarWPRespA2lWPRespModelMap,
      final Map<Long, A2lWorkPackage> a2lWpMap, final Map<Long, A2lResponsibility> a2lRespMap,
      final Map<Long, RvwQnaireResponse> rvwQnaireRespMap, final Map<Long, RvwQnaireRespVersion> rvwQnaireRespVersMap,
      final A2LStructInternalModel a2lStructInternalModel)
      throws IcdmException {
    Long varGrpId =
        a2lStructInternalModel.getA2lVarGrp() != null ? a2lStructInternalModel.getA2lVarGrp().getId() : null;
    Set<A2lWPRespModel> wpRespLabelResponse =
        getWpRespLabelResponse(varGrpId, a2lStructInternalModel.getActiveVersion().getId());

    Long pidcVarId = a2lStructInternalModel.getPidcVarId();

    CdrReportQnaireRespWrapper cdrReportQnaireRespWrapper = new RvwQnaireResponseLoader(getServiceData())
        .getQniareRespVersByPidcVersIdAndVarId(a2lStructInternalModel.getPidcVersId(), pidcVarId, true);

    for (A2lWPRespModel a2lWPRespModel : wpRespLabelResponse) {

      fillA2lRespMap(a2lRespMap, a2lWPRespModel.getA2lRespId());
      fillA2LWpMap(a2lWpMap, a2lWPRespModel.getA2lWpId());

      Map<Long, Set<RvwQnaireRespVersion>> wpRespVerMap =
          cdrReportQnaireRespWrapper.getAllWpRespQnaireRespVersMap().get(a2lWPRespModel.getA2lRespId());
      if (CommonUtils.isNotEmpty(wpRespVerMap)) {
        Set<RvwQnaireRespVersion> rvwRespVersSet = wpRespVerMap.get(a2lWPRespModel.getA2lWpId());
        if (CommonUtils.isNotEmpty(rvwRespVersSet)) {
          for (RvwQnaireRespVersion rvwQnaireRespVersion : rvwRespVersSet) {
            // rvwQnaireRespVersion is null for Simplified Qnaire
            if (CommonUtils.isNotNull(rvwQnaireRespVersion)) {
              RvwQnaireResponse rvwQnaireResponse =
                  cdrReportQnaireRespWrapper.getQnaireResponseMap().get(rvwQnaireRespVersion.getId());

              Long qnaireRespId = rvwQnaireResponse.getId();

              fillRvwQnaireRespMap(rvwQnaireRespMap, qnaireRespId);
              fillRvwQnaireRespVersMap(rvwQnaireRespVersMap, qnaireRespId);

              fillPidcVarAndVarRespWpQnaireMap(pidcVarIdVarMap, varRespWpQniareMap, pidcVarId, a2lWPRespModel,
                  qnaireRespId);
            }
            else {
              // Filling varRespWpQniareMap with null response to create empty WP/Resp structure under A2L for
              // Simplified
              // Qnaire
              fillPidcVarAndVarRespWpQnaireMap(pidcVarIdVarMap, varRespWpQniareMap, pidcVarId, a2lWPRespModel,
                  ApicConstants.SIMP_QUES_RESP_ID);
            }
            if (checkMatchingVaraintGroup(a2lStructInternalModel.getA2lVarGrp(), a2lWPRespModel)) {
              fillStatusNWPRespAtVarMap(pidcVarWPRespA2lWPRespModelMap, pidcVarIdVarMap, pidcVarId, a2lWPRespModel,
                  pidcVarIDRespIDWpIDStatusMap, new A2lWpResponsibilityStatusLoader(getServiceData()));
            }
          }
        }
      }
    }
  }


  /**
   * @param pidcVarRespWPA2lWpRespModelMap
   * @param pidcVarIdVarMap
   * @param pidcVarId
   * @param a2lWPRespModel
   * @param string
   * @param pidcVarIDRespIDWpIDStatusMap
   * @throws IcdmException
   */
  private void fillStatusNWPRespAtVarMap(
      final Map<Long, Map<Long, Map<Long, A2lWPRespModel>>> pidcVarRespWPA2lWpRespModelMap,
      final Map<Long, PidcVariant> pidcVarIdVarMap, final Long pidcVarId, final A2lWPRespModel a2lWPRespModel,
      final Map<Long, Map<Long, Map<Long, String>>> pidcVarIDRespIDWpIDStatusMap,
      final A2lWpResponsibilityStatusLoader a2lWpResponsibilityStatusLoader)
      throws IcdmException {

    if (CommonUtils.isNotNull(pidcVarId)) {
      fillPidcVarMap(pidcVarIdVarMap, pidcVarId);
    }

    Long varId = CommonUtils.isNull(pidcVarId) || CommonUtils.isEqual(pidcVarId, ApicConstants.NO_VARIANT_ID)
        ? ApicConstants.NO_VARIANT_ID : pidcVarId;


    fillWpRespMap(pidcVarRespWPA2lWpRespModelMap, varId, a2lWPRespModel);

    A2lWpResponsibilityStatus a2lWpRespStatus =
        a2lWpResponsibilityStatusLoader.getA2lWpStatusByVarAndWpRespId(pidcVarId, a2lWPRespModel.getWpRespId(),
            a2lWPRespModel.isInheritedFlag() ? null : a2lWPRespModel.getA2lRespId());

    if (CommonUtils.isNotNull(a2lWpRespStatus)) {
      fillWpRespStatusMap(pidcVarIDRespIDWpIDStatusMap, varId, a2lWPRespModel, a2lWpRespStatus.getWpRespFinStatus());
    }
  }


  /**
   * @param wpRespMap
   * @param noVariantId
   * @param a2lWPRespModel
   */
  private void fillWpRespMap(final Map<Long, Map<Long, Map<Long, A2lWPRespModel>>> wpRespMap, final Long pidcVarId,
      final A2lWPRespModel a2lWPRespModel) {
    Long a2lRespId = a2lWPRespModel.getA2lRespId();
    Long a2lWpId = a2lWPRespModel.getA2lWpId();

    Map<Long, Map<Long, A2lWPRespModel>> respWPWPRespIDMap;
    if (wpRespMap.containsKey(pidcVarId)) {
      respWPWPRespIDMap = wpRespMap.get(pidcVarId);

      if (respWPWPRespIDMap.containsKey(a2lRespId)) {
        respWPWPRespIDMap.get(a2lRespId).put(a2lWpId, a2lWPRespModel);
      }
      else {
        fillWPIDWPRespIDMap(a2lRespId, a2lWpId, respWPWPRespIDMap, a2lWPRespModel);
      }
    }
    else {
      respWPWPRespIDMap = new HashMap<>();
      fillWPIDWPRespIDMap(a2lRespId, a2lWpId, respWPWPRespIDMap, a2lWPRespModel);
      wpRespMap.put(pidcVarId, respWPWPRespIDMap);
    }

  }


  /**
   * @param a2lRespId
   * @param a2lWpId
   * @param respWPWPRespIDMap
   * @param a2lWPRespModel
   */
  private void fillWPIDWPRespIDMap(final Long a2lRespId, final Long a2lWpId,
      final Map<Long, Map<Long, A2lWPRespModel>> respWPWPRespIDMap, final A2lWPRespModel a2lWPRespModel) {
    Map<Long, A2lWPRespModel> wpIDWPRespIDMap = new HashMap<>();

    wpIDWPRespIDMap.put(a2lWpId, a2lWPRespModel);
    respWPWPRespIDMap.put(a2lRespId, wpIDWPRespIDMap);
  }


  /**
   * @param pidcVarRespIDWpIDStatusMap
   * @param pidcVarId
   * @param a2lWPRespModel
   * @throws IcdmException
   */
  private void fillWpRespStatusMap(final Map<Long, Map<Long, Map<Long, String>>> pidcVarRespIDWpIDStatusMap,
      final Long pidcVarId, final A2lWPRespModel a2lWPRespModel, final String wpFinStatus) {

    Long a2lRespId = a2lWPRespModel.getA2lRespId();
    Long a2lWpId = a2lWPRespModel.getA2lWpId();
    Map<Long, Map<Long, String>> respWpIDStatusMap;

    if (pidcVarRespIDWpIDStatusMap.containsKey(pidcVarId)) {

      respWpIDStatusMap = pidcVarRespIDWpIDStatusMap.get(pidcVarId);

      if (respWpIDStatusMap.containsKey(a2lRespId)) {
        respWpIDStatusMap.get(a2lRespId).put(a2lWpId, wpFinStatus);
      }
      else {
        fillWPStatusMap(a2lRespId, a2lWpId, respWpIDStatusMap, wpFinStatus);
      }
    }
    else {
      respWpIDStatusMap = new HashMap<>();
      fillWPStatusMap(a2lRespId, a2lWpId, respWpIDStatusMap, wpFinStatus);
      pidcVarRespIDWpIDStatusMap.put(pidcVarId, respWpIDStatusMap);
    }
  }


  /**
   * @param a2lRespId
   * @param a2lWpId
   * @param respIDWpIDStatusMap
   */
  private void fillWPStatusMap(final Long a2lRespId, final Long a2lWpId,
      final Map<Long, Map<Long, String>> respIDWpIDStatusMap, final String wpFinStatus) {
    Map<Long, String> wpIDStatusMap = new HashMap<>();
    wpIDStatusMap.put(a2lWpId, wpFinStatus);
    respIDWpIDStatusMap.put(a2lRespId, wpIDStatusMap);
  }


  /**
   * @param pidcVarIdVarMap
   * @param varRespWpQniareMap
   * @param pidcVarId
   * @param a2lWPRespModel
   * @param rvwQnaireRespId
   * @throws DataException
   */
  private void fillPidcVarAndVarRespWpQnaireMap(final Map<Long, PidcVariant> pidcVarIdVarMap,
      final Map<Long, Map<Long, Map<Long, Set<Long>>>> varRespWpQniareMap, final Long pidcVarId,
      final A2lWPRespModel a2lWPRespModel, final Long rvwQnaireRespId)
      throws DataException {

    if ((pidcVarId == null) || ApicConstants.NO_VARIANT_ID.equals(pidcVarId)) {
      // For no-variant variant id is -1
      fillVarRespWpQnaireMap(varRespWpQniareMap, ApicConstants.NO_VARIANT_ID, a2lWPRespModel.getA2lRespId(),
          a2lWPRespModel.getA2lWpId(), rvwQnaireRespId);
    }
    else {
      fillPidcVarMap(pidcVarIdVarMap, pidcVarId);
      fillVarRespWpQnaireMap(varRespWpQniareMap, pidcVarId, a2lWPRespModel.getA2lRespId(), a2lWPRespModel.getA2lWpId(),
          rvwQnaireRespId);
    }
  }

  /**
   * @param pidcVarIdVarMap
   * @param varLoader
   * @param variantId
   * @throws DataException
   */
  private void fillPidcVarMap(final Map<Long, PidcVariant> pidcVarIdVarMap, final long variantId) throws DataException {
    if (!pidcVarIdVarMap.containsKey(variantId)) {
      pidcVarIdVarMap.put(variantId, new PidcVariantLoader(getServiceData()).getDataObjectByID(variantId));
    }
  }

  /**
   * @param rvwQnaireRespVersMap key - qnaire resp id Value - Qnairerespversion working set
   * @param qnaireRespId qnaire resp id
   * @throws IcdmException exception in ws
   */
  private void fillRvwQnaireRespVersMap(final Map<Long, RvwQnaireRespVersion> rvwQnaireRespVersMap,
      final Long qnaireRespId)
      throws DataException {

    Map<Long, RvwQnaireRespVersion> retMap =
        new RvwQnaireRespVersionLoader(getServiceData()).getQnaireRespVersionsByRespId(qnaireRespId);

    for (RvwQnaireRespVersion rvwQnaireRespVersion : retMap.values()) {
      if (RvwQnaireRespVersionLoader.isWorkingSet(rvwQnaireRespVersion)) {
        rvwQnaireRespVersMap.put(qnaireRespId, rvwQnaireRespVersion);
      }
    }
  }

  /**
   * @param rvwQnaireRespMap
   * @param a2lRespId
   * @param qnaireRespId
   * @throws DataException
   */
  private void fillRvwQnaireRespMap(final Map<Long, RvwQnaireResponse> rvwQnaireRespMap, final long qnaireRespId)
      throws DataException {

    if (!rvwQnaireRespMap.containsKey(qnaireRespId)) {
      rvwQnaireRespMap.put(qnaireRespId, new RvwQnaireResponseLoader(getServiceData()).getDataObjectByID(qnaireRespId));
    }
  }

  /**
   * @param a2lWpMap
   * @param a2lWorkPackageLoader
   * @param a2lRespId
   * @param a2lWpId
   * @throws DataException
   */
  private void fillA2LWpMap(final Map<Long, A2lWorkPackage> a2lWpMap, final long a2lWpId) throws DataException {
    if (!a2lWpMap.containsKey(a2lWpId)) {
      a2lWpMap.put(a2lWpId, new A2lWorkPackageLoader(getServiceData()).getDataObjectByID(a2lWpId));
    }
  }

  /**
   * @param a2lRespMap
   * @param a2lRespLoader
   * @param a2lRespId
   * @throws DataException
   */
  private void fillA2lRespMap(final Map<Long, A2lResponsibility> a2lRespMap, final long a2lRespId)
      throws DataException {

    if (!a2lRespMap.containsKey(a2lRespId)) {
      a2lRespMap.put(a2lRespId, new A2lResponsibilityLoader(getServiceData()).getDataObjectByID(a2lRespId));
    }
  }

  /**
   * @param varRespWpQniareMap
   * @param variantId
   * @param a2lRespId
   * @param a2lWpId
   * @param qnaireRespId
   */
  private void fillVarRespWpQnaireMap(final Map<Long, Map<Long, Map<Long, Set<Long>>>> varRespWpQniareMap,
      final Long variantId, final Long a2lRespId, final Long a2lWpId, final Long qnaireRespId) {

    if (varRespWpQniareMap.containsKey(variantId)) {
      // check whether Resp Id is already available
      Map<Long, Map<Long, Set<Long>>> wpRespMap = varRespWpQniareMap.get(variantId);
      if (wpRespMap.containsKey(a2lRespId)) {
        // check whether Wp Id is already available
        Map<Long, Set<Long>> wpQnaireRespMap = wpRespMap.get(a2lRespId);
        if (wpQnaireRespMap.containsKey(a2lWpId)) {
          wpQnaireRespMap.get(a2lWpId).add(qnaireRespId);
        }
        else {
          // Add WP Id and Qnaire Resp Id
          Set<Long> qnaireRespIDSet = new java.util.HashSet<>();
          qnaireRespIDSet.add(qnaireRespId);
          wpQnaireRespMap.put(a2lWpId, qnaireRespIDSet);
        }
      }
      else {
        // Add Resp Id ,WP Id and Qnaire Resp Id
        Map<Long, Set<Long>> wpQnaireMap = new HashMap<>();
        Set<Long> qnaireRespIDSet = new java.util.HashSet<>();
        qnaireRespIDSet.add(qnaireRespId);
        wpQnaireMap.put(a2lWpId, qnaireRespIDSet);
        wpRespMap.put(a2lRespId, wpQnaireMap);
      }
    }
    else {
      addVarRespWpQnaireMap(varRespWpQniareMap, variantId, a2lRespId, a2lWpId, qnaireRespId);
    }
  }

  /**
   * @param varRespWpQniareMap
   * @param variantId
   * @param a2lRespId
   * @param a2lWpId
   * @param qnaireRespId
   */
  private void addVarRespWpQnaireMap(final Map<Long, Map<Long, Map<Long, Set<Long>>>> varRespWpQniareMap,
      final long variantId, final long a2lRespId, final long a2lWpId, final long qnaireRespId) {

    Map<Long, Map<Long, Set<Long>>> respWPQnaireMap = new HashMap<>();
    Map<Long, Set<Long>> wpQnaireMap = new HashMap<>();
    Set<Long> qnaireRespIDSet = new java.util.HashSet<>();
    qnaireRespIDSet.add(qnaireRespId);
    wpQnaireMap.put(a2lWpId, qnaireRespIDSet);
    respWPQnaireMap.put(a2lRespId, wpQnaireMap);
    varRespWpQniareMap.put(variantId, respWPQnaireMap);
  }


  /**
   * @param varGrpId
   * @param activeWpDefnVersID
   * @param projectId
   * @param pidcVersId
   * @param a2lRespModel
   * @return
   * @throws IcdmException
   */
  public Set<A2lWPRespModel> getWpRespLabelResponse(final Long varGrpId, final Long activeWpDefnVersID)
      throws IcdmException {

    Map<Long, A2lVariantGroup> a2lVarGrpMap = new HashMap<>();
    Map<Long, A2lWpParamMapping> a2lWParamInfoMap = new HashMap<>();
    Map<Long, A2lWpResponsibility> wpRespMap = new HashMap<>();

    TA2lWpDefnVersion ta2lWpDefnVersion =
        new A2lWpDefnVersionLoader(getServiceData()).getEntityObject(activeWpDefnVersID);

    List<TA2lWpResponsibility> ta2lWpRespPals = ta2lWpDefnVersion.getTA2lWpResponsibility();


    for (TA2lWpResponsibility tA2lWPRespPal : ta2lWpRespPals) {
      wpRespMap.put(tA2lWPRespPal.getWpRespId(),
          new A2lWpResponsibilityLoader(getServiceData()).getDataObjectByID(tA2lWPRespPal.getWpRespId()));

      for (TA2lWpParamMapping tA2lParamMapping : tA2lWPRespPal.getTA2lWpParamMappings()) {
        a2lWParamInfoMap.put(tA2lParamMapping.getWpParamMappingId(),
            new A2lWpParamMappingLoader(getServiceData()).getDataObjectByID(tA2lParamMapping.getWpParamMappingId()));
      }
    }

    ParamWpRespResolver resolver = new ParamWpRespResolver(a2lVarGrpMap, wpRespMap, a2lWParamInfoMap);

    return resolver.getWPRespForVariant(varGrpId);

  }

  /**
   * @param a2lVarGrp
   * @param a2lWpResponsibility
   * @return
   * @throws DataException
   */
  private boolean checkMatchingVaraintGroup(final A2lVariantGroup a2lVarGrp, final A2lWPRespModel a2lWPRespModel)
      throws DataException {
    A2lWpResponsibility a2lWpResponsibility =
        new A2lWpResponsibilityLoader(getServiceData()).getDataObjectByID(a2lWPRespModel.getWpRespId());
    return ((null == a2lVarGrp) && (null == a2lWpResponsibility.getVariantGrpId())) ||
        ((null != a2lVarGrp) && CommonUtils.isEqual(a2lWpResponsibility.getVariantGrpId(), a2lVarGrp.getId()));
  }
}
