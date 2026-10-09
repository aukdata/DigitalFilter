package aukdata.digitalfilter;

import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkConstants;

@Mod(DigitalFilter.MODID)
public class DigitalFilter {

    public static final String MODID = "digitalfilter";

    public DigitalFilter() {
        //Client-only mod: don't require the server to have it installed
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
              () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (remoteVersion, isServer) -> true));
    }
}
