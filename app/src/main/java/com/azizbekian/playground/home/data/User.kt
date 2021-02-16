package com.azizbekian.playground.home.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
  val name: String,
  val id: Long
) : Parcelable

val EmptyUser = User("", -1)

val Users = listOf(
  User("John", 1),
  User("Peter", 2),
  User("Pavel", 3),
  User("George", 4),
  User("Vasquez", 5),
  User("Rodrigo", 6),
  User("Artur", 7),
  User("Viktor", 8),
  User("Ivan", 9),
  User("Oleg", 10),
  User("Dominique", 11),
  User("Fabio", 12),
  User("Rafael", 13),
  User("Alexander", 14),
  User("Roger", 15),
  User("Novak", 16),
  User("Cameron", 17),
)