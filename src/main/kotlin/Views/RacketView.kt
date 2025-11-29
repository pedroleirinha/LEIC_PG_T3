package org.example.Views

import org.example.Models.RACKET_CENTRAL_ZONE
import org.example.Models.RACKET_EDGE_ZONE
import org.example.Models.RACKET_HEIGHT
import org.example.Models.RACKET_MIDDLE_EDGE_COLOR
import org.example.Models.RACKET_MIDDLE_ZONE
import org.example.Models.RACKET_TOP_LAYER_HEIGHT
import org.example.Models.RACKET_WIDTH
import org.example.Models.Racket
import org.example.Models.arena
import pt.isel.canvas.RED
import pt.isel.canvas.WHITE

const val RACKET_BASE_COLOR = WHITE
const val RACKET_EDGES_COLOR = RED

fun drawRacketBottomLayer(racket: Racket) {
    arena.drawRect(
        x = racket.x,
        y = racket.y + RACKET_TOP_LAYER_HEIGHT,
        width = RACKET_WIDTH,
        height = RACKET_HEIGHT - RACKET_TOP_LAYER_HEIGHT,
        color = RACKET_BASE_COLOR
    )
}

fun drawRacketEdge(racket: Racket) {
    arena.drawRect(
        x = racket.x,
        y = racket.y,
        width = RACKET_EDGE_ZONE,
        height = RACKET_TOP_LAYER_HEIGHT,
        color = RACKET_EDGES_COLOR
    )

    arena.drawRect(
        x = racket.x + RACKET_WIDTH - RACKET_EDGE_ZONE,
        y = racket.y,
        width = RACKET_EDGE_ZONE,
        height = RACKET_TOP_LAYER_HEIGHT,
        color = RACKET_EDGES_COLOR
    )
}

fun drawRacketMiddleEdge(racket: Racket) {
    arena.drawRect(
        x = racket.x + RACKET_EDGE_ZONE,
        y = racket.y,
        width = RACKET_MIDDLE_ZONE,
        height = RACKET_TOP_LAYER_HEIGHT,
        color = RACKET_MIDDLE_EDGE_COLOR
    )


    arena.drawRect(
        x = racket.x + RACKET_WIDTH - RACKET_EDGE_ZONE - RACKET_MIDDLE_ZONE,
        y = racket.y,
        width = RACKET_MIDDLE_ZONE,
        height = RACKET_TOP_LAYER_HEIGHT,
        color = RACKET_MIDDLE_EDGE_COLOR
    )
}

fun drawRacketCenter(racket: Racket) {
    arena.drawRect(
        x = racket.x + RACKET_EDGE_ZONE + RACKET_MIDDLE_ZONE,
        y = racket.y,
        width = RACKET_CENTRAL_ZONE,
        height = RACKET_TOP_LAYER_HEIGHT,
        color = RACKET_BASE_COLOR
    )
}

fun drawRacketTopLayer(racket: Racket) {
    drawRacketEdge(racket)
    drawRacketMiddleEdge(racket)
    drawRacketCenter(racket)
}

fun drawRacket(racket: Racket) {
    drawRacketBottomLayer(racket)
    drawRacketTopLayer(racket)
}