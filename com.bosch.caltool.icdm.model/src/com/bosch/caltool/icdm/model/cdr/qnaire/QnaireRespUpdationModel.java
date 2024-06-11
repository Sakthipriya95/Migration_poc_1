/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.qnaire;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.icdm.model.wp.WorkPkg;

/**
 * @author dmr1cob
 */
public class QnaireRespUpdationModel {

  /**
   * Selected pidc version
   */
  private Long pidcVersionId;
  /**
   * selected pidc variant id
   */
  private Long pidcVariantId;
  /**
   * division id
   */
  private Long divId;
  /**
   * selected wp id
   */
  private Long selWpId;
  /**
   * selected resp id
   */
  private Long selRespId;
  /**
   * selected wp for which qnaire resp to be created
   */
  private SortedSet<WorkPkg> workPkgSet = new TreeSet<>();
  /**
   * Already available qnaire resp
   */
  private SortedSet<RvwQnaireResponse> oldQnaireRespSet = new TreeSet<>();
  /**
   * Update qnaire resp
   */
  private Map<Long, RvwQnaireResponse> updatedQnaireRespMap = new HashMap<>();
  /**
   * Qnaire resp created from selected work package object
   */
  private SortedSet<RvwQnaireResponse> newQnaireRespSet = new TreeSet<>();

  private Long qnaireVersId;
  /**
   * Variant linking details
   */
  private Set<QnaireRespVarRespWpLink> qnaireRespVarLinkSet = new HashSet<>();
  /**
   * Newwly created {@link RvwQnaireRespVariant} variant link object
   */
  private Set<RvwQnaireRespVariant> createdQnaireRespVariantSet = new HashSet<>();
  /**
   * Deleted {@link RvwQnaireRespVariant} variant link object
   */
  private Set<RvwQnaireRespVariant> deletedQnaireRespVariantMap = new HashSet<>();

  /**
   * Names of 'General questionnaires not required when adding these questionnaires'
   */
  private final Set<String> genQuesNotReqQues = new HashSet<>();

  /**
   * @return the pidcVersion
   */
  public Long getPidcVersionId() {
    return this.pidcVersionId;
  }


  /**
   * @param pidcVersion the pidcVersion to set
   */
  public void setPidcVersionId(final Long pidcVersion) {
    this.pidcVersionId = pidcVersion;
  }


  /**
   * @return the pidcVariant
   */
  public Long getPidcVariantId() {
    return this.pidcVariantId;
  }


  /**
   * @param pidcVariant the pidcVariant to set
   */
  public void setPidcVariantId(final Long pidcVariant) {
    this.pidcVariantId = pidcVariant;
  }


  /**
   * @return the divId
   */
  public Long getDivId() {
    return this.divId;
  }


  /**
   * @param divId the divId to set
   */
  public void setDivId(final Long divId) {
    this.divId = divId;
  }


  /**
   * @return the workPkgSet
   */
  public SortedSet<WorkPkg> getWorkPkgSet() {
    return this.workPkgSet;
  }


  /**
   * @param workPkgSet the workPkgSet to set
   */
  public void setWorkPkgSet(final SortedSet<WorkPkg> workPkgSet) {
    this.workPkgSet = workPkgSet;
  }


  /**
   * @return the oldQnaireRespSet
   */
  public SortedSet<RvwQnaireResponse> getOldQnaireRespSet() {
    return this.oldQnaireRespSet;
  }


  /**
   * @param oldQnaireRespSet the oldQnaireRespSet to set
   */
  public void setOldQnaireRespSet(final SortedSet<RvwQnaireResponse> oldQnaireRespSet) {
    this.oldQnaireRespSet = oldQnaireRespSet;
  }


  /**
   * @return the updatedQnaireRespSet
   */
  public Map<Long, RvwQnaireResponse> getUpdatedQnaireRespMap() {
    return this.updatedQnaireRespMap;
  }


  /**
   * @param updatedQnaireRespMap the updatedQnaireRespSet to set
   */
  public void setUpdatedQnaireRespSet(final Map<Long, RvwQnaireResponse> updatedQnaireRespMap) {
    this.updatedQnaireRespMap = updatedQnaireRespMap;
  }


  /**
   * @return the selWpId
   */
  public Long getSelWpId() {
    return this.selWpId;
  }


  /**
   * @param selWpId the selWpId to set
   */
  public void setSelWpId(final Long selWpId) {
    this.selWpId = selWpId;
  }


  /**
   * @return the selRespId
   */
  public Long getSelRespId() {
    return this.selRespId;
  }


  /**
   * @param selRespId the selRespId to set
   */
  public void setSelRespId(final Long selRespId) {
    this.selRespId = selRespId;
  }

  /**
   * @return the qnaireVersId
   */
  public Long getQnaireVersId() {
    return this.qnaireVersId;
  }


  /**
   * @param qnaireVersId the qnaireVersId to set
   */
  public void setQnaireVersId(final Long qnaireVersId) {
    this.qnaireVersId = qnaireVersId;
  }


  /**
   * @return the qnaireRespVarLinkSet
   */
  public Set<QnaireRespVarRespWpLink> getQnaireRespVarLinkSet() {
    return this.qnaireRespVarLinkSet;
  }


  /**
   * @param qnaireRespVarLinkSet the qnaireRespVarLinkSet to set
   */
  public void setQnaireRespVarLinkSet(final Set<QnaireRespVarRespWpLink> qnaireRespVarLinkSet) {
    this.qnaireRespVarLinkSet = qnaireRespVarLinkSet;
  }


  /**
   * @return the rvwQnaireRespVariant
   */
  public Set<RvwQnaireRespVariant> getCreatedQnaireRespVariantSet() {
    return this.createdQnaireRespVariantSet;
  }


  /**
   * @param createdQnaireRespVariant the rvwQnaireRespVariant to set
   */
  public void setCreatedQnaireRespVariantSet(final Set<RvwQnaireRespVariant> createdQnaireRespVariant) {
    this.createdQnaireRespVariantSet = createdQnaireRespVariant;
  }


  /**
   * @return the deletedQnaireRespVariant
   */
  public Set<RvwQnaireRespVariant> getDeletedQnaireRespVariant() {
    return this.deletedQnaireRespVariantMap;
  }


  /**
   * @param deletedQnaireRespVariant the deletedQnaireRespVariant to set
   */
  public void setDeletedQnaireRespVariant(final Set<RvwQnaireRespVariant> deletedQnaireRespVariant) {
    this.deletedQnaireRespVariantMap = deletedQnaireRespVariant;
  }


  /**
   * @return the newQnaireRespSet
   */
  public SortedSet<RvwQnaireResponse> getNewQnaireRespSet() {
    return this.newQnaireRespSet;
  }


  /**
   * @param newQnaireRespSet the newQnaireRespSet to set
   */
  public void setNewQnaireRespSet(final SortedSet<RvwQnaireResponse> newQnaireRespSet) {
    this.newQnaireRespSet = newQnaireRespSet;
  }


  /**
   * @return the genQuesNotReqQues
   */
  public Set<String> getGenQuesNotReqQues() {
    return this.genQuesNotReqQues;
  }

}
