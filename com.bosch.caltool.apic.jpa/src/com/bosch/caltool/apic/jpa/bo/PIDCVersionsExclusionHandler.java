/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.TypedQuery;

import com.bosch.caltool.icdm.database.entity.apic.TabvProjectAttr;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * PIDCVersionExclusion.java, This class will include the set of picdversionids which needs to be hidden : iCDM-2182
 *
 * @author dmo5cob
 */
public class PIDCVersionsExclusionHandler {


  /**
   * The set contains the pidc version ids which needs to be excluded
   */
  private final Set<Long> pidcVrsnIdsExclusionSet = new HashSet<>();

  /**
   * flag to refresh the collection of pidcversion ids
   */
  private boolean refreshFlag;

  /**
   * ApicDataProvider instance
   */
  private final ApicDataProvider apicDataProvider;

  /**
   * @param apicDataProvider ApicDataProvider instance
   */
  public PIDCVersionsExclusionHandler(final ApicDataProvider apicDataProvider) {
    this.apicDataProvider = apicDataProvider;
  }


  /**
   * @return the pidcVersionIdsExclusionSet
   */
  private Set<Long> getPidcVersionIdsExclusionSet() {
    if (!this.refreshFlag) {

      load();
    }
    return this.pidcVrsnIdsExclusionSet;
  }


  /**
   * Resetting the refersh flag to false
   */
  public void resetRefreshFlag() {
    this.refreshFlag = false;
  }

  /**
   * @param pidcVersionId : PIDC version id to be checked
   * @return This method returns true if the pidcVersionId is in the excluded list
   */
  public boolean isExcluded(final Long pidcVersionId) {
    return getPidcVersionIdsExclusionSet().contains(pidcVersionId);
  }

  /**
   */
  private void load() {

    CDMLogger.getInstance().debug("fetching excluded list of pidc version ids...");
    this.pidcVrsnIdsExclusionSet.clear();
    String attrId = this.apicDataProvider.getParameterValue(ApicConstants.KEY_QUOT_ATTR_ID);
    String valueIds = this.apicDataProvider.getParameterValue(ApicConstants.KEY_QUOT_VALUE_HIDDEN_STATUS);
    if ((null != attrId) && (null != valueIds)) {
      TypedQuery<Long> pidcVrsnIdsQry = this.apicDataProvider.getEntityProvider().getEm()
          .createNamedQuery(TabvProjectAttr.GET_PIDCVERSIONIDS_FOR_VALUEID, Long.class);
      String[] valueIdsArray = valueIds.split(",");
      // ICDM-2513, to pass comma seperated value-id as collection, the set has been used
      Set<Long> valueIdSet = new HashSet<Long>();
      for (String attrValueId : valueIdsArray) {
        // trimming function a
        valueIdSet.add(Long.valueOf(attrValueId.trim()));
      }
      pidcVrsnIdsQry.setParameter("valIDs", valueIdSet);
      this.pidcVrsnIdsExclusionSet.addAll(pidcVrsnIdsQry.getResultList());

      this.refreshFlag = true;
      CDMLogger.getInstance().debug("fetching of excluded list of pidc version ids completed.");
    }
  }
}
