package edu.wcsu.cs360.algolearn.controller

import edu.wcsu.cs360.algolearn.model.User
import edu.wcsu.cs360.algolearn.model.UserRepository
import org.springframework.beans.factory.annotation.Autowired
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
        userRepository.save(user)
        return user
    }

    @PatchMapping(path = ["/{username}"])
    fun modifyUser(@PathVariable username: String, @RequestBody user: User): Any {
        val oldUser = userRepository!!.findById(username)
        if (oldUser.isEmpty)
            return ResponseEntity<Any>(HttpStatus.NOT_FOUND)

        if(username != user.username)
            userRepository.deleteById(username)

        if(user.username == null)
            user.username = username
        if(user.name == null)
            user.name = oldUser.get().name
        if(user.email == null)
            user.email = oldUser.get().email
        if(user.isAdmin == null)
            user.isAdmin = oldUser.get().isAdmin

        userRepository.save(user)

        return user
    }

    @DeleteMapping(path = ["/{username}"])
    fun deleteUserById(@PathVariable username: String): ResponseEntity<Any> {
        if (userRepository!!.findById(username).isEmpty)
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        userRepository.deleteById(username)
        return ResponseEntity(HttpStatus.OK)
    }
}