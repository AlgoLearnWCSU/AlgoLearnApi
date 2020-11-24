package edu.wcsu.cs360.algolearn.model

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import javax.persistence.*
import javax.transaction.Transactional


@Entity // This tells Hibernate to make a table out of this class
class TestCase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Int? = null

    @Column(nullable = false)
    var problem: Int? = null

    @Column(nullable = false)
    var isPublic: Boolean? = null

    @Column(nullable = false)
    @Lob
    var sampleInput: String? = null

    @Column(nullable = false)
    @Lob
    var sampleOutput: String? = null

    constructor() { }

    constructor(id:Int?, problem:Int?, isPublic:Boolean?, sampleInput:String?, sampleOutput:String?){
        this.id = id
        this.problem = problem
        this.isPublic = isPublic
        this.sampleInput = sampleInput
        this.sampleOutput = sampleOutput
    }

}

interface TestCaseRepository : JpaRepository<TestCase?, Int?> {
    @Transactional
    @Modifying
    @Query("update TestCase t set t.problem = :problem where t.id = :id")
    fun updateProblemById(@Param("id")id: Int, @Param("problem")problem: Int)

    @Transactional
    @Modifying
    @Query("update TestCase t set t.isPublic = :isPublic where t.id = :id")
    fun updateIsPublicById(@Param("id")id: Int, @Param("isPublic")ispublic: Boolean)

    @Transactional
    @Modifying
    @Query("update TestCase t set t.sampleInput = :sampleInput where t.id = :id")
    fun updateSampleInputById(@Param("id")id: Int, @Param("sampleInput")sampleInput: String)

    @Transactional
    @Modifying
    @Query("update TestCase t set t.sampleOutput = :sampleOutput where t.id = :id")
    fun updateSampleOutputById(@Param("id")id: Int, @Param("sampleOutput")sampleOutput: String)
}
