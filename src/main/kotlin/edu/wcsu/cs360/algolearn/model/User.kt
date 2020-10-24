package edu.wcsu.cs360.algolearn.model

import org.springframework.data.repository.CrudRepository
import javax.persistence.*

@Entity // This tells Hibernate to make a table out of this class
@Table(name="user_t", schema="public") // change table name to avoid keyword conflict
class User {
    @Id
    var username: String? = null

    @Column(nullable = false, unique = true)
    var name: String? = null

    @Column(nullable = false, unique = true)
    var email: String? = null

    @Column(nullable = false)
    var isAdmin: Boolean? = null
}

interface UserRepository : CrudRepository<User?, String?>
