package edu.wcsu.cs360.algolearn.controller

import edu.wcsu.cs360.algolearn.model.Comment
import edu.wcsu.cs360.algolearn.model.CommentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/comment"])
class CommentController {
    @Autowired
    private val commentRepository: CommentRepository? = null

    @GetMapping
    fun getDemComments(): List<Comment?> {
        return commentRepository!!.findAll().toList()
    }

    @GetMapping(path = ["/{commentId}"])
    fun getDemCommentsById(@PathVariable commentId: Int?): Any {
        val data = commentRepository!!.findById(commentId!!)
        if (data.isEmpty)
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
        return data.get()
    }

    @PostMapping // Map ONLY POST Requests
    fun addNewComment(@RequestBody comment: Comment): Any {
        if (comment.id != null &&
                commentRepository!!.findById(comment.id!!).isPresent)
            return ResponseEntity<Any>(HttpStatus.CONFLICT)
        if(comment.timestamp == null){
            comment.timestamp = java.util.Date()
        }
        commentRepository?.save(comment)
        return comment
    }

    @PutMapping(path = ["/{id}"])
    fun replaceComment(@PathVariable id: Int, @RequestBody comment: Comment): Any {
        if (commentRepository!!.findById(id).isEmpty)
            return ResponseEntity<Any>(HttpStatus.NOT_FOUND)

        comment.id = id
        commentRepository.updateCommenterById(id, comment.commenter!!)
        commentRepository.updateProblemById(id, comment.problem!!)
        commentRepository.updateTimeStampById(id, comment.timestamp!!)
        commentRepository.updateCommentById(id, comment.comment!!)

        return comment
    }

    @PatchMapping(path = ["/{id}"])
    fun modifyComment(@PathVariable id: Int, @RequestBody comment: Comment): Any {
        val oldComment = commentRepository!!.findById(id)
        if (oldComment.isEmpty)
            return ResponseEntity<Any>(HttpStatus.NOT_FOUND)


        comment.id = id
        if(comment.commenter != null)
            commentRepository.updateCommenterById(id, comment.commenter!!)
        else
            comment.commenter = oldComment.get().commenter
        if(comment.problem != null)
            commentRepository.updateProblemById(id, comment.problem!!)
        else
            comment.problem = oldComment.get().problem
        if(comment.timestamp != null)
            commentRepository.updateTimeStampById(id, comment.timestamp!!)
        else
            comment.timestamp = oldComment.get().timestamp
        if(comment.comment != null)
            commentRepository.updateCommentById(id, comment.comment!!)
        else
            comment.comment = oldComment.get().comment

        return comment
    }

    @DeleteMapping(path = ["/{commentId}"])
    fun deleteCommentById(@PathVariable commentId: Int): ResponseEntity<Any> {
        if (commentRepository!!.findById(commentId).isEmpty)
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        commentRepository.deleteById(commentId)
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping(path = ["/search"])
    fun searchParam(@RequestParam id: Int?,
                    @RequestParam commenter: String?,
                    @RequestParam problem: Int?,
                    @RequestParam timestamp: java.util.Date?,
                    @RequestParam comment: String?): Any {
        val matcher: ExampleMatcher = ExampleMatcher
                .matchingAll()
                .withMatcher("id", exact())
                .withMatcher("commenter", contains().ignoreCase())
                .withMatcher("problem", exact())
                .withMatcher("timestamp", exact())
                .withMatcher("comment", contains().ignoreCase())

        return commentRepository!!.findAll(
                Example.of(Comment(id,
                        commenter,
                        problem,
                        timestamp,
                        comment), matcher))
    }
}
