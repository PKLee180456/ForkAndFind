package com.forkandfind.app

import org.junit.Assert.*
import org.junit.Test

class RestaurantSearchTest {
    @Test fun searchCombinesWordsAcrossFieldsIgnoringCase() {
        val result = findRestaurants("  JAPANESE   wan ", "All", 3, false, false, emptySet(), "Top rated")
        assertEquals(listOf("Nori House"), result.map { it.name })
    }
    @Test fun filtersIntersectWithSavedCollection() {
        val result = findRestaurants("", "Italian", 2, true, true, setOf(3, 11), "Top rated")
        assertEquals(listOf(3), result.map { it.id })
    }
    @Test fun emptyFavoritesReturnsNoResults() {
        assertTrue(findRestaurants("", "All", 3, false, true, emptySet(), "Name").isEmpty())
    }
    @Test fun priceSortIsAscending() {
        val prices = findRestaurants("", "All", 3, false, false, emptySet(), "Price").map { it.price }
        assertEquals(prices.sorted(), prices)
    }
    @Test fun unknownQueryReturnsNoResults() {
        assertTrue(findRestaurants("unfindable", "All", 3, false, false, emptySet(), "Top rated").isEmpty())
    }
}
