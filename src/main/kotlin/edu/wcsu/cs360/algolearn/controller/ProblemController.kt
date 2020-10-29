package edu.wcsu.cs360.algolearn.controller

import edu.wcsu.cs360.algolearn.model.Problem
import edu.wcsu.cs360.algolearn.model.ProblemRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/problem"])
class ProblemController {
    @Autowired
    private val problemRepository: ProblemRepository? = null

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
    fun addNewProblem(@RequestBody problem: Problem): Any {
        if (problem.id != null &&
                problemRepository!!.findById(problem.id!!).isPresent)
            return ResponseEntity<Any>(HttpStatus.CONFLICT)
        problemRepository?.save(problem)
        return problem
    }

    @PutMapping(path = ["/{id}"])
    fun replaceProblem(@PathVariable id: Int, @RequestBody problem: Problem): Any {
        if (problemRepository!!.findById(id).isEmpty)
            return ResponseEntity<Any>(HttpStatus.NOT_FOUND)
        problemRepository.save(problem)
        return problem
    }

    @PatchMapping(path = ["/{id}"])
    fun modifyProblem(@PathVariable id: Int, @RequestBody problem: Problem): Any {
        val oldProblem = problemRepository!!.findById(id)
        if (oldProblem.isEmpty)
            return ResponseEntity<Any>(HttpStatus.NOT_FOUND)

        if(id != problem.id)
            problemRepository.deleteById(id)

        if(problem.id == null)
            problem.id = id
        if(problem.description == null)
            problem.description = oldProblem.get().description
        if(problem.isReviewed == null)
            problem.isReviewed = oldProblem.get().isReviewed

        problemRepository.save(problem)

        return problem
    }

    @DeleteMapping(path = ["/{id}"])
    fun deleteProblemById(@PathVariable id: Int): ResponseEntity<Any> {
        if (problemRepository!!.findById(id).isEmpty)
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        problemRepository.deleteById(id)
        return ResponseEntity(HttpStatus.OK)
    }
}