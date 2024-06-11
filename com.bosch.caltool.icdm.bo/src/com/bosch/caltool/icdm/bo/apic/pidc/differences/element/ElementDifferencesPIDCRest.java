package com.bosch.caltool.icdm.bo.apic.pidc.differences.element;

import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.icdm.bo.apic.pidc.differences.adapter.PIDCChangedHistAdapterRest;
import com.bosch.caltool.icdm.bo.apic.pidc.differences.adapter.PIDCChangedPIDCHistAdapterRest;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDiffsResponseType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;


/**
 * @author imi2si
 */
public class ElementDifferencesPIDCRest extends ElementDifferencesRest {

  public static PidcDiffsResponseType getDummyResponse(final PidcVersion pidcVer, final Long oldPidcChangeNumber,
      final Long newPidcChangeNumber)
      throws IcdmException {
    PidcDiffsResponseType response = new PidcDiffsResponseType();
    // get the Pidc id from the Active Version
    response.setPidcId(pidcVer.getPidcId());
    response.setOldChangeNumber(oldPidcChangeNumber);
    response.setNewChangeNumber(newPidcChangeNumber);
    response.setOldPidcVersionNumber(pidcVer.getVersion());
    response.setNewPidcVersionNumber(pidcVer.getVersion());
    response.setOldPidcStatus("0");
    response.setNewPidcStatus("0");
    response.setModifiedDate(pidcVer.getModifiedDate());
    response.setModifiedUser(pidcVer.getModifiedUser());

    return response;
  }

  /**
   * @param oldPidcChangeNumber
   * @param newPidcChangeNumber
   * @param changeHistory
   * @param b
   */
  public ElementDifferencesPIDCRest(final long oldPidcChangeNumber, final long newPidcChangeNumber,
      final SortedSet<PidcChangeHistory> changeHistory, final long idOfSuperiorElement, final boolean fromVersion) {
    super(oldPidcChangeNumber, newPidcChangeNumber, changeHistory, idOfSuperiorElement, fromVersion);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void createElementsList(final SortedSet<PidcChangeHistory> changeHistory,
      final Long idOfSuperiorElement) {
    for (PidcChangeHistory entry : changeHistory) {
      if (((entry.getPidcId() != null) && (entry.getVarId() == null) && (entry.getSvarId() == null)) &&
          (entry.getPidcId().equals(idOfSuperiorElement))) {
        // ICDM-1407
        if ((entry.getAttrId() != null) || ((entry.getAttrId() == null) && (entry.getPidcAction() != null))) {
          this.relevantHistoryEntries.add(entry);
        }
      }
      else {
        if ((entry.getPidcVersion() >= this.oldPidcChangeNumber) &&
            ((this.newPidcChangeNumber == -1) || (entry.getPidcVersion() <= this.newPidcChangeNumber))) {
          this.relevantHistoryEntries.add(entry);
        }
      }
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected final void createResultSet() {
    for (PidcChangeHistory entry : this.relevantHistoryEntries) {
      String key = ApicConstants.EMPTY_STRING + entry.getPidcId() + entry.getPidcVersId();
      PIDCChangedPIDCHistAdapterRest type;
      if (super.elements.containsKey(key)) {
        type = (PIDCChangedPIDCHistAdapterRest) super.elements.get(key);
        type = new PIDCChangedPIDCHistAdapterRest(type.getHistEntryOld(), entry, this.fromVersion);
      }
      else {
        type = new PIDCChangedPIDCHistAdapterRest(entry, entry, this.fromVersion);
      }

      this.elements.put(key, type);
    }
  }

  @Override
  public final PIDCChangedHistAdapterRest[] toArray() {
    SortedSet<PIDCChangedPIDCHistAdapterRest> list = new TreeSet<>();

    for (Entry<String, PIDCChangedHistAdapterRest> entry : this.elements.entrySet()) {
      PIDCChangedPIDCHistAdapterRest attr = (PIDCChangedPIDCHistAdapterRest) entry.getValue();
      attr.removeEquals();
      list.add(attr);
    }
    return list.toArray(new PIDCChangedPIDCHistAdapterRest[list.size()]);
  }
}
