package aukdata.digitalfilter.mixin;

import mekanism.client.gui.element.scroll.GuiScrollableElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GuiScrollableElement.class, remap = false)
public interface GuiScrollableElementAccessor {

    @Accessor("scroll")
    void df$setScroll(double scroll);
}
