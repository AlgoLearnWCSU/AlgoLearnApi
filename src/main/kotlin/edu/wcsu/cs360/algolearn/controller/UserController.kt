package edu.wcsu.cs360.algolearn.controller

import edu.wcsu.cs360.algolearn.model.User
import edu.wcsu.cs360.algolearn.model.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/user"])
class UserController {
    @Autowired
    private val userRepository: UserRepository? = null

    @GetMapping
    fun getDemUsers(): List<User?> {
        return userRepository!!.findAll().toList()
    }

    @GetMapping(path = ["/{username}"])
    fun getDemUsersById(@PathVariable username: String?): Any {
        val data = userRepository!!.findById(username!!)
        if (data.isEmpty)
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
        return data.get()
    }

    @PostMapping // Map ONLY POST Requests
    fun addNewUser(@RequestBody user: User): Any {
        if (userRepository!!.findById(user.username!!).isPresent)
            return ResponseEntity<Any>(HttpStatus.CONFLICT)
        userRepository.save(user)
        return user
    }

    @PutMapping(path = ["/{username}"])
    fun replaceUser(@PathVariable username: String, @RequestBody user: User): Any {
        if (userRepository!!.findById(username).isEmpty)
            return ResponseEntity<Any>(HttpStatus.NOT_FOUND)

        user.username = username;

        userRepository.updateAvatar_urlByUsername(username, user.avatar_url!!)
        userRepository.updateEmailByUsername(username, user.email!!)
        userRepository.updateIsAdminByUsername(username, user.isAdmin!!)
        userRepository.updateNameByUsername(username, user.name!!)

        return user
    }

    @PatchMapping(path = ["/{username}"])
    fun modifyUser(@PathVariable username: String, @RequestBody user: User): Any {
        val oldUser = userRepository!!.findById(username)
        if (oldUser.isEmpty)
            return ResponseEntity<Any>(HttpStatus.NOT_FOUND)

        user.username = username

        if(user.name != null)
            userRepository.updateNameByUsername(username, user.name!!)
        else
            user.name = oldUser.get().name
        if(user.email != null)
            userRepository.updateEmailByUsername(username, user.email!!)
        else
            user.email = oldUser.get().email
        if(user.isAdmin != null)
            userRepository.updateIsAdminByUsername(username, user.isAdmin!!)
        else
            user.isAdmin = oldUser.get().isAdmin

        return user
    }

    @DeleteMapping(path = ["/{username}"])
    fun deleteUserById(@PathVariable username: String): ResponseEntity<Any> {
        if (userRepository!!.findById(username).isEmpty)
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        userRepository.deleteById(username)
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping(path = ["/search"])
    fun searchParam(@RequestParam username: String?,
                    @RequestParam name: String?,
                    @RequestParam email: String?,
                    @RequestParam admin: Boolean?,
                    @RequestParam avatar_url: String?) : Any {
        val matcher: ExampleMatcher = ExampleMatcher
                .matchingAll()
                .withMatcher("username", contains().ignoreCase())
                .withMatcher("name", contains().ignoreCase())
                .withMatcher("email", contains().ignoreCase())
                .withMatcher("isAdmin", exact())
                .withMatcher("avatar_url", contains().ignoreCase())

        return userRepository!!.findAll(
                Example.of(User(username,
                        name,
                        email,
                        admin,
                        avatar_url), matcher))
    }
}