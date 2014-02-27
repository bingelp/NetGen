package com.scires.netgen;

import java.util.ArrayList;

/**
 * Created by Justin on 2/25/14.
 *
 * <P>Contains target and replacement text along with {@link com.scires.netgen.Location } of the targets</P>
 *
 * @author Justin Robinson
 * @version 0.0.3
 */
public class Entry {
	private static String THREE_IP_OCTETS =
			"\\b(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)" +
			"\\.(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)" +
			"\\.(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)";
	static String IP_GENERIC = THREE_IP_OCTETS+"\\.(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\b";
	static String IP_GENERIC_LINE = ".*"+IP_GENERIC+".*";
	static String IP_HOST = THREE_IP_OCTETS + "\\.(25[0-5]|2[0-4]\\d|[01]?[1-9]\\d?)\\b";
	static String IP_GATEWAY = THREE_IP_OCTETS + "\\.0";
	static String KEY_TIME =
			"(?:((?:\\d{2}:){2}\\d{2} " +
			"(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) " +
			"\\d{1,2} \\d{4}|infinite)( )?){2}";
	static String COC_PWD = "(?=^.{15,}$)" +
			"(?=.*\\d.*\\d)" +
			"(?=.*[a-z].*[a-z])" +
			"(?=.*[A-Z].*[A-Z])" +
			"(?=.*[!@#$%^;*()_+}{&\":;'?/><].*[!@#$%^;*()_+}{&\":;'?/><])" +
			"(?!.*\\s).*$";

	String target = null;
	String replacement = null;
	String labelText = null;
	ArrayList<Location> locations = null;
	String regex = null;

	public Entry(){
		locations = new ArrayList<Location>();
	}

	public void setTarget(String target){this.target = target;}
	public void setLabelText(String text){this.labelText = text;}
	public void setRegex(String regex){this.regex = regex;}
}
