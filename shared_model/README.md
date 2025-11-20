# shared_model module

The module contains the small set of data classes that are shared across the project.

It is a plain Kotlin module, with **no Android dependency**.  
It only knows about simple types like `String`, `Double`, `Int`, and `Long`.

---

## Why use a separate module for these models / why this approach?

I chose to keep these models in a separate `shared_model` module for a few reasons:

1. **Clear contract between modules**

   All feature modules (`nearbypois_api`, `platform_api`, `platform_impl`, the app) can talk to each other using these common types.  
   This keeps the boundaries simple and easy to understand: everyone agrees on the same `Poi`, `Coordinate`, and `VehicleInfo` shapes.

2. **Independent from Android**

   Because this module does not depend on Android, it is easy to:
    - reuse in tests (plain unit tests, not instrumented),
    - reuse in other modules (for example a future backend client),
    - reason about data without pulling in Android frameworks.

3**Lower coupling**

   The app module and feature modules only depend on this small shared module instead of depending on each other directly.  
   This makes it easier to:
    - swap implementations (for example a different `platform_impl` for another car),
    - add new modules later without changing existing ones.
---

## other alternatives

There are few other alternatives to structure this, but I did not choose them here:

1. **Keep all models inside the `app` module**

    - *Pros:* fewer Gradle modules, slightly simpler build.
    - *Cons:* platform and feature modules would then depend on `app`, which creates a messy dependency graph and makes reuse and testing harder.

2. **Put models inside each feature module**

   For example:
    - `Poi` and `PoiCategory` inside `nearbypois_api`,
    - `VehicleInfo` inside `platform_api`.

    - *Pros:* each module owns its own models.
    - *Cons:* if two modules need the same type, we either duplicate it or add mapping between similar classes, which adds boilerplate and noise for a small project.

3. **Single big “domain” module including models + use cases**

    - *Pros:* still cleaner than putting everything in `app`.
    - *Cons:* mixes pure models with higher-level logic, for this assignment I wanted the shared models to stay very small and obvious.

---