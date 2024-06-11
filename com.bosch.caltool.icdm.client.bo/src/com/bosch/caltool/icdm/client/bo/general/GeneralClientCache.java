/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.general;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.client.bo.framework.CnsUtils;
import com.bosch.caltool.icdm.client.bo.framework.HandlerRegistry;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.ICnsApplicabilityCheckerChangeData;
import com.bosch.caltool.icdm.client.bo.framework.ICnsRefresherDce;
import com.bosch.caltool.icdm.common.exception.IcdmRuntimeException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.UserPreference;
import com.bosch.caltool.icdm.model.cdr.RvwCommentTemplate;
import com.bosch.caltool.icdm.model.cdr.RvwUserCmntHistory;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.general.Link;
import com.bosch.caltool.icdm.model.user.ApicAccessRight;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwCommentTemplateServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwUserCmntHistoryServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.ApicAccessRightServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.general.CommonParamServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.general.LinkServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.general.MessageServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.general.NodeAccessServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.general.UserPreferenceServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.general.UserServiceClient;
import com.bosch.caltool.security.Decryptor;
import com.bosch.caltool.security.Encryptor;

/**
 * @author gge6cob
 */
class GeneralClientCache implements IClientDataHandler {

  // Singleton class instance
  private static GeneralClientCache singletonObj;
  /**
   * Current user details
   */
  private User currentUser;

  /**
   * Encrypted password
   */
  private String encPassword;

  /**
   * Node Access Rights<br>
   * Key - nodeID - Long <br>
   * Value - NodeAccess <br>
   */
  private final ConcurrentMap<Long, NodeAccess> nodeAccsCurUsrMap = new ConcurrentHashMap<>();


  /**
   * APIC rights of user
   */
  private ApicAccessRight apicAccsRightCurUsr;

  /**
   * Messages.<br>
   * Key - Message ID, combination of group name and obj name.<br>
   * Value - message
   */
  private final ConcurrentMap<String, String> messageMap = new ConcurrentHashMap<>();

  /**
   * common parameters and values<br>
   * Key - Parameter Key<br>
   * Value - Parameter value
   */
  private final ConcurrentMap<CommonParamKey, String> commonParamsMap = new ConcurrentHashMap<>();
  /**
   * This is the encrypted form of password entered by the user during the vcdm transfer when SSO is enabled.
   */
  private String encryptedvCDMTransferPwd;

  /**
   * Help links .<br>
   * Key - node type, reference string to identify help link belongs to which component<br>
   * Value - wiki help link
   */
  private final ConcurrentMap<String, Link> helpLinkMap = new ConcurrentHashMap<>();

  /**
   * This is a set of review comments template map
   */
  private final Map<Long, RvwCommentTemplate> rvwCommentTemplateMap = new HashMap<>();

  /**
   * Key - RvwCmntHistory Id, Value - RvwCmntHistory object
   */
  private final Map<Long, RvwUserCmntHistory> rvwCmntHistoryMap = new HashMap<>();

  /**
   * user preferences and values, Key - Parameter Key, Value - Parameter value
   */
  private final ConcurrentMap<String, UserPreference> userPreferenceMap = new ConcurrentHashMap<>();

  /**
   * Constructor
   *
   * @throws ApicWebServiceException
   */
  private GeneralClientCache() throws ApicWebServiceException {
    refreshCurrentUserCache();
    refreshCommonDataCache();
    HandlerRegistry.INSTANCE.registerSingletonDataHandler(this);
  }

  public static GeneralClientCache getInstance() throws ApicWebServiceException {
    if (CommonUtils.isNull(singletonObj)) {
      singletonObj = new GeneralClientCache();
    }
    return singletonObj;
  }

  public void loadRvwCommentTemplate() throws ApicWebServiceException {
    this.rvwCommentTemplateMap.putAll(new RvwCommentTemplateServiceClient().getAll());
  }

  public void loadRvwCommentHistory() throws ApicWebServiceException {
    this.rvwCmntHistoryMap
        .putAll(new RvwUserCmntHistoryServiceClient().getRvwCmntHistoryByUser(this.currentUser.getId()));
  }

  /**
   * Load/Reload User related objects by calling WS
   *
   * @throws ApicWebServiceException error during service call
   */
  public void refreshCurrentUserCache() throws ApicWebServiceException {
    loadCurrentUser();
    loadApicAccessRights();
    loadNodeAccessRights();
    loadAllUserPreferences();
  }


  /**
   * Load/Reload common data by calling WS : messages, common params, sys params
   *
   * @throws ApicWebServiceException
   */
  private void refreshCommonDataCache() throws ApicWebServiceException {
    loadAllMessages();
    loadAllCommonParams();
    loadHelpLinks();
  }

  /**
   * load help links
   *
   * @throws ApicWebServiceException
   */
  private void loadHelpLinks() throws ApicWebServiceException {
    this.helpLinkMap.putAll(new LinkServiceClient().getHelpLinks());
  }

  /**
   * @param servData
   * @throws ApicWebServiceException
   */
  private void loadCurrentUser() throws ApicWebServiceException {
    this.currentUser = new UserServiceClient().getCurrentApicUser();

  }

  /**
   * @param servData
   * @throws ApicWebServiceException
   */
  private void loadApicAccessRights() throws ApicWebServiceException {
    this.apicAccsRightCurUsr = new ApicAccessRightServiceClient().getCurrentUserApicAccessRight();
  }

  /**
   * @param servData
   * @throws ApicWebServiceException
   */
  private void loadNodeAccessRights() throws ApicWebServiceException {
    this.nodeAccsCurUsrMap.clear();
    this.nodeAccsCurUsrMap.putAll(new NodeAccessServiceClient().getAllNodeAccessforCurrentUser());

  }

  /**
   * Load all messages
   *
   * @throws ApicWebServiceException
   */
  private void loadAllMessages() throws ApicWebServiceException {
    this.messageMap.clear();
    this.messageMap.putAll(new MessageServiceClient().getAll());

  }

  /**
   * Load all common parameters
   *
   * @throws ApicWebServiceException
   */
  private void loadAllCommonParams() throws ApicWebServiceException {
    Map<String, String> serviceParamsMap = new CommonParamServiceClient().getAll();

    for (CommonParamKey cpk : CommonParamKey.values()) {
      String val = serviceParamsMap.get(cpk.getParamName());
      if (val == null) {
        throw new IcdmRuntimeException("Configuration parameter not found '" + cpk.getParamName() + "'");
      }

      this.commonParamsMap.put(cpk, val);
      serviceParamsMap.remove(cpk.getParamName());
    }

    if (!serviceParamsMap.isEmpty()) {
      getLoggerCDM().info("Extra common parameters found in service response. List : {}", serviceParamsMap.keySet());
    }
  }

  /**
   * Load all user preferences
   * 
   * @throws ApicWebServiceException
   */
  private void loadAllUserPreferences() throws ApicWebServiceException {
    Map<Long, UserPreference> servicePreferenceMap =
        new UserPreferenceServiceClient().getByUserId(this.currentUser.getId());

    for (Entry<Long, UserPreference> set : servicePreferenceMap.entrySet()) {
      this.userPreferenceMap.put(set.getValue().getUserPrefKey(), set.getValue());
    }
  }

  /**
   * @return the currentUser
   */
  public User getCurrentUser() {
    return this.currentUser;
  }

  /**
   * @return the apicAccessRightCurrentUser
   */
  public ApicAccessRight getApicAccessRightCurrentUser() {
    return this.apicAccsRightCurUsr;
  }

  /**
   * @return the nodeAccessCurrentUser
   */
  public Map<Long, NodeAccess> getNodeAccessCurrentUser() {
    return this.nodeAccsCurUsrMap;
  }

  /**
   * @return the messageMap
   */
  public Map<String, String> getMessageMap() {
    return this.messageMap;
  }

  /**
   * Returns the value of the parameter identified by name
   *
   * @param key parameter key
   * @return the parameter value
   */
  public String getParameterValue(final CommonParamKey key) {
    return this.commonParamsMap.get(key);
  }

  /**
   * Returns the value of the preference
   *
   * @param key - preference key
   * @return the preference value
   */
  public UserPreference getUserPreferenceValue(final String key) {
    return this.userPreferenceMap.get(key);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Map<IModelType, ICnsApplicabilityCheckerChangeData> getCnsApplicabilityCheckersChangeData() {

    Map<IModelType, ICnsApplicabilityCheckerChangeData> retMap = new HashMap<>();

    retMap.put(MODEL_TYPE.APIC_USER, chData -> chData.getObjId() == this.currentUser.getId().longValue());

    retMap.put(MODEL_TYPE.NODE_ACCESS,
        chData -> CommonUtils.isEqual(((NodeAccess) CnsUtils.getModel(chData)).getUserId(), this.currentUser.getId()));
    retMap.put(MODEL_TYPE.ACTIVE_DIRECTORY_GROUP_NODE_ACCES, chData -> true);
    // True because, user id is not known for group. and there is no way to compare the user id of current user with
    // group modified.

    return retMap;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public ICnsRefresherDce getCnsRefresherDce() {
    return dce -> {
      ChangeData<?> changeData = dce.getConsChangeData().get(MODEL_TYPE.APIC_USER) == null ? null
          : dce.getConsChangeData().get(MODEL_TYPE.APIC_USER).get(this.currentUser.getId());
      if ((changeData != null) && CnsUtils.hasChanges(changeData, this.currentUser)) {
        CommonUtils.shallowCopy(this.currentUser, changeData.getNewData());
      }

      Map<Long, ChangeData<?>> nodeAccChgMap = dce.getConsChangeData().get(MODEL_TYPE.NODE_ACCESS);
      if (nodeAccChgMap != null) {
        for (ChangeData<?> chData : nodeAccChgMap.values()) {
          NodeAccess acc = (NodeAccess) (chData.getNewData() == null ? chData.getOldData() : chData.getNewData());
          Long nodeId = acc.getNodeId();
          if (CommonUtils.isEqual(acc.getUserId(), this.currentUser.getId()) &&
              CnsUtils.hasChanges(chData, this.nodeAccsCurUsrMap.get(nodeId))) {
            mergeNodeAccess(chData, nodeId);
          }
        }
      }
    };

  }


  private void mergeNodeAccess(final ChangeData<?> changeData, final Long nodeId) {
    if ((changeData.getChangeType() == CHANGE_OPERATION.CREATE) ||
        (changeData.getChangeType() == CHANGE_OPERATION.UPDATE)) {

      NodeAccess newData = (NodeAccess) changeData.getNewData();
      NodeAccess local = this.nodeAccsCurUsrMap.get(nodeId);
      if (local == null) {
        this.nodeAccsCurUsrMap.put(nodeId, newData);
      }
      else {
        CommonUtils.shallowCopy(local, newData);
      }
    }
    else {
      this.nodeAccsCurUsrMap.remove(nodeId);
    }
  }

  /**
   * @return the encryptedvCDMTransferPwd
   */
  public String getEncryptedvCDMTransferPwd() {
    return this.encryptedvCDMTransferPwd;
  }


  /**
   * @param encryptedvCDMTransferPwd the encryptedvCDMTransferPwd to set
   */
  public void setEncryptedvCDMTransferPwd(final String encryptedvCDMTransferPwd) {
    this.encryptedvCDMTransferPwd = encryptedvCDMTransferPwd;
  }

  /**
   * @param helpLinkKey String
   * @return wiki link as help
   */
  public Link getHelpLink(final String helpLinkKey) {
    return this.helpLinkMap.get(helpLinkKey);
  }

  /**
   * @return the password
   */
  public String getPassword() {
    String ret = null;
    if (this.encPassword != null) {
      ret = Decryptor.getInstance().decrypt(this.encPassword, getLoggerCDM());
    }

    return ret;
  }

  /**
   * @param password the password to set
   */
  public void setPassword(final String password) {
    this.encPassword = Encryptor.getInstance().encrypt(password, getLoggerCDM());
  }


  /**
   * @return the encPassword
   */
  public String getEncPassword() {
    return this.encPassword;
  }

  private CDMLogger getLoggerCDM() {
    return CDMLogger.getInstance();
  }


  /**
   * @return the rvwCommentTemplateMap
   */
  public Map<Long, RvwCommentTemplate> getRvwCommentTemplateMap() {
    return this.rvwCommentTemplateMap;
  }


  /**
   * @return the rvwCmntHistoryMap
   */
  public Map<Long, RvwUserCmntHistory> getRvwCmntHistoryMap() {
    return this.rvwCmntHistoryMap;
  }


}