package aukdata.digitalfilter.mixin;

import aukdata.digitalfilter.SearchableFilterScreen;
import mekanism.client.gui.element.GuiElement;
import mekanism.client.gui.element.button.FilterButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FilterButton.class, remap = false)
public abstract class FilterButtonMixin {

    /**
     * While searching, the button's position refers to the searched list, so map it back to the real filter index. This covers which filter
     * is displayed and the enable toggle.
     */
    @Inject(method = "getActualIndex", at = @At("RETURN"), cancellable = true)
    private void df$mapToRealIndex(CallbackInfoReturnable<Integer> cir) {
        if (((GuiElement) (Object) this).gui() instanceof SearchableFilterScreen screen && screen.df$isSearching()) {
            cir.setReturnValue(screen.df$toRealIndex(cir.getReturnValue()));
        }
    }
}
