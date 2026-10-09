package aukdata.digitalfilter;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

/**
 * How many extra filter rows the Digital Miner config screen gets, so the screen uses the available window height.
 */
public final class DigitalFilterLayout {

    public static final int ROW_HEIGHT = 29;
    public static final int BASE_ROWS = 4;
    private static final int MAX_EXTRA_ROWS = 6;
    //Height of Mekanism's config screen with the default 4 rows, plus a small margin
    private static final int BASE_SCREEN_HEIGHT = 252 + 8;

    private static int extraRows;

    private DigitalFilterLayout() {
    }

    /**
     * Called when the config container is created (before its screen), so the inventory slots and the screen agree on the size.
     */
    public static int computeExtraRows() {
        int guiHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        extraRows = Mth.clamp((guiHeight - BASE_SCREEN_HEIGHT) / ROW_HEIGHT, 0, MAX_EXTRA_ROWS);
        return extraRows;
    }

    public static int extraRows() {
        return extraRows;
    }
}
