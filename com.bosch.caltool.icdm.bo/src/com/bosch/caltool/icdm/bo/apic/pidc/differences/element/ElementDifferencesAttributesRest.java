package com.bosch.caltool.icdm.bo.apic.pidc.differences.element;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.SortedSet;

import com.bosch.caltool.icdm.bo.apic.pidc.differences.adapter.PIDCChangedAttrHistAdapterRestType;
import com.bosch.caltool.icdm.bo.apic.pidc.differences.adapter.PIDCChangedHistAdapterRest;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;


/**
 * @author imi2si
 */
public abstract class ElementDifferencesAttributesRest extends ElementDifferencesRest {

  /**
   * @param oldPidcChangeNumber
   * @param newPidcChangeNumber
   * @param changeHistory
   * @param fromVersion
   */
  public ElementDifferencesAttributesRest(final Long oldPidcChangeNumber, final Long newPidcChangeNumber,
      final SortedSet<PidcChangeHistory> changeHistory, final Long idOfSuperiorElement, final boolean fromVersion) {
    super(oldPidcChangeNumber, newPidcChangeNumber, changeHistory, idOfSuperiorElement, fromVersion);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected abstract void createElementsList(final SortedSet<PidcChangeHistory> changeHistory,
      final Long idOfSuperiorElement);

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void createResultSet() {
    for (PidcChangeHistory entry : this.relevantHistoryEntries) {
      String key = ApicConstants.EMPTY_STRING + entry.getAttrId() + entry.getPidcVersId();
      if (entry.getPidcAction() != null) {
        key = key + entry.getChangedDate();
      }
      PIDCChangedAttrHistAdapterRestType type;
      if (super.elements.containsKey(key)) {
        type = (PIDCChangedAttrHistAdapterRestType) super.elements.get(key);
        type = new PIDCChangedAttrHistAdapterRestType(type.getHistEntryOld(), entry);
      }
      else {
        type = new PIDCChangedAttrHistAdapterRestType(entry, entry);
      }

      this.elements.put(key, type);
    }
  }

  @Override
  public final PIDCChangedHistAdapterRest[] toArray() {
    ArrayList<PIDCChangedAttrHistAdapterRestType> list = new ArrayList<>();

    for (Entry<String, PIDCChangedHistAdapterRest> entry : this.elements.entrySet()) {
      PIDCChangedAttrHistAdapterRestType attr = (PIDCChangedAttrHistAdapterRestType) entry.getValue();
      attr.removeEquals();

      if (attr.isModified()) {
        list.add(attr);
      }
      if (((null == attr.getAttrId()) || ("0").equalsIgnoreCase(String.valueOf(attr.getAttrId()))) &&
          (attr.getPidcAction() != null)) {
        list.add(attr);
      }
    }

    return list.toArray(new PIDCChangedAttrHistAdapterRestType[list.size()]);
  }
}
