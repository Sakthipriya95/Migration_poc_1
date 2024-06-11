package com.bosch.caltool.apic.ws.differences.element;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.SortedSet;

import com.bosch.caltool.apic.ws.differences.adapter.PIDCChangedAttrHistAdapterType;
import com.bosch.caltool.apic.ws.differences.adapter.PIDCChangedHistAdapter;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;


/**
 * @author imi2si
 */
public abstract class ElementDifferencesAttributes extends ElementDifferences {

  /**
   * @param oldPidcChangeNumber
   * @param newPidcChangeNumber
   * @param changeHistory
   * @param fromVersion
   */
  /*
   this method to find the element differences in the attributes
   */
  public ElementDifferencesAttributes(final long oldPidcChangeNumber, final long newPidcChangeNumber,
      final SortedSet<PidcChangeHistory> changeHistory, final long idOfSuperiorElement, final boolean fromVersion) {
    super(oldPidcChangeNumber, newPidcChangeNumber, changeHistory, idOfSuperiorElement, fromVersion);
  }
  /**
   * {@inheritDoc}
   */
  /**
   * This is to create the element list
   * @param changeHistory - the input parameter is a sortedSet containing the change History.
   * @param idOfSuperiorElement - Contains the id of the superior element
   */
  @Override
  protected abstract void createElementsList(final SortedSet<PidcChangeHistory> changeHistory,
      final Long idOfSuperiorElement);

  /**
   * {@inheritDoc}
   *
   * @throws IcdmException
   */
  @Override
  protected final void createResultSet() throws IcdmException {
    // This method is to create a ruleset.
    // iterate through the relevant history entries
    for (PidcChangeHistory entry : this.relevantHistoryEntries) {
    //append attribute id and version id to the key
      String key = ApicConstants.EMPTY_STRING + entry.getAttrId() + entry.getPidcVersId();
      if (entry.getPidcAction() != null) {
        key = key + entry.getChangedDate();
      }
      PIDCChangedAttrHistAdapterType type;
      if (super.elements.containsKey(key)) {
        type = (PIDCChangedAttrHistAdapterType) super.elements.get(key);
        type = new PIDCChangedAttrHistAdapterType(type.getHistEntryOld(), entry);
      }
      else {
        type = new PIDCChangedAttrHistAdapterType(entry, entry);
      }
      // add the relevant history entries to the elements Map
      this.elements.put(key, type);
    }
  }

  @Override
  public final PIDCChangedAttrHistAdapterType[] toArray() {
    ArrayList<PIDCChangedAttrHistAdapterType> list = new ArrayList<>();
    // iterate through elements map 
    for (Entry<String, PIDCChangedHistAdapter> entry : this.elements.entrySet()) {
      PIDCChangedAttrHistAdapterType attr = (PIDCChangedAttrHistAdapterType) entry.getValue();
      attr.removeEquals();
      //check if the attribute is modified  add it to the list
      if (attr.isModified()) {
        list.add(attr);
      }
      //check if the attribute value equals zero and pidc action is not null
      if (("0").equalsIgnoreCase(String.valueOf(attr.getAttrID())) && (attr.getPidcAction() != null)) {
        list.add(attr);
      }
    }
    return list.toArray(new PIDCChangedAttrHistAdapterType[list.size()]);
  }
}
