/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;
import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;


/**
 * ICDM-1027 This class stores the parent usecase items for TUsecaseFavorite entity
 *
 * @author mkl2cob
 */
public class FavUseCaseItemNode extends ApicObject implements Comparable<FavUseCaseItemNode> {


  /**
   * uc item type
   */
  private final IEntityType<?, ?> ucType;

  /**
   * Apic User to which the private usecase item belongs to
   */
  private ApicUser apicUser;

  /**
   * Project id card to which project usecase item belongs to
   */
  private PIDCard pidCard;

  /**
   * FavUseCaseItem - null if not FavUseCaseItem
   */
  private FavUseCaseItem favUcItem;

  /**
   * parent node
   */
  private final FavUseCaseItemNode parent;

  /**
   * boolean to tell whether the node can be shown or not
   */
  private boolean visible = true;


  /**
   * All child nodes
   */
  SortedSet<FavUseCaseItemNode> childNodeSet;


  /**
   * Constructor for Private usecase fav node
   *
   * @param dataProvider AbstractDataProvider
   * @param objID usecase ID
   * @param ucType uc item type
   * @param apicUser ApicUser
   * @param favUcItem FavUseCaseItem
   * @param parent parent uc fav node
   */
  protected FavUseCaseItemNode(final AbstractDataProvider dataProvider, final Long objID,
      final IEntityType<?, ?> ucType, final ApicUser apicUser, final FavUseCaseItem favUcItem,
      final FavUseCaseItemNode parent) {
    super(dataProvider, objID);
    this.ucType = ucType;
    this.apicUser = apicUser;
    this.favUcItem = favUcItem;
    this.parent = parent;
    if (this.parent != null) {
      if (CommonUtils.isNull(this.parent.childNodeSet)) {
        this.parent.childNodeSet = Collections.synchronizedSortedSet(new TreeSet<FavUseCaseItemNode>());
      }
      this.parent.childNodeSet.add(this);
    }
    addThisObjToUserCache();
    this.visible = true;
  }


  /**
   * Constructor for Project usecase fav node
   *
   * @param dataProvider AbstractDataProvider
   * @param objID usecase ID
   * @param ucType uc item type
   * @param pidCard PIDCard
   * @param favUcItem FavUseCaseItem
   * @param parent parent uc fav node
   */
  protected FavUseCaseItemNode(final AbstractDataProvider dataProvider, final Long objID,
      final IEntityType<?, ?> ucType, final PIDCard pidCard, final FavUseCaseItem favUcItem,
      final FavUseCaseItemNode parent) {
    super(dataProvider, objID);
    this.ucType = ucType;
    this.pidCard = pidCard;
    this.favUcItem = favUcItem;
    this.parent = parent;
    if (this.parent != null) {
      if (CommonUtils.isNull(this.parent.childNodeSet)) {
        this.parent.childNodeSet = Collections.synchronizedSortedSet(new TreeSet<FavUseCaseItemNode>());
      }
      this.parent.childNodeSet.add(this);
    }
    addThisObjToProjCache();
    this.visible = true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getUseCaseItem().getName();
  }

  /**
   * {@inheritDoc} returns USE_CASE/USE_CASE_SECT/USE_CASE_GROUP based on type
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return this.ucType;
  }

  /**
   * @return AbstractUseCaseItem : Usecase/Usecase group /Usecase Section
   */
  public AbstractUseCaseItem getUseCaseItem() {
    EntityType entType = (EntityType) this.ucType;

    if (CommonUtils.isEqual(this.ucType, EntityType.USE_CASE_GROUP)) {
      return (UseCaseGroup) entType.getDataObject((ApicDataProvider) getDataProvider(), getID());
    }
    else if (CommonUtils.isEqual(this.ucType, EntityType.USE_CASE)) {
      return (UseCase) entType.getDataObject((ApicDataProvider) getDataProvider(), getID());
    }
    else if (CommonUtils.isEqual(this.ucType, EntityType.USE_CASE_SECT)) {
      return (UseCaseSection) entType.getDataObject((ApicDataProvider) getDataProvider(), getID());
    }
    return null;
  }

  /**
   * @return Apic User if this is a private Favourite item
   */
  public ApicUser getApicUser() {
    return this.apicUser;
  }

  /**
   * @return PIDCard if this is a project favourite item
   */
  public PIDCard getPIDC() {
    return this.pidCard;
  }

  /**
   * @return FavUseCaseItem if it is persisted in entity
   */
  public FavUseCaseItem getFavUcItem() {
    return this.favUcItem;
  }

  /**
   * @return Sorted set of childUcItems
   */
  public SortedSet<AbstractUseCaseItem> getChildUCItems() {
    return getUseCaseItem().getChildUCItems();
  }

  /**
   * @return sorted set of child nodes,null in case of fav use case item is not null
   */
  public SortedSet<FavUseCaseItemNode> getChildFavNodes() {

    if (this.favUcItem == null) {
      if (CommonUtils.isNotNull(this.childNodeSet)) {
        SortedSet<FavUseCaseItemNode> returnSet = new TreeSet<FavUseCaseItemNode>(this.childNodeSet);
        for (FavUseCaseItemNode node : this.childNodeSet) {
          // if the node is not visible , remove the node from the return set
          if (!node.isVisible()) {
            returnSet.remove(node);
          }
        }
        return returnSet;
      }
    }
    return null;

  }

  /**
   * @return true if the usecase item is deleted
   */
  public boolean isDeleted() {
    return getUseCaseItem().isDeleted();
  }

  /**
   * @param attr Attribute
   * @return true if the attr is mapped to UC item
   */
  public boolean isMapped(final Attribute attr) {
    if (CommonUtils.isNotNull(this.favUcItem)) {
      return getUseCaseItem().isMapped(attr);
    }
    if (CommonUtils.isNotNull(getChildFavNodes())) {
      for (FavUseCaseItemNode favNode : getChildFavNodes()) {
        if (favNode.isMapped(attr)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * ICDM-1040
   *
   * @param dataProvider ApicDataProvider
   * @param favUc FavUseCaseItem
   * @param pidCard PIDCard
   * @return FavUseCaseItemNode
   */
  public static FavUseCaseItemNode getFavUcNode(final ApicDataProvider dataProvider, final FavUseCaseItem favUc,
      final PIDCard pidCard) {
    final Long nodeID = favUc.getUseCaseItem().getID();
    FavUseCaseItemNode retNode = dataProvider.getDataCache().getPidcFavUcNode(pidCard.getID(), nodeID);
    if (retNode == null) {
      retNode = new FavUseCaseItemNode(dataProvider, nodeID, favUc.getUseCaseItem().getEntityType(), pidCard, favUc,
          getParentNode(dataProvider, pidCard, favUc.getUseCaseItem()));
    }
    return retNode;
  }


  /**
   * ICDM-1040
   *
   * @param dataProvider ApicDataProvider
   * @param pidc PIDCard
   * @param abstractUseCaseItem AbstractUseCaseItem
   * @return FavUseCaseItemNode - parent node
   */
  private static FavUseCaseItemNode getParentNode(final ApicDataProvider dataProvider, final PIDCard pidc,
      final AbstractUseCaseItem abstractUseCaseItem) {

    AbstractUseCaseItem parentUcItem = abstractUseCaseItem.getParent();

    if (parentUcItem == null) {
      // No parent Nodes
      return null;
    }
    Long parentNodeID = parentUcItem.getID();

    FavUseCaseItemNode retNode = dataProvider.getDataCache().getPidcFavUcNode(pidc.getID(), parentNodeID);
    if (retNode == null) {
      // Create node
      FavUseCaseItemNode myParent = getParentNode(dataProvider, pidc, parentUcItem);
      retNode = new FavUseCaseItemNode(dataProvider, parentNodeID, parentUcItem.getEntityType(), pidc, null, myParent);
    }

    return retNode;
  }

  /**
   * ICDM-1040
   *
   * @param dataProvider ApicDataProvider
   * @param favUc FavUseCaseItem
   * @param user ApicUser
   * @return FavUseCaseItemNode
   */
  public static FavUseCaseItemNode getFavUcNode(final ApicDataProvider dataProvider, final FavUseCaseItem favUc,
      final ApicUser user) {
    final Long nodeID = favUc.getUseCaseItem().getID();
    FavUseCaseItemNode retNode = dataProvider.getDataCache().getUserFavUcNode(nodeID);
    if (retNode == null) {
      retNode = new FavUseCaseItemNode(dataProvider, nodeID, favUc.getUseCaseItem().getEntityType(), user, favUc,
          getParentNode(dataProvider, user, favUc.getUseCaseItem()));
    }
    return retNode;
  }


  /**
   * ICDM-1040
   *
   * @param dataProvider ApicDataProvider
   * @param pidc PIDCard
   * @param abstractUseCaseItem AbstractUseCaseItem
   * @return FavUseCaseItemNode - parent node
   */
  private static FavUseCaseItemNode getParentNode(final ApicDataProvider dataProvider, final ApicUser user,
      final AbstractUseCaseItem abstractUseCaseItem) {

    AbstractUseCaseItem parentUcItem = abstractUseCaseItem.getParent();

    if (parentUcItem == null) {
      // No parent Nodes
      return null;
    }
    Long parentNodeID = parentUcItem.getID();

    FavUseCaseItemNode retNode = dataProvider.getDataCache().getUserFavUcNode(parentNodeID);
    if (retNode == null) {
      // Create node
      FavUseCaseItemNode myParent = getParentNode(dataProvider, user, parentUcItem);
      retNode = new FavUseCaseItemNode(dataProvider, parentNodeID, parentUcItem.getEntityType(), user, null, myParent);
    }

    return retNode;
  }

  /**
   * ICDM-1040
   *
   * @return parent nodes of this node in the hierarchy
   */
  public Map<Long, FavUseCaseItemNode> getParentNodes() {
    Map<Long, FavUseCaseItemNode> parNodeMap = new ConcurrentHashMap<Long, FavUseCaseItemNode>();
    FavUseCaseItemNode node = getParentNode();
    while (node != null) {
      parNodeMap.put(node.getID(), node);
      node = node.getParentNode();
    }
    return parNodeMap;
  }

  /**
   * ICDM-1040
   *
   * @return Top node of this node
   */
  public FavUseCaseItemNode getTopNode() {
    FavUseCaseItemNode parNode = getParentNode();
    FavUseCaseItemNode topNode = parNode;
    while (parNode != null) {
      topNode = parNode;
      parNode = parNode.getParentNode();
    }
    return topNode;
  }

  /**
   * ICDM-1040
   *
   * @return parent node
   */
  private FavUseCaseItemNode getParentNode() {
    return this.parent;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final FavUseCaseItemNode arg0) {
    return ApicUtil.compare(getName(), arg0.getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * Adds this node to the data cache
   */
  private void addThisObjToProjCache() {
    getDataCache().getPidcFavUcNodes(this.pidCard.getID()).put(getID(), this);
  }

  /**
   * Adds this node to the data cache
   */
  private void addThisObjToUserCache() {
    getDataCache().getCurrentFavUcNodes().put(getID(), this);
  }

  /**
   * @param isVisible visible
   */
  public void setVisible(final boolean isVisible) {
    this.visible = isVisible;
    FavUseCaseItemNode parentNode = getParentNode();
    while (parentNode != null) {
      parentNode.setVisible(true);
      parentNode = parentNode.getParentNode();
    }
  }

  /**
   * @return true if the node is not deleted
   */
  public boolean isVisible() {
    return this.visible;
  }

  /**
   * ICDM-1092 clears the child nodeSet
   */
  public void clearChildNodeSet() {
    this.childNodeSet.clear();
  }


  /**
   * @param favUcItem2 FavUseCaseItem
   */
  public void setUcItem(final FavUseCaseItem favUcItem2) {
    this.favUcItem = favUcItem2;
  }


  /**
   * ICDM-1040 reset the nodes
   */
  public void resetNode() {
    setVisible(false);
    this.favUcItem = null;
  }

  /**
   * @return node type with descrioption
   */
  @Override
  public String getDescription() {
    if (CommonUtils.isNull(this.favUcItem)) {
      return "[Virtual Node]:" + getUseCaseItem().getDescription();
    }
    return "[Favorite Node]:" + getUseCaseItem().getDescription();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return getFavUcItem().getCreatedUser();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return getFavUcItem().getModifiedUser();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return getFavUcItem().getCreatedDate();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    return getFavUcItem().getModifiedDate();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTip() {
    // If use case item is available add prefix 'Favorite Node' else add 'Virtual Node'
    return (CommonUtils.isNull(this.favUcItem) ? "[Virtual Node]\n" : "[Favorite Node]\n") +
        getUseCaseItem().getToolTip();
  }

}
