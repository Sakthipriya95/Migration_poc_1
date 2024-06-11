/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.dialogs;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpDefinitionVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * @author pdh2cob
 */
public class AddA2lWpDefinitionVersionDialog extends AbstractDialog {

  private FormToolkit formToolkit;
  private Form form;
  private Section section;

  private Text versNameText;

  private Text versDescText;

  private String selectedVersNameString;

  /**
   * Instance of A2LWPInfoBO
   */
  private final A2LWPInfoBO a2lWpInfoBo;


  /**
   *
   */
  private static final String VALIDATE_DUPLICATE_STRING = "Version name already exists!";

  /**
   *
   */
  private A2lWpDefnVersion selectedA2lWpDefnVers;

  /**
   * @param parentShell - wp defn list dialog
   * @param a2lWpInfoBo bo instance
   * @param selectedA2lWpDefnVers - selected A2lWpDefinitionVersion
   */
  public AddA2lWpDefinitionVersionDialog(final Shell parentShell, final A2LWPInfoBO a2lWpInfoBo,
      final A2lWpDefnVersion selectedA2lWpDefnVers) {
    super(parentShell);
    this.a2lWpInfoBo = a2lWpInfoBo;
    if (selectedA2lWpDefnVers != null) {
      this.selectedA2lWpDefnVers = selectedA2lWpDefnVers;
      this.selectedVersNameString = this.selectedA2lWpDefnVers.getName();
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // Title modified
    newShell.setText("Create/Edit Version");
    newShell.setSize(500, 250);
    super.configureShell(newShell);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isResizable() {
    return true;
  }

  /**
   * Creates the dialog's contents
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.None);
    initializeDialogUnits(composite);

    final GridLayout gridLayout = new GridLayout();
    parent.setLayout(gridLayout);
    parent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
    composite.setLayout(gridLayout);
    composite.setLayoutData(GridDataUtil.getInstance().getGridData());
    createSection(getFormToolkit(), composite);
    createButtonBar(parent);
    return composite;
  }

  /**
   * This method initializes formToolkit
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  private FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  /**
   * @param toolkit This method initializes sectionOne
   * @param gridLayout
   * @param composite
   */
  private void createSection(final FormToolkit toolkit, final Composite composite) {

    this.section = toolkit.createSection(composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.section.setText("Version Details");
    this.section.setExpanded(true);
    this.section.getDescriptionControl().setEnabled(false);
    createForm(toolkit);
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm(final FormToolkit toolkit) {

    this.form = toolkit.createForm(this.section);

    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    this.form.getBody().setLayout(gridLayout);

    this.formToolkit.createLabel(this.form.getBody(), "Name");

    this.versNameText = this.formToolkit.createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.versNameText.setLayoutData(GridDataUtil.getInstance().getTextGridData());
    this.versNameText.setTextLimit(255);

    this.formToolkit.createLabel(this.form.getBody(), "Description");

    this.versDescText = this.formToolkit.createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.versDescText.setTextLimit(4000);
    this.versDescText.setLayoutData(GridDataUtil.getInstance().getTextGridData());

    if (this.selectedA2lWpDefnVers != null) {
      setData();
    }
  }

  /**
   * Set data to text fields
   */
  private void setData() {
    this.versNameText.setText(this.selectedA2lWpDefnVers.getVersionName());
    if (this.selectedA2lWpDefnVers.getDescription() != null) {
      this.versDescText.setText(this.selectedA2lWpDefnVers.getDescription());
    }
  }

  @Override
  public void okPressed() {

    String name = this.versNameText.getText();
    String desc = this.versDescText.getText();
    ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
    try {
      dialog.run(true, true, monitor -> {

        try {
          if (AddA2lWpDefinitionVersionDialog.this.selectedA2lWpDefnVers == null) {
            // Getting the existing active version Id before new active version is created
            long a2lWpDefnVersionIdOld = AddA2lWpDefinitionVersionDialog.this.a2lWpInfoBo.getActiveVers().getId();
            monitor.beginTask("Creating Work Package defintion version", 100);
            monitor.worked(30);
            boolean isA2lWpDefVersCreated = createA2lWpDefinitionVersion(name, desc);
            monitor.done();
            // If new active version of A21WPDefinitionVersion is created,
            // update the WP finished status of new version from old active version
            if (isA2lWpDefVersCreated) {
              // Getting the created active version Id
              A2lWpDefnVersion a2lWpDefnVersionNew = AddA2lWpDefinitionVersionDialog.this.a2lWpInfoBo.getActiveVers();
              new A2lWpDefinitionVersionServiceClient().updateWorkpackageStatus(a2lWpDefnVersionIdOld,
                  a2lWpDefnVersionNew);
            }
          }
          else {
            monitor.beginTask("Updating Work Package defintion version", 100);
            monitor.worked(30);
            updateA2lWpDefinitionVersion(AddA2lWpDefinitionVersionDialog.this.selectedA2lWpDefnVers, name, desc);
            monitor.done();
          }
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
        }

      });

    }
    catch (InvocationTargetException | InterruptedException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      // Restore interrupted state...
      Thread.currentThread().interrupt();
    }
    super.okPressed();
  }


  private boolean createA2lWpDefinitionVersion(final String name, final String desc) throws ApicWebServiceException {
    A2lWpDefinitionVersionServiceClient client = new A2lWpDefinitionVersionServiceClient();
    boolean isDefaultWpRespLabelAssignmentExist =
        client.isDefaultWpRespLabelAssignmentExist(this.a2lWpInfoBo.getPidcA2lBo().getPidcA2lId());
    boolean confirm = true;
    boolean isA2lWpDefVersCreated = false;
    if (isDefaultWpRespLabelAssignmentExist) {
      confirm = MessageDialogUtils.getConfirmMessageDialogWithYesNo("Warning",
          "There are parameters that have the default WP/Resp Default/Robert Bosch. It is recommended to assign all labels to a real WP and responsibility. Do you nevertheless want to continue?");
    }

    if (confirm) {
      A2lWpDefnVersion a2lWpDefnVersion = new A2lWpDefnVersion();

      A2lWpDefnVersion workingSet = this.a2lWpInfoBo.getWorkingSet();

      a2lWpDefnVersion.setVersionName(name);
      a2lWpDefnVersion.setDescription(desc);
      a2lWpDefnVersion.setActive(true);
      a2lWpDefnVersion.setParamLevelChgAllowedFlag(workingSet.isParamLevelChgAllowedFlag());
      a2lWpDefnVersion.setPidcA2lId(this.a2lWpInfoBo.getPidcA2lBo().getPidcA2lId());
      if (validate(a2lWpDefnVersion.getVersionName(), true)) {
        client.create(a2lWpDefnVersion, this.a2lWpInfoBo.getPidcA2lBo().getPidcA2l());
        isA2lWpDefVersCreated = true;
      }
    }
    return isA2lWpDefVersCreated;
  }

  /**
   * @param a2lWpDefinitionVersion
   * @param desc
   * @param name
   * @throws ApicWebServiceException
   */
  private void updateA2lWpDefinitionVersion(final A2lWpDefnVersion a2lWpDefinitionVersion, final String name,
      final String desc)
      throws ApicWebServiceException {

    if (validate(name, false)) {
      a2lWpDefinitionVersion.setVersionName(name);
      a2lWpDefinitionVersion.setDescription(desc);
      A2lWpDefinitionVersionServiceClient client = new A2lWpDefinitionVersionServiceClient();

      List<A2lWpDefnVersion> versionList = new ArrayList<>();
      versionList.add(a2lWpDefinitionVersion);
      Set<A2lWpDefnVersion> updatedVersionSet = client.update(versionList);
      for (A2lWpDefnVersion updatedA2lWpDefinitionVersion : updatedVersionSet) {
        this.a2lWpInfoBo.getA2lWpDefnVersMap().put(updatedA2lWpDefinitionVersion.getId(),
            updatedA2lWpDefinitionVersion);
      }

    }

  }


  private boolean validate(final String a2lWpDefinitionVersionName, final boolean isCreate) {
    boolean flag = true;
    if (CommonUtils.isEmptyString(a2lWpDefinitionVersionName)) {
      flag = false;
      CDMLogger.getInstance().errorDialog("Version name cannot be empty!", Activator.PLUGIN_ID);
    }
    if (this.a2lWpInfoBo.getA2lWpValidationBO().isWpDefnVersTooLong(a2lWpDefinitionVersionName)) {
      flag = false;
      CDMLogger.getInstance().errorDialog("Version name is too long! Maximum allowed length is 50 characters.",
          Activator.PLUGIN_ID);
    }
    if (isCreate) {
      if (this.a2lWpInfoBo.getA2lWpValidationBO().isWpDefnVersDuplicate(a2lWpDefinitionVersionName)) {
        flag = false;
        CDMLogger.getInstance().errorDialog(AddA2lWpDefinitionVersionDialog.VALIDATE_DUPLICATE_STRING,
            Activator.PLUGIN_ID);
      }
    }
    else {
      if (!(this.selectedVersNameString.equals(a2lWpDefinitionVersionName)) &&
          this.a2lWpInfoBo.getA2lWpValidationBO().isWpDefnVersDuplicate(a2lWpDefinitionVersionName)) {
        flag = false;
        CDMLogger.getInstance().errorDialog(AddA2lWpDefinitionVersionDialog.VALIDATE_DUPLICATE_STRING,
            Activator.PLUGIN_ID);
      }
    }
    return flag;
  }

}
