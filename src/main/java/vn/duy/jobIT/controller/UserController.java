package vn.duy.jobIT.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.duy.jobIT.domain.User;
import vn.duy.jobIT.domain.dto.UpdateUserDTO;
import vn.duy.jobIT.domain.res.user.CreatedUserResponse;
import vn.duy.jobIT.domain.res.ResultPaginationResponse;
import vn.duy.jobIT.domain.res.user.UpdatedUserResponse;
import vn.duy.jobIT.service.UserService;
import vn.duy.jobIT.util.annotation.ApiMessage;

@RequestMapping(path = "${apiPrefix}/users")
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @PostMapping("")
    @ApiMessage("Create a user")
    public ResponseEntity<CreatedUserResponse> createUser(@Valid @RequestBody User user) {
        CreatedUserResponse newUser = this.userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @GetMapping("")
    @ApiMessage("Fetch all user data")
    public ResponseEntity<ResultPaginationResponse> getAllUser(
            @Filter Specification<User> spec,
            Pageable pageable
    ){
        return ResponseEntity.status(HttpStatus.OK)
                .body(this.userService.getAllUser(pageable, spec));
    }

    @GetMapping("/{id}")
    @ApiMessage("Fetch user by id")
    public ResponseEntity<CreatedUserResponse> fetchUserById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(this.userService.fetchUserById(id));
    }

    @PutMapping("/{id}")
    @ApiMessage("Update a user")
    public ResponseEntity<UpdatedUserResponse> updateUser(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateUserDTO user
    ) {
        return ResponseEntity.ok(this.userService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        this.userService.deleteUser(id);
        return ResponseEntity.ok(null);
    }
}
