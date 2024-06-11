/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.io.Serializable;

/**
 * @author dmr1cob
 */
public class A2lImportProfileFileType implements Serializable {

  private static final long serialVersionUID = -8355912317979787165L;
  /**
   * Profile file type - excel or csv
   */
  private String type;
  /**
   * Seperator in case of csv
   */
  private String seperator;
  /**
   * format in case of excel file - xlsx,xls,xlsm
   */
  private String format;

  /**
   * @return the fileType
   */
  public String getType() {
    return this.type;
  }

  /**
   * @param type the fileType to set
   */
  public void setType(final String type) {
    this.type = type;
  }

  /**
   * @return the seperator
   */
  public String getSeperator() {
    return this.seperator;
  }

  /**
   * @param seperator the seperator to set
   */
  public void setSeperator(final String seperator) {
    this.seperator = seperator;
  }

  /**
   * @return the format
   */
  public String getFormat() {
    return this.format;
  }

  /**
   * @param format the format to set
   */
  public void setFormat(final String format) {
    this.format = format;
  }


}
