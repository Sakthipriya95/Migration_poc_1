/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.ui.providers.ScratchPadDataFetcher;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author rgo7cob New Class for Scratch Pad filter
 */
public class ScratchPadFilter extends AbstractViewerFilter {

  // Create a Filter for scratch pad View part.
  private static final String STR_COLON = " :: ";

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    boolean returnType = false;
    // element type ScratchPadDataProvider
    if (element instanceof ScratchPadDataFetcher) {
      final ScratchPadDataFetcher data = (ScratchPadDataFetcher) element;
      if ((CommonUtils.isNotNull(data.getSeriesStatsInfo())) &&
          (CommonUtils.isNotNull(data.getSeriesStatsInfo().getCalData()))) {
        returnType = matchForCalData(data);
      }
      else {
        // match for PIDC,Varaint
        returnType = matchForOtherTypes(data);
      }


    }
    return returnType;
  }

  /**
   * Method to match the Cal data Objects
   *
   * @param data
   * @return
   */
  private boolean matchForCalData(final ScratchPadDataFetcher data) {
    boolean returnType;
    // match for Cal dat phy object.
    returnType = matchText(data.getSeriesStatsInfo().getCalData().getShortName()) ||
        matchText(data.getSeriesStatsInfo().getCalDataPhyValType().getLabel()) ||
        matchText(data.getSeriesStatsInfo().getCalData().getCalDataPhy().getSimpleDisplayValue());
    if (CommonUtils.isNotNull(data.getSeriesStatsInfo().getDataSetName()) &&
        (ApicUtil.compare(data.getSeriesStatsInfo().getDataSetName(), ApicConstants.EMPTY_STRING) != 0)) {
      returnType = returnType || matchText(data.getSeriesStatsInfo().getDataSetName());
    }

    String fullStr = matchCalDataForAll(data);

    returnType = returnType || matchText(fullStr);
    return returnType;
  }

  private String matchCalDataForAll(final ScratchPadDataFetcher data) {
    String fullStr = CommonUtils.concatenate(data.getSeriesStatsInfo().getCalData().getShortName(), STR_COLON,
        data.getSeriesStatsInfo().getCalDataPhyValType().getLabel(), STR_COLON,
        data.getSeriesStatsInfo().getCalData().getCalDataPhy().getSimpleDisplayValue());
    if (!data.getSeriesStatsInfo().getDataSetName()
        .equals(com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants.EMPTY_STRING)) {
      fullStr = CommonUtils.concatenate(fullStr, STR_COLON, "(", data.getSeriesStatsInfo().getDataSetName(), ")");
    }
    return fullStr;
  }

  /**
   * @param data
   */
  private boolean matchForOtherTypes(final ScratchPadDataFetcher data) {
    boolean returnType = false;
    // match for A2l.
    if (CommonUtils.isNotNull(data.getPidcA2l())) {
      returnType = matchText(data.getPidcA2l().getName());
    }
    // match for Attr Val.
    else if (CommonUtils.isNotNull(data.getAttrVal())) {
      returnType = matchText(data.getAttrVal().getName());
    }
    // match for Pidc Variant
    else if (CommonUtils.isNotNull(data.getPidcVariant())) {
      returnType = matchVaraint(data);

    }
    // match for Pidc sub Variant
    else if (CommonUtils.isNotNull(data.getPidcSubVariant())) {
      returnType = matchSubVariant(data);

    }
    // matcch for function.
    else if (CommonUtils.isNotNull(data.getFunction())) {
      returnType = matchText(data.getFunction().getName());
    }
    // match for pid card.
    else if (CommonUtils.isNotNull(data.getPidcVersion())) {
      returnType = matchText(data.getPidcVersion().getName()) || matchText(data.getPidcVersion().getVersionName());
    }
    return returnType;
  }

  /**
   * Match for Sub variant
   *
   * @param data
   * @return
   */
  private boolean matchSubVariant(final ScratchPadDataFetcher data) {

    PidcVersion pidcVersion;
    try {
      pidcVersion = new PidcVersionServiceClient().getById(data.getPidcSubVariant().getPidcVersionId());
      Long pidcId = pidcVersion.getPidcId();

      return matchText(data.getPidcSubVariant().getName()) || matchText(data.getPidcVariant().getName()) ||
          matchText(new PidcServiceClient().getById(pidcId).getName()) || matchText(pidcVersion.getName());

    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return false;
  }

  /**
   * Match for Variant
   *
   * @param data data
   * @return the boolean for match text
   */
  private boolean matchVaraint(final ScratchPadDataFetcher data) {
    PidcVersion pidcVersion;

    try {
      pidcVersion = new PidcVersionServiceClient().getById(data.getPidcVariant().getPidcVersionId());
      Long pidcId = pidcVersion.getPidcId();


      return matchText(data.getPidcVariant().getName()) ||
          matchText(new PidcServiceClient().getById(pidcId).getName()) || matchText(pidcVersion.getName());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }

    return false;

  }

}
