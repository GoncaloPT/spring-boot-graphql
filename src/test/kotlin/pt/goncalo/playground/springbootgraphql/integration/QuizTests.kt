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
import pt.goncalo.playground.springbootgraphql.repository.entity.Company
import pt.goncalo.playground.springbootgraphql.repository.entity.Quiz
import pt.goncalo.playground.springbootgraphql.repository.entity.QuizType
import kotlin.test.assertEquals


@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
class QuizTests @Autowired constructor(private val webApplicationContext: WebApplicationContext) {
    /**
     * Requisite: It expects that companies, quizzes and quiz types are already created by startup script
     */
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
                .path("quiz[0]")
                .entity(Quiz::class.java)
                .get()
            val firstQuizCategories = getAllResult
                .path("quiz[0].categories[*]")
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

    /**
     * Requisite: It expects that companies and quiz type are already created by startup script
     */
    @Test
    fun `it should return expected element after mutation`() {
        val company = anyCompany()
        val quizType = anyQuizType()
        val title = "new from test"
        val description = "random one"

        val createQuizDocument = """
            mutation createQuiz{
              addQuiz( quiz: {
                companyID: "${company.companyID}"
                title: "$title"
                description: "$description"
                quizTypeId: "${quizType.quizTypeId}"
                
              }){
                quizId, company{ companyID },
                description,
                title
              }
            }
        """
        with(GraphQlTestHelper()) {
            val tester = httpGraphqlTester(webApplicationContext)
            val mutationResult = tester
                .document(createQuizDocument)
                .execute()


            val createdQuiz = mutationResult
                .path("addQuiz")
                .hasValue()
                .entity(Quiz::class.java).matches {
                    it.company.companyID == company.companyID
                    it.title == title
                    it.description == description
                }

        }
    }


    /**
     * Requisite: It expects that companies and quiz type are already created by startup script
     * Also, it expects a subscription endpoint that produces values when a new subscription is made,
     * those values are just db values feed to the sink multiple times.
     * The idea is to simulate an event drive system.
     * This is mainly useful for graphiql / presentation, since otherwise we would have to do some mutation and
     * have 2 tabs open to see this happing.
     *
     * Of course, other way of doing this would be to get a sink instance from {@see QuizStreamController#getSink}
     * and feed the values there.
     *
     *
     */
    @Test
    fun `it should have consistent results between queries and subscription`() {
        val onNewQuizSubscriptionDocument = """
           subscription onNewQuiz{
              onQuizChange{
                quizId,title,description,
                quizType{
                  quizTypeId
                },
                company{
                   companyID
                },
                categories{
                  categoryId
                }
              }
            } 
        """
        val getQuiz = """
           query quizFindById{
            quizFindById(id: "%s" ){
               quizId,company{
                 companyID
               },categories {
                 categoryId
               },description
            }
           }
        """
        with(GraphQlTestHelper()) {
            val wsTester = websocketGraphqlTester()
            val tester = httpGraphqlTester(webApplicationContext)


            val wsQuizResponse = wsTester
                .document(onNewQuizSubscriptionDocument)
                .executeSubscription()
                .toFlux()
                .blockFirst()

            val wsQuiz = wsQuizResponse
                .path("onQuizChange")
                .entity(Quiz::class.java)
                .get()


            val quizQueryResponse = tester
                .document(String.format(getQuiz, wsQuiz.quizId))
                .execute()
            quizQueryResponse
                .path("quizFindById")
                .entity(Quiz::class.java)
                .matches {
                    it.quizId == wsQuiz.quizId
                    it.type == wsQuiz.type
                    it.title == wsQuiz.title
                    it.description == wsQuiz.description
                    it.company == wsQuiz.company
                }.get()

            var wsCategories = wsQuizResponse
                .path("onQuizChange.categories[*]")
                .entityList(Category::class.java)
                .get()
            var queryCategories = quizQueryResponse
                .path("quizFindById.categories[*]")
                .entityList(Category::class.java)
                .get()

            println("wsCategories are $wsCategories")
            println("queryCategories are $queryCategories")
            assertEquals(queryCategories.size, wsCategories.size, "fetched quiz categories must be the same in subscription and query")
            assertThat(queryCategories equalsIgnoreOrder wsCategories)


        }
    }

    private fun anyQuizType(): QuizType {
        val findQuizTypesDocument = """
            query getTypes{
              quizTypes{
                quizTypeId,name
              }
            }
        """
        with(GraphQlTestHelper()) {
            val tester = httpGraphqlTester(webApplicationContext)
            val getAllResult = tester
                .document(findQuizTypesDocument)
                .execute()
            // no errors expected
            getAllResult.errors().verify()
            return getAllResult.path("quizTypes")
                .entityList(QuizType::class.java)
                .get().first()
        }
    }

    private fun getCompanies(): List<Company> {
        val findCompaniesDocument = """
            query getCompanies{
                companies{companyID,name}  
            }
        """
        with(GraphQlTestHelper()) {
            val tester = httpGraphqlTester(webApplicationContext)
            val getAllResult = tester
                .document(findCompaniesDocument)
                .execute()
            // no errors expected
            getAllResult.errors().verify()
            return getAllResult.path("companies")
                .entityList(Company::class.java)
                .get()
        }

    }

    private fun anyCompany(): Company {
        val findCompaniesDocument = """
            query getCompanies{
                companies{companyID}  
            }
        """
        with(GraphQlTestHelper()) {
            val tester = httpGraphqlTester(webApplicationContext)
            val getAllResult = tester
                .document(findCompaniesDocument)
                .execute()
            // no errors expected
            getAllResult.errors().verify()
            return getAllResult.path("companies")
                .entityList(Company::class.java)
                .get().first()
        }

    }

}
