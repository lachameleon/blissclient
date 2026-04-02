package com.dwarslooper.cactus.client.feature.content;

import com.dwarslooper.cactus.client.addon.v2.Addon;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import java.util.function.Consumer;
import net.minecraft.class_1792;
import net.minecraft.class_2477;
import net.minecraft.class_2960;

public class ContentPack {
   private Addon owner;
   private final String id;
   private final ContentPack.ActivationPolicy activationPolicy;
   private boolean enabled;
   private class_1792 icon;
   private Consumer<ContentPack> changedListener;

   public ContentPack(String id, ContentPack.ActivationPolicy activationPolicy) {
      this.id = id;
      this.activationPolicy = activationPolicy;
   }

   public ContentPack(String id, ContentPack.ActivationPolicy activationPolicy, class_1792 item) {
      this(id, activationPolicy);
      this.icon = item;
      if (activationPolicy == ContentPack.ActivationPolicy.ALWAYS_ENABLED) {
         this.enabled = true;
      }

   }

   public ContentPack(String id, ContentPack.ActivationPolicy activationPolicy, Consumer<ContentPack> changedListener) {
      this(id, activationPolicy);
      this.setChangedListener(changedListener);
   }

   public ContentPack(String id, ContentPack.ActivationPolicy activationPolicy, class_1792 item, Consumer<ContentPack> changedListener) {
      this(id, activationPolicy, item);
      this.setChangedListener(changedListener);
   }

   public void setChangedListener(Consumer<ContentPack> changedListener) {
      this.changedListener = changedListener;
      changedListener.accept(this);
   }

   public Consumer<ContentPack> getChangedListener() {
      return this.changedListener;
   }

   public String getId() {
      return this.owner != null ? class_2960.method_60655(this.owner.id(), this.id).toString() : this.id;
   }

   public String getName() {
      return class_2477.method_10517().method_48307("content_packs." + this.id + ".name");
   }

   public String getDescription() {
      return class_2477.method_10517().method_4679("content_packs." + this.id + ".description", (String)null);
   }

   public boolean matchesSearch(String search) {
      String description = this.getDescription();
      return Utils.matchesSearch(this.getName(), search) || description != null && Utils.matchesSearch(description, search);
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public ContentPack.ActivationPolicy getActivationPolicy() {
      return this.activationPolicy;
   }

   public class_1792 getIcon() {
      return this.icon;
   }

   public void setOwner(Addon owner) {
      this.owner = owner;
   }

   public String ownerOrDefault() {
      return this.owner != null ? this.owner.name() : CactusConstants.META.getName();
   }

   public void setEnabled(boolean enabled) {
      if (this.getActivationPolicy() != ContentPack.ActivationPolicy.ALWAYS_ENABLED || enabled) {
         this.enabled = enabled;
      }

      if (this.changedListener != null) {
         this.changedListener.accept(this);
      }

   }

   public static enum ActivationPolicy {
      ALWAYS_ENABLED,
      DEFAULT_ENABLED,
      DEFAULT_DISABLED;

      // $FF: synthetic method
      private static ContentPack.ActivationPolicy[] $values() {
         return new ContentPack.ActivationPolicy[]{ALWAYS_ENABLED, DEFAULT_ENABLED, DEFAULT_DISABLED};
      }
   }
}
