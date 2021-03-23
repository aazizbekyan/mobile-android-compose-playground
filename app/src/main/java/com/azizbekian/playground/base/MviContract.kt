package com.azizbekian.playground.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Describes actions that were initiated from the UI layer.
 *
 * Typical examples: "user clicked on load button", "snackbar got dismissed".
 *
 * This event is picked up by the processor and a new state (or/and effect)
 * is being created by the processor and is dispatched to the View.
 */
interface UiEvent

/**
 * Corresponds to the actual state of the application.
 * At any point of time the UI of the app might be constructed
 * with this state, regardless of the state that UI was at
 * (i.e. it was absent, it was halted).
 *
 * So, theoretically, one could create a state on the backend and
 * dispatch the state remotely to the app and the view should be
 * able to draw itself regardless of it's previous state (because the view
 * is dumb and simply picks up the pencil and draws itself based on the state).
 *
 * Typical examples: "Current revenue is 120$, Best employees are John and Peter".
 */
interface UiState

/**
 * Corresponds to actions that should be performed just once
 * and should not be reflected on the UI as a "state of the UI".
 *
 * Typical examples: "navigate to details screen", "display snackbar".
 */
interface UiEffect

abstract class BaseViewModel<
  Event : UiEvent,
  State : UiState,
  Effect : UiEffect
  > : ViewModel() {

  private val initialState: State by lazy { createInitialState() }
  abstract fun createInitialState(): State

  private val currentState: State
    get() = uiState.value

  private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
  val uiState = _uiState.asStateFlow()

  private val _event: MutableSharedFlow<Event> = MutableSharedFlow()
  private val event = _event.asSharedFlow()

  // this part is a bit questionable: what if multiple effects are dispatched
  // and there is no consumer of this flow?
  // dispatching EFFECT_1 will suspend the execution until EFFECT_1 gets consumed
  // not sure if this is an issue, to-be-discussed
  private val _effect: Channel<Effect> = Channel()
  val effect = _effect.receiveAsFlow()

  init {
    subscribeEvents()
  }

  private fun subscribeEvents() {
    viewModelScope.launch {
      event.collect {
        handleEvent(it)
      }
    }
  }

  abstract fun handleEvent(event: Event)

  fun setEvent(event: Event) {
    val newEvent = event
    viewModelScope.launch { _event.emit(newEvent) }
  }


  protected fun setState(reduce: State.() -> State) {
    val newState = currentState.reduce()
    _uiState.value = newState
  }

  protected fun setEffect(effect: Effect) {
    viewModelScope.launch { _effect.send(effect) }
  }
}