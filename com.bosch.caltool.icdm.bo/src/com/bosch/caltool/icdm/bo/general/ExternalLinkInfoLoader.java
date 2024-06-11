/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.general;

import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import com.bosch.calcomp.externallink.LinkRegistry;
import com.bosch.caltool.datamodel.core.ModelTypeRegistry;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2lResponsibilityLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWorkPackageLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcSubVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.cdr.CDRReviewResultLoader;
import com.bosch.caltool.icdm.bo.cdr.RuleSetLoader;
import com.bosch.caltool.icdm.bo.cdr.RvwVariantLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireRespVariantLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireResponseLoader;
import com.bosch.caltool.icdm.bo.uc.UseCaseGroupLoader;
import com.bosch.caltool.icdm.bo.uc.UseCaseLoader;
import com.bosch.caltool.icdm.bo.uc.UseCaseSectionLoader;
import com.bosch.caltool.icdm.bo.uc.UsecaseFavoriteLoader;
import com.bosch.caltool.icdm.common.bo.general.EXTERNAL_LINK_TYPE;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;
import com.bosch.caltool.icdm.model.general.ExternalLinkInfo;


/**
 * @author bne4cob
 */
public class ExternalLinkInfoLoader extends AbstractSimpleBusinessObject {

  /**
   *
   */
  private static final String LINK_ID_SEPARATOR = "-";

  /**
   * @param serviceData Service Data
   */
  public ExternalLinkInfoLoader(final ServiceData serviceData) {
    super(serviceData);
  }


  /**
   * Get link information for the given linkable object
   *
   * @param objId linkable object's ID
   * @param typeCode model type code
   * @param additionalDetails extra details to create the link
   * @return ExternalLinkInfo
   * @throws IcdmException error while fetching details
   */
  public ExternalLinkInfo getLinkInfo(final Long objId, final String typeCode,
      final Map<String, String> additionalDetails)
      throws IcdmException {
    MODEL_TYPE type = (MODEL_TYPE) ModelTypeRegistry.INSTANCE.getTypeByCode(typeCode);

    ExternalLinkInfo ret = null;

    EXTERNAL_LINK_TYPE linkType = EXTERNAL_LINK_TYPE.getLinkType(typeCode);
    switch (linkType) {
      case A2L_FILE:
        ret = getLinkInfoA2l(objId);
        break;
      case CDR_RESULT:
        ret = getLinkInfoCdrResult(objId);
        break;
      case CDR_RESULT_VAR:
        ret = getLinkInfoCdrResultVar(objId);
        break;
      case QNAIRE_RESPONSE:
        ret = getLinkInfoForQnaireResp(objId);
        break;
      case QNAIRE_RESPONSE_VAR:
        ret = getLinkInfoForQnaireRespWithVariant(objId);
        break;
      case PIDC:
        ret = getLinkInfoPidc(objId);
        break;
      case PIDC_VARIANT:
        ret = getLinkInfoPidcVariant(objId);
        break;
      case SUB_VARIANT:
        ret = getLinkInfoPidcSubVariant(objId);
        break;
      case PIDC_VERSION:
        ret = getLinkInfoPidcVersion(objId);
        break;
      case RULE_SET:
        ret = getLinkInfoRuleSet(objId, additionalDetails);
        break;
      case USE_CASE:
        ret = getLinkInfoUcPidcVersion(additionalDetails);
        break;
      case USE_CASE_GROUP:
        ret = getLinkInfoUcGrpPidcVersion(additionalDetails);
        break;
      case USE_CASE_SECT:
        ret = getLinkInfoUcSecPidcVersion(additionalDetails);
        break;
      case PROJECT_USE_CASE:
        ret = getLinkForProjUseCase(objId, additionalDetails);
        break;

      default:
        throw new IcdmException("Invalid type code " + type.getTypeCode() + ". This is not a linkable object.");
    }

    return ret;
  }


  /**
   * @param ucFavId
   * @param pidcVersId
   * @param ucItemId
   * @return
   * @throws IcdmException
   */
  private ExternalLinkInfo getLinkForProjUseCase(final Long ucFavId, final Map<String, String> additionalDetails)
      throws IcdmException {
    Long pidcVersId = null;
    Long ucItemId = null;
    if (!additionalDetails.isEmpty()) {
      pidcVersId = Long.valueOf(additionalDetails.get(ApicConstants.PIDC_VERS_ID_STR));
      ucItemId = Long.valueOf(additionalDetails.get(ApicConstants.SELECTED_UC_ITEM_ID_STR));
    }
    checkForValidExtraInfo(pidcVersId, ucItemId);
    UsecaseFavoriteLoader ucFavLdr = new UsecaseFavoriteLoader(getServiceData());
    ExternalLinkInfo ret = new ExternalLinkInfo();
    ret.setDisplayText(ucFavLdr.getExtendedName(pidcVersId, ucItemId));
    ret.setUrl(getUrl(EXTERNAL_LINK_TYPE.PROJECT_USE_CASE, ucFavId.toString().concat(LINK_ID_SEPARATOR)
        .concat(pidcVersId.toString()).concat(LINK_ID_SEPARATOR).concat(ucItemId.toString())));
    ret.setLinkableObjId(ucItemId);
    ret.setLinkType(EXTERNAL_LINK_TYPE.PROJECT_USE_CASE.getName());
    return ret;
  }


  /**
   * @param linkType
   * @param ruleSetId
   * @param additionalDetails
   * @return
   * @throws IcdmException
   */
  private ExternalLinkInfo getLinkInfoRuleSet(final Long ruleSetId, final Map<String, String> additionalDetails)
      throws IcdmException {
    RuleSetLoader ruleSetLdr = new RuleSetLoader(getServiceData());

    String ruleSet = additionalDetails.isEmpty() ? ruleSetId.toString()
        : ruleSetId.toString().concat(LINK_ID_SEPARATOR).concat(additionalDetails.get(ApicConstants.RULESET_PARAM_ID))
            .concat(LINK_ID_SEPARATOR).concat(additionalDetails.get(ApicConstants.RULE_ID));
    ExternalLinkInfo ret = new ExternalLinkInfo();
    ret.setDisplayText(ruleSetLdr.getExtendedName(ruleSetId, additionalDetails));
    ret.setUrl(getUrl(EXTERNAL_LINK_TYPE.RULE_SET, ruleSet));
    ret.setLinkableObjId(ruleSetId);
    ret.setLinkType(EXTERNAL_LINK_TYPE.RULE_SET.getName());

    return ret;
  }


  /**
   * @param linkType
   * @param variantId
   * @return
   * @throws DataException
   */
  private ExternalLinkInfo getLinkInfoPidcVariant(final Long variantId) throws DataException {
    PidcVariantLoader varLdr = new PidcVariantLoader(getServiceData());
    PidcVariant variant = varLdr.getDataObjectByID(variantId);

    ExternalLinkInfo ret = new ExternalLinkInfo();
    ret.setDisplayText(varLdr.getExtendedName(variantId));

    // Key ID to construct the link
    String keyId = variant.getPidcVersionId() + LINK_ID_SEPARATOR + variant.getId();
    ret.setUrl(getUrl(EXTERNAL_LINK_TYPE.PIDC_VARIANT, keyId));

    ret.setLinkableObjId(variant.getId());
    ret.setLinkType(EXTERNAL_LINK_TYPE.PIDC_VARIANT.getName());

    return ret;
  }


  /**
   * @param linkType
   * @param subVariantId
   * @return
   * @throws DataException
   */
  private ExternalLinkInfo getLinkInfoPidcSubVariant(final Long subVariantId) throws DataException {
    PidcSubVariantLoader subVarLdr = new PidcSubVariantLoader(getServiceData());
    PidcSubVariant subVariant = subVarLdr.getDataObjectByID(subVariantId);

    ExternalLinkInfo ret = new ExternalLinkInfo();
    ret.setDisplayText(subVarLdr.getExtendedName(subVariantId));

    // Key ID to construct the link
    String keyId = subVariant.getPidcVersionId() + LINK_ID_SEPARATOR + subVariant.getId();
    ret.setUrl(getUrl(EXTERNAL_LINK_TYPE.SUB_VARIANT, keyId));

    ret.setLinkableObjId(subVariant.getId());
    ret.setLinkType(EXTERNAL_LINK_TYPE.SUB_VARIANT.getName());

    return ret;
  }

  /**
   * @param linkType
   * @param objId
   * @return
   * @throws DataException
   */
  private ExternalLinkInfo getLinkInfoPidc(final Long pidcId) throws DataException {
    PidcVersionLoader pidcVersLdr = new PidcVersionLoader(getServiceData());
    PidcVersion activePidcVers = pidcVersLdr.getActivePidcVersion(pidcId);

    ExternalLinkInfo ret = new ExternalLinkInfo();
    ret.setDisplayText(pidcVersLdr.getExtendedName(activePidcVers.getId()));
    ret.setUrl(getUrl(EXTERNAL_LINK_TYPE.PIDC_VERSION, activePidcVers.getId().toString()));
    ret.setLinkableObjId(activePidcVers.getId());
    ret.setLinkType(EXTERNAL_LINK_TYPE.PIDC_VERSION.getName());

    return ret;
  }

  /**
   * @param linkType
   * @param pidcVersId
   * @param ucId
   * @return external link info
   * @throws DataException
   */
  private ExternalLinkInfo getLinkInfoUcPidcVersion(final Map<String, String> additionalDetails) throws IcdmException {
    Long pidcVersId = null;
    Long ucId = null;
    if (!additionalDetails.isEmpty()) {
      pidcVersId = Long.valueOf(additionalDetails.get(ApicConstants.PIDC_VERS_ID_STR));
      ucId = Long.valueOf(additionalDetails.get(ApicConstants.SELECTED_UC_ITEM_ID_STR));
    }
    checkForValidExtraInfo(pidcVersId, ucId);
    UseCaseLoader ucLdr = new UseCaseLoader(getServiceData());
    ExternalLinkInfo ret = new ExternalLinkInfo();
    ret.setDisplayText(ucLdr.getExtendedName(ucId, pidcVersId));
    ret.setUrl(
        getUrl(EXTERNAL_LINK_TYPE.USE_CASE, pidcVersId.toString().concat(LINK_ID_SEPARATOR).concat(ucId.toString())));
    ret.setLinkableObjId(ucId);
    ret.setLinkType(EXTERNAL_LINK_TYPE.USE_CASE.getName());
    return ret;
  }

  /**
   * @param pidcVersId
   * @param ucId
   * @throws IcdmException
   */
  private void checkForValidExtraInfo(final Long pidcVersId, final Long ucItemId) throws IcdmException {
    if ((null == pidcVersId) || (null == ucItemId)) {
      throw new IcdmException("Error in fetching extra details while forming the link");
    }
  }


  /**
   * @param linkType
   * @param pidcVersId
   * @param ucGrpId
   * @return external link info
   * @throws DataException
   */
  private ExternalLinkInfo getLinkInfoUcGrpPidcVersion(final Map<String, String> additionalDetails)
      throws IcdmException {
    Long pidcVersId = null;
    Long ucGrpId = null;
    if (!additionalDetails.isEmpty()) {
      pidcVersId = Long.valueOf(additionalDetails.get(ApicConstants.PIDC_VERS_ID_STR));
      ucGrpId = Long.valueOf(additionalDetails.get(ApicConstants.SELECTED_UC_ITEM_ID_STR));
    }
    checkForValidExtraInfo(pidcVersId, ucGrpId);
    UseCaseGroupLoader ucGrpLdr = new UseCaseGroupLoader(getServiceData());
    ExternalLinkInfo ret = new ExternalLinkInfo();
    ret.setDisplayText(ucGrpLdr.getExtendedName(ucGrpId, pidcVersId));
    ret.setUrl(getUrl(EXTERNAL_LINK_TYPE.USE_CASE_GROUP,
        pidcVersId.toString().concat(LINK_ID_SEPARATOR).concat(ucGrpId.toString())));
    ret.setLinkableObjId(ucGrpId);
    ret.setLinkType(EXTERNAL_LINK_TYPE.USE_CASE_GROUP.getName());
    return ret;
  }

  /**
   * @param linkType
   * @param pidcVersId
   * @param ucSecId
   * @return external link info
   * @throws DataException
   */
  private ExternalLinkInfo getLinkInfoUcSecPidcVersion(final Map<String, String> additionalDetails)
      throws IcdmException {
    Long pidcVersId = null;
    Long ucSecId = null;
    if (!additionalDetails.isEmpty()) {
      pidcVersId = Long.valueOf(additionalDetails.get(ApicConstants.PIDC_VERS_ID_STR));
      ucSecId = Long.valueOf(additionalDetails.get(ApicConstants.SELECTED_UC_ITEM_ID_STR));
    }
    checkForValidExtraInfo(pidcVersId, ucSecId);
    UseCaseSectionLoader ucSecLdr = new UseCaseSectionLoader(getServiceData());
    ExternalLinkInfo ret = new ExternalLinkInfo();
    ret.setDisplayText(ucSecLdr.getExtendedName(ucSecId, pidcVersId));
    ret.setUrl(getUrl(EXTERNAL_LINK_TYPE.USE_CASE_SECT,
        pidcVersId.toString().concat(LINK_ID_SEPARATOR).concat(ucSecId.toString())));
    ret.setLinkableObjId(ucSecId);
    ret.setLinkType(EXTERNAL_LINK_TYPE.USE_CASE_SECT.getName());
    return ret;
  }

  /**
   * @param linkType
   * @param pidcVersId
   * @return
   * @throws DataException
   */
  private ExternalLinkInfo getLinkInfoPidcVersion(final Long pidcVersId) throws DataException {
    PidcVersionLoader pidcVersLdr = new PidcVersionLoader(getServiceData());

    ExternalLinkInfo ret = new ExternalLinkInfo();
    ret.setDisplayText(pidcVersLdr.getExtendedName(pidcVersId));
    ret.setUrl(getUrl(EXTERNAL_LINK_TYPE.PIDC_VERSION, pidcVersId.toString()));
    ret.setLinkableObjId(pidcVersId);
    ret.setLinkType(EXTERNAL_LINK_TYPE.PIDC_VERSION.getName());

    return ret;
  }


  /**
   * @param linkType
   * @param resultId
   * @return
   * @throws DataException
   */
  private ExternalLinkInfo getLinkInfoCdrResult(final Long resultId) throws DataException {
    CDRReviewResultLoader ldr = new CDRReviewResultLoader(getServiceData());
    CDRReviewResult result = ldr.getDataObjectByID(resultId);

    ExternalLinkInfo ret = new ExternalLinkInfo();
    ret.setDisplayText(ldr.getExtendedName(resultId));

    // Key ID to construct the link
    String keyId = result.getId() + LINK_ID_SEPARATOR + result.getPidcVersionId();
    ret.setUrl(getUrl(EXTERNAL_LINK_TYPE.CDR_RESULT, keyId));

    ret.setLinkableObjId(result.getId());
    ret.setLinkType(EXTERNAL_LINK_TYPE.CDR_RESULT.getName());

    return ret;
  }

  /**
   * @param qNaireRespId - Rvw Qnaire Response id
   * @return constructed ExternalLinkInfo object for rvw qnaire response
   * @throws DataException
   */
  private ExternalLinkInfo getLinkInfoForQnaireResp(final Long qNaireRespId) throws DataException {

    RvwQnaireResponseLoader rvwQnaireRespLoader = new RvwQnaireResponseLoader(getServiceData());
    RvwQnaireResponse qNaireResponse = rvwQnaireRespLoader.getDataObjectByID(qNaireRespId);

    ExternalLinkInfo qNaireRespLinkInfo = new ExternalLinkInfo();
    qNaireRespLinkInfo.setLinkableObjId(qNaireRespId);
    qNaireRespLinkInfo.setLinkType(EXTERNAL_LINK_TYPE.QNAIRE_RESPONSE.getName());


    // Key ID to construct the link
    String keyId =
        qNaireResponse.getId().toString().concat(LINK_ID_SEPARATOR).concat(qNaireResponse.getPidcVersId().toString());
    String linkUrl = getUrl(EXTERNAL_LINK_TYPE.QNAIRE_RESPONSE, keyId);
    qNaireRespLinkInfo.setUrl(linkUrl);
    qNaireRespLinkInfo.setDisplayText(getDisplayTextForQnaireResponse(qNaireResponse, null,
        qNaireResponse.getA2lRespId(), qNaireResponse.getA2lWpId()));

    return qNaireRespLinkInfo;

  }


  /**
   * @param qNaireRespId - Rvw Qnaire Response Variant id
   * @return constructed ExternalLinkInfo object for rvw qnaire response variant
   * @throws DataException
   */
  private ExternalLinkInfo getLinkInfoForQnaireRespWithVariant(final Long qNaireRespVarId) throws DataException {

    RvwQnaireRespVariantLoader rvwQnaireRespVarLoader = new RvwQnaireRespVariantLoader(getServiceData());
    RvwQnaireRespVariant qNaireRespVar = rvwQnaireRespVarLoader.getDataObjectByID(qNaireRespVarId);

    ExternalLinkInfo qNaireRespLinkInfo = new ExternalLinkInfo();
    qNaireRespLinkInfo.setLinkableObjId(qNaireRespVar.getQnaireRespId());
    qNaireRespLinkInfo.setLinkType(EXTERNAL_LINK_TYPE.QNAIRE_RESPONSE_VAR.getName());

    // Key ID to construct the link
    String keyId = qNaireRespVar.getQnaireRespId().toString().concat(LINK_ID_SEPARATOR)
        .concat(qNaireRespVar.getPidcVersId().toString()).concat(LINK_ID_SEPARATOR)
        .concat(qNaireRespVar.getVariantId().toString());
    qNaireRespLinkInfo.setUrl(getUrl(EXTERNAL_LINK_TYPE.QNAIRE_RESPONSE_VAR, keyId));
    qNaireRespLinkInfo.setDisplayText(getDisplayTextForQnaireResponse(
        new RvwQnaireResponseLoader(getServiceData()).getDataObjectByID(qNaireRespVar.getQnaireRespId()),
        new PidcVariantLoader(getServiceData()).getDataObjectByID(qNaireRespVar.getVariantId()),
        qNaireRespVar.getA2lRespId(), qNaireRespVar.getA2lWpId()));

    return qNaireRespLinkInfo;

  }


  /**
   * @param linkType
   * @param rvwVarId
   * @return
   * @throws DataException
   */
  private ExternalLinkInfo getLinkInfoCdrResultVar(final Long rvwVarId) throws DataException {
    RvwVariantLoader ldr = new RvwVariantLoader(getServiceData());
    RvwVariant rvwVar = ldr.getDataObjectByID(rvwVarId);

    CDRReviewResult result = new CDRReviewResultLoader(getServiceData()).getDataObjectByID(rvwVar.getResultId());

    ExternalLinkInfo ret = new ExternalLinkInfo();
    ret.setDisplayText(ldr.getExtendedName(rvwVarId));

    // Key ID to construct the link
    String keyId =
        result.getId() + LINK_ID_SEPARATOR + result.getPidcVersionId() + LINK_ID_SEPARATOR + rvwVar.getVariantId();
    ret.setUrl(getUrl(EXTERNAL_LINK_TYPE.CDR_RESULT_VAR, keyId));

    ret.setLinkableObjId(result.getId());
    ret.setLinkType(EXTERNAL_LINK_TYPE.CDR_RESULT.getName());

    return ret;
  }

  /**
   * @param linkType
   * @param pidcA2lId
   * @return
   * @throws DataException
   */
  private ExternalLinkInfo getLinkInfoA2l(final Long pidcA2lId) throws DataException {
    PidcA2lLoader pa2lLdr = new PidcA2lLoader(getServiceData());
    PidcA2l pidcA2l = pa2lLdr.getDataObjectByID(pidcA2lId);

    ExternalLinkInfo ret = new ExternalLinkInfo();
    ret.setDisplayText(pa2lLdr.getExtendedName(pidcA2lId));

    String keyId = pidcA2l.getId() + LINK_ID_SEPARATOR + pidcA2l.getA2lFileId();
    ret.setUrl(getUrl(EXTERNAL_LINK_TYPE.A2L_FILE, keyId));

    ret.setLinkableObjId(pidcA2l.getA2lFileId());
    ret.setLinkType(EXTERNAL_LINK_TYPE.A2L_FILE.getName());

    return ret;
  }

  private String getUrl(final EXTERNAL_LINK_TYPE linkType, final String keyId) {
    return LinkRegistry.INSTANCE.getProtocolWithSep() + linkType.getKey() + ',' + keyId;
  }

  private String getDisplayTextForQnaireResponse(final RvwQnaireResponse qnaireResponse, final PidcVariant variant,
      final Long respId, final Long wpId)
      throws DataException {

    StringJoiner joiner = new StringJoiner("->");

    joiner.add("Questionnaire Response");

    PidcVersionLoader pidcVersLdr = new PidcVersionLoader(getServiceData());
    PidcVersion pidcVers = pidcVersLdr.getDataObjectByID(qnaireResponse.getPidcVersId());

    String variantName = (variant == null) ? "" : variant.getName();

    String respName = qnaireResponse.getA2lRespId() == null ? ""
        : new A2lResponsibilityLoader(getServiceData()).getDataObjectByID(qnaireResponse.getA2lRespId()).getName();

    String wpName = qnaireResponse.getA2lWpId() == null ? ""
        : new A2lWorkPackageLoader(getServiceData()).getDataObjectByID(qnaireResponse.getA2lWpId()).getName();

    joiner.add(pidcVersLdr.getPidcTreePath(pidcVers.getId()) + pidcVers.getName());

    if (!variantName.isEmpty()) {
      joiner.add(variantName);
    }

    joiner.add(respName);

    joiner.add(wpName);

    // add primaray variant name to qnaire response name if link is from a linked variant
    if (isSameCopiedQnaireLinkedInDest(qnaireResponse.getSecondaryQnaireLinkMap(),
        variant == null ? ApicConstants.NO_VARIANT_ID : variant.getId(), respId, wpId)) {
      joiner.add(qnaireResponse.getName() + " - " + qnaireResponse.getPrimaryVarRespWpName());
    }
    else {
      joiner.add(qnaireResponse.getName());
    }

    return joiner.toString();

  }

  /*
   * This method loops through the secondary Qnaire Resp variants and checks if given Qnaire is available
   */
  private boolean isSameCopiedQnaireLinkedInDest(final Map<Long, Map<Long, Set<Long>>> secondaryQnaireLinkMap,
      final Long pidcVarId, final Long respId, final Long wpId) {
    for (Map.Entry<Long, Map<Long, Set<Long>>> varEntry : secondaryQnaireLinkMap.entrySet()) {
      if (CommonUtils.isEqual(varEntry.getKey(), pidcVarId)) {
        for (Map.Entry<Long, Set<Long>> respEntry : varEntry.getValue().entrySet()) {
          if (CommonUtils.isEqual(respEntry.getKey(), respId) && respEntry.getValue().contains(wpId)) {
            return true;
          }
        }
      }
    }
    return false;
  }
}
