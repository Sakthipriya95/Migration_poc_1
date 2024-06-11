/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.xml.bind.annotation.XmlRootElement;

import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibilityStatus;
import com.bosch.caltool.icdm.model.a2l.ParamProperties;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;


/**
 * @author bne4cob
 */
@XmlRootElement
public class CdrReport {

  private PidcVersion pidcVersion;

  private PidcA2l pidcA2l;

  private A2LFile a2lFile;

  private boolean considerReviewsOfPrevPidcVers;

  /**
   * Parameter properties
   * <p>
   * Key - Parameter name <br>
   * Value - ParamProperties
   */
  private Map<String, ParamProperties> paramPropsMap = new HashMap<>();

  /**
   * Key- parameter name<br>
   * Value -Work package name
   */
  private Map<String, String> parameterWorkPackageMap = new HashMap<>();

  /**
   * Parameter specific review details
   * <p>
   * Key - Parameter name <br>
   * Value - List of review details, sorted by date
   */
  private Map<String, List<ParameterReviewDetails>> paramRvwDetMap = new HashMap<>();

  /**
   * Common information of the reviews, like description, review date etc.
   * <p>
   * Key - review ID <br>
   * Value - CDRReviewResult object
   */
  private Map<Long, ReviewDetails> reviewDetMap = new HashMap<>();

  private Map<Long, CDRReviewResult> cdrReviewResultMap = new HashMap<>();

  private Map<Long, PidcA2l> reviewDetA2lMap = new HashMap<>();

  private Map<Long, PidcVersion> reviewDetPidcVersMap = new HashMap<>();

  private Map<Long, PidcVariant> reviewDetPidcVariantMap = new HashMap<>();
  // Key - param id value - arc release flag
  private Map<Long, String> paramwithARCReleaseFlagMap = new HashMap<>();
  // Key - Wp Id, Value - Map Of Resp Id and Wp Resp Status
  private Map<Long, Map<Long, A2lWpResponsibilityStatus>> wpIdRespIdAndStatusMap = new HashMap<>();

  /**
   * Maximum number of reviews available any parameter in the result
   */
  private int maxParamReviewCount;
  // ICDM-1839
  /**
   * Map of func id and fucntion ver of param during review
   * <p>
   * Key - review file ID<br>
   * Value - File name
   */
  private final ConcurrentMap<Long, String> rvwFuncMap = new ConcurrentHashMap<>();
  /**
   * true, if 'Generate Data Review Report' service is called for WP/Resp node under A2l Structure
   */
  private boolean isToGenDataRvwRprtForWPResp;

  /**
   * @return the pidcVersion
   */
  public PidcVersion getPidcVersion() {
    return this.pidcVersion;
  }


  /**
   * @param pidcVersion the pidcVersion to set
   */
  public void setPidcVersion(final PidcVersion pidcVersion) {
    this.pidcVersion = pidcVersion;
  }

  /**
   * @return the pidcA2l
   */
  public PidcA2l getPidcA2l() {
    return this.pidcA2l;
  }


  /**
   * @param pidcA2l the pidcA2l to set
   */
  public void setPidcA2l(final PidcA2l pidcA2l) {
    this.pidcA2l = pidcA2l;
  }


  /**
   * @return the a2lFile
   */
  public A2LFile getA2lFile() {
    return this.a2lFile;
  }


  /**
   * @param a2lFile the a2lFile to set
   */
  public void setA2lFile(final A2LFile a2lFile) {
    this.a2lFile = a2lFile;
  }

  /**
   * @return the paramPropsMap
   */
  public Map<String, ParamProperties> getParamPropsMap() {
    return this.paramPropsMap;
  }

  /**
   * @param paramPropsMap the paramPropsMap to set
   */
  public void setParamPropsMap(final Map<String, ParamProperties> paramPropsMap) {
    this.paramPropsMap = paramPropsMap;
  }


  /**
   * @return the parameterWorkPackageMap
   */
  public Map<String, String> getParameterWorkPackageMap() {
    return this.parameterWorkPackageMap;
  }


  /**
   * @param parameterWorkPackageMap the parameterWorkPackageMap to set
   */
  public void setParameterWorkPackageMap(final Map<String, String> parameterWorkPackageMap) {
    this.parameterWorkPackageMap = parameterWorkPackageMap;
  }


  /**
   * @return the paramRvwDetMap
   */
  public Map<String, List<ParameterReviewDetails>> getParamRvwDetMap() {
    return this.paramRvwDetMap;
  }


  /**
   * @param paramRvwDetMap the paramRvwDetMap to set
   */
  public void setParamRvwDetMap(final Map<String, List<ParameterReviewDetails>> paramRvwDetMap) {
    this.paramRvwDetMap = paramRvwDetMap;
  }


  /**
   * @return the reviewDetMap
   */
  public Map<Long, ReviewDetails> getReviewDetMap() {
    return this.reviewDetMap;
  }


  /**
   * @param reviewDetMap the reviewDetMap to set
   */
  public void setReviewDetMap(final Map<Long, ReviewDetails> reviewDetMap) {
    this.reviewDetMap = reviewDetMap;
  }


  /**
   * @return the cdrReviewResultMap
   */
  public Map<Long, CDRReviewResult> getCdrReviewResultMap() {
    return this.cdrReviewResultMap;
  }


  /**
   * @param cdrReviewResultMap the cdrReviewResultMap to set
   */
  public void setCdrReviewResultMap(final Map<Long, CDRReviewResult> cdrReviewResultMap) {
    this.cdrReviewResultMap = cdrReviewResultMap;
  }


  /**
   * @return the reviewDetA2lMap
   */
  public Map<Long, PidcA2l> getReviewDetA2lMap() {
    return this.reviewDetA2lMap;
  }


  /**
   * @param reviewDetA2lMap the reviewDetA2lMap to set
   */
  public void setReviewDetA2lMap(final Map<Long, PidcA2l> reviewDetA2lMap) {
    this.reviewDetA2lMap = reviewDetA2lMap;
  }


  /**
   * @return the reviewDetPidcVersMap
   */
  public Map<Long, PidcVersion> getReviewDetPidcVersMap() {
    return this.reviewDetPidcVersMap;
  }


  /**
   * @param reviewDetPidcVersMap the reviewDetPidcVersMap to set
   */
  public void setReviewDetPidcVersMap(final Map<Long, PidcVersion> reviewDetPidcVersMap) {
    this.reviewDetPidcVersMap = reviewDetPidcVersMap;
  }


  /**
   * @return the reviewDetPidcVariantMap
   */
  public Map<Long, PidcVariant> getReviewDetPidcVariantMap() {
    return this.reviewDetPidcVariantMap;
  }


  /**
   * @param reviewDetPidcVariantMap the reviewDetPidcVariantMap to set
   */
  public void setReviewDetPidcVariantMap(final Map<Long, PidcVariant> reviewDetPidcVariantMap) {
    this.reviewDetPidcVariantMap = reviewDetPidcVariantMap;
  }


  /**
   * @return the maxParamReviewCount
   */
  public int getMaxParamReviewCount() {
    return this.maxParamReviewCount;
  }

  /**
   * @param maxParamReviewCount the maxParamReviewCount to set
   */
  public void setMaxParamReviewCount(final int maxParamReviewCount) {
    this.maxParamReviewCount = maxParamReviewCount;
  }


  /**
   * @return the rvwFuncMap
   */
  public Map<Long, String> getRvwFuncMap() {
    return this.rvwFuncMap;
  }


  /**
   * @return the isToGenDataRvwRprtForWPResp
   */
  public boolean isToGenDataRvwRprtForWPResp() {
    return this.isToGenDataRvwRprtForWPResp;
  }


  /**
   * @param isToGenDataRvwRprtForWPResp the isToGenDataRvwRprtForWPResp to set
   */
  public void setToGenDataRvwRprtForWPResp(final boolean isToGenDataRvwRprtForWPResp) {
    this.isToGenDataRvwRprtForWPResp = isToGenDataRvwRprtForWPResp;
  }


  /**
   * @return the paramwithARCReleaseFlagMap
   */
  public Map<Long, String> getParamwithARCReleaseFlagMap() {
    return this.paramwithARCReleaseFlagMap;
  }


  /**
   * @param paramwithARCReleaseFlagMap the paramwithARCReleaseFlagMap to set
   */
  public void setParamwithARCReleaseFlagMap(final Map<Long, String> paramwithARCReleaseFlagMap) {
    this.paramwithARCReleaseFlagMap = paramwithARCReleaseFlagMap;
  }

  /**
   * @return the wpIdRespIdAndStatusMap
   */
  public Map<Long, Map<Long, A2lWpResponsibilityStatus>> getWpIdRespIdAndStatusMap() {
    return this.wpIdRespIdAndStatusMap;
  }


  /**
   * @param wpIdRespIdAndStatusMap the wpIdRespIdAndStatusMap to set
   */
  public void setWpIdRespIdAndStatusMap(final Map<Long, Map<Long, A2lWpResponsibilityStatus>> wpIdRespIdAndStatusMap) {
    this.wpIdRespIdAndStatusMap = wpIdRespIdAndStatusMap;
  }


  /**
   * @return the considerReviews
   */
  public boolean getConsiderReviewsOfPrevPidcVers() {
    return this.considerReviewsOfPrevPidcVers;
  }


  /**
   * @param considerReviews the considerReviews to set
   */
  public void setConsiderReviewsOfPrevPidcVers(final boolean considerReviews) {
    this.considerReviewsOfPrevPidcVers = considerReviews;
  }


}
