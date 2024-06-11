package com.bosch.easee.eASEE_ComAPI;

import com4j.*;


/**
 * Defines methods to create COM objects
 */
public abstract class ClassFactory {
    private ClassFactory() {
    } // instanciation is not allowed

    /**
     * ClientApi2 Class
     */
    public static com.bosch.easee.eASEE_ComAPI.IClientApi2 createClientApi2() {
        return COM4J.createInstance(com.bosch.easee.eASEE_ComAPI.IClientApi2.class,
            "{24EF5DCA-E477-4292-8622-E1EACA6ED348}");
    }

    /**
     * VersionObj Class
     */
    public static com.bosch.easee.eASEE_ComAPI.IVersionObj createVersionObj() {
        return COM4J.createInstance(com.bosch.easee.eASEE_ComAPI.IVersionObj.class,
            "{E7097E9C-89AF-47A7-9A86-FC5EE1B78337}");
    }

    /**
     * VersionCol Class
     */
    public static com.bosch.easee.eASEE_ComAPI.IVersionCol createVersionCol() {
        return COM4J.createInstance(com.bosch.easee.eASEE_ComAPI.IVersionCol.class,
            "{6014FF18-CE6F-452F-89D1-147D63621711}");
    }

    /**
     * FilterObj Class
     */
    public static com.bosch.easee.eASEE_ComAPI.IFilterObj createFilterObj() {
        return COM4J.createInstance(com.bosch.easee.eASEE_ComAPI.IFilterObj.class,
            "{32003844-0C9B-4499-B0B8-A7A6D149A79D}");
    }

    /**
     * Application Class
     */
    public static com.bosch.easee.eASEE_ComAPI.IApplication createApplication() {
        return COM4J.createInstance(com.bosch.easee.eASEE_ComAPI.IApplication.class,
            "{E54ECC12-7BAE-451F-AD23-82474EFE6743}");
    }

    /**
     * ClientObj Class
     */
    public static com.bosch.easee.eASEE_ComAPI.IClientObj createClientObj() {
        return COM4J.createInstance(com.bosch.easee.eASEE_ComAPI.IClientObj.class,
            "{A39B8D0B-E6F9-4C8F-8646-E6B5F0F96C92}");
    }

    /**
     * ClientObjCol Class
     */
    public static com.bosch.easee.eASEE_ComAPI.IClientObjCol createClientObjCol() {
        return COM4J.createInstance(com.bosch.easee.eASEE_ComAPI.IClientObjCol.class,
            "{DCAF73C9-7072-4295-9158-69BEB226DFA9}");
    }

    /**
     * PluginActions Class
     */
    public static com.bosch.easee.eASEE_ComAPI.IPluginActions createPluginActions() {
        return COM4J.createInstance(com.bosch.easee.eASEE_ComAPI.IPluginActions.class,
            "{2616F427-465F-4063-B542-E89D2EFC2AE5}");
    }

    /**
     * PluginAction Class
     */
    public static com.bosch.easee.eASEE_ComAPI.IPluginAction createPluginAction() {
        return COM4J.createInstance(com.bosch.easee.eASEE_ComAPI.IPluginAction.class,
            "{DB2698E9-494F-4A80-A0C1-59C8A4F3BBBF}");
    }

    /**
     * PluginMenus Class
     */
    public static com.bosch.easee.eASEE_ComAPI.IPluginMenus createPluginMenus() {
        return COM4J.createInstance(com.bosch.easee.eASEE_ComAPI.IPluginMenus.class,
            "{2799173B-A59A-4D17-8EB8-FDD9CB6CA298}");
    }

    /**
     * PluginMenu Class
     */
    public static com.bosch.easee.eASEE_ComAPI.IPluginMenu createPluginMenu() {
        return COM4J.createInstance(com.bosch.easee.eASEE_ComAPI.IPluginMenu.class,
            "{6B14AFD4-FDEC-41A2-A496-420A31599B1E}");
    }

    /**
     * PluginMenuItem Class
     */
    public static com.bosch.easee.eASEE_ComAPI.IPluginMenuItem createPluginMenuItem() {
        return COM4J.createInstance(com.bosch.easee.eASEE_ComAPI.IPluginMenuItem.class,
            "{2165D2FC-037B-44B6-B316-5E461D233E53}");
    }

    /**
     * GCView Class
     */
    public static com.bosch.easee.eASEE_ComAPI.IGCView createGCView() {
        return COM4J.createInstance(com.bosch.easee.eASEE_ComAPI.IGCView.class,
            "{036832EE-BB52-493D-8399-88026981AEBB}");
    }

    /**
     * GTView Class
     */
    public static com.bosch.easee.eASEE_ComAPI.IGTView createGTView() {
        return COM4J.createInstance(com.bosch.easee.eASEE_ComAPI.IGTView.class,
            "{D604D1A4-C126-44A8-87EF-86A7FE9191BE}");
    }

    /**
     * ContentView Class
     */
    public static com.bosch.easee.eASEE_ComAPI.IContentView createContentView() {
        return COM4J.createInstance(com.bosch.easee.eASEE_ComAPI.IContentView.class,
            "{7AB01ACC-442B-4C11-AEFB-8AF79E1D4595}");
    }

    /**
     * GroupEditView Class
     */
    public static com.bosch.easee.eASEE_ComAPI.IGroupEditView createGroupEditView() {
        return COM4J.createInstance(com.bosch.easee.eASEE_ComAPI.IGroupEditView.class,
            "{47CA5B42-0B27-4534-90A6-4501E642B4D0}");
    }

    /**
     * TreeListCursor Class
     */
    public static com.bosch.easee.eASEE_ComAPI.ITreeListCursor createTreeListCursor() {
        return COM4J.createInstance(com.bosch.easee.eASEE_ComAPI.ITreeListCursor.class,
            "{E6BA5B2D-EC89-4B6F-8E40-F910DDD4AA8C}");
    }

    /**
     * SearchDialog Class
     */
    public static com.bosch.easee.eASEE_ComAPI.ISearchView createSearchDialog() {
        return COM4J.createInstance(com.bosch.easee.eASEE_ComAPI.ISearchView.class,
            "{432CAECE-138D-4915-97BA-99819C73AB86}");
    }

    /**
     * DeleteObjectDialog Class
     */
    public static com.bosch.easee.eASEE_ComAPI.IDeleteObjectDialog createDeleteObjectDialog() {
        return COM4J.createInstance(com.bosch.easee.eASEE_ComAPI.IDeleteObjectDialog.class,
            "{62D8C5EC-9CEC-4825-A7A2-3ED86423C9CE}");
    }

    /**
     * VersionCollection Class
     */
    public static com.bosch.easee.eASEE_ComAPI.IVersionCollection createVersionCollection() {
        return COM4J.createInstance(com.bosch.easee.eASEE_ComAPI.IVersionCollection.class,
            "{F88F88CD-D180-4B7E-B419-9DA9F0C89F84}");
    }

    /**
     * FolderObj Class
     */
    public static com.bosch.easee.eASEE_ComAPI.IFolderObj createFolderObj() {
        return COM4J.createInstance(com.bosch.easee.eASEE_ComAPI.IFolderObj.class,
            "{169072F7-0DE4-410E-8C2F-EA592BB76033}");
    }

    /**
     * FolderCollection Class
     */
    public static com.bosch.easee.eASEE_ComAPI.IFolderCollection createFolderCollection() {
        return COM4J.createInstance(com.bosch.easee.eASEE_ComAPI.IFolderCollection.class,
            "{540A0A21-D6F2-4421-BBD5-F5FCE2A50C42}");
    }

    /**
     * ViewDetails Class
     */
    public static com.bosch.easee.eASEE_ComAPI.IViewDetails createViewDetails() {
        return COM4J.createInstance(com.bosch.easee.eASEE_ComAPI.IViewDetails.class,
            "{01ABBA30-D6AE-4FD2-AD68-22FA5CB7D069}");
    }

    /**
     * GCVViewDetails Class
     */
    public static com.bosch.easee.eASEE_ComAPI.IGCVViewDetails createGCVViewDetails() {
        return COM4J.createInstance(com.bosch.easee.eASEE_ComAPI.IGCVViewDetails.class,
            "{AC1E835B-D6D3-410F-B32D-0C1E14F3EF2F}");
    }

    /**
     * ViewTypeInfo Class
     */
    public static com.bosch.easee.eASEE_ComAPI.IViewTypeInfo createViewTypeInfo() {
        return COM4J.createInstance(com.bosch.easee.eASEE_ComAPI.IViewTypeInfo.class,
            "{B5633F27-EBE6-46DE-96A5-15CC4CA88D19}");
    }

    /**
     * RelationObj Class
     */
    public static com.bosch.easee.eASEE_ComAPI.IRelationObj createRelationObj() {
        return COM4J.createInstance(com.bosch.easee.eASEE_ComAPI.IRelationObj.class,
            "{DF6136FB-8955-471F-A932-ACF243F79084}");
    }

    /**
     * RelationCollection Class
     */
    public static com.bosch.easee.eASEE_ComAPI.IRelationCollection createRelationCollection() {
        return COM4J.createInstance(com.bosch.easee.eASEE_ComAPI.IRelationCollection.class,
            "{B3746C8E-2D18-4E42-AF6A-A1EA364F29E7}");
    }

    /**
     * RelationMgr Class
     */
    public static com.bosch.easee.eASEE_ComAPI.IRelationMgr createRelationMgr() {
        return COM4J.createInstance(com.bosch.easee.eASEE_ComAPI.IRelationMgr.class,
            "{A5BFBEE8-FEB7-4BA2-BE17-BE25F80633DE}");
    }

    /**
     * MultiValueAttribute Class
     */
    public static com.bosch.easee.eASEE_ComAPI.IMultiValueAttribute createMultiValueAttribute() {
        return COM4J.createInstance(com.bosch.easee.eASEE_ComAPI.IMultiValueAttribute.class,
            "{CCE73FCF-487F-4563-AE1C-60C61839B9C2}");
    }

    /**
     * AttributesView Class
     */
    public static com.bosch.easee.eASEE_ComAPI.IAttributesView createAttributesView() {
        return COM4J.createInstance(com.bosch.easee.eASEE_ComAPI.IAttributesView.class,
            "{9FCAE478-27EE-4D6F-84AD-DBAF5C06FB01}");
    }
}
