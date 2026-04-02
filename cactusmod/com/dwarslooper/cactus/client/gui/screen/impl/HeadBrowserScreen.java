package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.feature.content.ContentPackManager;
import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.systems.ItemGroupSystem;
import com.dwarslooper.cactus.client.systems.config.CactusSystemConfig;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.render.RenderUtils;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import java.awt.Color;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1814;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_5250;
import net.minecraft.class_9290;
import net.minecraft.class_9296;
import net.minecraft.class_9334;
import org.jetbrains.annotations.NotNull;

public class HeadBrowserScreen extends CScreen {
   private float loadProgress = 0.0F;
   private static boolean cached = false;
   private long lastUpdate = System.currentTimeMillis();
   private static final String[] tags = new String[]{"alphabet", "animals", "blocks", "decoration", "food-drinks", "humans", "humanoid", "miscellaneous", "monsters", "plants"};
   public static final String apiUrl = "https://minecraft-heads.com/scripts/api.php";
   public static final Map<String, List<class_1799>> categoryData = new HashMap();
   private class_1799 currentStack;

   public HeadBrowserScreen() {
      super("head_browser");
   }

   public void method_25426() {
      super.method_25426();
      if (!cached) {
         this.reloadAll();
      }

   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      if (!cached) {
         float f = this.loadProgress / (float)tags.length;
         class_327 var10001 = this.field_22793;
         class_5250 var10002 = class_2561.method_43470("Loading head data ...");
         int var10003 = this.field_22789 / 2;
         int var10004 = this.field_22790 / 2 - 10;
         Objects.requireNonNull(this.field_22793);
         context.method_27534(var10001, var10002, var10003, var10004 - 9, Color.GREEN.getRGB());
         RenderUtils.renderProgressBar(context, this.field_22789 / 2 - 200, this.field_22790 / 2 - 8, this.field_22789 / 2 + 200, this.field_22790 / 2 + 8, f);
      } else {
         if (System.currentTimeMillis() - this.lastUpdate > 2000L) {
            this.lastUpdate = System.currentTimeMillis();
            Random generator = new Random();
            Object[] values = categoryData.values().toArray();
            List<class_1799> randomValue = (List)values[generator.nextInt(values.length)];
            this.currentStack = (class_1799)randomValue.get(generator.nextInt(randomValue.size()));
         }

         context.method_51448().pushMatrix();
         context.method_51448().scale(4.0F, 4.0F);
         context.method_51448().translate(-4.0F, -4.0F);
         if (this.currentStack != null) {
            context.method_51427(this.currentStack, this.field_22789 / 2 / 4, this.field_22790 / 2 / 4);
         }

         context.method_51448().popMatrix();
      }
   }

   public void reloadAll() {
      System.out.println("Loading..");
      fetchAll((s) -> {
         ++this.loadProgress;
      }).whenComplete((unused, throwable) -> {
         System.out.println("Load finished");
         if (throwable == null) {
            CactusClient.getLogger().info("Successfully loaded all heads!");
            cached = true;
            CactusConstants.mc.method_1507((class_437)null);
         } else {
            CactusClient.getLogger().error("Can't fetch head data", throwable);
         }

         ItemGroupSystem.reloadAll();
      });
   }

   public static CompletableFuture<Void> fetchAll(Consumer<String> stepConsumer) {
      CompletableFuture<Void> future = new CompletableFuture();
      CompletableFuture.runAsync(() -> {
         String[] var2 = tags;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String tag = var2[var4];

            try {
               URL url = (new URI("https://minecraft-heads.com/scripts/api.php?tags=true&cat=" + tag)).toURL();
               InputStream stream = url.openStream();
               JsonArray data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonArray();
               List<class_1799> stacks = new ArrayList();
               data.forEach((jsonElement) -> {
                  stacks.add(buildItemStack(jsonElement.getAsJsonObject()));
               });
               categoryData.put(tag, stacks);
            } catch (Exception var10) {
               future.completeExceptionally(var10);
            }

            CactusClient.getLogger().info("Loaded head Category '{}'", tag);
            stepConsumer.accept(tag);
         }

         future.complete((Object)null);
      });
      return future;
   }

   public static boolean isCached() {
      return cached;
   }

   public static void setCached(boolean cached) {
      HeadBrowserScreen.cached = cached;
   }

   public static List<class_1799> getAllItems() {
      List<class_1799> list = new ArrayList();
      Collection var10000 = categoryData.values();
      Objects.requireNonNull(list);
      var10000.forEach(list::addAll);
      return list;
   }

   public static List<class_1799> getItemsFromCat(String cat) {
      return (List)categoryData.getOrDefault(cat, Collections.emptyList());
   }

   public static class_1799 buildItemStack(JsonObject object) {
      class_1799 headStack = new class_1799(class_1802.field_8575);
      Builder<String, Property> builder = ImmutableMultimap.builder();
      Property property = new Property("textures", object.get("value").getAsString());
      builder.put(property.name(), property);
      PropertyMap properties = new PropertyMap(builder.build());
      GameProfile gameProfile = new GameProfile(UUID.fromString(object.get("uuid").getAsString()), object.get("name").getAsString(), properties);
      headStack.method_57379(class_9334.field_49617, class_9296.method_73307(gameProfile));
      headStack.method_57379(class_9334.field_50239, class_2561.method_43470(object.get("name").getAsString()));
      headStack.method_57379(class_9334.field_50073, class_1814.field_8903);
      headStack.method_57379(class_9334.field_49632, new class_9290(List.of(class_2561.method_43470(object.get("tags").getAsString()))));
      return headStack;
   }

   public static String[] getTags() {
      return tags;
   }

   public static void showWarning() {
      if (class_310.method_1551().method_53466()) {
         if (!CactusSystemConfig.skipHDBWarning) {
            CactusConstants.mc.method_1507(new ChoiceWarningScreen(CactusConstants.mc.field_1755, class_2561.method_43470("This feature is still in development. Because of the way the Inventory system works, it can cause the game to freeze for a short time when initially opening the creative Inventory. Be aware that this is not yet finished, things might break.\n\n§eRejoining your world / server is required in order to correctly load Inventory changes"), (b) -> {
               CactusSystemConfig.skipHDBWarning = b;
            }, (b) -> {
               if (!b) {
                  ContentPackManager.get().ofId("head_database").setEnabled(false);
               }

            }));
         }
      }
   }
}
