package com.bosch.caltool.usecase.ui.dialogs;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.prefs.BackingStoreException;

import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseGroupClientBO;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UsecaseCreationData;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.uc.UseCaseServiceClient;
import com.bosch.caltool.usecase.ui.Activator;

/**
 * This class provides a dialog to add a new Super Group ICDM-298
 */
@SuppressWarnings("unused")
public class AddUseCaseDialog extends AbstractUseCaseDialog {


  /**
   * UseCaseGroupClientBO
   */
  private UseCaseGroupClientBO ucGrp;

  private UseCase newlyCreatedUseCase;

  /**
   * boolean to indicate whether to open usecase editor on creation
   */
  private boolean userPrefToOpen;
  /**
   * key which store the value(user selection to open editor on creation) in the file
   * C:\Users\NT-ID\AppData\Roaming\iCDM\.metadata\.plugins\org.eclipse.core.runtime\.settings\com.bosch.caltool.usecase.ui.prefs
   */
  private static final String PREF_KEY_OPEN_USECASE_EDITOR_ON_CREATE = "usecase.create.open.uceditor";
  /**
   * instance of prefereence file to store user preference and get the stored user preference
   */
  private static final IEclipsePreferences preference = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);

  /**
   * The parameterized constructor
   *
   * @param parentShell instance
   * @param item item
   */
  public AddUseCaseDialog(final Shell parentShell, final Object item) {
    super(parentShell, false, true);
    setUseCaseItem(item);
  }

  /**
   * @param item
   */
  private void setUseCaseItem(final Object item) {

    if (item instanceof UseCaseGroupClientBO) {
      setUcGrp((UseCaseGroupClientBO) item);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Add Use Case");
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
    setTitle("Add UseCase");
    // Set the message
    setMessage("Enter UseCase Details", IMessageProvider.INFORMATION);
    setUserOptToOpenEditorWithStoredValue();
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {

    final String nameEng = this.nameEngText.getText().trim();
    final String nameGer = this.nameGermText.getText().trim();
    final String descEnglish = this.descEngText.getText().trim();
    final String descGermany = this.descGermText.getText().trim();
    setUserPrefToOpen(this.chkBtnOpenEditorChkBox.getSelection());
    setNewlyCreatedUseCase(addToCommandStack(nameEng, descEnglish, nameGer, descGermany));
    // store the entered value in the eclipse preference store
    storeOpenEditorSelInPref();
    super.okPressed();
  }

  /**
   * This method creates a new record in TABV_ATTR_SUPER_GROUPS table
   *
   * @param nameEng String
   * @param descEng String
   * @param nameGer String
   * @param descGer String
   */
  private UseCase addToCommandStack(final String nameEng, final String descEnglish, final String nameGer,
      final String descGermany) {

    UseCaseServiceClient ucServiceClient = new UseCaseServiceClient();
    UseCase usecase = new UseCase();
    usecase.setNameEng(nameEng);
    usecase.setNameGer(nameGer);
    usecase.setDescEng(descEnglish);
    usecase.setDescGer(descGermany);
    usecase.setFocusMatrixYn(this.chkBtnFocusMatrixRelev.getSelection());
    usecase.setGroupId(this.ucGrp.getUseCaseGroup().getId());

    UsecaseCreationData ucCreationData = new UsecaseCreationData();
    CurrentUserBO currUserBo = new CurrentUserBO();
    try {
      ucCreationData.setOwnerId(currUserBo.getUserID());
      ucCreationData.setUsecase(usecase);
      return ucServiceClient.create(ucCreationData).getDataCreated();
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void addNewLinkAction(final ToolBarManager toolBarManager) {
    // Not applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addDeleteLinkActionToSection(final ToolBarManager toolBarManager) {
    // Not applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addEditLinkAction(final ToolBarManager toolBarManager) {
    // Not Applicable
  }

  /**
  *
  */
  private void storeOpenEditorSelInPref() {
    preference.put(PREF_KEY_OPEN_USECASE_EDITOR_ON_CREATE, isUserPrefToOpen() ? "true" : "false");
    try {
      preference.flush();
    }
    catch (BackingStoreException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp);
    }
  }

  /**
   * @return
   */
  private String getOpenEditorSelFromPref() {
    return preference.get(PREF_KEY_OPEN_USECASE_EDITOR_ON_CREATE, "true");
  }

  /**
   * method to set the value stored in eclipse preferences
   */
  private void setUserOptToOpenEditorWithStoredValue() {
    this.chkBtnOpenEditorChkBox.setSelection(Boolean.parseBoolean(getOpenEditorSelFromPref()));
  }


  /**
   * @return the ucGrp
   */
  public UseCaseGroupClientBO getUcGrp() {
    return this.ucGrp;
  }


  /**
   * @param ucGrp the ucGrp to set
   */
  public void setUcGrp(final UseCaseGroupClientBO ucGrp) {
    this.ucGrp = ucGrp;
  }


  /**
   * @return the newlyCreatedUseCase
   */
  public UseCase getNewlyCreatedUseCase() {
    return this.newlyCreatedUseCase;
  }


  /**
   * @param newlyCreatedUseCase the newlyCreatedUseCase to set
   */
  public void setNewlyCreatedUseCase(final UseCase newlyCreatedUseCase) {
    this.newlyCreatedUseCase = newlyCreatedUseCase;
  }


  /**
   * @return the userPrefToOpen
   */
  public boolean isUserPrefToOpen() {
    return this.userPrefToOpen;
  }


  /**
   * @param userPrefToOpen the userPrefToOpen to set
   */
  public void setUserPrefToOpen(final boolean userPrefToOpen) {
    this.userPrefToOpen = userPrefToOpen;
  }

}