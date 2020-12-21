package me.nfekete.adventofcode.y2020.day21

import me.nfekete.adventofcode.y2020.common.*

object Day21 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = classpathFile("input.txt").readLines().map { line ->
            line.splitByDelimiter(" (contains ")
                .map1 { it.split(" ") }
                .map2 { it.removeSuffix(")").split(", ") }
        }.onEach(::println)
        val allIngredients = input.flatMap { it.first }.toSet()
        val map = input.map { it.swapped }
            .flatMap { it.first.map { allergen -> allergen to it.second.toSet() } }
            .groupBy { it.first }
            .mapValues { (_, value) -> value.unzip().second }
        val narrowSetOfIngredientsByAllergens =
            map.mapValues { (_, ingredientSets) -> ingredientSets.reduce { acc, set -> acc.intersect(set) } }
        val ingredientsNotAssociatedWithAllergens = allIngredients - narrowSetOfIngredientsByAllergens.values.flatten()
        val numberOfTimesIngredientsNotAssociatedWithAllergensAppear =
            ingredientsNotAssociatedWithAllergens.map { ingredient -> input.count { (ingredients, _) -> ingredient in ingredients } }
                .sum()
        println("All ingredients: $allIngredients")
        println("Ingredients to allergens map: $map")
        println("Narrow set of ingredients by allergens: $narrowSetOfIngredientsByAllergens")
        println("Ingredients that are not associated with any allergens: $ingredientsNotAssociatedWithAllergens")
        println("Number of times ingredients not associated with allergens appear: $numberOfTimesIngredientsNotAssociatedWithAllergensAppear")
    }
}
