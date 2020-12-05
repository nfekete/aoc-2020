package me.nfekete.adventofcode.y2020.common

import java.io.BufferedReader
import java.io.InputStreamReader

inline fun <reified T : Any> T.classpathFile(path: String) =
    BufferedReader(
        InputStreamReader(
            javaClass.getResourceAsStream(path)
        )
    )
