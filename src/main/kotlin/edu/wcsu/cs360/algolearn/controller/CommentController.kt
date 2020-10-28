package edu.wcsu.cs360.algolearn.controller

import edu.wcsu.cs360.algolearn.model.Comment
import edu.wcsu.cs360.algolearn.model.CommentRepository
import org.springframework.beans.factory.annotation.Autowired
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
        println("test")
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
        commentRepository?.save(comment)
        return comment
    }

    @PutMapping(path = ["/{commentId}"])
    fun replaceComment(@PathVariable commentId: Int, @RequestBody comment: Comment): Any {
        if (commentRepository!!.findById(commentId).isEmpty)
            return ResponseEntity<Any>(HttpStatus.NOT_FOUND)
        commentRepository.save(comment)
        return comment
    }

    @PatchMapping(path = ["/{commentId}"])
    fun modifyComment(@PathVariable commentId: Int, @RequestBody comment: Comment): Any {
        val oldComment = commentRepository!!.findById(commentId)
        if (oldComment.isEmpty)
            return ResponseEntity<Any>(HttpStatus.NOT_FOUND)

        if(commentId != comment.id)
            commentRepository.deleteById(commentId)

        if(comment.id == null)
            comment.id = commentId
        if(comment.commenter == null)
            comment.commenter = oldComment.get().commenter
        if(comment.problem == null)
            comment.problem = oldComment.get().problem
        if(comment.timestamp == null)
            comment.timestamp = oldComment.get().timestamp
        if(comment.comment == null)
            comment.comment = oldComment.get().comment

        commentRepository.save(comment)

        return comment
    }

    @DeleteMapping(path = ["/{commentId}"])
    fun deleteCommentById(@PathVariable commentId: Int): ResponseEntity<Any> {
        if (commentRepository!!.findById(commentId).isEmpty)
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        commentRepository.deleteById(commentId)
        return ResponseEntity(HttpStatus.OK)
    }
}