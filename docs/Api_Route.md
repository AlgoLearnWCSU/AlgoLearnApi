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
| GET           | `/user/{username}`    | False         |               | `JsonResponse<List<User>>`    | Get user by username                  |
| POST          | `/user`               | True          | `User`		| `JsonResponse<List<User>>`    | Create a new user 					|
| PUT           | `/user/{username}`    | True          | `User`		| `JsonResponse<List<User>>`    | Replace an existing user by username	|
| PATCH         | `/user/{username}`    | True          | `User`		| `JsonResponse<List<User>>`    | Modify an existing user by username 	|
| DELETE        | `/user/{username}`    | True          |               | `JsonResponse<List<User>>`    | Delete an existing user by username 	|
