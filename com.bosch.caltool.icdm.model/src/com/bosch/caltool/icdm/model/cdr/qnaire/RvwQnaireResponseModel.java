/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.qnaire;

import java.util.HashMap;
import java.util.Map;

import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.general.Link;

/**
 * The Class RvwQnaireResponseExt.
 *
 * @author gge6cob
 */
public class RvwQnaireResponseModel {

  boolean isModifiable = false;

  /**
   * The pidc version object
   */
  private PidcVersion pidcVersion;

  /**
   * The pidc variant object
   */
  private PidcVariant pidcVariant;

  /**
   * The pidc object
   */
  private Pidc pidc;

  /**
   * General Qnr Versiond Id
   */
  private Long generalQnaireVersId;

  /**
   * The rvw qnr response
   */
  private RvwQnaireResponse rvwQnrResponse;

  /**
   * The A2L Responsibility for rvw questionnaire response.
   */
  private A2lResponsibility a2lResponsibility;

  /**
   * The rvw qnr response version.
   */
  private RvwQnaireRespVersion rvwQnrRespVersion;

  /**
   * The rvw qnr answr map. key - review qnire answer id, valuew - review qnire answer object
   */
  private Map<Long, RvwQnaireAnswer> rvwQnrAnswrMap;

  /**
   * Key : QnrAnswerId , value : Map of (key - opl id, value - open points object)
   */
  private Map<Long, Map<Long, RvwQnaireAnswerOpl>> openPointsMap = new HashMap<>();

  /**
   * Key : QnrAnswerId , value :Map of (key - link id, value - link object)
   */
  private Map<Long, Map<Long, Link>> linksMap = new HashMap<>();

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
   * @return the pidcVariant
   */
  public PidcVariant getPidcVariant() {
    return this.pidcVariant;
  }


  /**
   * @param pidcVariant the pidcVariant to set
   */
  public void setPidcVariant(final PidcVariant pidcVariant) {
    this.pidcVariant = pidcVariant;
  }


  /**
   * @return the rvwQnrAnswrMap
   */
  public Map<Long, RvwQnaireAnswer> getRvwQnrAnswrMap() {
    return this.rvwQnrAnswrMap;
  }


  /**
   * @param rvwQnrAnswrMap the rvwQnrAnswrMap to set
   */
  public void setRvwQnrAnswrMap(final Map<Long, RvwQnaireAnswer> rvwQnrAnswrMap) {
    this.rvwQnrAnswrMap = rvwQnrAnswrMap;
  }


  /**
   * @return the openPointsMap
   */
  public Map<Long, Map<Long, RvwQnaireAnswerOpl>> getOpenPointsMap() {
    return this.openPointsMap;
  }


  /**
   * @param openPointsMap the openPointsMap to set
   */
  public void setOpenPointsMap(final Map<Long, Map<Long, RvwQnaireAnswerOpl>> openPointsMap) {
    this.openPointsMap = openPointsMap;
  }


  /**
   * @return the isModifiable
   */
  public boolean isModifiable() {
    return this.isModifiable;
  }


  /**
   * @param isModifiable the isModifiable to set
   */
  public void setModifiable(final boolean isModifiable) {
    this.isModifiable = isModifiable;
  }


  /**
   * @return the rvwQnrResponse
   */
  public RvwQnaireResponse getRvwQnrResponse() {
    return this.rvwQnrResponse;
  }


  /**
   * @param rvwQnrResponse the rvwQnrResponse to set
   */
  public void setRvwQnrResponse(final RvwQnaireResponse rvwQnrResponse) {
    this.rvwQnrResponse = rvwQnrResponse;
  }


  /**
   * @return the generalQnaireVersId
   */
  public Long getGeneralQnaireVersId() {
    return this.generalQnaireVersId;
  }


  /**
   * @param generalQnaireVersId the generalQnaireVersId to set
   */
  public void setGeneralQnaireVersId(final Long generalQnaireVersId) {
    this.generalQnaireVersId = generalQnaireVersId;
  }


  /**
   * @return the rvwQnrRespVersion
   */
  public RvwQnaireRespVersion getRvwQnrRespVersion() {
    return this.rvwQnrRespVersion;
  }


  /**
   * @param rvwQnrRespVersion the rvwQnrRespVersion to set
   */
  public void setRvwQnrRespVersion(final RvwQnaireRespVersion rvwQnrRespVersion) {
    this.rvwQnrRespVersion = rvwQnrRespVersion;
  }


  /**
   * @return the pidc
   */
  public Pidc getPidc() {
    return this.pidc;
  }


  /**
   * @param pidc the pidc to set
   */
  public void setPidc(final Pidc pidc) {
    this.pidc = pidc;
  }


  /**
   * @return the linksMap
   */
  public Map<Long, Map<Long, Link>> getLinksMap() {
    return this.linksMap;
  }


  /**
   * @param linksMap the linksMap to set
   */
  public void setLinksMap(final Map<Long, Map<Long, Link>> linksMap) {
    this.linksMap = linksMap;
  }


  /**
   * @return the qnaireRespA2lResponsibility
   */
  public A2lResponsibility getQnaireRespA2lResponsibility() {
    return this.a2lResponsibility;
  }


  /**
   * @param qnaireRespA2lResponsibility the qnaireRespA2lResponsibility to set
   */
  public void setQnaireRespA2lResponsibility(final A2lResponsibility qnaireRespA2lResponsibility) {
    this.a2lResponsibility = qnaireRespA2lResponsibility;
  }
}
