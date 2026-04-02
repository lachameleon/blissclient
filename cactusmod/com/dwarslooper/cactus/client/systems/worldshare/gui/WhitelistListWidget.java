package com.dwarslooper.cactus.client.systems.worldshare.gui;

import com.dwarslooper.cactus.client.gui.widget.CTextureButtonWidget;
import com.dwarslooper.cactus.client.systems.ias.account.Auth;
import com.dwarslooper.cactus.client.systems.ias.skins.SkinHelper;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_364;
import net.minecraft.class_4265;
import net.minecraft.class_6379;
import net.minecraft.class_7532;
import net.minecraft.class_4265.class_4266;
import org.jetbrains.annotations.NotNull;

public class WhitelistListWidget extends class_4265<WhitelistListWidget.WhitelistEntry> {
   private static final Map<String, UUID> UUID_CACHE = new WeakHashMap();
   public Consumer<List<WhitelistListWidget.WhitelistEntry>> callback;

   public WhitelistListWidget(WhitelistScreen parent, Consumer<List<WhitelistListWidget.WhitelistEntry>> callback) {
      super(CactusConstants.mc, parent.field_22789, parent.field_22790 - 80 - 40, 80, 20);
      this.callback = callback;
   }

   public void add(String username) {
      this.method_44399(new WhitelistListWidget.WhitelistEntry(username));
   }

   public void addAll(List<String> list) {
      list.forEach(this::add);
   }

   public boolean has(String username) {
      return this.method_25396().stream().anyMatch((whitelistEntry) -> {
         return whitelistEntry.getName().equalsIgnoreCase(username);
      });
   }

   public void remove(String username) {
      this.method_25396().stream().filter((e) -> {
         return e.getName().equalsIgnoreCase(username);
      }).findFirst().ifPresent(this::remove);
   }

   private void remove(WhitelistListWidget.WhitelistEntry entry) {
      this.method_25330(entry);
      this.callback.accept(this.method_25396());
   }

   public class WhitelistEntry extends class_4266<WhitelistListWidget.WhitelistEntry> {
      private static final UUID EMPTY_UUID = new UUID(0L, 0L);
      private final String name;
      private final class_339 removeButton = (new CTextureButtonWidget.Builder((button) -> {
         WhitelistListWidget.this.remove(this);
      })).uv(201, 1).dimensions(18, 18).build();

      public WhitelistEntry(String name) {
         this.name = name;
         CompletableFuture.supplyAsync(() -> {
            return Auth.resolveUUID(name);
         }).thenAccept((id) -> {
            WhitelistListWidget.UUID_CACHE.putIfAbsent(this.name, id);
         });
      }

      public void method_25343(@NotNull class_332 context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
         int x = this.method_46426();
         int y = this.method_46427();
         int entryWidth = this.method_25368();
         class_7532.method_52722(context, SkinHelper.getCachedSkinOrFetch((UUID)WhitelistListWidget.UUID_CACHE.getOrDefault(this.name, EMPTY_UUID)), x, y, 16);
         this.removeButton.method_48229(x + entryWidth - 20 - 1, y - 1);
         this.removeButton.method_25394(context, mouseX, mouseY, tickDelta);
         context.method_51439(CactusConstants.mc.field_1772, class_2561.method_43470(this.name), x + 24, y + 3, -1, true);
      }

      @NotNull
      public List<? extends class_364> method_25396() {
         return ImmutableList.of(this.removeButton);
      }

      @NotNull
      public List<? extends class_6379> method_37025() {
         return ImmutableList.of(this.removeButton);
      }

      public String getName() {
         return this.name;
      }
   }
}
