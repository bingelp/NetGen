package com.scires.netgen;


import java.awt.*;

/**
 * Created by Justin on 2/19/14.
 * <P>Application to import and edit Cisco configuration files</P>
 *
 * @author Justin Robinson
 * @version 0.0.6
 */
public class NetGen {
    public final static String PROGRAM_NAME = "Netgen";
    public final static String PROGRAM_VERSION = "0.1.1";
    public final static Color COLOR_ERROR = new Color(255, 75, 75);
    public final static Color COLOR_DEFAULT = new Color(150, 200, 255);
    private static String REGEX_THREE_IP_OCTETS =
            "\\b(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)" +
                    "\\.(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)" +
                    "\\.(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)";
    private static String REGEX_THREE_HOST_IP_OCTETS =
            "\\b(25[0-4]|2[0-4]\\d|[01]?\\d\\d?)" +
                    "\\.(25[0-4]|2[0-4]\\d|[01]?\\d\\d?)" +
                    "\\.(25[0-4]|2[0-4]\\d|[01]?\\d\\d?)";
    public final static String REGEX_IP_GENERIC = REGEX_THREE_IP_OCTETS +"\\.(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\b";
    public final static String REGEX_IP_GENERIC_LINE = ".*"+ REGEX_IP_GENERIC +".*";
    public final static String REGEX_IP_HOST = REGEX_THREE_HOST_IP_OCTETS + "\\.(25[0-4]|2[0-4]\\d||[1]\\d\\d|[1-9]\\d?)\\b";
    public final static String REGEX_IP_GATEWAY = REGEX_THREE_IP_OCTETS + "\\.0";
    public final static String REGEX_KEY_TIME =
            "(?:((?:\\d{2}:){2}\\d{2} " +
                    "(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) " +
                    "\\d{1,2} \\d{4}|infinite))";
    public final static String REGEX_COC_PWD = "(?=^.{15,}$)" +
            "(?=.*\\d.*\\d)" +
            "(?=.*[a-z].*[a-z])" +
            "(?=.*[A-Z].*[A-Z])" +
            "(?=.*[!@#$%^;*()_+}{&\":;'?/><].*[!@#$%^;*()_+}{&\":;'?/><])" +
            "(?!.*\\s).*$";
    public final static String REGEX_NOT_BLANK = ".{1,}$";

    public static void main (String[] args){
        String[] split = System.getProperty("java.version").split("\\.");
        float version = Float.parseFloat(split[0]);
        version += Float.parseFloat(split[1])/10;
        if(version >= 1.7)
            new IPGUI();
        else
            new ErrorDialog("Java version 1.7 or greater is required to run NetGen");
    }
}
