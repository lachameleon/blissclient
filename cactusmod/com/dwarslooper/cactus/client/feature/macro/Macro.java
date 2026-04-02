package com.dwarslooper.cactus.client.feature.macro;

import com.dwarslooper.cactus.client.systems.key.KeyBind;
import java.util.ArrayList;
import java.util.List;

public class Macro {
   public KeyBind keyBinding;
   public String name;
   public boolean enabled;
   public List<Action<?>> actions;

   public static Macro empty() {
      return new Macro("", KeyBind.none(), new ArrayList(), true);
   }

   public Macro(String name, KeyBind keyBinding, List<Action<?>> actions, boolean enabled) {
      this.name = name;
      this.keyBinding = keyBinding;
      this.actions = new ArrayList(actions);
      this.enabled = enabled;
   }

   public void run() {
      if (this.enabled) {
         this.actions.forEach(Action::run);
      }

   }

   public void runWithArguments(String... arguments) {
      if (this.enabled) {
         this.actions.stream().filter((action) -> {
            boolean var10000;
            if (action.type() == Action.Type.RUN_COMMAND) {
               Object patt0$temp = action.data();
               if (patt0$temp instanceof String) {
                  String s = (String)patt0$temp;
                  var10000 = true;
                  return var10000;
               }
            }

            var10000 = false;
            return var10000;
         }).forEach((action) -> {
            action.runMapped((o) -> {
               return o;
            });
         });
      }

   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public KeyBind getKeyBinding() {
      return this.keyBinding;
   }

   public void setKeyBinding(KeyBind keyBinding) {
      this.keyBinding = keyBinding;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public List<Action<?>> getActions() {
      return this.actions;
   }
}
