package me.nfekete.adventofcode.y2020.day17

import me.nfekete.adventofcode.y2020.common.classpathFile

data class Coords3(val x: Int, val y: Int, val z: Int)
data class Coords2(val x: Int, val y: Int)

val origin = Coords3(0, 0, 0)
val directions =
    (-1..1).flatMap { x -> (-1..1).flatMap { y -> (-1..1).map { z -> Coords3(x, y, z) }.filter { origin != it } } }

data class Space3(val cubes: Set<Coords3>) {
    fun isActive(coords3: Coords3) = cubes.contains(coords3)
    private fun range(fn: (Coords3) -> Int) =
        cubes.map(fn).let { if (it.isEmpty()) 0..0 else (it.minOrNull()!! - 1)..(it.maxOrNull()!! + 1) }.asSequence()

    val xrange get() = range { it.x }
    val yrange get() = range { it.y }
    val zrange get() = range { it.z }
    val range: Sequence<Coords3>
        get() =
            xrange.flatMap { x ->
                yrange.flatMap { y ->
                    zrange.map { z -> Coords3(x, y, z) }
                }
            }

    private fun iterate(): Space3 {
        val newCubes = range.mapNotNull { coord3 ->
            val active = isActive(coord3)
            val count = coord3.neighbors.count { neighboringCoord -> isActive(neighboringCoord) }
            when {
                active && count in 2..3 -> coord3
                !active && count == 3 -> coord3
                else -> null
            }
        }.toSet()
        return Space3(newCubes)
    }

    fun boot(): Space3 = generateSequence(this.iterate()) { space3 -> space3.iterate() }.take(6).last()
}

fun Coords2.extend(z: Int) = Coords3(x, y, z)
infix operator fun Coords3.plus(other: Coords3) = Coords3(x + other.x, y + other.y, z + other.z)
val Coords3.neighbors get() = directions.map { it plus this }

object Day17 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = classpathFile("input.txt").readLines().mapIndexed { row, line ->
            line.mapIndexed { column, char ->
                Coords2(column, row) to (char == '#')
            }.filter { (_, active) -> active }.map { (coords, _) -> coords }
        }.flatten().map { it.extend(0) }.toSet().let(::Space3)
        println(input)
        part1(input).let(::println)
    }

    private fun part1(input: Space3) = input.boot().cubes.size
}
