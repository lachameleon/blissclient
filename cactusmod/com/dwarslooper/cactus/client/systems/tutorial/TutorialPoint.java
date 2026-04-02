package com.dwarslooper.cactus.client.systems.tutorial;

import com.dwarslooper.cactus.client.feature.content.impl.ContentPackScreen;
import com.dwarslooper.cactus.client.gui.screen.impl.AccountListScreen;
import com.dwarslooper.cactus.client.gui.screen.impl.CactusMainScreen;
import com.dwarslooper.cactus.client.gui.screen.impl.CactusSettingsScreen;
import com.dwarslooper.cactus.client.gui.screen.impl.ModuleListScreen;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.generic.ScreenUtils;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.class_2561;
import net.minecraft.class_339;
import net.minecraft.class_437;
import net.minecraft.class_442;

public enum TutorialPoint {
   TITLE_SCREEN(class_442::new, (s) -> {
      class_339 cactusButton = ScreenUtils.getButton(s, "gui.screen.cactus.title");
      return cactusButton == null ? Collections.emptyList() : List.of(new TutorialGuide.Highlight(cactusButton.method_46426(), cactusButton.method_46427(), cactusButton.method_25368(), cactusButton.method_25364(), class_2561.method_43470("This is the cactus widget. From here, you can access all of cactus' features and settings.")));
   }, class_2561.method_43470("Title Screen"), class_2561.method_43470("The Title screen holds the small cactus widget")),
   MAIN_UI(() -> {
      return new CactusMainScreen((class_437)null);
   }, (s) -> {
      return List.of(new TutorialGuide.Highlight(6, 6, 20, 20, class_2561.method_43470("This button is in almost every one of Cactus' Screens and is used to go back to the previous Screen")));
   }, class_2561.method_43470("The main Screen"), class_2561.method_43470("This is the main Screen of Cactus. From here you can access all settings, modules and other features.")),
   SETTINGS(() -> {
      return new CactusSettingsScreen((class_437)null);
   }, (s) -> {
      return Collections.emptyList();
   }, class_2561.method_43470("Cactus Settings"), class_2561.method_43470("This is the main Settings screen. All Settings UIs of Cactus have this layout")),
   CONTENT_PACKS(ContentPackScreen::new, (s) -> {
      return List.of();
   }, class_2561.method_43470("Content packs"), class_2561.method_43470("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.")),
   ACCOUNT_SWITCHER(() -> {
      return new AccountListScreen((class_437)null);
   }, (s) -> {
      return List.of(new TutorialGuide.Highlight(s.field_22789 - 208, s.field_22790 - 24, 100, 20, class_2561.method_43470("If you happen to have a valid access token for any Minecraft account, you can enter it here to directly log in. This can be useful for testing or development purposes.")), new TutorialGuide.Highlight(s.field_22789 - 312, s.field_22790 - 24, 100, 20, class_2561.method_43470("You can use this to set an offline session, which means that your profile is not authorized with Mojang and you will only be able to play Singleplayer or on servers which don't have online mode disabled, tho such servers are against the EULA of Minecraft.")));
   }, class_2561.method_43470("Account switcher"), class_2561.method_43470("Lorem ipsum dolor sit amet")),
   MODULES(ModuleListScreen::new, (s) -> {
      return List.of(new TutorialGuide.Highlight(0, 32, 114, s.field_22790 - 72, class_2561.method_43470("This is actually a button. WOW SO AWESOME!")));
   }, class_2561.method_43470("Cactus' Modules"), class_2561.method_43470("Lorem ipsum dolor sit amet"));

   private final Supplier<class_437> view;
   public final Function<class_437, List<TutorialGuide.Highlight>> highlights;
   public final class_2561 title;
   public final class_2561 description;

   private TutorialPoint(Supplier<class_437> view, Function<class_437, List<TutorialGuide.Highlight>> highlights, class_2561 title, class_2561 description) {
      this.view = view;
      this.highlights = highlights;
      this.title = title;
      this.description = description;
   }

   public class_437 getView() {
      class_437 screen = (class_437)this.view.get();
      screen.method_25423(CactusConstants.mc.method_22683().method_4486(), CactusConstants.mc.method_22683().method_4502());
      return screen;
   }

   public List<TutorialGuide.Highlight> getHighlights(class_437 view) {
      return (List)this.highlights.apply(view);
   }

   // $FF: synthetic method
   private static TutorialPoint[] $values() {
      return new TutorialPoint[]{TITLE_SCREEN, MAIN_UI, SETTINGS, CONTENT_PACKS, ACCOUNT_SWITCHER, MODULES};
   }
}
