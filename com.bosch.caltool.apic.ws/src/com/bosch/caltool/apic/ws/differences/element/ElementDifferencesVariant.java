package com.bosch.caltool.apic.ws.differences.element;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.SortedSet;

import com.bosch.caltool.apic.ws.ProjectIdCardChangedAttributeType;
import com.bosch.caltool.apic.ws.ProjectIdCardChangedSubVarType;
import com.bosch.caltool.apic.ws.differences.adapter.PIDCChangedHistAdapter;
import com.bosch.caltool.apic.ws.differences.adapter.PIDCChangedVariantHistAdapterType;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;


/**
 * @author imi2si
 */
public class ElementDifferencesVariant extends ElementDifferences {

  /**
   * @param oldPidcChangeNumber
   * @param newPidcChangeNumber
   * @param changeHistory
   * @param fromVersion
   */
  public ElementDifferencesVariant(final long oldPidcChangeNumber, final long newPidcChangeNumber,
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
      if (null == entry.getFmVersId()) {
        String key = ApicConstants.EMPTY_STRING + entry.getVarId() + entry.getPidcVersId();
        if (this.fromVersion) {
          // Consider only records that are in the requested range of change numbers
          if ((entry.getPidcVersVers() >= this.oldPidcChangeNumber) &&
              ((this.newPidcChangeNumber == -1) || (entry.getPidcVersVers() <= this.newPidcChangeNumber))) {

            // To be relevant for variants, the PIDC and Attribute ID must always be set
            if ((entry.getPidcId() != null) && (entry.getVarId() != null) &&
                entry.getPidcId().equals(idOfSuperiorElement)) {

              // Relevant records for the variants
              if ((entry.getAttrId() == null) && (entry.getSvarId() == null)) {
                this.relevantHistoryEntries.add(entry);
              }
              else if ((entry.getAttrId() != null) && (entry.getSvarId() == null)) {

                putRelevantHistoryEntriesSubElements(key, Element.ATTRIBUT, entry);
              }
              else if (entry.getSvarId() != null) {
                putRelevantHistoryEntriesSubElements(key, Element.SUB_VARIANT, entry);
              }
            }
          }
        }
        else {
          // Consider only records that are in the requested range of change numbers
          if ((entry.getPidcVersion() >= this.oldPidcChangeNumber) &&
              ((this.newPidcChangeNumber == -1) || (entry.getPidcVersion() <= this.newPidcChangeNumber))) {
            // To be relevant for variants, the PIDC and Attribute ID must always be set
            if ((entry.getPidcId() != null) && (entry.getVarId() != null) &&
                entry.getPidcId().equals(idOfSuperiorElement)) {

              // Relevant records for the variants
              if ((entry.getAttrId() == null) && (entry.getSvarId() == null)) {
                this.relevantHistoryEntries.add(entry);
              }
              else if ((entry.getAttrId() != null) && (entry.getSvarId() == null)) {

                putRelevantHistoryEntriesSubElements(key, Element.ATTRIBUT, entry);
              }
              else if (entry.getSvarId() != null) {
                putRelevantHistoryEntriesSubElements(key, Element.SUB_VARIANT, entry);
              }
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
      String key = ApicConstants.EMPTY_STRING + entry.getVarId() + entry.getPidcVersId();
      PIDCChangedVariantHistAdapterType type;
      if (super.elements.containsKey(key)) {
        type = (PIDCChangedVariantHistAdapterType) super.elements.get(key);
        type = new PIDCChangedVariantHistAdapterType(type.getHistEntryOld(), entry);
      }
      else {
        type = new PIDCChangedVariantHistAdapterType(entry, entry);
      }

      this.elements.put(key, type);
    }
  }

  @Override
  public final PIDCChangedVariantHistAdapterType[] toArray() throws IcdmException {
    ArrayList<PIDCChangedVariantHistAdapterType> list = new ArrayList<>();

    for (Entry<String, PIDCChangedHistAdapter> entry : this.elements.entrySet()) {
      PIDCChangedVariantHistAdapterType attr = (PIDCChangedVariantHistAdapterType) entry.getValue();
      attr.removeEquals();

      addAttributes(attr);

      addSubVariants(attr);

      if (attr.isModified() || validateChangedAttrs(attr) ||
          validateSubvars(attr)) {
        list.add(attr);
      }
    }
    return list.toArray(new PIDCChangedVariantHistAdapterType[list.size()]);
  }

  /**
   * @param attr
   * @return
   */
  private boolean validateSubvars(PIDCChangedVariantHistAdapterType attr) {
    return (attr.getChangedSubVariants() != null) && (attr.getChangedSubVariants().length > 0);
  }

  /**
   * @param attr
   * @return
   */
  private boolean validateChangedAttrs(PIDCChangedVariantHistAdapterType attr) {
    return (attr.getChangedAttributes() != null) && (attr.getChangedAttributes().length > 0);
  }

  private void addSubVariants(final PIDCChangedVariantHistAdapterType attribute) throws IcdmException {

    String key = ApicConstants.EMPTY_STRING + attribute.getVariantID() + attribute.getPidcVersion();
    ElementDifferences attr = new ElementDifferencesSubVariant(super.oldPidcChangeNumber, super.newPidcChangeNumber,
        getRelevantHistoryEntriesSubElements(key, Element.SUB_VARIANT), attribute.getVariantID(), this.fromVersion);

    attr.analyzeDifferences();

    ProjectIdCardChangedSubVarType[] attrArray = (ProjectIdCardChangedSubVarType[]) attr.toArray();

    for (ProjectIdCardChangedSubVarType element : attrArray) {
      attribute.addChangedSubVariants(element);
    }
  }

  private void addAttributes(final PIDCChangedVariantHistAdapterType attribute) throws IcdmException {

    String key = ApicConstants.EMPTY_STRING + attribute.getVariantID() + attribute.getPidcVersion();
    ElementDifferences attr =
        new ElementDifferencesAttributesVariant(super.oldPidcChangeNumber, super.newPidcChangeNumber,
            getRelevantHistoryEntriesSubElements(key, Element.ATTRIBUT), attribute.getVariantID(), this.fromVersion);
    attr.analyzeDifferences();

    ProjectIdCardChangedAttributeType[] attrArray = (ProjectIdCardChangedAttributeType[]) attr.toArray();

    for (ProjectIdCardChangedAttributeType element : attrArray) {
      attribute.addChangedAttributes(element);
    }
  }
}
