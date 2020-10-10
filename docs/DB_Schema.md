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
    <ins>Problem</ins>,
    IsPublic*,
    SampleInput*,
    ExpectedOutput*
)

**Solution**(
    <ins>Solver</ins>,
    <ins>Problem</ins>,
    PassedTests*,
    CompTime*
)

- Solver References User
- Problem References Problem

**Comment**(
    <ins>Commenter</ins>,
    <ins>Problem</ins>,
    <ins>Timestamp</ins>,
    Comment*
)

- Commenter References User
- Problem References Problem
