/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.review;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.LabelList;
import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.module.calibration.group.Group;
import com.bosch.calmodel.a2ldata.ref.concrete.DefCharacteristic;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.logger.ParserLogger;
import com.bosch.caltool.labfunparser.exception.ParserException;
import com.bosch.caltool.labfunparser.textparser.InputFileParser;

/**
 * @author bru2cob
 */
public class LabFunFileResolver implements IReviewParamResolver {

  private final InputStream inputStream;

  private final String labFunPath;

  private final A2LFileInfo a2lFileContents;

  /**
   * @param labFunPath
   * @param inputStream
   */
  public LabFunFileResolver(final String labFunPath, final InputStream inputStream, final A2LFileInfo a2lFileContents) {
    this.inputStream = inputStream;
    this.labFunPath = labFunPath;
    this.a2lFileContents = a2lFileContents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> getParameters() throws IcdmException {

    Set<String> paramSet = new HashSet<>();
    InputFileParser parser = getParser();
    if (parser != null) {
      paramSet.addAll(parser.getLabels());

      // get labels from functions
      List<String> funcList = parser.getFunctions();
      if (CommonUtils.isNotEmpty(funcList)) {
        funcList.stream().forEach(func -> paramSet.addAll(getParamListfromFunction(func)));
      }

      // get labels from groups
      List<String> grpList = parser.getGroups();
      if (CommonUtils.isNotEmpty(grpList)) {
        paramSet.addAll(getParamListFromGroups(grpList));
      }
    }
    return paramSet.stream().collect(Collectors.toList());
  }

  /**
   * @param grpList - list of group names
   * @return Set of parameter names for given input
   */
  private Set<String> getParamListFromGroups(final List<String> grpList) {
    Set<String> paramSet = new HashSet<>();
    Map<String, Group> grps = this.a2lFileContents.getAllModulesGroups();
    if (CommonUtils.isNotEmpty(grpList) && !CommonUtils.isNullOrEmpty(grps)) {
      for (String grp : grpList) {
        if (grps.get(grp) != null) {
          LabelList labelList = grps.get(grp).getFirstLabelList();
          if (CommonUtils.isNotEmpty(labelList)) {
            labelList.stream().forEach(paramSet::add);
          }
        }
      }
    }
    return paramSet;
  }


  private Set<String> getParamListfromFunction(final String funName) {
    return getParamListfromFunction(this.a2lFileContents.getAllModulesFunctions().get(funName));
  }

  /**
   * @param function
   * @return
   */
  private Set<String> getParamListfromFunction(final Function function) {
    Set<String> paramSet = new HashSet<>();
    if (function != null) {
      List<DefCharacteristic> defCharRefList = function.getDefCharRefList();
      // Get parameter names
      if (defCharRefList != null) {
        defCharRefList.stream().map(DefCharacteristic::getName).forEach(paramSet::add);
      }
    }
    return paramSet;
  }


  /**
   * Get the paarser file
   *
   * @param inpFileType
   */
  private InputFileParser getParser() {
    InputFileParser parser = null;
    try {
      parser = new InputFileParser(ParserLogger.getInstance(), this.inputStream, this.labFunPath);
      parser.parse();

    }
    catch (ParserException exp) {
      CDMLogger.getInstance().error("Parser Exception : " + exp.getMessage(), exp);
    }
    return parser;
  }


}
