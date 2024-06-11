package com.bosch.caltool.apic.ws.differences.element;

import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.apic.ws.GetPidcDiffsResponseType;
import com.bosch.caltool.apic.ws.differences.adapter.PIDCChangedHistAdapter;
import com.bosch.caltool.apic.ws.differences.adapter.PIDCChangedPIDCHistAdapter;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;


/**
 * @author imi2si
 */
public class ElementDifferencesPIDC extends ElementDifferences {

  /**
   * @param pidcVer - Contains the version number of PIDC
   * @param oldPidcChangeNumber - Has the old PIDC Change number
   * @param newPidcChangeNumber - Has the new PIDC Change number
   * @return Pidc Difference Response
   * this method is to get the dummy response
   * @throws IcdmException
   */
  public static GetPidcDiffsResponseType getDummyResponse(final PidcVersion pidcVer, final long oldPidcChangeNumber,
      final long newPidcChangeNumber)
      throws IcdmException {
    GetPidcDiffsResponseType response = new GetPidcDiffsResponseType();
    // get the Pidc id from the Active Version
    //setters to set the values to response object
    response.setPidcID(pidcVer.getPidcId());
    response.setOldChangeNumber(oldPidcChangeNumber);
    response.setNewChangeNumber(newPidcChangeNumber);
    response.setOldPidcVersionNumber(pidcVer.getVersion());
    response.setNewPidcVersionNumber(pidcVer.getVersion());
    response.setOldPidcStatus("0");
    response.setNewPidcStatus("0");
    response.setModifyDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, pidcVer.getModifiedDate()));
    response.setModifyUser(pidcVer.getModifiedUser());

    return response;
  }

  /**
   * @param oldPidcChangeNumber  Has the old PIDC Change number
   * @param newPidcChangeNumber  Has the new PIDC Change number
   * @param changeHistory Has the Change History details
   * @param idOfSuperiorElement has the id of the superior element
   * @param fromVersion This has the value of the version number
   * this method is to find the differences in the PIDC
   */
  public ElementDifferencesPIDC(final long oldPidcChangeNumber, final long newPidcChangeNumber,
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
    // iterate through the change history
    for (PidcChangeHistory entry : changeHistory) {
      // check if the pidc id is not null and variant and subvariant id is null
      if (((entry.getPidcId() != null) && (entry.getVarId() == null) && (entry.getSvarId() == null)) &&
          (entry.getPidcId().equals(idOfSuperiorElement))) {
        // ICDM-1407
        // check if the attribute id is not null and variant and subvariant id is null
        if ((entry.getAttrId() != null) || ((entry.getAttrId() == null) && (entry.getPidcAction() != null))) {
          this.relevantHistoryEntries.add(entry);
        }
      }
      else {
        // check if the pidc version id is > than the old pidc change number
        // and new pidc change number = -1 and pidc version is < than the new pidc change number
        if ((entry.getPidcVersion() >= this.oldPidcChangeNumber) &&
            ((this.newPidcChangeNumber == -1) || (entry.getPidcVersion() <= this.newPidcChangeNumber))) {
          this.relevantHistoryEntries.add(entry);
        }
      }
    }
  }


  /**
   * {@inheritDoc}
   *
   * @throws IcdmException
   */
  @Override
  protected final void createResultSet() throws IcdmException {
    for (PidcChangeHistory entry : this.relevantHistoryEntries) {
      String key = ApicConstants.EMPTY_STRING + entry.getPidcId() + entry.getPidcVersId();
      PIDCChangedPIDCHistAdapter type;
      if (super.elements.containsKey(key)) {
        type = (PIDCChangedPIDCHistAdapter) super.elements.get(key);
        type = new PIDCChangedPIDCHistAdapter(type.getHistEntryOld(), entry, this.fromVersion);
      }
      else {
        type = new PIDCChangedPIDCHistAdapter(entry, entry, this.fromVersion);
      }

      this.elements.put(key, type);
    }
  }

  @Override
  public final PIDCChangedPIDCHistAdapter[] toArray() {
    SortedSet<PIDCChangedPIDCHistAdapter> list = new TreeSet<>();

    for (Entry<String, PIDCChangedHistAdapter> entry : this.elements.entrySet()) {
      PIDCChangedPIDCHistAdapter attr = (PIDCChangedPIDCHistAdapter) entry.getValue();
      attr.removeEquals();
      list.add(attr);
    }
    // convert the PIDCChangedPIDCHistAdapter list to array
    return list.toArray(new PIDCChangedPIDCHistAdapter[list.size()]);
  }
}
