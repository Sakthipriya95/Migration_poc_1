/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.differences.element;

import java.util.HashMap;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.bosch.caltool.apic.ws.differences.adapter.PIDCChangedHistAdapter;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;


/**
 * @author imi2si
 */
public abstract class ElementDifferences {

  
  //This enum is created to differentiate between the elements
   
  public enum Element {
                       ATTRIBUT,
                       VARIANT,
                       SUB_VARIANT
  }

  public static final int LEVEL_PIDC = 0;
  public static final int LEVEL_VARIANT = 1;
  public static final int LEVEL_SUB_VARIANT = 2;
  public static final int LEVEL_ATTRIBUTE = 3;

  long pidc;
  long oldPidcChangeNumber;
  long newPidcChangeNumber;
  long idOfSuperiorElement;

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
  protected SortedMap<String, PIDCChangedHistAdapter> elements;
  protected final boolean fromVersion;
  
  
   
  /**
   * @param oldPidcChangeNumber - old Pidc Change Number
   * @param newPidcChangeNumber - new Pidc Change Number
   * @param changeHistory - set of PidcChangeHistory
   * @param idOfSuperiorElement
   * @param fromVersion
   */
  public ElementDifferences(final long oldPidcChangeNumber, final long newPidcChangeNumber,
      final SortedSet<PidcChangeHistory> changeHistory, final long idOfSuperiorElement, final boolean fromVersion) {
  //this method id to get the differences in the elements
    this.oldPidcChangeNumber = oldPidcChangeNumber;
    this.newPidcChangeNumber = newPidcChangeNumber;
    this.changeHistory = changeHistory;
    this.idOfSuperiorElement = idOfSuperiorElement;

    this.elements = new TreeMap<>();
    this.relevantHistoryEntries = new TreeSet<>();
    this.relevantHistoryEntriesSubElements = new HashMap<>();
    this.fromVersion = fromVersion;
  }


  /**
   * this method is to analyse the differences
   * @throws IcdmException
   */
  public final void analyzeDifferences() throws IcdmException {
    //This method is to find the differences in change history
    //create the list of elements
    createElementsList(this.changeHistory, this.idOfSuperiorElement);
    //create the ruleset 
    createResultSet();
  }

  /**
   * @param changeHistory
   * @param idOfSuperiorElement
   */
  protected abstract void createElementsList(SortedSet<PidcChangeHistory> changeHistory, Long idOfSuperiorElement);

  protected abstract void createResultSet() throws IcdmException;

  /*
   * @param histID HistoryID
   * @param element Element for which the history has to be Added
   * @param entry - this has the details of the changes history
   */
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

  
  /*
   * @param histID HistoryID
   * @param element Element for which the history has to be checked
   * @return sortedSet
   * This method is to get relevant History Entries
   */
  protected SortedSet<PidcChangeHistory> getRelevantHistoryEntriesSubElements(final String histID,
      final Element element) {

    if (this.relevantHistoryEntriesSubElements.containsKey(histID) &&
        this.relevantHistoryEntriesSubElements.get(histID).containsKey(element)) {
      return this.relevantHistoryEntriesSubElements.get(histID).get(element);
    }

    return new TreeSet<>();
  }

  public abstract PIDCChangedHistAdapter[] toArray() throws IcdmException;

  public final SortedMap<String, PIDCChangedHistAdapter> getResultSet() {
    return this.elements;
  }
}
