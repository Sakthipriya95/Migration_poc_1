/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.general;

import java.text.MessageFormat;
import java.util.Map;
import java.util.SortedSet;

import org.apache.commons.lang.StringEscapeUtils;

import com.bosch.caltool.icdm.common.bo.general.MessageCodeUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.RvwUserCmntHistory;
import com.bosch.caltool.icdm.model.cdr.RvwCommentTemplate;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.general.Link;
import com.bosch.caltool.icdm.ws.rest.client.ClientConfiguration;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author gge6cob
 */
public class CommonDataBO {

  /**
   * @param grpName grpName
   * @param name name
   * @param varArgs varArgs
   * @return the message String for the Group name and name along with Var args
   * @throws ApicWebServiceException error during service call
   */
  public String getMessage(final String grpName, final String name, final Object... varArgs)
      throws ApicWebServiceException {
    String message = ApicConstants.EMPTY_STRING;
    String key = MessageCodeUtils.createMessageCode(grpName, name);
    if (GeneralClientCache.getInstance().getMessageMap().containsKey(key)) {
      message = CommonUtils.checkNull(GeneralClientCache.getInstance().getMessageMap().get(key));
      message = StringEscapeUtils.unescapeJava(message);
    }
    return MessageFormat.format(message, varArgs);
  }

  /**
   * Returns a set of review comment templates
   *
   * @return Set of Review Comment Templates
   * @throws ApicWebServiceException error during exception
   */
  public SortedSet<RvwCommentTemplate> getAllRvwCommentTemplate() throws ApicWebServiceException {
    if (GeneralClientCache.getInstance().getRvwCommentTemplateMap().isEmpty()) {
      GeneralClientCache.getInstance().loadRvwCommentTemplate();
    }
    return new java.util.TreeSet<>(GeneralClientCache.getInstance().getRvwCommentTemplateMap().values());
  }
  
  /**
   * Returns a set of review comment templates
   *
   * @return Set of Review Comment Templates
   * @throws ApicWebServiceException error during exception
   */
  public Map<Long,RvwUserCmntHistory> getRvwCommentHistoryForUser() throws ApicWebServiceException {
    if (GeneralClientCache.getInstance().getRvwCmntHistoryMap().isEmpty()) {
      GeneralClientCache.getInstance().loadRvwCommentHistory();
    }
    return GeneralClientCache.getInstance().getRvwCmntHistoryMap();
  }


  /**
   * Returns the value of the parameter identified by name
   *
   * @param key parameter key
   * @return the parameter value
   * @throws ApicWebServiceException error during service call
   */
  public final String getParameterValue(final CommonParamKey key) throws ApicWebServiceException {
    return GeneralClientCache.getInstance().getParameterValue(key);
  }

  /**
   * @param helpLinkKey String
   * @return help link
   * @throws ApicWebServiceException Exception while getting help links
   */
  public final Link getHelpLink(final String helpLinkKey) throws ApicWebServiceException {
    return GeneralClientCache.getInstance().getHelpLink(helpLinkKey);
  }

  /**
   * Get the version of iCDM installation
   *
   * @return iCDM version
   * @throws ApicWebServiceException error when retrieving data via service
   */
  public final String getIcdmVersion() throws ApicWebServiceException {
    return getParameterValue(CommonParamKey.ICDM_CLIENT_VERSION);
  }

  /**
   * @return current language
   */
  public final Language getLanguage() {
    return Language.getLanguage(ClientConfiguration.getDefault().getLanguage());
  }

  /**
   * @param currentLanguage current language
   */
  public final void setLanguage(final Language currentLanguage) {
    ClientConfiguration.getDefault().setLanguage(currentLanguage.getText());
  }

  /**
   * @return the encrypted password entered by user in password dialog during vcdm transfer
   * @throws ApicWebServiceException error during service call
   */
  public final String getEncryptedvCDMTransferPwd() throws ApicWebServiceException {
    return GeneralClientCache.getInstance().getEncryptedvCDMTransferPwd();
  }

  /**
   * @param encryptedpwd encrypted password
   * @throws ApicWebServiceException error during service call
   */
  public final void setEncryptedvCDMTransferPwd(final String encryptedpwd) throws ApicWebServiceException {
    GeneralClientCache.getInstance().setEncryptedvCDMTransferPwd(encryptedpwd);
  }
}
