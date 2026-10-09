package aukdata.digitalfilter.mixin;

import aukdata.digitalfilter.FilterSearch;
import aukdata.digitalfilter.SearchableFilterScreen;
import java.util.ArrayList;
import java.util.List;
import mekanism.client.gui.GuiFilterHolder;
import mekanism.client.gui.element.text.GuiTextField;
import mekanism.common.content.filter.IFilter;
import mekanism.common.content.miner.MinerFilter;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.tile.machine.TileEntityDigitalMiner;
import mekanism.client.gui.machine.GuiDigitalMinerConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiDigitalMinerConfig.class, remap = false)
public abstract class GuiDigitalMinerConfigMixin extends GuiFilterHolder<MinerFilter<?>, TileEntityDigitalMiner, MekanismTileContainer<TileEntityDigitalMiner>>
      implements SearchableFilterScreen {

    //Search box sits on the right of the title bar; the title is squeezed into the space between the back button and it
    @Unique
    private static final int DF_SEARCH_X = 98;
    @Unique
    private static final int DF_SEARCH_Y = 5;
    @Unique
    private static final int DF_SEARCH_WIDTH = 72;
    @Unique
    private static final int DF_SEARCH_HEIGHT = 11;
    @Unique
    private static final int DF_TITLE_LEFT = 16;

    @Unique
    private GuiTextField df$searchField;
    @Unique
    private String df$query = "";
    @Unique
    private final List<Integer> df$matches = new ArrayList<>();
    @Unique
    private final List<MinerFilter<?>> df$snapshot = new ArrayList<>();
    @Unique
    private String df$snapshotQuery;
    @Unique
    private boolean df$redirectingClick;

    protected GuiDigitalMinerConfigMixin(MekanismTileContainer<TileEntityDigitalMiner> container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Inject(method = "addGuiElements", at = @At("TAIL"))
    private void df$addSearchField(CallbackInfo ci) {
        String previous = df$query;
        df$searchField = addRenderableWidget(new GuiTextField(this, DF_SEARCH_X, DF_SEARCH_Y, DF_SEARCH_WIDTH, DF_SEARCH_HEIGHT));
        df$searchField.setMaxLength(64);
        //Keep the query when the screen is re-initialized (e.g. on window resize)
        df$searchField.setText(previous);
        df$searchField.setResponder(text -> {
            if (!text.equals(df$query)) {
                df$query = text;
                ((GuiScrollableElementAccessor) ((GuiFilterHolderAccessor) this).df$getScrollBar()).df$setScroll(0);
            }
        });
    }

    @Inject(method = "drawTitleText", at = @At("HEAD"), cancellable = true)
    private void df$drawTitleBesideSearch(GuiGraphics guiGraphics, Component text, float y, CallbackInfo ci) {
        ci.cancel();
        int width = DF_SEARCH_X - 2 - DF_TITLE_LEFT;
        float scale = Math.min(1, (width - 4F) / getStringWidth(text));
        drawScaledCenteredText(guiGraphics, text, DF_TITLE_LEFT + width / 2F, y, titleTextColor(), scale);
    }

    @Inject(method = "drawForegroundText", at = @At("TAIL"))
    private void df$drawSearchHint(GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (df$searchField != null && df$query.isEmpty() && !df$searchField.isTextFieldFocused()) {
            drawTextScaledBound(guiGraphics, Component.translatable("digitalfilter.search.hint"), DF_SEARCH_X + 3, DF_SEARCH_Y + 2, 0xFF808080,
                  DF_SEARCH_WIDTH - 6);
        }
    }

    /**
     * Filter buttons pass their position in the searched list; open the filter that is actually shown there.
     */
    @Inject(method = "onClick", at = @At("HEAD"), cancellable = true)
    private void df$remapClick(IFilter<?> filter, int index, CallbackInfo ci) {
        if (df$redirectingClick || !df$isSearching()) {
            return;
        }
        ci.cancel();
        int realIndex = df$toRealIndex(index);
        if (realIndex >= 0) {
            df$redirectingClick = true;
            try {
                onClick(getFilterManager().getFilters().get(realIndex), realIndex);
            } finally {
                df$redirectingClick = false;
            }
        }
    }

    @Override
    public boolean df$isSearching() {
        return !df$query.isBlank();
    }

    @Override
    public int df$visibleCount() {
        df$refreshMatches();
        return df$matches.size();
    }

    @Override
    public int df$toRealIndex(int visibleIndex) {
        if (!df$isSearching()) {
            return visibleIndex;
        }
        df$refreshMatches();
        return visibleIndex >= 0 && visibleIndex < df$matches.size() ? df$matches.get(visibleIndex) : -1;
    }

    @Unique
    private void df$refreshMatches() {
        List<MinerFilter<?>> filters = getFilterManager().getFilters();
        if (df$query.equals(df$snapshotQuery) && df$sameFilters(filters)) {
            return;
        }
        df$snapshotQuery = df$query;
        df$snapshot.clear();
        df$snapshot.addAll(filters);
        df$matches.clear();
        for (int i = 0; i < filters.size(); i++) {
            if (FilterSearch.matches(filters.get(i), df$query)) {
                df$matches.add(i);
            }
        }
    }

    @Unique
    private boolean df$sameFilters(List<MinerFilter<?>> filters) {
        if (filters.size() != df$snapshot.size()) {
            return false;
        }
        for (int i = 0; i < filters.size(); i++) {
            if (filters.get(i) != df$snapshot.get(i)) {
                return false;
            }
        }
        return true;
    }
}
