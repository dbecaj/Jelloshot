package me.dbecaj.jelloshot.core

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3

val Int.pixelsToMeters: Float
    get() = this / 32F

val Float.pixelsToMeters: Float
    get() = this / 32F

val Int.metersToPixels: Float
    get() = this * 32F

val Float.metersToPixels: Float
    get() = this * 32F

val Float.toDegrees : Float
    get() = MathUtils.radiansToDegrees * this

val Vector3.toVector2 : Vector2
    get() = Vector2(this.x, this.y)