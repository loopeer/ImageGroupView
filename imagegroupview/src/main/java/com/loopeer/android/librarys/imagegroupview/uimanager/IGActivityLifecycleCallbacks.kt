package com.loopeer.android.librarys.imagegroupview.uimanager

interface IGActivityLifecycleCallbacks {
    fun onCreated()
    fun onPostCreated()
    fun onStarted()
    fun onResumed()
    fun onPaused()
    fun onStopped()
    fun onDestroyed()
}
