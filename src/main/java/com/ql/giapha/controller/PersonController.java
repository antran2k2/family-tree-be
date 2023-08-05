package com.ql.giapha.controller;

import java.util.List;
import java.util.Optional;

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

import com.ql.giapha.model.AppUser;
import com.ql.giapha.model.Child;
import com.ql.giapha.model.Family;
import com.ql.giapha.model.Gender;
import com.ql.giapha.model.Parent;
import com.ql.giapha.model.Person;
import com.ql.giapha.model.RelationshipType;
import com.ql.giapha.model.Sibling;
import com.ql.giapha.model.Spouse;
import com.ql.giapha.repository.ChildRepo;
import com.ql.giapha.repository.FamilyRepo;
import com.ql.giapha.repository.ParentRepo;
import com.ql.giapha.repository.PersonRepo;
import com.ql.giapha.repository.SiblingRepo;
import com.ql.giapha.repository.SpouseRepo;
import com.ql.giapha.repository.UserRepo;
import com.ql.giapha.service.UserDetailsImpl;

import jakarta.transaction.Transactional;
import lombok.Data;

@RestController
@RequestMapping("/api/person")
public class PersonController {

    @Autowired
    FamilyRepo familyRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    PersonRepo personRepo;

    @Autowired
    ChildRepo childRepo;

    @Autowired
    ParentRepo parentRepo;

    @Autowired
    SiblingRepo siblingRepo;

    @Autowired
    SpouseRepo spouseRepo;

    @GetMapping("")
    public ResponseEntity<?> getAllMembers(@RequestParam(value = "id", required = false) Long id) {
        Person person = personRepo.findById(id).orElse(null);
        if (person != null) {
            PersonDTO personDTO = new PersonDTO();
            personDTO.setId(person.getIdPerson());
            personDTO.setName(person.getName());
            personDTO.setGender(person.getGender());
            return ResponseEntity.ok(personDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/upload/{id}")
    public ResponseEntity<String> uploadPerson(@PathVariable("id") Long id, @RequestBody PersonDTO personDTO) {
        Person person = personRepo.findById(id).orElse(null);
        if (person != null) {
            person.setName(personDTO.getName());
            person.setGender(personDTO.getGender());
            person.setDetails(personDTO.getDetails());
            person.setDob(personDTO.getDob());
            person.setDod(personDTO.getDod());
            person.setAlive(personDTO.isAlive());
            return ResponseEntity.ok("Cập nhật thông tin thành công");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/addPerson")
    public ResponseEntity<String> addPerson(@RequestBody PersonRequestDTO personRequestDTO) {
        // Lấy thông tin người dùng đã xác thực từ SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginedUser = (UserDetailsImpl) authentication.getPrincipal();

        // Tìm người dùng từ database bằng ID đã xác thực
        AppUser user = userRepo.findById(loginedUser.getId()).orElse(null);

        if (user != null) {
            // Tìm family bằng familyId từ PersonRequestDTO
            Optional<Family> optionalFamily = familyRepo.findById(personRequestDTO.getFamilyId());
            if (optionalFamily.isPresent()) {
                Family family = optionalFamily.get();

                // Kiểm tra xem người dùng hiện tại có quyền thêm Person vào family này hay
                // không
                if (family.getOwner().equals(user)) {
                    // Tạo mới đối tượng Person từ thông tin trong PersonRequestDTO và family đã tìm
                    // thấy

                    Person person = new Person();
                    person.setGender(personRequestDTO.getGender());
                    person.setName(personRequestDTO.getName());
                    person.setFamily(family);
                    personRepo.save(person);
                    // Các trường và quan hệ khác nếu có

                    Person parent = personRepo.findById(personRequestDTO.getFromId()).get();
                    List<Child> siblings = parent.getChildren();

                    for (Child sibling : siblings) {
                        Sibling relation = new Sibling();
                        Person ae = sibling.getChild();
                        relation.setSibling(person);
                        relation.setPerson(ae);
                        relation.setType(RelationshipType.blood);
                        ae.getSiblings().add(relation);
                        personRepo.save(ae);

                        Sibling relation2 = new Sibling();
                        relation2.setSibling(ae);
                        relation2.setPerson(person);
                        relation2.setType(RelationshipType.blood);
                        person.getSiblings().add(relation2);

                    }

                    Child child = new Child();
                    child.setPerson(parent);
                    child.setChild(person);
                    child.setType(RelationshipType.blood);
                    parent.getChildren().add(child);

                    Parent pare = new Parent();
                    pare.setPerson(person);
                    pare.setParent(parent);
                    pare.setType(RelationshipType.blood);
                    person.getParents().add(pare);

                    if (!parent.getSpouses().isEmpty()) {
                        Parent par = new Parent();
                        Person p = parent.getSpouses().get(0).getSpouse();
                        par.setPerson(person);
                        par.setParent(p);
                        par.setType(RelationshipType.blood);
                        person.getParents().add(par);

                        Child child2 = new Child();
                        child2.setPerson(p);
                        child2.setChild(person);
                        child2.setType(RelationshipType.blood);
                        p.getChildren().add(child2);
                        personRepo.save(p);

                    }
                    // Lưu Person vào database
                    personRepo.save(parent);
                    personRepo.save(person);

                    // Trả về thông báo thành công nếu muốn
                    return ResponseEntity.ok("Person added successfully.");
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("You don't have permission to add a person to this family.");
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/married")
    public ResponseEntity<String> married(@RequestBody PersonRequestDTO personRequestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginedUser = (UserDetailsImpl) authentication.getPrincipal();

        // Tìm người dùng từ database bằng ID đã xác thực
        AppUser user = userRepo.findById(loginedUser.getId()).orElse(null);

        if (user != null) {
            // Tìm family bằng familyId từ PersonRequestDTO
            Optional<Family> optionalFamily = familyRepo.findById(personRequestDTO.getFamilyId());
            if (optionalFamily.isPresent()) {
                Family family = optionalFamily.get();

                // Kiểm tra xem người dùng hiện tại có quyền thêm Person vào family này hay
                // không
                if (family.getOwner().equals(user)) {
                    // Tạo mới đối tượng Person từ thông tin trong PersonRequestDTO và family đã tìm
                    // thấy

                    Person person = new Person();
                    person.setGender(personRequestDTO.getGender());
                    person.setName(personRequestDTO.getName());
                    person.setFamily(family);
                    personRepo.save(person);
                    // Các trường và quan hệ khác nếu có

                    Person spouse = personRepo.findById(personRequestDTO.getFromId()).get();
                    List<Child> children = spouse.getChildren();

                    for (Child child : children) {
                        Parent relation = new Parent();
                        Person c = child.getChild();

                        relation.setPerson(c);
                        relation.setParent(person);
                        relation.setType(RelationshipType.blood);
                        c.getParents().add(relation);
                        personRepo.save(c);

                        Child child2 = new Child();
                        child2.setChild(c);
                        child2.setPerson(person);
                        child2.setType(RelationshipType.blood);

                        person.getChildren().add(child2);

                    }

                    Spouse married1 = new Spouse();
                    Spouse married2 = new Spouse();
                    married1.setPerson(person);
                    married1.setSpouse(spouse);
                    married1.setType(RelationshipType.married);
                    person.getSpouses().add(married1);

                    married2.setPerson(spouse);
                    married2.setSpouse(person);
                    married2.setType(RelationshipType.married);
                    spouse.getSpouses().add(married2);
                    // Lưu Person vào database
                    personRepo.save(spouse);
                    personRepo.save(person);

                    // Trả về thông báo thành công nếu muốn
                    return ResponseEntity.ok("Person added successfully.");
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("You don't have permission to add a person to this family.");
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    @Transactional

    public ResponseEntity<?> deletePerson(@PathVariable("id") Long id) {
        try {
            Person person = personRepo.findById(id).get();

            spouseRepo.deleteAllBySpouse(person);
            siblingRepo.deleteAllBySibling(person);
            childRepo.deleteAllByChild(person);
            parentRepo.deleteAllByParent(person);
            personRepo.deleteById(id);
            return ResponseEntity.ok("Xoá thành công");

        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.badRequest().body("Xoá không thành công");
        }
    }
}

@Data
class FamilyRequestDTO {

    private String familyName;
    private String address;

    // Getters and setters
}

@Data
class PersonRequestDTO {

    private Gender gender;
    private String name;
    private Long fromId;
    private Long familyId;
    // Các trường và quan hệ khác nếu có

    // Getters and setters
}