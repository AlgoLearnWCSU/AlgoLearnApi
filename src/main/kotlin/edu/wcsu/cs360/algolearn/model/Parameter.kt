package edu.wcsu.cs360.algolearn.model

import org.springframework.data.repository.CrudRepository
import java.io.Serializable
import javax.persistence.*

class ParameterId(private val problem: Int, private val name: String) : Serializable {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is ParameterId -> (other.name.compareTo(name) == 0 && other.problem == problem)
            else -> false
        }
    }

    override fun hashCode(): Int {
        return problem*name.length+72%66047
    }

}

@Entity(name = "parameter") // This tells Hibernate to make a table out of this class
@IdClass(ParameterId::class)// change table name to avoid keyword conflict
class Parameter {

    @Id
    @Column(nullable = false)
    var problem: Int? = null

    @Id
    @Column(nullable = false)
    var name: String? = null
}

interface ParameterRepository : CrudRepository<Parameter?, ParameterId?>
