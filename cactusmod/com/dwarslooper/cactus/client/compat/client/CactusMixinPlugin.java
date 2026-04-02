package com.dwarslooper.cactus.client.compat.client;

import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class CactusMixinPlugin implements IMixinConfigPlugin {
   private final Logger logger = LogManager.getLogger("Cactus / Prelaunch");
   private static final Supplier<Boolean> TRUE = () -> {
      return true;
   };
   private static final Map<String, Supplier<Boolean>> CONDITIONS = ImmutableMap.of("com.dwarslooper.cactus.client.mixins.client.ChatHudMixin$ChatHudExtendHistoryMixin", () -> {
      return !FabricLoader.getInstance().isModLoaded("feather") && !FabricLoader.getInstance().isModLoaded("morechathistory");
   }, "com.dwarslooper.cactus.client.mixins.render.PlayerListHudMixin", () -> {
      return !FabricLoader.getInstance().isModLoaded("feather");
   }, "com.dwarslooper.cactus.client.mixins.client.MinecraftMainMixin", () -> {
      return !FabricLoader.getInstance().isModLoaded("afix");
   }, "com.dwarslooper.cactus.client.mixins.client.HotbarStorageMixin", () -> {
      return !FabricLoader.getInstance().isModLoaded("librarian");
   }, "com.dwarslooper.cactus.client.mixins.render.sodium.SodiumWorldRendererMixin", () -> {
      return FabricLoader.getInstance().isModLoaded("sodium");
   });

   public void onLoad(String mixinPackage) {
      FabricLoader var2 = FabricLoader.getInstance();

      try {
         Class<?> configClass = Class.forName("org.spongepowered.asm.mixin.transformer.MixinConfig");
         Field field = configClass.getDeclaredField("globalMixinList");
         if (field.trySetAccessible()) {
            Set<String> globalMixinList = (Set)field.get((Object)null);
            field.set((Object)null, globalMixinList);
         } else {
            this.logger.error("Can't set field accessible to force-disable foreign mixins");
         }
      } catch (Exception var6) {
      }

   }

   public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
      boolean allow = (Boolean)((Supplier)CONDITIONS.getOrDefault(mixinClassName, TRUE)).get();
      if (!allow) {
         this.logger.info("Disabling mixin {} because of incompatibility", mixinClassName);
      }

      return allow;
   }

   public String getRefMapperConfig() {
      return null;
   }

   public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
   }

   public List<String> getMixins() {
      return null;
   }

   public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }

   public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }
}
