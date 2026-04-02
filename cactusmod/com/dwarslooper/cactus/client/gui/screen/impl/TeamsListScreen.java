package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.gui.screen.EventHandlingCScreen;
import com.dwarslooper.cactus.client.systems.teams.ClientTeamData;
import com.dwarslooper.cactus.client.systems.teams.TeamManager;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.render.RenderUtils;
import java.util.List;
import net.minecraft.class_11909;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_4280;
import net.minecraft.class_5481;
import net.minecraft.class_4280.class_4281;
import org.jetbrains.annotations.NotNull;

public class TeamsListScreen extends EventHandlingCScreen {
   private TeamsListScreen.Widget widget;

   public TeamsListScreen() {
      super("teamsList");
   }

   public void method_25426() {
      super.method_25426();
      this.method_37063(this.widget = new TeamsListScreen.Widget(this.field_22789, this.field_22790 - 80, 40));
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
   }

   public static class Widget extends class_4280<TeamsListScreen.Widget.TeamEntry> {
      public Widget(int width, int height, int y) {
         super(CactusConstants.mc, width, height, y, 36);
         TeamManager.CACHE.asMap().values().forEach((teamData) -> {
            this.method_25321(new TeamsListScreen.Widget.TeamEntry(teamData));
         });
      }

      public int method_25322() {
         return 305;
      }

      public static class TeamEntry extends class_4281<TeamsListScreen.Widget.TeamEntry> {
         private final int teamId;
         private final ClientTeamData.Info teamInfo;

         public TeamEntry(ClientTeamData teamData) {
            this.teamId = teamData.getId();
            this.teamInfo = teamData.getInfo();
         }

         @NotNull
         public class_2561 method_37006() {
            return class_2561.method_43473();
         }

         public void method_25343(@NotNull class_332 context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
         }

         public void render(class_332 context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.method_51448().pushMatrix();
            context.method_51448().scale(2.0F, 2.0F);
            context.method_51445(this.teamInfo.display().method_7854(), x / 2, y / 2);
            context.method_51448().popMatrix();
            context.method_51433(CactusConstants.mc.field_1772, this.teamInfo.name(), x + 32 + 3, y + 1, -1, false);
            RenderUtils.drawTextAlignedRight(context, (class_2561)class_2561.method_43470("\ud83d\udc64 " + this.teamInfo.members()).method_27692(class_124.field_1080), x + entryWidth - 6, y + 1, -1, false);
            List<class_5481> list = CactusConstants.mc.field_1772.method_1728(class_2561.method_43470(this.teamInfo.motd()), entryWidth - 32 - 2);

            for(int i = 0; i < Math.min(list.size(), 2); ++i) {
               context.method_51430(CactusConstants.mc.field_1772, (class_5481)list.get(i), x + 32 + 3, y + 12 + 9 * i, -8355712, false);
            }

         }

         public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
            CactusConstants.mc.method_1507(new TeamInfoScreen((ClientTeamData)TeamManager.CACHE.getIfPresent(this.teamId)));
            return true;
         }
      }
   }
}
