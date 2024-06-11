package com.bosch.caltool.icdm.bo.cdr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.cdr.GttRuleLinks;
import com.bosch.caltool.icdm.database.entity.cdr.TRuleLinks;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.RuleLinks;
import com.bosch.ssd.icdm.model.CDRRule;


/**
 * Loader class for RuleLinks
 *
 * @author UKT1COB
 */
public class RuleLinksLoader extends AbstractBusinessObject<RuleLinks, TRuleLinks> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public RuleLinksLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.RULE_LINKS, TRuleLinks.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected RuleLinks createDataObject(final TRuleLinks entity) throws DataException {
    RuleLinks object = new RuleLinks();

    setCommonFields(object, entity);

    object.setRevId(entity.getRevId());
    object.setRuleId(entity.getRuleId());
    object.setLink(entity.getLink());
    object.setDescEng(entity.getDescEng());
    object.setDescGer(entity.getDescGer());

    return object;
  }


  /***
   * @param ruleList
   * @return
   * @throws DataException exception
   */
  private Map<Long, List<TRuleLinks>> getByRuleRevIds(final List<CDRRule> ruleList) throws DataException {

    Map<Long, List<TRuleLinks>> ruleLinksMap = new HashMap<>();

    if (ruleList.size() < 2) {
      for (CDRRule cdrRuleId : ruleList) {
        List<TRuleLinks> ruleLinksList = new ArrayList<>();
        TypedQuery<TRuleLinks> namedQuery =
            getEntMgr().createNamedQuery(TRuleLinks.NQ_GET_BY_RULE_REV_ID, TRuleLinks.class);
        BigDecimal ruleId = cdrRuleId.getRuleId();
        namedQuery.setParameter("ruleId", ruleId);
        namedQuery.setParameter("revId", cdrRuleId.getRevId());

        // Fill result Map
        ruleLinksList.addAll(namedQuery.getResultList());
        ruleLinksMap.put(ruleId.longValue(), ruleLinksList);
      }
    }
    else {
      EntityManager entMgr = null;
      try {
        entMgr = ObjectStore.getInstance().getEntityManagerFactory().createEntityManager();
        entMgr.getTransaction().begin();
        deleteGttRuleLinks(entMgr);
        createTempObjects(ruleList, entMgr);
        entMgr.flush();

        TypedQuery<TRuleLinks> namedQuery = entMgr.createNamedQuery(TRuleLinks.NQ_GET_BY_RULE_ID_GTT, TRuleLinks.class);
        List<TRuleLinks> ruleLinksList = namedQuery.getResultList();

        for (CDRRule rule : ruleList) {
          long cdrRuleId = rule.getRuleId().longValue();
          ruleLinksMap.put(cdrRuleId, ruleLinksList.stream()
              .filter(tRuleLink -> CommonUtils.isEqual(tRuleLink.getRuleId(), cdrRuleId)).collect(Collectors.toList()));
        }

        deleteGttRuleLinks(entMgr);
        entMgr.getTransaction().rollback();
      }

      catch (Exception exp) {
        throw new DataException(exp.getMessage(), exp);
      }
      finally {
        if ((entMgr != null) && entMgr.isOpen()) {
          entMgr.close();
        }
      }
    }

    return ruleLinksMap;
  }

  /**
   * @param ruleLinksMap
   * @param ruleLinksList
   * @param ruleId
   */
  private void fillResultMap(final Map<Long, List<TRuleLinks>> ruleLinksMap, final List<TRuleLinks> ruleLinksList,
      final Long ruleId) {
    if (ruleLinksMap.containsKey(ruleId)) {
      ruleLinksMap.get(ruleId).addAll(ruleLinksList);
    }
    else {
      ruleLinksMap.put(ruleId.longValue(), ruleLinksList);
    }
  }


  /**
   * @param ruleList rules to get Links
   * @return Map of cdr rule, review rule
   * @throws DataException exception from DB
   */
  public Map<Long, List<RuleLinks>> getRuleIdLinksMap(final List<CDRRule> ruleList) throws DataException {
    Map<Long, List<RuleLinks>> ruleLinksMap = new HashMap<>();
    Map<Long, CDRRule> cdrRuleMap = new HashMap<>();

    ruleList.forEach(cdr -> cdrRuleMap.put(cdr.getRuleId().longValue(), cdr));

    Map<Long, List<TRuleLinks>> resultMap = getByRuleRevIds(ruleList);
    for (Entry<Long, List<TRuleLinks>> ruleLinksEntityEntrySet : resultMap.entrySet()) {
      List<RuleLinks> ruleLinksObjList = new ArrayList<>();
      for (TRuleLinks ruleLinksEntity : ruleLinksEntityEntrySet.getValue()) {
        ruleLinksObjList.add(createDataObject(ruleLinksEntity));
      }
      ruleLinksMap.put(ruleLinksEntityEntrySet.getKey(), ruleLinksObjList);
    }

    return ruleLinksMap;
  }

  /**
   * @param ruleList
   * @param entMgr
   */
  private void createTempObjects(final List<CDRRule> ruleList, final EntityManager entMgr) {
    GttRuleLinks tempRuleLink;
    long recID = 1;
    // Create entities for all the parameters
    for (CDRRule rule : ruleList) {
      tempRuleLink = new GttRuleLinks();
      tempRuleLink.setId(recID);
      tempRuleLink.setRuleId(rule.getRuleId().longValue());
      tempRuleLink.setRevId(rule.getRevId().longValue());

      entMgr.persist(tempRuleLink);

      recID++;
    }
  }

  /**
   * @param entMgr
   */
  private void deleteGttRuleLinks(final EntityManager entMgr) {
    // Delete the existing records in this temp table, if any
    final Query delQuery = entMgr.createNamedQuery(GttRuleLinks.NQ_DELETE_GTT_LINKS);
    delQuery.executeUpdate();
  }


}
