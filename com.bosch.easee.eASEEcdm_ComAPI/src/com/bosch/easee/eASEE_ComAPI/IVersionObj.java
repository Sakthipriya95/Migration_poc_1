package com.bosch.easee.eASEE_ComAPI;

import com4j.Holder;
import com4j.IID;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;


/**
 * IVersionObj Interface
 */
@IID("{BFD99318-95AE-47FB-9513-CB590A8F2DA6}")
public interface IVersionObj extends com.bosch.easee.eASEE_ComAPI.IClientObj {

  /**
   * method LoadDBData
   */
  @VTID(7)
  void loadDBData(int nVersNumber);

  /**
   * property Class
   */
  @VTID(8)
  java.lang.String _class();

  /**
   * property Number
   */
  @VTID(9)
  int number();

  /**
   * property Type
   */
  @VTID(10)
  java.lang.String type();

  /**
   * property VersionName
   */
  @VTID(11)
  java.lang.String versionName();

  /**
   * method GetParent
   */
  @VTID(12)
  void getParent(Holder<com.bosch.easee.eASEE_ComAPI.IVersionObj> pParentItem);

  /**
   * property State
   */
  @VTID(13)
  java.lang.String state();

  /**
   * property Name
   */
  @VTID(14)
  java.lang.String name();

  /**
   * property Variant
   */
  @VTID(15)
  java.lang.String variant();

  /**
   * property Revision
   */
  @VTID(16)
  int revision();

  /**
   * property Comment
   */
  @VTID(17)
  java.lang.String comment();

  /**
   * property LifeCycle
   */
  @VTID(18)
  java.lang.String lifeCycle();

  /**
   * property Repository
   */
  @VTID(19)
  java.lang.String repository();

  /**
   * property CreationUser
   */
  @VTID(20)
  java.lang.String creationUser();

  /**
   * property CreationDate
   */
  @VTID(21)
  java.util.Date creationDate();

  /**
   * property CreationDateString
   */
  @VTID(22)
  java.lang.String creationDateString();

  /**
   * property ModificationUser
   */
  @VTID(23)
  java.lang.String modificationUser();

  /**
   * property ModificationDate
   */
  @VTID(24)
  java.util.Date modificationDate();

  /**
   * property ModificationDateString
   */
  @VTID(25)
  java.lang.String modificationDateString();

  /**
   * property ConfigState
   */
  @VTID(26)
  com.bosch.easee.eASEE_ComAPI.VersionUseState configState();

  /**
   * property ConfigOrder
   */
  @VTID(27)
  int configOrder();

  /**
   * method GetChildren
   */
  @VTID(28)
  void getChildren(Holder<com.bosch.easee.eASEE_ComAPI.IVersionCol> pChildItems);

  /**
   * method GetParent2
   */
  @VTID(29)
  com.bosch.easee.eASEE_ComAPI.IVersionObj getParent2();

  /**
   * method GetChildren2
   */
  @VTID(30)
  com.bosch.easee.eASEE_ComAPI.IVersionCol getChildren2();

  /**
   * method GetElementAttribute
   */
  @VTID(31)
  java.lang.String getElementAttribute(java.lang.String name);

  /**
   * method SetElementAttribute
   */
  @VTID(32)
  void setElementAttribute(java.lang.String name, java.lang.String value);

  /**
   * method GetVersionAttribute
   */
  @VTID(33)
  java.lang.String getVersionAttribute(java.lang.String name);

  /**
   * method SetVersionAttribute
   */
  @VTID(34)
  void setVersionAttribute(java.lang.String name, java.lang.String value);

  /**
   * method GetConfigAttribute
   */
  @VTID(35)
  java.lang.String getConfigAttribute(java.lang.String name);

  /**
   * method SetConfigAttribute
   */
  @VTID(36)
  void setConfigAttribute(java.lang.String name, java.lang.String value);

  /**
   * method IsGroup
   */
  @VTID(37)
  boolean isGroup();

  /**
   * property TypeID
   */
  @VTID(38)
  java.lang.String typeID();

  /**
   * method CreateRelation
   */
  @VTID(39)
  void createRelation(java.lang.String relationName, com.bosch.easee.eASEE_ComAPI.IVersionObj pIVersionTarget);

  /**
   * property ClassID
   */
  @VTID(40)
  java.lang.String classID();

  /**
   * property Domain
   */
  @VTID(41)
  java.lang.String domain();

  /**
   * property IsCheckedOut
   */
  @VTID(42)
  boolean isCheckedOut();


  /**
   * method ChangeVersionName
   */
  @VTID(43)
  void changeVersionName(java.lang.String newVersionName);

  /**
   * method ChangeVariant
   */
  @VTID(44)
  void changeVariant(java.lang.String newVariant);

  /**
   * method ChangeType
   */
  @VTID(45)
  void changeType(java.lang.String newType);


  /**
   * property ElementName
   */
  @VTID(46)
  java.lang.String elementName();

  /**
   * property CheckoutPath
   */
  @VTID(47)
  java.lang.String checkoutPath();

  /**
   * property FileName
   */
  @VTID(48)
  java.lang.String fileName();

  /**
   * property HasPredecessor
   */
  @VTID(49)
  boolean hasPredecessor();

  /**
   * method GetPredecessor
   */
  @VTID(50)
  com.bosch.easee.eASEE_ComAPI.IVersionObj getPredecessor();

  /**
   * method GetPredecessorCollection
   */
  @VTID(51)
  com.bosch.easee.eASEE_ComAPI.IVersionCollection getPredecessorCollection();

  /**
   * property HasSuccessor
   */
  @VTID(52)
  boolean hasSuccessor();

  /**
   * method GetSuccessor
   */
  @VTID(53)
  com.bosch.easee.eASEE_ComAPI.IVersionObj getSuccessor();

  /**
   * method GetSuccessorCollection
   */
  @VTID(54)
  com.bosch.easee.eASEE_ComAPI.IVersionCollection getSuccessorCollection();

  /**
   * method GetContainedVersions
   */
  @VTID(55)
  com.bosch.easee.eASEE_ComAPI.IVersionCol getContainedVersions();

  /**
   * method RemoveRelation
   */
  @VTID(56)
  void removeRelation(java.lang.String relationName, com.bosch.easee.eASEE_ComAPI.IVersionObj pIVersionTarget);

  /**
   * property HasRelations
   */
  @VTID(57)
  boolean hasRelations();

  /**
   * method GetRelationCollection
   */
  @VTID(58)
  com.bosch.easee.eASEE_ComAPI.IRelationCollection getRelationCollection();

  /**
   * method GetCheckoutPathAndContext
   */
  @VTID(59)
  void getCheckoutPathAndContext(Holder<java.lang.String> pFullPath, Holder<java.lang.String> pPathContext);

  /**
   * property _CollectionId
   */
  @VTID(60)
  int _CollectionId();

  /**
   * property _CollectionId
   */
  @VTID(61)
  int _ObjectId();

  /**
   * property ConfigurationComment
   */
  @VTID(62)
  java.lang.String configurationComment();

  /**
   * method SetLifeCycleStateByName
   */
  @VTID(63)
  void setLifeCycleStateByName(java.lang.String lifeCycleStateName, java.lang.String stateComment);

  /**
   * method GetLifeCycleStates
   */
  @VTID(64)
  @ReturnValue(type = NativeType.VARIANT)
  java.lang.Object getLifeCycleStates();

  /**
   * property SystemState
   */
  @VTID(65)
  java.lang.String systemState();

  /**
   * method AddToCoreCache
   */
  @VTID(66)
  void addToCoreCache();

  /**
   * method GetVersionAttributeMulti
   */
  @VTID(67)
  @ReturnValue(type = NativeType.VARIANT)
  java.lang.Object getVersionAttributeMulti(java.lang.String name);

  /**
   * method GetElementAttributeMulti
   */
  @VTID(68)
  @ReturnValue(type = NativeType.VARIANT)
  java.lang.Object getElementAttributeMulti(java.lang.String name);

  /**
   * method GetMultiValueVersionAttribute
   */
  @VTID(69)
  com.bosch.easee.eASEE_ComAPI.IMultiValueAttribute getMultiValueVersionAttribute(java.lang.String name);

  /**
   * method GetMultiValueElementAttribute
   */
  @VTID(70)
  com.bosch.easee.eASEE_ComAPI.IMultiValueAttribute getMultiValueElementAttribute(java.lang.String name);

  /**
   * method SetMultiValueAttribute
   */
  @VTID(71)
  void setMultiValueAttribute(com.bosch.easee.eASEE_ComAPI.IMultiValueAttribute pValue);

  /**
   * property UsageContext
   */
  @VTID(72)
  java.lang.String usageContext();

  /**
   * property FolderId
   */
  @VTID(73)
  java.lang.String folderId();

  /**
   * property ClassDefID
   */
  @VTID(74)
  int classDefID();
}
