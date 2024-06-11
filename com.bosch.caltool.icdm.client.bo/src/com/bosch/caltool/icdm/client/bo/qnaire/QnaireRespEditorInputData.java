/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.qnaire;

import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;

/**
 * @author say8cob
 */
/**
 * @author say8cob
 */
public class QnaireRespEditorInputData {

  private RvwQnaireRespVersion rvwQnaireRespVersion;

  private PidcVersion pidcVersion;

  private PidcVariant pidcVariant;
  
  private PidcVariant secondaryPidcVariant;

  private RvwQnaireResponse rvwQnaireResponse;
  
  


  
  /**
   * @return the secondaryPidcVariant
   */
  public PidcVariant getSecondaryPidcVariant() {
    return secondaryPidcVariant;
  }


  
  /**
   * @param secondaryPidcVariant the secondaryPidcVariant to set
   */
  public void setSecondaryPidcVariant(PidcVariant secondaryPidcVariant) {
    this.secondaryPidcVariant = secondaryPidcVariant;
  }


  /**
   * @return the rvwQnaireRespVersion
   */
  public RvwQnaireRespVersion getRvwQnaireRespVersion() {
    return this.rvwQnaireRespVersion;
  }


  /**
   * @param rvwQnaireRespVersion the rvwQnaireRespVersion to set
   */
  public void setRvwQnaireRespVersion(final RvwQnaireRespVersion rvwQnaireRespVersion) {
    this.rvwQnaireRespVersion = rvwQnaireRespVersion;
  }


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
   * @return the rvwQnaireResponse
   */
  public RvwQnaireResponse getRvwQnaireResponse() {
    return this.rvwQnaireResponse;
  }


  /**
   * @param rvwQnaireResponse the rvwQnaireResponse to set
   */
  public void setRvwQnaireResponse(final RvwQnaireResponse rvwQnaireResponse) {
    this.rvwQnaireResponse = rvwQnaireResponse;
  }

  /**
   * @param inputData Qnaire response editor input data model
   * @return primary /secondary variant id
   */
  public Long getLinkedVariantId(QnaireRespEditorInputData inputData) {
    Long variantId=null;
    if (inputData.getSecondaryPidcVariant() != null){
      variantId=inputData.getSecondaryPidcVariant().getId();
    }
    else if(inputData.getPidcVariant()!=null) {
      variantId=inputData.getPidcVariant().getId();
    }
    return variantId;
  }


}
