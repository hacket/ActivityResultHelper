package me.hacket.library;

public interface IActivityResultProvider {
    default int getResultKey() {
        return this.hashCode();
    }
}
