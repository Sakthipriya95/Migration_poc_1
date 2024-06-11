/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model;


import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.datamodel.core.ModelTypeRegistry;
import com.bosch.caltool.icdm.model.a2l.A2lResp;
import com.bosch.caltool.icdm.model.a2l.A2lRespBoschGroupUser;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lVarGrpVariantMapping;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.A2lWpImportProfile;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpResp;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibilityStatus;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.FunctionVersion;
import com.bosch.caltool.icdm.model.a2l.FunctionVersionUnique;
import com.bosch.caltool.icdm.model.a2l.IcdmA2lGroup;
import com.bosch.caltool.icdm.model.a2l.PTType;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.a2l.ParameterAttribute;
import com.bosch.caltool.icdm.model.a2l.WpResp;
import com.bosch.caltool.icdm.model.apic.AliasDef;
import com.bosch.caltool.icdm.model.apic.AliasDetail;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.Unit;
import com.bosch.caltool.icdm.model.apic.UserPreference;
import com.bosch.caltool.icdm.model.apic.WebflowElement;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.Characteristic;
import com.bosch.caltool.icdm.model.apic.attr.CharacteristicValue;
import com.bosch.caltool.icdm.model.apic.attr.MandatoryAttr;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedValidity;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcSubVarCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVariantCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVersCocWp;
import com.bosch.caltool.icdm.model.apic.pidc.AlternateAttr;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrix;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersion;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersionAttr;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDetStructure;
import com.bosch.caltool.icdm.model.apic.pidc.PidcFavourite;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.cdr.A2lDepParam;
import com.bosch.caltool.icdm.model.cdr.CDRResultFunction;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.DaDataAssessment;
import com.bosch.caltool.icdm.model.cdr.DaFile;
import com.bosch.caltool.icdm.model.cdr.DaParameter;
import com.bosch.caltool.icdm.model.cdr.DaQnaireResp;
import com.bosch.caltool.icdm.model.cdr.DaWpResp;
import com.bosch.caltool.icdm.model.cdr.RuleLinks;
import com.bosch.caltool.icdm.model.cdr.RuleRemark;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameterAttr;
import com.bosch.caltool.icdm.model.cdr.RulesetHwComponent;
import com.bosch.caltool.icdm.model.cdr.RulesetParamResp;
import com.bosch.caltool.icdm.model.cdr.RulesetParamType;
import com.bosch.caltool.icdm.model.cdr.RulesetSysElement;
import com.bosch.caltool.icdm.model.cdr.RvwAttrValue;
import com.bosch.caltool.icdm.model.cdr.RvwCommentTemplate;
import com.bosch.caltool.icdm.model.cdr.RvwFile;
import com.bosch.caltool.icdm.model.cdr.RvwParametersSecondary;
import com.bosch.caltool.icdm.model.cdr.RvwParticipant;
import com.bosch.caltool.icdm.model.cdr.RvwResultsSecondary;
import com.bosch.caltool.icdm.model.cdr.RvwUserCmntHistory;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;
import com.bosch.caltool.icdm.model.cdr.RvwWpResp;
import com.bosch.caltool.icdm.model.cdr.VcdmPst;
import com.bosch.caltool.icdm.model.cdr.VcdmPstContent;
import com.bosch.caltool.icdm.model.cdr.WpArchival;
import com.bosch.caltool.icdm.model.cdr.WpFiles;
import com.bosch.caltool.icdm.model.cdr.cdfx.CDFxDelWpResp;
import com.bosch.caltool.icdm.model.cdr.cdfx.CDFxDelivery;
import com.bosch.caltool.icdm.model.cdr.cdfx.CdfxDelvryParam;
import com.bosch.caltool.icdm.model.cdr.compli.CompliReviewResult;
import com.bosch.caltool.icdm.model.cdr.compli.CompliReviewResultHex;
import com.bosch.caltool.icdm.model.cdr.compli.CompliRvwHexParam;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionConfig;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionDepenAttr;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionDepenAttrValue;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionResultOption;
import com.bosch.caltool.icdm.model.cdr.qnaire.Questionnaire;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswerDummy;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswerOpl;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.caltool.icdm.model.comppkg.CompPkgBc;
import com.bosch.caltool.icdm.model.comppkg.CompPkgFc;
import com.bosch.caltool.icdm.model.comppkg.CompPkgParamAttr;
import com.bosch.caltool.icdm.model.emr.EmrCategory;
import com.bosch.caltool.icdm.model.emr.EmrColumn;
import com.bosch.caltool.icdm.model.emr.EmrColumnValue;
import com.bosch.caltool.icdm.model.emr.EmrEmissionStandard;
import com.bosch.caltool.icdm.model.emr.EmrExcelMapping;
import com.bosch.caltool.icdm.model.emr.EmrFile;
import com.bosch.caltool.icdm.model.emr.EmrFileData;
import com.bosch.caltool.icdm.model.emr.EmrMeasureUnit;
import com.bosch.caltool.icdm.model.emr.EmrPidcVariant;
import com.bosch.caltool.icdm.model.emr.EmrUploadError;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPDef;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMapPtType;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMapping;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPRelvPTType;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPVersion;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroup;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroupNodeAccess;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroupUser;
import com.bosch.caltool.icdm.model.general.IcdmFileData;
import com.bosch.caltool.icdm.model.general.IcdmFiles;
import com.bosch.caltool.icdm.model.general.Link;
import com.bosch.caltool.icdm.model.general.Message;
import com.bosch.caltool.icdm.model.general.UserLoginInfo;
import com.bosch.caltool.icdm.model.general.WsService;
import com.bosch.caltool.icdm.model.general.WsSystem;
import com.bosch.caltool.icdm.model.general.WsSystemService;
import com.bosch.caltool.icdm.model.rm.PidcRmDefinition;
import com.bosch.caltool.icdm.model.rm.PidcRmProjCharacter;
import com.bosch.caltool.icdm.model.rm.RmCategory;
import com.bosch.caltool.icdm.model.rm.RmCategoryMeasures;
import com.bosch.caltool.icdm.model.rm.RmProjectCharacter;
import com.bosch.caltool.icdm.model.rm.RmRiskLevel;
import com.bosch.caltool.icdm.model.uc.UcpAttr;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UseCaseGroup;
import com.bosch.caltool.icdm.model.uc.UseCaseSection;
import com.bosch.caltool.icdm.model.uc.UsecaseFavorite;
import com.bosch.caltool.icdm.model.user.ApicAccessRight;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.model.wp.Region;
import com.bosch.caltool.icdm.model.wp.WPResourceDetails;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;
import com.bosch.caltool.icdm.model.wp.WorkPkg;
import com.bosch.caltool.icdm.model.wp.WorkpackageDivisionCdl;
import com.bosch.caltool.icdm.model.wp.WpmlWpMasterlist;

/**
 * Type of model
 *
 * @author bne4cob
 */
/**
 * @author pdh2cob
 */
public enum MODEL_TYPE implements IModelType {

                                              /**
                                               * User. Model class is {@link User}
                                               */
                                              APIC_USER(User.class, "User"),
                                              /**
                                               * System Access Rights. Model class is {@link ApicAccessRight}
                                               */
                                              APIC_ACCESS_RIGHT(ApicAccessRight.class, "System Access Rights"),
                                              /**
                                               * Attribute Super Group. Model class is {@link AttrSuperGroup}
                                               */
                                              SUPER_GROUP(AttrSuperGroup.class, "Attribute Super Group"),
                                              /**
                                               * Attribute Group. Model class is {@link AttrGroup}
                                               */
                                              GROUP(AttrGroup.class, "Attribute Group"),
                                              /**
                                               * Attribute Characteristic. Model class is {@link Characteristic}
                                               */
                                              // Icdm-954
                                              CHARACTERISTIC(Characteristic.class, "Attribute Characteristic"),

                                              /**
                                               * Attribute Characteristic Value. Model class is
                                               * {@link CharacteristicValue}
                                               */
                                              CHARACTERISTIC_VALUE(
                                                                   CharacteristicValue.class,
                                                                   "Attribute Characteristic Value"),
                                              /**
                                               * Attribute. Model class is {@link Attribute}
                                               */
                                              ATTRIBUTE(Attribute.class, "Attribute") {

                                                /**
                                                 * {@inheritDoc}
                                                 */
                                                @Override
                                                public String getTypeCode() {
                                                  return ApicConstants.ATTR_NODE_TYPE;
                                                }
                                              },
                                              /**
                                               * Attribute Value. Model class is {@link AttributeValue}
                                               */
                                              ATTRIB_VALUE(AttributeValue.class, "Attribute Value"),
                                              /**
                                               * Attribute/Value dependency - Model class is
                                               * {@link AttrNValueDependency}
                                               */
                                              ATTR_N_VAL_DEP(AttrNValueDependency.class, "Attribute/Value Dependency"),
                                              /**
                                               * Alias Definition. Model class is {@link AliasDef}
                                               */
                                              // ICDM-1844
                                              ALIAS_DEFINITION(AliasDef.class, "Alias Definition") {

                                                /**
                                                 * {@inheritDoc}
                                                 */
                                                @Override
                                                public String getTypeCode() {
                                                  return ApicConstants.ALIAS_DEF_TYPE;
                                                }

                                              },
                                              /**
                                               * Alias Detail type. Model class is {@link AliasDetail}
                                               */
                                              ALIAS_DETAIL(AliasDetail.class, "Attribute/Value Alias"),
                                              /**
                                               * Alternate Attribute Model class is {@link AlternateAttr}
                                               */
                                              ALTERNATE_ATTR(AlternateAttr.class, "Alternate Attribute"),
                                              /**
                                               * Use Case Group. Model class is {@link UseCaseGroup}
                                               */
                                              USE_CASE_GROUP(UseCaseGroup.class, "Use Case Group"),
                                              /**
                                               * Use Case. Model class is {@link UseCase}
                                               */
                                              USE_CASE(UseCase.class, "Use Case") {

                                                /**
                                                 * {@inheritDoc}
                                                 */
                                                @Override
                                                public String getTypeCode() {
                                                  return ApicConstants.UC_NODE_TYPE;
                                                }

                                              },
                                              /**
                                               * Use case Section. Model class is {@link UseCaseSection}
                                               */
                                              USE_CASE_SECT(UseCaseSection.class, "Use Case Section"),
                                              /**
                                               * Use case Attribute. Model class is {@link UcpAttr}
                                               */
                                              UCP_ATTR(UcpAttr.class, "Use Case Attribute"),
                                              /**
                                               * ICDM-1836 Mandatory attr
                                               */
                                              MANDATORY_ATTR(MandatoryAttr.class, "Mandatory Attribute"),
                                              /**
                                               * Predefined Attribute Value. Model class is {@link PredefinedAttrValue}
                                               */
                                              PREDEFND_ATTR_VALUE(
                                                                  PredefinedAttrValue.class,
                                                                  "Predefined Attribute Value"),
                                              /**
                                               * Predefined Validity. Model class is {@link PredefinedValidity}
                                               */
                                              PREDEFND_VALIDITY(PredefinedValidity.class, "Predefined Validity"),
                                              /**
                                               * Project ID Card. Model class is {@link Pidc}
                                               */
                                              PIDC(Pidc.class, "Project ID Card"),
                                              /**
                                               * PIDC Version. Model class is {@link PidcVersion}
                                               */
                                              PIDC_VERSION(PidcVersion.class, "PIDC Version"),
                                              /**
                                               * PidcChangeHistory. Model class is {@link PidcChangeHistory }
                                               */
                                              PIDC_CHANGE_HISTORY(PidcChangeHistory.class, "PidcChangeHistory"),
                                              /**
                                               * PIDC Favourite. Model class is {@link PidcFavourite}
                                               */
                                              PIDC_FAVORITE(PidcFavourite.class, "PIDC Favourite"),
                                              /**
                                               * PIDC Variant. Model class is {@link PidcVariant}
                                               */
                                              VARIANT(PidcVariant.class, "PIDC Variant"),
                                              /**
                                               * PIDC Sub-Variant. Model class is {@link PidcSubVariant}
                                               */
                                              SUB_VARIANT(PidcSubVariant.class, "PIDC Sub-variant"),
                                              /**
                                               * PIDC Attribute. Model class is {@link PidcVersionAttribute}
                                               */
                                              PROJ_ATTR(PidcVersionAttribute.class, "PIDC Attribute"),
                                              /**
                                               * PIDC Variant Attribute. Model class is {@link PidcVariantAttribute}
                                               */
                                              VAR_ATTR(PidcVariantAttribute.class, "PIDC Variant Attribute"),
                                              /**
                                               * PIDC Sub-variant Attribute. Model class is
                                               * {@link PidcSubVariantAttribute}
                                               */
                                              SUBVAR_ATTR(PidcSubVariantAttribute.class, "PIDC Sub-variant Attribute"),
                                              /**
                                               * Favourite Use case. Model class is {@link UsecaseFavorite}
                                               */
                                              // icdm-1027
                                              UC_FAV(UsecaseFavorite.class, "Favourite Use case"),
                                              /**
                                               * PIDC Version. Model class is {@link PidcVersion}
                                               */
                                              PIDC_DET_STRUCT(PidcDetStructure.class, "PIDC Details Structure"),
                                              /**
                                               * PIDC A2L File. Model class is {@link PidcA2l}
                                               */
                                              PIDC_A2L(PidcA2l.class, "PIDC A2L File"),
                                              /**
                                               * Focus Matrix. Model class is {@link FocusMatrix}
                                               */
                                              FOCUS_MATRIX(FocusMatrix.class, "Focus Matrix"),
                                              /**
                                               * Focus Matrix Version. Model class is {@link FocusMatrixVersion}
                                               */
                                              // ICDM-2569
                                              FOCUS_MATRIX_VERSION(FocusMatrixVersion.class, "Focus Matrix Version"),
                                              /**
                                               * Focus Matrix Version Attribute
                                               */
                                              // ICDM-2569
                                              FOCUS_MATRIX_VERSION_ATTR(
                                                                        FocusMatrixVersionAttr.class,
                                                                        "Focus Matrix Version Attribute"),
                                              /**
                                               * A2L Responsibility. Model class is {@link A2lResp}
                                               */
                                              A2L_RESP(A2lResp.class, "A2L Responsibility"),
                                              /**
                                               * CDR Review File - TRvwFile
                                               */
                                              // A2L_GRP_PARAM(A2lGrpParam.class,
                                              // ModelOrder.ORDER_A2L_GRP_PARAM),
                                              /**
                                               * WP Responsibility. Model class is {@link WpResp}
                                               */
                                              WP_RESP(WpResp.class, "WP Responsibility"),


                                              /**
                                               * A2l WP Definition Version. Model class is {@link A2lWpDefnVersion }
                                               */
                                              A2L_WP_DEFN_VERSION(A2lWpDefnVersion.class, "A2l WP Definition Version"),
                                              /**
                                               * A2lVariantGroup. Model class is {@link A2lVariantGroup }
                                               */
                                              A2L_VARIANT_GROUP(A2lVariantGroup.class, "A2l Variant Group"),


                                              /**
                                               * A2lVarGrpVarMapping. Model class is {@link A2lVarGrpVariantMapping }
                                               */
                                              A2L_VAR_GRP_VAR_MAPPING(
                                                                      A2lVarGrpVariantMapping.class,
                                                                      "A2l Variant Group Variant Mapping"),


                                              /**
                                               * A2L Responsibility definition. Model class {@link A2lResponsibility}
                                               */
                                              A2L_RESPONSIBILITY(
                                                                 A2lResponsibility.class,
                                                                 "A2L Responsibility Definition"),
                                              /**
                                               * A2lWpResponsibility. Model class is {@link A2lWpResponsibility }
                                               */
                                              A2L_WP_RESPONSIBILITY(A2lWpResponsibility.class, "A2l WP Responsibility"),
                                              /**
                                               * A2L WP Responsibility. Model class is {@link A2lWpResp}
                                               */
                                              A2L_WP_RESP(A2lWpResp.class, "A2L WP Responsibility"),

                                              /**
                                               * A2lWpParamMapping. Model class is {@link A2lWpParamMapping }
                                               */
                                              A2L_WP_PARAM_MAPPING(A2lWpParamMapping.class, "A2l WP Parameter Mapping"),
                                              /**
                                               * A2L Group Model class is {@link IcdmA2lGroup}
                                               */
                                              ICDM_A2L_GROUP(IcdmA2lGroup.class, "A2L Group"),
                                              /**
                                               * PT Type. Model class is {@link PTType}
                                               */
                                              POWER_TRAIN_TYPE(PTType.class, "Power Train Type"),
                                              /**
                                               * FC-WP Definition. Model class is {@link FC2WPDef}
                                               */
                                              FC2WP_DEF(FC2WPDef.class, "FC-WP Definition"),
                                              /**
                                               * FC-WP Definition Version. Model class is {@link FC2WPVersion}
                                               */
                                              FC2WP_DEF_VERS(FC2WPVersion.class, "FC-WP Definition Version"),
                                              /**
                                               * FC-WP Mapping. Model class is {@link FC2WPMapping}
                                               */
                                              FC2WP_MAPPING(FC2WPMapping.class, "FC-WP Mapping"),
                                              /**
                                               * FC-WP Mapped PT Type. Model class is {@link FC2WPMapPtType}
                                               */
                                              FC2WP_MAPPED_PT_TYPE(FC2WPMapPtType.class, "FC-WP Mapped PT Type"),
                                              /**
                                               * FC-WP Relevant PT Type. Model class is {@link FC2WPRelvPTType}
                                               */
                                              FC2WP_RELV_PT_TYPE(FC2WPRelvPTType.class, "FC-WP Relevant PT Type"),
                                              /**
                                               * Function. Model class is {@link Function}
                                               */
                                              CDR_FUNCTION(Function.class, "Function") {

                                                @Override
                                                public String getTypeCode() {
                                                  return ApicConstants.FUNC_NODE_TYPE;
                                                }

                                              },
                                              /**
                                               * Review Result WorkPackage Resp. Model class is {@link RvwWpResp }
                                               */
                                              RVW_WP_RESP(RvwWpResp.class, "Review Result WorkPackage Resp"),
                                              /**
                                               * Function Version. Model class is {@link FunctionVersion}
                                               */
                                              FUNCTION_VERSION(FunctionVersion.class, "Function Version"),
                                              /**
                                               * Function Version Unique. Model class is {@link FunctionVersionUnique}
                                               */
                                              FUNC_VERS_UNIQUE(FunctionVersionUnique.class, "Function Version"),
                                              /**
                                               * Parameter. Model class is {@link Parameter}
                                               */
                                              /* iCDM-471 */
                                              CDR_FUNC_PARAM(Parameter.class, "Parameter"),
                                              /**
                                               * Parameter Attribute. Model class is {@link ParameterAttribute}
                                               */
                                              // Icdm-1032
                                              PARAMETER_ATTR(ParameterAttribute.class, "Parameter Attribute"),
                                              /**
                                               * Rule Set. Model class is {@link RuleSet}
                                               */
                                              // iCDM-1366
                                              CDR_RULE_SET(RuleSet.class, "Rule Set"),
                                              /**
                                               * Rule Set Parameter. Model class is {@link RuleSetParameter}
                                               */
                                              // iCDM-1366
                                              CDR_RULE_SET_PARAM(RuleSetParameter.class, "Rule Set Parameter"),
                                              /**
                                               * Rule Set Parameter Attribute. Model class is
                                               * {@link RuleSetParameterAttr}
                                               */
                                              // iCDM-1366
                                              CDR_RULE_SET_PARAM_ATTR(
                                                                      RuleSetParameterAttr.class,
                                                                      "Rule Set Parameter Attribute"),
                                              /**
                                               * Work Package Resource. Model class is {@link WPResourceDetails}
                                               */
                                              WORK_PKG_RESOURCE(WPResourceDetails.class, "Work Package Resource"),
                                              /**
                                               * Region.Model class is {@link Region}}
                                               */
                                              REGION(Region.class, "Region"),
                                              /**
                                               * Work Package. Model class is {@link WorkPkg}
                                               */
                                              WORK_PACKAGE(WorkPkg.class, "Work Package"),
                                              /**
                                               * Work Package Division. Model class is {@link WorkPackageDetails}
                                               */
                                              WORKPACKAGE_DIVISION(WorkPackageDivision.class, "Work Package Division") {

                                                /**
                                                 * {@inheritDoc}
                                                 */
                                                @Override
                                                public String getTypeCode() {
                                                  return ApicConstants.WORKPACKAGE_DIV_NODE_TYPE;
                                                }

                                              },

                                              /**
                                               * Work Package Division Cdl Model class is{@link WorkpackageDivisionCdl}
                                               */
                                              WORKPACKAGE_DIVISION_CDL(WorkpackageDivisionCdl.class, "WP Division CDL"),
                                              /**
                                               * Questionnaire. Model class is {@link Questionnaire}
                                               */
                                              QUESTIONNAIRE(Questionnaire.class, "Questionnaire"),
                                              /**
                                               * Question Result. Model class is {@link QuestionResultOption}
                                               */
                                              QUESTION_RESULT_OPTION(
                                                                     QuestionResultOption.class,
                                                                     "Question Result Option"),
                                              /**
                                               * Questionnaire Version. Model class is {@link QuestionnaireVersion}
                                               */
                                              QUESTIONNAIRE_VERSION(
                                                                    QuestionnaireVersion.class,
                                                                    "Questionnaire Version"),
                                              /**
                                               * Question - TQuestion
                                               */
                                              QUESTION(Question.class, "Question"),
                                              /**
                                               * QuestionConfig - TQuestion
                                               */
                                              QUESTION_CONFIG(QuestionConfig.class, "Question Configuration"),
                                              /**
                                               * QUESTION_DEPEN_ATTR - TQuestionDepenAttribute
                                               */
                                              QUESTION_DEPEN_ATTR(
                                                                  QuestionDepenAttr.class,
                                                                  "Question Dependency Attribute"),
                                              /**
                                               * QUESTION_DEPEN_ATTR_VALUE - TQuestionDepenAttribute
                                               */
                                              QUESTION_DEPEN_ATTR_VALUE(
                                                                        QuestionDepenAttrValue.class,
                                                                        "Question Dependency Attribute Value"),
                                              /**
                                               * Data Review Result. Model class is {@link CDRReviewResult}
                                               */
                                              CDR_RESULT(CDRReviewResult.class, "Data Review Result") {

                                                /**
                                                 * {@inheritDoc}
                                                 */
                                                @Override
                                                public String getTypeCode() {
                                                  return "REVIEW_RESULT";
                                                }

                                              },
                                              /**
                                               * Review Result Variant. Model class is {@link RvwVariant}
                                               */
                                              CDR_RES_VARIANTS(RvwVariant.class, "Review Result Variant"),
                                              /**
                                               * Review Result Function. Model class is {@link CDRResultFunction}
                                               */
                                              CDR_RES_FUNCTION(CDRResultFunction.class, "Review Result Function"),
                                              /**
                                               * Review Result Parameter. Model class is {@link CDRResultParameter}
                                               */
                                              CDR_RES_PARAMETER(CDRResultParameter.class, "Review Result Parameter"),
                                              /**
                                               * Review Result Participant. Model class is {@link RvwParticipant}
                                               */
                                              CDR_PARTICIPANT(RvwParticipant.class, "Review Participant"),

                                              CDR_RVW_COMMENT_TEMPLATE(
                                                                       RvwCommentTemplate.class,
                                                                       "Review Comment Template"),
                                              /**
                                               * Review File. Model class is {@link RvwFile}
                                               */
                                              CDR_RES_FILE(RvwFile.class, "Review File"),
                                              /**
                                               * Review Result Attribute Value. Model class is {@link RvwAttrValue}
                                               */
                                              // Icdm-1214
                                              CDR_RES_ATTR_VALUE(RvwAttrValue.class, "Review Result Attribute Value"),
                                              /**
                                               * Secondary Review Result. Model class is {@link RvwResultsSecondary}
                                               */
                                              CDR_RESULT_SECONDARY(
                                                                   RvwResultsSecondary.class,
                                                                   "Secondary Review Result"),
                                              /**
                                               * Secondary Result Parameter. Model class is
                                               * {@link RvwParametersSecondary}
                                               */
                                              CDR_RESULT_PARAM_SECONDARY(
                                                                         RvwParametersSecondary.class,
                                                                         "Secondary Result Parameter"),
                                              /**
                                               * QuestionnaireResponse. Model class is {@link RvwQnaireResponse }
                                               */
                                              RVW_QNAIRE_RESPONSE(RvwQnaireResponse.class, "Questionnaire Response"),
                                              /**
                                               * RvwQnaireRespVersion. Model class is {@link RvwQnaireRespVersion}
                                               */
                                              RVW_QNAIRE_RESP_VERSION(
                                                                      RvwQnaireRespVersion.class,
                                                                      "Questionnaire Response Version"),
                                              /**
                                               * RvwQnaireRespVariant. Model class is {@link RvwQnaireRespVariant}
                                               */
                                              RVW_QNAIRE_RESP_VARIANT(
                                                                      RvwQnaireRespVariant.class,
                                                                      "Questionnaire Response Variant Mapping"),
                                              /**
                                               * RvwQnaireAnswer - Model class is {@link RvwQnaireAnswer}
                                               */
                                              RVW_QNAIRE_ANS(RvwQnaireAnswer.class, "Review Questionnaire Answer") {

                                                /**
                                                 * {@inheritDoc}
                                                 */
                                                @Override
                                                public String getTypeCode() {
                                                  return ApicConstants.RVW_QNAIRE_ANS_NODE_TYPE;
                                                }
                                              },
                                              /**
                                               * RVW_QNAIRE_ANSWER - TRvwQnaireAnswer
                                               */
                                              RVW_QNAIRE_ANS_DUMMY(
                                                                   RvwQnaireAnswerDummy.class,
                                                                   "Review Questionnaire Answer"),
                                              /**
                                               * Qnaire Answer Opl. Model calss is {@link RvwQnaireAnswerOpl}
                                               */
                                              RVW_QNAIRE_ANSWER_OPL(
                                                                    RvwQnaireAnswerOpl.class,
                                                                    "Review Questionnaire Answer OPL"),
                                              /**
                                               * RVW_QNAIRE_ANSWER - TRvwQnaireAnswer
                                               */
                                              // QNAIRE_ANS_OPEN_POINTS(RvwQnaireAnswerOpl.class,
                                              // ModelOrder.ORDER_QS_ANS_OPEN_POINTS),
                                              /**
                                               * Component Package. Model class is {@link CompPackage}
                                               */
                                              COMP_PKG(CompPackage.class, "Component Package"),
                                              /**
                                               * Component package BC. Model class is {@link CompPkgBc}
                                               */
                                              COMP_PKG_BC(CompPkgBc.class, "Component package BC"),
                                              /**
                                               * Component package Rule Attribute. Model class is {@link CpRuleAttr}
                                               */
                                              // COMP_PKG_RULE_ATTR(CpRuleAttr.class, "Component
                                              // package Rule
                                              // Attribute")
                                              /**
                                               * Component package Function. Model class is {@link CompPkgFc}
                                               */
                                              COMP_PKG_BC_FC(CompPkgFc.class, "Component package Function"),
                                              /**
                                               * Component package Function. Model class is {@link CompPkgParamAttr}
                                               */
                                              COMP_PKG_PARAMETER_ATTR(
                                                                      CompPkgParamAttr.class,
                                                                      "Component package Parameter Attribute"),
                                              /**
                                               * Compliance Review Result. Model class is {@link CompliReviewResult}
                                               */
                                              COMPLI_RVW_RES(CompliReviewResult.class, "Compliance Review Result"),
                                              /**
                                               * Compliance Review Result HEX Details. Model class is
                                               * {@link CompliReviewResultHex}
                                               */
                                              COMPLI_RVW_RES_HEX(
                                                                 CompliReviewResultHex.class,
                                                                 "Compliance Review Result HEX Details"),
                                              /**
                                               * CompliReviewParamResultHex. Model class is {@link CompliRvwHexParam }
                                               */
                                              COMPLI_RVW_HEX_PARAM(
                                                                   CompliRvwHexParam.class,
                                                                   "CompliReviewParamResultHex"),
                                              /**
                                               * EMR Category. Model class is {@link EmrCategory}
                                               */
                                              EMR_CATEGORY(EmrCategory.class, "EMR Category"),
                                              /**
                                               * EMR Column. Model class is {@link EmrColumn}
                                               */
                                              EMR_COLUMN(EmrColumn.class, "EMR Column"),
                                              /**
                                               * EMR Column Value. Model class is {@link EmrColumnValue}
                                               */
                                              EMR_COLUMN_VALUE(EmrColumnValue.class, "EMR Column Value"),
                                              /**
                                               * Emission Standard. Model class is {@link EmrEmissionStandard}
                                               */
                                              EMR_EMIS_STD(EmrEmissionStandard.class, "Emission Standard"),
                                              /**
                                               * EMR File Data. Model class is {@link EmrFileData}
                                               */
                                              EMR_FILE_DATA(EmrFileData.class, "EMR File Data"),
                                              /**
                                               * EMR Excel Mapping. Model class is {@link EmrExcelMapping}
                                               */
                                              EMR_EXCL_MAPPING(EmrExcelMapping.class, "EMR Excel Mapping"),
                                              /**
                                               * EMR File. Model class is {@link EmrFile}
                                               */
                                              EMR_FILE(EmrFile.class, "EMR File"),
                                              /**
                                               * EMR Measure Unit. Model class is {@link EmrMeasureUnit}
                                               */
                                              EMR_MEASURE_UNIT(EmrMeasureUnit.class, "EMR Measure Unit"),
                                              /**
                                               * EMR PIDC Variant. Model class is {@link EmrPidcVariant}
                                               */
                                              EMR_PIDC_VARIANT(EmrPidcVariant.class, "EMR PIDC Variant"),
                                              /**
                                               * EMR Upload Error. Model class is {@link EmrUploadError}
                                               */
                                              EMR_UPLOAD_ERR(EmrUploadError.class, "EMR Upload Error"),
                                              /**
                                               * Risk Category. Model class is {@link RmCategory}
                                               */
                                              RM_CATEGORY(RmCategory.class, "Risk Category"),
                                              /**
                                               * Risk Category Measure. Model class is {@link RmCategoryMeasures}
                                               */
                                              RM_CATEGORY_MEASURE(RmCategoryMeasures.class, "Risk Category Measure"),
                                              /**
                                               * Project Risk Character. Model class is {@link RmProjectCharacter}
                                               */
                                              RM_PROJECT_CHAR(RmProjectCharacter.class, "Project Risk Character"),
                                              /**
                                               * Risk Level. Model class is {@link RmRiskLevel}
                                               */
                                              RM_RISK_LEVEL(RmRiskLevel.class, "Risk Level"),
                                              /**
                                               * PIDC Risk Definition. Model class is {@link PidcRmDefinition}
                                               */
                                              PIDC_RM_DEFINITION(PidcRmDefinition.class, "PIDC Risk Definition"),
                                              /**
                                               * Pidc Risk Project Character. Model class is {@link PidcRmProjCharacter}
                                               */
                                              PIDC_RM_PROJ_CHAR(
                                                                PidcRmProjCharacter.class,
                                                                "PIDC Risk Project Character"),

                                              /**
                                               * Vcdm Pst Contents. Model class is {@link VcdmPstContent }
                                               */
                                              PST_CONT(VcdmPstContent.class, "vCDM PST Content"),
                                              /**
                                               * Vcdm Pst. Model class is {@link VcdmPst }
                                               */
                                              PST(VcdmPst.class, "vCDM PST"),
                                              /**
                                               * Link. Model class is {@link Link}
                                               */
                                              // icdm-977
                                              LINK(Link.class, "Link"),
                                              /**
                                               * File. Model class is {@link IcdmFiles}
                                               */
                                              ICDM_FILE(IcdmFiles.class, "File"),
                                              /**
                                               * File Data. Model class is {@link IcdmFileData}
                                               */
                                              ICDM_FILE_DATA(IcdmFileData.class, "File Data"),
                                              /**
                                               * Message. Model class is {@link Message}
                                               */
                                              MESSAGE(Message.class, "Message"),
                                              /**
                                               * User Node Access Right. Model class is {@link NodeAccess}
                                               */
                                              NODE_ACCESS(NodeAccess.class, "Node Access Right"),
                                              /**
                                               * Webflow element model class
                                               */
                                              WEBFLOW(WebflowElement.class, "Web Flow Element"),

                                              /**
                                               * A2l Work Package. Model class is {@link A2lWorkPackage }
                                               */
                                              A2L_WORK_PACKAGE(A2lWorkPackage.class, "A2l Work Package Definition"),

                                              /**
                                               * A2l WP Profile. Model class is {@link A2lWpImportProfile }
                                               */
                                              A2L_WP_IMPORT_PROFILE(A2lWpImportProfile.class, "A2l WP Import Profile"),

                                              /**
                                               * CDFxDelivery. Model class is {@link CDFxDelivery }
                                               */
                                              CDFX_DELIVERY(CDFxDelivery.class, "CDFxDelivery"),

                                              /**
                                               * CDFxDelWpResp. Model class is {@link CDFxDelWpResp }
                                               */
                                              CDFX_DEL_WP_RESP(CDFxDelWpResp.class, "CDFxDelWpResp"),

                                              /**
                                               * CDFx Delivery Parameter. Model class is {@link CdfxDelvryParam }
                                               */
                                              CDFX_DELVRY_PARAM(CdfxDelvryParam.class, "CDFx Delivery Parameter"),

                                              /**
                                               * Web Service Client. Model class is {@link WsSystem }
                                               */
                                              WS_SYSTEM(WsSystem.class, "Web Service Client"),

                                              /**
                                               * Web Service. Model class is {@link WsService }
                                               */
                                              WS_SERVICE(WsService.class, "Web Service"),

                                              /**
                                               * Service Client Mapping. Model class is {@link WsSystemService }
                                               */
                                              WS_SYSTEM_SERVICE(WsSystemService.class, "Service-Client Mapping"),
                                              /**
                                               * Service Client Mapping. Model class is {@link RuleRemark }
                                               */
                                              RULE_REMARK(RuleRemark.class, "Rule Remark"),
                                              /**
                                               * Units. Model class is {@link Unit}
                                               */
                                              UNITS(Unit.class, "Unit"),
                                              /**
                                               * Review Comment History. Model class is {@link RvwUserCmntHistory }
                                               */
                                              RVW_CMNT_HISTORY(RvwUserCmntHistory.class, "Review User Comment History"),
                                              /**
                                               * A2L Responsibility Bosch Group User. Model class is
                                               * {@link A2lRespBoschGroupUser }
                                               */
                                              A2L_RESPONSIBLITY_BSHGRP_USR(
                                                                           A2lRespBoschGroupUser.class,
                                                                           "A2L Responsibility Bosch Group User"),
                                              /**
                                               * PidcVersCocWp. Model class is {@link PidcVersCocWp }
                                               */
                                              PIDC_VERS_COC_WP(PidcVersCocWp.class, "PIDC Version CoC Work Package"),
                                              /**
                                               * PidcVariantCocWp. Model class is {@link PidcVariantCocWp }
                                               */
                                              PIDC_VARIANT_COC_WP(
                                                                  PidcVariantCocWp.class,
                                                                  "PIDC Variant CoC Work Package"),
                                              /**
                                               * PidcSubVarCocWp. Model class is {@link PidcSubVarCocWp }
                                               */
                                              PIDC_SUB_VAR_COC_WP(
                                                                  PidcSubVarCocWp.class,
                                                                  "PIDC Sub Variant CoC Work Package"),
                                              /**
                                               * WpmlWpMasterlist. Model class is {@link WpmlWpMasterlist }
                                               */
                                              WPML_WP_MASTERLIST(
                                                                 WpmlWpMasterlist.class,
                                                                 "WPML Work Package Masterlist"),
                                              /**
                                               * A2lDepParam. Model class is {@link A2lDepParam }
                                               */
                                              A2L_DEP_PARAM(A2lDepParam.class, "A2L Dependent Parameter"),
                                              /**
                                               * A2LWPResponsibilityStatus. Model class is
                                               * {@link A2lWpResponsibilityStatus }
                                               */
                                              A2L_WP_RESPONSIBILITY_STATUS(
                                                                           A2lWpResponsibilityStatus.class,
                                                                           "A2LWPResponsibilityStatus"),

                                              /**
                                               * DaDataAssessment. Model class is {@link DaDataAssessment }
                                               */
                                              DA_DATA_ASSESSMENT(DaDataAssessment.class, "Data Assessment Model"),

                                              /**
                                               * DaFile. Model class is {@link DaFile }
                                               */
                                              DA_FILE(DaFile.class, "Data Assessment Archival File"),

                                              /**
                                               * DaParameter. Model class is {@link DaParameter }
                                               */
                                              DA_PARAMETER(DaParameter.class, "Data Assessment Parameter"),

                                              /**
                                               * DaQnaireResp. Model class is {@link DaQnaireResp }
                                               */
                                              DA_QNAIRE_RESP(
                                                             DaQnaireResp.class,
                                                             "Data Assessment Questionaire Response"),

                                              /**
                                               * DaWpResp. Model class is {@link DaWpResp }
                                               */
                                              DA_WP_RESP(DaWpResp.class, "Data Assessment WorkPackage Responsible"),

                                              /**
                                               * WpArchival. Model class is {@link WpArchival }
                                               */
                                              WP_ARCHIVAL(WpArchival.class, "WpArchival Model"),

                                              /**
                                               * WpFiles. Model class is {@link WpFiles }
                                               */
                                              WP_FILES(WpFiles.class, "WpArchival Files Model"),

                                              /**
                                               * RuleLinks. Model class is {@link RuleLinks }
                                               */
                                              RULE_LINKS(RuleLinks.class, "RuleLinks"),
                                              /**
                                               * RulesetParamType. Model class is{@link RulesetParamType}
                                               */
                                              RULESET_PARAM_TYPE(RulesetParamType.class, "RulesetParamType"),
                                              /**
                                               * RulesetParamResp. Model class is{@link RulesetParamResp}
                                               */
                                              RULESET_PARAM_RESP(RulesetParamResp.class, "RulesetParamType"),
                                              /**
                                               * RulesetHwComponent. Model class is{@link RulesetHwComponent}
                                               */
                                              RULESET_HW_COMPONENT(RulesetHwComponent.class, "RulesetHwComponent"),
                                              /**
                                               * RulesetSysElement. Model class is{@link RulesetSysElement}
                                               */
                                              RULESET_SYS_ELEMENT(RulesetSysElement.class, "RulesetSysElemet"),


                                              /**
                                               * Active Directory Group. Model class is {@link ActiveDirectoryGroup}
                                               */
                                              ACTIVE_DIRECTORY_GROUP(
                                                                     ActiveDirectoryGroup.class,
                                                                     "Active Directory Group"),
                                              /**
                                               * Active Directory Group Node Access. Model class is
                                               * {@link ActiveDirectoryGroupNodeAccess}
                                               */
                                              ACTIVE_DIRECTORY_GROUP_NODE_ACCES(
                                                                                ActiveDirectoryGroupNodeAccess.class,
                                                                                "Active Directory Group Node Access"),
                                              /**
                                               * Active Directory Group Users. Model class is
                                               * {@link ActiveDirectoryGroupUser}
                                               */
                                              ACTIVE_DIRECTORY_GROUP_USER(
                                                                          ActiveDirectoryGroupUser.class,
                                                                          "Active Directory Group User"),
                                              /**
                                               * User Login Info. Model class is {@link UserLoginInfo}
                                               */
                                              USER_LOGIN_INFO(UserLoginInfo.class, "User Login Info"),
                                              /**
                                               * User Preference. Model class is {@link UserPreference}
                                               */
                                              USER_PREFERENCE(UserPreference.class, "User Preference");

  /**
   * Entity class for this enumeration value
   */
  private Class<?> modelClazz;
  /**
   * Display name of the type
   */
  private String typeName;

  /**
   * Constructor
   *
   * @param clazz entity class
   */
  MODEL_TYPE(final Class<?> clazz, final String typeName) {
    this.modelClazz = clazz;
    this.typeName = typeName;

    ModelTypeRegistry.INSTANCE.register(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getTypeName() {
    return this.typeName == null ? getTypeCode() : this.typeName;
  }

  /**
   * @return the entity class of this enumeration value
   */
  @Override
  public Class<?> getTypeClass() {
    return this.modelClazz;
  }

  /**
   * Retrieves the entity type value for the given entity class
   *
   * @param clazz entity class
   * @return the entity type
   */
  public static MODEL_TYPE getTypeByClass(final Class<?> clazz) {
    for (MODEL_TYPE eType : MODEL_TYPE.values()) {
      if (eType.getTypeClass().equals(clazz)) {
        return eType;
      }
    }
    return null;
  }

  /**
   * @param model IModel
   * @return IModelType
   */
  public static IModelType getTypeOfModel(final IModel model) {
    return model == null ? null : getTypeByClass(model.getClass());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getTypeCode() {
    return name();
  }


  /**
   * @return true for TOP_LEVEL_ENTITY.Modifying the data for TOP_LEVEL_ENTITY should not stop the command execution.
   */
  // TODO is this method needed any more?
  public boolean stopCommandForEntityUpdate() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getOrder() {
    return ordinal();
  }


}
