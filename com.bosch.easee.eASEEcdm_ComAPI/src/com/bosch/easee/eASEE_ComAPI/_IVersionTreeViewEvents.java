package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * _IVersionTreeViewEvents Interface
 */
@IID("{6BDC9ECF-36ED-4E05-B9F3-D261A2DD1D75}")
public interface _IVersionTreeViewEvents extends Com4jObject {
    /**
     * method SelectionChanged
     */
    @VTID(7)
    void selectionChanged(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject pDispObjectOld,
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject pDispObjectNew);

    /**
     * method TreeCursorSelectionChanged
     */
    @VTID(8)
    void treeCursorSelectionChanged(
        com.bosch.easee.eASEE_ComAPI.ITreeListCursor pTreeListCursor);

    /**
     * method TreeUpdate
     */
    @VTID(9)
    void treeUpdate();

    /**
     * method TreeNodeClicked
     */
    @VTID(10)
    void treeNodeClicked(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject pIObjectTarget,
        boolean leftMouseButton);

    /**
     * method TreeCursorExpanding
     */
    @VTID(11)
    void treeCursorExpanding(
        com.bosch.easee.eASEE_ComAPI.ITreeListCursor pTreeListCursor,
        Holder<Boolean> expandNode);

    /**
     * method TreeCursorExpanded
     */
    @VTID(12)
    void treeCursorExpanded(
        com.bosch.easee.eASEE_ComAPI.ITreeListCursor pTreeListCursor);

    /**
     * method TreeCursorCollapsing
     */
    @VTID(13)
    void treeCursorCollapsing(
        com.bosch.easee.eASEE_ComAPI.ITreeListCursor pTreeListCursor,
        Holder<Boolean> collapseNode);

    /**
     * method TreeCursorCollapsed
     */
    @VTID(14)
    void treeCursorCollapsed(
        com.bosch.easee.eASEE_ComAPI.ITreeListCursor pTreeListCursor);

    /**
     * method TreeCursorSelectionChanging
     */
    @VTID(15)
    void treeCursorSelectionChanging(
        com.bosch.easee.eASEE_ComAPI.ITreeListCursor pTreeListCursorOld,
        com.bosch.easee.eASEE_ComAPI.ITreeListCursor pTreeListCursorNew,
        Holder<Boolean> change);

}
