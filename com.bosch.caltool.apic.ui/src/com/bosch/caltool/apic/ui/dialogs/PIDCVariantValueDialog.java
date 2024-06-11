/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.jface.gridviewer.CheckEditingSupport;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridColumnGroup;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.actions.PIDCGroupedAttrActionSet;
import com.bosch.caltool.apic.ui.editors.pages.PIDCAttrPage;
import com.bosch.caltool.apic.ui.sorter.PIDCVariantValTabSorter;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.apic.ui.util.PIDCPageEditUtil;
import com.bosch.caltool.apic.ui.views.providers.PIDCAttrValEditColLabelProvider;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectObjectBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeValueClientBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.ProjectAttributeUtil;
import com.bosch.caltool.icdm.client.bo.apic.ProjectHandlerInit;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.PROJ_ATTR_USED_FLAG;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.GroupdAttrPredefAttrModel;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridColumnUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * @author bru2cob
 */
// ICDM-270
public class PIDCVariantValueDialog extends AbstractDialog {

  /**
   * Attribute value col width
   */
  private static final int ATTR_VAL_COL_WIDTH = 150;

  /**
   * attr used info col index
   */
  private static final int USED_INFO_COL = 4;

  /**
   * attr not used col index
   */
  private static final int NOT_USED_COL = 3;

  /**
   * attr unknown col index
   */
  private static final int ATTR_UNKNOWN_COL = 2;

  /**
   * Summary column index
   */
  private static final int SUMMARY_COL = 6;
  /**
   * Used flag diff column index
   */
  private static final int DIFF_COL = 5;

  /**
   * Button instance for save
   */
  private Button saveBtn;

  /**
   * Composite instance for the dialog
   */
  private Composite composite;

  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * Section instance
   */
  private Section section;
  /**
   * Form instance
   */
  private Form form;
  /**
   * Composite instance
   */
  private Composite top;
  /**
   * Defines PIDC attribute value edit gridviewer column index
   */
  private static final int PIDC_ATTR_VAL_EDIT_COL_INDEX = 8;

  /**
   * PIDC Attribute Used info Grid Column group instance
   */
  private GridColumnGroup usedColumnGroup;
  /**
   * String to store the partnum,speclink and desc
   */
  private String partNum;
  /**
   * String to store the speclink
   */
  private String specLink;
  /**
   * String to store the desc
   */
  private String desc;
  /**
   * Instance of page edit util
   */
  private final PIDCPageEditUtil pageEditUtil;

  /**
   * holds the currently selected variant
   */
  private PidcVariant pidcVariant;
  /**
   * holds the currently selected sub-variant
   */
  private PidcSubVariant pidcSubVariant;
  /**
   * GridTableViewer instance for PIDC attribute
   */
  private GridTableViewer pidcAttrTabViewer;

  private final PidcDataHandler pidcDataHandler;


  /**
   * @return the pidcAttrTabViewer
   */
  public GridTableViewer getPidcAttrTabViewer() {
    return this.pidcAttrTabViewer;
  }


  /**
   * first variant of multiple variants selected
   */
  private PidcVariant variant;

  /**
   * Variants selected
   */
  private PidcVariant variants[];
  /**
   * first variant of multiple sub-variants selected
   */
  private PidcSubVariant subVariant;

  /**
   * SubVariants selected
   */
  private PidcSubVariant subVariants[];

  /**
   * selected variants pidcard version
   */
  private final PidcVersion selPidcVer;

  /**
   * add attribute value dialog
   */
  private PIDCAttrValueEditDialog dialog;
  /**
   * map which holds the variants whose value is set through this dialog
   */
  private final Map<IProjectAttribute, AttributeValue> varValMap = new HashMap<>();
  /**
   * map which holds the subvariants whose value is set through this dialog
   */
  private final Map<IProjectAttribute, AttributeValue> subVarValMap = new HashMap<>();
  /**
   * map which holds the variants and sub variants whose used status is set through this dialog
   */
  private final Map<IProjectAttribute, String> attrUsedMap = new HashMap<>();
  // ICDM-912
  /**
   * map which holds the attributes which has the same value in all variants to be set as input for this dialog
   */
  private final Map<IProjectAttribute, AttributeValue> varAttrMap = new HashMap<>();
  /**
   * map which holds the attributes which has the same value in all variants to be set as input for this dialog
   */
  private final Map<IProjectAttribute, AttributeValue> subVarAttrMap = new HashMap<>();
  /**
   * map which holds the attributes which has the same used flag in all variants to be set as input for this dialog
   */
  private final Map<IProjectAttribute, String> oldAttrUsedMap = new HashMap<>();

  /**
   * map which holds the attributes which has the same part number in all variants to be set as input for this dialog
   */
  private final Map<IProjectAttribute, String> oldPartNumMap = new HashMap<>();
  /**
   * map which holds the attributes which has the same comment in all variants to be set as input for this dialog
   */
  private final Map<IProjectAttribute, String> oldCommentMap = new HashMap<>();
  /**
   * map which holds the attributes which has the same speclink in all variants to be set as input for this dialog
   */
  private final Map<IProjectAttribute, String> oldSpecLinkMap = new HashMap<>();

  private final Map<IProjectAttribute, String> oldAttrValMap = new HashMap<>();

  /**
   * @return the oldAttrValMap
   */
  public Map<IProjectAttribute, String> getOldAttrValMap() {
    return this.oldAttrValMap;
  }


  /**
   * @return the oldAttrUsedMap
   */
  public Map<IProjectAttribute, String> getOldAttrUsedMap() {
    return this.oldAttrUsedMap;
  }


  /**
   * subvaraint attributes in the varaints selected
   */

  private final Set<String> subVars = new TreeSet<>();
  /**
   * subvariant whose values are set using this dialog
   */
  private final Set<String> selSubVars = new TreeSet<>();
  /**
   * Instance of cursor
   */
  private Cursor cursor;
  /**
   * set when variants are selected
   */
  private boolean varSelFlag;

  /**
   * set when sub-vars are selected
   */
  private boolean subVarSelFlag;

  /**
   * Sorter instance
   */
  private PIDCVariantValTabSorter valtabSorter;

  /**
   * map having all Part number in UI.
   */
  private final Map<IProjectAttribute, String> partNumMap = new ConcurrentHashMap<>();

  /**
   * @return the partNumMap
   */
  public Map<IProjectAttribute, String> getPartNumMap() {
    return this.partNumMap;
  }


  /**
   * map having all comments in UI.
   */
  private final Map<IProjectAttribute, String> commMap = new ConcurrentHashMap<>();


  /**
   * @return the commMap
   */
  public Map<IProjectAttribute, String> getCommMap() {
    return this.commMap;
  }


  /**
   * map having all spec Link in UI.
   */
  private final Map<IProjectAttribute, String> specLinkMap = new ConcurrentHashMap<>();


  /**
   * @return the specLinkMap
   */
  public Map<IProjectAttribute, String> getSpecLinkMap() {
    return this.specLinkMap;
  }

  final PIDCAttrPage pidcPage;

  // List of Grouped attributes edited from set value dialog
  private final List<IProjectAttribute> grpAttrEditList = new ArrayList<>();


  /**
   * @return the grpAttrEditList
   */
  public List<IProjectAttribute> getGrpAttrEditList() {
    return this.grpAttrEditList;
  }


  /**
   * @param parentShell shell instance
   * @param variants selected variants
   * @param subVars selected sub-vars
   * @param editorPidcVer sel pidc
   * @param pidcPage pidc attribute page
   */
  public PIDCVariantValueDialog(final Shell parentShell, final PidcVariant[] variants, final PidcSubVariant[] subVars,
      final PidcVersion editorPidcVer, final PIDCAttrPage pidcPage) {
    super(parentShell);
    this.pidcPage = pidcPage;
    this.pidcDataHandler = this.pidcPage.getProjectObjectBO().getPidcDataHandler();
    this.pageEditUtil = new PIDCPageEditUtil(PIDCVariantValueDialog.this, this.pidcPage.getProjectObjectBO());
    /**
     * multiple variant selection
     */
    if (subVars == null) {
      this.varSelFlag = true;
      setVarVals(variants);
      // ICDM-912
      setVarAttrWithSameVal();
      setVarAttrWithSameFlag();
    }
    /**
     * multiple sub-variant selection
     */
    else if (variants == null) {
      this.subVarSelFlag = true;
      setSubVarVals(subVars);
      // ICDM-912
      setSubVarAttrWithSameVal();
      setSubVarAttrWithSameFlag();
    }
    /**
     * Clear all the maps
     */
    this.varValMap.clear();
    this.attrUsedMap.clear();
    this.subVarValMap.clear();
    this.selPidcVer = editorPidcVer;


  }


  /**
   * Adds the attributes with same used flag in all sub-variants to the map
   */
  private void setSubVarAttrWithSameFlag() {
    this.oldAttrUsedMap.clear();
    boolean flag = false;
    for (IProjectAttribute attr : this.pidcDataHandler.getSubVariantAttributeMap().get(this.subVariant.getId())
        .values()) {
      for (PidcSubVariant var : this.subVariants) {
        IProjectAttribute varAttr = getSubVarAttr(attr, var);
        // attr can be null if it is invisible(dependent attr case) in that case this used flag check is skipped
        if (varAttr == null) {
          continue;
        }
        // if attr is present in both variants used flag is compared
        else if (varAttr.getUsedFlag().equals(attr.getUsedFlag())) {
          flag = true;
        }
        else {
          flag = false;
          break;
        }
      }
      // map will contain the attrs which has the same used flag
      if (flag) {
        this.oldAttrUsedMap.put(attr, attr.getUsedFlag());
      }
    }

  }


  /**
   * Adds the attributes with same used flag in all variants to the map
   */
  private void setVarAttrWithSameFlag() {
    this.oldAttrUsedMap.clear();
    boolean flag = false;
    for (IProjectAttribute attr : this.pidcDataHandler.getVariantAttributeMap().get(this.variant.getId()).values()) {
      for (PidcVariant var : this.variants) {
        IProjectAttribute varAttr = getVarAttr(attr, var);
        // attr can be null if it is invisible(dependent attr case) in that case this used flag check is skipped
        if (varAttr == null) {
          continue;
        }
        // if attr is present in both variants used flag is compared
        else if (varAttr.getUsedFlag().equals(attr.getUsedFlag())) {
          flag = true;
        }
        else {
          flag = false;
          break;
        }
      }
      // map will contain the attrs which has the same used flag
      if (flag) {
        this.oldAttrUsedMap.put(attr, attr.getUsedFlag());
      }
    }

  }


  /**
   * Store the attrs(along with value) which has same value in all the sub-varaints
   */
  private void setSubVarAttrWithSameVal() {
    this.subVarAttrMap.clear();
    boolean flag = false;
    for (IProjectAttribute attr : this.pidcDataHandler.getSubVariantAttributeMap().get(this.subVariant.getId())
        .values()) {
      for (PidcSubVariant var : this.subVariants) {
        IProjectAttribute subVarAttr = getSubVarAttr(attr, var);
        // attr can be null if it is invisible(dependent attr case) in that case this used flag check is skipped
        if (subVarAttr == null) {
          continue;
        }
        // value equal when both values are null
        if (isAttrValNull(attr, subVarAttr) || (isAttrValNotNull(attr, subVarAttr) &&
            (this.pidcDataHandler.getAttributeValueMap().get(subVarAttr.getValueId())
                .equals(this.pidcDataHandler.getAttributeValueMap().get(attr.getValueId()))))) {
          flag = true;
        }
        // value not equal if attr in any one varaint does not have value
        else {
          flag = false;
          break;
        }
      }
      // this map contains attrs which has same value in all sub-vars
      if (flag) {
        this.subVarAttrMap.put(attr, this.pidcDataHandler.getAttributeValueMap().get(attr.getValueId()));
      }
    }

  }


  /**
   * @param attr
   * @param subVarAttr
   * @return
   */
  private boolean isAttrValNotNull(final IProjectAttribute attr, final IProjectAttribute subVarAttr) {
    return (subVarAttr.getValue() != null) && (attr.getValue() != null);
  }


  /**
   * @param attr
   * @param subVarAttr
   * @return
   */
  private boolean isAttrValNull(final IProjectAttribute attr, final IProjectAttribute subVarAttr) {
    return (subVarAttr.getValue() == null) && (attr.getValue() == null);
  }


  /**
   * Store the attrs (along with value) which has same value in all the varaints
   */
  private void setVarAttrWithSameVal() {
    this.varAttrMap.clear();
    boolean flag = false;
    for (IProjectAttribute attr : this.pidcDataHandler.getVariantAttributeMap().get(this.variant.getId()).values()) {
      for (PidcVariant var : this.variants) {
        IProjectAttribute varAttr = getVarAttr(attr, var);

        // attr can be null if it is invisible(dependent attr case) in that case this used flag check is skipped
        if (varAttr == null) {
          continue;
        } // value not equal if attr in any one varaint does not have value
        else if (this.subVars.contains(varAttr.getName()) ||
            ((varAttr.getValue() == null) || (attr.getValue() == null))) {
          flag = false;
        }
        // value equal when both values are null // compare the value for equality
        else if (isAttrValNull(attr, varAttr) ||
            CommonUtils.isEqual(this.pidcDataHandler.getAttributeValueMap().get(varAttr.getValueId()),
                this.pidcDataHandler.getAttributeValueMap().get(attr.getValueId()))) {
          flag = true;
        }
        else {
          flag = false;
          break;
        }
      }
      // this map contains attrs which has same value in all vars
      addAttrValToMap(flag, attr);
    }
  }


  /**
   * @param flag boolean
   * @param attr IProjectAttribute
   */
  private void addAttrValToMap(final boolean flag, final IProjectAttribute attr) {
    if (flag) {
      this.varAttrMap.put(attr, this.pidcDataHandler.getAttributeValueMap().get(attr.getValueId()));
    }
  }


  /**
   * Returns the var attr
   *
   * @param attr attr
   * @param var varaint
   * @return varAttr
   */
  private IProjectAttribute getVarAttr(final IProjectAttribute attr, final PidcVariant var) {
    IProjectAttribute varAttr = null;
    for (IProjectAttribute selAttr : this.pidcDataHandler.getVariantAttributeMap().get(var.getId()).values()) {
      if (this.pidcDataHandler.getAttributeMap().get(selAttr.getAttrId())
          .compareTo(this.pidcDataHandler.getAttributeMap().get(attr.getAttrId())) == 0) {
        varAttr = selAttr;
        break;
      }
    }
    return varAttr;
  }

  /**
   * Retruns the sub-varaint attr
   *
   * @param attr attr
   * @param subVar sub-varaint
   * @return subVarAttr
   */
  private IProjectAttribute getSubVarAttr(final IProjectAttribute attr, final PidcSubVariant subVar) {
    IProjectAttribute subVarAttr = null;
    for (IProjectAttribute selAttr : this.pidcDataHandler.getSubVariantAttributeMap().get(subVar.getId()).values()) {
      if (this.pidcDataHandler.getAttributeMap().get(selAttr.getAttrId())
          .equals(this.pidcDataHandler.getAttributeMap().get(attr.getAttrId()))) {
        subVarAttr = selAttr;
        break;
      }
    }
    return subVarAttr;
  }

  /**
   * Set the sub-varaint having the largest number of attributes(including the dependent attrs which may not be present
   * in other sub-varaints)
   *
   * @param subVars variants array
   */
  private void setSubVarVals(final PidcSubVariant... subVars) {
    int attrCount = 0;
    for (PidcSubVariant pidSubVar : subVars) {
      final Collection<PidcSubVariantAttribute> attrs =
          this.pidcDataHandler.getSubVariantAttributeMap().get(pidSubVar.getId()).values();
      if (attrs.size() > attrCount) {
        attrCount = attrs.size();
        this.subVariant = pidSubVar;
      }
    }
    this.subVariants = subVars.clone();
  }

  /**
   * If any attr is present as sub-varaint in any of the var then value cannot be set for that attr. For this purpose
   * the subvar attrs are stored in a map
   *
   * @param variants variants array
   */
  private void setVarVals(final PidcVariant[] variants) {
    int attrCount = 0;
    /**
     * set subvariants present in the variants selected.
     */
    for (PidcVariant pidVar : variants) {
      // varaint having the largest number of attributes(including the dependent attrs which may not be present in other
      // varaints)
      final Collection<PidcVariantAttribute> attrs =
          this.pidcDataHandler.getVariantAttributeMap().get(pidVar.getId()).values();
      if (attrs.size() > attrCount) {
        attrCount = attrs.size();
        this.variant = pidVar;
      }
      /**
       * if any attr in a variant is present as sub-variant store in map. This data is used to prevent setting sub-var
       * attr value through multiple selection
       */
      for (PidcVariantAttribute attr : attrs) {
        if (attr.isAtChildLevel()) {
          this.subVars.add(attr.getName());
        }
      }
    }
    this.variants = variants.clone();
  }


  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set the title
    setTitle("Set Value");
    // Set the message
    setMessage("Set value for multiple attributes", IMessageProvider.INFORMATION);

    return contents;
  }


  @Override
  protected void configureShell(final Shell newShell) {
    /**
     * Set shell title
     */
    newShell.setText("Add value to attributes");

    super.configureShell(newShell);

    super.setHelpAvailable(true);
  }


  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse .swt.widgets.Composite)
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    /**
     * Create the save and cancel buttons. The save button is intially disabled.
     */
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Save", true);
    this.saveBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }


  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets .Composite)
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    // create composite
    createComposite();
    return this.top;

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
   * This method initializes composite
   */
  private void createComposite() {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    // create section
    createSection();
    this.composite.setLayoutData(gridData);
    this.section.getDescriptionControl().setEnabled(false);
  }


  /**
   * This method initializes section
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Add value to  attributes");
    // create form
    createForm();
    this.section.setClient(this.form);
  }


  /**
   * This method initializes form
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);
    this.form.getToolBarManager().update(true);
    this.form.setToolBarVerticalAlignment(SWT.UP);
    this.form.getBody().setLayout(new GridLayout());
    // create the pidc attr table
    createPIDCAttrTableViewer();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {

    // Get existing grp attr values for reset during grp attr update
    // key - var id or subvar id, value - old value of grp attr
    Map<Long, Long> pidcElementValMap = new HashMap<>();
    // if grp attr is edited, store existing attrs
    if (!this.grpAttrEditList.isEmpty()) {
      for (IProjectAttribute iProjectAttribute : this.grpAttrEditList) {
        if (this.varSelFlag) {
          PidcVariantAttribute varAttr = (PidcVariantAttribute) iProjectAttribute;
          Map<Long, PidcVariantAttribute> variantAttrMap =
              this.pidcDataHandler.getVariantAttributeMap().get(varAttr.getVariantId());
          if ((variantAttrMap != null) && !variantAttrMap.isEmpty() &&
              (variantAttrMap.get(varAttr.getAttrId()) != null)) {
            pidcElementValMap.put(varAttr.getVariantId(), variantAttrMap.get(varAttr.getAttrId()).getValueId());
          }
        }
        else if (this.subVarSelFlag) {
          PidcSubVariantAttribute subVarAttr = (PidcSubVariantAttribute) iProjectAttribute;
          Map<Long, PidcSubVariantAttribute> subVariantAttrMap =
              this.pidcDataHandler.getSubVariantAttributeMap().get(subVarAttr.getSubVariantId());
          if ((subVariantAttrMap != null) && !subVariantAttrMap.isEmpty() &&
              (subVariantAttrMap.get(subVarAttr.getAttrId()) != null)) {
            pidcElementValMap.put(subVarAttr.getSubVariantId(), this.pidcDataHandler.getSubVariantAttributeMap()
                .get(subVarAttr.getSubVariantId()).get(subVarAttr.getAttrId()).getValueId());
          }
        }
      }
    }


    /**
     * if multiple variants are selected
     */
    if (this.varSelFlag) {
      /**
       * set the attr value in all variants , if any of attr is sub-varaint value is not set for it
       */
      setVarValue();
      /**
       * set the attr used flag status in all variants , if any of attr is sub-varaint it is skipped
       */
      setVarUsedFlag();
      /**
       * If there are any sub-var attrs which is skipped , user is informed by displaying a info dialog
       */
      isSubVarsAvail();
    }
    else if (this.subVarSelFlag) {
      /**
       * set the attr value in all sub-variants
       */
      setSubVarVal();
      /**
       * set the attr used flag status in all sub-variants
       */
      setSubVarUsedFlag();

    }
    super.okPressed();


    if (!this.grpAttrEditList.isEmpty()) {
      PIDCGroupedAttrActionSet actionSet =
          new PIDCGroupedAttrActionSet(this.pidcDataHandler, this.pidcPage.getPidcVersionBO());
      List<GroupdAttrPredefAttrModel> grpAttrValList = actionSet.getAllPidcGrpAttrVal(this.selPidcVer,
          this.pidcPage.getPidcVersionBO().getAttributesAll(), this.grpAttrEditList);
      if ((null != grpAttrValList) && !grpAttrValList.isEmpty()) {
        PIDCGrpdAttrChangesDialog grpdAttrChangesDialog = new PIDCGrpdAttrChangesDialog(
            Display.getDefault().getActiveShell(), this.pidcPage, null, null, this.selPidcVer, true,
            this.pidcPage.getPidcVersionBO().getAttributesAll(), null, grpAttrValList, pidcElementValMap);


        grpdAttrChangesDialog.open();
      }
    }
  }

  /**
   * set the attribute used state to all the selected variants
   */
  private void setSubVarUsedFlag() {
    final Set<IProjectAttribute> attrsUsed = this.attrUsedMap.keySet();
    /**
     * Iterate all sub-var attrs for which used-flag is set using this dialog
     */
    for (IProjectAttribute attrVal : attrsUsed) {
      for (PidcSubVariant pidcSubVar : this.subVariants) {
        /**
         * Set the selc sub-variant
         */
        setPidcSubVariant(pidcSubVar);
        final Collection<PidcSubVariantAttribute> svarAttrs =
            this.pidcDataHandler.getSubVariantAttributeMap().get(pidcSubVar.getId()).values();
        for (PidcSubVariantAttribute sAttr : svarAttrs) {
          /**
           * Set the used flag for attr in each of the sub-varaint
           */
          if (sAttr.compareTo(attrVal) == 0) {

            final String valueType = this.attrUsedMap.get(attrVal);
            // used-no flag
            if (valueType.equalsIgnoreCase(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType())) {
              PIDCVariantValueDialog.this.pageEditUtil.editProjectAttributeNotUsedInfo(sAttr, true);
            }
            // used-yes flag
            else if (valueType.equalsIgnoreCase(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType())) {
              PIDCVariantValueDialog.this.pageEditUtil.editProjectAttributeUsedInfo(sAttr, true);
            }
            // used-not defined flag
            else if (valueType.equalsIgnoreCase(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType())) {
              PIDCVariantValueDialog.this.pageEditUtil.editProjectAttributeNotDefinedFlag(sAttr);
            }
            break;
          }
        }
      }
    }
  }


  /**
   * set the attribute value to all the selected sub-varaints
   */
  private void setSubVarVal() {
    this.pageEditUtil.editSubVarAttrValue(this.subVarValMap, this.partNumMap, this.specLinkMap, this.commMap);
  }

  /**
   * Map which stores the attrs for which the used flag is set using this dialog
   *
   * @return the attrUsedMap attr-flag(yes,no,not-def)
   */
  public Map<IProjectAttribute, String> getAttrUsedMap() {
    return this.attrUsedMap;
  }

  /**
   * Dialog to notify users that sub-variant values which has been set through this dialog wont be set
   */
  private void isSubVarsAvail() {
    if (this.selSubVars.size() != 0) {
      String attrName = "";
      for (String attr : this.selSubVars) {
        attrName += attr + ",";
      }
      CDMLogger.getInstance().infoDialog(
          "The following attributes " + attrName +
              " are not updated as they are present as subvariant in certain variants",
          ApicUiConstants.PID_DETAILS_TREE_VIEW);
    }
  }


  /**
   * set the attribute used state to all the selected variants
   */
  private void setVarUsedFlag() {
    final Set<IProjectAttribute> attrsUsed = this.attrUsedMap.keySet();
    for (IProjectAttribute attrVal : attrsUsed) {
      /**
       * Iterate all var attrs for which used flag is set using this dialog
       */
      for (PidcVariant pidcVar : this.variants) {
        setPidcVariant(pidcVar);
        final Collection<PidcVariantAttribute> varAttrs =
            this.pidcDataHandler.getVariantAttributeMap().get(pidcVar.getId()).values();
        for (PidcVariantAttribute vAttr : varAttrs) {
          /**
           * Used flag of attr is set in each variant
           */
          if (vAttr.compareTo(attrVal) == 0) {
            // skip setting the used state if attribute is present as sub-variant in any selected variant
            if (this.subVars.contains(attrVal.getName())) {
              // add the sub-variant attributes, for which used info is set using this dialog, to notify the user
              // since
              // the values wont be set.
              this.selSubVars.add(attrVal.getName());

            }
            else {
              final String valueType = this.attrUsedMap.get(attrVal);
              // used-no flag
              if (valueType.equalsIgnoreCase(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType())) {
                PIDCVariantValueDialog.this.pageEditUtil.editProjectAttributeNotUsedInfo(vAttr, true);
              }
              // used-yes flag
              else if (valueType.equalsIgnoreCase(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType())) {
                PIDCVariantValueDialog.this.pageEditUtil.editProjectAttributeUsedInfo(vAttr, true);
              }
              // used-not def flag
              else if (valueType.equalsIgnoreCase(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType())) {
                PIDCVariantValueDialog.this.pageEditUtil.editProjectAttributeNotDefinedFlag(vAttr);
              }
            }
            break;
          }
        }
      }
    }
  }


  /**
   * set the attribute value to all the selected varaints
   */
  private void setVarValue() {

    Iterator<Entry<IProjectAttribute, AttributeValue>> iterator = this.varValMap.entrySet().iterator();

    while (iterator.hasNext()) {
      Entry<IProjectAttribute, AttributeValue> entry = iterator.next();
      /**
       * Iterate all var attrs for which value is set using this dialog
       */
      for (PidcVariant pidcVar : this.variants) {
        setPidcVariant(pidcVar);
        final Collection<PidcVariantAttribute> varAttrs =
            this.pidcDataHandler.getVariantAttributeMap().get(pidcVar.getId()).values();
        boolean removed = false;
        for (PidcVariantAttribute vAttr : varAttrs) {
          if (vAttr.compareTo(entry.getKey()) == 0) {
            // skip setting the value if attribute is present as sub-variant in any selected variant
            if (this.subVars.contains(entry.getKey().getName())) {
              // add the sub-variant attributes, for which value is set using this dialog, to notify the user since
              // the
              // values wont be set.
              this.selSubVars.add(entry.getKey().getName());

              // remove from map, as they need not be updated
              iterator.remove();
              removed = true;
              this.partNumMap.remove(entry.getKey());
              this.specLinkMap.remove(entry.getKey());
              this.commMap.remove(entry.getKey());
            }
            break;
          }

        }
        if (removed) {
          break;
        }
      }
    }


    // save the value for the attr
    this.pageEditUtil.editVariantAttrValue(this.varValMap, this.partNumMap, this.specLinkMap, this.commMap);
  }

  /**
   * This method creates PIDC TableViewer
   */
  private void createPIDCAttrTableViewer() {
    createPIDCAttrTable();
  }

  /**
   * This method creates PIDC TableViewer Columns
   *
   * @param attrMap
   */
  private void createdPIDCAttrTabViewerColumns(final Map<Long, IProjectAttribute> attrMap) {

    // Create PIDC Attribute name Viewer Column
    createPIDCAttrNameColViewer();
    // Create PIDC Attribute description Viewer Column
    createPIDCAttrDescColViewer();
    // Create PIDC Attribute used Viewer Column
    createPIDCAttrUsedColViewer();
    // Create PIDC Attribute value Viewer Column
    createPIDCAttrValColViewer(attrMap);
    // Create PIDC Attribute value Edit Viewer Column
    createPIDCAttrValEditColViewer();
    createPIDCAttrPartNumColViewer();
    createPIDCAttrCSpecLinkColViewer();
    createPIDCAttrCommColViewer();

  }

  /**
   * set the spec Link
   */
  private void createPIDCAttrCSpecLinkColViewer() {
    final GridViewerColumn specLinkCol =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.pidcAttrTabViewer, "Specification (Link)", 175);
    specLinkCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        IProjectAttribute pidcAttr = (IProjectAttribute) element;
        String specLinkInUI = PIDCVariantValueDialog.this.specLinkMap.get(pidcAttr);
        if (specLinkInUI != null) {
          return specLinkInUI;
        }
        String specLinkInDB = PIDCVariantValueDialog.this.pageEditUtil.getTextForSpecLink((IProjectAttribute) element,
            PIDCVariantValueDialog.this.variants, PIDCVariantValueDialog.this.subVariants);
        PIDCVariantValueDialog.this.oldSpecLinkMap.put((IProjectAttribute) element, specLinkInDB);
        return specLinkInDB;
      }

    });
    specLinkCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        specLinkCol.getColumn(), ApicUiConstants.COLUMN_INDEX_9, this.valtabSorter, this.pidcAttrTabViewer));
  }

  /**
   * set the Attr Comments
   */
  private void createPIDCAttrCommColViewer() {
    final GridViewerColumn commCol =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.pidcAttrTabViewer, "Comments", 175);
    commCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        IProjectAttribute pidcAttr = (IProjectAttribute) element;
        String commentsInUI = PIDCVariantValueDialog.this.commMap.get(pidcAttr);
        if (commentsInUI != null) {
          return commentsInUI;
        }
        String commentsInDB = PIDCVariantValueDialog.this.pageEditUtil.getTextForComments((IProjectAttribute) element,
            PIDCVariantValueDialog.this.variants, PIDCVariantValueDialog.this.subVariants);
        PIDCVariantValueDialog.this.oldCommentMap.put((IProjectAttribute) element, commentsInDB);
        return commentsInDB;
      }
    });
    commCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(commCol.getColumn(),
        ApicUiConstants.COLUMN_INDEX_10, this.valtabSorter, this.pidcAttrTabViewer));
  }

  /**
   * create the column for the part number
   */
  private void createPIDCAttrPartNumColViewer() {
    final GridViewerColumn partCol =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.pidcAttrTabViewer, "Part Number", 175);
    partCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        IProjectAttribute pidcAttr = (IProjectAttribute) element;
        String partNumInUI = PIDCVariantValueDialog.this.partNumMap.get(pidcAttr);
        if (partNumInUI != null) {
          return partNumInUI;
        }
        String partNumInDb = PIDCVariantValueDialog.this.pageEditUtil.getTextForPartNum((IProjectAttribute) element,
            PIDCVariantValueDialog.this.variants, PIDCVariantValueDialog.this.subVariants);
        PIDCVariantValueDialog.this.oldPartNumMap.put((IProjectAttribute) element, partNumInDb);
        return partNumInDb;
      }
    });
    partCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(partCol.getColumn(),
        ApicUiConstants.COLUMN_INDEX_8, this.valtabSorter, this.pidcAttrTabViewer));
  }

  /**
   * This method creates PIDC attribute used table viewer column
   */
  private void createPIDCAttrUsedColViewer() {
    // create a group for flags
    this.usedColumnGroup = new GridColumnGroup(this.pidcAttrTabViewer.getGrid(), SWT.TOGGLE);
    this.usedColumnGroup.setText(Messages.getString(IMessageConstants.USED_LABEL));
    // attr-unknown flag(???)
    createAttrUnKnownInfoColumn(this.usedColumnGroup);
    // attr-no flag
    createAttrUsedInfoNoViewerColumn(this.usedColumnGroup);
    // attr-yes flag
    createAttrUsedInfoYesViewerColumn(this.usedColumnGroup);
    // attr-different flag
    createAttrUsedInfoDiffViewerColumn(this.usedColumnGroup);
    // attr-summary flag
    createAttrUsedSummaryViewerColumn(this.usedColumnGroup);
  }

  // ICDM-912
  /**
   * This method creates PIDC attribute used no information column
   *
   * @param usedColumnGroup group
   */
  private void createAttrUsedInfoDiffViewerColumn(final GridColumnGroup usedColumnGroup) {
    final GridColumn attrUsedDiffColumn =
        GridColumnUtil.getInstance().createGridColumn(usedColumnGroup, SWT.CHECK, 35, "Diff", false);
    final GridViewerColumn attrDiffInfoViewerCol = new GridViewerColumn(this.pidcAttrTabViewer, attrUsedDiffColumn);
    attrDiffInfoViewerCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * Update the column {@inheritDoc}
       */
      @Override
      public void update(final ViewerCell cell) {
        final Object element = cell.getElement();
        if (element instanceof IProjectAttribute) {
          setFlagForAttr(cell, (IProjectAttribute) element);
        }
      }
    });
    attrDiffInfoViewerCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrDiffInfoViewerCol.getColumn(), DIFF_COL, this.valtabSorter, this.pidcAttrTabViewer));
  }

  /**
   * Set the flag for the attr
   *
   * @param cell
   * @param prjAttr
   */
  private void setFlagForAttr(final ViewerCell cell, final IProjectAttribute prjAttr) {
    if (PIDCVariantValueDialog.this.oldAttrUsedMap.containsKey(prjAttr) ||
        ((PIDCVariantValueDialog.this.attrUsedMap != null) &&
            (PIDCVariantValueDialog.this.attrUsedMap.containsKey(prjAttr)))) {
      final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
      gridItem.setChecked(cell.getVisualIndex(), false);
      gridItem.setCheckable(cell.getVisualIndex(), false);
    }
    else {
      final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
      gridItem.setChecked(cell.getVisualIndex(), true);
      gridItem.setCheckable(cell.getVisualIndex(), false);
    }
  }

  /**
   * Create attribute summary of the used-flag column
   *
   * @param usedColGrp group
   */
  private void createAttrUsedSummaryViewerColumn(final GridColumnGroup usedColGrp) {
    final GridColumn attrUsedSummaryColumn = GridColumnUtil.getInstance().createGridColumn(usedColGrp, SWT.NONE, 75,
        Messages.getString(IMessageConstants.SUMMARY_LABEL), false, true);

    GridViewerColumn attrSummaryViewerCol = new GridViewerColumn(this.pidcAttrTabViewer, attrUsedSummaryColumn);
    attrSummaryViewerCol.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        return getAttrVal(element);
      }

    });
    // ICDM-1023
    attrSummaryViewerCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrSummaryViewerCol.getColumn(), SUMMARY_COL, this.valtabSorter, this.pidcAttrTabViewer));

  }

  /**
   * @param prjAttr
   * @return
   */
  private String getAttrVal(final Object prjAttr) {
    String attrVal = "";
    final Set<IProjectAttribute> attrs = PIDCVariantValueDialog.this.attrUsedMap.keySet();
    for (IProjectAttribute attr : attrs) {
      if (attr.compareTo((IProjectAttribute) prjAttr) == 0) {
        attrVal = PIDCVariantValueDialog.this.attrUsedMap.get(attr);
      }
    }
    // ICDM-912
    if (attrVal.isEmpty()) {
      if (PIDCVariantValueDialog.this.oldAttrUsedMap.containsKey(prjAttr)) {
        String flag = PIDCVariantValueDialog.this.oldAttrUsedMap.get(prjAttr);
        if (flag.equalsIgnoreCase(ApicConstants.USED_YES_DISPLAY)) {
          attrVal = ApicConstants.USED_YES_DISPLAY;
        }
        else if (flag.equalsIgnoreCase(ApicConstants.USED_NO_DISPLAY)) {
          attrVal = ApicConstants.USED_NO_DISPLAY;
        }
        else if (flag.equalsIgnoreCase(ApicConstants.USED_NOTDEF_DISPLAY)) {
          attrVal = ApicConstants.USED_NOTDEF_DISPLAY;
        }
      }
      else {
        attrVal = "DIFF";
      }
    }
    return attrVal;
  }

  /**
   * This method creates PIDC attribute used no information column
   *
   * @param usedColumnGroup group
   */
  private void createAttrUsedInfoNoViewerColumn(final GridColumnGroup usedColumnGroup) {
    final GridColumn attrUsedNoColumn = GridColumnUtil.getInstance().createGridColumn(usedColumnGroup, SWT.CHECK, 35,
        Messages.getString(IMessageConstants.NO_LABEL), false);
    final GridViewerColumn attrNotUsedInfoViewerCol = new GridViewerColumn(this.pidcAttrTabViewer, attrUsedNoColumn);
    attrNotUsedInfoViewerCol.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public void update(final ViewerCell cell) {
        final Object element = cell.getElement();
        setUsedFlgUI(cell, element, ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType());
      }
    });
    // set editing support PIDC attribute used info no columnviewer
    attrNotUsedInfoViewerCol.setEditingSupport(new CheckEditingSupport(attrNotUsedInfoViewerCol.getViewer()) {

      @Override
      public void setValue(final Object arg0, final Object arg1) {

        /**
         * Add the attrs to attr-used map for which the no used flag is set
         */
        if ((arg0 instanceof PidcVariantAttribute) || (arg0 instanceof PidcSubVariantAttribute)) {

          setAttrUsedMap((IProjectAttribute) arg0, ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType());

        }

      }
    });
    // ICDM-1023
    attrNotUsedInfoViewerCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        attrNotUsedInfoViewerCol.getColumn(), NOT_USED_COL, this.valtabSorter, this.pidcAttrTabViewer));
  }

  /**
   * This method creates PIDC attribute used yes information column
   *
   * @param usedColumnGroup group
   */
  private void createAttrUsedInfoYesViewerColumn(final GridColumnGroup usedColumnGroup) {
    final GridColumn attrUsedYesColumn = GridColumnUtil.getInstance().createGridColumn(usedColumnGroup, SWT.CHECK, 35,
        Messages.getString(IMessageConstants.YES_LABEL), false);
    final GridViewerColumn attrUsedInfoViewerCol = new GridViewerColumn(this.pidcAttrTabViewer, attrUsedYesColumn);
    attrUsedInfoViewerCol.setLabelProvider(new ColumnLabelProvider() {


      @Override
      public void update(final ViewerCell cell) {
        final Object element = cell.getElement();
        setUsedFlgUI(cell, element, ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType());
      }
    });
    // set editing support PIDC attribute used info yes columnviewer
    attrUsedInfoViewerCol.setEditingSupport(new CheckEditingSupport(attrUsedInfoViewerCol.getViewer()) {

      /**
       * Add the attrs to attr-used map for which the yes used flag is set
       */
      @Override
      public void setValue(final Object arg0, final Object arg1) {
        if ((arg0 instanceof PidcVariantAttribute) || (arg0 instanceof PidcSubVariantAttribute)) {
          setAttrUsedMap((IProjectAttribute) arg0, ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType());
        }

      }
    });
    // ICDM-1023
    attrUsedInfoViewerCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        attrUsedInfoViewerCol.getColumn(), USED_INFO_COL, this.valtabSorter, this.pidcAttrTabViewer));
  }


  /**
   * This method creates PIDC attribute used unknown information column
   *
   * @param usedColumnGroup group
   */
  public void createAttrUnKnownInfoColumn(final GridColumnGroup usedColumnGroup) {
    final GridColumn attrUnknownInfoColumn = GridColumnUtil.getInstance().createGridColumn(usedColumnGroup, SWT.CHECK,
        35, Messages.getString(IMessageConstants.QUESTION_LABEL), false);
    GridViewerColumn attrUnknownInfoCol = new GridViewerColumn(this.pidcAttrTabViewer, attrUnknownInfoColumn);
    attrUnknownInfoCol.setLabelProvider(new ColumnLabelProvider() {


      @Override
      public void update(final ViewerCell cell) {
        final Object element = cell.getElement();
        setUsedFlgUI(cell, element, ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType());
      }

    });
    // set editing support PIDC attribute used info unknown columnviewer
    attrUnknownInfoCol.setEditingSupport(new CheckEditingSupport(attrUnknownInfoCol.getViewer()) {

      @Override
      public void setValue(final Object arg0, final Object arg1) {
        /**
         * Add the attrs to attr-used map for which the not defined used flag is set
         */
        if ((arg0 instanceof PidcVariantAttribute) || (arg0 instanceof PidcSubVariantAttribute)) {
          setAttrUsedMap((IProjectAttribute) arg0, ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType());
        }
      }
    });
    // ICDM-1023
    attrUnknownInfoCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        attrUnknownInfoCol.getColumn(), ATTR_UNKNOWN_COL, this.valtabSorter, this.pidcAttrTabViewer));
  }

  /**
   * Add the var attr and its value to the map
   *
   * @param attr attibute
   * @param attrVal attr value
   * @param desc
   * @param specLink
   * @param partNumber
   */
  public void setVarValMap(final IProjectAttribute selVar, final AttributeValue attrVal, final String partNumber,
      final String specLink, final String desc) {

    this.varValMap.put(selVar, attrVal);
    this.partNumMap.put(selVar, partNumber);
    this.specLinkMap.put(selVar, specLink);
    this.commMap.put(selVar, desc);
    if (!"".equalsIgnoreCase(attrVal.getName()) && !"ERROR".equalsIgnoreCase(attrVal.getName())) {
      setAttrUsedMap(selVar, ApicConstants.USED_YES_DISPLAY);
    }


  }

  /**
   * Add the sub-var attr and its value to the map
   *
   * @param attr sub-var
   * @param attrVal attr value
   * @param desc
   * @param specLink
   * @param partNumber
   */
  public void setSubVarValMap(final IProjectAttribute selVar, final AttributeValue attrVal, final String partNumber,
      final String specLink, final String desc) {

    this.subVarValMap.put(selVar, attrVal);
    this.partNumMap.put(selVar, partNumber);
    this.specLinkMap.put(selVar, specLink);
    this.commMap.put(selVar, desc);
    if (!"".equalsIgnoreCase(attrVal.getName()) && !"ERROR".equalsIgnoreCase(attrVal.getName())) {
      setAttrUsedMap(selVar, ApicConstants.USED_YES_DISPLAY);
    }

  }


  /**
   * Map which has var attrs and its value set using this dialog
   *
   * @return the varMap
   */
  public Map<IProjectAttribute, AttributeValue> getVarValMap() {
    return this.varValMap;
  }

  /**
   * Map which has sub-var attrs and its value set using this dialog
   *
   * @return the varMap
   */
  public Map<IProjectAttribute, AttributeValue> getSubVarValMap() {
    return this.subVarValMap;
  }


  /**
   * This method creates PIDC attribute value table viewer column
   *
   * @param attrMap
   */
  private void createPIDCAttrValColViewer(final Map<Long, IProjectAttribute> attrMap) {
    final GridViewerColumn attrValColumn = GridViewerColumnUtil.getInstance().createGridViewerColumn(
        this.pidcAttrTabViewer, Messages.getString(IMessageConstants.VALUE_LABEL), ATTR_VAL_COL_WIDTH);
    attrValColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * Set the value of the attrs in the value col of the table {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        boolean flag;
        Map<IProjectAttribute, AttributeValue> attrsMap = setNewAttrMap();
        // ICDM-912
        Map<IProjectAttribute, AttributeValue> attrsValMap = setOldAttrMap();
        String val = CommonUIConstants.DISP_TEXT_USE_CUR_VAL;
        // ICDM-912
        if (!attrsValMap.isEmpty() && (attrsMap != null)) {
          flag = checkAttr(element, attrsMap);
          // ICDM-912
          if (!flag) {
            val = displayValueUI(element, attrsValMap);
          }
        }
        if ((PIDCVariantValueDialog.this.dialog != null) && (attrsMap != null)) {
          val = getNewAtrrVal(element, attrsMap);
        }

        PIDCVariantValueDialog.this.oldAttrValMap.put((IProjectAttribute) element, val);
        return val;
      }


      /**
       * Set the colour for the attr values {@inheritDoc}
       */
      @Override
      public Color getForeground(final Object element) {
        return getForegrndColorForAttrVal(attrMap, element);
      }
    });
    // ICDM-1023
    attrValColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        attrValColumn.getColumn(), ApicUiConstants.COLUMN_INDEX_7, this.valtabSorter, this.pidcAttrTabViewer));
  }

  /**
   * This map contains the attrs and its old values
   *
   * @return map
   */
  private Map<IProjectAttribute, AttributeValue> setOldAttrMap() {
    Map<IProjectAttribute, AttributeValue> attrsValMap;
    if (PIDCVariantValueDialog.this.variants == null) {
      attrsValMap = PIDCVariantValueDialog.this.subVarAttrMap;
    }
    else {
      attrsValMap = PIDCVariantValueDialog.this.varAttrMap;
    }
    return attrsValMap;
  }

  /**
   * this map contains the attrs and its new values set using this dialog
   *
   * @return map
   */
  private Map<IProjectAttribute, AttributeValue> setNewAttrMap() {
    Map<IProjectAttribute, AttributeValue> attrsMap = null;
    if (PIDCVariantValueDialog.this.varValMap.size() == 0) {
      attrsMap = PIDCVariantValueDialog.this.subVarValMap;
    }
    else if (PIDCVariantValueDialog.this.subVarValMap.size() == 0) {
      attrsMap = PIDCVariantValueDialog.this.varValMap;
    }
    return attrsMap;
  }

  /**
   * @param attrMap
   * @param element
   * @return
   */
  private Color getForegrndColorForAttrVal(final Map<Long, IProjectAttribute> attrMap, final Object element) {

    if (element instanceof IProjectAttribute) {
      return getProjAttrColor(attrMap, element);
    }
    // Fix made to directly send the black colour
    return Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
  }


  /**
   * @param attrMap
   * @param element
   */
  private Color getProjAttrColor(final Map<Long, IProjectAttribute> attrMap, final Object element) {
    final IProjectAttribute pidcAttr = (IProjectAttribute) element;
    Color projAttrColor = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
    if (pidcAttr.getValue() != null) {
      final AttributeValue attrValue =
          PIDCVariantValueDialog.this.pidcDataHandler.getAttributeValueMap().get(pidcAttr.getValueId());
      // Red colour if its deleted
      if (CommonUtils.isNotNull(attrValue)) {
        if (attrValue.isDeleted()) {
          projAttrColor = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        }
        // Magenta colour if the value is invalid
        if (!((new AttributeValueClientBO(attrValue)).isValidValue(attrMap, PIDCVariantValueDialog.this.pidcDataHandler,
            PIDCVariantValueDialog.this.pidcDataHandler.getAttrDependenciesMapForAllAttr().get(pidcAttr.getAttrId())
                .get(attrValue.getId())))) {
          projAttrColor = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_MAGENTA);
        }
      }
    }
    // Blue if the value is link
    if (getProjAttrHandler(pidcAttr).isHyperLinkValue() && (!pidcAttr.isAttrHidden())) {
      projAttrColor = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
    }
    return projAttrColor;
  }

  /**
   * This method creates PIDC attribute description table viewer column
   */
  private void createPIDCAttrDescColViewer() {
    final GridViewerColumn attrDescColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.pidcAttrTabViewer, Messages.getString(IMessageConstants.DESCRIPTION_LABEL), 175);
    ColumnViewerToolTipSupport.enableFor(this.pidcAttrTabViewer, ToolTip.NO_RECREATE);
    attrDescColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getToolTipText(final Object element) {
        final IProjectAttribute item = (IProjectAttribute) element;
        return PIDCVariantValueDialog.this.pidcDataHandler.getAttributeMap().get(item.getAttrId()).getDescription();
      }

      @Override
      public String getText(final Object element) {

        final IProjectAttribute item = (IProjectAttribute) element;
        return PIDCVariantValueDialog.this.pidcDataHandler.getAttributeMap().get(item.getAttrId()).getDescription();
      }
    });
    // ICDM-1023
    attrDescColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrDescColumn.getColumn(), 1, this.valtabSorter, this.pidcAttrTabViewer));
  }

  /**
   * This method creates PIDC attribute name table viewer column
   */
  private void createPIDCAttrNameColViewer() {
    final GridViewerColumn attrNameColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.pidcAttrTabViewer, Messages.getString(IMessageConstants.ATTRIBUTE_LABEL), 175);
    attrNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof IProjectAttribute) {
          final IProjectAttribute item = (IProjectAttribute) element;
          return item.getName();
        }
        return "";
      }
    });
    // ICDM-1023
    attrNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrNameColumn.getColumn(), 0, this.valtabSorter, this.pidcAttrTabViewer));
  }

  /**
   * This method created PIDC attribute TableViewer
   */
  private void createPIDCAttrTable() {
    this.pidcAttrTabViewer =
        new GridTableViewer(this.form.getBody(), SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL

        );

    this.pidcAttrTabViewer.setContentProvider(new ArrayContentProvider());
    this.pidcAttrTabViewer.getGrid().setLayoutData(GridDataUtil.getInstance().getGridData());

    this.pidcAttrTabViewer.getGrid().setLinesVisible(true);
    this.pidcAttrTabViewer.getGrid().setHeaderVisible(true);
    ConcurrentMap<Long, IProjectAttribute> attrMap = new ConcurrentHashMap<>();
    /**
     * Set the table input to attrs map of variant/sub-variant
     */
    if (this.varSelFlag) {
      attrMap.putAll(this.pidcDataHandler.getVariantAttributeMap().get(this.variant.getId()));
      attrMap.keySet().removeAll(this.pidcDataHandler.getVariantInvisbleAttributeMap().get(this.variant.getId()));
    }
    else if (this.subVarSelFlag) {
      attrMap.putAll(this.pidcDataHandler.getSubVariantAttributeMap().get(this.subVariant.getId()));
      attrMap.keySet().removeAll(this.pidcDataHandler.getSubVariantInvisbleAttributeMap().get(this.subVariant.getId()));
    }

    // ICDM-1023
    this.valtabSorter =
        new PIDCVariantValTabSorter(PIDCVariantValueDialog.this, this.partNumMap, this.specLinkMap, this.commMap);
    createdPIDCAttrTabViewerColumns(attrMap);

    this.pidcAttrTabViewer.setInput(attrMap.values());
    // ICDM-1023
    this.pidcAttrTabViewer.setComparator(this.valtabSorter);
    /**
     * Add mouse listeners to show hand symbol when moved over link
     */
    addMouseDownListener();
    addMouseMoveListener();
  }

  /**
   * This method creates PIDC attribute value edit column
   */
  private void createPIDCAttrValEditColViewer() {
    final GridViewerColumn attrValEditColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.pidcAttrTabViewer, 25);
    attrValEditColumn.setLabelProvider(new PIDCAttrValEditColLabelProvider());
  }


  /**
   * Add mouse down listener to the pidc attribute value edit column
   */
  private void addMouseDownListener() {
    this.pidcAttrTabViewer.getGrid().addMouseListener(new MouseAdapter() {

      @Override
      public void mouseDown(final MouseEvent event) {
        final int columnIndex =
            GridTableViewerUtil.getInstance().getTabColIndex(event, PIDCVariantValueDialog.this.pidcAttrTabViewer);
        /*
         * If used column group is expanded the value column index is 6 else if user clicks to show the summary column
         * then value column index is 7
         */
        final int usedColIndex = PIDCVariantValueDialog.this.usedColumnGroup.getExpanded()
            ? ApicUiConstants.COLUMN_INDEX_6 : ApicUiConstants.COLUMN_INDEX_7;

        if ((columnIndex == usedColIndex) || (columnIndex == PIDC_ATTR_VAL_EDIT_COL_INDEX)) {
          final Point point = new Point(event.x, event.y);
          // Determine which row was selected
          final GridItem item = PIDCVariantValueDialog.this.pidcAttrTabViewer.getGrid().getItem(point);
          if ((item != null) && !item.isDisposed()) {
            final Object data = item.getData();
            final IProjectAttribute pidcAttr = (IProjectAttribute) data;
            AbstractProjectAttributeBO handler = getProjAttrHandler(pidcAttr);

            if ((columnIndex == usedColIndex) && !handler.isHyperLinkValue()) {
              return;
            }
            checkForPredefAttr(columnIndex, point, item);
          }
        }
      }
    });
  }

  /**
   * @param pidcAttr
   * @return
   */
  private AbstractProjectAttributeBO getProjAttrHandler(final IProjectAttribute pidcAttr) {

    AbstractProjectObjectBO projObjBO = new ProjectHandlerInit(PIDCVariantValueDialog.this.selPidcVer,
        new ProjectAttributeUtil()
            .getProjectObject(PIDCVariantValueDialog.this.pidcPage.getProjectObjectBO().getPidcDataHandler(), pidcAttr),
        PIDCVariantValueDialog.this.pidcPage.getProjectObjectBO().getPidcDataHandler(),
        new ProjectAttributeUtil().getProjectObjectLevel(pidcAttr)).getProjectObjectBO();
    return new ProjectAttributeUtil().getProjectAttributeHandler(pidcAttr, projObjBO);
  }

  /**
   * @param columnIndex
   * @param point
   * @param item
   */
  private void checkForPredefAttr(final int columnIndex, final Point point, final GridItem item) {
    // Determine which column was selected
    for (int i = 0, n = PIDCVariantValueDialog.this.pidcAttrTabViewer.getGrid().getColumnCount(); i < n; i++) {
      final Rectangle rect = item.getBounds(i);
      if (rect.contains(point)) {
        if (!PIDCVariantValueDialog.this.pidcPage.getPidcVersionBO().getPredefAttrGrpAttrMap()
            .containsKey(getSelectedPIDCAttr(point))) {
          editTabItem(columnIndex, point, item.getText(7));
          break;
        }
        CDMLogger.getInstance().infoDialog("Predefined Attribute can not be edited", Activator.PLUGIN_ID);
        break;
      }
    }
  }

  /**
   * Value column to open the set value dialog
   *
   * @param columnIndex deines gridviewer column index
   * @param string
   */
  private void editTabItem(final int columnIndex, final Point point, final String dispStr) {
    // Edit PIDC attribute value column index is 3
    final IProjectAttribute editableAttr = getSelectedPIDCAttr(point);
    CommonActionSet actionSet = new CommonActionSet();
    if ((columnIndex == PIDC_ATTR_VAL_EDIT_COL_INDEX) && this.pidcPage.getProjectObjectBO().isModifiable()) {
      if (editableAttr == null) {
        CDMLogger.getInstance().info(ApicUiConstants.EDIT_NOT_ALLOWED, Activator.PLUGIN_ID);
      }
      else {
        /**
         * Open the value set dialog based on var/sub-var attrs
         */
        if (this.varSelFlag) {
          // New Change tp ass the List of varaints
          this.dialog =
              new PIDCAttrValueEditDialog(editableAttr, true, PIDCVariantValueDialog.this, dispStr, this.pidcPage);
        }
        else if (this.subVarSelFlag) {
          // New Change tp ass the List of Sub -Variants
          this.dialog =
              new PIDCAttrValueEditDialog(editableAttr, false, PIDCVariantValueDialog.this, dispStr, this.pidcPage);
        }
        this.dialog.open();
      }
    }
    /*
     * If used column group is expanded the value column index is 6 else if user clicks to show the summary column then
     * value column index is 7
     */

    else if ((columnIndex == ApicUiConstants.COLUMN_INDEX_5) || (columnIndex == ApicUiConstants.COLUMN_INDEX_6)) {
      // ICDM-2529
      actionSet.openLink(editableAttr.getValue());
    }
    /**
     * Open the link based on its type
     */
    else if (columnIndex == ApicUiConstants.COLUMN_INDEX_10) {
      // ICDM-2529
      actionSet.openLink(editableAttr.getSpecLink());
    }

  }

  private void addMouseMoveListener() {
    this.pidcAttrTabViewer.getGrid().addMouseMoveListener(this::mouseListenerAction);
  }


  /**
   * @param event
   */
  private void mouseListenerAction(final MouseEvent event) {
    final ViewerCell cell = PIDCVariantValueDialog.this.pidcAttrTabViewer.getCell(new Point(event.x, event.y));
    boolean changed = false;
    if (cell != null) {
      final Object data = cell.getElement();
      if (data instanceof IProjectAttribute) {
        final IProjectAttribute pidcAttr = (IProjectAttribute) data;

        // Check for hyper link value & PIDCAttribute value column

        AbstractProjectObjectBO projObjBO = new ProjectHandlerInit(PIDCVariantValueDialog.this.selPidcVer,
            new ProjectAttributeUtil().getProjectObject(
                PIDCVariantValueDialog.this.pidcPage.getProjectObjectBO().getPidcDataHandler(), pidcAttr),
            PIDCVariantValueDialog.this.pidcPage.getProjectObjectBO().getPidcDataHandler(),
            new ProjectAttributeUtil().getProjectObjectLevel(pidcAttr)).getProjectObjectBO();
        AbstractProjectAttributeBO attrHandler =
            new ProjectAttributeUtil().getProjectAttributeHandler(pidcAttr, projObjBO);
        if ((cell.getColumnIndex() == ApicUiConstants.COLUMN_INDEX_6) && attrHandler.isHyperLinkValue()) {
          int cursorType = SWT.CURSOR_HAND;
          PIDCVariantValueDialog.this.cursor =
              new Cursor(PIDCVariantValueDialog.this.pidcAttrTabViewer.getControl().getDisplay(), cursorType);
          // Set the cursor to pidcAttrTabViewer
          PIDCVariantValueDialog.this.pidcAttrTabViewer.getGrid().setCursor(PIDCVariantValueDialog.this.cursor);
          changed = true;
        }
      }
    }
    if (!changed && ((PIDCVariantValueDialog.this.pidcAttrTabViewer.getGrid().getCursor() != null) &&
        PIDCVariantValueDialog.this.pidcAttrTabViewer.getGrid().getCursor()
            .equals(PIDCVariantValueDialog.this.cursor))) {
      final Cursor defCursor =
          new Cursor(PIDCVariantValueDialog.this.pidcAttrTabViewer.getControl().getDisplay(), SWT.CURSOR_ARROW);
      // Set the cursor to pidcAttrTabViewer
      PIDCVariantValueDialog.this.pidcAttrTabViewer.getGrid().setCursor(defCursor);
    }
  }


  /**
   * This method returns selected PIDC attribute from PIDCAttribute GridTableViewer
   *
   * @return IProjectAttribute
   */

  private IProjectAttribute getSelectedPIDCAttr(final Point point) {
    IProjectAttribute pidcAttr = null;
    // Determine which row was selected
    PIDCVariantValueDialog.this.pidcAttrTabViewer.getGrid().selectCell(point);
    final IStructuredSelection selection = (IStructuredSelection) this.pidcAttrTabViewer.getSelection();
    if ((selection != null) && (selection.size() != 0)) {
      final Object element = selection.getFirstElement();
      if (element instanceof IProjectAttribute) {
        pidcAttr = (IProjectAttribute) element;

      }
    }
    return pidcAttr;
  }


  /**
   * Set the attrs and its flag in map
   *
   * @param variantAttr attr
   * @param val flag type
   */
  public void setAttrUsedMap(final IModel variantAttr, final String val) {
    IProjectAttribute selVar = (IProjectAttribute) variantAttr;
    Set<IProjectAttribute> attrs = this.attrUsedMap.keySet();
    for (IProjectAttribute attr : attrs) {
      if (attr.compareTo((IProjectAttribute) variantAttr) == 0) {
        selVar = attr;
      }
    }
    this.attrUsedMap.put(selVar, val);
    if (val.equalsIgnoreCase(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType()) ||
        val.equalsIgnoreCase(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType())) {
      if (this.varSelFlag) {
        Set<IProjectAttribute> varAttrs = this.varValMap.keySet();
        for (IProjectAttribute varAtt : varAttrs) {
          if (varAtt.compareTo((IProjectAttribute) variantAttr) == 0) {
            this.varValMap.remove(varAtt);
            break;
          }
        }
      }
      else if (this.subVarSelFlag) {

        Set<IProjectAttribute> subVarAttrs = this.subVarValMap.keySet();
        for (IProjectAttribute varAtt : subVarAttrs) {
          if (varAtt.compareTo((IProjectAttribute) variantAttr) == 0) {
            this.subVarValMap.remove(varAtt);
            break;
          }
        }

      }
    }
    this.pidcAttrTabViewer.refresh();

  }

  /**
   * @return the pidcVariant
   */
  public PidcVariant getPidcVariant() {
    return this.pidcVariant;
  }


  /**
   * @param pidcVariant the pidcVariant to set
   */
  public void setPidcVariant(final PidcVariant pidcVariant) {
    this.pidcVariant = pidcVariant;
  }


  /**
   * @return the desc
   */
  public String getDesc() {
    return this.desc;
  }


  /**
   * @return the pidcSubVariant
   */
  public PidcSubVariant getPidcSubVariant() {
    return this.pidcSubVariant;
  }


  /**
   * @param pidcSubVariant the pidcSubVariant to set
   */
  public void setPidcSubVariant(final PidcSubVariant pidcSubVariant) {
    this.pidcSubVariant = pidcSubVariant;
  }

  /**
   * @param desc the desc to set
   */
  public void setDesc(final String desc) {
    this.desc = desc;
  }


  /**
   * @return the specLink
   */
  public String getSpecLink() {
    return this.specLink;
  }


  /**
   * @param specLink the specLink to set
   */
  public void setSpecLink(final String specLink) {
    this.specLink = specLink;
  }


  /**
   * @return the partNum
   */
  public String getPartNum() {
    return this.partNum;
  }


  /**
   * @param partNum the partNum to set
   */
  public void setPartNum(final String partNum) {
    this.partNum = partNum;
  }

  // ICDM-912
  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isResizable() {
    return true;
  }


  /**
   * Sets the used flag in the UI
   *
   * @param cell selected cell
   * @param element element
   */
  private void setUsedFlgUI(final ViewerCell cell, final Object element, final String usedFlag) {
    if (element instanceof IProjectAttribute) {
      final IProjectAttribute item = (IProjectAttribute) element;
      String usedValue = "";
      boolean intialValue = true;
      /**
       * Set the flag for the attr
       */
      if ((PIDCVariantValueDialog.this.attrUsedMap.size() != 0) &&
          PIDCVariantValueDialog.this.attrUsedMap.containsKey(item)) {
        String usdVal = PIDCVariantValueDialog.this.attrUsedMap.get(item);
        if (PROJ_ATTR_USED_FLAG.getType(usdVal) == null) {
          usdVal = PROJ_ATTR_USED_FLAG.getDbType(usdVal);
        }
        usedValue = usdVal;
        intialValue = false;
      }
      // ICDM-912
      else if (PIDCVariantValueDialog.this.oldAttrUsedMap.containsKey(item)) {
        usedValue = PIDCVariantValueDialog.this.oldAttrUsedMap.get(item);
      }
      if (usedValue.isEmpty()) {
        final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
        gridItem.setChecked(cell.getVisualIndex(), false);
      }
      else {
        setUsedFlgStatus(cell, usedFlag, usedValue, intialValue);
      }
    }
  }


  /**
   * @param cell
   * @param usedFlag
   * @param usedValue
   * @param intialValue
   */
  private void setUsedFlgStatus(final ViewerCell cell, final String usedFlag, final String usedValue,
      final boolean intialValue) {

    if (usedValue.equalsIgnoreCase(usedFlag)) {
      final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
      gridItem.setChecked(cell.getVisualIndex(), true);
      if (!intialValue) {
        PIDCVariantValueDialog.this.saveBtn.setEnabled(true);
      }
    }
    else {
      final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
      gridItem.setChecked(cell.getVisualIndex(), false);
    }
  }


  /**
   * Returns the value of attr to display in UI
   *
   * @param element attr
   * @param attrsValMap val map
   * @return value
   */
  private String displayValueUI(final Object element, final Map<IProjectAttribute, AttributeValue> attrsValMap) {
    if (attrsValMap.containsKey(element)) {
      if (attrsValMap.get(element) == null) {
        return "";
      }
      return attrsValMap.get(element).getName();
    }
    return CommonUIConstants.DISP_TEXT_USE_CUR_VAL;
  }


  /**
   * checks whether the attr is set value through this dialog .
   *
   * @param element attr
   * @param attrsMap map
   * @return true is attr is assgined new value
   */
  private boolean checkAttr(final Object element, final Map<IProjectAttribute, AttributeValue> attrsMap) {
    boolean flag = false;
    for (IProjectAttribute attr : attrsMap.keySet()) {
      if (attr.getAttrId().equals(((IProjectAttribute) element).getAttrId())) {
        flag = true;
        break;
      }
    }
    return flag;
  }


  /**
   * Gets the attr value set using this dialog
   *
   * @param element attr
   * @param attrsMap val map
   */
  private String getNewAtrrVal(final Object element, final Map<IProjectAttribute, AttributeValue> attrsMap) {
    PIDCVariantValueDialog.this.saveBtn.setEnabled(true);
    Set<IProjectAttribute> ipidcAttrs = attrsMap.keySet();
    for (IProjectAttribute ipidcAt : ipidcAttrs) {
      if (ipidcAt.compareTo((IProjectAttribute) element) == 0) {
        AttributeValue attrVal = attrsMap.get(ipidcAt);
        if (attrVal != null) {
          return attrVal.getName();

        }
      }
    }
    return CommonUIConstants.DISP_TEXT_USE_CUR_VAL;
  }


  /**
   * @return the pidcDataHandler
   */
  public PidcDataHandler getPidcDataHandler() {
    return this.pidcDataHandler;
  }


  /**
   * @return the varAttrMap
   */
  public Map<IProjectAttribute, AttributeValue> getVarAttrMap() {
    return this.varAttrMap;
  }


  /**
   * @return the subVarAttrMap
   */
  public Map<IProjectAttribute, AttributeValue> getSubVarAttrMap() {
    return this.subVarAttrMap;
  }


  /**
   * @return the oldPartNumMap
   */
  public Map<IProjectAttribute, String> getOldPartNumMap() {
    return this.oldPartNumMap;
  }


  /**
   * @return the oldCommentMap
   */
  public Map<IProjectAttribute, String> getOldCommentMap() {
    return this.oldCommentMap;
  }


  /**
   * @return the oldSpecLinkMap
   */
  public Map<IProjectAttribute, String> getOldSpecLinkMap() {
    return this.oldSpecLinkMap;
  }


  /**
   * @return the selPidcVer
   */
  public PidcVersion getSelPidcVer() {
    return this.selPidcVer;
  }


  /**
   * @return the variant
   */
  public PidcVariant getVariant() {
    return this.variant;
  }


  /**
   * @param variant the variant to set
   */
  public void setVariant(final PidcVariant variant) {
    this.variant = variant;
  }


  /**
   * @return the variants
   */
  public PidcVariant[] getVariants() {
    return this.variants;
  }


  /**
   * @return the subVariants
   */
  public PidcSubVariant[] getSubVariants() {
    return this.subVariants;
  }


}
