/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.oimaging.gui;

import fr.jmmc.jmcs.gui.action.ActionRegistrar;
import fr.jmmc.oimaging.gui.action.LoadFitsImageAction;
import fr.jmmc.oimaging.model.IRModel;
import fr.jmmc.oimaging.services.Service;
import fr.jmmc.oimaging.services.ServiceList;
import fr.jmmc.oitools.image.FitsImageHDU;
import fr.jmmc.oitools.image.ImageOiConstants;
import fr.jmmc.oitools.image.ImageOiInputParam;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JFormattedTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 * @author mellag
 */
public class SoftwareSettingsPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    /* members */
    /** associated mainPanel */
    private MainPanel mainPanel;

    /** Creates new form AlgorithmSettinsPanel */
    public SoftwareSettingsPanel() {
        initComponents();
        postInit();
    }

    /**
     * This method is useful to set the models and specific features of initialized swing components :
     */
    private void postInit() {
        registerActions();
        jComboBoxImage.setRenderer(new OiCellRenderer());
        jTableKeywordsEditor.setNotifiedParent(this);
        this.jTextAreaOptions.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent e) {
                updateModel();
            }

            @Override
            public void removeUpdate(final DocumentEvent e) {
                updateModel();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                //Plain text components do not fire these events
            }
        });
    }

    /**
     * Create the main actions
     */
    private void registerActions() {
        // Map actions to widgets
        jButtonLoadFitsImage.setAction(ActionRegistrar.getInstance().get(LoadFitsImageAction.className, LoadFitsImageAction.actionName));
        jButtonLoadFitsImage.setHideActionText(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jideToggleSplitButton1 = new com.jidesoft.swing.JideToggleSplitButton();
        jPanelForm = new javax.swing.JPanel();
        jLabelInitImg = new javax.swing.JLabel();
        jLabelMaxIter = new javax.swing.JLabel();
        jLabelRglName = new javax.swing.JLabel();
        jLabelRglWgt = new javax.swing.JLabel();
        jLabelRglAlph = new javax.swing.JLabel();
        jLabelRglBeta = new javax.swing.JLabel();
        jLabelRglPrio = new javax.swing.JLabel();
        jComboBoxSoftware = new javax.swing.JComboBox();
        jComboBoxImage = new javax.swing.JComboBox();
        jSpinnerMaxIter = new javax.swing.JSpinner();
        jComboBoxRglName = new javax.swing.JComboBox();
        jFormattedTextFieldRglWgt = new javax.swing.JFormattedTextField();
        jFormattedTextFieldRglAlph = new javax.swing.JFormattedTextField();
        jFormattedTextFieldRglBeta = new javax.swing.JFormattedTextField();
        jComboBoxRglPrio = new javax.swing.JComboBox();
        jButtonRemoveFitsImage = new javax.swing.JButton();
        jButtonLoadFitsImage = new javax.swing.JButton();
        jTableKeywordsEditor = new fr.jmmc.oimaging.gui.TableKeywordsEditor();
        jPanelOptions = new javax.swing.JPanel();
        jTextAreaOptions = new javax.swing.JTextArea();

        jideToggleSplitButton1.setText("jideToggleSplitButton1");

        setBorder(javax.swing.BorderFactory.createTitledBorder("Algorithm settings"));
        setLayout(new java.awt.GridBagLayout());

        jPanelForm.setLayout(new java.awt.GridBagLayout());

        jLabelInitImg.setText("INIT_IMG");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 6);
        jPanelForm.add(jLabelInitImg, gridBagConstraints);

        jLabelMaxIter.setText("MAXITER");
        jLabelMaxIter.setToolTipText(getTooltip(ImageOiConstants.KEYWORD_MAXITER));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 6);
        jPanelForm.add(jLabelMaxIter, gridBagConstraints);

        jLabelRglName.setText("RGL_NAME");
        jLabelRglName.setToolTipText(getTooltip(ImageOiConstants.KEYWORD_RGL_NAME));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 6);
        jPanelForm.add(jLabelRglName, gridBagConstraints);

        jLabelRglWgt.setText("RGL_WGT");
        jLabelRglWgt.setToolTipText(getTooltip(ImageOiConstants.KEYWORD_RGL_WGT));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 6);
        jPanelForm.add(jLabelRglWgt, gridBagConstraints);

        jLabelRglAlph.setText("RGL_ALPH");
        jLabelRglAlph.setToolTipText(getTooltip(ImageOiConstants.KEYWORD_RGL_ALPH));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 6);
        jPanelForm.add(jLabelRglAlph, gridBagConstraints);

        jLabelRglBeta.setText("RGL_BETA");
        jLabelRglBeta.setToolTipText(getTooltip(ImageOiConstants.KEYWORD_RGL_BETA));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 6);
        jPanelForm.add(jLabelRglBeta, gridBagConstraints);

        jLabelRglPrio.setText("RGL_PRIO");
        jLabelRglPrio.setToolTipText(getTooltip(ImageOiConstants.KEYWORD_RGL_PRIO));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 6);
        jPanelForm.add(jLabelRglPrio, gridBagConstraints);

        jComboBoxSoftware.setModel(ServiceList.getAvailableServices());
        jComboBoxSoftware.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxSoftwareActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 6);
        jPanelForm.add(jComboBoxSoftware, gridBagConstraints);

        jComboBoxImage.setToolTipText(getTooltip(ImageOiConstants.KEYWORD_INIT_IMG));
        jComboBoxImage.setMinimumSize(new java.awt.Dimension(140, 28));
        jComboBoxImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxImageActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 6);
        jPanelForm.add(jComboBoxImage, gridBagConstraints);

        jSpinnerMaxIter.setModel(new javax.swing.SpinnerNumberModel(0, -1, null, 5));
        jSpinnerMaxIter.setToolTipText(getTooltip(ImageOiConstants.KEYWORD_MAXITER));
        jSpinnerMaxIter.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerMaxIterStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 6);
        jPanelForm.add(jSpinnerMaxIter, gridBagConstraints);

        jComboBoxRglName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "mem_prior" }));
        jComboBoxRglName.setToolTipText(getTooltip(ImageOiConstants.KEYWORD_RGL_NAME));
        jComboBoxRglName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxRglNameActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 6);
        jPanelForm.add(jComboBoxRglName, gridBagConstraints);

        jFormattedTextFieldRglWgt.setFormatterFactory(getDecimalFormatterFactory());
        jFormattedTextFieldRglWgt.setToolTipText(getTooltip(ImageOiConstants.KEYWORD_RGL_WGT));
        jFormattedTextFieldRglWgt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextFieldRglWgtjFormattedTextFieldActionPerformed(evt);
            }
        });
        jFormattedTextFieldRglWgt.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jFormattedTextFieldRglWgtjFormattedTextFieldPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 6);
        jPanelForm.add(jFormattedTextFieldRglWgt, gridBagConstraints);

        jFormattedTextFieldRglAlph.setFormatterFactory(getDecimalFormatterFactory());
        jFormattedTextFieldRglAlph.setToolTipText(getTooltip(ImageOiConstants.KEYWORD_RGL_ALPH));
        jFormattedTextFieldRglAlph.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextFieldRglAlphjFormattedTextFieldActionPerformed(evt);
            }
        });
        jFormattedTextFieldRglAlph.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jFormattedTextFieldRglAlphjFormattedTextFieldPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 6);
        jPanelForm.add(jFormattedTextFieldRglAlph, gridBagConstraints);

        jFormattedTextFieldRglBeta.setFormatterFactory(getDecimalFormatterFactory());
        jFormattedTextFieldRglBeta.setToolTipText(getTooltip(ImageOiConstants.KEYWORD_RGL_BETA));
        jFormattedTextFieldRglBeta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextFieldRglBetajFormattedTextFieldActionPerformed(evt);
            }
        });
        jFormattedTextFieldRglBeta.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jFormattedTextFieldRglBetajFormattedTextFieldPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 6);
        jPanelForm.add(jFormattedTextFieldRglBeta, gridBagConstraints);

        jComboBoxRglPrio.setToolTipText(getTooltip(ImageOiConstants.KEYWORD_RGL_PRIO));
        jComboBoxRglPrio.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 6);
        jPanelForm.add(jComboBoxRglPrio, gridBagConstraints);

        jButtonRemoveFitsImage.setText("-");
        jButtonRemoveFitsImage.setEnabled(false);
        jButtonRemoveFitsImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveFitsImageActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        jPanelForm.add(jButtonRemoveFitsImage, gridBagConstraints);

        jButtonLoadFitsImage.setText("+");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        jPanelForm.add(jButtonLoadFitsImage, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(jPanelForm, gridBagConstraints);

        jTableKeywordsEditor.setBorder(javax.swing.BorderFactory.createTitledBorder("Specific parameters"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(jTableKeywordsEditor, gridBagConstraints);

        jPanelOptions.setBorder(javax.swing.BorderFactory.createTitledBorder("Manual options"));
        jPanelOptions.setLayout(new java.awt.GridBagLayout());

        jTextAreaOptions.setColumns(10);
        jTextAreaOptions.setLineWrap(true);
        jTextAreaOptions.setRows(3);
        jTextAreaOptions.setTabSize(2);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        jPanelOptions.add(jTextAreaOptions, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        add(jPanelOptions, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBoxSoftwareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxSoftwareActionPerformed
        updateModel();
    }//GEN-LAST:event_jComboBoxSoftwareActionPerformed

    private void jComboBoxImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxImageActionPerformed
        updateModel();

    }//GEN-LAST:event_jComboBoxImageActionPerformed

    private void jSpinnerMaxIterStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerMaxIterStateChanged
        updateModel();
    }//GEN-LAST:event_jSpinnerMaxIterStateChanged

    private void jComboBoxRglNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxRglNameActionPerformed
        updateModel();
    }//GEN-LAST:event_jComboBoxRglNameActionPerformed

    private void jFormattedTextFieldRglWgtjFormattedTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextFieldRglWgtjFormattedTextFieldActionPerformed
        updateModel();
    }//GEN-LAST:event_jFormattedTextFieldRglWgtjFormattedTextFieldActionPerformed

    private void jFormattedTextFieldRglWgtjFormattedTextFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jFormattedTextFieldRglWgtjFormattedTextFieldPropertyChange
        updateModel();
    }//GEN-LAST:event_jFormattedTextFieldRglWgtjFormattedTextFieldPropertyChange

    private void jFormattedTextFieldRglAlphjFormattedTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextFieldRglAlphjFormattedTextFieldActionPerformed
        updateModel();
    }//GEN-LAST:event_jFormattedTextFieldRglAlphjFormattedTextFieldActionPerformed

    private void jFormattedTextFieldRglAlphjFormattedTextFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jFormattedTextFieldRglAlphjFormattedTextFieldPropertyChange
        updateModel();
    }//GEN-LAST:event_jFormattedTextFieldRglAlphjFormattedTextFieldPropertyChange

    private void jFormattedTextFieldRglBetajFormattedTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextFieldRglBetajFormattedTextFieldActionPerformed
        updateModel();
    }//GEN-LAST:event_jFormattedTextFieldRglBetajFormattedTextFieldActionPerformed

    private void jFormattedTextFieldRglBetajFormattedTextFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jFormattedTextFieldRglBetajFormattedTextFieldPropertyChange
        updateModel();
    }//GEN-LAST:event_jFormattedTextFieldRglBetajFormattedTextFieldPropertyChange

    private void jButtonRemoveFitsImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveFitsImageActionPerformed
        // TODO
    }//GEN-LAST:event_jButtonRemoveFitsImageActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonLoadFitsImage;
    private javax.swing.JButton jButtonRemoveFitsImage;
    private javax.swing.JComboBox jComboBoxImage;
    private javax.swing.JComboBox jComboBoxRglName;
    private javax.swing.JComboBox jComboBoxRglPrio;
    private javax.swing.JComboBox jComboBoxSoftware;
    private javax.swing.JFormattedTextField jFormattedTextFieldRglAlph;
    private javax.swing.JFormattedTextField jFormattedTextFieldRglBeta;
    private javax.swing.JFormattedTextField jFormattedTextFieldRglWgt;
    private javax.swing.JLabel jLabelInitImg;
    private javax.swing.JLabel jLabelMaxIter;
    private javax.swing.JLabel jLabelRglAlph;
    private javax.swing.JLabel jLabelRglBeta;
    private javax.swing.JLabel jLabelRglName;
    private javax.swing.JLabel jLabelRglPrio;
    private javax.swing.JLabel jLabelRglWgt;
    private javax.swing.JPanel jPanelForm;
    private javax.swing.JPanel jPanelOptions;
    private javax.swing.JSpinner jSpinnerMaxIter;
    private fr.jmmc.oimaging.gui.TableKeywordsEditor jTableKeywordsEditor;
    private javax.swing.JTextArea jTextAreaOptions;
    private com.jidesoft.swing.JideToggleSplitButton jideToggleSplitButton1;
    // End of variables declaration//GEN-END:variables

    public static JFormattedTextField.AbstractFormatterFactory getDecimalFormatterFactory() {
        return new DefaultFormatterFactory(new NumberFormatter(new java.text.DecimalFormat("#0.0####E00")) {
            public String valueToString(Object value) throws ParseException {
                final String formatted = super.valueToString(value);
                if (formatted.endsWith("E00")) {  
                    return formatted.substring(0, formatted.length() - 3);
                }
                return formatted;
            }
        });
    }

    public static JFormattedTextField.AbstractFormatterFactory getIntegerFormatterFactory() {
        return new DefaultFormatterFactory(new NumberFormatter(NumberFormat.getIntegerInstance()));
    }

    void syncUI(final MainPanel panel, final IRModel irModel, final List<String> failures) {
        mainPanel = panel;

        final ImageOiInputParam inputParam = irModel.getImageOiData().getInputParam();
        final Service service = irModel.getSelectedService();
        boolean show;

        if (inputParam.getSpecificKeywords().isEmpty()) {
            jTableKeywordsEditor.setModel(null);
            jTableKeywordsEditor.setVisible(false);
        } else {
            jTableKeywordsEditor.setModel(inputParam, inputParam.getSpecificKeywords());
            jTableKeywordsEditor.setVisible(true);
        }

        // Initial Image (combo):
        jComboBoxImage.removeAllItems();
        for (FitsImageHDU fitsImageHDU : irModel.getFitsImageHDUs()) {
            jComboBoxImage.addItem(fitsImageHDU);
        }

        final FitsImageHDU selectedFitsImageHDU = irModel.getSelectedInputImageHDU();
        if (selectedFitsImageHDU != null) {
            jComboBoxImage.getModel().setSelectedItem(selectedFitsImageHDU);
        } else {
            failures.add(irModel.getSelectedInputFitsImageError());
        }

        // Max iter:
        jSpinnerMaxIter.setValue(inputParam.getMaxiter());
        show = service.supportsStandardKeyword(ImageOiConstants.KEYWORD_MAXITER);
        jLabelMaxIter.setVisible(show);
        jSpinnerMaxIter.setVisible(show);

        // regulation Name:
        // update content of jCombobox
        updateJComboBoxRglName(inputParam, service);

        // regulation Weight:
        jFormattedTextFieldRglWgt.setValue(inputParam.getRglWgt());
        show = service.supportsStandardKeyword(ImageOiConstants.KEYWORD_RGL_WGT);
        jLabelRglWgt.setVisible(show);
        jFormattedTextFieldRglWgt.setVisible(show);

        // change visibility / enabled if RglWgt keyword exists (bsmem auto)
        final boolean enabled = inputParam.hasKeywordMeta(ImageOiConstants.KEYWORD_RGL_WGT);
        jLabelRglWgt.setEnabled(enabled);
        jFormattedTextFieldRglWgt.setEnabled(enabled);

        // regulation Alpha:
        jFormattedTextFieldRglAlph.setValue(inputParam.getRglAlph());
        show = service.supportsStandardKeyword(ImageOiConstants.KEYWORD_RGL_ALPH);
        jLabelRglAlph.setVisible(show);
        jFormattedTextFieldRglAlph.setVisible(show);

        // regulation Beta:
        jFormattedTextFieldRglBeta.setValue(inputParam.getRglBeta());
        show = service.supportsStandardKeyword(ImageOiConstants.KEYWORD_RGL_BETA);
        jLabelRglBeta.setVisible(show);
        jFormattedTextFieldRglBeta.setVisible(show);

        // regulation Prior:
        final String rglPrio = inputParam.getRglPrio();
        if (rglPrio != null) {
            jComboBoxRglPrio.setSelectedItem(rglPrio);
        }
        show = service.supportsStandardKeyword(ImageOiConstants.KEYWORD_RGL_PRIO);
        jLabelRglPrio.setVisible(show);
        jComboBoxRglPrio.setVisible(show);

        // validate
        service.validate(inputParam, failures);

        // identity check on singletons:
        if (service != jComboBoxSoftware.getSelectedItem()) {
            jComboBoxSoftware.setSelectedItem(service);
        }

        // CLI Options:
        final String cliOptions = irModel.getCliOptions();
        if (!jTextAreaOptions.getText().equals(cliOptions)) {
            jTextAreaOptions.setText(cliOptions == null ? "" : cliOptions);
        }
    }

    protected void updateModel() {
        updateModel(false);
    }

    protected void updateModel(final boolean forceChange) {
        if (mainPanel != null) {
            mainPanel.updateModel(forceChange);
        }
    }

    boolean updateModel(IRModel irModel) {
        final ImageOiInputParam inputParam = irModel.getImageOiData().getInputParam();

        // Update if model_values != swing_values and detect change if one or more values change
        boolean changed = false;
        double mDouble, wDouble;
        String mString, wString;
        int mInt, wInt;

        // Selected software
        final Service guiService = (Service) jComboBoxSoftware.getSelectedItem();
        final Service modelSoftware = irModel.getSelectedService();
        if (guiService != null && !guiService.equals(modelSoftware)) {
            irModel.setSelectedService(guiService);
            changed = true;
        }

        // Init Image
        final FitsImageHDU mFitsImageHDU = irModel.getSelectedInputImageHDU();
        final FitsImageHDU sFitsImageHDU = (FitsImageHDU) this.jComboBoxImage.getSelectedItem();
        if (sFitsImageHDU != null && !(sFitsImageHDU == mFitsImageHDU)) {
            irModel.setSelectedInputImageHDU(sFitsImageHDU);
            changed = true;
        }

        // max iter
        try {
            // guarantee last user value
            jSpinnerMaxIter.commitEdit();
        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        mInt = inputParam.getMaxiter();
        wInt = (Integer) jSpinnerMaxIter.getValue();
        if (mInt != wInt) {
            inputParam.setMaxiter(wInt);
            changed = true;
        }

        // regularization
        mString = inputParam.getRglName();
        if (jComboBoxRglName.getSelectedItem() != null) {
            wString = (String) jComboBoxRglName.getSelectedItem();
            if (!wString.equals(mString)) {
                inputParam.setRglName(wString);
                irModel.initSpecificParams(); // update call required to apply on fly specific param handling
                changed = true;
            }
        }

        mDouble = inputParam.getRglWgt();
        if (jFormattedTextFieldRglWgt.getValue() != null) {
            wDouble = ((Number) jFormattedTextFieldRglWgt.getValue()).doubleValue();
            if (mDouble != wDouble) {
                inputParam.setRglWgt(wDouble);
                changed = true;
            }
        }

        mDouble = inputParam.getRglAlph();
        if (jFormattedTextFieldRglAlph.getValue() != null) {
            wDouble = ((Number) jFormattedTextFieldRglAlph.getValue()).doubleValue();
            if (mDouble != wDouble) {
                inputParam.setRglAlph(wDouble);
                changed = true;
            }
        }

        mDouble = inputParam.getRglBeta();
        if (jFormattedTextFieldRglBeta.getValue() != null) {
            wDouble = ((Number) jFormattedTextFieldRglBeta.getValue()).doubleValue();
            if (mDouble != wDouble) {
                inputParam.setRglBeta(wDouble);
                changed = true;
            }
        }

        mString = inputParam.getRglPrio();
        if (jComboBoxRglPrio.getSelectedItem() != null) {
            wString = (String) jComboBoxRglPrio.getSelectedItem();
            if (!wString.equals(mString)) {
                inputParam.setRglPrio(wString);
                changed = true;
            }
        }

        // cliOptions
        mString = irModel.getCliOptions();
        wString = jTextAreaOptions.getText();
        if (!wString.equals(mString)) {
            irModel.setCliOptions(wString);
            changed = true;
        }

        return changed;
    }

    private void updateJComboBoxRglName(final ImageOiInputParam inputParam, final Service service) {
        jComboBoxRglName.removeAllItems();
        for (String v : service.getSupported_RGL_NAME()) {
            jComboBoxRglName.addItem(v);
        }
        String rglName = inputParam.getRglName();
        if (rglName != null) {
            jComboBoxRglName.setSelectedItem(rglName);
        }
        final boolean show = service.supportsStandardKeyword(ImageOiConstants.KEYWORD_RGL_NAME);
        jLabelRglName.setVisible(show);
        jComboBoxRglName.setVisible(show);
    }

    private static String getTooltip(final String name) {
        return ImageOiInputParam.getDescription(name);
    }
}
