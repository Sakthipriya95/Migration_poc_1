package com.bosch.caltool.icdm.bo.apic.pidc.differences.element;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.SortedSet;

import com.bosch.caltool.icdm.bo.apic.pidc.differences.adapter.PIDCChangedHistAdapterRest;
import com.bosch.caltool.icdm.bo.apic.pidc.differences.adapter.PIDCChangedSubVariantHistAdapterRestType;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangedAttrType;


/**
 * @author imi2si
 */
public class ElementDifferencesSubVariantRest extends ElementDifferencesRest {

  /**
   * @param oldPidcChangeNumber
   * @param newPidcChangeNumber
   * @param changeHistory
   * @param fromVersion
   */
  public ElementDifferencesSubVariantRest(final Long oldPidcChangeNumber, final Long newPidcChangeNumber,
      final SortedSet<PidcChangeHistory> changeHistory, final Long idOfSuperiorElement, final boolean fromVersion) {
    super(oldPidcChangeNumber, newPidcChangeNumber, changeHistory, idOfSuperiorElement, fromVersion);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void createElementsList(final SortedSet<PidcChangeHistory> changeHistory,
      final Long idOfSuperiorElement) {

    for (PidcChangeHistory entry : changeHistory) {
      String key = ApicConstants.EMPTY_STRING + entry.getSvarId() + entry.getPidcVersId();
      if (this.fromVersion) {
        // Consider only records that are in the requested range of change numbers
        if ((entry.getPidcVersVers() >= this.oldPidcChangeNumber) &&
            ((this.newPidcChangeNumber == -1) || (entry.getPidcVersVers() <= this.newPidcChangeNumber))) {

          // To be relevant for variants, the PIDC and Attribute ID must always be set
          if ((entry.getPidcId() != null) && (entry.getVarId() != null) && (entry.getSvarId() != null) &&
              entry.getVarId().equals(idOfSuperiorElement)) {

            // Relevant records for the variants
            if (entry.getAttrId() == null) {
              this.relevantHistoryEntries.add(entry);
            }
            else {

              putRelevantHistoryEntriesSubElements(key, Element.ATTRIBUT, entry);
            }
          }
        }
      }
      else {
        // Consider only records that are in the requested range of change numbers
        if ((entry.getPidcVersion() >= this.oldPidcChangeNumber) &&
            ((this.newPidcChangeNumber == -1) || (entry.getPidcVersVers() <= this.newPidcChangeNumber))) {
          // To be relevant for variants, the PIDC and Attribute ID must always be set
          if ((entry.getPidcId() != null) && (entry.getVarId() != null) && (entry.getSvarId() != null) &&
              entry.getVarId().equals(idOfSuperiorElement)) {

            // Relevant records for the variants
            if (entry.getAttrId() == null) {
              this.relevantHistoryEntries.add(entry);
            }
            else {

              putRelevantHistoryEntriesSubElements(key, Element.ATTRIBUT, entry);
            }
          }
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
      String key = ApicConstants.EMPTY_STRING + entry.getSvarId() + entry.getPidcVersId();
      PIDCChangedSubVariantHistAdapterRestType type;
      if (super.elements.containsKey(key)) {
        type = (PIDCChangedSubVariantHistAdapterRestType) super.elements.get(key);
        type = new PIDCChangedSubVariantHistAdapterRestType(type.getHistEntryOld(), entry);
      }
      else {
        type = new PIDCChangedSubVariantHistAdapterRestType(entry, entry);
      }

      this.elements.put(key, type);
    }
  }

  @Override
  public final PIDCChangedHistAdapterRest[] toArray() throws DataException {
    ArrayList<PIDCChangedSubVariantHistAdapterRestType> list = new ArrayList<>();

    for (Entry<String, PIDCChangedHistAdapterRest> entry : this.elements.entrySet()) {
      PIDCChangedSubVariantHistAdapterRestType attr = (PIDCChangedSubVariantHistAdapterRestType) entry.getValue();
      attr.removeEquals();
      addAttributes(attr);

      if (attr.isModified() || ((attr.getChangedAttrList() != null) && (attr.getChangedAttrList().size() > 0))) {
        list.add(attr);
      }
    }

    return list.toArray(new PIDCChangedSubVariantHistAdapterRestType[list.size()]);
  }

  private void addAttributes(final PIDCChangedSubVariantHistAdapterRestType attribute) throws DataException {

    String key = ApicConstants.EMPTY_STRING + attribute.getSubVariantId() + attribute.getPidcVersion();
    ElementDifferencesRest attr =
        new ElementDifferencesAttributesSubVariantRest(super.oldPidcChangeNumber, super.newPidcChangeNumber,
            getRelevantHistoryEntriesSubElements(key, Element.ATTRIBUT), attribute.getSubVariantId(), this.fromVersion);
    attr.analyzeDifferences();

    PidcChangedAttrType[] attrArray = (PidcChangedAttrType[]) attr.toArray();

    for (PidcChangedAttrType element : attrArray) {
      attribute.getChangedAttrList().add(element);
    }
  }
}
