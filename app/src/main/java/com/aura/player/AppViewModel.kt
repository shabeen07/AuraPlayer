package com.aura.player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.aura.player.data.DataRepository
import com.aura.player.data.DefaultDataRepository

class AppViewModel(application: Application) : AndroidViewModel(application) {
    val repository: DataRepository = DefaultDataRepository(application)

    override fun onCleared() {
        super.onCleared()
        repository.playerController.release()
    }
}
