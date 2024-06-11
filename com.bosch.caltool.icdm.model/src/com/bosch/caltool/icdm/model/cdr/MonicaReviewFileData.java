/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author say8cob
 */
public class MonicaReviewFileData {

  private String dcmFileName;

  private String monicaFileName;

  private byte[] dcmByteArray;

  private byte[] monicaByteArray;

  private MonicaReviewInputData monicaReviewInputData;

  private Set<String> a2lFunctionList;

  private Map<String, byte[]> calDataByteArray;

  private String warnMsg;

  /**
   * set of wpRespId and A2lRespId
   */
  private Set<RvwWpAndRespModel> rvwMonicaWpAndRespModelSet = new HashSet<>();

  /**
   * Map Contains Review Params and Wp Resp Map - Key Param Id and Value combination of WpRespId and A2lRespId
   */
  private Map<Long, RvwWpAndRespModel> rvwMonicaParamAndWpRespModelMap = new HashMap<>();

  private Long a2lWpDefVersId;


  /**
   * @return the warnMsg
   */
  public String getWarnMsg() {
    return this.warnMsg;
  }


  /**
   * @param warnMsg the warnMsg to set
   */
  public void setWarnMsg(final String warnMsg) {
    this.warnMsg = warnMsg;
  }


  /**
   * @return the calDataByteArray
   */
  public Map<String, byte[]> getCalDataByteArray() {
    return this.calDataByteArray;
  }


  /**
   * @param calDataByteArray the calDataByteArray to set
   */
  public void setCalDataByteArray(final Map<String, byte[]> calDataByteArray) {
    this.calDataByteArray = calDataByteArray;
  }


  /**
   * @return the a2lFunctionList
   */
  public Set<String> getA2lFunctionList() {
    return this.a2lFunctionList;
  }


  /**
   * @param a2lFunctionList the a2lFunctionList to set
   */
  public void setA2lFunctionList(final Set<String> a2lFunctionList) {
    this.a2lFunctionList = a2lFunctionList;
  }


  /**
   * @return the dcmFileName
   */
  public String getDcmFileName() {
    return this.dcmFileName;
  }


  /**
   * @param dcmFileName the dcmFileName to set
   */
  public void setDcmFileName(final String dcmFileName) {
    this.dcmFileName = dcmFileName;
  }


  /**
   * @return the monicaFileName
   */
  public String getMonicaFileName() {
    return this.monicaFileName;
  }


  /**
   * @param monicaFileName the monicaFileName to set
   */
  public void setMonicaFileName(final String monicaFileName) {
    this.monicaFileName = monicaFileName;
  }

  /**
   * @return the dcmByteArray
   */
  public byte[] getDcmByteArray() {
    return this.dcmByteArray;
  }


  /**
   * @param dcmByteArray the dcmByteArray to set
   */
  public void setDcmByteArray(final byte[] dcmByteArray) {
    this.dcmByteArray = dcmByteArray;
  }


  /**
   * @return the monicaByteArray
   */
  public byte[] getMonicaByteArray() {
    return this.monicaByteArray;
  }


  /**
   * @param monicaByteArray the monicaByteArray to set
   */
  public void setMonicaByteArray(final byte[] monicaByteArray) {
    this.monicaByteArray = monicaByteArray;
  }


  /**
   * @return the monicaReviewInputData
   */
  public MonicaReviewInputData getMonicaReviewInputData() {
    return this.monicaReviewInputData;
  }


  /**
   * @param monicaReviewInputData the monicaReviewInputData to set
   */
  public void setMonicaReviewInputData(final MonicaReviewInputData monicaReviewInputData) {
    this.monicaReviewInputData = monicaReviewInputData;
  }


  /**
   * @return the rvwMonicaWpAndRespModelSet
   */
  public Set<RvwWpAndRespModel> getRvwMonicaWpAndRespModelSet() {
    return this.rvwMonicaWpAndRespModelSet;
  }


  /**
   * @param rvwMonicaWpAndRespModelSet the rvwMonicaWpAndRespModelSet to set
   */
  public void setRvwMonicaWpAndRespModelSet(final Set<RvwWpAndRespModel> rvwMonicaWpAndRespModelSet) {
    this.rvwMonicaWpAndRespModelSet = rvwMonicaWpAndRespModelSet;
  }


  /**
   * @return the rvwMonicaParamAndWpRespModelMap
   */
  public Map<Long, RvwWpAndRespModel> getRvwMonicaParamAndWpRespModelMap() {
    return this.rvwMonicaParamAndWpRespModelMap;
  }


  /**
   * @param rvwMonicaParamAndWpRespModelMap the rvwMonicaParamAndWpRespModelMap to set
   */
  public void setRvwMonicaParamAndWpRespModelMap(final Map<Long, RvwWpAndRespModel> rvwMonicaParamAndWpRespModelMap) {
    this.rvwMonicaParamAndWpRespModelMap = rvwMonicaParamAndWpRespModelMap;
  }


  /**
   * @return the a2lWpDefVersId
   */
  public Long getA2lWpDefVersId() {
    return this.a2lWpDefVersId;
  }


  /**
   * @param a2lWpDefVersId the a2lWpDefVersId to set
   */
  public void setA2lWpDefVersId(final Long a2lWpDefVersId) {
    this.a2lWpDefVersId = a2lWpDefVersId;
  }

}
