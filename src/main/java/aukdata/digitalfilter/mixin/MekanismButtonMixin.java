package aukdata.digitalfilter.mixin;

import aukdata.digitalfilter.FilterTooltipProvider;
import mekanism.client.gui.element.button.MekanismButton;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MekanismButton.class, remap = false)
public abstract class MekanismButtonMixin {

    //FilterButton doesn't override renderToolTip, so hook the parent and only act for filter buttons
    @Inject(method = "renderToolTip", at = @At("TAIL"))
    private void df$renderFilterTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        if ((Object) this instanceof FilterTooltipProvider provider) {
            provider.df$renderFilterTooltip(guiGraphics, mouseX, mouseY);
        }
    }
}
