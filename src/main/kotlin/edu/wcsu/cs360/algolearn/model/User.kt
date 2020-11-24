package edu.wcsu.cs360.algolearn.model

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import javax.persistence.*
import javax.transaction.Transactional

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

interface UserRepository : JpaRepository<User?, String?> {
    @Transactional
    @Modifying
    @Query("update User u set u.name = :name where u.username = :username")
    fun updateNameByUsername(@Param("username")username: String, @Param("name")name: String)

    @Transactional
    @Modifying
    @Query("update User u set u.email =:email where u.username = :username")
    fun updateEmailByUsername(@Param("username")username: String, @Param("email")email: String)

    @Transactional
    @Modifying
    @Query("update User u set u.isAdmin = :isAdmin where u.username = :username")
    fun updateIsAdminByUsername(@Param("username")username: String, @Param("isAdmin")isAdmin: Boolean)

    @Transactional
    @Modifying
    @Query("update User u set u.avatar_url = :avatar_url where u.username = :username")
    fun updateAvatar_urlByUsername(@Param("username")username: String, @Param("avatar_url")avatar_url: String)
}
