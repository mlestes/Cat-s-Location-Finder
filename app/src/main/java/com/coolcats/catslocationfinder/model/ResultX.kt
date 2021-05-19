package com.coolcats.catslocationfinder.model

data class ResultX(
    val business_status: String,
    val geometry: GeometryX,
    val icon: String,
    val name: String,
    val opening_hours: OpeningHours,
    val photos: List<Photo>,
    val place_id: String,
    val plus_code: PlusCodeXX,
    val price_level: Int,
    val rating: Double,
    val reference: String,
    val scope: String,
    val types: List<String>,
    val user_ratings_total: Int,
    val vicinity: String
)