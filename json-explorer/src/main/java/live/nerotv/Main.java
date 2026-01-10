package live.nerotv;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import live.nerotv.jsonexplorer.APIExplorer;
import live.nerotv.jsonexplorer.utils.ApexLogger;

import javax.swing.*;

public class Main {

    private static ApexLogger logger;
    private static String apiKey;

    public static void main(String[] a) {
        logger = new ApexLogger("JSON-Explorer");
        logger.log("JSON-Explorer by nerotvlive: https://a.nerotv.live");
        if (resolveArguments(a)) {
            logger.log("Starting JSON-Explorer...");
            initDesktop();
            new APIExplorer(apiKey);
            apiKey = null;
        }
    }

    private static void initDesktop() {
        logger.dbg("Initializing...");
        try {
            FlatDarkLaf.setup();
            if (System.getProperty("os.name").contains("mac")) {
                UIManager.setLookAndFeel(new FlatMacDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            }
            logger.dbg("Initialed!");
        } catch (Exception e) {
            logger.err("Failed to initialize!");
            logger.err(e.getMessage());
        }
    }

    private static boolean resolveArguments(String[] args) {
        logger.log("Resolving arguments...");
        for (int i = 0; i < args.length; i++) {
            String argument = args[i];
            if (argument.equalsIgnoreCase("--api-key") || argument.equalsIgnoreCase("-a")) {
                if (i + 1 < args.length) {
                    apiKey = args[i + 1];
                    i++;
                } else {
                    logger.err("You need to specify an api key!");
                    return false;
                }
            }
        }
        return true;
    }

    public static ApexLogger getLogger() {
        return logger;
    }
}