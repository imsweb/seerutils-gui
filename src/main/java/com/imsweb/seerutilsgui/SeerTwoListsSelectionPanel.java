/*
 * Copyright (C) 2012 Information Management Services, Inc.
 */
package com.imsweb.seerutilsgui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

@SuppressWarnings({"unchecked", "unused"})
public class SeerTwoListsSelectionPanel<E> extends JPanel implements ActionListener {

    private final List<JButton> _orderedButtons;
    private final SeerList<E> _leftList;
    private final SeerList<E> _rightList;
    private final JScrollPane _leftPane;
    private final JScrollPane _rightPane;
    private final JButton _moveLeftBtn;
    private final JButton _moveRightBtn;
    private final JButton _moveAllLeftBtn;
    private final JButton _moveAllRightBtn;
    private JButton _moveUpBtn;
    private JButton _moveDownBtn;
    private JButton _moveFirstBtn;
    private JButton _moveLastBtn;
    private final JPanel _centerPnl;
    private final JPanel _leftPnl;
    private final JPanel _rightPnl;
    private JTextField _leftFilterFld;
    private JTextField _rightFilterFld;

    public static final int DEFAULT_FILTER = SeerList.FILTERING_MODE_CONTAINED;

    /**
     * Constructor.
     * <p/>
     * Created on Dec 31, 2010 by depryf
     * @param leftItems list of items to add to the LEFT list, can be empty but not null
     * @param rightItems list of items to add to the RIGHT list, can be empty but not null
     */
    public SeerTwoListsSelectionPanel(List<E> leftItems, List<E> rightItems) {
        this(leftItems, rightItems, null, null, null, null);
    }

    /**
     * Constructor.
     * <p/>
     * Created on Dec 31, 2010 by depryf
     * @param leftItems list of items to add to the LEFT list, can be empty but not null
     * @param rightItems list of items to add to the RIGHT list, can be empty but not null
     * @param leftText text for the left header
     * @param rightText text for the right header
     */
    public SeerTwoListsSelectionPanel(List<E> leftItems, List<E> rightItems, String leftText, String rightText) {
        this(leftItems, rightItems, leftText == null ? null : SeerGuiUtils.createLabel(leftText), rightText == null ? null : SeerGuiUtils.createLabel(rightText));
    }

    /**
     * Constructor.
     * <p/>
     * Created on Dec 31, 2010 by depryf
     * @param leftItems list of items to add to the LEFT list, can be empty but not null
     * @param rightItems list of items to add to the RIGHT list, can be empty but not null
     * @param leftLbl label for the left header (optional)
     * @param rightLbl label for the right header (optional)
     */
    public SeerTwoListsSelectionPanel(List<E> leftItems, List<E> rightItems, JLabel leftLbl, JLabel rightLbl) {
        this(leftItems, rightItems, leftLbl, rightLbl, null, null);
    }

    /**
     * Constructor.
     * <p/>
     * Created on Dec 31, 2010 by depryf
     * @param leftItems list of items to add to the LEFT list, can be empty but not null
     * @param rightItems list of items to add to the RIGHT list, can be empty but not null
     * @param leftLbl label for the left header (optional)
     * @param rightLbl label for the right header (optional)
     * @param leftComp comparator for the left list (optional)
     * @param rightComp comparator for the right list (optional)
     */
    public SeerTwoListsSelectionPanel(List<E> leftItems, List<E> rightItems, JLabel leftLbl, JLabel rightLbl, Comparator<E> leftComp, Comparator<E> rightComp) {
        this(new SeerListModel<>(leftItems, DEFAULT_FILTER, leftComp), new SeerListModel<>(rightItems, DEFAULT_FILTER, rightComp), leftLbl, rightLbl, true, true);
    }

    /**
     * Constructor.
     * <p/>
     * Created on Dec 31, 2010 by depryf
     * @param leftItems list of items to add to the LEFT list, can be empty but not null
     * @param rightItems list of items to add to the RIGHT list, can be empty but not null
     * @param leftLbl label for the left header (optional)
     * @param rightLbl label for the right header (optional)
     * @param leftComp comparator for the left list (optional)
     * @param rightComp comparator for the right list (optional)
     * @param showFilter if true, a filter will be shown under both lists
     * @param addFocusBorder if true, the lists and filters will have a black border when they receive focus
     */

    public SeerTwoListsSelectionPanel(List<E> leftItems, List<E> rightItems, JLabel leftLbl, JLabel rightLbl, Comparator<E> leftComp, Comparator<E> rightComp, boolean showFilter, boolean addFocusBorder) {
        this(new SeerListModel<>(leftItems, DEFAULT_FILTER, leftComp), new SeerListModel<>(rightItems, DEFAULT_FILTER, rightComp), leftLbl, rightLbl, showFilter, addFocusBorder);
    }

    /**
     * Constructor.
     * <p/>
     * Created on Dec 31, 2010 by depryf
     * @param leftModel list model to add to the LEFT list, cannot be null
     * @param rightModel list model to add to the RIGHT list, cannot null
     * @param leftLbl label for the left header (optional)
     * @param rightLbl label for the right header (optional)
     * @param showFilter if true, a filter will be shown under both lists
     * @param addFocusBorder if true, the lists and filters will have a black border when they receive focus
     */

    public SeerTwoListsSelectionPanel(SeerListModel<E> leftModel, SeerListModel<E> rightModel, JLabel leftLbl, JLabel rightLbl, boolean showFilter, final boolean addFocusBorder) {
        this.setOpaque(false);
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        // LEFT - left items
        _leftPnl = SeerGuiUtils.createPanel();
        _leftPnl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        if (leftLbl != null)
            _leftPnl.add(leftLbl, BorderLayout.NORTH);
        _leftList = new SeerList<>(leftModel, SeerList.DISPLAY_MODE_NONE, false);
        _leftList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        _leftList.getSelectionModel().addListSelectionListener(e -> enableButtons());
        _leftList.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                _rightList.clearSelection();
                if (addFocusBorder)
                    _leftPane.setBorder(SeerGuiUtils.BORDER_FOCUS_IN);
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (addFocusBorder)
                    _leftPane.setBorder(SeerGuiUtils.BORDER_FOCUS_OUT);
            }
        });
        _leftPane = new JScrollPane(_leftList);
        _leftPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        _leftPnl.add(_leftPane, BorderLayout.CENTER);
        if (showFilter) {
            JPanel leftFilterPnl = SeerGuiUtils.createPanel();
            leftFilterPnl.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
            leftFilterPnl.add(SeerGuiUtils.createLabel("Filter"), BorderLayout.WEST);
            _leftFilterFld = new JTextField();
            _leftList.setFont(SeerGuiUtils.adjustFontSize(_leftFilterFld.getFont()));
            _leftFilterFld.setBorder(SeerGuiUtils.BORDER_TEXT_FIELD_OUT);
            _leftFilterFld.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    performApplyLeftFilter();
                }
            });
            if (addFocusBorder) {
                _leftFilterFld.addFocusListener(new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        _leftFilterFld.setBorder(SeerGuiUtils.BORDER_TEXT_FIELD_IN);
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        _leftFilterFld.setBorder(SeerGuiUtils.BORDER_TEXT_FIELD_OUT);
                    }
                });
            }
            JPanel leftFilterWrapperPnl = SeerGuiUtils.createPanel();
            leftFilterWrapperPnl.add(Box.createVerticalStrut(2), BorderLayout.NORTH);
            leftFilterWrapperPnl.add(Box.createHorizontalStrut(5), BorderLayout.WEST);
            leftFilterWrapperPnl.add(_leftFilterFld, BorderLayout.CENTER);
            leftFilterWrapperPnl.add(Box.createHorizontalStrut(5), BorderLayout.EAST);
            leftFilterWrapperPnl.add(Box.createVerticalStrut(2), BorderLayout.SOUTH);
            leftFilterPnl.add(leftFilterWrapperPnl, BorderLayout.CENTER);
            leftFilterPnl.add(SeerGuiUtils.createButton("Reset", "reset-left-filter", "Reset Filter", this), BorderLayout.EAST);
            _leftPnl.add(leftFilterPnl, BorderLayout.SOUTH);
        }
        this.add(_leftPnl);

        // CENTER - controls
        _orderedButtons = new ArrayList<>();
        _centerPnl = SeerGuiUtils.createPanel(new GridBagLayout());
        JPanel controlsPnl = SeerGuiUtils.createPanel();
        controlsPnl.setLayout(new BoxLayout(controlsPnl, BoxLayout.Y_AXIS));
        controlsPnl.add(Box.createVerticalStrut(15));
        _moveLeftBtn = SeerGuiUtils.createButton("Move Left", "move-left", "Move Selected Items Left", this);
        _orderedButtons.add(_moveLeftBtn);
        controlsPnl.add(_moveLeftBtn);
        controlsPnl.add(Box.createVerticalStrut(5));
        _moveRightBtn = SeerGuiUtils.createButton("Move Right", "move-right", "Move Selected Items Right", this);
        _orderedButtons.add(_moveRightBtn);
        controlsPnl.add(_moveRightBtn);
        controlsPnl.add(Box.createVerticalStrut(15));
        _moveAllLeftBtn = SeerGuiUtils.createButton("Move All Left", "move-all-left", "Move All Items Left", this);
        _orderedButtons.add(_moveAllLeftBtn);
        controlsPnl.add(_moveAllLeftBtn);
        controlsPnl.add(Box.createVerticalStrut(5));
        _moveAllRightBtn = SeerGuiUtils.createButton("Move All Right", "move-all-right", "Move All Items Right", this);
        _orderedButtons.add(_moveAllRightBtn);
        controlsPnl.add(_moveAllRightBtn);

        if (rightModel._comparator == null) {
            controlsPnl.add(Box.createVerticalStrut(20));
            _moveUpBtn = SeerGuiUtils.createButton("Move Up", "move-up", "Move Selected Item Up", this);
            _orderedButtons.add(_moveUpBtn);
            controlsPnl.add(_moveUpBtn);
            controlsPnl.add(Box.createVerticalStrut(5));
            _moveDownBtn = SeerGuiUtils.createButton("Move Down", "move-down", "Move Selected Item Down", this);
            _orderedButtons.add(_moveDownBtn);
            controlsPnl.add(_moveDownBtn);
            controlsPnl.add(Box.createVerticalStrut(15));
            _moveFirstBtn = SeerGuiUtils.createButton("Move First", "move-first", "Move Selected Item First", this);
            _orderedButtons.add(_moveFirstBtn);
            controlsPnl.add(_moveFirstBtn);
            controlsPnl.add(Box.createVerticalStrut(5));
            _moveLastBtn = SeerGuiUtils.createButton("Move Last", "move-last", "Move Selected Item Last", this);
            _orderedButtons.add(_moveLastBtn);
            controlsPnl.add(_moveLastBtn);
        }

        controlsPnl.add(Box.createVerticalStrut(15));

        _centerPnl.add(controlsPnl);
        _centerPnl.setMaximumSize(new Dimension(100, 400));
        if (rightModel._comparator == null)
            SeerGuiUtils.synchronizedComponentsWidth(_moveLeftBtn, _moveRightBtn, _moveAllLeftBtn, _moveAllRightBtn, _moveUpBtn, _moveDownBtn, _moveFirstBtn, _moveLastBtn);
        else
            SeerGuiUtils.synchronizedComponentsWidth(_moveLeftBtn, _moveRightBtn, _moveAllLeftBtn, _moveAllRightBtn);
        this.add(_centerPnl);

        // RIGHT - right items
        _rightPnl = SeerGuiUtils.createPanel();
        _rightPnl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        if (rightLbl != null)
            _rightPnl.add(rightLbl, BorderLayout.NORTH);
        _rightList = new SeerList<>(rightModel, SeerList.DISPLAY_MODE_NONE, false);
        _rightList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        _rightList.getSelectionModel().addListSelectionListener(e -> enableButtons());
        _rightList.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                _leftList.clearSelection();
                if (addFocusBorder)
                    _rightPane.setBorder(SeerGuiUtils.BORDER_FOCUS_IN);
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (addFocusBorder)
                    _rightPane.setBorder(SeerGuiUtils.BORDER_FOCUS_OUT);
            }
        });
        _rightPane = new JScrollPane(_rightList);
        _rightPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        _rightPnl.add(_rightPane, BorderLayout.CENTER);
        if (showFilter) {
            JPanel rightFilterPnl = SeerGuiUtils.createPanel();
            rightFilterPnl.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
            rightFilterPnl.add(SeerGuiUtils.createLabel("Filter"), BorderLayout.WEST);
            _rightFilterFld = new JTextField();
            _rightFilterFld.setFont(SeerGuiUtils.adjustFontSize(_leftFilterFld.getFont()));
            _rightFilterFld.setBorder(SeerGuiUtils.BORDER_TEXT_FIELD_OUT);
            _rightFilterFld.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    performApplyRightFilter();
                }
            });
            if (addFocusBorder) {
                _rightFilterFld.addFocusListener(new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        _rightFilterFld.setBorder(SeerGuiUtils.BORDER_TEXT_FIELD_IN);
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        _rightFilterFld.setBorder(SeerGuiUtils.BORDER_TEXT_FIELD_OUT);
                    }
                });
            }
            JPanel rightFilterWrapperPnl = SeerGuiUtils.createPanel();
            rightFilterWrapperPnl.add(Box.createVerticalStrut(2), BorderLayout.NORTH);
            rightFilterWrapperPnl.add(Box.createHorizontalStrut(5), BorderLayout.WEST);
            rightFilterWrapperPnl.add(_rightFilterFld, BorderLayout.CENTER);
            rightFilterWrapperPnl.add(Box.createHorizontalStrut(5), BorderLayout.EAST);
            rightFilterWrapperPnl.add(Box.createVerticalStrut(2), BorderLayout.SOUTH);
            rightFilterPnl.add(rightFilterWrapperPnl, BorderLayout.CENTER);
            rightFilterPnl.add(SeerGuiUtils.createButton("Reset", "reset-right-filter", "Reset Filter", this), BorderLayout.EAST);
            _rightPnl.add(rightFilterPnl, BorderLayout.SOUTH);
        }
        this.add(_rightPnl);

        enableButtons();

        resizeLists();

        _leftList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2)
                    performMoveRight();
            }
        });
        _leftList.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "switch-side-action");
        _leftList.getActionMap().put("switch-side-action", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performMoveRight();
            }
        });

        _rightList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2)
                    performMoveLeft();
            }
        });
        _rightList.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "switch-side-action");
        _rightList.getActionMap().put("switch-side-action", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performMoveLeft();
            }
        });
    }

    public void resizeLists() {
        int width = (this.getPreferredSize().width - _centerPnl.getPreferredSize().width) / 2;
        _leftPnl.setPreferredSize(new Dimension(width, _leftPnl.getPreferredSize().height));
        _rightPnl.setPreferredSize(new Dimension(width, _rightPnl.getPreferredSize().height));
    }

    public void enableButtons() {
        int numRightSelected = _leftList.getSelectedIndices().length;
        int numLeftSelected = _rightList.getSelectedIndices().length;

        // keep track of which button has focus (if any)
        int focusIdx = -1;
        for (int i = 0; i < _orderedButtons.size(); i++)
            if (_orderedButtons.get(i).hasFocus())
                focusIdx = i;

        _moveRightBtn.setEnabled(numRightSelected > 0);
        _moveLeftBtn.setEnabled(numLeftSelected > 0);
        _moveAllRightBtn.setEnabled(_leftList.getModel().getSize() > 0);
        _moveAllLeftBtn.setEnabled(_rightList.getModel().getSize() > 0);
        if (_moveUpBtn != null)
            _moveUpBtn.setEnabled(numLeftSelected != 0 && _rightList.getSelectedIndex() != 0);
        if (_moveDownBtn != null)
            _moveDownBtn.setEnabled(numLeftSelected != 0 && _rightList.getSelectedIndices()[numLeftSelected - 1] != _rightList.getModel().getSize() - 1);
        if (_moveFirstBtn != null)
            _moveFirstBtn.setEnabled(numLeftSelected != 0 && _rightList.getSelectedIndex() != 0);
        if (_moveLastBtn != null)
            _moveLastBtn.setEnabled(numLeftSelected != 0 && _rightList.getSelectedIndices()[numLeftSelected - 1] != _rightList.getModel().getSize() - 1);

        // re-apply focus
        if (focusIdx != -1) {
            boolean focusApplied = false;
            for (int i = focusIdx; i < _orderedButtons.size(); i++) {
                JButton btn = _orderedButtons.get(i);
                if (btn.isEnabled()) {
                    btn.requestFocusInWindow();
                    focusApplied = true;
                    break;
                }
            }
            if (!focusApplied) {
                for (int i = 0; i < focusIdx; i++) {
                    JButton btn = _orderedButtons.get(i);
                    if (btn.isEnabled()) {
                        btn.requestFocusInWindow();
                        break;
                    }
                }
            }
        }
    }

    public void updateItemsFromModels() {

        SeerListModel<E> leftModel = (SeerListModel<E>)_leftList.getModel();
        SeerListModel<E> rightModel = (SeerListModel<E>)_rightList.getModel();

        _leftList.resetData(new ArrayList<>());
        for (int i = 0; i < leftModel.getSize(); i++)
            ((SeerListModel<E>)(_leftList.getModel())).addElement(leftModel.getElementAt(i));
        _rightList.resetData(new ArrayList<>());
        for (int i = 0; i < rightModel.getSize(); i++)
            ((SeerListModel<E>)(_rightList.getModel())).addElement(rightModel.getElementAt(i));
    }

    public SeerList<E> getLeftList() {
        return _leftList;
    }

    public List<E> getLeftListContent() {
        return getLeftListContent(false);
    }

    public List<E> getLeftListContent(boolean filtered) {
        if (filtered)
            return ((SeerListModel<E>)_leftList.getModel()).getFilteredData();
        else
            return ((SeerListModel<E>)_leftList.getModel()).getOriginalData();
    }

    public SeerList<E> getRightList() {
        return _rightList;
    }

    public List<E> getRightListContent() {
        return getRightListContent(false);
    }

    public List<E> getRightListContent(boolean filtered) {
        if (filtered)
            return ((SeerListModel<E>)_rightList.getModel()).getFilteredData();
        else
            return ((SeerListModel<E>)_rightList.getModel()).getOriginalData();
    }

    public void performMoveLeft() {
        // make sure there is no filter on since it could hide the values that are about to be added, which would be really weird for the user
        performResetLeftFilter();

        SeerListModel<E> leftModel = (SeerListModel<E>)_leftList.getModel();
        SeerListModel<E> rightModel = (SeerListModel<E>)_rightList.getModel();

        E firstMoved = null;
        for (E obj : _rightList.getSelectedValuesList()) {
            if (firstMoved == null)
                firstMoved = obj;
            rightModel.removeElement(obj);
            leftModel.addElement(obj);
        }

        // make sure the first value moved is visible to the user
        _leftList.ensureIndexIsVisible(leftModel.indexOf(firstMoved));

        enableButtons();
    }

    public void performMoveAllLeft() {
        // make sure there is no filter on since it could hide the values that are about to be added, which would be really weird for the user
        performResetLeftFilter();

        SeerListModel<E> leftModel = (SeerListModel<E>)_leftList.getModel();
        SeerListModel<E> rightModel = (SeerListModel<E>)_rightList.getModel();

        E firstMoved = null;
        List<Object> itemsToMove = new ArrayList<>();
        for (int i = 0; i < rightModel.getSize(); i++) {
            E obj = rightModel.getElementAt(i);
            if (firstMoved == null)
                firstMoved = obj;
            itemsToMove.add(obj);
        }
        for (Object obj : itemsToMove) {
            rightModel.removeElement((E)obj);
            leftModel.addElement((E)obj);
        }

        // make sure the first value moved is visible to the user
        _leftList.ensureIndexIsVisible(leftModel.indexOf(firstMoved));

        enableButtons();
    }

    public void performMoveRight() {
        // make sure there is no filter on since it could hide the values that are about to be added, which would be really weird for the user
        performResetRightFilter();

        SeerListModel<E> leftModel = (SeerListModel<E>)_leftList.getModel();
        SeerListModel<E> rightModel = (SeerListModel<E>)_rightList.getModel();

        E firstMoved = null;
        for (E obj : _leftList.getSelectedValuesList()) {
            if (firstMoved == null)
                firstMoved = obj;
            leftModel.removeElement(obj);
            rightModel.addElement(obj);
        }

        // make sure the first value moved is visible to the user
        _rightList.ensureIndexIsVisible(leftModel.indexOf(firstMoved));

        enableButtons();
    }

    public void performMoveAllRight() {
        // make sure there is no filter on since it could hide the values that are about to be added, which would be really weird for the user
        performResetRightFilter();

        SeerListModel<E> leftModel = (SeerListModel<E>)_leftList.getModel();
        SeerListModel<E> rightModel = (SeerListModel<E>)_rightList.getModel();

        E firstMoved = null;
        List<Object> itemsToMove = new ArrayList<>();
        for (int i = 0; i < leftModel.getSize(); i++) {
            E obj = leftModel.getElementAt(i);
            if (firstMoved == null)
                firstMoved = obj;
            itemsToMove.add(obj);
        }
        for (Object obj : itemsToMove) {
            leftModel.removeElement((E)obj);
            rightModel.addElement((E)obj);
        }

        // make sure the first value moved is visiable to the user
        _rightList.ensureIndexIsVisible(leftModel.indexOf(firstMoved));

        enableButtons();
    }

    public void performMoveUp() {
        SeerListModel<E> selMod = (SeerListModel<E>)_rightList.getModel();

        int[] indices = _rightList.getSelectedIndices();
        int counter = 0;
        for (int i : indices) {
            E obj = selMod.getElementAt(i);
            selMod.removeElement(obj);
            selMod.addElement(i - 1, obj);
            indices[counter] = i - 1;
            counter++;
        }

        _rightList.setSelectedIndices(indices);
    }

    public void performMoveFirst() {
        SeerListModel<E> selMod = (SeerListModel<E>)_rightList.getModel();

        int[] indices = _rightList.getSelectedIndices();
        int index1 = indices[0];
        int counter = 0;
        for (int i : indices) {
            E obj = selMod.getElementAt(i);
            selMod.removeElement(obj);
            selMod.addElement(i - index1, obj);
            indices[counter] = i - index1;
            counter++;
        }

        _rightList.setSelectedIndices(indices);
    }

    public void performMoveDown() {
        SeerListModel<E> selMod = (SeerListModel<E>)_rightList.getModel();

        int[] indices = _rightList.getSelectedIndices();
        int counter = indices.length - 1;
        for (int i = indices.length - 1; i >= 0; i--) {
            E obj = selMod.getElementAt(indices[i]);
            selMod.removeElement(obj);
            selMod.addElement(indices[i] + 1, obj);
            indices[counter] = indices[i] + 1;
            counter--;
        }

        _rightList.setSelectedIndices(indices);
    }

    public void performMoveLast() {
        SeerListModel<E> selMod = (SeerListModel<E>)_rightList.getModel();

        int[] indices = _rightList.getSelectedIndices();
        int counter = indices.length - 1;
        int indexLast = indices[indices.length - 1];
        for (int i = indices.length - 1; i >= 0; i--) {
            E obj = selMod.getElementAt(indices[i]);
            selMod.removeElement(obj);
            selMod.addElement(indices[i] + (selMod.getSize() - indexLast), obj);
            indices[counter] = indices[i] + ((selMod.getSize() - 1) - indexLast);
            counter--;
        }

        _rightList.setSelectedIndices(indices);
    }

    public void performResetRightFilter() {
        if (_rightFilterFld != null)
            _rightFilterFld.setText("");
        _rightList.resetFilter();
    }

    public void performApplyRightFilter() {
        if (_rightFilterFld != null)
            _rightList.filter(_rightFilterFld.getText());
    }

    public void performResetLeftFilter() {
        if (_leftFilterFld != null)
            _leftFilterFld.setText("");
        _leftList.resetFilter();
    }

    public void performApplyLeftFilter() {
        if (_leftFilterFld != null)
            _leftList.filter(_leftFilterFld.getText());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String com = e.getActionCommand();

        if ("move-left".equals(com))
            performMoveLeft();
        else if ("move-right".equals(com))
            performMoveRight();
        else if ("move-all-left".equals(com))
            performMoveAllLeft();
        else if ("move-all-right".equals(com))
            performMoveAllRight();
        else if ("move-up".equals(com))
            performMoveUp();
        else if ("move-down".equals(com))
            performMoveDown();
        else if ("move-first".equals(com))
            performMoveFirst();
        else if ("move-last".equals(com))
            performMoveLast();
        else if ("reset-left-filter".equals(com))
            performResetLeftFilter();
        else if ("reset-right-filter".equals(com))
            performResetRightFilter();
    }
}
