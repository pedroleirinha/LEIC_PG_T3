import org.example.models.BALL_RADIUS
import org.example.models.BRICK_HEIGHT
import org.example.models.BRICK_WIDTH
import org.example.models.Ball
import org.example.models.Brick
import org.example.models.BrickType
import org.example.models.Collision
import org.example.models.MAX_DELTA_Y
import org.example.models.checkBrickCollision
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CollisionTests {

    @Test
    fun `bola colide por cima do brick`() {
        val ball = Ball(x = 81.5, y = 98.0, deltaX = -1, deltaY = 2, mass = 1.5, stuck = false)
        val brick = Brick(x = 64, y = 105, type = BrickType.GREEN, hitCounter = 0, gift = null)

        assertEquals(Collision.VERTICAL, checkBrickCollision(ball, brick))
    }

    @Test
    fun `bola colide por cima do brick2`() {
        val brick = Brick(x = 288, y = 60, type = BrickType.MAGENTA, hitCounter = 0)
        val ball = Ball(
            x = 300.0,
            y = 60.0 - BALL_RADIUS + 1,
            deltaX = 0,
            deltaY = 2,
            mass = 1.0,
            stuck = false
        )

        assertEquals(Collision.VERTICAL, checkBrickCollision(ball, brick))
    }

    @Test
    fun `bola colide por baixo do brick`() {
        val brick = Brick(x = 288, y = 60, type = BrickType.WHITE, hitCounter = 0)
        val ball = Ball(
            x = 300.0,
            y = 60.0 + BRICK_HEIGHT + BALL_RADIUS - 1,
            deltaX = 0,
            deltaY = -2,
            mass = 1.0,
            stuck = false
        )

        assertEquals(Collision.VERTICAL, checkBrickCollision(ball, brick))
    }

    @Test
    fun `bola colide na lateral esquerda do brick`() {
        val brick = Brick(x = 288, y = 60, type = BrickType.WHITE, hitCounter = 0)
        val ball = Ball(
            x = 288.0 - BALL_RADIUS + 1,
            y = 70.0,
            deltaX = 2,
            deltaY = 0,
            mass = 1.0,
            stuck = false
        )

        assertEquals(Collision.HORIZONTAL, checkBrickCollision(ball, brick))
    }

    @Test
    fun `bola colide na lateral direita do brick`() {
        val brick = Brick(x = 288, y = 60, type = BrickType.WHITE, hitCounter = 0)
        val ball = Ball(
            x = 288.0 + BRICK_WIDTH + BALL_RADIUS - 1,
            y = 70.0,
            deltaX = -2,
            deltaY = 0,
            mass = 1.0,
            stuck = false
        )

        assertEquals(Collision.HORIZONTAL, checkBrickCollision(ball, brick))
    }

    // Testes para casos limítes
    @Test
    fun `bola quase colidindo no canto superior esquerdo retorna NONE`() {
        val brick = Brick(x = 288, y = 60, type = BrickType.WHITE, hitCounter = 0)
        val ball = Ball(
            x = 288.0 - BALL_RADIUS - 5,  // Fora do alcance
            y = 60.0 - BALL_RADIUS - 5,
            deltaX = 2,
            deltaY = 2,
            mass = 1.0,
            stuck = false
        )

        assertEquals(Collision.NONE, checkBrickCollision(ball, brick))
    }

    @Test
    fun `bola colide exatamente no meio de dois tijolos adjacentes horizontalmente`() {
        val brick1 = Brick(x = 100, y = 60, type = BrickType.WHITE)
        val brick2 = Brick(x = 100 + BRICK_WIDTH, y = 60, type = BrickType.WHITE)

        // Bola posicionada exatamente na junção entre os dois
        val ball = Ball(
            x = 100.0 + BRICK_WIDTH,
            y = 60.0 - BALL_RADIUS + 1,
            deltaX = 0,
            deltaY = 2
        )

        // A colisão deve ser VERTICAL (bateu no topo de ambos)
        assertEquals(Collision.VERTICAL, checkBrickCollision(ball, brick1))
        assertEquals(Collision.VERTICAL, checkBrickCollision(ball, brick2))
    }

    @Test
    fun `bola passa num buraco estreito entre dois tijolos sem colidir`() {
        val brickLeft = Brick(x = 0, y = 100, type = BrickType.WHITE)
        val brickRight = Brick(x = 100, y = 100, type = BrickType.WHITE)
        // Se a largura entre eles for > raio*2, a bola deve passar

        val ball = Ball(
            x = 75.0, // Supondo que este X está no vazio entre eles
            y = 100.0,
            deltaX = 0,
            deltaY = 5
        )

        assertEquals(Collision.NONE, checkBrickCollision(ball, brickLeft))
        assertEquals(Collision.NONE, checkBrickCollision(ball, brickRight))
    }

    @Test
    fun `teste bola`() {
        val brickLeft = Brick(x = 224, y = 60, type = BrickType.ORANGE, hitCounter = 0, gift = null)
        val ball = Ball(x = 262.0, y = 77.0, deltaX = 0, deltaY = -2, mass = 1.0, stuck = false)


        assertEquals(Collision.VERTICAL, checkBrickCollision(ball, brickLeft))
    }
}

