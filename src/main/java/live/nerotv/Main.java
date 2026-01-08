package live.nerotv;

import live.nerotv.jsonexplorer.APIExplorer;
import org.zyneonstudios.apex.utilities.ApexUtilities;
import org.zyneonstudios.apex.utilities.logger.ApexLogger;

public class Main {

    private static ApexLogger logger;
    private static String apiKey;
    private static APIExplorer explorer;
    private static boolean oldFrame = false;

    static void main(String[] a) {
        logger = new ApexLogger("JSON-Explorer");
        logger.log("JSON-Explorer by nerotvlive: https://a.nerotv.live");
        if(resolveArguments(a)) {
            logger.log("Starting JSON-Explorer...");
            ApexUtilities.initDesktop();
            explorer = new APIExplorer(apiKey,oldFrame);
            apiKey = null;
        }
    }

    private static boolean resolveArguments(String[] args) {
        logger.log("Resolving arguments...");
        for(int i = 0; i < args.length; i++) {
            String argument = args[i];
            if(argument.equalsIgnoreCase("--api-key")||argument.equalsIgnoreCase("-a")) {
                try {
                    apiKey = args[i+1];
                } catch (Exception e) {
                    logger.err("You need to specify an api key when using -a or --api-key!");
                    return false;
                }
            } else if(argument.equalsIgnoreCase("--old")||argument.equalsIgnoreCase("-o")) {
                oldFrame = true;
            }
        }
        if(apiKey==null) {
            apiKey = "";
        }
        return true;
    }

    public static ApexLogger getLogger() {
        return logger;
    }

    public static void setAPIKey(String newApiKey) {
        explorer.setAPIKey(newApiKey);
    }
}