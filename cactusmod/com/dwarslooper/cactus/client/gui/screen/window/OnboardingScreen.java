package com.dwarslooper.cactus.client.gui.screen.window;

import net.minecraft.class_332;
import org.jetbrains.annotations.NotNull;

public abstract class OnboardingScreen extends WindowScreen {
   public OnboardingScreen(int width, int height) {
      super("onboarding", width, height);
   }

   public static class PromptScreen extends OnboardingScreen {
      public PromptScreen() {
         super(200, 124);
      }

      public void method_25426() {
         super.method_25426();
      }

      public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
         super.method_25394(context, mouseX, mouseY, delta);
      }
   }
}
