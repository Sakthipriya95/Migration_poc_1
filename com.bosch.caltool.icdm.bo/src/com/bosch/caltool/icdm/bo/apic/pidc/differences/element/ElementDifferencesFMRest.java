/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.differences.element;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.SortedSet;

import com.bosch.caltool.icdm.bo.apic.pidc.differences.adapter.PIDCChangedFMHistAdapterRestType;
import com.bosch.caltool.icdm.bo.apic.pidc.differences.adapter.PIDCChangedHistAdapterRest;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;

/**
 * @author svj7cob
 */
// iCDM-2614
public class ElementDifferencesFMRest extends ElementDifferencesRest {

  /**
   * @param oldPidcChangeNumber oldPidcChangeNumber
   * @param newPidcChangeNumber newPidcChangeNumber
   * @param changeHistory changeHistory
   * @param idOfSuperiorElement idOfSuperiorElement
   * @param fromVersion fromVersion
   */
  public ElementDifferencesFMRest(final Long oldPidcChangeNumber, final Long newPidcChangeNumber,
      final SortedSet<PidcChangeHistory> changeHistory, final Long idOfSuperiorElement, final boolean fromVersion) {
    super(oldPidcChangeNumber, newPidcChangeNumber, changeHistory, idOfSuperiorElement, fromVersion);
  }


  /**
   * Creates the Element list
   */
  @Override
  protected void createElementsList(final SortedSet<PidcChangeHistory> changeHistory, final Long idOfSuperiorElement) {
    // storing only focus matrix objects
    for (PidcChangeHistory entry : changeHistory) {
      if (((entry.getPidcId() != null) && (entry.getPidcId().equals(idOfSuperiorElement))) &&
          (null != entry.getFmId())) {
        addRelevantEntries(entry);
      }
    }
  }

  /**
   * @param entry
   */
  private void addRelevantEntries(final PidcChangeHistory entry) {
    // if from pidc version
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

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createResultSet() {
    for (PidcChangeHistory entry : this.relevantHistoryEntries) {
      PIDCChangedFMHistAdapterRestType type;
      // key is <FM-ID>+<PIDC-VERSION-ID>
      String key = ApicConstants.EMPTY_STRING + entry.getFmId() + entry.getPidcVersId();

      // pidc action is vCDM Transfer in database
      if (entry.getPidcAction() != null) {
        key = key + entry.getChangedDate();
      }
      if (super.elements.containsKey(key)) {
        type = (PIDCChangedFMHistAdapterRestType) super.elements.get(key);

        type = new PIDCChangedFMHistAdapterRestType(type.getHistEntryOld(), entry, this.fromVersion);
      }
      else {
        type = new PIDCChangedFMHistAdapterRestType(entry, entry, this.fromVersion);
      }
      this.elements.put(key, type);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PIDCChangedHistAdapterRest[] toArray() {
    ArrayList<PIDCChangedFMHistAdapterRestType> list = new ArrayList<>();
    for (Entry<String, PIDCChangedHistAdapterRest> entry : this.elements.entrySet()) {

      PIDCChangedFMHistAdapterRestType attr = (PIDCChangedFMHistAdapterRestType) entry.getValue();

      // nullify the object if old & new parameters are same
      attr.removeEquals();

      // check if old & new parameters are different after ignoring null
      if (attr.isModified()) {
        list.add(attr);
      }
    }
    return list.toArray(new PIDCChangedFMHistAdapterRestType[list.size()]);
  }


}
