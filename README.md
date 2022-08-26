# Spring boot graphql + spring data

## Goals

1. Understand how query, mutations and subscriptions work with spring-web ( not reactive! )
2. Understand how to use spring-data mapping with graphql and the pros and cons of such

## Conclusions

1. Goal 1
   It is clear how to use @QueryMapping, @MutationMapping, @BatchMapping alongside with @Argument inside of spring web
   controllers.
   One downside was the verbose nature of graphqls, but maybe it is an advantage also.

On the mutation part, it is interesting to see that even if the object return from a mutation is "detached",
the framework is clever enough to know how to fetch everything the client requested.
Example:
Call addQuiz mutation and ask for categories and questions

2. TODO

## References

[Paylist - Josh long spring for graphql](https://www.youtube.com/playlist?list=PLgGXSWYM2FpNRPDQnAGfAHxMl3zUG2Run)
[Article - integration tests](https://itnext.io/graphql-api-integration-tests-in-a-spring-boot-2-x-kotlin-application-5840d3c5d66f)
[Docs - Graphql official](https://www.howtographql.com/basics/2-core-concepts/)
[Docs - spring-graphql](https://docs.spring.io/spring-graphql/docs/current/reference/html/)
[Article - Angular client, subscriptions](https://apollo-angular.com/docs/data/subscriptions/#:~:text=In%20addition%20to%20fetching%20data,time%20messages%20from%20the%20server.)

### Batching Requests

Since mappings between objects dont always exists, in graphql we could have a n+1 number
of network requests in a entity that is linked to other.
To resolve such scenario, one can batch requests.
DataLoader is the project that deals with this ( it has cache )
@BatchMapping | Syntactic sugar over DataLoader API

[Usage](https://youtu.be/a1vZcSaicmY?list=PLgGXSWYM2FpNRPDQnAGfAHxMl3zUG2Run&t=680)

### Mutations

@Valid works with @Arguments

### client side

[Angular example](https://developer.okta.com/blog/2021/10/22/angular-graphql)




