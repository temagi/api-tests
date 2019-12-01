package tests

import Comment
import Post
import helpers.Endpoints.COMMENTS
import helpers.Endpoints.POSTS
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.parsing.Parser
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestApi {
    val url = "https://jsonplaceholder.typicode.com"

    // All posts
    @Test
    fun getPostsListTest() {
        RestAssured.defaultParser = Parser.JSON
        given().
        `when`()
            .get(url + POSTS.ulr)
        .then()
            .statusCode(200)
            .body("$.size()", equalTo(100))
            // TODO: additional checks - list contains something
    }

    // post with id = 40
    // TODO: Make test parametrized, here and below
    @Test
    fun getPostWithId() {
        RestAssured.defaultParser = Parser.JSON
        given()
        .`when`()
            .get(url + POSTS.ulr + "/40")
        .then()
            .statusCode(200)
            //.body("$.size()", equalTo(1))
            .body("userId", equalTo(4))
            .body("id", equalTo(40))
            .body("title", equalTo("enim quo cumque"))
            .body("body", equalTo("ut voluptatum aliquid illo tenetur nemo sequi quo facilis\n" +
                    "ipsum rem optio mollitia quas\n" +
                    "voluptatem eum voluptas qui\n" +
                    "unde omnis voluptatem iure quasi maxime voluptas nam"))
    }

    // comment with id = 17 of post with id = 4
    @Test
    fun getCommentOfPost() {
        RestAssured.defaultParser = Parser.JSON
        val expectedComment = Comment(
            4,
            17,
            "eos est animi quis",
            "Preston_Hudson@blaise.tv",
            "consequatur necessitatibus totam sed sit dolorum\n" +
                    "recusandae quae odio excepturi voluptatum harum voluptas\n" +
                    "quisquam sit ad eveniet delectus\n" +
                    "doloribus odio qui non labore"
        )
        val comment =
            given()
            .`when`()
                .get(url + COMMENTS.ulr + "?postId=4")
            .then()
                .statusCode(200)
                //.body("$.size()", equalTo(5))
            .extract()
                .body()
                .jsonPath()
                .getList(".", Comment::class.java)
                .filter { comment -> comment.id == 17 }
                .first()
        assertEquals(expectedComment, comment)
    }

    // post with id = 42 and userId = 5
    @Test
    fun getPostOfUser() {
        RestAssured.defaultParser = Parser.JSON
        val expectedPost = Post(
            5,
            42,
            "commodi ullam sint et excepturi error explicabo praesentium voluptas",
            "odio fugit voluptatum ducimus earum autem est incidunt voluptatem\n" +
                    "odit reiciendis aliquam sunt sequi nulla dolorem\n" +
                    "non facere repellendus voluptates quia\n" +
                    "ratione harum vitae ut"
        )
        val actualPost = given()
            .`when`()
                .get(url + POSTS.ulr + "?userId=5")
            .then()
                .statusCode(200)
            .extract()
                .body()
                .jsonPath()
                .getList(".", Post::class.java)
                // TODO: How about check there is no more than 1 post with same id?
                .filter { post -> post.id == 42 }
                .first()
        assertEquals(expectedPost, actualPost)
    }

    @Test
    fun postCreateNewPost() {
        RestAssured.defaultParser = Parser.JSON
        val newPost = Post(userId = 1, title = "New super post", body = "So cool, much post, very wow")
        given()
            .contentType("application/json")
            .body(newPost)
            .log().all()
        .`when`()
            .post(url + POSTS.ulr)
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
    @Test
    fun deletePostsOfUser() {
        RestAssured.defaultParser = Parser.JSON
        val posts = given()
            .`when`()
                .get(url + POSTS.ulr + "?userId=938")
            .then()
                .statusCode(200)
            .extract()
                .response()
                .path<List<Int>>("id")
        posts.forEach {
            given()
            .`when`()
                .delete(url + POSTS.ulr + "/" + it)
            .then()
                .statusCode(200)
        }
    }
}
