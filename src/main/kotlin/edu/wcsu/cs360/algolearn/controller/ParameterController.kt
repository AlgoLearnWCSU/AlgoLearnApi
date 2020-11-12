package edu.wcsu.cs360.algolearn.controller

import edu.wcsu.cs360.algolearn.model.Parameter
import edu.wcsu.cs360.algolearn.model.ParameterRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.*


@RestController
@RequestMapping(path = ["/parameter"])
class ParameterController {
    @Autowired
    private val parameterRepository: ParameterRepository? = null

    @GetMapping
    fun getDemParameters(): List<Parameter?> {
        println("getting all params")
        return parameterRepository!!.findAll().toList()
    }

    @GetMapping(path = ["/{paramId}"])
    fun getDemParametersById(@PathVariable paramId: Int): Any {
        val data = parameterRepository!!.findById(paramId)
        if (data.isEmpty)
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
        return data.get()
    }

    @PostMapping // Map ONLY POST Requests
    fun addNewParameter(@RequestBody parameter: Parameter): Any {
        println("Creating Param: $parameter")
        if (parameter.paramId != null && parameterRepository!!.findById(parameter.paramId!!).isPresent)
            return ResponseEntity<Any>(HttpStatus.CONFLICT)
        parameterRepository?.save(parameter)
        return parameter
    }

    @PutMapping(path = ["/{paramId}"])
    fun replaceParameter(@PathVariable paramId: Int, @RequestBody parameter: Parameter): Any {
        if (parameterRepository!!.findById(paramId).isEmpty)
            return ResponseEntity<Any>(HttpStatus.NOT_FOUND)
        parameterRepository.save(parameter)
        return parameter
    }

    @PatchMapping(path = ["/{paramId}"])
    fun modifyParameter(@PathVariable paramId: Int, @RequestBody parameter: Parameter): Any {
        val oldParameter = parameterRepository!!.findById(paramId)
        if (oldParameter.isEmpty)
            return ResponseEntity<Any>(HttpStatus.NOT_FOUND)

        if(paramId != parameter.problem)
            parameterRepository.deleteById(paramId)

        if(parameter.problem == null)
            parameter.problem = oldParameter.get().problem
        if(parameter.name == null)
            parameter.name = oldParameter.get().name

        parameterRepository.save(parameter)

        return parameter
    }

    @DeleteMapping(path = ["/{paramId}"])
    fun deleteParameterById(@PathVariable paramId: Int): ResponseEntity<Any> {
        if (parameterRepository!!.findById(paramId).isEmpty)
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        parameterRepository.deleteById(paramId)
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping(path = ["/search"])
    fun searchParam(@RequestParam paramId: Int?,
                    @RequestParam problem: Int?,
                    @RequestParam name: String?): Any {
        val matcher: ExampleMatcher = ExampleMatcher
                .matchingAll()
                .withMatcher("name", contains().ignoreCase())
                .withMatcher("problem", exact())
                .withMatcher("paramId", exact())
        return parameterRepository!!.findAll(
                Example.of(Parameter(paramId,
                        problem,
                        name), matcher))
    }
}