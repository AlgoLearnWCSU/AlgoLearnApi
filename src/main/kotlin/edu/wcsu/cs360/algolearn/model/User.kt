package edu.wcsu.cs360.algolearn.model

import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.*

@Entity // This tells Hibernate to make a table out of this class
@Table(name="user_t") // change table name to avoid keyword conflict
class User {
    @Id
    var username: String? = null

    @Column(nullable = false, unique = true)
    var name: String? = null

    @Column(nullable = false, unique = true)
    var email: String? = null

    @Column(nullable = false)
    var isAdmin: Boolean? = null

    @Column
    var avatar_url: String? = null

    constructor()

    constructor(username: String?, name: String?, email: String?,
                isAdmin: Boolean?, avatar_url: String?) {
        this.username = username
        this.name = name
        this.email = email
        this.isAdmin = isAdmin
        this.avatar_url = avatar_url
    }
}

interface UserRepository : JpaRepository<User?, String?>
