/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.differences.element;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.SortedSet;

import com.bosch.caltool.apic.ws.differences.adapter.PIDCChangedFMVersHistAdapterType;
import com.bosch.caltool.apic.ws.differences.adapter.PIDCChangedHistAdapter;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;

/**
 * @author svj7cob
 */
// iCDM-2614
public class ElementDifferencesFMVersion extends ElementDifferences {

  private final ServiceData serviceData;

  /**
   * @param oldPidcChangeNumber oldPidcChangeNumber
   * @param newPidcChangeNumber newPidcChangeNumber
   * @param changeHistory changeHistory
   * @param idOfSuperiorElement idOfSuperiorElement
   * @param fromVersion fromVersion
   */
  public ElementDifferencesFMVersion(final long oldPidcChangeNumber, final long newPidcChangeNumber,
      final SortedSet<PidcChangeHistory> changeHistory, final long idOfSuperiorElement, final boolean fromVersion,
      final ServiceData serviceData) {
    super(oldPidcChangeNumber, newPidcChangeNumber, changeHistory, idOfSuperiorElement, fromVersion);
    this.serviceData = serviceData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createElementsList(final SortedSet<PidcChangeHistory> changeHistory, final Long idOfSuperiorElement) {
    // storing only focus matrix version objects
    for (PidcChangeHistory entry : changeHistory) {
      if ((entry.getPidcId() != null) && entry.getPidcId().equals(idOfSuperiorElement) && (null == entry.getFmId()) &&
          (null != entry.getFmVersId())) {
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
      PIDCChangedFMVersHistAdapterType type;

      // key is <FM-VERSION-ID>+<PIDC-VERSION-ID>
      String key = ApicConstants.EMPTY_STRING + entry.getFmVersId() + entry.getPidcVersId();

      // pidc action is vCDM Transfer in database
      if (entry.getPidcAction() != null) {
        key = key + entry.getChangedDate();
      }

      if (super.elements.containsKey(key)) {
        type = (PIDCChangedFMVersHistAdapterType) super.elements.get(key);

        type = new PIDCChangedFMVersHistAdapterType(type.getHistEntryOld(), entry, this.serviceData);
      }
      else {
        type = new PIDCChangedFMVersHistAdapterType(entry, entry, this.serviceData);
      }
      this.elements.put(key, type);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws DataException
   */
  @Override
  public PIDCChangedHistAdapter[] toArray() throws DataException {
    ArrayList<PIDCChangedFMVersHistAdapterType> list = new ArrayList<>();
    for (Entry<String, PIDCChangedHistAdapter> entry : this.elements.entrySet()) {
      PIDCChangedFMVersHistAdapterType attr = (PIDCChangedFMVersHistAdapterType) entry.getValue();

      // nullify the object if old & new parameters are same
      attr.removeEquals();

      // check if old & new parameters are different after ignoring null
      if (attr.isModified()) {
        list.add(attr);
      }
    }

    return list.toArray(new PIDCChangedFMVersHistAdapterType[list.size()]);
  }


}
