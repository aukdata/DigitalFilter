package aukdata.digitalfilter.mixin;

import mekanism.client.gui.element.text.GuiTextField;
import mekanism.client.gui.element.window.filter.GuiTextFilter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GuiTextFilter.class, remap = false)
public interface GuiTextFilterAccessor {

    @Accessor("text")
    GuiTextField df$getText();
}
