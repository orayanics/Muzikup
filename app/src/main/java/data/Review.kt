package data

data class Review(val reviewId: String,
                  val track: String,
                  val artist: String,
                  var content: String,
                  val likes: Int,
                  val isLiked: MutableMap<String, Boolean> = mutableMapOf(),
                  val username: String)