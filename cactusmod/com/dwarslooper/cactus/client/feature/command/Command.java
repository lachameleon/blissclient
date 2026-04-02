package com.dwarslooper.cactus.client.feature.command;

import com.dwarslooper.cactus.client.gui.screen.ITranslatable;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.class_2170;
import net.minecraft.class_2172;
import net.minecraft.class_2477;
import net.minecraft.class_7157;
import net.minecraft.class_7887;

public abstract class Command implements ITranslatable {
   public static final int SINGLE_SUCCESS = 1;
   public static final class_7157 commandRegistryAccess = class_2170.method_46732(class_7887.method_46817());
   private final String command;

   protected static <T> RequiredArgumentBuilder<class_2172, T> argument(String name, ArgumentType<T> type) {
      return RequiredArgumentBuilder.argument(name, type);
   }

   protected static LiteralArgumentBuilder<class_2172> literal(String name) {
      return LiteralArgumentBuilder.literal(name);
   }

   public Command(String command) {
      this.command = command;
   }

   public final String getName() {
      return this.command;
   }

   public final String getDescription() {
      return class_2477.method_10517().method_4679(this.getKey() + ".description", class_2477.method_10517().method_4679("generic.no_description", "No description given"));
   }

   public String getKey() {
      return "commands." + this.command;
   }

   public abstract void build(LiteralArgumentBuilder<class_2172> var1);
}
