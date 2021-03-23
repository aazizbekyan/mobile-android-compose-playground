package com.azizbekian.playground.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.azizbekian.playground.R
import com.azizbekian.playground.home.MainContract.Effect.ShowDialog
import com.azizbekian.playground.home.MainContract.Effect.ShowErrorMessage
import com.azizbekian.playground.home.MainContract.UsersState.*
import com.azizbekian.playground.home.data.EmptyUser
import com.azizbekian.playground.home.data.User

/**
 * This is a stateful composable which will operate on top of
 * stateless ones.
 */
@Composable
fun MainScreen(
  uiState: MainContract.State,
  /**
   * We treat effects as events should happen just one (i.e. display snackbar,
   * navigate to detail screen). In contrast to `uiState`s, effects should be caught,
   * operated on and disposed from: i.e. when "show dialog" effect comes we should
   * take care of displaying the dialog and remember that state internally, because
   * we won't be getting "show dialog" effect on re-composition.
   */
  effect: MainContract.Effect,
  scaffoldState: ScaffoldState,
  onSnackbarDismissed: () -> Unit,
  onLoadUsers: () -> Unit,
  onErrorLoadingUsers: () -> Unit,
  onUserClick: (User) -> Unit,
  onDialogDismiss: () -> Unit,
) {
  // we want to the user to survive configuration changes
  // in order to re-display the dialog if it was displayed previously
  var user = rememberSaveable { EmptyUser }

  // as soon as we receive a one-time event indicating that
  // dialog should get displayed we update the internal state
  // within this composable
  if (effect is ShowDialog) user = effect.user

  // if the internally saved state is not the default one (which is EmptyUser)
  // then we are free to assume that we should display the dialog
  // because otherwise it would be the default state
  if (user != EmptyUser) {
    // the `onDismiss` callback will be called only when the dialog gets actually
    // dismissed (by user's action). In other words, we won't be getting false positives
    // when dialog gets dismissed as a result of configuration change
    // (think of handling this in fragments, good luck with that)
    NoticeDialog(user) {
      // we reset the state of in order to not display the dialog anymore
      // after configuration change
      user = EmptyUser
      onDialogDismiss()
    }
  }

  if (effect is ShowErrorMessage) {
    CustomSnackbar(
      onSnackbarDismissed = onSnackbarDismissed,
      scaffoldState = scaffoldState
    )
  }

  Column {
    UsersContainer(
      state = uiState,
      modifier = Modifier.weight(1f),
      onUserClick = onUserClick
    )
    Surface(elevation = 8.dp) {
      Divider(modifier = Modifier.fillMaxWidth())
      ButtonsContainer(onLoadUsers, onErrorLoadingUsers)
    }
  }
}

@Composable
private fun CustomSnackbar(
  onSnackbarDismissed: () -> Unit,
  scaffoldState: ScaffoldState
) {
  LaunchedEffect(scaffoldState) {
    val result = scaffoldState.snackbarHostState.showSnackbar(
      message = "Something went wrong"
    )
    if (result == SnackbarResult.Dismissed) onSnackbarDismissed()
  }
}

@Composable
private fun UsersContainer(
  state: MainContract.State,
  onUserClick: (User) -> Unit,
  modifier: Modifier = Modifier
) {
  UsersScaffold(modifier) {
    when (state.usersState) {
      Idle -> {
        Text(text = "Nothing to display yet", modifier = it)
      }
      Loading -> {
        CircularProgressIndicator(
          modifier = it
            .size(48.dp)
            .padding(4.dp)
        )
      }
      is Success -> {
        LazyColumn {
          val users = state.usersState.users
          users.forEachIndexed { index, user ->
            item {
              Column(Modifier.clickable { onUserClick(users[index]) }) {
                UsersListItem(user)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun UsersListItem(user: User) {
  Row(
    Modifier.padding(12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      modifier = Modifier.weight(1f),
      text = user.name,
      style = MaterialTheme.typography.subtitle1
    )
    Text(text = user.id.toString())
  }

  UsersDivider()
}

@Composable
private fun UsersDivider() {
  Divider(
    modifier = Modifier.padding(top = 4.dp),
    color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
  )
}

@Composable
private fun UsersScaffold(
  modifier: Modifier = Modifier,
  mainContent: @Composable (modifier: Modifier) -> Unit
) {
  Box(modifier = modifier.fillMaxSize()) {
    // please come up with a better idea related to this thing
    // the requirement is to impose "center" constraint to all children
    // within `mainContent` slot
    mainContent(modifier = Modifier.align(Alignment.Center))
  }
}

@Composable
private fun ButtonsContainer(
  onLoadUsers: () -> Unit,
  onErrorLoadingUsers: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    horizontalArrangement = Arrangement.SpaceAround,
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .fillMaxWidth()
      .padding(all = 16.dp)
  ) {
    val buttonModifier = Modifier
      .padding(horizontal = 16.dp)
      .wrapContentHeight()

    Button(
      modifier = buttonModifier,
      onClick = onLoadUsers
    ) {
      Text(
        text = stringResource(id = R.string.retrieve_users),
        modifier = Modifier.padding(horizontal = 16.dp)
      )
    }

    Button(
      modifier = buttonModifier,
      onClick = onErrorLoadingUsers
    ) {
      Text(
        text = stringResource(id = R.string.display_error_message),
        modifier = Modifier.padding(horizontal = 16.dp),
      )
    }
  }
}