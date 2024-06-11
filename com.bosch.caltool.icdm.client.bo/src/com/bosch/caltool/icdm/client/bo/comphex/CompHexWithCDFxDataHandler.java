/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.comphex;

import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.client.bo.a2l.A2LEditorDataProvider;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.a2l.PidcA2LBO;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.CdrReport;
import com.bosch.caltool.icdm.model.cdr.CdrReportQnaireRespWrapper;
import com.bosch.caltool.icdm.model.comphex.CompHexResponse;
import com.bosch.caltool.icdm.model.comphex.CompHexStatistics;
import com.bosch.caltool.icdm.model.comphex.CompHexWithCDFParam;

/**
 * ICDM-2496 class to store the result of HEX and CDFx comparsion.
 *
 * @author mkl2cob
 */
public class CompHexWithCDFxDataHandler {

  /** list of CompHEXWtihCDFParam. */
  private SortedSet<CompHexWithCDFParam> compareParamSet;

  /** The comp hex response. */
  private final CompHexResponse compHexResponse;

  /** The hex file pth. */
  private final String hexFilePth;

  /** The a 2 l editor DP. */
  private final A2LEditorDataProvider a2lEditorDP;

  /** The pidc variant. */
  private final PidcVariant pidcVariant;

  /** The cdr report data. */
  private CdrReportDataHandler cdrReportData;

  private final ConcurrentMap<String, CalData> calDataObjectsFromHex;

  private String cssdExcelReportPath = null;

  private CdrReportQnaireRespWrapper reportQnaireRespWrapper;

  /**
   * The Enum SortColumns.
   *
   * @author Sort the Parametres
   */
  public enum SortColumns {

                           /** Sort Based On the Name. */
                           SORT_PARAM_COMPLI,

                           /** Sort Based On the Name. */
                           SORT_PARAM_NAME,

                           /** Sort Based on FUNC name. */
                           SORT_FUNC_NAME,

                           /** Sort Based on FUNC name. */
                           SORT_FUNC_VERS,

                           /** Sort Based on Type. */
                           SORT_PARAM_TYPE,

                           /** Sort Based on parameter reviewed. */
                           SORT_PARAM_RVWD,

                           /** Sort Based on cal data objects equal. */
                           SORT_PARAM_EQUAL,

                           /** Sort Based on CDF cal data object. */
                           SORT_CDF_CALDATA,

                           /** Sort Based on HEX caldata object. */
                           SORT_HEX_CALDATA,

                           /** Compli/Shape Result. */
                           SORT_COMPLI_SHAPE_RESULT,

                           /** Review Score. */
                           SORT_REVIEW_SCORE,

                           /** Review Comments. */
                           SORT_REVIEW_COMMENTS,

                           /** Review Description */
                           SORT_REVIEW_DESCRIPTION,


  }

  /**
   * Instantiates a new comp hex with CD fx data handler.
   *
   * @param hexFilePth the hex file pth
   * @param calDataObjectsFromHex
   * @param a2lEditorDP the a 2 l editor DP
   * @param pidcData the pidc data
   * @param pidcVariant the pidc variant
   * @param compHexResponse the comp hex response
   */
  public CompHexWithCDFxDataHandler(final String hexFilePth, final ConcurrentMap<String, CalData> calDataObjectsFromHex,
      final A2LEditorDataProvider a2lEditorDP, final PidcVariant pidcVariant, final CompHexResponse compHexResponse) {
    this.pidcVariant = pidcVariant;
    this.hexFilePth = hexFilePth;
    this.a2lEditorDP = a2lEditorDP;
    this.compHexResponse = compHexResponse;
    this.calDataObjectsFromHex = calDataObjectsFromHex;
  }

  /**
   * Gets the comp hex result param.
   *
   * @return the comp hex result param
   */
  public SortedSet<CompHexWithCDFParam> getCompHexResultParamSet() {
    if (this.compareParamSet == null) {
      this.compareParamSet = this.compHexResponse.getCompHexResult();
    }
    return this.compareParamSet;
  }

  /**
   * Gets the hex file name.
   *
   * @return hex file name without full path info
   */
  public String getHexFileName() {
    String pattern = Pattern.quote(System.getProperty("file.separator"));
    String[] split = this.hexFilePth.split(pattern);
    int length = split.length;
    return split[length - 1];
  }

  /**
   * Compare to.
   *
   * @param param1 the param 1
   * @param param2 param2
   * @param sortColumn sortColumn
   * @return the Compare Int
   */
  public int compareTo(final CompHexWithCDFParam param1, final CompHexWithCDFParam param2,
      final SortColumns sortColumn) {
    int compareResult;

    switch (sortColumn) {
      case SORT_PARAM_COMPLI:
        compareResult = ApicUtil.compare(param1.isCompli(), param2.isCompli());
        break;

      case SORT_PARAM_TYPE:
        compareResult = ApicUtil.compare(param1.getParamType().getText(), param2.getParamType().getText());
        break;

      case SORT_PARAM_NAME:
        compareResult = ApicUtil.compare(param1.getParamName(), param2.getParamName());
        break;

      case SORT_FUNC_NAME:
        compareResult = ApicUtil.compare(param1.getFuncName(), param2.getFuncName());
        break;

      case SORT_FUNC_VERS:
        compareResult = ApicUtil.compare(param1.getFuncVers(), param2.getFuncVers());
        break;

      case SORT_PARAM_RVWD:
        compareResult = ApicUtil.compare(param1.isReviewed(), param2.isReviewed());
        break;

      case SORT_PARAM_EQUAL:
        compareResult = ApicUtil.compare(
            param1.isNeverReviewed() ? ApicConstants.NOT_APPLICABLE : Boolean.toString(param1.isEqual()),
            param2.isNeverReviewed() ? ApicConstants.NOT_APPLICABLE : Boolean.toString(param2.isEqual()));
        break;

      case SORT_HEX_CALDATA:
        compareResult = compareHexCalData(param1, param2);
        break;

      case SORT_CDF_CALDATA:
        compareResult = compareCdfCalData(param1, param2);
        break;

      case SORT_COMPLI_SHAPE_RESULT:
        compareResult = ApicUtil.compare(param1.getCompliResult(), param2.getCompliResult());
        break;

      case SORT_REVIEW_SCORE:
        compareResult = ApicUtil.compare(param1.getReviewScore(), param2.getReviewScore());
        break;

      case SORT_REVIEW_COMMENTS:
        compareResult = ApicUtil.compare(param1.getLatestReviewComments(), param2.getLatestReviewComments());
        break;

      case SORT_REVIEW_DESCRIPTION:
        compareResult = ApicUtil.compare(this.cdrReportData.getReviewResultName(param1.getParamName()),
            this.cdrReportData.getReviewResultName(param2.getParamName()));
        break;

      default:
        compareResult = 0;
        break;
    }
    return compareResult;
  }

  /**
   * @param param1
   * @param param2
   * @return
   */
  private int compareCdfCalData(final CompHexWithCDFParam param1, final CompHexWithCDFParam param2) {
    int compareResult;
    compareResult = ApicUtil.compare(getCdfxCalDataPhyVal(param1), getCdfxCalDataPhyVal(param2));
    return compareResult;
  }

  /**
   * @param param1
   * @return
   */
  private String getCdfxCalDataPhyVal(final CompHexWithCDFParam param1) {
    return param1.isNeverReviewed() && CommonUtils.isNotNull(param1.getCdfxCalDataPhySimpleDispValue()) ? ""
        : param1.getCdfxCalDataPhySimpleDispValue();
  }

  /**
   * @param param1
   * @param param2
   * @return
   */
  private int compareHexCalData(final CompHexWithCDFParam param1, final CompHexWithCDFParam param2) {
    int compareResult;
    compareResult = ApicUtil.compare(
        CommonUtils.isNotNull(param1.getHexCalDataPhySimpleDispValue()) ? param1.getHexCalDataPhySimpleDispValue() : "",
        CommonUtils.isNotNull(param2.getHexCalDataPhySimpleDispValue()) ? param2.getHexCalDataPhySimpleDispValue()
            : "");
    return compareResult;
  }

  /**
   * Gets the comp hex statitics.
   *
   * @return the comp hex statitics
   */
  public CompHexStatistics getCompHexStatitics() {
    return this.compHexResponse.getCompHexStatistics();
  }

  /**
   * Gets the cdr report.
   *
   * @return the cdr report
   */
  public CdrReport getCdrReport() {
    return this.compHexResponse.getCdrReport();
  }

  /**
   * Gets the cdr report data.
   *
   * @return the cdr report data
   */
  public CdrReportDataHandler getCdrReportData() {
    boolean fetchCheckVal = true;// true as the check value needs to be exported
    // 1 is given as we need only
    int maxReviews = 1;
    if (this.cdrReportData == null) {

      CdrReport cdrRprt = this.compHexResponse.getCdrReport();
      PidcA2LBO pidcA2LBO = this.a2lEditorDP.getPidcA2LBO();
      cdrRprt.setPidcA2l(pidcA2LBO.getPidcA2l());
      cdrRprt.setPidcVersion(pidcA2LBO.getPidcVersion());

      this.cdrReportData =
          new CdrReportDataHandler(this.a2lEditorDP, this.pidcVariant, maxReviews, cdrRprt, fetchCheckVal, null, null);

      if (null != this.reportQnaireRespWrapper) {
        this.cdrReportData.setCdrReportQnaireRespWrapper(this.reportQnaireRespWrapper);
      }
    }
    return this.cdrReportData;
  }

  /**
   * reviewed parameters with bosch responsibility count and total parameter count in an array
   *
   * @return percentage
   */
  public String getReviewedParameterWithBoschResp() {
    return this.cdrReportData.getReviewedParameterWithBoschResp(getAllA2lParamNames());
  }

  /**
   * the count of number of parameters in bosch responsibility
   *
   * @return count of the parameters
   */
  public int getParameterInBoschResp() {
    return this.cdrReportData.getParameterInBoschResp(getAllA2lParamNames());
  }


  /**
   * reviewed parameters with bosch responsibility count for completed questionnaire and total parameter count in an
   * array
   *
   * @return percentage
   */
  public String getRvwParamWithBoschRespForCompletedQnaire() {
    return this.cdrReportData.getRvwParamWithBoschRespForCompletedQnaire(getAllA2lParamNames());
  }

  /**
   * the count of number of parameters in bosch responsibility reviewed
   *
   * @return count of the parameters reviewed
   */
  public int getParameterInBoschRespRvwed() {
    return this.cdrReportData.getParameterInBoschRespRvwed(getAllA2lParamNames());
  }


  /**
   * @return count of qnaire with negaive answers count
   */
  public int getQnaireWithNegativeAnswersCount() {
    return this.cdrReportData.getQnaireWithNegativeAnswersCount();
  }

  private Set<String> getAllA2lParamNames() {
    return this.a2lEditorDP.getA2lFileInfoBO().getA2lParamMap(null).values().stream().map(A2LParameter::getName)
        .collect(Collectors.toSet());
  }


  /**
   * Gets the a 2 l param.
   *
   * @param paramName the param name
   * @return the a 2 l param
   */
  public A2LParameter getA2lParam(final String paramName) {
    return this.a2lEditorDP.getA2lFileInfoBO().getA2lParamMap(null).get(paramName);
  }


  /**
   * Gets the a 2 l file.
   *
   * @return the a 2 l file
   */
  public A2LFile getA2lFile() {
    return this.a2lEditorDP.getPidcA2LBO().getA2lFile();
  }

  /**
   * Gets the selcted var.
   *
   * @return the selcted var
   */
  public PidcVariant getSelctedVar() {
    return this.pidcVariant;
  }

  /**
   * Gets the comp hex response.
   *
   * @return the comp hex response
   */
  public CompHexResponse getCompHexResponse() {
    return this.compHexResponse;
  }

  /**
   * Gets the referene id.
   *
   * @return the referene id
   */
  public String getRefereneId() {
    return this.compHexResponse.getReferenceId();
  }

  /**
   * Gets the hex cal data.
   *
   * @param compHEXWtihCDFParam the comp HEX wtih CDF param
   * @return the hex cal data
   */
  public CalData getHexCalData(final CompHexWithCDFParam compHEXWtihCDFParam) {
    if (this.calDataObjectsFromHex == null) {
      return null;
    }
    return this.calDataObjectsFromHex.get(compHEXWtihCDFParam.getParamName());
  }

  /**
   * Gets the cdfx cal data.
   *
   * @param compHEXWtihCDFParam the comp HEX wtih CDF param
   * @return the cdfx cal data
   */
  public CalData getCdfxCalData(final CompHexWithCDFParam compHEXWtihCDFParam) {
    if (getCdrReport() == null) {
      return null;
    }
    return getCdrReportData().getParamCheckedVal(compHEXWtihCDFParam.getParamName(), 0);
  }


  /**
   * @return the cssdExcelReportPath
   */
  public String getCssdExcelReportPath() {
    return this.cssdExcelReportPath;
  }


  /**
   * @param cssdExcelReportPath the cssdExcelReportPath to set
   */
  public void setCssdExcelReportPath(final String cssdExcelReportPath) {
    this.cssdExcelReportPath = cssdExcelReportPath;
  }

  /**
   * @param compareHexParam param in compare hex
   * @return Result value
   */
  public String getResult(final CompHexWithCDFParam compareHexParam) {
    return this.cdrReportData.getResult(compareHexParam).toString();
  }


  /**
   * @return the reportQnaireRespWrapper
   */
  public CdrReportQnaireRespWrapper getReportQnaireRespWrapper() {
    return this.reportQnaireRespWrapper;
  }


  /**
   * @param reportQnaireRespWrapper the reportQnaireRespWrapper to set
   */
  public void setReportQnaireRespWrapper(final CdrReportQnaireRespWrapper reportQnaireRespWrapper) {
    this.reportQnaireRespWrapper = reportQnaireRespWrapper;
  }
}
