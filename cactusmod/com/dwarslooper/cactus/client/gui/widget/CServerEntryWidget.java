package com.dwarslooper.cactus.client.gui.widget;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import com.dwarslooper.cactus.client.util.game.render.RenderUtils;
import com.dwarslooper.cactus.client.util.generic.MathUtils;
import com.google.common.collect.Lists;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.class_1011;
import net.minecraft.class_10799;
import net.minecraft.class_1144;
import net.minecraft.class_12239;
import net.minecraft.class_124;
import net.minecraft.class_155;
import net.minecraft.class_156;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_3532;
import net.minecraft.class_500;
import net.minecraft.class_5244;
import net.minecraft.class_5348;
import net.minecraft.class_5481;
import net.minecraft.class_6382;
import net.minecraft.class_642;
import net.minecraft.class_644;
import net.minecraft.class_8016;
import net.minecraft.class_8023;
import net.minecraft.class_8573;
import net.minecraft.class_642.class_8678;
import net.minecraft.class_642.class_9083;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CServerEntryWidget extends class_339 implements AutoCloseable {
   static final class_2561 CANNOT_RESOLVE_TEXT = class_2561.method_43471("multiplayer.status.cannot_resolve").method_27694((style) -> {
      return style.method_36139(-65536);
   });
   static final class_2561 CANNOT_CONNECT_TEXT = class_2561.method_43471("multiplayer.status.cannot_connect").method_27694((style) -> {
      return style.method_36139(-65536);
   });
   static final class_2561 INCOMPATIBLE_TEXT = class_2561.method_43471("multiplayer.status.incompatible");
   static final class_2561 NO_CONNECTION_TEXT = class_2561.method_43471("multiplayer.status.no_connection");
   static final class_2561 PINGING_TEXT = class_2561.method_43471("multiplayer.status.pinging");
   private final class_644 serverListPinger;
   private class_8573 icon;
   private byte[] favicon;
   private class_642 server;
   private final ExecutorService pingerService = Executors.newScheduledThreadPool(1);

   public CServerEntryWidget(int x, int y, int width, int height, class_500 mpParent) {
      super(x, y, width, height, class_2561.method_43473());
      this.serverListPinger = (class_644)Utils.orElse(mpParent, class_500::method_2538, new class_644());
   }

   @Nullable
   public class_8016 method_48205(@NotNull class_8023 navigation) {
      return super.method_48205(navigation);
   }

   public void update(String name, String address) {
      this.server = new class_642(name, address, class_8678.field_45611);
      this.icon = class_8573.method_52202(CactusConstants.mc.method_1531(), this.server.field_3761);
   }

   public void method_48579(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      if (this.server != null) {
         context.method_25294(this.method_46426() - 5, this.method_46427() - 5, this.method_46426() + this.method_25368() + 5, this.method_46427() + this.method_25364() + 5, -2013265920);
         if (this.server.method_55825() == class_9083.field_47880) {
            this.server.method_55824(class_9083.field_47881);
            this.server.field_3757 = class_5244.field_39003;
            this.server.field_3753 = class_5244.field_39003;
            this.pingerService.submit(() -> {
               try {
                  this.serverListPinger.method_3003(this.server, () -> {
                  }, () -> {
                     CactusConstants.mc.execute(() -> {
                        this.server.method_55824(this.protocolVersionMatches() ? class_9083.field_47884 : class_9083.field_47883);
                     });
                  }, class_12239.method_75867(CactusConstants.mc.field_1690.method_1639()));
               } catch (UnknownHostException var2) {
                  this.server.method_55824(class_9083.field_47882);
                  this.server.field_3757 = CANNOT_RESOLVE_TEXT;
               } catch (Exception var3) {
                  this.server.method_55824(class_9083.field_47882);
                  this.server.field_3757 = CANNOT_CONNECT_TEXT;
               }

            });
         }

         if (this.server.method_55825() == class_9083.field_47881) {
            this.serverListPinger.method_3000();
         }

         boolean protocolMismatch = this.server.method_55825() == class_9083.field_47883;
         class_2561 safeName = class_2561.method_43470(this.server.field_3752);
         class_2561 safeLabel = this.server.field_3757;
         class_2561 safePlayerCount = this.server.field_3753;
         context.method_51439(CactusConstants.mc.field_1772, safeName, this.method_46426() + 32 + 3, this.method_46427() + 1, -1, false);
         List<class_5481> descriptionMotd = CactusConstants.mc.field_1772.method_1728(safeLabel, this.method_25368() - 32 - 2);

         for(int i = 0; i < Math.min(descriptionMotd.size(), 2); ++i) {
            class_327 var10001 = CactusConstants.mc.field_1772;
            class_5481 var10002 = (class_5481)descriptionMotd.get(i);
            int var10003 = this.method_46426() + 32 + 3;
            int var10004 = this.method_46427() + 12;
            Objects.requireNonNull(CactusConstants.mc.field_1772);
            context.method_51430(var10001, var10002, var10003, var10004 + 9 * i, -8355712, false);
         }

         class_2561 playerVersionLabel = protocolMismatch ? this.server.field_3760.method_27661().method_27692(class_124.field_1061) : safePlayerCount;
         int playerLabelWidth = CactusConstants.mc.field_1772.method_27525((class_5348)playerVersionLabel);
         int rightAlignText = this.method_46426() + this.method_25368() - 2;
         String pingLabel = null;
         int dy;
         if (this.server.method_55825() == class_9083.field_47884) {
            int var10000 = (int)this.server.field_3758;
            pingLabel = MathUtils.quality(var10000, 80, 200, 400, MathUtils.QualityMode.INCREMENT) + "ms";
            if (pingLabel.length() > 10) {
               String var33 = pingLabel.substring(0, 10);
               pingLabel = var33.replaceAll("§$", "") + "...";
            }

            RenderUtils.drawTextAlignedRight(context, (class_2561)class_2561.method_43470(pingLabel), rightAlignText, this.method_46427() + 1, -8355712, false);
         } else if (this.server.method_55825() == class_9083.field_47881 || this.server.method_55825() == class_9083.field_47880) {
            int dotSize = 3;
            int spacing = 5;
            int dots = 5;
            dy = rightAlignText - dots * (dotSize + spacing);
            int baseY = this.method_46427() + 3;
            double t = (double)(class_156.method_658() % 1200L) / 1200.0D;

            for(int i = 0; i < dots; ++i) {
               double phase = t * 6.283185307179586D - (double)i * 0.9D;
               float v = (float)((Math.sin(phase) + 1.0D) * 0.5D);
               int g = class_3532.method_15340((int)(140.0F + v * 80.0F), 0, 255);
               int color = -16777216 | g << 16 | g << 8 | g;
               int yOffset = (int)(v * 2.0F);
               int x = dy + i * (dotSize + spacing);
               context.method_25294(x, baseY - yOffset, x + dotSize, baseY + dotSize - yOffset, color);
            }
         }

         int pingWidth = CactusConstants.mc.field_1772.method_1727(pingLabel);
         int labelPosRight = rightAlignText - pingWidth - 4;
         RenderUtils.drawTextAlignedRight(context, (class_2561)playerVersionLabel, rightAlignText - pingWidth - 4, this.method_46427() + 1, -8355712, false);
         List playerSummaryTooltip;
         if (protocolMismatch) {
            playerSummaryTooltip = this.server.field_3762;
         } else if (this.pingSuccessful()) {
            if (this.server.field_3758 < 0L) {
               playerSummaryTooltip = Collections.emptyList();
            } else {
               playerSummaryTooltip = this.server.field_3762;
            }
         } else {
            playerSummaryTooltip = Collections.emptyList();
         }

         byte[] bs = this.server.method_49306();
         if (!Arrays.equals(bs, this.favicon)) {
            if (this.uploadFavicon(bs)) {
               this.favicon = bs;
            } else {
               this.server.method_49305((byte[])null);
            }
         }

         this.drawIcon(context, this.method_46426(), this.method_46427(), this.icon.method_52201());
         dy = mouseY - this.method_46427();
         if (mouseX >= labelPosRight - playerLabelWidth && mouseX <= labelPosRight && dy >= 0 && dy <= 8) {
            context.method_71274(Lists.transform(playerSummaryTooltip, class_2561::method_30937), mouseX, mouseY);
         }

      }
   }

   public boolean pingSuccessful() {
      return this.server != null && (this.server.method_55825() == class_9083.field_47884 || this.server.method_55825() == class_9083.field_47883);
   }

   public class_642 getInfo() {
      return this.server;
   }

   private boolean protocolVersionMatches() {
      return this.server.field_3756 == class_155.method_16673().comp_4027();
   }

   protected void drawIcon(class_332 context, int x, int y, class_2960 textureId) {
      context.method_25290(class_10799.field_56883, textureId, x, y, 0.0F, 0.0F, 32, 32, 32, 32);
   }

   private boolean uploadFavicon(byte[] bytes) {
      if (bytes == null) {
         this.icon.method_52198();
      } else {
         try {
            this.icon.method_52199(class_1011.method_49277(bytes));
         } catch (Exception var3) {
            CactusClient.getLogger().error("Invalid icon for server {} ({})", new Object[]{this.server.field_3752, this.server.field_3761, var3});
            return false;
         }
      }

      return true;
   }

   public void method_25354(@NotNull class_1144 soundManager) {
   }

   protected void method_47399(@NotNull class_6382 builder) {
   }

   public class_642 getServer() {
      return this.server;
   }

   public void close() {
      this.icon.close();
   }
}
