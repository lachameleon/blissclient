package com.dwarslooper.cactus.client.feature.commands;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.feature.command.Command;
import com.dwarslooper.cactus.client.render.RenderHelper;
import com.dwarslooper.cactus.client.render.RenderableObject;
import com.dwarslooper.cactus.client.render.objects.BoxObject;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.generic.ColorUtils;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.awt.Color;
import net.minecraft.class_2172;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_370;
import net.minecraft.class_370.class_9037;

public class MoveBox extends Command {
   public static final float ZFF = 0.0F;

   public MoveBox() {
      super("box");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder.then(literal("spawn").then(argument("pos", LongArgumentType.longArg(0L, 10000L)).executes((context) -> {
         long time = LongArgumentType.getLong(context, "pos");
         class_243 antiZFight = new class_243(0.0D, 0.0D, 0.0D);
         BoxObject boxObject = new BoxObject(ColorUtils.withAlpha(new Color(CactusClient.getInstance().getRGB()), 0.0F), RenderableObject.RenderMode.Permanent, new class_238(class_243.method_24954(CactusConstants.mc.field_1724.method_24515()).method_1020(antiZFight), class_243.method_24954(CactusConstants.mc.field_1724.method_24515().method_10069(1, 1, 1)).method_1019(antiZFight)), RenderableObject.BoxMode.Both);
         if (time != 0L) {
            boxObject.timed(time);
         }

         boxObject.build();
         return 1;
      })))).then(literal("clear").executes((context) -> {
         RenderHelper.hitBoxes.clear();
         return 1;
      }))).then(literal("show").executes((context) -> {
         class_370 warning = class_370.method_29047(CactusConstants.mc, class_9037.field_47589, class_2561.method_30163("Warning: Suspicious Server"), class_2561.method_30163("You have joined a server that has been flagged as potentially pay-to-win or a scam. Consider verifying the server's legitimacy before making any purchases or commitments."));
         CactusConstants.mc.method_1566().method_1999(warning);
         return 1;
      }));
   }
}
