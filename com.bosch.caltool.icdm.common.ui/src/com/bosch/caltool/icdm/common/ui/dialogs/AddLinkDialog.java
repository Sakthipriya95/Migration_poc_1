/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.dialogs;

import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.views.data.LinkData;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;


/**
 * ICDM-765 Dialog to add links
 *
 * @author mkl2cob
 */
public class AddLinkDialog extends LinkDialog {

  /**
   * new LinkData that will be created
   */
  private final LinkData linkData = new LinkData(null);

  /**
   * ICDM-452 Constructor
   *
   * @param parentShell parent shell
   * @param linkTabViewer GridTableViewer
   */
  public AddLinkDialog(final Shell parentShell, final GridTableViewer linkTabViewer, final boolean createDescGer) {
    super(parentShell, createDescGer);
    setLinksTabViewer(linkTabViewer);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Add Link");
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
    setTitle("Add Link");
    // Set the message
    setMessage("Enter Link Details", IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {

    final String link = this.linkText.getText().trim();
    final String descEnglish = this.descEngText.getText().trim();
    String descGermany = "";
    if (isCreateDescGer()) {
      descGermany = this.descGermText.getText().trim();
    }
    if ((null != getLinksTabViewer()) && checkForDuplicateLinkDesc(descEnglish, null)) {
      // if desc already exists show error message
      MessageDialogUtils.getErrorMessageDialog("Duplicate Description(Eng)",
          "The Description(Eng) already exists! Please give some other Description(Eng)!");
    }
    else {
      addToAddLinkList(link, descEnglish, descGermany);

      super.okPressed();
    }
  }

  /**
   * add to command stack & refresh the table viewer
   *
   * @param link
   * @param descEnglish
   * @param descGermany
   */
  private void addToAddLinkList(final String link, final String descEnglish, final String descGermany) {
    this.linkData.setNewLink(link);
    this.linkData.setNewDescEng(descEnglish);
    this.linkData.setNewDescGer(descGermany);
    this.linkData.setOprType(CommonUIConstants.CHAR_CONSTANT_FOR_ADD);

    if (null != getLinksTabViewer()) {
      @SuppressWarnings("unchecked")
      SortedSet<LinkData> input = (SortedSet<LinkData>) getLinksTabViewer().getInput();
      if (null == input) {
        input = new TreeSet<>();
      }
      input.add(this.linkData);
      getLinksTabViewer().setInput(input);
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
