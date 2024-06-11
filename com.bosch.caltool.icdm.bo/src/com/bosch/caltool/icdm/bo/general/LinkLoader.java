/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.general;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TLink;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.general.Link;


/**
 * @author bne4cob
 */
public class LinkLoader extends AbstractBusinessObject<Link, TLink> {

  /**
   * @param serviceData Service Data
   */
  public LinkLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.LINK, TLink.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Link createDataObject(final TLink entity) throws DataException {
    Link ret = new Link();

    ret.setId(entity.getLinkId());

    ret.setDescription(getLangSpecTxt(entity.getDescEng(), entity.getDescGer()));
    ret.setDescriptionEng(entity.getDescEng());
    ret.setDescriptionGer(entity.getDescGer());

    ret.setNodeId(entity.getNodeId());
    ret.setNodeType(formatNodeType(entity.getNodeType()));

    ret.setLinkUrl(entity.getLinkUrl());

    ret.setCreatedDate(timestamp2Date(entity.getCreatedDate()));
    ret.setCreatedUser(entity.getCreatedUser());
    ret.setModifiedDate(timestamp2Date(entity.getModifiedDate()));
    ret.setModifiedUser(entity.getModifiedUser());
    ret.setVersion(entity.getVersion());

    ret.setAttributeValueId(null == entity.getTabvAttrValue() ? null : entity.getTabvAttrValue().getValueId());

    return ret;
  }

  private String formatNodeType(final String type) {
    return type.toUpperCase(Locale.getDefault());
  }

  /**
   * @param nodeType node type
   * @return Set of nodes with links
   */
  public Set<Long> getNodesWithLink(final String nodeType) {
    TypedQuery<Long> qry = getEntMgr().createNamedQuery(TLink.NQ_GET_NODES_WITH_LINKS_BY_TYPE, Long.class);
    qry.setParameter("nodeType", formatNodeType(nodeType));

    return new HashSet<>(qry.getResultList());
  }


  /**
   * Fetch links of the given node
   *
   * @param nodeId node id
   * @param type node type
   * @return Map of links. Key - link id, value link
   * @throws DataException error during data retrieval
   */
  public Map<Long, Link> getLinksByNode(final Long nodeId, final String type) throws DataException {

    Map<Long, Link> retMap = new HashMap<>();

    TypedQuery<TLink> qry = getEntMgr().createNamedQuery(TLink.NQ_GET_BY_NODE, TLink.class);
    qry.setParameter("nodeId", nodeId);
    qry.setParameter("nodeType", formatNodeType(type));

    for (TLink entity : qry.getResultList()) {
      retMap.put(entity.getLinkId(), createDataObject(entity));
    }

    return retMap;
  }

  /**
   * Get all Links records in system
   *
   * @return Map. Key - NodeType, Value - Set<NodeId>
   */
  public Map<String, Set<Long>> getAllNodeIdByType() {
    Map<String, Set<Long>> objMap = new HashMap<>();
    TypedQuery<TLink> tQuery = getEntMgr().createNamedQuery(TLink.GET_ALL, TLink.class);
    List<TLink> dbObj = tQuery.getResultList();
    for (TLink entity : dbObj) {
      if (objMap.containsKey(entity.getNodeType())) {
        Set<Long> nodeIdSet = objMap.get(entity.getNodeType());
        nodeIdSet.add(entity.getNodeId());
      }
      else {
        Set<Long> nodeIdSet = new HashSet<>();
        nodeIdSet.add(entity.getNodeId());
        objMap.put(entity.getNodeType(), nodeIdSet);
      }
    }
    return objMap;
  }

  /**
   * @return Map<String, String>
   * @throws DataException exception while getting data of links
   */
  public Map<String, Link> getHelpLinks() throws DataException {
    Map<String, Link> helpLinksMap = new HashMap<>();
    TypedQuery<TLink> tQuery = getEntMgr().createNamedQuery(TLink.NQ_GET_HELP_LINKS, TLink.class);
    List<TLink> dbObj = tQuery.getResultList();
    for (TLink entity : dbObj) {
      helpLinksMap.put(entity.getNodeType(), getDataObjectByID(entity.getLinkId()));
    }
    return helpLinksMap;
  }

}
