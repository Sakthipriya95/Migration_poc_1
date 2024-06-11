/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.admin.ui.editors.pages;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.calcomp.externallink.exception.ExternalLinkException;
import com.bosch.calcomp.externallink.process.LinkProcessor;
import com.bosch.caltool.admin.ui.Activator;
import com.bosch.caltool.admin.ui.editors.UnmapA2lAdminEditor;
import com.bosch.caltool.apic.ui.dialogs.CustomProgressDialog;
import com.bosch.caltool.apic.ui.editors.pages.UnmapA2lDetailsComposite;
import com.bosch.caltool.icdm.common.bo.general.EXTERNAL_LINK_TYPE;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2lFileExt;
import com.bosch.caltool.icdm.ws.rest.client.a2l.UnmapA2LServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcA2lServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;


/**
 * Unmap A2l Admin Page - used by users with special admin access rights to unmap the a2l
 *
 * @author hnu1cob
 */
public class UnmapA2lAdminPage extends AbstractFormPage {

  /**
   * Number of Ids in the A2l Link
   */
  private static final int CMD_ARGS_SIZE = 2;
  /**
   * Delimiter used in the iCDM links
   */
  private static final String DELIMITER_MULTIPLE_ID = "-";
  private Form nonScrollableForm;
  /**
   * UnmapA2lAdminEditor , the editor where this page is build on
   */
  private final UnmapA2lAdminEditor editor;
  /**
   * UnmapA2lDetailsComposite , the composite which displays a2l related details
   */
  private UnmapA2lDetailsComposite detailsComp;
  private Button confirmBtn;
  private Long pidcA2lId;
  private Text a2lLinkTxt;


  /**
   * @param editor UnmapA2lAdminEditor
   * @param title String
   */
  public UnmapA2lAdminPage(final FormEditor editor, final String title) {
    super(editor, title, title);
    this.editor = (UnmapA2lAdminEditor) editor;
  }


  @Override
  public void createPartControl(final Composite parent) {
    // Create an ordinary non scrollable form on which widgets are built
    this.nonScrollableForm = this.editor.getToolkit().createForm(parent);
    this.nonScrollableForm.getBody().setLayout(new GridLayout());
    this.nonScrollableForm.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());
    // set title
    this.nonScrollableForm.setText("Unmap A2L from PIDC Version");
    // set description
    Label pageDescLbl = LabelUtil.getInstance().createLabel(this.nonScrollableForm.getBody(),
        "The below entries will be deleted during unmapping of A2L");
    // Display the description in Bold Red Colour
    pageDescLbl.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
    Font boldFont =
        new Font(pageDescLbl.getDisplay(), new FontData(GUIHelper.DEFAULT_FONT.toString(), 10, SWT.LINE_SOLID));
    pageDescLbl.setFont(boldFont);

    ManagedForm mform = new ManagedForm(parent);

    createFormContent(mform);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Control getPartControl() {
    return this.nonScrollableForm;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    /** The form toolkit. */
    FormToolkit formToolkit = managedForm.getToolkit();
    createComposite(formToolkit);
  }


  /**
   * @param formToolkit
   */
  private void createComposite(final FormToolkit formToolkit) {
    GridData gridData = GridDataUtil.getInstance().getGridData();
    GridLayout gridLayout = new GridLayout();
    // create scrollable composite
    ScrolledComposite scrollComp = new ScrolledComposite(this.nonScrollableForm.getBody(), SWT.V_SCROLL | SWT.H_SCROLL);
    // create main composite
    Composite topComp = new Composite(scrollComp, SWT.NONE);
    topComp.setLayoutData(gridData);
    topComp.setLayout(gridLayout);
    // create A2l File Link load details composites
    createLinkSection(formToolkit, topComp);
    // create Unmap a2l details composite (which includes the details of the a2l to be unmapped)
    this.detailsComp = new UnmapA2lDetailsComposite(topComp, formToolkit, null, true, null);
    this.detailsComp.setLayout(gridLayout);
    this.detailsComp.setLayoutData(gridData);
    // create Confirm Button
    this.confirmBtn = new Button(topComp, SWT.NONE);
    this.confirmBtn.setText("Confirm to Unmap A2L");
    this.confirmBtn.setToolTipText(
        "Confirm to unmap A2L. The entries in the above tables will be deleted during unmapping of A2L");

    // disable confirm button initially , it is enabled only after loading the details successfully
    this.confirmBtn.setEnabled(false);
    // addd selection listener to confirm button
    addListenerToConfirmBtn();
    // Update the layout details
    topComp.layout(true, true);

    scrollComp.setLayout(gridLayout);
    scrollComp.setLayoutData(gridData);
    scrollComp.setExpandHorizontal(true);
    scrollComp.setExpandVertical(true);
    scrollComp.setContent(topComp);
    // set the minimum size of the scrollable composite
    scrollComp.setMinSize(topComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));

  }


  /**
   * listener to confirm button
   */
  private void addListenerToConfirmBtn() {
    this.confirmBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        // call the service to delete the contents related to A2L and unmap A2L from PIDC Version
        callService();
      }

    });


  }

  /**
   * call service with progress monitor
   */
  private void callService() {
    ProgressMonitorDialog dialog = new CustomProgressDialog(Display.getDefault().getActiveShell());
    try {
      dialog.run(true, true, monitor -> {
        monitor.beginTask("Unmapping A2L...", 100);
        monitor.worked(20);
        if (UnmapA2lAdminPage.this.pidcA2lId != null) {
          try {
            new UnmapA2LServiceClient().deleteA2lrelatedEntries(UnmapA2lAdminPage.this.pidcA2lId);
            // If the unmapping is done without any errors show the message
            CDMLogger.getInstance().infoDialog("Unmapping of A2L and related entries deletion is completed",
                Activator.PLUGIN_ID);
          }
          catch (ApicWebServiceException ex) {
            // else show the error
            CDMLogger.getInstance().errorDialog(CommonUIConstants.EXCEPTION + ex.getMessage(), Activator.PLUGIN_ID);
          }
        }
        monitor.worked(100);
        monitor.done();
      });
    }
    catch (InvocationTargetException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
    }
    catch (InterruptedException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
      Thread.currentThread().interrupt();
    }
    // reset the confirm button to default
    UnmapA2lAdminPage.this.confirmBtn.setEnabled(false);
  }


  /**
   * This method creates the section to display A2L Link details
   *
   * @param formToolkit
   * @param topComp
   */
  private void createLinkSection(final FormToolkit formToolkit, final Composite topComp) {
    GridData gridDataWithoutVSGrab = GridDataUtil.getInstance().getGridData();

    GridLayout gridLayout = new GridLayout();
    // create A2L File Link section
    Section section = formToolkit.createSection(topComp, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    section.setText("A2L File Link");
    section.getDescriptionControl().setEnabled(false);
    section.setExpanded(true);
    section.setLayout(gridLayout);
    gridDataWithoutVSGrab.grabExcessVerticalSpace = false;
    section.setLayoutData(gridDataWithoutVSGrab);

    GridData gridData = GridDataUtil.getInstance().getGridData();
    Composite composite = formToolkit.createComposite(section);
    composite.setLayout(new GridLayout(3, false));
    composite.setLayoutData(gridData);
    // create label
    LabelUtil.getInstance().createLabel(composite, "A2L File Link");
    // create text field for A2L FileLink
    this.a2lLinkTxt = new Text(composite, SWT.BORDER);
    this.a2lLinkTxt.setLayoutData(gridData);
    // create Load details Button
    Button loadBtn = new Button(composite, SWT.NONE);
    loadBtn.setText("Load Details");
    // add selection to load details button
    loadBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent evnt) {
        // reset the fields to default
        resetFields();
        // process link and get pidc A2L id
        Long processedA2lId = processLink(UnmapA2lAdminPage.this.a2lLinkTxt.getText());
        if (null != processedA2lId) {
          try {
            // Get the a2l file for the pidc id
            PidcA2lFileExt a2lFileExt = new PidcA2lServiceClient().getPidcA2LFileDetails(processedA2lId);
            // Set the selected a2l file
            UnmapA2lAdminPage.this.detailsComp.setSelA2lFile(a2lFileExt);
            // fetch the details from service and fill the table
            UnmapA2lAdminPage.this.detailsComp.fetchAndSetInputForTables();
            if (UnmapA2lAdminPage.this.confirmBtn != null) {
              UnmapA2lAdminPage.this.confirmBtn.setEnabled(true);
            }
          }
          catch (ApicWebServiceException ex) {
            CDMLogger.getInstance().errorDialog(CommonUIConstants.EXCEPTION + ex.getMessage(), Activator.PLUGIN_ID);
          }
        }
      }
    });

    section.setClient(composite);
  }

  /**
   * Reset the fields to default
   */
  private void resetFields() {
    // clear all fields
    UnmapA2lAdminPage.this.detailsComp.clearAllFields();
    if (UnmapA2lAdminPage.this.confirmBtn != null) {
      UnmapA2lAdminPage.this.confirmBtn.setEnabled(false);
    }
  }


  /**
   * This method validates the a2l file id and returns the pidc a2l id
   *
   * @param a2lLink , the icdm a2l link
   * @return pidcA2lId
   */
  protected Long processLink(final String a2lLink) {
    this.pidcA2lId = null;
    try {
      LinkProcessor linkOpener = new LinkProcessor(a2lLink.trim());
      // validate the format of the a2l file link
      linkOpener.validateLink();
      // a2lID String contains A2L File Id and pidc a2l id and it will be in the format (a2lId-pidcA2lId)
      String a2lID = linkOpener.getLinkProperties().get(EXTERNAL_LINK_TYPE.A2L_FILE.getKey());
      // Display error if the a2lId is null
      if (null == a2lID) {
        throw new InvalidInputException("Invalid A2L link");
      }
      String[] strIds = a2lID.split(DELIMITER_MULTIPLE_ID);
      // Display error when the a2l file link does not
      if ((strIds == null) || (strIds.length != CMD_ARGS_SIZE)) {
        CDMLogger.getInstance().errorDialog("Invalid hyperlink for A2L file!", Activator.PLUGIN_ID);
      }
      else {
        // get the pidc A2l Id form the link
        this.pidcA2lId = Long.valueOf(strIds[0]);
      }
    }
    catch (ExternalLinkException | InvalidInputException e) {
      // if the link is not valid displays the error
      CDMLogger.getInstance().errorDialog(CommonUIConstants.EXCEPTION + e.getMessage(), Activator.PLUGIN_ID);
    }

    return this.pidcA2lId;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    // no toolbars
    return null;
  }

}
