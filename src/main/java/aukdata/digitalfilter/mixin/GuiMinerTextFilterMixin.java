package aukdata.digitalfilter.mixin;

import aukdata.digitalfilter.FilterSuggestions;
import aukdata.digitalfilter.GuiSuggestionList;
import mekanism.client.gui.element.GuiElement;
import mekanism.client.gui.element.window.filter.miner.GuiMinerModIDFilter;
import mekanism.client.gui.element.window.filter.miner.GuiMinerTagFilter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {GuiMinerTagFilter.class, GuiMinerModIDFilter.class}, remap = false)
public abstract class GuiMinerTextFilterMixin {

    /**
     * Added last so it is drawn on top and gets key presses before the text field (Mekanism dispatches to children in reverse order).
     */
    @Inject(method = "init", at = @At("TAIL"))
    private void df$addSuggestions(CallbackInfo ci) {
        GuiElement self = (GuiElement) (Object) this;
        boolean tags = self instanceof GuiMinerTagFilter;
        ((GuiElementInvoker) self).df$addChild(new GuiSuggestionList(self.gui(), ((GuiTextFilterAccessor) self).df$getText(),
              tags ? FilterSuggestions::blockTags : FilterSuggestions::blockModIDs));
    }
}
