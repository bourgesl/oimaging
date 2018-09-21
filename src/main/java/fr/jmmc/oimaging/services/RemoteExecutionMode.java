/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.oimaging.services;

import fr.cnes.sitools.extensions.astro.application.uws.client.ClientUWS;
import fr.cnes.sitools.extensions.astro.application.uws.client.ClientUWSException;
import fr.jmmc.jmcs.network.http.Http;
import fr.jmmc.jmcs.util.StringUtils;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import net.ivoa.xml.uws.v1.ExecutionPhase;
import net.ivoa.xml.uws.v1.JobSummary;
import net.ivoa.xml.uws.v1.ResultReference;
import net.ivoa.xml.uws.v1.Results;
import org.restlet.data.MediaType;
import org.restlet.ext.html.FormData;
import org.restlet.ext.html.FormDataSet;
import org.restlet.representation.FileRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Support remote service runner.
 * @author Guillaume MELLA.
 */
public final class RemoteExecutionMode implements OImagingExecutionMode {

    // TODO add a preference to switch or revert order between dev/prod
    public static final boolean USE_LOCAL = false;

    /** Class logger */
    private static final Logger _logger = LoggerFactory.getLogger(RemoteExecutionMode.class.getName());

    public static final String SERVICE_PATH = "oimaging/oimaging";

    public static final String[] SERVER_URLS = (USE_LOCAL)
            ? new String[]{"http://127.0.0.1:8080/OImaging-uws/"}
            : new String[]{
                "http://fe.jmmc.fr/OImaging-uws/",
                "http://fe.preprod.jmmc.fr/OImaging-uws/"
            };

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
                // Move it in a property file ( or constant at least)
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
                    throw cue;
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
     * @param inputFilename input filename
     * @param result the service result pointing result file to write data into.
     * @throws IllegalStateException if the job can not be submitted to the job queue
     */
    public void callUwsOimagingService(final String software, final String inputFilename, ServiceResult result) throws IllegalStateException, ClientUWSException, URISyntaxException, IOException {

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

        _logger.info("callUwsOimagingService: {} {}", software, inputFilename);

        // prepare input of next uws call
        FormDataSet fds = new FormDataSet();
        fds.setMultipart(true);

        //Disposition disposition = new Disposition(Disposition.TYPE_INLINE, fileForm);
        File f = new File(inputFilename);
        FileRepresentation entity = new FileRepresentation(f, MediaType.IMAGE_PNG); // TODO: FIX MediaType
        //entity.setDisposition(disposition);

        // TODO declare field name as constant ( and share them with server side )
        FormData fdInputFile = new FormData("inputfile", entity);
        fds.getEntries().add(fdInputFile);

        FormData fdSoftware = new FormData("software", software);
        fds.getEntries().add(fdSoftware);

        // start task in autostart mode
        fds.add("PHASE", "RUN");

        // create job
        ClientUWS client = null;
        String jobId = null;

        boolean retry = true;
        while (client == null) {
            // may throw IllegalStateException if no running service available:
            client = FACTORY.getClient();

            try {
                jobId = client.createJob(fds);
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

        // loop and query return status
        while (phase == ExecutionPhase.EXECUTING) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException ie) {
                _logger.info("Interrupted.");

                result.setErrorMessage("Cancelled job.");
                client.setAbortJob(jobId);
                return;
            }
            phase = client.getJobPhase(jobId);

            _logger.debug("getJobPhase[{}] : {}", jobId, phase);

            // TODO timeout ? or just wait 'Cancel' button
        }
        _logger.info("End of execution for job '{}' in phase '{}'", jobId, phase);

        try {
            if (phase == ExecutionPhase.COMPLETED) {
                prepareResult(client, jobId, result);
            } else {
                JobSummary jobInfo = client.getJobInfo(jobId);
                _logger.error("Error in execution for job '{}': {} ", jobId, jobInfo.getErrorSummary());

                result.setErrorMessage("Execution error: " + jobInfo.getErrorSummary());
            }
        } finally {
            try {
                client.deleteJobInfo(jobId);
            } catch (ClientUWSException cue) {
                _logger.warn("Can't delete job", cue);
            }
        }
    }

    private static void prepareResult(final ClientUWS client, String jobId, ServiceResult result) throws ClientUWSException, URISyntaxException, IOException {
        Results results = client.getJobResults(jobId);

        for (ResultReference resultRef : results.getResult()) {
            final String id = resultRef.getId();
            URI uri = new URI(resultRef.getHref());
            if ("logfile".equals(id)) {
                // get logfile
                if (Http.download(uri, result.getExecutionLogResultFile(), false)) {
                    _logger.info("logfile downloaded at : {}", result.getExecutionLogResultFile());
                }
            } else if ("outputfile".equals(id)) {
                // get result file
                if (Http.download(uri, result.getOifitsResultFile(), false)) {
                    _logger.info("outputfile downloaded at : {}", result.getOifitsResultFile());
                }
            } else {
                // TODO: FIX such error
                // store additional information
                throw new IllegalStateException("UWS service return more info than required : " + id);
            }
        }
    }

    @Override
    public ServiceResult reconstructsImage(final String software, final File inputFile) {
        final ServiceResult result = new ServiceResult(inputFile);

        Exception e = null;
        try {
            // TODO add log output retrieval
            callUwsOimagingService(software, inputFile.getAbsolutePath(), result);
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
