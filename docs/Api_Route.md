## Api Routes

### User

HTTP Method |  Path | Requires Auth | Request Body |  Response Body | Description 
--- | --- | --- | --- | --- | ---
| GET | `/user` | False |  | List of User Objects | Get all users 
| GET | `/user/{username}` | False |  | User Object | Get user by username
| POST | `/user` | True | User Object | User Object | Create a new user
| PUT | `/user/{username}` | True | User Object | User Object | Replace an existing user by username
| PATCH | `/user/{username}` | True | User Object | User Object | Modify an existing user by username
| DELETE | `/user/{username}` | True |  |  | Delete an existing user by username
