package com.ql.giapha.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ql.giapha.dto.FamilyDTO;
import com.ql.giapha.dto.RelationshipDTO;
import com.ql.giapha.model.AppUser;
import com.ql.giapha.model.Child;
import com.ql.giapha.model.Family;
import com.ql.giapha.model.Gender;
import com.ql.giapha.model.Parent;
import com.ql.giapha.model.Person;
import com.ql.giapha.model.Sibling;
import com.ql.giapha.model.Spouse;
import com.ql.giapha.repository.FamilyRepo;
import com.ql.giapha.repository.PersonRepo;
import com.ql.giapha.repository.UserRepo;
import com.ql.giapha.service.UserDetailsImpl;

import lombok.Data;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    @Autowired
    FamilyRepo familyRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    PersonRepo personRepo;

    @GetMapping("")
    public ResponseEntity<List<CalendarDTO>> getAllEvents() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginedUser = (UserDetailsImpl) (authentication.getPrincipal());

        AppUser user = userRepo.findById(loginedUser.getId()).orElse(null);
        if (user != null) {
            List<Family> families = familyRepo.findAllByOwner(user);
            List<CalendarDTO> events = new ArrayList<>();

            for (Family family : families) {
                List<Person> members = family.getMembers();
                for (Person person : members) {
                    if (person.isAlive()) {
                        // Nếu person còn sống và dob không null, thêm sự kiện ngày sinh nhật vào danh
                        // sách
                        if (person.getDob() != null) {
                            CalendarDTO birthdayEvent = new CalendarDTO();
                            birthdayEvent.setEvent("Sinh nhật " + person.getName());
                            birthdayEvent.setDate(person.getDob());
                            birthdayEvent.setAlive(true);
                            events.add(birthdayEvent);
                        }
                    } else {
                        // Nếu person đã mất và dod không null, thêm sự kiện ngày mất vào danh sách
                        if (person.getDod() != null) {
                            CalendarDTO deathEvent = new CalendarDTO();
                            deathEvent.setEvent("Ngày giỗ " + person.getName());
                            deathEvent.setDate(person.getDod());
                            deathEvent.setAlive(false);
                            events.add(deathEvent);
                        }
                    }
                }
            }
            return ResponseEntity.ok(events);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}

@Data
class CalendarDTO {

    private String event;
    private Date date;
    private boolean alive;

    // Getters and setters
}
