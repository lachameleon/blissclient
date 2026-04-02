package com.dwarslooper.cactus.client.util.generic;

public enum WidgetIcons {
   CACTUS,
   CHAT,
   BOOK,
   LIGHT,
   JIGSAW,
   BACK_ARROW,
   SETTINGS,
   ACCOUNT_SWITCHER,
   MODULES,
   DIRECTORY,
   DELETE,
   EDIT,
   TOOLBOX,
   SERVER,
   HUD_EDITOR,
   RESET,
   MACROS,
   SCREENSHOTS,
   COSMETICS,
   COPY,
   PASTE,
   IMPORT,
   EXPORT,
   SAVE,
   LOAD,
   EMOTES;

   private final int offsetX = this.ordinal() * 20;

   public int offsetX() {
      return this.offsetX;
   }

   // $FF: synthetic method
   private static WidgetIcons[] $values() {
      return new WidgetIcons[]{CACTUS, CHAT, BOOK, LIGHT, JIGSAW, BACK_ARROW, SETTINGS, ACCOUNT_SWITCHER, MODULES, DIRECTORY, DELETE, EDIT, TOOLBOX, SERVER, HUD_EDITOR, RESET, MACROS, SCREENSHOTS, COSMETICS, COPY, PASTE, IMPORT, EXPORT, SAVE, LOAD, EMOTES};
   }
}
