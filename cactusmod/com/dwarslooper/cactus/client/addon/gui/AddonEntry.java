package com.dwarslooper.cactus.client.addon.gui;

import com.dwarslooper.cactus.client.addon.v2.Addon;
import com.dwarslooper.cactus.client.gui.util.CSimpleListEntry;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_10799;
import net.minecraft.class_11909;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_4280;
import org.jetbrains.annotations.NotNull;

public class AddonEntry extends CSimpleListEntry<AddonEntry> {
   private final Addon addon;
   private final String authors;
   private final String version;
   private class_2960 iconTexture;
   private static final class_2960 DEFAULT_ICON = class_2960.method_60655("cactus", "textures/cactus.png");
   private static final Map<String, class_2960> ICON_CACHE = new HashMap();

   public AddonEntry(class_4280<AddonEntry> owner, Addon addon) {
      super(owner);
      this.addon = addon;
      List<String> a = addon.authors();
      StringBuilder authorList = new StringBuilder();
      Iterator var5 = a.iterator();

      while(var5.hasNext()) {
         String person = (String)var5.next();
         authorList.append("§7").append(person).append("§r§8");
         if (a.size() - 2 > a.indexOf(person)) {
            authorList.append(", ");
         } else if (a.size() - 1 > a.indexOf(person)) {
            authorList.append(" & ");
         }
      }

      this.authors = authorList.toString();
      if (addon.metadata() != null) {
         this.version = "v" + addon.metadata().getVersion().getFriendlyString();
      } else {
         this.version = "v1.0.0";
      }

      this.iconTexture = this.loadAddonIcon();
   }

   private class_2960 loadAddonIcon() {
      if (this.addon.metadata() == null) {
         return DEFAULT_ICON;
      } else {
         String modId = this.addon.id();
         if (ICON_CACHE.containsKey(modId)) {
            return (class_2960)ICON_CACHE.get(modId);
         } else {
            try {
               Optional<String> iconPathOpt = this.addon.metadata().getIconPath(32);
               if (iconPathOpt.isEmpty()) {
                  iconPathOpt = this.addon.metadata().getIconPath(64);
               }

               if (iconPathOpt.isEmpty()) {
                  iconPathOpt = this.addon.metadata().getIconPath(128);
               }

               if (iconPathOpt.isPresent()) {
                  String iconPathStr = (String)iconPathOpt.get();
                  Optional<Path> modPath = FabricLoader.getInstance().getModContainer(modId).flatMap((container) -> {
                     return container.findPath(iconPathStr);
                  });
                  if (modPath.isPresent()) {
                     Path iconPath = (Path)modPath.get();
                     InputStream stream = Files.newInputStream(iconPath);

                     class_2960 var10;
                     try {
                        class_1011 image = class_1011.method_4309(stream);
                        class_2960 textureId = class_2960.method_60655("cactus", "addon_icons/" + modId);
                        class_1043 texture = new class_1043(() -> {
                           return "Addon Icon: " + this.addon.name();
                        }, image);
                        CactusConstants.mc.method_1531().method_4616(textureId, texture);
                        ICON_CACHE.put(modId, textureId);
                        var10 = textureId;
                     } catch (Throwable var12) {
                        if (stream != null) {
                           try {
                              stream.close();
                           } catch (Throwable var11) {
                              var12.addSuppressed(var11);
                           }
                        }

                        throw var12;
                     }

                     if (stream != null) {
                        stream.close();
                     }

                     return var10;
                  }
               }
            } catch (Exception var13) {
            }

            ICON_CACHE.put(modId, DEFAULT_ICON);
            return DEFAULT_ICON;
         }
      }
   }

   public void method_25343(@NotNull class_332 context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
      int x = this.method_46426();
      int y = this.method_46427();
      int width = this.method_25368();
      int height = this.method_25364();
      int iconSize = 32;
      int iconX = x + 4;
      int iconY = y + (height - iconSize) / 2;

      try {
         context.method_25290(class_10799.field_56883, this.iconTexture, iconX, iconY, 0.0F, 0.0F, iconSize, iconSize, iconSize, iconSize);
      } catch (Exception var19) {
         context.method_25294(iconX, iconY, iconX + iconSize, iconY + iconSize, -15066598);
         context.method_25300(CactusConstants.mc.field_1772, "?", iconX + iconSize / 2, iconY + iconSize / 2 - 4, -10066330);
      }

      int textX = iconX + iconSize + 8;
      int nameY = y + 8;
      context.method_25303(CactusConstants.mc.field_1772, this.addon.name(), textX, nameY, -1);
      int versionWidth = CactusConstants.mc.field_1772.method_1727(this.version);
      int versionX = x + width - versionWidth - 8;
      context.method_25303(CactusConstants.mc.field_1772, this.version, versionX, nameY, -8355712);
      Objects.requireNonNull(CactusConstants.mc.field_1772);
      int authorsY = nameY + 9 + 2;
      String authorPrefix = "§8by §7";
      context.method_25303(CactusConstants.mc.field_1772, authorPrefix + this.authors, textX, authorsY, -6710887);
      context.method_25294(x + 2, y + height - 1, x + width - 2, y + height, -12566464);
   }

   public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
      return false;
   }
}
