package org.example.views

import org.example.models.BALL_COLOR
import org.example.models.BALL_RADIUS
import org.example.models.Ball
import org.example.models.arena
import kotlin.math.roundToInt


/*
* Desenha as bolas em jogo no canvas
* */
fun drawBalls(ballsList: List<Ball>) {
    ballsList.forEach { arena.drawCircle(xCenter = it.x.roundToInt(), yCenter = it.y.roundToInt(), radius = BALL_RADIUS, color = BALL_COLOR) }
}
