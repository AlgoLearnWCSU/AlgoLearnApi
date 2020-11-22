package edu.wcsu.cs360.algolearn.controller

import edu.wcsu.cs360.algolearn.model.*
import edu.wcsu.cs360.algolearn.model.AuthRepository
import edu.wcsu.cs360.algolearn.model.Problem
import edu.wcsu.cs360.algolearn.model.ProblemRepository
import edu.wcsu.cs360.algolearn.model.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.ExampleMatcher
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.*

@RestController
@RequestMapping(path = ["/problem"])
class ProblemController {
    @Autowired
    private val problemRepository: ProblemRepository? = null

    @Autowired
    private val parameterRepository: ParameterRepository? = null

    @Autowired
    private val testcaseRepository: TestCaseRepository? = null

    @Autowired
    private val commentRepository: CommentRepository? = null

    @Autowired
    private val authRepository: AuthRepository? = null

    @Autowired
    private val userRepository: UserRepository? = null
    

    @GetMapping
    fun getDemProblems(): List<Problem?> {
        return problemRepository!!.findAll().toList()
    }

    @GetMapping(path = ["/{id}"])
    fun getDemProblemsById(@PathVariable id: Int?): Any {
        val data = problemRepository!!.findById(id!!)
        if (data.isEmpty)
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
        return data.get()
    }

    @PostMapping // Map ONLY POST Requests
    fun addNewProblem(@RequestBody problem: Problem,
                      @RequestHeader("auth-token") authToken: String): Any {
        val authSession = authRepository!!.findById(authToken)

        if(problem.isReviewed == null)
            problem.isReviewed = false

         if (authSession.isEmpty ||
                !((authSession.get().username == problem.poster && problem.isReviewed  == false) ||
                userRepository!!.findById(authSession.get().username!!).get().isAdmin!!))
            return ResponseEntity<Any>(HttpStatus.UNAUTHORIZED)

        if (problem.id != null &&
                problemRepository!!.findById(problem.id!!).isPresent)
            return ResponseEntity<Any>(HttpStatus.CONFLICT)
        problemRepository?.save(problem)

        return problem
    }

    @PutMapping(path = ["/{id}"])
    fun replaceProblem(@PathVariable id: Int, @RequestBody problem: Problem,
                       @RequestHeader("auth-token") authToken: String): Any {

        val authSession = authRepository!!.findById(authToken)

        if(problem.isReviewed == null)
            problem.isReviewed = false

        if (problemRepository!!.findById(id).isEmpty)
            return ResponseEntity<Any>(HttpStatus.NOT_FOUND)

        if (authSession.isEmpty ||
                !((authSession.get().username == problemRepository.findById(id).get().poster && authSession.get().username == problem.poster && problem.isReviewed  == false) ||
                        userRepository!!.findById(authSession.get().username!!).get().isAdmin!!))
            return ResponseEntity<Any>(HttpStatus.UNAUTHORIZED)

        problem.id = id

        problemRepository.updateDescriptionById(id, problem.description!!)
        problemRepository.updateNameById(id, problem.name!!)
        problemRepository.updatePosterById(id, problem.poster!!)
        problemRepository.updateReviewedById(id, problem.isReviewed!!)

        return problem
    }

    @PatchMapping(path = ["/{id}"])
    fun modifyProblem(@PathVariable id: Int, @RequestBody problem: Problem,
                      @RequestHeader("auth-token") authToken: String): Any {

        val authSession = authRepository!!.findById(authToken)

        val oldProblem = problemRepository!!.findById(id)
        if (oldProblem.isEmpty)
            return ResponseEntity<Any>(HttpStatus.NOT_FOUND)

        if (authSession.isEmpty ||
                !((authSession.get().username == oldProblem.get().poster && (problem.poster == null || authSession.get().username == problem.poster) && (problem.isReviewed  == false || problem.isReviewed  == null )) ||
                        userRepository!!.findById(authSession.get().username!!).get().isAdmin!!))
            return ResponseEntity<Any>(HttpStatus.UNAUTHORIZED)

        problem.id = id

        if(problem.description != null)
            problemRepository.updateDescriptionById(id, problem.description!!)
        else
            problem.description = oldProblem.get().description
        if(problem.isReviewed != null)
            problemRepository.updateReviewedById(id, problem.isReviewed!!)
        else
            problem.isReviewed = oldProblem.get().isReviewed
        if(problem.poster != null)
            problemRepository.updatePosterById(id, problem.poster!!)
        else
            problem.poster = oldProblem.get().poster
        if(problem.name != null)
            problemRepository.updateNameById(id, problem.name!!)
        else
            problem.name = oldProblem.get().name

        return problem
    }

    @DeleteMapping(path = ["/{id}"])
    fun deleteProblemById(@PathVariable id: Int,
                          @RequestHeader("auth-token") authToken: String): ResponseEntity<Any> {

        val authSession = authRepository!!.findById(authToken)

        if (problemRepository!!.findById(id).isEmpty)
            return ResponseEntity(HttpStatus.BAD_REQUEST)

        if (authSession.isEmpty ||
            !((authSession.get().username == problemRepository.findById(id).get().poster ) ||
                    userRepository!!.findById(authSession.get().username!!).get().isAdmin!!))
        return ResponseEntity<Any>(HttpStatus.UNAUTHORIZED)

        // Delete TestCases
        testcaseRepository!!.findAll(
                Example.of(TestCase(null,
                        id,
                        null,
                        null,
                        null), ExampleMatcher
                        .matchingAll()
                        .withMatcher("problem", exact()))).forEach {
            testcaseRepository.deleteById(it.id!!)
        }

        // Delete Parameters
        parameterRepository!!.findAll(
                Example.of(Parameter(null, id, null), ExampleMatcher
                        .matchingAll()
                        .withMatcher("problem", exact()))).forEach {
            parameterRepository.deleteById(it.id!!)
        }

        // Delete Comments
        commentRepository!!.findAll(
                Example.of(Comment(null, null, id, null, null), ExampleMatcher
                        .matchingAll()
                        .withMatcher("problem", exact()))).forEach {
            commentRepository.deleteById(it.id!!)
        }


        problemRepository.deleteById(id)
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping(path = ["/search"])
    fun searchParam(@RequestParam id: Int?,
                    @RequestParam name:String?,
                    @RequestParam description: String?,
                    @RequestParam reviewed: Boolean?,
                    @RequestParam poster: String?): Any {
        val matcher: ExampleMatcher = ExampleMatcher
                .matchingAll()
                .withMatcher("id", exact())
                .withMatcher("name", contains().ignoreCase())
                .withMatcher("description", contains().ignoreCase())
                .withMatcher("isReviewed", exact())
                .withMatcher("poster", contains().ignoreCase())
        return problemRepository!!.findAll(
                Example.of(Problem(id,
                            name,
                            description,
                            reviewed,
                            poster), matcher))
    }
}
