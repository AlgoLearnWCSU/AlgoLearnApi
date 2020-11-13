package edu.wcsu.cs360.algolearn.model


import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.*


@Entity // This tells Hibernate to make a table out of this class
class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Int? = null

    @Column(nullable = false)
    var name:String? = null

    @Column(nullable = false)
    @Lob
    var description: String? = null

    @Column(nullable = false)
    var isReviewed: Boolean? = null

    @Column(nullable = false)
    var poster: String? = null

    constructor() { }

    constructor(id: Int?, name: String?, description: String?, isReviewed: Boolean?, poster: String?) {
        this.id = id
        this.name = name
        this.description = description
        this.isReviewed = isReviewed
        this.poster = poster
    }
}

interface ProblemRepository : JpaRepository<Problem?, Int?>
