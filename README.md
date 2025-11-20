# InfyAssignment – Nearby POIs Based on Car Location

This assesment was bascially is an AAOS-style, with clean architecture and MVVM based Android app
that shows **nearby Points of Interest (POIs)** such as restaurants, charging stations, gas stations, 
supermarkets, and parking spots based on the **location/type/model of the vehicle**.

The design focuses on:

- Separating **platform access**, **algorithm logic**, and **app/UI**.
- Allowing a **neighbouring team** to implement their own filtering algorithms per **vehicle model**.
- Storing POIs **only while the app is running**, so the user can view session history.
- Ensuring the app is **only started explicitly by the user** (no auto-start).

### Module-wise interaction diagram

High-level module interaction for the POI flow:

User  
|  
v  
`app` module  
(MainActivity → PoisScreen → PoisViewModel → UseCases → Repository → PoisManager)  
|                         \  
|                          \ uses `NearbyPoisApi`  
|                           \  
|                            v  
|                      `nearbypois` module  
|                      (NearbyPoisApi, NearbyPois)  
|  
| uses `PlatformApi`  
v  
`platform` module  
(PlatformApi, VehiclePlatform)


Shared models (used by ALL modules):

            +--------------------+
            |   shared_model     |
            | (Poi, Location,    |
            |  VehicleInfo,      |
            |  PoiCategory, etc) |
            +--------------------+
              ^          ^         ^
              |          |         |
        used by    used by   used by
          app     nearbypois platform


## Functional Requirements

The requirement mapping, the app supports:

1. **Show POIs based on car location**
    - `PlatformApi.observeCurrentLocation()` → `PoisManager` → `NearbyPoisApi` → `currentPoisFlow` → UI.

2. **Support filtering algorithms on raw location data**
    - `NearbyPoisApi` with `rawLocationList` and category.
    - `NearbyPois` is prepared for advanced algorithms.

3. **Allow different algorithms per car model**
    - `PlatformApi.observeVehicleInfo()`
    - `NearbyPois` tracks `currentVehicleType` / `currentEngineType` and can branch behaviour.

4. **Provide POI history within the session**
    - `HistoricalPoisStorage` + `PoisManager.savePoisToHistory()` + `GetHistoricalPoisUseCase`.

5. **Run only when explicitly started**
    - No auto-start components, single `MainActivity`

## Non-Functional Requirements

1. **Modularity**
    - Clear separation: `app`, `nearbypois`, `platform`, `shared_model`.

2. **Extensibility**
    - New categories → `PoiCategory`.
    - New algorithms → new `NearbyPoisApi` implementations.
    - Real storage → implement `HistoricalPoisDatabaseStorage`.

3. **Testability**
    - `PlatformApi`, `NearbyPoisApi`, `HistoricalPoisStorage`, `NearbyPoisRepository` are interfaces → easy to mock.
    - `PoisManager` and `PoisViewModel` can be tested with fake dependencies.

4. **Performance**
    - Uses `Flow`, `StateFlow`, and `Dispatchers.IO`.
    - Location updates are sampled every 5 seconds to avoid overloading, other logics can also be implemented.

5. **Maintainability**
    - Layers: UI → Use Cases → Repository → Manager → Platform/Algorithm.
    - Hilt DI centralizes wiring.
    - Shared models are in a dedicated module.
    - 
- ## Assumptions

- The app runs in an **AAOS-like environment** where `android.car` APIs are available.
- **Location permission** (`ACCESS_FINE_LOCATION`) is granted, otherwise `observeCurrentLocation()` silently emits nothing.
- The **algorithm team** will implement the real logic in `NearbyPois`.
- The datastorage implementation is not required, so have created the stub to connect to any db schema.
- The POIs to be shown will not be more than 500 items in the list, if a huge list to be shown then data chunking/pagination is required.
- The algorithm will consider vehicle type, engine type, vehicle model for the algorithm.
- UI implementation is not strict for the assignment.
- Currently [platform, nearbypois]api and its implementation is present in same module, but in production it should be kept separate.
- Using of remote configuration to switch algorithms per model or region is not required.
- Huge dataset is not considered and Caching and paging is not required.

## Design Choices

- **Interfaces for boundaries**: `PlatformApi`, `NearbyPoisApi`, `HistoricalPoisStorage`, `NearbyPoisRepository`.
- **Coroutines + Flow** for async streams from platform to UI.
- **Sampling** location updates to balance responsiveness and load.
- **Hilt** for DI:
    - Cleaner construction.
    - Easier swapping of implementations.
- **Shared model module** to avoid duplication and cross-module confusion.
- **Use cases** to define domain operations explicitly.

## Benefits

- Algorithms can evolve separately from the app.
- Platform/AAOS dependencies are isolated behind `PlatformApi`.
- Easy to test with mock implementations (no real car hardware needed).

## Scaling Possibilties

If the app grows:

- Multiple algorithms per OEM/model with dedicated `NearbyPoisApi` implementations.
- More data from platform (speed, heading, fuel/battery).
- More screens and navigation with multiple ViewModels.
- Caching and paging for large history datasets.
- Potential move of core POI logic into a dedicated service module.
