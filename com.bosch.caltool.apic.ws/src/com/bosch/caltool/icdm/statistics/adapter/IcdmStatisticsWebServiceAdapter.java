/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.statistics.adapter;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.apic.ws.AttributeValue;
import com.bosch.caltool.apic.ws.AttributeWithValueType;
import com.bosch.caltool.apic.ws.LevelAttrInfo;
import com.bosch.caltool.apic.ws.PIDCVersionStatResponseType;
import com.bosch.caltool.apic.ws.ProjectIdCardInfoType;
import com.bosch.caltool.apic.ws.ProjectIdCardType;
import com.bosch.caltool.apic.ws.ProjectIdCardVariantType;
import com.bosch.caltool.icdm.logger.WSLogger;
import com.bosch.caltool.icdm.statistics.adapter.client.ApicWebServiceClientAdapter;


/**
 * A concrete implementation for ICDM-web service based statistics for a PIDC.
 *
 * @author imi2si
 */
public class IcdmStatisticsWebServiceAdapter extends AbstractIcdmStatistics {

  /**
   * The APIC web service client that delivers that fetches the information for the statistics
   */
  private final ApicWebServiceClientAdapter client;

  /**
   * The PIDC represented by this object
   */
  private final ProjectIdCardInfoType pidc;

  /**
   * The PIDC details
   */
  private final ProjectIdCardType pidcDetails;

  /**
   * The level statistics for PIDC-Attributes, Variants and Subvariants
   */
  private final Map<PidcLevel, AbstractIcdmLevelStatistics> levelStatistics = new HashMap<>();

  /**
   * The logger used for this object
   */
  @SuppressWarnings("unused")
  private final ILoggerAdapter logger;

  /**
   * Constructs a new adapter for the given webservice client and the PIDC
   *
   * @param client the APICWebServiceClient that is used to fetch attribute details
   * @param pidc the ProjectIdCardInfoType that has all information for the adapter
   * @throws Exception when web service calls can't be executed
   */
  public IcdmStatisticsWebServiceAdapter(final ApicWebServiceClientAdapter client, final ProjectIdCardInfoType pidc)
      throws Exception {
    this(client, pidc, WSLogger.getInstance());
  }

  /**
   * Constructs a new adapter for the given webservice client and the PIDC
   *
   * @param client the APICWebServiceClient that is used to fetch attribute details
   * @param pidc the ProjectIdCardInfoType that has all information for the adapter
   * @param logger the ILoggerAdapter used to log messages
   * @throws Exception when web service calls can't be executed
   */
  public IcdmStatisticsWebServiceAdapter(final ApicWebServiceClientAdapter client, final ProjectIdCardInfoType pidc,
      final ILoggerAdapter logger) throws Exception {

    logger.debug("Start Gathering statistics for PIDC: {}", pidc.getName());

    this.client = client;
    this.pidc = pidc;
    this.logger = logger;
    this.pidcDetails = client.getPidcDetails(pidc.getId());

    // Generate numbers of usage of attributes on the three levels PIDC, Variant and SubVariant
    generateLevelStatPIDC();
    generateLevelStatVariant();
    generateLevelStatSubVariant();

    logger.debug("End Gathering statistics for PIDC: {}", pidc.getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPidcName() {
    return this.pidc.getName();
  }

  private LevelAttrInfo[] getAllPidcTreeAttributes() {
    LevelAttrInfo[] allLevelAttr = this.pidc.getLevelAttrInfoList();

    return allLevelAttr == null ? new LevelAttrInfo[0] : allLevelAttr;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getPidcNoOfTreeAttributes() {

    return getAllPidcTreeAttributes().length;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPidcTreeAttribute(final int level) {
    for (LevelAttrInfo entry : getAllPidcTreeAttributes()) {
      if (entry.getLevelNo() == level) {
        try {
          this.logger.debug("Level " + level + " Attribute ID " + entry.getLevelAttrId() + " Attribute Value-ID " +
              entry.getLevelAttrValueId());
          return this.client.getAttributeValueByID(entry.getLevelAttrId(), entry.getLevelAttrValueId()).getValueE();
        }
        catch (Exception e) {
          this.logger.error(e.getMessage(), e);
          return "Error when returning attribute ID: " + e.getLocalizedMessage();
        }
      }
    }

    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPidcOwner() {
    return this.client.getPidcOwnerMail();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Date getPidcCreateDate() {
    return this.pidc.getCreateDate().getTime();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPidcCreateDateString() {
    if (getPidcCreateDate() == null) {
      return "";
    }

    return super.dateFromater.format(getPidcCreateDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPidcModifyDateString() {
    if (getPidcModifyDate() == null) {
      return "";
    }

    return super.dateFromater.format(getPidcModifyDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Date getPidcModifyDate() {
    if (this.pidc.getModifyDate() == null) {
      return null;
    }

    return this.pidc.getModifyDate().getTime();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getPidcNoOfAttributes() {
    /*
     * Number of attributes can be taken from PIDC level, without considering variants or subvariants, because for
     * attributes maintained on a lower level, there must be an entry on PIDC-level telling that it is maintained on
     * lower level. So to say, the number of attributes on PIDC-Level includes also the attributes on Detail-Level.
     */
    return this.client.getPidcHeaderStatistics(this.pidc.getId()).getVersionStatResponse().getTotalAttributes();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getPidcNoOfMandAttr() {
    int values = 0;

    for (Entry<PidcLevel, AbstractIcdmLevelStatistics> entry : this.levelStatistics.entrySet()) {
      values += entry.getValue().getNoAttrMandWithVal();
    }
    return values;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getPidcNoOfMandMax() {
    int values = 0;

    for (Entry<PidcLevel, AbstractIcdmLevelStatistics> entry : this.levelStatistics.entrySet()) {
      values += entry.getValue().getNoAttrMandMax();
    }
    return values;
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public double getPidcPercMandAttr() {
    double noOfAttrMandMax = getPidcNoOfMandMax();
    double noOfMandAttrWithValue = getPidcNoOfMandAttr();

    return noOfMandAttrWithValue / noOfAttrMandMax;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int getPidcNoOfVariants() {
    return this.levelStatistics.get(PidcLevel.VARIANT).getNoOfVariants();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getPidcNoOfSubVariants() {
    return this.levelStatistics.get(PidcLevel.SUB_VARIANT).getNoOfVariants();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addLevelStatistics(final PidcLevel level, final AbstractIcdmLevelStatistics levelStatistic) {
    this.levelStatistics.put(level, levelStatistic);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AbstractIcdmLevelStatistics getLevelStatistics(final PidcLevel level) {
    return this.levelStatistics.get(level);
  }

  /**
   * Generates the statistics for the attributes on PIDC-level
   */
  private void generateLevelStatPIDC() {
    addLevelStatistics(AbstractIcdmStatistics.PidcLevel.PIDC, new IcdmLevelStatisticsWebServiceAdapter() {

      {
        addAttributes(IcdmStatisticsWebServiceAdapter.this.pidcDetails.getAttributes());
      }
    });
  }

  /**
   * Generates the statistics for the attributes on Variant-level and for the variants itself
   */
  private void generateLevelStatVariant() {
    addLevelStatistics(AbstractIcdmStatistics.PidcLevel.VARIANT, new IcdmLevelStatisticsWebServiceAdapter() {

      {
        addVariants(IcdmStatisticsWebServiceAdapter.this.pidcDetails.getVariants());
      }
    });
  }

  /**
   * Generates the statistics for the attributes on Sub-Variant-level and for the sub-variants itself
   */
  private void generateLevelStatSubVariant() {
    addLevelStatistics(AbstractIcdmStatistics.PidcLevel.SUB_VARIANT, new IcdmLevelStatisticsWebServiceAdapter() {

      {
        if (IcdmStatisticsWebServiceAdapter.this.pidcDetails.getVariants() != null) {
          for (ProjectIdCardVariantType variant : IcdmStatisticsWebServiceAdapter.this.pidcDetails.getVariants()) {
            addVariants(variant.getSubVariants());
          }
        }
      }
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getUsageOfCodexEmAttr() {
    return Stream.of(this.pidcDetails.getAttributes())
        .filter(attr -> "Codex - EM relevant CAL".equalsIgnoreCase(attr.getAttribute().getNameE()))
        .map(AttributeWithValueType::getUsed).collect(Collectors.joining(","));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getUsageOfCodexDiaAttr() {
    return Stream.of(this.pidcDetails.getAttributes())
        .filter(attr -> "Codex - DIA relevant CAL".equalsIgnoreCase(attr.getAttribute().getNameE()))
        .map(AttributeWithValueType::getUsed).collect(Collectors.joining(","));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSAPNumber() {
    return Stream.of(this.pidcDetails.getAttributes())
        .filter(attr -> "SAP number".equalsIgnoreCase(attr.getAttribute().getNameE()))
        .map(AttributeWithValueType::getValue).filter(Objects::nonNull).map(AttributeValue::getValueE)
        .collect(Collectors.joining(","));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSpjmCal() {
    return Stream.of(this.pidcDetails.getAttributes())
        .filter(attr -> "SPjM CAL".equalsIgnoreCase(attr.getAttribute().getNameE()))
        .map(AttributeWithValueType::getValue).filter(Objects::nonNull).map(AttributeValue::getValueE)
        .collect(Collectors.joining(","));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PIDCVersionStatResponseType getPidcHeaderCoverageReport() {
    return this.client.getPidcHeaderStatistics(this.pidc.getId()).getVersionStatResponse();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int getCoverageMandAttr() {
    return this.client.getPidcHeaderStatistics(this.pidc.getId()).getVersionStatResponse()
        .getTotalMandtryAttrUsedCountOnly();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getCoverageMandAttrMax() {
    return this.client.getPidcHeaderStatistics(this.pidc.getId()).getVersionStatResponse()
        .getTotalMandatoryAttrCountOnly();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double getCoverageMandPercentage() {
    if (getCoverageMandAttrMax() == 0) {
      return 0;
    }

    return Double.valueOf(getCoverageMandAttr()) / Double.valueOf(getCoverageMandAttrMax());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCalProjectOrga() {
    return Stream.of(this.pidcDetails.getAttributes())
        .filter(attr -> "CAL Project Orga".equalsIgnoreCase(attr.getAttribute().getNameE()))
        .map(AttributeWithValueType::getValue).filter(Objects::nonNull).map(AttributeValue::getValueE)
        .collect(Collectors.joining(","));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double getUnratedFocusMatrix() {
    return this.client.getPidcHeaderStatistics(this.pidc.getId()).getVersionStatResponse()
        .getFocusMatrixUnratedAttributes();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double getFocusMatrixApplicabeAttributes() {
    return this.client.getPidcHeaderStatistics(this.pidc.getId()).getVersionStatResponse()
        .getFocusMatrixApplicabeAttributes();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double getCoveragePidcPercentage() {
    if (getCoveragePidcMax() == 0) {
      return 0;
    }

    return Double.valueOf(getCoveragePidc()) / Double.valueOf(getCoveragePidcMax());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getCoveragePidcMax() {
    return this.client.getPidcHeaderStatistics(this.pidc.getId()).getVersionStatResponse()
        .getMandateProjectUcAttributes();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getCoveragePidc() {
    return this.client.getPidcHeaderStatistics(this.pidc.getId()).getVersionStatResponse()
        .getUsedFlagSetImportedAttributes();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double getProjectUseCases() {
    return this.client.getPidcHeaderStatistics(this.pidc.getId()).getVersionStatResponse().getProjectUseCases();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double getNewAttributes() {
    return this.client.getPidcHeaderStatistics(this.pidc.getId()).getVersionStatResponse().getNewAttributes();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double getNotDefinedAttributes() {
    return this.client.getPidcHeaderStatistics(this.pidc.getId()).getVersionStatResponse().getNotDefinedAttribute();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double getUsedAttributes() {
    return this.client.getPidcHeaderStatistics(this.pidc.getId()).getVersionStatResponse().getUsedAttributes();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double getNotUsedAttributes() {
    return this.client.getPidcHeaderStatistics(this.pidc.getId()).getVersionStatResponse().getNotUsedAttributes();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getLastConfirmationDate() {
    return this.client.getLastConfirmationDate();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getLastConfirmationDateAsString() {
    if (getLastConfirmationDate() == null) {
      return "";
    }

    return super.dateFromater.format(getLastConfirmationDate().getTime());
  }
}
