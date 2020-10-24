package edu.wcsu.cs360.algolearn.model

import org.springframework.data.repository.CrudRepository
import javax.persistence.*


@Entity // This tells Hibernate to make a table out of this class
@Table(name = "problem", schema = "public") // change table name to avoid keyword conflict
class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Int? = null

    @Column(nullable = false)
    @Lob
    var description: String? = null

    @Column(nullable = false)
    var isReviewed: Boolean? = null

    @Column(nullable = false)
    val poster: String? = null

}

interface ProblemRepository : CrudRepository<Problem?, Int?>