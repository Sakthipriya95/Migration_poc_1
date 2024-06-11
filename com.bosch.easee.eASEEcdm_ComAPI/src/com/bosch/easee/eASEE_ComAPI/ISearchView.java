package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * ISearchView Interface
 */
@IID("{6751099E-D114-4CE7-A83B-4767476B1077}")
public interface ISearchView extends com.bosch.easee.eASEE_ComAPI.IWindow {
    /**
     * property ActiveTab
     */
    @VTID(30)
    int activeTab();

    /**
     * property ActiveTab
     */
    @VTID(31)
    void activeTab(
        int pVal);

    /**
     * property Filter
     */
    @VTID(32)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject filter();

    /**
     * property Filter
     */
    @VTID(33)
    void filter(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject ppDispFilterObj);

    /**
     * property SearchDialogMode
     */
    @VTID(34)
    int searchDialogMode();

    /**
     * property SearchDialogMode
     */
    @VTID(35)
    void searchDialogMode(
        int pVal);

    /**
     * property SearchResult
     */
    @VTID(36)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject searchResult();

    /**
     * property InitDialogExpanded
     */
    @VTID(37)
    void initDialogExpanded(
        boolean rhs);

    /**
     * property SearchScope
     */
    @VTID(38)
    com.bosch.easee.eASEE_ComAPI.SearchScopeType searchScope();

    /**
     * property SearchScope
     */
    @VTID(39)
    void searchScope(
        com.bosch.easee.eASEE_ComAPI.SearchScopeType searchScope);

    /**
     * property SelectedGroupVersionNumber
     */
    @VTID(40)
    int selectedGroupVersionNumber();

    /**
     * property SelectedGroupVersionNumber
     */
    @VTID(41)
    void selectedGroupVersionNumber(
        int pVal);

}
