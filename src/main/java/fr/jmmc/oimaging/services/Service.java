/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.oimaging.services;

import fr.jmmc.oimaging.services.software.SoftwareInputParam;
import fr.jmmc.oitools.image.ImageOiInputParam;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Details service as a combinaison of program handled by a dedicated execution
 * mode, with other metadata attributes.
 * @author mellag
 */
// Could be replaced by a jaxb generated class that could load input from xml
// ...and completed at runtime by a remote capability discovery ...
public final class Service {

    /** Logger */
    private static final Logger logger = LoggerFactory.getLogger(Service.class);

    private final String name;
    private final String program;
    private final OImagingExecutionMode execMode;
    private final String description;
    private final String contact;
    private final SoftwareInputParam softwareInputParam;

    public Service(final String name, final String program, final OImagingExecutionMode execMode, final String description, final String contact,
                   final SoftwareInputParam softwareInputParam) {
        this.name = name;
        this.program = program;
        this.execMode = execMode;
        this.description = description;
        this.contact = contact;
        this.softwareInputParam = softwareInputParam;
    }

    public boolean isCompatibleParams(final Service otherService) {
        return this.softwareInputParam == otherService.getSoftwareInputParam();
    }

    public String getName() {
        return name;
    }

    public String getProgram() {
        return program;
    }

    public OImagingExecutionMode getExecMode() {
        return execMode;
    }

    public String getDescription() {
        return description;
    }

    public String getContact() {
        return contact;
    }

    protected SoftwareInputParam getSoftwareInputParam() {
        return softwareInputParam;
    }

    public String toString() {
        return name;
    }

    public String[] getSupported_RGL_NAME() {
        return softwareInputParam.getSupported_RGL_NAME();
    }

    public void initSpecificParams(final ImageOiInputParam params, final boolean applyDefaults) {
        softwareInputParam.update(params, applyDefaults);
    }

    public void validate(final ImageOiInputParam params, final List<String> failures) {
        softwareInputParam.validate(params, failures);
    }

    public String getDefaultCliOptions() {
        return softwareInputParam.getDefaultCliOptions();
    }

    public boolean supportsStandardKeyword(final String keywordName) {
        final boolean supports = softwareInputParam.supportsStandardKeyword(keywordName);
        if (logger.isDebugEnabled()) {
            logger.debug("Service[{}] supports keyword [{}]: {}", name, keywordName, supports);
        }
        return supports;
    }

    public boolean supportsMissingKeyword(final String keywordName) {
        final boolean supports = softwareInputParam.supportsMissingKeyword(keywordName);
        if (logger.isDebugEnabled()) {
            logger.debug("Service[{}] supports Missing keyword [{}]: {}", name, keywordName, supports);
        }
        return supports;
    }
}
