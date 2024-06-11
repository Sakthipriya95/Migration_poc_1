/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.bosch.caltool.icdm.client.bo.apic.EMRFileBO;
import com.bosch.caltool.icdm.client.bo.apic.EMRFileDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcCocWPDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcCocWpBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcDetailsLoader;
import com.bosch.caltool.icdm.client.bo.apic.PidcEditorDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.client.bo.apic.ProjectHandlerInit;
import com.bosch.caltool.icdm.client.bo.apic.WPRespDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.WorkPkgResponsibilityBO;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixDataHandler;
import com.bosch.caltool.icdm.client.bo.general.NodeAccessPageDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.OutLineViewDataHandler;
import com.bosch.caltool.icdm.common.ui.listeners.ILinkSelectionProvider;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;

/**
 * @author adn1cob
 */
public class PIDCEditorInput implements IEditorInput, ILinkSelectionProvider {

  /**
   * This flag is set to true only when invoked from pidc creation wizrd
   */
  private boolean newlyCreatedPIDC;
  /**
   * true, if invoked from URI client
   */
  private final boolean invokedFromURIClient;

  private final NodeAccessPageDataHandler nodeAccessBO;

  private OutLineViewDataHandler outlineDataHandler;

  private final PidcVersion selPidcVer;
  /**
   * FocusMatrixDataHandler
   */
  private FocusMatrixDataHandler fmDataHandler;

  private final PidcEditorDataHandler dataHandler;
  /**
   * WorkPkgResponsibilityBO
   */
  private WorkPkgResponsibilityBO workPkgResponsibilityBO;
  /**
   * WPRespDataHandler
   */
  private final WPRespDataHandler wpRespDataHandler;
  private final EMRFileBO emrFileBO;

  private final PidcCocWpBO pidcCocWpBO;
  private final EMRFileDataHandler emrFileDataHandler;

  private final PidcCocWPDataHandler cocWPDataHandler;

  /**
   * ucItemId usecase item selected in outline view to create link to usecase for pidc version
   */
  private Long ucItemId;
  /**
   * projUseCase flag to verify whether it is a project use case
   */
  private boolean projUseCase;
  /**
   * flag to indicate whether warning dialog is to be shown ,do not show warning dialog when pidc editor is opened using
   * usecase link
   */
  private boolean isNotShowWarnDialog;

  /**
   * Constructor to open PIDC editor when pidc version is available
   *
   * @param selPidcVer - selected pidc version
   * @param invokedFromURIClient boolean flag which denotes whether this is instantiated from URIClient or not
   */
  public PIDCEditorInput(final PidcVersion selPidcVer, final boolean invokedFromURIClient) {
    this.invokedFromURIClient = invokedFromURIClient;
    // NEED TO CHECK THIS
    this.selPidcVer = selPidcVer;
    PidcDataHandler pidcDtaHndlr = new PidcDataHandler();
    PidcDetailsLoader loader = new PidcDetailsLoader(pidcDtaHndlr);
    loader.loadDataModel(selPidcVer.getId());

    ProjectHandlerInit handlerInit =
        new ProjectHandlerInit(selPidcVer, selPidcVer, pidcDtaHndlr, ApicConstants.LEVEL_PIDC_VERSION);

    this.dataHandler = new PidcEditorDataHandler((PidcVersionBO) handlerInit.getProjectObjectBO());

    this.pidcCocWpBO = new PidcCocWpBO(this.dataHandler.getPidcVersionBO());

    this.cocWPDataHandler = new PidcCocWPDataHandler(this.pidcCocWpBO);
    this.workPkgResponsibilityBO = new WorkPkgResponsibilityBO(this.dataHandler.getPidcVersionBO());
    this.wpRespDataHandler = new WPRespDataHandler(this.workPkgResponsibilityBO, getPidcVersionBO().getPidc());
    this.emrFileBO = new EMRFileBO(getSelectedPidcVersion().getId(), this.dataHandler.getPidcVersionBO());
    this.emrFileDataHandler = new EMRFileDataHandler(this.emrFileBO);
    this.nodeAccessBO = new NodeAccessPageDataHandler(pidcDtaHndlr.getPidcVersionInfo().getPidc());
    this.nodeAccessBO.setReadColApplicable(true);

  }


  /**
   * @return the pidcCocWpBO
   */
  public PidcCocWpBO getPidcCocWpBO() {
    return this.pidcCocWpBO;
  }


  /**
   * @return Main Data Handler of the editor
   */
  public PidcEditorDataHandler getDataHandler() {
    return this.dataHandler;
  }


  /**
   * @return the Project Object BO
   */
  public PidcVersionBO getPidcVersionBO() {
    return this.dataHandler.getPidcVersionBO();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getAdapter(@SuppressWarnings("rawtypes") final Class adapter) {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean exists() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ImageDescriptor getImageDescriptor() {
    return ImageManager.getImageDescriptor(ImageKeys.PIDC_16X16);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getSelectedPidcVersion().getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IPersistableElement getPersistable() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTipText() {
    return getSelectedPidcVersion().getName();
  }

  /**
   * @return selectedPIDCard version
   */
  public PidcVersion getSelectedPidcVersion() {
    return this.dataHandler.getPidcVersion();
  }


  /**
   * @return the workPkgResponsibilityBO
   */
  public WorkPkgResponsibilityBO getWorkPkgResponsibilityBO() {
    return this.workPkgResponsibilityBO;
  }


  /**
   * @param workPkgResponsibilityBO the workPkgResponsibilityBO to set
   */
  public void setWorkPkgResponsibilityBO(final WorkPkgResponsibilityBO workPkgResponsibilityBO) {
    this.workPkgResponsibilityBO = workPkgResponsibilityBO;
  }


  /**
   * @return the wpRespDataHandler
   */
  public WPRespDataHandler getWpRespDataHandler() {
    return this.wpRespDataHandler;
  }


  /**
   * @return the emrSheetDataHandler
   */
  public EMRFileDataHandler getEmrFileDataHandler() {
    return this.emrFileDataHandler;
  }

  /**
   * @return the emrSheetBO
   */
  public EMRFileBO getEmrFileBO() {
    return this.emrFileBO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return getSelectedPidcVersion().getId().intValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    PIDCEditorInput other = (PIDCEditorInput) obj;
    // iCDM-208
    return (getSelectedPidcVersion().getId().longValue() == other.getSelectedPidcVersion().getId().longValue()) &&
        (getSelectedPidcVersion().getProRevId() == other.getSelectedPidcVersion().getProRevId());

  }

  // iCDM-1241
  /**
   * {@inheritDoc}
   */
  @Override
  public PidcVersion getEditorInputSelection() {
    return this.selPidcVer;
  }

  // iCDM-1254
  /**
   * @return the invokedFromURIClient
   */
  public boolean isInvokedFromURIClient() {
    return this.invokedFromURIClient;
  }


  /**
   * @return the newlyCreatedPIDC
   */
  public boolean isNewlyCreatedPIDC() {
    return this.newlyCreatedPIDC;
  }


  /**
   * @param newlyCreatedPIDC the newlyCreatedPIDC to set
   */
  public void setNewlyCreatedPIDC(final boolean newlyCreatedPIDC) {
    this.newlyCreatedPIDC = newlyCreatedPIDC;
  }


  /**
   * @return the nodeAccessBO
   */
  public NodeAccessPageDataHandler getNodeAccessBO() {
    return this.nodeAccessBO;
  }

  /**
   * @return the outlineDataHandler
   */
  public OutLineViewDataHandler getOutlineDataHandler() {
    return this.outlineDataHandler;
  }

  /**
   * @param outlineDataHandler the outlineDataHandler to set
   */
  public void setOutlineDataHandler(final OutLineViewDataHandler outlineDataHandler) {
    this.outlineDataHandler = outlineDataHandler;
  }

  /**
   * @return the fmDataHandler
   */
  public FocusMatrixDataHandler getFmDataHandler() {
    if (this.fmDataHandler == null) {
      this.fmDataHandler = new FocusMatrixDataHandler(getPidcVersionBO());
    }
    return this.fmDataHandler;
  }

  /**
   * @param fmDataHandler the fmDataHandler to set
   */
  public void setFmDataHandler(final FocusMatrixDataHandler fmDataHandler) {
    this.fmDataHandler = fmDataHandler;
  }

  /**
   * @param ucItemId usecase item selected in outline view to create link to usecase for pidc version
   */
  public void setUcItemId(final Long ucItemId) {
    this.ucItemId = ucItemId;
  }


  /**
   * @return the ucItemId
   */
  public final Long getUcItemId() {
    return this.ucItemId;
  }


  /**
   * @param isNotShowWarnDialog the isNotShowWarnDialog to set
   */
  public final void setNotShowWarnDialog(final boolean isNotShowWarnDialog) {
    this.isNotShowWarnDialog = isNotShowWarnDialog;
  }

  /**
   * @return boolean
   */
  public boolean isNotShowWarnDailog() {
    return this.isNotShowWarnDialog;
  }

  /**
   * @return the projUseCase
   */
  public boolean isProjUseCase() {
    return this.projUseCase;
  }

  /**
   * @param projUseCase the projUseCase to set
   */
  public void setProjUseCase(final boolean projUseCase) {
    this.projUseCase = projUseCase;
  }


  /**
   * @return the cocWPDataHandler
   */
  public PidcCocWPDataHandler getCocWPDataHandler() {
    return this.cocWPDataHandler;
  }


}
