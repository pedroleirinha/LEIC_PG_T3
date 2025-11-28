package org.example

import pt.isel.canvas.BLACK
import pt.isel.canvas.Canvas
import pt.isel.canvas.ESCAPE_CODE
import pt.isel.canvas.WHITE

const val WIDTH = BRICK_WIDTH * 13
const val HEIGHT = 600
const val BACKGROUND_COLOR = BLACK
const val TIME_TICK_MLS = 10

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
    val points: Int = 0
)

val arena = Canvas(WIDTH, HEIGHT, BACKGROUND_COLOR)

fun gameStart() {
    var game = Game(bricks = generateInitialBricksLayout(bricksLayout), balls = listOf(generateRandomBall()))

    arena.onTimeProgress(period = TIME_TICK_MLS) {
        arena.erase()
        val bricks = clearBrokenBricks(game.bricks, game.balls)
        val points = sumPoints(bricks)
        val updatedBalls = handleGameBallsBehaviour(balls = game.balls, racket = game.racket, bricks = game.bricks)

        val filteredBricks = bricks.filter { !it.isBroken() }
        if (!game.balls.isEmpty() && updatedBalls.isEmpty()) arena.close()

        game = game.copy(bricks = filteredBricks, balls = updatedBalls, points = game.points + points)

        drawGame(game)
    }

    arena.onMouseMove { me ->
        game = game.copy(racket = game.racket.moveTo(to = me.x))
    }

    arena.onKeyPressed {
        if (it.code == ESCAPE_CODE) arena.close()
    }
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
* Desenha as bolas em jogo no canvas
* */
fun drawBalls(ballsList: List<Ball>) {
    ballsList.forEach { arena.drawCircle(xCenter = it.x, yCenter = it.y, radius = BALL_RADIUS, color = BALL_COLOR) }
}

/*
* Desenha no canvas o número de pontos adquiridos durante o jogo no momento*/
fun drawGamePoints(points: Int) {
    arena.drawText(
        x = WIDTH / 2,
        y = BALL_COUNTER_YCORD,
        txt = points.toString(),
        color = WHITE,
        fontSize = BALL_COUNT_FONTSIZE
    )
}

/*
* A cada step do jogo, remove as bolas fora de jogo, verifica as colisões e atualiza os movimentos das bolas para serem desenhadas novamente.
* */
fun handleGameBallsBehaviour(balls: List<Ball>, racket: Racket, bricks: List<Brick>): List<Ball> {

    val filteredBalls = filterBallsOutOfBounds(balls = balls)
    val newBallsUpdated =
        (if (runningEnviroment == ENVIROMENT.DEBUG) balls else filteredBalls).map {
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

/*
* Desenha o jogo no canvas, que inclui desenhar a raquete, bolas e o contador de bolas em jogo
* */
fun drawGame(game: Game) {
    drawRacket(racket = game.racket)
    drawBalls(ballsList = game.balls)
    drawGamePoints(game.points)
    drawBricks(bricks = game.bricks)
}
