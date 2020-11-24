package edu.wcsu.cs360.algolearn.controller

import edu.wcsu.cs360.algolearn.model.TestCase
import edu.wcsu.cs360.algolearn.model.TestCaseRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/testcase"])
class TestCaseController {
    @Autowired
    private val testcaseRepository: TestCaseRepository? = null

    @GetMapping
    fun getDemTestCases(): List<TestCase?> {
        println("test")
        return testcaseRepository!!.findAll().toList()
    }

    @GetMapping(path = ["/{id}"])
    fun getDemTestCasesById(@PathVariable id: Int?): Any {
        val data = testcaseRepository!!.findById(id!!)
        if (data.isEmpty)
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
        return data.get()
    }

    @PostMapping // Map ONLY POST Requests
    fun addNewTestCase(@RequestBody testcase: TestCase): Any {
        if (testcase.id != null &&
                testcaseRepository!!.findById(testcase.id!!).isPresent)
            return ResponseEntity<Any>(HttpStatus.CONFLICT)
        testcaseRepository?.save(testcase)
        return testcase
    }

    @PutMapping(path = ["/{id}"])
    fun replaceTestCase(@PathVariable id: Int, @RequestBody testcase: TestCase): Any {
        if (testcaseRepository!!.findById(id).isEmpty)
            return ResponseEntity<Any>(HttpStatus.NOT_FOUND)
        testcase.id = id

        testcaseRepository.updateIsPublicById(id, testcase.isPublic!!)
        testcaseRepository.updateProblemById(id, testcase.problem!!)
        testcaseRepository.updateSampleInputById(id, testcase.sampleInput!!)
        testcaseRepository.updateSampleOutputById(id, testcase.sampleOutput!!)

        return testcase
    }

    @PatchMapping(path = ["/{id}"])
    fun modifyTestCase(@PathVariable id: Int, @RequestBody testcase: TestCase): Any {
        val oldTestCase = testcaseRepository!!.findById(id)
        if (oldTestCase.isEmpty)
            return ResponseEntity<Any>(HttpStatus.NOT_FOUND)

        testcase.id = id

        if(testcase.problem != null)
            testcaseRepository.updateProblemById(id, testcase.problem!!)
        else
            testcase.problem = oldTestCase.get().problem
        if(testcase.isPublic != null)
            testcaseRepository.updateIsPublicById(id, testcase.isPublic!!)
        else
            testcase.isPublic = oldTestCase.get().isPublic
        if(testcase.sampleInput != null)
            testcaseRepository.updateSampleInputById(id, testcase.sampleInput!!)
        else
            testcase.sampleInput = oldTestCase.get().sampleInput
        if(testcase.sampleOutput != null)
            testcaseRepository.updateSampleOutputById(id, testcase.sampleOutput!!)
        else
            testcase.sampleOutput = oldTestCase.get().sampleOutput

        return testcase
    }

    @DeleteMapping(path = ["/{id}"])
    fun deleteTestCaseById(@PathVariable id: Int): ResponseEntity<Any> {
        if (testcaseRepository!!.findById(id).isEmpty)
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        testcaseRepository.deleteById(id)
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping(path= ["/search"])
    fun searchParam(@RequestParam id: Int?,
                    @RequestParam problem: Int?,
                    @RequestParam public: Boolean?,
                    @RequestParam sampleInput: String?,
                    @RequestParam sampleOutput: String? ): Any {
        val matcher: ExampleMatcher = ExampleMatcher
                .matchingAll()
                .withMatcher("id", exact())
                .withMatcher("problem", exact())
                .withMatcher("public", exact())
                .withMatcher("sampleInput", contains().ignoreCase())
                .withMatcher("sampleOutput", contains().ignoreCase())

        return testcaseRepository!!.findAll(
                Example.of(TestCase(id,
                        problem,
                        public,
                        sampleInput,
                        sampleOutput), matcher))

    }
}
