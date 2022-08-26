package pt.goncalo.playground.springbootgraphql.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.context.WebApplicationContext
import pt.goncalo.playground.springbootgraphql.repository.entity.Category
import pt.goncalo.playground.springbootgraphql.repository.entity.Quiz
import kotlin.test.assertEquals


@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
class QuizTests @Autowired constructor(private val webApplicationContext: WebApplicationContext) {
    @Test
    fun `it should have consistent results between list and getById`() {
        with(GraphQlTestHelper()) {
            val tester = httpGraphqlTester(webApplicationContext)
            val getAllQuizDocument = """ 
            query getAllQuiz{
              quiz{
                quizId,title,description,categories{
                  categoryId,name
                }
              }
            }
            """
            val getAllResult = tester
                .document(getAllQuizDocument)
                .execute()

            val firstQuiz = getAllResult
                .path("quiz")
                .entityList(Quiz::class.java)
                .get().get(0)
            val firstQuizCategories = getAllResult
                .path("quiz[*].categories[*]")
                .entityList(Category::class.java)
                .get()

            val getQuizByIdDocument = """
            query getById{
              quizFindById(id: "${firstQuiz.quizId}") {
                quizId,title,description,categories{
                  categoryId,name
                }
              }
            }
            """.trimIndent()
            val getByIdResponse = tester
                .document(getQuizByIdDocument)
                .execute()

            val getByIdQuiz = getByIdResponse
                .path("quizFindById")
                .entity(Quiz::class.java)
                .get()

            val getByIdCategories = getByIdResponse
                .path("quizFindById.categories[*]")
                .entityList(Category::class.java)
                .get()


            assertEquals(firstQuiz.quizId, getByIdQuiz.quizId)
            println("firstQuizCategories $firstQuizCategories")
            println("getByIdCategory $getByIdCategories")

            firstQuizCategories.let {
                assertThat(getByIdCategories equalsIgnoreOrder it).isTrue
            }


        }
    }

    @Test
    fun `it should have consistent results between queries and subscription`() {

    }

}
