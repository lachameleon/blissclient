package dev.stardust.util;

import java.io.File;
import java.time.Instant;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.util.Crypt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import io.netty.util.internal.ThreadLocalRandom;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.world.Dimension;
import static meteordevelopment.meteorclient.MeteorClient.mc;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.systems.modules.misc.AutoReconnect;
import meteordevelopment.meteorclient.mixin.ClientPacketListenerAccessor;

/**
 * @author Tas [@0xTas] <root@0xTas.dev>
 **/
public class StardustUtil {
    public static final boolean XAERO_AVAILABLE = FabricLoader.getInstance().isModLoaded("xaeroworldmap")
        && FabricLoader.getInstance().isModLoaded("xaerominimap");

    public enum RainbowColor {
        Reds(new String[]{"§c", "§4"}),
        Yellows(new String[]{"§e", "§6"}),
        Greens(new String[]{"§a", "§2"}),
        Cyans(new String[]{"§b", "§3"}),
        Blues(new String[]{"§9", "§1"}),
        Purples(new String[]{"§d", "§5"});

        public final String[] labels;

        RainbowColor(String[] labels) { this.labels = labels; }

        public static RainbowColor getFirst() {
            return RainbowColor.values()[ThreadLocalRandom.current().nextInt(RainbowColor.values().length)];
        }

        public static RainbowColor getNext(RainbowColor previous) {
            return switch (previous) {
                case Reds -> Yellows;
                case Yellows -> Greens;
                case Greens -> Cyans;
                case Cyans -> Blues;
                case Blues -> Purples;
                case Purples -> Reds;
            };
        }
    }

    public enum TextColor {
        Black("§0"), White("§f"), Gray("§8"), Light_Gray("§7"),
        Dark_Green("§2"), Green("§a"), Dark_Aqua("§3"), Aqua("§b"),
        Dark_Blue("§1"), Blue("§9"), Dark_Red("§4"), Red("§c"),
        Dark_Purple("§5"), Purple("§d"), Gold("§6"), Yellow("§e"),
        Random("");

        public final String label;

        TextColor(String label) {
            this.label = label;
        }
    }

    public enum TextFormat {
        Plain(""), Italic("§o"), Bold("§l"),
        Underline("§n"), Strikethrough("§m"),
        Obfuscated("§k");

        public final String label;

        TextFormat(String label) {
            this.label = label;
        }
    }

    /** Random Color-Code */
    public static String rCC() {
        String color = "§7";
        TextColor[] colors = TextColor.values();

        // Omit gray, light_gray, and black from accent colors.
        while (color.equals("§0") || color.equals("§8") || color.equals("§7")) {
            int luckyIndex = ThreadLocalRandom.current().nextInt(colors.length);
            color = colors[luckyIndex].label;
        }

        return color;
    }

    public static ItemStack chooseMenuIcon() {
        int luckyIndex = ThreadLocalRandom.current().nextInt(menuIcons.length + 3);

        if (luckyIndex < menuIcons.length) return menuIcons[luckyIndex].getDefaultInstance();
        if (luckyIndex == menuIcons.length) return discIcons[ThreadLocalRandom.current().nextInt(discIcons.length)].getDefaultInstance();
        if (luckyIndex == menuIcons.length + 1) return doorIcons[ThreadLocalRandom.current().nextInt(doorIcons.length)].getDefaultInstance();

        ItemStack[] customIcons = getCustomIcons();
        return customIcons[ThreadLocalRandom.current().nextInt(customIcons.length)];
    }

    private static final Item[] discIcons = {
        Items.MUSIC_DISC_5,
        Items.MUSIC_DISC_11,
        Items.MUSIC_DISC_13,
        Items.MUSIC_DISC_CAT,
        Items.MUSIC_DISC_FAR,
        Items.MUSIC_DISC_MALL,
        Items.MUSIC_DISC_STAL,
        Items.MUSIC_DISC_WARD,
        Items.MUSIC_DISC_WAIT,
        Items.MUSIC_DISC_CHIRP,
        Items.MUSIC_DISC_STRAD,
        Items.MUSIC_DISC_RELIC,
        Items.MUSIC_DISC_BLOCKS,
        Items.MUSIC_DISC_MELLOHI,
        Items.MUSIC_DISC_PIGSTEP,
        Items.MUSIC_DISC_CREATOR,
        Items.MUSIC_DISC_PRECIPICE,
        Items.MUSIC_DISC_OTHERSIDE,
        Items.MUSIC_DISC_CREATOR_MUSIC_BOX,
    };

    private static final Item[] doorIcons = {
        Items.OAK_DOOR,
        Items.IRON_DOOR,
        Items.BIRCH_DOOR,
        Items.BAMBOO_DOOR,
        Items.CHERRY_DOOR,
        Items.JUNGLE_DOOR,
        Items.ACACIA_DOOR,
        Items.SPRUCE_DOOR,
        Items.WARPED_DOOR,
        Items.COPPER_DOOR,
        Items.CRIMSON_DOOR,
        Items.MANGROVE_DOOR,
        Items.DARK_OAK_DOOR,
        Items.EXPOSED_COPPER_DOOR,
        Items.OXIDIZED_COPPER_DOOR,
        Items.WEATHERED_COPPER_DOOR
    };

    private static final Item[] menuIcons = {
        Items.CAKE,
        Items.SPAWNER,
        Items.BEDROCK,
        Items.GOAT_HORN,
        Items.HONEYCOMB,
        Items.LODESTONE,
        Items.DRAGON_EGG,
        Items.FILLED_MAP,
        Items.PINK_TULIP,
        Items.TURTLE_EGG,
        Items.NETHER_STAR,
        Items.WITHER_ROSE,
        Items.PINK_PETALS,
        Items.WARPED_SIGN,
        Items.CHERRY_SIGN,
        Items.WIND_CHARGE,
        Items.WRITTEN_BOOK,
        Items.DAMAGED_ANVIL,
        Items.CHERRY_SAPLING,
        Items.JACK_O_LANTERN,
        Items.KNOWLEDGE_BOOK,
        Items.FIREWORK_ROCKET,
        Items.TOTEM_OF_UNDYING,
        Items.LIME_SHULKER_BOX,
        Items.AMETHYST_CLUSTER,
        Items.FLOWERING_AZALEA,
        Items.PINK_SHULKER_BOX,
        Items.GILDED_BLACKSTONE,
        Items.OMINOUS_TRIAL_KEY,
        Items.HEART_POTTERY_SHERD,
        Items.LIGHT_BLUE_SHULKER_BOX,
        Items.ENCHANTED_GOLDEN_APPLE,
        Items.HEARTBREAK_POTTERY_SHERD,
        Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE
    };

    private static ItemStack[] getCustomIcons() {
        ItemStack enchantedPick = new ItemStack(
            ThreadLocalRandom.current().nextInt(2) == 0 ? Items.DIAMOND_PICKAXE : Items.NETHERITE_PICKAXE);
        enchantedPick.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

        ItemStack[] enchantedGlass = new ItemStack[] {
            Items.GLASS.getDefaultInstance(),
            Items.RED_STAINED_GLASS.getDefaultInstance(),
            Items.CYAN_STAINED_GLASS.getDefaultInstance(),
            Items.LIME_STAINED_GLASS.getDefaultInstance(),
            Items.PINK_STAINED_GLASS.getDefaultInstance(),
            Items.WHITE_STAINED_GLASS.getDefaultInstance(),
            Items.BLACK_STAINED_GLASS.getDefaultInstance(),
            Items.LIGHT_BLUE_STAINED_GLASS.getDefaultInstance(),
        };

        for (ItemStack g : enchantedGlass) {
            g.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        }

        ItemStack cgiElytra = new ItemStack(Items.ELYTRA);
        cgiElytra.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

        ItemStack sword32k = new ItemStack(
            ThreadLocalRandom.current().nextInt(2) == 0 ? Items.DIAMOND_SWORD : Items.WOODEN_SWORD);
        sword32k.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

        ItemStack illegalBow = new ItemStack(Items.BOW);
        illegalBow.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

        ItemStack bindingPumpkin = new ItemStack(Items.CARVED_PUMPKIN);
        bindingPumpkin.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

        ItemStack ripTridentFly = new ItemStack(Items.TRIDENT);
        ripTridentFly.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

        return new ItemStack[] {
            Items.PLAYER_HEAD.getDefaultInstance(),
            enchantedPick, sword32k, illegalBow, bindingPumpkin, cgiElytra, ripTridentFly,
            enchantedGlass[ThreadLocalRandom.current().nextInt(enchantedGlass.length)]
        };
    }

    public static boolean checkOrCreateFile(Minecraft mc, String fileName) {
        File file =FabricLoader.getInstance().getGameDir().resolve(fileName).toFile();

        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    if (mc.player != null) {
                        MsgUtil.sendMsg("Created " + file.getName() + " in your meteor-client folder.");
                        Style style = Style.EMPTY.withClickEvent(new ClickEvent.OpenFile(file.getAbsolutePath()));

                        MsgUtil.sendMsg("Click §2§lhere §r§7to open the file.", style);
                    }
                    return true;
                }
            }catch (Exception err) {
                LogUtil.error("Error creating " + file.getAbsolutePath() + "! - Why:\n" + err, "StardustUtil#checkOrCreateFile");
            }
        } else return true;

        return false;
    }

    public static void openFile(String fileName) {
        File file = FabricLoader.getInstance().getGameDir().resolve(fileName).toFile();

        try {
            Runtime runtime = Runtime.getRuntime();
            if (System.getenv("OS") == null) return;
            if (System.getenv("OS").contains("Windows")) {
                runtime.exec(new String[]{"rundll32", "url.dll,", "FileProtocolHandler", file.getAbsolutePath()});
            }else {
                runtime.exec(new String[]{"xdg-open", file.getAbsolutePath()});
            }
        } catch (Exception err) {
            MsgUtil.sendMsg("Failed to open " + file.getName() + "§c..!");
            LogUtil.error("Failed to open " + file.getAbsolutePath() + "! - Why:\n" + err, "StardustUtil#openFile");
        }
    }

    public static boolean isIn2b2tQueue() {
        if (mc.player == null || mc.getConnection() == null) return false;

        return PlayerUtils.getDimension().equals(Dimension.End)
            && mc.player.getAbilities().mayfly && mc.getConnection().getOnlinePlayers().size() <= 1;
    }

    public enum IllegalDisconnectMethod {
        Slot, Chat, Interact, Movement, SequenceBreak, InvalidSettings
    }

    public static void illegalDisconnect(boolean disableAutoReconnect, IllegalDisconnectMethod illegalDisconnectMethod) {
        if (!Utils.canUpdate()) return;
        if (disableAutoReconnect) disableAutoReconnect();

        Packet<?> illegalPacket = null;
        switch (illegalDisconnectMethod) {
            case Slot -> illegalPacket = new ServerboundSetCarriedItemPacket(-69);
            case Chat -> illegalPacket = new ServerboundChatPacket(
                "§",
                Instant.now(),
                Crypt.SaltSupplier.getLong(),
                null,
                ((ClientPacketListenerAccessor) mc.getConnection()).meteor$getLastSeenMessages().generateAndApplyUpdate().update()
            );
            case Interact -> illegalPacket = new ServerboundInteractPacket(mc.player.getId(), InteractionHand.MAIN_HAND, null, false);
            case Movement -> illegalPacket = new ServerboundMovePlayerPacket.Pos(Double.NaN, 69, Double.NaN, false, false);
            case SequenceBreak -> illegalPacket = new ServerboundUseItemPacket(InteractionHand.MAIN_HAND, -420, 13.37F, 69.69F);
            case InvalidSettings -> illegalPacket = new ServerboundClientInformationPacket(new ClientInformation(
                mc.options.languageCode, -69,
                mc.options.chatVisibility().get(), mc.options.chatColors().get(),
                mc.options.buildPlayerInformation().modelCustomisation(), mc.options.mainHand().get(),
                mc.options.buildPlayerInformation().textFilteringEnabled(), mc.options.allowServerListing().get(),
                mc.options.buildPlayerInformation().particleStatus()
            ));
        }
        if (illegalPacket != null) mc.getConnection().getConnection().send(illegalPacket);
    }

    public static void disableAutoReconnect() {
        Modules mods = Modules.get();
        if (mods == null) return;
        AutoReconnect atrc = mods.get(AutoReconnect.class);
        if (atrc.isActive()) atrc.toggle();
    }
}
