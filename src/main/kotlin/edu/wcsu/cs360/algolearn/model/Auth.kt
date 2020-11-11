package edu.wcsu.cs360.algolearn.model

import org.springframework.data.repository.CrudRepository
import javax.persistence.*


@Entity // This tells Hibernate to make a table out of this class
class Auth {
    @Id
    @Column(nullable = false)
    var access_token: String? = null

    @Column(nullable = false)
    var username: String? = null

    constructor()

    constructor(access_token: String?, username: String?) {
        this.access_token = access_token
        this.username = username
    }
}

interface AuthRepository : CrudRepository<Auth?, String?>