## Api Routes

### Data Types

- JsonResponse\<T>: 
```js
{
    "response": T 
}
```

- JsonError\<T>:
```js
{
	"error": String
	"status": Number
}
```

- User:
```js
{
    "username": String,
    "name": String,
    "email": String,
    "admin": Boolean
}
```

### Routes

#### User

| HTTP Method   |  Path                 | Requires Auth | Request Body  |  Response Body                | Description                           | Possible Error Status |
--------------- | --------------------- | ------------- | ------------- | ----------------------------- | ------------------------------------- | --------------------- |
| GET           | `/user`               | False         |               | `JsonResponse<List<User>>`    | Get all users                         | `500`					|
| GET           | `/user/{username}`    | False         |               | `JsonResponse<User>`    		| Get user by username                  | `400, 500`			|
| POST          | `/user`               | True          | `User`		| `JsonResponse<User>`		    | Create a new user 					| `400, 401, 500`		|
| PUT           | `/user/{username}`    | True          | `User`		| `JsonResponse<User>`		    | Replace an existing user by username	| `400, 401, 404, 500`	|
| PATCH         | `/user/{username}`    | True          | `User`		| `JsonResponse<User>`		    | Modify an existing user by username 	| `400, 401, 404, 500`	|
| DELETE        | `/user/{username}`    | True          |               | 							    | Delete an existing user by username 	| `400, 401, 404, 500`	|
