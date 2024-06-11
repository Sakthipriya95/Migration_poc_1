package com.bosch.caltool.apic.ui.views.providers;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

import com.bosch.caltool.apic.ui.editors.pages.AttributesPage;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;

/**
 * @author dmo5cob
 */
public class AttributesColumnLabelProvider extends ColumnLabelProvider {


  /**
   * AttributesPage instance
   */
  private final AttributesPage attrPage;
  /**
   * col index
   */
  private final int columIndex;


  /**
   * @param attrPage AttributesPage instance
   * @param columIndex column index
   */
  public AttributesColumnLabelProvider(final AttributesPage attrPage, final int columIndex) {
    super();
    this.attrPage = attrPage;
    this.columIndex = columIndex;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Color getBackground(final Object element) {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Color getForeground(final Object element) {
    Attribute attr = (Attribute) element;
    // check if attr is deleted
    if (attr.isDeleted()) {
      return this.attrPage.getAttrTableViewer().getGrid().getDisplay().getSystemColor(SWT.COLOR_RED);

    }
    return this.attrPage.getAttrTableViewer().getGrid().getDisplay().getSystemColor(SWT.COLOR_BLACK);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getText(final Object element) {
    String result = "";
    if (element instanceof Attribute) {

      Attribute attr = (Attribute) element;
      switch (this.columIndex) {
        // Attr name
        case ApicUiConstants.COLUMN_INDEX_0:
          result = attr.getName();
          break;
        // attr desc
        case ApicUiConstants.COLUMN_INDEX_1:
          result = attr.getDescription();
          break;
        // Value type
        case ApicUiConstants.COLUMN_INDEX_2:
          result = attr.getValueType();
          break;
        // is normalised
        case ApicUiConstants.COLUMN_INDEX_3:
          result = attr.isNormalized() ? CommonUtilConstants.DISPLAY_YES : CommonUtilConstants.DISPLAY_NO;
          break;
        // is mandatory
        case ApicUiConstants.COLUMN_INDEX_4:
          result = attr.isMandatory() ? CommonUtilConstants.DISPLAY_YES : CommonUtilConstants.DISPLAY_NO;
          break;
        // Unit
        case ApicUiConstants.COLUMN_INDEX_5:
          result = attr.getUnit();
          break;
        // Format
        case ApicUiConstants.COLUMN_INDEX_6:
          result = attr.getFormat();
          break;
        // has Part num
        case ApicUiConstants.COLUMN_INDEX_7:
          result = attr.isWithPartNumber() ? CommonUtilConstants.DISPLAY_YES : CommonUtilConstants.DISPLAY_NO;
          break;
        // has spec link
        case ApicUiConstants.COLUMN_INDEX_8:
          result = attr.isWithSpecLink() ? CommonUtilConstants.DISPLAY_YES : CommonUtilConstants.DISPLAY_NO;
          break;
        // Attr class
        case ApicUiConstants.COLUMN_INDEX_9:

          result = attr.getCharStr();
          break;
        // is attr external
        case ApicUiConstants.COLUMN_INDEX_10:
          result = attr.isExternal() ? CommonUtilConstants.DISPLAY_YES : CommonUtilConstants.DISPLAY_NO;
          break;
        // is val external
        case ApicUiConstants.COLUMN_INDEX_11:
          result = attr.isExternalValue() ? CommonUtilConstants.DISPLAY_YES : CommonUtilConstants.DISPLAY_NO;
          break;
        // ICDM-1560
        case ApicUiConstants.COLUMN_INDEX_12:
          result = attr.getEadmName();
          break;
        case ApicUiConstants.COLUMN_INDEX_13:
          result = attr.isAddValByUserFlag() ? CommonUtilConstants.DISPLAY_YES : CommonUtilConstants.DISPLAY_NO;
          break;
        default:
          result = "";
          break;
      }
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTipText(final Object element) {
    String result = "";
    if (element instanceof Attribute) {
//      typecast element to attribute type
      Attribute attr = (Attribute) element;
      switch (this.columIndex) {
        // attr name
        case ApicUiConstants.COLUMN_INDEX_0:
          result = attr.getName();
          break;
        // desc
        case ApicUiConstants.COLUMN_INDEX_1:
          result = attr.getDescription();
          break;
        // val type
        case ApicUiConstants.COLUMN_INDEX_2:
          result = attr.getValueType();
          break;
        default:
          result = "";
          break;
      }
    }
    return result;
  }

}