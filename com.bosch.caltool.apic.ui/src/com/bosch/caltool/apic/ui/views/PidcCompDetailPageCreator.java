/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.Page;

import com.bosch.caltool.apic.ui.editors.PIDCCompareEditor;
import com.bosch.caltool.apic.ui.editors.PIDCCompareEditorInput;
import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectObjectBO;
import com.bosch.caltool.icdm.common.ui.views.AbstractPage;
import com.bosch.caltool.icdm.common.ui.views.IStructurePageCreator;
import com.bosch.caltool.icdm.common.ui.views.PIDCDetailsViewPart;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectObject;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;

/**
 * @author rgo7cob
 */
public class PidcCompDetailPageCreator implements IStructurePageCreator {


  private final PIDCCompareEditor editor;
  private final PIDCDetailsViewPart viewPart;

  /**
   * @param pidcCompareEditor
   * @param viewPart
   */
  public PidcCompDetailPageCreator(final PIDCCompareEditor pidcCompareEditor, final PIDCDetailsViewPart viewPart) {
    this.editor = pidcCompareEditor;
    this.viewPart = viewPart;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Page createPage() {
    IEditorInput editorInput = ((IEditorPart) this.editor).getEditorInput();
    PIDCCompareEditorInput compareEditorInput = (PIDCCompareEditorInput) editorInput;
    IProjectObject abstractProjectObject = compareEditorInput.getCompareObjs().get(0);
    boolean isVarCompare = false;
    boolean isVerCompare = false;
    AbstractProjectObjectBO projObjBO = null;
    if (abstractProjectObject instanceof PidcVariant) {
      isVarCompare = true;
      projObjBO = compareEditorInput.getComparePidcHandler().getCompareObjectsHandlerMap()
          .get(((PidcVariant) abstractProjectObject).getId());
    }
    else if (abstractProjectObject instanceof PidcVersion) {
      isVerCompare = true;
      projObjBO = compareEditorInput.getComparePidcHandler().getCompareObjectsHandlerMap()
          .get(((PidcVersion) abstractProjectObject).getId());
    }
    else if (abstractProjectObject instanceof PidcSubVariant) {
      projObjBO = compareEditorInput.getComparePidcHandler().getCompareObjectsHandlerMap()
          .get(((PidcSubVariant) abstractProjectObject).getId());
    }
    AbstractPage page;
    if (!isVerCompare) {
      page = new PIDCCompareDetailsPg(compareEditorInput.getComparePidcHandler().getPidcVersionBO(), this.viewPart,
          isVarCompare, isVerCompare);
    }
    else {
      page = new PIDCCompareDetailsPg(projObjBO, this.viewPart, isVarCompare, isVerCompare);
    }
    this.viewPart.initPage(page);
    page.createControl(this.viewPart.getPageBook());

    return page;
  }


}
