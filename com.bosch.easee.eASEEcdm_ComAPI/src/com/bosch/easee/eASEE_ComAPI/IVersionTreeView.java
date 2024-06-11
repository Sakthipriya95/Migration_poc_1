package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IVersionTreeView Interface
 */
@IID("{4A5B3455-348A-46CF-9D63-B7D0C937E438}")
public interface IVersionTreeView extends com.bosch.easee.eASEE_ComAPI.IWindow {
    /**
     * method GetCursorFromPath
     */
    @VTID(30)
    com.bosch.easee.eASEE_ComAPI.ITreeListCursor getCursorFromPath(
        java.lang.String pathToObject);

    /**
     * SelectedItemCursor
     */
    @VTID(31)
    com.bosch.easee.eASEE_ComAPI.ITreeListCursor getSelectedItemCursor();

}
