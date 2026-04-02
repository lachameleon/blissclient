package com.dwarslooper.cactus.client.feature.commands;

import com.dwarslooper.cactus.client.event.CRunnableClickEvent;
import com.dwarslooper.cactus.client.feature.command.Command;
import com.dwarslooper.cactus.client.feature.content.ContentPackDependent;
import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.systems.key.KeyBind;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.class_2172;
import net.minecraft.class_2561;
import net.minecraft.class_2568.class_10613;

@ContentPackDependent("cactus")
public class BindsCommand extends Command {
   public BindsCommand() {
      super("binds");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      ((LiteralArgumentBuilder)builder.executes((context) -> {
         Map<String, KeyBind> binds = new HashMap();
         Iterator var2 = ModuleManager.get().getModules().values().iterator();

         while(var2.hasNext()) {
            Module module = (Module)var2.next();
            if (module.getBind().isBound()) {
               binds.put(module.getDisplayName(), module.getBind());
            }
         }

         if (!binds.isEmpty()) {
            ChatUtils.info((class_2561)class_2561.method_43471("commands.binds.header"));
            binds.forEach((name, bind) -> {
               ChatUtils.info("§e" + name + "§8: §a" + bind.getDisplay());
            });
         } else {
            ChatUtils.error((class_2561)class_2561.method_43471("commands.binds.noBinds"));
         }

         return 1;
      })).then(literal("clear").executes((context) -> {
         ChatUtils.warning((class_2561)class_2561.method_43471("commands.binds.clear.header"));
         ChatUtils.warning((class_2561)class_2561.method_43471("commands.binds.clear.confirm").method_27694((style) -> {
            return style.method_10958(new CRunnableClickEvent(() -> {
               Iterator var0 = ModuleManager.get().getModules().values().iterator();

               while(var0.hasNext()) {
                  Module value = (Module)var0.next();
                  value.setBind(KeyBind.none());
               }

               ChatUtils.info((class_2561)class_2561.method_43471("commands.binds.clear.success"));
            })).method_10949(new class_10613(class_2561.method_43471("commands.binds.clear.hover")));
         }));
         return 1;
      }));
   }
}
