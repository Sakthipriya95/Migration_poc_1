/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.cdr.cdfx.CDFxDelWpRespCommand;
import com.bosch.caltool.icdm.bo.cdr.cdfx.CDFxDelWpRespLoader;
import com.bosch.caltool.icdm.bo.cdr.cdfx.CdfxDelvryParamCommand;
import com.bosch.caltool.icdm.bo.cdr.cdfx.CdfxDelvryParamLoader;
import com.bosch.caltool.icdm.bo.cdr.cdfx.CdfxDlvryWpRespUniqueRespMerge;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.cdr.cdfx.TCDFxDeliveryParam;
import com.bosch.caltool.icdm.database.entity.cdr.cdfx.TCDFxDelvryWpResp;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibilityMergeModel;
import com.bosch.caltool.icdm.model.cdr.cdfx.CDFxDelWpResp;
import com.bosch.caltool.icdm.model.cdr.cdfx.CdfxDelvryParam;

/**
 * @author dmr1cob
 */
public class A2lRespMergeCdfxDlvryCommand extends AbstractSimpleCommand {

  private final A2lResponsibilityMergeModel mergeModel;

  private final Set<CDFxDelWpResp> cdfxDlvryWpRespOld = new HashSet<>();

  private final Map<Long, CDFxDelWpResp> cdfxDlvryWpRespUpdate = new HashMap<>();

  private final Set<CDFxDelWpResp> cdfxDlvryWpRespDelete = new HashSet<>();

  private final Set<CdfxDelvryParam> cdfxDelvryParamOld = new HashSet<>();

  private final Map<Long, CdfxDelvryParam> cdfxDelvryParamUpdate = new HashMap<>();

  /**
   * @param serviceData Service data
   * @param mergeModel Merge Model
   * @throws IcdmException Exception in webservice
   */
  public A2lRespMergeCdfxDlvryCommand(final ServiceData serviceData, final A2lResponsibilityMergeModel mergeModel)
      throws IcdmException {
    super(serviceData);
    this.mergeModel = mergeModel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {
    // Get destination a2l resp cdfxDlvrywpresp map Key - CdfxDlvryWpRespUniqueRespMerge value - cdfxdlvrywpresp id
    Map<CdfxDlvryWpRespUniqueRespMerge, Long> cdfDlvryUniqueMergeToMap = new HashMap<>();
    getDestCdfxDlvryWpRespList(cdfDlvryUniqueMergeToMap);

    for (Long a2lRespMergeFromId : this.mergeModel.getA2lRespMergeFromIdSet()) {
      mergeCdfxDlvryWpResp(a2lRespMergeFromId, cdfDlvryUniqueMergeToMap);
    }

    this.mergeModel.setCdfxDlvryWpRespUpdate(this.cdfxDlvryWpRespUpdate);
    this.mergeModel.setCdfxDlvryWpRespDelete(this.cdfxDlvryWpRespDelete);
    this.mergeModel.setCdfxDelvryParamUpdate(this.cdfxDelvryParamUpdate);

    getLogger().info("Update count : CDFX delivery wp resp - {}, CDFX delivery param - {}",
        this.cdfxDlvryWpRespUpdate.size(), this.cdfxDelvryParamUpdate.size());
    getLogger().info("Delete count : CDFX delivery wp resp - {}", this.cdfxDlvryWpRespDelete.size());
  }

  /**
   * In Cdfx delivery table the following fields deliveryId, wpid, resp id are unique fields. In order to prevent unique
   * constrain, check whether the data is already available. In this method fill the set using
   * {@link CdfxDlvryWpRespUniqueRespMerge} for source a2l responsibility
   *
   * @param a2lRespMergeFromId source a2l resp id
   * @param cdfDlvryUniqueMergeToMap Key is {@link CdfxDlvryWpRespUniqueRespMerge} and value is cdfxwprespid for
   *          destination a2l resp
   */
  private void mergeCdfxDlvryWpResp(final Long a2lRespMergeFromId,
      final Map<CdfxDlvryWpRespUniqueRespMerge, Long> cdfDlvryUniqueMergeToMap)
      throws IcdmException {
    A2lResponsibilityLoader a2lResponsibilityLoader = new A2lResponsibilityLoader(getServiceData());
    List<TCDFxDelvryWpResp> tCDFxDelWpRespMergeFromList =
        a2lResponsibilityLoader.getEntityObject(a2lRespMergeFromId).gettCDFxDelWpRespList();

    if (CommonUtils.isNotEmpty(tCDFxDelWpRespMergeFromList)) {
      Set<CdfxDlvryWpRespUniqueRespMerge> cdfxDlvryUniqueMergeFromSet = new HashSet<>();
      for (TCDFxDelvryWpResp tCDFxDelWpResp : tCDFxDelWpRespMergeFromList) {
        CdfxDlvryWpRespUniqueRespMerge cdfxDlvryUnique = new CdfxDlvryWpRespUniqueRespMerge(
            tCDFxDelWpResp.getCdfxDelWpRespId(), tCDFxDelWpResp.getTCdfxDelivery().getCdfxDeliveryId(),
            tCDFxDelWpResp.getWp().getA2lWpId(), tCDFxDelWpResp.getResp().getA2lRespId());
        cdfxDlvryUniqueMergeFromSet.add(cdfxDlvryUnique);
      }
      getLogger().info("Unique Wp resp combination for source a2l responsibility count - {}",
          cdfxDlvryUniqueMergeFromSet.size());

      updateCdfxDlvry(cdfDlvryUniqueMergeToMap, cdfxDlvryUniqueMergeFromSet);
    }
  }

  /**
   * Merge A2L responsibilty in parent and child tables
   *
   * @param cdfDlvryUniqueMergeToMap Key is {@link CdfxDlvryWpRespUniqueRespMerge} and value is cdfxwprespid for
   *          destination a2l resp
   * @param cdfxDlvryUniqueMergeFromSet destination a2lresp {@link CdfxDlvryWpRespUniqueRespMerge} set
   * @throws IcdmException exception in ws call
   */
  private void updateCdfxDlvry(final Map<CdfxDlvryWpRespUniqueRespMerge, Long> cdfDlvryUniqueMergeToMap,
      final Set<CdfxDlvryWpRespUniqueRespMerge> cdfxDlvryUniqueMergeFromSet)
      throws IcdmException {
    CDFxDelWpRespLoader cdfxDelWpRespLoader = new CDFxDelWpRespLoader(getServiceData());
    CDFxDelWpRespCommand cDFxDelWpRespCommand;

    for (CdfxDlvryWpRespUniqueRespMerge cdfxDlvryUnique : cdfxDlvryUniqueMergeFromSet) {
      cdfxDlvryUnique.setRespId(this.mergeModel.getA2lRespMergeToId());
      CDFxDelWpResp cdfxDlvryWpResp = cdfxDelWpRespLoader.getDataObjectByID(cdfxDlvryUnique.getCdfxDelWpRespId());

      if (!cdfDlvryUniqueMergeToMap.containsKey(cdfxDlvryUnique)) {
        cdfDlvryUniqueMergeToMap.put(cdfxDlvryUnique, cdfxDlvryUnique.getCdfxDelWpRespId());
        updateCdfxDlvryWpResp(cdfxDlvryWpResp);
      }
      else {
        List<TCDFxDeliveryParam> tcdFxDeliveryParamsList =
            cdfxDelWpRespLoader.getEntityObject(cdfxDlvryUnique.getCdfxDelWpRespId()).getTCDFxDeliveryParams();

        if (CommonUtils.isNotEmpty(tcdFxDeliveryParamsList)) {
          updateCdfxDlvryParam(cdfDlvryUniqueMergeToMap.get(cdfxDlvryUnique), tcdFxDeliveryParamsList);
        }

        cDFxDelWpRespCommand = new CDFxDelWpRespCommand(getServiceData(), cdfxDlvryWpResp, false, true);
        executeChildCommand(cDFxDelWpRespCommand);

        this.cdfxDlvryWpRespDelete.add(cdfxDlvryWpResp);
      }
    }
  }

  /**
   * In Cdfx delivery table the following fields deliveryId, wpid, resp id are unique fields. In order to prevent unique
   * constrain, check whether the data is already available. The data will be stored in a class
   * {@link CdfxDlvryWpRespUniqueRespMerge} where the equality will be checked using the unique fields(deliveryid, wpid,
   * respid). In this method fill the map where Key is {@link CdfxDlvryWpRespUniqueRespMerge} and value is cdfxwprespid
   * for destination a2l resp
   *
   * @param cdfDlvryUniqueMergeToMap cdfDlvryUniqueMergeToMap Key is {@link CdfxDlvryWpRespUniqueRespMerge} and value is
   *          cdfxwprespid
   */
  private void getDestCdfxDlvryWpRespList(final Map<CdfxDlvryWpRespUniqueRespMerge, Long> cdfDlvryUniqueMergeToMap) {
    A2lResponsibilityLoader a2lResponsibilityLoader = new A2lResponsibilityLoader(getServiceData());
    List<TCDFxDelvryWpResp> tCDFxDelWpRespMergeToList =
        a2lResponsibilityLoader.getEntityObject(this.mergeModel.getA2lRespMergeToId()).gettCDFxDelWpRespList();

    if (CommonUtils.isNotEmpty(tCDFxDelWpRespMergeToList)) {
      for (TCDFxDelvryWpResp tCDFxDelWpResp : tCDFxDelWpRespMergeToList) {
        CdfxDlvryWpRespUniqueRespMerge cdfxDlvryUnique = new CdfxDlvryWpRespUniqueRespMerge(
            tCDFxDelWpResp.getCdfxDelWpRespId(), tCDFxDelWpResp.getTCdfxDelivery().getCdfxDeliveryId(),
            tCDFxDelWpResp.getWp().getA2lWpId(), tCDFxDelWpResp.getResp().getA2lRespId());
        cdfDlvryUniqueMergeToMap.put(cdfxDlvryUnique, tCDFxDelWpResp.getCdfxDelWpRespId());
      }
    }
    getLogger().info("Unique Wp resp combination for destination a2l responsibility count - {}",
        cdfDlvryUniqueMergeToMap.size());
  }

  /**
   * Update cdfx wp resp id in {@link CdfxDelvryParam} which had src resp id
   *
   * @param cdfxDlvryWpRespId cdfxDlvryWpRespId
   * @param tcdFxDeliveryParamsList tcdFxDeliveryParamsList
   * @throws IcdmException Exception in ws call
   */
  private void updateCdfxDlvryParam(final Long cdfxDlvryWpRespId,
      final List<TCDFxDeliveryParam> tcdFxDeliveryParamsList)
      throws IcdmException {
    CdfxDelvryParamCommand cdfxDelvryParamCommand;
    CdfxDelvryParamLoader cdfxDelvryParamLoader = new CdfxDelvryParamLoader(getServiceData());

    for (TCDFxDeliveryParam tcdFxDeliveryParam : tcdFxDeliveryParamsList) {
      CdfxDelvryParam cdfxDelvryParam = cdfxDelvryParamLoader.getDataObjectByID(tcdFxDeliveryParam.getCdfxDelParamId());
      this.cdfxDelvryParamOld.add(cdfxDelvryParam.clone());
      cdfxDelvryParam.setCdfxDelvryWpRespId(cdfxDlvryWpRespId);
      cdfxDelvryParamCommand = new CdfxDelvryParamCommand(getServiceData(), cdfxDelvryParam, true, false);
      executeChildCommand(cdfxDelvryParamCommand);
    }
  }

  /**
   * Update to destination resp id
   *
   * @param cdfxDlvryWpResp update to destination resp id in {@link CDFxDelWpResp} object
   * @throws IcdmException Exception in ws call
   */
  private void updateCdfxDlvryWpResp(final CDFxDelWpResp cdfxDlvryWpResp) throws IcdmException {
    this.cdfxDlvryWpRespOld.add(cdfxDlvryWpResp.clone());
    cdfxDlvryWpResp.setRespId(this.mergeModel.getA2lRespMergeToId());
    CDFxDelWpRespCommand cDFxDelWpRespCommand =
        new CDFxDelWpRespCommand(getServiceData(), cdfxDlvryWpResp, true, false);
    executeChildCommand(cDFxDelWpRespCommand);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // No implementation
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

}
