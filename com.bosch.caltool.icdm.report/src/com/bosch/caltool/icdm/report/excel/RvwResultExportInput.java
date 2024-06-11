/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.report.excel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;

/**
 * @author pdh2cob
 */
public class RvwResultExportInput {

  private boolean exportOnlyVisibleCols;

  private boolean separateExportForWp;

  private boolean onlyFiltered;

  private boolean fromEditor;

  private List<A2lWorkPackage> selectedWpList = new ArrayList<>();

  private A2lWorkPackage workPackage;

  private boolean openAutomatically;

  private ReviewResultClientBO resultClientBO;

  private CDRReviewResult reviewResult;

  private Set<CDRReviewResult> selectedReviewResultSet = new HashSet<>();

  private String filePath;

  private String fileExt;

  private boolean onlyRvwResult;
  
  private boolean onlyRvwResAndQnaireWrkSet;
  
  private boolean onlyRvwResAndQnaireLstBaseline;

  /**
   * @return the selectedReviewResultSet
   */
  public Set<CDRReviewResult> getSelectedReviewResultSet() {
    return this.selectedReviewResultSet;
  }


  /**
   * @param selectedReviewResultSet the selectedReviewResultSet to set
   */
  public void setSelectedReviewResultSet(final Set<CDRReviewResult> selectedReviewResultSet) {
    this.selectedReviewResultSet = selectedReviewResultSet;
  }


  /**
   * @return the fromEditor
   */
  public boolean isFromEditor() {
    return this.fromEditor;
  }


  /**
   * @param fromEditor the fromEditor to set
   */
  public void setFromEditor(final boolean fromEditor) {
    this.fromEditor = fromEditor;
  }


  /**
   * @return the separateExportForWp
   */
  public boolean isSeparateExportForWp() {
    return this.separateExportForWp;
  }


  /**
   * @param separateExportForWp the separateExportForWp to set
   */
  public void setSeparateExportForWp(final boolean separateExportForWp) {
    this.separateExportForWp = separateExportForWp;
  }


  /**
   * @param selectedWpList the selectedWpList to set
   */
  public void setSelectedWpList(final List<A2lWorkPackage> selectedWpList) {
    this.selectedWpList = selectedWpList;
  }


  /**
   * @return the reviewResult
   */
  public CDRReviewResult getReviewResult() {
    return this.reviewResult;
  }


  /**
   * @param reviewResult the reviewResult to set
   */
  public void setReviewResult(final CDRReviewResult reviewResult) {
    this.reviewResult = reviewResult;
  }


  /**
   * @return the filePath
   */
  public String getFilePath() {
    return this.filePath;
  }


  /**
   * @param filePath the filePath to set
   */
  public void setFilePath(final String filePath) {
    this.filePath = filePath;
  }


  /**
   * @return the fileExt
   */
  public String getFileExt() {
    return this.fileExt;
  }


  /**
   * @param fileExt the fileExt to set
   */
  public void setFileExt(final String fileExt) {
    this.fileExt = fileExt;
  }


  /**
   * @return the workPackage
   */
  public A2lWorkPackage getWorkPackage() {
    return this.workPackage;
  }


  /**
   * @param workPackage the workPackage to set
   */
  public void setWorkPackage(final A2lWorkPackage workPackage) {
    this.workPackage = workPackage;
  }


  /**
   * @return the openAutomatically
   */
  public boolean isOpenAutomatically() {
    return this.openAutomatically;
  }


  /**
   * @param openAutomatically the openAutomatically to set
   */
  public void setOpenAutomatically(final boolean openAutomatically) {
    this.openAutomatically = openAutomatically;
  }


  /**
   * @return the exportOnlyVisibleCols
   */
  public boolean isExportOnlyVisibleCols() {
    return this.exportOnlyVisibleCols;
  }


  /**
   * @param exportOnlyVisibleCols the exportOnlyVisibleCols to set
   */
  public void setExportOnlyVisibleCols(final boolean exportOnlyVisibleCols) {
    this.exportOnlyVisibleCols = exportOnlyVisibleCols;
  }


  /**
   * @return the onlyFiltered
   */
  public boolean isOnlyFiltered() {
    return this.onlyFiltered;
  }


  /**
   * @param onlyFiltered the onlyFiltered to set
   */
  public void setOnlyFiltered(final boolean onlyFiltered) {
    this.onlyFiltered = onlyFiltered;
  }


  /**
   * @return the selectedWpList
   */
  public java.util.List<A2lWorkPackage> getSelectedWpList() {
    return this.selectedWpList;
  }


  /**
   * @return the resultClientBO
   */
  public ReviewResultClientBO getResultClientBO() {
    return this.resultClientBO;
  }


  /**
   * @param resultClientBO the resultClientBO to set
   */
  public void setResultClientBO(final ReviewResultClientBO resultClientBO) {
    this.resultClientBO = resultClientBO;
  }


  
  /**
   * @return the onlyRvwResult
   */
  public boolean isOnlyRvwResult() {
    return onlyRvwResult;
  }


  
  /**
   * @param onlyRvwResult the onlyRvwResult to set
   */
  public void setOnlyRvwResult(boolean onlyRvwResult) {
    this.onlyRvwResult = onlyRvwResult;
  }


  
  /**
   * @return the onlyRvwResAndQnaireWrkSet
   */
  public boolean isOnlyRvwResAndQnaireWrkSet() {
    return onlyRvwResAndQnaireWrkSet;
  }


  
  /**
   * @param onlyRvwResAndQnaireWrkSet the onlyRvwResAndQnaireWrkSet to set
   */
  public void setOnlyRvwResAndQnaireWrkSet(boolean onlyRvwResAndQnaireWrkSet) {
    this.onlyRvwResAndQnaireWrkSet = onlyRvwResAndQnaireWrkSet;
  }


  
  /**
   * @return the onlyRvwResAndQnaireLstBaseline
   */
  public boolean isOnlyRvwResAndQnaireLstBaseline() {
    return onlyRvwResAndQnaireLstBaseline;
  }


  
  /**
   * @param onlyRvwResAndQnaireLstBaseline the onlyRvwResAndQnaireLstBaseline to set
   */
  public void setOnlyRvwResAndQnaireLstBaseline(boolean onlyRvwResAndQnaireLstBaseline) {
    this.onlyRvwResAndQnaireLstBaseline = onlyRvwResAndQnaireLstBaseline;
  }


}
