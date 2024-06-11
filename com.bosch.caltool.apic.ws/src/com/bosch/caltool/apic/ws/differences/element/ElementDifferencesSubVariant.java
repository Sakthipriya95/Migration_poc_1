package com.bosch.caltool.apic.ws.differences.element;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.SortedSet;

import com.bosch.caltool.apic.ws.ProjectIdCardChangedAttributeType;
import com.bosch.caltool.apic.ws.differences.adapter.PIDCChangedHistAdapter;
import com.bosch.caltool.apic.ws.differences.adapter.PIDCChangedSubVariantHistAdapterType;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;


/**
 * @author imi2si
 */
public class ElementDifferencesSubVariant extends ElementDifferences {

  /**
   * @param oldPidcChangeNumber
   * @param newPidcChangeNumber
   * @param changeHistory
   * @param fromVersion
   */
  public ElementDifferencesSubVariant(final long oldPidcChangeNumber, final long newPidcChangeNumber,
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
   *
   * @throws IcdmException
   */
  @Override
  protected final void createResultSet() throws IcdmException {
    for (PidcChangeHistory entry : this.relevantHistoryEntries) {
      String key = ApicConstants.EMPTY_STRING + entry.getSvarId() + entry.getPidcVersId();
      PIDCChangedSubVariantHistAdapterType type;
      if (super.elements.containsKey(key)) {
        type = (PIDCChangedSubVariantHistAdapterType) super.elements.get(key);
        type = new PIDCChangedSubVariantHistAdapterType(type.getHistEntryOld(), entry);
      }
      else {
        type = new PIDCChangedSubVariantHistAdapterType(entry, entry);
      }

      this.elements.put(key, type);
    }
  }

  @Override
  public final PIDCChangedSubVariantHistAdapterType[] toArray() throws IcdmException {
    ArrayList<PIDCChangedSubVariantHistAdapterType> list = new ArrayList<>();

    for (Entry<String, PIDCChangedHistAdapter> entry : this.elements.entrySet()) {
      PIDCChangedSubVariantHistAdapterType attr = (PIDCChangedSubVariantHistAdapterType) entry.getValue();
      attr.removeEquals();
      addAttributes(attr);

      if (attr.isModified() || ((attr.getChangedAttributes() != null) && (attr.getChangedAttributes().length > 0))) {
        list.add(attr);
      }
    }

    return list.toArray(new PIDCChangedSubVariantHistAdapterType[list.size()]);
  }

  private void addAttributes(final PIDCChangedSubVariantHistAdapterType attribute) throws IcdmException {

    String key = ApicConstants.EMPTY_STRING + attribute.getSubVariantID() + attribute.getPidcVersion();
    ElementDifferences attr =
        new ElementDifferencesAttributesSubVariant(super.oldPidcChangeNumber, super.newPidcChangeNumber,
            getRelevantHistoryEntriesSubElements(key, Element.ATTRIBUT), attribute.getSubVariantID(), this.fromVersion);
    attr.analyzeDifferences();

    ProjectIdCardChangedAttributeType[] attrArray = (ProjectIdCardChangedAttributeType[]) attr.toArray();

    for (ProjectIdCardChangedAttributeType element : attrArray) {
      attribute.addChangedAttributes(element);
    }
  }
}
