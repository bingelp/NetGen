package com.scires.netgen;

import org.jdesktop.swingx.JXDatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Justin on 3/5/14.
 *
 * <P>Calendar GUI item that can convert from Java @{link Calendar} time format to CISCO format</P>
 *
 * @author Justin Robinson
 * @version 0.0.5
 */
public class RouterDatePicker extends JXDatePicker {
	public static String ROUTER_DATE_FORMAT = "HH:mm:ss MMM d yyyy";
	private SimpleDateFormat sdf;
	private static String TAG = " :: RouterDatePicker";

	public RouterDatePicker(){
		sdf = new SimpleDateFormat(ROUTER_DATE_FORMAT);
	}

	public String getRouterTime(){
		String routerTime;
		if( this.isInfinite() ){
			return this.getEditor().getText();
		}else{
			Calendar cal = new GregorianCalendar();
			cal.setTime(this.getDate());
			routerTime = sdf.format(cal.getTime());
		}
		return routerTime;
	}

	public void makeTimeFromRouter(String routerTime){
		if( routerTime.matches("infinite"))
			this.getEditor().setText(routerTime);
		else{
			Date date = null;
			try{
				date = sdf.parse(routerTime);
			}catch (Exception e){
				System.out.println(Parser.ERROR + e.getMessage() + TAG);
			}

			if (date != null){
				this.setDate(date);
			}
		}
	}

	public boolean isInfinite(){
		return this.getEditor().getText().matches("infinite");
	}
}
