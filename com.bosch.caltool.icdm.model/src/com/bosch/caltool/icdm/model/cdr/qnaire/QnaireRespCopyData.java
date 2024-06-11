/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.qnaire;

/**
 * Data needed for copy/paste qnaire resp service call
 *
 * @author UKT1COB
 */
public class QnaireRespCopyData extends QnaireRespActionData {

  /**
   * Qnaire resp obj that needs to be copied to the destination
   */
  private RvwQnaireResponse copiedQnaireResp;
 
  /**
   * true,if it is to copy only working set
   */
  private boolean copyOnlyWorkingSet;
  /**
   * true,if we done undo delete
   */
  private boolean undoDelete;
  /**
   * destination general questionnaire
   */
  private RvwQnaireResponse destGeneralQuesResp;


  /**
   * @return the copiedQnaireResp
   */
  public RvwQnaireResponse getCopiedQnaireResp() {
    return this.copiedQnaireResp;
  }

  /**
   * @param copiedQnaireResp the copiedQnaireResp to set
   */
  public void setCopiedQnaireResp(final RvwQnaireResponse copiedQnaireResp) {
    this.copiedQnaireResp = copiedQnaireResp;
  }

  /**
   * @return the copyOnlyWorkingSet
   */
  public boolean isCopyOnlyWorkingSet() {
    return this.copyOnlyWorkingSet;
  }

  /**
   * @param copyOnlyWorkingSet the copyOnlyWorkingSet to set
   */
  public void setCopyOnlyWorkingSet(final boolean copyOnlyWorkingSet) {
    this.copyOnlyWorkingSet = copyOnlyWorkingSet;
  }

  /**
   * @return the undoDelete
   */
  public boolean isUndoDelete() {
    return this.undoDelete;
  }

  /**
   * @param undoDelete the undoDelete to set
   */
  public void setUndoDelete(final boolean undoDelete) {
    this.undoDelete = undoDelete;
  }

  /**
   * @return the destGeneralQuesResp
   */
  public RvwQnaireResponse getDestGeneralQuesResp() {
    return this.destGeneralQuesResp;
  }

  /**
   * @param destGeneralQuesResp the destGeneralQuesResp to set
   */
  public void setDestGeneralQuesResp(final RvwQnaireResponse destGeneralQuesResp) {
    this.destGeneralQuesResp = destGeneralQuesResp;
  }

}