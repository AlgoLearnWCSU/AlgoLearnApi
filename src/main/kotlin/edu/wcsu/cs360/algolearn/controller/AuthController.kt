package edu.wcsu.cs360.algolearn.controller

import edu.wcsu.cs360.algolearn.model.Auth
import edu.wcsu.cs360.algolearn.model.AuthRepository
import edu.wcsu.cs360.algolearn.model.User
import edu.wcsu.cs360.algolearn.model.UserRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.view.RedirectView
import java.security.Key
import java.util.*
import javax.crypto.spec.SecretKeySpec
import javax.xml.bind.DatatypeConverter


class GitHubAuthRequest(val code: String,
                        val client_id: String,
                        val client_secret: String)

class GitHubAuthResponse(val access_token: String,
                         val refresh_token: String,
                         val expires_in: Int,
                         val refresh_token_expires_in: Int)

class GitHubEmailResponse(val email: String?,
                          val primary: Boolean?)

class GitHubUserResponse(val login: String?,
                          val avatar_url: String?,
                          val name: String?)

private const val SECRET_KEY = """oeRaYY7Wo24sDqKSX3IM9ASGmdGPmkTd9jo1QTy4b7P9Ze5_9hKolVX8xNrQDcNRfVEdTZNOuOyqEGhXEbdJI-ZQ19k_o9MI0y3eZN2lp9jow55FfXMiINEdt1XR85VipRLSOkT6kSpzs2x-jbLDiz9iFVzkd81YKxMgPA7VfZeQUm4n-mOmnWMaVX30zGFU4L3oPBctYKkl4dYfqYWqRNfrgPJVi5DGFjywgxx0ASEiJHtV72paI3fDR2XwlSkyhhmY-ICjCRmsJN4fX1pdoL8a18-aQrvyu4j0Os6dVPYIoPvvY0SAZtWYKHfM15g7A3HD4cVREf9cUsprCRK93w"""

fun createJWT(id: String, issuer: String, subject: String, ttlMillis: Long): String? {
    //The JWT signature algorithm we will be using to sign the token

    //The JWT signature algorithm we will be using to sign the token
    val signatureAlgorithm = SignatureAlgorithm.HS256

    val nowMillis = System.currentTimeMillis()
    val now = Date(nowMillis)

    //We will sign our JWT with our ApiKey secret

    //We will sign our JWT with our ApiKey secret
    val apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY)
    val signingKey: Key = SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.jcaName)

    //Let's set the JWT Claims

    //Let's set the JWT Claims
    val builder = Jwts.builder().setId(id)
            .setIssuedAt(now)
            .setSubject(subject)
            .setIssuer(issuer)
            .signWith(signatureAlgorithm, signingKey)

    //if it has been specified, let's add the expiration

    //if it has been specified, let's add the expiration
    if (ttlMillis >= 0) {
        val expMillis: Long = nowMillis + ttlMillis
        val exp = Date(expMillis)
        builder.setExpiration(exp)
    }

    //Builds the JWT and serializes it to a compact, URL-safe string

    //Builds the JWT and serializes it to a compact, URL-safe string
    return builder.compact()
}

fun decodeJWT(jwt: String?): Claims? {
    //This line will throw an exception if it is not a signed JWS (as expected)
    return Jwts.parser()
            .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
            .parseClaimsJws(jwt).body
}

@RestController
@RequestMapping(path = ["/auth"])
class AuthController {
    @Autowired private val authRepository: AuthRepository? = null
    @Autowired private val userRepository: UserRepository? = null
    @Autowired private val bcrypt: BCryptPasswordEncoder? = null
    @Autowired private val restTemplateBuilder: RestTemplateBuilder? = null

    private var restTemplate: RestTemplate? = null


    @GetMapping(path = ["/login/{env}"])
    fun login(@RequestParam code: String, @PathVariable env: String): Any {
        var clientId: String
        var clientSecret: String
        val url = when(env) {
            "local" -> {
                clientId = "Iv1.2c3f97ee17f544a1"
                clientSecret = "4106b49d691b7b0cdee692efad30d8d74e633d89"
                "http://localhost:4200/#/home"
            }
            "dev" -> {
                clientId = "Iv1.500c711bc765c8f5"
                clientSecret = "5628f2b869e9469a7b415dda4f23ee5312144a36"
                "https://agreeable-mud-0fd849a0f.azurestaticapps.net/#/home"
            }
            else -> return ResponseEntity<Any>(HttpStatus.NOT_FOUND)
        }

        var email: String? = null

        if(restTemplate == null) {
            restTemplate = restTemplateBuilder!!.build()
        }
        // Get Auth Token
        val res: GitHubAuthResponse = restTemplate?.postForEntity(
                "https://github.com/login/oauth/access_token",
                GitHubAuthRequest(code, clientId, clientSecret),
                GitHubAuthResponse::class.java)?.body
                ?: return RedirectView("$url?login_failed=true")

        // Create Auth header
        val headers = HttpHeaders()
        headers["Authorization"] = "token ${res.access_token}"
        val entity = HttpEntity("", headers)

        // Get User emails
        val emails = restTemplate!!.exchange("https://api.github.com/user/emails", HttpMethod.GET, entity, Array<GitHubEmailResponse>::class.java).body
                ?: return RedirectView("$url?login_failed=true")

        // Set email from user emails
        emails.forEach {
            if(it.primary!!)
                email = it.email
        }

        // Get username, name, and avatar url
        val user = restTemplate!!.exchange("https://api.github.com/user", HttpMethod.GET, entity, GitHubUserResponse::class.java).body
                ?: return RedirectView("$url?login_failed=true")

        if (userRepository!!.findById(user.login!!).isEmpty) {
            userRepository.save(User(user.login, user.name, email, false, user.avatar_url))
        }

        val jwt = createJWT(res.access_token, user.login, email!!, 10800000)

        authRepository!!.save(Auth(bcrypt!!.encode(jwt), user.login))

        println("${user.login}, ${user.name}, ${email}, ${user.avatar_url}")

        val test = decodeJWT(jwt)

        println(test?.id)

        return RedirectView("$url?jwt=$jwt&username=${user.login}")
    }
}