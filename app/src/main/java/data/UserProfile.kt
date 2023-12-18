package data

data class UserProfile(
    val displayName: String,
    var userExp: Int = 0,
    var userLevel: Int = 1,
    var totalLikes: Int = 0,
    var userReview: Int = 0
)
{
    // Default (no-argument) constructor
    constructor() : this("", 0, 1)

    companion object {
        fun fromMap(map: Map<String, Any>): UserProfile {
            return UserProfile(
                map["displayName"] as String,
                (map["userExp"] as Long).toInt(),
                (map["userLevel"] as Long).toInt(),
                (map["totalLikes"] as Long).toInt(),
                (map["userReview"] as Long).toInt()
            )
        }
    }
}
