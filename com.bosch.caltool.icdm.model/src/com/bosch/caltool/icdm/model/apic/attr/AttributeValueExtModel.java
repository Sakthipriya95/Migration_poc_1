/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.attr;

import java.math.BigDecimal;

/**
 * @author mkl2cob
 */
public class AttributeValueExtModel {

  /**
   * text value
   */
  private String textValueEng;
  /**
   * text value
   */
  private String textValueGer;
  /**
   * number value
   */
  private BigDecimal numValue;
  /**
   * boolean value
   */
  private Boolean boolvalue;
  /**
   * date value
   */
  private String dateValue;

  /**
   * hyperlink Value
   */
  private String hyperLinkValue;

  /**
   * user value
   */
  private String otherValue;
  /**
   * description
   */
  private String descriptionEng;
  /**
   * description
   */
  private String descriptionGer;


  /**
   * @return the textValue
   */
  public String getTextValue() {
    return this.textValueEng;
  }

  /**
   * @param textValue the textValue to set
   */
  public void setTextValue(final String textValue) {
    this.textValueEng = textValue;
  }

  /**
   * @return the numValue
   */
  public BigDecimal getNumValue() {
    return this.numValue;
  }

  /**
   * @param numValue the numValue to set
   */
  public void setNumValue(final BigDecimal numValue) {
    this.numValue = numValue;
  }

  /**
   * @return the boolvalue
   */
  public Boolean getBoolvalue() {
    return this.boolvalue;
  }

  /**
   * @param boolvalue the boolvalue to set
   */
  public void setBoolvalue(final Boolean boolvalue) {
    this.boolvalue = boolvalue;
  }

  /**
   * @return the dateValue
   */
  public String getDateValue() {
    return this.dateValue;
  }

  /**
   * @param dateValue the dateValue to set
   */
  public void setDateValue(final String dateValue) {
    this.dateValue = dateValue;
  }


  /**
   * @return the hyperLinkValue
   */
  public String getHyperLinkValue() {
    return this.hyperLinkValue;
  }


  /**
   * @param hyperLinkValue the hyperLinkValue to set
   */
  public void setHyperLinkValue(final String hyperLinkValue) {
    this.hyperLinkValue = hyperLinkValue;
  }


  /**
   * @return the description
   */
  public String getDescriptionEng() {
    return this.descriptionEng;
  }


  /**
   * @param description the description to set
   */
  public void setDescriptionEng(final String description) {
    this.descriptionEng = description;
  }

  /**
   * @return the descriptionGer
   */
  public String getDescriptionGer() {
    return this.descriptionGer;
  }

  /**
   * @param descriptionGer the descriptionGer to set
   */
  public void setDescriptionGer(final String descriptionGer) {
    this.descriptionGer = descriptionGer;
  }

  /**
   * @return the textValueGer
   */
  public String getTextValueGer() {
    return this.textValueGer;
  }

  /**
   * @param textValueGer the textValueGer to set
   */
  public void setTextValueGer(final String textValueGer) {
    this.textValueGer = textValueGer;
  }

  /**
   * @return the otherValue
   */
  public String getOtherValue() {
    return this.otherValue;
  }

  /**
   * @param otherValue the otherValue to set
   */
  public void setOtherValue(final String otherValue) {
    this.otherValue = otherValue;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "AttributeValueExtModel [textValueEng=" + this.textValueEng + ", textValueGer=" + this.textValueGer +
        ", numValue=" + this.numValue + ", boolvalue=" + this.boolvalue + ", dateValue=" + this.dateValue +
        ", hyperLinkValue=" + this.hyperLinkValue + ", otherValue=" + this.otherValue + ", descriptionEng=" +
        this.descriptionEng + ", descriptionGer=" + this.descriptionGer + "]";
  }


}
