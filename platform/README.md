# platformapi_impl module

The module contains the **Android / AAOS implementation** of `PlatformApi`.

- `VehiclePlatform` – reads data from the car and from the integrator provider, and exposes it through `PlatformApi`.

## Why use a separate implementation module?

I chose to keep this implementation in its own `platformapi_impl` module for a few reasons:

1. **Separation of concerns**

    - `PlatformApi` (in another module) is a clean, Android-free interface.
    - This module contains the **Android / AAOS-specific** code.
    - Other modules only depend on the `PlatformApi` interface, not on Android car classes.

2. **Easier testing**

    - Business logic and feature modules can be tested using a **fake implementation** of `PlatformApi` (pure Kotlin, no Android).
    - Only this module needs instrumented tests or a device/car environment, testing of this module can be done in isolated build or pipeline.

3. **Flexible data source strategy**

    - All logic of “use provider first, fall back to AAOS / Android if provider has nothing” lives here in one place.
    - The rest of the app just consumes `PlatformApi` flows and does not need to know where the data came from.

---
## other alternatives

There are few other alternatives to structure this, but I did not choose them here:

1. **Put `VehiclePlatform` inside the `app` module**

    - *Pros:* fewer Gradle modules, simpler setup.
    - *Cons:* Android / AAOS code would sit next to UI code, and it becomes harder to reuse or swap the platform implementation in another project.

2. **Have one combined `platform` module with both API and implementation**

    - *Pros:* slightly simpler module layout.
    - *Cons:* mixes the clean interface (`PlatformApi`) with Android-specific details in the same place.

3. **Call `PlatformProviderApi` directly from the app or feature modules**

    - *Pros:* one fewer layer between provider and UI.
    - *Cons:* the “provider vs AAOS fallback” decision would leak into screens or view models.  
      That would make the app more coupled to integration details and harder to maintain.
---







