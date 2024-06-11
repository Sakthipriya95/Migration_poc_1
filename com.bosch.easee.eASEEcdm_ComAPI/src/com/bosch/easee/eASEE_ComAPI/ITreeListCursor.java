package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * ITreeListCursor Interface
 */
@IID("{FE444E67-1C4A-4517-A30D-6E5A2961F657}")
public interface ITreeListCursor extends Com4jObject {
    /**
     * method GetRoot
     */
    @VTID(7)
    com.bosch.easee.eASEE_ComAPI.ITreeListCursor getRoot();

    /**
     * method GetSelected
     */
    @VTID(8)
    com.bosch.easee.eASEE_ComAPI.ITreeListCursor getSelected();

    /**
     * method GetNext
     */
    @VTID(9)
    com.bosch.easee.eASEE_ComAPI.ITreeListCursor getNext();

    /**
     * method GetParent
     */
    @VTID(10)
    com.bosch.easee.eASEE_ComAPI.ITreeListCursor getParent();

    /**
     * method GetChild
     */
    @VTID(11)
    com.bosch.easee.eASEE_ComAPI.ITreeListCursor getChild();

    /**
     * method GetNextSibling
     */
    @VTID(12)
    com.bosch.easee.eASEE_ComAPI.ITreeListCursor getNextSibling();

    /**
     * method GetPrevSibling
     */
    @VTID(13)
    com.bosch.easee.eASEE_ComAPI.ITreeListCursor getPrevSibling();

    /**
     * method GetClientObject
     */
    @VTID(14)
    com.bosch.easee.eASEE_ComAPI.IClientObj2 getClientObject();

    /**
     * method HasChildren
     */
    @VTID(15)
    boolean hasChildren();

    /**
     * method Expand
     */
    @VTID(16)
    void expand();

    /**
     * method Collapse
     */
    @VTID(17)
    void collapse();

    /**
     * method Select
     */
    @VTID(18)
    void select();

    /**
     * property Text
     */
    @VTID(19)
    java.lang.String text();

    /**
     * property Text
     */
    @VTID(20)
    void text(
        java.lang.String pVal);

    /**
     * property Color
     */
    @VTID(21)
    void color(
        int rhs);

    /**
     * property Bold
     */
    @VTID(22)
    void bold(
        boolean rhs);

    /**
     * property Path
     */
    @VTID(23)
    java.lang.String path();

    /**
     * property IsSelected
     */
    @VTID(24)
    boolean isSelected();

    /**
     * method GetFirstSelectedItem
     */
    @VTID(25)
    com.bosch.easee.eASEE_ComAPI.ITreeListCursor getFirstSelectedItem();

    /**
     * method GetNextSelectedItem
     */
    @VTID(26)
    com.bosch.easee.eASEE_ComAPI.ITreeListCursor getNextSelectedItem();

    /**
     * method GetPrevSelectedItem
     */
    @VTID(27)
    com.bosch.easee.eASEE_ComAPI.ITreeListCursor getPrevSelectedItem();

}
