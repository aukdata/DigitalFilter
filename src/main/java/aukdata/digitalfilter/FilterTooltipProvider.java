package aukdata.digitalfilter;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Implemented (via mixin) by Mekanism's filter list buttons to show which item is displayed and what the filter was configured with.
 */
public interface FilterTooltipProvider {

    void df$renderFilterTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY);
}
