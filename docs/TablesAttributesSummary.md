# Table and Attribute Summaries

## User

Table Name | Primary Key | Foreign Key | Uniqueness Constraint
--- | --- | --- | ---
User | Username | N/A | Email

<br>

Name | Type | Range | Req / pt | Derive / Store
--- | --- | --- | --- | ---
Username | char(32) | N/A | required | store
Name | char(50) | N/A | required | store
Email | char(50) | N/A | required | store
IsAdmin | Boolean | true/false | required | store

<br>

## Problem

Table Name | Primary Key | Foreign Key | Uniqueness Constraint
--- | --- | --- | ---
Problem | ProblemID | Poster references User | N/A

<br>

Name | Type | Range | Req / Opt | Derive / Store
--- | --- | --- | --- | ---
ProblemID | bigserial(64-bit int) | 1-(2^64)-1 | Required | Store
Name | char(32) | N/A | Required | Store
Poster | char(32) | N/A | Required | Store
IsReviewed | bool | true / false | Required | Store
Description | text | N/A | Required | Store

<br>

## Parameter

Table Name | Primary Key | Foreign Key | Uniqueness Constraint
--- | --- | --- | ---
Parameter | ParamID | Problem references Problem | N/A

<br>

Name | Type | Range | Req / Opt | Derive / Store
--- | --- | --- | --- | ---
ParamId | bigserial(64-bit int) | 1-(2^64)-1 | Required | Store
Problem | bigserial(64-bit int) | 1-(2^64)-1 | Required | Store
Name | char(32) | N/A | Required | Store

<br>

## Test Case

Table Name | Primary Key | Foreign Key | Uniqueness Constraint
--- | --- | --- | ---
Test Case | TCID | Problem references Problem | N/A

<br>

Name | Type | Range | Req / Opt | Derive / Store
--- | --- | --- | --- | ---
TCID | bigserial(64-bit int) | 1-(2^64)-1 | Required | Store
Problem | bigserial(64-bit int) | 1-(2^64)-1 | Required | Store
IsPublic | bool | true / false | Required | Store
SampleInput | text | N/A | Required | Store
SampleOutput | text | N/A | Required | Store

<br>

## Solution_t

Table Name | Primary Key | Foreign Key | Uniqueness Constraint
--- | --- | --- | ---
Solution | SolutionId | Solver references User, Problem references Problem | N/A

<br>

Name | Type | Range | Req / Opt | Derive / Store
--- | --- | --- | --- | ---
SolutionId | bigserial(64-bit int) | 1-(2^64)-1 | Required | Store
Solver | char(32) | N/A | Required | Store
Problem | bigserial(64-bit int) | 1-(2^64)-1 | Required | Store
Code | text | N/A | Required | Store
PassedTests | Integer | 3 -  10 | Required | Store
CompTime | interval[minute to second][4] | 0 Seconds - 5? minutes | Required | Store

<br>

### Solution

Name | Type | Range | Req / Opt | Derive / Store
--- | --- | --- | --- | ---
SolutionId | bigserial(64-bit int) | 1-(2^64)-1 | Required | Store
Solver | char(32) | N/A | Required | Store
Problem | bigserial(64-bit int) | 1-(2^64)-1 | Required | Store
PassedTests | Integer | 3 -  10 | Required | Store
CompTime | interval[minute to second][4] | 0 Seconds - 5? minutes | Required | Store
TotalTests | smallint(16-bit) | 3 - 10 | Required | Derive

<br>

## Comment

Table Name | Primary Key | Foreign Key | Uniqueness Constraint
--- | --- | --- | ---
Comment  | CommentId | Commenter references User, Problem references Problem | N/A

<br>

Name | Type | Range | Req / Opt | Derive / Store
--- | --- | --- | --- | ---
CommentId | bigserial(64-bit int) | 1-(2^64)-1 | Required | Store
Commenter | char(32) | 1-(2^31)-1 | Required | Store
Problem | bigserial(64-bit int) | 1-(2^64)-1 | Required | Store
Timestamp | timestamptz[3] | 00:00:00+1459-24:00:00-1459 | Required | Store
Comment | text | N/A | Required |Store

## Category

Table Name | Primary Key | Foreign Key | Uniqueness Constraint
--- | --- | --- | ---
Category | CategoryID | Problem references Problem | N/A

<br>

Name | Type | Range | Req / Opt | Derive / Store
--- | --- | --- | --- | ---
CategoryId | bigserial(64-bit int) | 1-(2^64)-1 | Required | Store
CategoryId | bigserial(64-bit int) | 1-(2^64)-1 | Required | Store
Name | char(32) | N/A | Required | Store

<br>