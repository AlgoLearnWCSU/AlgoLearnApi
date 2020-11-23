package edu.wcsu.cs360.algolearn.controller

import kotlinx.coroutines.*
import edu.wcsu.cs360.algolearn.model.Solution
import edu.wcsu.cs360.algolearn.model.SolutionRepository
import edu.wcsu.cs360.algolearn.model.TestCase
import edu.wcsu.cs360.algolearn.model.TestCaseRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.sql.Time
import kotlin.math.roundToLong

class Status {
    var id = 0
    var description = ""
}

class RequestBatch{
    var submissions = ArrayList<RequestCreateSubmission>()
}

class ResponseBatch{
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
    private val restTemplateBuilder: RestTemplateBuilder? = null

    private var restTemplate: RestTemplate? = null

    private val apiKey = "fe73008d0cmsh5ad27cb522ca15bp1c24a5jsne2c7de669881"
    private val apiHost = "https://judge0.p.rapidapi.com/"

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

    @PostMapping // Map ONLY POST Requests
    fun addNewSolution(@RequestBody solution: Solution): Any {

        if (solution.id != null &&
                solutionRepository!!.findById(solution.id!!).isPresent)
            return ResponseEntity<Any>(HttpStatus.CONFLICT)

        val postedSolution = postSolutionJudgeZero(solution)

        solutionRepository?.save(postedSolution)
        return postedSolution
    }

    @PutMapping(path = ["/{id}"])
    fun replaceSolution(@PathVariable id: Int, @RequestBody solution: Solution): Any {
        if (solutionRepository!!.findById(id).isEmpty)
            return ResponseEntity<Any>(HttpStatus.NOT_FOUND)

        solution.id = id

        val postedSolution = postSolutionJudgeZero(solution)
        solutionRepository!!.updateTokensById(id, postedSolution.tokens!!)

        return postedSolution
    }

    @PatchMapping(path = ["/{id}"])
    fun modifySolution(@PathVariable id: Int, @RequestBody solution: Solution): Any {
        val oldSolution = solutionRepository!!.findById(id)
        if (oldSolution.isEmpty)
            return ResponseEntity<Any>(HttpStatus.NOT_FOUND)

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

    @DeleteMapping(path = ["/{solutionId}"])
    fun deleteSolutionById(@PathVariable solutionId: Int): ResponseEntity<Any> {
        if (solutionRepository!!.findById(solutionId).isEmpty)
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        solutionRepository.deleteById(solutionId)
        return ResponseEntity(HttpStatus.OK)
    }

    fun postSolutionJudgeZero(solution: Solution) : Solution{
        val testCases = searchTestCaseByProblemId(solution.problem)
        if (restTemplate == null) {
            restTemplate = restTemplateBuilder!!.build()
        }

        val headers = HttpHeaders()
        headers["x-rapidapi-key"] = "4f06d70076msh666d1184a5becedp13ef9cjsn9271945f91cf"
        headers["x-rapidapi-host"] = "judge0.p.rapidapi.com"
        headers["useQueryString"] = true.toString()

        val body = RequestBatch()

        for (iter in 0 until testCases.size) {
            val submission = RequestCreateSubmission()

            submission.stdin = testCases[iter].sampleInput!!
            submission.source_code = solution.code!!
            submission.language_id = solution.languageId!!

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
                    ResponseBatch::class.java).body


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
