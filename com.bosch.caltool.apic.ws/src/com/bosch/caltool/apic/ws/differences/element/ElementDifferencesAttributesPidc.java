/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.differences.element;

import java.util.SortedSet;

import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;


/**
 * @author imi2si
 */


public class ElementDifferencesAttributesPidc extends ElementDifferencesAttributes {

  /*
   * @param oldPidcChangeNumber - the input parameter is a old Pidc Change Number .
   * @param newPidcChangeNumber - the input parameter is a new Pidc Change Number .
   * @param changeHistory - the input parameter is a sortedSet containing the change History.
   * @param fromVersion - the input parameter is version Number .
   * this method to find the element differences in the attributes
   */
  public ElementDifferencesAttributesPidc(final long oldPidcChangeNumber, final long newPidcChangeNumber,
      final SortedSet<PidcChangeHistory> changeHistory, final long idOfSuperiorElement, final boolean fromVersion) {
    super(oldPidcChangeNumber, newPidcChangeNumber, changeHistory, idOfSuperiorElement, fromVersion);
  }

  /**
   * This is to create the element list
   * @param changeHistory - the input parameter is a sortedSet containing the change History.
   * @param idOfSuperiorElement - Contains the id of the superior element
   */
  @Override
  protected final void createElementsList(final SortedSet<PidcChangeHistory> changeHistory,
      final Long idOfSuperiorElement) {
    for (PidcChangeHistory entry : changeHistory) {
      if (isIdsNotNull(entry) && (entry.getPidcId().equals(idOfSuperiorElement)) && (null == entry.getFmVersId()) &&
          ((entry.getAttrId() != null) || ((entry.getAttrId() == null) && (entry.getPidcAction() != null)))) {
        // ICDM-1407
        addRelevantEntries(entry);
      }
    }
  }

  /*
   * @param entry
   * @return boolean value, indicates if the id is null or not
   */
  private boolean isIdsNotNull(PidcChangeHistory entry) {
    return (entry.getPidcId() != null) && (entry.getVarId() == null) && (entry.getSvarId() == null);
  }

  /**
   * @param entry
   */
  //This method is to add relevant PidcChanged entries
  private void addRelevantEntries(final PidcChangeHistory entry) {
    if (this.fromVersion) {
      //check if the pidc verison's version number is greater than or equal to the old pidc change number and
      //and new pidc change number is -1 
      if ((entry.getPidcVersVers() >= this.oldPidcChangeNumber) &&
          ((this.newPidcChangeNumber == -1) || (entry.getPidcVersVers() <= this.newPidcChangeNumber))) {
        this.relevantHistoryEntries.add(entry);
      }
    }
    //check if the pid version number is greater than or equal to the old pidc change number and
    //and new pidc change number is -1 
    else if ((entry.getPidcVersion() >= this.oldPidcChangeNumber) &&
        ((this.newPidcChangeNumber == -1) || (entry.getPidcVersion() <= this.newPidcChangeNumber))) {
      this.relevantHistoryEntries.add(entry);
    }
  }

}
