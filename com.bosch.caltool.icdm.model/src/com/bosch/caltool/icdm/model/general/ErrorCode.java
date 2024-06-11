/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.general;

import com.bosch.caltool.datamodel.core.IModel;

/**
 * @author ukt1cob
 */
public class ErrorCode implements IModel {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private Long id;

  private String code;

  private String message;

  private String messageEng;

  private String messageGer;

  private String cause;

  private String solution;

  private Long version;


  /**
   * @return the code
   */
  public String getCode() {
    return this.code;
  }


  /**
   * @param code the code to set
   */
  public void setCode(final String code) {
    this.code = code;
  }


  /**
   * @return the message
   */
  public String getMessage() {
    return this.message;
  }


  /**
   * @param message the message to set
   */
  public void setMessage(final String message) {
    this.message = message;
  }


  /**
   * @return the messageEng
   */
  public String getMessageEng() {
    return this.messageEng;
  }


  /**
   * @param messageEng the messageEng to set
   */
  public void setMessageEng(final String messageEng) {
    this.messageEng = messageEng;
  }


  /**
   * @return the messageGer
   */
  public String getMessageGer() {
    return this.messageGer;
  }


  /**
   * @param messageGer the messageGer to set
   */
  public void setMessageGer(final String messageGer) {
    this.messageGer = messageGer;
  }


  /**
   * @return the cause
   */
  public String getCause() {
    return this.cause;
  }


  /**
   * @param cause the cause to set
   */
  public void setCause(final String cause) {
    this.cause = cause;
  }


  /**
   * @return the solution
   */
  public String getSolution() {
    return this.solution;
  }


  /**
   * @param solution the solution to set
   */
  public void setSolution(final String solution) {
    this.solution = solution;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return this.id;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long id) {
    this.id = id;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return this.version;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    this.version = version;
  }


}
