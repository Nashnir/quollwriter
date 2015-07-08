package com.quollwriter.editors.ui;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

import java.awt.event.*;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.*;
import javax.swing.border.*;

import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

import com.quollwriter.*;
import com.quollwriter.ui.*;
import com.quollwriter.data.*;
import com.quollwriter.data.editors.*;
import com.quollwriter.ui.components.ImagePanel;
import com.quollwriter.editors.messages.*;
import com.quollwriter.editors.*;

public class InviteMessageBox extends MessageBox<InviteMessage>
{
        
    private Box responseBox = null;
        
    public InviteMessageBox (InviteMessage         mess,
                             AbstractProjectViewer viewer)
    {
        
        super (mess,
               viewer);
        
    }
    
    @Override
    public boolean isShowAttentionBorder ()
    {
        
        return false;
        
    }
    
    @Override
    public boolean isAutoDealtWith ()
    {
        
        return false;
        
    }    
        
    public void doUpdate ()
    {
        
        this.responseBox.setVisible (!this.message.isDealtWith ());
                
    }
    
    public void doInit ()
    {
        
        final InviteMessageBox _this = this;
                                        
        if ((!this.message.isDealtWith ())
            &&
            (!this.message.isSentByMe ())
           )
        {
            
            this.responseBox = new Box (BoxLayout.Y_AXIS);
            
            this.add (this.responseBox);
                        
            JComponent l = UIUtils.createBoldSubHeader ("Select your response below",
                                                        null);
            
            this.responseBox.add (l);           
            
            JButton accept = UIUtils.createButton ("Accept",
                                                   null);            
            
            accept.setToolTipText ("Click to accept the invitation");
            accept.addActionListener (new ActionListener ()
            {
                 
                 public void actionPerformed (ActionEvent ev)
                 {
                     
                     _this.handleResponse (true);
                     
                 }
                 
            });
            
            JButton reject = UIUtils.createButton ("Reject",
                                                   null);
            reject.setToolTipText ("Click to reject the invitation");
            reject.addActionListener (new ActionListener ()
            {
                 
                 public void actionPerformed (ActionEvent ev)
                 {
                           
                     _this.handleResponse (false);
                     
                 }
                 
            });

            JButton[] buts = new JButton[] { accept, reject };
                            
            JComponent bb = UIUtils.createButtonBar2 (buts,
                                                      Component.LEFT_ALIGNMENT);
                        
            bb.setAlignmentX (Component.LEFT_ALIGNMENT);
            
            this.responseBox.add (bb); 
            
        }
                     
    }
    
    private void handleResponse (boolean accepted)
    {
        
        final InviteMessageBox _this = this;

        EditorEditor ed = this.message.getEditor ();
        
        InviteResponseMessage rm = new InviteResponseMessage (accepted,
                                                              EditorsEnvironment.getUserAccount ());
        rm.setEditor (ed);
        
        ActionListener onComplete = new ActionListener ()
        {
            
            public void actionPerformed (ActionEvent ev)
            {
                
                _this.message.setDealtWith (true);
                
                try
                {
                
                    EditorsEnvironment.updateMessage (_this.message);
                    
                } catch (Exception e) {
                    
                    Environment.logError ("Unable to update message: " +
                                          _this.message,
                                          e);
                    
                    UIUtils.showErrorMessage (_this.projectViewer,
                                              "Unable to update invite, please contact Quoll Writer support for assistance.");
                    
                }
                
            }
            
        };
                
        if (accepted)
        {
        
            EditorsEnvironment.acceptInvite (ed,
                                             rm,
                                             onComplete);

        } else {
            
            EditorsEnvironment.rejectInvite (ed,
                                             rm,
                                             onComplete);
            
        }
                
    }
}