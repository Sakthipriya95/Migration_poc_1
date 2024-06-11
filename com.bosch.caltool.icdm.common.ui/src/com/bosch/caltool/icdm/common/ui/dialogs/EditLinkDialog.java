/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.dialogs;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.views.data.LinkData;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;


/**
 * @author mkl2cob
 */
public class EditLinkDialog extends LinkDialog {


  /**
   * LinkData instance
   */
  private final LinkData linkData;

  /**
   * @param parentShell parent shell
   * @param linkData AbstractUseCaseItem
   * @param linksTabViewer GridTableViewer
   * @param createDescGer
   */
  public EditLinkDialog(final Shell parentShell, final LinkData linkData, final GridTableViewer linksTabViewer,
      final boolean createDescGer) {
    super(parentShell, createDescGer);
    this.linkData = linkData;
    setLinksTabViewer(linksTabViewer);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Edit Link");
    super.configureShell(newShell);
  }

  /**
   * Creates the dialog's contents
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);

    // Set the title
    setTitle("Edit Link");
    // Set the message
    setMessage("This is to edit Link details", IMessageProvider.INFORMATION);
    setExistingValues();

    return contents;
  }


  /**
   * Set existing values to fields
   */
  private void setExistingValues() {
    this.linkText.setText(this.linkData.getNewLink());
    this.descEngText.setText(this.linkData.getNewDescEng());
    if ((this.linkData.getNewDescGer() != null) && isCreateDescGer()) {
      this.descGermText.setText(this.linkData.getNewDescGer());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    final String linkStr = this.linkText.getText().trim();
    String descEnglish = "";
    String descGermany = "";
    if (isCreateDescGer()) {
      descGermany = this.descGermText.getText().trim();
      descEnglish = this.descEngText.getText().trim();
    }
    else {
      descEnglish = this.descEngText.getText().trim();
    }
    if (checkForDuplicateLinkDesc(descEnglish, this.linkData)) {
      // if desc already exists show error message
      MessageDialogUtils.getErrorMessageDialog("Duplicate description(Eng)",
          "The description(Eng) already exists! Please give some other description(Eng");
    }
    else {
      this.linkData.setNewLink(linkStr);
      this.linkData.setNewDescEng(descEnglish);
      this.linkData.setNewDescGer(descGermany);

      addToEditLinkList(linkStr, descEnglish, descGermany);
      super.okPressed();
    }
  }

  /**
   * This method adds the edited link
   *
   * @param link2 Link
   * @param descEnglish Eng desc
   * @param descGermany Ger desc
   */
  private void addToEditLinkList(final String link2, final String descEnglish, final String descGermany) {

    this.linkData.setNewLink(link2);
    this.linkData.setNewDescEng(descEnglish);
    this.linkData.setNewDescGer(descGermany);
    if (this.linkData.getOprType() != CommonUIConstants.CHAR_CONSTANT_FOR_ADD) {
      this.linkData.setOprType(CommonUIConstants.CHAR_CONSTANT_FOR_EDIT);
    }

    if (null != getLinksTabViewer()) {
      getLinksTabViewer().setInput(getLinksTabViewer().getInput());// to invoke input changed() method
      getLinksTabViewer().refresh();
    }
  }


  /**
   * @return the linkData
   */
  public LinkData getLinkData() {
    return this.linkData;
  }
}
