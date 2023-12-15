package data

data class UserProfile(
    val displayName: String,
    var experience: Int = 0,
    var level: Int = 1
)
{
    // Default (no-argument) constructor
    constructor() : this("", 0, 1)
}
