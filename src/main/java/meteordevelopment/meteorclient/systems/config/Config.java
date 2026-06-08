/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.config;

import dev.stardust.modules.*;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.renderer.Fonts;
import meteordevelopment.meteorclient.renderer.text.FontFace;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.*;
import meteordevelopment.meteorclient.systems.modules.movement.*;
import meteordevelopment.meteorclient.systems.modules.player.*;
import meteordevelopment.meteorclient.systems.modules.render.*;
import meteordevelopment.meteorclient.systems.modules.world.*;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Config extends System<Config> {
    public final Settings settings = new Settings();

    private final SettingGroup sgVisual = settings.createGroup("Visual");
    private final SettingGroup sgModules = settings.createGroup("Modules");
    private final SettingGroup sgChat = settings.createGroup("Chat");
    private final SettingGroup sgMisc = settings.createGroup("Misc");

    // Visual

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
        .description("Show Meteor credits on title screen")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> titleScreenSplashes = sgVisual.add(new BoolSetting.Builder()
        .name("title-screen-splashes")
        .description("Show Meteor splash texts on title screen")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> customWindowTitle = sgVisual.add(new BoolSetting.Builder()
        .name("custom-window-title")
        .description("Show custom text in the window title.")
        .defaultValue(false)
        .onModuleActivated(_ -> mc.updateTitle())
        .onChanged(_ -> mc.updateTitle())
        .build()
    );

    public final Setting<String> customWindowTitleText = sgVisual.add(new StringSetting.Builder()
        .name("window-title-text")
        .description("The text it displays in the window title.")
        .visible(customWindowTitle::get)
        .defaultValue("Bliss Client {mc_version} - based on Meteor, adapted by lachameleon")
        .onChanged(_ -> mc.updateTitle())
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

    public final Setting<ButtonPosition> accountButtonAnchor = sgVisual.add(new EnumSetting.Builder<ButtonPosition>()
        .name("accounts-button")
        .description("Controls the position and visibility of the accounts button in the multiplayer screen.")
        .defaultValue(ButtonPosition.TopRight)
        .build()
    );

    public final Setting<Boolean> showAccountStatus = sgVisual.add(new BoolSetting.Builder()
        .name("account-status")
        .description("Shows information about the current account in the multiplayer screen.")
        .defaultValue(true)
        .build()
    );

    public final Setting<ButtonPosition> proxiesButtonAnchor = sgVisual.add(new EnumSetting.Builder<ButtonPosition>()
        .name("proxies-button")
        .description("Controls the position and visibility of the proxies button in the multiplayer screen.")
        .defaultValue(ButtonPosition.TopRight)
        .build()
    );

    public final Setting<Boolean> showProxiesStatus = sgVisual.add(new BoolSetting.Builder()
        .name("proxy-status")
        .description("Shows information about the current proxy in the multiplayer screen.")
        .defaultValue(true)
        .build()
    );

    // Modules

    public final Setting<List<Module>> hiddenModules = sgModules.add(new ModuleListSetting.Builder()
        .name("hidden-modules")
        .description("Modules hidden from the click GUI.")
        .defaultValue(new ArrayList<>())
        .visible(() -> false)
        .build()
    );

    public void initHiddenModules() {
        Modules modules = Modules.get();
        if (modules == null) return;

        Set<Class<? extends Module>> allowed = Set.of(
            AntiAFK.class, AutoEat.class, AutoFish.class, AutoGap.class, AutoMend.class, AutoReplenish.class,
            AutoRespawn.class, AutoTool.class, BreakDelay.class, ChestSwap.class, EXPThrower.class,
            LiquidInteract.class, MiddleClickExtra.class, NameProtect.class, Portals.class,

            BetterTooltips.class, BossStack.class, CameraTweaks.class, Fullbright.class, Grid.class, HandView.class,
            ItemPhysics.class, ItemHighlight.class, LightOverlay.class, Nametags.class, TimeChanger.class, Zoom.class, ZoomPlus.class,

            AutoBreed.class, AutoBrewer.class, AutoMount.class, AutoNametag.class, AutoShearer.class, AutoSign.class,
            AutoSmelter.class, DoubleDoorsInteract.class, HighwayBuilder.class, LiquidFiller.class, NoGhostBlocks.class, SpawnProofer.class,

            AutoWalk.class, Sprint.class, AutoWasp.class,

            AutoReconnect.class, AutoSleep.class, BetterChat.class, InventoryTweaks.class,
            Notebot.class, Notifier.class, SoundBlocker.class,

            MusicTweaks.class, BannerData.class, LoreLocator.class, Loadouts.class, AutoDoors.class,
            AdBlocker.class, RoadTrip.class, StashBrander.class, SignatureSign.class, Minesweeper.class,
            DiscordChatIntegration.class
        );

        List<Module> hidden = new ArrayList<>();
        for (Module module : modules.getAll()) {
            if (!allowed.contains(module.getClass())) hidden.add(module);
        }

        hiddenModules.set(hidden);
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
        .description("Whether or not module aliases will be used in the module search bar.")
        .defaultValue(true)
        .build()
    );

    // Chat

    public final Setting<String> prefix = sgChat.add(new StringSetting.Builder()
        .name("prefix")
        .description("Prefix.")
        .defaultValue(".")
        .build()
    );

    public final Setting<Boolean> chatFeedback = sgChat.add(new BoolSetting.Builder()
        .name("chat-feedback")
        .description("Sends chat feedback when meteor performs certain actions.")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> deleteChatFeedback = sgChat.add(new BoolSetting.Builder()
        .name("delete-chat-feedback")
        .description("Delete previous matching chat feedback to keep chat clear.")
        .visible(chatFeedback::get)
        .defaultValue(true)
        .build()
    );

    // Misc

    public final Setting<List<String>> hiddenCommands = sgMisc.add(new StringListSetting.Builder()
        .name("hidden-commands")
        .description("Commands hidden from the help menu, autocomplete, and execution.")
        .defaultValue(List.of("bind", "toggle", "hclip", "vclip"))
        .visible(() -> false)
        .build()
    );

    public final Setting<Integer> rotationHoldTicks = sgMisc.add(new IntSetting.Builder()
        .name("rotation-hold")
        .description("Hold long to hold server side rotation when not sending any packets.")
        .defaultValue(4)
        .build()
    );

    public final Setting<Boolean> useTeamColor = sgMisc.add(new BoolSetting.Builder()
        .name("use-team-color")
        .description("Uses player's team color for rendering things like esp and tracers.")
        .defaultValue(true)
        .build()
    );

    public List<String> dontShowAgainPrompts = new ArrayList<>();

    public Config() {
        super("config");
    }

    public static Config get() {
        return Systems.get(Config.class);
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();

        tag.putString("version", MeteorClient.VERSION.toString());
        tag.put("settings", settings.toTag());
        tag.put("dontShowAgainPrompts", listToTag(dontShowAgainPrompts));

        return tag;
    }

    @Override
    public Config fromTag(CompoundTag tag) {
        if (tag.contains("settings")) settings.fromTag(tag.getCompoundOrEmpty("settings"));
        if (tag.contains("dontShowAgainPrompts")) dontShowAgainPrompts = listFromTag(tag, "dontShowAgainPrompts");

        return this;
    }

    private ListTag listToTag(List<String> list) {
        ListTag nbt = new ListTag();
        for (String item : list) nbt.add(StringTag.valueOf(item));
        return nbt;
    }

    private List<String> listFromTag(CompoundTag tag, String key) {
        List<String> list = new ArrayList<>();
        for (Tag item : tag.getListOrEmpty(key)) list.add(item.asString().orElse(""));
        return list;
    }

    public enum ButtonPosition {
        TopLeft,
        TopRight,
        BottomLeft,
        BottomRight,
        Hidden,
    }
}
