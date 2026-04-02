package com.dwarslooper.cactus.client.systems.profile;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.gui.screen.impl.CosmeticsListScreen;
import com.dwarslooper.cactus.client.irc.protocol.packets.UserInfoRequestC2SPacket;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.mixinterface.IAvatarRenderState;
import com.dwarslooper.cactus.client.util.mixinterface.IEntityRenderState;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.class_10055;
import net.minecraft.class_742;
import org.jetbrains.annotations.Nullable;

public class ProfileHandler {
   private static final Set<AbstractCosmetic<?>> clientAvailableCosmetics = new LinkedHashSet();
   private static final Cache<UUID, ProfileHandler> CACHED_PROFILES;
   private final UUID uuid;
   private boolean isCactus = false;
   @Nullable
   private String rank;
   private CosmeticList cosmetics;

   public static Set<AbstractCosmetic<?>> getClientAvailableCosmetics(boolean skipNonEquippable) {
      return !skipNonEquippable ? clientAvailableCosmetics : (Set)clientAvailableCosmetics.stream().filter(ICosmetic::isStandardEquippable).collect(Collectors.toCollection(LinkedHashSet::new));
   }

   public static void setClientAvailableCosmetics(Collection<AbstractCosmetic<?>> cosmetics) {
      clientAvailableCosmetics.clear();
      clientAvailableCosmetics.addAll(cosmetics);
   }

   public ProfileHandler(UUID uuid) {
      this.uuid = uuid;
      this.refresh();
   }

   private void refresh() {
      CactusClient.getInstance().getIrcClient().sendPacket(new UserInfoRequestC2SPacket(this.uuid, false));
   }

   public void unsetAllCosmetics() {
      this.updateCosmetics((CosmeticList)null);
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public void updateCosmetics(CosmeticList list) {
      this.cosmetics = list;
   }

   public CosmeticList getCosmetics() {
      return this.cosmetics;
   }

   public void ifCosmeticsPresent(Consumer<CosmeticList> callback) {
      if (this.cosmetics != null) {
         callback.accept(this.cosmetics);
      }

   }

   public void setProfileState(boolean cactus, @Nullable String rank) {
      this.isCactus = cactus;
      this.rank = rank;
   }

   public boolean isCactus() {
      return this.isCactus;
   }

   @Nullable
   public String getRank() {
      return this.rank;
   }

   public static ProfileHandler from(class_10055 renderState) {
      ProfileHandler override = ((IAvatarRenderState)renderState).cactus$getProfileHandler();
      return override != null ? override : fromProfile(((IEntityRenderState)renderState).cactus$getUUID());
   }

   public static ProfileHandler from(class_742 entity) {
      if (entity instanceof CosmeticsListScreen.PreviewPlayer) {
         CosmeticsListScreen.PreviewPlayer player = (CosmeticsListScreen.PreviewPlayer)entity;
         return player.getProfileOverride();
      } else {
         return fromProfile(entity.method_5667());
      }
   }

   public static ProfileHandler fromProfile(UUID id) {
      return id == null ? null : (ProfileHandler)CACHED_PROFILES.asMap().computeIfAbsent(id, ProfileHandler::new);
   }

   public static ProfileHandler me() {
      return fromProfile(CactusConstants.mc.method_1548().method_44717());
   }

   public static boolean exists(UUID uuid) {
      return CACHED_PROFILES.asMap().containsKey(uuid);
   }

   public static boolean hasCape(UUID uuid) {
      return CACHED_PROFILES.asMap().containsKey(uuid) && ((ProfileHandler)CACHED_PROFILES.asMap().get(uuid)).getCosmetics() != null;
   }

   public static void handleUpdatePacket(UUID profile, CosmeticList cosmetics) {
      ProfileHandler handler = fromProfile(profile);
      handler.unsetAllCosmetics();
      if (!cosmetics.isEmpty()) {
         handler.updateCosmetics(cosmetics);
      }
   }

   public static void invalidateProfiles() {
      CACHED_PROFILES.invalidateAll();
      CACHED_PROFILES.cleanUp();
   }

   public static void invalidateAll() {
      clientAvailableCosmetics.clear();
      invalidateProfiles();
      AbstractCosmetic.invalidate();
   }

   static {
      CACHED_PROFILES = CacheBuilder.newBuilder().expireAfterAccess(20L, TimeUnit.MINUTES).removalListener((n) -> {
      }).build();
   }
}
