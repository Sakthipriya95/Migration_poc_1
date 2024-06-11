package com.bosch.caltool.icdm.bo.apic.pidc.differences.element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.SortedSet;

import com.bosch.caltool.icdm.bo.apic.pidc.differences.adapter.PIDCChangedHistAdapterRest;
import com.bosch.caltool.icdm.bo.apic.pidc.differences.adapter.PIDCChangedVariantHistAdapterRestType;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangedAttrType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangedSubVarType;


/**
 * @author imi2si
 */
public class ElementDifferencesVariantRest extends ElementDifferencesRest {

  /**
   * @param oldPidcChangeNumber old pidc change number
   * @param newPidcChangeNumber new pidc change number
   * @param changeHistory set of pidc change history
   * @param superiorElementId superior element id
   * @param fromVersion boolean
   */
  public ElementDifferencesVariantRest(final Long oldPidcChangeNumber, final Long newPidcChangeNumber,
      final SortedSet<PidcChangeHistory> changeHistory, final Long superiorElementId, final boolean fromVersion) {
    super(oldPidcChangeNumber, newPidcChangeNumber, changeHistory, superiorElementId, fromVersion);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void createElementsList(final SortedSet<PidcChangeHistory> chngeHistory,
      final Long superiorElementId) {

    for (PidcChangeHistory entry : chngeHistory) {
      if (null == entry.getFmVersId()) {
        String key = ApicConstants.EMPTY_STRING + entry.getVarId() + entry.getPidcVersId();
        if (this.fromVersion) {
          createElementListForPidcVersVersion(superiorElementId, entry, key);
        }
        else {
          createElementListForPidcVerison(superiorElementId, entry, key);
        }
      }
    }
  }

  /**
   * @param superiorElementId
   * @param entry
   * @param key
   */
  private void createElementListForPidcVerison(final Long superiorElementId, final PidcChangeHistory entry,
      final String key) {
    // Consider only records that are in the requested range of change numbers
    if (isPidcVerValid(entry) && isPidcAndVarValid(superiorElementId, entry)) {
      // To be relevant for variants, the PIDC and Attribute ID must always be set
      putRelevantHistoryEntriesToMap(entry, key);
    }
  }

  /**
   * @param superiorElementId
   * @param entry
   * @param key
   */
  private void createElementListForPidcVersVersion(final Long superiorElementId, final PidcChangeHistory entry,
      final String key) {
    // Consider only records that are in the requested range of change numbers
    if ((entry.getPidcVersVers() >= this.oldPidcChangeNumber) &&
        ((this.newPidcChangeNumber == -1) || (entry.getPidcVersVers() <= this.newPidcChangeNumber)) &&
        isPidcAndVarValid(superiorElementId, entry)) {
      // To be relevant for variants, the PIDC and Attribute ID must always be set
      // Relevant records for the variants
      putRelevantHistoryEntriesToMap(entry, key);

    }
  }

  /**
   * @param entry
   * @param key
   */
  private void putRelevantHistoryEntriesToMap(final PidcChangeHistory entry, final String key) {
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

  /**
   * @param superiorElementId
   * @param entry
   * @return
   */
  private boolean isPidcAndVarValid(final Long superiorElementId, final PidcChangeHistory entry) {
    return (entry.getPidcId() != null) && (entry.getVarId() != null) && entry.getPidcId().equals(superiorElementId);
  }

  /**
   * @param entry
   * @return
   */
  private boolean isPidcVerValid(final PidcChangeHistory entry) {
    return (entry.getPidcVersion() >= this.oldPidcChangeNumber) &&
        ((this.newPidcChangeNumber == -1) || (entry.getPidcVersion() <= this.newPidcChangeNumber));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void createResultSet() {
    for (PidcChangeHistory entry : this.relevantHistoryEntries) {
      String key = ApicConstants.EMPTY_STRING + entry.getVarId() + entry.getPidcVersId();
      PIDCChangedVariantHistAdapterRestType type;
      if (super.elements.containsKey(key)) {
        type = (PIDCChangedVariantHistAdapterRestType) super.elements.get(key);
        type = new PIDCChangedVariantHistAdapterRestType(type.getHistEntryOld(), entry);
      }
      else {
        type = new PIDCChangedVariantHistAdapterRestType(entry, entry);
      }

      this.elements.put(key, type);
    }

    // Iterate through the outer keys in the relevantHistoryEntriesSubElements map
    for (String outerKey : this.relevantHistoryEntriesSubElements.keySet()) {
      // Retrieve the inner map associated with the current outer key
      HashMap<Element, SortedSet<PidcChangeHistory>> innerMap = this.relevantHistoryEntriesSubElements.get(outerKey);

      // Iterate through the inner keys in the inner map
      for (Element innerKey : innerMap.keySet()) {
        // Retrieve the sorted set of PidcChangeHistory objects associated with the current inner key
        SortedSet<PidcChangeHistory> historySet = innerMap.get(innerKey);

        // Process each PidcChangeHistory object in the sorted set
        for (PidcChangeHistory entry : historySet) {
          // Generate a unique key based on the inner key, varId, and pidcVersId
          String key = ApicConstants.EMPTY_STRING + innerKey + entry.getVarId() + entry.getPidcVersId();

          PIDCChangedVariantHistAdapterRestType type;

          // Check if the key already exists in the elements map
          if (super.elements.containsKey(key)) {
            // If the key exists, update the existing PIDCChangedVariantHistAdapterRestType
            type = (PIDCChangedVariantHistAdapterRestType) super.elements.get(key);
            type = new PIDCChangedVariantHistAdapterRestType(type.getHistEntryOld(), entry);
          }
          else {
            // If the key does not exist, create a new PIDCChangedVariantHistAdapterRestType
            type = new PIDCChangedVariantHistAdapterRestType(entry, entry);
          }

          // Add the type to the elements map
          this.elements.put(key, type);
        }
      }
    }
  }


  @Override
  public final PIDCChangedHistAdapterRest[] toArray() throws DataException {
    ArrayList<PIDCChangedVariantHistAdapterRestType> list = new ArrayList<>();

    for (Entry<String, PIDCChangedHistAdapterRest> entry : this.elements.entrySet()) {
      String key = entry.getKey();
      if (!key.contains("ATTRIBUT")) {
        PIDCChangedVariantHistAdapterRestType attr = (PIDCChangedVariantHistAdapterRestType) entry.getValue();
        attr.removeEquals();

        addAttributes(attr);

        addSubVariants(attr);

        if (attr.isModified() || CommonUtils.isNotEmpty(attr.getChangedAttrList()) ||
            CommonUtils.isNotEmpty(attr.getChangedSubVariantList())) {
          if (key.contains("SUB_VARIANT")) {

            attr.setNewValueID(-1l);
            attr.setOldValueID(-1l);

          }

          list.add(attr);
        }
      }
    }
    return list.toArray(new PIDCChangedVariantHistAdapterRestType[list.size()]);
  }


  private void addSubVariants(final PIDCChangedVariantHistAdapterRestType attribute) throws DataException {

    String key = ApicConstants.EMPTY_STRING + attribute.getVariantId() + attribute.getPidcVersion();
    ElementDifferencesRest attr =
        new ElementDifferencesSubVariantRest(super.oldPidcChangeNumber, super.newPidcChangeNumber,
            getRelevantHistoryEntriesSubElements(key, Element.SUB_VARIANT), attribute.getVariantId(), this.fromVersion);

    attr.analyzeDifferences();

    PidcChangedSubVarType[] attrArray = (PidcChangedSubVarType[]) attr.toArray();

    for (PidcChangedSubVarType element : attrArray) {
      attribute.getChangedSubVariantList().add(element);
    }
  }

  private void addAttributes(final PIDCChangedVariantHistAdapterRestType attribute) throws DataException {

    String key = ApicConstants.EMPTY_STRING + attribute.getVariantId() + attribute.getPidcVersion();
    ElementDifferencesRest attr =
        new ElementDifferencesAttributesVariantRest(super.oldPidcChangeNumber, super.newPidcChangeNumber,
            getRelevantHistoryEntriesSubElements(key, Element.ATTRIBUT), attribute.getVariantId(), this.fromVersion);
    attr.analyzeDifferences();

    PidcChangedAttrType[] attrArray = (PidcChangedAttrType[]) attr.toArray();

    for (PidcChangedAttrType element : attrArray) {
      attribute.getChangedAttrList().add(element);
    }
  }
}
