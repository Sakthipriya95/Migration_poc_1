/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.statistics.adapter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.caltool.apic.ws.client.APICStub.LevelAttrInfo;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardInfoType;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardType;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardVariantType;
import com.bosch.caltool.apic.ws.client.serviceclient.APICWebServiceClient;


/**
 * A concrete implementation for ICDM-web service based statistics for a PIDC.
 *
 * @author imi2si
 */
public class IcdmStatisticsWebServiceAdapter extends AbstractIcdmStatistics {

  /**
   * The APIC web service client that delivers that fetches the information for the statistics
   */
  private final APICWebServiceClient client;

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
  public IcdmStatisticsWebServiceAdapter(final APICWebServiceClient client, final ProjectIdCardInfoType pidc)
      throws Exception {
    this(client, pidc, new Log4JLoggerAdapterImpl(LogManager.getLogger(IcdmStatisticsWebServiceAdapter.class)));
  }

  /**
   * Constructs a new adapter for the given webservice client and the PIDC
   *
   * @param client the APICWebServiceClient that is used to fetch attribute details
   * @param pidc the ProjectIdCardInfoType that has all information for the adapter
   * @param logger the ILoggerAdapter used to log messages
   * @throws Exception when web service calls can't be executed
   */
  public IcdmStatisticsWebServiceAdapter(final APICWebServiceClient client, final ProjectIdCardInfoType pidc,
      final ILoggerAdapter logger) throws Exception {

    logger.debug("Start Gathering statistics for PIDC: " + pidc.getName());

    this.client = client;
    this.pidc = pidc;
    this.logger = logger;
    this.pidcDetails = client.getPidcDetails(pidc.getId());

    // Generate numbers of usage of attributes on the three levels PIDC, Variant and SubVariant
    generateLevelStatPIDC();
    generateLevelStatVariant();
    generateLevelStatSubVariant();

    logger.debug("End Gathering statistics for PIDC: " + pidc.getName());
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
  public Date getPidcCreateDate() {
    return this.pidc.getCreateDate().getTime();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPidcCreateDateString() {
    return super.dateFromater.format(getPidcCreateDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPidcModifyDateString() {
    return super.dateFromater.format(getPidcModifyDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Date getPidcModifyDate() {
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
    return this.pidcDetails.getAttributes().length;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getPidcNoOfMandAttr() {
    return this.levelStatistics.get(PidcLevel.PIDC).getNoAttrMandWithVal();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getPidcNoOfMandMax() {
    return this.levelStatistics.get(PidcLevel.PIDC).getNoAttrMandMax();
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public double getPidcPercMandAttr() {
    int withValue = 0;
    int withoutValue = 0;

    for (Entry<PidcLevel, AbstractIcdmLevelStatistics> entry : this.levelStatistics.entrySet()) {
      withValue += entry.getValue().getNoAttrMandWithVal();
      withoutValue += entry.getValue().getNoAttrMandWoVal();
    }
    return Double.valueOf(withValue) / Double.valueOf((double) withValue + withoutValue);
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
}
