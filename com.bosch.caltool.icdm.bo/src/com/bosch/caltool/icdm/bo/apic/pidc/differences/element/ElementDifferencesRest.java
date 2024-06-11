/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.differences.element;

import java.util.HashMap;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.bosch.caltool.icdm.bo.apic.pidc.differences.adapter.PIDCChangedHistAdapterRest;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;


/**
 * @author imi2si
 */
public abstract class ElementDifferencesRest {

  public enum Element {
                       ATTRIBUT,
                       VARIANT,
                       SUB_VARIANT
  }

  public static final int LEVEL_PIDC = 0;
  public static final int LEVEL_VARIANT = 1;
  public static final int LEVEL_SUB_VARIANT = 2;
  public static final int LEVEL_ATTRIBUTE = 3;

  Long pidc;
  Long oldPidcChangeNumber;
  Long newPidcChangeNumber;
  Long idOfSuperiorElement;

  protected SortedSet<PidcChangeHistory> changeHistory;
  protected SortedSet<PidcChangeHistory> relevantHistoryEntries;

  /**
   * Contains all change history for sub elements. When creating element differences for variances, this list is
   * additionally filled and used when creating the change history for the sub variant and attributes below one variant
   */
  protected HashMap<String, HashMap<Element, SortedSet<PidcChangeHistory>>> relevantHistoryEntriesSubElements;

  /**
   * Contains the change history for one element. An element can be an pidc/attribute/varaiant/sub/variant.
   */
  protected SortedMap<String, PIDCChangedHistAdapterRest> elements;
  protected final boolean fromVersion;


  public ElementDifferencesRest(final Long oldPidcChangeNumber, final Long newPidcChangeNumber,
      final SortedSet<PidcChangeHistory> changeHistory, final Long idOfSuperiorElement, final boolean fromVersion) {
    this.oldPidcChangeNumber = oldPidcChangeNumber;
    this.newPidcChangeNumber = newPidcChangeNumber;
    this.changeHistory = changeHistory;
    this.idOfSuperiorElement = idOfSuperiorElement;

    this.elements = new TreeMap<>();
    this.relevantHistoryEntries = new TreeSet<>();
    this.relevantHistoryEntriesSubElements = new HashMap<>();
    this.fromVersion = fromVersion;
  }


  public final void analyzeDifferences() throws DataException {
    createElementsList(this.changeHistory, this.idOfSuperiorElement);
    createResultSet();
  }

  protected abstract void createElementsList(SortedSet<PidcChangeHistory> changeHistory, Long idOfSuperiorElement);

  protected abstract void createResultSet() throws DataException;

  protected void putRelevantHistoryEntriesSubElements(final String histID, final Element element,
      final PidcChangeHistory entry) {

    // Create entry in map for ID if not existing
    if (!this.relevantHistoryEntriesSubElements.containsKey(histID)) {
      this.relevantHistoryEntriesSubElements.put(histID, new HashMap<>());
      this.relevantHistoryEntriesSubElements.get(histID).put(Element.ATTRIBUT, new TreeSet<PidcChangeHistory>());
      this.relevantHistoryEntriesSubElements.get(histID).put(Element.SUB_VARIANT, new TreeSet<PidcChangeHistory>());
      this.relevantHistoryEntriesSubElements.get(histID).put(Element.VARIANT, new TreeSet<PidcChangeHistory>());
    }

    // Put the entry into the map
    this.relevantHistoryEntriesSubElements.get(histID).get(element).add(entry);
  }

  protected SortedSet<PidcChangeHistory> getRelevantHistoryEntriesSubElements(final String histID,
      final Element element) {

    if (this.relevantHistoryEntriesSubElements.containsKey(histID) &&
        this.relevantHistoryEntriesSubElements.get(histID).containsKey(element)) {
      return this.relevantHistoryEntriesSubElements.get(histID).get(element);
    }

    return new TreeSet<>();
  }

  public abstract PIDCChangedHistAdapterRest[] toArray() throws DataException;

  public final SortedMap<String, PIDCChangedHistAdapterRest> getResultSet() {
    return this.elements;
  }
}
