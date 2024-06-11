/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.RvwWpResp;
import com.bosch.caltool.icdm.model.cdr.cdfx.CDFxDelWpResp;
import com.bosch.caltool.icdm.model.cdr.cdfx.CdfxDelvryParam;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswerOpl;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;

/**
 * @author dmr1cob
 */
public class A2lResponsibilityMergeModel {

  /**
   * Destination a2l responsibility id
   */
  private Long a2lRespMergeToId;

  /**
   * Set of source responsibility id that needs to be merged with destination resp id
   */
  private Set<Long> a2lRespMergeFromIdSet = new HashSet<>();

  /**
   * Set of duplicate qnaire resp id that needs to be retained after merge
   */
  private Set<Long> qnaireRespIdSet = new HashSet<>();

  private Set<CDFxDelWpResp> cdfxDlvryWpRespOld = new HashSet<>();

  private Map<Long, CDFxDelWpResp> cdfxDlvryWpRespUpdate = new HashMap<>();

  private Set<CDFxDelWpResp> cdfxDlvryWpRespDelete = new HashSet<>();

  private Set<CdfxDelvryParam> cdfxDelvryParamOld = new HashSet<>();

  private Map<Long, CdfxDelvryParam> cdfxDelvryParamUpdate = new HashMap<>();

  private Set<RvwWpResp> rvwWpRespOld = new HashSet<>();

  private Map<Long, RvwWpResp> rvwWpRespUpdate = new HashMap<>();

  private Set<RvwWpResp> rvwWpRespDelete = new HashSet<>();

  private Set<CDRResultParameter> cdrResultParameterOld = new HashSet<>();

  private Map<Long, CDRResultParameter> cdrResultParameterUpdate = new HashMap<>();

  private Set<RvwQnaireResponse> rvwQnaireResponseOld = new HashSet<>();

  private Map<Long, RvwQnaireResponse> rvwQnaireResponseUpdate = new HashMap<>();

  private Set<RvwQnaireResponse> rvwQnaireResponseDelete = new HashSet<>();

  private Set<RvwQnaireRespVariant> rvwQnaireRespVariantDelete = new HashSet<>();

  private Set<RvwQnaireAnswer> rvwQnaireAnswerDelete = new HashSet<>();

  private Set<RvwQnaireAnswerOpl> rvwQnaireAnswerOplDelete = new HashSet<>();

  private Set<RvwQnaireRespVersion> rvwQnaireRespVersionDelete = new HashSet<>();

  private Set<A2lWpParamMapping> a2lWpParamMappingOld = new HashSet<>();

  private Set<A2lWpResponsibility> a2lWpResponsibilityOld = new HashSet<>();

  private Map<Long, A2lWpParamMapping> a2lWpParamMappingUpdate = new HashMap<>();

  private Map<Long, A2lWpResponsibility> a2lWpResponsibilityUpdate = new HashMap<>();

  private Set<A2lResponsibility> a2lResponsibilityDeleteSet = new HashSet<>();

  /**
   * @return the a2lRespMergeToId
   */
  public Long getA2lRespMergeToId() {
    return this.a2lRespMergeToId;
  }


  /**
   * @param a2lRespMergeToId the a2lRespMergeToId to set
   */
  public void setA2lRespMergeToId(final Long a2lRespMergeToId) {
    this.a2lRespMergeToId = a2lRespMergeToId;
  }


  /**
   * @return the a2lRespMergeFromIdList
   */
  public Set<Long> getA2lRespMergeFromIdSet() {
    return new HashSet<>(this.a2lRespMergeFromIdSet);
  }


  /**
   * @param a2lRespMergeFromIdSet the a2lRespMergeFromIdList to set
   */
  public void setA2lRespMergeFromIdSet(final Set<Long> a2lRespMergeFromIdSet) {
    this.a2lRespMergeFromIdSet = new HashSet<>(a2lRespMergeFromIdSet);
  }


  /**
   * @return the qnaireRespIdSet
   */
  public Set<Long> getQnaireRespIdSet() {
    return new HashSet<>(this.qnaireRespIdSet);
  }


  /**
   * @param qnaireRespIdSet the qnaireRespIdSet to set
   */
  public void setQnaireRespIdSet(final Set<Long> qnaireRespIdSet) {
    this.qnaireRespIdSet = new HashSet<>(qnaireRespIdSet);
  }

  /**
   * @return the cdfxDlvryWpRespOld
   */
  public Set<CDFxDelWpResp> getCdfxDlvryWpRespOld() {
    return this.cdfxDlvryWpRespOld;
  }


  /**
   * @param cdfxDlvryWpRespOld the cdfxDlvryWpRespOld to set
   */
  public void setCdfxDlvryWpRespOld(final Set<CDFxDelWpResp> cdfxDlvryWpRespOld) {
    this.cdfxDlvryWpRespOld = cdfxDlvryWpRespOld;
  }


  /**
   * @return the cdfxDlvryWpRespUpdate
   */
  public Map<Long, CDFxDelWpResp> getCdfxDlvryWpRespUpdate() {
    return new HashMap<>(this.cdfxDlvryWpRespUpdate);
  }


  /**
   * @param cdfxDlvryWpRespUpdate the cdfxDlvryWpRespUpdate to set
   */
  public void setCdfxDlvryWpRespUpdate(final Map<Long, CDFxDelWpResp> cdfxDlvryWpRespUpdate) {
    this.cdfxDlvryWpRespUpdate = new HashMap<>(cdfxDlvryWpRespUpdate);
  }


  /**
   * @return the cdfxDlvryWpRespDelete
   */
  public Set<CDFxDelWpResp> getCdfxDlvryWpRespDelete() {
    return new HashSet<>(this.cdfxDlvryWpRespDelete);
  }


  /**
   * @param cdfxDlvryWpRespDelete the cdfxDlvryWpRespDelete to set
   */
  public void setCdfxDlvryWpRespDelete(final Set<CDFxDelWpResp> cdfxDlvryWpRespDelete) {
    this.cdfxDlvryWpRespDelete = new HashSet<>(cdfxDlvryWpRespDelete);
  }


  /**
   * @return the cdfxDelvryParamOld
   */
  public Set<CdfxDelvryParam> getCdfxDelvryParamOld() {
    return this.cdfxDelvryParamOld;
  }


  /**
   * @param cdfxDelvryParamOld the cdfxDelvryParamOld to set
   */
  public void setCdfxDelvryParamOld(final Set<CdfxDelvryParam> cdfxDelvryParamOld) {
    this.cdfxDelvryParamOld = cdfxDelvryParamOld;
  }


  /**
   * @return the cdfxDelvryParamUpdate
   */
  public Map<Long, CdfxDelvryParam> getCdfxDelvryParamUpdate() {
    return new HashMap<>(this.cdfxDelvryParamUpdate);
  }


  /**
   * @param cdfxDelvryParamUpdate the cdfxDelvryParamUpdate to set
   */
  public void setCdfxDelvryParamUpdate(final Map<Long, CdfxDelvryParam> cdfxDelvryParamUpdate) {
    this.cdfxDelvryParamUpdate = new HashMap<>(cdfxDelvryParamUpdate);
  }


  /**
   * @return the rvwWpRespOld
   */
  public Set<RvwWpResp> getRvwWpRespOld() {
    return this.rvwWpRespOld;
  }


  /**
   * @param rvwWpRespOld the rvwWpRespOld to set
   */
  public void setRvwWpRespOld(final Set<RvwWpResp> rvwWpRespOld) {
    this.rvwWpRespOld = rvwWpRespOld;
  }


  /**
   * @return the rvwWpRespUpdate
   */
  public Map<Long, RvwWpResp> getRvwWpRespUpdate() {
    return new HashMap<>(this.rvwWpRespUpdate);
  }


  /**
   * @param rvwWpRespUpdate the rvwWpRespUpdate to set
   */
  public void setRvwWpRespUpdate(final Map<Long, RvwWpResp> rvwWpRespUpdate) {
    this.rvwWpRespUpdate = new HashMap<>(rvwWpRespUpdate);
  }


  /**
   * @return the rvwWpRespDelete
   */
  public Set<RvwWpResp> getRvwWpRespDelete() {
    return new HashSet<>(this.rvwWpRespDelete);
  }


  /**
   * @param rvwWpRespDelete the rvwWpRespDelete to set
   */
  public void setRvwWpRespDelete(final Set<RvwWpResp> rvwWpRespDelete) {
    this.rvwWpRespDelete = new HashSet<>(rvwWpRespDelete);
  }


  /**
   * @return the cdrResultParameterOld
   */
  public Set<CDRResultParameter> getCdrResultParameterOld() {
    return this.cdrResultParameterOld;
  }


  /**
   * @param cdrResultParameterOld the cdrResultParameterOld to set
   */
  public void setCdrResultParameterOld(final Set<CDRResultParameter> cdrResultParameterOld) {
    this.cdrResultParameterOld = cdrResultParameterOld;
  }


  /**
   * @return the cDRResultParameterUpdate
   */
  public Map<Long, CDRResultParameter> getCdrResultParameterUpdate() {
    return new HashMap<>(this.cdrResultParameterUpdate);
  }


  /**
   * @param cDRResultParameterUpdate the cDRResultParameterUpdate to set
   */
  public void setCdrResultParameterUpdate(final Map<Long, CDRResultParameter> cdrResultParameterUpdate) {
    this.cdrResultParameterUpdate = new HashMap<>(cdrResultParameterUpdate);
  }

  /**
   * @return the rvwQnaireResponseOld
   */
  public Set<RvwQnaireResponse> getRvwQnaireResponseOld() {
    return this.rvwQnaireResponseOld;
  }


  /**
   * @param rvwQnaireResponseOld the rvwQnaireResponseOld to set
   */
  public void setRvwQnaireResponseOld(final Set<RvwQnaireResponse> rvwQnaireResponseOld) {
    this.rvwQnaireResponseOld = rvwQnaireResponseOld;
  }


  /**
   * @return the rvwQnaireResponseUpdate
   */
  public Map<Long, RvwQnaireResponse> getRvwQnaireResponseUpdate() {
    return new HashMap<>(this.rvwQnaireResponseUpdate);
  }


  /**
   * @param rvwQnaireResponseUpdate the rvwQnaireResponseUpdate to set
   */
  public void setRvwQnaireResponseUpdate(final Map<Long, RvwQnaireResponse> rvwQnaireResponseUpdate) {
    this.rvwQnaireResponseUpdate = new HashMap<>(rvwQnaireResponseUpdate);
  }


  /**
   * @return the rvwQnaireResponseDelete
   */
  public Set<RvwQnaireResponse> getRvwQnaireResponseDelete() {
    return new HashSet<>(this.rvwQnaireResponseDelete);
  }


  /**
   * @param rvwQnaireResponseDelete the rvwQnaireResponseDelete to set
   */
  public void setRvwQnaireResponseDelete(final Set<RvwQnaireResponse> rvwQnaireResponseDelete) {
    this.rvwQnaireResponseDelete = new HashSet<>(rvwQnaireResponseDelete);
  }


  /**
   * @return the rvwQnaireRespVariantDelete
   */
  public Set<RvwQnaireRespVariant> getRvwQnaireRespVariantDelete() {
    return new HashSet<>(this.rvwQnaireRespVariantDelete);
  }


  /**
   * @param rvwQnaireRespVariantDelete the rvwQnaireRespVariantDelete to set
   */
  public void setRvwQnaireRespVariantDelete(final Set<RvwQnaireRespVariant> rvwQnaireRespVariantDelete) {
    this.rvwQnaireRespVariantDelete = new HashSet<>(rvwQnaireRespVariantDelete);
  }


  /**
   * @return the rvwQnaireAnswerDelete
   */
  public Set<RvwQnaireAnswer> getRvwQnaireAnswerDelete() {
    return new HashSet<>(this.rvwQnaireAnswerDelete);
  }


  /**
   * @param rvwQnaireAnswerDelete the rvwQnaireAnswerDelete to set
   */
  public void setRvwQnaireAnswerDelete(final Set<RvwQnaireAnswer> rvwQnaireAnswerDelete) {
    this.rvwQnaireAnswerDelete = new HashSet<>(rvwQnaireAnswerDelete);
  }


  /**
   * @return the rvwQnaireAnswerOplDelete
   */
  public Set<RvwQnaireAnswerOpl> getRvwQnaireAnswerOplDelete() {
    return new HashSet<>(this.rvwQnaireAnswerOplDelete);
  }


  /**
   * @param rvwQnaireAnswerOplDelete the rvwQnaireAnswerOplDelete to set
   */
  public void setRvwQnaireAnswerOplDelete(final Set<RvwQnaireAnswerOpl> rvwQnaireAnswerOplDelete) {
    this.rvwQnaireAnswerOplDelete = new HashSet<>(rvwQnaireAnswerOplDelete);
  }


  /**
   * @return the rvwQnaireRespVersionDelete
   */
  public Set<RvwQnaireRespVersion> getRvwQnaireRespVersionDelete() {
    return new HashSet<>(this.rvwQnaireRespVersionDelete);
  }


  /**
   * @param rvwQnaireRespVersionDelete the rvwQnaireRespVersionDelete to set
   */
  public void setRvwQnaireRespVersionDelete(final Set<RvwQnaireRespVersion> rvwQnaireRespVersionDelete) {
    this.rvwQnaireRespVersionDelete = new HashSet<>(rvwQnaireRespVersionDelete);
  }


  /**
   * @return the a2lWpParamMappingOld
   */
  public Set<A2lWpParamMapping> getA2lWpParamMappingOld() {
    return this.a2lWpParamMappingOld;
  }


  /**
   * @param a2lWpParamMappingOld the a2lWpParamMappingOld to set
   */
  public void setA2lWpParamMappingOld(final Set<A2lWpParamMapping> a2lWpParamMappingOld) {
    this.a2lWpParamMappingOld = a2lWpParamMappingOld;
  }


  /**
   * @return the a2lWpResponsibilityOld
   */
  public Set<A2lWpResponsibility> getA2lWpResponsibilityOld() {
    return this.a2lWpResponsibilityOld;
  }


  /**
   * @param a2lWpResponsibilityOld the a2lWpResponsibilityOld to set
   */
  public void setA2lWpResponsibilityOld(final Set<A2lWpResponsibility> a2lWpResponsibilityOld) {
    this.a2lWpResponsibilityOld = a2lWpResponsibilityOld;
  }


  /**
   * @return the a2lWpParamMappingSet
   */
  public Map<Long, A2lWpParamMapping> getA2lWpParamMappingUpdate() {
    return new HashMap<>(this.a2lWpParamMappingUpdate);
  }


  /**
   * @param a2lWpParamMappingSet the a2lWpParamMappingSet to set
   */
  public void setA2lWpParamMappingUpdate(final Map<Long, A2lWpParamMapping> a2lWpParamMappingUpdate) {
    this.a2lWpParamMappingUpdate = new HashMap<>(a2lWpParamMappingUpdate);
  }


  /**
   * @return the a2lWpResponsibilityUpdateSet
   */
  public Map<Long, A2lWpResponsibility> getA2lWpResponsibilityUpdate() {
    return this.a2lWpResponsibilityUpdate;
  }


  /**
   * @param a2lWpResponsibilityUpdate the a2lWpResponsibilityUpdateSet to set
   */
  public void setA2lWpResponsibilityUpdate(final Map<Long, A2lWpResponsibility> a2lWpResponsibilityUpdate) {
    this.a2lWpResponsibilityUpdate = a2lWpResponsibilityUpdate;
  }


  /**
   * @return the a2lResponsibilitySet
   */
  public Set<A2lResponsibility> getA2lResponsibilityDeleteSet() {
    return new HashSet<>(this.a2lResponsibilityDeleteSet);
  }


  /**
   * @param a2lResponsibilityDeleteSet the a2lResponsibilitySet to set
   */
  public void setA2lResponsibilityDeleteSet(final Set<A2lResponsibility> a2lResponsibilityDeleteSet) {
    this.a2lResponsibilityDeleteSet = new HashSet<>(a2lResponsibilityDeleteSet);
  }


}
