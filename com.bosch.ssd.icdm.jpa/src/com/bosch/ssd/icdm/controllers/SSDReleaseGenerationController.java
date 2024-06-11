/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException;
import com.bosch.ssd.icdm.model.ComPkgBcModel;
import com.bosch.ssd.icdm.model.FeaValModel;
import com.bosch.ssd.icdm.model.SSDMessage;
import com.bosch.ssd.icdm.model.SSDMessageOptions;
import com.bosch.ssd.icdm.service.internal.SSDReleaseGenerationService;
import com.bosch.ssd.icdm.service.internal.servinterface.SSDNodeInfoAccessor;
import com.bosch.ssd.icdm.service.utility.CreateSSDRelease;

/**
 * Controller Class for validating the input and invoking service methods in @SSDReleaseGenerationService
 *
 * @author SSN9COB
 */
public class SSDReleaseGenerationController {

  private SSDReleaseGenerationService releaseGenService;
  private final SSDNodeInfoAccessor ssdNodeInfo;

  /**
   * @param ssdNodeInfo SSD Node Info
   */
  public SSDReleaseGenerationController(final SSDNodeInfoAccessor ssdNodeInfo) {
    this.ssdNodeInfo = ssdNodeInfo;
  }

  /**
   * @return the node Info
   */
  public SSDNodeInfoAccessor getSSDNodeInfo() {
    return this.ssdNodeInfo;
  }

  /**
   * @return service instance
   */
  public SSDReleaseGenerationService getSSDReleaseGenerationService() {
    if (Objects.isNull(this.releaseGenService)) {
      createSSDReleaseGenerationService();
    }
    return this.releaseGenService;
  }

  /**
   * @throws SSDiCDMInterfaceException exception
   */
  private void createSSDReleaseGenerationService() {
    this.releaseGenService = new SSDReleaseGenerationService(getSSDNodeInfo());
  }

  /**
   * invokeComplianceRelease - Controller method to handle all validations and pass the input to service class
   *
   * @param labelList List
   * @param compPkgBCs set
   * @param isQSSDOnlyRelease QSSDRel
   * @param nodeId nodeId
   * @return Options
   * @throws SSDiCDMInterfaceException Exception
   */
  public SSDMessageOptions invokeComplianceRelease(final Map<String, String> labelList,
      final Set<ComPkgBcModel> compPkgBCs, final boolean isQSSDOnlyRelease, final BigDecimal nodeId)
      throws SSDiCDMInterfaceException {

    SSDMessageOptions ssdMsgOpt = new SSDMessageOptions();
    // If not invalid input, perform compliance release
    if (!validateInputFields(labelList, compPkgBCs, ssdMsgOpt, true, nodeId)) {
      ssdMsgOpt = getSSDReleaseGenerationService().invokeComplianceRelease(labelList, compPkgBCs, isQSSDOnlyRelease);
    }
    return ssdMsgOpt;
  }

  /**
   * invokeNonSDOMComplianceRelease - Controller method to handle all validations and pass the input to service class
   *
   * @param labelList List
   * @param isQSSDOnlyRelease QSSDRel
   * @param nodeId nodeId
   * @return Options
   * @throws SSDiCDMInterfaceException Exception
  */
  public SSDMessageOptions invokeComplianceReleaseForNonSDOMSW(final Map<String, String> labelList,
      final boolean isQSSDOnlyRelease, final BigDecimal nodeId)
      throws SSDiCDMInterfaceException {

    SSDMessageOptions ssdMsgOpt = new SSDMessageOptions();
    // If not invalid input, perform compliance release
    if (!validateNodeNLabelList(labelList, ssdMsgOpt, true, nodeId)) {
      ssdMsgOpt = getSSDReleaseGenerationService().invokeComplianceReleaseForNonSDOMSWs(labelList, isQSSDOnlyRelease);
    }

    return ssdMsgOpt;
  }

  /**
   * invokeCompPckgRelease - Controller method to handle all validations and pass the input to service class
   *
   * @param labelList list
   * @param compPkgBCs bcs
   * @param nodeId id
   * @return options
   * @throws SSDiCDMInterfaceException ex
   */
  public SSDMessageOptions invokeCompPckgRelease(final Map<String, String> labelList,
      final Set<ComPkgBcModel> compPkgBCs, final BigDecimal nodeId)
      throws SSDiCDMInterfaceException {
    SSDMessageOptions ssdMsgOpt = new SSDMessageOptions();
    // If not invalid input, perform comp pckg release
    if (!validateInputFields(labelList, compPkgBCs, ssdMsgOpt, false, nodeId)) {
      ssdMsgOpt = getSSDReleaseGenerationService().invokeCompPckgRelease(labelList, compPkgBCs);
    }
    return ssdMsgOpt;
  }

  /**
   * contReleaseWithfeaValSelection - Controller method to handle all validations and pass the input to service class
   *
   * @param feaValMap feaval
   * @return message
   * @throws SSDiCDMInterfaceException exception
   */
  public SSDMessage contReleaseWithfeaValSelection(final Map<BigDecimal, FeaValModel> feaValMap)
      throws SSDiCDMInterfaceException {

    if (Objects.isNull(feaValMap)) {
      return SSDMessage.NULLFEAVAL;
    }
    return getSSDReleaseGenerationService().contReleaseWithfeaValSelection(feaValMap);
  }

  /**
   * @return List
   */
  public List<String> getErrorListFromLabellist() {
    return getSSDReleaseGenerationService().getErrorListFromLabellist();
  }

  /**
   * @return modelmap
   */
  public Map<BigDecimal, FeaValModel> getFeaValueForSelection() {
    return getSSDReleaseGenerationService().getFeaValueForSelection();
  }

  /**
   * @throws SSDiCDMInterfaceException ex
   */
  public void cancelRelease() throws SSDiCDMInterfaceException {
    getSSDReleaseGenerationService().cancelRelease();
  }

  /**
   * @return CreateSSDRelease
   */
  public CreateSSDRelease getReleaseUtils() {
    return getSSDReleaseGenerationService().getReleaseUtils();
  }

  /**
   * Handle input validation for compliance or comp package release
   * 
   * @Return TRUE if INVALID
   */
  private boolean validateInputFields(final Map<String, String> labelList, final Set<ComPkgBcModel> compPkgBCs,
      final SSDMessageOptions ssdMsgOpt, final boolean isComplianceRelease, final BigDecimal nodeId) {
    boolean isInvalidInput = false;
    if (validateNodeNLabelList(labelList, ssdMsgOpt, isComplianceRelease, nodeId)) {
      isInvalidInput = true;
    }
    else if (Objects.isNull(compPkgBCs)) {
      ssdMsgOpt.setSsdMessage(SSDMessage.NULLBC);
      isInvalidInput = true;
    }

    return isInvalidInput;
  }

  /**
   * Handle input validation for compliance or comp package release
   * 
   * @Return TRUE if INVALID
   */
  private boolean validateNodeNLabelList(final Map<String, String> labelList, final SSDMessageOptions ssdMsgOpt,
      final boolean isComplianceRelease, final BigDecimal nodeId) {
    boolean isInvalidInput = false;
    if (isComplianceRelease && Objects.isNull(nodeId)) {
      ssdMsgOpt.setSsdMessage(SSDMessage.COMPLINODENOTSET);
      isInvalidInput = true;
    }
    else if (!isComplianceRelease && Objects.isNull(nodeId)) {
      ssdMsgOpt.setSsdMessage(SSDMessage.COMPNODENOTSET);
      isInvalidInput = true;
    }
    else if (Objects.isNull(labelList) || labelList.isEmpty()) {
      ssdMsgOpt.setSsdMessage(SSDMessage.EMPTYORNULLLABELLIST);
      isInvalidInput = true;
    }

    return isInvalidInput;
  }
}
