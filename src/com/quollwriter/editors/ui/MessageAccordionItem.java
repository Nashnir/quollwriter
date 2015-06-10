package com.quollwriter.editors.ui;

import java.awt.Component;
import java.awt.Point;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.*;
import java.awt.dnd.*;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Set;
import java.util.LinkedHashSet;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.tree.*;

import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

import com.quollwriter.data.*;
import com.quollwriter.data.editors.*;
import com.quollwriter.*;
import com.quollwriter.events.*;
import com.quollwriter.editors.messages.*;
import com.quollwriter.ui.*;
import com.quollwriter.ui.actionHandlers.*;
import com.quollwriter.ui.components.ActionAdapter;
import com.quollwriter.ui.components.Header;
import com.quollwriter.ui.components.ImagePanel;
import com.quollwriter.ui.renderers.*;

public class MessageAccordionItem extends AccordionItem
{
        
    private AbstractProjectViewer projectViewer = null;
    private Box content = null;
    private Set<MessageBox> messageBoxes = new LinkedHashSet ();
    private Date date = null;
    private EditorMessage lastMessage = null;
        
    public MessageAccordionItem (AbstractProjectViewer pv,
                                 Date                  d,
                                 Set<EditorMessage>    messages)
    {
        
        super ("",
               null);
                
        this.date = d;
        
        if (messages == null)
        {
            
            messages = new LinkedHashSet ();
            
        }
                
        int c = messages.size ();
                
        this.setTitle (Environment.formatDate (d) + " (" + c + ")");
        this.setIconType (null);

        this.projectViewer = pv;

        final MessageAccordionItem _this = this;
        
        this.content = new Box (BoxLayout.Y_AXIS);
                
        Header h = this.getHeader ();
                      
        h.setTitleColor (UIUtils.getColor ("#aaaaaa"));
        h.setFontSize (14);
                                        
        h.setBorder (new CompoundBorder (new CompoundBorder (new MatteBorder (0, 0, 1, 0, UIUtils.getColor ("#dddddd")),
                                                             new EmptyBorder (0, 0, 3, 0)),
                                         h.getBorder ()));

        //this.content.add ();
        this.content.setBorder (UIUtils.createPadding (5, 0, 10, 0));
        
        for (EditorMessage m : messages)
        {
                        
            JComponent mb = this.buildMessageBox (m);
            
            if (mb == null)
            {
                
                continue;
                
            }

            this.content.add (mb);
            
            this.lastMessage = m;
            
        }
            
        this.content.add (Box.createVerticalGlue ());

    }
    
    private JLabel createLabel (String m)
    {
        
        JLabel l = new JLabel (m);
        l.setForeground (UIUtils.getColor ("#aaaaaa"));
        
        return l;
        
    }
    
    public void addMessage (EditorMessage m)
    {
        
        JComponent mb = this.buildMessageBox (m);
        
        if (mb == null)
        {
            
            return;
            
        }
        
        this.content.add (mb);
                
        int c = this.messageBoxes.size ();
        
        this.setTitle (Environment.formatDate (this.date) + " (" + c + ")");        
        
        this.validate ();
        this.repaint ();
        
    }
        
    public MessageBox getMessageBoxForMessage (EditorMessage m)
    {
        
        for (MessageBox mb : this.messageBoxes)
        {
        
            if (mb.getMessage () == m)
            {
                
                return mb;
                
            }
                            
        }
        
        return null;
        
    }
    
    private JComponent buildMessageBox (EditorMessage m)
    {
        
        MessageBox mb = null;
        
        try
        {
        
            mb = MessageBoxFactory.getMessageBoxInstance (m,
                                                          this.projectViewer);

        } catch (Exception e) {
            
            Environment.logError ("Unable to get message box for message: " +
                                  m,
                                  e);
            
            return null;
            
        }

        if (mb != null)
        {
            
            try
            {
            
                mb.init ();
                
            } catch (Exception e) {
                
                Environment.logError ("Unable to init message box for message: " +
                                      m,
                                      e);
                
                return null;
                
            }
            
        } else {
          
            return null;
            
        }
        
        this.messageBoxes.add (mb);
                
        Box b = new Box (BoxLayout.Y_AXIS);
        b.setAlignmentX (Component.LEFT_ALIGNMENT);

        Box details = new Box (BoxLayout.X_AXIS);
        details.setAlignmentX (Component.LEFT_ALIGNMENT);
        
        String name = m.getEditor ().getMainName ();
        
        if (m.isSentByMe ())
        {
            
            name = "Me";
            
        }
        
        details.add (this.createLabel (name));
        details.add (Box.createHorizontalGlue ());
        details.add (this.createLabel (Environment.formatTime (m.getWhen ())));
        b.add (details);
        
        b.add (Box.createVerticalStrut (5));
        
        mb.setAlignmentX (Component.LEFT_ALIGNMENT);
        //p.setMaximumSize (null);
        mb.setBorder (new CompoundBorder (mb.getBorder (),
                                          new EmptyBorder (0, 5, 0, 0)));
    
        b.add (mb);
                
        b.setBorder (new EmptyBorder (0, 5, 3, 0));

        details.setBorder (new CompoundBorder (new MatteBorder (0, 0, 1, 0, UIUtils.getColor ("#dddddd")),
                                                             new EmptyBorder (5, 0, 5, 0)));
        
        
        return b;
        
    }
                        
    public JComponent getContent ()
    {
        
        return this.content;
                
    }
        
    public void init ()
    {
        
        super.init ();
                
    }
    
}