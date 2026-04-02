package com.dwarslooper.cactus.client.systems;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.gui.screen.impl.SubmitLastCrashScreen;
import com.dwarslooper.cactus.client.systems.config.CactusSystemConfig;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;

public class RunUtils {
   public static void handleLastCrash() {
      if (CactusSystemConfig.lastKnownCrash == -1L) {
         CactusSystemConfig.lastKnownCrash = System.currentTimeMillis();
      }

      File crashReportsDirectory = new File(CactusConstants.mc.field_1697, "crash-reports");
      if (crashReportsDirectory.exists()) {
         File latestCrash = Utils.getLastModifiedFile(crashReportsDirectory);
         if (latestCrash != null) {
            FileTime time = Utils.fileCreationTime(latestCrash);
            if (time != null) {
               long timeMS = time.toMillis();
               if (timeMS > CactusSystemConfig.lastKnownCrash) {
                  CactusSystemConfig.lastKnownCrash = timeMS;

                  try {
                     String content = Files.readString(latestCrash.toPath());
                     Package pckg = CactusClient.getInstance().getClass().getPackage();
                     if (content.contains("at %s".formatted(new Object[]{pckg.getName()}))) {
                        CactusConstants.mc.method_1507(new SubmitLastCrashScreen(latestCrash, content));
                     }
                  } catch (IOException var7) {
                     CactusClient.getLogger().error("Failed to find crash info", var7);
                  }
               }
            }
         }

      }
   }
}
