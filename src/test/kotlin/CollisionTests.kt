import org.example.models.BALL_RADIUS
import org.example.models.BRICK_HEIGHT
import org.example.models.BRICK_WIDTH
import org.example.models.Ball
import org.example.models.Brick
import org.example.models.BrickType
import org.example.models.Collision
import org.example.models.checkBrickCollision
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CollisionTests {

    @Test
    fun `bola colide por cima do brick`() {
        val brick = Brick(x = 288, y = 60, type = BrickType.MAGENTA, hitCounter = 0)
        val ball = Ball(
            x = 300.0,
            y = 60.0 - BALL_RADIUS + 1,
            deltaX = 0,
            deltaY = 2,
            weight = 1.0,
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
            weight = 1.0,
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
            weight = 1.0,
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
            weight = 1.0,
            stuck = false
        )

        assertEquals(Collision.HORIZONTAL, checkBrickCollision(ball, brick))
    }

    @Test
    fun `bola colide no canto superior esquerdo do brick`() {
        val brick = Brick(x = 288, y = 60, type = BrickType.MAGENTA, hitCounter = 0)
        val ball = Ball(
            x = 288.0 - 4,
            y = 60.0 - 4,
            deltaX = 2,
            deltaY = -2,
            weight = 1.0,
            stuck = false
        )

        assertEquals(Collision.HORIZONTAL, checkBrickCollision(ball, brick))
    }

    @Test
    fun `bola colide no canto inferior direito do brick`() {
        val brick = Brick(x = 288, y = 60, type = BrickType.WHITE, hitCounter = 0)
        val ball = Ball(
            x = 288.0 + BRICK_WIDTH + BALL_RADIUS - 3,
            y = 60.0 + BRICK_HEIGHT + BALL_RADIUS - 3,
            deltaX = -2,
            deltaY = 2,
            weight = 1.0,
            stuck = false
        )

        assertEquals(Collision.HORIZONTAL, checkBrickCollision(ball, brick))
    }

    @Test
    fun `bola colide no canto inferior direito do brick mas a subir`() {
        val brick = Brick(x = 288, y = 60, type = BrickType.WHITE, hitCounter = 0)
        val ball = Ball(
            x = 288.0 + BRICK_WIDTH + BALL_RADIUS - 3,
            y = 60.0 + BRICK_HEIGHT + BALL_RADIUS - 3,
            deltaX = -2,
            deltaY = -2,
            weight = 1.0,
            stuck = false
        )

        assertEquals(Collision.BOTH, checkBrickCollision(ball, brick))
    }

    @Test
    fun `bola longe do brick nao colide`() {
        val brick = Brick(x = 288, y = 60, type = BrickType.WHITE, hitCounter = 0)
        val ball = Ball(
            x = 100.0,
            y = 100.0,
            deltaX = 1,
            deltaY = 1,
            weight = 1.0,
            stuck = false
        )

        assertEquals(Collision.NONE, checkBrickCollision(ball, brick))
    }

    @Test
    fun `bola apenas tangente ao brick conta como colisao`() {
        val brick = Brick(x = 288, y = 60, type = BrickType.WHITE, hitCounter = 0)
        val ball = Ball(
            x = 288.0 - BALL_RADIUS,
            y = 70.0,
            deltaX = 1,
            deltaY = 0,
            weight = 1.0,
            stuck = false
        )

        assertEquals(Collision.HORIZONTAL, checkBrickCollision(ball, brick))
    }
}