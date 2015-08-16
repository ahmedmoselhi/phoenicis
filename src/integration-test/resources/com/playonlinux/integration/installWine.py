from com.playonlinux.integration import ServiceManagerGetter
from com.playonlinux.framework import SetupWizard, WineVersion
from com.playonlinux.framework import Environment
from com.playonlinux.engines.wine import WineVersionManager

from java.lang import Class

import unittest, time, os
from com.playonlinux.core.utils import OperatingSystem


class TestInstallWine(unittest.TestCase):
    def testInstallWineVersion(self):
        time.sleep(2)
        ServiceManagerGetter().init("com.playonlinux.contexts.WineVersionServicesContext")

        setupWizard = SetupWizard("Mock setup wizard")
        setupWizard.init()

        wineVersionManager = ServiceManagerGetter.serviceManager.getService(WineVersionManager)

        print ServiceManagerGetter.serviceManager
        while(wineVersionManager.isUpdating()):
            print "Updating wine version list..."
            time.sleep(2)


        wineInstallation = WineVersion("1.7.36", "upstream-x86", setupWizard)
        wineInstallation.install()

        installationPath = "%s/engines/wine/upstream-%s-x86/1.7.36/bin/wine" % (Environment.getUserRoot(),
         OperatingSystem.fetchCurrentOperationSystem().getNameForWinePackages())

        ServiceManagerGetter.serviceManager.shutdown()

        print "Checking that wine binary is installed in %s" % installationPath
        self.assertTrue(os.path.exists(installationPath))

