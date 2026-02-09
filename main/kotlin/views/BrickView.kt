package org.example.views

import org.example.models.BRICK_HEIGHT
import org.example.models.BRICK_STROKE_OFFSET_ADJUSTMENT
import org.example.models.BRICK_WIDTH
import org.example.models.Brick
import org.example.models.arena
import pt.isel.canvas.BLACK

const val BRICK_GIFT_TEXT_SIZE = 10
const val BRICK_GIFT_Y_ADJUSTMENT = 4

/*Desenha os tijolos na arena.
* Se for passado o segundo parâmetro, é mostrado o "GIFT" de cada tijolo.
*/
fun drawBricks(bricks: List<Brick>, showGift: Boolean = false) {
    bricks.forEach {
        arena.drawRect(
            x = it.x,
            y = it.y,
            width = BRICK_WIDTH,
            height = BRICK_HEIGHT,
            color = it.type.color
        )
        arena.drawRect(
            x = it.x + BRICK_STROKE_OFFSET_ADJUSTMENT,
            y = it.y + BRICK_STROKE_OFFSET_ADJUSTMENT,
            width = BRICK_WIDTH - BRICK_STROKE_OFFSET_ADJUSTMENT * 2,
            height = BRICK_HEIGHT - BRICK_STROKE_OFFSET_ADJUSTMENT * 2,
            color = BLACK,
            thickness = 2
        )


        if (showGift && it.gift != null) {
            arena.drawText(
                x = it.x + BRICK_WIDTH / 2,
                y = it.y + BRICK_HEIGHT - BRICK_GIFT_Y_ADJUSTMENT,
                txt = it.gift.type.letter,
                color = BLACK,
                fontSize = BRICK_GIFT_TEXT_SIZE
            )
        }

    }
}