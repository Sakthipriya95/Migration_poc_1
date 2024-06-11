/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.actions;

import java.io.IOException;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.exception.MailException;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.FileIOUtil;
import com.bosch.caltool.icdm.common.util.OutlookMail;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.product.Activator;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.IcdmClientStartupServiceClient;


/**
 * Open HotLine Mail Action class
 *
 * @author jvi6cob
 */
public class OpenHotLineMailAction extends Action implements IWorkbenchAction {

  /**
   * Action text
   */
  private static final String ACTION_TEXT = "Mail to iCDM Support";
  /**
   * hot line action ID
   */
  private static final String HOTLINE_ID = "com.bosch.icdm.hotline";

  /**
   * Constructor. Set the icon and action text and its id
   */
  public OpenHotLineMailAction() {

    super(ACTION_TEXT, ImageManager.getImageDescriptor(ImageKeys.SEND_MAIL_16X16));
    setId(HOTLINE_ID);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {


    try {
      // Get required details to compose the oulook mail
      String toAddress = new CommonDataBO().getParameterValue(CommonParamKey.ICDM_HOTLINE_TO);

      byte[] bytes = new IcdmClientStartupServiceClient().getMailtoHotLineFile(CommonUtils.getSystemUserTempDirPath());
      Map<String, byte[]> bytesMap = ZipUtils.unzip(bytes);
      byte[] mailtoHotLineFile = bytesMap.get(ApicConstants.ICDM_HOTLINE_FILE_NAME);

      String htmlContentStr = "";
      if (mailtoHotLineFile != null) {
        // Convert the contents to String
        htmlContentStr = FileIOUtil.convertHtmlByteToString(mailtoHotLineFile);
      }

      // send mail
      OutlookMail mailUtil = new OutlookMail(CDMLogger.getInstance());
      // Empty subject, so that user is forced to give a valid subject line
      mailUtil.composeEmail(toAddress, "", htmlContentStr);
    }
    catch (ApicWebServiceException | IOException | MailException exp) {
      CDMLogger.getInstance().warn(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }


  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    // not implemented
  }

}
