package am.fats;

/**
 * Version information for JXPlotterSVG
 */
public class Version {
    private static final String VERSION = "1.3.0";
    private static final String BUILD_DATE = "2025-07-04";
    
    /**
     * Returns the current version of the application
     * @return Version string
     */
    public static String version() {
        return VERSION;
    }
    
    /**
     * Returns the build date of the application
     * @return Build date string
     */
    public static String buildDate() {
        return BUILD_DATE;
    }
    
    /**
     * Returns full version information including version and build date
     * @return Full version information
     */
    public static String fullVersion() {
        return VERSION + " (" + BUILD_DATE + ")";
    }
}