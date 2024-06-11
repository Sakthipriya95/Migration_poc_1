/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.views;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.Page;

import com.bosch.caltool.icdm.client.bo.a2l.A2LEditorDataHandler;
import com.bosch.caltool.icdm.common.ui.views.IStructurePageCreator;
import com.bosch.caltool.icdm.common.ui.views.PIDCDetailsViewPart;
import com.bosch.caltool.icdm.ui.editors.A2LContentsEditor;
import com.bosch.caltool.icdm.ui.editors.A2LContentsEditorInput;


/**
 * @author rgo7cob
 */
public class A2lDetailsPageCreator implements IStructurePageCreator {


  private final A2LContentsEditor editor;
  private final PIDCDetailsViewPart viewPart;
  private A2LEditorDataHandler a2lEditorDataHandler;
  private A2LDetailsPage a2lDetailsPage;


  /**
   * @param viewPart PIDCDetailsViewPart
   * @param editor A2LContentsEditor
   */
  public A2lDetailsPageCreator(final A2LContentsEditor editor, final PIDCDetailsViewPart viewPart) {
    this.editor = editor;
    this.viewPart = viewPart;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Page createPage() {
    IEditorInput editorInput = ((IEditorPart) this.editor).getEditorInput();
    if (editorInput instanceof A2LContentsEditorInput) {
      A2LContentsEditorInput a2lEditorInput = (A2LContentsEditorInput) editorInput;
      this.a2lEditorDataHandler = a2lEditorInput.getDataHandler();


      this.a2lDetailsPage = new A2LDetailsPage(this.a2lEditorDataHandler);
      this.viewPart.initPage(this.a2lDetailsPage);

      this.a2lDetailsPage.createControl(this.viewPart.getPageBook());


      return this.a2lDetailsPage;
    }
    return null;
  }


}
