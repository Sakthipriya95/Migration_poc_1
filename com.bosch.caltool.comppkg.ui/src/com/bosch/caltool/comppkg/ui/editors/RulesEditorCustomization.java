/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.ui.editors;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.forms.editor.FormEditor;

import com.bosch.caltool.comppkg.ui.editors.pages.ComponentDetailsPage;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.caltool.icdm.ruleseditor.editor.IRulesEditorCustomization;


/**
 * @author dmo5cob
 */
public class RulesEditorCustomization implements IRulesEditorCustomization {


  /**
   * List of pages
   */
  List<AbstractFormPage> listOfPages = new ArrayList<>();

  /**
   * selected component package
   */
  CompPackage selectedCompPkg;

  /**
   * FormEditor instance
   */
  FormEditor editor;


  /**
   * @param selectedCompPkg CompPkg
   */
  public RulesEditorCustomization(final CompPackage selectedCompPkg) {
    this.selectedCompPkg = selectedCompPkg;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<AbstractFormPage> getPages() {
    ComponentDetailsPage dtlsPage = new ComponentDetailsPage(this.editor, this.selectedCompPkg);
    this.listOfPages.add(dtlsPage);
    return this.listOfPages;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setEditor(final FormEditor editor) {
    this.editor = editor;

  }
}
