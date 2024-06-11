/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.differences.element;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.SortedSet;

import com.bosch.caltool.apic.ws.differences.adapter.PIDCChangedFMHistAdapterType;
import com.bosch.caltool.apic.ws.differences.adapter.PIDCChangedHistAdapter;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;

/**
 * @author svj7cob
 */
// iCDM-2614
public class ElementDifferencesFM extends ElementDifferences {

  /**
   * @param oldPidcChangeNumber oldPidcChangeNumber
   * @param newPidcChangeNumber newPidcChangeNumber
   * @param changeHistory changeHistory
   * @param idOfSuperiorElement idOfSuperiorElement
   * @param fromVersion fromVersion
   */
  public ElementDifferencesFM(final long oldPidcChangeNumber, final long newPidcChangeNumber,
      final SortedSet<PidcChangeHistory> changeHistory, final long idOfSuperiorElement, final boolean fromVersion) {
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
   *
   * @throws IcdmException
   */
  @Override
  protected void createResultSet() throws IcdmException {
    for (PidcChangeHistory entry : this.relevantHistoryEntries) {
      PIDCChangedFMHistAdapterType type;
      // key is <FM-ID>+<PIDC-VERSION-ID>
      String key = ApicConstants.EMPTY_STRING + entry.getFmId() + entry.getPidcVersId();

      // pidc action is vCDM Transfer in database
      if (entry.getPidcAction() != null) {
        key = key + entry.getChangedDate();
      }
      if (super.elements.containsKey(key)) {
        type = (PIDCChangedFMHistAdapterType) super.elements.get(key);

        type = new PIDCChangedFMHistAdapterType(type.getHistEntryOld(), entry);
      }
      else {
        type = new PIDCChangedFMHistAdapterType(entry, entry);
      }
      this.elements.put(key, type);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PIDCChangedHistAdapter[] toArray() {
    ArrayList<PIDCChangedFMHistAdapterType> list = new ArrayList<>();
    for (Entry<String, PIDCChangedHistAdapter> entry : this.elements.entrySet()) {

      PIDCChangedFMHistAdapterType attr = (PIDCChangedFMHistAdapterType) entry.getValue();

      // nullify the object if old & new parameters are same
      attr.removeEquals();

      // check if old & new parameters are different after ignoring null
      if (attr.isModified()) {
        list.add(attr);
      }
    }
    return list.toArray(new PIDCChangedFMHistAdapterType[list.size()]);
  }


}
