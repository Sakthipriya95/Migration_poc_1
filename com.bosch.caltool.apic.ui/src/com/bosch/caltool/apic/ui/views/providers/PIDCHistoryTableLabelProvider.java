package com.bosch.caltool.apic.ui.views.providers;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.AttrDiffType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UseCaseSection;

/**
 * @author dmo5cob
 */
public class PIDCHistoryTableLabelProvider implements ITableLabelProvider {

  private PidcVersion pidcVrsn;

  /**
   * key - use case id, value - UseCase
   */
  private Map<Long, UseCase> useCaseDetails = new HashMap<>();
  /**
   * key - use case section id, value - UseCaseSection
   */
  private Map<Long, UseCaseSection> useCaseSectionDetails = new HashMap<>();

  /**
   * @param useCaseDetails the useCaseDetails to set
   */
  public void setUseCaseDetails(final Map<Long, UseCase> useCaseDetails) {
    this.useCaseDetails = useCaseDetails;
  }

  /**
   * @param useCaseSectionDetails the useCaseSectionDetails to set
   */
  public void setUseCaseSectionDetails(final Map<Long, UseCaseSection> useCaseSectionDetails) {
    this.useCaseSectionDetails = useCaseSectionDetails;
  }

  /**
   * @return the pidcVrsn
   */
  public PidcVersion getPidcVrsn() {
    return this.pidcVrsn;
  }

  /**
   * @param pidcVrsn the pidcVrsn to set
   */
  public void setPidcVrsn(final PidcVersion pidcVrsn) {
    this.pidcVrsn = pidcVrsn;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isLabelProperty(final Object element, final String property) {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  /**
   * {@inheritDoc}
   */
  @Override
  public Image getColumnImage(final Object element, final int columnIndex) {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getColumnText(final Object element, final int columnIndex) {

    String result = ApicConstants.EMPTY_STRING;
    if (element instanceof AttrDiffType) {

      AttrDiffType attr = (AttrDiffType) element;
      switch (columnIndex) {
        // change no:
        case ApicUiConstants.COLUMN_INDEX_0:
          result = String.valueOf(attr.getVersionId());
          break;
        // pidc version
        case ApicUiConstants.COLUMN_INDEX_1:
          result = this.pidcVrsn.getName();
          break;
        // Level column
        case ApicUiConstants.COLUMN_INDEX_2:
          result = attr.getLevel();
          break;
        // Attr name
        case ApicUiConstants.COLUMN_INDEX_3:
          result = caseAttrName(attr);
          break;
        // Change item
        case ApicUiConstants.COLUMN_INDEX_4:
          result = attr.getChangedItem();
          break;
        // old value
        case ApicUiConstants.COLUMN_INDEX_5:
          // ICDM-2485 P1.27.118
          result = getYesNoDisplay(attr.getOldValue());
          break;
        // new value
        case ApicUiConstants.COLUMN_INDEX_6:
          // ICDM-2485 P1.27.118
          result = getYesNoDisplay(attr.getNewValue());
          break;
        // modified name
        case ApicUiConstants.COLUMN_INDEX_7:
          result = attr.getModifiedName();
          break;
        // modified date
        case ApicUiConstants.COLUMN_INDEX_8:
          result = formatModifiedDate(attr);
          break;
        default:
          result = "";
          break;
      }
    }
    return result;
  }

  /**
   * @param result
   * @param attr
   * @return
   */
  private String formatModifiedDate(final AttrDiffType attr) {
    String result = ApicConstants.EMPTY_STRING;
    try {
      result = ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_15, attr.getModifiedDate(), DateFormat.DATE_FORMAT_08);
    }
    catch (ParseException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return result;
  }

  /**
   * @param attr
   * @return
   */
  private String caseAttrName(final AttrDiffType attr) {
    String result;
    CommonDataBO cmnData = new CommonDataBO();
    if (cmnData.getLanguage() == Language.ENGLISH) {
      result = attr.getAttribute().getNameEng();
    }
    else {// GERMAN:
      result = attr.getAttribute().getNameGer();
      if (CommonUtils.isEmptyString(result)) {
        result = attr.getAttribute().getNameEng();
      }
    }
    // iCDM-2614
    if (attr.isFocusMatrixChange() && "Focus Matrix".equals(attr.getLevel())) {

      String useCaseItemName;

      if ((this.useCaseDetails.get(attr.getUseCaseId()) == null) ||
          (this.useCaseSectionDetails.get(attr.getUseCaseSectionId()) == null)) {
        useCaseItemName = "";
      }
      else if (cmnData.getLanguage() == Language.ENGLISH) {
        useCaseItemName = this.useCaseSectionDetails.get(attr.getUseCaseSectionId()).getNameEng();
      }
      else {
        useCaseItemName = this.useCaseSectionDetails.get(attr.getUseCaseSectionId()).getNameGer();
        if (CommonUtils.isEmptyString(useCaseItemName)) {
          useCaseItemName = this.useCaseSectionDetails.get(attr.getUseCaseSectionId()).getName();
        }
      }

      if (CommonUtils.isEmptyString(useCaseItemName)) {
        if (cmnData.getLanguage() == Language.ENGLISH) {
          useCaseItemName = this.useCaseDetails.get(attr.getUseCaseId()).getNameEng();
        }
        else {
          useCaseItemName = this.useCaseDetails.get(attr.getUseCaseId()).getNameGer();
          if (CommonUtils.isEmptyString(useCaseItemName)) {
            useCaseItemName = this.useCaseDetails.get(attr.getUseCaseId()).getName();
          }
        }
      }

      useCaseItemName = (null == useCaseItemName) ? "" : CommonUtils.concatenate(" / ", useCaseItemName);
      result = result + useCaseItemName;
    }

    return result;
  }

  /**
   * @param codeYesNo input
   * @return Yes or No or input
   */
  // ICDM-2485 P1.27.118
  private static String getYesNoDisplay(final String codeYesNo) {
    if (null == codeYesNo) {
      return "";
    }
    else if (CommonUtilConstants.CODE_YES.equals(codeYesNo)) {
      return CommonUtilConstants.DISPLAY_YES;
    }
    else if (CommonUtilConstants.CODE_NO.equals(codeYesNo)) {
      return CommonUtilConstants.DISPLAY_NO;
    }
    return codeYesNo;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addListener(final ILabelProviderListener arg0) {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    // TODO Auto-generated method stub

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void removeListener(final ILabelProviderListener arg0) {
    // TODO Auto-generated method stub

  }


}
