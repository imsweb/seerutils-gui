/*
 * Copyright 2008 Ayman Al-Sairafi ayman.alsairafi@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License
 *       at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.imsweb.seerutilsgui.editor;

import java.awt.Color;
import java.util.Properties;

import javax.swing.JEditorPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.JTextComponent;

public class ComponentPairsMarker implements CaretListener, ComponentSyntax {

    private JTextComponent _pane;

    private SyntaxUtils.SimpleMarker _marker;

    @Override
    public void caretUpdate(CaretEvent e) {
        removeMarkers();
        int pos = e.getDot();
        SyntaxDocument doc = SyntaxUtils.getSyntaxDocument(_pane);
        if (doc != null) {
            LexerToken token = doc.getTokenAt(pos);
            if (token != null && token.pairValue != 0) {
                SyntaxUtils.markToken(_pane, token, _marker);
                LexerToken other = doc.getPairFor(token);
                if (other != null) {
                    SyntaxUtils.markToken(_pane, other, _marker);
                }
            }
        }
    }

    /**
     * Remove all the highlights from the editor pane.  This should be called when the editorkit is removed.
     */
    public void removeMarkers() {
        SyntaxUtils.removeMarkers(_pane, _marker);
    }

    @Override
    public void install(JEditorPane editor, Properties config) {
        _pane = editor;
        _marker = new SyntaxUtils.SimpleMarker(Color.ORANGE);

        _pane.addCaretListener(this);
    }

    @Override
    public void deinstall(JEditorPane editor) {
        _pane.removeCaretListener(this);
        removeMarkers();
    }
}
