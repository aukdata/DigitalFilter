package aukdata.digitalfilter.mixin;

import mekanism.client.gui.GuiFilterHolder;
import mekanism.client.gui.element.scroll.GuiScrollBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GuiFilterHolder.class, remap = false)
public interface GuiFilterHolderAccessor {

    @Accessor("scrollBar")
    GuiScrollBar df$getScrollBar();
}
