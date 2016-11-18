/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.oimaging.gui.action;

import fr.jmmc.jmcs.gui.action.RegisteredAction;
import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.jmcs.gui.component.StatusBar;
import fr.jmmc.jmcs.gui.task.Task;
import fr.jmmc.jmcs.gui.task.TaskSwingWorker;
import fr.jmmc.jmcs.gui.task.TaskSwingWorkerExecutor;
import fr.jmmc.jmcs.util.ImageUtils;
import fr.jmmc.oimaging.model.IRModel;
import fr.jmmc.oimaging.model.IRModelManager;
import fr.jmmc.oimaging.services.Service;
import fr.jmmc.oimaging.services.ServiceResult;
import fr.nom.tam.fits.FitsException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import javax.swing.Action;
import javax.swing.ButtonModel;
import javax.swing.text.Document;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunAction extends RegisteredAction {

    private static final long serialVersionUID = 1L;
    /** Main logger */
    static final Logger logger = LoggerFactory.getLogger(RunAction.class.getName());
    /** Class name. This name is used to register to the ActionRegistrar */
    public final static String className = RunAction.class.getName();
    /** Action name. This name is used to register to the ActionRegistrar */
    public static final String actionName = "run";

    private ButtonModel iTMaxButtonModel = null;
    private Document iTMaxDocument = null;
    private ButtonModel skipPlotDocument = null;

    private final Task task;

    public RunAction() {
        super(className, actionName);
        task = new Task("MakeImage");
    }

    private IRModel getCurrentIRModel() {
        return IRModelManager.getInstance().getIRModel();
    }

    public void setConstraints(ButtonModel iTMaxButtonModel, Document iTMaxDocument, ButtonModel skipPlotDocument) {
        this.iTMaxButtonModel = iTMaxButtonModel;
        this.iTMaxDocument = iTMaxDocument;
        this.skipPlotDocument = skipPlotDocument;
    }

    private void setRunningState(final IRModel irModel, boolean running) {
        irModel.setRunning(running);
        putValue(Action.NAME, (running) ? "Cancel" : "Run");
        putValue(Action.LARGE_ICON_KEY, running ? ImageUtils.loadResourceIcon("fr/jmmc/jmcs/resource/image/spinner.gif") : null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO: make a snapshot of the required information from model
        final IRModel irModel = getCurrentIRModel();
        if (irModel.isRunning()) {
            // cancel job
            TaskSwingWorkerExecutor.cancelTask(getTask());
            setRunningState(irModel, false);
        } else {

            File inputFile = null;
            try {
                inputFile = irModel.prepareTempFile();
            } catch (FitsException ex) {
                logger.error("Can't prepare temporary file before running process", ex);
                setRunningState(irModel, false);
            } catch (IOException ex) {
                logger.error("Can't prepare temporary file before running process", ex);
                setRunningState(irModel, false);
            }

            StatusBar.show("Spawn " + irModel.getSelectedService() + " process");
            // change model state to lock it and extract its snapshot
            setRunningState(irModel, true);
            new RunFitActionWorker(getTask(), inputFile, irModel, this).executeTask();
        }
    }

    public Task getTask() {
        return task;
    }

    static class RunFitActionWorker extends TaskSwingWorker<ServiceResult> {

        private final File inputFile;
        private final IRModel irModel;
        private final RunAction parentAction;

        RunFitActionWorker(Task task, File inputFile, IRModel irModel, RunAction runAction) {
            super(task);
            this.inputFile = inputFile;
            this.irModel = irModel; // only for callback
            this.parentAction = runAction;
        }

        @Override
        public ServiceResult computeInBackground() {
            final Service service = irModel.getSelectedService();
            return service.getExecMode().reconstructsImage(service.getProgram(), inputFile);
        }

        @Override
        public void refreshUI(ServiceResult serviceResult) {
            // action finished, we can change state and update model just after.
            parentAction.setRunningState(irModel, false);

            if (serviceResult.isValid()) {
                this.irModel.updateWithNewModel(serviceResult);
            } else {
                this.irModel.showLog(serviceResult.getErrorMessage(), serviceResult, null);
            }
        }

        /**
         * Refresh GUI when no data (null returned or cancelled) invoked by the Swing Event Dispatcher Thread (Swing EDT)
         * @param cancelled true if task cancelled; false if null returned by computeInBackground()
         */
        @Override
        public void refreshNoData(final boolean cancelled) {
            // action finished, we can change state.
            parentAction.setRunningState(irModel, false);
        }

        @Override
        public void handleException(ExecutionException ee) {
            // action finished, we can change state.
            parentAction.setRunningState(irModel, false);

            // filter some exceptions to avoid feedback report
            if (filterNetworkException(ee)) {
                MessagePane.showErrorMessage("Please check your network setup", "Please check your network setup", ee);
            } else {
                super.handleException(ee);
            }

            StatusBar.show("Error occured during process");
        }

        public boolean filterNetworkException(Exception e) {
            Throwable c = getRootCause(e);
            return c != null
                    && (c instanceof UnknownHostException
                    || c instanceof ConnectTimeoutException);
        }

        private static Throwable getRootCause(final Throwable th) {
            Throwable parent = th;
            while (parent.getCause() != null) {
                parent = parent.getCause();
            }
            return parent;
        }
    }
}
