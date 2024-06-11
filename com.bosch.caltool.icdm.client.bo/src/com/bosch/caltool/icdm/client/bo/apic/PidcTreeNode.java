/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bosch.caltool.datamodel.core.IBasicObject;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.a2l.PidcA2lTreeStructureModel;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcFavourite;
import com.bosch.caltool.icdm.model.apic.pidc.PidcQnaireInfo;
import com.bosch.caltool.icdm.model.apic.pidc.PidcReviewDetails;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionInfo;
import com.bosch.caltool.icdm.model.apic.pidc.SdomPVER;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;
import com.bosch.caltool.icdm.model.util.ModelUtil;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CDRReviewResultServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * A node in the PIDC hierarchy
 */
public class PidcTreeNode implements IBasicObject, Comparable<PidcTreeNode> {

  /**
   * Pidc tree node type
   */
  public enum PIDC_TREE_NODE_TYPE {

                                   /**
                                    * Level AttributeNode
                                    */
                                   LEVEL_ATTRIBUTE("Level Atribute Node"),
                                   /**
                                    * Pidc Node - active version
                                    */
                                   ACTIVE_PIDC_VERSION("Active Pidc Version Node"),
                                   /**
                                    * Pidc Node - Other Version
                                    */
                                   OTHER_PIDC_VERSION("Other Pidc Version Node"),
                                   /**
                                    * Sdom Pver Node
                                    */
                                   SDOM_PVER("Sdom Pver Node"),
                                   /**
                                    * Pidc A2l Node
                                    */
                                   PIDC_A2L("Pidc A2l Node"),
                                   /**
                                    * Review result Node
                                    */
                                   REV_RES_NODE("Review Result Node"),
                                   /**
                                    * Questionnaire variant Node
                                    */
                                   QNAIRE_VAR_NODE("Questionnaire Variant Node"),
                                   /**
                                    * Pidc A2l Variant Node
                                    */
                                   PIDC_A2L_VAR_NODE("Pidc A2l Variant Node"),
                                   /**
                                    * Questionnaire Response Node
                                    */
                                   QNAIRE_RESP_NODE("Questionnaire Response Node"),
                                   /**
                                    * variant node while expanding review results
                                    */
                                   CDR_VAR_NODE("Review Result Variant Node"),
                                   /**
                                    *
                                    */
                                   PIDC_VAR_NODE("Pidc Variant Node"),
                                   /**
                                   *
                                   */
                                   OTHER_VER_TITLE_NODE("Other Versions"),
                                   /**
                                   *
                                   */
                                   RVW_RES_TITLE_NODE_NEW("Review Results"),
                                   /**
                                   *
                                   */
                                   RVW_QNAIRE_TITLE_NODE("Review Questionnaires"),
                                   /**
                                   *
                                   */
                                   RVW_QNAIRE_RESPONSIBILITY_NODE("Qnaire Responsibility"),
                                   /**
                                    * PIDC A2l Qnaire Responsibility Node
                                    */
                                   PIDC_A2L_QNAIRE_RESP_NODE("PIDC A2L Qnaire Responsibility"),
                                   /**
                                    * Responsibility Node for Pidc A2l Structure Tree View
                                    */
                                   PIDC_A2L_RESPONSIBILITY_NODE("Pidc A2l Responsibility"),
                                   /**
                                   *
                                   */
                                   PIDC_A2L_WP_NODE("Pidc A2l Workpackage"),
                                   /**
                                   *
                                   */
                                   RVW_QNAIRE_WP_NODE("Qnaire Responsibility Workpackage"),
                                   /**
                                   *
                                   */
                                   RVW_WORKPACAKGES_TITLE_NODE("Work Packages"),
                                   /**
                                    *
                                    */
                                   RVW_RESPONSIBILITIES_TITLE_NODE("Responsibilities"),
                                   /**
                                   *
                                   */
                                   RVW_RESPONSIBILITY_NODE("Responsibility"),
                                   /**
                                   *
                                   */
                                   RVW_OTHER_RVW_SCOPES_TITLE_NODE("Other Scopes"),
                                   /**
                                   *
                                   */
                                   RVW_OTHER_RVW_SCOPES_NODE("Other Review Scopes"),
                                   /**
                                   *
                                   */
                                   RVW_RESP_WP_NODE("Responsibility Workpackage"),
                                   /**
                                   *
                                   */
                                   REV_RES_WP_GRP_NODE("Workpackage");

    private final String uiType;

    PIDC_TREE_NODE_TYPE(final String uiType) {
      this.uiType = uiType;
    }

    /**
     * @return UI type
     */
    public final String getUiType() {
      return this.uiType;
    }

    /**
     * Return the risk flag object for the given db type
     *
     * @param uiType ui literal of flag
     * @return the flag object
     */
    public static PIDC_TREE_NODE_TYPE getType(final String uiType) {
      for (PIDC_TREE_NODE_TYPE type : PIDC_TREE_NODE_TYPE.values()) {
        if (type.uiType.equals(uiType)) {
          return type;
        }
      }
      return null;
    }
  }

  /**
   * When object's are equal
   */
  private static final int OBJ_EQUAL_CHK_VAL = 0;

  /**
   * Node name
   */
  private String name;
  /**
   * Node attribute
   */
  private Attribute nodeAttr;
  /**
   * Node attribute value
   */
  private AttributeValue nodeAttrValue;
  /**
   * level in the hierarchy root node: level 0
   */
  private int level;
  /**
   * unique node id in the tree view
   */
  private String nodeId;
  /**
   * parent node id in the tree view
   */
  private String parentNodeId;
  /**
   * parent node in the tree view
   */
  private PidcTreeNode parentNode;
  /**
   * Pidc version info if the node is a pidc version node
   */
  private PidcVersionInfo pidcVerInfo;
  /**
   * Pidc version - other version nodes
   */
  private PidcVersion pidcVersion;
  /**
   * Pidc variant - for review
   */
  private PidcVariant pidcVariant;

  /**
   * Pidc - other version nodes
   */
  private Pidc pidc;
  /**
   * Sdom Pver node
   */
  private SdomPVER sdomPver;
  /**
   * Pidc A2L node
   */
  private PidcA2l pidcA2l;
  /**
   * Review result for review result node
   */
  private CDRReviewResult reviewResult;
  /**
   * Review result for review result node
   */
  private RvwVariant reviewVarResult;
  /**
   * Pidc favourite object in case of Favourites view part
   */
  private PidcFavourite pidcFavourite;
  /**
   * Questionnaire response id - applicable for qnaire response nodes only
   */
  private Long qnaireRespId;
  /**
   * Questionnaire response - applicable for qnaire response nodes only
   */
  private RvwQnaireResponse qnaireResp;

  private String rvwVarName;

  private PidcQnaireInfo qnaireInfo;

  private PidcA2lTreeStructureModel a2lStructureModel;

  /**
   * Node type
   */
  private PIDC_TREE_NODE_TYPE nodeType;
  /**
  *
  */
  private Map<String, Map<Long, PidcA2l>> sdomA2lMap = new HashMap<>();
  /**
  *
  */
  private Map<String, Map<Long, RvwQnaireResponse>> varQnaireRespMap = new HashMap<>();
  /**
  *
  */
  private Map<Long, RvwQnaireResponse> qnaireResponseMap = new HashMap<>();

  /**
  *
  */
  private Map<String, PidcTreeNode> childNodesMap = new HashMap<>();

  /**
  *
  */
  private final Map<String, List<PidcTreeNode>> pidcChildrenMap = new HashMap<>();
  /**
  *
  */
  private Map<String, PidcTreeNode> unDelchildNodesMap = new HashMap<>();

  private PidcReviewDetails pidcRvwDetails;

  /**
   * A2lWorkPackage
   */
  private A2lWorkPackage a2lWorkpackage;

  /**
   * A2lResponsibility
   */
  private A2lResponsibility a2lResponsibility;

  /**
   *
   */
  private int sizeOfSelectedNodes;


  /**
   * @return the a2lStructureModel
   */
  public PidcA2lTreeStructureModel getA2lStructureModel() {
    return this.a2lStructureModel;
  }


  /**
   * @param a2lStructureModel the a2lStructureModel to set
   */
  public void setA2lStructureModel(final PidcA2lTreeStructureModel a2lStructureModel) {
    this.a2lStructureModel = a2lStructureModel;
  }

  /**
   * @return the nodeId
   */
  public String getNodeId() {
    return this.nodeId;
  }

  /**
   * @param nodeId the nodeId to set
   */
  public void setNodeId(final String nodeId) {
    this.nodeId = nodeId;
  }

  /**
   * @return the nodeAttr
   */
  public Attribute getNodeAttr() {
    return this.nodeAttr;
  }

  /**
   * @param nodeAttr the nodeAttr to set
   */
  public void setNodeAttr(final Attribute nodeAttr) {
    this.nodeAttr = nodeAttr;
  }

  /**
   * @return the nodeAttrValue
   */
  public AttributeValue getNodeAttrValue() {
    return this.nodeAttrValue;
  }

  /**
   * @param nodeAttrValue the nodeAttrValue to set
   */
  public void setNodeAttrValue(final AttributeValue nodeAttrValue) {
    this.nodeAttrValue = nodeAttrValue;
  }

  /**
   * @return the level
   */
  public int getLevel() {
    return this.level;
  }

  /**
   * @param level the level to set
   */
  public void setLevel(final int level) {
    this.level = level;
  }

  /**
   * @return the name
   */
  @Override
  public String getName() {
    if ((this.level == ApicConstants.PIDC_ROOT_LEVEL) && this.nodeType.equals(PIDC_TREE_NODE_TYPE.LEVEL_ATTRIBUTE)) {
      return this.nodeAttr.getName();
    }
    return this.name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final PidcTreeNode pidcNode) {
    // condition to put general question on top of all the questionnaire response
    if (getNodeType().equals(pidcNode.getNodeType()) && checkForSortingQnaireResponseInPidcTree()) {
      if ((null != pidcNode.getName()) && (pidcNode.getName().startsWith(ApicConstants.GENERAL_QUESTIONS) ||
          pidcNode.getName().startsWith(ApicConstants.OBD_GENERAL_QUESTIONS))) {
        return 1;
      }
      if ((null != getName()) && (getName().startsWith(ApicConstants.GENERAL_QUESTIONS) ||
          getName().startsWith(ApicConstants.OBD_GENERAL_QUESTIONS))) {
        return -1;
      }
      return compareObjId(pidcNode);
    }
    if (getNodeType().equals(pidcNode.getNodeType())) {
      if (getName().equals(pidcNode.getName())) {
        return ModelUtil.compare(getNodeId(), pidcNode.getNodeId());
      }
      return ModelUtil.compare(getName(), pidcNode.getName());
    }
    if (getNodePosition(getNodeType()) < getNodePosition(pidcNode.getNodeType())) {
      return -1;
    }
    else if (getNodePosition(getNodeType()) > getNodePosition(pidcNode.getNodeType())) {
      return 1;
    }
    return ModelUtil.compare(getName(), pidcNode.getName());
  }


  /**
   * @param pidcNode
   * @return
   */
  private int compareObjId(final PidcTreeNode pidcNode) {
    int compareName = ModelUtil.compare(getName(), pidcNode.getName());
    // When object name is same compare using id
    if (compareName == OBJ_EQUAL_CHK_VAL) {
      return ModelUtil.compare(getQnaireResp().getId(), pidcNode.getQnaireResp().getId());
    }
    return compareName;
  }


  /**
   * @return
   */
  private boolean checkForSortingQnaireResponseInPidcTree() {
    return getNodeType().equals(PIDC_TREE_NODE_TYPE.QNAIRE_RESP_NODE) ||
        getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_QNAIRE_RESP_NODE);
  }

  /**
   * @param nodeType2
   * @return
   */
  private int getNodePosition(final PIDC_TREE_NODE_TYPE nodeType) {
    switch (nodeType) {
      case OTHER_VER_TITLE_NODE:
        return 1;
      case SDOM_PVER:
        return 2;
      case RVW_RES_TITLE_NODE_NEW:
        return 3;
      case RVW_QNAIRE_TITLE_NODE:
        return 4;
      default:
        return 0;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }
    return getNodeId().equals(((PidcTreeNode) obj).getNodeId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getNodeId());
  }


  /**
   * @param name the name to set
   */
  @Override
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * @return the parentNodeId
   */
  public String getParentNodeId() {
    return this.parentNodeId;
  }

  /**
   * @param parentNodeId the parentNodeId to set
   */
  public void setParentNodeId(final String parentNodeId) {
    this.parentNodeId = parentNodeId;
  }

  /**
   * @return the pidcVerInfo
   */
  public PidcVersionInfo getPidcVerInfo() {
    return this.pidcVerInfo;
  }

  /**
   * @param pidcVerInfo the pidcVerInfo to set
   */
  public void setPidcVerInfo(final PidcVersionInfo pidcVerInfo) {
    this.pidcVerInfo = pidcVerInfo;
  }

  /**
   * @return the pidcVersion
   */
  public PidcVersion getPidcVersion() {
    return this.pidcVersion;
  }

  /**
   * @param pidcVersion the pidcVersion to set
   */
  public void setPidcVersion(final PidcVersion pidcVersion) {
    this.pidcVersion = pidcVersion;
  }

  /**
   * @return the pidc
   */
  public Pidc getPidc() {
    return this.pidc;
  }

  /**
   * @param pidc the pidc to set
   */
  public void setPidc(final Pidc pidc) {
    this.pidc = pidc;
  }

  /**
   * @return the nodeType
   */
  public PIDC_TREE_NODE_TYPE getNodeType() {
    return this.nodeType;
  }

  /**
   * @param nodeType the nodeType to set
   */
  public void setNodeType(final PIDC_TREE_NODE_TYPE nodeType) {
    this.nodeType = nodeType;
  }

  /**
   * @return the sdomPver
   */
  public SdomPVER getSdomPver() {
    return this.sdomPver;
  }

  /**
   * @param sdomPver the sdomPver to set
   */
  public void setSdomPver(final SdomPVER sdomPver) {
    this.sdomPver = sdomPver;
  }

  /**
   * @return the pidcA2l
   */
  public PidcA2l getPidcA2l() {
    return this.pidcA2l;
  }

  /**
   * @param pidcA2l the pidcA2l to set
   */
  public void setPidcA2l(final PidcA2l pidcA2l) {
    this.pidcA2l = pidcA2l;
  }

  /**
   * @return the reviewResult
   */
  public CDRReviewResult getReviewResult() {
    return this.reviewResult;
  }

  /**
   * @param reviewResult the reviewResult to set
   */
  public void setReviewResult(final CDRReviewResult reviewResult) {
    this.reviewResult = reviewResult;
  }

  /**
   * @return the pidcVariantt
   */
  public PidcVariant getPidcVariant() {
    return this.pidcVariant;
  }

  /**
   * @param pidcVariantt the pidcVariantt to set
   */
  public void setPidcVariant(final PidcVariant pidcVariantt) {
    this.pidcVariant = pidcVariantt;
  }

  /**
   * @return the childNodesMap
   */
  public Map<String, PidcTreeNode> getChildNodesMap() {
    return this.childNodesMap;
  }

  /**
   * @param childNodesMap the childNodesMap to set
   */
  public void setChildNodesMap(final Map<String, PidcTreeNode> childNodesMap) {
    this.childNodesMap = childNodesMap;
  }

  /**
   * @return the parentNode
   */
  public PidcTreeNode getParentNode() {
    return this.parentNode;
  }

  /**
   * @param parentNode the parentNode to set
   */
  public void setParentNode(final PidcTreeNode parentNode) {
    this.parentNode = parentNode;
  }

  /**
   * @return the pidcFavourite
   */
  public PidcFavourite getPidcFavourite() {
    return this.pidcFavourite;
  }

  /**
   * @param pidcFavourite the pidcFavourite to set
   */
  public void setPidcFavourite(final PidcFavourite pidcFavourite) {
    this.pidcFavourite = pidcFavourite;
  }

  /**
   * @return the qnaireRespId
   */
  public Long getQnaireRespId() {
    return this.qnaireRespId;
  }

  /**
   * @param qnaireRespId the qnaireRespId to set
   */
  public void setQnaireRespId(final Long qnaireRespId) {
    this.qnaireRespId = qnaireRespId;
  }

  /**
   * @return the rvwVarName
   */
  public String getRvwVarName() {
    return this.rvwVarName;
  }

  /**
   * @param rvwVarName the rvwVarName to set
   */
  public void setRvwVarName(final String rvwVarName) {
    this.rvwVarName = rvwVarName;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return super.toString() + " [nodeType=" + this.nodeType + ", name=" + this.name + ", level=" + this.level +
        ", nodeId=" + this.nodeId + "]";
  }

  /**
   * @return the reviewVarResult
   */
  public RvwVariant getReviewVarResult() {
    return this.reviewVarResult;
  }

  /**
   * @param reviewVarResult the reviewVarResult to set
   */
  public void setReviewVarResult(final RvwVariant reviewVarResult) {
    this.reviewVarResult = reviewVarResult;
  }

  /**
   * @return the varQnaireRespMap
   */
  public Map<String, Map<Long, RvwQnaireResponse>> getVarQnaireRespMap() {
    return this.varQnaireRespMap;
  }

  /**
   * @param varQnaireRespMap the varQnaireRespMap to set
   */
  public void setVarQnaireRespMap(final Map<String, Map<Long, RvwQnaireResponse>> varQnaireRespMap) {
    this.varQnaireRespMap = varQnaireRespMap;
  }

  /**
   * @return the qnaireResponseMap
   */
  public Map<Long, RvwQnaireResponse> getQnaireResponseMap() {
    return this.qnaireResponseMap;
  }

  /**
   * @param qnaireResponseMap the qnaireResponseMap to set
   */
  public void setQnaireResponseMap(final Map<Long, RvwQnaireResponse> qnaireResponseMap) {
    this.qnaireResponseMap = qnaireResponseMap;
  }

  /**
   * @return the sdomA2lMap
   */
  public Map<String, Map<Long, PidcA2l>> getSdomA2lMap() {
    return this.sdomA2lMap;
  }

  /**
   * @param sdomA2lMap the sdomA2lMap to set
   */
  public void setSdomA2lMap(final Map<String, Map<Long, PidcA2l>> sdomA2lMap) {
    this.sdomA2lMap = sdomA2lMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return getName();
  }


  /**
   * @return the qnaireResp
   */
  public RvwQnaireResponse getQnaireResp() {
    return this.qnaireResp;
  }

  /**
   * @param qnaireResp the qnaireResp to set
   */
  public void setQnaireResp(final RvwQnaireResponse qnaireResp) {
    this.qnaireResp = qnaireResp;
  }

  /**
   * @return the qnaireInfo
   */
  public PidcQnaireInfo getQnaireInfo() {
    return this.qnaireInfo;
  }

  /**
   * @param qnaireInfo the qnaireInfo to set
   */
  public void setQnaireInfo(final PidcQnaireInfo qnaireInfo) {
    this.qnaireInfo = qnaireInfo;
  }

  /**
   * @return the unDelchildNodesMap
   */
  public Map<String, PidcTreeNode> getUnDelchildNodesMap() {
    return this.unDelchildNodesMap;
  }

  /**
   * @param unDelchildNodesMap the unDelchildNodesMap to set
   */
  public void setUnDelchildNodesMap(final Map<String, PidcTreeNode> unDelchildNodesMap) {
    this.unDelchildNodesMap = unDelchildNodesMap;
  }

  /**
   * @return the pidcChildrenMap
   */
  public Map<String, List<PidcTreeNode>> getPidcChildrenMap() {
    return this.pidcChildrenMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(final String description) {
    // no impl
  }

  /**
   * @return the pidcRvwDetails
   */
  public PidcReviewDetails getPidcRvwDetails() {
    if ((null == this.pidcRvwDetails) && (null != getPidcVersion()) &&
        PIDC_TREE_NODE_TYPE.RVW_RES_TITLE_NODE_NEW.equals(getNodeType())) {
      try {
        this.pidcRvwDetails = new CDRReviewResultServiceClient().getReviewResultInfo(getPidcVersion().getId());
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      }
    }
    return this.pidcRvwDetails;
  }

  /**
   * @param pidcRvwDetails the pidcRvwDetails to set
   */
  public void setPidcRvwDetails(final PidcReviewDetails pidcRvwDetails) {
    this.pidcRvwDetails = pidcRvwDetails;
  }

  /**
   * @return the a2lWorkpackage
   */
  public A2lWorkPackage getA2lWorkpackage() {
    return this.a2lWorkpackage;
  }

  /**
   * @param a2lWorkpackage the a2lWorkpackage to set
   */
  public void setA2lWorkpackage(final A2lWorkPackage a2lWorkpackage) {
    this.a2lWorkpackage = a2lWorkpackage;
  }

  /**
   * @return the a2lResponsibility
   */
  public A2lResponsibility getA2lResponsibility() {
    return this.a2lResponsibility;
  }

  /**
   * @param a2lResponsibility the a2lResponsibility to set
   */
  public void setA2lResponsibility(final A2lResponsibility a2lResponsibility) {
    this.a2lResponsibility = a2lResponsibility;
  }


  /**
   * @return the sizeOfSelectedNodes
   */
  public int getSizeOfSelectedNodes() {
    return this.sizeOfSelectedNodes;
  }


  /**
   * @param sizeOfSelectedNodes the sizeOfSelectedNodes to set
   */
  public void setSizeOfSelectedNodes(final int sizeOfSelectedNodes) {
    this.sizeOfSelectedNodes = sizeOfSelectedNodes;
  }

}