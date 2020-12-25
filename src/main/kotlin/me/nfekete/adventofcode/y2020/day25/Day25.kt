package me.nfekete.adventofcode.y2020.day25

private fun loop(subject: Long): (Long) -> Long = { it * subject % 20201227 }
private fun countLoops(cardPk: Long) = generateSequence(1, loop(7)).takeWhile { it != cardPk }.count()
private fun encryptionKey(publicKey: Long, loops: Int) = generateSequence(1, loop(publicKey)).drop(loops).first()

private object Day25 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = 11239946L to 10464955L
        val (cardPk, doorPk) = input
        val cardLoopSize = countLoops(cardPk).also { println("Card loops = $it") }
        val doorLoopSize = countLoops(doorPk).also { println("Door loops = $it") }
        encryptionKey(cardPk, doorLoopSize).also { println("Encryption key = $it") }
        encryptionKey(doorPk, cardLoopSize).also { println("Encryption key = $it") }
    }
}
