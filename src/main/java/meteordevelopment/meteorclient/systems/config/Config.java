package meteordevelopment.meteorclient.systems.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import dev.stardust.modules.AdBlocker;
import dev.stardust.modules.AutoDoors;
import dev.stardust.modules.BannerData;
import dev.stardust.modules.Loadouts;
import dev.stardust.modules.LoreLocator;
import dev.stardust.modules.Minesweeper;
import dev.stardust.modules.MusicTweaks;
import dev.stardust.modules.RoadTrip;
import dev.stardust.modules.SignatureSign;
import dev.stardust.modules.StashBrander;
import meteordevelopment.meteorclient.MeteorClient;
import static meteordevelopment.meteorclient.MeteorClient.mc;
import meteordevelopment.meteorclient.renderer.Fonts;
import meteordevelopment.meteorclient.renderer.text.FontFace;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.FontFaceSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.ModuleListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.AutoReconnect;
import meteordevelopment.meteorclient.systems.modules.misc.AutoSleep;
import meteordevelopment.meteorclient.systems.modules.misc.BetterChat;
import meteordevelopment.meteorclient.systems.modules.misc.DiscordPresence;
import meteordevelopment.meteorclient.systems.modules.misc.InventoryTweaks;
import meteordevelopment.meteorclient.systems.modules.misc.Notebot;
import meteordevelopment.meteorclient.systems.modules.misc.Notifier;
import meteordevelopment.meteorclient.systems.modules.misc.SoundBlocker;
import meteordevelopment.meteorclient.systems.modules.movement.AutoWalk;
import meteordevelopment.meteorclient.systems.modules.movement.AutoWasp;
import meteordevelopment.meteorclient.systems.modules.movement.Sprint;
import meteordevelopment.meteorclient.systems.modules.player.AntiAFK;
import meteordevelopment.meteorclient.systems.modules.player.AutoEat;
import meteordevelopment.meteorclient.systems.modules.player.AutoFish;
import meteordevelopment.meteorclient.systems.modules.player.AutoGap;
import meteordevelopment.meteorclient.systems.modules.player.AutoMend;
import meteordevelopment.meteorclient.systems.modules.player.AutoReplenish;
import meteordevelopment.meteorclient.systems.modules.player.AutoRespawn;
import meteordevelopment.meteorclient.systems.modules.player.AutoTool;
import meteordevelopment.meteorclient.systems.modules.player.BreakDelay;
import meteordevelopment.meteorclient.systems.modules.player.ChestSwap;
import meteordevelopment.meteorclient.systems.modules.player.EXPThrower;
import meteordevelopment.meteorclient.systems.modules.player.LiquidInteract;
import meteordevelopment.meteorclient.systems.modules.player.MiddleClickExtra;
import meteordevelopment.meteorclient.systems.modules.player.NameProtect;
import meteordevelopment.meteorclient.systems.modules.player.Portals;
import meteordevelopment.meteorclient.systems.modules.render.BetterTooltips;
import meteordevelopment.meteorclient.systems.modules.render.BossStack;
import meteordevelopment.meteorclient.systems.modules.render.CameraTweaks;
import meteordevelopment.meteorclient.systems.modules.render.Fullbright;
import meteordevelopment.meteorclient.systems.modules.render.Grid;
import meteordevelopment.meteorclient.systems.modules.render.HandView;
import meteordevelopment.meteorclient.systems.modules.render.ItemHighlight;
import meteordevelopment.meteorclient.systems.modules.render.ItemPhysics;
import meteordevelopment.meteorclient.systems.modules.render.LightOverlay;
import meteordevelopment.meteorclient.systems.modules.render.Nametags;
import meteordevelopment.meteorclient.systems.modules.render.TimeChanger;
import meteordevelopment.meteorclient.systems.modules.render.Zoom;
import meteordevelopment.meteorclient.systems.modules.render.ZoomPlus;
import meteordevelopment.meteorclient.systems.modules.world.AutoBreed;
import meteordevelopment.meteorclient.systems.modules.world.AutoBrewer;
import meteordevelopment.meteorclient.systems.modules.world.AutoMount;
import meteordevelopment.meteorclient.systems.modules.world.AutoNametag;
import meteordevelopment.meteorclient.systems.modules.world.AutoShearer;
import meteordevelopment.meteorclient.systems.modules.world.AutoSign;
import meteordevelopment.meteorclient.systems.modules.world.AutoSmelter;
import meteordevelopment.meteorclient.systems.modules.world.DoubleDoorsInteract;
import meteordevelopment.meteorclient.systems.modules.world.HighwayBuilder;
import meteordevelopment.meteorclient.systems.modules.world.LiquidFiller;
import meteordevelopment.meteorclient.systems.modules.world.NoGhostBlocks;
import meteordevelopment.meteorclient.systems.modules.world.SpawnProofer;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

public class Config extends System<Config> {
    public final Settings settings = new Settings();

    private final SettingGroup sgVisual  = settings.createGroup("Visual");
    private final SettingGroup sgModules = settings.createGroup("Modules");
    private final SettingGroup sgChat    = settings.createGroup("Chat");
    private final SettingGroup sgMisc    = settings.createGroup("Misc");

    // ================================
    // VISUAL SETTINGS
    // ================================

    public final Setting<Boolean> customFont = sgVisual.add(new BoolSetting.Builder()
        .name("custom-font")
        .description("Use a custom font.")
        .defaultValue(true)
        .build()
    );

    public final Setting<FontFace> font = sgVisual.add(new FontFaceSetting.Builder()
        .name("font")
        .description("Custom font to use.")
        .visible(customFont::get)
        .onChanged(Fonts::load)
        .build()
    );

    public final Setting<Double> rainbowSpeed = sgVisual.add(new DoubleSetting.Builder()
        .name("rainbow-speed")
        .description("The global rainbow speed.")
        .defaultValue(0.5)
        .range(0, 10)
        .sliderMax(5)
        .build()
    );

    public final Setting<Boolean> titleScreenCredits = sgVisual.add(new BoolSetting.Builder()
        .name("title-screen-credits")
        .description("Show Meteor credits on title screen.")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> titleScreenSplashes = sgVisual.add(new BoolSetting.Builder()
        .name("title-screen-splashes")
        .description("Show Meteor splash texts on title screen.")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> customWindowTitle = sgVisual.add(new BoolSetting.Builder()
        .name("custom-window-title")
        .description("Show custom text in the window title.")
        .defaultValue(false)
        .onModuleActivated(setting -> mc.updateWindowTitle())
        .onChanged(v -> mc.updateWindowTitle())
        .build()
    );

    public final Setting<String> customWindowTitleText = sgVisual.add(new StringSetting.Builder()
        .name("window-title-text")
        .description("The text displayed in the window title.")
        .visible(customWindowTitle::get)
        .defaultValue("Bliss Client {mc_version} - based on Meteor, adapted by lachameleon")
        .onChanged(v -> mc.updateWindowTitle())
        .build()
    );

    public final Setting<SettingColor> friendColor = sgVisual.add(new ColorSetting.Builder()
        .name("friend-color")
        .description("The color used to show friends.")
        .defaultValue(new SettingColor(0, 255, 180))
        .build()
    );

    public final Setting<Boolean> syncListSettingWidths = sgVisual.add(new BoolSetting.Builder()
        .name("sync-list-setting-widths")
        .description("Prevents the list setting screens from moving around as you add & remove elements.")
        .defaultValue(false)
        .build()
    );

    // Modules

    public final Setting<List<Module>> hiddenModules = sgModules.add(new ModuleListSetting.Builder()
        .name("hidden-modules")
        .description("Modules hidden from the click GUI.")
        .defaultValue(new ArrayList<>())   // SAFE — filled later
        .visible(() -> false)
        .build()
    );

    /**
     * Call this AFTER modules are initialized.
     */
    public void initHiddenModules() {
        Modules modules = Modules.get();
        if (modules == null) return; // Systems not ready yet

        // Allow-list only automation/QoL modules; hide everything else to stay server‑friendly.
        Set<Class<? extends Module>> allowed = Set.of(
            // Player automation / QoL
            AntiAFK.class, AutoEat.class, AutoFish.class, AutoGap.class, AutoMend.class, AutoReplenish.class,
            AutoRespawn.class, AutoTool.class, BreakDelay.class, ChestSwap.class, EXPThrower.class,
            LiquidInteract.class, MiddleClickExtra.class, NameProtect.class,
            Portals.class,

            // Render (cosmetic/info-lite)
            BetterTooltips.class, BossStack.class, CameraTweaks.class, Fullbright.class, Grid.class, HandView.class,
            ItemPhysics.class, ItemHighlight.class, LightOverlay.class, Nametags.class, TimeChanger.class, Zoom.class, ZoomPlus.class,

            // World automation
            AutoBreed.class, AutoBrewer.class, AutoMount.class, AutoNametag.class, AutoShearer.class, AutoSign.class,
            AutoSmelter.class, DoubleDoorsInteract.class, HighwayBuilder.class, LiquidFiller.class,
            NoGhostBlocks.class, SpawnProofer.class,

            // Movement exceptions
            AutoWalk.class, Sprint.class, AutoWasp.class,

            // Misc utilities
            AutoReconnect.class, AutoSleep.class, BetterChat.class, DiscordPresence.class, InventoryTweaks.class,
            Notebot.class, Notifier.class, SoundBlocker.class
            ,

            // Stardust modules requested in Bliss
            MusicTweaks.class, BannerData.class, LoreLocator.class, Loadouts.class, AutoDoors.class,
            AdBlocker.class, RoadTrip.class, StashBrander.class, SignatureSign.class, Minesweeper.class
        );

        List<Module> list = new ArrayList<>();
        for (Module module : modules.getAll()) {
            if (!allowed.contains(module.getClass())) list.add(module);
        }

        hiddenModules.set(list);
    }

    public final Setting<Integer> moduleSearchCount = sgModules.add(new IntSetting.Builder()
        .name("module-search-count")
        .description("Number of modules shown in the search bar.")
        .defaultValue(4)
        .min(1)
        .sliderMax(8)
        .build()
    );

    public final Setting<Boolean> moduleAliases = sgModules.add(new BoolSetting.Builder()
        .name("search-module-aliases")
        .description("Use module aliases in the search bar.")
        .defaultValue(true)
        .build()
    );

    // ================================
    // CHAT SETTINGS
    // ================================

    public final Setting<String> prefix = sgChat.add(new StringSetting.Builder()
        .name("prefix")
        .description("Command prefix.")
        .defaultValue(".")
        .build()
    );

    public final Setting<Boolean> chatFeedback = sgChat.add(new BoolSetting.Builder()
        .name("chat-feedback")
        .description("Send chat feedback for Meteor actions.")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> deleteChatFeedback = sgChat.add(new BoolSetting.Builder()
        .name("delete-chat-feedback")
        .description("Delete previous feedback messages.")
        .visible(chatFeedback::get)
        .defaultValue(true)
        .build()
    );

    // ================================
    // MISC SETTINGS
    // ================================

    public final Setting<List<String>> hiddenCommands = sgMisc.add(new StringListSetting.Builder()
        .name("hidden-commands")
        .description("Commands hidden from the help menu, autocomplete, and execution.")
        .defaultValue(List.of("bind", "toggle", "hclip", "vclip"))
        .visible(() -> false)
        .build()
    );

    public final Setting<Integer> rotationHoldTicks = sgMisc.add(new IntSetting.Builder()
        .name("rotation-hold")
        .description("Amount of ticks to hold rotation without sending packets.")
        .defaultValue(4)
        .build()
    );

    public final Setting<Boolean> useTeamColor = sgMisc.add(new BoolSetting.Builder()
        .name("use-team-color")
        .description("Use player's team color for rendering ESP/tracers.")
        .defaultValue(true)
        .build()
    );

    public List<String> dontShowAgainPrompts = new ArrayList<>();

    // ================================
    // CONSTRUCTOR WITH SAFE DEFAULT CONFIG LOADING
    // ================================

    public Config() {
        super("config");

        Path configPath = Path.of(MeteorClient.FOLDER.getPath(), "config.nbt");

        if (Files.notExists(configPath)) {
            NbtCompound root = new NbtCompound();
            root.putString("version", MeteorClient.VERSION.toString());
            root.put("settings", settings.toTag());
            root.put("dontShowAgainPrompts", listToTag(new ArrayList<>()));

            NbtCompound settingsCompound = root.getCompoundOrEmpty("settings");

            // General scale
            NbtCompound generalGroup = settingsCompound.getCompoundOrEmpty("General");
            generalGroup.putDouble("scale", 1.2158119500350015);
            settingsCompound.put("General", generalGroup);

            // Colors
            NbtCompound colorsGroup = settingsCompound.getCompoundOrEmpty("Colors");

            NbtCompound accentColor = new NbtCompound();
            accentColor.putInt("a", 255);
            accentColor.putInt("rainbow", 0);
            accentColor.putInt("r", 255);
            accentColor.putInt("g", 5);
            accentColor.putInt("b", 226);
            colorsGroup.put("accent-color", accentColor);

            NbtCompound checkboxColor = new NbtCompound();
            checkboxColor.putInt("a", 255);
            checkboxColor.putInt("rainbow", 0);
            checkboxColor.putInt("r", 108);
            checkboxColor.putInt("g", 31);
            checkboxColor.putInt("b", 106);
            colorsGroup.put("checkbox-color", checkboxColor);

            settingsCompound.put("Colors", colorsGroup);
            root.put("settings", settingsCompound);

            try {
                NbtIo.write(root, configPath);
                this.fromTag(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // If Modules were already initialized earlier, apply hiddenModules now
        if (Modules.get().getAll().size() > 0) {
            initHiddenModules();
            this.save();
        }
    }

    public static Config get() {
        return Systems.get(Config.class);
    }

    // ================================
    // NBT SAVE / LOAD
    // ================================

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();
        tag.putString("version", MeteorClient.VERSION.toString());
        tag.put("settings", settings.toTag());
        tag.put("dontShowAgainPrompts", listToTag(dontShowAgainPrompts));
        return tag;
    }

    @Override
    public Config fromTag(NbtCompound tag) {
        if (tag.contains("settings")) settings.fromTag(tag.getCompoundOrEmpty("settings"));
        if (tag.contains("dontShowAgainPrompts"))
            dontShowAgainPrompts = listFromTag(tag, "dontShowAgainPrompts");
        return this;
    }

    private NbtList listToTag(List<String> list) {
        NbtList nbt = new NbtList();
        for (String s : list) nbt.add(NbtString.of(s));
        return nbt;
    }

    private List<String> listFromTag(NbtCompound tag, String key) {
        List<String> list = new ArrayList<>();
        for (NbtElement item : tag.getListOrEmpty(key))
            list.add(item.asString().orElse(""));
        return list;
    }
}
