/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.icdm.client.bo.apic.FavouritesTreeNodeHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNodeHandler;
import com.bosch.caltool.icdm.client.bo.apic.TreeViewFlagValueProvider;
import com.bosch.caltool.icdm.common.ui.dialogs.PIDCVaraintSelDialog;
import com.bosch.caltool.icdm.common.ui.providers.FavoritesTreeViewContentProvider;
import com.bosch.caltool.icdm.common.ui.providers.FavoritesTreeViewLabelProvider;
import com.bosch.caltool.icdm.common.ui.providers.PIDTreeViewContentProvider;
import com.bosch.caltool.icdm.common.ui.providers.PIDTreeViewLabelProvider;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CompliReviewUsingHexData;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;


/**
 * @author svj7cob
 */
public class PidcVersionVaraintSelDialog extends PIDCVaraintSelDialog {

  /**
   * Constant for Favorites
   */
  private static final String FAVORITES = "Favorites";
  /**
   * Constant for PIDC card
   */
  private static final String PID_CARD = "PID Card";
  /**
   * Shell Width
   */
  private static final int SHELL_WIDTH = 600;
  /**
   * Shell Height
   */
  private static final int SHELL_HEIGHT = 600;
  /**
   * Review Comment Dialog
   */
  private static final String TITLE = "Select PIDC Version / Variant";
  /**
   * hex file selection dialog
   */
  private final HexFileSelectionDialog hexFileSelectionDialog;

  /** The folder. */
  private TabFolder folder;

  /** The section one. */
  private Section sectionOne;

  /** The section Two. */
  private Section sectionTwo;

  /** The form toolkit. */
  private FormToolkit formToolkit;

  /** The form one. */
  private Form formOne;

  /** The form two. */
  private Form formTwo;

  /**
   * compli ReviewDialog
   */
  private final CompliReviewDialog compliReviewDialog;

  private PidcTreeNodeHandler treeHandler;

  /**
   * favouritesTreeHandler
   */
  private FavouritesTreeNodeHandler favouritesTreeHandler;

  /**
   * favourites tree viewer
   */
  protected TreeViewer favouritesViewer;


  /**
   * @param parentShell parentShell
   * @param hexFileSelectionDialog hexFile SelectionDialog
   */
  public PidcVersionVaraintSelDialog(final Shell parentShell, final HexFileSelectionDialog hexFileSelectionDialog,
      final CompliReviewDialog compliReviewDialog) {
    super(parentShell);
    this.hexFileSelectionDialog = hexFileSelectionDialog;
    this.compliReviewDialog = compliReviewDialog;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle(TITLE);

    // Set the message
    setMessage("Select PIDC/Variant for Compli Review", IMessageProvider.INFORMATION);
    return contents;
  }


  @Override
  protected void createContent() {
    initializeDialogUnits(this.composite);

    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    this.composite.setLayout(gridLayout);
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());

    this.folder = new TabFolder(this.composite, SWT.NONE);
    this.folder.setLayout(gridLayout);
    this.folder.setLayoutData(GridDataUtil.getInstance().getGridData());

    // Tab 1
    final TabItem tab1 = new TabItem(this.folder, SWT.NONE);
    tab1.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.FUNCTION_28X30));
    tab1.setText(PID_CARD);
    createSectionOne(getFormToolkit(), gridLayout);
    tab1.setControl(this.sectionOne);


    // Tab 2
    final TabItem tab2 = new TabItem(this.folder, SWT.NONE);
    tab2.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.FUNCTION_28X30));
    tab2.setText(FAVORITES);
    createSectionTwo(getFormToolkit(), gridLayout);
    folderSelectionListeners();
    tab2.setControl(this.sectionTwo);

  }


  private void folderSelectionListeners() {
    this.folder.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        setPIDCAndVariant(null);
        if (FAVORITES.equals(PidcVersionVaraintSelDialog.this.folder.getSelection()[0].getText()) &&
            (PidcVersionVaraintSelDialog.this.favouritesTreeHandler == null)) {
          PidcVersionVaraintSelDialog.this.favouritesTreeHandler =
              new FavouritesTreeNodeHandler(PidcVersionVaraintSelDialog.this.treeHandler);
          // Set Content provider for the tree
          if (null != PidcVersionVaraintSelDialog.this.hexFileSelectionDialog) {
            // Set Content provider for the tree ( enable variant node)
            FavoritesTreeViewContentProvider favTreeViewContentProvider =
                new FavoritesTreeViewContentProvider(PidcVersionVaraintSelDialog.this.treeHandler,
                    PidcVersionVaraintSelDialog.this.favouritesTreeHandler, false, false, true, false, false);
            favTreeViewContentProvider
                .setPidcVarMap(PidcVersionVaraintSelDialog.this.hexFileSelectionDialog.getPidcVarMap());
            PidcVersionVaraintSelDialog.this.favouritesViewer.setContentProvider(favTreeViewContentProvider);
          }
          else {
            // Set Content provider for the tree
            PidcVersionVaraintSelDialog.this.favouritesViewer
                .setContentProvider(new FavoritesTreeViewContentProvider(PidcVersionVaraintSelDialog.this.treeHandler,
                    PidcVersionVaraintSelDialog.this.favouritesTreeHandler, true, false, false, false, false));
          }

          // Set Label provider for the tree
          PidcVersionVaraintSelDialog.this.favouritesViewer
              .setLabelProvider(new FavoritesTreeViewLabelProvider(PidcVersionVaraintSelDialog.this.favouritesViewer));
          // Call to build tree using setInput(), EMPTY string object indicates to
          // create root node in Content provider
          PidcVersionVaraintSelDialog.this.favouritesViewer.setInput("");
          // Add the doublle click listener.
          addDoubleClickListenerForFavouritesViewer();
        }
      }

    });
  }


  /**
   * This method initializes formToolkit.
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
   * Creates the section one.
   *
   * @param toolkit This method initializes sectionOne
   * @param gridLayout the grid layout
   */
  private void createSectionOne(final FormToolkit toolkit, final GridLayout gridLayout) {

    this.sectionOne = toolkit.createSection(this.folder, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.sectionOne.setText(PID_CARD);
    this.sectionOne.getDescriptionControl().setEnabled(false);
    createPidcTreeView(toolkit, gridLayout);
    this.sectionOne.setLayoutData(getGridDataWithOutGrabExcessVSpace());
    this.sectionOne.setClient(this.formOne);

  }

  /**
   * Creates the section two.
   *
   * @param toolkit This method initializes sectionOne
   * @param gridLayout the grid layout
   */
  private void createSectionTwo(final FormToolkit toolkit, final GridLayout gridLayout) {

    this.sectionTwo = toolkit.createSection(this.folder, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.sectionTwo.setText(FAVORITES);
    this.sectionTwo.getDescriptionControl().setEnabled(false);
    createFavouritePidcView(toolkit, gridLayout);
    this.sectionTwo.setLayoutData(getGridDataWithOutGrabExcessVSpace());
    this.sectionTwo.setClient(this.formTwo);

  }


  /**
   * Gets the grid data with out grab excess V space.
   *
   * @return the grid data with out grab excess V space
   */
  private GridData getGridDataWithOutGrabExcessVSpace() {
    final GridData gridDataFour = new GridData();
    gridDataFour.horizontalAlignment = GridData.FILL;
    gridDataFour.grabExcessHorizontalSpace = true;
    gridDataFour.grabExcessVerticalSpace = true;
    gridDataFour.verticalAlignment = GridData.FILL;
    return gridDataFour;
  }

  /**
   */
  private void createFavouritePidcView(final FormToolkit toolkit, final GridLayout gridLayout) {
    this.formTwo = toolkit.createForm(this.sectionTwo);
    this.formTwo.setLayoutData(getGridDataWithOutGrabExcessVSpace());


    // Create filters
    // Create filters
    final PatternFilter filter = new PatternFilter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void setPattern(final String patternString) {
        final StringBuilder sbRegExp = new StringBuilder();
        sbRegExp.append("*");
        if ((null != patternString) && patternString.isEmpty()) {
          PidcVersionVaraintSelDialog.this.treeHandler.setFilterText(false);
        }
        sbRegExp.append(patternString);
        sbRegExp.append("*");

        super.setPattern(sbRegExp.toString());
      }


      @Override
      protected boolean isParentMatch(final Viewer tviewer, final Object element) {
        // restrict search on other other parent
        if ((element instanceof PidcTreeNode) &&
            ((PidcTreeNode) element).getNodeType().equals(PidcTreeNode.PIDC_TREE_NODE_TYPE.LEVEL_ATTRIBUTE)) {
          PidcVersionVaraintSelDialog.this.treeHandler.setFilterText(true);
          return super.isParentMatch(tviewer, element);
        }
        return false;
      }

      @Override
      protected boolean isLeafMatch(final Viewer tviewer, final Object element) {
        if ((element instanceof PidcTreeNode) &&
            ((PidcTreeNode) element).getNodeType().equals(PidcTreeNode.PIDC_TREE_NODE_TYPE.ACTIVE_PIDC_VERSION)) {
          PidcVersionVaraintSelDialog.this.treeHandler.setFilterText(true);
          return super.isLeafMatch(tviewer, element);
        }
        return false;
      }
    };
    // Apply grid data styles
    Composite childComposite = applyGridDataStyles(this.formTwo.getBody(), new GridData());
    FilteredTree tree = new FilteredTree(childComposite, SWT.BORDER, filter, true);

    // Get viewer and set styled layout for tree
    this.favouritesViewer = tree.getViewer();
    // Review 286997
    // hiding the filter control due to performance issues
    Text filterControl = tree.getFilterControl();
    filterControl.setVisible(true);
    filterControl.getParent().setVisible(true);


    this.favouritesViewer.addSelectionChangedListener(new ISelectionChangedListener() {

      @Override
      public void selectionChanged(final SelectionChangedEvent event) {
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        Object selected = selection.getFirstElement();
        setPIDCAndVariant(selected);
      }
    });

    setSelectionForTreeViewer();

    this.formTwo.getBody().setLayout(gridLayout);
  }

  /**
   */
  private void createPidcTreeView(final FormToolkit toolkit, final GridLayout gridLayout) {

    this.formOne = toolkit.createForm(this.sectionOne);
    this.formOne.setLayoutData(getGridDataWithOutGrabExcessVSpace());


    // Create filters
    // Create filters
    final PatternFilter filter = new PatternFilter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void setPattern(final String patternString) {
        final StringBuilder sbRegExp = new StringBuilder();
        sbRegExp.append("*");
        if ((null != patternString) && patternString.isEmpty()) {
          PidcVersionVaraintSelDialog.this.treeHandler.setFilterText(false);
        }
        sbRegExp.append(patternString);
        sbRegExp.append("*");

        super.setPattern(sbRegExp.toString());
      }

      @Override
      protected boolean isParentMatch(final Viewer tviewer, final Object element) {
        // restrict search on other other parent
        if ((element instanceof PidcTreeNode) &&
            ((PidcTreeNode) element).getNodeType().equals(PidcTreeNode.PIDC_TREE_NODE_TYPE.LEVEL_ATTRIBUTE)) {
          PidcVersionVaraintSelDialog.this.treeHandler.setFilterText(true);
          return super.isParentMatch(tviewer, element);
        }
        return false;
      }

      @Override
      protected boolean isLeafMatch(final Viewer tviewer, final Object element) {
        if ((element instanceof PidcTreeNode) &&
            ((PidcTreeNode) element).getNodeType().equals(PidcTreeNode.PIDC_TREE_NODE_TYPE.ACTIVE_PIDC_VERSION)) {
          PidcVersionVaraintSelDialog.this.treeHandler.setFilterText(true);
          return super.isLeafMatch(tviewer, element);
        }
        return false;
      }
    };
    GridData gridData = new GridData();
    // Apply grid data styles
    Composite childComposite = applyGridDataStyles(this.formOne.getBody(), gridData);
    FilteredTree tree = new FilteredTree(childComposite, SWT.BORDER, filter, true);
    // Review 286997
    // hiding the filter control due to performance issues
    Text filterControl = tree.getFilterControl();
    filterControl.setVisible(true);
    filterControl.getParent().setVisible(true);

    this.treeHandler = CommonUiUtils.getInstance().getPidcViewPartHandlerIfPresent();

    // Get viewer and set styled layout for tree
    this.viewer = tree.getViewer();
    this.viewer.getTree().setLayoutData(gridData);
    // set auto expand level
    this.viewer.setAutoExpandLevel(TREE_EXPANSION_LEVEL);

    if (null != this.hexFileSelectionDialog) {

      // Set Content provider for the tree ( enable variant node)
      PIDTreeViewContentProvider pidTreeViewContentProvider =
          new PIDTreeViewContentProvider(this.treeHandler, new TreeViewFlagValueProvider(false, false, true, false, false, true, false));
      pidTreeViewContentProvider.setPidcVarMap(this.hexFileSelectionDialog.getPidcVarMap());
      this.viewer.setContentProvider(pidTreeViewContentProvider);
    }
    else {
      // Set Content provider for the tree
      this.viewer.setContentProvider(
          new PIDTreeViewContentProvider(this.treeHandler, new TreeViewFlagValueProvider(true, false, false, false, false, true, false)));
    }

    // Set Label provider for the tree
    this.viewer.setLabelProvider(new PIDTreeViewLabelProvider(this.treeHandler, this.viewer));
    // Call to build tree using setInput(), EMPTY string object indicates to
    // create root node in Content provider
    this.viewer.setInput("");
    this.viewer.addSelectionChangedListener(new ISelectionChangedListener() {

      @Override
      public void selectionChanged(final SelectionChangedEvent event) {
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        Object selected = selection.getFirstElement();
        setPIDCAndVariant(selected);
      }


    });
    // Add the doublle click listener.
    addDoubleClickListener();
    setSelectionForTreeViewer();
    this.formOne.getBody().setLayout(gridLayout);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setSize(SHELL_WIDTH, SHELL_HEIGHT);
    newShell.setText("Select PIDC/Variant for Compli Review");
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    newShell.setLayout(new GridLayout());
    newShell.setLayoutData(gridData);
    super.configureShell(newShell);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    CompliReviewUsingHexData pidcData;
    PidcVersionServiceClient client = new PidcVersionServiceClient();
    try {

      if (null == this.hexFileSelectionDialog) {
        pidcData = client.getPidcVersDetailsForCompliHex(this.selPidcVer.getId());
        this.compliReviewDialog.setCompliHexData(pidcData);
        this.compliReviewDialog.setPidcVersion(this.selPidcVer);
        this.compliReviewDialog.getPidcVersionText().setText(getPidcElementText());
      }
      else {
        Long pidcVersid;
        if (this.selPidcVer == null) {
          pidcVersid = this.selVar.getPidcVersionId();
        }
        else {
          pidcVersid = this.selPidcVer.getId();
        }

        pidcData = client.getPidcVersDetailsForCompliHex(pidcVersid);
        this.compliReviewDialog.setCompliHexData(pidcData);
        // If the Varaint is not selected
        if ((this.selPidcVer != null) && !pidcData.getPidcVariants().isEmpty() && CommonUtils.isNull(this.selVar)) {
          CDMLogger.getInstance().errorDialog("Variant is Defined for the PIDC. Please select Variant for Review",
              Activator.PLUGIN_ID);
          if (FAVORITES.equals(PidcVersionVaraintSelDialog.this.folder.getSelection()[0].getText())) {
            // Set the Selection to the leaf node.
            this.favouritesViewer.setSelection(new StructuredSelection(this.selPidcVer), true);
            // Set Expansion to the leaf node.
            this.favouritesViewer.setExpandedState(this.selPidcVer, true);
          }
          else {
            this.viewer.setSelection(new StructuredSelection(this.selPidcVer), true);
            // Set Expansion to the leaf node.
            this.viewer.setExpandedState(this.selPidcVer, true);
          }
          return;
        }
        this.compliReviewDialog.setPidcVariant(this.selVar);
        // If the selected Version is null get the Version from Variant.
        if (this.selPidcVer == null) {
          this.selPidcVer = pidcData.getPidcVersion();
        }
        this.hexFileSelectionDialog.getPidcObjNameText().setText("");
        if (null == this.hexFileSelectionDialog.getCompliReviewDialog().getPidcVersion()) {
          this.hexFileSelectionDialog.getCompliReviewDialog().setPidcVersion(this.selPidcVer);
          this.hexFileSelectionDialog.setSamePidcElement(true);
          setPidcVersionInDialog();
        }
        else {
          if (!this.hexFileSelectionDialog.getCompliReviewDialog().getPidcVersion().equals(this.selPidcVer)) {
            this.hexFileSelectionDialog.setSamePidcElement(false);
          }
          else {
            this.hexFileSelectionDialog.setSamePidcElement(true);
            setPidcVersionInDialog();
          }
        }
      }


    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    super.okPressed();
  }

  /**
   *
   */
  private void setPidcVersionInDialog() {
    // If the version is not selected then the version name must be empty.
    this.hexFileSelectionDialog.setPidcVersId(this.selPidcVer.getId());
    this.hexFileSelectionDialog.setPidcVersName(this.selPidcVer.getName());

    this.hexFileSelectionDialog.getPidcObjNameText().setText(getPidcElementText());
    if (null != this.selVar) {
      this.hexFileSelectionDialog.setPidcVariantId(this.selVar.getId());
      this.hexFileSelectionDialog.setPidcVariantName(this.selVar.getName());
    }
  }

  private String getPidcElementText() {
    if (CommonUtils.isNull(this.selVar)) {
      return this.selPidcVer.getName();
    }
    return this.selVar.getName() + " [ PIDC Version : " + this.selPidcVer.getName() + " ]";
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void cancelPressed() {
    if (null != this.hexFileSelectionDialog) {
      this.hexFileSelectionDialog.setSamePidcElement(true);
    }
    super.cancelPressed();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setSelectionForTreeViewer() {
    PidcVersion pidcVersion = null;
    if (null == this.hexFileSelectionDialog) {
      pidcVersion = this.compliReviewDialog.getPidcVersion();
    }
    else {
      pidcVersion = this.hexFileSelectionDialog.getCompliReviewDialog().getPidcVersion();
    }

    if (CommonUtils.isNotNull(pidcVersion)) {
      // set active pidc version
      PidcTreeNode activeVers =
          PidcVersionVaraintSelDialog.this.treeHandler.getPidcVerIdTreenodeMap().get(pidcVersion.getId());
      if (FAVORITES.equals(PidcVersionVaraintSelDialog.this.folder.getSelection()[0].getText())) {
        // Set the Selection to the leaf node.
        this.favouritesViewer.setSelection(new StructuredSelection(activeVers), true);
        // Set Expansion to the leaf node.
        this.favouritesViewer.setExpandedState(activeVers, true);
        this.favouritesViewer.getTree().getItem(activeVers.getLevel()).setExpanded(true);

      }
      else {
        // Set the Selection to the leaf node.
        this.viewer.setSelection(new StructuredSelection(activeVers), true);
        // Set Expansion to the leaf node.
        this.viewer.setExpandedState(activeVers, true);
      }
    }
  }


  @Override
  protected void addDoubleClickListener() {
    this.viewer.addDoubleClickListener(new IDoubleClickListener() {

      @Override
      public void doubleClick(final DoubleClickEvent event) {

        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        Object selected = selection.getFirstElement();

        // Set the selected PIDC and Variant
        setPIDCAndVariant(selected);

        if (null == PidcVersionVaraintSelDialog.this.selPidcVer) {

          final ITreeContentProvider provider =
              (ITreeContentProvider) PidcVersionVaraintSelDialog.this.viewer.getContentProvider();

          if (!provider.hasChildren(selected)) {
            return;
          }

          if (PidcVersionVaraintSelDialog.this.viewer.getExpandedState(selected)) {
            PidcVersionVaraintSelDialog.this.viewer.collapseToLevel(selected, AbstractTreeViewer.ALL_LEVELS);
          }
          else {
            PidcVersionVaraintSelDialog.this.viewer.expandToLevel(selected, 1);
          }
          return;
        }
        // call Ok pressed
        okPressed();
      }


    });


  }

  /**
   *
   */
  protected void addDoubleClickListenerForFavouritesViewer() {
    this.favouritesViewer.addDoubleClickListener(new IDoubleClickListener() {

      @Override
      public void doubleClick(final DoubleClickEvent event) {

        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        Object selected = selection.getFirstElement();

        // Set the selected PIDC and Variant
        setPIDCAndVariant(selected);

        if (null == PidcVersionVaraintSelDialog.this.selPidcVer) {

          final ITreeContentProvider provider =
              (ITreeContentProvider) PidcVersionVaraintSelDialog.this.favouritesViewer.getContentProvider();

          if (!provider.hasChildren(selected)) {
            return;
          }

          if (PidcVersionVaraintSelDialog.this.favouritesViewer.getExpandedState(selected)) {
            PidcVersionVaraintSelDialog.this.favouritesViewer.collapseToLevel(selected, AbstractTreeViewer.ALL_LEVELS);
          }
          else {
            PidcVersionVaraintSelDialog.this.favouritesViewer.expandToLevel(selected, 1);
          }
          return;
        }
        // call Ok pressed
        okPressed();
      }


    });
  }
}
