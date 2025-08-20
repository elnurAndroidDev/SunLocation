# ☀️ Sun Position App

An Android app that calculates the Sun’s position (azimuth & altitude) based on your location or custom input.  
Built with modern Android technologies and clean architecture principles.

---

## 📱 Screenshots
<p align="center">
  <img src="screenshots/main.png" alt="Main screen" width="30%" />
  <img src="screenshots/calculate.png" alt="Calculate screen" width="30%" />
</p>

## ✨ Features

- **Main Screen**
  - Detects your current location in real time
  - Calculates and displays the Sun’s **azimuth** and **altitude**
  - Shows your **city, temperature, and weather condition**
  - Shows compass

- **Calculate Screen**
  - Enter custom data (location, date, time)
  - Get the Sun’s position for that input

---

## 🛠 Tech Stack

- [Kotlin](https://kotlinlang.org/) & [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)  
- [Jetpack Compose](https://developer.android.com/jetpack/compose) for UI  
- [Retrofit](https://square.github.io/retrofit/) for networking  
- [Google Play Services Location](https://developers.google.com/android/reference/com/google/android/gms/location/package-summary) for GPS  
- [Hilt](https://dagger.dev/hilt/) for dependency injection  
- **Clean Architecture** (multi-module setup)

---
