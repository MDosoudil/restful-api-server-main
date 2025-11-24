package utb.fai.RESTAPIServer;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/users")
    public ResponseEntity<List<MyUser>> getAllUsers() {
        List<MyUser> users = repository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/getUser")
    public ResponseEntity<MyUser> getUserById(@RequestParam(name = "id") Long id) {
        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Optional<MyUser> found = repository.findById(id);
        return found.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/createUser")
    public ResponseEntity<MyUser> createUser(@RequestBody MyUser newUser) {
        newUser.setId(null);
        if (!newUser.isUserDataValid()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        MyUser saved = repository.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/editUser")
    public ResponseEntity<MyUser> editUser(@RequestParam(name = "id", required = false) Long id,
                                           @RequestBody(required = false) MyUser updated) {
        if (id == null && updated != null && updated.getId() != null) {
            id = updated.getId();
        }
        if (id == null || updated == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Optional<MyUser> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        MyUser existing = existingOpt.get();
        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        existing.setPhoneNumber(updated.getPhoneNumber());
        if (!existing.isUserDataValid()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        MyUser saved = repository.save(existing);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<String> deleteUser(@RequestParam(name = "id") Long id) {
        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid id");
        }
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }
        try {
            repository.deleteById(id);
            return ResponseEntity.ok("Deleted id=" + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Delete failed");
        }
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deleteAllUsers() {
        try {
            repository.deleteAll();
            return ResponseEntity.ok("Deleted all");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Delete all failed");
        }
    }
}
