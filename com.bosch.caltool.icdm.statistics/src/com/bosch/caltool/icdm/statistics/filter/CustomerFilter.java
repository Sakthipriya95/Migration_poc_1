/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.statistics.filter;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.apic.ws.client.APICStub.LevelAttrInfo;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardInfoType;
import com.bosch.caltool.apic.ws.client.serviceclient.APICWebServiceClient;


/**
 * Filter for customer names.
 *
 * @author imi2si
 */
public class CustomerFilter implements IStatisticFilter {

  private final String filterTxt;
  private final boolean inclusive;
  private final APICWebServiceClient client;

  private final ILoggerAdapter logger;

  /**
   * Constructor for a Customer Filter
   *
   * @param filter the name of the customer that should be filtered
   * @param inclusive true, if the filter should be inclusive, otherwise false
   * @param client the webservice object, that is used to check if the pidc passed in the filter method matches with the
   *          filter
   * @param logger ICDM logger
   */
  public CustomerFilter(final String filter, final boolean inclusive, final APICWebServiceClient client,
      final ILoggerAdapter logger) {
    this.filterTxt = ".*" + filter + ".*";
    this.inclusive = inclusive;
    this.client = client;
    this.logger = logger;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean filter(final ProjectIdCardInfoType pidc) {
    this.logger.debug("Filtering PIDC" + pidc.getId());

    String curValue;
    try {
      LevelAttrInfo attr = pidc.getLevelAttrInfoList()[0];
      curValue = this.client.getAttributeValueByID(attr.getLevelAttrId(), attr.getLevelAttrValueId()).getValueE();

      if ((curValue.matches(this.filterTxt) && this.inclusive) ||
          (!curValue.matches(this.filterTxt) && !this.inclusive)) {
        return true;
      }

    }
    catch (Exception e) {
      this.logger.error(e.getMessage(), e);
    }


    return false;
  }

}
