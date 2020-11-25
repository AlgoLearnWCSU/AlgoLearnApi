package edu.wcsu.cs360.algolearn.controller


import edu.wcsu.cs360.algolearn.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.*


@RestController
@RequestMapping(path = ["/category"])
class CategoryController{
    @Autowired
    private val categoryRepository: CategoryRepository? = null

    @Autowired
    private val authRepository: AuthRepository? = null

    @Autowired
    private val userRepository: UserRepository? = null

    @Autowired
    private val problemRepository: ProblemRepository? = null

    @GetMapping
    fun getDemCategories(): List<Category?> {
        println("getting all Categories")
        return categoryRepository!!.findAll().toList()
    }

    @GetMapping(path = ["/{id}"])
    fun getDemCategoriesById(@PathVariable id: Int): Any {
        val data = categoryRepository!!.findById(id)
        if (data.isEmpty)
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
        return data.get()
    }

    @PostMapping // Map ONLY POST Requests
    fun addNewCategory(@RequestBody category: Category,
                       @RequestHeader("auth-token")authToken: String): Any {
        val authSession = authRepository!!.findById(authToken)
        println("Creating cate: $category")
        if (category.id != null && categoryRepository!!.findById(category.id!!).isPresent)
            return ResponseEntity<Any>(HttpStatus.CONFLICT)
    val problem = problemRepository!!.findById(category.problem!!)
        if (problem.isEmpty)
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
        if (authSession.isEmpty ||
                !((authSession.get().username == problem.get().poster) ||
                        userRepository!!.findById(authSession.get().username!!).get().isAdmin!!))
            return ResponseEntity<Any>(HttpStatus.UNAUTHORIZED)

        categoryRepository?.save(category)
        return category
    }

    @PutMapping(path = ["/{id}"])
    fun replaceCategory(@PathVariable id: Int, @RequestBody category:Category,
    @RequestHeader("auth-token") authToken: String): Any {

        val authSession = authRepository!!.findById(authToken)

        if (categoryRepository!!.findById(id).isEmpty)
            return ResponseEntity<Any>(HttpStatus.NOT_FOUND)
        val problem = problemRepository!!.findById(category.problem!!)
        if (problem.isEmpty)
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
        if (authSession.isEmpty ||
                !((authSession.get().username == problem.get().poster) ||
                        userRepository!!.findById(authSession.get().username!!).get().isAdmin!!))
            return ResponseEntity<Any>(HttpStatus.UNAUTHORIZED)

        category.id = id

        categoryRepository.updateNameById(id, category.name!!)
        categoryRepository.updateProblemById(id, category.problem!!)

        return category

    }

    @PatchMapping(path = ["/{id}"])
    fun modifyCategory(@PathVariable id: Int, @RequestBody category: Category,
                       @RequestHeader("auth-token") authToken: String): Any {

        val authSession = authRepository!!.findById(authToken)
        val oldCategory = categoryRepository!!.findById(id)

        if (oldCategory.isEmpty)
            return ResponseEntity<Any>(HttpStatus.NOT_FOUND)

        var problem:java.util.Optional<Problem?> ?=null

        problem = if(category.problem == null)
            problemRepository!!.findById(oldCategory.get().problem!!)
        else
            problemRepository!!.findById(category.problem!!)

        if (problem.isEmpty)
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)

        if (authSession.isEmpty ||
                !((authSession.get().username == problem.get().poster) ||
                        userRepository!!.findById(authSession.get().username!!).get().isAdmin!!))
            return ResponseEntity<Any>(HttpStatus.UNAUTHORIZED)

        if(category.problem == null)
            category.problem = oldCategory.get().problem
        if(category.name == null)
            category.name = oldCategory.get().name

        category.id = id

        if(category.name != null)
            categoryRepository.updateNameById(id, category.name!!)
        else
            category.name = oldCategory.get().name
        if(category.problem != null)
            categoryRepository.updateProblemById(id, category.problem!!)
        else
            category.problem = oldCategory.get().problem


        return category
    }

    @DeleteMapping(path = ["/{id}"])
    fun deleteParameterById(@PathVariable id: Int, @RequestBody category: Category,
    @RequestHeader("auth-token") authToken: String): ResponseEntity<Any> {


        val authSession = authRepository!!.findById(authToken)
        val oldCategory = categoryRepository!!.findById(id)
        if(oldCategory.isEmpty)
            return ResponseEntity<Any>(HttpStatus.NOT_FOUND)

        var problem:java.util.Optional<Problem?> ?=null

        problem = if(category.problem == null)
            problemRepository!!.findById(oldCategory.get().problem!!)
        else
            problemRepository!!.findById(category.problem!!)

        if (problem.isEmpty)
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
        if (authSession.isEmpty ||
                !((authSession.get().username == problem.get().poster) ||
                        userRepository!!.findById(authSession.get().username!!).get().isAdmin!!))
            return ResponseEntity<Any>(HttpStatus.UNAUTHORIZED)

        if (categoryRepository!!.findById(id).isEmpty)
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        categoryRepository.deleteById(id)
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping(path = ["/search"])
    fun searchCategory(@RequestParam id: Int?,
                    @RequestParam problem: Int?,
                    @RequestParam name: String?): Any {
        val matcher: ExampleMatcher = ExampleMatcher
                .matchingAll()
                .withMatcher("name", contains().ignoreCase())
                .withMatcher("problem", exact())
                .withMatcher("id", exact())
        return categoryRepository!!.findAll(
                Example.of(Category(id,
                        problem,
                        name), matcher))
    }
}

