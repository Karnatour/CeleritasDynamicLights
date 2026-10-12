package toni.sodiumdynamiclights.config;

import dhj.embeddedt.embeddium.api.OptionGUIConstructionEvent;

public class ActiniumOptionsListener {
    public static void onActiniumOptionsConstruct(OptionGUIConstructionEvent event) {
        event.addPage(DynamicLightsActiniumPage.celeritasDynamicLights());
    }
}