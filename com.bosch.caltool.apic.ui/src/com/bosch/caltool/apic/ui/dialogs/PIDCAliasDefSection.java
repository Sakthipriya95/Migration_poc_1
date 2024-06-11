/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.model.apic.AliasDef;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * Creates pidc version details section in PIDCAttrValueEditDialog and AddValueDialog in case of creating a new PIDC
 *
 * @author dmo5cob
 */
public class PIDCAliasDefSection {


  /**
   * Number of columns in the section
   */
  private static final int NMBR_COLS_SECTION = 2;
  /**
   * Section version details
   */
  private Section sectionProjVrsn;
  /**
   * Composite version details
   */
  private final Composite composite;
  /**
   * Form version details
   */
  private Form formProjVrsn;


  /**
   * FormToolkit instance
   */
  private final FormToolkit formToolKit;
  /**
   * Ok button
   */
  private Button okBtn;
  /**
   * Version desc english text area
   */
  private Text versionDescEngTxt;
  /**
   * Version desc german text area
   */
  private Text versionDescGerTxt;

  /**
   * value dialog instance
   */
  private final ValueDialog valueDialog;
  /**
   * combo for alias def
   */
  private Combo comboAliasDef;

  /**
   * alias definition map
   */
  private Map<String, AliasDef> aliasDefinitionMap = new HashMap<>();

  /**
   * selected alias definition
   */
  private AliasDef selAliasDef;

  /**
   * @param composite Composite
   * @param formToolKit FormToolkit
   * @param valueDialog null
   * @param aliasDefMap
   */
  public PIDCAliasDefSection(final Composite composite, final FormToolkit formToolKit, final ValueDialog valueDialog,
      final Map<String, AliasDef> aliasDefMap) {
    this.composite = composite;
    this.formToolKit = formToolKit;
    this.valueDialog = valueDialog;
    setAliasDefinitionMap(aliasDefMap);
  }

  /**
   * @return ValueDialog
   */
  public ValueDialog getValueDialog() {
    return this.valueDialog;
  }

  /**
   * This method initializes section
   */
  public void createSectionAliasDef() {

    this.sectionProjVrsn = SectionUtil.getInstance().createSection(this.composite, this.formToolKit,
        GridDataUtil.getInstance().getGridData(), "Alias Definition details");
    this.sectionProjVrsn.getDescriptionControl().setEnabled(true);
    this.sectionProjVrsn.getDescriptionControl().setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
    // Text message for alias
    this.sectionProjVrsn
        .setDescription("use this only,if you need to use Alias names for Attributes or Values in vCDM");
    createFormAliasDef();
    this.sectionProjVrsn.setClient(this.formProjVrsn);
  }

  /**
   * create the form of version details
   */
  private void createFormAliasDef() {
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = NMBR_COLS_SECTION;
    this.formProjVrsn = this.formToolKit.createForm(this.sectionProjVrsn);
    this.formProjVrsn.getBody().setLayout(gridLayout);
    createaliasDefField();
  }

  /**
   *
   */
  private void createaliasDefField() {
    this.formToolKit.createLabel(this.formProjVrsn.getBody(), "Alias Definition:");
    GridData txtGrid = GridDataUtil.getInstance().createGridData();
    this.comboAliasDef = new Combo(this.formProjVrsn.getBody(), SWT.READ_ONLY);
    this.comboAliasDef.setLayoutData(txtGrid);

    SortedSet<String> aliasDefSet = new TreeSet<>();
    aliasDefSet.addAll(this.aliasDefinitionMap.keySet());

    this.comboAliasDef.add(ApicConstants.DEFAULT_COMBO_SELECT);
    for (String aliasName : aliasDefSet) {
      this.comboAliasDef.add(aliasName);
    }

    this.comboAliasDef.select(this.comboAliasDef.indexOf(ApicConstants.DEFAULT_COMBO_SELECT));

    this.comboAliasDef.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent evnt) {
        int index = PIDCAliasDefSection.this.comboAliasDef.getSelectionIndex();
        String selItem = PIDCAliasDefSection.this.comboAliasDef.getItem(index);
        if (selItem.equals(ApicConstants.DEFAULT_COMBO_SELECT)) {
          PIDCAliasDefSection.this.setSelAliasDef(null);
        }
        else {
          PIDCAliasDefSection.this.setSelAliasDef(PIDCAliasDefSection.this.aliasDefinitionMap.get(selItem));
        }
      }
    });

  }


  /**
   * @return the okBtn
   */
  public Button getOkBtn() {
    return this.okBtn;
  }


  /**
   * @param okBtn the okBtn to set
   */
  public void setOkBtn(final Button okBtn) {
    this.okBtn = okBtn;
  }


  /**
   * @return the versionDescEngTxt
   */
  public Text getVersionDescEngTxt() {
    return this.versionDescEngTxt;
  }


  /**
   * @return the versionDescGerTxt
   */
  public Text getVersionDescGerTxt() {
    return this.versionDescGerTxt;
  }

  /**
   * @return the aliasDefinitionMap
   */
  public Map<String, AliasDef> getAliasDefinitionMap() {
    return this.aliasDefinitionMap;
  }

  /**
   * @param aliasDefinitionMap the aliasDefinitionMap to set
   */
  public void setAliasDefinitionMap(final Map<String, AliasDef> aliasDefinitionMap) {
    this.aliasDefinitionMap = aliasDefinitionMap;
  }

  /**
   * @return the selAliasDef
   */
  public AliasDef getSelAliasDef() {
    return this.selAliasDef;
  }

  /**
   * @param selAliasDef the selAliasDef to set
   */
  public void setSelAliasDef(final AliasDef selAliasDef) {
    this.selAliasDef = selAliasDef;
  }


}
