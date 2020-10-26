package me.hacket.library

interface IActivityResultProvider {
    val resultKey: Int
        get() = this.hashCode()
}

interface IActivityResultProviderForStart : IActivityResultProvider
interface IActivityResultProviderForPermissions : IActivityResultProvider
interface IActivityResultProviderForTakePicture : IActivityResultProvider
interface IActivityResultProviderForTakeVideo : IActivityResultProvider