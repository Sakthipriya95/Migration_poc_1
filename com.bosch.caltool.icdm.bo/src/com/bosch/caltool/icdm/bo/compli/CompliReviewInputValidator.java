/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.compli;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;

import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.SdomPverLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CompliReviewInputMetaData;


/**
 * @author svj7cob
 */
public class CompliReviewInputValidator {

  /**
   * The input a2l file
   */
  private final InputStream a2lInputStream;

  /**
   * The input hex file
   */
  private final Map<String, InputStream> hexFileInputStreams;

  /**
   * The input web flow json input
   */
  private final CompliReviewInputMetaData inputMetadata;


  /**
   * the service data
   */
  private final ServiceData serviceData;

  private final String a2lFileName;


  /**
   * @param serviceData serviceData
   * @param a2lInputStream a2lInputStream
   * @param hexFileInputStreams hexFileInputStreams
   * @param inputMetaData Input metadata from json
   * @param a2lFileName a2lfile name from stream
   */
  public CompliReviewInputValidator(final ServiceData serviceData, final InputStream a2lInputStream,
      final Map<String, InputStream> hexFileInputStreams, final CompliReviewInputMetaData inputMetaData,
      final String a2lFileName) {

    this.serviceData = serviceData;
    this.a2lInputStream = a2lInputStream;
    this.hexFileInputStreams = hexFileInputStreams;
    this.inputMetadata = inputMetaData;
    this.a2lFileName = a2lFileName;
  }

  /**
   * Checks for validation in input json file
   *
   * @param isNonSDOMBCRelease
   * @throws IcdmException IcdmException
   */
  public void validate(final List<Boolean> isNonSDOMBCRelease) throws IcdmException {

    validateA2lFile();

    validatePverArtifacts(isNonSDOMBCRelease);

    validateHexFiles();

    validatePidcInput();

    validatePidcVarAvailability();

    validateSameVersion();
  }

  /**
   * @throws DataException
   */
  private void validateSameVersion() throws DataException {
    // get Json pidc's
    Collection<Long> jsonPidcObjIds = this.inputMetadata.getHexFilePidcElement().values();
    int inputSize = jsonPidcObjIds.size();
    // create loader
    PidcVersionLoader pidcVersLoader = new PidcVersionLoader(this.serviceData);
    PidcVariantLoader variantLoader = new PidcVariantLoader(this.serviceData);
    PidcVersion firstPidcVer = null;
    for (Long pidcObjId : jsonPidcObjIds) {

      TabvProjectVariant tabvVar = variantLoader.getEntityObject(pidcObjId);
      // If input is more than one and all are versions
      if ((tabvVar == null) && (inputSize > 1)) {
        StringBuilder errorCause = new StringBuilder();
        errorCause.append("When a PIDC and not a PIDC variant is assigned to a HEX, only one HEX file can be checked.")
            .append("\n")
            .append(
                "Cause: There's only a PIDC assigned to the HEX files. This PIDC has no variants. In such a case it is only allowed to do a check with one HEX file. Reason is, that one variant stands for one HEX file. If you've just a PIDC, the whole PIDC stands for one HEX file.")
            .append("\n").append(
                "Action: Do a check only for one HEX file. If you really have different HEX files, create variants within the PIDC that covers the differences in the HEX files. One variant in a PIDC stands for one HEX file.");
        throw new InvalidInputException(errorCause.toString());

      }
      if (tabvVar != null) {
        long pidcVersId = tabvVar.getTPidcVersion().getPidcVersId();
        if (firstPidcVer == null) {
          // get first version
          firstPidcVer = pidcVersLoader.getDataObjectByID(pidcVersId);
        }
        else {
          // get Subsequent versions
          PidcVersion subeqVer = pidcVersLoader.getDataObjectByID(pidcVersId);
          // If version is different then throw error
          if (CommonUtils.isNotEqual(subeqVer.getId(), firstPidcVer.getId())) {
            throw new InvalidInputException("COMPLI_REVIEW.PIDC_VAR_DIFF");
          }
        }

      }
    }
  }

  /**
   * @throws InvalidInputException if the input is invalid
   */
  private void validateA2lFile() throws InvalidInputException {
    if (CommonUtils.isNull(this.a2lInputStream)) {
      throw new InvalidInputException("COMPLI_REVIEW.A2L_MISSING");
    }
    if (CommonUtils.isEmptyString(this.inputMetadata.getA2lFileName())) {
      throw new InvalidInputException("COMPLI_REVIEW.A2L_MISSING_IN_METADATA");
    }
    if (!CommonUtils.isEqual(this.inputMetadata.getA2lFileName(), this.a2lFileName)) {
      throw new InvalidInputException("COMPLI_REVIEW.A2L_MISMATCH");
    }
  }

  /**
   * @param isNonSDOMBCRelease
   * @throws IcdmException
   */
  private void validatePverArtifacts(final List<Boolean> isNonSDOMBCRelease) throws IcdmException {

    SdomPverLoader pvrLdr = new SdomPverLoader(this.serviceData);
    String pverName = this.inputMetadata.getPverName();
    SortedSet<String> pverVariantNamesSet = pvrLdr.getPverVariantNames(pverName);

    isNonSDOMBCRelease.add(pverVariantNamesSet.isEmpty());

    if (CommonUtils.isEmptyString(this.inputMetadata.getPverName())) {
      throw new InvalidInputException("COMPLI_REVIEW.PVER_MISSING");
    }
    if (!pverVariantNamesSet.isEmpty()) {
      if (CommonUtils.isEmptyString(this.inputMetadata.getPverVariant())) {
        throw new InvalidInputException("COMPLI_REVIEW.PVER_VAR_MISSING");
      }
      if (CommonUtils.isEmptyString(this.inputMetadata.getPverRevision())) {
        throw new InvalidInputException("COMPLI_REVIEW.PVER_REV_MISSING");
      }

      try {
        Long.valueOf(this.inputMetadata.getPverRevision());
      }
      catch (NumberFormatException exp) {
        throw new InvalidInputException("COMPLI_REVIEW.PVER_REV_TYPE_INVALID");
      }
    }
    validatePverArtifactNames(isNonSDOMBCRelease);
  }


  /**
   * Validate the PVER input names
   *
   * @param isNonSDOMBCRelease
   * @throws DataException
   */
  private void validatePverArtifactNames(final List<Boolean> isNonSDOMBCRelease) throws DataException {
    SdomPverLoader pvrLdr = new SdomPverLoader(this.serviceData);

    // Check PVER name
    String pverName = this.inputMetadata.getPverName();
    SortedSet<String> pverVariantNamesSet = pvrLdr.getPverVariantNames(pverName);


    if (isNonSDOMBCRelease.get(0).booleanValue()) {
      AttributeLoader loader = new AttributeLoader(this.serviceData);
      Map<Long, Attribute> attrRetMap = loader.getAllAttributes(false);
      Optional<Attribute> attributeInfo = attrRetMap.values().stream()
          .filter(attribute -> attribute.getName().equals(CDRConstants.PVER_NAME_IN_SDOM)).findAny();
      if (attributeInfo.isPresent()) {
        AttributeValueLoader attributeValueLoader = new AttributeValueLoader(this.serviceData);
        Set<Long> attrIdSet = new HashSet();
        attrIdSet.add(attributeInfo.get().getId());
        // fetch attribute value
        Map<Long, Map<Long, AttributeValue>> attrValretMap = attributeValueLoader.getValuesByAttribute(attrIdSet);
        Optional<AttributeValue> attributeValueInfo = attrValretMap.get(attributeInfo.get().getId()).values().stream()
            .filter(attributeValue -> attributeValue.getName().equals(pverName)).findAny();
        if (attributeValueInfo.isPresent() && !attributeValueInfo.get().getClearingStatus().equals("Y")) {
          throw new InvalidInputException("COMPLI_REVIEW.PVER_NOT_CLEARED", pverName);
        }
      }
    }

    String pverVariant = null;
    // Check PVER Variant
    if (!isNonSDOMBCRelease.get(0).booleanValue()) {
      pverVariant = this.inputMetadata.getPverVariant();
      if (!pverVariantNamesSet.contains(pverVariant)) {
        throw new InvalidInputException("COMPLI_REVIEW.PVER_VAR_INVALID", pverVariant, pverName);
      }
    }

    // Check PVER revsion
    if (!isNonSDOMBCRelease.get(0).booleanValue()) {
      Long pverRevision = Long.valueOf(this.inputMetadata.getPverRevision());
      if (!pvrLdr.getPverVariantVersions(pverName, pverVariant).contains(pverRevision)) {
        throw new InvalidInputException("COMPLI_REVIEW.PVER_REV_INVALID", pverRevision, pverName, pverVariant);
      }
    }
  }

  /**
   * @throws InvalidInputException if the input is invalid
   */
  private void validateHexFiles() throws InvalidInputException {
    Collection<String> values = this.inputMetadata.getHexfileIdxMap().values();
    Set<String> hexSet = new HashSet<>(values);
    if (hexSet.size() != this.hexFileInputStreams.size()) {
      throw new InvalidInputException("COMPLI_REVIEW.HEX_MISMATCH");
    }

    // The Hex file Name in the json file & in stream should be equal
    Set<String> hexFileNames = this.hexFileInputStreams.keySet();
    if (!CommonUtils.equals(hexSet, hexFileNames)) {
      throw new InvalidInputException("COMPLI_REVIEW.HEX_NAME_MISMATCH");
    }

    // hexFilePidcElement size should be less than or equal to hexfileIdxMap
    if (this.inputMetadata.getHexFilePidcElement().size() > this.inputMetadata.getHexfileIdxMap().size()) {
      throw new InvalidInputException("COMPLI_REVIEW.HEX_FILES_MISMATCH");
    }

    // If hexFilePidcElement has ids, then the index number of hexFilePidcElement & hexfileIdxMap should be equal
    Set<Long> hexFilePidcKeys = this.inputMetadata.getHexFilePidcElement().keySet();
    Set<Long> hexFildIdKeys = this.inputMetadata.getHexfileIdxMap().keySet();
    if (!hexFildIdKeys.containsAll(hexFilePidcKeys)) {
      throw new InvalidInputException("Information in meta data is invalid");
    }
  }

  /**
   * @param invalidHexFileMsg
   * @throws InvalidInputException
   */
  private void validatePidcInput() throws InvalidInputException {
    // If hexFilePidcElement has ids, it should be either pidc version or pidc variant.
    Collection<Long> jsonPidcObjIds = this.inputMetadata.getHexFilePidcElement().values();

    PidcVersionLoader pidcVersLoader = new PidcVersionLoader(this.serviceData);
    PidcVariantLoader variantLoader = new PidcVariantLoader(this.serviceData);
    for (Long pidcObjId : jsonPidcObjIds) {
      if (!variantLoader.isValidId(pidcObjId) && !pidcVersLoader.isValidId(pidcObjId)) {
        throw new InvalidInputException("COMPLI_REVIEW.PIDC_VER_VAR_INVALID");
      }

    }
  }

  /**
   * @throws UnAuthorizedAccessException
   * @throws DataException
   * @throws InvalidInputException
   */
  private void validatePidcVarAvailability() throws DataException {
    // If hexFilePidcElement has ids, it should be either pidc version or pidc variant.
    Collection<Long> jsonPidcObjIds = this.inputMetadata.getHexFilePidcElement().values();
    PidcVersionLoader pidcVerLoader = new PidcVersionLoader(this.serviceData);
    for (Long pidcObjId : jsonPidcObjIds) {
      // checking whether given input pidc element id is a pidc version id
      if (pidcVerLoader.isValidId(pidcObjId)) {
        // checking whether variants are available for the pidc version
        PidcVariantLoader pidcVarLoader = new PidcVariantLoader(this.serviceData);
        if (CommonUtils.isNotEmpty(pidcVarLoader.getVariants(pidcObjId, false).values())) {
          // Group name for error code-COMPLI_REVIEW,name of error code-VARIANT_REQUIRED
          throw new InvalidInputException("COMPLI_REVIEW.VARIANT_REQUIRED");
        }
      }
    }
  }
}
