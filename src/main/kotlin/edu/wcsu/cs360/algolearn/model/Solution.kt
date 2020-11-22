package edu.wcsu.cs360.algolearn.model

import org.springframework.data.repository.CrudRepository
import javax.persistence.*


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

    @Column(nullable = false)
    var passedTests: Int? = null

    @Column(nullable = false)
    @Temporal(TemporalType.TIME)
    var avgCompTime: java.util.Date? = null

}

interface SolutionRepository : CrudRepository<Solution?, Int?>
