/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.PageBookView;
import org.eclipse.ui.views.properties.IPropertySheetPage;

import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.common.ui.listeners.IDceRefresher;
import com.bosch.caltool.icdm.common.ui.sorters.CdmPropertySheetSorter;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;


/**
 * @author adn1cob
 */
// iCDM-250
public abstract class AbstractPageBookView extends PageBookView implements IDceRefresher {

  /**
   * List of pages in view part
   */
  protected final List<PageRec> pageRecordList = new ArrayList<>();

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
    // Default implementation, no actions
  }

  @Override
  public IClientDataHandler getDataHandler() {
    return null;
  }

  /**
   * @return pages
   */
  public List<IPage> getPages() {
    List<IPage> retList = new ArrayList<>();
    this.pageRecordList.forEach(rec -> retList.add(rec.page));
    return retList;
  }

}
