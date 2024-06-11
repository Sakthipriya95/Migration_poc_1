package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IWindow interface
 */
@IID("{C32C6907-393C-44EC-A315-6A1BD1EFB7D9}")
public interface IWindow extends Com4jObject {
    /**
     * method Show
     */
    @VTID(7)
    void show();

    /**
     * method Hide
     */
    @VTID(8)
    void hide();

    /**
     * method Reload
     */
    @VTID(9)
    void reload();

    /**
     * method Redraw
     */
    @VTID(10)
    void redraw();

    /**
     * method Destroy
     */
    @VTID(11)
    void destroy();

    /**
     * property Caption
     */
    @VTID(12)
    java.lang.String caption();

    /**
     * property Caption
     */
    @VTID(13)
    void caption(
        java.lang.String pVal);

    /**
     * property Visible
     */
    @VTID(14)
    boolean visible();

    /**
     * property Visible
     */
    @VTID(15)
    void visible(
        boolean pVal);

    /**
     * property SplitterVertical
     */
    @VTID(16)
    boolean splitterVertical();

    /**
     * property SplitterVertical
     */
    @VTID(17)
    void splitterVertical(
        boolean pVal);

    /**
     * property SplitterHorizontalLeft
     */
    @VTID(18)
    boolean splitterHorizontalLeft();

    /**
     * property SplitterHorizontalLeft
     */
    @VTID(19)
    void splitterHorizontalLeft(
        boolean pVal);

    /**
     * property SplitterHorizontalRight
     */
    @VTID(20)
    boolean splitterHorizontalRight();

    /**
     * property SplitterHorizontalRight
     */
    @VTID(21)
    void splitterHorizontalRight(
        boolean pVal);

    /**
     * method Maximize
     */
    @VTID(22)
    void maximize();

    /**
     * method Restore
     */
    @VTID(23)
    void restore();

    /**
     * property Maximized
     */
    @VTID(24)
    boolean maximized();

    /**
     * property Maximized
     */
    @VTID(25)
    void maximized(
        boolean pVal);

    /**
     * method Activate
     */
    @VTID(26)
    void activate();

    /**
     * property Active
     */
    @VTID(27)
    boolean active();

    /**
     * property Active
     */
    @VTID(28)
    void active(
        boolean pVal);

    /**
     * method ShowDialog
     */
    @VTID(29)
    int showDialog();

}
