====================================================================
 Pre-CTS 
 by Taeho Kim <tandroid.kim@samsung.com>
====================================================================

* Features
  - Install/copy package or required files before running CTS
  - Auto-set required settings by UI automation
 
* Prerequisites
  Before using Pre-CTS, following files should be placed alongside
  application binary :
  
   - CTS media files
   - CTS Device admin apk
   
  All required files can be downloaded from http://s.android.com/compatibility/downloads.html
   
  [CTS media files]
  You should create folder 'cts_media' then copy cts media files as follows :
  
    - cts_media
      ¦¦ bbb_short
      ¦¦ bbb_full
      
  [CTS Device admin apk]
  You should create folder 'cts_device_admin' then copy device admin apk to following folder
  that fits with its own version.
  
    - cts_device_admin
      ¦¦ 4.1
        ¦¦ CtsDeviceAdmin.apk (from cts 4.1)
      ¦¦ 4.2
        ¦¦ CtsDeviceAdmin.apk (from cts 4.2)
      ¦¦ 4.3
        ¦¦ CtsDeviceAdmin.apk (from cts 4.3)
  