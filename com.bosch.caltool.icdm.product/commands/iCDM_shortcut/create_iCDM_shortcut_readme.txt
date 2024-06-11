================================================================================
                           create_iCDM_shortcut.cmd
================================================================================

=== Description ===

    create_iCDM_shortcut.cmd is a windows batch script file which can be used to
 create shortcut for iCDM application on desktop and start menu. This will create
 the shortcut only if the shortcut is not present in both the start menu and desktop already.
 
=== How to use ===
 
STEP 1: Copy the icons folder present inside the commands folder in com.bosch.caltool.icdm.product
plugin and paste it along with the toolbase files required for deployment.

STEP 2: Copy the create_iCDM_shortcut.cmd file present inside the commands folder in com.bosch.caltool.icdm.product
plugin and paste it inside the bin folder of toolbase files.

STEP 3: Add the below piece of code in icdm_client.cmd file before the start of iCDM.exe.
        call "%RTC_ICDM_HOME%\bin\create_iCDM_shortcut.cmd"