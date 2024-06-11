/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.general;

import com.bosch.caltool.datamodel.core.IModel;

/**
 * @author bne4cob
 */
public class Message implements IModel {

  /**
   *
   */
  private static final long serialVersionUID = 7351160451148082685L;

  private Long messageId;

  private String groupName;

  private String name;

  private String messageText;

  private String messageTextGer;

  private Long version;

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return this.messageId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long objId) {
    this.messageId = objId;

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


  /**
   * @return the groupName
   */
  public String getGroupName() {
    return this.groupName;
  }


  /**
   * @param groupName the groupName to set
   */
  public void setGroupName(final String groupName) {
    this.groupName = groupName;
  }


  /**
   * @return the name
   */
  public String getName() {
    return this.name;
  }


  /**
   * @param name the name to set
   */
  public void setName(final String name) {
    this.name = name;
  }


  /**
   * @return the messageText
   */
  public String getMessageText() {
    return this.messageText;
  }


  /**
   * @param messageText the messageText to set
   */
  public void setMessageText(final String messageText) {
    this.messageText = messageText;
  }


  /**
   * @return the messageTextGer
   */
  public String getMessageTextGer() {
    return this.messageTextGer;
  }


  /**
   * @param messageTextGer the messageTextGer to set
   */
  public void setMessageTextGer(final String messageTextGer) {
    this.messageTextGer = messageTextGer;
  }

}
