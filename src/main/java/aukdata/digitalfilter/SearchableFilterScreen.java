package aukdata.digitalfilter;

/**
 * Implemented (via mixin) by the Digital Miner config screen to expose its search state to the filter list widgets.
 */
public interface SearchableFilterScreen {

    boolean df$isSearching();

    /**
     * @return the number of filters matching the current search
     */
    int df$visibleCount();

    /**
     * Converts an index into the searched (visible) list into an index into the miner's full filter list.
     *
     * @return the real index, or -1 if there is no filter at that position
     */
    int df$toRealIndex(int visibleIndex);
}
