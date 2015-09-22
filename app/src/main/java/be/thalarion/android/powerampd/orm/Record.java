package be.thalarion.android.powerampd.orm;

import java.util.List;

import be.thalarion.android.powerampd.password.Entry;

public class Record<T> {

    /**
     * find
     * @param format
     * @param arg
     * @return
     */
    public static <T> List<T> find(Class<T> classType, String format, String arg) {

    }

    /**
     * findById
     * @param entryId
     * @return
     */
    public static <T> T findById(Class<T> classType, long entryId) {

    }

    public static <T> List<T> findAll() {

    }
}
