/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.differences.element;

import java.util.SortedSet;

import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;


/**
 * @author imi2si
 */
public class ElementDifferencesAttributesPidcRest extends ElementDifferencesAttributesRest {

  /**
   * @param oldPidcChangeNumber old pidc change number
   * @param newPidcChangeNumber new pidc change number
   * @param changeHistory set of pidc change history
   * @param idOfSuperiorElement superior element id
   * @param fromVersion boolean
   */
  public ElementDifferencesAttributesPidcRest(final Long oldPidcChangeNumber, final Long newPidcChangeNumber,
      final SortedSet<PidcChangeHistory> changeHistory, final Long idOfSuperiorElement, final boolean fromVersion) {
    super(oldPidcChangeNumber, newPidcChangeNumber, changeHistory, idOfSuperiorElement, fromVersion);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void createElementsList(final SortedSet<PidcChangeHistory> historySet,
      final Long superiorElementId) {
    for (PidcChangeHistory entry : historySet) {

      if (isPidcHistoryRecord(superiorElementId, entry) &&
          isAttrOrPidcActionHistoryRec(entry)) {
        addRelevantEntries(entry);
      }
    }
  }

  /**
   * @param superiorElementId
   * @param entry
   * @return
   */
  private boolean isPidcHistoryRecord(final Long superiorElementId, PidcChangeHistory entry) {
    return isPidcHistoryRec(entry) && isPidcAndSuperiorEleIdSame(superiorElementId, entry);
  }

  /**
   * @param superiorElementId
   * @param entry
   * @return
   */
  private boolean isPidcAndSuperiorEleIdSame(final Long superiorElementId, final PidcChangeHistory entry) {
    return (entry.getPidcId().equals(superiorElementId)) && (null == entry.getFmVersId());
  }

  /**
   * @param entry
   * @return
   */
  private boolean isAttrOrPidcActionHistoryRec(final PidcChangeHistory entry) {
    return (null != entry.getAttrId()) || ((null == entry.getAttrId()) && (null != entry.getPidcAction()));
  }

  /**
   * @param entry
   * @return
   */
  private boolean isPidcHistoryRec(final PidcChangeHistory entry) {
    return (entry.getPidcId() != null) && (entry.getVarId() == null) && (entry.getSvarId() == null);
  }

  /**
   * @param entry
   */
  private void addRelevantEntries(final PidcChangeHistory entry) {
    if (this.fromVersion) {
      if ((entry.getPidcVersVers() >= this.oldPidcChangeNumber) &&
          ((this.newPidcChangeNumber == -1) || (entry.getPidcVersVers() <= this.newPidcChangeNumber))) {
        this.relevantHistoryEntries.add(entry);
      }
    }
    else if ((entry.getPidcVersion() >= this.oldPidcChangeNumber) &&
        ((this.newPidcChangeNumber == -1) || (entry.getPidcVersion() <= this.newPidcChangeNumber))) {
      this.relevantHistoryEntries.add(entry);
    }
  }

}
