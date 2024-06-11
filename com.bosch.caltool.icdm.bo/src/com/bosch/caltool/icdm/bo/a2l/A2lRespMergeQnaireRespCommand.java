/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand.COMMAND_MODE;
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireAnswerCommand;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireAnswerLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireAnswerOplCommand;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireAnswerOplLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireRespUniqueRespMerge;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireRespVariantCommand;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireRespVariantLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireRespVersionCommand;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireRespVersionLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireResponseCommand;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireResponseLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireAnswer;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireAnswerOpl;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireRespVariant;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireRespVersion;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireResponse;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibilityMergeModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswerOpl;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;

/**
 * @author dmr1cob
 */
public class A2lRespMergeQnaireRespCommand extends AbstractSimpleCommand {

  private final A2lResponsibilityMergeModel mergeModel;

  private final Set<RvwQnaireRespUniqueRespMerge> updateRvwQnaireRespUniqueRespMerge = new HashSet<>();

  private final Set<Long> deleteRvwQnaireRespId = new HashSet<>();

  private final Set<RvwQnaireResponse> rvwQnaireResponseOld = new HashSet<>();

  private final Map<Long, RvwQnaireResponse> rvwQnaireResponseUpdate = new HashMap<>();

  private final Set<RvwQnaireResponse> rvwQnaireResponseDelete = new HashSet<>();

  private final Set<RvwQnaireRespVariant> rvwQnaireRespVariantDelete = new HashSet<>();

  private final Set<RvwQnaireAnswer> rvwQnaireAnswerDelete = new HashSet<>();

  private final Set<RvwQnaireAnswerOpl> rvwQnaireAnswerOplDelete = new HashSet<>();

  private final Set<RvwQnaireRespVersion> rvwQnaireRespVersionDelete = new HashSet<>();

  // List of simplified Qnaire Resp Records in TRvwQnaireRespVariants table for Source Responsibilities
  private final Set<RvwQnaireRespVariant> rvwSimpQnaireRespVariantsInSrc = new HashSet<>();

  // List of simplified Qnaire Resp Records in TRvwQnaireRespVariants table for Dest Responsibility
  private final Set<RvwQnaireRespVariant> rvwSimpQnaireRespVariantsInDest = new HashSet<>();

  // List of simplified Qnaire Resp Records to be deleted from TRvwQnaireRespVariants table
  private final Set<RvwQnaireRespVariant> rvwSimpQnaireRespVarsToBeDeleted = new HashSet<>();


  /**
   * @param serviceData Service data
   * @param mergeModel Merge Model
   * @throws IcdmException Exception in webservice
   */
  public A2lRespMergeQnaireRespCommand(final ServiceData serviceData, final A2lResponsibilityMergeModel mergeModel)
      throws IcdmException {
    super(serviceData);
    this.mergeModel = mergeModel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {
    // Get destination a2l resp RvwQnaireResp map Key - RvwQnaireRespUniqueRespMerge Value - RvwQnaireResp Id
    Map<RvwQnaireRespUniqueRespMerge, Long> rvwQnaireRespUniqueRespMergeMap = new HashMap<>();
    getDestRvwQnaireRespMap(rvwQnaireRespUniqueRespMergeMap);

    for (Long a2lRespMergeFromId : this.mergeModel.getA2lRespMergeFromIdSet()) {
      mergeRvwQnaireWpResp(a2lRespMergeFromId, rvwQnaireRespUniqueRespMergeMap);
    }

    fillRvwQnaireRespVarDelList();
    delRvwSimpQnaireRespVars();
    updRvwSimpQnaireRespVars();

    deleteRvwQnaireResponse();
    updateRvwQnaireResponse();
  }

  /**
   *
   */
  private void fillRvwQnaireRespVarDelList() {
    for (RvwQnaireRespVariant srcQnaireRespVariant : this.rvwSimpQnaireRespVariantsInSrc) {
      for (RvwQnaireRespVariant destQnaireRespVariant : this.rvwSimpQnaireRespVariantsInDest) {
        // if simplified Qnaire is already available in dest responsibility under same WP and variant, delete the record
        // of Source from TRwQnaireRespVariants table
        if (CommonUtils.isEqual(srcQnaireRespVariant.getA2lWpId(), destQnaireRespVariant.getA2lWpId()) &&
            CommonUtils.isEqual(srcQnaireRespVariant.getVariantId(), destQnaireRespVariant.getVariantId())) {
          this.rvwSimpQnaireRespVarsToBeDeleted.add(srcQnaireRespVariant);
          // Remove rvwQnaireRespVar record to be deleted from rvwSimpQnaireRespVariantsInSrc set so that the remaining
          // available record in rvwSimpQnaireRespVariantsInSrc will be updated to destination A2l Responsibility
          this.rvwSimpQnaireRespVariantsInSrc.remove(srcQnaireRespVariant);
        }
      }
    }
  }


  /**
   * @throws IcdmException
   */
  private void delRvwSimpQnaireRespVars() throws IcdmException {
    for (RvwQnaireRespVariant rvwQnaireRespVarTobeDel : this.rvwSimpQnaireRespVarsToBeDeleted) {
      RvwQnaireRespVariantCommand rvwQnaireRespVarCmd =
          new RvwQnaireRespVariantCommand(getServiceData(), rvwQnaireRespVarTobeDel, false, true);
      executeChildCommand(rvwQnaireRespVarCmd);
    }
  }

  /**
   * @throws IcdmException
   */
  private void updRvwSimpQnaireRespVars() throws IcdmException {
    for (RvwQnaireRespVariant rvwQnaireRespVarTobeUpd : this.rvwSimpQnaireRespVariantsInSrc) {
      rvwQnaireRespVarTobeUpd.setA2lRespId(this.mergeModel.getA2lRespMergeToId());
      RvwQnaireRespVariantCommand rvwQnaireRespVarCmd =
          new RvwQnaireRespVariantCommand(getServiceData(), rvwQnaireRespVarTobeUpd, true, false);
      executeChildCommand(rvwQnaireRespVarCmd);
    }
  }

  /**
   * In Rvw Qnaire response table the following fields qnaire version id, wpid, resp id are unique fields. In order to
   * prevent unique constrain, check whether the data is already available. The data will be stored in a class
   * {@link RvwQnaireRespUniqueRespMerge} where the equality will be checked using the unique fields(qnaireversionid,
   * wpid, respid). In this method fill the set for source a2l resp
   *
   * @param a2lRespMergeFromId src resp id
   * @param rvwQnaireRespUniqueRespMergeMap rvwQnaireRespUniqueRespMergeMap
   * @throws DataException
   */
  private void mergeRvwQnaireWpResp(final Long a2lRespMergeFromId,
      final Map<RvwQnaireRespUniqueRespMerge, Long> rvwQnaireRespUniqueRespMergeMap)
      throws DataException {

    List<Long> a2lRespIdList = new ArrayList<>();
    a2lRespIdList.add(a2lRespMergeFromId);

    List<TRvwQnaireRespVariant> tRvwQnaireRespVariantList =
        new RvwQnaireRespVariantLoader(getServiceData()).getQnaireRespVariantListForA2lRespIds(a2lRespIdList);

    if (CommonUtils.isNotEmpty(tRvwQnaireRespVariantList)) {
      Set<RvwQnaireRespUniqueRespMerge> rvwQnaireRespUniqueRespMergeSet = new HashSet<>();

      for (TRvwQnaireRespVariant tRvwQnaireRespVariant : tRvwQnaireRespVariantList) {
        TRvwQnaireResponse tRvwQnaireResponse = tRvwQnaireRespVariant.getTRvwQnaireResponse();

        // tRvwQnaireResponse is Null for Simplified Qnaire
        if (CommonUtils.isNotNull(tRvwQnaireResponse)) {
          RvwQnaireRespUniqueRespMerge rvwQnaireRespUniqueRespMerge =
              new RvwQnaireRespUniqueRespMerge(tRvwQnaireResponse.getQnaireRespId(),
                  tRvwQnaireResponse.getTRvwQnaireRespVersions().iterator().next().getTQuestionnaireVersion()
                      .getQnaireVersId(),
                  tRvwQnaireRespVariant.gettA2lWorkPackage().getA2lWpId(),
                  tRvwQnaireRespVariant.gettA2lResponsibility().getA2lRespId());
          rvwQnaireRespUniqueRespMergeSet.add(rvwQnaireRespUniqueRespMerge);
        }
        else {
          this.rvwSimpQnaireRespVariantsInSrc.add(new RvwQnaireRespVariantLoader(getServiceData())
              .getDataObjectByID(tRvwQnaireRespVariant.getQnaireRespVarId()));
        }
      }

      getLogger().info("Unique wp resp combination for source a2l responsibility - {}",
          rvwQnaireRespUniqueRespMergeSet.size());

      fillUpdateQnaireResponseData(rvwQnaireRespUniqueRespMergeMap, rvwQnaireRespUniqueRespMergeSet);
    }
  }

  /**
   * @throws IcdmException
   * @throws CloneNotSupportedException
   */
  private void updateRvwQnaireResponse() throws IcdmException {
    RvwQnaireResponseLoader rvwQnaireResponseLoader = new RvwQnaireResponseLoader(getServiceData());
    RvwQnaireResponseCommand rvwQnaireResponseCommand;
    RvwQnaireResponse rvwQnaireResp;

    for (RvwQnaireRespUniqueRespMerge rvwQnaireRespUniqueRespMerge : this.updateRvwQnaireRespUniqueRespMerge) {
      rvwQnaireResp = rvwQnaireResponseLoader.getDataObjectByID(rvwQnaireRespUniqueRespMerge.getQnaireRespId());
      this.rvwQnaireResponseOld.add(rvwQnaireResp.clone());
      rvwQnaireResp.setA2lRespId(this.mergeModel.getA2lRespMergeToId());
      rvwQnaireResponseCommand = new RvwQnaireResponseCommand(getServiceData(), rvwQnaireResp, true, false, false);
      executeChildCommand(rvwQnaireResponseCommand);

    }

    this.mergeModel.setRvwQnaireResponseOld(this.rvwQnaireResponseOld);
    this.mergeModel.setRvwQnaireResponseUpdate(this.rvwQnaireResponseUpdate);

    getLogger().info("Update count : Rvw qnaire response - {}", this.rvwQnaireResponseUpdate.size());
  }

  /**
   * @throws IcdmException
   */
  private void deleteRvwQnaireResponse() throws IcdmException {
    RvwQnaireResponseLoader rvwQnaireResponseLoader = new RvwQnaireResponseLoader(getServiceData());
    RvwQnaireResponseCommand rvwQnaireResponseCommand;
    RvwQnaireResponse rvwQnaireResp;

    for (Long qnaireRespId : this.deleteRvwQnaireRespId) {
      rvwQnaireResp = rvwQnaireResponseLoader.getDataObjectByID(qnaireRespId);
      TRvwQnaireResponse tRvwQnaireResp = rvwQnaireResponseLoader.getEntityObject(qnaireRespId);
      deleteRvwQnaireRespVariant(tRvwQnaireResp);
      deleteRvwQnaireRespAnswer(tRvwQnaireResp);
      deleteRvwQnaireRespVersion(tRvwQnaireResp);
      rvwQnaireResponseCommand = new RvwQnaireResponseCommand(getServiceData(), rvwQnaireResp, false, true, false);
      executeChildCommand(rvwQnaireResponseCommand);
      this.rvwQnaireResponseDelete.add(rvwQnaireResp);
    }

    this.mergeModel.setRvwQnaireResponseDelete(this.rvwQnaireResponseDelete);
    this.mergeModel.setRvwQnaireRespVariantDelete(this.rvwQnaireRespVariantDelete);
    this.mergeModel.setRvwQnaireAnswerDelete(this.rvwQnaireAnswerDelete);
    this.mergeModel.setRvwQnaireAnswerOplDelete(this.rvwQnaireAnswerOplDelete);
    this.mergeModel.setRvwQnaireRespVersionDelete(this.rvwQnaireRespVersionDelete);

    getLogger().info(
        "Delete count : Rvw qnaire response - {}, Rvw qnaire response variant - {}, Rvw qnaire response answer - {}, Rvw qnaire response answer option - {},Rvw qnaire response version - {}, Rvw qnaire response result - {}",
        this.rvwQnaireResponseDelete.size(), this.rvwQnaireRespVariantDelete.size(), this.rvwQnaireAnswerDelete.size(),
        this.rvwQnaireAnswerOplDelete.size(), this.rvwQnaireRespVersionDelete.size());
  }

  /**
   * @param tRvwQnaireResp
   * @throws IcdmException
   */
  private void deleteRvwQnaireRespVersion(final TRvwQnaireResponse tRvwQnaireResp) throws IcdmException {
    RvwQnaireRespVersion rvwQnaireRespVersion;
    RvwQnaireRespVersionCommand rvwQnaireRespVersionCommand;

    if (CommonUtils.isNotEmpty(tRvwQnaireResp.getTRvwQnaireRespVersions())) {
      Set<TRvwQnaireRespVersion> tRvwQnaireRespVersionsSet = new HashSet<>(tRvwQnaireResp.getTRvwQnaireRespVersions());

      for (TRvwQnaireRespVersion tRvwQnaireRespVersion : tRvwQnaireRespVersionsSet) {
        rvwQnaireRespVersion = new RvwQnaireRespVersionLoader(getServiceData())
            .getDataObjectByID(tRvwQnaireRespVersion.getQnaireRespVersId());
        rvwQnaireRespVersionCommand =
            new RvwQnaireRespVersionCommand(getServiceData(), rvwQnaireRespVersion, false, true, false, false);
        executeChildCommand(rvwQnaireRespVersionCommand);
        this.rvwQnaireRespVersionDelete.add(rvwQnaireRespVersion);
      }
    }
  }

  /**
   * @param tRvwQnaireResp
   * @throws IcdmException
   */
  private void deleteRvwQnaireRespAnswer(final TRvwQnaireResponse tRvwQnaireResp) throws IcdmException {
    for (TRvwQnaireRespVersion tRvwQnaireRespVersion : tRvwQnaireResp.getTRvwQnaireRespVersions()) {
      if (CommonUtils.isNotEmpty(tRvwQnaireRespVersion.getTRvwQnaireAnswers())) {
        Set<TRvwQnaireAnswer> tRvwQnaireAnswersSet = new HashSet<>(tRvwQnaireRespVersion.getTRvwQnaireAnswers());
        RvwQnaireAnswer rvwQnaireAnswer;
        RvwQnaireAnswerCommand rvwQnaireAnswerCommand;
        RvwQnaireAnswerLoader rvwQnaireAnswerLoader = new RvwQnaireAnswerLoader(getServiceData());

        for (TRvwQnaireAnswer tRvwQnaireAnswer : tRvwQnaireAnswersSet) {
          rvwQnaireAnswer = rvwQnaireAnswerLoader.getDataObjectByID(tRvwQnaireAnswer.getRvwAnswerId());
          deleteRvwQnaireAnsOpl(tRvwQnaireAnswer);
          rvwQnaireAnswerCommand = new RvwQnaireAnswerCommand(getServiceData(), rvwQnaireAnswer, COMMAND_MODE.DELETE);
          executeChildCommand(rvwQnaireAnswerCommand);
          this.rvwQnaireAnswerDelete.add(rvwQnaireAnswer);
        }
      }
    }
  }

  /**
   * @param rvwQnaireAnswerLoader
   * @param tRvwQnaireAnswer
   * @throws DataException
   * @throws IcdmException
   */
  private void deleteRvwQnaireAnsOpl(final TRvwQnaireAnswer tRvwQnaireAnswer) throws IcdmException {
    if (CommonUtils.isNotEmpty(tRvwQnaireAnswer.getTQnaireAnsOpenPoints())) {
      Set<TRvwQnaireAnswerOpl> tQnaireAnsOpenPointsSet = new HashSet<>(tRvwQnaireAnswer.getTQnaireAnsOpenPoints());
      RvwQnaireAnswerOpl rvwQnaireAnswerOpl;
      RvwQnaireAnswerOplCommand rvwQnaireAnswerOplCommand;

      for (TRvwQnaireAnswerOpl tRvwQnaireAnswerOpl : tQnaireAnsOpenPointsSet) {
        rvwQnaireAnswerOpl =
            new RvwQnaireAnswerOplLoader(getServiceData()).getDataObjectByID(tRvwQnaireAnswerOpl.getOpenPointsId());
        rvwQnaireAnswerOplCommand =
            new RvwQnaireAnswerOplCommand(getServiceData(), rvwQnaireAnswerOpl, COMMAND_MODE.DELETE);
        executeChildCommand(rvwQnaireAnswerOplCommand);
        this.rvwQnaireAnswerOplDelete.add(rvwQnaireAnswerOpl);
      }
    }
  }

  /**
   * @param rvwQnaireResponseLoader
   * @param qnaireRespId
   * @throws IcdmException
   */
  private void deleteRvwQnaireRespVariant(final TRvwQnaireResponse tRvwQnaireResp) throws IcdmException {
    if (CommonUtils.isNotEmpty(tRvwQnaireResp.getTRvwQnaireRespVariants())) {
      Set<TRvwQnaireRespVariant> tRvwQnaireRespVariantsSet = new HashSet<>(tRvwQnaireResp.getTRvwQnaireRespVariants());
      RvwQnaireRespVariant rvwQnaireRespVariant;
      RvwQnaireRespVariantCommand rvwQnaireRespVariantCommand;

      for (TRvwQnaireRespVariant tRvwQnaireRespVariant : tRvwQnaireRespVariantsSet) {
        rvwQnaireRespVariant = new RvwQnaireRespVariantLoader(getServiceData())
            .getDataObjectByID(tRvwQnaireRespVariant.getQnaireRespVarId());
        rvwQnaireRespVariantCommand =
            new RvwQnaireRespVariantCommand(getServiceData(), rvwQnaireRespVariant, false, true);
        executeChildCommand(rvwQnaireRespVariantCommand);
        this.rvwQnaireRespVariantDelete.add(rvwQnaireRespVariant);
      }
    }
  }

  /**
   * Fill data for update and delete of parent and child tables for merging a2l response
   *
   * @param rvwQnaireRespUniqueRespMergeMap rvwQnaireRespUniqueRespMergeMap
   * @param rvwQnaireRespUniqueRespMergeSet rvwQnaireRespUniqueRespMergeSet
   * @throws IcdmException Exception in ws call
   */
  private void fillUpdateQnaireResponseData(
      final Map<RvwQnaireRespUniqueRespMerge, Long> rvwQnaireRespUniqueRespMergeMap,
      final Set<RvwQnaireRespUniqueRespMerge> rvwQnaireRespUniqueRespMergeSet) {
    for (RvwQnaireRespUniqueRespMerge rvwQnaireRespUniqueRespMerge : rvwQnaireRespUniqueRespMergeSet) {
      rvwQnaireRespUniqueRespMerge.setA2lRespId(this.mergeModel.getA2lRespMergeToId());

      if (!rvwQnaireRespUniqueRespMergeMap.containsKey(rvwQnaireRespUniqueRespMerge)) {
        rvwQnaireRespUniqueRespMergeMap.put(rvwQnaireRespUniqueRespMerge,
            rvwQnaireRespUniqueRespMerge.getQnaireRespId());
        this.updateRvwQnaireRespUniqueRespMerge.add(rvwQnaireRespUniqueRespMerge);
      }
      else {
        if (this.mergeModel.getQnaireRespIdSet().contains(rvwQnaireRespUniqueRespMerge.getQnaireRespId())) {
          this.updateRvwQnaireRespUniqueRespMerge.remove(rvwQnaireRespUniqueRespMerge);
          this.deleteRvwQnaireRespId.add(rvwQnaireRespUniqueRespMergeMap.remove(rvwQnaireRespUniqueRespMerge));
          rvwQnaireRespUniqueRespMergeMap.put(rvwQnaireRespUniqueRespMerge,
              rvwQnaireRespUniqueRespMerge.getQnaireRespId());
          this.updateRvwQnaireRespUniqueRespMerge.add(rvwQnaireRespUniqueRespMerge);
        }
        else {
          this.deleteRvwQnaireRespId.add(rvwQnaireRespUniqueRespMerge.getQnaireRespId());
        }
      }
    }
  }

  /**
   * In Rvw Qnaire response table the following fields qnaire version id, wpid, resp id are unique fields. In order to
   * prevent unique constrain, check whether the data is already available. The data will be stored in a class
   * {@link RvwQnaireRespUniqueRespMerge} where the equality will be checked using the unique fields(qnaireversionid,
   * wpid, respid). In this method fill the map where Key is {@link rvwQnaireRespUniqueMap} and value is RvwQnairerespid
   * for destination a2l resp
   *
   * @param rvwQnaireRespUniqueMap rvwQnaireRespUniqueMap Key is {@link rvwQnaireRespUniqueMap} and value is
   *          RvwQnairerespid
   * @throws DataException
   */
  private void getDestRvwQnaireRespMap(final Map<RvwQnaireRespUniqueRespMerge, Long> rvwQnaireRespUniqueMap)
      throws DataException {

    List<Long> a2lRespIdList = new ArrayList<>();
    a2lRespIdList.add(this.mergeModel.getA2lRespMergeToId());

    List<TRvwQnaireRespVariant> tRvwQnaireRespVariantList =
        new RvwQnaireRespVariantLoader(getServiceData()).getQnaireRespVariantListForA2lRespIds(a2lRespIdList);

    if (CommonUtils.isNotEmpty(tRvwQnaireRespVariantList)) {
      for (TRvwQnaireRespVariant tRvwQnaireRespVariant : tRvwQnaireRespVariantList) {

        TRvwQnaireResponse tRvwQnaireResponse = tRvwQnaireRespVariant.getTRvwQnaireResponse();
        // tRvwQnaireResp is null for simplified Qnaire
        if (CommonUtils.isNotNull(tRvwQnaireResponse)) {
          RvwQnaireRespUniqueRespMerge rvwQnaireRespUniqueRespMerge =
              new RvwQnaireRespUniqueRespMerge(tRvwQnaireResponse.getQnaireRespId(),
                  tRvwQnaireResponse.getTRvwQnaireRespVersions().iterator().next().getTQuestionnaireVersion()
                      .getQnaireVersId(),
                  tRvwQnaireRespVariant.gettA2lWorkPackage().getA2lWpId(),
                  tRvwQnaireRespVariant.gettA2lResponsibility().getA2lRespId());
          rvwQnaireRespUniqueMap.put(rvwQnaireRespUniqueRespMerge, tRvwQnaireResponse.getQnaireRespId());
        }
        else {
          this.rvwSimpQnaireRespVariantsInDest.add(new RvwQnaireRespVariantLoader(getServiceData())
              .getDataObjectByID(tRvwQnaireRespVariant.getQnaireRespVarId()));
        }
      }
    }

    getLogger().info("Unique wp resp combination for destination a2l responsibility - {}",
        rvwQnaireRespUniqueMap.size());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // Not Applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }
}
