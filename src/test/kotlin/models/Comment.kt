import com.fasterxml.jackson.annotation.JsonProperty

data class Comment(
    @JsonProperty("postId")
    val postId: Int,
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("email")
    val email: String,
    @JsonProperty("body")
    val body: String
)
