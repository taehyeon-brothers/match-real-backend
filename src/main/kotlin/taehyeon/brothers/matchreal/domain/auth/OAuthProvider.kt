package taehyeon.brothers.matchreal.domain.auth

enum class OAuthProvider {
    GOOGLE;

    companion object {
        fun from(name: String): OAuthProvider =
            entries.find { it.name.equals(name, ignoreCase = true) }
                ?: throw IllegalArgumentException("Invalid OAuth provider: $name")
    }
} 