package com.example.myapplication1

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


// ViewModel для получения местоположения
class LocationViewModel : ViewModel() {
    private val _locationData = MutableStateFlow<Location?>(null)
    val locationData: StateFlow<Location?> = _locationData

    fun getLocation() {
        // Замените на логику получения реального местоположения
        _locationData.value = Location(55.7558, 37.6176) // Пример координат для Москвы
    }
}

data class Location(val latitude: Double, val longitude: Double)

@Composable
fun MainScreen(modifier: Modifier = Modifier, viewModel: LocationViewModel = viewModel()) {
    val context = LocalContext.current
    val locationData by viewModel.locationData.collectAsState()
    val locationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

    // Функция для отображения сообщений о статусе разрешений
    fun showPermissionMessage() {
        if (!locationPermissionState.) {
            Toast.makeText(context, "Необходимо разрешение для определения местоположения", Toast.LENGTH_LONG).show()
        }
    }

    // Проверка на разрешение при необходимости
    LaunchedEffect(locationPermissionState.hasPermission) {
        if (!locationPermissionState.hasPermission) {
            showPermissionMessage()
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Отображение координат или сообщения о недоступности местоположения
        locationData?.let { location ->
            val latitude = location.latitude
            val longitude = location.longitude
            Text("Координаты: широта $latitude, долгота $longitude")

            // Создание состояния камеры для отображения карты
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(
                    LatLng(latitude, longitude), 15f
                )
            }

            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = locationPermissionState.hasPermission)
            ) {
                Marker(
                    position = LatLng(latitude, longitude),
                    title = "Вы здесь"
                )
            }
        } ?: run {
            Text("Местоположение не определено")
        }

        Spacer(Modifier.height(10.dp))

        // Кнопка для запроса местоположения
        Button(onClick = {
            if (locationPermissionState.hasPermiss
                ion) {
                viewModel.getLocation() // Получаем местоположение
            } else {
                locationPermissionState.launchPermissionRequest()
            }
        }) {
            Text("Получить местоположение")
        }
    }
}
