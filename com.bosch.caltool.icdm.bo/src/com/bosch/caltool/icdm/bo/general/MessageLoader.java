/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.general;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringEscapeUtils;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.bo.general.MessageCodeUtils;
import com.bosch.caltool.icdm.common.exception.DataNotFoundException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TMessage;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.general.ErrorCode;
import com.bosch.caltool.icdm.model.general.Message;


/**
 * @author bne4cob
 */
public class MessageLoader extends AbstractBusinessObject<Message, TMessage> {

  /**
   * @param serviceData service Data
   */
  public MessageLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.MESSAGE, TMessage.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Message createDataObject(final TMessage entity) {
    Message message = new Message();

    message.setId(entity.getMessageId());
    message.setVersion(entity.getVersion());

    message.setGroupName(entity.getGroupName());
    message.setName(entity.getName());
    message.setMessageText(entity.getMessageText());
    message.setMessageTextGer(entity.getMessageTextGer());

    return message;
  }

  /**
   * @return Map of messages. <br>
   *         Key - Message ID, combination of group name and obj name.<br>
   *         Value - item[0] = English message; item[1] = German message
   */
  Map<String, String[]> getAllMessages() {

    final Map<String, String[]> messageMap = new HashMap<>();

    final TypedQuery<TMessage> query = getEntMgr().createNamedQuery(TMessage.NQ_GET_ALL, TMessage.class);
    final List<TMessage> retList = query.getResultList();

    String msgId;
    String[] message;
    for (TMessage entity : retList) {
      msgId = MessageCodeUtils.createMessageCode(entity.getGroupName(), entity.getName());
      message = new String[] { entity.getMessageText(), entity.getMessageTextGer() };
      messageMap.put(msgId, message);
    }

    return messageMap;
  }

  /**
   * @param grpName grp Name
   * @param name name
   * @param varArgs var Args
   * @return the message String for the Group name and name along with Var args
   */
  public String getMessage(final String grpName, final String name, final Object... varArgs) {
    String[] messageFull = GeneralCache.INSTANCE.getMessage(MessageCodeUtils.createMessageCode(grpName, name));

    String message = null;

    // if message configuration is not available in DB, return null
    if (messageFull != null) {
      message = getLangSpecTxt(messageFull[0], messageFull[1]);
      message = StringEscapeUtils.unescapeJava(message);
      message = MessageFormat.format(message, varArgs);
    }

    return message;
  }

  /**
   * @return Map ; Key - Group name and name, Value - message
   */
  public Map<String, String> getAllMessagesMap() {
    Map<String, String[]> messageMap = GeneralCache.INSTANCE.getAllMessages();
    Map<String, String> allMessages = new HashMap<>();
    for (Map.Entry<String, String[]> entry : messageMap.entrySet()) {
      String[] messageFull = entry.getValue();
      String message = getLangSpecTxt(messageFull[0], messageFull[1]);
      message = StringEscapeUtils.unescapeJava(message);
      allMessages.put(entry.getKey(), message);
    }
    return allMessages;
  }

  /**
   * @return Map of Key - ErrorCodeID(Group name + '.' + Name) Value - List of ErrorCodes
   */
  public Map<String, ErrorCode> getAllErrorCodes() {

    final Map<String, ErrorCode> errorCodeMap = new HashMap<>();
    ErrorCode errorCode = null;

    final TypedQuery<TMessage> query = getEntMgr().createNamedQuery(TMessage.NQ_GET_ALL_ERRORCODE, TMessage.class);

    for (TMessage entity : query.getResultList()) {
      errorCode = createErrorCodeObj(entity);
      errorCodeMap.put(errorCode.getCode(), errorCode);
    }

    getLogger().info("Retrieval of Error codes completed. Count = {}", errorCodeMap.size());

    return errorCodeMap;
  }


  /**
   * @param code - Error Code (Group name + '.' + Name)
   * @return ErrorCode
   * @throws DataNotFoundException -exception
   */
  public ErrorCode getErrorCodeById(final String code) throws DataNotFoundException {
    ErrorCode errorCode = null;
    // checking whether the code is not empty and in valid format "GroupName.Name"
    if (CommonUtils.isNotEmptyString(code) && code.contains(MessageCodeUtils.MESSAGECODE_SEPARATOR)) {
      String[] errorCodeSplit = code.split("\\.");

      final TypedQuery<TMessage> query = getEntMgr().createNamedQuery(TMessage.NQ_GET_ERRORCODE_BY_ID, TMessage.class);
      query.setParameter("groupName", errorCodeSplit[0]);
      query.setParameter("name", errorCodeSplit[1]);
      List<TMessage> resultList = query.getResultList();

      // checking whether the code is a valid error code
      if (CommonUtils.isNotEmpty(resultList)) {
        final TMessage entity = resultList.get(0);
        errorCode = createErrorCodeObj(entity);
        getLogger().info("Error code for code {} retreived successfully", code);
      }
    }

    if (errorCode == null) {
      throw new DataNotFoundException("Could not find the details for the given error code " + code);
    }

    return errorCode;
  }

  private ErrorCode createErrorCodeObj(final TMessage entity) {
    ErrorCode errorCode = new ErrorCode();
    errorCode.setId(entity.getMessageId());
    errorCode.setCode(MessageCodeUtils.createMessageCode(entity.getGroupName(), entity.getName()));
    errorCode.setId(entity.getMessageId());
    errorCode.setVersion(entity.getVersion());
    // setting errorcode text based on language specification
    errorCode.setMessage(getLangSpecTxt(entity.getMessageText(), entity.getMessageTextGer()));
    errorCode.setMessageEng(entity.getMessageText());
    errorCode.setMessageGer(entity.getMessageTextGer());
    errorCode.setCause(entity.getCause());
    errorCode.setSolution(entity.getSolution());
    return errorCode;
  }

}
