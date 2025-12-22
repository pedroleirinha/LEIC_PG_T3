package org.example.views


import org.example.models.Racket
import org.example.models.WIDTH
import org.example.models.arena
import pt.isel.canvas.RED
import pt.isel.canvas.WHITE


const val RACKET_CENTRAL_ZONE = 10
const val RACKET_EDGE_ZONE = 10
const val RACKET_MIDDLE_ZONE = 15
const val RACKET_EDGE_ZONE_DELTA_CHANGE = 3
const val RACKET_MIDDLE_EDGE_ZONE_DELTA_CHANGE = 1
const val RACKET_MIDDLE_EDGE_COLOR = 0xF59827
const val RACKET_DEFAULT_Y_CORD = 540
const val RACKET_INITIAL_WIDTH = RACKET_CENTRAL_ZONE + RACKET_MIDDLE_ZONE * 2 + RACKET_EDGE_ZONE * 2
const val RACKET_STARTING_POS_X = (WIDTH / 2) - (RACKET_INITIAL_WIDTH / 2)
const val RACKET_X_CORD = RACKET_STARTING_POS_X
const val RACKET_HEIGHT = 12
const val RACKET_TOP_LAYER_HEIGHT = 5
const val RACKET_BASE_COLOR = WHITE
const val RACKET_EDGES_COLOR = RED

fun drawRacketBottomLayer(racket: Racket) {
    arena.drawRect(
        x = racket.x,
        y = racket.y + RACKET_TOP_LAYER_HEIGHT,
        width = racket.width,
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
        x = racket.x + racket.width - RACKET_EDGE_ZONE,
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
        x = racket.x + racket.width - RACKET_EDGE_ZONE - RACKET_MIDDLE_ZONE,
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