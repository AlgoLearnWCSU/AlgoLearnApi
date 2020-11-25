package edu.wcsu.cs360.algolearn.controller

import edu.wcsu.cs360.algolearn.model.*
import kotlinx.coroutines.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate

class Status {
    var id = 0
    var description = ""
}

class RequestCreateBatch{
    var submissions = ArrayList<RequestCreateSubmission>()
}

class ResponseGetBatch{
    var submissions = ArrayList<ResponseGetSubmission>()
}

class RequestCreateSubmission{
    var language_id = 0
    var source_code = ""
    var stdin = ""
}

class ResponseCreateSubmission {
    var token: String? = null
}

class ResponseGetSubmission {
    var stdout: String? = null
    var time: String? = null
    var memory: Int? = null
    var stderr: String? = null
    var token: String? = null
    var compileOutput: String? = null
    var message: String? = null
    var status: Status? = null
}

@RestController
@RequestMapping(path = ["/solution"])
class SolutionController {

    @Autowired
    private val testCaseRepository: TestCaseRepository? = null

    @Autowired
    private val solutionRepository: SolutionRepository? = null

    @Autowired
    private val userRepository: UserRepository? = null

    @Autowired
    private val authRepository: AuthRepository? = null

    @Autowired
    private val restTemplateBuilder: RestTemplateBuilder? = null

    private var restTemplate: RestTemplate? = null

    private val apiKey = "fe73008d0cmsh5ad27cb522ca15bp1c24a5jsne2c7de669881"
    private val apiHost = "judge0.p.rapidapi.com"

    private val viewModelJob = SupervisorJob()
    val viewmodelCoroutineScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    fun searchTestCaseByProblemId(problem: Int?): MutableList<TestCase> {
        val matcher: ExampleMatcher = ExampleMatcher
                .matchingAll()
                .withMatcher("problem", ExampleMatcher.GenericPropertyMatchers.exact())

        return testCaseRepository!!.findAll(
                Example.of(TestCase(null, problem, null, null, null), matcher))
    }

    @GetMapping
    fun getDemSolutions(): List<Solution?> {
        return solutionRepository!!.findAll().toList()
    }

    @GetMapping(path = ["/{id}"])
    fun getDemSolutionsById(@PathVariable id: Int?): Any {
        val data = solutionRepository!!.findById(id!!)
        if (data.isEmpty)
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
        return data.get()
    }

    @GetMapping(path = ["/result/testcase/{token}"])
    fun getTokenResult(@PathVariable token: String, @RequestHeader("auth-token") authToken: String): Any {

        val authSession = authRepository!!.findById(authToken)

        if ( authSession.isEmpty )
            return ResponseEntity<Any>(HttpStatus.UNAUTHORIZED)

        val headers = HttpHeaders()
        headers["x-rapidapi-key"] = apiKey
        headers["x-rapidapi-host"] = apiHost
        headers["useQueryString"] = true.toString()
        headers["Connection"] = "keep-alive"

        if (restTemplate == null)
            restTemplate = restTemplateBuilder!!.build()

        val res = restTemplate!!.exchange("https://judge0.p.rapidapi.com/submissions/${token}", HttpMethod.GET, HttpEntity("", headers),
                ResponseGetSubmission::class.java).body

        return res!!
    }

    @GetMapping(path = ["/result/{id}"])
    fun getSolutionResult(@PathVariable id: Int, @RequestHeader("auth-token") authToken: String): Any {

        val solution = solutionRepository!!.findById(id)
        if (solution.isEmpty)
            return ResponseEntity<Any>(HttpStatus.NOT_FOUND)

        val authSession = authRepository!!.findById(authToken)

        if ( authSession.isEmpty )
            return ResponseEntity<Any>(HttpStatus.UNAUTHORIZED)

        val headers = HttpHeaders()
        headers["x-rapidapi-key"] = apiKey
        headers["x-rapidapi-host"] = apiHost
        headers["useQueryString"] = true.toString()
        headers["Connection"] = "keep-alive"

        if (restTemplate == null)
            restTemplate = restTemplateBuilder!!.build()

        val res = restTemplate!!.exchange("https://judge0.p.rapidapi.com/submissions/batch?tokens=${solution.get().tokens}", HttpMethod.GET, HttpEntity("", headers),
                ResponseGetBatch::class.java).body

        return res!!
    }

    @PostMapping // Map ONLY POST Requests
    fun addNewSolution(@RequestBody solution: Solution, @RequestHeader("auth-token") authToken: String): Any {

        if (solution.id != null &&
                solutionRepository!!.findById(solution.id!!).isPresent)
            return ResponseEntity<Any>(HttpStatus.CONFLICT)

        val authSession = authRepository!!.findById(authToken)

        if ( authSession.isEmpty || !(authSession.get().username == solution.solver ||
                userRepository!!.findById(authSession.get().username!!).get().isAdmin!!))
            return ResponseEntity<Any>(HttpStatus.UNAUTHORIZED)

        val postedSolution = postSolutionJudgeZero(solution)

        solutionRepository?.save(postedSolution)
        return postedSolution
    }

    @PutMapping(path = ["/{id}"])
    fun replaceSolution(@PathVariable id: Int, @RequestBody solution: Solution, @RequestHeader("auth-token") authToken: String): Any {
        if (solutionRepository!!.findById(id).isEmpty)
            return ResponseEntity<Any>(HttpStatus.NOT_FOUND)

        val authSession = authRepository!!.findById(authToken)

        if (authSession.isEmpty ||
                !((authSession.get().username == solutionRepository.findById(id).get().solver ) ||
                        userRepository!!.findById(authSession.get().username!!).get().isAdmin!!))
            return ResponseEntity<Any>(HttpStatus.UNAUTHORIZED)

        solution.id = id

        val postedSolution = postSolutionJudgeZero(solution)
        solutionRepository!!.updateTokensById(id, postedSolution.tokens!!)

        return postedSolution
    }

    @PatchMapping(path = ["/{id}"])
    fun modifySolution(@PathVariable id: Int, @RequestBody solution: Solution, @RequestHeader("auth-token") authToken: String): Any {
        val oldSolution = solutionRepository!!.findById(id)

        if (oldSolution.isEmpty)
            return ResponseEntity<Any>(HttpStatus.NOT_FOUND)

        val authSession = authRepository!!.findById(authToken)

        if (authSession.isEmpty ||
                !((authSession.get().username == oldSolution.get().solver ) ||
                        userRepository!!.findById(authSession.get().username!!).get().isAdmin!!))
            return ResponseEntity<Any>(HttpStatus.UNAUTHORIZED)

        var rerun = false

        solution.id = id

        if(solution.solver != null)
            solutionRepository.updateSolverById(id, solution.solver!!)
        else
            solution.solver = oldSolution.get().solver
        if (solution.problem != null)
            solutionRepository.updateProblemById(id, solution.problem!!)
        else
            solution.problem = oldSolution.get().problem
        if (solution.code != null) {
            solutionRepository.updateCodeById(id,solution.code!!)
            rerun = true
        }
        else
            solution.code = oldSolution.get().code
        if(solution.languageId != null) {
            solutionRepository.updateLanguageIdById(id, solution.languageId!!)
            rerun = true
        }
        else
            solution.languageId = oldSolution.get().languageId

        if(rerun){
            val postedSolution = postSolutionJudgeZero(solution)
            solutionRepository!!.updateTokensById(id, postedSolution.tokens!!);
            return postedSolution
        }

        return solution
    }

    @DeleteMapping(path = ["/{id}"])
    fun deleteSolutionById(@PathVariable id: Int, @RequestHeader("auth-token") authToken: String): ResponseEntity<Any> {
        if (solutionRepository!!.findById(id).isEmpty)
            return ResponseEntity(HttpStatus.NOT_FOUND)

        val authSession = authRepository!!.findById(authToken)

        if (authSession.isEmpty ||
                !((authSession.get().username == solutionRepository.findById(id).get().solver ) ||
                        userRepository!!.findById(authSession.get().username!!).get().isAdmin!!))
            return ResponseEntity<Any>(HttpStatus.UNAUTHORIZED)

        solutionRepository.deleteById(id)
        return ResponseEntity(HttpStatus.OK)
    }

    fun postSolutionJudgeZero(solution: Solution) : Solution{
        val testCases = searchTestCaseByProblemId(solution.problem)
        if (restTemplate == null) {
            restTemplate = restTemplateBuilder!!.build()
        }

        val headers = HttpHeaders()
        headers["x-rapidapi-key"] = apiKey
        headers["x-rapidapi-host"] = apiHost
        headers["useQueryString"] = true.toString()
        headers["Connection"] = "keep-alive"

        val body = RequestCreateBatch()

        for (iter in 0 until testCases.size) {
            val submission = RequestCreateSubmission()

            submission.stdin = testCases[iter].sampleInput!!
            submission.source_code = solution.code!!
            submission.language_id = solution.languageId!!

            if (submission.stdin == null)
                submission.stdin = ""

            body.submissions.add(submission)
        }

        val response = restTemplate!!.postForObject("https://judge0.p.rapidapi.com/submissions/batch", HttpEntity(body, headers), Array<ResponseCreateSubmission>::class.java)

        var tokenString = ""
        response!!.forEach {
            tokenString += "${it.token},"
        }

        solution.tokens = tokenString.substring(0,tokenString.length - 1)

        viewmodelCoroutineScope.launch {
            delay(5000)

            val res = restTemplate!!.exchange("https://judge0.p.rapidapi.com/submissions/batch?tokens=${solution.tokens}", HttpMethod.GET, HttpEntity("", headers),
                    ResponseGetBatch::class.java).body

            var time = 0.0
            var passed = 0

            for (subNum in 0 until testCases.size) {
                if (res!!.submissions[subNum].status!!.id == 3) {
                    time += res.submissions[subNum].time!!.toDouble()
                    if(res.submissions[subNum].stdout != null && res.submissions[subNum].stdout!!.compareTo(testCases[subNum].sampleOutput!!) == 0)
                        passed++
                }
            }

            solutionRepository!!.updatePassedTestsById(solution.id!!, passed)
            solutionRepository.updateAvgCompTimeById(solution.id!!, time)
        }
        return solution
    }
}
