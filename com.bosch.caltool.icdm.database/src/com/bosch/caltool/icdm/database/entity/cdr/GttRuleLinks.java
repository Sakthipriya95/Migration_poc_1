package com.bosch.caltool.icdm.database.entity.cdr;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the GTT_RULE_LINKS database table.
 */
@Entity
@Table(name = "GTT_RULE_LINKS")
@NamedQueries(value = { @NamedQuery(name = GttRuleLinks.NQ_DELETE_GTT_LINKS, query = "delete from GttRuleLinks temp") })
public class GttRuleLinks implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
  *
  */
  public static final String NQ_DELETE_GTT_LINKS = "GttRuleRemark.deletegttlinks";

  @Id
  private long id;

  @Column(name = "RULE_ID")
  private long ruleId;

  @Column(name = "REV_ID")
  private long revId;

  public GttRuleLinks() {}

  public long getId() {
    return this.id;
  }

  public void setId(final long id) {
    this.id = id;
  }

  public long getRuleId() {
    return this.ruleId;
  }

  public void setRuleId(final long ruleId) {
    this.ruleId = ruleId;
  }

  public long getRevId() {
    return this.revId;
  }

  public void setRevId(final long revId) {
    this.revId = revId;
  }

}
