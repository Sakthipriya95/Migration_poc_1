:: variable declaration
set desktoppath=%userprofile%\desktop\iCDM.lnk
set startmenu=%APPDATA%\Microsoft\Windows\Start Menu\Programs\iCDM.lnk
set iconfolder=%APPDATA%\iCDM\icons
set icdmicon=%APPDATA%\iCDM\icons\icdm.ico
set target=%PROGRAMDATA%\iCDM\LinkClient\link_start.bat

:: check if the shortcut exist or not
if not exist "%desktoppath%" (
    if not exist "%startmenu%" (
        call :create_shortcut
    )
) 

GOTO completed

:: Shortcut creation method 
:create_shortcut
    :: create a folder for the icons
    mkdir "%iconfolder%" 
    
    :: copy the icons to icon_folder
    copy "%RTC_ICDM_HOME%\icons\icdm.ico" "%iconfolder%"
    copy "%RTC_ICDM_HOME%\icons\pidc.ico" "%iconfolder%"
    
    set SCRIPT="%TEMP%\%RANDOM%-%RANDOM%-%RANDOM%-%RANDOM%.vbs"
    echo Set oWS = WScript.CreateObject("WScript.Shell") >> %SCRIPT%
    
    :: desktop shortcut creation
    echo Set oLink = oWS.CreateShortcut("%desktoppath%") >> %SCRIPT%
    echo oLink.TargetPath = "%target%" >> %SCRIPT%
    echo oLink.IconLocation = "%icdmicon%" >> %SCRIPT%
    echo oLink.Save >> %SCRIPT%
    echo "iCDM desktop shortcut created successfully..."

    :: start menu shortcut creation
    echo Set stLink = oWS.CreateShortcut("%startmenu%") >> %SCRIPT%
    echo stLink.TargetPath = "%target%" >> %SCRIPT%
    echo stLink.IconLocation = "%icdmicon%" >> %SCRIPT%
    echo stLink.Save >> %SCRIPT%
    echo "iCDM start menu shortcut created successfully..."

    cscript /nologo %SCRIPT%
    del %SCRIPT%
    
:completed
    