package aukdata.digitalfilter.mixin;

import aukdata.digitalfilter.SearchableFilterScreen;
import java.util.function.IntSupplier;
import mekanism.client.gui.GuiFilterHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = GuiFilterHolder.class, remap = false)
public abstract class GuiFilterHolderMixin {

    /**
     * Makes the scroll bar size itself to the searched list instead of every filter.
     */
    @ModifyArg(method = "addGuiElements", index = 4, at = @At(value = "INVOKE",
          target = "Lmekanism/client/gui/element/scroll/GuiScrollBar;<init>(Lmekanism/client/gui/IGuiWrapper;IIILjava/util/function/IntSupplier;Ljava/util/function/IntSupplier;)V"))
    private IntSupplier df$searchedFilterCount(IntSupplier maxElements) {
        if ((Object) this instanceof SearchableFilterScreen screen) {
            return () -> screen.df$isSearching() ? screen.df$visibleCount() : maxElements.getAsInt();
        }
        return maxElements;
    }
}
