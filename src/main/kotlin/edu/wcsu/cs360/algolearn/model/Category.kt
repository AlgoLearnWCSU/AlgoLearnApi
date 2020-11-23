package edu.wcsu.cs360.algolearn.model

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import javax.persistence.*
import javax.transaction.Transactional

@Entity // This tells Hibernate to make a table out of this class
class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null

    @Column(nullable = false)
    var problem: Int? = null

    @Column(nullable = false)
    var name: String? = null

    constructor()

    constructor(id: Int?, problem: Int?, name: String?) {
        this.id = id
        this.problem = problem
        this.name = name
    }

}

interface CategoryRepository : JpaRepository<Category?, Int?>{


    @Transactional
    @Modifying(flushAutomatically = true)
    @Query("update Category c set c.name = :name where c.id = :id")
    fun updateNameById(@Param("id") id: Int, @Param("name") name: String)

    @Transactional
    @Modifying(flushAutomatically = true)
    @Query("update Category c set c.problem = :problem where c.id = :id")
    fun updateProblemById(@Param("id") id: Int, @Param("problem") problem: Int)



}


