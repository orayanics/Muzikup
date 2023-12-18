package data

data class UserProfile(
    val displayName: String,
    var experience: Int = 0,
    var level: Int = 1,
    var userExp: Int = 0,
    var userLevel: Int = 1,
    var totalLikes: Int = 0,
    var userReview: Int = 0
)
{
    // Default (no-argument) constructor
    constructor() : this("", 0, 1)
}
