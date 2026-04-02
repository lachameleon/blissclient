package com.dwarslooper.cactus.client.irc.protocol.packets.security;

import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.toast.internal.GenericToast;
import com.dwarslooper.cactus.client.irc.IRCClient;
import com.dwarslooper.cactus.client.irc.protocol.BufferUtils;
import com.dwarslooper.cactus.client.irc.protocol.PacketIn;
import com.dwarslooper.cactus.client.irc.protocol.PacketOut;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_407;
import net.minecraft.class_4185;
import net.minecraft.class_5244;
import org.jetbrains.annotations.NotNull;

public class ServerCheckBiDPacket implements PacketIn, PacketOut {
   private String address;
   private boolean flag;
   private String typeName;
   private String description;
   private long reviewedAt;
   private int id;

   public ServerCheckBiDPacket(String address) {
      this.address = address;
   }

   public ServerCheckBiDPacket(ByteBuf buf) {
      this.flag = buf.readBoolean();
      if (this.flag) {
         this.typeName = BufferUtils.readString(buf);
         this.description = BufferUtils.readString(buf);
         this.reviewedAt = buf.readLong();
         this.id = buf.readInt();
      }

   }

   public void handle(IRCClient client) throws IOException {
      if (this.flag) {
         CactusConstants.mc.execute(() -> {
            GenericToast toast = new GenericToast(class_2561.method_43470("Server warning").method_27692(class_124.field_1061), class_2561.method_43470("This server has been deemed unsafe by our staff team. Reason: " + this.typeName), 10000L) {
               public boolean mouseClicked(double mouseX, double mouseY, int button) {
                  CactusConstants.mc.method_1507(new ServerCheckBiDPacket.ExplainerScreen(ServerCheckBiDPacket.this.typeName, ServerCheckBiDPacket.this.description, ServerCheckBiDPacket.this.reviewedAt, ServerCheckBiDPacket.this.id));
                  return true;
               }
            };
            toast.setStyle(GenericToast.Style.SYSTEM);
            CactusConstants.mc.method_1566().method_1999(toast);
         });
      }

   }

   public void write(ByteBuf buf) throws IOException {
      String encodedAddress = Utils.computeSecureEncoded(512, this.address.getBytes(StandardCharsets.UTF_8));
      BufferUtils.writeString(buf, encodedAddress);
   }

   public static class ExplainerScreen extends CScreen {
      private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
      private final String typeName;
      private final class_2561 description;
      private final String reviewedAt;
      private final int id;

      public ExplainerScreen(String typeName, String description, long reviewedAt, int id) {
         super("serverReportExplainer");
         this.typeName = typeName;
         this.description = class_2561.method_43470(description);
         this.reviewedAt = dateTimeFormatter.format(Instant.ofEpochMilli(reviewedAt).atZone(ZoneId.systemDefault()));
         this.id = id;
      }

      public void method_25426() {
         super.init(false);
         this.method_37063(class_4185.method_46430(class_5244.field_24334, (button) -> {
            this.method_25419();
         }).method_46434(this.field_22789 / 2 + 4, this.field_22790 / 2 + 100 + 8, 120, 20).method_46431());
         this.method_37063(class_4185.method_46430(class_2561.method_43470("Learn More"), class_407.method_49625(this, "https://cactusmod.xyz/go/?serverreports")).method_46434(this.field_22789 / 2 - 4 - 120, this.field_22790 / 2 + 100 + 8, 120, 20).method_46431());
      }

      public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float deltaTicks) {
         super.method_25394(context, mouseX, mouseY, deltaTicks);
         int left = this.field_22789 / 2 - 200;
         int top = this.field_22790 / 2 - 100;
         context.method_25294(left, top, left + 400, top + 200, -16777216);
         context.method_73198(left, top, 400, 200, -1);
         int textX = left + 4;
         int textYOrigin = top + 4;
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         int spacing = 9 + 2;
         context.method_25303(CactusConstants.mc.field_1772, "Report ID", textX, textYOrigin, -1);
         context.method_25303(CactusConstants.mc.field_1772, Integer.toString(this.id), textX, textYOrigin + spacing, -5592406);
         context.method_25303(CactusConstants.mc.field_1772, "Primary Report Reason", textX, textYOrigin + spacing * 3, -1);
         context.method_25303(CactusConstants.mc.field_1772, this.typeName, textX, textYOrigin + spacing * 4, -5592406);
         context.method_25303(CactusConstants.mc.field_1772, "Reviewed At", textX, textYOrigin + spacing * 6, -1);
         context.method_25303(CactusConstants.mc.field_1772, this.reviewedAt, textX, textYOrigin + spacing * 7, -5592406);
         context.method_25303(CactusConstants.mc.field_1772, "Description", textX, textYOrigin + spacing * 9, -1);
         context.method_65179(CactusConstants.mc.field_1772, this.description, textX, textYOrigin + spacing * 10, 392, -5592406);
      }
   }
}
