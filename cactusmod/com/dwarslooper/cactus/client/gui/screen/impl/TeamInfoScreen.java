package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.BackendPacketReceiveEvent;
import com.dwarslooper.cactus.client.gui.screen.EventHandlingCScreen;
import com.dwarslooper.cactus.client.gui.widget.StyledTextListWidget;
import com.dwarslooper.cactus.client.irc.protocol.PacketIn;
import com.dwarslooper.cactus.client.irc.protocol.packets.teams.TeamDataPacket;
import com.dwarslooper.cactus.client.systems.teams.ClientTeamData;
import com.dwarslooper.cactus.client.systems.teams.TeamDataType;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.PositionUtils;
import com.dwarslooper.cactus.client.util.game.render.RenderUtils;
import com.google.gson.JsonElement;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_124;
import net.minecraft.class_2338;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_4280;
import net.minecraft.class_5250;
import net.minecraft.class_5481;
import net.minecraft.class_4280.class_4281;
import org.jetbrains.annotations.NotNull;

public class TeamInfoScreen extends EventHandlingCScreen {
   private final ClientTeamData teamData;
   private boolean initialized;
   private StyledTextListWidget aboutInfoWidget;
   private TeamInfoScreen.TeamItemListWidget itemListWidget;

   public TeamInfoScreen(ClientTeamData teamData) {
      super("teamInfo");
      this.teamData = teamData;
   }

   public void method_25426() {
      super.method_25426();
      this.initialized = true;
      int var10004 = this.field_22789 / 2 - 8;
      int var10005 = this.field_22790 - 108 - 40;
      Objects.requireNonNull(CactusConstants.mc.field_1772);
      this.method_37063(this.aboutInfoWidget = new StyledTextListWidget(var10004, var10005, 108, 9 + 2));
      this.method_37063(this.itemListWidget = new TeamInfoScreen.TeamItemListWidget(this.field_22789 / 2 - 8, this.field_22790 - 108 - 40, 108, 36));
      this.itemListWidget.method_46421(this.field_22789 / 2 + 8);
      this.update();
   }

   @EventHandler
   public void onPacketReceive(BackendPacketReceiveEvent event) {
      if (this.initialized) {
         PacketIn var3 = event.packet();
         if (var3 instanceof TeamDataPacket) {
            TeamDataPacket teamDataPacket = (TeamDataPacket)var3;
            if (teamDataPacket.getTeamId() == this.teamData.getId()) {
               this.update();
            }
         }
      }

   }

   private void update() {
      if (!this.teamData.getInfo().isComplete()) {
         this.teamData.load(TeamDataType.INFO);
      }

      this.aboutInfoWidget.updateFromText(this.teamData.getInfo().about());
      if (this.teamData.getFiles() != null) {
         this.teamData.getFiles().forEach((fd) -> {
            this.itemListWidget.addEntry(new TeamInfoScreen.TeamItemListWidget.SchematicEntry(fd));
         });
      }

   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      int x = 0;
      int y = 68;
      int w = this.field_22789 / 2 - 8;
      context.method_51448().pushMatrix();
      context.method_51448().scale(2.0F, 2.0F);
      context.method_51445(this.teamData.getInfo().display().method_7854(), x + 2, 34);
      context.method_51448().popMatrix();
      context.method_25303(CactusConstants.mc.field_1772, this.teamData.getInfo().name(), x + 32 + 8, y + 1, -1);
      RenderUtils.drawTextAlignedRight(context, (class_2561)class_2561.method_43470("\ud83d\udc64 " + this.teamData.getInfo().members()).method_27692(class_124.field_1080), x + w, y + 1, -1, true);
      List<class_5481> list = CactusConstants.mc.field_1772.method_1728(class_2561.method_43470(this.teamData.getInfo().motd()), w - 32 - 8);

      for(int i = 0; i < Math.min(list.size(), 2); ++i) {
         context.method_35720(CactusConstants.mc.field_1772, (class_5481)list.get(i), x + 32 + 8, y + 12 + 9 * i, -8355712);
      }

   }

   public static class TeamItemListWidget extends class_4280<TeamInfoScreen.TeamItemListWidget.Entry> {
      public TeamItemListWidget(int width, int height, int y, int itemHeight) {
         super(CactusConstants.mc, width, height, y, itemHeight);
      }

      public int addEntry(TeamInfoScreen.TeamItemListWidget.Entry entry) {
         return super.method_25321(entry);
      }

      public int method_25322() {
         return this.field_22758 - 20;
      }

      protected int method_65507() {
         return this.method_46426() + this.method_25368() - 6;
      }

      public abstract static class Entry extends class_4281<TeamInfoScreen.TeamItemListWidget.Entry> {
         @NotNull
         public class_2561 method_37006() {
            return class_2561.method_43473();
         }
      }

      public static class SchematicEntry extends TeamInfoScreen.TeamItemListWidget.Entry {
         private final ClientTeamData.FileData fileData;
         private final class_2338 position;

         public SchematicEntry(ClientTeamData.FileData fileData) {
            if (!fileData.type().equalsIgnoreCase("SCHEMATIC")) {
               throw new IllegalArgumentException("File data is not of type 'schematic'");
            } else {
               this.fileData = fileData;
               this.position = new class_2338((Integer)fileData.getOption("x", Integer.MIN_VALUE, JsonElement::getAsInt), (Integer)fileData.getOption("y", 0, JsonElement::getAsInt), (Integer)fileData.getOption("z", 0, JsonElement::getAsInt));
            }
         }

         public void method_25343(@NotNull class_332 context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
         }

         public void render(class_332 context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.method_51439(CactusConstants.mc.field_1772, class_2561.method_43470(this.fileData.name()), x + 4, y + 1, -1, false);
            class_327 var10001 = CactusConstants.mc.field_1772;
            class_5250 var10002 = class_2561.method_43470("Position: " + PositionUtils.toString(this.position)).method_27692(class_124.field_1080);
            int var10003 = x + 4;
            int var10004 = y + 1;
            Objects.requireNonNull(CactusConstants.mc.field_1772);
            context.method_51439(var10001, var10002, var10003, var10004 + 9 + 2, -1, false);
            var10001 = CactusConstants.mc.field_1772;
            Date var11 = new Date(this.fileData.lastModified());
            var10002 = class_2561.method_43470("Last modified: " + String.valueOf(var11)).method_27692(class_124.field_1080);
            var10003 = x + 4;
            var10004 = y + 1;
            Objects.requireNonNull(CactusConstants.mc.field_1772);
            context.method_51439(var10001, var10002, var10003, var10004 + (9 + 2) * 2, -1, false);
         }
      }
   }
}
