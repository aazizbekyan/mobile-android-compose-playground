package com.azizbekian.playground.home

import com.azizbekian.playground.base.UiEffect
import com.azizbekian.playground.base.UiEvent
import com.azizbekian.playground.base.UiState
import com.azizbekian.playground.home.data.User

class MainContract {

  sealed class Event : UiEvent {
    object OnLoadUsersClicked : Event()
    object OnShowErrorMessageClicked : Event()
    object OnSnackbarDismissed : Event()
    data class OnUserClicked(val user: User) : Event()
    object OnUserDialogDismissed : Event()
  }

  data class State(
    val usersState: UsersState,
    // here might be plenty of other "child" states
    // this separation looks promising, because this allows to encapsulate
    // various states (consider the users & dogs scenario from the slack thread)
  ) : UiState

  sealed class UsersState {
    object Idle : UsersState()
    object Loading : UsersState()
    data class Success(val users: List<User>) : UsersState()
  }

  sealed class Effect : UiEffect {
    object None : Effect()
    object ShowErrorMessage : Effect()
    data class ShowDialog(val user: User) : Effect()
  }

}