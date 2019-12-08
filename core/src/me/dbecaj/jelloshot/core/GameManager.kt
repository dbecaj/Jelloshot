package me.dbecaj.jelloshot.core

import com.google.inject.Inject
import com.google.inject.Singleton

@Singleton
class GameManager @Inject() constructor() {
    public var score = 0
}