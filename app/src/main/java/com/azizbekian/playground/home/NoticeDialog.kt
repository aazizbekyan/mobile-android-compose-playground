package com.azizbekian.playground.home

import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import com.azizbekian.playground.home.data.User

@Composable
fun NoticeDialog(
  user: User,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = { onDismiss() },
    text = {
      Text(
        text = "Hello, ${user.name}",
        style = MaterialTheme.typography.body2
      )
    },
    confirmButton = {
      TextButton(onClick = onDismiss) {
        Text(text = "CLOSE")
      }
    }
  )
}