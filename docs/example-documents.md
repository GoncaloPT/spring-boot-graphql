# Example documents
In this document you will find documents that can serve as an example for interactions with this graphql API.

## Queries

### Company

```graphql
query allCompanies{
  companies{
    name,companyID
  }
}

# No Result expected ( wrong name )
query companyByName{
    companyByName(name: "my copany"){
        name,companyID
    }
}
# 1 Result expected
query companyByName{
    companyByName(name: "my company"){
        name,companyID
    }
}

query companyById{
    companyById(companyID: "b6a889eb-bb1d-4221-bda3-54fa776e845"){
        name,companyID
    }
}
```

### Quiz

### Others - temp
query companyFindByCompanyID{
findCompanyById(id: 1){
companyID,name
}
}

query getAllQuiz{
quiz{
quizId,title,description,company{
name
},categories{
categoryId,name
}
}
}

query getQuizById{
quizFindById(id: "9f33094a-9b14-4aec-807b-ff925a383096"){
quizId,title,description,categories{
categoryId,name
}
}
}
query getCompanyById{

quizByCompanyID(companyId: "72956c79-d0e9-494b-98c2-5e12f907abfe") {
    quizId,title,description,company{
    name
    }, categories{
    categoryId,name
    }
    }
}



subscription onChange{
onQuizChange{
quizId,title,description,categories{
categoryId,name
}
}
}



