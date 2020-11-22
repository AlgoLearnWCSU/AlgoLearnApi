package edu.wcsu.cs360.algolearn.model


import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import javax.persistence.*
import javax.transaction.Transactional


@Entity // This tells Hibernate to make a table out of this class
class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    constructor(){}

    constructor(id: Int?, name: String?, description: String?, isReviewed: Boolean?, poster: String?) {
        this.id = id
        this.name = name
        this.description = description
        this.isReviewed = isReviewed
        this.poster = poster
    }
}

interface ProblemRepository : JpaRepository<Problem?, Int?>{
    @Transactional
    @Modifying(flushAutomatically = true)
    @Query("update Problem p set p.poster = :poster where p.id = :id")
    fun updatePosterById(@Param("id") id: Int,@Param("poster") poster: String)

    @Transactional
    @Modifying(flushAutomatically = true)
    @Query("update Problem p set p.name = :name where p.id = :id")
    fun updateNameById(@Param("id") id: Int,@Param("name") name: String)

    @Transactional
    @Modifying(flushAutomatically = true)
    @Query("update Problem p set p.isReviewed = :isReviewed where p.id = :id")
    fun updateReviewedById(@Param("id") id: Int,@Param("isReviewed") isReviewed: Boolean)

    @Transactional
    @Modifying(flushAutomatically = true)
    @Query("update Problem p set p.description = :description where p.id = :id")
    fun updateDescriptionById(@Param("id") id: Int,@Param("description") description: String)
}
