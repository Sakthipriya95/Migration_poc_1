/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.Page;

import com.bosch.caltool.apic.ui.editors.PIDCEditor;
import com.bosch.caltool.apic.ui.editors.PIDCEditorInput;
import com.bosch.caltool.icdm.common.ui.views.IStructurePageCreator;
import com.bosch.caltool.icdm.common.ui.views.PIDCDetailsViewPart;

/**
 * @author rgo7cob
 */
public class PidcDetailsPageCreator implements IStructurePageCreator {


  private final PIDCEditor editor;
  private final PIDCDetailsViewPart viewPart;

  /**
   * @param viewPart
   * @param pidcEditor
   */
  public PidcDetailsPageCreator(final PIDCEditor editor, final PIDCDetailsViewPart viewPart) {
    this.editor = editor;
    this.viewPart = viewPart;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Page createPage() {
    IEditorInput editorInput = ((IEditorPart) this.editor).getEditorInput();
    PIDCEditorInput editorInputNew = (PIDCEditorInput) editorInput;
    // Pidc details page
    PIDCDetailsPage page = new PIDCDetailsPage(editorInputNew.getDataHandler(), this.viewPart);
    // Set page
    page.setPidcPage(this.editor.getPidcPage());
    // init page
    this.viewPart.initPage(page);
    page.createControl(this.viewPart.getPageBook());

    return page;
  }


}
