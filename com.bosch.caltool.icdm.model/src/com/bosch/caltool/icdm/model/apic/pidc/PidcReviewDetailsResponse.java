/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;

/**
 * @author mkl2cob
 */
public class PidcReviewDetailsResponse {

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


}
