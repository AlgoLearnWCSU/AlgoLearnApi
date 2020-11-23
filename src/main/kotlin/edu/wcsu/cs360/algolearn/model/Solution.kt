package edu.wcsu.cs360.algolearn.model

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import javax.persistence.*
import javax.transaction.Transactional


@Entity // This tells Hibernate to make a table out of this class
class Solution {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Int? = null

    @Column(nullable = false)
    var solver: String? = null

    @Column(nullable = false)
    var problem: Int? = null

    @Column(nullable = false)
    @Lob
    var code: String? = null

    @Column(nullable = false)
    var languageId: Int? = null

    @Column(nullable = false)
    var tokens: String? = null

    @Column(nullable = true)
    var passedTests: Int? = null

    @Column(nullable = true)
    var avgCompTime: Double? = null

    constructor()

    constructor(id: Int?, solver: String?, problem: Int?, code: String?, languageId: Int?,
                tokens: String?, passedTests: Int?, avgCompTime: Double?) {
        this.id = id
        this.solver = solver
        this.problem = problem
        this.code = code
        this.languageId = languageId
        this.tokens = tokens
        this.passedTests = passedTests
        this.avgCompTime = avgCompTime
    }
}

interface SolutionRepository : JpaRepository<Solution?, Int?>{
    @Transactional
    @Modifying
    @Query("update Solution s set s.solver = :solver where s.id = :id")
    fun updateSolverById(@Param("id")id: Int, @Param("solver")solver: String)

    @Transactional
    @Modifying
    @Query("update Solution s set s.problem = :problem where s.id = :id")
    fun updateProblemById(@Param("id")id: Int, @Param("problem")problem: Int)

    @Transactional
    @Modifying
    @Query("update Solution s set s.code = :code where s.id = :id")
    fun updateCodeById(@Param("id")id: Int, @Param("code")code: String)

    @Transactional
    @Modifying
    @Query("update Solution s set s.languageId = :languageId where s.id = :id")
    fun updateLanguageIdById(@Param("id")id: Int, @Param("languageId")languageId: Int)

    @Transactional
    @Modifying
    @Query("update Solution s set s.tokens = :tokens where s.id = :id")
    fun updateTokensById(@Param("id")id: Int, @Param("tokens")tokens: String)

    @Transactional
    @Modifying
    @Query("update Solution s set s.passedTests = :passedTests where s.id = :id")
    fun updatePassedTestsById(@Param("id")id: Int, @Param("passedTests")passedTests: Int)

    @Transactional
    @Modifying
    @Query("update Solution s set s.avgCompTime = :avgCompTime where s.id = :id")
    fun updateAvgCompTimeById(@Param("id")id: Int, @Param("avgCompTime")avgCompTime: Double)
}
