package com.dwarslooper.cactus.client.feature.modules.render;

import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.ItemUtils;
import com.dwarslooper.cactus.client.util.game.render.RenderUtils;
import com.dwarslooper.cactus.client.util.generic.MathUtils;
import java.awt.Color;
import java.util.List;
import net.minecraft.class_1799;
import net.minecraft.class_332;

public class ItemHud extends Module {
   public static ItemHud instance;
   public Setting<Boolean> displayArmor;
   public Setting<Boolean> showDurability;
   public Setting<Boolean> renderOverChat;

   public ItemHud() {
      super("item_hud", ModuleManager.CATEGORY_RENDERING, (new Module.Options()).set(Module.Flag.HUD_LISTED, false).set(Module.Flag.EXPERIMENTAL, true));
      this.displayArmor = this.mainGroup.add(new BooleanSetting("armor", true));
      this.showDurability = this.mainGroup.add(new BooleanSetting("durability", false));
      this.renderOverChat = this.mainGroup.add(new BooleanSetting("overChat", false));
      instance = this;
   }

   public void render(class_332 context, float tickDelta, int width, int height) {
      context.method_51448().pushMatrix();
      if ((Boolean)this.displayArmor.get()) {
         if (!(Boolean)this.renderOverChat.get()) {
            context.method_51448().translate(0.0F, 0.0F);
         }

         List<class_1799> armor = ItemUtils.getPlayerArmor();
         armor.forEach((itemStack) -> {
            if (!itemStack.method_7960()) {
               int y = height - 4 - 16 * (armor.indexOf(itemStack) + 1);
               RenderUtils.drawItemWithMeta(context, itemStack, 4, y);
               if ((Boolean)this.showDurability.get()) {
                  context.method_25303(CactusConstants.mc.field_1772, MathUtils.quality(itemStack.method_7936() - itemStack.method_7919(), this.calcItemDamage(itemStack, 50), this.calcItemDamage(itemStack, 20), this.calcItemDamage(itemStack, 10), MathUtils.QualityMode.DECREMENT), 22, y + 4, Color.WHITE.getRGB());
               }

            }
         });
      }

      context.method_51448().popMatrix();
   }

   private int calcItemDamage(class_1799 stack, int percent) {
      return (int)((double)stack.method_7936() * ((double)percent / 100.0D));
   }
}
