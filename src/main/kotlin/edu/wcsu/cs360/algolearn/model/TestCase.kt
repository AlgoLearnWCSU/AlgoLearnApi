package edu.wcsu.cs360.algolearn.model

import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.*


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

interface TestCaseRepository : JpaRepository<TestCase?, Int?>
