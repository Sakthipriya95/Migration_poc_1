package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IDetailsView Interface
 */
@IID("{E1671F1D-177C-4337-B526-CAF4BCBFD218}")
public interface IDetailsView extends Com4jObject {
    /**
     * property Caption
     */
    @VTID(7)
    java.lang.String caption();

    /**
     * property TabIndex
     */
    @VTID(8)
    int tabOrder();

    /**
     * property IsEnabled
     */
    @VTID(9)
    boolean isEnabled();

    /**
     * method OnSelectionChanging
     */
    @VTID(10)
    boolean onSelectionChanging();

    /**
     * method OnSelectionChanged
     */
    @VTID(11)
    void onSelectionChanged(
        int versionNumber,
        java.lang.String context);

    /**
     * method OnClosing
     */
    @VTID(12)
    boolean onClosing();

    /**
     * method OnClosed
     */
    @VTID(13)
    void onClosed();

    /**
     * method IsModified
     */
    @VTID(14)
    boolean isModified();

    /**
     * method OnSave
     */
    @VTID(15)
    boolean onSave();

}
