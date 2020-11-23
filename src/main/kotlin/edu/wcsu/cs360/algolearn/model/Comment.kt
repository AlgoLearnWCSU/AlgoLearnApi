package edu.wcsu.cs360.algolearn.model

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*
import javax.persistence.*
import javax.transaction.Transactional


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
    var timestamp: Date? = null

    @Column(nullable = false)
    @Lob
    var comment: String? = null

    constructor() { }

    constructor(id: Int?, commenter: String?, problem: Int?, timestamp: Date?, comment: String?) {
        this.id = id
        this.commenter = commenter
        this.problem = problem
        this.timestamp = timestamp
        this.comment = comment
    }

}

interface CommentRepository : JpaRepository<Comment?, Int?> {
    @Transactional
    @Modifying(flushAutomatically = true)
    @Query("update Comment c set c.commenter = :commenter where c.id = :id")
    fun updateCommenterById(@Param("id") id: Int, @Param("commenter") commenter: String)

    @Transactional
    @Modifying(flushAutomatically = true)
    @Query("update Comment c set c.problem = :problem where c.id = :id")
    fun updateProblemById(@Param("id") id: Int, @Param("problem") problem: Int)

    @Transactional
    @Modifying(flushAutomatically = true)
    @Query("update Comment c set c.timestamp = :timestamp where c.id = :id")
    fun updateTimeStampById(@Param("id") id: Int, @Param("timestamp") timestamp: Date)

    @Transactional
    @Modifying(flushAutomatically = true)
    @Query("update Comment c set c.comment = :comment where c.id = :id")
    fun updateCommentById(@Param("id") id: Int, @Param("comment") comment: String)
}