package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IDialog Interface
 */
@IID("{315C31B6-8654-4F3D-9F7B-F3D03E31CCB1}")
public interface IDialog extends Com4jObject {
    /**
     * Show the dialog as a modal dialog.
     */
    @VTID(7)
    int showDialog();

    /**
     * Set the parent window
     */
    @VTID(8)
    void setParent(
        int parentWindowHandle);

}
