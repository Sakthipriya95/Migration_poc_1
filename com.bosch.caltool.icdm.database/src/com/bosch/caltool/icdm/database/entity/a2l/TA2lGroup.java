package com.bosch.caltool.icdm.database.entity.a2l;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;


/**
 * The persistent class for the T_A2L_GROUP database table.
 */
@Entity
@Table(name = "T_A2L_GROUP")
@NamedNativeQueries(value = {
    @NamedNativeQuery(name = TA2lGroup.NNP_IMPORT_WP_RESP_GRPS_FROM_A2L, query = "               " +
        " {call PK_GROUP2PAL.TransferGroup2PalWP(?1,?2)} ") })
@NamedQueries(value = {
    @NamedQuery(name = "TA2lGroup.findAll", query = "SELECT t FROM TA2lGroup t"),
    @NamedQuery(name = TA2lGroup.GET_BY_A2LFILEID, query = "SELECT t FROM TA2lGroup t WHERE t.a2lId = :a2lId"),
    @NamedQuery(name = TA2lGroup.GET_BY_A2LFILE_ATTR_VAL_ID, query = "SELECT t FROM TA2lGroup t WHERE t.a2lId = :a2lId and t.tabvAttrValue.valueId = :valueId") })
public class TA2lGroup implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Query to fetch A2lGroup by A2LFileId
   */
  public static final String GET_BY_A2LFILEID = "TA2lGroup.findByA2lId";

  /**
  *
  */
  public static final String NNP_IMPORT_WP_RESP_GRPS_FROM_A2L = "NNP_TransferGroup2PalWP";


  /**
   * Query to fetch A2lGroup by A2LFileId & Attr value
   */
  public static final String GET_BY_A2LFILE_ATTR_VAL_ID = "TA2lGroup.findByA2lAttrValId";
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "GROUP_ID", unique = true, nullable = false)
  private long groupId;

  @Column(name = "A2L_ID", nullable = false)
  private Long a2lId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "GRP_LONG_NAME", length = 255)
  private String grpLongName;

  @Column(name = "GRP_NAME", nullable = false, length = 100)
  private String grpName;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private Long version;

  // bi-directional many-to-one association to TabvAttrValue
  @ManyToOne
  @JoinColumn(name = "WP_ROOT_ID")
  private TabvAttrValue tabvAttrValue;

  // bi-directional many-to-one association to TA2lGrpParam
  @OneToMany(mappedBy = "TA2lGroup")
  private List<TA2lGrpParam> TA2lGrpParams;

  // bi-directional many-to-one association to TA2lWpResp
  @OneToMany(mappedBy = "TA2lGroup")
  private List<TA2lWpResp> TA2lWpResps;

  public TA2lGroup() {}

  public long getGroupId() {
    return this.groupId;
  }

  public void setGroupId(final long groupId) {
    this.groupId = groupId;
  }

  public Long getA2lId() {
    return this.a2lId;
  }

  public void setA2lId(final Long a2lId) {
    this.a2lId = a2lId;
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

  public String getGrpLongName() {
    return this.grpLongName;
  }

  public void setGrpLongName(final String grpLongName) {
    this.grpLongName = grpLongName;
  }

  public String getGrpName() {
    return this.grpName;
  }

  public void setGrpName(final String grpName) {
    this.grpName = grpName;
  }

  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }

  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public String getModifiedUser() {
    return this.modifiedUser;
  }

  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public TabvAttrValue getTabvAttrValue() {
    return this.tabvAttrValue;
  }

  public void setTabvAttrValue(final TabvAttrValue tabvAttrValue) {
    this.tabvAttrValue = tabvAttrValue;
  }

  public List<TA2lGrpParam> getTA2lGrpParams() {
    return this.TA2lGrpParams;
  }

  public void setTA2lGrpParams(final List<TA2lGrpParam> TA2lGrpParams) {
    this.TA2lGrpParams = TA2lGrpParams;
  }

  public TA2lGrpParam addTA2lGrpParam(final TA2lGrpParam TA2lGrpParam) {
    getTA2lGrpParams().add(TA2lGrpParam);
    TA2lGrpParam.setTA2lGroup(this);

    return TA2lGrpParam;
  }

  public TA2lGrpParam removeTA2lGrpParam(final TA2lGrpParam TA2lGrpParam) {
    getTA2lGrpParams().remove(TA2lGrpParam);
    TA2lGrpParam.setTA2lGroup(null);

    return TA2lGrpParam;
  }

  public List<TA2lWpResp> getTA2lWpResps() {
    return this.TA2lWpResps;
  }

  public void setTA2lWpResps(final List<TA2lWpResp> TA2lWpResps) {
    this.TA2lWpResps = TA2lWpResps;
  }

  public TA2lWpResp addTA2lWpResp(final TA2lWpResp TA2lWpResp) {
    getTA2lWpResps().add(TA2lWpResp);
    TA2lWpResp.setTA2lGroup(this);

    return TA2lWpResp;
  }

  public TA2lWpResp removeTA2lWpResp(final TA2lWpResp TA2lWpResp) {
    getTA2lWpResps().remove(TA2lWpResp);
    TA2lWpResp.setTA2lGroup(null);

    return TA2lWpResp;
  }

}