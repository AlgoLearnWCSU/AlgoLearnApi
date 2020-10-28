package edu.wcsu.cs360.algolearn.model

import org.springframework.data.repository.CrudRepository
import javax.persistence.*


@Entity // This tells Hibernate to make a table out of this class
@Table(name = "test_case", schema = "public") // change table name to avoid keyword conflict
class TestCase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Int? = null

    @Column(nullable = false)
    var problem: Int? = null

    @Column(nullable = false)
    var public: Boolean? = null

    @Column(nullable = false)
    @Lob
    var sampleInput: String? = null

    @Column(nullable = false)
    @Lob
    var sampleOutput: String? = null

}

interface TestCaseRepository : CrudRepository<TestCase?, Int?>