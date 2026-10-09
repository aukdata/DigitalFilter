package aukdata.digitalfilter.mixin;

import aukdata.digitalfilter.FilterTooltipProvider;
import aukdata.digitalfilter.SearchableFilterScreen;
import java.util.ArrayList;
import java.util.List;
import mekanism.client.gui.element.GuiElement;
import mekanism.client.gui.element.button.FilterButton;
import mekanism.client.gui.element.slot.GuiSequencedSlotDisplay;
import mekanism.common.content.filter.IFilter;
import mekanism.common.content.filter.IModIDFilter;
import mekanism.common.content.filter.ITagFilter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FilterButton.class, remap = false)
public abstract class FilterButtonMixin implements FilterTooltipProvider {

    @Shadow
    @Final
    private GuiSequencedSlotDisplay slotDisplay;

    @Shadow
    @Nullable
    protected abstract IFilter<?> getFilter();

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

    /**
     * Hovering the item icon shows the displayed item's name, plus the configured tag or mod ID for tag and mod ID filters.
     */
    @Override
    public void df$renderFilterTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        GuiElement self = (GuiElement) (Object) this;
        if (!(self.gui() instanceof SearchableFilterScreen) || !slotDisplay.visible) {
            return;
        }
        if (mouseX < slotDisplay.getX() || mouseX >= slotDisplay.getX() + 16 || mouseY < slotDisplay.getY() || mouseY >= slotDisplay.getY() + 16) {
            return;
        }
        IFilter<?> filter = getFilter();
        if (filter == null) {
            return;
        }
        List<Component> lines = new ArrayList<>();
        ItemStack stack = slotDisplay.getRenderStack();
        if (!stack.isEmpty()) {
            lines.add(stack.getHoverName());
        }
        if (filter instanceof ITagFilter<?> tagFilter && tagFilter.getTagName() != null) {
            lines.add(Component.literal(tagFilter.getTagName()).withStyle(ChatFormatting.GRAY));
        } else if (filter instanceof IModIDFilter<?> modIDFilter && modIDFilter.getModID() != null) {
            lines.add(Component.literal(modIDFilter.getModID()).withStyle(ChatFormatting.GRAY));
        }
        if (!lines.isEmpty()) {
            self.displayTooltips(guiGraphics, mouseX, mouseY, lines);
        }
    }
}
