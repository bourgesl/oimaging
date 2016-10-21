/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.oimaging.model;

import fr.jmmc.jmcs.data.MimeType;
import fr.jmmc.jmcs.data.preference.SessionSettingsPreferences;
import fr.jmmc.jmcs.gui.component.StatusBar;
import fr.jmmc.jmcs.service.RecentFilesManager;
import fr.jmmc.jmcs.util.FileUtils;
import fr.jmmc.jmcs.util.ObjectUtils;
import fr.jmmc.jmcs.util.jaxb.JAXBFactory;
import fr.jmmc.jmcs.util.jaxb.JAXBUtils;
import fr.jmmc.jmcs.util.jaxb.XmlBindException;
import fr.jmmc.oiexplorer.core.model.event.EventNotifier;
import fr.jmmc.oitools.image.FitsImageFile;
import fr.jmmc.oitools.image.FitsImageLoader;
import fr.jmmc.oitools.model.OIFitsChecker;
import fr.jmmc.oitools.model.OIFitsFile;
import fr.jmmc.oitools.model.OIFitsLoader;
import fr.nom.tam.fits.FitsException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumMap;
import org.apache.commons.httpclient.auth.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: remove StatusBar / MessagePane (UI)
/**
 * Handle the oifits files collection.
 * @author mella, bourgesl
 */
public final class IRModelManager implements IRModelEventListener {

    /** Logger */
    private static final Logger logger = LoggerFactory.getLogger(IRModelManager.class);
    /** package name for JAXB generated code */
    private final static String IRMODEL_JAXB_PATH = IRModel.class.getPackage().getName();
    /** Singleton pattern */
    private final static IRModelManager instance = new IRModelManager();
    /* members */
    /** internal JAXB Factory */
    private final JAXBFactory jf;
    /** flag to enable/disable firing events during startup (before calling start) */
    private boolean enableEvents = false;

    /** associated file to the OIFits explorer collection */
    private File irModelFile = null;
    /** IR Model (session)*/
    private IRModel irModel = null;
    /* event dispatchers */
    /** IRModelEventType event notifier map */
    private final EnumMap<IRModelEventType, EventNotifier<IRModelEvent, IRModelEventType, Object>> irModelManagerEventNotifierMap;

    /**
     * Return the Manager singleton
     * @return singleton instance
     */
    public static IRModelManager getInstance() {
        return instance;
    }

    /**
     * Prevent instanciation of singleton.
     * Manager instance should be obtained using getInstance().
     */
    private IRModelManager() {
        super();

//        this.jf = JAXBFactory.getInstance(IRMODEL_JAXB_PATH);
        this.jf = null; // TODO

        logger.debug("IRModelManager: JAXBFactory: {}", this.jf);

        this.irModelManagerEventNotifierMap = new EnumMap<IRModelEventType, EventNotifier<IRModelEvent, IRModelEventType, Object>>(IRModelEventType.class);

        int priority = 0;
        EventNotifier<IRModelEvent, IRModelEventType, Object> eventNotifier;

        for (IRModelEventType eventType : IRModelEventType.values()) {
            // false argument means allow self notification:
            final boolean skipSourceListener = (eventType != IRModelEventType.IRMODEL_CHANGED);

            eventNotifier = new EventNotifier<IRModelEvent, IRModelEventType, Object>(eventType.name(), priority, skipSourceListener);

            this.irModelManagerEventNotifierMap.put(eventType, eventNotifier);
            priority += 10;
        }

        // listen for IRMODEL_CHANGED event to analyze collection and fire initial events:
        getirModelChangedEventNotifier().register(this);

        // reset anyway:
        reset();
    }

    /**
     * Free any resource or reference to this instance :
     * throw an IllegalStateException as it is invalid
     */
    @Override
    public void dispose() {
        if (logger.isDebugEnabled()) {
            logger.debug("dispose: {}", ObjectUtils.getObjectInfo(this));
        }

        throw new IllegalStateException("Using IRModelManager.dispose() is invalid !");
    }

    /* --- OIFits file collection handling ------------------------------------- */
    /**
     * Load the IR Model at given URL
     * @param file OIFits explorer collection file file to load
     * @param checker optional OIFits checker instance (may be null)
     * @param listener progress listener
     * @throws IOException if an I/O exception occurred
     * @throws IllegalStateException if an unexpected exception occurred
     * @throws XmlBindException if a JAXBException was caught while creating an unmarshaller
     */
    public void loadIRModel(final File file, final OIFitsChecker checker,
            final LoadIRModelListener listener) throws IOException, IllegalStateException, XmlBindException {
        loadIRModel(file, checker, listener, false);
    }

    /**
     * Load the IR Model at given URL or onl the include OIFits file references.
     * @param file OIFits explorer collection file file to load
     * @param checker optional OIFits checker instance (may be null)
     * @param listener progress listener
     * @param appendOIFitsFilesOnly load only OIFits and skip plot+subset if true, else reset and load whole collection content
     * @throws IOException if an I/O exception occurred
     * @throws IllegalStateException if an unexpected exception occurred
     * @throws XmlBindException if a JAXBException was caught while creating an unmarshaller
     */
    public void loadIRModel(final File file, final OIFitsChecker checker,
            final LoadIRModelListener listener, final boolean appendOIFitsFilesOnly) throws IOException, IllegalStateException, XmlBindException {

        final IRModel loadedUserCollection = (IRModel) JAXBUtils.loadObject(file.toURI().toURL(), this.jf);

//        OIDataCollectionFileProcessor.onLoad(loadedUserCollection);
    }

    private void postLoadIRModel(final File file, final IRModel oiDataCollection, final OIFitsChecker checker) {

        // after loadOIDataCollection as it calls reset():
        setIRModelFile(file);

        // add given file to Open recent menu
        RecentFilesManager.addFile(file);

        throw new IllegalStateException("TODO");
    }

    /**
     * Load the IR Model at given URL
     * @param file OIFits explorer collection file file to load
     * @throws IOException if an I/O exception occurred
     * @throws IllegalStateException if an unexpected exception occurred
     */
    public void saveIRModel(final File file) throws IOException, IllegalStateException {
        final long startTime = System.nanoTime();

        final IRModel saved = getIRModel();

//        OIDataCollectionFileProcessor.onSave(savedUserCollection);
        // TODO: may also save OIFits file copies into zip archive (xml + OIFits files) ??
        JAXBUtils.saveObject(file, saved, this.jf);

        setIRModelFile(file);

        logger.info("saveIRModel: duration = {} ms.", 1e-6d * (System.nanoTime() - startTime));

        // add given file to Open recent menu
        RecentFilesManager.addFile(file);
    }

    public void loadOIFitsFile(File file) throws IOException {
        loadOIFitsFile(file.getAbsolutePath(), null);
    }

    /**
     * Load the given OI Fits File with the given checker component
     * and add it to the IR Model
     * @param fileLocation absolute File Path or remote URL
     * @param checker checker component
     * @throws IOException if a fits file can not be loaded
     */
    public void loadOIFitsFile(final String fileLocation, final OIFitsChecker checker) throws IOException {
        addOIFitsFile(loadOIFits(fileLocation, checker));
    }

    /**
     * (Download) and load the given OI Fits File with the given checker component
     * @param fileLocation absolute File Path or remote URL
     * @param checker checker component
     * @return loaded OIFits File
     * @throws IOException if a fits file can not be loaded
     */
    private static OIFitsFile loadOIFits(final String fileLocation, final OIFitsChecker checker) throws IOException {
        //@todo test if file has already been loaded before going further ??

        final OIFitsFile oifitsFile;
        try {
            // retrieve oifits if remote or use local one
            if (FileUtils.isRemote(fileLocation)) {
                // TODO let the user customize the application file storage preference:
                final String parentPath = SessionSettingsPreferences.getApplicationFileStorage();

                final File localCopy = FileUtils.retrieveRemoteFile(fileLocation, parentPath, MimeType.OIFITS);

                if (localCopy != null) {
                    // TODO: remove StatusBar !
                    StatusBar.show("loading file: " + fileLocation + " ( local copy: " + localCopy.getAbsolutePath() + " )");

                    oifitsFile = OIFitsLoader.loadOIFits(checker, localCopy.getAbsolutePath());
                    oifitsFile.setSourceURI(new URI(fileLocation));
                } else {
                    // download failed:
                    oifitsFile = null;
                }
            } else {
                // TODO: remove StatusBar !
                StatusBar.show("loading file: " + fileLocation);

                oifitsFile = OIFitsLoader.loadOIFits(checker, fileLocation);
            }
        } catch (AuthenticationException ae) {
            throw new IOException("Could not load the file : " + fileLocation, ae);
        } catch (IOException ioe) {
            throw new IOException("Could not load the file : " + fileLocation, ioe);
        } catch (FitsException fe) {
            throw new IOException("Could not load the file : " + fileLocation, fe);
        } catch (URISyntaxException use) {
            throw new IOException("Could not load the file : " + fileLocation, use);
        }

        if (oifitsFile == null) {
            throw new IOException("Could not load the file : " + fileLocation);
        }
        return oifitsFile;
    }

    /**
     * Add an OIDataFile given its corresponding OIFits structure
     * @param oiFitsFile OIFits structure
     * @return true if an OIDataFile was added
     */
    public boolean addOIFitsFile(final OIFitsFile oiFitsFile) {
        if (oiFitsFile != null) {

            irModel.setOifitsFile(oiFitsFile);

            fireIRModelChanged();

            return true;

        }
        return false;
    }

    public void loadFitsImageFile(File file) throws IOException {
        loadFitsImageFile(file.getAbsolutePath());
    }

    /**
     * Load the given OI Fits File with the given checker component
     * and add it to the IR Model
     * @param fileLocation absolute File Path or remote URL
     * @param checker checker component
     * @throws IOException if a fits file can not be loaded
     */
    public void loadFitsImageFile(final String fileLocation) throws IOException {
        addFitsImageFile(loadFitsImage(fileLocation));
    }

    /**
     * (Download) and load the given OI Fits File with the given checker component
     * @param fileLocation absolute File Path or remote URL
     * @param checker checker component
     * @return loaded FitsImage File
     * @throws IOException if a fits file can not be loaded
     */
    private static FitsImageFile loadFitsImage(final String fileLocation) throws IOException {
        //@todo test if file has already been loaded before going further ??

        final FitsImageFile fitsImageFile;
        try {
            // retrieve oifits if remote or use local one
            if (FileUtils.isRemote(fileLocation)) {
                // TODO let the user customize the application file storage preference:
                final String parentPath = SessionSettingsPreferences.getApplicationFileStorage();

                final File localCopy = FileUtils.retrieveRemoteFile(fileLocation, parentPath, MimeType.OIFITS);

                if (localCopy != null) {
                    // TODO: remove StatusBar !
                    StatusBar.show("loading file: " + fileLocation + " ( local copy: " + localCopy.getAbsolutePath() + " )");

                    fitsImageFile = FitsImageLoader.load(localCopy.getAbsolutePath(), true, true);
                } else {
                    // download failed:
                    fitsImageFile = null;
                }
            } else {
                // TODO: remove StatusBar !
                StatusBar.show("loading file: " + fileLocation);

                fitsImageFile = FitsImageLoader.load(fileLocation, true, true);
            }
        } catch (AuthenticationException ae) {
            throw new IOException("Could not load the file : " + fileLocation, ae);
        } catch (IOException ioe) {
            throw new IOException("Could not load the file : " + fileLocation, ioe);
        } catch (URISyntaxException use) {
            throw new IOException("Could not load the file : " + fileLocation, use);
        } catch (FitsException fe) {
            throw new IOException("Could not load the file : " + fileLocation, fe);
        } catch (IllegalArgumentException iae) {
            throw iae;
        }

        if (fitsImageFile == null) {
            throw new IOException("Could not load the file : " + fileLocation);
        }
        return fitsImageFile;
    }

    /**
     * Add an FitsImageFile given its corresponding FitsImage structure
     * @param fitsImageFile FitsImage file
     * @return true if an FitsImageFile was added
     */
    public boolean addFitsImageFile(final FitsImageFile fitsImageFile) {
        if (fitsImageFile != null) {
            irModel.addFitsImageFile(fitsImageFile);
            fireIRModelChanged();
            return true;
        }
        return false;
    }

    /**
     * Return the current OIFits explorer collection file
     * @return the current OIFits explorer collection file or null if undefined
     */
    public File getirModelFile() {
        return this.irModelFile;
    }

    /**
     * Private : define the current OIFits explorer collection file
     * @param file new OIFits explorer collection file to use
     */
    private void setIRModelFile(final File file) {
        this.irModelFile = file;
    }

    // TODO: save / merge ... (elsewhere)
    /**
     * Reset the OIFits file collection and start firing events
     */
    public void start() {
        enableEvents = true;
        reset();
    }

    /**
     * Reset the OIFits file collection
     */
    public void reset() {

        irModel = new IRModel();
        setIRModelFile(null);

        fireIRModelChanged();
    }

    /**
     * Remove the OIDataFile given its corresponding OIFits structure (filePath matching)
     * @param oiFitsFile OIFits structure
     * @return removed OIDataFile or null if not found
     */
    public OIFitsFile removeOIFitsFile(final OIFitsFile oiFitsFile) {
        final OIFitsFile previous = null;

        return previous;
    }

    /**
     * Protected: return the IR Model
     * // TODO try to make method private back and replace by event handling
     * @return IR Model
     */
    public IRModel getIRModel() {
        return irModel;
    }

    // --- EVENTS ----------------------------------------------------------------
    /**
     * Unbind the given listener to ANY event
     * @param listener listener to unbind
     */
    public void unbind(final IRModelEventListener listener) {
        for (final EventNotifier<IRModelEvent, IRModelEventType, Object> eventNotifier : this.irModelManagerEventNotifierMap.values()) {
            eventNotifier.unregister(listener);
        }
    }

    /**
     * Bind the given listener to IRMODEL_CHANGED event and fire such event to initialize the listener properly
     * @param listener listener to bind
     */
    public void bindIRModelChangedEvent(final IRModelEventListener listener) {
        getirModelChangedEventNotifier().register(listener);

        // Note: no fire IRMODEL_CHANGED event because first call to reset() fires it (at the right time i.e. not too early):
        // force fire IRMODEL_CHANGED event to initialize the listener ASAP:
        fireIRModelChanged(null, listener);
    }

    /**
     * Return the IRMODEL_CHANGED event notifier
     * @return IRMODEL_CHANGED event notifier
     */
    private EventNotifier<IRModelEvent, IRModelEventType, Object> getirModelChangedEventNotifier() {
        return this.irModelManagerEventNotifierMap.get(IRModelEventType.IRMODEL_CHANGED);
    }

    /**
     * Return the READY event notifier
     * @return READY event notifier
     */
    public EventNotifier<IRModelEvent, IRModelEventType, Object> getReadyEventNotifier() {
        return this.irModelManagerEventNotifierMap.get(IRModelEventType.READY);
    }

    /**
     * This fires an COLLECTION_CHANGED event to given registered listener ASYNCHRONOUSLY !
     *
     * Note: this is ONLY useful to initialize new registered listeners properly !
     *
     * @param source event source
     * @param destination destination listener (null means all)
     */
    public void fireIRModelChanged(final Object source, final IRModelEventListener destination) {
        if (enableEvents) {
            if (logger.isDebugEnabled()) {
                logger.debug("fireOIFitsCollectionChanged TO {}", (destination != null) ? destination : "ALL");
            }
            getirModelChangedEventNotifier().queueEvent((source != null) ? source : this,
                    new IRModelEvent(IRModelEventType.IRMODEL_CHANGED, null, getIRModel()), destination);
        }
    }

    /**
     * This fires an COLLECTION_CHANGED event to given registered listeners ASYNCHRONOUSLY !
     */
    private void fireIRModelChanged() {
        fireIRModelChanged(this, null);
    }

    /**
     * This fires a READY event to given registered listener ASYNCHRONOUSLY !
     * @param source event source
     * @param destination destination listener (null means all)
     */
    public void fireReady(final Object source, final IRModelEventListener destination) {
        if (enableEvents) {
            if (logger.isDebugEnabled()) {
                logger.debug("fireReadyChanged TO {}", (destination != null) ? destination : "ALL");
            }
            getReadyEventNotifier().queueEvent((source != null) ? source : this,
                    new IRModelEvent(IRModelEventType.READY, null, getIRModel()), destination);
        }
    }

    /*
     * IRModelEventListener implementation
     */
    /**
     * Return the optional subject id i.e. related object id that this listener accepts
     * @param type event type
     * @return subject id (null means accept any event) or DISCARDED_SUBJECT_ID to discard event
     */
    @Override
    public String getSubjectId(final IRModelEventType type) {
        // accept all
        return null;
    }

    /**
     * Handle the given IR Model event
     * @param event IR Model event
     */
    @Override
    public void onProcess(final IRModelEvent event) {
        logger.debug("onProcess {}", event);

//        switch (event.getType()) {
//            case IRMODEL_CHANGED:
//                // perform analysis:
//                irModel.analyzeCollection();
//                break;
//            default:
//        }
        logger.debug("onProcess {} - done", event);
    }

}