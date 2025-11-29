package org.example.Views

import org.example.Models.BALL_COLOR
import org.example.Models.BALL_RADIUS
import org.example.Models.Ball
import org.example.Models.arena
import pt.isel.canvas.CYAN
import kotlin.collections.forEach
import kotlin.math.sign


/*
* Desenha as bolas em jogo no canvas
* */
fun drawBalls(ballsList: List<Ball>) {
    ballsList.forEach { arena.drawCircle(xCenter = it.x, yCenter = it.y, radius = BALL_RADIUS, color = BALL_COLOR) }
}
