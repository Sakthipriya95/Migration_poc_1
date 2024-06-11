/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.views.properties.IPropertySheetPage;

import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.common.ui.listeners.IDceRefresher;
import com.bosch.caltool.icdm.common.ui.sorters.CdmPropertySheetSorter;
import com.bosch.caltool.icdm.common.ui.views.CdmPropertySheetPage;
import com.bosch.caltool.icdm.common.ui.views.IPageCreator;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.rcputils.wbutils.WorkbenchUtils;


/**
 * Abstraction for FormEditor
 *
 * @author adn1cob
 */
// iCDM-241
public abstract class AbstractFormEditor extends FormEditor implements IDceRefresher {


  /**
   * Uses a custom property sheet page for displaying properties when key is <code>IPropertySheetPage</code>, to disable
   * sorting of properties
   * <p>
   * {@inheritDoc}
   */
  @Override
  public Object getAdapter(@SuppressWarnings("rawtypes") final Class key) {
    if (key == IPropertySheetPage.class) {
      CdmPropertySheetPage page = new CdmPropertySheetPage();
      page.setSheetSorter(new CdmPropertySheetSorter());
      return page;
    }
    return super.getAdapter(key);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    // No default actions
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IClientDataHandler getDataHandler() {
    IEditorInput editorInput = getEditorInput();
    if (editorInput instanceof IIcdmEditorInput) {
      return ((IIcdmEditorInput) editorInput).getDataHandler();
    }
    return null;
  }


  /**
   * @return pages in this editor
   */
  public List<Object> getPages() {
    return new ArrayList<>(this.pages);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
    setSite(site);
    setInput(input);
    createOutlineViewPage();
  }

  /**
   * @throws PartInitException
   */
  private final void createOutlineViewPage() throws PartInitException {
    IPageCreator pageCreator = getOutlinePageCreator();
    OutlineViewPart viewPart = (OutlineViewPart) WorkbenchUtils.getView(OutlineViewPart.PART_ID);
    if ((pageCreator != null) && (viewPart == null)) {
      // Outline view should know what pages to show in outline
      // if the outline view is closed open it progamatically
      // Then open Outline programatically
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(OutlineViewPart.PART_ID);
      viewPart = (OutlineViewPart) WorkbenchUtils.getView(OutlineViewPart.PART_ID);
    }
    if (viewPart != null) {
      viewPart.setPageCreator(pageCreator);
    }
  }

  /**
   * @return Outline Page Creator for this editor page
   */
  protected IPageCreator getOutlinePageCreator() {
    return null;
  }


}
