/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;

/**
 * @author mkl2cob
 */
public class PidcReviewDetails {

  /**
   * ------------------------variant workpackage information ----------------------------------- key - variant id ,
   * Value - Map<wp id , Set<review result id>>
   */
  private Map<Long, Map<Long, Set<Long>>> varWpMap = new HashMap<>();
  /**
   * ------------------------variant reponsibility information along with workpackage-----------------------------------
   * key - variant id , Value - Map of <resp id, Map<wp id , Set<review result id>>>>
   */
  private Map<Long, Map<Long, Map<Long, Set<Long>>>> varRespWpMap = new HashMap<>();

  /**
   * ------------------------variant workpackage information ----------------------------------- key - variant id ,
   * Value - Map<String , Set<review result id>>
   */
  private Map<Long, Map<String, Set<Long>>> otherSrcTypeResults = new HashMap<>();

  /**
   * key - result id , Value - CDRReviewResult
   */
  private Map<Long, CDRReviewResult> cdrResultMap = new HashMap<>();

  /**
   * key - review variant id , value - RvwVariant
   */
  private Map<Long, RvwVariant> rvwVariantMap = new HashMap<>();

  /**
   * key - workpackage id , value - A2LWorkpackage
   */
  private Map<Long, A2lWorkPackage> a2lWpMap = new HashMap<>();

  /**
   * key - responsibility id , value - A2lResponsibility
   */
  private Map<Long, A2lResponsibility> a2lRespMap = new HashMap<>();

  /**
   * key - pidc variant id, value - PidcVariant
   */
  private Map<Long, PidcVariant> pidcVarMap = new HashMap<>();

  /**
   * key - result id, value - Map of <var id, rvw var id>>
   */
  private final Map<Long, Map<Long, Long>> resToVarMap = new HashMap<>();


  /**
   * @param pidcReviewDetailsResponse
   */
  public PidcReviewDetails(final PidcReviewDetailsResponse pidcReviewDetailsResponse) {
    setA2lRespMap(pidcReviewDetailsResponse.getA2lRespMap());
    setA2lWpMap(pidcReviewDetailsResponse.getA2lWpMap());
    setCdrResultMap(pidcReviewDetailsResponse.getCdrResultMap());
    setOtherSrcTypeResults(pidcReviewDetailsResponse.getOtherSrcTypeResults());
    setPidcVarMap(pidcReviewDetailsResponse.getPidcVarMap());
    setRvwVariantMap(pidcReviewDetailsResponse.getRvwVariantMap());
    setVarRespWpMap(pidcReviewDetailsResponse.getVarRespWpMap());
    createVarWpMap(pidcReviewDetailsResponse.getVarRespWpMap());
    createResToVarMap(this.cdrResultMap);
  }


  /**
   * @param varRespWpMap2
   */
  private void createResToVarMap(final Map<Long, CDRReviewResult> cdrResultMap2) {

    cdrResultMap2.entrySet().forEach(resEntry -> {
      Map<Long, Long> varRvwVarMap = this.resToVarMap.get(resEntry.getKey());
      if (null == varRvwVarMap) {
        varRvwVarMap = new HashMap<>();
      }
      for (Entry<Long, RvwVariant> rvwVariant : this.rvwVariantMap.entrySet()) {
        if (rvwVariant.getValue().getResultId().longValue() == resEntry.getKey().longValue()) {
          varRvwVarMap.put(rvwVariant.getValue().getVariantId(), rvwVariant.getKey());
        }
      }
      this.resToVarMap.put(resEntry.getKey(), varRvwVarMap);
    });


  }


  /**
   * @param varRespWpMap2
   */
  private void createVarWpMap(final Map<Long, Map<Long, Map<Long, Set<Long>>>> varRespWpMap) {

    varRespWpMap.entrySet().forEach(varRespWpEntry -> {

      varRespWpEntry.getValue().entrySet().forEach(respEntry -> {

        respEntry.getValue().entrySet().forEach(wpEntry -> {
          Map<Long, Set<Long>> wpRevResultMap = this.varWpMap.get(varRespWpEntry.getKey());
          if (wpRevResultMap == null) {
            wpRevResultMap = new HashMap<>();
            this.varWpMap.put(varRespWpEntry.getKey(), wpRevResultMap);
          }
          checkAndAddResToMap(wpEntry, wpRevResultMap);

        });
      });

    });

  }


  /**
   * @param wpEntry
   * @param wpRevResultMap
   */
  private void checkAndAddResToMap(final Entry<Long, Set<Long>> wpEntry, final Map<Long, Set<Long>> wpRevResultMap) {
    Set<Long> reviewResultSet = wpRevResultMap.get(wpEntry.getKey());
    if (null == reviewResultSet) {
      reviewResultSet = new HashSet<>();
      wpRevResultMap.put(wpEntry.getKey(), reviewResultSet);
    }
    for (Long resultId : wpEntry.getValue()) {

      if (!reviewResultSet.contains(resultId)) {
        reviewResultSet.add(resultId);
      }

    }
  }


  /**
   * @return the varWpMap
   */
  public Map<Long, Map<Long, Set<Long>>> getVarWpMap() {
    return this.varWpMap;
  }


  /**
   * @param varWpMap the varWpMap to set
   */
  public void setVarWpMap(final Map<Long, Map<Long, Set<Long>>> varWpMap) {
    this.varWpMap = varWpMap;
  }


  /**
   * @return the varRespWpMap
   */
  public Map<Long, Map<Long, Map<Long, Set<Long>>>> getVarRespWpMap() {
    return this.varRespWpMap;
  }


  /**
   * @param varRespWpMap the varRespWpMap to set
   */
  public void setVarRespWpMap(final Map<Long, Map<Long, Map<Long, Set<Long>>>> varRespWpMap) {
    this.varRespWpMap = varRespWpMap;
  }


  /**
   * @return the otherSrcTypeResults
   */
  public Map<Long, Map<String, Set<Long>>> getOtherSrcTypeResults() {
    return this.otherSrcTypeResults;
  }


  /**
   * @param otherSrcTypeResults the otherSrcTypeResults to set
   */
  public void setOtherSrcTypeResults(final Map<Long, Map<String, Set<Long>>> otherSrcTypeResults) {
    this.otherSrcTypeResults = otherSrcTypeResults;
  }


  /**
   * @return the cdrResultMap
   */
  public Map<Long, CDRReviewResult> getCdrResultMap() {
    return this.cdrResultMap;
  }


  /**
   * @param cdrResultMap the cdrResultMap to set
   */
  public void setCdrResultMap(final Map<Long, CDRReviewResult> cdrResultMap) {
    this.cdrResultMap = cdrResultMap;
  }


  /**
   * @return the rvwVariantMap
   */
  public Map<Long, RvwVariant> getRvwVariantMap() {
    return this.rvwVariantMap;
  }


  /**
   * @param rvwVariantMap the rvwVariantMap to set
   */
  public void setRvwVariantMap(final Map<Long, RvwVariant> rvwVariantMap) {
    this.rvwVariantMap = rvwVariantMap;
  }


  /**
   * @return the a2lWpMap
   */
  public Map<Long, A2lWorkPackage> getA2lWpMap() {
    return this.a2lWpMap;
  }


  /**
   * @param a2lWpMap the a2lWpMap to set
   */
  public void setA2lWpMap(final Map<Long, A2lWorkPackage> a2lWpMap) {
    this.a2lWpMap = a2lWpMap;
  }


  /**
   * @return the a2lRespMap
   */
  public Map<Long, A2lResponsibility> getA2lRespMap() {
    return this.a2lRespMap;
  }


  /**
   * @param a2lRespMap the a2lRespMap to set
   */
  public void setA2lRespMap(final Map<Long, A2lResponsibility> a2lRespMap) {
    this.a2lRespMap = a2lRespMap;
  }


  /**
   * @return the pidcVarMap
   */
  public Map<Long, PidcVariant> getPidcVarMap() {
    return this.pidcVarMap;
  }


  /**
   * @param pidcVarMap the pidcVarMap to set
   */
  public void setPidcVarMap(final Map<Long, PidcVariant> pidcVarMap) {
    this.pidcVarMap = pidcVarMap;
  }


  /**
   * @return the resToVarMap
   */
  public Map<Long, Map<Long, Long>> getResToVarMap() {
    return this.resToVarMap;
  }
}
