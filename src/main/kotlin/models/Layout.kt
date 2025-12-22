package org.example.models

val basicTypes = BrickType.entries.filter { it.hits == SINGLE_HIT }

val allColors: BricksColumn = BricksColumn(rows = basicTypes.map { BricksRow(bricks = listOf(it, it, it)) })
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

val bricksLayout: List<BricksColumn> = listOf(
    allColors,
    middleColors,
    allColors
)

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

