/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.oimaging.services;

import fr.jmmc.jmcs.util.FileUtils;
import fr.jmmc.oitools.model.OIFitsFile;
import fr.jmmc.oitools.model.OIFitsLoader;
import fr.nom.tam.fits.FitsException;
import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Result container that gather multiple elements.
 * @author mellag
 */
public final class ServiceResult {
    
    private final File inputFile;
    private final File oifitsResultFile;
    private final File executionLogResultFile;
    // TODO int errorCode;
    private boolean cancelled = false;
    private boolean valid = false;
    private String errorMessage = null;
    private Date startTime;
    private Date endTime;

    // User appreciation attribute
    private int rating;
    private String comments;
    
    private Service service;

    // Post process cached data
    private OIFitsFile oiFitsFile = null;
    private String executionLog = null;

    /**
     * Helper constructor that created result files using given inputfile name.
     * inputFile must be a tempory filename.
     * The oifitsResultFile result get inpuFilename with .output.fits suffix.
     * The log file get inputFilename with .log.txt suffix.
     */
    public ServiceResult(File inputFile) {
        String inputFilename = inputFile.getName();
        this.inputFile = inputFile;
        this.oifitsResultFile = FileUtils.getTempFile(inputFilename + ".output.fits");
        this.executionLogResultFile = FileUtils.getTempFile(inputFilename + ".log.txt");
        
        this.rating = 0;
        this.comments = "No comments";
        
        init();
    }
    
    private void init() {
        setStartTime(new Date());
    }
    
    public File getInputFile() {
        return inputFile;
    }
    
    public File getOifitsResultFile() {
        return oifitsResultFile;
    }
    
    public void loadOIFitsFile() throws IOException, FitsException {
        if ((oiFitsFile == null) && (oifitsResultFile != null) && oifitsResultFile.exists()) {
            try {
                oiFitsFile = OIFitsLoader.loadOIFits(oifitsResultFile.getAbsolutePath());
            } catch (IOException | FitsException e) {
                setValid(false);
                throw e;
            }
        }
    }
    
    public OIFitsFile getOifitsFile() {
        return oiFitsFile;
    }
    
    public File getExecutionLogResultFile() {
        return executionLogResultFile;
    }
    
    public void loadExecutionLogFile() throws IOException {
        if ((executionLog == null) && (executionLogResultFile != null) && executionLogResultFile.exists()) {
            executionLog = FileUtils.readFile(executionLogResultFile.getAbsoluteFile());
        }
    }
    
    public String getExecutionLog() {
        return executionLog;
    }
    
    public int getRating() {
        return rating;
    }
    
    public String getComments() {
        return comments;
    }
    
    public boolean isCancelled() {
        return cancelled;
    }
    
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public boolean isValid() {
        return valid;
    }
    
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public Date getStartTime() {
        return this.startTime;
    }
    
    public Date getEndTime() {
        return this.endTime;
    }
    
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
    
    public Service getService() {
        return service;
    }
    
    public void setService(Service service) {
        this.service = service;
    }
    
    public void setRating(int rating) {
        this.rating = rating;
    }
    
    public void setComments(String comments) {
        this.comments = comments;
    }
    
}
