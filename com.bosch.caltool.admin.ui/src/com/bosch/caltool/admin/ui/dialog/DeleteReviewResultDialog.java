/*
 * Copyright (c) ETAS GmbH 2024. All rights reserved.
 */
package com.bosch.caltool.admin.ui.dialog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.calcomp.externallink.exception.ExternalLinkException;
import com.bosch.calcomp.externallink.process.LinkProcessor;
import com.bosch.caltool.admin.ui.Activator;
import com.bosch.caltool.icdm.common.bo.general.EXTERNAL_LINK_TYPE;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.ReviewResultDeleteValidation;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CDRReviewResultServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.text.TextUtil;

/**
 * The Dialog to handle the deletion of Review Result under Admin menu
 */
public class DeleteReviewResultDialog extends TitleAreaDialog {

  /**
   * Delete review result dialog Title
   */
  private static final String DIALOG_TITLE = "Delete Review Result ";

  private static final String DESC_TXT_MANDATORY = "This field is mandatory";

  /**
   * Delimiter used in the iCDM links
   */
  private static final String DELIMITER_MULTIPLE_ID = "-";

  /**
   * Number of Ids in the Review Result Link
   */
  private static final int CMD_ARGS_SIZE = 3;

  private final FormToolkit formToolKit = new FormToolkit(Display.getCurrent());

  private Button deleteButton;

  private Text reviewResultIds;

  CDRReviewResultServiceClient cdrReviewResultServiceClient = new CDRReviewResultServiceClient();

  private final StringBuilder linkValidationInfo = new StringBuilder();


  /**
   * @param parentShell -parent shell for DeleteReview Result Dialog
   */
  public DeleteReviewResultDialog(final Shell parentShell) {
    super(parentShell);
  }


  /**
   * @return the reviewResultId
   */
  public Text getReviewResultIds() {
    return this.reviewResultIds;
  }


  /**
   * @param reviewResultId the reviewResultId to set
   */
  public void setReviewResultIds(final Text reviewResultId) {
    this.reviewResultIds = reviewResultId;
  }


  /**
   * @return the okButton
   */
  public Button getDeleteButton() {
    return this.deleteButton;
  }


  /**
   * @param okButton the okButton to set
   */
  public void setDeleteButton(final Button okButton) {
    this.deleteButton = okButton;
  }


  /**
   * @return the dialogTitle
   */
  public static String getDialogTitle() {
    return DIALOG_TITLE;
  }


  /**
   * @return the formToolKit
   */
  public FormToolkit getFormToolKit() {
    return this.formToolKit;
  }


  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText(DIALOG_TITLE);
    super.configureShell(newShell);
  }

  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle(DIALOG_TITLE);
    // Set the message
    setMessage("Fill the Review Result Link(s) to delete");
    return contents;
  }

  @Override
  protected boolean isResizable() {
    return true;
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.deleteButton = createButton(parent, IDialogConstants.OK_ID, "Delete", false);
    this.deleteButton.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, "Cancel", false);
  }

  @Override
  protected Control createDialogArea(final Composite parent) {
//   set grid for the dialog
    final Composite composite = (Composite) super.createDialogArea(parent);
    composite.setLayout(new GridLayout());
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.minimumWidth = 100;
    gridData.minimumHeight = 100;
    composite.setLayoutData(gridData);
    createMainComposite(composite);
    return composite;
  }

  private void createMainComposite(final Composite composite) {
    final Composite mainComposite = getFormToolKit().createComposite(composite);
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    mainComposite.setLayout(gridLayout);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    mainComposite.setLayoutData(gridData);
    createReviewIdTextField(mainComposite);
  }

  private void createReviewIdTextField(final Composite composite) {
    createLabelControl(composite, "Review Result Link ");
    this.reviewResultIds = createTextField(composite);
    this.reviewResultIds.setEnabled(true);
    this.reviewResultIds.setEditable(true);
    this.reviewResultIds.addModifyListener(e -> enableOkButton());
    ControlDecoration decorator = new ControlDecoration(this.reviewResultIds, SWT.LEFT | SWT.TOP);
    decorator.setDescriptionText(DESC_TXT_MANDATORY);
    FieldDecoration fieldDecoration =
        FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_REQUIRED);
    decorator.setImage(fieldDecoration.getImage());
    decorator.show();
    createLabelControl(composite, "");
  }

  private void createLabelControl(final Composite comp, final String lblName) {
//  form field label alignment
    final GridData gridData = new GridData();
    gridData.verticalAlignment = SWT.TOP;
    LabelUtil.getInstance().createLabel(this.formToolKit, comp, lblName);
  }

  private Text createTextField(final Composite composite) {
//  text field and its alignment
    final Text text = TextUtil.getInstance().createEditableText(this.formToolKit, composite, false, "");
    final GridData widthHintGridData = new GridData();
    widthHintGridData.horizontalAlignment = GridData.FILL;
    widthHintGridData.grabExcessHorizontalSpace = true;
    text.setLayoutData(widthHintGridData);
    return text;
  }

  private void enableOkButton() {
    if (null != this.deleteButton) {
      this.deleteButton.setEnabled(CommonUtils.isNotEmptyString(this.reviewResultIds.getText()));
    }
  }

  @Override
  protected void okPressed() {
    StringBuilder reviewResultName = new StringBuilder();
    // Multiple review results separated by delimiter ; can be splitted and fetched
    String[] inputreviewResultLinks = getReviewResultIds().getText().trim().split(";");
    // Validating the link and fetching valid review results Id's
    Set<Long> reviewResultIdSet = processReviewResultLinks(inputreviewResultLinks);
    try {
      if (CommonUtils.isNotEmpty(reviewResultIdSet)) {
        Map<Long, CDRReviewResult> reviewResultMap = this.cdrReviewResultServiceClient.getById(reviewResultIdSet);
        // Getting Delete validation map for passed review result Id
        Map<Long, ReviewResultDeleteValidation> reviewResultDeleteValidationMap =
            this.cdrReviewResultServiceClient.getMultipleReviewResultDeleteValidation(reviewResultIdSet);

        for (Map.Entry<Long, ReviewResultDeleteValidation> reviewResultDeleteValidation : reviewResultDeleteValidationMap
            .entrySet()) {
          reviewResultName.append(reviewResultMap.get(reviewResultDeleteValidation.getKey()).getName() + "\n");
          this.linkValidationInfo.append("\nThe following Review Result Link(s) are valid for Deletion: ");
          this.linkValidationInfo.append("\n" + reviewResultMap.get(reviewResultDeleteValidation.getKey()).getName() +
              " - " + (reviewResultDeleteValidation.getValue().isHasChildReview() ? "Has Child review(s)"
                  : "No Child Review(s)"));
        }
        if (MessageDialogUtils.getConfirmMessageDialogWithYesNo(DIALOG_TITLE,
            this.linkValidationInfo.toString() + "\n\n Do you want to delete?")) {
          this.cdrReviewResultServiceClient.deleteMultipleRvwResult(reviewResultIdSet);
          CDMLogger.getInstance().infoDialog(
              "The following Review Result(s) have been successfully deleted :\n" + reviewResultName,
              Activator.PLUGIN_ID);
          super.okPressed();
        }
      }
      else {
        MessageDialogUtils.getInfoMessageDialog(DIALOG_TITLE,
            this.linkValidationInfo.toString() + "\n\n Please provide Valid Review Result Links...");
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    finally {
      this.linkValidationInfo.delete(0, this.linkValidationInfo.length());
    }

  }


  /**
   * The methods to process the array of links: if the links are in invalid format -throws exception and added to
   * invalid link set else the valid links are processed to get the review result Id
   *
   * @param strReviewResultLinks - array of Reviewresult links
   * @return valid Review Result Id set
   */
  private Set<Long> processReviewResultLinks(final String[] strReviewResultLinks) {
    Set<Long> reviewResultIdSet = new HashSet<>();
    Set<String> invalidLinkSet = new HashSet<>();

    Map<String, Set<String>> linkValidationMap = new HashMap<>();
    String rvwResultId;
    String[] strRvwResultIds;

    for (String rvwresultLink : strReviewResultLinks) {
      LinkProcessor linkOpener = new LinkProcessor(rvwresultLink.trim());
      try {
        linkOpener.validateLink();
        // Checks whether the given link if CDR review result link
        rvwResultId = linkOpener.getLinkProperties().get(EXTERNAL_LINK_TYPE.CDR_RESULT.getKey());

        // Display error if the Review Result is null
        if (null == rvwResultId) {
          throw new InvalidInputException("Invalid Review Result link");
        }
        strRvwResultIds = rvwResultId.split(DeleteReviewResultDialog.DELIMITER_MULTIPLE_ID);
        if ((strRvwResultIds == null) || (strRvwResultIds.length != DeleteReviewResultDialog.CMD_ARGS_SIZE)) {
          invalidLinkSet.add(rvwresultLink);
        }
        else {
          // Check the review result id is present if not throws ApicWebServiceException
          this.cdrReviewResultServiceClient.getById(Long.valueOf(strRvwResultIds[0]));
          reviewResultIdSet.add(Long.valueOf(strRvwResultIds[0]));
        }
      }
      catch (ExternalLinkException | ApicWebServiceException | InvalidInputException e) {
        invalidLinkSet.add(rvwresultLink);
      }
    }
    linkValidationMap.put("Invalid", invalidLinkSet);
    getLinkValidationMessage(linkValidationMap);
    return reviewResultIdSet;
  }

  /**
   * Inputs linkValidationMap containing invalid links and renders the linkValidationInfo message
   */
  private void getLinkValidationMessage(final Map<String, Set<String>> linkValidationMap) {
    for (Map.Entry<String, Set<String>> linkValidationEntry : linkValidationMap.entrySet()) {
      if (CommonUtils.isNotEmpty(linkValidationEntry.getValue())) {
        if (linkValidationEntry.getKey().equals("Invalid")) {
          this.linkValidationInfo.append("Invalid Links :\n");
        }
        for (String reviewResultLink : linkValidationEntry.getValue()) {
          this.linkValidationInfo.append(reviewResultLink + "\n");
        }
      }
      this.linkValidationInfo.append("\n");
    }

  }

}

