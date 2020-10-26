package me.hacket.library

interface ActivityCallback<T> {
    fun onSuccess(result: T)
}