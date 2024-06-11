/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import com.bosch.caltool.datamodel.core.IBasicObject;
import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.FunctionVersionUnique;
import com.bosch.caltool.icdm.model.a2l.IParamRuleResponse;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.a2l.ParameterAttribute;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameterAttr;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.caltool.icdm.model.comppkg.CompPkgParamAttr;
import com.bosch.caltool.icdm.model.comppkg.CompPkgParameter;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FunctionVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.ParameterServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RuleSetServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.comppkg.CompPkgServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author rgo7cob
 */
public class ParamCollectionDataProvider {


  /**
   * Version seperator .
   */
  private static final String VER_SEP_DOT = ".";

  /**
   * Version seperator _
   */
  private static final String VER_SEP_UNDERSCORE = "_";


  /**
   * @param coll coll
   * @return true if apic Write or grant access
   */
  public boolean canModifyAccessRights(final ParamCollection coll) {
    CurrentUserBO userBo = new CurrentUserBO();
    try {
      return userBo.hasApicWriteAccess() || userBo.hasNodeGrantAccess(coll.getId());
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, com.bosch.caltool.icdm.client.bo.Activator.PLUGIN_ID);
      return false;
    }
  }


  /**
   * @param coll coll
   * @return true if apic Write or grant access
   */
  public boolean canModifyOwnerRights(final ParamCollection coll) {
    CurrentUserBO userBo = new CurrentUserBO();
    try {
      return userBo.hasApicWriteAccess() || userBo.hasNodeOwnerAccess(coll.getId());
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, com.bosch.caltool.icdm.client.bo.Activator.PLUGIN_ID);
      return false;
    }
  }

  /**
   * @param paramColl paramColl oibject
   * @return the tool tip for the Given param Collection
   */
  public String getToolTip(final IBasicObject paramColl) {

    StringBuilder toolTip = new StringBuilder("Name : ");
    toolTip.append(paramColl.getName());

    String desc = paramColl.getDescription();
    if (null != desc) {
      toolTip.append("\nDescription : ").append(desc);
    }
    return toolTip.toString();

  }


  /**
   * @param dataObj dataObj
   * @return the created user display name
   */
  public String getCreatedUserDisplayName(final IDataObject dataObj) {
    String userName = dataObj.getCreatedUser();
    // Empty string should not be passed to getApicUser method
    if (CommonUtils.isEmptyString(userName)) {
      return "";
    }

    return userName;
  }

  /**
   * @param dataObj paramColl
   * @return the modified user disp name
   */
  public String getModifiedUserDisplayName(final IDataObject dataObj) {
    String userName = dataObj.getModifiedUser();
    // Empty string should not be passed to getApicUser method
    if (CommonUtils.isEmptyString(userName)) {
      return "";
    }

    return userName;
  }


  /**
   * @param func func
   * @return true if big function
   */
  public boolean isBigFunction(final Function func) {
    return func.getBigFunc().equals(ApicConstants.YES);

  }


  /**
   * @param paramColl paramColl
   * @return true if it is a Cdr function
   */
  public boolean hasVersions(final ParamCollection paramColl) {
    return paramColl instanceof Function;
  }


  /**
   * @param paramColl paramColl
   * @return true if it is a Rule set
   */
  public boolean isParamMappingModifiable(final ParamCollection paramColl) {

    return (paramColl instanceof Function) || (paramColl instanceof CompPackage);
  }


  /**
   * @param paramCol paramCol
   * @return the object type name
   */
  public String getObjectTypeName(final ParamCollection paramCol) {
    if (paramCol instanceof Function) {
      return ApicConstants.FUNCTION;
    }
    if (paramCol instanceof RuleSet) {
      return ApicConstants.RULE_SET_NODE_TYPE;
    }
    return ApicConstants.COMP_PKG;
  }


  /**
   * @param paramColl param Coll
   * @return true for Rule set and Function
   */
  public boolean isRulesModifiable(final ParamCollection paramColl) {
    return (paramColl instanceof Function) || (paramColl instanceof RuleSet);
  }


  /**
   * @return boolean
   */
  public boolean isRuleImportAllowed() {
    return true;
  }


  /**
   * @param paramCol paramCol
   * @return true if paramcol is modifbale
   */
  public boolean isModifiable(final ParamCollection paramCol) {
    CurrentUserBO userBo = new CurrentUserBO();

    try {
      if (paramCol instanceof Function) {
        return userBo.hasNodeWriteAccess(paramCol.getId());
      }
      else if (paramCol instanceof RuleSet) {
        return userBo.hasNodeWriteAccess(paramCol.getId()) && !isDeleted(paramCol);
      }
      else if (paramCol instanceof CompPackage) {
        return userBo.hasNodeWriteAccess(paramCol.getId());
      }
      return false;
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, com.bosch.caltool.icdm.client.bo.Activator.PLUGIN_ID);
      return false;
    }


  }


  /**
   * @param paramCol whether rule set is deleted
   * @return true if it is deleted
   */
  public boolean isDeleted(final ParamCollection paramCol) {
    return (paramCol instanceof RuleSet) && ((RuleSet) paramCol).isDeleted();
  }


  /**
   * @param funcName funcName
   * @param version version
   * @return the param Rules Output
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public IParamRuleResponse<ParameterAttribute, Parameter> getRulesOutput(final String funcName, final String version,
      final String byVariant)
      throws ApicWebServiceException {


    ParameterServiceClient client = new ParameterServiceClient();

    IParamRuleResponse<ParameterAttribute, Parameter> paramRulesOutput =
        client.getParamRulesOutput(funcName, version, byVariant);

    return paramRulesOutput;

  }


  /**
   * @param ruleSetId ruleSetId
   * @return the rule set Id
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public IParamRuleResponse<RuleSetParameterAttr, RuleSetParameter> getRulesOutput(final Long ruleSetId)
      throws ApicWebServiceException {

    RuleSetServiceClient client = new RuleSetServiceClient();
    IParamRuleResponse<RuleSetParameterAttr, RuleSetParameter> paramRulesOutput =
        client.getRuleSetParamRules(ruleSetId);

    return paramRulesOutput;

  }

  /**
   * @param compPkgId compPkgId
   * @return the rule set Id
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public IParamRuleResponse<CompPkgParamAttr, CompPkgParameter> getCompPkg(final Long compPkgId)
      throws ApicWebServiceException {
    CompPkgServiceClient client = new CompPkgServiceClient();
    IParamRuleResponse<CompPkgParamAttr, CompPkgParameter> compPkgRule = client.getCompPkgRule(compPkgId);
    return compPkgRule;

  }


  /**
   * @param paramCollection paramCollection
   * @return true if it is funcation or rule set.
   */
  public boolean isInputParamChecked(final ParamCollection paramCollection) {
    return (paramCollection instanceof Function) || (paramCollection instanceof RuleSet);

  }

  /**
   * @param paramCollection paramCollection
   * @return the sorted Func version
   */
  public SortedSet<FunctionVersionUnique> getSortedVersions(final ParamCollection paramCollection) {
    SortedSet<FunctionVersionUnique> sortedFuncVersions = null;
    Set<FunctionVersionUnique> allVersions = null;
    try {
      FunctionVersionServiceClient client = new FunctionVersionServiceClient();
      allVersions = client.getAllFuncVersions(paramCollection.getName());


      // Using lambda's
      sortedFuncVersions = new TreeSet<>((final FunctionVersionUnique ver1, final FunctionVersionUnique ver2) -> {
        int maxLen = ver1.getFuncVersion().length() > ver2.getFuncVersion().length() ? ver1.getFuncVersion().length()
            : ver2.getFuncVersion().length();
        // Normalize the strings before comparison
        String thisVersion = getNormalisedVersion(ver1.getFuncVersion(), maxLen);
        String otherVersion = getNormalisedVersion(ver2.getFuncVersion(), maxLen);
        return ApicUtil.compare(thisVersion.trim(), otherVersion.trim());
      });


      sortedFuncVersions.addAll(allVersions);

    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error("Error while fetching Function versions", exp);
    }


    return sortedFuncVersions;

  }


  /**
   * Normalize the string to enable version comparison
   *
   * @param ver version
   * @param maxLen length
   * @return normalized string
   */
  private String getNormalisedVersion(final String ver, final int maxLen) {
    StringBuilder nVersion = new StringBuilder();
    return getNormalizedVersion(ver, VER_SEP_DOT, maxLen, nVersion);
  }


  /**
   * Normalize the version with delimiter as '.'
   *
   * @param ver version
   * @param sep seperator as '.'
   * @param maxWidth width for formatting
   * @param nVersion
   * @return normalized string
   */
  private String getNormalizedVersion(final String ver, final String sep, final int maxWidth,
      final StringBuilder nVersion) {

    String[] split = Pattern.compile(sep, Pattern.LITERAL).split(ver);
    for (String str : split) {
      // Consider handling underscore character also
      if (str.contains(VER_SEP_UNDERSCORE)) {
        nVersion.append(getNormalizedVersion(str, VER_SEP_UNDERSCORE, maxWidth, nVersion));
      }
      String format = "%" + maxWidth + 's';
      nVersion.append(String.format(format, str));
    }
    return nVersion.toString();
  }

  /**
   * @param cdrFunction
   * @return
   */
  public boolean isParamPropsModifiable(final ParamCollection cdrFunction) {
    return cdrFunction instanceof Function;
  }
}
