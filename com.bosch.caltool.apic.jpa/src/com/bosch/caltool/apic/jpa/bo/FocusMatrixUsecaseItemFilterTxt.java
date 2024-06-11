/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;


/**
 * This class is used to construct the filter text required for combo-box column filtering.</br>
 * </br>
 * An instanceof check of this class is used in the <b>displayConverter's canonicalToDisplayValue()</b> method to
 * prevent the text associated with image cells.</br>
 * Text is associated with each cell to enable combo-box based filtering in the individual column filter.
 *
 * @author jvi6cob
 */
public class FocusMatrixUsecaseItemFilterTxt {


  /**
   * Constant for string <ANY COLOR>
   */
  private static final String ANY_COLOR = "<ANY COLOR>";
  private final FocusMatrixUseCaseItem focusMatrixUseCaseItem;
  private final Attribute attribute;

  /**
   * Constructor
   *
   * @param focusMatrixUseCaseItem FocusMatrixUseCaseItem
   * @param attribute Attribute
   */
  public FocusMatrixUsecaseItemFilterTxt(final FocusMatrixUseCaseItem focusMatrixUseCaseItem,
      final Attribute attribute) {
    super();
    this.focusMatrixUseCaseItem = focusMatrixUseCaseItem;
    this.attribute = attribute;
  }


  /**
   * Method used to return the filter text associated with the {@link FocusMatrixUseCaseItem}
   *
   * @return String
   */
  public String getFilterTxt() {
    StringBuilder filterText = new StringBuilder();
    FocusMatrixColorCode colorCode = this.focusMatrixUseCaseItem.getColorCode(this.attribute);
    if (colorCode != null) {
      filterText.append(" ").append(colorCode.getDisplayColorTxt());
    }
    if (null == this.focusMatrixUseCaseItem.getAttributeMapping().get(this.attribute)) {
      if (this.focusMatrixUseCaseItem.isMapped(this.attribute)) {
        if (filterText.toString().trim().isEmpty()) {
          filterText.append(" ").append("White");
        }
        filterText.append(" ").append(ANY_COLOR);
      }
    }
    if (!filterText.toString().contains(ANY_COLOR)) {
      if (this.focusMatrixUseCaseItem.getUseCaseItem().isMapped(this.attribute)) {
        if ((colorCode != null) && colorCode.getDisplayColorTxt().isEmpty()) {
          filterText.append(" ").append("White");
        }
        filterText.append(" ").append(ANY_COLOR);
      }
      else {
        filterText.append(" ").append("<NOT MAPPED>");
      }
    }
    return filterText.toString();
  }

}
