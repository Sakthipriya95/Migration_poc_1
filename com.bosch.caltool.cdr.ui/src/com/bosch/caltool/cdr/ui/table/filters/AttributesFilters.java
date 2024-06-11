package com.bosch.caltool.cdr.ui.table.filters;

import com.bosch.caltool.icdm.client.bo.cdr.QuesDepnAttributeRow;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;

/**
 * This class handles the common filters for the Attributes table viewer
 */
public class AttributesFilters extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    boolean flag = false;
    // attr instance
    if (element instanceof Attribute) {
      final Attribute attribute = (Attribute) element;
      flag = checkText(attribute);
    }
    // question dep attr instance
    else if (element instanceof QuesDepnAttributeRow) {
      final QuesDepnAttributeRow attribute = (com.bosch.caltool.icdm.client.bo.cdr.QuesDepnAttributeRow) element;
      flag = checkText(attribute.getAttribute());
    }
    return flag;

  }

  /**
   * @param attribute attribute
   * @return True if the properties are matching.
   */
  private boolean checkText(final Attribute attribute) {
    boolean flag = false;
    // Match the Text with the Attribute properties. If any one matches return true.
    if (validateNameDesc(attribute) ||
        validateValueUnit(attribute) || validatePartNumSpecLink(attribute) || validateExtVal(attribute)) {
      flag = true;
    }
    return flag;
  }

  /**
   * @param attribute
   * @return
   */
  private boolean validateExtVal(final Attribute attribute) {
    return matchText(CommonUtils.getDisplayText(attribute.isExternal())) ||
            matchText(CommonUtils.getDisplayText(attribute.isExternalValue())) || (matchText(attribute.getCharStr()));
  }

  /**
   * @param attribute
   * @return
   */
  private boolean validatePartNumSpecLink(final Attribute attribute) {
    return matchText(attribute.getFormat()) || matchText(CommonUtils.getDisplayText(attribute.isWithPartNumber())) ||
            matchText(CommonUtils.getDisplayText(attribute.isWithSpecLink()));
  }

  /**
   * @param attribute
   * @return
   */
  private boolean validateValueUnit(final Attribute attribute) {
    return matchText(attribute.getValueType()) || matchText(CommonUtils.getDisplayText(attribute.isNormalized())) ||
            matchText(CommonUtils.getDisplayText(attribute.isMandatory())) || matchText(attribute.getUnit());
  }

  /**
   * @param attribute
   * @return
   */
  private boolean validateNameDesc(final Attribute attribute) {
    return matchText(attribute.getNameEng()) || matchText(attribute.getNameGer()) ||
            matchText(attribute.getDescriptionEng()) || matchText(attribute.getDescriptionGer());
  }
}