//package edu.wcsu.cs360.algolearn.controller
//
//import edu.wcsu.cs360.algolearn.model.Solution
//import edu.wcsu.cs360.algolearn.model.SolutionRepository
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.http.HttpStatus
//import org.springframework.http.ResponseEntity
//import org.springframework.web.bind.annotation.*
//
//@RestController
//@RequestMapping(path = ["/solution"])
//class SolutionController {
//    @Autowired
//    private val solutionRepository: SolutionRepository? = null
//
//    @GetMapping
//    fun getDemSolutions(): List<Solution?> {
//        println("test")
//        return solutionRepository!!.findAll().toList()
//    }
//
//    @GetMapping(path = ["/{solutionId}"])
//    fun getDemSolutionsById(@PathVariable solutionId: Int?): Any {
//        val data = solutionRepository!!.findById(solutionId!!)
//        if (data.isEmpty)
//            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
//        return data.get()
//    }
//
//    @PostMapping // Map ONLY POST Requests
//    fun addNewSolution(@RequestBody solution: Solution): Any {
//        if (solution.id != null &&
//                solutionRepository!!.findById(solution.id!!).isPresent)
//            return ResponseEntity<Any>(HttpStatus.CONFLICT)
//        solutionRepository?.save(solution)
//        return solution
//    }
//
//    @PutMapping(path = ["/{solutionId}"])
//    fun replaceSolution(@PathVariable solutionId: Int, @RequestBody solution: Solution): Any {
//        if (solutionRepository!!.findById(solutionId).isEmpty)
//            return ResponseEntity<Any>(HttpStatus.NOT_FOUND)
//        solutionRepository.save(solution)
//        return solution
//    }
//
//    @PatchMapping(path = ["/{solutionId}"])
//    fun modifySolution(@PathVariable solutionId: Int, @RequestBody solution: Solution): Any {
//        val oldSolution = solutionRepository!!.findById(solutionId)
//        if (oldSolution.isEmpty)
//            return ResponseEntity<Any>(HttpStatus.NOT_FOUND)
//
//        if(solutionId != solution.id)
//            solutionRepository.deleteById(solutionId)
//
//        if(solution.id == null)
//            solution.id = solutionId
//        if(solution.problem == null)
//            solution.problem = oldSolution.get().problem
//        if(solution.public == null)
//            solution.public = oldSolution.get().public
//        if(solution.sampleInput == null)
//            solution.sampleInput = oldSolution.get().sampleInput
//        if(solution.sampleOutput == null)
//            solution.sampleOutput = oldSolution.get().sampleOutput
//
//        solutionRepository.save(solution)
//
//        return solution
//    }
//
//    @DeleteMapping(path = ["/{solutionId}"])
//    fun deleteSolutionById(@PathVariable solutionId: Int): ResponseEntity<Any> {
//        if (solutionRepository!!.findById(solutionId).isEmpty)
//            return ResponseEntity(HttpStatus.BAD_REQUEST)
//        solutionRepository.deleteById(solutionId)
//        return ResponseEntity(HttpStatus.OK)
//    }
//}