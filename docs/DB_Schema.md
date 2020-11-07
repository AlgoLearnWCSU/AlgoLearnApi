# AlgoLearn Database Schemata

## Conceptual Schema

![AlgoLearn Conceptual Schema](./img/CS360_Database_Design_Transparent.png)

## Logical Schema

**User**(
<ins>Username</ins>,
Name*,
Email*,
isAdmin*
)


**Problem**(
<ins>ProblemID</ins>,
Name*,
Poster*,
IsReviewed*,
Description*
)

- Poster references User

**Parameter**(
    <ins>Problem</ins>,
    <ins>Name</ins>
)

- Problem References Problem

**Test Case**(
    <ins>TCID</ins>,
    Problem*,
    IsPublic*,
    SampleInput*,
    ExpectedOutput*
)

- Problem References Problem

**Solution**(
    <ins>SolutionID*</ins>,
    Solver*,
    Problem*,
    Code*,
    PassedTests*,
    CompTime*
    TotalTests*
)

- Solver References User
- Problem References Problem

**Comment**(
    <ins>CommentID</ins>
    Commenter*,
    Problem*,
    Timestamp*,
    Comment*
)

- Commenter References User
- Problem References Problem
