package com.dwarslooper.cactus.client.systems.profile;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.render.cosmetics.emotes.EmoteAnimation;
import com.dwarslooper.cactus.client.render.cosmetics.models.DragonWingCosmeticRenderer;
import com.dwarslooper.cactus.client.render.cosmetics.models.JsonCosmeticRenderer;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.client.CactusModelLayerRegistry;
import com.dwarslooper.cactus.client.util.generic.WebImage;
import com.dwarslooper.cactus.client.util.networking.HttpUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonParseException;
import com.ibasco.image.gif.GifFrame;
import com.ibasco.image.gif.GifImageReader;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.minecraft.class_10055;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_1060;
import net.minecraft.class_11659;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_4587;
import net.minecraft.class_591;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractCosmetic<T extends AbstractCosmetic<?>> implements ICosmetic<T> {
   public static Cache<String, AbstractCosmetic<?>> COSMETICS_CACHE = CacheBuilder.newBuilder().build();
   public static Cache<String, AbstractCosmetic<?>> DATA_CACHE;
   private static final ExecutorService loader;
   private final String id;
   private final String name;
   @Nullable
   private final String group;
   private final class_2561 typeName;

   public static void push(AbstractCosmetic<?> cosmetic) {
      if (COSMETICS_CACHE.asMap().containsKey(cosmetic.getId())) {
         CactusClient.getLogger().error("Attempted to push cosmetic {} into cache but this entry is already present", cosmetic.getId());
      } else {
         COSMETICS_CACHE.put(cosmetic.getId(), cosmetic);
      }
   }

   public static void invalidate() {
      COSMETICS_CACHE.invalidateAll();
      COSMETICS_CACHE.cleanUp();
      DATA_CACHE.invalidateAll();
      DATA_CACHE.cleanUp();
   }

   public AbstractCosmetic(String id, String name, @Nullable String group) {
      this.id = id;
      this.name = name;
      this.group = group;
      this.typeName = class_2561.method_43471("irc.cosmetics." + this.getParser().name().toLowerCase());
   }

   public boolean isDataLoaded() {
      return DATA_CACHE.asMap().containsKey(this.id);
   }

   public void ensureDataLoaded() {
      if (!this.isDataLoaded()) {
         DATA_CACHE.put(this.id, this);
         this.fetch();
      }

   }

   public static void onMc(AbstractCosmetic.ThrowingRunnable runnable) {
      CactusConstants.mc.execute(() -> {
         try {
            runnable.run();
         } catch (Exception var2) {
            throw new RuntimeException(var2);
         }
      });
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   @Nullable
   public String getGroup() {
      return this.group;
   }

   public boolean canEquipWith(ICosmetic<?> cosmetic) {
      String cg = cosmetic.getGroup();
      return this.group == null || cg == null || !Objects.equals(cg, this.group);
   }

   public boolean equals(Object obj) {
      boolean var10000;
      if (obj instanceof ICosmetic) {
         ICosmetic<?> iCosmetic = (ICosmetic)obj;
         if (this.id.equals(iCosmetic.getId())) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   public int hashCode() {
      return this.id.hashCode();
   }

   public class_2561 getTypeName() {
      return this.typeName;
   }

   static {
      DATA_CACHE = CacheBuilder.newBuilder().expireAfterAccess(20L, TimeUnit.MINUTES).removalListener((n) -> {
         if (n.getValue() != null) {
            CactusConstants.mc.execute(() -> {
               ((AbstractCosmetic)n.getValue()).drop();
            });
         }

      }).build();
      loader = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().name("Cactus / Cosmetic Loading Dispatcher #", 0L).factory());
   }

   @FunctionalInterface
   public interface ThrowingRunnable {
      void run() throws Exception;
   }

   public static class Emote extends AbstractCosmetic<AbstractCosmetic.Emote> {
      private final String animationUrl;
      private final boolean cancelOnMove;
      private byte[] emoteData;
      private Runnable loadCallback;

      public Emote(String id, String name, @Nullable String group, String animationUrl, boolean cancelOnMove) {
         super(id, name, group);
         this.animationUrl = animationUrl;
         this.cancelOnMove = cancelOnMove;
      }

      public CosmeticParser getParser() {
         return CosmeticParser.EMOTE;
      }

      public class_1792 getDisplayIcon() {
         throw new IllegalStateException("Not displayable");
      }

      public void fetch() {
         CactusClient.getLogger().info("Loading emote {} from {}", this.getId(), this.animationUrl);
         AbstractCosmetic.loader.submit(() -> {
            try {
               HttpURLConnection con = (HttpURLConnection)(new URI(this.animationUrl)).toURL().openConnection();
               if (con.getResponseCode() == 200) {
                  InputStream in = con.getInputStream();
                  this.emoteData = in.readAllBytes();
                  if (this.loadCallback != null) {
                     this.loadCallback.run();
                  }
               }
            } catch (URISyntaxException | IOException var3) {
               CactusClient.getLogger().error("Failed to load emote", var3);
            }

         });
      }

      public CompletableFuture<EmoteAnimation> getEmote() {
         CompletableFuture<EmoteAnimation> future = new CompletableFuture();
         Runnable r = () -> {
            future.complete(this.createEmote());
         };
         if (this.isDataLoaded() && this.emoteData != null) {
            r.run();
         } else {
            this.loadCallback = r;
         }

         this.ensureDataLoaded();
         return future;
      }

      public EmoteAnimation createEmote() {
         EmoteAnimation emote = new EmoteAnimation(this.emoteData);
         emote.play(0L);
         return emote;
      }

      public void drop() {
         this.emoteData = null;
      }

      public boolean isStandardEquippable() {
         return false;
      }

      public boolean cancelsOnMove() {
         return this.cancelOnMove;
      }
   }

   public static class Wings extends AbstractCosmetic<AbstractCosmetic.Wings> {
      private static final class_2960 DEFAULT_TEXTURE = class_2960.method_60656("textures/entity/enderdragon/dragon.png");
      private final String textureUrl;
      private final class_2960 textureIdentifier;
      private DragonWingCosmeticRenderer renderer;

      public Wings(String id, String name, @Nullable String group, String textureUrl) {
         super(id, name, group);
         this.textureUrl = textureUrl;
         if (!textureUrl.isEmpty()) {
            this.textureIdentifier = class_2960.method_60655("cactus", "dynamic/cosmetics/%s".formatted(new Object[]{this.getId()}));
         } else {
            this.textureIdentifier = DEFAULT_TEXTURE;
         }

      }

      public CosmeticParser getParser() {
         return CosmeticParser.WINGS;
      }

      public class_1792 getDisplayIcon() {
         return class_1802.field_8833;
      }

      public boolean isStandardEquippable() {
         return true;
      }

      public void fetch() {
         CactusClient.getLogger().info("Loading wings {} from {}", this.getId(), this.textureUrl.isEmpty() ? "DEFAULT" : this.textureUrl);
         AbstractCosmetic.loader.submit(() -> {
            try {
               if (!this.textureUrl.isEmpty()) {
                  class_1011 texture = WebImage.fetchTexture(this.textureUrl);
                  if (texture == null) {
                     return;
                  }

                  onMc(() -> {
                     CactusConstants.mc.method_1531().method_4616(this.textureIdentifier, new class_1043(() -> {
                        return "WebImage:" + this.textureUrl;
                     }, texture));
                  });
               }

               this.renderer = new DragonWingCosmeticRenderer(this.textureIdentifier);
            } catch (URISyntaxException | IOException var2) {
               CactusClient.getLogger().error("Failed to load model", var2);
            } catch (Exception var3) {
               CactusClient.getLogger().error("Other model loading error", var3);
            }

         });
      }

      public void drop() {
         if (!this.textureUrl.isEmpty()) {
            CactusConstants.mc.method_1531().method_4615(this.textureIdentifier);
         }

      }

      public void render(class_4587 matrices, class_11659 queue, class_10055 entity, class_591 ctx, float tickDelta, int light) {
         this.ensureDataLoaded();
         if (this.renderer != null && CactusConstants.mc.method_1531().field_5286.get(this.textureIdentifier) != null) {
            this.renderer.render(matrices, queue, entity, ctx, tickDelta, light);
         }

      }
   }

   public static class Model extends AbstractCosmetic<AbstractCosmetic.Model> {
      private final String textureUrl;
      private final String modelUrl;
      private final class_2960 textureIdentifier;
      private JsonCosmeticRenderer renderer;

      public JsonCosmeticRenderer getRenderer() {
         return this.renderer;
      }

      public Model(String id, String name, @Nullable String group, String textureUrl, String modelUrl) {
         super(id, name, group);
         this.textureUrl = textureUrl;
         this.modelUrl = modelUrl;
         this.textureIdentifier = class_2960.method_60655("cactus", "dynamic/cosmetics/%s".formatted(new Object[]{id}));
      }

      public CosmeticParser getParser() {
         return CosmeticParser.MODEL;
      }

      public class_1792 getDisplayIcon() {
         return class_1802.field_8434;
      }

      public boolean isStandardEquippable() {
         return true;
      }

      public void fetch() {
         CactusClient.getLogger().info("Loading dynamic model {} from {}", this.getId(), this.modelUrl);
         AbstractCosmetic.loader.submit(() -> {
            try {
               String modelData = HttpUtils.fetchUTF(this.modelUrl);
               if (!this.textureUrl.isEmpty()) {
                  class_1011 tex = WebImage.fetchTexture(this.textureUrl);
                  if (tex != null) {
                     onMc(() -> {
                        CactusModelLayerRegistry.registerSprite(this.textureIdentifier, tex);
                        CactusClient.getLogger().info("Custom model texture loaded: {}", this.textureIdentifier);
                        this.renderer = new JsonCosmeticRenderer(modelData, this.textureIdentifier);
                     });
                     return;
                  }
               }

               this.renderer = new JsonCosmeticRenderer(modelData, (class_2960)null);
            } catch (IllegalArgumentException | JsonParseException var3) {
               CactusClient.getLogger().error("Error loading / parsing model", var3);
            } catch (Exception var4) {
               CactusClient.getLogger().error("Unknown model loading error", var4);
            }

         });
      }

      public void drop() {
         CactusModelLayerRegistry.drop(this.textureIdentifier);
      }

      public void rebuildModelData() {
         if (this.renderer != null) {
            this.renderer = this.renderer.recreate();
         }

      }

      public void render(class_4587 matrices, class_11659 queue, class_10055 entity, class_591 entityModel, float tickDelta, int light) {
         this.ensureDataLoaded();
         if (this.renderer != null) {
            this.renderer.render(matrices, queue, entity, entityModel, tickDelta, light);
         }

      }

      public String getTextureUrl() {
         return this.textureUrl;
      }

      public String getModelUrl() {
         return this.modelUrl;
      }

      public String toString() {
         String var10000 = this.textureUrl;
         return "Model{textureUrl='" + var10000 + "', modelUrl='" + this.modelUrl + "', renderer=" + String.valueOf(this.renderer) + ", loader=" + String.valueOf(AbstractCosmetic.loader) + ", id='" + this.getId() + "', name='" + this.getName() + "', group='" + this.getGroup() + "'}";
      }
   }

   public static class Cape extends AbstractCosmetic<AbstractCosmetic.Cape> {
      private int FRAME_COUNTER = 0;
      private final String textureUrl;
      private final int animationSpeed;
      private final List<class_2960> frames = new ArrayList();

      public Cape(String id, String name, @Nullable String group, String url, int animationSpeed) {
         super(id, name, group);
         this.textureUrl = url;
         this.animationSpeed = animationSpeed;
      }

      public CosmeticParser getParser() {
         return CosmeticParser.CAPE;
      }

      public class_1792 getDisplayIcon() {
         return class_1802.field_18674;
      }

      public boolean isStandardEquippable() {
         return true;
      }

      public void fetch() {
         CactusClient.getLogger().info("Loading cape {} from {}", this.getId(), this.textureUrl);
         AbstractCosmetic.loader.submit(() -> {
            CopyOnWriteArrayList readFrames = new CopyOnWriteArrayList();

            try {
               byte[] texture = HttpUtils.fetchRaw(this.textureUrl);
               if (texture == null) {
                  return;
               }

               GifImageReader reader = new GifImageReader(new ByteArrayInputStream(texture));

               class_1011 image;
               for(class_1011 lastImage = null; reader.hasRemaining(); lastImage = image) {
                  GifFrame frame = reader.read();
                  if (lastImage != null) {
                     image = new class_1011(lastImage.method_4307(), lastImage.method_4323(), true);
                  } else {
                     image = new class_1011(frame.getWidth(), frame.getHeight(), true);
                  }

                  for(int y = 0; y < frame.getHeight(); ++y) {
                     for(int x = 0; x < frame.getWidth(); ++x) {
                        if (frame.getData()[y * frame.getWidth() + x] != 0) {
                           Color c = new Color(frame.getData()[y * frame.getWidth() + x], false);
                           image.method_4305(x, y, (new Color(c.getBlue(), c.getGreen(), c.getRed(), c.getAlpha())).getRGB());
                        } else if (lastImage != null) {
                           image.method_4305(x, y, lastImage.method_61940(x, y));
                        } else {
                           image.method_4305(x, y, (new Color(0, 0, 0, 0)).getRGB());
                        }
                     }
                  }

                  String var10001 = this.getId();
                  class_2960 identifier = class_2960.method_60655("cactus", "cape." + var10001 + "_" + this.FRAME_COUNTER++);
                  onMc(() -> {
                     class_1060 var10000 = CactusConstants.mc.method_1531();
                     Objects.requireNonNull(identifier);
                     var10000.method_4616(identifier, new class_1043(identifier::toString, image));
                  });
                  readFrames.add(identifier);
               }

               this.frames.addAll(readFrames);
            } catch (IOException var10) {
               CactusClient.getLogger().error("Failed to load cape", var10);
            }

         });
      }

      public void drop() {
         this.frames.forEach((identifier) -> {
            CactusConstants.mc.method_1531().method_4615(identifier);
         });
         this.frames.clear();
      }

      public class_2960 getCurrentTextureFrame() {
         this.ensureDataLoaded();
         return !this.frames.isEmpty() ? (class_2960)this.frames.get((int)(System.currentTimeMillis() / (long)this.animationSpeed % (long)this.frames.size())) : null;
      }

      public String getTextureUrl() {
         return this.textureUrl;
      }

      public int getAnimationSpeed() {
         return this.animationSpeed;
      }

      public String toString() {
         String var10000 = this.textureUrl;
         return "Cape{textureUrl='" + var10000 + "', animationSpeed=" + this.animationSpeed + ", id='" + this.getId() + "', name='" + this.getName() + "', group='" + this.getGroup() + "'}";
      }
   }
}
