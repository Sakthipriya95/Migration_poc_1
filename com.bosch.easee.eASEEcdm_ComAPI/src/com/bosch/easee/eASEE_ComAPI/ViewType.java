package com.bosch.easee.eASEE_ComAPI;

import com4j.*;
public enum ViewType implements ComEnum {vtUnknown(-1),
    vtAction(0),
    vtPrefList(1),
    vtNewList(2),
    vtGenericViewBar(3),
    vtGenericViewMdi(4),
    vtScratchList(6),
    vtGroupEdit(7),
    vtUsedIn(8),
    vtInheritTree(9),
    vtProperties(11),
    vtSearchVersion(12),
    vtSearchGroup(13),
    vtContainer(14),
    vtEqualGroups(15),
    vtCompareObjects(17),
    vtSearchFilterContextGroup(18),
    vtSearchFilterContextVersion(19);

    private final int value;

    ViewType(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
