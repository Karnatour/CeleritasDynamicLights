package toni.sodiumdynamiclights.config;

import org.taumc.celeritas.api.OptionGUIConstructionEvent;

public class CeleritasOptionsListener {

    public static void onCeleritasOptionsConstruct(OptionGUIConstructionEvent event) {
        event.addPage(DynamicLightsCeleritasPage.celeritasDynamicLights());
    }

}
