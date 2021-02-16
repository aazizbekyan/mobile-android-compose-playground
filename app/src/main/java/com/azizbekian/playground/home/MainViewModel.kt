package com.azizbekian.playground.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.azizbekian.playground.base.BaseViewModel
import com.azizbekian.playground.home.MainContract.Effect
import com.azizbekian.playground.home.MainContract.Effect.ShowErrorMessage
import com.azizbekian.playground.home.MainContract.Event.*
import com.azizbekian.playground.home.MainContract.UsersState.Idle
import com.azizbekian.playground.home.MainContract.UsersState.Success
import com.azizbekian.playground.home.data.Users
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() :
  BaseViewModel<MainContract.Event, MainContract.State, Effect>() {

  override fun createInitialState(): MainContract.State {
    return MainContract.State(Idle)
  }

  override fun handleEvent(event: MainContract.Event) {
    when (event) {
      is OnLoadUsersClicked -> loadUsers()
      is OnShowErrorMessageClicked -> {
        setEffect { Effect.ShowErrorMessage }
      }
      is OnSnackbarDismissed -> {
        setEffect { Effect.None }
      }
      is OnUserClicked -> {
        setEffect { Effect.ShowDialog(event.user) }
      }
      is OnUserDialogDismissed -> {
        setEffect { Effect.None }
      }
    }
  }

  private fun loadUsers() {
    viewModelScope.launch {
      // dispatches loading state which initiates recomposition and displays
      // loading indicator
      setState { copy(usersState = MainContract.UsersState.Loading) }

      // will imitate a long running action
      // you can perform a configuration change and verify that state is
      // correctly handle (i.e. in-flight request state is correctly displayed on UI)
      delay(5_000)

      val random = (0..6).random()
      Log.i("titan", "Random number is $random")
      if (random % 5 == 0) {
        setState { copy(usersState = Idle) }
        setEffect { ShowErrorMessage }
      } else {
        setState { copy(usersState = Success(users = Users)) }
      }
    }
  }
}