package com.forkandfind.app

data class Restaurant(
    val id: Int, val name: String, val cuisine: String, val neighborhood: String,
    val rating: Double, val price: Int, val vegetarian: Boolean,
    val emoji: String, val description: String, val specialty: String
)

/** Fictional demonstration data. Replace with a provider-backed repository for live results. */
object RestaurantCatalog {
    val restaurants = listOf(
        Restaurant(1, "Little Bamboo", "Cantonese", "Central", 4.9, 2, true, "🥟", "A quiet neighborhood kitchen serving delicate handmade dim sum and fragrant tea. Gather around a table and order a little of everything.", "Mushroom & bamboo dumplings"),
        Restaurant(2, "Nori House", "Japanese", "Sheung Wan", 4.8, 3, false, "🍣", "Seasonal sushi, warm wood interiors, and a small counter overlooking the kitchen. A relaxed spot for a long lunch.", "Chef’s seasonal sushi selection"),
        Restaurant(3, "Sunday Dough", "Italian", "Wan Chai", 4.7, 2, true, "🍕", "Slow-fermented pizza with blistered crusts, bright tomato sauce, and generous toppings. Made for sharing with friends.", "Burrata & basil pizza"),
        Restaurant(4, "Green Table", "Vegetarian", "Central", 4.8, 2, true, "🥗", "Colorful vegetables take center stage in nourishing bowls and inventive plant-based plates.", "Roasted pumpkin grain bowl"),
        Restaurant(5, "Seoul Social", "Korean", "Causeway Bay", 4.6, 2, true, "🍜", "Comforting Korean flavors, bubbling stews, and an easygoing atmosphere for a casual dinner.", "Stone-pot mushroom bibimbap"),
        Restaurant(6, "Chai & Chutney", "Indian", "Tsim Sha Tsui", 4.7, 1, true, "🍛", "A cozy kitchen with freshly ground spices, flaky breads, and warming curries inspired by family recipes.", "Chickpea masala with garlic naan"),
        Restaurant(7, "Harbour Roast", "Cafe", "Kennedy Town", 4.5, 1, true, "☕", "Specialty coffee and all-day breakfast in a sunlit corner cafe. Bring a book and settle in.", "Sourdough toast & flat white"),
        Restaurant(8, "Siam Garden", "Thai", "Wan Chai", 4.6, 2, true, "🍲", "Fresh herbs, citrus, and a little heat bring classic Thai dishes to life in a leafy dining room.", "Coconut vegetable green curry"),
        Restaurant(9, "The Burger Club", "American", "Mong Kok", 4.4, 1, false, "🍔", "Juicy smash burgers, crispy fries, and old-fashioned shakes in a lively little diner.", "Double smash cheeseburger"),
        Restaurant(10, "Golden Wok", "Cantonese", "Tsim Sha Tsui", 4.6, 1, false, "🍚", "Wok-fired comfort food and generous portions, with a menu built around familiar Cantonese favorites.", "Signature roast duck rice"),
        Restaurant(11, "Pasta Posto", "Italian", "Sheung Wan", 4.8, 3, true, "🍝", "Fresh pasta rolled each morning and paired with simple, thoughtful sauces in an intimate dining room.", "Wild mushroom tagliatelle"),
        Restaurant(12, "Ramen Alley", "Japanese", "Mong Kok", 4.7, 1, false, "🍜", "Rich, slow-simmered broth and springy noodles served at a compact counter tucked away from the busy streets.", "Black garlic tonkotsu ramen")
    )
}

fun findRestaurants(query: String, cuisine: String, maxPrice: Int, vegetarian: Boolean,
                    savedOnly: Boolean, favorites: Set<Int>, sort: String): List<Restaurant> {
    val terms = query.trim().lowercase().split(Regex("\\s+")).filter { it.isNotEmpty() }
    val matches = RestaurantCatalog.restaurants.filter { r ->
        val searchable = "${r.name} ${r.cuisine} ${r.neighborhood} ${r.specialty}".lowercase()
        terms.all { it in searchable } && (cuisine == "All" || r.cuisine == cuisine) &&
            r.price <= maxPrice && (!vegetarian || r.vegetarian) && (!savedOnly || r.id in favorites)
    }
    return when (sort) {
        "Price" -> matches.sortedWith(compareBy<Restaurant> { it.price }.thenByDescending { it.rating })
        "Name" -> matches.sortedBy { it.name }
        else -> matches.sortedByDescending { it.rating }
    }
}
