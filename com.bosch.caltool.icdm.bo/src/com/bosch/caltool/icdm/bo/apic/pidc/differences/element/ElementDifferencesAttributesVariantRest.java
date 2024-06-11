/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.differences.element;

import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;


/**
 * @author imi2si
 */
public class ElementDifferencesAttributesVariantRest extends ElementDifferencesAttributesRest {

  /**
   * @param oldPidcChangeNumber oldPidcChangeNumber
   * @param newPidcChangeNumber newPidcChangeNumber
   * @param changeHistory changeHistory
   * @param idOfSuperiorElement idOfSuperiorElement
   * @param fromVersion fromVersion
   */
  public ElementDifferencesAttributesVariantRest(final Long oldPidcChangeNumber, final Long newPidcChangeNumber,
      final SortedSet<PidcChangeHistory> changeHistory, final Long idOfSuperiorElement, final boolean fromVersion) {
    super(oldPidcChangeNumber, newPidcChangeNumber, changeHistory, idOfSuperiorElement, fromVersion);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void createElementsList(final SortedSet<PidcChangeHistory> changeHistory,
      final Long idOfSuperiorElement) {
    SortedSet<PidcChangeHistory> rlvantHistoryEntries = new TreeSet<>();
    // put the history enries.
    if (changeHistory != null) {
      for (PidcChangeHistory entry : changeHistory) {
        // iCDM-2614
        if (null == entry.getFmVersId()) {
          rlvantHistoryEntries.add(entry);
        }
      }
      this.relevantHistoryEntries = rlvantHistoryEntries;
    }

  }
}