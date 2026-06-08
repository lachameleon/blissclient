package dev.stardust.modules;

import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.ArrayDeque;
import com.google.gson.Gson;
import oshi.util.tuples.Pair;
import meteordevelopment.meteorclient.systems.modules.Categories;
import java.lang.reflect.Type;
import dev.stardust.util.LogUtil;
import dev.stardust.util.MsgUtil;
import dev.stardust.util.StardustUtil;
import com.google.common.reflect.TypeToken;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.events.world.TickEvent;

/**
 * @author Tas [0xTas] <root@0xTas.dev>
 **/
public class Loadouts extends Module {
    public Loadouts() { super(Categories.Player, "Loadouts", "Save and load inventory configurations."); }

    public static final String LOADOUTS_FILE = "meteor-client/loadouts.json";

    // See InventoryScreenMixin.java
    public final Setting<Boolean> quickLoadout = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("quick-loadout-buttons")
            .description("Adds quicksave loadout buttons to the inventory screen.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> chatNotify = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("chat-notify")
            .description("Notify you in chat when your loadout is saved or loaded.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> debug = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("debug")
            .defaultValue(false)
            .visible(() -> false)
            .build()
    );

    private final Setting<Integer> tickRateSetting = settings.getDefaultGroup().add(
        new IntSetting.Builder()
            .name("tick-rate")
            .range(1, 50)
            .sliderRange(1, 20)
            .defaultValue(3)
            .build()
    );

    private int ticks = 0;
    public boolean isSorted = true;
    private boolean doubleTap = false;
    private String activeLoadoutKey = "quicksave";
    private final ArrayDeque<Pair<Integer, Integer>> jobs = new ArrayDeque<>();
    private final HashMap<String, HashMap<Integer, Item>> loadouts = new HashMap<>();

    @Override
    public void onActivate() {
        loadLoadoutsFromFile();
    }

    @Override
    public void onDeactivate() {
        ticks = 0;
        jobs.clear();
        isSorted = true;
        doubleTap = false;
        saveLoadoutsToFile();
        activeLoadoutKey = "quicksave";
    }

    // See Loadout.java
    public void clearLoadouts() {
        loadouts.clear();
        saveLoadoutsToFile();
    }
    public void deleteLoadout(String name) {
        loadouts.remove(name);
        saveLoadoutsToFile();
    }
    public boolean noLoadout(String name) {
        return !loadouts.containsKey(name);
    }

    private void loadLoadoutsFromFile() {
        if (!StardustUtil.checkOrCreateFile(mc, LOADOUTS_FILE)) {
            LogUtil.error("Error checking loadouts file for loading..!", this.name);
        }

        Gson gson = new Gson();
        try (Reader reader = new FileReader(LOADOUTS_FILE)) {
            Type type = new TypeToken<HashMap<String, HashMap<Integer, String>>>() {}.getType();
            HashMap<String, HashMap<Integer, String>> loaded = gson.fromJson(reader, type);

            loadouts.clear();
            for (Map.Entry<String, HashMap<Integer, String>> entry : loaded.entrySet()) {
                HashMap<Integer, Item> itemMap = new HashMap<>();
                for (Map.Entry<Integer, String> itemId : entry.getValue().entrySet()) {
                    itemMap.put(itemId.getKey(), BuiltInRegistries.ITEM.getValue(Identifier.parse(itemId.getValue())));
                }

                loadouts.put(entry.getKey(), itemMap);
                LogUtil.info("Successfully loaded loadouts from file..!", this.name);
            }
        } catch (Exception err) {
            LogUtil.error("Error loading loadouts from file..! - Why: " + err, this.name);
        }
    }

    private void saveLoadoutsToFile() {
        if (!StardustUtil.checkOrCreateFile(mc, LOADOUTS_FILE)) {
            LogUtil.error("Error checking loadouts file for saving..!", this.name);
        }

        Gson gson = new Gson();
        try (Writer writer = new FileWriter(LOADOUTS_FILE)) {
            HashMap<String, HashMap<Integer, String>> itemNameMap = new HashMap<>();
            for (Map.Entry<String, HashMap<Integer, Item>> entry : loadouts.entrySet()) {
                HashMap<Integer, String> nameMap = new HashMap<>();
                for (Map.Entry<Integer, Item> itemEntry : entry.getValue().entrySet()) {
                    nameMap.put(itemEntry.getKey(), BuiltInRegistries.ITEM.getKey(itemEntry.getValue()).toString());
                }

                itemNameMap.put(entry.getKey(), nameMap);
            }

            gson.toJson(itemNameMap, writer);
            LogUtil.info("Successfully saved loadouts to file..!", this.name);
        } catch (Exception err) {
            LogUtil.error("Error saving loadouts to file..! - Why: " + err, this.name);
        }
    }

    private boolean isLoaded(String loadoutKey) {
        if (loadouts.isEmpty()) return true;
        if (mc.player == null) return true;
        if (!loadouts.containsKey(loadoutKey)) return true;
        if (!(mc.player.containerMenu instanceof InventoryMenu handler)) return true;

        HashMap<Integer, Item> loadout = loadouts.get(loadoutKey);
        for (int n = InventoryMenu.ARMOR_SLOT_START; n < handler.slots.size(); n++) {
            if (!loadout.containsKey(n)) continue;
            ItemStack stack = handler.getSlot(n).getItem();
            if (!stack.is(loadout.get(n))) return false;
        }

        return true;
    }

    public void saveLoadout(String name) {
        if (mc.player == null) return;
        if (!(mc.player.containerMenu instanceof InventoryMenu handler)) return;

        HashMap<Integer, Item> loadout = new HashMap<>();
        for (int n = InventoryMenu.ARMOR_SLOT_START; n < handler.slots.size(); n++) {
            ItemStack stack = handler.getSlot(n).getItem();
            if (!stack.isEmpty() && !stack.is(Items.AIR)) {
                loadout.put(n, stack.getItem());
            }
        }
        loadouts.put(name, loadout);

        saveLoadoutsToFile();
        if (chatNotify.get()) {
            MsgUtil.sendModuleMsg("§oLoadout \"§a§o" + name + "§7§o\" saved successfully§8§o.", this.name);
        }
    }

    public void loadLoadout(String name) {
        if (mc.player == null) return;
        if (!(mc.player.containerMenu instanceof InventoryMenu handler)) return;

        if (loadouts.isEmpty() || !loadouts.containsKey(name) || loadouts.get(name).isEmpty()) {
            MsgUtil.sendModuleMsg("§oNo loadout \"§3§o" + name + "§7§o\" saved§c§o..!", this.name);
            return;
        }

        jobs.clear();
        activeLoadoutKey = name;
        ArrayList<Integer> sorted = new ArrayList<>();
        HashMap<Integer, Item> loadout = loadouts.get(name);
        HashMap<Integer, ItemStack> changedSlots = new HashMap<>();
        for (int to = InventoryMenu.ARMOR_SLOT_START; to < handler.slots.size(); to++) {
            Item assigned = loadout.get(to);
            if (assigned == null) continue;

            ItemStack current = handler.getSlot(to).getItem();
            if (debug.get()) {
                LogUtil.info(
                    "Assigned: " + assigned.getDefaultInstance().getHoverName().getString()
                    + " | Current: " + current.getHoverName().getString(), this.name
                );
            }

            if (current.is(assigned)) {
                if (debug.get()) LogUtil.info("Slot already sorted..!", this.name);
                sorted.add(to);
                continue;
            }

            for (int from = InventoryMenu.ARMOR_SLOT_START; from < handler.slots.size(); from++) {
                if (to == from || sorted.contains(from)) continue;
                ItemStack occupiedBy;
                if (changedSlots.containsKey(from)) {
                    occupiedBy = changedSlots.get(from);
                } else {
                    occupiedBy = handler.getSlot(from).getItem();
                }
                if (debug.get()) {
                    LogUtil.info(
                        "Looking for: " + assigned.getDefaultInstance().getHoverName().getString()
                        + " | found: " + occupiedBy.getHoverName().getString(), this.name
                    );
                }
                if (occupiedBy.is(assigned)) {
                    if (loadout.get(from) != null && occupiedBy.is(loadout.get(from))) {
                        sorted.add(from);
                        continue;
                    }

                    if (!current.isEmpty()) {
                        sorted.add(to);
                        changedSlots.put(from, current);
                        jobs.addLast(new Pair<>(from, to));
                    } else {
                        sorted.add(to);
                        sorted.add(from);
                        changedSlots.remove(from);
                        jobs.addLast(new Pair<>(from, to));
                    }

                    if (debug.get()) {
                        LogUtil.info(
                            "Moving stack: " + occupiedBy.getHoverName().getString()
                            + " from slot " + from + " to slot " + to + "..!", this.name
                        );
                    }
                    break;
                }
            }
        }
    }


    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null) return;
        if (!(mc.player.containerMenu instanceof InventoryMenu)) return;

        ++ticks;
        if (ticks >= tickRateSetting.get()) {
            ticks = 0;
            if (!jobs.isEmpty()) {
                isSorted = false;
                Pair<Integer, Integer> entry = jobs.removeFirst();
                InvUtils.move().fromId(entry.getA()).toId(entry.getB());
            }
            if (jobs.isEmpty() && !isSorted) {
                if (!doubleTap && !isLoaded(activeLoadoutKey)) {
                    doubleTap = true;
                    loadLoadout(activeLoadoutKey);
                } else {
                    isSorted = true;
                    doubleTap = false;
                }

                if (isSorted && chatNotify.get()) {
                    MsgUtil.sendModuleMsg(
                        "§oInventory sorted according to the loadout \"§a§o"
                            + activeLoadoutKey + "\"§e§o..!", this.name
                    );
                }
            }
        }
    }
}
