package tests

import Comment
import Post
import helpers.BaseTest
import helpers.Endpoints.COMMENTS
import helpers.Endpoints.POSTS
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestApi : BaseTest() {
    // All posts
    @Test
    fun getPostsListTest() {
        given()
        .`when`()
            .get(POSTS.ulr)
        .then()
            .statusCode(200)
            .body("$.size()", equalTo(100))
            // TODO: additional checks - list contains something
    }

    // post with id = 40
    @ParameterizedTest
    @ValueSource(ints = [40])
    fun getPostWithId(id: Int) {
        given()
        .`when`()
            .get(POSTS.ulr + "/${id}")
        .then()
            .statusCode(200)
            // .body("$.size()", equalTo(1))
            .body("userId", equalTo(4))
            .body("id", equalTo(id))
            .body("title", equalTo("enim quo cumque"))
            .body("body", equalTo("ut voluptatum aliquid illo tenetur nemo sequi quo facilis\n" +
                    "ipsum rem optio mollitia quas\n" +
                    "voluptatem eum voluptas qui\n" +
                    "unde omnis voluptatem iure quasi maxime voluptas nam"))
    }

    // comment with id = 17 of post with id = 4
    @ParameterizedTest
    @MethodSource("commentsProvider")
    fun getCommentOfPost(expectedComment: Comment) {
        val comment =
            given()
            .`when`()
                .get(COMMENTS.ulr + "?postId=${expectedComment.postId}")
            .then()
                .statusCode(200)
                // .body("$.size()", equalTo(5))
            .extract()
                .body()
                .jsonPath()
                .getList(".", Comment::class.java)
                .filter { comment -> comment.id == expectedComment.id }
                .first()
        assertEquals(expectedComment, comment)
    }

    // post with id = 42 and userId = 5
    @ParameterizedTest
    @MethodSource("postsProvider")
    fun getPostOfUser(expectedPost: Post) {
        val actualPost = given()
            .`when`()
                .get(POSTS.ulr + "?userId=${expectedPost.userId}")
            .then()
                .statusCode(200)
            .extract()
                .body()
                .jsonPath()
                .getList(".", Post::class.java)
                // TODO: How about check there is no more than 1 post with same id?
                .filter { post -> post.id == expectedPost.id }
                .first()
        assertEquals(expectedPost, actualPost)
    }

    @Test
    fun postCreateNewPost() {
        val newPost = Post(userId = 1, title = "New super post", body = "So cool, much post, very wow")
        given()
            .contentType("application/json")
            .body(newPost)
            .log().all()
        .`when`()
            .post(POSTS.ulr)
        .then()
            .statusCode(201) // Created
            // TODO: map to Post and compare objects, need to validate only id directly
            .body("userId", equalTo(newPost.userId))
            .body("title", equalTo(newPost.title))
            .body("body", equalTo(newPost.body))
            .body("id", notNullValue())
            .log().all()
        // TODO: IRL here should be additional check that resource have been really created
    }

    // delete posts of user with id = 938
    @ParameterizedTest
    @ValueSource(ints = [938])
    fun deletePostsOfUser(userId: Int) {
        val posts = given()
            .`when`()
                .get(POSTS.ulr + "?userId=${userId}")
            .then()
                .statusCode(200)
            .extract()
                .response()
                .path<List<Int>>("id")
        posts.forEach {
            given()
            .`when`()
                .delete(POSTS.ulr + "/" + it)
            .then()
                .statusCode(200)
        }
    }

    // provider of comments data
    fun commentsProvider(): Stream<Comment> = Stream.of(
        Comment(
            postId = 4,
            id = 17,
            name = "eos est animi quis",
            email = "Preston_Hudson@blaise.tv",
            body = "consequatur necessitatibus totam sed sit dolorum\n" +
                    "recusandae quae odio excepturi voluptatum harum voluptas\n" +
                    "quisquam sit ad eveniet delectus\n" +
                    "doloribus odio qui non labore"
        )
    )

    // provider of posts data
    fun postsProvider(): Stream<Post> = Stream.of(
        Post(
            userId = 5,
            id = 42,
            title = "commodi ullam sint et excepturi error explicabo praesentium voluptas",
            body = "odio fugit voluptatum ducimus earum autem est incidunt voluptatem\n" +
                    "odit reiciendis aliquam sunt sequi nulla dolorem\n" +
                    "non facere repellendus voluptates quia\n" +
                    "ratione harum vitae ut"
        )
    )
}
