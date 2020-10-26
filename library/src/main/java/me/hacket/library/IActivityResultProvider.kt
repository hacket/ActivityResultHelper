package me.hacket.library

interface IActivityResultProvider {
    val resultKey: Int
        get() = this.hashCode()
}

interface IStartActivityForResultProvider : IActivityResultProvider
interface IRequestPermissionProvider : IActivityResultProvider
interface ITakePictureProvider : IActivityResultProvider
interface ITakeVideoProvider : IActivityResultProvider