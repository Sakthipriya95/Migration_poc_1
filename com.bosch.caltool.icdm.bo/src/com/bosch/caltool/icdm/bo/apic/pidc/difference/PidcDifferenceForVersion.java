/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.difference;

import java.util.SortedSet;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.differences.adapter.PIDCChangedFMHistAdapterRestType;
import com.bosch.caltool.icdm.bo.apic.pidc.differences.adapter.PIDCChangedFMVersHistAdapterRestType;
import com.bosch.caltool.icdm.bo.apic.pidc.differences.adapter.PIDCChangedHistAdapterRest;
import com.bosch.caltool.icdm.bo.apic.pidc.differences.element.ElementDifferencesAttributesPidcRest;
import com.bosch.caltool.icdm.bo.apic.pidc.differences.element.ElementDifferencesFMRest;
import com.bosch.caltool.icdm.bo.apic.pidc.differences.element.ElementDifferencesFMVersionRest;
import com.bosch.caltool.icdm.bo.apic.pidc.differences.element.ElementDifferencesPIDCRest;
import com.bosch.caltool.icdm.bo.apic.pidc.differences.element.ElementDifferencesRest;
import com.bosch.caltool.icdm.bo.apic.pidc.differences.element.ElementDifferencesVariantRest;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangedAttrType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangedVariantType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDiffsForVersType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDiffsResponseType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;


/**
 * @author imi2si
 */
public class PidcDifferenceForVersion implements IWebServiceResponse {

  /**
   * the change history
   */
  protected SortedSet<PidcChangeHistory> changeHistory;
  /**
   * the pidc diff version type
   */
  protected PidcDiffsForVersType request;
  /**
   * the given pidc version
   */
  protected PidcVersion pidcVer;

  /**
   * the response
   */
  protected PidcDiffsResponseType response;
  /**
   * the old pidc change number
   */
  protected Long oldPidcChangeNumber;
  /**
   * the new pidc change number
   */
  protected Long newPidcChangeNumber;
  /**
   * the logger
   */
  protected ILoggerAdapter logger;
  /**
   * Service Data
   */
  protected ServiceData serviceData;

  /**
   * @param request {@link PidcDiffsForVersType}
   * @param pidcVer {@link PidcVersion}
   * @param changeHistory {@link PidcChangeHistory}
   * @param logger Logger
   * @param serviceData serviceData
   * @throws IcdmException Exception
   */
  public PidcDifferenceForVersion(final PidcDiffsForVersType request, final PidcVersion pidcVer,
      final SortedSet<PidcChangeHistory> changeHistory, final ILoggerAdapter logger, final ServiceData serviceData)
      throws IcdmException {
    this.changeHistory = changeHistory;
    this.request = request;
    this.pidcVer = pidcVer;
    this.logger = logger;

    this.response = ElementDifferencesPIDCRest.getDummyResponse(pidcVer, request.getOldPidcChangeNumber(),
        request.getNewPidcChangeNumber());

    this.oldPidcChangeNumber = this.request.getOldPidcChangeNumber() + 1;

    this.newPidcChangeNumber = this.request.getNewPidcChangeNumber();

    this.serviceData = serviceData;
  }

  /**
   * {@inheritDoc}
   *
   * @throws IcdmException
   */
  @Override
  public void createWsResponse() throws IcdmException {

    this.response = (PidcDiffsResponseType) getPidcDiff().toArray()[0];

    for (Long loop = getMinDiffIdForVersion(); loop <= getMaxDiffIdForVersion(); loop++) {
      // add attribute to result
      addAttrToResult(getAttrDiff(loop, loop, true));

      // add variant to result
      addVarToResult(getVarDiff(loop, loop, true));

      // iCDM-2614
      // add focus matrix version to result
      addFmVersToResult(getFmVersDiff(loop, loop, true));

      // iCDM-2614
      // add focus matrix to result
      addFmToResult(getFmDiff(loop, loop, true));
    }
  }

  /**
   * gets the minimum difference id for version
   *
   * @return
   */
  private Long getMinDiffIdForVersion() {
    Long minPidcVersion = this.changeHistory.first().getPidcVersVers();

    return minPidcVersion >= this.oldPidcChangeNumber ? minPidcVersion : this.oldPidcChangeNumber;
  }

  /**
   * gets the maximum difference id for version
   *
   * @return
   */
  private Long getMaxDiffIdForVersion() {
    Long maxPidcVersion = this.changeHistory.last().getPidcVersVers();

    return (maxPidcVersion <= this.newPidcChangeNumber) || (this.newPidcChangeNumber == -1) ? maxPidcVersion
        : this.newPidcChangeNumber;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PidcDiffsResponseType getWsResponse() {
    return isValid() ? this.response : getWsResponse();
  }

  public Long getMinDiffId() {
    Long minPidcVersion = this.changeHistory.first().getPidcVersion();

    return minPidcVersion >= this.oldPidcChangeNumber ? minPidcVersion : this.oldPidcChangeNumber;
  }

  public Long getMaxDiffId() {
    Long maxPidcVersion = this.changeHistory.last().getPidcVersion();

    return (maxPidcVersion <= this.newPidcChangeNumber) || (this.newPidcChangeNumber == -1) ? maxPidcVersion
        : this.newPidcChangeNumber;
  }

  /**
   * Gets the pidc differences
   *
   * @return element differences
   * @throws IcdmException
   */
  protected ElementDifferencesRest getPidcDiff() throws IcdmException {
    // Get the Pidc Id from the active PIDC
    ElementDifferencesRest pidcElement = new ElementDifferencesPIDCRest(this.oldPidcChangeNumber,
        this.newPidcChangeNumber, this.changeHistory, this.pidcVer.getPidcId(), true);
    pidcElement.analyzeDifferences();

    return pidcElement;
  }

  /**
   * Gets the attr differencs
   *
   * @param firstChangeNum the first change number
   * @param lastChangeNum the last change number
   * @param fromVersion is from pidc version
   * @return element differences
   * @throws IcdmException
   */
  protected ElementDifferencesRest getAttrDiff(final Long firstChangeNum, final Long lastChangeNum,
      final boolean fromVersion)
      throws IcdmException {
    // Get the Pidc id From the active Version
    ElementDifferencesRest attr = new ElementDifferencesAttributesPidcRest(firstChangeNum, lastChangeNum,
        this.changeHistory, this.pidcVer.getPidcId(), fromVersion);
    attr.analyzeDifferences();
    return attr;
  }


  // iCDM-2614
  /**
   * Gets the fm version differences
   *
   * @param firstChangeNum the pidc version first change number
   * @param lastChangeNum the pidc version last change number
   * @param fromVersion the boolean flag to indicate from pidc Version
   * @return Element Differences
   * @throws IcdmException
   */
  protected ElementDifferencesRest getFmVersDiff(final Long firstChangeNum, final Long lastChangeNum,
      final boolean fromVersion)
      throws IcdmException {
    // Get the Pidc id From the active Version
    ElementDifferencesRest attr = new ElementDifferencesFMVersionRest(firstChangeNum, lastChangeNum, this.changeHistory,
        this.pidcVer.getPidcId(), fromVersion, this.serviceData);
    attr.analyzeDifferences();
    return attr;
  }

  // iCDM-2614
  /**
   * Gets the fm differences
   *
   * @param firstChangeNum the pidc version first change number
   * @param lastChangeNum the pidc version last change number
   * @param fromVersion the boolean flag to indicate from pidc Version
   * @return Element Differences
   * @throws IcdmException
   */
  protected ElementDifferencesRest getFmDiff(final Long firstChangeNum, final Long lastChangeNum,
      final boolean fromVersion)
      throws IcdmException {
    // Get the Pidc id From the active Version
    ElementDifferencesRest attr = new ElementDifferencesFMRest(firstChangeNum, lastChangeNum, this.changeHistory,
        this.pidcVer.getPidcId(), fromVersion);
    attr.analyzeDifferences();
    return attr;
  }

  /**
   * Gets the variant differences
   *
   * @param firstChangeNum the pidc version first change number
   * @param lastChangeNum the pidc version last change number
   * @param fromVersion the boolean flag to indicate from pidc Version
   * @return Element Differences
   * @throws IcdmException
   */
  protected ElementDifferencesRest getVarDiff(final Long firstChangeNum, final Long lastChangeNum,
      final boolean fromVersion)
      throws IcdmException {
    // Get the Pidc id From the active Version
    ElementDifferencesRest variants = new ElementDifferencesVariantRest(firstChangeNum, lastChangeNum,
        this.changeHistory, this.pidcVer.getPidcId(), fromVersion);
    variants.analyzeDifferences();
    return variants;
  }

  // iCDM-2614
  /**
   * @param attr attr
   * @throws IcdmException
   */
  private void addFmVersToResult(final ElementDifferencesRest attr) throws IcdmException {
    for (PIDCChangedHistAdapterRest entry : attr.toArray()) {
      this.response.getPidcFocusMatrixVersTypeList().add((PIDCChangedFMVersHistAdapterRestType) entry);
    }
  }

  // iCDM-2614
  /**
   * @param attr
   * @throws IcdmException
   */
  private void addFmToResult(final ElementDifferencesRest attr) throws IcdmException {
    for (PIDCChangedHistAdapterRest entry : attr.toArray()) {
      this.response.getPidcFocusMatrixTypeList().add((PIDCChangedFMHistAdapterRestType) entry);
    }
  }

  private void addAttrToResult(final ElementDifferencesRest attr) throws IcdmException {
    for (PIDCChangedHistAdapterRest entry : attr.toArray()) {
      this.response.getPidcChangedAttrTypeList().add((PidcChangedAttrType) entry);
    }
  }

  private void addVarToResult(final ElementDifferencesRest var) throws IcdmException {
    for (PIDCChangedHistAdapterRest entry : var.toArray()) {
      this.response.getPidcChangedVariantTypeList().add((PidcChangedVariantType) entry);
    }
  }

  private boolean isValid() {
    return (this.newPidcChangeNumber == -1) ||
        ((this.oldPidcChangeNumber <= this.newPidcChangeNumber) && (CommonUtils.isNotEmpty(this.changeHistory)));
  }
}
