package org.example.models

val basicTypes = BrickType.entries.filter { it.hits == SINGLE_HIT }

val allColors: BricksColumn = BricksColumn(basicTypes.map { BricksRow(listOf(it, it, it)) })
val middleColors: BricksColumn = BricksColumn(
    rows = listOf(
        BricksRow(bricks = listOf(BrickType.WHITE, BrickType.GOLD, BrickType.WHITE)),
        BricksRow(bricks = listOf(BrickType.ORANGE, BrickType.ORANGE, BrickType.ORANGE)),
        BricksRow(bricks = listOf(BrickType.CYAN, BrickType.CYAN, BrickType.CYAN)),
        BricksRow(bricks = listOf(BrickType.GREEN, BrickType.GREEN, BrickType.GREEN)),
        BricksRow(bricks = listOf(BrickType.RED, BrickType.RED, BrickType.RED)),
        BricksRow(bricks = listOf(BrickType.BLUE, BrickType.BLUE, BrickType.BLUE)),
        BricksRow(bricks = listOf(BrickType.MAGENTA, BrickType.MAGENTA, BrickType.MAGENTA)),
        BricksRow(bricks = listOf(BrickType.SILVER, BrickType.SILVER, BrickType.SILVER)),
    )
)

val allRedBricks: BricksColumn =
    BricksColumn((0 until 10).map { BricksRow(listOf(BrickType.RED, BrickType.RED, BrickType.RED)) })
val allYellowBricks: BricksColumn = BricksColumn((0 until 10).mapIndexed { index, _ ->
    when (index) {
        in 0 until 2 -> BricksRow(
            listOf(
                BrickType.RED,
                BrickType.RED,
                BrickType.EMPTY,
                BrickType.GREEN,
                BrickType.GREEN
            )
        )

        in 3 until 7 -> BricksRow(
            listOf(
                BrickType.EMPTY,
                BrickType.YELLOW,
                BrickType.YELLOW,
                BrickType.YELLOW,
                BrickType.EMPTY
            )
        )

        in 8 until 10 -> BricksRow(
            listOf(
                BrickType.RED,
                BrickType.RED,
                BrickType.EMPTY,
                BrickType.GREEN,
                BrickType.GREEN
            )
        )

        else -> BricksRow(listOf(BrickType.EMPTY))
    }
})

val allGreenBricks: BricksColumn =
    BricksColumn((0 until 10).map { BricksRow(listOf(BrickType.GREEN, BrickType.GREEN, BrickType.GREEN)) })

val bricksLayout: List<BricksColumn> = listOf(
    allColors,
    middleColors,
    allColors
)

val portugalBricksLayout: List<BricksColumn> = listOf(
    allRedBricks,
    allYellowBricks,
    allGreenBricks,
)

val singleBricksLayout: List<BricksColumn> = listOf(
    BricksColumn(
        listOf(
            BricksRow(bricks = listOf(BrickType.WHITE)),
        )
    )
)

fun generateWallBricks(): List<Brick> {
    var lista: List<Brick> = emptyList()
    for (x in LeftMarginBricks * 4..WIDTH - RightMarginBricks * 4 step BRICK_WIDTH) {
        for (y in TopMarginBricks..TopMarginBricks + BRICK_HEIGHT * 3 step BRICK_HEIGHT + 2) {
            lista = lista + Brick(x, y, BrickType.entries.random())
        }
    }
    return lista
}

fun generateInitialBricksLayout(layout: List<BricksColumn>): List<Brick> {
    var lista: List<Brick> = emptyList()
    var brickY = TopMarginBricks
    var brickX = 0
    var initialX = 0
    var columnSize = 0

    for (column in layout) {
        column.rows.forEach {
            columnSize = it.bricks.size
            it.bricks.forEach {
                brickX += BRICK_WIDTH
                lista = lista + Brick(brickX, brickY, it)
            }
            brickY += BRICK_HEIGHT
            brickX = initialX
        }
        initialX += BRICK_WIDTH * (columnSize + 1)
        brickX = initialX
        brickY = TopMarginBricks
    }

    return lista
}

