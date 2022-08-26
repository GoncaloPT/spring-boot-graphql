package pt.goncalo.playground.springbootgraphql.integration


import org.springframework.graphql.test.tester.HttpGraphQlTester
import org.springframework.graphql.test.tester.WebSocketGraphQlTester
import org.springframework.test.web.servlet.client.MockMvcWebTestClient
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import org.springframework.web.reactive.socket.client.WebSocketClient


class GraphQlTestHelper {
    fun httpGraphqlTester(context: WebApplicationContext): HttpGraphQlTester {
        val client = MockMvcWebTestClient.bindToApplicationContext(context)
            .configureClient()
            .baseUrl("/graphql")
            .build()

        return HttpGraphQlTester.create(client)
    }

    fun websocketGraphqlTester(): WebSocketGraphQlTester {
        val url = "http://localhost:8080/graphql"
        val client: WebSocketClient = ReactorNettyWebSocketClient()
        return WebSocketGraphQlTester.builder(url, client).build()
    }
    infix fun <T> List<T>.equalsIgnoreOrder(other: List<T>) = this.size == other.size && this.toSet() == other.toSet()


    interface DiffValue<T> {
        val old : T?
    }
}
