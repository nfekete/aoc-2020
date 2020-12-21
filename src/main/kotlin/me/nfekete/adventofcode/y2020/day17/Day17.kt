package me.nfekete.adventofcode.y2020.day17

import me.nfekete.adventofcode.y2020.common.classpathFile
import me.nfekete.adventofcode.y2020.common.crossProduct

data class Point2D(val x: Int, val y: Int) {
    fun extend(z: Int) = Point3(x, y, z)
}

interface Point<out T : Point<T>> {
    val neighbors: List<T>
}

data class Point3(val x: Int, val y: Int, val z: Int) : Point<Point3> {
    override val neighbors get() = directions.map { it plus this }
    infix operator fun plus(other: Point3) = Point3(x + other.x, y + other.y, z + other.z)
    fun extend(w: Int) = Point4(x, y, z, w)

    companion object {
        private val origin = Point3(0, 0, 0)
        val directions = crossProduct(-1..1, -1..1, -1..1, ::Point3).filter { origin != it }
    }
}

data class Point4(val x: Int, val y: Int, val z: Int, val w: Int) : Point<Point4> {
    override val neighbors get() = directions.map { it plus this }
    infix operator fun Point4.plus(other: Point4) = Point4(x + other.x, y + other.y, z + other.z, w + other.w)

    companion object {
        private val origin = Point4(0, 0, 0, 0)
        val directions = crossProduct(-1..1, -1..1, -1..1, -1..1, ::Point4).filter { origin != it }
    }
}

interface Space<POINT> {
    val cubes: Set<POINT>
    val range: Sequence<POINT>
    fun range(fn: (POINT) -> Int) =
        cubes.map(fn).let { if (it.isEmpty()) 0..0 else (it.minOrNull()!! - 1)..(it.maxOrNull()!! + 1) }
            .asSequence()

    fun isActive(point: POINT): Boolean = cubes.contains(point)
}

data class Space3(override val cubes: Set<Point3>) : Space<Point3> {
    val extend = cubes.map { it.extend(0) }.toSet().let(::Space4)
    override val range: Sequence<Point3>
        get() = crossProduct(range { it.x }, range { it.y }, range { it.z }, ::Point3)
}

data class Space4(override val cubes: Set<Point4>) : Space<Point4> {
    override fun isActive(point: Point4) = cubes.contains(point)

    override val range: Sequence<Point4>
        get() = crossProduct(range { it.x }, range { it.y }, range { it.z }, range { it.w }, ::Point4)
}

interface Simulator<POINT : Point<POINT>, SPACE : Space<POINT>> {
    fun newSpace(cubes: Set<POINT>): SPACE
    fun boot(space: SPACE): SPACE = generateSequence(space) { current -> iterate(current) }.take(1+6).last()
    fun iterate(space: SPACE): SPACE {
        val newCubes = space.range.mapNotNull { point ->
            val active = space.isActive(point)
            val count = point.neighbors.count { neighbor -> space.isActive(neighbor) }
            when {
                active && count in 2..3 -> point
                !active && count == 3 -> point
                else -> null
            }
        }.toSet()
        return newSpace(newCubes)
    }
}

class Simulator3 : Simulator<Point3, Space3> {
    override fun newSpace(cubes: Set<Point3>) = Space3(cubes)
}

class Simulator4 : Simulator<Point4, Space4> {
    override fun newSpace(cubes: Set<Point4>) = Space4(cubes)
}

object Day17 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = classpathFile("input.txt").readLines().mapIndexed { row, line ->
            line.mapIndexed { column, char ->
                Point2D(column, row) to (char == '#')
            }.filter { (_, active) -> active }.map { (point, _) -> point }
        }.flatten().map { it.extend(0) }.toSet().let(::Space3)
        part1(input).let(::println)
        part2(input.extend).let(::println)
    }

    private fun part1(input: Space3) = Simulator3().boot(input).cubes.size
    private fun part2(input: Space4) = Simulator4().boot(input).cubes.size
}
