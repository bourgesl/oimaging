/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.oimaging.model;

/**
 *
 */
public abstract class ColumnValueProvider<K> {

    public abstract Object getValue(K result, final String columnKey);

    /**
     * To be overriden to set value
     * @param result
     * @param columnKey
     * @param value
     */
    public void setValue(K result, final String columnKey, final Object value) {
    }
}
