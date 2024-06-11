/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.uc;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UseCaseGroup;
import com.bosch.caltool.icdm.model.uc.UseCaseSection;
import com.bosch.caltool.icdm.model.uc.UsecaseModel;
import com.bosch.caltool.icdm.model.user.User;


/**
 * ICDM-1027 This class stores the parent usecase items for TUsecaseFavorite entity
 *
 * @author mkl2cob
 */

public class FavUseCaseItemNode implements Comparable<FavUseCaseItemNode> {

  /**
   * uc item type
   */
  private final IUseCaseItemClientBO ucType;

  /**
   * Apic User to which the private usecase item belongs to
   */
  private User apicUser;


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
   * Unique id
   */
  private final Long objID;

  private final UseCaseDataHandler useCaseDataHandler;

  private Long pidCardId;


  /**
   * Constructor for Private usecase fav node
   *
   * @param objID usecase ID
   * @param ucType uc item type
   * @param apicUser ApicUser
   * @param favUcItem FavUseCaseItem
   * @param parent parent uc fav node
   */
  protected FavUseCaseItemNode(final Long objID, final IUseCaseItemClientBO ucType, final User apicUser,
      final FavUseCaseItem favUcItem, final FavUseCaseItemNode parent, final UseCaseDataHandler useCaseDataHandler) {
    this.objID = objID;
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
    addThisObjToUserCache(useCaseDataHandler);
    this.visible = true;
    this.useCaseDataHandler = useCaseDataHandler;
  }


  /**
   * Constructor for Project usecase fav node
   *
   * @param objID usecase ID
   * @param ucType uc item type
   * @param pidCard PIDCard
   * @param favUcItem FavUseCaseItem
   * @param parent parent uc fav node
   * @param useCaseDataHandler
   */
  protected FavUseCaseItemNode(final Long objID, final IUseCaseItemClientBO ucType, final Long pidCard,
      final FavUseCaseItem favUcItem, final FavUseCaseItemNode parent, final UseCaseDataHandler useCaseDataHandler) {
    this.objID = objID;
    this.ucType = ucType;
    this.pidCardId = pidCard;
    this.favUcItem = favUcItem;
    this.parent = parent;
    if (this.parent != null) {
      if (CommonUtils.isNull(this.parent.childNodeSet)) {
        this.parent.childNodeSet = Collections.synchronizedSortedSet(new TreeSet<FavUseCaseItemNode>());
      }
      this.parent.childNodeSet.add(this);
    }
    addThisObjToProjCache(useCaseDataHandler);
    this.visible = true;
    this.useCaseDataHandler = useCaseDataHandler;
  }

  /**
   * id
   *
   * @return id
   */
  public Long getID() {
    return this.objID;
  }


  /**
   * @return String
   */
  public String getName() {
    return getUseCaseItem().getName();
  }


  /**
   * @return IUseCaseItemClientBO : Usecase/Usecase group /Usecase Section
   */
  public IUseCaseItemClientBO getUseCaseItem() {
    return this.ucType;

  }

  /**
   * @return Apic User if this is a private Favourite item
   */
  public User getApicUser() {
    return this.apicUser;
  }

  /**
   * @return PIDCard if this is a project favourite item
   */
  public Long getPIDCId() {
    return this.pidCardId;
  }

  /**
   * @return FavUseCaseItem if it is persisted in entity
   */
  public FavUseCaseItem getFavUcItem() {
    return this.favUcItem;
  }

  /**
   * @param ucDataHandler UseCaseDataHandler
   * @return Sorted set of childUcItems
   */
  public SortedSet<IUseCaseItemClientBO> getChildUCItems(final UseCaseDataHandler ucDataHandler) {
    SortedSet<IUseCaseItemClientBO> childSet = new TreeSet<>();
    if (getUseCaseItem() instanceof UseCaseGroupClientBO) {
      UseCaseGroupClientBO grp = (UseCaseGroupClientBO) getUseCaseItem();
      Set<Long> childGrpIds = ucDataHandler.getUseCaseDetailsModel().getChildGroupSetMap().get(grp.getID());
      if (null != childGrpIds) {
        for (Long grpId : childGrpIds) {
          UseCaseGroup ucg = ucDataHandler.getUseCaseDetailsModel().getUseCaseGroupMap().get(grpId);
          UseCaseGroup parentucg = ucDataHandler.getUseCaseDetailsModel().getUseCaseGroupMap().get(grp.getID());
          UseCaseGroupClientBO parentUcgClientBO =
              new UseCaseGroupClientBO(parentucg, ucDataHandler.getUseCaseDetailsModel(), null);
          UseCaseGroupClientBO ucBo =
              new UseCaseGroupClientBO(ucg, ucDataHandler.getUseCaseDetailsModel(), parentUcgClientBO);
          if (!ucBo.isDeleted()) {
            childSet.add(ucBo);
          }
        }
      }
      Set<Long> childUCIds = ucDataHandler.getUseCaseDetailsModel().getChildUsecaseSetMap().get(grp.getID());
      if (null != childUCIds) {
        for (Long ucId : childUCIds) {
          UseCase uc = ucDataHandler.getUseCaseDetailsModel().getUsecaseMap().get(ucId);
          UseCaseGroup ucg = ucDataHandler.getUseCaseDetailsModel().getUseCaseGroupMap().get(uc.getGroupId());
          UseCaseGroupClientBO ucgClientBO =
              new UseCaseGroupClientBO(ucg, ucDataHandler.getUseCaseDetailsModel(), null);
          UsecaseClientBO ucBo = new UsecaseClientBO(uc, ucgClientBO);
          if (!ucBo.isDeleted()) {
            childSet.add(ucBo);
          }
        }
      }
    }
    else if (getUseCaseItem() instanceof UsecaseClientBO) {
      UsecaseClientBO uc = (UsecaseClientBO) getUseCaseItem();

      Set<Long> childSectionIds =
          ucDataHandler.getUseCaseDetailsModel().getUsecaseDetailsModelMap().get(uc.getID()).getFirstLevelUCSIDSet();
      if (null != childSectionIds) {
        for (Long secId : childSectionIds) {
          UseCaseSection ucs = ucDataHandler.getUseCaseDetailsModel().getUsecaseDetailsModelMap().get(uc.getID())
              .getUcSectionMap().get(secId);
          UseCase usc = ucDataHandler.getUseCaseDetailsModel().getUsecaseMap().get(uc.getID());
          UseCaseGroup ucg = ucDataHandler.getUseCaseDetailsModel().getUseCaseGroupMap().get(uc.getParent().getID());
          UseCaseGroupClientBO ucgClientBO =
              new UseCaseGroupClientBO(ucg, ucDataHandler.getUseCaseDetailsModel(), null);

          UseCaseSectionClientBO ucBo = new UseCaseSectionClientBO(
              ucDataHandler.getUseCaseDetailsModel().getUsecaseDetailsModelMap().get(uc.getID()), ucs,
              new UsecaseClientBO(usc, ucgClientBO));
          if (!ucBo.isDeleted()) {
            childSet.add(ucBo);
          }
        }
      }
    }
    else if (getUseCaseItem() instanceof UseCaseSectionClientBO) {
      UseCaseSectionClientBO ucSectionBo = (UseCaseSectionClientBO) getUseCaseItem();

      UsecaseModel usecaseModel = ucDataHandler.getUseCaseDetailsModel().getUsecaseDetailsModelMap()
          .get(ucSectionBo.getUseCaseSection().getUseCaseId());
      Set<Long> childSectionIds = usecaseModel.getChildSectionsMap().get(ucSectionBo.getID());
      for (Long secId : childSectionIds) {

        Map<Long, UseCaseSection> ucSectionMap = usecaseModel.getUcSectionMap();
        if (CommonUtils.isNotEmpty(ucSectionMap)) {
          UseCaseSection ucs = ucSectionMap.get(secId);
          UseCase usc = ucDataHandler.getUseCaseDetailsModel().getUsecaseMap()
              .get(ucSectionBo.getUseCaseSection().getUseCaseId());
          UseCaseGroup ucg = ucDataHandler.getUseCaseDetailsModel().getUseCaseGroupMap().get(usc.getGroupId());
          UseCaseGroupClientBO ucgClientBO =
              new UseCaseGroupClientBO(ucg, ucDataHandler.getUseCaseDetailsModel(), null);

          UseCaseSectionClientBO ucBo =
              new UseCaseSectionClientBO(usecaseModel, ucs, new UsecaseClientBO(usc, ucgClientBO));
          if (!ucBo.isDeleted()) {
            childSet.add(ucBo);
          }
        }

      }
    }

    return childSet;
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
      if (getUseCaseItem() instanceof UseCaseGroupClientBO) {
        UseCaseGroupClientBO grpBO = (UseCaseGroupClientBO) getUseCaseItem();
        return isUcGroupMapped(attr, grpBO);
      }
      else if (getUseCaseItem() instanceof UsecaseClientBO) {
        UsecaseClientBO ucBO = (UsecaseClientBO) getUseCaseItem();
        ucBO.setUsecaseEditorModel(this.useCaseDataHandler.getUseCaseDetailsModel().getUsecaseDetailsModelMap()
            .get(ucBO.getUseCase().getId()));
        return isUCMapped(attr, ucBO);
      }
      else if (getUseCaseItem() instanceof UseCaseSectionClientBO) {
        UseCaseSectionClientBO ucBO = (UseCaseSectionClientBO) getUseCaseItem();
        return isUCSMapped(attr, ucBO);
      }
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

  private boolean isUcGroupMapped(final Attribute attr, final UseCaseGroupClientBO grp) {

    // Check whether child groups are mapped
    for (UseCaseGroupClientBO childUcg : grp.getChildGroups(false)) {
      if (isUcGroupMapped(attr, childUcg)) {
        return true;
      }
    }

    // Check whether use cases are mapped
    for (UsecaseClientBO ucase : grp.getUseCases(false)) {
      ucase.setUsecaseEditorModel(
          this.useCaseDataHandler.getUseCaseDetailsModel().getUsecaseDetailsModelMap().get(ucase.getUseCase().getId()));
      if (isUCMapped(attr, ucase)) {
        return true;
      }
    }

    // if no mapping done, then return false
    return false;

  }


  private boolean isUCMapped(final Attribute attr, final UsecaseClientBO uc) {
    if (uc.canMapAttributes()) {
      Set<Long> mappedAttrIds = this.useCaseDataHandler.getUseCaseDetailsModel().getUsecaseDetailsModelMap()
          .get(uc.getId()).getUcItemAttrMap().get(uc.getId());
      if (!CommonUtils.isNullOrEmpty(mappedAttrIds) && mappedAttrIds.contains(attr.getId())) {
        return true;
      }
    }
    else {
      for (UseCaseSectionClientBO ucSection : uc.getUseCaseSections(false)) {
        if (isUCSMapped(attr, ucSection)) {
          return true;

        }
      }
    }

    return false;
  }

  /**
   * @param attr
   * @param ucs
   * @return
   */
  public boolean isUCSMapped(final Attribute attr, final UseCaseSectionClientBO ucs) {
    if (ucs.canMapAttributes()) {
      Set<Long> mappedAttrIds = this.useCaseDataHandler.getUseCaseDetailsModel().getUsecaseDetailsModelMap()
          .get(ucs.getUseCaseSection().getUseCaseId()).getUcItemAttrMap().get(ucs.getId());
      if (!CommonUtils.isNullOrEmpty(mappedAttrIds) && mappedAttrIds.contains(attr.getId())) {
        return true;
      }
    }
    else {
      for (UseCaseSectionClientBO ucsChild : ucs.getChildSections(false)) {
        if (isUCSMapped(attr, ucsChild)) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * ICDM-1040
   *
   * @param favUc FavUseCaseItem
   * @param pidCardId PIDCard id
   * @param useCaseDataHandler UseCaseDataHandler
   * @return FavUseCaseItemNode
   */
  public static FavUseCaseItemNode getFavUcNode(final FavUseCaseItem favUc, final Long pidCardId,
      final UseCaseDataHandler useCaseDataHandler) {
    final Long nodeID = favUc.getUseCaseItem(useCaseDataHandler).getID();
    FavUseCaseItemNode retNode = useCaseDataHandler.getPidcFavUcNode(pidCardId, nodeID);
    if (retNode == null) {
      retNode = new FavUseCaseItemNode(nodeID, favUc.getUseCaseItem(useCaseDataHandler), pidCardId, favUc,
          getParentNode(pidCardId, favUc.getUseCaseItem(useCaseDataHandler), useCaseDataHandler), useCaseDataHandler);
    }
    return retNode;
  }


  /**
   * ICDM-1040
   *
   * @param dataProvider ApicDataProvider
   * @param pidc PIDCard
   * @param abstractUseCaseItem AbstractUseCaseItem
   * @param useCaseDataHandler
   * @return FavUseCaseItemNode - parent node
   */
  private static FavUseCaseItemNode getParentNode(final Long pidcId, final IUseCaseItemClientBO abstractUseCaseItem,
      final UseCaseDataHandler useCaseDataHandler) {

    IUseCaseItemClientBO parentUcItem = getParent(abstractUseCaseItem);

    if (parentUcItem == null) {
      // No parent Nodes
      return null;
    }
    Long parentNodeID = parentUcItem.getID();

    FavUseCaseItemNode retNode = useCaseDataHandler.getPidcFavUcNode(pidcId, parentNodeID);
    if (retNode == null) {
      // Create node
      FavUseCaseItemNode myParent = getParentNode(pidcId, parentUcItem, useCaseDataHandler);
      retNode = new FavUseCaseItemNode(parentNodeID, parentUcItem, pidcId, null, myParent, useCaseDataHandler);
    }

    return retNode;
  }

  /**
   * ICDM-1040
   *
   * @param favUc FavUseCaseItem
   * @param user ApicUser
   * @param useCaseDataHandler UseCaseDataHandler
   * @return FavUseCaseItemNode
   */
  public static FavUseCaseItemNode getFavUcNode(final FavUseCaseItem favUc, final User user,
      final UseCaseDataHandler useCaseDataHandler) {
    if (null != favUc.getUseCaseItem(useCaseDataHandler)) {
      final Long nodeID = favUc.getUseCaseItem(useCaseDataHandler).getID();
      FavUseCaseItemNode retNode = useCaseDataHandler.getUserFavUcNode(nodeID);
      if (retNode == null) {
        retNode = new FavUseCaseItemNode(nodeID, favUc.getUseCaseItem(useCaseDataHandler), user, favUc,
            getParentNode(user, favUc.getUseCaseItem(useCaseDataHandler), useCaseDataHandler), useCaseDataHandler);
      }
      return retNode;
    }
    return null;
  }


  /**
   * ICDM-1040
   *
   * @param dataProvider ApicDataProvider
   * @param pidc PIDCard
   * @param abstractUseCaseItem AbstractUseCaseItem
   * @param useCaseDataHandler
   * @return FavUseCaseItemNode - parent node
   */
  private static FavUseCaseItemNode getParentNode(final User user, final IUseCaseItemClientBO abstractUseCaseItem,
      final UseCaseDataHandler useCaseDataHandler) {
    IUseCaseItemClientBO parentUcItem = getParent(abstractUseCaseItem);

    if (parentUcItem == null) {
      // No parent Nodes
      return null;
    }
    Long parentNodeID = parentUcItem.getID();

    FavUseCaseItemNode retNode = useCaseDataHandler.getUserFavUcNode(parentNodeID);
    if (retNode == null) {
      // Create node
      FavUseCaseItemNode myParent = getParentNode(user, parentUcItem, useCaseDataHandler);
      retNode = new FavUseCaseItemNode(parentNodeID, parentUcItem, user, null, myParent, useCaseDataHandler);
    }

    return retNode;
  }


  /**
   * @param abstractUseCaseItem
   * @param useCaseDataHandler
   * @return
   */
  private static IUseCaseItemClientBO getParent(final IUseCaseItemClientBO abstractUseCaseItem) {
    IUseCaseItemClientBO parentUcItem = null;
    if (abstractUseCaseItem instanceof UseCaseGroupClientBO) {
      parentUcItem = ((UseCaseGroupClientBO) abstractUseCaseItem).getParent();

    }
    else if (abstractUseCaseItem instanceof UsecaseClientBO) {
      parentUcItem = ((UsecaseClientBO) abstractUseCaseItem).getParent();

    }
    else if (abstractUseCaseItem instanceof UseCaseSectionClientBO) {
      if (null != ((UseCaseSectionClientBO) abstractUseCaseItem).getParent()) {
        parentUcItem = ((UseCaseSectionClientBO) abstractUseCaseItem).getParent();
      }


    }
    return parentUcItem;
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
   *
   */

  public int compareTo(final FavUseCaseItemNode arg0) {

    int compareName = ApicUtil.compare(getName(), arg0.getName());
    if (compareName == 0) {
      return ApicUtil.compare(getID(), arg0.getID());
    }
    return compareName;
  }

  /**
   *
   */

  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }

  /**
   *
   */

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * Adds this node to the data cache
   *
   * @param useCaseDataHandler
   */
  private void addThisObjToProjCache(final UseCaseDataHandler useCaseDataHandler) {
    useCaseDataHandler.getPidcFavUcNodes(this.pidCardId).put(getID(), this);
  }

  /**
   * Adds this node to the data cache
   *
   * @param useCaseDataHandler
   */
  private void addThisObjToUserCache(final UseCaseDataHandler useCaseDataHandler) {
    useCaseDataHandler.getCurrentFavUcNodes().put(getID(), this);
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

  public String getDescription() {
    if (CommonUtils.isNull(this.favUcItem)) {
      return "[Virtual Node]:" + getUseCaseItem().getName();
    }
    return "[Favorite Node]:" + getUseCaseItem().getName();
  }


  /**
   *
   */

  public String getCreatedUser() {
    return getFavUcItem().getCreatedUser();
  }


  /**
   *
   */

  public String getModifiedUser() {
    return getFavUcItem().getModifiedUser();
  }


  /**
   *
   */

  public String getCreatedDate() {
    return getFavUcItem().getCreatedDate();
  }


  /**
   *
   */

  public String getModifiedDate() {
    return getFavUcItem().getModifiedDate();
  }


  /**
   * @return the tooltip text
   */

  public String getToolTip() {
    // If use case item is available add prefix 'Favorite Node' else add 'Virtual Node'
    return (CommonUtils.isNull(this.favUcItem) ? "[Virtual Node]\n" : "[Favorite Node]\n") +
        getUseCaseItem().getToolTip();
  }

  /**
   * @return the parent
   */
  public FavUseCaseItemNode getParent() {
    return this.parent;
  }


}
