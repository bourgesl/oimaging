/** *****************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ***************************************************************************** */
package fr.jmmc.oimaging.model;

import fr.jmmc.jmcs.util.ObjectUtils;
import fr.jmmc.oimaging.services.ServiceResult;
import fr.jmmc.oitools.fits.FitsHeaderCard;
import fr.jmmc.oitools.fits.FitsTable;
import fr.jmmc.oitools.image.ImageOiConstants;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author martin
 */
public class ResultSetTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    /* fixed columns */
    public static final String COLUMN_FILE = "FILE";
    public static final String COLUMN_TIMESTAMP = "TIMESTAMP";
    public static final String COLUMN_ALGORITHM = "ALGORITHM";
    public static final String COLUMN_SUCCESS = "SUCCESS";
    public static final String COLUMN_RATING = "RATING";
    public static final String COLUMN_COMMENTS = "COMMENTS";

    private final static ColumnValueProvider<ServiceResult> RESULT_VALUE_PROVIDER = new ResultColumnValueProvider();
    private final static ColumnValueProvider<ServiceResult> INPUT_PARAM_VALUE_PROVIDER = new InputParamColumnValueProvider();
    private final static ColumnValueProvider<ServiceResult> OUTPUT_PARAM_VALUE_PROVIDER = new OutputParamColumnValueProvider();

    /* members */
    private final List<ServiceResult> results;
    /** List of column meta data */
    private final List<ColumnDescriptor<ServiceResult>> columnDescList;

    public ResultSetTableModel() {
        super();
        results = new ArrayList<>();

        // define fixed column mapping:
        columnDescList = new ArrayList<>();
        columnDescList.add(new ColumnDescriptor<>(COLUMN_FILE, RESULT_VALUE_PROVIDER));
        columnDescList.add(new ColumnDescriptor<>(ImageOiConstants.KEYWORD_TARGET, INPUT_PARAM_VALUE_PROVIDER));
        columnDescList.add(new ColumnDescriptor<>(COLUMN_TIMESTAMP, RESULT_VALUE_PROVIDER, Date.class));

        columnDescList.add(new ColumnDescriptor<>(ImageOiConstants.KEYWORD_WAVE_MIN, INPUT_PARAM_VALUE_PROVIDER, Double.class));
        columnDescList.add(new ColumnDescriptor<>(ImageOiConstants.KEYWORD_WAVE_MAX, INPUT_PARAM_VALUE_PROVIDER, Double.class));

        columnDescList.add(new ColumnDescriptor<>(COLUMN_ALGORITHM, RESULT_VALUE_PROVIDER));

        columnDescList.add(new ColumnDescriptor<>(ImageOiConstants.KEYWORD_RGL_NAME, INPUT_PARAM_VALUE_PROVIDER));
        columnDescList.add(new ColumnDescriptor<>(ImageOiConstants.KEYWORD_RGL_WGT, INPUT_PARAM_VALUE_PROVIDER, Double.class));

        columnDescList.add(new ColumnDescriptor<>(COLUMN_SUCCESS, RESULT_VALUE_PROVIDER, Boolean.class));
        columnDescList.add(new ColumnDescriptor<>(COLUMN_RATING, RESULT_VALUE_PROVIDER, Integer.class));
        columnDescList.add(new ColumnDescriptor<>(COLUMN_COMMENTS, RESULT_VALUE_PROVIDER));

        // sample for output param:
        columnDescList.add(new ColumnDescriptor<>("ENTROPY", OUTPUT_PARAM_VALUE_PROVIDER, Double.class));
    }

    public void setServiceResults(final List<ServiceResult> results) {
        // make a snapshot of the given list:
        this.results.clear();
        this.results.addAll(results);

        // TODO: define here dynamic column mapping:
        fireTableDataChanged();
    }

    public ServiceResult getServiceResult(final int rowIndex) {
        return this.results.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return this.results.size();
    }

    @Override
    public int getColumnCount() {
        return this.columnDescList.size();
    }

    public ColumnDescriptor<ServiceResult> getColumnDesc(int columnIndex) {
        return this.columnDescList.get(columnIndex);
    }

    @Override
    public String getColumnName(int columnIndex) {
        return getColumnDesc(columnIndex).getColumnKey();
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        return getColumnDesc(columnIndex).getColumnType();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // or ask to column desc ?
        switch (getColumnName(columnIndex)) {
            case COLUMN_RATING:
            case COLUMN_COMMENTS:
                return true;

            default:
                return false;
        }
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        final ServiceResult result = getServiceResult(rowIndex);
        getColumnDesc(columnIndex).setValue(result, value);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        final ServiceResult result = getServiceResult(rowIndex);
        Object o = getColumnDesc(columnIndex).getValue(result);
        System.out.println("row: "+rowIndex + " " + getColumnName(columnIndex) + " = "+o + " "+ObjectUtils.getObjectInfo(o));
        // TODO: check classtype => convert if needed
        return o;
    }

    public final class ColumnDescriptor<K> {

        private final String columnKey;
        private final ColumnValueProvider<K> valueProvider;
        /* optional type */
        private final Class columnType;
        // include other metadata ?

        public ColumnDescriptor(final String columnKey, ColumnValueProvider<K> valueProvider) {
            this(columnKey, valueProvider, Object.class);
        }

        public ColumnDescriptor(String columnKey, ColumnValueProvider<K> valueProvider, Class columnType) {
            this.columnKey = columnKey;
            this.valueProvider = valueProvider;
            this.columnType = columnType;
        }

        public String getColumnKey() {
            return columnKey;
        }

        private ColumnValueProvider<K> getColumnValueProvider() {
            return valueProvider;
        }

        public Class getColumnType() {
            return columnType;
        }

        public Object getValue(K result) {
            return getColumnValueProvider().getValue(result, getColumnKey());
        }

        public void setValue(K result, final Object value) {
            getColumnValueProvider().setValue(result, getColumnKey(), value);
        }

        @Override
        public String toString() {
            return "ColumnDescriptor{" + "columnKey=" + columnKey + ", valueProvider=" + valueProvider + ", columnType=" + columnType + '}';
        }
    }

    /**
     * Provides the generic get/set value on the given instance
     * @param <K> class of the handled instances
     */
    private final static class ResultColumnValueProvider extends ColumnValueProvider<ServiceResult> {

        @Override
        public Object getValue(final ServiceResult result, final String columnKey) {
            switch (columnKey) {
                case COLUMN_FILE:
                    return result.getInputFile().getName();

                case COLUMN_TIMESTAMP:
                    return result.getEndTime();

                case COLUMN_ALGORITHM:
                    return result.getService().getProgram();

                case COLUMN_SUCCESS:
                    return result.isValid();

                case COLUMN_RATING:
                    return result.getRating();

                case COLUMN_COMMENTS:
                    return result.getComments();

                default:
                    return null;
            }
        }

        @Override
        public void setValue(ServiceResult result, final String columnKey, final Object value) {
            switch (columnKey) {
                case COLUMN_RATING:
                    result.setRating((int) value);
                    break;
                case COLUMN_COMMENTS:
                    result.setComments((String) value);
                    break;
            }
        }
    }

    private final static class InputParamColumnValueProvider extends ColumnValueProvider<ServiceResult> {

        @Override
        public Object getValue(final ServiceResult result, final String columnKey) {
            if (result.getOifitsFile() != null) {
                final FitsTable table = result.getOifitsFile().getImageOiData().getInputParam();
                return getKeywordOrHeaderCard(table, columnKey);
            }
            return null;
        }
    }

    private final static class OutputParamColumnValueProvider extends ColumnValueProvider<ServiceResult> {

        @Override
        public Object getValue(final ServiceResult result, final String columnKey) {
            if (result.getOifitsFile() != null) {
                final FitsTable table = result.getOifitsFile().getImageOiData().getOutputParam();
                return getKeywordOrHeaderCard(table, columnKey);
            }
            return null;
        }
    }

    static Object getKeywordOrHeaderCard(final FitsTable table, final String key) {
        if (table != null) {
            // Keyword (first) ?
            if (table.hasKeywordMeta(key)) {
                return table.getKeywordValue(key); // Object
            } else {
                // find header card (return first matching key):
                for (FitsHeaderCard card : table.getHeaderCards()) {
                    if (key.equals(card.getKey())) {
                        if (card.isString() || card.getValue() == null) {
                            return card.getValue(); // String
                        }
                        // TODO: test and refine card parsing (type logical or int)
                        // like check (true|false) and '.' to distinguish double / int
                        // or such card should be declared as std (known) keyword instead ?
                        try {
                            return Double.valueOf(card.getValue());
                        } catch (NumberFormatException nfe) {
                            // ignore
                        }
                        return card.getValue(); // String
                    }
                }
            }
        }
        return null;
    }
}
