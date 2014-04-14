package com.scires.netgen;


import java.awt.*;

/**
 * Created by Justin on 2/19/14.
 * <P>Application to import and edit Cisco configuration files</P>
 *
 * @author Justin Robinson
 * @version 0.0.8
 */
public class NetGen {
	private static final String REGEX_3_IP_OCTETS		=
		"\\b(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)" +
		"\\.(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)" +
		"\\.(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)";
	private static final String REGEX_3_HOST_IP_OCTETS	=
		"\\b(25[0-4]|2[0-4]\\d|[01]?\\d\\d?)" +
		"\\.(25[0-4]|2[0-4]\\d|[01]?\\d\\d?)" +
		"\\.(25[0-4]|2[0-4]\\d|[01]?\\d\\d?)";
	public static final String PROGRAM_NAME             = "NetGen";
    public static final String PROGRAM_VERSION          = "0.2.2";
    public static final int TAB_GLOBAL                  = 0; //Domain name, name server, secret, username, pwd, vtp pwd, logging
    public static final int TAB_HOST_NAME               = 1;
    public static final int TAB_KEY_CHAIN               = 2;
    public static final int TAB_INTERFACE               = 3;
    public static final int TAB_ROUTER                  = 4;
    public static final int TAB_ACCESS_LIST             = 5;
    public static final int TAB_NTP_PEER                = 6;
    public static final Color COLOR_ERROR               = new Color(255, 75, 75);
    public static final Color COLOR_DEFAULT             = new Color(150, 200, 255);
	public static final Color COLOR_GREEN				= new Color(0, 255, 100);
    public static final String REGEX_IP_GENERIC         =
		REGEX_3_IP_OCTETS +"\\.(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\b";
    public static final String REGEX_IP_GENERIC_LINE    =
		".*"+ REGEX_IP_GENERIC +".*";
    public static final String REGEX_IP_HOST            =
		REGEX_3_HOST_IP_OCTETS + "\\.(25[0-4]|2[0-4]\\d||[1]\\d\\d|[1-9]\\d?)\\b";
    public static final String REGEX_IP_GATEWAY         =
		REGEX_3_IP_OCTETS + "\\.0";
    public static final String REGEX_KEY_TIME           =
		"(?:((?:\\d{2}:){2}\\d{2} " +
		"(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) " +
		"\\d{1,2} \\d{4}|infinite))";
    public static final String REGEX_COC_PWD            =
		"(?=^.{15,}$)" +													// at least 15 characters
		"(?=.*\\d.*\\d)" +													// at least 2 digits
		"(?=.*[a-z].*[a-z])" +												// at least 2 lower case letters
		"(?=.*[A-Z].*[A-Z])" +												// at least 2 upper case letters
		"(?=.*[!@#$%^;*()_+}{&\":;'?/><].*[!@#$%^;*()_+}{&\":;'?/><])" +	// at least 2 special characters
		"(?!.*\\s).*$";														//
    public static final String REGEX_NOT_BLANK          = ".{1,}$";

    public static void main (String[] args){
		new GUI();
    }
}
