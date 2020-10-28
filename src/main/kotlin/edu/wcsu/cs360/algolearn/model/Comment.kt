package edu.wcsu.cs360.algolearn.model

import org.springframework.data.repository.CrudRepository
import javax.persistence.*


@Entity // This tells Hibernate to make a table out of this class
@Table(name = "comment", schema = "public") // change table name to avoid keyword conflict
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
    val comment: String? = null

}

interface CommentRepository : CrudRepository<Comment?, Int?>