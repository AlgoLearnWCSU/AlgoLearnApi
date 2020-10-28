package edu.wcsu.cs360.algolearn.controller

import edu.wcsu.cs360.algolearn.model.Parameter
import edu.wcsu.cs360.algolearn.model.ParameterId
import edu.wcsu.cs360.algolearn.model.ParameterRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/parameter"])
class ParameterController {
    @Autowired
    private val parameterRepository: ParameterRepository? = null

    @GetMapping
    fun getDemParameters(): List<Parameter?> {
        println("test")
        return parameterRepository!!.findAll().toList()
    }

    @GetMapping(path = ["/{problem}/{name}"])
    fun getDemParametersById(@PathVariable problem: Int?, @PathVariable name: String?): Any {
        val data = parameterRepository!!.findById(ParameterId(problem!!, name!!))
        if (data.isEmpty)
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
        return data.get()
    }

    @PostMapping // Map ONLY POST Requests
    fun addNewParameter(@RequestBody parameter: Parameter): Any {
        if (parameter.problem!! != null && parameter.name!! != null &&
                parameterRepository!!.findById(ParameterId(parameter.problem!!, parameter.name!!)).isPresent)
            return ResponseEntity<Any>(HttpStatus.CONFLICT)
        parameterRepository?.save(parameter)
        return parameter
    }

    @PutMapping(path = ["/{problem}/{name}"])
    fun replaceParameter(@PathVariable problem: Int?, @PathVariable name: String?, @RequestBody parameter: Parameter): Any {
        if (parameterRepository!!.findById(ParameterId(problem!!, name!!)).isEmpty)
            return ResponseEntity<Any>(HttpStatus.NOT_FOUND)
        parameterRepository.save(parameter)
        return parameter
    }

    @PatchMapping(path = ["/{problem}/{name}"])
    fun modifyParameter(@PathVariable problem: Int?, @PathVariable name: String?, @RequestBody parameter: Parameter): Any {
        val oldParameter = parameterRepository!!.findById(ParameterId(problem!!, name!!))
        if (oldParameter.isEmpty)
            return ResponseEntity<Any>(HttpStatus.NOT_FOUND)

        if(problem != parameter.problem || name != parameter.name)
            parameterRepository.deleteById(ParameterId(problem!!, name!!))
        else
            return parameter

        if(parameter.problem == null)
            parameter.problem = oldParameter.get().problem
        if(parameter.name == null)
            parameter.name = oldParameter.get().name

        parameterRepository.save(parameter)

        return parameter
    }

    @DeleteMapping(path = ["/{problem}/{name}"])
    fun deleteParameterById(@PathVariable problem: Int?, @PathVariable name: String?): ResponseEntity<Any> {
        if (parameterRepository!!.findById(ParameterId(problem!!, name!!)).isEmpty)
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        parameterRepository.deleteById(ParameterId(problem!!, name!!))
        return ResponseEntity(HttpStatus.OK)
    }
}