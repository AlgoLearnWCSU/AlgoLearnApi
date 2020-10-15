## Api Routes

### Data Types

- JsonResponse\<T>: 
```js
{
    "resposne": T 
}
```

- JsonError\<T>:
```js
{
    "error": String
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

| HTTP Method   |  Path                 | Requires Auth | Request Body  |  Response Body                | Description                           |
--------------- | --------------------- | ------------- | ------------- | ----------------------------- | ------------------------------------- |
| GET           | `/user`               | False         |               | `JsonResponse<List<User>>`    | Get all users                         |
| GET           | `/user/{username}`    | False         |               | `JsonResponse<User>`    		| Get user by username                  |
| POST          | `/user`               | True          | `User`		| `JsonResponse<User>`		    | Create a new user 					|
| PUT           | `/user/{username}`    | True          | `User`		| `JsonResponse<User>`		    | Replace an existing user by username	|
| PATCH         | `/user/{username}`    | True          | `User`		| `JsonResponse<User>`		    | Modify an existing user by username 	|
| DELETE        | `/user/{username}`    | True          |               | 							    | Delete an existing user by username 	|
