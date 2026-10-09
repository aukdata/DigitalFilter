package aukdata.digitalfilter.mixin;

import aukdata.digitalfilter.DigitalFilterLayout;
import aukdata.digitalfilter.SearchableFilterScreen;
import java.util.function.IntSupplier;
import mekanism.client.gui.GuiFilterHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = GuiFilterHolder.class, remap = false)
public abstract class GuiFilterHolderMixin {

    @Unique
    private static final String SCROLL_BAR_INIT = "Lmekanism/client/gui/element/scroll/GuiScrollBar;<init>(Lmekanism/client/gui/IGuiWrapper;IIILjava/util/function/IntSupplier;Ljava/util/function/IntSupplier;)V";
    @Unique
    private static final String ELEMENT_HOLDER_INIT = "Lmekanism/client/gui/element/GuiElementHolder;<init>(Lmekanism/client/gui/IGuiWrapper;IIII)V";

    /**
     * Makes the scroll bar size itself to the searched list instead of every filter.
     */
    @ModifyArg(method = "addGuiElements", index = 4, at = @At(value = "INVOKE", target = SCROLL_BAR_INIT))
    private IntSupplier df$searchedFilterCount(IntSupplier maxElements) {
        if ((Object) this instanceof SearchableFilterScreen screen) {
            return () -> screen.df$isSearching() ? screen.df$visibleCount() : maxElements.getAsInt();
        }
        return maxElements;
    }

    //The rest stretches the Digital Miner config screen's filter list by the extra rows

    @ModifyArg(method = "addGuiElements", index = 5, at = @At(value = "INVOKE", target = SCROLL_BAR_INIT))
    private IntSupplier df$visibleRows(IntSupplier focusedElements) {
        if (df$extraRows() > 0) {
            int rows = DigitalFilterLayout.BASE_ROWS + df$extraRows();
            return () -> rows;
        }
        return focusedElements;
    }

    @ModifyArg(method = "addGuiElements", index = 3, at = @At(value = "INVOKE", target = SCROLL_BAR_INIT))
    private int df$scrollBarHeight(int height) {
        return height + df$extraPixels();
    }

    @ModifyArg(method = "addGuiElements", index = 4, at = @At(value = "INVOKE",
          target = "Lmekanism/client/gui/element/GuiInnerScreen;<init>(Lmekanism/client/gui/IGuiWrapper;IIII)V"))
    private int df$innerScreenHeight(int height) {
        return height + df$extraPixels();
    }

    @ModifyArg(method = "addGuiElements", index = 4, at = @At(value = "INVOKE", target = ELEMENT_HOLDER_INIT, ordinal = 0))
    private int df$filterHolderHeight(int height) {
        return height + df$extraPixels();
    }

    @ModifyArg(method = "addGuiElements", index = 2, at = @At(value = "INVOKE", target = ELEMENT_HOLDER_INIT, ordinal = 1))
    private int df$newFilterBorderY(int y) {
        return y + df$extraPixels();
    }

    @ModifyConstant(method = "addGuiElements", constant = @Constant(intValue = DigitalFilterLayout.BASE_ROWS))
    private int df$filterButtonCount(int count) {
        return count + df$extraRows();
    }

    @Unique
    private int df$extraRows() {
        return (Object) this instanceof SearchableFilterScreen ? DigitalFilterLayout.extraRows() : 0;
    }

    @Unique
    private int df$extraPixels() {
        return df$extraRows() * DigitalFilterLayout.ROW_HEIGHT;
    }
}
