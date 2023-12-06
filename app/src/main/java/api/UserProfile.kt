package api

data class UserProfile(
    val id: String,
    val email: String,
    val images: List<UserImage>?
)

data class UserImage(
    val url: String,
)