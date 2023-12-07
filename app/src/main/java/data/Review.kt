package data

data class Review(
    val reviewId: String? = null,
    val track: String? = null,
    val artist: String? = null,
    var content: String? = null,
    val likes: Int,
    val isLiked: MutableMap<String, Boolean> = mutableMapOf(),
    val username: String? = null)
{
    // Add a no-argument constructor
    constructor() : this("", "", "", "", 0, mutableMapOf(), "")
}