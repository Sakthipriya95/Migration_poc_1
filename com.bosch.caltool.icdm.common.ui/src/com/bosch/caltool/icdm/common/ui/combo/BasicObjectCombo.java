/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.combo;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import com.bosch.caltool.datamodel.core.IBasicObject;
import com.bosch.caltool.icdm.common.ui.utils.DuplicateElementException;


/**
 * @author rgo7cob
 * @param <D> any implementation of IBasicObject
 */
public class BasicObjectCombo<D extends IBasicObject> extends Combo {

  /**
   * Empty text to be displayed
   */
  // ICDM-2593
  private String emptyText;

  /**
   * combo items map index as key and Item as value.
   */
  protected ConcurrentMap<Integer, D> comboItems = new ConcurrentHashMap<>();

  /**
   * @param parent parent
   * @param style style
   */
  public BasicObjectCombo(final Composite parent, final int style) {
    this(parent, style, null);
  }

  /**
   * @param parent parent
   * @param style style
   * @param emptyText text to be displayed for no selection
   */
  // ICDM-2593
  public BasicObjectCombo(final Composite parent, final int style, final String emptyText) {
    super(parent, style);
    this.emptyText = emptyText;
  }


  /**
   * Set the elements in this combo box.
   *
   * @param elements items to be added.
   */
  // ICDM-2612
  public void setElements(final Collection<D> elements) {
    removeAll();

    String[] items;
    int index = 0;
    Set<String> addedElements = new HashSet<>();

    if (hasEmptyText()) {
      // ICDM-2593
      items = new String[elements.size() + 1];
      items[index] = this.emptyText;
      addedElements.add(this.emptyText);
      index++;
    }
    else {
      items = new String[elements.size()];
    }

    // add items to combo and put that into the map. Key Index and value is object.
    for (D dataObj : elements) {
      String name = getName(dataObj);

      if (!addedElements.add(name)) {
        throw new DuplicateElementException("Element already exists. Name = " + name + "; Object = " + dataObj);
      }

      items[index] = name;
      this.comboItems.put(index, dataObj);

      index++;
    }
    setItems(items);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeAll() {
    this.comboItems.clear();
    super.removeAll();
  }


  /**
   * Name of the object. This is the display name, and should be unique.
   *
   * @param element element
   * @return name
   */
  // ICDM-2612
  protected String getName(final D element) {
    return element.getName();
  }


  /**
   * @return the current selection
   */
  public D getSelectedItem() {
    int selndx = getSelectionIndex();
    if (hasEmptyText() && (selndx == 0)) {
      return null;
    }
    return this.comboItems.get(selndx);

  }

  /**
   * @return true if empty text is set
   */
  // ICDM-2593
  public boolean hasEmptyText() {
    return this.emptyText != null;
  }

  @Override
  protected void checkSubclass() {
    // Disable the check that prevents subclassing of SWT components
  }

  /**
   * Get the index of the given element. If element is not found, -1 is returned.
   *
   * @param element element
   * @return the element's index
   */
  public final int getIndex(final D element) {
    for (Entry<Integer, D> entry : this.comboItems.entrySet()) {
      if (getName(entry.getValue()).equals(getName(element))) {
        return entry.getKey();
      }
    }
    return -1;
  }

  /**
   * @param index object's index
   * @return the item at the given index
   */
  public D getItemObject(final int index) {
    if (hasEmptyText() && (index == 0)) {
      return null;
    }
    return this.comboItems.get(index);
  }

  /**
   * Select an element in the combo box
   *
   * @param element element to select
   */
  public void select(final D element) {
    int index = getIndex(element);
    select(index);
  }
}
