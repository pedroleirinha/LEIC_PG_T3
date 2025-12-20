package org.example.models

import org.example.ENVIRONMENT
import org.example.runningENVIRONMENT
import pt.isel.canvas.BLACK
import pt.isel.canvas.Canvas

const val WIDTH = BRICK_WIDTH * 13
const val HEIGHT = 600
const val BACKGROUND_COLOR = BLACK
const val LIVES_Y_POSITION = HEIGHT - 20
const val LIVES_X_SPACE = (BALL_RADIUS * 3)
const val TIME_TICK_MLS = 10
const val KEY_S_CODE = 83

enum class Collision {
    HORIZONTAL,
    VERTICAL,
    BOTH,
    NONE
}

enum class DIRECTIONS(val value: Int) {
    DOWN(value = 1),
    UP(value = -1),
}

data class Area(val width: Int = WIDTH, val height: Int = HEIGHT)
data class Game(
    val area: Area = Area(),
    val balls: List<Ball> = emptyList(),
    val racket: Racket = Racket(),
    val bricks: List<Brick> = emptyList(),
    val points: Int = 0,
    val lives: Int = 2
)

val arena = Canvas(WIDTH, HEIGHT, BACKGROUND_COLOR)

fun Game.loseLife() = copy(lives=lives-1)

fun Game.newBall() = copy(balls = listOf(generateNewBall(this.racket)))

fun unstuckBalls(game: Game) = game.copy(balls = game.balls.map {
    if (it.stuck) {
        it.copy(stuck = false)
    } else it
})

fun adjustHorizontalCordForStuckBall(game: Game, mouseX: Int): Game {
    val updatedRacket = game.racket.moveTo(to = mouseX)
    val newBalls = game.balls.map {
        if (it.stuck) {
            val relativeX = it.x - game.racket.x
            val newX = updatedRacket.x + relativeX
            it.copy(x = newX)
        } else it
    }

    return game.copy(balls = newBalls, racket = updatedRacket)
}
fun clearBrokenBricks(bricks: List<Brick>, balls: List<Ball>): List<Brick> {
    val newBricks = bricks.map { brick ->
        if (balls.any {
                checkBrickCollision(
                    it,
                    brick
                ) != Collision.NONE
            })
            brick.addHit()
        else
            brick

    }
    return newBricks
}
fun sumPoints(bricks: List<Brick>) =
    bricks
        .filter { it.hitCounter > 0 }
        .fold(0) { sum, brick -> sum + brick.type.points * brick.hitCounter }

/*
* A cada step do jogo, remove as bolas fora de jogo, verifica as colisões e atualiza os movimentos das bolas para serem desenhadas novamente.
* */
fun handleGameBallsBehaviour(balls: List<Ball>, racket: Racket, bricks: List<Brick>): List<Ball> {

    val filteredBalls = filterBallsOutOfBounds(balls = balls)
    val newBallsUpdated =
        (if (runningENVIRONMENT == ENVIRONMENT.DEBUG) balls else filteredBalls).map {
            checkAndUpdateBallMovementAfterCollision(
                ball = it,
                racket = racket,
                bricks = bricks
            )
        }
    val ballsMoved = updateBallsMovement(balls = newBallsUpdated)
    return ballsMoved
}

/*
* Filtra a lista de bolas de jogo removendo as que estão fora dos limites de jogo
* */
fun filterBallsOutOfBounds(balls: List<Ball>) = balls.filter { !it.isOutOfBounds() }

/*
* Verifica a colisão de cada bola e atualiza a velocidade e movimento de cada uma
* */
fun checkAndUpdateBallMovementAfterCollision(ball: Ball, racket: Racket, bricks: List<Brick>) =
    updateBallMovementAfterCollision(
        ball = ball,
        racket = racket,
        racketCollision = ball.isCollidingWithRacket(racket = racket),
        areaCollision = ball.isCollidingWithArea(),
        brickCollision = ball.checkBricksCollision(bricks)
    )

fun generateLives(lives: Int): List<Ball> {
    return (1..lives).map {
        Ball(x = it * LIVES_X_SPACE, y = LIVES_Y_POSITION, stuck = true)
    }
}