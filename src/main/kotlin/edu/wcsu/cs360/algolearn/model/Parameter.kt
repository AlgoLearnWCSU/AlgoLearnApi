package edu.wcsu.cs360.algolearn.model

import org.springframework.data.jpa.repository.JpaRepository
import java.io.Serializable
import javax.persistence.*

@Entity(name = "parameter") // This tells Hibernate to make a table out of this class
class Parameter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var paramId: Int? = null

    @Column(nullable = false)
    var problem: Int? = null

    @Column(nullable = false)
    var name: String? = null

    constructor()

    constructor(paramId: Int?, problem: Int?, name: String?) {
        this.paramId = paramId
        this.problem = problem
        this.name = name
    }

}

interface ParameterRepository : JpaRepository<Parameter?, Int?>
