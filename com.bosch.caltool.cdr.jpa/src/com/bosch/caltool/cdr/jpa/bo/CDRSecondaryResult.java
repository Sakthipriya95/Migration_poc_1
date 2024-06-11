/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.calcomp.externallink.ILinkableObject;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TRuleSet;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParametersSecondary;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResultsSecondary;


/**
 * @author rgo7cob
 */
public class CDRSecondaryResult extends AbstractCdrObject implements Comparable<CDRSecondaryResult>, ILinkableObject {


  private ConcurrentHashMap<Long, CDRResParamSecondary> seconndaryParamMap;


  /**
   * @author rgo7cob
   */
  public enum RULE_SOURCE {
                           /**
                            * Common rules
                            */
                           COMMON_RULES("Common Rules", "C"),
                           /**
                            * Rule set
                            */
                           RULE_SET("Rule Set", "R"),
                           /**
                            * ssd release
                            */
                           SSD_RELEASE("SSD Release", "S"),

                           /**
                            * ssd file
                            */
                           SSD_FILE("SSD File", "F");

    private final String uiVal;

    /**
     * @return the uiVal
     */
    public String getUiVal() {
      return this.uiVal;
    }


    /**
     * @return the dbVal
     */
    public String getDbVal() {
      return this.dbVal;
    }

    private final String dbVal;

    RULE_SOURCE(final String uiVal, final String dbVal) {
      this.uiVal = uiVal;
      this.dbVal = dbVal;
    }

    /**
     * @param dbvalue dbvalue
     * @return the db val
     */
    public static RULE_SOURCE getSource(final String dbvalue) {
      for (RULE_SOURCE source : RULE_SOURCE.values()) {
        if (source.dbVal.equals(dbvalue)) {
          return source;
        }
      }
      return null;
    }

  }


  /**
   * Constructor
   *
   * @param dataProvider the data provider
   * @param objID resultID
   */
  protected CDRSecondaryResult(final CDRDataProvider dataProvider, final Long objID) {
    super(dataProvider, objID);
    dataProvider.getDataCache().getAllCDRSecResults().put(objID, this);
  }


  /**
   * @return the cdr result for the Secondary
   */
  public CDRResult getCdrResult() {
    TRvwResultsSecondary dbResSecondary = getEntityProvider().getDbResSecondary(getID());
    long resultId = dbResSecondary.getTRvwResult().getResultId();
    return getDataProvider().getDataCache().getAllCDRResults().get(resultId);
  }


  /**
   * @return the map with param id as key and CDRResParamSecondary as value
   */
  public Map<Long, CDRResParamSecondary> getSecondaryResParams() {
    if (CommonUtils.isNullOrEmpty(this.seconndaryParamMap)) {
      this.seconndaryParamMap = new ConcurrentHashMap<>();
      Set<TRvwParametersSecondary> tRvwParametersSecondaries =
          getEntityProvider().getDbResSecondary(getID()).getTRvwParametersSecondaries();
      if (tRvwParametersSecondaries != null) {
        for (TRvwParametersSecondary tRvwParametersSecondary : tRvwParametersSecondaries) {
          CDRResParamSecondary secondaryResParam =
              new CDRResParamSecondary(getDataProvider(), tRvwParametersSecondary.getSecRvwParamId());
          this.seconndaryParamMap.put(tRvwParametersSecondary.getTRvwParameter().getRvwParamId(), secondaryResParam);
        }

      }
    }
    return this.seconndaryParamMap;
  }

  /**
   * @return the ssd version id
   */
  public Long getSSDVersionID() {
    return getEntityProvider().getDbResSecondary(getID()).getSsdVersID();
  }

  /**
   * @return the Source of rule
   */
  public RULE_SOURCE getSource() {
    return RULE_SOURCE.getSource(getEntityProvider().getDbResSecondary(getID()).getSource());

  }


  /**
   * @return the Source of rule
   */
  public long getSSdReleaseID() {
    return getEntityProvider().getDbResSecondary(getID()).getSsdReleaseID();
  }

  /**
   * @return the Rule set
   */
  public RuleSet getRuleSet() {
    RuleSet ruleSet = null;
    TRuleSet tRuleSet = getEntityProvider().getDbResSecondary(getID()).getTRuleSet();
    if (tRuleSet != null) {
      SortedSet<RuleSet> allRuleSets = getDataProvider().getAllRuleSets(false);
      for (RuleSet ruleSet2 : allRuleSets) {
        if (ruleSet2.getID().equals(tRuleSet.getRsetId())) {
          ruleSet = getDataProvider().getRuleSet(tRuleSet.getRsetId());
        }
      }
    }
    return ruleSet;
  }


  /**
   * @return the created user
   */
  @Override
  public final String getCreatedUser() {
    return getEntityProvider().getDbResSecondary(getID()).getCreatedUser();
  }

  /**
   * @return the created date
   */
  @Override
  public final Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbResSecondary(getID()).getCreatedDate());
  }

  /**
   * @return the modified date
   */
  @Override
  public final Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbResSecondary(getID()).getModifiedDate());
  }

  /**
   * @return the modified user
   */
  @Override
  public final String getModifiedUser() {
    return getEntityProvider().getDbResSecondary(getID()).getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CDRSecondaryResult other) {
    return ApicUtil.compareLong(getID(), other.getID());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * {@inheritDoc} return object details in Map
   */
  @Override
  public Map<String, String> getObjectDetails() {
    final Map<String, String> objDetails = new HashMap<String, String>();
    return objDetails;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return CDREntityType.CDR_RESULT_SECONDARY;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return getCdrResult().getDescription();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getCdrResult().getName();
  }
}
