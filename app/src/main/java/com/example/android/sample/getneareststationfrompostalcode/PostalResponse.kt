package com.example.android.sample.getneareststationfrompostalcode

data class PostalResponse(
    val response: Response
)

data class Response(
    val station: List<Station>
)

data class Station (
    val name : String,
    val kana: String,
    val line : String,
    val y: String,
    val x: String,
    val postal: String,
    val prev : String,
    val next: String,
    val prefecture: String,
    val distance: String
)