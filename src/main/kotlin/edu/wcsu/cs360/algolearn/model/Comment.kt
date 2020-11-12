package edu.wcsu.cs360.algolearn.model

import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.*


@Entity // This tells Hibernate to make a table out of this class
class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Int? = null

    @Column(nullable = false)
    var commenter: String? = null

    @Column(nullable = false)
    var problem: Int? = null

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    var timestamp: java.util.Date? = null

    @Column(nullable = false)
    @Lob
    var comment: String? = null

    constructor() { }

    constructor(id: Int?, commenter: String?, problem: Int?, timestamp: java.util.Date?, comment: String?) {
        this.id = id
        this.commenter = commenter
        this.problem = problem
        this.timestamp = timestamp
        this.comment = comment
    }

}

interface CommentRepository : JpaRepository<Comment?, Int?>