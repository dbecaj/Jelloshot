package me.dbecaj.jelloshot.core

import com.badlogic.gdx.math.MathUtils

val Int.pixelsToMeters: Float
    get() = this / 32F

val Float.toDegrees : Float
    get() = MathUtils.radiansToDegrees * this