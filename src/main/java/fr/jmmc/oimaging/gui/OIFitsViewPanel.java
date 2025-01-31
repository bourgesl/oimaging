/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.oimaging.gui;

import fr.jmmc.jmcs.gui.component.Disposable;
import fr.jmmc.oiexplorer.core.export.DocumentExportable;
import fr.jmmc.oiexplorer.core.export.DocumentOptions;
import fr.jmmc.oiexplorer.core.gui.PlotChartPanel;
import fr.jmmc.oiexplorer.core.gui.PlotView;
import fr.jmmc.oiexplorer.core.gui.action.ExportDocumentAction;
import fr.jmmc.oiexplorer.core.model.OIFitsCollectionManager;
import fr.jmmc.oiexplorer.core.model.oi.SubsetDefinition;
import fr.jmmc.oitools.model.OIFitsFile;
import java.awt.BorderLayout;
import org.jfree.chart.ChartColor;
import org.jfree.chart.ui.Drawable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This panel embeds the OIFitsExplorer into Aspro2
 * @author bourgesl
 */
public final class OIFitsViewPanel extends javax.swing.JPanel implements Disposable, DocumentExportable {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Class logger */
    private static final Logger logger = LoggerFactory.getLogger(OIFitsViewPanel.class.getName());

    /* members */
    /** OIFitsCollectionManager singleton */
    private final OIFitsCollectionManager ocm = OIFitsCollectionManager.getInstance();
    /** chart data */
    private OIFitsFile oiFitsFile = null;
    /** Oifits explorer Plot view */
    private PlotView plotView;
    /** Oifits explorer Plot chart panel */
    private PlotChartPanel plotChartPanel;

    /**
     * Constructor
     */
    public OIFitsViewPanel() {
        initComponents();

        postInit();
    }

    /**
     * Free any ressource or reference to this instance:
     * remove the plot chart panel from OIFitsCollectionManager event notifiers
     * and this instance from ObservationManager listeners
     *
     * @see PlotChartPanel#dispose()
     */
    @Override
    public void dispose() {
        // forward dispose() to child components:
        if (plotView != null) {
            plotView.dispose();
        }
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelMessage = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.BorderLayout());

        jLabelMessage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelMessage.setText("LABEL");
        jLabelMessage.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        add(jLabelMessage, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Export the component as a document using the given action:
     * the component should check if there is something to export ?
     * @param action export action to perform the export action
     */
    @Override
    public void performAction(final ExportDocumentAction action) {
        if (getOIFitsData() == null) {
            return;
        }
        action.process(this);
    }

    /**
     * Return the default file name
     * @param fileExtension  document's file extension
     * @return default file name
     */
    @Override
    public String getDefaultFileName(final String fileExtension) {
        return this.plotChartPanel.getDefaultFileName(fileExtension);
    }

    /**
     * Prepare the page layout before doing the export:
     * Performs layout and modifies the given options
     * @param options document options used to prepare the document
     */
    @Override
    public void prepareExport(final DocumentOptions options) {
        this.plotChartPanel.prepareExport(options);
    }

    /**
     * Return the page to export given its page index
     * @param pageIndex page index (1..n)
     * @return Drawable array to export on this page
     */
    @Override
    public Drawable[] preparePage(final int pageIndex) {
        return this.plotChartPanel.preparePage(pageIndex);
    }

    /**
     * Callback indicating the export is done to reset the component's state
     */
    @Override
    public void postExport() {
        this.plotChartPanel.postExport();
    }

    /**
     * This method is useful to set the models and specific features of initialized swing components :
     */
    private void postInit() {
        ocm.start();

        this.jLabelMessage.setForeground(ChartColor.DARK_RED);

        // define which plot to use:
        final String plotId = OIFitsCollectionManager.CURRENT_VIEW;

        this.plotView = new PlotView(plotId);
        this.plotChartPanel = this.plotView.getPlotPanel();

        add(this.plotView, BorderLayout.CENTER);
    }

    /**
     * Return the OIFits data
     * @return OIFits data
     */
    OIFitsFile getOIFitsData() {
        return this.oiFitsFile;
    }

    /**
     * Define the OIFits data
     * @param oiFitsFile OIFits data
     */
    private void setOIFitsData(final OIFitsFile oiFitsFile) {
        this.oiFitsFile = oiFitsFile;
    }

    /**
     * Plot OIFits data using embedded OIFitsExplorer Plot panel
     * This code must be executed by the Swing Event Dispatcher thread (EDT)
     * @param oiFitsData OIFits data
     */
    public void plot(OIFitsFile oiFitsFile, String targetName) {
        logger.debug("plot : {} for target {}", oiFitsFile, targetName);

        // memorize chart data (used by export PDF):
        setOIFitsData(oiFitsFile);

        if (oiFitsFile == null || !oiFitsFile.hasOiData() || targetName == null || oiFitsFile.getAbsoluteFilePath() == null) {
            if (targetName == null) {
                this.jLabelMessage.setText("Missing target name in response.");
                // We could implement some fall back looking at OI_TARGET, using selected input one...
            } else {
                this.jLabelMessage.setText("No OIFits data available.");

            }

            display(false, true);

            // reset:
            ocm.reset();

        } else {
            display(true, false);
            /*
            // Fix file paths ie generate file names ?
            for (OIFitsFile oiFitsFile : oiFitsList) {
                oiFitsFile.setAbsoluteFilePath(ExportOIFitsAction.getDefaultFileName(oiFitsFile));
            }
             */
            // remove all oifits files:
            ocm.removeAllOIFitsFiles();

            // Add the given OIFits file:
            ocm.addOIFitsFile(oiFitsFile);

            // get current subset definition (copy):
            final SubsetDefinition subsetCopy = ocm.getCurrentSubsetDefinition();

            subsetCopy.getFilter().setTargetUID(targetName);
            // use all data files (default):
            // subset.getTables().clear();

            // fire subset changed event (generates OIFitsSubset and then plot asynchronously):
            ocm.updateSubsetDefinition(this, subsetCopy);
        }
    }

    /**
     * Show message or plot
     * @param showPlot flag indicating to show the plot view
     * @param showMessage flag indicating to show the message label
     */
    private void display(final boolean showPlot, final boolean showMessage) {
        this.jLabelMessage.setVisible(showMessage);
        this.plotView.setVisible(showPlot);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelMessage;
    // End of variables declaration//GEN-END:variables
}
