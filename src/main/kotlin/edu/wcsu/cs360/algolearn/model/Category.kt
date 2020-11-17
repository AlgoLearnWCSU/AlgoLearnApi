package edu.wcsu.cs360.algolearn.model

import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.*

@Entity(name = "category") // This tells Hibernate to make a table out of this class
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

interface CategoryRepository : JpaRepository<Category?, Int?>
