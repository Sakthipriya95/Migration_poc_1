/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.bo.qnaire;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcQnaireInfo;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespVarRespWpLink;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;

/**
 * @author dmr1cob
 */
public class QnaireRespVarRespWpLinkBo {


  private String qnaireRespAlreadyLinkedMsg = ApicConstants.EMPTY_STRING;

  private String noWpInVariantMsg = ApicConstants.EMPTY_STRING;

  private String unLinkPrimaryLinkNotPossibleMsg = ApicConstants.EMPTY_STRING;

  private String defaultWPlinkNotPossibleMsg = ApicConstants.EMPTY_STRING;

  /*
   * Qnaire info object
   */
  private final PidcQnaireInfo pidcQnaireInfo;

  /*
   * selected Qnaire response in qnaire response table
   */
  private final RvwQnaireResponse selectedQnaireResp;

  /*
   * Qnaire Resp variant link
   */
  private final SortedSet<QnaireRespVarRespWpLink> qnaireRespVarLinkSet;


  /**
   * @param pidcQnaireInfo {@link PidcQnaireInfo} object
   * @param qnaireResp {@link RvwQnaireResponse} object
   * @param qnaireRespVarLinkSet {@link QnaireRespVarRespWpLink} set
   * @param messagesMap messagesMap
   */
  public QnaireRespVarRespWpLinkBo(final PidcQnaireInfo pidcQnaireInfo, final RvwQnaireResponse qnaireResp,
      final SortedSet<QnaireRespVarRespWpLink> qnaireRespVarLinkSet, final Map<String, String> messagesMap) {
    this.pidcQnaireInfo = pidcQnaireInfo;
    this.selectedQnaireResp = qnaireResp;
    this.qnaireRespVarLinkSet = qnaireRespVarLinkSet;
    loadDispMsgsFromDb(messagesMap);
  }

  /**
   *
   */
  private void loadDispMsgsFromDb(final Map<String, String> messagesMap) {
    if (messagesMap.containsKey("QNAIRE_RESP_ALREADY_LINKED")) {
      this.qnaireRespAlreadyLinkedMsg = messagesMap.get("QNAIRE_RESP_ALREADY_LINKED");
    }
    if (messagesMap.containsKey("NO_WP_IN_VARIANT")) {
      this.noWpInVariantMsg = messagesMap.get("NO_WP_IN_VARIANT");
    }
    if (messagesMap.containsKey("UNLINK_PRIMARY_LINK_NOT_POSSIBLE")) {
      this.unLinkPrimaryLinkNotPossibleMsg = messagesMap.get("UNLINK_PRIMARY_LINK_NOT_POSSIBLE");
    }
    if (messagesMap.containsKey("NO_DEFAULT_WP_LINK")) {
      this.defaultWPlinkNotPossibleMsg = messagesMap.get("NO_DEFAULT_WP_LINK");
    }
  }

  /**
   * Update Qnaire variant linking table content
   *
   * @param isWpRespLinking Flag True for Wp Resp Linking
   * @param isQnaireAlreadyLinked Flag true or false
   */
  public void updateDetails(final boolean isWpRespLinking, final boolean isQnaireAlreadyLinked) {
      for (QnaireRespVarRespWpLink qnaireRespVariantLink : this.qnaireRespVarLinkSet) {
        qnaireRespVariantLink
            .setDetails(validateWpAvailable(qnaireRespVariantLink, isWpRespLinking, isQnaireAlreadyLinked));
        qnaireRespVariantLink.setSelQnaireRespId(this.selectedQnaireResp.getId());
      }
    }

  // Validate whether workpackage is available for variants and variant linking
  private String validateWpAvailable(final QnaireRespVarRespWpLink qnaireRespVariantLink, final boolean isWpRespLinking,
      final boolean isQnaireAlreadyLinked) {
    String details = "";
    qnaireRespVariantLink.setDetails(null);
    qnaireRespVariantLink.setLinked(false);

    if (isWpRespLinking) {
      details = validateVarLinked(qnaireRespVariantLink, isQnaireAlreadyLinked);
    }
    else if (isWpAvailable(qnaireRespVariantLink.getPidcVariant())) {
      // Check Qnaire Response is linked with any variant
      details = validateVarLinked(qnaireRespVariantLink, isQnaireAlreadyLinked);
    }
    else {
      details = this.noWpInVariantMsg;
      qnaireRespVariantLink.setDisabled(true);
    }

    return details;
  }

  /**
   * @param pidcVar
   * @return
   */
  private boolean isWpAvailable(final PidcVariant pidcVar) {

    // Check whether variant is avaialable
    boolean isVariantAvailable = this.pidcQnaireInfo.getVarRespWpQniareMap().containsKey(pidcVar.getId());
    boolean isRespAvailable = false;
    boolean isWpAvailable = false;

    if (isVariantAvailable) {
      // Check whether the responsibility is available for the variant
      isRespAvailable = this.pidcQnaireInfo.getVarRespWpQniareMap().get(pidcVar.getId())
          .containsKey(this.selectedQnaireResp.getA2lRespId());
    }
    if (isRespAvailable) {
      // Check whether the wp is available for the variant
      isWpAvailable = this.pidcQnaireInfo.getVarRespWpQniareMap().get(pidcVar.getId())
          .get(this.selectedQnaireResp.getA2lRespId()).containsKey(this.selectedQnaireResp.getA2lWpId());
    }
    return isWpAvailable;
  }

  /**
   * @param qnaireRespVariantLink row object in Qnaire variant link table
   * @param pidcVar selected pidc variant
   * @return variant details
   */
  private String validateVarLinked(final QnaireRespVarRespWpLink qnaireRespVariantLink,
      final boolean isQnaireAlreadyLinked) {
    String details = "";
    if (isQnaireAlreadyLinked) {
      details = this.qnaireRespAlreadyLinkedMsg;
    }
    if (isPrimaryQnaireVarRespWp(qnaireRespVariantLink)) {
      details = this.unLinkPrimaryLinkNotPossibleMsg;
      qnaireRespVariantLink.setLinked(true);
      qnaireRespVariantLink.setDisabled(true);
    }
    else if (isQnaireRespLinked(this.selectedQnaireResp.getSecondaryQnaireLinkMap(),
        qnaireRespVariantLink.getPidcVariant().getId(), qnaireRespVariantLink.getA2lResponsibility().getId(),
        qnaireRespVariantLink.getA2lWorkPackage().getId())) {
      qnaireRespVariantLink.setLinked(true);
      qnaireRespVariantLink.setDisabled(false);
    }
    else {
      qnaireRespVariantLink.setDisabled(false);
    }

    if (CommonUtils.isEqual(ApicConstants.DEFAULT_A2L_WP_NAME,qnaireRespVariantLink.getA2lWorkPackage().getName())) {
      details = this.defaultWPlinkNotPossibleMsg;
      qnaireRespVariantLink.setLinked(false);
      qnaireRespVariantLink.setDisabled(true);
    }
    return details;
  }

  /*
   * This method loops through the secondary Qnaire Resp variants and checks if given Qnaire is available
   */
  private boolean isQnaireRespLinked(final Map<Long, Map<Long, Set<Long>>> secondaryQnaireLinkMap, final Long pidcVarId,
      final Long respId, final Long wpId) {
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

  private boolean isPrimaryQnaireVarRespWp(final QnaireRespVarRespWpLink qnaireRespVariantLink) {
    return CommonUtils.isEqual(this.selectedQnaireResp.getVariantId(),
        qnaireRespVariantLink.getPidcVariant().getId()) &&
        CommonUtils.isEqual(this.selectedQnaireResp.getA2lWpId(), qnaireRespVariantLink.getA2lWorkPackage().getId()) &&
        CommonUtils.isEqual(this.selectedQnaireResp.getA2lRespId(),
            qnaireRespVariantLink.getA2lResponsibility().getId());
  }

}
