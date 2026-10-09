package aukdata.digitalfilter.mixin;

import aukdata.digitalfilter.SearchableFilterScreen;
import mekanism.client.gui.element.GuiElement;
import mekanism.client.gui.element.button.FilterSelectButton;
import mekanism.client.gui.element.button.MovableFilterButton;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MovableFilterButton.class, remap = false)
public abstract class MovableFilterButtonMixin {

    @Shadow
    @Final
    private FilterSelectButton upButton;
    @Shadow
    @Final
    private FilterSelectButton downButton;

    /**
     * Reordering only makes sense on the full list, so hide the move buttons while searching.
     */
    @Inject(method = "updateButtonVisibility", at = @At("TAIL"))
    private void df$hideMoveButtonsWhileSearching(CallbackInfo ci) {
        if (((GuiElement) (Object) this).gui() instanceof SearchableFilterScreen screen && screen.df$hasCustomView()) {
            upButton.visible = false;
            downButton.visible = false;
        }
    }
}
