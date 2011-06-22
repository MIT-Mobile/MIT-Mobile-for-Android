#!/usr/bin/python

import sys
import os
import build_include

"""   parameters -- tag: the git tag or branch to use, fast: use git pull versus git clone
      zero parameters means build local source without pulling from git"""

if len(sys.argv) > 1:
    tag = sys.argv[1]
else:
    tag = False

if len(sys.argv) > 2:
   fast = sys.argv[2]
else:
   fast = "true"

fast = ( fast == "true" )

android_path = build_include.build_apk(tag, not fast)
    
import build_settings

# old path for adb
adb_path = build_settings.android_sdk_path + "/tools/adb"
if not os.path.exists(adb_path):
    adb_path = build_settings.android_sdk_path + "/platform-tools/adb"
    if not os.path.exists(adb_path):
        raise Exception("adb not found")

build_include.shell(adb_path + " install -r " + android_path + "/bin/MITSplashActivity-debug.apk", False)
