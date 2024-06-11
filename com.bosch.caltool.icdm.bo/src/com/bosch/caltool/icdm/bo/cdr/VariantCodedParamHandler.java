/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bosch.caltool.icdm.model.cdr.IParameter;

/**
 * @author rgo7cob
 */
public class VariantCodedParamHandler<P extends IParameter> {


  /**
   * changed the pattern for the display removed $ to check if [1] is in middle iCDM-605 <br>
   * Defines Pattern for Variant coded parameter
   */
  private static final String PATTERN_VARIANT_CODE = "(\\[\\d+\\])";

  /**
   * @param paramMap paramMap
   * @return the filtered Map
   */
  public Map<String, P> getFilteredParams(final Map<String, P> paramMap) {


    final Pattern pattern = Pattern.compile(PATTERN_VARIANT_CODE);
    final Map<String, P> filteredParamsMap = new ConcurrentHashMap<>();

    for (P param : paramMap.values()) {
      final Matcher matcher = pattern.matcher(param.getName());
      // skip variant coded labels
      if (!matcher.find()) {
        filteredParamsMap.put(param.getName(), param);
      }
    }
    return filteredParamsMap;

  }


}
