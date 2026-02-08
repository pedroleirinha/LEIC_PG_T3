import org.example.models.*
import org.example.models.Brick
import org.junit.jupiter.api.Test
import kotlin.collections.listOf
import kotlin.test.assertEquals

class BallTests {

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
