/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views.providers;

import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.apic.ui.editors.pages.PIDCSearchPage;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;

// ICDM-1135
/**
 * This class is to build attribute tree in PIDC scout
 *
 * @author bru2cob
 */
public class PIDCSearchContentProvider implements ITreeContentProvider {


  private final PIDCSearchPage searchPage;

  /**
   * @param searchPage PIDCSearchPage
   */
  public PIDCSearchContentProvider(final PIDCSearchPage searchPage) {
    this.searchPage = searchPage;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void inputChanged(final Viewer viewer, final Object obj, final Object obj1) {
    // TODO Auto-generated method stub

  }

  @Override
  public Object[] getChildren(final Object parentElement) {
    /**
     * parameters are returned when the editor is opened
     */
    if (parentElement instanceof Set) {
      return ((Collection<?>) parentElement).toArray();
    }
    /**
     * Attribute values are returned as child
     */
    if (parentElement instanceof Attribute) {
      Attribute selAttr = (Attribute) parentElement;
      // deleted attribute values are not considered
      SortedSet<AttributeValue> values = new TreeSet<>(this.searchPage.getDataHandler().getAllAttrValues(selAttr));
      return values.toArray();
    }
    return new Object[0];
  }

  /**
   * Returns the children(attr values) for the attribute. {@inheritDoc}
   */
  @Override
  public Object[] getElements(final Object elements) {
    return getChildren(elements);
  }

  /**
   * Returns the attribute for attributevalue {@inheritDoc}
   */
  @Override
  public Object getParent(final Object obj) {
    if (obj instanceof AttributeValue) {
      AttributeValue val = (AttributeValue) obj;
      return this.searchPage.getDataHandler().getAttribute(val.getAttributeId());
    }
    return null;
  }

  /**
   * Attribute will have children if it has values defined
   */
  @Override
  public boolean hasChildren(final Object element) {
    return element instanceof Attribute;
  }


}
