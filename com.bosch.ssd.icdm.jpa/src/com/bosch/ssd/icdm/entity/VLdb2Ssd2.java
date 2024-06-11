package com.bosch.ssd.icdm.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.annotations.Direction;
import org.eclipse.persistence.annotations.NamedStoredProcedureQuery;
import org.eclipse.persistence.annotations.StoredProcedureParameter;

import com.bosch.ssd.icdm.constants.JPAQueryConstants;
import com.bosch.ssd.icdm.entity.keys.VLdb2Ssd2PK;


/**
 * The persistent class for the V_LDB2_SSD2 database table.
 */
@IdClass(VLdb2Ssd2PK.class)
@Entity
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
@SqlResultSetMappings({

    @SqlResultSetMapping(name = "oemRuleDesc", entities = { @EntityResult(entityClass = VLdb2Ssd2.class) }),

    // ALL New result set mapping during revamp
    @SqlResultSetMapping(name = "readRulesMapping", entities = {
        @EntityResult(entityClass = VLdb2Ssd2.class),
        @EntityResult(entityClass = VLdb2Pavast.class) }, columns = {
            @ColumnResult(name = "label1"),
            @ColumnResult(name = "dcmstring"),
            @ColumnResult(name = "feaValues"),
          //  @ColumnResult(name = "fullscope"),
            @ColumnResult(name = "maturity"),
            @ColumnResult(name = "ruleOwner"),
            @ColumnResult(name = "coc") }) })
@Table(name = "V_LDB2_SSD2")
@NamedNativeQueries({
    @NamedNativeQuery(name = "VLdb2Ssd2.findCustomerNodeId", query = "SELECT component " + "FROM v_ldb2_object_tree " +
        "WHERE scope = 5" + "CONNECT BY PRIOR parent_id = node_id " + "START WITH node_id = ? "),
    @NamedNativeQuery(name = "VLdb2Ssd2.findScope", query = "SELECT scope " + "FROM v_ldb2_object_tree " +
        "WHERE  node_id = ? "),
//    @NamedNativeQuery(name = "VLdb2Ssd2.getRuleForAllVersions", query = "select a.*,b.*, (select data from k5esk_ldb2.v_ldb2_dcm_clob_data where historie = 'N' and lab_obj_id=a.lab_obj_id and rev_id=a.rev_id) AS dcmstring , feavalTogether(a.lab_obj_id,a.rev_id)  AS feaValues " +
//        " ,  (select maturity from v_ldb2_maturity_level where lab_obj_id=a.lab_obj_id AND REV_ID=a.rev_id ) maturity, " +
//        "  K5ESK_LDB2.FN_GET_CNT_DISPNAM_FOR_RULE(a.lab_obj_id, a.rev_id, 1) as ruleOwner, K5ESK_LDB2.FN_GET_CNT_DISPNAM_FOR_RULE(a.lab_obj_id, a.rev_id, 2) as coc from v_ldb2_ssd2 a, v_ldb2_pavast b " +
//        " where a.lab_lab_id=b.lab_id and " + " a.obj_id_1= ? " + " and a.historie='N' and a.lab_lab_id in  " +
//        " (select lab_Id from V_Ldb2_Pavast where upper_Label in " +
//        " (SELECT DISTINCT(upper(f.defcharname)) from t_functionversions f  " +
//        " where  f.funcname = ? ))", resultSetMapping = "labelAndSsd", hints = {
//            @QueryHint(name = QueryHints.REFRESH, value = HintValues.TRUE) }),
    
//    @NamedNativeQuery(name = "VLdb2Ssd2.getRuleForVersion", query = "select a.*,b.*, (select data from k5esk_ldb2.v_ldb2_dcm_clob_data where historie = 'N' and lab_obj_id=a.lab_obj_id and rev_id=a.rev_id)  AS dcmstring ,  feavalTogether(a.lab_obj_id,a.rev_id)  AS feaValues " +
//        ",  (select maturity from v_ldb2_maturity_level where lab_obj_id=a.lab_obj_id AND REV_ID=a.rev_id ) maturity, " +
//        "  K5ESK_LDB2.FN_GET_CNT_DISPNAM_FOR_RULE(a.lab_obj_id, a.rev_id, 1) as ruleOwner, K5ESK_LDB2.FN_GET_CNT_DISPNAM_FOR_RULE(a.lab_obj_id, a.rev_id, 2) as coc from v_ldb2_ssd2 a, v_ldb2_pavast b " +
//        " where a.lab_lab_id=b.lab_id and " + " a.obj_id_1= ? " + " and a.historie='N' and a.lab_lab_id in " +
//        " (select lab_Id from V_Ldb2_Pavast where upper_Label in " +
//        " (SELECT DISTINCT(upper(f.defcharname)) from t_functionversions f  " +
//        " where  f.funcname = ? and  f.funcversion = ? ))", resultSetMapping = "labelAndSsd", hints = {
//            @QueryHint(name = QueryHints.REFRESH, value = HintValues.TRUE) }),
    // SSD V3.2.1
    @NamedNativeQuery(name = "VLdb2Ssd2.getDefaultRuleDetailsForLabel",
        // "select obj.feature_id,fea.feature_text,obj.value_id,val.value_text "+
        query = "select  LAB_OBJ_ID,MAX(REV_ID) as REV_ID from v_ldb2_ssd2 m where m.obj_id_1=? and m.Lab_lab_id=? and m.historie='Y' and  " +
            " m.lab_obj_ID =(select max(lab_obj_id) from v_LDB2_SSD2 WHERE obj_id_1=? and Lab_lab_id=? and historie='Y'and lab_obj_id not in (select distinct lab_obj_id from T_LDB2_COMP ) and  " +
            "NOT Exists (select 'x'  from v_ldb2_ssd2 where m.lab_obj_ID=lab_obj_ID and historie='N' ) ) GROUP BY LAB_OBJ_ID"),
    @NamedNativeQuery(name = JPAQueryConstants.V_LDB2_SSD2_GET_OEM_DESCRIPTION, query = "select enc_dec.encrypt(rule.lab_obj_id) as lab_obj_id, rule.REV_ID as rev_id, rule.DATA_DESCR as data_descr, " +
        "rule.DESCRIPTION as description, rule.HISTORIE_DESCR as historie_descr from V_ldb2_ssd2 rule, TEMP_RULE_ID_OEM tempRule where rule.lab_obj_id=enc_dec.decrypt(tempRule.lab_obj_id) and rule.rev_id=tempRule.rev_id", resultSetMapping = "oemRuleDesc"),
    @NamedNativeQuery(name = "VLdb2Ssd2.getDependecyRuleDetailsForLabel",

        query = " SELECT S1.LAB_OBJ_ID,MAX(R.REV_ID) from V_LDB2_SSD2 R INNER JOIN " +
            "(    select max(CO.LAB_OBJ_ID) as LAB_OBJ_ID  from ( " +
            " select COUNT(S.LAB_OBJ_ID) as cnt,S.LAB_OBJ_ID,S.REV_ID  from V_LDB2_SSD2 S " +
            "  INNER JOIN T_LDB2_COMP C ON C.LAB_OBJ_ID=S.LAB_OBJ_ID AND C.REV_ID=S.REV_ID AND C.HISTORIE=S.HISTORIE " +
            "  INNER JOIN TEMP_ICDM_FEAVAL T ON C.FEATURE_ID=T.FEATURE_ID AND C.VALUE_ID=T.VALUE_ID " +
            " WHERE S.obj_id_1=? and S. Lab_lab_id=? and S.historie='Y' AND NOT EXISTS (SELECT 'x' from V_LDB2_SSD2 WHERE lab_OBJ_ID=S.LAB_OBJ_ID and historie='N') and not exists (select 'x' from V_ldb2_SSD2  WHERE lab_OBJ_ID=S.LAB_OBJ_ID and rev_id>S.REV_ID and historie='Y' ) GROUP BY S.LAB_OBJ_ID,S.REV_ID " +
            "  ) CO WHERE CO.cnt=(select count(*) from TEMP_ICDM_FEAVAL where LAB_ID=?)  ) s1 on  S1.LAB_OBJ_ID=R.LAB_OBJ_ID GROUP BY S1.LAB_OBJ_ID "),
    // all new named queries for revamp
    @NamedNativeQuery(name = JPAQueryConstants.V_LDB2_SSD2_GET_RULES_FOR_RELEASE, query = JPAQueryConstants.GET_RULE_SELECT_QUERY +
        JPAQueryConstants.GET_RULE_FOR_RELEASE_WHERE_CLAUSE, resultSetMapping = "readRulesMapping"),
    @NamedNativeQuery(name = JPAQueryConstants.V_LDB2_SSD2_GET_RULES_FOR_COMPLI_RELEASE, query = JPAQueryConstants.GET_RULE_SELECT_QUERY +
        JPAQueryConstants.GET_RULE_FOR_COMPLI_RELEASE_WHERE_CLAUSE, resultSetMapping = "readRulesMapping"),
    @NamedNativeQuery(name = JPAQueryConstants.V_LDB2_SSD2_HISTORY_RULE_FOR_LABEL, query = JPAQueryConstants.GET_RULE_SELECT_QUERY +
        JPAQueryConstants.GET_HISTORY_RULE_FOR_LABEL_WHERE_CLAUSE, resultSetMapping = "readRulesMapping"),
    @NamedNativeQuery(name = JPAQueryConstants.V_LDB2_SSD2_HISTORY_RULE_FOR_CDR_RULE, query = JPAQueryConstants.GET_RULE_SELECT_QUERY +
        JPAQueryConstants.GET_HISTORY_FOR_RULE_WHERE_CLAUSE, resultSetMapping = "readRulesMapping"),
    @NamedNativeQuery(name = JPAQueryConstants.V_LDB2_SSD2_HISTORY_COMPLI_RULE_FOR_CDR_RULE, query = JPAQueryConstants.GET_RULE_SELECT_QUERY +
        JPAQueryConstants.GET_HISTORY_FOR_COMPLI_RULE_WHERE_CLAUSE, resultSetMapping = "readRulesMapping"),

    @NamedNativeQuery(name = JPAQueryConstants.GET_RULE_FOR_LABEL_LIST, query = JPAQueryConstants.GET_RULE_SELECT_QUERY +
        JPAQueryConstants.GET_RULE_FOR_LABEL_LIST_WHERE_CLAUSE, resultSetMapping = "readRulesMapping"),
    @NamedNativeQuery(name = JPAQueryConstants.GET_DEF_RULE_FOR_LABEL_LIST, query = JPAQueryConstants.GET_RULE_SELECT_QUERY +
        JPAQueryConstants.GET_DEF_RULE_FOR_LABEL_LIST_WHERE_CLAUSE, resultSetMapping = "readRulesMapping"),
    @NamedNativeQuery(name = JPAQueryConstants.GET_RULE_FROM_TEMP, query = JPAQueryConstants.GET_RULE_SELECT_QUERY +
        JPAQueryConstants.GET_RULE_FROM_TEMP_WHERE_CLAUSE, resultSetMapping = "readRulesMapping"),
    
    @NamedNativeQuery(name = JPAQueryConstants.GET_RULE_FROM_TEMP_VARCODE, query = JPAQueryConstants.GET_RULE_SELECT_QUERY +
        JPAQueryConstants.GET_RULE_FROM_TEMP_VARCODE_WHERE_CLAUSE, resultSetMapping = "readRulesMapping"),
   
//    @NamedNativeQuery(name = "VLdb2Ssd2.getRuleFromTempVarCode", query = "select c.label AS label, a.*,b.*, (select data from k5esk_ldb2.v_ldb2_dcm_clob_data where historie = 'N' and lab_obj_id=a.lab_obj_id and rev_id=a.rev_id)  AS dcmstring , " +
//        "feavalTogether(a.lab_obj_id,a.rev_id)  AS feaValues " +
//        " ,  (select maturity from v_ldb2_maturity_level where lab_obj_id=a.lab_obj_id AND REV_ID=a.rev_id ) maturity " +
//        " from v_ldb2_ssd2 a, v_ldb2_pavast b , K5ESK_LDB2.TEMP_LABELLIST_IF c " + " where a.lab_lab_id=b.lab_id and " +
//        " a.obj_id_1= ? " + " and a.historie='N'  and a.lab_obj_id in (select lab_obj_id from TEMP_SSD2_VARCODE) " +
//        " and a.lab_lab_id in " + " (select lab_Id from V_Ldb2_Pavast where upper_Label in " +
//        " (SELECT DISTINCT(upper_label) from K5ESK_LDB2.TEMP_LABELLIST_IF where default_rule='Y') )" +
//        " and b.upper_label=c.upper_label ", resultSetMapping = "readRulesMapping"),
    

    
    @NamedNativeQuery(name = JPAQueryConstants.GET_RULE_FOR_SINGLE_LABEL, query = JPAQueryConstants.GET_RULE_SELECT_QUERY +
        JPAQueryConstants.GET_RULE_FOR_SINGLE_LABEL_WHERE_CLAUSE, resultSetMapping = "readRulesMapping"),
//    @NamedNativeQuery(name = JPAQueryConstants.GET_RULE_FOR_VERSION_WHERE_CLAUSE, query = JPAQueryConstants.GET_RULE_SELECT_QUERY +
//        JPAQueryConstants.GET_RULE_FOR_ALL_VERSION_WHERE_CLAUSE, resultSetMapping = "readRulesMapping"),
//    @NamedNativeQuery(name = JPAQueryConstants.GET_RULE_FOR_ALL_VERSION, query = JPAQueryConstants.GET_RULE_SELECT_QUERY +
//    JPAQueryConstants.GET_RULE_FOR_ALL_VERSION_WHERE_CLAUSE, resultSetMapping = "readRulesMapping"),
    @NamedNativeQuery(name = JPAQueryConstants.GET_RULE_FOR_LABOBJ_REVID, query = JPAQueryConstants.GET_RULE_SELECT_QUERY +
    JPAQueryConstants.GET_RULE_FOR_LABOBJ_REVID_WHERE_CLAUSE, resultSetMapping = "readRulesMapping"),
    @NamedNativeQuery(name = JPAQueryConstants.GET_RULE_FOR_SINGLE_NODE, query = JPAQueryConstants.GET_RULE_SELECT_QUERY +
        JPAQueryConstants.GET_RULE_FOR_SINGLE_NODE_WHERE_CLAUSE, resultSetMapping = "readRulesMapping")

})


@NamedQueries({
    @NamedQuery(name = "VLdb2Ssd2.findRuleForLabel", query = "select ssd from VLdb2Ssd2 ssd where ssd.objId1= :nodeId " +
        "and ssd.historie='N' and ssd.labLabId in" + " (select pavast.labId from VLdb2Pavast pavast where " +
        "     pavast.upperLabel=:upperLabel)"),
    @NamedQuery(name = "VLdb2Ssd2.checkSSDChanged", query = "SELECT s.revId FROM VLdb2Ssd2 s " +
        "WHERE s.labObjId = :labObjId AND s.historie = 'N' ") })

@NamedStoredProcedureQuery(name = "VLdb2Ssd2.prcUpdateRec", procedureName = "pkg_ssd_edit.prc_update_rec_nonui", parameters = {
    @StoredProcedureParameter(queryParameter = "labObjId", name = "p_lab_obj_id", type = BigDecimal.class, direction = Direction.IN),
    @StoredProcedureParameter(queryParameter = "REV_ID", name = "p_REV_ID", type = BigDecimal.class, direction = Direction.IN),
    @StoredProcedureParameter(queryParameter = "result", name = "p_result", type = Integer.class, direction = Direction.OUT) })
public class VLdb2Ssd2 implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Cases
   */
  private String cases = "Review";

  /**
   * Contact
   */
  private String contact;

  /**
   * Created Date
   */
  @Temporal(TemporalType.DATE)
  @Column(name = "CRE_DATE")
  private Date creDate;

  /**
   * Created User
   */
  @Column(name = "CRE_USER")
  private String creUser;

  /**
   * Data Description
   */
  @Column(name = "DATA_DESCR")
  private String dataDescr;

  private String dcm2ssd = "Y";

  /**
   * Department ID
   */
  @Column(name = "DEPT_DEPT_ID")
  private BigDecimal deptDeptId;

  /**
   * Description
   */
  private String description;

  /**
   * Dimension
   */
  private String dim;

  /**
   * Formula
   */
  private String formula;

  /**
   * Formula Description
   */
  @Lob
  @Column(name = "FORMULA_DESC")
  private String formulaDesc;

  /**
   * Historie
   */
  private String historie = "N";

  /**
   * Historie Description
   */
  @Column(name = "HISTORIE_DESCR")
  private String historieDescr;

  /**
   * Label ID
   */
  @Column(name = "LAB_LAB_ID")
  private BigDecimal labLabId;

  /**
   * Rule ID
   */
  @SequenceGenerator(name = "ldb2_all", sequenceName = "SEQ_LDB2_ALL", allocationSize = 1)
  @Id
  @GeneratedValue(generator = "ldb2_all")
  @Column(name = "LAB_OBJ_ID")
  private BigDecimal labObjId;

  /**
   * Max
   */
  @Column(name = "\"MAX\"")
  private BigDecimal max;

  /**
   * Min
   */
  @Column(name = "\"MIN\"")
  private BigDecimal min;

  /**
   * Mod Date
   */
  @Temporal(TemporalType.DATE)
  @Column(name = "MOD_DATE")
  private Date modDate;

  /**
   * Mod Flag
   */
  @Column(name = "MOD_FLAG")
  private String modFlag;

  /**
   * Mod User
   */
  @Column(name = "MOD_USER")
  private String modUser;

  /**
   * Node ID
   */
  @Column(name = "OBJ_ID_1")
  private BigDecimal objId1;

  /**
   * OBJ ID 2
   */
  @Column(name = "OBJ_ID_2")
  private BigDecimal objId2;

  /**
   * Rev_ID
   */
  @Id
  @Column(name = "REV_ID")
  private BigDecimal revId;

  /**
   * Scope
   */
  @Column(name = "\"SCOPE\"")
  private BigDecimal scope;

  /**
   * Src Obj ID
   */
  @Column(name = "SRC_OBJ_ID")
  private BigDecimal srcObjId;

  /**
   * Src Rev ID
   */
  @Column(name = "SRC_REV_ID")
  private BigDecimal srcRevId;

  /**
   * SSD Grp ID
   */
  @Column(name = "SSD_GRP_ID")
  private BigDecimal ssdGrpId;

  /**
   * SSD Relevance
   */
  @Column(name = "SSD_RELEVANCE")
  private String ssdRelevance = "N";

  /**
   * State
   */
  @Column(name = "\"STATE\"")
  private BigDecimal state;

  /**
   * Typ
   */
  private BigDecimal typ;

  /**
   *
   */
  public VLdb2Ssd2() {
    // constructor
  }

  /**
   * @return cases
   */
  public String getCases() {
    return this.cases;
  }

  /**
   * @param cases cases
   */
  public void setCases(final String cases) {
    this.cases = cases;
  }

  /**
   * @return contact
   */
  public String getContact() {
    return this.contact;
  }

  /**
   * @param contact contact
   */
  public void setContact(final String contact) {
    this.contact = contact;
  }

  /**
   * @return creation date
   */
  public Date getCreDate() {
    if (Objects.nonNull(this.creDate)) {
      return (Date) this.creDate.clone();
    }
    return null;
  }

  /**
   * @param creDate creation date
   */
  public void setCreDate(final Date creDate) {
    this.creDate = (Date) creDate.clone();
  }

  /**
   * @return user
   */
  public String getCreUser() {
    return this.creUser;
  }

  /**
   * @param creUser user
   */
  public void setCreUser(final String creUser) {
    this.creUser = creUser;
  }

  /**
   * @return description
   */
  public String getDataDescr() {
    return this.dataDescr;
  }

  /**
   * @param dataDescr description
   */
  public void setDataDescr(final String dataDescr) {
    this.dataDescr = dataDescr;
  }

  /**
   * @return dcm2ssd
   */
  public String getDcm2ssd() {
    return this.dcm2ssd;
  }

  /**
   * @param dcm2ssd dcm2ssd
   */
  public void setDcm2ssd(final String dcm2ssd) {
    this.dcm2ssd = dcm2ssd;
  }

  /**
   * @return DeptId
   */
  public BigDecimal getDeptDeptId() {
    return this.deptDeptId;
  }

  /**
   * @param deptDeptId DeptId
   */
  public void setDeptDeptId(final BigDecimal deptDeptId) {
    this.deptDeptId = deptDeptId;
  }

  /**
   * @return description
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * @param description description
   */
  public void setDescription(final String description) {
    this.description = description;
  }

  /**
   * @return dim
   */
  public String getDim() {
    return this.dim;
  }

  /**
   * @param dim dim
   */
  public void setDim(final String dim) {
    this.dim = dim;
  }

  /**
   * @return formula
   */
  public String getFormula() {
    return this.formula;
  }

  /**
   * @param formula formula
   */
  public void setFormula(final String formula) {
    this.formula = formula;
  }

  /**
   * @return formulaDesc
   */
  public String getFormulaDesc() {
    return this.formulaDesc;
  }

  /**
   * @param formulaDesc desc
   */
  public void setFormulaDesc(final String formulaDesc) {
    this.formulaDesc = formulaDesc;
  }

  /**
   * @return historie
   */
  public String getHistorie() {
    return this.historie;
  }

  /**
   * @param historie historie
   */
  public void setHistorie(final String historie) {
    this.historie = historie;
  }

  /**
   * @return historieDescr
   */
  public String getHistorieDescr() {
    return this.historieDescr;
  }

  /**
   * @param historieDescr historieDescr
   */
  public void setHistorieDescr(final String historieDescr) {
    this.historieDescr = historieDescr;
  }

  /**
   * @return labLabId
   */
  public BigDecimal getLabLabId() {
    return this.labLabId;
  }

  /**
   * @param labLabId labLabId
   */
  public void setLabLabId(final BigDecimal labLabId) {
    this.labLabId = labLabId;
  }

  /**
   * @return labObjId
   */
  public BigDecimal getLabObjId() {
    return this.labObjId;
  }

  /**
   * @param labObjId labObjId
   */
  public void setLabObjId(final BigDecimal labObjId) {
    this.labObjId = labObjId;
  }

  /**
   * @return max
   */
  public BigDecimal getMax() {
    return this.max;
  }

  /**
   * @param max max
   */
  public void setMax(final BigDecimal max) {
    this.max = max;
  }

  /**
   * @return min
   */
  public BigDecimal getMin() {
    return this.min;
  }

  /**
   * @param min min
   */
  public void setMin(final BigDecimal min) {
    this.min = min;
  }

  /**
   * @return date
   */
  public Date getModDate() {
    return (Date) this.modDate.clone();
  }

  /**
   * @param modDate date
   */
  public void setModDate(final Date modDate) {
    this.modDate = (Date) modDate.clone();
  }

  /**
   * @return flag
   */
  public String getModFlag() {
    return this.modFlag;
  }

  /**
   * @param modFlag flag
   */
  public void setModFlag(final String modFlag) {
    this.modFlag = modFlag;
  }

  /**
   * @return user
   */
  public String getModUser() {
    return this.modUser;
  }

  /**
   * @param modUser user
   */
  public void setModUser(final String modUser) {
    this.modUser = modUser;
  }

  /**
   * @return objId1
   */
  public BigDecimal getObjId1() {
    return this.objId1;
  }

  /**
   * @param objId1 objId1
   */
  public void setObjId1(final BigDecimal objId1) {
    this.objId1 = objId1;
  }

  /**
   * @return objId2
   */
  public BigDecimal getObjId2() {
    return this.objId2;
  }

  /**
   * @param objId2 objId2
   */
  public void setObjId2(final BigDecimal objId2) {
    this.objId2 = objId2;
  }

  /**
   * @return rev id
   */
  public BigDecimal getRevId() {
    return this.revId;
  }

  /**
   * @param revId revision id
   */
  public void setRevId(final BigDecimal revId) {
    this.revId = revId;
  }

  /**
   * @return scope
   */
  public BigDecimal getScope() {
    return this.scope;
  }

  /**
   * @param scope scope
   */
  public void setScope(final BigDecimal scope) {
    this.scope = scope;
  }

  /**
   * @return srcObjId
   */
  public BigDecimal getSrcObjId() {
    return this.srcObjId;
  }

  /**
   * @param srcObjId srcObjId
   */
  public void setSrcObjId(final BigDecimal srcObjId) {
    this.srcObjId = srcObjId;
  }

  /**
   * @return srcRevId
   */
  public BigDecimal getSrcRevId() {
    return this.srcRevId;
  }

  /**
   * @param srcRevId srcRevId
   */
  public void setSrcRevId(final BigDecimal srcRevId) {
    this.srcRevId = srcRevId;
  }

  /**
   * @return ssdGrpId
   */
  public BigDecimal getSsdGrpId() {
    return this.ssdGrpId;
  }

  /**
   * @param ssdGrpId ssdGrpId
   */
  public void setSsdGrpId(final BigDecimal ssdGrpId) {
    this.ssdGrpId = ssdGrpId;
  }

  /**
   * @return ssdRelevance
   */
  public String getSsdRelevance() {
    return this.ssdRelevance;
  }

  /**
   * @param ssdRelevance ssdRelevance
   */
  public void setSsdRelevance(final String ssdRelevance) {
    this.ssdRelevance = ssdRelevance;
  }

  /**
   * @return starte
   */
  public BigDecimal getState() {
    return this.state;
  }

  /**
   * @param state ssd state
   */
  public void setState(final BigDecimal state) {
    this.state = state;
  }

  /**
   * @return type
   */
  public BigDecimal getTyp() {
    return this.typ;
  }

  /**
   * @param typ type
   */
  public void setTyp(final BigDecimal typ) {
    this.typ = typ;
  }

}