# Hotel Reservation App
## A00488636 - Anuja Dinuwara Gamage
An Android application for searching and booking hotel accommodations.

## Overview

The Hotel Reservation App is a modern Android application that allows users to:
- Search for available hotels by check-in and check-out dates
- View hotel details including pricing and availability
- Make hotel reservations for multiple guests
- Receive reservation confirmations

## Features

- **Hotel Search**: Search for available hotels for specific dates
- **Hotel Listings**: View available hotels with relevant details
- **Guest Management**: Add multiple guests with names and gender information
- **Reservation System**: Create hotel reservations with all guest details
- **Confirmation**: Receive and view reservation confirmation details

## Technology Stack

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **UI**: Android XML layouts with ViewBinding
- **Navigation**: Jetpack Navigation Component
- **Network**: Retrofit for API communication
- **Concurrency**: Kotlin Coroutines for asynchronous operations

## Project Structure

- **api/**: Contains Retrofit setup and API service definitions
- **model/**: Data classes for the application (Hotel, Guest, Reservation, etc.)
- **repository/**: Repository layer that handles data operations
- **adapter/**: RecyclerView adapters for displaying lists
- **view/**: UI components and custom views
- **fragments/**: UI screens implemented as fragments
  - SearchFragment: Hotel search interface
  - HotelListFragment: Displays available hotels
  - ReservationFragment: Handles guest details and reservation creation
  - ConfirmationFragment: Shows reservation confirmation

## Getting Started

### Prerequisites

- Android Studio Arctic Fox (2020.3.1) or higher
- Android SDK (minimum API level as specified in build.gradle)
- Kotlin 1.5.0 or higher

### Setup Instructions

1. Clone the repository:
   ```
   git clone https://github.com/AnujaSMU/HotelResAndriod.git
   ```

2. Open the project in Android Studio

3. Sync Gradle and build the project

4. Run the application on an emulator or physical device

## API Integration

The app integrates with a hotel reservation backend API with the following endpoints:

- GET `/available_hotels/`: Retrieves available hotels based on check-in and check-out dates
- POST `/reservation/`: Creates a new reservation with guest details

## Screenshots

[Add screenshots here]

## Future Enhancements

- User authentication and profiles
- Booking history
- Payment integration
- Filtering and sorting options for hotel search

