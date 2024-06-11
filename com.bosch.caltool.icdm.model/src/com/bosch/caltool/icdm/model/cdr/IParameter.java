/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.datamodel.core.IDataObject;


/**
 * @author rgo7cob
 */
public interface IParameter extends IDataObject {


  /**
   * @return German name
   */
  String getLongNameGer();

  /**
   * @return English name
   */
  String getLongNameEng();


  /**
   * @return paramater type
   */
  String getType();

  /**
   * @return param hint
   */
  String getParamHint();

  /**
   * @return the Code Word String
   */
  String getCodeWord();


  String getIsBitWise();

  /**
   * @return the isBlackList
   */
  boolean isBlackList();

  /**
   * ICDM-1113
   *
   * @return the longName
   */
  String getLongName();

  /**
   * @return the text representation of class
   */
  String getpClassText();


  /**
   * @return the ssd class
   */
  String getSsdClass();

  /**
   * @param codeWord
   */
  void setCodeWord(final String codeWord);


  /**
   * @param ssdClass
   */
  void setSsdClass(final String ssdClass);


  /**
   * @param longName
   */
  void setLongName(final String longName);

  /**
   * @param type
   */
  void setType(final String type);

  /**
   * @param paramHint
   */
  void setParamHint(final String paramHint);

  /**
   * @param longNameGer
   */
  void setLongNameGer(final String longNameGer);

  void setpClassText(final String pClassText);


  /**
   * @param isBlackList the isBlackList to set
   */
  void setBlackList(final boolean isBlackList);

  /**
   * @return qssdFlag
   */
  boolean isQssdFlag();

  /**
   * @param qssdFlag
   */
  void setQssdFlag(boolean qssdFlag);


}
