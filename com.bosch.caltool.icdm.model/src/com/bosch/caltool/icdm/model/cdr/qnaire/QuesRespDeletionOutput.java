/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.qnaire;


/**
 * @author mkl2cob
 */
public class QuesRespDeletionOutput {

  private RvwQnaireResponse delUndeleteQnaireResp;

  private RvwQnaireResponse undeletedGenQues;

  /**
   * @return the delUndeleteQnaireResp
   */
  public RvwQnaireResponse getDelUndeleteQnaireResp() {
    return delUndeleteQnaireResp;
  }

  /**
   * @param delUndeleteQnaireResp the delUndeleteQnaireResp to set
   */
  public void setDelUndeleteQnaireResp(RvwQnaireResponse delUndeleteQnaireResp) {
    this.delUndeleteQnaireResp = delUndeleteQnaireResp;
  }

  /**
   * @return the undeletedGenQues
   */
  public RvwQnaireResponse getUndeletedGenQues() {
    return undeletedGenQues;
  }

  /**
   * @param undeletedGenQues the undeletedGenQues to set
   */
  public void setUndeletedGenQues(RvwQnaireResponse undeletedGenQues) {
    this.undeletedGenQues = undeletedGenQues;
  }
}
