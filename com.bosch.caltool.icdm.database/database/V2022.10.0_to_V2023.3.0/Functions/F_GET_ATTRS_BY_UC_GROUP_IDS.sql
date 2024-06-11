--------------------------------------------------------
-- Copyright (c) Robert Bosch GmbH. All rights reserved.
--
--
--  DDL for Function F_GET_ATTRS_BY_UC_GROUP_IDS
--------------------------------------------------------

-- This function does not have a commit statement. commit is happening via the command. 
CREATE OR REPLACE FUNCTION f_get_attrs_by_uc_group_ids (
    get_quotation_relevant_attributes IN VARCHAR2 DEFAULT 'FALSE'
) RETURN sys.odcinumberlist AS
    ucg_attr_ids sys.odcinumberlist := sys.odcinumberlist();
BEGIN
    SELECT
        final_attr_ids.attr_id
    BULK COLLECT
    INTO ucg_attr_ids
    FROM
        (
            ( SELECT
                tabv_attrs.attr_id
            FROM
                tabv_attributes tabv_attrs
            WHERE
                tabv_attrs.attr_id IN (
                    SELECT DISTINCT
                        tabv_ucpa.attr_id
                    FROM
                        tabv_ucp_attrs tabv_ucpa, (
--selecting only the use cases which does not have any use case sections referenced to it
                            SELECT
                                tabv_uc.use_case_id
                            FROM
                                tabv_use_cases tabv_uc
                            WHERE
                                ( tabv_uc.deleted_flag != 'Y'
                                  OR tabv_uc.deleted_flag IS NULL )
                                AND tabv_uc.group_id IN (
--'start with' refers the starting point of the recursive query, here group_id is used to include the given group id records also in the result set
                                    SELECT
                                        tabv_ucgs.group_id
                                    FROM
                                        tabv_use_case_groups tabv_ucgs
                                    START WITH
                                        tabv_ucgs.group_id IN (
--get the input group ids from the temporary gtt table and ensure its undeleted
                                            SELECT
                                                temp_ucg.group_id
                                            FROM
                                                tabv_use_case_groups temp_ucg
                                            WHERE
                                                temp_ucg.group_id IN (
                                                    SELECT
                                                        gtt.id
                                                    FROM
                                                        gtt_object_names gtt
                                                    WHERE
                                                        gtt.obj_name = 'TabvUseCaseGroup'
                                                )
                                                AND ( temp_ucg.deleted_flag != 'Y'
                                                      OR temp_ucg.deleted_flag IS NULL )
                                        )
                                    CONNECT BY PRIOR tabv_ucgs.group_id = tabv_ucgs.parent_group_id
                                               AND ( tabv_ucgs.deleted_flag != 'Y'
                                                     OR tabv_ucgs.deleted_flag IS NULL )
                                )
                                AND NOT EXISTS (
                                    SELECT
                                        1
                                    FROM
                                        tabv_use_case_sections temp_ucs
                                    WHERE
                                        tabv_uc.use_case_id = temp_ucs.use_case_id
                                )
                        )              final_uc_ids
                    WHERE
                        (
--one record from tabv_ucpa.use_case_id is compared with the list of final_uc_ids.use_case_id to include in the result set
                         tabv_ucpa.use_case_id = final_uc_ids.use_case_id
                          AND (
--conditional where clause
                           ( get_quotation_relevant_attributes = 'TRUE'
                                  AND tabv_ucpa.mapping_flags = 1 )
                                OR ( get_quotation_relevant_attributes = 'FALSE' ) ) )
                )
            )
            UNION
            ( SELECT
                tabv_attrs.attr_id
            FROM
                tabv_attributes tabv_attrs
            WHERE
                tabv_attrs.attr_id IN (
                    SELECT DISTINCT
                        tabv_ucpa.attr_id
                    FROM
                        tabv_ucp_attrs tabv_ucpa, (
--if tabv_parent_ucs.parent_section_id is null then the section is a parent
                            SELECT
                                tabv_parent_ucs.section_id
                            FROM
                                tabv_use_case_sections tabv_parent_ucs
                            WHERE
                                ( tabv_parent_ucs.deleted_flag != 'Y'
                                  OR tabv_parent_ucs.deleted_flag IS NULL )
                                AND tabv_parent_ucs.parent_section_id IS NULL
                                AND tabv_parent_ucs.use_case_id IN (
                                    SELECT
                                        tabv_uc.use_case_id
                                    FROM
                                        tabv_use_cases tabv_uc
                                    WHERE
                                        ( tabv_uc.deleted_flag != 'Y'
                                          OR tabv_uc.deleted_flag IS NULL )
                                        AND tabv_uc.group_id IN (
                                            SELECT
                                                tabv_ucgs.group_id
                                            FROM
                                                tabv_use_case_groups tabv_ucgs
                                            START WITH
                                                tabv_ucgs.group_id IN (
                                                    SELECT
                                                        temp_ucg.group_id
                                                    FROM
                                                        tabv_use_case_groups temp_ucg
                                                    WHERE
                                                        temp_ucg.group_id IN (
                                                            SELECT
                                                                gtt.id
                                                            FROM
                                                                gtt_object_names gtt
                                                            WHERE
                                                                gtt.obj_name = 'TabvUseCaseGroup'
                                                        )
                                                        AND ( temp_ucg.deleted_flag != 'Y'
                                                              OR temp_ucg.deleted_flag IS NULL )
                                                )
                                            CONNECT BY PRIOR tabv_ucgs.group_id = tabv_ucgs.parent_group_id
                                                       AND ( tabv_ucgs.deleted_flag != 'Y'
                                                             OR tabv_ucgs.deleted_flag IS NULL )
                                        )
                                )
                        )              final_parent_ucs_ids
                    WHERE
                        ( tabv_ucpa.section_id = final_parent_ucs_ids.section_id
                          AND ( ( get_quotation_relevant_attributes = 'TRUE'
                                  AND tabv_ucpa.mapping_flags = 1 )
                                OR ( get_quotation_relevant_attributes = 'FALSE' ) ) )
                )
            )
            UNION
            ( SELECT
                tabv_attrs.attr_id
            FROM
                tabv_attributes tabv_attrs
            WHERE
                tabv_attrs.attr_id IN (
                    SELECT DISTINCT
                        tabv_ucpa.attr_id
                    FROM
                        tabv_ucp_attrs tabv_ucpa, (
--the select below this select will include all the first level child sections even if it is in deleted state (once the deleted section selected, 
--the childs under it will not be considered). So, removing the delected sections again.
                            SELECT
                                tabv_undeleted_child_ucs.section_id
                            FROM
                                tabv_use_case_sections tabv_undeleted_child_ucs
                            WHERE
                                ( tabv_undeleted_child_ucs.deleted_flag != 'Y'
                                  OR tabv_undeleted_child_ucs.deleted_flag IS NULL )
                                AND tabv_undeleted_child_ucs.section_id IN (
--get all the child use case sections from the parent section id (start with parent_section_id to excluse the parent sections since its already
--available in the above block)
                                    SELECT
                                        tabv_child_ucs.section_id
                                    FROM
                                        tabv_use_case_sections tabv_child_ucs
                                    START WITH
                                        tabv_child_ucs.parent_section_id IN (
                                            SELECT
                                                tabv_parent_ucs.section_id
                                            FROM
                                                tabv_use_case_sections tabv_parent_ucs
                                            WHERE
                                                ( tabv_parent_ucs.deleted_flag != 'Y'
                                                  OR tabv_parent_ucs.deleted_flag IS NULL )
                                                AND tabv_parent_ucs.parent_section_id IS NULL
                                                AND tabv_parent_ucs.use_case_id IN (
                                                    SELECT
                                                        tabv_uc.use_case_id
                                                    FROM
                                                        tabv_use_cases tabv_uc
                                                    WHERE
                                                        ( tabv_uc.deleted_flag != 'Y'
                                                          OR tabv_uc.deleted_flag IS NULL )
                                                        AND tabv_uc.group_id IN (
                                                            SELECT
                                                                tabv_ucgs.group_id
                                                            FROM
                                                                tabv_use_case_groups tabv_ucgs
                                                            START WITH
                                                                tabv_ucgs.group_id IN (
                                                                    SELECT
                                                                        temp_ucg.group_id
                                                                    FROM
                                                                        tabv_use_case_groups temp_ucg
                                                                    WHERE
                                                                        temp_ucg.group_id IN (
                                                                            SELECT
                                                                                gtt.id
                                                                            FROM
                                                                                gtt_object_names gtt
                                                                            WHERE
                                                                                gtt.obj_name = 'TabvUseCaseGroup'
                                                                        )
                                                                        AND ( temp_ucg.deleted_flag != 'Y'
                                                                              OR temp_ucg.deleted_flag IS NULL )
                                                                )
                                                            CONNECT BY PRIOR tabv_ucgs.group_id = tabv_ucgs.parent_group_id
                                                                       AND ( tabv_ucgs.deleted_flag != 'Y'
                                                                             OR tabv_ucgs.deleted_flag IS NULL )
                                                        )
                                                )
                                        )
                                    CONNECT BY PRIOR tabv_child_ucs.section_id = tabv_child_ucs.parent_section_id
                                               AND ( tabv_child_ucs.deleted_flag != 'Y'
                                                     OR tabv_child_ucs.deleted_flag IS NULL )
                                )
                        )              final_child_ucs_ids
                    WHERE
                        ( tabv_ucpa.section_id = final_child_ucs_ids.section_id
                          AND ( ( get_quotation_relevant_attributes = 'TRUE'
                                  AND tabv_ucpa.mapping_flags = 1 )
                                OR ( get_quotation_relevant_attributes = 'FALSE' ) ) )
                )
            )
        ) final_attr_ids;

    RETURN ucg_attr_ids;
EXCEPTION
    WHEN OTHERS THEN
        RETURN NULL;
END f_get_attrs_by_uc_group_ids;

-------Function to get a list of iCDM PIDC attributes based on the given iCDM use case group ID's---------------------------------
-------(Optional function parameter: to apply quotation relevant filter on top of the result set of iCDM PIDC attributes)---------
--Step 1: The function query is divided into 3 major blocks. 
--            1. To get the list of use case ids
--            2. To get the list of undeleted parent use case section ids
--            3. To get the list of undeleted child use case section ids
-- The hierarchy of the use case groups:
--group_id
--    group_id
--        group_id
--            use_case
--                use_case_section
--                    use_case_section
--                        mapped_attributes
--                    use_case_section
--                        use_case_section
--                            mapped_attributes
--                use_case_section
--                    mapped_attributes
--            use_case
--                mapped_attributes
--    group_id
--        use_case
--            use_case_section
--                mapped_attributes
--1. Consider if we have one use case group ID given as the input. It is the primary key of tabv_use_case_groups table, group_id column.
--2. Also, the group id could be available in the parent_group_id column of the tabv_use_case_groups table. This means the record with the
--group id in its parent_group_id column is the child group id. Then we need to get the attributes under the child group_id as well.
--3. A group can have any number of child group ids and the child group ids can have their own child groups.
--4. In the query, we first collect all the group_ids including all the parent and child.
--5. Then with the collected group ids we fetch the list of use cases from the tabv_use_cases.group_id (there are no childs for the use cases).
--6. The collected use cases will be stored in final_uc_ids. final_uc_ids will have only the use cases which does not have any 
--use case sections referenced to it. Because, if there are any use case sections under a use case, then only the use case sections must be
--used to fetch the attributes and not the use cases (attributes cannot be mapped to use case if it has use case sections).
--7. With the set of use cases, we can fetch the use case sections from tabv_use_case_sections.use_case_id.
--8. First we collect the parent use case sections alone which are undeleted, because the use case sections also have the hierarchy similar to 
--that of the group ids. The child use case sections will be having its parent use case section on its parent_section_id column.
--9. With the parent use case sections (final_parent_ucs_ids), we shall now collect all the child use case sections (final_child_ucs_ids).
--10. Once all the required use case ids and use case section ids, we query the tabv_ucp_attrs based on these ids and remove duplicates and 
--union the result set.
--11. Finally collecting the attributes list from tabv_attributes table to ensure the ucp attributes are availble in its reference table.
--12. If the function parameter is passed as 'TRUE', then when collecting the tabv_ucp_attrs list, we also check for the mapping_flags=1
--using a conditional where clause, which refers that the appropriate attribute is a quotation relevant attribute.

/