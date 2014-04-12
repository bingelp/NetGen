package com.scires.netgen;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Created by Justin on 2/26/14.
 *
 *
 * @author Justin Robinson
 * @version 0.0.5
 */
public class PanelGroup extends MinimumPanel{
    public PanelGroup(String groupKey){
        TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), groupKey);
        title.setTitleJustification(TitledBorder.LEFT);
        this.setBorder(title);
        this.setLayout(new GridLayout(0, 1));
    }
}
