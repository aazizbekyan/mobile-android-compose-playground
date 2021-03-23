package com.azizbekian.playground.home

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.azizbekian.playground.home.MainContract.Effect.None
import com.azizbekian.playground.home.MainContract.Event.*
import com.azizbekian.playground.ui.theme.ComposePlaygroundTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  private val viewModel: MainViewModel by viewModels()

  @ExperimentalCoroutinesApi
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      ComposePlaygroundTheme {
        Surface(color = MaterialTheme.colors.background) {
          MainScreen(viewModel)
        }
      }
    }
  }
}

@ExperimentalCoroutinesApi
@Composable
private fun MainScreen(
  viewModel: MainViewModel
) {
  val state by viewModel.uiState.collectAsState()
  val effect by viewModel.effect.collectAsState(None /* by default we have no effect */)
  val scaffoldState = rememberScaffoldState()

  Scaffold(
    scaffoldState = scaffoldState,
    content = {
      MainScreen(
        uiState = state,
        effect = effect,
        scaffoldState = scaffoldState,
        onSnackbarDismissed = { viewModel.setEvent(OnSnackbarDismissed) },
        onLoadUsers = { viewModel.setEvent(OnLoadUsersClicked) },
        onErrorLoadingUsers = { viewModel.setEvent(OnShowErrorMessageClicked) },
        onUserClick = { user -> viewModel.setEvent(OnUserClicked(user)) },
        onDialogDismiss = { viewModel.setEvent(OnUserDialogDismissed) },
      )
    }
  )

}