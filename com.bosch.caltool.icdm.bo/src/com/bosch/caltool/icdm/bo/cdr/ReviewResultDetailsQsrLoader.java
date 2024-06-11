/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.datamodel.core.ModelTypeRegistry;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.general.ExternalLinkInfoLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.ReviewResultDetailsQsr;
import com.bosch.caltool.icdm.model.cdr.ReviewVariantModel;
import com.bosch.caltool.icdm.model.cdr.RvwParticipant;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;
import com.bosch.caltool.icdm.model.general.ExternalLinkInfo;

/**
 * @author DJA7COB
 */
public class ReviewResultDetailsQsrLoader extends AbstractSimpleBusinessObject {

  /**
   * @param serviceData instance
   */
  public ReviewResultDetailsQsrLoader(final ServiceData serviceData) {
    super(serviceData);
  }

  private static final String DELIMITER_CDR_LINK = "-";
  private static final int CMD_ARGS_SIZE = 2;
  private static final int CMD_ARGS_SIZE_WITH_VAR = 3;

  /**
   * Fetches review results based on the input cdr link
   *
   * @param cdrLink input cdr link
   * @return ReviewResultDetails for QSR
   * @throws IcdmException exception
   */
  public ReviewResultDetailsQsr fetchReviewResultDetails(final String cdrLink) throws IcdmException {

    String[] strIds = cdrLink.split(DELIMITER_CDR_LINK);
    if ((null == strIds) || ((strIds.length != CMD_ARGS_SIZE) && (strIds.length != CMD_ARGS_SIZE_WITH_VAR))) {
      throw new InvalidInputException("Invalid hyperlink for CDR Result!");
    }

    ReviewResultDetailsQsr reviewResultDetails = new ReviewResultDetailsQsr();
    CDRReviewResult cdrResult;

    try {
      CDRReviewResultLoader cdrReviewResultLoader = new CDRReviewResultLoader(getServiceData());
      Long variantId = null;
      // split link to find the cdr id
      String[] cdrIdArr = strIds[0].split(",");
      if (strIds.length == CMD_ARGS_SIZE_WITH_VAR) {

        // If the review result has variant
        RvwVariant rvwVar = new RvwVariantLoader(getServiceData())
            .getRvwVariantByResultNVarId(Long.valueOf(cdrIdArr[1]), Long.valueOf(strIds[CMD_ARGS_SIZE]));

        variantId = rvwVar.getVariantId();
        cdrResult = cdrReviewResultLoader.getDataObjectByID(rvwVar.getResultId());
        reviewResultDetails.setVarNameFromInput(rvwVar.getVariantName());

        IModelType type = ModelTypeRegistry.INSTANCE
            .getTypeOfModel(new PidcVariantLoader(getServiceData()).getDataObjectByID(variantId));
        String typeCode = type.getTypeCode();
        ExternalLinkInfo linkInfo = new ExternalLinkInfoLoader(getServiceData()).getLinkInfo(variantId, typeCode, null);
        reviewResultDetails.setPidcVariantLink(linkInfo.getUrl());
      }
      else {
        // review result with No-Variant
        cdrResult = cdrReviewResultLoader.getDataObjectByID(Long.valueOf(cdrIdArr[1]));
      }

      setResultDetails(reviewResultDetails, cdrResult, variantId);
    }
    catch (NumberFormatException exp) {
      throw new IcdmException(exp.getMessage(), exp);
    }
    return reviewResultDetails;
  }

  /**
   * Set review result details (review status,pidc version id, variant id, customer name, fuel type)
   *
   * @param reviewResultDetails response object
   * @param cdrResult result object from cdr link
   * @param variantIdFromInput
   * @throws IcdmException exception
   */
  private void setResultDetails(final ReviewResultDetailsQsr reviewResultDetails, final CDRReviewResult cdrResult,
      final Long variantIdFromInput)
      throws IcdmException {
    reviewResultDetails.setReviewStatus(CDRConstants.REVIEW_STATUS.getType(cdrResult.getRvwStatus()).getUiType());

    // Fetch calibration engineer from review participants
    Map<Long, RvwParticipant> rvwParticipants = new RvwParticipantLoader(getServiceData())
        .getByResultObj(new CDRReviewResultLoader(getServiceData()).getEntityObject(cdrResult.getId()));
    Optional<RvwParticipant> calEngineerOpt =
        rvwParticipants.values().stream().filter(participant -> participant.getActivityType().equals("C")).findFirst();
    if (calEngineerOpt.isPresent()) {
      reviewResultDetails.setCalibrationEngineer(calEngineerOpt.get().getName());
    }

    Long pidcVersionId = cdrResult.getPidcVersionId();

    // Construct the pidc version link
    IModelType type = ModelTypeRegistry.INSTANCE
        .getTypeOfModel(new PidcVersionLoader(getServiceData()).getDataObjectByID(pidcVersionId));
    String typeCode = type.getTypeCode();
    ExternalLinkInfo linkInfo = new ExternalLinkInfoLoader(getServiceData()).getLinkInfo(pidcVersionId, typeCode, null);
    reviewResultDetails.setPidcVersionLink(linkInfo.getUrl());

    // Construct the pidc variant link(s)
    Set<String> reviewVariants = new HashSet<>();
    Map<Long, ReviewVariantModel> rvwVariantsMap =
        new RvwVariantLoader(getServiceData()).getReviewVarMap(cdrResult.getId());
    rvwVariantsMap.values().stream().forEach(rvwVariant -> {
      try {
        collectRvwVar(rvwVariant.getRvwVariant(), reviewVariants);
      }
      catch (IcdmException e) {
        getLogger().error(e.getMessage(), e);
      }
    });
    reviewResultDetails.setLinkedVarUrls(reviewVariants);

    // Set customer name
    PidcVersionAttributeLoader pidcVersionAttributeLoader = new PidcVersionAttributeLoader(getServiceData());
    reviewResultDetails
        .setCustomer(pidcVersionAttributeLoader.getStructureAttributes(pidcVersionId).get(1L).getValue());

    // Set fuel type
    reviewResultDetails
        .setFuelType(pidcVersionAttributeLoader.getPidcFuelTypeAttrVal(pidcVersionId, variantIdFromInput));
  }

  /**
   * Collects url of variants linked
   *
   * @param rvwVarId
   * @param reviewVariants
   * @throws IcdmException
   */
  private void collectRvwVar(final RvwVariant rvwVar, final Set<String> reviewVariants) throws IcdmException {
    IModelType type = ModelTypeRegistry.INSTANCE
        .getTypeOfModel(new PidcVariantLoader(getServiceData()).getDataObjectByID(rvwVar.getVariantId()));
    String typeCode = type.getTypeCode();
    ExternalLinkInfo linkInfo =
        new ExternalLinkInfoLoader(getServiceData()).getLinkInfo(rvwVar.getVariantId(), typeCode, null);
    reviewVariants.add(linkInfo.getUrl());
  }
}
