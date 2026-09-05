package com.forkandfind.app

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Forest = Color(0xFF183D35)
private val Cream = Color(0xFFFAF8F4)
private val Muted = Color(0xFF69766E)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(primary = Forest, background = Cream,
                surface = Cream, secondaryContainer = Color(0xFFE2EBDD), onSecondaryContainer = Forest)) {
                RestaurantApp()
            }
        }
    }
}

@Composable
private fun RestaurantApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("favorites", 0) }
    var favorites by remember { mutableStateOf(prefs.getStringSet("ids", emptySet()).orEmpty().mapNotNull { it.toIntOrNull() }.toSet()) }
    var query by rememberSaveable { mutableStateOf("") }
    var cuisine by rememberSaveable { mutableStateOf("All") }
    var maxPrice by rememberSaveable { mutableIntStateOf(3) }
    var vegetarian by rememberSaveable { mutableStateOf(false) }
    var savedOnly by rememberSaveable { mutableStateOf(false) }
    var sort by rememberSaveable { mutableStateOf("Top rated") }
    var selectedId by rememberSaveable { mutableStateOf<Int?>(null) }
    val toggle: (Int) -> Unit = { id ->
        favorites = if (id in favorites) favorites - id else favorites + id
        prefs.edit().putStringSet("ids", favorites.map { it.toString() }.toSet()).apply()
    }
    val selected = RestaurantCatalog.restaurants.firstOrNull { it.id == selectedId }
    BackHandler(selected != null) { selectedId = null }
    Scaffold(containerColor = Cream, bottomBar = {
        if (selected == null) NavigationBar(containerColor = Color.White) {
            NavigationBarItem(selected = !savedOnly, onClick = { savedOnly = false },
                icon = { Icon(Icons.Default.Explore, null) }, label = { Text("Explore") })
            NavigationBarItem(selected = savedOnly, onClick = { savedOnly = true },
                icon = { Icon(Icons.Default.Favorite, null) }, label = { Text("Saved (${favorites.size})") })
        }
    }) { padding ->
        if (selected != null) {
            RestaurantDetails(selected, selected.id in favorites, { toggle(selected.id) },
                { selectedId = null }, Modifier.padding(padding))
        } else {
            val results = findRestaurants(query, cuisine, maxPrice, vegetarian, savedOnly, favorites, sort)
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item {
                    Column(Modifier.padding(horizontal = 24.dp).padding(top = 24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Restaurant, null, tint = Forest)
                            Text("  FORK & FIND", color = Forest, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                        }
                        Spacer(Modifier.height(28.dp))
                        Text(if (savedOnly) "Your little black\nbook of good food." else "Good food.\nGreat discoveries.",
                            fontSize = 36.sp, lineHeight = 40.sp, fontWeight = FontWeight.Bold, color = Forest)
                        Spacer(Modifier.height(12.dp))
                        Text(if (savedOnly) "All your favorites, in one delicious place." else "Find your next favorite table in Hong Kong.", color = Muted)
                        Spacer(Modifier.height(20.dp))
                        OutlinedTextField(value = query, onValueChange = { query = it },
                            modifier = Modifier.fillMaxWidth(), singleLine = true,
                            placeholder = { Text("Restaurant, cuisine, or neighborhood", fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, null) },
                            trailingIcon = { if (query.isNotEmpty()) IconButton(onClick = { query = "" }) { Icon(Icons.Default.Close, "Clear search") } },
                            shape = RoundedCornerShape(18.dp))
                    }
                }
                item {
                    LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(listOf("All") + RestaurantCatalog.restaurants.map { it.cuisine }.distinct().sorted()) { category ->
                            FilterChip(selected = cuisine == category, onClick = { cuisine = category }, label = { Text(category) })
                        }
                    }
                }
                item {
                    Column(Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Budget", color = Muted, fontSize = 13.sp)
                            (1..3).forEach { price ->
                                FilterChip(selected = maxPrice == price, onClick = { maxPrice = price }, label = { Text(if (price == 3) "Any" else "$".repeat(price)) })
                            }
                        }
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Eco, null, tint = Forest, modifier = Modifier.size(20.dp))
                            Text(" Vegetarian options", modifier = Modifier.weight(1f), color = Forest)
                            Switch(checked = vegetarian, onCheckedChange = { vegetarian = it })
                        }
                        Surface(color = Color(0xFFEDE9DE), shape = RoundedCornerShape(12.dp)) {
                            Text("DEMO COLLECTION · Fictional restaurants & ratings", Modifier.padding(12.dp), fontSize = 11.sp, color = Forest)
                        }
                    }
                }
                item {
                    Row(Modifier.padding(horizontal = 24.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("${results.size} places to try", fontWeight = FontWeight.Bold, color = Forest, modifier = Modifier.weight(1f))
                        var expanded by remember { mutableStateOf(false) }
                        Box {
                            TextButton(onClick = { expanded = true }) { Text(sort); Icon(Icons.Default.KeyboardArrowDown, null) }
                            DropdownMenu(expanded, onDismissRequest = { expanded = false }) {
                                listOf("Top rated", "Price", "Name").forEach { option ->
                                    DropdownMenuItem(text = { Text(option) }, onClick = { sort = option; expanded = false })
                                }
                            }
                        }
                    }
                }
                if (results.isEmpty()) item {
                    Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(if (savedOnly) Icons.Default.FavoriteBorder else Icons.Default.SearchOff, null, Modifier.size(48.dp), tint = Muted)
                        Spacer(Modifier.height(12.dp))
                        Text(if (savedOnly && favorites.isEmpty()) "Your next favorite is out there" else "No matching restaurants", fontWeight = FontWeight.Bold)
                        Text("Try another search or explore the full collection.", color = Muted)
                        TextButton(onClick = { query = ""; cuisine = "All"; maxPrice = 3; vegetarian = false; savedOnly = false }) { Text("Explore all restaurants") }
                    }
                }
                items(results, key = { it.id }) { restaurant ->
                    RestaurantCard(restaurant, restaurant.id in favorites, { toggle(restaurant.id) }, { selectedId = restaurant.id })
                }
            }
        }
    }
}

@Composable
private fun FoodArtwork(restaurant: Restaurant, modifier: Modifier = Modifier) {
    val palette = listOf(0xFFE6EAD9, 0xFFF0DDC8, 0xFFF1E6CA, 0xFFDFE9DF)
    Box(modifier.background(Color(palette[(restaurant.id - 1) % palette.size])), contentAlignment = Alignment.Center) {
        Text(restaurant.emoji, fontSize = 76.sp)
    }
}

@Composable
private fun RestaurantCard(r: Restaurant, saved: Boolean, toggle: () -> Unit, open: () -> Unit) {
    Card(onClick = open, modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(),
        shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Box {
            FoodArtwork(r, Modifier.fillMaxWidth().height(152.dp))
            FilledIconButton(onClick = toggle, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.White, contentColor = Forest)) {
                Icon(if (saved) Icons.Default.Favorite else Icons.Default.FavoriteBorder, if (saved) "Unsave ${r.name}" else "Save ${r.name}")
            }
            Surface(modifier = Modifier.align(Alignment.BottomStart).padding(12.dp), color = Color.White, shape = RoundedCornerShape(8.dp)) {
                Text(r.cuisine, Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 12.sp, color = Forest)
            }
        }
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(r.name, Modifier.weight(1f), fontSize = 21.sp, fontWeight = FontWeight.Bold, color = Forest)
                Icon(Icons.Default.Star, "Sample rating", Modifier.size(16.dp), tint = Color(0xFFB27918))
                Text(" ${r.rating}", fontWeight = FontWeight.Bold, color = Forest)
            }
            Text("${r.neighborhood}  ·  ${"$".repeat(r.price)}", color = Muted, fontSize = 13.sp)
            Text(r.specialty, color = Muted, fontSize = 13.sp)
        }
    }
}

@Composable
private fun RestaurantDetails(r: Restaurant, saved: Boolean, toggle: () -> Unit, back: () -> Unit, modifier: Modifier) {
    val context = LocalContext.current
    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 32.dp)) {
        item {
            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = back) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                Text("The details", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = Forest)
                IconButton(onClick = toggle) { Icon(if (saved) Icons.Default.Favorite else Icons.Default.FavoriteBorder, if (saved) "Remove from saved" else "Save restaurant", tint = Forest) }
            }
            FoodArtwork(r, Modifier.fillMaxWidth().height(240.dp))
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Text("${r.cuisine.uppercase()}  ·  ${r.neighborhood.uppercase()}", color = Muted, letterSpacing = 1.sp, fontSize = 12.sp)
                Text(r.name, fontSize = 34.sp, fontWeight = FontWeight.Bold, color = Forest)
                Text("★ ${r.rating} sample rating   ·   ${"$".repeat(r.price)} budget", color = Forest)
                Text(r.description, lineHeight = 25.sp, color = Muted)
                HorizontalDivider()
                Text("Come for the…", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Forest)
                Text(r.specialty, color = Muted)
                if (r.vegetarian) Text("🌿 Vegetarian options available in this sample menu", color = Forest)
                Button(onClick = toggle, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp), shape = RoundedCornerShape(14.dp)) {
                    Icon(if (saved) Icons.Default.Favorite else Icons.Default.FavoriteBorder, null)
                    Text(if (saved) "  Saved to your collection" else "  Save to your collection")
                }
                OutlinedButton(onClick = {
                    val url = "https://www.google.com/maps/search/?api=1&query=" + Uri.encode("${r.cuisine} restaurants in ${r.neighborhood}, Hong Kong")
                    try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
                    catch (_: ActivityNotFoundException) { Toast.makeText(context, "Install a browser or maps app to open this link.", Toast.LENGTH_LONG).show() }
                }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp), shape = RoundedCornerShape(14.dp)) {
                    Icon(Icons.Default.LocationOn, null); Text("  Explore this area on Maps")
                }
                Text("This is a fictional demo listing. Maps searches for real ${r.cuisine.lowercase()} restaurants in ${r.neighborhood}; it does not navigate to this sample restaurant. Prices and ratings are illustrative.", color = Muted, fontSize = 12.sp, lineHeight = 18.sp)
            }
        }
    }
}
