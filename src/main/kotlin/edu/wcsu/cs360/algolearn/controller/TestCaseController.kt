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

    @GetMapping(path = ["/{tcid}"])
    fun getDemTestCasesById(@PathVariable tcid: Int?): Any {
        val data = testcaseRepository!!.findById(tcid!!)
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

    @PutMapping(path = ["/{tcid}"])
    fun replaceTestCase(@PathVariable tcid: Int, @RequestBody testcase: TestCase): Any {
        if (testcaseRepository!!.findById(tcid).isEmpty)
            return ResponseEntity<Any>(HttpStatus.NOT_FOUND)
        testcaseRepository.save(testcase)
        return testcase
    }

    @PatchMapping(path = ["/{tcid}"])
    fun modifyTestCase(@PathVariable tcid: Int, @RequestBody testcase: TestCase): Any {
        val oldTestCase = testcaseRepository!!.findById(tcid)
        if (oldTestCase.isEmpty)
            return ResponseEntity<Any>(HttpStatus.NOT_FOUND)

        if(tcid != testcase.id)
            testcaseRepository.deleteById(tcid)

        if(testcase.id == null)
            testcase.id = tcid
        if(testcase.problem == null)
            testcase.problem = oldTestCase.get().problem
        if(testcase.isPublic == null)
            testcase.isPublic = oldTestCase.get().isPublic
        if(testcase.sampleInput == null)
            testcase.sampleInput = oldTestCase.get().sampleInput
        if(testcase.sampleOutput == null)
            testcase.sampleOutput = oldTestCase.get().sampleOutput

        testcaseRepository.save(testcase)

        return testcase
    }

    @DeleteMapping(path = ["/{tcid}"])
    fun deleteTestCaseById(@PathVariable tcid: Int): ResponseEntity<Any> {
        if (testcaseRepository!!.findById(tcid).isEmpty)
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        testcaseRepository.deleteById(tcid)
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
