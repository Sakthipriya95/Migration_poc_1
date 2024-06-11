/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * @author bne4cob
 */
@XmlRootElement
public class ParamProperties {

  /**
   * Parameter ID, from T_PARAMETER
   */
  long id;

  /**
   * Code Word Y/N
   */
  private String cwYN;

  /**
   * If true, the param is a code word
   */
  private boolean codeWord;

  /**
   * Parameter class
   */
  private String pClass;

  /**
   * ssd class
   */
  private String ssdClass;
  /**
   * black list label
   */
  private boolean isBlackList;

  /**
   * qssd parameter
   */
  private boolean isQssdParameter;

  /**
   * @return the isBlackList
   */
  public boolean isBlackList() {
    return this.isBlackList;
  }


  /**
   * @param isBlackList the isBlackList to set
   */
  public void setBlackList(final boolean isBlackList) {
    this.isBlackList = isBlackList;
  }

  /**
   * @return the Code Word YN
   */
  public String getCwYN() {
    return this.cwYN;
  }

  /**
   * @param codeWordYN the Code Word YN to set
   */
  public void setCwYN(final String codeWordYN) {
    this.cwYN = codeWordYN;
  }

  /**
   * @return the codeWord
   */
  public boolean isCodeWord() {
    return this.codeWord;
  }


  /**
   * @param codeWord the codeWord to set
   */
  public void setCodeWord(final boolean codeWord) {
    this.codeWord = codeWord;
  }


  /**
   * @return the parameter Class
   */
  public String getPClass() {
    return this.pClass;
  }

  /**
   * @param paramClass the parameter Class to set
   */
  public void setPClass(final String paramClass) {
    this.pClass = paramClass;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return super.toString() + " [cwYN=" + this.cwYN + ", pClass=" + this.pClass + "]";
  }

  /**
   * @param ssdClass ssdClass
   */
  public void setSsdClass(final String ssdClass) {
    this.ssdClass = ssdClass;

  }


  /**
   * @return the ssdClass
   */
  public String getSsdClass() {
    return this.ssdClass;
  }


  /**
   * @return the id
   */
  public long getId() {
    return this.id;
  }


  /**
   * @param id the id to set
   */
  public void setId(final long id) {
    this.id = id;
  }


  /**
   * @return the isQssdParameter
   */
  public boolean isQssdParameter() {
    return this.isQssdParameter;
  }


  /**
   * @param isQssdParameter the isQssdParameter to set
   */
  public void setQssdParameter(final boolean isQssdParameter) {
    this.isQssdParameter = isQssdParameter;
  }


}
