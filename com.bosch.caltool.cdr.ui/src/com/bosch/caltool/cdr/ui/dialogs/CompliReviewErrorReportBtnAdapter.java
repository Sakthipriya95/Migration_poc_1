/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.views.providers.HexFileSelectionRowProvider;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.exception.MailException;
import com.bosch.caltool.icdm.common.util.OutlookMail;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CompliReviewUsingHexData;
import com.bosch.caltool.icdm.model.cdr.ExcelReportTypeEnum;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dmr1cob
 */
public class CompliReviewErrorReportBtnAdapter {

  /**
   *
   */
  private static final String LINE_BREAK = "<br>";
  /**
   *
   */
  private static final String TD_OPENING_TAG = "<td style=\"border: 1px solid #dddddd\">";
  /**
   *
   */
  private static final String TR_OPENING_TAG = "<tr style=\"border: 1px solid #dddddd\">";
  /**
   *
   */
  private static final String TD_CLOSING_TAG = "</td>";
  /**
   *
   */
  private static final String TR_CLOSING_TAG = "</tr>";

  private final CompliReviewDialog compliReviewDialog;

  /**
   * @param compliReviewDialog compli review dialog object
   */
  public CompliReviewErrorReportBtnAdapter(final CompliReviewDialog compliReviewDialog) {
    this.compliReviewDialog = compliReviewDialog;
  }

  /**
   * @return the selection adapter for the compli review dialog
   */
  public SelectionAdapter getCompliReviewSelectionAdapter() {
    return new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {

        OutlookMail mail = new OutlookMail(CDMLogger.getInstance());
        // setting priority flag to send mail with high importance
        mail.setHighPriority(true);
        StringBuilder mailContent = new StringBuilder();
        CompliReviewDialog compliRvwDialog = CompliReviewErrorReportBtnAdapter.this.compliReviewDialog;
        CompliReviewUsingHexData compliHexData = compliRvwDialog.getCompliHexData();
        PidcVersion pidcVersion = null == compliHexData ? null : compliHexData.getPidcVersion();
        String a2lFilePath = compliRvwDialog.getA2lFilePath();
        String pidcVersLink = null == pidcVersion ? "" : "icdm:pidvid," + pidcVersion.getId();
        boolean predecessorCheck = compliRvwDialog.getPredecessorCheckbox().getSelection();
        ExcelReportTypeEnum dataFileOption = null;
        if (compliRvwDialog.getOneFilePerCheckRadio().getSelection()) {
          dataFileOption = ExcelReportTypeEnum.ONEFILECHECK;
        }
        else if (compliRvwDialog.getSingleFileWithSumRadio().getSelection()) {
          dataFileOption = ExcelReportTypeEnum.SINGLEFILEWITHSUMMARY;
        }
        else if (compliRvwDialog.getSingleFileWithRedSumRadio().getSelection()) {
          dataFileOption = ExcelReportTypeEnum.SINGLEFILEWITHREDUCTIONSUMMARY;
        }
        String a2lLink = null == compliRvwDialog.getPidcA2l() ? null
            : "icdm:a2lid," + compliRvwDialog.getPidcA2l().getId() + "-" + compliRvwDialog.getPidcA2l().getA2lFileId();
        String subject = "#REPORT ISSUE - SSD Compliance check";
        try {
          mailContent.append("<html>");
          mailContent.append("Hello Hotline,");
          mailContent.append(LINE_BREAK);
          mailContent.append(LINE_BREAK);
          mailContent.append("User Comments:");
          mailContent.append(LINE_BREAK);
          mailContent.append(LINE_BREAK);
          mailContent.append("<p style=\"font-weight:bold\">");
          mailContent.append("Execution Details:");
          mailContent.append("</p>");
          mailContent.append("<p>");
          if (null != compliRvwDialog.getErrCode()) {
            mailContent.append("<span style=\"font-weight:bold\">Error Code : </span>");
            mailContent.append(compliRvwDialog.getErrCode());
            mailContent.append(LINE_BREAK);
          }
          if (null != compliRvwDialog.getErrMsg()) {
            mailContent.append("<span style=\"font-weight:bold\">Error Description : </span>");
            mailContent.append(compliRvwDialog.getErrMsg());
            mailContent.append(LINE_BREAK);
          }
          mailContent.append("<span style=\"font-weight:bold\">User ID : </span>");
          mailContent.append(new CurrentUserBO().getUserName());
          mailContent.append(LINE_BREAK);
          if (null != compliRvwDialog.getExecutionId()) {
            mailContent.append("<span style=\"font-weight:bold\">Execution ID : </span>");
            mailContent.append(compliRvwDialog.getExecutionId());
            mailContent.append(LINE_BREAK);
          }
          if (null != compliRvwDialog.getSid()) {
            mailContent.append("<span style=\"font-weight:bold\">Service ID : </span>");
            mailContent.append(compliRvwDialog.getSid());
            mailContent.append(LINE_BREAK);
          }
          mailContent.append("</p>");
          mailContent.append("<table style=\"width:100%;border-collapse: collapse\">");
          mailContent.append(TR_OPENING_TAG);
          mailContent.append(TD_OPENING_TAG);
          mailContent.append("PIDC");
          mailContent.append(TD_CLOSING_TAG);
          mailContent.append(TD_OPENING_TAG);
          mailContent.append("<a href =" + pidcVersLink + ">");
          mailContent.append(null == pidcVersion ? "" : pidcVersion.getName());
          mailContent.append("</a>");
          mailContent.append(TD_CLOSING_TAG);
          mailContent.append(TR_CLOSING_TAG);
          mailContent.append(TR_OPENING_TAG);
          mailContent.append(TD_OPENING_TAG);
          mailContent.append("A2l file name");
          mailContent.append(TD_CLOSING_TAG);
          mailContent.append(TD_OPENING_TAG);
          if (null != a2lLink) {
            mailContent.append("<a href =" + a2lLink + ">");
            mailContent.append(null == a2lFilePath ? "" : FilenameUtils.getBaseName(a2lFilePath) + ".a2l");
            mailContent.append("</a>");
          }
          else {
            mailContent.append(null == a2lFilePath ? "" : FilenameUtils.getBaseName(a2lFilePath) + ".a2l");
          }
          mailContent.append(TD_CLOSING_TAG);
          mailContent.append(TR_CLOSING_TAG);
          mailContent.append(TR_OPENING_TAG);
          mailContent.append(TD_OPENING_TAG);
          mailContent.append("PVER Name");
          mailContent.append(TD_CLOSING_TAG);
          mailContent.append(TD_OPENING_TAG);
          mailContent.append(compliRvwDialog.getPverNameText().getText());
          mailContent.append(TD_CLOSING_TAG);
          mailContent.append(TR_CLOSING_TAG);
          mailContent.append(TR_OPENING_TAG);
          mailContent.append(TD_OPENING_TAG);
          mailContent.append("PVER Variant");
          mailContent.append(TD_CLOSING_TAG);
          mailContent.append(TD_OPENING_TAG);
          mailContent.append(compliRvwDialog.getPverVariantText().getText());
          mailContent.append(TD_CLOSING_TAG);
          mailContent.append(TR_CLOSING_TAG);
          mailContent.append(TR_OPENING_TAG);
          mailContent.append(TD_OPENING_TAG);
          mailContent.append("PVER Revision");
          mailContent.append(TD_CLOSING_TAG);
          mailContent.append(TD_OPENING_TAG);
          mailContent.append(compliRvwDialog.getPverRevisionText().getText());
          mailContent.append(TD_CLOSING_TAG);
          mailContent.append(TR_CLOSING_TAG);
          mailContent.append(TR_OPENING_TAG);
          mailContent.append(TD_OPENING_TAG);
          mailContent.append("Hex File : Pidc Variant");
          mailContent.append(TD_CLOSING_TAG);
          mailContent.append(TD_OPENING_TAG);
          for (HexFileSelectionRowProvider hexFilerowProvider : compliRvwDialog.getHexFileRowProviderList()) {
            mailContent
                .append(hexFilerowProvider.getHexFileName() + " : " + (null != hexFilerowProvider.getPidcVariantName()
                    ? hexFilerowProvider.getPidcVariantName() : hexFilerowProvider.getPidcVersName()));
            mailContent.append(LINE_BREAK);
          }
          mailContent.append(TD_CLOSING_TAG);
          mailContent.append(TR_CLOSING_TAG);
          mailContent.append("DataFile Options");
          mailContent.append(TD_CLOSING_TAG);
          mailContent.append(TD_OPENING_TAG);
          mailContent.append(dataFileOption);
          mailContent.append(TD_CLOSING_TAG);
          mailContent.append(TR_CLOSING_TAG);
          mailContent.append(TR_OPENING_TAG);
          mailContent.append(TD_OPENING_TAG);
          mailContent.append("Predecessor Check");
          mailContent.append(TD_CLOSING_TAG);
          mailContent.append(TD_OPENING_TAG);
          mailContent.append(predecessorCheck);
          mailContent.append(TD_CLOSING_TAG);
          mailContent.append(TR_CLOSING_TAG);
          mailContent.append(TR_OPENING_TAG);
          mailContent.append(TD_OPENING_TAG);
          mailContent.append("</table>");
          mailContent.append(LINE_BREAK);
          mailContent.append(LINE_BREAK);
          mailContent.append("Thank You!");
          mailContent.append(LINE_BREAK);
          mailContent.append(LINE_BREAK);
          mailContent.append("This is an auto generated mail from iCDM client");
          mailContent.append("</html>");


          String[] mailToAddress =
              new CommonDataBO().getParameterValue(CommonParamKey.COMPLI_ISSUE_REPORT_TO).split(";");
          Set<String> mailToAddressSet = new HashSet<>(Arrays.asList(mailToAddress));
          mail.composeEmail(mailToAddressSet, subject, mailContent.toString());
        }
        catch (MailException | ApicWebServiceException e) {
          CDMLogger.getInstance().errorDialog(
              "Failed to create outlook mail for reported issue with execution id " + compliRvwDialog.getExecutionId(),
              e, Activator.PLUGIN_ID);
        }
      }
    };
  }
}
