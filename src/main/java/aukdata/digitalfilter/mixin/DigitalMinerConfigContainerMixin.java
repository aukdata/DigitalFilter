package aukdata.digitalfilter.mixin;

import aukdata.digitalfilter.DigitalFilterLayout;
import mekanism.common.inventory.container.tile.DigitalMinerConfigContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DigitalMinerConfigContainer.class, remap = false)
public abstract class DigitalMinerConfigContainerMixin {

    /**
     * Moves the player inventory down to make room for the extra filter rows. Slot positions only matter on the client, so this is safe
     * when the server doesn't have the mod.
     */
    @Inject(method = "getInventoryYOffset", at = @At("RETURN"), cancellable = true)
    private void df$moveInventoryDown(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(cir.getReturnValue() + DigitalFilterLayout.computeExtraRows() * DigitalFilterLayout.ROW_HEIGHT);
    }
}
