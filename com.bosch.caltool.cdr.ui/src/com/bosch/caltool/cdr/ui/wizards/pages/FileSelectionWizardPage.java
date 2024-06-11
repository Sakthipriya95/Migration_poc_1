/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizards.pages;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.cdr.ui.wizard.pages.resolver.FileSelectionPageResolver;
import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizard;
import com.bosch.caltool.icdm.client.bo.apic.FavouritesTreeNodeHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNodeHandler;
import com.bosch.caltool.icdm.client.bo.apic.TreeViewFlagValueProvider;
import com.bosch.caltool.icdm.common.ui.providers.FavoritesTreeViewContentProvider;
import com.bosch.caltool.icdm.common.ui.providers.FavoritesTreeViewLabelProvider;
import com.bosch.caltool.icdm.common.ui.providers.PIDTreeViewContentProvider;
import com.bosch.caltool.icdm.common.ui.providers.PIDTreeViewLabelProvider;
import com.bosch.caltool.icdm.common.ui.table.filters.PidcNameFilter;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.rcputils.griddata.GridDataUtil;


/**
 * @author bru2cob
 */
public class FileSelectionWizardPage extends WizardPage {


  /**
   * Constant to info user about Delta review opened.
   */
  private static final String DELTA_REVIEW_CANNOT_BE_PERFORMED_FOR_A_REVIEW_IN_OPEN_STATE =
      "Delta Review cannot be performed for a review in OPEN State!";

  /**
   * Instance of selected A2L file
   */
  private Long selectedA2lFileId;

  private Long selectedRvwResId;
  /**
   * Title for the a2l selection page
   */
  private static final String A2L_PAGE_TITLE = "Select an A2L File";
  /**
   * Title for the cdr result selection page
   */
  private static final String CDR_RESULT_PAGE_TITLE = "Select an Calibration Data Review Result";
  /**
   * Instance of calDataReviewWizard
   */
  private CalDataReviewWizard calDataReviewWizard;
  /**
   * Description for the a2l selection page
   */
  private static final String A2L_PAGE_DESCRIPTION =
      "Please select an A2L file, on which you need to start the review,in the Project-ID-Card tree";
  /**
   * Description for the cdr result selection page
   */
  private static final String CDR_RESULT_PAGE_DESCRIPTION =
      "Please select an Calibration Data Review Result file, on which you" + "\n" +
          " need to start the Delta review,in the Project-ID-Card tree";


  private final FileSelectionPageResolver fileSelectionPageResolver;

  /**
   *
   */
  public PidcTreeNode pidcTreeNode;

  private Long pidcA2lId;

  private final boolean isDeltaReview;
  /**
   * The folder.
   */
  private TabFolder folder;

  /** The form toolkit. */
  private FormToolkit formToolkit;
  /**
   * favouritesTreeHandler
   */
  protected FavouritesTreeNodeHandler favouritesTreeHandler;
  /**
   * favourites tree viewer
   */
  protected TreeViewer favouritesViewer;
  /**
   ** Constant for Favorites
   */
  private static final String FAVORITES = "Favorites";

  /**
   * @return the pidcA2lId
   */
  public Long getPidcA2lId() {
    return this.pidcA2lId;
  }


  /**
   * @param pidcA2lId the pidcA2lId to set
   */
  public void setPidcA2lId(final Long pidcA2lId) {
    this.pidcA2lId = pidcA2lId;
  }

  /**
   * @param pageName page name
   * @param isDeltaReview true is delta review
   */
  public FileSelectionWizardPage(final String pageName, final boolean isDeltaReview) {
    super(pageName);
    this.isDeltaReview = isDeltaReview;
    if (isDeltaReview) {
      setTitle(CDR_RESULT_PAGE_TITLE);
      setDescription(CDR_RESULT_PAGE_DESCRIPTION);
    }
    else {
      setTitle(A2L_PAGE_TITLE);
      setDescription(A2L_PAGE_DESCRIPTION);
    }
    setPageComplete(true);
    this.fileSelectionPageResolver = new FileSelectionPageResolver();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {
    initializeDialogUnits(parent);
    Composite top = new Composite(parent, SWT.NONE);
    top.setLayout(new GridLayout());
    GridData gridData = new GridData();
    // Apply grid data styles
    Composite composite = applyGridDataStyles(top, gridData);

    this.calDataReviewWizard = (CalDataReviewWizard) FileSelectionWizardPage.this.getWizard();

    // initialise the folder
    this.folder = new TabFolder(composite, SWT.NONE);
    this.folder.setLayout(new GridLayout());
    this.folder.setLayoutData(GridDataUtil.getInstance().getGridData());

    // Create Tab for PID Card
    final TabItem pidcTab = new TabItem(this.folder, SWT.NONE);
    pidcTab.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.FUNCTION_28X30));
    pidcTab.setText("PID_CARD");

    // Create Section for PID Card
    Section sectionOne =
        getFormToolkit().createSection(this.folder, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    sectionOne.setText("PID_CARD");
    sectionOne.getDescriptionControl().setEnabled(false);
    sectionOne.setLayoutData(getGridDataWithOutGrabExcessVSpace());
    pidcTab.setControl(sectionOne);


    Composite childCompositePIDC = getFormToolkit().createComposite(sectionOne, SWT.NONE);
    childCompositePIDC.setLayout(new GridLayout());
    childCompositePIDC.setLayoutData(GridDataUtil.getInstance().getGridData());
    sectionOne.setClient(childCompositePIDC);

    // Creating the pidc tree for PID Card tab
    pidcTreeView(gridData, childCompositePIDC);

    // Create Tab for Favorites
    final TabItem favTab = new TabItem(this.folder, SWT.NONE);
    favTab.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.FUNCTION_28X30));
    favTab.setText(FAVORITES);

    Section sectionTwo =
        getFormToolkit().createSection(this.folder, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    sectionTwo.setText(FAVORITES);
    sectionTwo.getDescriptionControl().setEnabled(false);
    sectionTwo.setLayoutData(getGridDataWithOutGrabExcessVSpace());
    favTab.setControl(sectionTwo);

    Composite childCompositeFav = getFormToolkit().createComposite(sectionTwo, SWT.NONE);
    childCompositeFav.setLayout(new GridLayout());
    childCompositeFav.setLayoutData(GridDataUtil.getInstance().getGridData());


    createResultTreeFav(childCompositeFav);
    folderSelectionListeners();
    sectionTwo.setClient(childCompositeFav);

    setControl(top);


  }

  private void folderSelectionListeners() {

    this.folder.addSelectionListener(new SelectionAdapter() {


      @Override
      public void widgetSelected(final SelectionEvent event) {

        if (FAVORITES.equals(FileSelectionWizardPage.this.folder.getSelection()[0].getText()) &&
            (FileSelectionWizardPage.this.favouritesTreeHandler == null)) {
          FileSelectionWizardPage.this.favouritesTreeHandler =
              new FavouritesTreeNodeHandler(CommonUiUtils.getInstance().getPidcViewPartHandlerIfPresent());


          // Set Content provider for the tree
          if (FileSelectionWizardPage.this.isDeltaReview) {
            FavoritesTreeViewContentProvider favTreeViewContentProvider =
                new FavoritesTreeViewContentProvider(CommonUiUtils.getInstance().getPidcViewPartHandlerIfPresent(),
                    FileSelectionWizardPage.this.favouritesTreeHandler, false, true, false, false, false);
            FileSelectionWizardPage.this.favouritesViewer.setContentProvider(favTreeViewContentProvider);
          }
          else {
            FavoritesTreeViewContentProvider favTreeViewContentProvider =
                new FavoritesTreeViewContentProvider(CommonUiUtils.getInstance().getPidcViewPartHandlerIfPresent(),
                    FileSelectionWizardPage.this.favouritesTreeHandler, true, false, false, false, false);
            FileSelectionWizardPage.this.favouritesViewer.setContentProvider(favTreeViewContentProvider);

          }
          // Set Label provider for the tree
          FileSelectionWizardPage.this.favouritesViewer
              .setLabelProvider(new FavoritesTreeViewLabelProvider(FileSelectionWizardPage.this.favouritesViewer));
          // Call to build tree using setInput(), EMPTY string object indicates to
          // create root node in Content provider
          FileSelectionWizardPage.this.favouritesViewer.setInput("");

        }
      }

    });

  }


  private void createResultTreeFav(final Composite favChildComposite) {
    PidcNameFilter filter = new PidcNameFilter();

    // Filtered initialisation
    final FilteredTree tree = new FilteredTree(favChildComposite, SWT.BORDER, filter, true);

    // Review 286997
    // hiding the filter control due to performance issues

    Text filterControl = tree.getFilterControl();
    filterControl.setVisible(true);
    filterControl.getParent().setVisible(true);

    // gridData initialisation
    final GridData gridData = new GridData();

    applyGridDataStyles(gridData, favChildComposite);

    this.favouritesViewer = tree.getViewer();

    this.favouritesViewer.addSelectionChangedListener(event -> {

      // Clear the error message everytime
      FileSelectionWizardPage.this.setErrorMessage(null);
      IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      Object selected = selection.getFirstElement();
      if (selected instanceof PidcTreeNode) {
        FileSelectionWizardPage.this.pidcTreeNode = (PidcTreeNode) selected;
        FileSelectionWizardPage.this.pidcTreeNode.getNodeType();
        if (FileSelectionWizardPage.this.pidcTreeNode.getNodeType().getUiType()
            .equals(PidcTreeNode.PIDC_TREE_NODE_TYPE.PIDC_A2L.getUiType()) &&
            (!FileSelectionWizardPage.this.calDataReviewWizard.isDeltaReview() &&
                this.pidcTreeNode.getPidcA2l().isActive())) {
          FileSelectionWizardPage.this.selectedA2lFileId =
              FileSelectionWizardPage.this.pidcTreeNode.getPidcA2l().getA2lFileId();
          FileSelectionWizardPage.this.calDataReviewWizard.getCdrWizardUIModel().setExceptioninWizard(false);
          getContainer().updateButtons();
          FileSelectionWizardPage.this.setPageComplete(true);
        }
        else if (FileSelectionWizardPage.this.pidcTreeNode.getNodeType().getUiType()
            .equals(PidcTreeNode.PIDC_TREE_NODE_TYPE.REV_RES_NODE.getUiType()) &&
            (FileSelectionWizardPage.this.calDataReviewWizard.isDeltaReview())) {
          if (!FileSelectionWizardPage.this.pidcTreeNode.getReviewResult().getRvwStatus()
              .equals(CDRConstants.REVIEW_STATUS.OPEN.getDbType())) {
            FileSelectionWizardPage.this.selectedRvwResId =
                FileSelectionWizardPage.this.pidcTreeNode.getReviewResult().getId();
            FileSelectionWizardPage.this.calDataReviewWizard.getCdrWizardUIModel().setExceptioninWizard(false);
            getContainer().updateButtons();
            FileSelectionWizardPage.this.setPageComplete(true);
          }
          else {
            FileSelectionWizardPage.this.setErrorMessage(DELTA_REVIEW_CANNOT_BE_PERFORMED_FOR_A_REVIEW_IN_OPEN_STATE);
            FileSelectionWizardPage.this.selectedA2lFileId = null;
            FileSelectionWizardPage.this.selectedRvwResId = null;
            getContainer().updateButtons();
            FileSelectionWizardPage.this.setPageComplete(false);
          }
        }
        else {
          FileSelectionWizardPage.this.selectedA2lFileId = null;
          FileSelectionWizardPage.this.selectedRvwResId = null;
          getContainer().updateButtons();
          FileSelectionWizardPage.this.setPageComplete(false);
        }


      }
    });

    // adding double click listener
    addDoubleClickListener(this.favouritesViewer);
  }

  private GridData getGridDataWithOutGrabExcessVSpace() {
    final GridData gridDataFour = new GridData();
    gridDataFour.horizontalAlignment = GridData.FILL;
    gridDataFour.grabExcessHorizontalSpace = true;
    gridDataFour.grabExcessVerticalSpace = true;
    gridDataFour.verticalAlignment = GridData.FILL;
    return gridDataFour;
  }

  private void applyGridDataStyles(final GridData gridData, final Composite childComposite) {
    // Apply the standard styles
    gridData.verticalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    // Create GridLayout
    final GridLayout gridLayout = new GridLayout();
    childComposite.setLayoutData(gridData);
    childComposite.setLayout(gridLayout);
  }

  private FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  /**
   * @param gridData
   * @param pidcChildComposite
   */
  private void pidcTreeView(final GridData gridData, final Composite pidcChildComposite) {
    // Create filters
    PidcNameFilter filter = new PidcNameFilter();
    FilteredTree tree = new FilteredTree(pidcChildComposite, SWT.BORDER, filter, true);

    // Review 286997
    // hiding the filter control due to performance issues
    Text filterControl = tree.getFilterControl();
    filterControl.setVisible(true);
    filterControl.getParent().setVisible(true);

    // Get viewer and set styled layout for tree TreeViewer
    TreeViewer viewer = tree.getViewer();
    viewer.getTree().setLayoutData(gridData);
    // set auto expand level
    viewer.setAutoExpandLevel(2);
    PidcTreeNodeHandler treeHandler = CommonUiUtils.getInstance().getPidcViewPartHandlerIfPresent();

    // Set Content provider for the tree
    setContentProvider(viewer, treeHandler);
    // Set Label provider for the tree
    viewer.setLabelProvider(new PIDTreeViewLabelProvider(treeHandler, viewer));
    // Call to build tree using setInput(), EMPTY string object indicates to
    // create root node in Content provider
    viewer.setInput("");
    viewer.addSelectionChangedListener(event -> {

      // Clear the error message everytime
      FileSelectionWizardPage.this.setErrorMessage(null);
      IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      Object selected = selection.getFirstElement();
      if (selected instanceof PidcTreeNode) {
        FileSelectionWizardPage.this.pidcTreeNode = (PidcTreeNode) selected;
        FileSelectionWizardPage.this.pidcTreeNode.getNodeType();
        if (FileSelectionWizardPage.this.pidcTreeNode.getNodeType().getUiType()
            .equals(PidcTreeNode.PIDC_TREE_NODE_TYPE.PIDC_A2L.getUiType()) &&
            (!FileSelectionWizardPage.this.calDataReviewWizard.isDeltaReview())) {
          FileSelectionWizardPage.this.selectedA2lFileId =
              FileSelectionWizardPage.this.pidcTreeNode.getPidcA2l().getA2lFileId();
          FileSelectionWizardPage.this.calDataReviewWizard.getCdrWizardUIModel().setExceptioninWizard(false);
          getContainer().updateButtons();
          FileSelectionWizardPage.this.setPageComplete(true);
        }
        else if (FileSelectionWizardPage.this.pidcTreeNode.getNodeType().getUiType()
            .equals(PidcTreeNode.PIDC_TREE_NODE_TYPE.REV_RES_NODE.getUiType()) &&
            (FileSelectionWizardPage.this.calDataReviewWizard.isDeltaReview())) {
          if (!FileSelectionWizardPage.this.pidcTreeNode.getReviewResult().getRvwStatus()
              .equals(CDRConstants.REVIEW_STATUS.OPEN.getDbType())) {
            FileSelectionWizardPage.this.selectedRvwResId =
                FileSelectionWizardPage.this.pidcTreeNode.getReviewResult().getId();
            FileSelectionWizardPage.this.calDataReviewWizard.getCdrWizardUIModel().setExceptioninWizard(false);
            getContainer().updateButtons();
            FileSelectionWizardPage.this.setPageComplete(true);
          }
          else {
            FileSelectionWizardPage.this.setErrorMessage(DELTA_REVIEW_CANNOT_BE_PERFORMED_FOR_A_REVIEW_IN_OPEN_STATE);
            FileSelectionWizardPage.this.selectedA2lFileId = null;
            FileSelectionWizardPage.this.selectedRvwResId = null;
            getContainer().updateButtons();
            FileSelectionWizardPage.this.setPageComplete(false);
          }
        }
        else {
          FileSelectionWizardPage.this.selectedA2lFileId = null;
          FileSelectionWizardPage.this.selectedRvwResId = null;
          getContainer().updateButtons();
          FileSelectionWizardPage.this.setPageComplete(false);
        }


      }
    });

    // adding double click listener
    addDoubleClickListener(viewer);
  }


  /**
   * @param viewer
   * @param treeHandler
   */
  private void setContentProvider(TreeViewer viewer, PidcTreeNodeHandler treeHandler) {
    if (this.isDeltaReview) {
      viewer.setContentProvider(new PIDTreeViewContentProvider(treeHandler,
          new TreeViewFlagValueProvider(false, true, false, false, false, false, false)));
    }
    else {
      viewer.setContentProvider(new PIDTreeViewContentProvider(treeHandler,
          new TreeViewFlagValueProvider(true, false, false, false, false, false, false)));
    }
  }


  /**
   * ICDM 574-This method defines the activities to be performed when double clicked on the table
   *
   * @param functionListTableViewer2
   */
  private void addDoubleClickListener(final TreeViewer treeViewer) {
    treeViewer.addDoubleClickListener(doubleclickevent -> {
      Display.getDefault().asyncExec(() -> {


        if ((FileSelectionWizardPage.this.pidcTreeNode.getNodeType().getUiType()
            .equals(PidcTreeNode.PIDC_TREE_NODE_TYPE.PIDC_A2L.getUiType())) &&
            (!FileSelectionWizardPage.this.calDataReviewWizard.isDeltaReview()) &&
            this.pidcTreeNode.getPidcA2l().isActive()) {
          FileSelectionWizardPage.this.fileSelectionPageResolver
              .setInput(FileSelectionWizardPage.this.calDataReviewWizard);
          FileSelectionWizardPage.this.calDataReviewWizard.getCdrWizardUIModel().setExceptioninWizard(false);
          getContainer().updateButtons();
          nextPressed();
          getContainer().showPage(getNextPage());
        }
        else if ((FileSelectionWizardPage.this.pidcTreeNode.getNodeType().getUiType()
            .equals(PidcTreeNode.PIDC_TREE_NODE_TYPE.REV_RES_NODE.getUiType())) &&
            (FileSelectionWizardPage.this.calDataReviewWizard.isDeltaReview())) {

          if (!FileSelectionWizardPage.this.pidcTreeNode.getReviewResult().getRvwStatus()
              .equals(CDRConstants.REVIEW_STATUS.OPEN.getDbType())) {
            FileSelectionWizardPage.this.selectedRvwResId =
                FileSelectionWizardPage.this.pidcTreeNode.getReviewResult().getId();
            FileSelectionWizardPage.this.calDataReviewWizard.getCdrWizardUIModel().setExceptioninWizard(false);
            getContainer().updateButtons();
            nextPressed();
            getContainer().showPage(getNextPage());
          }
          else {
            FileSelectionWizardPage.this.setErrorMessage(DELTA_REVIEW_CANNOT_BE_PERFORMED_FOR_A_REVIEW_IN_OPEN_STATE);
            FileSelectionWizardPage.this.selectedA2lFileId = null;
            FileSelectionWizardPage.this.selectedRvwResId = null;
            getContainer().updateButtons();
            FileSelectionWizardPage.this.setPageComplete(false);
          }
        }

      });

    });
  }

  /**
   * Data for cancelled Review
   */
  public void setDataForCancelPressed() {
    if (isPageComplete()) {
      this.fileSelectionPageResolver.setInput(this.calDataReviewWizard);
    }
  }

  /**
   * Method to invoke pages when next button is pressed
   */
  public void nextPressed() {
    // Calling setinput method in resolver
    this.fileSelectionPageResolver.setInput(FileSelectionWizardPage.this.calDataReviewWizard);
    this.fileSelectionPageResolver.processNextPressed();

  }


  /**
   * Applies styles to GridData
   *
   * @param gridData
   */
  private Composite applyGridDataStyles(final Composite parentComposite, final GridData gridData) {
    // Apply the standard styles
    gridData.verticalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    // Create GridLayout
    GridLayout gridLayout = new GridLayout();
    Composite composite = new Composite(parentComposite, SWT.NONE);
    composite.setLayoutData(gridData);
    composite.setLayout(gridLayout);
    return composite;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canFlipToNextPage() {
    this.calDataReviewWizard.setA2lFileAvailable(FileSelectionWizardPage.this.selectedA2lFileId != null);
    boolean canProceed = false;
    if (((FileSelectionWizardPage.this.selectedA2lFileId != null) || (this.selectedRvwResId != null)) &&
        !this.calDataReviewWizard.getCdrWizardUIModel().isExceptioninWizard()) {
      canProceed = true;
    }
    return canProceed;
  }

  /**
   *
   */
  public void backPressed() {
    // TODO Auto-generated method stub
  }


  /**
   * @return the selectedA2lFileId
   */
  public Long getSelectedA2lFileId() {
    return this.selectedA2lFileId;
  }


  /**
   * @param selectedA2lFileId the selectedA2lFileId to set
   */
  public void setSelectedA2lFileId(final Long selectedA2lFileId) {
    this.selectedA2lFileId = selectedA2lFileId;
  }


  /**
   * @return the selectedRvwResId
   */
  public Long getSelectedRvwResId() {
    return this.selectedRvwResId;
  }


  /**
   * @param selectedRvwResId the selectedRvwResId to set
   */
  public void setSelectedRvwResId(final Long selectedRvwResId) {
    this.selectedRvwResId = selectedRvwResId;
  }

}
