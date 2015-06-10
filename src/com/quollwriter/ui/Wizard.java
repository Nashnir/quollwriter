package com.quollwriter.ui;

import java.awt.*;
import java.awt.event.*;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import com.gentlyweb.utils.*;

import com.jgoodies.forms.factories.*;

import com.quollwriter.*;

import com.quollwriter.ui.components.*;

public abstract class Wizard extends Box
{

    private boolean                 inited = false;
    private WizardStep              current = null;
    private String                  currentStage = null;
    private Box                     contentPanel = null;
    private Map<String, WizardStep> stages = new LinkedHashMap ();
    private JButton                 cancelBut = null;
    private JButton                 nextBut = null;
    private JButton                 prevBut = null;
    private Header                  header = null;
    private JTextPane               helpText = null;
    protected AbstractProjectViewer   projectViewer = null;
    
    public Wizard (AbstractProjectViewer pv)
    {
        
        super (BoxLayout.Y_AXIS);
        
        this.contentPanel = new Box (BoxLayout.Y_AXIS);
        
        this.projectViewer = pv;
        
    }

    public int getContentPreferredHeight ()
    {
        
        return 250;
        
    }
    
    public String getCurrentStage ()
    {
        
        return this.currentStage;
        
    }
    
    public void resize ()
    {
        
        UIUtils.resizeParent (this.getParent ());
        
    }
    
    public void init ()
    {

        if (this.inited)
        {
            
            return;
            
        }
    
        final Wizard _this = this;
    
        this.helpText = UIUtils.createHelpTextPane (null,
                                                    this.projectViewer);

        this.helpText.setBorder (null);
                                                    
        this.add (this.helpText);
        this.add (Box.createVerticalStrut (10));        
        this.setAlignmentX (JComponent.LEFT_ALIGNMENT);

        this.header = UIUtils.createHeader ("",
                                         Constants.SUB_PANEL_TITLE,
                                         null,
                                         null);

        this.header.setBorder (UIUtils.createBottomLineWithPadding (0, 0, 2, 0));
        
        this.header.setAlignmentX (JComponent.LEFT_ALIGNMENT);
        this.add (Box.createVerticalStrut (10));
        this.add (this.header);
        this.add (Box.createVerticalStrut (10));
        this.add (this.contentPanel);
        this.contentPanel.setAlignmentX (JComponent.LEFT_ALIGNMENT);
        this.contentPanel.setAlignmentY (JComponent.TOP_ALIGNMENT);

        this.contentPanel.setPreferredSize (new Dimension (UIUtils.getPopupWidth (),
                                                           this.getContentPreferredHeight ()));

        this.add (Box.createVerticalStrut (10));        
        
        this.prevBut = new JButton ();
        this.prevBut.setText ("< Back");
        this.prevBut.setEnabled (false);

        this.prevBut.addActionListener (new ActionAdapter ()
            {

                public void actionPerformed (ActionEvent ev)
                {

                    WizardStep ws = null;

                    String prev = _this.getPreviousStage (_this.currentStage);
                    
                    if (prev != null)
                    {

                        ws = _this.stages.get (prev);

                        if (ws == null)
                        {

                            try
                            {

                                ws = _this.getStage (prev);

                            } catch (Exception e)
                            {

                                Environment.logError ("Unable to get stage for: " +
                                                      prev,
                                                      e);

                            }

                            _this.stages.put (prev,
                                              ws);

                        }

                        if (!_this.handleStageChange (_this.currentStage,
                                                      prev))
                        {

                            return;

                        }

                        if (_this.current.panel != null)
                        {
                        
                            _this.contentPanel.remove (_this.current.panel);
                            
                        }

                        _this.current = ws;
                        _this.currentStage = prev;

                        _this.enableButtons ();
                        
                        _this.initUI ();

                    }

                    /*
                            _this.setSize (new Dimension (_this.getSize ().width,
                                                          _this.getPreferredSize ().height));
                     */
                    _this.repaint ();

                }

            });

        this.nextBut = new JButton ();
        this.nextBut.setText ("Next >");

        this.nextBut.addActionListener (new ActionAdapter ()
            {

                public void actionPerformed (ActionEvent ev)
                {

                    WizardStep ws = null;

                    String next = _this.getNextStage (_this.currentStage);
                    
                    if (next != null)
                    {

                        ws = _this.stages.get (next);

                        if (ws == null)
                        {

                            try
                            {

                                ws = _this.getStage (next);

                            } catch (Exception e)
                            {

                                Environment.logError ("Unable to get stage: " +
                                                      next,
                                                      e);

                            }

                            if (ws != null)
                            {

                                _this.stages.put (next,
                                                  ws);

                            }

                        }

                        if (!_this.handleStageChange (_this.currentStage,
                                                      next))
                        {

                            return;

                        }

                        if (_this.current.panel != null)
                        {
                            
                            _this.contentPanel.remove (_this.current.panel);
                            
                        }

                        _this.current = ws;
                        _this.currentStage = next;

                        _this.enableButtons ();
                        
                        _this.initUI ();

                    } else
                    {

                        if (_this.handleFinish ())
                        {

                            _this.setVisible (false);

                            UIUtils.closePopupParent (_this.getParent ());
                            
                            return;

                        }

                    }

                    _this.repaint ();

                }

            });

        this.cancelBut = new JButton ();
        this.cancelBut.setText ("Cancel");

        final ActionAdapter cancel = new ActionAdapter ()
        {

            public void actionPerformed (ActionEvent ev)
            {

                _this.handleCancel ();

                _this.setVisible (false);
                
                UIUtils.closePopupParent (_this.getParent ());
                
            }

        };

        this.cancelBut.addActionListener (cancel);

        JButton[] buts = { this.prevBut, this.nextBut, this.cancelBut };

        JPanel bp = UIUtils.createButtonBar2 (buts,
                                              Component.CENTER_ALIGNMENT);
        bp.setOpaque (false);
        bp.setBorder (new EmptyBorder (0,
                                       0,
                                       0,
                                       0));
        bp.setAlignmentX (JComponent.LEFT_ALIGNMENT);
        this.add (bp);

        //this.add (Box.createVerticalStrut (5));        
        
        String startStage = this.getStartStage ();

        // Get the stage.
        WizardStep ws = this.getStage (startStage);
        this.stages.put (startStage,
                         ws);

        this.current = ws;
        this.currentStage = startStage;

        this.initUI ();

        this.enableButtons ();

        this.handleStageChange (null,
                                this.currentStage);

        this.inited = true;
                                
    }

    public void showStage (String stage)
    {
        
        WizardStep ws = this.stages.get (stage);
        
        if ((ws == null)
            ||
            (ws.alwaysRefreshPanel)
           )
        {        
        
            try
            {

                ws = this.getStage (stage);

            } catch (Exception e)
            {

                Environment.logError ("Unable to get stage for: " +
                                      stage,
                                      e);

            }

            this.stages.put (stage,
                             ws);

        } 

        if (!this.handleStageChange (this.currentStage,
                                     stage))
        {

            return;

        }
        
        if (this.current.panel != null)
        {
        
            this.contentPanel.remove (this.current.panel);
            
        }

        this.current = ws;
        this.currentStage = stage;

        this.enableButtons ();
        
        this.initUI ();
                        
    }
    
    public JButton[] getButtons ()
    {

        JButton[] buts = { this.prevBut, this.nextBut, this.cancelBut };

        return buts;

    }

    public void enableButton (String  name,
                              boolean enable)
    {

        if ((name.equals ("next")) ||
            (name.equals ("finish")))
        {

            this.nextBut.setEnabled (enable);

        }

        if (name.equals ("previous"))
        {

            this.prevBut.setEnabled (enable);

        }

        if (name.equals ("cancel"))
        {

            this.cancelBut.setEnabled (enable);

        }

    }

    private void initUI ()
    {

        this.header.setTitle (Environment.replaceObjectNames (this.current.title));

        if (this.current.helpText == null)
        {

            this.helpText.setText (this.getFirstHelpText ());

        } else
        {

            this.helpText.setText (this.current.helpText);

        }

        if (this.current.panel != null)
        {
        
            this.current.panel.setOpaque (false);
            this.current.panel.setAlignmentY (Component.TOP_ALIGNMENT);
            this.contentPanel.add (this.current.panel);

        }
        
        UIUtils.resizeParent (this.getParent ());        
            
    }

    //public abstract int getMaximumContentHeight ();

    public abstract String getNextStage (String currStage);

    public abstract String getPreviousStage (String currStage);

    public abstract String getStartStage ();

    public abstract boolean handleStageChange (String oldStage,
                                               String newStage);

    public abstract boolean handleFinish ();

    public abstract void handleCancel ();

    public abstract String getFirstHelpText ();
    
    public abstract WizardStep getStage (String stage);
    
    public String getNextButtonLabel (String currStage)
    {
        
        String next = this.getNextStage (currStage);
        
        if (next == null)
        {
            
            return "Finish";
        
        }
        
        return "Next >";
        
    }
    
    protected void enableButtons ()
    {

        String prev = this.getPreviousStage (this.currentStage);
        String next = this.getNextStage (this.currentStage);

        if (this.current != null)
        {

            this.prevBut.setEnabled (prev != null);
            this.nextBut.setEnabled (next != null);

        }

        if (next == null)
        {

            this.nextBut.setEnabled (true);

        }
        
        this.nextBut.setText (this.getNextButtonLabel (this.currentStage));

    }

}
