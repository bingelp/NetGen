package com.scires.netgen;

import org.jdesktop.swingx.JXDatePicker;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    private static String TAG               = " :: RouterDatePicker";

    public RouterDatePicker(){
        sdf = new SimpleDateFormat(ROUTER_DATE_FORMAT);
		//sets display format to CISCO style
        this.setFormats(ROUTER_DATE_FORMAT);
    }

    public String getRouterTime(){
        return this.getEditor().getText();
    }

    public void makeTimeFromRouter(String routerTime){
		//Converts times from config files into valid JXDatePicker format
        if( routerTime.matches("infinite"))
            this.getEditor().setText(routerTime);
        else{
            Date date = null;
            try{
                date = sdf.parse(routerTime);
            }catch (Exception e){
                new ErrorDialog(Parser.ERROR + e.getMessage() + TAG);
            }
            if (date != null){
                this.setDate(date);
            }
        }
    }
}
