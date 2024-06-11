/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.validaterules.messagesinterfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bosch.boot.ssd.api.service.ValidateRuleInvokerService;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.checkssd.datamodel.a2lvariable.A2LVariable;
import com.bosch.checkssd.datamodel.constantfunctions.AxsvalFunc;
import com.bosch.checkssd.datamodel.constantfunctions.FIncrDecrFunc;
import com.bosch.checkssd.datamodel.constantfunctions.MPMaxMinFunc;
import com.bosch.checkssd.datamodel.constantfunctions.MaxAxsPtFunc;
import com.bosch.checkssd.datamodel.constantfunctions.NumFunc;
import com.bosch.checkssd.datamodel.constantfunctions.SrcFunc;
import com.bosch.checkssd.datamodel.constantfunctions.VblkFunc;
import com.bosch.checkssd.datamodel.mathterm.ConstFunctions;
import com.bosch.checkssd.datamodel.mathterm.IMathTermElement;
import com.bosch.checkssd.datamodel.mathterm.MathFunction;
import com.bosch.checkssd.datamodel.mathterm.SSDString;

/**
 * IPrintMessages
 *
 * @author SMN6KOR
 */
public interface IPrintMessages {

  /**
   * @return validateRule instance
   */
  ValidateRuleInvokerService getInstance();


  /**
   * @param description
   */
  default void setMessageDesc(final String description) {
    getInstance().setExistingErrors(description);
  }

  /**
   * @param lineNoAndError
   */
  default void setLineNoAndError(final Map<Integer, String> lineNoAndError) {
    getInstance().setLineNoAndError(lineNoAndError);
  }

  /**
   * @return Map
   */
  default Map<Integer, String> getLineNoAndError() {
    return getInstance().getLineNoAndError();
  }

  /**
   * @param iMathTermElement
   * @return label name and label type map
   */
  default Map<String, String> getLabelNameAndType(final IMathTermElement iMathTermElement) {
    Map<String, String> lblAndTypeMap = new HashMap<>();

    if (iMathTermElement instanceof MathFunction) {
      for (Object obj : ((MathFunction) iMathTermElement).getParameter().getMathElement()) {
        if (obj instanceof SSDString) {
          lblAndTypeMap = lblAndTypeLoop(lblAndTypeMap, obj);

        }
        lblAndTypeMap = getLabelNameAndTypeFromParam(iMathTermElement, lblAndTypeMap);
      }
    }
    else if (iMathTermElement instanceof ConstFunctions) {
      lblAndTypeMap = lblInfoForConstFunc(iMathTermElement, lblAndTypeMap);

    }
    return lblAndTypeMap;
  }


  /**
   * @param iMathTermElement
   * @param lblAndTypeMap
   * @return
   */
  default Map<String, String> getLabelNameAndTypeFromParam(final IMathTermElement iMathTermElement,
      Map<String, String> lblAndTypeMap) {
    if (((MathFunction) iMathTermElement).getBinParam() != null) {
      for (Object object : ((MathFunction) iMathTermElement).getBinParam().getMathElement()) {
        if (object instanceof SSDString) {
          lblAndTypeMap = lblAndTypeLoop(lblAndTypeMap, object);
        }
      }
    }
    return lblAndTypeMap;
  }

  /**
   * Method to get the mathElement from respective object
   *
   * @param iMathTermElement
   * @param lblAndTypeMap
   * @return label name and label type map
   */
  default Map<String, String> lblInfoForConstFunc(final IMathTermElement iMathTermElement,
      final Map<String, String> lblAndTypeMap) {
    Map<String, String> lblTypeMap = new HashMap<>();
    List<IMathTermElement> mathTerms = new ArrayList<>();

    /*
     * Checking whether IMathTermElement is NUM instance . If so , getting its mathElement
     */
    if ((iMathTermElement instanceof NumFunc) && (((NumFunc) iMathTermElement).getVariable() != null)) {
      mathTerms = ((NumFunc) iMathTermElement).getVariable().getMathElement();
    }
    /*
     * Checking whether IMathTermElement is FIncrDecrFunc instance . If so , getting its mathElement
     */
    else if ((iMathTermElement instanceof FIncrDecrFunc) && (((FIncrDecrFunc) iMathTermElement).getTerm() != null)) {
      mathTerms = ((FIncrDecrFunc) iMathTermElement).getTerm().getMathElement();
    }
    /*
     * Checking whether IMathTermElement is SrcFunc instance . If so , getting its mathElement
     */
    else if ((iMathTermElement instanceof SrcFunc) && (((SrcFunc) iMathTermElement).getVariable() != null)) {
      mathTerms = ((SrcFunc) iMathTermElement).getVariable().getMathElement();
    }
    /*
     * Checking whether IMathTermElement is MPMaxMinFunc instance . If so , getting its mathElement
     */
    else if ((iMathTermElement instanceof MPMaxMinFunc) && (((MPMaxMinFunc) iMathTermElement).getVariable() != null)) {
      mathTerms = ((MPMaxMinFunc) iMathTermElement).getVariable().getMathElement();
    } /*
       * Checking whether IMathTermElement is AxsvalFunc instance . If so , getting its mathElement
       */
    else if ((iMathTermElement instanceof AxsvalFunc) && (((AxsvalFunc) iMathTermElement).getVariable() != null)) {
      mathTerms = ((AxsvalFunc) iMathTermElement).getVariable().getMathElement();
    }
    /*
     * Checking whether IMathTermElement is VblkFunc instance . If so , getting its mathElement
     */
    else if ((iMathTermElement instanceof VblkFunc) && (((VblkFunc) iMathTermElement).getVariable() != null)) {
      mathTerms = ((VblkFunc) iMathTermElement).getVariable().getMathElement();
    }
    /*
     * Checking whether IMathTermElement is MaxAxsPtFunc instance . If so , getting its mathElement
     */
    else if ((iMathTermElement instanceof MaxAxsPtFunc) && (((MaxAxsPtFunc) iMathTermElement).getA2lVarTrm() != null)) {
      mathTerms = ((MaxAxsPtFunc) iMathTermElement).getA2lVarTrm().getMathElement();
    }
    for (Object obj : mathTerms) {
      lblTypeMap = lblAndTypeLoop(lblAndTypeMap, obj);
    }
    return lblTypeMap;
  }

  /**
   * @param lblAndTypeMap
   * @param obj
   * @return label name and label type map
   */
  default Map<String, String> lblAndTypeLoop(final Map<String, String> lblAndTypeMap, final Object obj) {
    String labelName = "";
    if (obj instanceof SSDString) {
      labelName = ((SSDString) obj).getStrVal();
    }
    else if (obj instanceof Characteristic) {
      labelName = ((Characteristic) obj).getName();
    }
    else if (obj instanceof A2LVariable) {
      labelName = ((A2LVariable) obj).getLabelName();
    }
    String labelType = "";
    Characteristic charObj = getInstance().getLabelUtils().getCharacteristics(labelName, getInstance().getUtilInst());
    if (charObj != null) {
      labelType = charObj.getType();
      lblAndTypeMap.put(labelName, labelType);
    }

    return lblAndTypeMap;
  }

}

