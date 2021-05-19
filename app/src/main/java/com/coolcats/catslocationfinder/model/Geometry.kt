package com.coolcats.catslocationfinder.model

data class Geometry(
    val bounds: Bounds,
    val location: Loc,
    val location_type: String,
    val viewport: Viewport
)