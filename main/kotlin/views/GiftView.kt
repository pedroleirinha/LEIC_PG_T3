package org.example.views

import org.example.models.Gift
import org.example.models.arena
import pt.isel.canvas.BLACK
import pt.isel.canvas.WHITE

const val GIFT_CIRCLE_RADIUS: Int = 10
const val GIFT_LETTER_ADJUSTMENT: Int = 3
const val GIFT_LETTER_SIZE: Int = 12

/*
* Desenha o GIFT na arena, depois do tijolo ser partido
* */
fun Gift.drawGift() {
    arena.drawCircle(
        xCenter = this.x,
        yCenter = this.y + GIFT_CIRCLE_RADIUS,
        GIFT_CIRCLE_RADIUS,
        color = this.type.color
    )
    arena.drawCircle(xCenter = this.x, yCenter = this.y + GIFT_CIRCLE_RADIUS, GIFT_CIRCLE_RADIUS, color = BLACK, 1)
    arena.drawText(
        x = this.x - GIFT_LETTER_ADJUSTMENT,
        y = this.y + GIFT_CIRCLE_RADIUS + GIFT_LETTER_ADJUSTMENT,
        txt = this.type.letter,
        color = WHITE,
        fontSize = GIFT_LETTER_SIZE
    )
}