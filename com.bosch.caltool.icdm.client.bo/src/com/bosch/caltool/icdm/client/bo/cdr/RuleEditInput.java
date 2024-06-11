/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.util.SortedSet;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;


/**
 * @author rgo7cob
 */
public class RuleEditInput<D extends IParameterAttribute, P extends IParameter> {


  private final ParamCollection cdrFunction;
  private final Object firstElement;

  private final CalData checkValCalDataObj;
  private final String pidcVersName;
  private final String resultName;
  private final boolean readOnlyMode;
  private final ParameterDataProvider<D, P> paramDataProvider;
  private final ParamCollectionDataProvider paramColDataProvider;

  private final SortedSet<AttributeValueModel> attrValModel;


  /**
   * @param cdrFunction cdrFunction
   * @param firstElement firstElement
   * @param checkValCalDataObj checkValCalDataObj
   * @param pidcVersName pidcVersName
   * @param resultName resultName
   * @param readOnlyMode readOnlyMode
   * @param paramDataProvider paramDataProvider
   * @param paramColDataProvider paramColDataProvider
   */
  public RuleEditInput(final ParamCollection cdrFunction, final Object firstElement, final CalData checkValCalDataObj,
      final String pidcVersName, final String resultName, final boolean readOnlyMode,
      final ParameterDataProvider<D, P> paramDataProvider, final ParamCollectionDataProvider paramColDataProvider,
      final SortedSet<AttributeValueModel> attrValModel) {
    super();
    this.cdrFunction = cdrFunction;
    this.firstElement = firstElement;
    this.checkValCalDataObj = checkValCalDataObj;
    this.pidcVersName = pidcVersName;
    this.resultName = resultName;
    this.readOnlyMode = readOnlyMode;
    this.paramDataProvider = paramDataProvider;
    this.paramColDataProvider = paramColDataProvider;
    this.attrValModel = attrValModel;
  }


  /**
   * @return the cdrFunction
   */
  public ParamCollection getCdrFunction() {
    return this.cdrFunction;
  }


  /**
   * @return the firstElement
   */
  public Object getFirstElement() {
    return this.firstElement;
  }


  /**
   * @return the checkValCalDataObj
   */
  public CalData getCheckValCalDataObj() {
    return this.checkValCalDataObj;
  }


  /**
   * @return the pidcVersName
   */
  public String getPidcVersName() {
    return this.pidcVersName;
  }


  /**
   * @return the resultName
   */
  public String getResultName() {
    return this.resultName;
  }


  /**
   * @return the readOnlyMode
   */
  public boolean isReadOnlyMode() {
    return this.readOnlyMode;
  }


  /**
   * @return the paramDataProvider
   */
  public ParameterDataProvider<D, P> getParamDataProvider() {
    return this.paramDataProvider;
  }


  /**
   * @return the paramColDataProvider
   */
  public ParamCollectionDataProvider getParamColDataProvider() {
    return this.paramColDataProvider;
  }


  /**
   * @return the attrValModel
   */
  public SortedSet<AttributeValueModel> getAttrValModel() {
    return this.attrValModel;
  }

}
