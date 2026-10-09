package aukdata.digitalfilter.mixin;

import mekanism.client.gui.element.GuiElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = GuiElement.class, remap = false)
public interface GuiElementInvoker {

    @Invoker("addChild")
    GuiElement df$addChild(GuiElement element);
}
