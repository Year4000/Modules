package net.year4000.dashboard;

public interface Callback<T> {
    /** The method that gets called once something is done */
    public void callback(T callback, Throwable error);
}

