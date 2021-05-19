package com.coolcats.catslocationfinder.model

data class NearbyResult(
    val html_attributions: List<Any>,
    val next_page_token: String,
    val results: List<ResultX>,
    val status: String
)