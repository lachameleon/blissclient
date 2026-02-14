package dev.stardust;

import com.mojang.logging.LogUtils;
import dev.stardust.util.StardustUtil;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import org.slf4j.Logger;

public final class Stardust {
    public static final Logger LOG = LogUtils.getLogger();
    public static final HudGroup HUD_GROUP = new HudGroup("Stardust");
    public static final Category CATEGORY = new Category("Stardust", StardustUtil.chooseMenuIcon());

    private Stardust() {
    }
}
