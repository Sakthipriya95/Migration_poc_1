--
-- 221731
--
CREATE OR REPLACE FORCE VIEW 
  V_VCDM_DATASETS_WORKPKG_STAT 
  (ID, APRJ_ID, APRJ_NAME, VCDM_VARIANT, 
  VCDM_SOFTWARE, EASEEDST_ID, WORKPKG_NAME, 
  NOSTATE_LABELS, CHANGED_LABELS, PRELIMCALIBRATED_LABELS, 
  CALIBRATED_LABELS, CHECKED_LABELS, COMPLETED_LABELS, 
  REVISION,
  STATUS_ID, EASEEDST_MOD_DATE) AS 
  select wp.id id
     , datasets.element_id   aprj_id
     , datasets.element_name aprj_Name
     , datasets.prod_key     vcdm_Variant
     , datasets.prog_key     vcdm_Software
     , datasets.easeedst_id
     , wp.workpkg_name
     , wp.nostate_labels
     , wp.changed_labels
     , wp.prelimcalibrated_labels
     , wp.calibrated_labels
     , wp.checked_labels
     , wp.completed_labels
     , datasets.revision
     , datasets.status_id
     , datasets.easeedst_mod_date
  from tabe_datasets datasets
     , tabe_datasets_workpkg_stat wp
 where datasets.easeedst_id = wp.easeedst_id;
