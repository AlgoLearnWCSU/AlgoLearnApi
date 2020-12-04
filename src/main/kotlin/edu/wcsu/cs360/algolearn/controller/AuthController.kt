package edu.wcsu.cs360.algolearn.controller

import edu.wcsu.cs360.algolearn.model.Auth
import edu.wcsu.cs360.algolearn.model.AuthRepository
import edu.wcsu.cs360.algolearn.model.User
import edu.wcsu.cs360.algolearn.model.UserRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.security.Key
import java.util.*
import javax.crypto.spec.SecretKeySpec
import javax.servlet.http.HttpServletResponse
import javax.xml.bind.DatatypeConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.core.env.Environment
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.view.RedirectView

class GitHubAuthRequest(
    val code: String,
    val client_id: String,
    val client_secret: String
)

class GitHubAuthResponse(
    val access_token: String?,
    val refresh_token: String?,
    val expires_in: Int?,
    val refresh_token_expires_in: Int?
)

class GitHubRefreshRequest(
    val refresh_token: String?,
    val grant_type: String?,
    val client_id: String?,
    val client_secret: String?
)

class GitHubEmailResponse(
    val email: String?,
    val primary: Boolean?
)

class GitHubUserResponse(
    val login: String?,
    val avatar_url: String?,
    var name: String?
)

class AuthRequest {
    var code: String? = null
}

data class AuthUser(
    val username: String?,
    val jwt: String?,
    val expires_in: Int
)

private var SECRET_KEY: String? = null

fun createJWT(id: String, issuer: String, subject: String, ttlMillis: Long): String? {
    // The JWT signature algorithm we will be using to sign the token

    // The JWT signature algorithm we will be using to sign the token
    val signatureAlgorithm = SignatureAlgorithm.HS256

    val nowMillis = System.currentTimeMillis()
    val now = Date(nowMillis)

    // We will sign our JWT with our ApiKey secret

    // We will sign our JWT with our ApiKey secret
    val apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY)
    val signingKey: Key = SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.jcaName)

    // Let's set the JWT Claims

    // Let's set the JWT Claims
    val builder = Jwts.builder().setId(id)
            .setIssuedAt(now)
            .setSubject(subject)
            .setIssuer(issuer)
            .signWith(signatureAlgorithm, signingKey)

    // if it has been specified, let's add the expiration

    // if it has been specified, let's add the expiration
    if (ttlMillis >= 0) {
        val expMillis: Long = nowMillis + ttlMillis
        val exp = Date(expMillis)
        builder.setExpiration(exp)
    }

    // Builds the JWT and serializes it to a compact, URL-safe string

    // Builds the JWT and serializes it to a compact, URL-safe string
    return builder.compact()
}

fun decodeJWT(jwt: String?): Claims? {
    // This line will throw an exception if it is not a signed JWS (as expected)
    return Jwts.parser()
            .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
            .parseClaimsJws(jwt).body
}

@RestController
@RequestMapping(path = ["/auth"])
class AuthController {
    @Autowired
    private val authRepository: AuthRepository? = null

    @Autowired
    private val userRepository: UserRepository? = null

    @Autowired
    private val restTemplateBuilder: RestTemplateBuilder? = null

    private var restTemplate: RestTemplate? = null

    @Autowired
    private val osEnv: Environment? = null

    @PostMapping(path = ["/login/{env}"])
    fun login(@RequestBody code: AuthRequest?, @PathVariable env: String, response: HttpServletResponse): Any {

        if (SECRET_KEY == null) {
            SECRET_KEY = osEnv!!.getProperty("secrets.bcrypt_secret_key")!!
        }

        val clientId: String
        val clientSecret: String
        val secureString: String
        when (env) {
            "local" -> {
                clientId = "Iv1.2c3f97ee17f544a1"
                clientSecret = osEnv!!.getProperty("secrets.github-secret-local")!!
                secureString = ""
            }
            "dev" -> {
                clientId = "Iv1.500c711bc765c8f5"
                clientSecret = osEnv!!.getProperty("secrets.github-secret-dev")!!
                secureString = "Secure; SameSite=None;"
            }
            else -> return ResponseEntity<Any>(HttpStatus.NOT_FOUND)
        }

        var email: String? = null

        if (restTemplate == null) {
            restTemplate = restTemplateBuilder!!.build()
        }

        // Get Auth Token
        try {
            val res: GitHubAuthResponse = restTemplate?.postForEntity(
                    "https://github.com/login/oauth/access_token",
                    GitHubAuthRequest(code!!.code!!, clientId, clientSecret),
                    GitHubAuthResponse::class.java)?.body
                    ?: return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)

            // Create Auth header
            val headers = HttpHeaders()
            headers["Authorization"] = "token ${res.access_token}"
            val entity = HttpEntity("", headers)

            // Get User emails
            val emails = restTemplate!!.exchange("https://api.github.com/user/emails", HttpMethod.GET, entity, Array<GitHubEmailResponse>::class.java).body
                    ?: return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)

            // Set email from user emails
            emails.forEach {
                if (it.primary!!)
                    email = it.email
            }

            // Get username, name, and avatar url
            val user = restTemplate!!.exchange("https://api.github.com/user", HttpMethod.GET, entity, GitHubUserResponse::class.java).body
                    ?: return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)

            if (user.name == null) {
                user.name = user.login
            }

            if (userRepository!!.findById(user.login!!).isEmpty) {
                userRepository.save(User(user.login, user.name, email, false, user.avatar_url))
            }

            val jwt = createJWT(res.access_token!!, user.login, email!!, 10800000)

            authRepository!!.save(Auth(jwt, user.login))

            val responseHeaders = HttpHeaders()
            responseHeaders.add("Set-Cookie", "refresh_token=${res.refresh_token}; Max-Age=604800; Path=/; $secureString HttpOnly;")
            return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(AuthUser(user.login, jwt, res.expires_in!!))
        } catch (err: HttpClientErrorException) {
            println(err)
            if (err.rawStatusCode == 401) {
                return ResponseEntity<Any>(HttpStatus.UNAUTHORIZED)
            }
        }
        return ""
    }

    @GetMapping(path = ["/refresh/{env}"])
    fun refresh(@CookieValue refresh_token: String?, @PathVariable env: String, response: HttpServletResponse): Any {
        if (SECRET_KEY == null) {
            SECRET_KEY = osEnv!!.getProperty("secrets.bcrypt_secret_key")!!
        }

        val clientId: String
        val clientSecret: String
        val secureString: String
        when (env) {
            "local" -> {
                clientId = "Iv1.2c3f97ee17f544a1"
                clientSecret = osEnv!!.getProperty("secrets.github-secret-local")!!
                secureString = ""
            }
            "dev" -> {
                clientId = "Iv1.500c711bc765c8f5"
                clientSecret = osEnv!!.getProperty("secrets.github-secret-dev")!!
                secureString = "Secure; SameSite=None;"
            }
            else -> return ResponseEntity<Any>(HttpStatus.NOT_FOUND)
        }

        var email: String? = null

        if (restTemplate == null) {
            restTemplate = restTemplateBuilder!!.build()
        }

        // Get Auth Token
        try {
            val res: GitHubAuthResponse = restTemplate?.postForEntity(
                    "https://github.com/login/oauth/access_token",
                    GitHubRefreshRequest(refresh_token, "refresh_token", clientId, clientSecret),
                    GitHubAuthResponse::class.java)?.body
                    ?: return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)

            // Create Auth header
            val headers = HttpHeaders()
            headers["Authorization"] = "token ${res.access_token}"
            val entity = HttpEntity("", headers)

            // Get User emails
            val emails = restTemplate!!.exchange("https://api.github.com/user/emails", HttpMethod.GET, entity, Array<GitHubEmailResponse>::class.java).body
                    ?: return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)

            // Set email from user emails
            emails.forEach {
                if (it.primary!!)
                    email = it.email
            }

            // Get username, name, and avatar url
            val user = restTemplate!!.exchange("https://api.github.com/user", HttpMethod.GET, entity, GitHubUserResponse::class.java).body
                    ?: return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)

            if (user.name == null) {
                user.name = user.login
            }

            if (userRepository!!.findById(user.login!!).isEmpty) {
                userRepository.save(User(user.login, user.name, email, false, user.avatar_url))
            }

            val jwt = createJWT(res.access_token!!, user.login, email!!, 10800000)

            authRepository!!.save(Auth(jwt, user.login))

            val responseHeaders = HttpHeaders()
            responseHeaders.add("Set-Cookie", "refresh_token=${res.refresh_token}; Max-Age=604800; Path=/; $secureString HttpOnly")
            return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(AuthUser(user.login, jwt, res.expires_in!!))
        } catch (err: HttpClientErrorException) {
            println(err)
            if (err.rawStatusCode == 401) {
                return ResponseEntity<Any>(HttpStatus.UNAUTHORIZED)
            }
        }
        return ""
    }

    @GetMapping("/redirect/{env}")
    fun redirectToLocal(@RequestParam code: String, @PathVariable env: String): Any {
        if (SECRET_KEY == null) {
            SECRET_KEY = osEnv!!.getProperty("secrets.bcrypt_secret_key")!!
        }

        val url = when (env) {
            "local" -> "http://localhost:4200/#/home"
            "dev" -> "https://www.algolearn.dev/#/home"
            else -> return ResponseEntity<Any>(HttpStatus.NOT_FOUND)
        }
        return RedirectView("$url?code=$code")
    }
}
