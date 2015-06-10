package com.quollwriter.editors.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import com.quollwriter.*;

import com.quollwriter.data.*;

import com.quollwriter.ui.*;
import com.quollwriter.ui.components.ActionAdapter;
import com.quollwriter.editors.ui.panels.*;

public class DeleteCommentActionHandler extends ActionAdapter
{

    private EditorProjectViewer projectViewer = null;
    private Note                item = null;
    private boolean             showAtItem = false;
    
    public DeleteCommentActionHandler (Note                item,
                                       EditorProjectViewer projectViewer,
                                       boolean             showAtItem)
    {

        this.item = item;
        this.projectViewer = projectViewer;
        this.showAtItem = showAtItem;

    }

    public void actionPerformed (ActionEvent ev)
    {

        final DeleteCommentActionHandler _this = this;
        
        Point showAt = null;
        
        // Sigh...
        if (this.showAtItem)
        {
            
            // Get the editor panel.
            EditorChapterPanel p = this.projectViewer.getEditorForChapter (this.item.getChapter ());
            
            if (p != null)
            {
            
                showAt = p.getIconColumn ().getLocation (this.item);
                
                showAt = SwingUtilities.convertPoint (p,
                                                      showAt,
                                                      this.projectViewer);
                
                showAt.y -= p.getScrollOffset ();

            }
            
        }
       
        UIUtils.createQuestionPopup (this.projectViewer,
                                     "Delete {Comment}",
                                     Constants.DELETE_ICON_NAME,
                                     String.format ("Please confirm you wish to delete {Comment}<br />&#160;&#160;&#160;<b>%s</b>?",
                                                    this.item.getSummary ()),
                                     "Yes, delete it",
                                     null,
                                     new ActionListener ()
                                     {
                                        
                                        public void actionPerformed (ActionEvent ev)
                                        {

                                            _this.deleteItem ();
                                                                                
                                        }
                                        
                                     },
                                     null,
                                     null,
                                     showAt);

    }

    private void deleteItem ()
    {
        
        try
        {

            this.projectViewer.deleteObject (this.item,
                                             false);
        
        } catch (Exception e)
        {

            Environment.logError ("Unable to delete comment: " +
                                  this.item,
                                  e);

            com.quollwriter.ui.UIUtils.showErrorMessage (this.projectViewer,
                                                         "Unable to delete.");

        }        
        
    }
        
}
