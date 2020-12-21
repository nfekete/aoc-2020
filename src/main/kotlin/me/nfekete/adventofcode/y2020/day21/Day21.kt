package me.nfekete.adventofcode.y2020.day21

import me.nfekete.adventofcode.y2020.common.*

object Day21 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = classpathFile("input.txt").readLines().map { line ->
            line.splitByDelimiter(" (contains ")
                .map1 { it.split(" ") }
                .map2 { it.removeSuffix(")").split(", ") }
        }
        val allIngredients = input.flatMap { it.first }.toSet()
        val mapAllergenToSetsOfIngredients = input.map { it.swapped }
            .flatMap { it.first.map { allergen -> allergen to it.second.toSet() } }
            .groupBy { it.first }
            .mapValues { (_, value) -> value.unzip().second }
        val narrowSetOfIngredientsByAllergens =
            mapAllergenToSetsOfIngredients.mapValues { (_, ingredientSets) -> ingredientSets.reduce { acc, set -> acc.intersect(set) } }
        val ingredientsNotAssociatedWithAllergens = allIngredients - narrowSetOfIngredientsByAllergens.values.flatten()
        val numberOfTimesIngredientsNotAssociatedWithAllergensAppear =
            ingredientsNotAssociatedWithAllergens.map { ingredient -> input.count { (ingredients, _) -> ingredient in ingredients } }
                .sum()
        val (_, resolved) = generateSequence(narrowSetOfIngredientsByAllergens to emptyMap<String, String>()) { (candidates, resolved) ->
            val newlyResolved = candidates.filter { (_, v) -> v.size == 1 }.mapValues { (_, v) -> v.single() }
            val resolved = resolved + newlyResolved
            val candidates = (candidates - newlyResolved.keys).mapValues { (_, v) -> v - newlyResolved.values }
            candidates to resolved
        }.first { (ambiguous, _) -> ambiguous.isEmpty() }
        val ingredientsSortedByAllergens = resolved.entries.sortedBy { it.key }.joinToString(",") { it.value }
        println("Number of times ingredients not associated with allergens appear: $numberOfTimesIngredientsNotAssociatedWithAllergensAppear")
        println("Ingredients sorted by their allergen: $ingredientsSortedByAllergens")
    }
}
