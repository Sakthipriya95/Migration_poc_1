package com.bosch.caltool.icdm.database.entity.cdr;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.OptimisticLocking;

import com.bosch.caltool.icdm.database.entity.a2l.TA2lGrpParam;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpParamMapping;
import com.bosch.caltool.icdm.database.entity.cdr.cdfx.TCDFxDeliveryParam;


/**
 * The persistent class for the T_PARAMETER database table.
 */
@NamedQueries(value = {
    @NamedQuery(name = TParameter.NQ_GET_COMPLI_PARAMS, query = "SELECT tparam from TParameter tparam where tparam.ssdClass IN :ssdClassSet"),
    @NamedQuery(name = TParameter.NQ_GET_PARAM_BY_NAME, query = "SELECT  distinct param from TFunctionversion funcVer, TParameter param  where funcVer.defcharname = param.name and param.name=:name and param.ptype=:type"),
    @NamedQuery(name = TParameter.NQ_GET_PARAM_ONLY_BY_NAME, query = "SELECT  distinct param from TFunctionversion funcVer, TParameter param  where funcVer.defcharname = param.name and param.name=:name "),
    @NamedQuery(name = TParameter.NQ_GET_PARAM_BY_NAME_ONLY, query = "SELECT DISTINCT(funcVer.funcname), param from TFunctionversion funcVer, TParameter param  where funcVer.defcharname = param.name and param.name=:name"),
    @NamedQuery(name = TParameter.NQ_GET_PARAMS_BY_NAME, query = "SELECT param FROM TParameter param,GttObjectName temp  where param.name=temp.objName"),
    @NamedQuery(name = TParameter.NQ_GET_PARAM_IDS_BY_NAME, query = "SELECT param.id, param.name FROM TParameter param,GttObjectName temp  where param.name=temp.objName"),
    @NamedQuery(name = TParameter.NQ_GET_PARAMS_IN_LIST, query = "SELECT tparam from TParameter tparam where tparam.name IN :paramNameSet"),
    @NamedQuery(name = TParameter.NQ_GET_PARAMS_BY_PARAM_NAMES, query = "SELECT tparam from TParameter tparam,GttObjectName temp where tparam.name =temp.objName"),
    @NamedQuery(name = TParameter.NQ_PARAMS_N_FUNCTIONS_BY_TEMP_TABLE, query = "SELECT distinct p,f FROM TParameter p,TFunctionversion fv,TFunction f,GttFuncparam t where f.relevantName='Y' and f.upperName=fv.funcNameUpper and fv.defcharname=p.name and t.paramName=p.name"),
    @NamedQuery(name = TParameter.NQ_GET_PARAM_OBJ_BY_PARAM_NAME, query = "SELECT distinct param FROM TFunction f, TFunctionversion funcver" +
        ",TParameter param,GttObjectName temp  where funcver.defcharname = param.name and funcver.funcname =f.name " +
        "and param.name=temp.objName"),
    @NamedQuery(name = TParameter.NQ_GET_PARAM_COUNT_BY_FUNCNAMESET, query = "SELECT count(DISTINCT param) from TFunctionversion funcVer, TParameter param,GttObjectName temp" +
        "  where  funcVer.defcharname = param.name and funcVer.funcNameUpper =temp.objName"),
    @NamedQuery(name = TParameter.NQ_GET_INVALID_LABEL, query = "SELECT temp.objName FROM TParameter p,GttObjectName temp  where " +
        "temp.objName=p.name"),
    @NamedQuery(name = TParameter.NQ_GET_SEARCH_PARAM, query = "select DISTINCT fun.name, param.id, param.name, param.longname,param.longname_ger,param.ptype, fun.id " +
        "from TFunction fun, TFunctionversion funver, TParameter param where fun.name = funver.funcname and param.name = funver.defcharname and " +
        "UPPER(fun.name) like :funcName and UPPER(param.name) like :paramName and param.name not like '%[%' and param.id not in " +
        "(select rule_set_params.TParameter.id from TRuleSetParam rule_set_params where rule_set_params.TRuleSet.rsetId = :rset_id)"),
    @NamedQuery(name = TParameter.NQ_GET_QSSD_PARAM, query = "select param.name , param.ssdClass from TParameter param where param.qssdFlag ='Y'"),
    @NamedQuery(name = TParameter.NQ_GET_BLACK_LIST_PARAM, query = "select param.name from TParameter param where param.isBlackListLabel ='Y'") })
@NamedNativeQueries(value = {
    @NamedNativeQuery(name = TParameter.NNQ_GET_READONLY_PARAMS_ID_BY_A2L_ID, query = "               " +
        "SELECT                                                                   " +
        "     param.id                                                            " +
        "FROM                                                                     " +
        "       ta2l_characteristics achar                                        " +
        "     , ta2l_modules amod                                                 " +
        "     , t_parameter param                                                 " +
        "WHERE                                                                    " +
        "         achar.module_id = amod.module_id                                " +
        "     AND amod.file_id = ?                                                " +
        "     AND achar.name = param.name                                           " +
        "     AND readonly != 0                                                   "),
    @NamedNativeQuery(name = TParameter.NNQ_GET_ALL_A2L_PARAM_PROPS, query = "                        " +
        "SELECT                                                                   " +
        "       param.name                                                        " +
        "     , param.pclass                                                      " +
        "     , param.iscodeword                                                  " +
        "     , param.SSD_CLASS                                                   " +
        "     , param.IS_BLACK_LIST_LABEL                                         " +
        "     , param.QSSD_FLAG                                                   " +
        "     , param.id                                                          " +
        "FROM                                                                     " +
        "       T_PARAMETER param                                                 " +
        "     , TA2L_FILEINFO a2lfile                                             " +
        "     , TA2L_MODULES a2lmodule                                            " +
        "     , TA2L_CHARACTERISTICS a2lparam                                     " +
        "WHERE                                                                    " +
        "         a2lfile.id = a2lmodule.FILE_ID                                  " +
        "     AND a2lmodule.MODULE_ID = a2lparam.MODULE_ID                        " +
        "     AND a2lparam.name = param.name                                      " +
        "     AND a2lparam.DTYPE = param.PTYPE                                    " +
        "     AND a2lfile.id = ?                                                  ") })

@Entity
@OptimisticLocking(cascade = true)
@Table(name = "T_PARAMETER")
public class TParameter implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Named query to get compliance parameters from database
   */
  public static final String NNQ_GET_READONLY_PARAMS_ID_BY_A2L_ID = "TParameter.getReadonlyParamsIdByA2LId";

  /**
   * Named query to get compliance parameters from database
   */
  public static final String NQ_GET_COMPLI_PARAMS = "TParameter.getCompliParams";


  /**
   * Named query to parameters from database for given names
   */
  public static final String NQ_GET_PARAMS_IN_LIST = "TParameter.getParamsInList";
  /**
   * get the param by name
   */
  public static final String NQ_GET_PARAM_BY_NAME = "TParameter.getParamByName";

  /**
   * get the param by name ONLY
   */
  public static final String NQ_GET_PARAM_BY_NAME_ONLY = "TParameter.getParamByNameOnly";


  /**
   * get the param count by func Name Set
   */
  public static final String NQ_GET_PARAM_COUNT_BY_FUNCNAMESET = "TParameter.getParamCountByFuncNameSet";

  /**
   * get Search Param
   */
  public static final String NQ_GET_SEARCH_PARAM = "TParameter.getSearchParam";
  /**
   * get the set by name
   */
  public static final String NQ_GET_PARAMS_BY_NAME = "TParameter.getParamsByName";

  /**
   * get the set only by name
   */
  public static final String NQ_GET_PARAM_ONLY_BY_NAME = "TParameter.getParamsOnlyByName";
  /**
   * get the ID by name
   */
  public static final String NQ_GET_PARAM_IDS_BY_NAME = "TParameter.getParamIdsByName";

  /**
   * Named query - To fetch ParamObj based on paramName list
   */
  public static final String NQ_GET_PARAM_OBJ_BY_PARAM_NAME = "TParameter.getParamObjByParamName";

  /**
   * Named query - To fetch label list
   */
  public static final String NQ_GET_INVALID_LABEL = "TParameter.getInvalidLabel";

  /**
   * Named query - To fetch ParamObj based on paramName list
   */
  public static final String NQ_GET_PARAMS_BY_PARAM_NAMES = "TParameter.getParamsByParamNames";

  /**
   * Named query - To get Params AndFuncs By Param Names In Temp table
   */
  public static final String NQ_PARAMS_N_FUNCTIONS_BY_TEMP_TABLE = "TParameter.getParamsAndFuncsByParamNamesInTemp";

  /**
   * Named query - To fetch QSSD labels
   */
  public static final String NQ_GET_QSSD_PARAM = "TParameter.getQSSDParam";

  /**
   * Named query - To fetch Black List labels
   */
  public static final String NQ_GET_BLACK_LIST_PARAM = "TParameter.getBlackListParam";

  /**
   * Native query - To fetch all the properties of parameters in an A2L file, identified by a2l file ID
   */
  public static final String NNQ_GET_ALL_A2L_PARAM_PROPS = "TParameter.getAllA2LParamProps";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(unique = true, nullable = false)
  private long id;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", length = 100)
  private String createdUser;

  @Column(length = 1)
  private String iscodeword;

  @Column(length = 1)
  private String isbitwise;

  @Column(length = 1)
  private String iscustprm;

  @Column(length = 1024)
  private String longname;

  @Column(name = "LONGNAME_GER", length = 1024)
  private String longname_ger;

  @Column(name = "MODIFED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFED_USER", length = 100)
  private String modifiedUser;

  @Column(length = 255)
  private String name;

  @Column(length = 1)
  private String pclass;

  @Column(length = 31)
  private String ptype;

  @Column(length = 4000)
  private String hint;

  @Column(name = "SSD_CLASS", length = 20)
  private String ssdClass;


  // Adding the Version Tag
  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;

  // bi-directional one-to-one association to TA2lGrpParam
  @OneToMany(mappedBy = "TParameter", fetch = FetchType.LAZY)
  private List<TA2lGrpParam> tA2lGrpParam;

  // bi-directional one-to-one association to TParamAttr
  @OneToMany(mappedBy = "TParameter", fetch = FetchType.LAZY)
  private Set<TParamAttr> tParamAttr;

  // bi-directional many-to-one association to TA2lWpParamMapping
  @OneToMany(mappedBy = "tParameter", fetch = FetchType.LAZY)
  private List<TA2lWpParamMapping> tA2lWpParamMappings;

  // bi-directional many-to-one association to TCompliRvwHexsParam
  @OneToMany(mappedBy = "TParameter", fetch = FetchType.LAZY)
  private List<TCompliRvwHexParam> tCompliRvwHexsParam;

  // bi-directional many-to-one association to TCDFxDeliveryParam
  @OneToMany(mappedBy = "param", fetch = FetchType.LAZY)
  private List<TCDFxDeliveryParam> tCDFxDelParamList;

  @Column(name = "IS_BLACK_LIST_LABEL", nullable = false, length = 1)
  private String isBlackListLabel;


  @Column(name = "QSSD_FLAG", nullable = false, length = 1)
  private String qssdFlag;

  public TParameter() {
    // cmt
  }

  public long getId() {
    return this.id;
  }

  public void setId(final long id) {
    this.id = id;
  }

  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }

  public String getCreatedUser() {
    return this.createdUser;
  }

  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  public String getIscodeword() {
    return this.iscodeword;
  }

  public void setIscodeword(final String iscodeword) {
    this.iscodeword = iscodeword;
  }

  public String getIscustprm() {
    return this.iscustprm;
  }

  public void setIscustprm(final String iscustprm) {
    this.iscustprm = iscustprm;
  }

  public String getLongname() {
    return this.longname;
  }

  public void setLongname(final String longname) {
    this.longname = longname;
  }

  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }

  public void setModifiedDate(final Timestamp modifedDate) {
    this.modifiedDate = modifedDate;
  }

  public String getModifiedUser() {
    return this.modifiedUser;
  }

  public void setModifiedUser(final String modifedUser) {
    this.modifiedUser = modifedUser;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getPclass() {
    return this.pclass;
  }

  public void setPclass(final String pclass) {
    this.pclass = pclass;
  }

  public String getPtype() {
    return this.ptype;
  }

  public void setPtype(final String ptype) {
    this.ptype = ptype;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  // Icdm=1055 - new Column Hint
  public String getHint() {
    return this.hint;
  }


  public void setHint(final String hint) {
    this.hint = hint;
  }

  public String getLongnameGer() {
    return this.longname_ger;
  }

  public void setLongnameGer(final String longNameGer) {
    this.longname_ger = longNameGer;
  }


  /**
   * @return the isbitwise
   */
  public String getIsbitwise() {
    return this.isbitwise;
  }

  /**
   * @param isbitwise the isbitwise to set
   */
  public void setIsbitwise(final String isbitwise) {
    this.isbitwise = isbitwise;
  }

  /**
   * @return the ssdClass
   */
  public String getSsdClass() {
    return this.ssdClass;
  }


  /**
   * @param ssdClass the ssdClass to set
   */
  public void setSsdClass(final String ssdClass) {
    this.ssdClass = ssdClass;
  }

  public List<TA2lGrpParam> getTA2lGrpParam() {
    return this.tA2lGrpParam;
  }

  public void setTA2lGrpParam(final List<TA2lGrpParam> TA2lGrpParam) {
    this.tA2lGrpParam = TA2lGrpParam;
  }

  public Set<TParamAttr> getTParamAttr() {
    return this.tParamAttr;
  }

  public void setTParamAttr(final Set<TParamAttr> tParamAttr) {
    this.tParamAttr = tParamAttr;
  }


  /**
   * @return the tA2lWpParamMappings
   */
  public List<TA2lWpParamMapping> getTA2lWpParamMappings() {
    return this.tA2lWpParamMappings;
  }


  /**
   * @param tA2lWpParamMappings the tA2lWpParamMappings to set
   */
  public void setTA2lWpParamMappings(final List<TA2lWpParamMapping> tA2lWpParamMappings) {
    this.tA2lWpParamMappings = tA2lWpParamMappings;
  }


  /**
   * @return the isBlackListLabel
   */
  public String getIsBlackListLabel() {
    return this.isBlackListLabel;
  }


  /**
   * @param isBlackListLabel the isBlackListLabel to set
   */
  public void setIsBlackListLabel(final String isBlackListLabel) {
    this.isBlackListLabel = isBlackListLabel;
  }

  /**
   * Adds the TA 2 l wp param mapping.
   *
   * @param tA2lWpParamMapping the t A 2 l wp param mapping
   * @return the TA 2 l wp param mapping
   */
  public TA2lWpParamMapping addTA2lWpParamMapping(final TA2lWpParamMapping tA2lWpParamMapping) {
    if (getTA2lWpParamMappings() == null) {
      this.tA2lWpParamMappings = new ArrayList<>();
    }
    getTA2lWpParamMappings().add(tA2lWpParamMapping);
    tA2lWpParamMapping.setTParameter(this);

    return tA2lWpParamMapping;
  }


  /**
   * Removes the TA 2 l wp param mapping.
   *
   * @param tA2lWpParamMapping the t A 2 l wp param mapping
   * @return the TA 2 l wp param mapping
   */
  public TA2lWpParamMapping removeTA2lWpParamMapping(final TA2lWpParamMapping tA2lWpParamMapping) {
    if ((getTA2lWpParamMappings() != null) && (tA2lWpParamMapping != null)) {
      getTA2lWpParamMappings().remove(tA2lWpParamMapping);
    }
    return tA2lWpParamMapping;
  }


  /**
   * @return the tCompliRvwHexsParam
   */
  public List<TCompliRvwHexParam> gettCompliRvwHexsParam() {
    return this.tCompliRvwHexsParam;
  }


  /**
   * @param tCompliRvwHexsParam the tCompliRvwHexsParam to set
   */
  public void settCompliRvwHexsParam(final List<TCompliRvwHexParam> tCompliRvwHexsParam) {
    this.tCompliRvwHexsParam = tCompliRvwHexsParam;
  }

  /**
   * @return the qssdFlag
   */
  public String getQssdFlag() {
    return this.qssdFlag;
  }

  /**
   * @param qssdFlag the qssdFlag to set
   */
  public void setQssdFlag(final String qssdFlag) {
    this.qssdFlag = qssdFlag;
  }


  /**
   * @return the tCDFxDelParamList
   */
  public List<TCDFxDeliveryParam> gettCDFxDelParamList() {
    return this.tCDFxDelParamList;
  }


  /**
   * @param tCDFxDelParamList the tCDFxDelParamList to set
   */
  public void settCDFxDelParamList(final List<TCDFxDeliveryParam> tCDFxDelParamList) {
    this.tCDFxDelParamList = tCDFxDelParamList;
  }


}