package org.example

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
val bricksLayout: List<BricksColumn> = listOf(
    allColors,
    middleColors,
    allColors
)

