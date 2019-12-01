import com.fasterxml.jackson.annotation.JsonProperty

data class Post (
    @JsonProperty("userId")
    val userId: Int,
    @JsonProperty("id")
    val id: Int? = null,
    @JsonProperty("title")
    val title: String,
    @JsonProperty("body")
    val body: String
)
