package com.example.studivo.presentation.ui.commons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.studivo.domain.model.helpers.navRoute
import com.example.studivo.presentation.navegacion.AppRoutes
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.studivo.R


@Composable
fun BottomNavigationBar(
	navController: NavController,
	modifier: Modifier = Modifier
) {
	val navBackStackEntry by navController.currentBackStackEntryAsState()
	val currentRoute = navBackStackEntry?.destination?.route
	
	val mainRoute = navRoute<AppRoutes.MainScreen>()
	val metronome = navRoute<AppRoutes.MetronomeScreen>()
	val stadisticsRoute = navRoute<AppRoutes.StatisticsScreen>()
	val settingsRoute = navRoute<AppRoutes.SettingsScreen>()
	
	val colorScheme = MaterialTheme.colorScheme
	
	Surface(
		tonalElevation = 0.dp,
		shadowElevation = 8.dp,
		shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
		modifier = modifier
			.navigationBarsPadding()
			.imePadding(),
		color = colorScheme.surface // 👈 se adapta al tema
	) {
		NavigationBar(
			containerColor = Color.Transparent,
			contentColor = colorScheme.onSurface, // 👈 texto/íconos adaptados
			tonalElevation = 0.dp,
			modifier = Modifier
				.fillMaxWidth()
				.height(60.dp)
				.padding(horizontal = 6.dp, vertical = 4.dp)
		) {
			val items = listOf(
				NavigationItem("Home", R.drawable.ic_notification, mainRoute),
				NavigationItem("Metronomo", R.drawable.ic_notification, metronome),
   			NavigationItem("Estadisticas", R.drawable.ic_notification, stadisticsRoute),
				NavigationItem("Ajustes", R.drawable.ic_notification, settingsRoute)
			)
			
			items.forEach { item ->
				val selected = currentRoute == item.route
				
				NavigationBarItem(
					icon = {
						Column(
							horizontalAlignment = Alignment.CenterHorizontally,
							verticalArrangement = Arrangement.Center,
							modifier = Modifier.padding(vertical = 2.dp)
						) {
							// Contenedor del ícono
							Surface(
								modifier = Modifier.size(32.dp),
								shape = CircleShape,
								color = if (selected)
									colorScheme.onSurface.copy(alpha = 0.08f) // 👈 adaptado
								else
									Color.Transparent
							) {
								Icon(
									painter = painterResource(id = item.icon),
									contentDescription = item.label,
									modifier = Modifier
										.fillMaxSize()
										.padding(6.dp),
									tint = if (selected)
										colorScheme.onSurface // 👈 adaptado
									else
										colorScheme.onSurface.copy(alpha = 0.6f)
								)
							}
							
							// Etiqueta
							Text(
								text = item.label,
								style = MaterialTheme.typography.labelSmall.copy(
									fontWeight = if (selected)
										FontWeight.SemiBold
									else
										FontWeight.Normal,
									fontSize = 9.sp
								),
								color = if (selected)
									colorScheme.onSurface
								else
									colorScheme.onSurface.copy(alpha = 0.6f),
								maxLines = 1,
								overflow = TextOverflow.Ellipsis,
								modifier = Modifier.padding(top = 1.dp)
							)
						}
					},
					label = null,
					selected = selected,
					onClick = {
						navigateIfNeeded(navController, item.route, currentRoute)
					},
					alwaysShowLabel = false,
					colors = NavigationBarItemDefaults.colors(
						selectedIconColor = Color.Transparent,
						selectedTextColor = Color.Transparent,
						unselectedIconColor = Color.Transparent,
						unselectedTextColor = Color.Transparent,
						indicatorColor = Color.Transparent
					),
					modifier = Modifier.weight(1f)
				)
			}
		}
	}
}


// Data class para mayor organización (sin cambios)
data class NavigationItem(
	val label: String,
	val icon: Int,
	val route: String
)

fun navigateIfNeeded(
	navController: NavController,
	destination: String,
	currentRoute: String?
) {
	if (currentRoute != destination) {
		navController.navigate(destination) {
			launchSingleTop = true
			restoreState = true
			popUpTo(navController.graph.findStartDestination().id) {
				saveState = true
			}
		}
	}
}