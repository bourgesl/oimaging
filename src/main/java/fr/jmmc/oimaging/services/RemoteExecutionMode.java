/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.oimaging.services;

import fr.cnes.sitools.extensions.astro.application.uws.client.ClientUWS;
import fr.cnes.sitools.extensions.astro.application.uws.client.ClientUWSException;
import fr.jmmc.jmcs.data.app.ApplicationDescription;
import fr.jmmc.jmcs.util.StringUtils;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URISyntaxException;
import net.ivoa.xml.uws.v1.ExecutionPhase;
import net.ivoa.xml.uws.v1.JobSummary;
import net.ivoa.xml.uws.v1.ResultReference;
import net.ivoa.xml.uws.v1.Results;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.html.FormData;
import org.restlet.ext.html.FormDataSet;
import org.restlet.representation.FileRepresentation;
import org.restlet.util.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Support remote service runner.
 * @author Guillaume MELLA.
 */
public final class RemoteExecutionMode implements OImagingExecutionMode {

    // Use -DRemoteExecutionMode.local=true (dev) to use local uws server (docker)
    private static final boolean USE_LOCAL = Boolean.getBoolean("RemoteExecutionMode.local");

    // Use -DRemoteExecution.beta=true (dev) to use remote beta uws server (docker)
    private static final boolean USE_BETA = Boolean.getBoolean("RemoteExecution.beta") || ApplicationDescription.isBetaVersion();

    /** Class logger */
    private static final Logger _logger = LoggerFactory.getLogger(RemoteExecutionMode.class.getName());

    public static final String SERVICE_PATH = "oimaging/oimaging";

    public static final String[] SERVER_URLS = ((USE_LOCAL) ? new String[]{"http://127.0.0.1:8080/OImaging-uws/"}
            : ((USE_BETA) ? (new String[]{"http://oimaging-beta.jmmc.fr/OImaging-uws/"})
                    : (new String[]{"http://oimaging.jmmc.fr/OImaging-uws/"})));

    private static final ClientFactory FACTORY = new ClientFactory();

    /** singleton */
    public static final RemoteExecutionMode INSTANCE = new RemoteExecutionMode();

    private final static class ClientFactory {

        /** UWS client to execute IR on a remote server */
        private ClientUWS uwsClient = null;

        ClientFactory() {
        }

        /**
         * try to connect to the first server of the hardcoded list.
         * // TODO move this method in a factory
         */
        public ClientUWS getClient() throws ClientUWSException {
            if (uwsClient == null) {
                ClientUWSException cue = null;
                // Move it in a property file (or constant at least)
                for (String url : SERVER_URLS) {
                    try {
                        final ClientUWS c = new ClientUWS(url, SERVICE_PATH);

                        // Get home page as an isAlive request:
                        if (c.getHomePage() != null) {
                            uwsClient = c;
                            _logger.info("UWS service endpoint : '{}'", url);
                            break;
                        }
                    } catch (ClientUWSException ce) {
                        _logger.info("UWS service endpoint unreachable: '{}'", url);
                        if (cue == null) {
                            cue = ce;
                        }
                    } catch (Exception e) {
                        // we should avoid to catch Exception, please catch e just before
                        _logger.info("UWS service endpoint unreachable: '{}'", url);
                        throw new IllegalStateException("UWS service endpoint unreachable: '" + url + "'", e);
                    }
                }
                if (uwsClient == null) {
                    if (cue != null) {
                        throw cue;
                    }
                    throw new ClientUWSException(Status.SERVER_ERROR_SERVICE_UNAVAILABLE, "No available endpoint !");
                }
            }
            return uwsClient;
        }

        public void reset() {
            this.uwsClient = null;
        }
    }

    private RemoteExecutionMode() {
        super();
    }

    /**
     * Start the remote application and wait end of execution.
     *
     * @param software software to run
     * @param cliOptions software options on command line or null
     * @param inputFilename input filename
     * @param result the service result pointing result file to write data into.
     * @throws IllegalStateException if the job can not be submitted to the job queue
     */
    public void callUwsOimagingService(final String software, final String cliOptions, final String inputFilename, ServiceResult result) throws IllegalStateException, ClientUWSException, URISyntaxException, IOException {

        if (StringUtils.isEmpty(software)) {
            throw new IllegalArgumentException("empty application name !");
        }
        if (StringUtils.isEmpty(inputFilename)) {
            throw new IllegalArgumentException("empty input filename !");
        }
        if (StringUtils.isEmpty(result.getOifitsResultFile().getAbsolutePath())) {
            throw new IllegalArgumentException("empty output filename !");
        }
        if (StringUtils.isEmpty(result.getExecutionLogResultFile().getAbsolutePath())) {
            throw new IllegalArgumentException("empty log filename !");
        }

        _logger.info("callUwsOimagingService: software={} cliOptions={} inputFilenane={}", software, cliOptions, inputFilename);

        // prepare input of next uws call
        final FormDataSet formDataSet = new FormDataSet();
        formDataSet.setMultipart(true);

        final Series<FormData> fdsEntries = formDataSet.getEntries();

        // TODO declare field name as constant ( and share them with server side )
        fdsEntries.add(new FormData("inputfile",
                new FileRepresentation(new File(inputFilename), MediaType.IMAGE_ALL)));
        fdsEntries.add(new FormData("software", software));

        if (cliOptions != null) {
            fdsEntries.add(new FormData("cliOptions", cliOptions));
        }

        // start task in autostart mode
        formDataSet.add("PHASE", "RUN");

        // create job
        ClientUWS client = null;
        String jobId = null;

        boolean retry = true;
        while (client == null) {
            // may throw IllegalStateException if no running service available:
            client = FACTORY.getClient();

            try {
                jobId = client.createJob(formDataSet);
            } catch (ClientUWSException cue) {
                final Throwable rootCause = getRootCause(cue);
                if (rootCause instanceof ConnectException) {
                    if (retry) {
                        _logger.debug("ConnectException caught: ", rootCause);
                        // retry once if connect exception:
                        retry = false;
                        // reset to be sure:
                        FACTORY.reset();
                        client = null;
                        jobId = null;
                    } else {
                        // throw ConnectException
                        throw (ConnectException) rootCause;
                    }
                } else {
                    throw cue;
                }
            }
        }

        // Assume that first state is executing
        ExecutionPhase phase = ExecutionPhase.EXECUTING;

        boolean cancelled = false;
        try {
            // loop and query return status
            while (phase == ExecutionPhase.EXECUTING) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException ie) {
                    _logger.debug("Interrupted.");
                    result.setErrorMessage("Cancelled job.");

                    _logger.debug("Job[{}] aborting ...", jobId);
                    client.setAbortJob(jobId);
                    _logger.debug("Job[{}] aborted.", jobId);

                    cancelled = true;
                    phase = ExecutionPhase.ABORTED;
                    break;
                }
                phase = client.getJobPhase(jobId);

                _logger.debug("getJobPhase[{}] : {}", jobId, phase);

                // TODO timeout ? or just wait 'Cancel' button
            }
            _logger.info("End of execution for job '{}' in phase '{}'", jobId, phase);

            if (phase == ExecutionPhase.COMPLETED) {
                prepareResult(client, jobId, result);
            } else if (!cancelled || (phase != ExecutionPhase.ABORTED)) {
                JobSummary jobInfo = client.getJobInfo(jobId);
                _logger.error("Error in execution for job '{}': {} ", jobId, jobInfo.getErrorSummary());

                result.setErrorMessage("Execution error: "
                        + ((jobInfo.getErrorSummary() != null) ? jobInfo.getErrorSummary() : phase));
            }
            if (cancelled || (phase == ExecutionPhase.ABORTED)) {
                result.setCancelled(true);
            }
        } finally {
            // TODO: decide if cleanup is delayed on the server-side to collect datasets in error:
            if (true) {
                try {
                    client.deleteJobInfo(jobId);
                } catch (ClientUWSException cue) {
                    _logger.warn("Can't delete job '{}'", jobId, cue);
                }
            }
        }
    }

    private static void prepareResult(final ClientUWS client, String jobId, ServiceResult result) throws ClientUWSException, URISyntaxException, IOException {
        final Results results = client.getJobResults(jobId);

        for (ResultReference resultRef : results.getResult()) {
            final String id = resultRef.getId();
            final String href = resultRef.getHref();

            if ("logfile".equals(id)) {
                // get logfile
                _logger.info("Downloading logfile from: {}", href);

                if (client.downloadFile(resultRef.getHref(), result.getExecutionLogResultFile())) {
                    _logger.info("logfile downloaded at: {}", result.getExecutionLogResultFile());
                }
            } else if ("outputfile".equals(id)) {
                // get result file
                _logger.info("Downloading outputfile from: {}", href);

                if (client.downloadFile(href, result.getOifitsResultFile())) {
                    _logger.info("outputfile downloaded at: {}", result.getOifitsResultFile());
                }
            } else {
                // TODO: FIX such error
                // store additional information
                throw new IllegalStateException("UWS service returned more results than required : " + id);
            }
        }
    }

    @Override
    public ServiceResult reconstructsImage(final String software, final String options, final File inputFile) {
        final ServiceResult result = new ServiceResult(inputFile);

        Exception e = null;
        try {
            // TODO add log output retrieval
            callUwsOimagingService(software, options, inputFile.getAbsolutePath(), result);
        } catch (IllegalStateException ise) {
            throw ise;
        } catch (ClientUWSException ce) {
            final Throwable rootCause = getRootCause(ce);
            if (rootCause instanceof ConnectException) {
                e = (ConnectException) rootCause;
            } else {
                e = ce;
            }
        } catch (URISyntaxException use) {
            e = use;
        } catch (ConnectException ce) {
            e = ce;
        } catch (IOException ioe) {
            e = ioe;
        }
        if (e != null) {
            throw new IllegalStateException(e);
        }
        return result;
    }

    private static Throwable getRootCause(final Throwable th) {
        Throwable parent = th;
        while (parent.getCause() != null) {
            parent = parent.getCause();
        }
        return parent;
    }
}
