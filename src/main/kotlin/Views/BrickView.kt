package org.example.Views

import org.example.Models.BRICK_HEIGHT
import org.example.Models.BRICK_STROKE_OFFSET_ADJUSTMENT
import org.example.Models.BRICK_WIDTH
import org.example.Models.Brick
import org.example.Models.arena
import pt.isel.canvas.BLACK


fun drawBricks(bricks: List<Brick>) {
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
            width = BRICK_WIDTH - BRICK_STROKE_OFFSET_ADJUSTMENT,
            height = BRICK_HEIGHT - BRICK_STROKE_OFFSET_ADJUSTMENT,
            color = BLACK,
            thickness = 2
        )
    }
}