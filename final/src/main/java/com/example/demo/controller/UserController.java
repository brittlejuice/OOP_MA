package com.example.demo.controller;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Iterator;

import com.example.demo.model.Comment;
import com.example.demo.model.CommentClass;
import com.example.demo.model.CommentCreator;
import com.example.demo.model.Post;
import com.example.demo.model.PostClass;
import com.example.demo.model.User;
import com.example.demo.model.UserClass;
import com.example.demo.model.requests.CommentCreation;
import com.example.demo.model.requests.CommentDelete;
import com.example.demo.model.requests.CommentEdit;
import com.example.demo.model.requests.CommentRetrieval;
import com.example.demo.model.requests.PostCreation;
import com.example.demo.model.requests.PostDelete;
import com.example.demo.model.requests.PostEdit;
import com.example.demo.model.requests.UserLoginRequest;
import com.example.demo.repo.CommentRepository;
import com.example.demo.repo.PostRepository;
import com.example.demo.repo.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("")
public class UserController {

    List<UserClass> users = new ArrayList<>();

    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;


    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UserLoginRequest userLoginRequest) {
        // Check if the user exists
        List<User> userList = new ArrayList<>();
        userRepository.findAll().forEach(userList::add);
        User user = null;
        for(User a:userList)
        {
            if(a.getEmail().equals(userLoginRequest.getEmail()))
            {
                user = a;
            }
        }
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User does not exist");
        }

        // Check if the password matches
        if (!user.getPassword().equals(userLoginRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username/Password Incorrect");
        }

        // Login successful
        return ResponseEntity.ok("Login Successful");
    }

       @PostMapping("/signup")
    public ResponseEntity<String> SignUp(@RequestBody User user) {
        try {
            User userObj = userRepository.save(user);
            UserClass uc = new UserClass();
            uc.setEmail(user.getEmail());
            uc.setName(user.getName());
            uc.setPassword(user.getPassword());
            uc.setUserID(user.getUserID());
            users.add(uc);
            return ResponseEntity.ok("Account Creation Successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Forbidden, Account already exists ");
        }
    }

       @PostMapping("/post")
    public ResponseEntity<?> postCreate(@RequestBody PostCreation pc) {
        for(UserClass a:users)
        {
            if(a.getUserID() == pc.getUserID())
            {
                PostClass p = new PostClass();
                Post pst = new Post();
                pst.setPostBody(pc.getPostBody());
                p.setPostBody(pc.getPostBody());
                postRepository.save(pst);
                p.setPostID(pst.getPostId());
                a.getPosts().add(p);
                // User x = new User();
                // x.setEmail(a.getEmail());
                // x.setName(a.getName());
                // x.setPassword(a.getPassword());
                // x.setUserID(a.getUserID());
                // userRepository.save(x);
                return new ResponseEntity<>(p, HttpStatus.CREATED);
            }
        }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User does not exist");
    }

    

    
    @PostMapping("/comment")
    public ResponseEntity<String> commentCreate(@RequestBody CommentCreation cc) {
        for(UserClass a:users)
        {
            for(PostClass i:a.getPosts())
            {
                if(i.getPostID() == cc.getPostID())
                {
                    CommentClass c = new CommentClass();
                    c.setCommentBody(cc.getCommentBody());
                    CommentCreator comcreate = new CommentCreator();
                    comcreate.setUserID(cc.getUserID());
                    for(UserClass o:users)
                    {
                        if(o.getUserID() == cc.getUserID())
                        {
                            comcreate.setName(o.getName());
                            c.setCommentCreator(comcreate);
                            Comment comment = new Comment();
                            comment.setCommentBody(c.getCommentBody());
                            commentRepository.save(comment);
                            c.setCommentID(comment.getCommentId());
                            i.getComments().add(c);
                            return ResponseEntity.ok("Comment created successfully");
                        }
                    }
                }
            }
        }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Comment does not exist");
    }

@PatchMapping("/post")
public ResponseEntity<String> postEdit(@RequestBody PostEdit pe) {
    for(UserClass a:users)
    {
        Optional<Post> postOptional = postRepository.findById(pe.getPostID());
        List<PostClass> l = new ArrayList<>();
            for(PostClass v: a.getPosts())
            {
                if(v.getPostID() == pe.getPostID())
                {
                    v.setPostBody(pe.getPostBody());
                    Post post = postOptional.get();
                    post.setPostBody(pe.getPostBody());
                    postRepository.save(post);
                    return ResponseEntity.ok("post edited successfully");
                }
            }
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Post does not exist");
}

@PatchMapping("/comment")
public ResponseEntity<String> commentEdit(@RequestBody CommentEdit ce) {
    Optional<Comment> commentOptional = commentRepository.findById(ce.getCommentID());
    for(UserClass a:users)
    {
        for(PostClass i:a.getPosts())
        {
            for(CommentClass k:i.getComments())
            {
                if(k.getCommentID() == ce.getCommentID())
                {
                    //int updatedRows = commentRepository.updateCommentBody(ce.getCommentID(), ce.getCommentBody());
                    k.setCommentBody(ce.getCommentBody());
                    Comment comment = commentOptional.get();
                    comment.setCommentBody(ce.getCommentBody());
                    commentRepository.save(comment);
                    return ResponseEntity.ok("Comment edited successfully");
                }
            }
        }
    }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Comment does not exist");
}


    @DeleteMapping("/post")
    public ResponseEntity<String> postDelete(@RequestBody PostDelete p) {
        Optional<Post> postOptional = postRepository.findById(p.getPostID());
        for (UserClass user : users) {
            Iterator<PostClass> iterator = user.getPosts().iterator();
            while (iterator.hasNext()) {
                PostClass post = iterator.next();
                if (post.getPostID()==p.getPostID()) {
                    iterator.remove(); // Safely remove the post from the list
                    postRepository.deleteById(p.getPostID());
                    return ResponseEntity.ok("Post deleted successfully");
                }
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
    }
    

    @DeleteMapping("/comment")
    public ResponseEntity<String> commentDelete(@RequestBody CommentDelete cd) {
        Optional<Comment> commentOptional = commentRepository.findById(cd.getCommentID());
        for (UserClass user : users) {
            for (PostClass post : user.getPosts()) {
                Iterator<CommentClass> iterator = post.getComments().iterator();
                while (iterator.hasNext()) {
                    CommentClass comment = iterator.next();
                    if (comment.getCommentID() == cd.getCommentID()) {
                        iterator.remove();
                        commentRepository.deleteById(cd.getCommentID());
                        // Assuming this operation involves database modification,
                        // consider adding transaction management here.
                        return ResponseEntity.ok("Comment deleted successfully");
                    }
                }
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment not found");
    }
    

    @GetMapping("/user")
    public ResponseEntity<?> userInfo(@RequestParam("userID") int userID) {
        // Check if the user exists
        // List<UserClass> userList = new ArrayList<>();
        // userRepository.findAll().forEach(userList::add);
        UserClass user = null;
        for(UserClass a:users)
        {
            if(a.getUserID() == userID)
            {
                user = a;
            }
        }
        if (user == null) {
            // String errorMessage = "User does not exist";
            return ResponseEntity.ok("User does not exist");

        }
        else
        {
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        }
    }


    @GetMapping("/post")
    public ResponseEntity<?> postInfo(@RequestParam("postID") int postID) {
        for (UserClass user : users) {
            Iterator<PostClass> iterator = user.getPosts().iterator();
            while (iterator.hasNext()) {
                PostClass post = iterator.next();
                if (post.getPostID()==postID) {
                    return new ResponseEntity<>(post, HttpStatus.CREATED);
                }
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post does not exist");
    }

    @GetMapping("/")
    public ResponseEntity<List<PostClass>> getChrono() {
        // Check if the user exists
        // List<UserClass> userList = new ArrayList<>();
        // userRepository.findAll().forEach(userList::add);
        UserClass user = null;
        List<PostClass> l = new ArrayList<>();
        for(UserClass a:users)
        {
            List<PostClass> c = a.getPosts();
            for(PostClass v: c)
            {
            l.add(v);
            }
        }
            return new ResponseEntity<>(l, HttpStatus.CREATED);
    }


    @GetMapping("/comment")
    public ResponseEntity<?> commentRetrieve(@RequestBody CommentRetrieval cr) {
        for(UserClass a:users)
        {
            for(PostClass i:a.getPosts())
            {
                for(CommentClass k:i.getComments())
                {
                    if(k.getCommentID() == cr.getCommentID())
                    {
                        return new ResponseEntity<>(k, HttpStatus.CREATED);
                    }
                }
            }
        }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Comment does not exist");
    }




    // @DeleteMapping("/comment")
    // public ResponseEntity<String> commentDelete(@RequestBody CommentDelete cd) {
    //     for(UserClass a:users)
    //     {
    //         for(PostClass i:a.getPosts())
    //         {
    //             for(CommentClass k:i.getComments())
    //             {
    //                 if(k.getCommentID() == cd.getCommentID())
    //                 {
    //                     i.getComments().remove(k);
    //                     return ResponseEntity.ok("Comment edited successfully");
    //                 }
    //             }
    //         }
    //     }
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Comment does not exist");
    // }


    @GetMapping("/users")
    public ResponseEntity<List<UserClass>> allUsers() {
        return new ResponseEntity<>(users, HttpStatus.CREATED);
    }

}
