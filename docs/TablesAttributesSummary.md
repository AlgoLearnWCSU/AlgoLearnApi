# Table and Attribute Summaries

## User

| Table Name | Primary Key | Foreign Key | Uniqueness Constraint |
| ---------- | ----------- | ----------- | --------------------- |
| User       | Username    | N/A         | Email                 |

<br>

| Name     | Type    | Range | Req / pt | Derive / Store |
| -------- | ------- | ----- | -------- | -------------- |
| Username | String  | N/A   | required | store          |
| Name     | String  | N/A   | required | store          |
| Email    | String  | N/A   | required | store          |
| IsAdmin  | Boolean | N/A   | required | store          |

<br>

## Problem

| Table Name | Primary Key | Foreign Key            | Uniqueness Constraint |
| ---------- | ----------- | ---------------------- | --------------------- |
| Problem    | ProblemID   | Poster references User | N/A                   |

<br>

| Name        | Type    | Range | Req / Opt | Derive / Store |
| ----------- | ------- | ----- | --------- | -------------- |
| ProblemID   | Int     | N/A   | Optional  | Store          |
| Name        | String  | N/A   | Required  | Store          |
| Poster      | String  | N/A   | Required  | Store          |
| IsReviewed  | Boolean | N/A   | Optional  | Store          |
| Description | Lob     | N/A   | Required  | Store          |

<br>

## Test Case

| Table Name | Primary Key | Foreign Key                | Uniqueness Constraint |
| ---------- | ----------- | -------------------------- | --------------------- |
| Test Case  | TestCaseId  | Problem references Problem | N/A                   |

<br>

| Name         | Type   | Range | Req / Opt | Derive / Store |
| ------------ | ------ | ----- | --------- | -------------- |
| TestCaseId   | Int    | N/A   | Optional  | Store          |
| Problem      | Int    | N/A   | Required  | Store          |
| SampleInput  | String | N/A   | Optional  | Store          |
| SampleOutput | String | N/A   | Required  | Store          |

<br>

## Solution

| Name        | Type   | Range | Req / Opt | Derive / Store |
| ----------- | ------ | ----- | --------- | -------------- | --- |
| SolutionId  | Int    | N/A   | Required  | Store          |
| Solver      | String | N/A   | Required  | Store          |
| Problem     | Int    | N/A   | Required  | Store          |
| PassedTests | Int    | N/A   | Optional  | Store          |
| CompTime    | Float  | N/A   | Optional  | Store          |
| Tokens      | String | N/A   | Optional  | Store          | s   |

<br>

## Comment

| Table Name | Primary Key | Foreign Key                                           | Uniqueness Constraint |
| ---------- | ----------- | ----------------------------------------------------- | --------------------- |
| Comment    | CommentId   | Commenter references User, Problem references Problem | N/A                   |

<br>

| Name      | Type          | Range | Req / Opt | Derive / Store |
| --------- | ------------- | ----- | --------- | -------------- |
| CommentId | Int           | N/A   | Required  | Store          |
| Commenter | String        | N/A   | Required  | Store          |
| Problem   | Int           | N/A   | Required  | Store          |
| Timestamp | java.sql.time | N/A   | Optional  | Store          |
| Comment   | String        | N/A   | Required  | Store          |

<br>

## Category

| Table Name | Primary Key | Foreign Key                | Uniqueness Constraint |
| ---------- | ----------- | -------------------------- | --------------------- |
| Category   | CategoryID  | Problem references Problem | N/A                   |

<br>

| Name       | Type   | Range | Req / Opt | Derive / Store |
| ---------- | ------ | ----- | --------- | -------------- |
| CategoryId | Int    | N/A   | Optional  | Store          |
| Problem    | Int    | N/A   | Required  | Store          |
| Name       | String | N/A   | Required  | Store          |

<br>
