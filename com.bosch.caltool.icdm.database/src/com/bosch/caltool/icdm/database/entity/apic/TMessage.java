package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;


/**
 * The persistent class for the T_MESSAGES database table.
 */
@Entity
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
@Table(name = "T_MESSAGES")
@NamedQueries(value = {
    @NamedQuery(name = TMessage.NQ_GET_ALL, query = "select msg from TMessage msg"),
    @NamedQuery(name = TMessage.NQ_GET_ALL_ERRORCODE, query = "select msg from TMessage msg where msg.errorYN = 'Y'"),
    @NamedQuery(name = TMessage.NQ_GET_ERRORCODE_BY_ID, query = "select msg from TMessage msg where msg.groupName = :groupName and msg.name = :name and msg.errorYN = 'Y'") })

public class TMessage implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Named query to get all TMessages from database
   *
   * @return list of TMessage
   */
  public static final String NQ_GET_ALL = "TMessage.getAll";

  /**
   * Named query to get all error messge from TMessages table in database
   *
   * @return list of TMessage
   */
  public static final String NQ_GET_ALL_ERRORCODE = "TMessage.getAllErrorCode";

  /**
   * Named query to get all error messge from TMessages table in database
   *
   * @return list of TMessage
   */
  public static final String NQ_GET_ERRORCODE_BY_ID = "TMessage.getErrorCodeById";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "MESSAGE_ID")
  private long messageId;

  @Column(name = "GROUP_NAME")
  private String groupName;

  private String name;

  @Column(name = "MESSAGE_TEXT")
  private String messageText;

  @Column(name = "MESSAGE_TEXT_GER")
  private String messageTextGer;

  @Column(name = "ERROR_YN")
  private String errorYN;

  private String cause;

  private String solution;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private long version;

  /**
   *
   */
  public TMessage() {}

  /**
   * @return messageId
   */
  public long getMessageId() {
    return this.messageId;
  }

  /**
   * @param messageId messageId
   */
  public void setMessageId(final long messageId) {
    this.messageId = messageId;
  }

  public String getGroupName() {
    return this.groupName;
  }

  /**
   * @param groupName groupName
   */
  public void setGroupName(final String groupName) {
    this.groupName = groupName;
  }

  public String getName() {
    return this.name;
  }

  /**
   * @param name name
   */
  public void setName(final String name) {
    this.name = name;
  }

  public String getMessageText() {
    return this.messageText;
  }

  /**
   * @param messageText messageText
   */
  public void setMessageText(final String messageText) {
    this.messageText = messageText;
  }


  public String getMessageTextGer() {
    return this.messageTextGer;
  }

  /**
   * @param messageTextGer messageTextGer
   */
  public void setMessageTextGer(final String messageTextGer) {
    this.messageTextGer = messageTextGer;
  }

  public long getVersion() {
    return this.version;
  }

  /**
   * @param version version
   */
  public void setVersion(final long version) {
    this.version = version;
  }

  /**
   * @return the errorYN
   */
  public String getErrorYN() {
    return this.errorYN;
  }


  /**
   * @param errorYN the errorYN to set
   */
  public void setErrorYN(final String errorYN) {
    this.errorYN = errorYN;
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

}