/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.oitools.model;

import fr.jmmc.oitools.OIFitsConstants;
import fr.jmmc.oitools.meta.ColumnMeta;
import fr.jmmc.oitools.meta.KeywordMeta;
import fr.jmmc.oitools.meta.Types;
import fr.jmmc.oitools.meta.Units;
import java.util.logging.Level;

/**
 * OIData table is the base class for OI_VIS, OI_VIS2 and OI_T3 tables.
 */
public class OIData extends OITable {

    /* constants */
    /* static descriptors */
    /** DATE-OBS keyword descriptor */
    private final static KeywordMeta KEYWORD_DATE_OBS = new KeywordMeta(OIFitsConstants.KEYWORD_DATE_OBS,
            "UTC start date of observations", Types.TYPE_CHAR);
    /** TIME column descriptor */
    private final static ColumnMeta COLUMN_TIME = new ColumnMeta(OIFitsConstants.COLUMN_TIME,
            "UTC time of observation", Types.TYPE_DBL, Units.UNIT_SECOND);
    /** MJD column descriptor */
    private final static ColumnMeta COLUMN_MJD = new ColumnMeta(OIFitsConstants.COLUMN_MJD,
            "modified Julian Day", Types.TYPE_DBL, Units.UNIT_MJD);
    /** INT_TIME column descriptor */
    private final static ColumnMeta COLUMN_INT_TIME = new ColumnMeta(OIFitsConstants.COLUMN_INT_TIME,
            "integration time", Types.TYPE_DBL, Units.UNIT_SECOND);
    /** UCOORD column descriptor */
    protected final static ColumnMeta COLUMN_UCOORD = new ColumnMeta(OIFitsConstants.COLUMN_UCOORD,
            "U coordinate of the data", Types.TYPE_DBL, Units.UNIT_METER);
    /** VCOORD column descriptor */
    protected final static ColumnMeta COLUMN_VCOORD = new ColumnMeta(OIFitsConstants.COLUMN_VCOORD,
            "V coordinate of the data", Types.TYPE_DBL, Units.UNIT_METER);
    /** members */
    /** cached reference on OI_ARRAY table associated to this OIData table */
    private OIArray oiArrayRef = null;
    /** cached reference on OI_WAVELENGTH table associated to this OIData table */
    private OIWavelength oiWavelengthRef = null;

    /**
     * Protected OIData class contructor
     * @param oifitsFile main OifitsFile
     */
    protected OIData(final OIFitsFile oifitsFile) {
        super(oifitsFile);

        // since every class constructor of OI_VIS, OI_VIS2, OI_T3 calls super
        // constructor, next keywords will be common to every subclass :

        // DATE-OBS  keyword definition
        addKeywordMeta(KEYWORD_DATE_OBS);

        // ARRNAME  Optional keyword definition
        addKeywordMeta(new KeywordMeta(OIFitsConstants.KEYWORD_ARRNAME, "name of corresponding array", Types.TYPE_CHAR, 0) {

            @Override
            public String[] getStringAcceptedValues() {
                return getOIFitsFile().getAcceptedArrNames();
            }
        });

        // INSNAME  keyword definition
        addKeywordMeta(new KeywordMeta(OIFitsConstants.KEYWORD_INSNAME, "name of corresponding detector", Types.TYPE_CHAR) {

            @Override
            public String[] getStringAcceptedValues() {
                return getOIFitsFile().getAcceptedInsNames();
            }
        });

        // TARGET_ID  column definition
        addColumnMeta(new ColumnMeta(OIFitsConstants.COLUMN_TARGET_ID,
                "target number as index into OI_TARGET table", Types.TYPE_INT) {

            @Override
            public short[] getIntAcceptedValues() {
                return getOIFitsFile().getAcceptedTargetIds();
            }
        });

        // TIME  column definition
        addColumnMeta(COLUMN_TIME);

        // MJD  column definition
        addColumnMeta(COLUMN_MJD);

        // INT_TIME  column definition
        addColumnMeta(COLUMN_INT_TIME);
    }

    /**
     * Return the number of measurements in this table.
     * @return the number of measurements.
     */
    public final int getNbMeasurements() {
        return getNbRows();
    }

    /* --- Keywords --- */
    /**
     * Get the DATE-OBS keyword value.
     * @return the value of DATE-OBS keyword
     */
    public final String getDateObs() {
        return getKeyword(OIFitsConstants.KEYWORD_DATE_OBS);
    }

    /**
     * Define the DATE-OBS keyword value
     * @param dateObs value of DATE-OBS keyword
     */
    public final void setDateObs(final String dateObs) {
        setKeyword(OIFitsConstants.KEYWORD_DATE_OBS, dateObs);
    }

    /**
     * Return the Optional ARRNAME keyword value.
     * @return the value of ARRNAME keyword if present, NULL otherwise.
     */
    public final String getArrName() {
        return getKeyword(OIFitsConstants.KEYWORD_ARRNAME);
    }

    /**
     * Define the Optional ARRNAME keyword value
     * @param arrName value of ARRNAME keyword
     */
    public final void setArrName(final String arrName) {
        setKeyword(OIFitsConstants.KEYWORD_ARRNAME, arrName);
        // reset cached reference :
        this.oiArrayRef = null;
    }

    /**
     * Get the INSNAME keyword value.
     * @return the value of INSNAME keyword
     */
    public final String getInsName() {
        return getKeyword(OIFitsConstants.KEYWORD_INSNAME);
    }

    /**
     * Define the INSNAME keyword value
     * @param insName value of INSNAME keyword
     */
    protected final void setInsName(final String insName) {
        setKeyword(OIFitsConstants.KEYWORD_INSNAME, insName);
        // reset cached reference :
        this.oiWavelengthRef = null;
    }

    /* --- Columns --- */
    /**
     * Return the TARGET_ID column.
     * @return the TARGET_ID column.
     */
    public short[] getTargetId() {
        return this.getColumnShort(OIFitsConstants.COLUMN_TARGET_ID);
    }

    /**
     * Return the TIME column.
     * @return the TIME column.
     */
    public double[] getTime() {
        return this.getColumnDouble(OIFitsConstants.COLUMN_TIME);
    }

    /**
     * Return the MJD column.
     * @return the MJD column.
     */
    public double[] getMjd() {
        return this.getColumnDouble(OIFitsConstants.COLUMN_MJD);
    }

    /**
     * Return the INT_TIME column.
     * @return the INT_TIME column.
     */
    public double[] getIntTime() {
        return this.getColumnDouble(OIFitsConstants.COLUMN_INT_TIME);
    }

    /**
     * Return the STA_INDEX column.
     * @return the STA_INDEX column.
     */
    public final short[][] getStaIndex() {
        return this.getColumnShorts(OIFitsConstants.COLUMN_STA_INDEX);
    }

    /**
     * Return the FLAG column.
     * @return the FLAG column.
     */
    public final boolean[][] getFlag() {        
        return this.getColumnBooleans(OIFitsConstants.COLUMN_FLAG);
    }

    /* --- Alternate data representation methods --- */
    /**
     * Return the spatial coord given the coord array = coord / effWave
     * @param coord coord array
     * @return the computed spatial coords r[x][y] (x,y for coordIndex,effWaveIndex) .
     */
    protected double[][] getSpatialCoord(final double[] coord) {
        final int nRows = getNbRows();
        final int nWaves = getNWave();

        final double[][] r = new double[nRows][nWaves];
        final float[] effWaves = getOiWavelength().getEffWave();

        for (int i = 0, j = 0; i < nRows; i++) {
            for (j = 0; j < nWaves; j++) {
                r[i][j] = coord[i] / effWaves[j];
            }
        }
        return r;
    }

    /* --- Utility methods for cross-referencing --- */
    /**
     * Return the associated optional OIArray table.
     * @return the associated OIArray or null if the keyword ARRNAME is undefined
     */
    public final OIArray getOiArray() {
        /** cached resolved reference */
        if (this.oiArrayRef != null) {
            return this.oiArrayRef;
        }

        final String arrName = getArrName();
        if (arrName != null) {
            final OIArray oiArray = getOIFitsFile().getOiArray(getArrName());

            if (oiArray != null) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Resolved OI_Array reference [" + oiArray.getExtNb() + "] to " + super.toString());
                }
                this.oiArrayRef = oiArray;
            }
            return oiArray;
        }

        return null;
    }

    /**
     * Mediator method to resolve cross references. Returns the accepted (ie
     * valid) station indexes for the associated OIArray table.
     *
     * @return the array containing the indexes.
     */
    public final short[] getAcceptedStaIndexes() {
        return getOIFitsFile().getAcceptedStaIndexes(getOiArray());
    }

    /**
     * Return the associated OIWavelength table.
     * @return the associated OIWavelength
     */
    public final OIWavelength getOiWavelength() {
        /** cached resolved reference */
        if (this.oiWavelengthRef != null) {
            return this.oiWavelengthRef;
        }

        final String insName = getInsName();
        if (insName != null) {
            final OIWavelength oiWavelength = getOIFitsFile().getOiWavelength(insName);

            if (oiWavelength != null) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Resolved OI_WAVELENGTH reference [" + oiWavelength.getExtNb() + " | NWAVE=" + oiWavelength.getNWave() + " ] to " + super.toString());
                }
                this.oiWavelengthRef = oiWavelength;
            } else {
                if (logger.isLoggable(Level.WARNING)) {
                    logger.warning("Missing OI_WAVELENGTH identified by '" + insName + "'");
                }
            }
            return oiWavelength;
        }

        return null;
    }

    /**
     * Return the number of distinct spectral channels of the associated OI_WAVELENGTH.
     * @return the number of distinct spectral channels of the associated OI_WAVELENGTH.
     */
    public final int getNWave() {
        final OIWavelength oiWavelength = getOiWavelength();
        if (oiWavelength != null) {
            return oiWavelength.getNWave();
        }
        return 0;
    }

    /* --- Other methods --- */
    /**
     * Returns a string representation of this table
     * @return a string representation of this table
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [ INSNAME=").append(getInsName());
        sb.append(" NB_MEASUREMENTS=").append( getNbMeasurements());
        int nbOk=0;                
        int nbKo=0;
        boolean[][] flags = getFlag();
        for (int i = 0; i < flags.length; i++) {            
            for (int j = 0; j < flags[i].length; j++) {
                boolean b = flags[i][j];
                if(b){
                    nbKo++;
                }else{
                    nbOk++;
                }
            }            
        }
        if(nbKo>0){
            sb.append(" ").append(nbKo).append(" data flagged out");
        }
           sb.append(" ").append(nbOk).append(" data ok");
        sb.append(" ]");
        return  sb.toString();
    }

    /**
     * Add arrname and oiarray test in addition to OITable.checkKeywords()
     * @param checker checker component
     */
    @Override
    public final void checkKeywords(final OIFitsChecker checker) {
        super.checkKeywords(checker);

        if (getArrName() != null && getOiArray() == null) {
            /* No keyword with keywordName name */
            checker.severe("Missing OI_ARRAY table that describes the '" + getArrName() + "' array");
        }
    }
}
/*___oOo___*/