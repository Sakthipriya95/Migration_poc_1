/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.authentication.ldap.LdapException;
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.CommandExecuter;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.common.bo.user.LdapAuthenticationWrapper;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.IcdmRuntimeException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.MailHotline;
import com.bosch.caltool.icdm.logger.WSLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.general.CommonParamKey;

/**
 * @author rgo7cob
 */
public abstract class AbstractRestService {

  @Context
  private HttpServletRequest request;

  /**
   * @return the service data object
   * @throws UnAuthorizedAccessException when there are not sufficient access rights to perform the operation
   */
  protected ServiceData getServiceData() throws UnAuthorizedAccessException {

    String reqMeth = this.request.getMethod();
    ServiceData servData = (ServiceData) this.request.getAttribute(WsCommonConstants.RWS_REQUEST_ATTR_COMMON_SER_DATA);

    if (("PUT".equals(reqMeth) || "POST".equals(reqMeth) || "DELETE".equals(reqMeth)) &&
        validateServData(servData)) {
      // Should be a valid user to perform the data change operations
      throw new UnAuthorizedAccessException("Insufficient privileges to do this operation");
    }

    return servData;

  }

  /**
   * @param servData
   * @return
   */
  private boolean validateServData(ServiceData servData) {
    return (servData == null) || !servData.isAuthenticatedUser();
  }

  /**
   * @param command to execute
   * @throws IcdmException error in executing command
   */
  protected final void executeCommand(final AbstractSimpleCommand command) throws IcdmException {
    ServiceData serviceData = command.getServiceData();
    CommandExecuter cmdExecute = serviceData.getCommandExecutor();
    cmdExecute.execute(command);
  }

  /**
   * @param commandList to execute
   * @throws IcdmException error in executing command
   */
  protected final void executeCommand(final List<AbstractSimpleCommand> commandList) throws IcdmException {
    if (CommonUtils.isNullOrEmpty(commandList)) {
      throw new IcdmRuntimeException("Command list cannot be empty");
    }

    ServiceData serviceData = commandList.get(0).getServiceData();
    CommandExecuter cmdExecute = serviceData.getCommandExecutor();
    cmdExecute.execute(commandList);
  }

  /**
   * iCDM-834 Gets the sender object with receipient as iCDM Hotline
   *
   * @param userName as input
   * @return MailHotline
   * @throws IcdmException when ldap connection fails
   */
  protected MailHotline getHotlineNotifier(final String userName) throws IcdmException {

    String fromAddr = null;
    try {
      fromAddr = new LdapAuthenticationWrapper().getUserDetails(userName).getEmailAddress();
    }
    catch (LdapException e) {
      getLogger().error(e.getMessage(), e);
      throw new IcdmException(e.getMessage(), e);

    }

    // get the HOTLINE address from table
    String toAddr = new CommonParamLoader(getServiceData()).getValue(CommonParamKey.ICDM_HOTLINE_TO);
    // get notification status // icdm-946
    String status = new CommonParamLoader(getServiceData()).getValue(CommonParamKey.MAIL_NOTIFICATION_ENABLED);
    if (ApicUtil.compare(status, ApicConstants.CODE_YES) == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      return new MailHotline(fromAddr, toAddr, true/* automatic notification enabled */);
    }
    // Set details and send mail
    return new MailHotline(fromAddr, toAddr, false/* automatic notification disabled */);
  }


  /**
   * iCDM-889 Gets the sender object with receipient as iCDM User(s)
   *
   * @param toAddr to addresses to be notified by hotline
   * @return MailHotline
   * @throws UnAuthorizedAccessException error while retrieving data
   */
  protected MailHotline getUserNotifier(final Set<String> toAddr) throws UnAuthorizedAccessException {
    // get the HOTLINE address from table
    String fromAddr = new CommonParamLoader(getServiceData()).getValue(CommonParamKey.ICDM_HOTLINE_TO);
    // get notification status // icdm-946
    String status = new CommonParamLoader(getServiceData()).getValue(CommonParamKey.MAIL_NOTIFICATION_ENABLED);
    if (ApicUtil.compare(status, ApicConstants.CODE_YES) == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      return new MailHotline(fromAddr, toAddr, true/* automatic notification enabled */);
    }
    // Create the mail sender with from and to addresses
    return new MailHotline(fromAddr, toAddr, false/* automatic notification disabled */);
  }

  /**
   * @return logger
   */
  protected final ILoggerAdapter getLogger() {
    return WSLogger.getInstance();
  }

  /**
   * @return the request
   */
  public HttpServletRequest getRequest() {
    return this.request;
  }

  /**
   * Method to Format FilePath to UTF-8 to handle german special characters
   *
   * @param filePath as input
   * @return formatted filepath
   */
  protected String getUTFFilePath(final String filePath) {
    return new String(filePath.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
  }

}
