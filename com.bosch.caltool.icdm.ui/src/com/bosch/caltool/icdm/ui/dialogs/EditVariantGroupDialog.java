package com.bosch.caltool.icdm.ui.dialogs;


import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.icdm.client.bo.a2l.A2LEditorDataHandler;
import com.bosch.caltool.icdm.client.bo.a2l.VariantMapClientModel;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LDetailsStructureModel;
import com.bosch.caltool.icdm.model.a2l.A2lVarGrpMapCmdModel;
import com.bosch.caltool.icdm.model.a2l.A2lVarGrpVariantMapping;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lVarGrpVarMappingServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lVariantGroupServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * This class provides a dialog to add Varaint group Information
 */
public class EditVariantGroupDialog extends Dialog {

  /**
   * Dialog width constant
   */
  private static final int DIALOG_WIDTH = 500;

  /**
   * Dialog Height constant
   */
  private static final int DIALOG_HEIGHT = 250;

  /**
   * Save Button
   */
  protected Button saveBtn;

  /**
   * Composite instance
   */
  private Composite top;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * Cancel Button
   */
  protected Button cancelBtn;


  /**
   * PIDCVersionDetailsSection instance
   */
  private AddVaraintGroupSection adVarSection;


  private MapVariantSection mapVariantSection;

  private final A2lVariantGroup a2lVariantGroup;

  private final A2LDetailsStructureModel a2lDetailsStructureModel;

  private final PidcA2l pidcA2l;

  /**
   * @param parentShell instance
   * @param vrsnpage VersionsPage
   * @param isEdit is an update or create operation
   * @param a2lVariantGroup a2lVariantGroup
   * @param a2lDetailsStructureModel
   * @param dataHandler
   */
  public EditVariantGroupDialog(final Shell parentShell, final A2LEditorDataHandler dataHandler) {
    super(parentShell);
    this.a2lVariantGroup = dataHandler.getA2lWpInfoBo().getSelectedA2lVarGroup();
    this.a2lDetailsStructureModel = dataHandler.getDetailsStrucModel();
    this.pidcA2l = dataHandler.getA2lWpInfoBo().getPidcA2lBo().getPidcA2l();

  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {

    // set dialog title
    newShell.setText("Edit Variant Groups for Par2Wp Assignment");
    newShell.setMinimumSize(DIALOG_WIDTH, DIALOG_HEIGHT);
    super.configureShell(newShell);
  }

  /**
   * Creates the Dialog Area
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createDialogArea(final Composite parent) {

    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    this.top.setLayoutData(new GridData(GridData.FILL_BOTH));


    this.adVarSection = new AddVaraintGroupSection(this.top, getFormToolkit());
    this.adVarSection.createVarGroupSection();
    this.adVarSection.setNameDesc(this.a2lVariantGroup);
    this.adVarSection.setOkBtn(this.saveBtn);


    this.mapVariantSection = new MapVariantSection(this.top, getFormToolkit());
    this.mapVariantSection.createVarMapSection();
    SortedSet<VariantMapClientModel> varMapModelList =
        createVarMapModel(this.a2lDetailsStructureModel, this.a2lVariantGroup);

    this.mapVariantSection.setTabInput(varMapModelList);

    return this.top;
  }

  /**
   * @param a2lDetailsStructureModel
   * @param a2lVariantGroup
   * @return
   */
  private SortedSet<VariantMapClientModel> createVarMapModel(final A2LDetailsStructureModel a2lDetailsStructureModel,
      final A2lVariantGroup a2lVariantGroup) {
    Map<Long, List<PidcVariant>> mappedVarMap = a2lDetailsStructureModel.getMappedVariantsMap();


    SortedSet<VariantMapClientModel> modelList = new TreeSet<>();


    for (Entry<Long, List<PidcVariant>> entry : mappedVarMap.entrySet()) {

      Long key = entry.getKey();
      if (key.equals(a2lVariantGroup.getId())) {
        createModelListForMapped(modelList, entry);

      }
      else {
        if (entry.getValue() != null) {
          for (PidcVariant pidcVariant : entry.getValue()) {
            VariantMapClientModel model = new VariantMapClientModel();
            model.setVariantName(pidcVariant.getName());
            model.setVariantDesc(pidcVariant.getDescription());
            model.setMapped(false);
            model.setOtherVarGroupName(a2lDetailsStructureModel.getA2lVariantGrpMap().get(key).getName());
            model.setVariantId(pidcVariant.getId());
            modelList.add(model);

          }
        }


      }

    }


    createModelForUnmappedVar(a2lDetailsStructureModel, modelList);


    return modelList;

  }


  /**
   * @param modelList
   * @param entry
   */
  private void createModelListForMapped(final SortedSet<VariantMapClientModel> modelList,
      final Entry<Long, List<PidcVariant>> entry) {
    if (entry.getValue() != null) {
      for (PidcVariant pidcVariant : entry.getValue()) {
        VariantMapClientModel model = new VariantMapClientModel();
        model.setVariantName(pidcVariant.getName());
        model.setVariantDesc(pidcVariant.getDescription());
        model.setMapped(true);
        model.setOtherVarGroupName(null);
        model.setVariantId(pidcVariant.getId());
        modelList.add(model);


      }
    }
  }


  /**
   * @param a2lDetailsStructureModel
   * @param modelList
   */
  private void createModelForUnmappedVar(final A2LDetailsStructureModel a2lDetailsStructureModel,
      final SortedSet<VariantMapClientModel> modelList) {
    Set<PidcVariant> unmappedVariants = a2lDetailsStructureModel.getUnmappedVariants();

    for (PidcVariant pidcVariant : unmappedVariants) {
      VariantMapClientModel model = new VariantMapClientModel();
      model.setVariantName(pidcVariant.getName());
      model.setVariantDesc(pidcVariant.getDescription());
      model.setMapped(false);
      model.setOtherVarGroupName(null);
      model.setVariantId(pidcVariant.getId());
      modelList.add(model);
    }
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
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    boolean varGrpChanged = ((this.adVarSection.getChangedName() != null) &&
        !(this.adVarSection.getChangedName().equals(this.a2lVariantGroup.getName()))) ||
        ((this.adVarSection.getChangedDesc() != null) &&
            !(this.adVarSection.getChangedDesc().equals(this.a2lVariantGroup.getDescription())));

    if (varGrpChanged) {
      if (this.adVarSection.getChangedName() != null) {
        this.a2lVariantGroup.setName(this.adVarSection.getChangedName());
      }
      if (this.adVarSection.getChangedDesc() != null) {
        this.a2lVariantGroup.setDescription(this.adVarSection.getChangedDesc());
      }
      A2lVariantGroupServiceClient a2lVarServiceClient = new A2lVariantGroupServiceClient();
      try {
        a2lVarServiceClient.update(this.a2lVariantGroup, this.pidcA2l);
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().error("Error when updating A2L Variant group", exp);
      }

    }
    mapVariants();
    super.okPressed();
  }


  /**
   *
   */
  private void mapVariants() {
    try {

      A2lVarGrpMapCmdModel model = new A2lVarGrpMapCmdModel();
      for (VariantMapClientModel variantMapClientModel : this.mapVariantSection.getChangedModelList()) {
        A2lVarGrpVariantMapping mapping = new A2lVarGrpVariantMapping();
        mapping.setA2lVarGroupId(this.a2lVariantGroup.getId());
        mapping.setVariantId(variantMapClientModel.getVariantId());

        // get the value if already present for variant to be mapped
        A2lVarGrpVariantMapping a2lVarGrpVarMapping =
            this.a2lDetailsStructureModel.getGroupMappingMap().get(mapping.getVariantId());

        // Logic for new mapping to be created
        if (variantMapClientModel.isMapped()) {

          if ((a2lVarGrpVarMapping == null) ||
              (!a2lVarGrpVarMapping.getA2lVarGroupId().equals(mapping.getA2lVarGroupId()))) {
            model.getMappingTobeCreated().add(mapping);
          }

          // If the mapping is already present delete the mapping for the old var group
          if ((a2lVarGrpVarMapping != null) &&
              (!a2lVarGrpVarMapping.getA2lVarGroupId().equals(mapping.getA2lVarGroupId()))) {
            model.getMappingTobeDeleted().add(a2lVarGrpVarMapping);
          }
        }
        else {
          if ((a2lVarGrpVarMapping != null) &&
              ((a2lVarGrpVarMapping.getA2lVarGroupId().equals(mapping.getA2lVarGroupId())))) {
            mapping.setId(a2lVarGrpVarMapping.getId());
            mapping.setVersion(a2lVarGrpVarMapping.getVersion());

            model.getMappingTobeDeleted().add(mapping);
          }
        }

      }
      A2lVarGrpVarMappingServiceClient a2lVarGrpMapClient = new A2lVarGrpVarMappingServiceClient();
      if (CommonUtils.isNotEmpty(model.getMappingTobeCreated()) ||
          CommonUtils.isNotEmpty(model.getMappingTobeDeleted())) {
        a2lVarGrpMapClient.updateVariantMappings(model, this.pidcA2l);
      }

    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error("Error when mapping/unmapping Variants to Variant group", exp, Activator.PLUGIN_ID);
    }

  }

  public boolean validateFields() {

    return false;


  }

  /**
   * {@inheritDoc}
   */

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Save", true);
    this.saveBtn.setEnabled(false);
    if (null != this.adVarSection) {
      this.adVarSection.setOkBtn(this.saveBtn);
    }
    if (null != this.mapVariantSection) {
      this.mapVariantSection.setOkBtn(this.saveBtn);
    }
    this.cancelBtn = createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.DIALOG_TRIM);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isResizable() {
    return true;
  }

  /**
   * @return the saveBtn
   */
  public Button getSaveBtn() {
    return this.saveBtn;
  }


}