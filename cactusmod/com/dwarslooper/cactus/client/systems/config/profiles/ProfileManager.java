package com.dwarslooper.cactus.client.systems.config.profiles;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.WorldJoinedEvent;
import com.dwarslooper.cactus.client.gui.toast.ToastSystem;
import com.dwarslooper.cactus.client.systems.config.ConfigHandler;
import com.dwarslooper.cactus.client.systems.config.FileConfiguration;
import com.dwarslooper.cactus.client.systems.config.InternalOnly;
import com.dwarslooper.cactus.client.systems.config.TreeSerializerFilter;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import net.minecraft.class_1074;
import net.minecraft.class_1802;
import net.minecraft.class_642;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

@InternalOnly
public class ProfileManager extends FileConfiguration<ProfileManager> {
   private final List<ConfigurationProfile> profiles = new ArrayList();
   private final File profileDirectory;

   public ProfileManager(ConfigHandler handler) {
      super("profiles", handler);
      this.profileDirectory = new File(CactusConstants.DIRECTORY, "profiles");
      CactusClient.EVENT_BUS.subscribe(this);
   }

   public JsonObject toJson(TreeSerializerFilter filter) {
      JsonObject object = new JsonObject();
      JsonArray array = new JsonArray();
      Iterator var4 = this.profiles.iterator();

      while(var4.hasNext()) {
         ConfigurationProfile profile = (ConfigurationProfile)var4.next();
         array.add(profile.toJson(filter.resolve(profile.getName())));
      }

      object.add("profiles", array);
      return object;
   }

   public ProfileManager fromJson(JsonObject object) {
      this.profiles.clear();
      Iterator var2 = object.getAsJsonArray("profiles").iterator();

      while(var2.hasNext()) {
         JsonElement element = (JsonElement)var2.next();
         JsonObject profile = element.getAsJsonObject();
         this.profiles.add(ConfigurationProfile.createFromJson(profile));
      }

      return this;
   }

   public boolean addIfAbsent(ConfigurationProfile profile) {
      if (this.canAdd(profile.getName())) {
         this.profiles.add(profile);
         this.save(profile);
         return true;
      } else {
         return false;
      }
   }

   public void remove(ConfigurationProfile profile) {
      this.profiles.remove(profile);
   }

   public boolean canAdd(String name) {
      return this.profiles.stream().noneMatch((profile) -> {
         return profile.getName().equalsIgnoreCase(name);
      });
   }

   public List<ConfigurationProfile> getProfiles() {
      return this.profiles;
   }

   public static List<FileConfiguration<?>> getConfigurations() {
      return CactusClient.getInstance().getConfigHandler().getConfigurations().values().stream().filter((config) -> {
         return !config.isInternal();
      }).toList();
   }

   @EventHandler
   public void onJoin(WorldJoinedEvent event) {
      class_642 serverEntry = CactusConstants.mc.method_1558();
      if (serverEntry != null) {
         this.profiles.forEach((profile) -> {
            if (profile.loadsForServer(serverEntry.field_3761)) {
               this.load(profile);
            }

         });
      }

   }

   private String translated(String key, Object... args) {
      return class_1074.method_4662("gui.screen.profiles." + key, args);
   }

   public File getDirectory() {
      return this.profileDirectory;
   }

   public File getSaveDirectory(ConfigurationProfile profile) {
      return profile.getSaveDirectory(this.getDirectory());
   }

   public void load(ConfigurationProfile profile) {
      profile.getConfigurations(this.getHandler()).forEach((config) -> {
         this.getHandler().load(config, this.getSaveDirectory(profile));
      });
   }

   public void save(ConfigurationProfile profile) {
      profile.getConfigurations(this.getHandler()).forEach((config) -> {
         this.getHandler().save(config, this.getSaveDirectory(profile), TreeSerializerFilter.ALL);
      });
   }

   public void exportProfile(ConfigurationProfile profile) {
      this.save(profile);
      this.exportConfigurations(profile.getFiles(this.getSaveDirectory(profile), this.getHandler()));
   }

   public void importProfile(ConfigurationProfile profile) {
      this.importConfigurations(profile.getFiles(this.getSaveDirectory(profile), this.getHandler()));
   }

   public void exportConfigurations(List<File> files) {
      CompletableFuture.supplyAsync(() -> {
         return TinyFileDialogs.tinyfd_saveFileDialog(this.translated("exportTitle"), "cactus-configurations.zip", Utils.createFileTypeFilter("*.zip"), this.translated("fileTypeArchives"));
      }).thenAccept((path) -> {
         if (path != null) {
            try {
               ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(path));

               try {
                  FileInputStream fis;
                  for(Iterator var4 = files.iterator(); var4.hasNext(); fis.close()) {
                     File file = (File)var4.next();
                     fis = new FileInputStream(file);

                     try {
                        ZipEntry zipEntry = new ZipEntry(file.getName());
                        zipOut.putNextEntry(zipEntry);
                        byte[] bytes = new byte[1024];

                        int length;
                        while((length = fis.read(bytes)) >= 0) {
                           zipOut.write(bytes, 0, length);
                        }

                        zipOut.closeEntry();
                     } catch (Throwable var12) {
                        try {
                           fis.close();
                        } catch (Throwable var11) {
                           var12.addSuppressed(var11);
                        }

                        throw var12;
                     }
                  }

                  ToastSystem.displayMessage(this.translated("exportSuccess"), "", class_1802.field_8204);
               } catch (Throwable var13) {
                  try {
                     zipOut.close();
                  } catch (Throwable var10) {
                     var13.addSuppressed(var10);
                  }

                  throw var13;
               }

               zipOut.close();
            } catch (IOException var14) {
               CactusClient.getLogger().error("Configuration export failed", var14);
               ToastSystem.displayMessage(this.translated("exportFailed"), var14.getMessage(), class_1802.field_8077);
            }

         }
      });
   }

   public void importConfigurations(List<File> files) {
      CompletableFuture.supplyAsync(() -> {
         return TinyFileDialogs.tinyfd_openFileDialog(this.translated("importTitle"), (CharSequence)null, Utils.createFileTypeFilter("*.zip", "*.json"), this.translated("fileTypeConfig"), false);
      }).thenAccept((path) -> {
         if (path != null) {
            File source = new File(path);
            if (source.exists()) {
               boolean isZip = source.getName().endsWith(".zip");
               if (isZip) {
                  try {
                     ZipFile zip = new ZipFile(source);

                     try {
                        CactusClient.getInstance().getConfigHandler().save();
                        Enumeration<? extends ZipEntry> entries = zip.entries();
                        Map<String, ZipEntry> unsortedEntries = new HashMap();
                        entries.asIterator().forEachRemaining((z) -> {
                           unsortedEntries.put(z.getName(), z);
                        });
                        Iterator var8 = files.iterator();

                        while(true) {
                           if (!var8.hasNext()) {
                              CactusConstants.mc.execute(() -> {
                                 CactusClient.getInstance().getConfigHandler().reload();
                              });
                              ToastSystem.displayMessage(this.translated("importSuccess"), "", class_1802.field_8204);
                              break;
                           }

                           File file = (File)var8.next();
                           ZipEntry zipEntry = (ZipEntry)unsortedEntries.get(file.getName());
                           if (zipEntry != null) {
                              InputStream inputStream = zip.getInputStream(zipEntry);
                              Files.copy(inputStream, file.toPath(), new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
                           }
                        }
                     } catch (Throwable var13) {
                        try {
                           zip.close();
                        } catch (Throwable var12) {
                           var13.addSuppressed(var12);
                        }

                        throw var13;
                     }

                     zip.close();
                  } catch (IOException var14) {
                     CactusClient.getLogger().error("Configuration import failed", var14);
                     ToastSystem.displayMessage(this.translated("importFailed"), var14.getMessage(), class_1802.field_8077);
                  }
               }

            }
         }
      });
   }
}
