/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.views.providers;

import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;

/**
 * @author nip4cob
 */
public class ComboViewerContentPropsalProvider extends AbstractViewerFilter implements IContentProposalProvider {

  private final String[] comboItems;

  /**
   * @param comboItems items in comboviewer
   */
  public ComboViewerContentPropsalProvider(final String[] comboItems) {
    this.comboItems = comboItems;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IContentProposal[] getProposals(final String filterText, final int arg1) {
    IContentProposal[] proposals = new IContentProposal[this.comboItems.length];
    setFilterText(filterText);
    int i = 0;
    // If the filter text is just "*" then return all items in combo, else return the filtered items
    for (String comboItem : this.comboItems) {
      if (filterText.equals("*") || selectElement(comboItem)) {
        proposals[i] = new ContentProposal(comboItem);
        i++;
      }
    }
    return proposals;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    return matchText((String) element);
  }

}
