import org.example.Models.Ball
import org.example.Models.Brick
import org.example.Models.BrickType
import org.example.Models.Collision
import org.example.Models.checkBrickHorizontalCollision
import org.example.Models.checkBrickVerticalCollision
import org.example.Models.isCollidingWithBrick
import org.example.Models.sumPoints
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BallTests {

    @Test
    fun ballCollidingWithHorizontalSideBrickShouldReturnCorrectCollision() {
        var ball = Ball(100, 100, 1, -1)
        var brick = Brick(100, 100, BrickType.MAGENTA, 0)

        var collision = checkBrickHorizontalCollision(ball, brick)
        assertEquals(Collision.HORIZONTAL, collision)


        ball = Ball(80, 100, 1, -1)
        collision = checkBrickHorizontalCollision(ball, brick)
        assertEquals(Collision.NONE, collision)

        ball = Ball(135, 100, 1, -1)
        collision = checkBrickHorizontalCollision(ball, brick)
        assertEquals(Collision.HORIZONTAL, collision)

        ball = Ball(155, 100, 1, -1)
        collision = checkBrickHorizontalCollision(ball, brick)
        assertEquals(Collision.NONE, collision)

        ball = Ball(x = 312, y = 40, deltaX = -6, deltaY = 4)
        brick = Brick(x = 288, y = 45, type = BrickType.MAGENTA, hitCounter = 0)
        collision = ball.isCollidingWithBrick(brick)
        assertEquals(Collision.VERTICAL, collision)
    }

    @Test
    fun ballCollidingWithVerticalSideBrickShouldReturnCorrectCollision() {
        var ball = Ball(100, 100, 1, -1)
        var brick = Brick(100, 200, BrickType.MAGENTA, 0)

        var collision = checkBrickVerticalCollision(ball, brick)
        assertEquals(Collision.NONE, collision)

        ball = Ball(120, 195, 1, -1)
        collision = checkBrickVerticalCollision(ball, brick)
        assertEquals(Collision.VERTICAL, collision)

        ball = Ball(120, 220, 1, -1)
        collision = checkBrickVerticalCollision(ball, brick)
        assertEquals(Collision.VERTICAL, collision)

        ball = Ball(120, 275, 1, -1)
        collision = checkBrickVerticalCollision(ball, brick)
        assertEquals(Collision.NONE, collision)

    }

    @Test
    fun totalSumOfPointsFromHitsBricksShouldBeCorrect() {
        val bricks = listOf<Brick>(
            Brick(100, 200, BrickType.YELLOW, 0),
            Brick(100, 200, BrickType.YELLOW, 0),
            Brick(100, 200, BrickType.MAGENTA, 1),
            Brick(100, 200, BrickType.MAGENTA, 1),
            Brick(100, 200, BrickType.MAGENTA, 1),
        )

        val points = sumPoints(bricks)

        assertEquals(BrickType.MAGENTA.points * 3, points)
    }
}
