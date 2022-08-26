# Spring boot graphql + spring data

POC on spring-boot-graphql.  
The main goal is to try out the current implementation status and evaluate if this could be used in production.  
Concepts asserted:

- Easy of use
- Code readbility
- Learning curve
- Amount of written code ( lots of config classes?) vs work done
- Customization possibility
- Error Handling

## Good to know

To find your way around in this project.

### Run
`mvn spring-boot:run` the project  
`mvn integration-test` for integration tests

### Query examples
See docs/example-documents.md

### Tests
This project has integration tests.  
They can be found at test/kotlin folder.

The Tests will make sure the API has consistent outputs:
- A random listAll element can be searched byId and the values will match
- A value obtain on a subscription and one obtained in a query ( using same document ) are consistent
- After a mutation, query returns expected ( updated ) value

## Presentation

### Graphql overview

TODO

### Data binding system on spring-boot

TODO

### Query Mappings

TODO
Document examples can be found in docs/example-documents.md

### Batching Requests

Since mappings between objects dont always exists, in graphql we could have a n+1 number
of network requests in a entity that is linked to other.
To resolve such scenario, one can batch requests.
DataLoader is the project that deals with this ( it has cache )
@BatchMapping | Syntactic sugar over DataLoader API

[Usage](https://youtu.be/a1vZcSaicmY?list=PLgGXSWYM2FpNRPDQnAGfAHxMl3zUG2Run&t=680)

### Mutations

@Valid works with @Arguments

### Data Integration - Querydsl

spring-boot-graphql has a very powerful data binding.
It leverages existing spring-data and binds it automatically to graphql predefined(manual) queries.
This allows us to expose an entity, and queries for each field ( and combination of fields) just by annotating a
JpaRepository and QuerydslPredicateExecutor with GraphQlRepository.

#### Details

Querydsl code generation will generate Some EntityBasePath classes that will enable dynamic queries.
To use them we just have to declare a query that receives as arguments the field(s) we want to search for

### client side

[Angular example](https://developer.okta.com/blog/2021/10/22/angular-graphql)

## References

[Paylist - Josh long spring for graphql](https://www.youtube.com/playlist?list=PLgGXSWYM2FpNRPDQnAGfAHxMl3zUG2Run)
[Article - integration tests](https://itnext.io/graphql-api-integration-tests-in-a-spring-boot-2-x-kotlin-application-5840d3c5d66f)
[Docs - Graphql official](https://www.howtographql.com/basics/2-core-concepts/)
[Docs - spring-graphql](https://docs.spring.io/spring-graphql/docs/current/reference/html/)
[Article - Angular client, subscriptions](https://apollo-angular.com/docs/data/subscriptions/#:~:text=In%20addition%20to%20fetching%20data,time%20messages%20from%20the%20server.)

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
   The project was setup with Querydsl since it seems to easiest way of working with @GraphqlRepository 'dark magic'.
   More on #




