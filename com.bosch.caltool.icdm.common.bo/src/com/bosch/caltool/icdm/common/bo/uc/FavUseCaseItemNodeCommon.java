/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.bo.uc;

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
 * @author dmr1cob
 */
public class FavUseCaseItemNodeCommon implements Comparable<FavUseCaseItemNodeCommon> {


  /**
   * uc item type
   */
  private final IUsecaseItemCommonBO ucType;

  /**
   * Apic User to which the private usecase item belongs to
   */
  private User apicUser;


  /**
   * FavUseCaseItem - null if not FavUseCaseItem
   */
  private FavUseCaseItemCommon favUcItem;

  /**
   * parent node
   */
  private final FavUseCaseItemNodeCommon parent;

  /**
   * boolean to tell whether the node can be shown or not
   */
  private boolean visible = true;


  /**
   * All child nodes
   */
  SortedSet<FavUseCaseItemNodeCommon> childNodeSet;

  /**
   * Unique id
   */
  private final Long objID;

  private final UseCaseCommonDataHandler useCaseDataHandler;

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
  protected FavUseCaseItemNodeCommon(final Long objID, final IUsecaseItemCommonBO ucType, final User apicUser,
      final FavUseCaseItemCommon favUcItem, final FavUseCaseItemNodeCommon parent,
      final UseCaseCommonDataHandler useCaseDataHandler) {
    this.objID = objID;
    this.ucType = ucType;
    this.apicUser = apicUser;
    this.favUcItem = favUcItem;
    this.parent = parent;
    if (this.parent != null) {
      if (CommonUtils.isNull(this.parent.childNodeSet)) {
        this.parent.childNodeSet = Collections.synchronizedSortedSet(new TreeSet<FavUseCaseItemNodeCommon>());
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
  protected FavUseCaseItemNodeCommon(final Long objID, final IUsecaseItemCommonBO ucType, final Long pidCard,
      final FavUseCaseItemCommon favUcItem, final FavUseCaseItemNodeCommon parent,
      final UseCaseCommonDataHandler useCaseDataHandler) {
    this.objID = objID;
    this.ucType = ucType;
    this.pidCardId = pidCard;
    this.favUcItem = favUcItem;
    this.parent = parent;
    if (this.parent != null) {
      if (CommonUtils.isNull(this.parent.childNodeSet)) {
        this.parent.childNodeSet = Collections.synchronizedSortedSet(new TreeSet<FavUseCaseItemNodeCommon>());
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
  protected Long getID() {
    return this.objID;
  }


  /**
   * @return String
   */
  public String getName() {
    return getUseCaseItem().getName();
  }


  /**
   * @return IUsecaseItemCommonBO : Usecase/Usecase group /Usecase Section
   */
  public IUsecaseItemCommonBO getUseCaseItem() {
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
  public FavUseCaseItemCommon getFavUcItem() {
    return this.favUcItem;
  }

  /**
   * @param useCaseCommonDataHandler {@link UseCaseCommonDataHandler}
   * @return Sorted set of childUcItems
   */
  public SortedSet<IUsecaseItemCommonBO> getChildUCItems(final UseCaseCommonDataHandler useCaseCommonDataHandler) {
    SortedSet<IUsecaseItemCommonBO> childSet = new TreeSet<>();
    if (getUseCaseItem() instanceof UseCaseGroupCommonBO) {
      UseCaseGroupCommonBO grp = (UseCaseGroupCommonBO) getUseCaseItem();
      Set<Long> childGrpIds = useCaseCommonDataHandler.getUseCaseDetailsModel().getChildGroupSetMap().get(grp.getID());
      if (null != childGrpIds) {
        for (Long grpId : childGrpIds) {
          UseCaseGroup ucg = useCaseCommonDataHandler.getUseCaseDetailsModel().getUseCaseGroupMap().get(grpId);
          UseCaseGroup parentucg =
              useCaseCommonDataHandler.getUseCaseDetailsModel().getUseCaseGroupMap().get(grp.getID());
          UseCaseGroupCommonBO parentUcgCommonBO =
              new UseCaseGroupCommonBO(parentucg, useCaseCommonDataHandler.getUseCaseDetailsModel(), null);
          UseCaseGroupCommonBO ucBo =
              new UseCaseGroupCommonBO(ucg, useCaseCommonDataHandler.getUseCaseDetailsModel(), parentUcgCommonBO);
          if (!ucBo.isDeleted()) {
            childSet.add(ucBo);
          }
        }
      }
      Set<Long> childUCIds = useCaseCommonDataHandler.getUseCaseDetailsModel().getChildUsecaseSetMap().get(grp.getID());
      if (null != childUCIds) {
        for (Long ucId : childUCIds) {
          UseCase uc = useCaseCommonDataHandler.getUseCaseDetailsModel().getUsecaseMap().get(ucId);
          UseCaseGroup ucg =
              useCaseCommonDataHandler.getUseCaseDetailsModel().getUseCaseGroupMap().get(uc.getGroupId());
          UseCaseGroupCommonBO ucgCommonBO =
              new UseCaseGroupCommonBO(ucg, useCaseCommonDataHandler.getUseCaseDetailsModel(), null);
          UsecaseCommonBO ucBo = new UsecaseCommonBO(uc, ucgCommonBO);
          if (!ucBo.isDeleted()) {
            childSet.add(ucBo);
          }
        }
      }
    }
    else if (getUseCaseItem() instanceof UsecaseCommonBO) {
      UsecaseCommonBO uc = (UsecaseCommonBO) getUseCaseItem();

      Set<Long> childSectionIds = useCaseCommonDataHandler.getUseCaseDetailsModel().getUsecaseDetailsModelMap()
          .get(uc.getID()).getFirstLevelUCSIDSet();
      if (null != childSectionIds) {
        for (Long secId : childSectionIds) {
          UseCaseSection ucs = useCaseCommonDataHandler.getUseCaseDetailsModel().getUsecaseDetailsModelMap()
              .get(uc.getID()).getUcSectionMap().get(secId);
          UseCase usc = useCaseCommonDataHandler.getUseCaseDetailsModel().getUsecaseMap().get(uc.getID());
          UseCaseGroup ucg =
              useCaseCommonDataHandler.getUseCaseDetailsModel().getUseCaseGroupMap().get(uc.getParent().getID());
          UseCaseGroupCommonBO ucgCommonBO =
              new UseCaseGroupCommonBO(ucg, useCaseCommonDataHandler.getUseCaseDetailsModel(), null);

          UsecaseSectionCommonBO ucBo = new UsecaseSectionCommonBO(ucs,
              useCaseCommonDataHandler.getUseCaseDetailsModel().getUsecaseDetailsModelMap().get(uc.getID()),
              new UsecaseCommonBO(usc, ucgCommonBO));
          if (!ucBo.isDeleted()) {
            childSet.add(ucBo);
          }
        }
      }
    }
    else if (getUseCaseItem() instanceof UsecaseSectionCommonBO) {
      UsecaseSectionCommonBO ucSectionBo = (UsecaseSectionCommonBO) getUseCaseItem();

      UsecaseModel usecaseModel = useCaseCommonDataHandler.getUseCaseDetailsModel().getUsecaseDetailsModelMap()
          .get(ucSectionBo.getUseCaseSection().getUseCaseId());
      Set<Long> childSectionIds = usecaseModel.getChildSectionsMap().get(ucSectionBo.getID());
      for (Long secId : childSectionIds) {

        Map<Long, UseCaseSection> ucSectionMap = usecaseModel.getUcSectionMap();
        if (CommonUtils.isNotEmpty(ucSectionMap)) {
          UseCaseSection ucs = ucSectionMap.get(secId);
          UseCase usc = useCaseCommonDataHandler.getUseCaseDetailsModel().getUsecaseMap()
              .get(ucSectionBo.getUseCaseSection().getUseCaseId());
          UseCaseGroup ucg =
              useCaseCommonDataHandler.getUseCaseDetailsModel().getUseCaseGroupMap().get(usc.getGroupId());
          UseCaseGroupCommonBO ucgCommonBO =
              new UseCaseGroupCommonBO(ucg, useCaseCommonDataHandler.getUseCaseDetailsModel(), null);

          UsecaseSectionCommonBO ucBo =
              new UsecaseSectionCommonBO(ucs, usecaseModel, new UsecaseCommonBO(usc, ucgCommonBO));
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
  public SortedSet<FavUseCaseItemNodeCommon> getChildFavNodes() {

    if (this.favUcItem == null) {
      if (CommonUtils.isNotNull(this.childNodeSet)) {
        SortedSet<FavUseCaseItemNodeCommon> returnSet = new TreeSet<FavUseCaseItemNodeCommon>(this.childNodeSet);
        for (FavUseCaseItemNodeCommon node : this.childNodeSet) {
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
      if (getUseCaseItem() instanceof UseCaseGroupCommonBO) {
        UseCaseGroupCommonBO grpBO = (UseCaseGroupCommonBO) getUseCaseItem();
        return isUcGroupMapped(attr, grpBO);
      }
      else if (getUseCaseItem() instanceof UsecaseCommonBO) {
        UsecaseCommonBO ucBO = (UsecaseCommonBO) getUseCaseItem();
        ucBO.setUsecaseEditorModel(this.useCaseDataHandler.getUseCaseDetailsModel().getUsecaseDetailsModelMap()
            .get(ucBO.getUseCase().getId()));
        return isUCMapped(attr, ucBO);
      }
      else if (getUseCaseItem() instanceof UsecaseSectionCommonBO) {
        UsecaseSectionCommonBO ucBO = (UsecaseSectionCommonBO) getUseCaseItem();
        return isUCSMapped(attr, ucBO);
      }
    }
    if (CommonUtils.isNotNull(getChildFavNodes())) {
      for (FavUseCaseItemNodeCommon favNode : getChildFavNodes()) {
        if (favNode.isMapped(attr)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean isUcGroupMapped(final Attribute attr, final UseCaseGroupCommonBO grp) {

    // Check whether child groups are mapped
    for (UseCaseGroupCommonBO childUcg : grp.getChildGroups(false)) {
      if (isUcGroupMapped(attr, childUcg)) {
        return true;
      }
    }

    // Check whether use cases are mapped
    for (UsecaseCommonBO ucase : grp.getUseCases(false)) {
      ucase.setUsecaseEditorModel(
          this.useCaseDataHandler.getUseCaseDetailsModel().getUsecaseDetailsModelMap().get(ucase.getUseCase().getId()));
      if (isUCMapped(attr, ucase)) {
        return true;
      }
    }

    // if no mapping done, then return false
    return false;

  }


  private boolean isUCMapped(final Attribute attr, final UsecaseCommonBO uc) {
    if (uc.canMapAttributes()) {
      Set<Long> mappedAttrIds = this.useCaseDataHandler.getUseCaseDetailsModel().getUsecaseDetailsModelMap()
          .get(uc.getId()).getUcItemAttrMap().get(uc.getId());
      if (!CommonUtils.isNullOrEmpty(mappedAttrIds) && mappedAttrIds.contains(attr.getId())) {
        return true;
      }
    }
    else {
      for (UsecaseSectionCommonBO ucSection : uc.getUseCaseSections(false)) {
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
  public boolean isUCSMapped(final Attribute attr, final UsecaseSectionCommonBO ucs) {
    if (ucs.canMapAttributes()) {
      Set<Long> mappedAttrIds = this.useCaseDataHandler.getUseCaseDetailsModel().getUsecaseDetailsModelMap()
          .get(ucs.getUseCaseSection().getUseCaseId()).getUcItemAttrMap().get(ucs.getId());
      if (!CommonUtils.isNullOrEmpty(mappedAttrIds) && mappedAttrIds.contains(attr.getId())) {
        return true;
      }
    }
    else {
      for (UsecaseSectionCommonBO ucsChild : ucs.getChildSections(false)) {
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
   * @param useCaseCommonDataHandler UseCaseDataHandler
   * @return FavUseCaseItemNode
   */
  public static FavUseCaseItemNodeCommon getFavUcNode(final FavUseCaseItemCommon favUc, final Long pidCardId,
      final UseCaseCommonDataHandler useCaseCommonDataHandler) {
    final Long nodeID = favUc.getUseCaseItem(useCaseCommonDataHandler).getID();
    FavUseCaseItemNodeCommon retNode = useCaseCommonDataHandler.getPidcFavUcNode(pidCardId, nodeID);
    if (retNode == null) {
      retNode = new FavUseCaseItemNodeCommon(nodeID, favUc.getUseCaseItem(useCaseCommonDataHandler), pidCardId, favUc,
          getParentNode(pidCardId, favUc.getUseCaseItem(useCaseCommonDataHandler), useCaseCommonDataHandler),
          useCaseCommonDataHandler);
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
  private static FavUseCaseItemNodeCommon getParentNode(final Long pidcId,
      final IUsecaseItemCommonBO abstractUseCaseItem, final UseCaseCommonDataHandler useCaseDataHandler) {

    IUsecaseItemCommonBO parentUcItem = getParent(abstractUseCaseItem);

    if (parentUcItem == null) {
      // No parent Nodes
      return null;
    }
    Long parentNodeID = parentUcItem.getID();

    FavUseCaseItemNodeCommon retNode = useCaseDataHandler.getPidcFavUcNode(pidcId, parentNodeID);
    if (retNode == null) {
      // Create node
      FavUseCaseItemNodeCommon myParent = getParentNode(pidcId, parentUcItem, useCaseDataHandler);
      retNode = new FavUseCaseItemNodeCommon(parentNodeID, parentUcItem, pidcId, null, myParent, useCaseDataHandler);
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
  public static FavUseCaseItemNodeCommon getFavUcNode(final FavUseCaseItemCommon favUc, final User user,
      final UseCaseCommonDataHandler useCaseDataHandler) {
    if (null != favUc.getUseCaseItem(useCaseDataHandler)) {
      final Long nodeID = favUc.getUseCaseItem(useCaseDataHandler).getID();
      FavUseCaseItemNodeCommon retNode = useCaseDataHandler.getUserFavUcNode(nodeID);
      if (retNode == null) {
        retNode = new FavUseCaseItemNodeCommon(nodeID, favUc.getUseCaseItem(useCaseDataHandler), user, favUc,
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
  private static FavUseCaseItemNodeCommon getParentNode(final User user, final IUsecaseItemCommonBO abstractUseCaseItem,
      final UseCaseCommonDataHandler useCaseDataHandler) {
    IUsecaseItemCommonBO parentUcItem = getParent(abstractUseCaseItem);

    if (parentUcItem == null) {
      // No parent Nodes
      return null;
    }
    Long parentNodeID = parentUcItem.getID();

    FavUseCaseItemNodeCommon retNode = useCaseDataHandler.getUserFavUcNode(parentNodeID);
    if (retNode == null) {
      // Create node
      FavUseCaseItemNodeCommon myParent = getParentNode(user, parentUcItem, useCaseDataHandler);
      retNode = new FavUseCaseItemNodeCommon(parentNodeID, parentUcItem, user, null, myParent, useCaseDataHandler);
    }

    return retNode;
  }


  /**
   * @param abstractUseCaseItem
   * @param useCaseDataHandler
   * @return
   */
  private static IUsecaseItemCommonBO getParent(final IUsecaseItemCommonBO abstractUseCaseItem) {
    IUsecaseItemCommonBO parentUcItem = null;
    if (abstractUseCaseItem instanceof UseCaseGroupCommonBO) {
      parentUcItem = ((UseCaseGroupCommonBO) abstractUseCaseItem).getParent();

    }
    else if (abstractUseCaseItem instanceof UsecaseCommonBO) {
      parentUcItem = ((UsecaseCommonBO) abstractUseCaseItem).getParent();

    }
    else if (abstractUseCaseItem instanceof UsecaseSectionCommonBO) {
      if (null != ((UsecaseSectionCommonBO) abstractUseCaseItem).getParent()) {
        parentUcItem = ((UsecaseSectionCommonBO) abstractUseCaseItem).getParent();
      }


    }
    return parentUcItem;
  }

  /**
   * ICDM-1040
   *
   * @return parent nodes of this node in the hierarchy
   */
  public Map<Long, FavUseCaseItemNodeCommon> getParentNodes() {
    Map<Long, FavUseCaseItemNodeCommon> parNodeMap = new ConcurrentHashMap<Long, FavUseCaseItemNodeCommon>();
    FavUseCaseItemNodeCommon node = getParentNode();
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
  public FavUseCaseItemNodeCommon getTopNode() {
    FavUseCaseItemNodeCommon parNode = getParentNode();
    FavUseCaseItemNodeCommon topNode = parNode;
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
  private FavUseCaseItemNodeCommon getParentNode() {
    return this.parent;
  }

  /**
   *
   */

  public int compareTo(final FavUseCaseItemNodeCommon arg0) {

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
  private void addThisObjToProjCache(final UseCaseCommonDataHandler useCaseDataHandler) {
    useCaseDataHandler.getPidcFavUcNodes(this.pidCardId).put(getID(), this);
  }

  /**
   * Adds this node to the data cache
   *
   * @param useCaseDataHandler
   */
  private void addThisObjToUserCache(final UseCaseCommonDataHandler useCaseDataHandler) {
    useCaseDataHandler.getCurrentFavUcNodes().put(getID(), this);
  }

  /**
   * @param isVisible visible
   */
  public void setVisible(final boolean isVisible) {
    this.visible = isVisible;
    FavUseCaseItemNodeCommon parentNode = getParentNode();
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
  public void setUcItem(final FavUseCaseItemCommon favUcItem2) {
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

}
