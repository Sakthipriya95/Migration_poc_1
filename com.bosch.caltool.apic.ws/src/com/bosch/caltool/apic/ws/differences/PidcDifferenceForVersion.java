/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.differences;

import java.util.SortedSet;
import java.util.TimeZone;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.apic.ws.AllPidcDiffVersType;
import com.bosch.caltool.apic.ws.GetPidcDiffsResponseType;
import com.bosch.caltool.apic.ws.ProjectIdCardChangedAttributeType;
import com.bosch.caltool.apic.ws.ProjectIdCardChangedVariantsType;
import com.bosch.caltool.apic.ws.db.IWebServiceResponse;
import com.bosch.caltool.apic.ws.differences.adapter.PIDCChangedFMHistAdapterType;
import com.bosch.caltool.apic.ws.differences.adapter.PIDCChangedFMVersHistAdapterType;
import com.bosch.caltool.apic.ws.differences.adapter.PIDCChangedHistAdapter;
import com.bosch.caltool.apic.ws.differences.element.ElementDifferences;
import com.bosch.caltool.apic.ws.differences.element.ElementDifferencesAttributesPidc;
import com.bosch.caltool.apic.ws.differences.element.ElementDifferencesFM;
import com.bosch.caltool.apic.ws.differences.element.ElementDifferencesFMVersion;
import com.bosch.caltool.apic.ws.differences.element.ElementDifferencesPIDC;
import com.bosch.caltool.apic.ws.differences.element.ElementDifferencesVariant;
import com.bosch.caltool.apic.ws.differences.timezone.PidcDiffTypeTimeZone;
import com.bosch.caltool.apic.ws.timezone.AbstractTimeZone;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;
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
  protected AllPidcDiffVersType request;
  /**
   * the given pidc version
   */
  protected PidcVersion pidcVer;

  /**
   * the response
   */
  protected GetPidcDiffsResponseType response;
  /**
   * the old pidc change number
   */
  protected long oldPidcChangeNumber;
  /**
   * the new pidc change number
   */
  protected long newPidcChangeNumber;
  /**
   * the logger
   */
  protected ILoggerAdapter logger;
  /**
   * Service Data
   */
  protected ServiceData serviceData;

  /**
   * the time zone
   */
  private final TimeZone timeZoneVal;

  /**
   * @param request {@link AllPidcDiffVersType}
   * @param pidcVer {@link PidcVersion}
   * @param changeHistory {@link PidcChangeHistory}
   * @param timeZoneVar timeZone
   * @param logger Logger
   * @param serviceData serviceData
   * @throws IcdmException Exception
   */
  public PidcDifferenceForVersion(final AllPidcDiffVersType request, final PidcVersion pidcVer,
      final SortedSet<PidcChangeHistory> changeHistory, final TimeZone timeZoneVar, final ILoggerAdapter logger,
      final ServiceData serviceData) throws IcdmException {
    this.changeHistory = changeHistory;
    this.request = request;
    this.pidcVer = pidcVer;
    this.timeZoneVal = timeZoneVar;
    this.logger = logger;

    this.response = ElementDifferencesPIDC.getDummyResponse(pidcVer, request.getOldPidcChangeNumber(),
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
    if (getPidcDiff().toArray().length > 0) {
      this.response = (GetPidcDiffsResponseType) getPidcDiff().toArray()[0];
    }

    for (Long loop = getMinDiffIdForVersion(); loop <= getMaxDiffIdForVersion(); loop++) {
      // add attribute to resule
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
    setTimeZone();

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
  public GetPidcDiffsResponseType getWsResponse() {
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
  protected ElementDifferences getPidcDiff() throws IcdmException {
    // Get the Pidc Id from the active PIDC
    ElementDifferences pidcElement = new ElementDifferencesPIDC(this.oldPidcChangeNumber, this.newPidcChangeNumber,
        this.changeHistory, this.pidcVer.getPidcId(), true);
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
  protected ElementDifferences getAttrDiff(final long firstChangeNum, final long lastChangeNum,
      final boolean fromVersion)
      throws IcdmException {
    // Get the Pidc id From the active Version
    ElementDifferences attr = new ElementDifferencesAttributesPidc(firstChangeNum, lastChangeNum, this.changeHistory,
        this.pidcVer.getPidcId(), fromVersion);
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
  protected ElementDifferences getFmVersDiff(final long firstChangeNum, final long lastChangeNum,
      final boolean fromVersion)
      throws IcdmException {
    // Get the Pidc id From the active Version
    ElementDifferences attr = new ElementDifferencesFMVersion(firstChangeNum, lastChangeNum, this.changeHistory,
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
  protected ElementDifferences getFmDiff(final long firstChangeNum, final long lastChangeNum, final boolean fromVersion)
      throws IcdmException {
    // Get the Pidc id From the active Version
    ElementDifferences attr = new ElementDifferencesFM(firstChangeNum, lastChangeNum, this.changeHistory,
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
  protected ElementDifferences getVarDiff(final long firstChangeNum, final long lastChangeNum,
      final boolean fromVersion)
      throws IcdmException {
    // Get the Pidc id From the active Version
    ElementDifferences variants = new ElementDifferencesVariant(firstChangeNum, lastChangeNum, this.changeHistory,
        this.pidcVer.getPidcId(), fromVersion);
    variants.analyzeDifferences();
    return variants;
  }

  // iCDM-2614
  /**
   * @param attr attr
   * @throws IcdmException
   */
  private void addFmVersToResult(final ElementDifferences attr) throws IcdmException {
    for (PIDCChangedHistAdapter entry : attr.toArray()) {
      this.response.addChangedFocusMatrixVersion((PIDCChangedFMVersHistAdapterType) entry);
    }
  }

  // iCDM-2614
  /**
   * @param attr
   * @throws IcdmException
   */
  private void addFmToResult(final ElementDifferences attr) throws IcdmException {
    for (PIDCChangedHistAdapter entry : attr.toArray()) {
      this.response.addChangedFocusMatrix((PIDCChangedFMHistAdapterType) entry);
    }
  }

  private void addAttrToResult(final ElementDifferences attr) throws IcdmException {
    for (PIDCChangedHistAdapter entry : attr.toArray()) {
      this.response.addChangedAttributes((ProjectIdCardChangedAttributeType) entry);
    }
  }

  private void addVarToResult(final ElementDifferences var) throws IcdmException {
    for (PIDCChangedHistAdapter entry : var.toArray()) {
      this.response.addChangedVariants((ProjectIdCardChangedVariantsType) entry);
    }
  }

  private boolean isValid() {
    return (this.newPidcChangeNumber == -1) ||
        ((this.oldPidcChangeNumber <= this.newPidcChangeNumber) && (CommonUtils.isNotEmpty(this.changeHistory)));
  }

  private GetPidcDiffsResponseType getDummyResponse() throws IcdmException {
    return ElementDifferencesPIDC.getDummyResponse(this.pidcVer, this.request.getOldPidcChangeNumber(),
        this.request.getNewPidcChangeNumber());
  }

  private void setTimeZone() {
    AbstractTimeZone timeZone = new PidcDiffTypeTimeZone(this.timeZoneVal, this.response);
    timeZone.adjustTimeZoneFields();
    this.response = (GetPidcDiffsResponseType) timeZone.getWsResponse();
  }
}
