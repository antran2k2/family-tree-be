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
@RequestMapping("/api/family")
public class FamilyController {

    @Autowired
    FamilyRepo familyRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    PersonRepo personRepo;

    @GetMapping("/getList")
    public ResponseEntity<?> getListFamily() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginedUser = (UserDetailsImpl) (authentication.getPrincipal());

        AppUser user = userRepo.findById(loginedUser.getId()).orElse(null);
        if (user != null) {
            List<Family> families = familyRepo.findAllByOwner(user);
            List<FamilyDTO> familyDTOs = families.stream()
                    .map(family -> new FamilyDTO(family.getFamilyName(), family.getAddress(), family.getIdFamily(),
                            family.getMembers().size()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(familyDTOs);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getFamily")
    public ResponseEntity<?> getAllMembers(@RequestParam(value = "id", required = false) Long id) {
        System.out.println(12321);
        Family family = familyRepo.findById(id).orElse(null);
        if (family != null) {
            List<Person> res = personRepo.findAllByFamily(family);

            List<PersonDTO> personDTOs = new ArrayList<>();
            for (Person person : res) {
                PersonDTO personDTO = new PersonDTO();
                personDTO.setId(person.getIdPerson());
                personDTO.setName(person.getName());
                personDTO.setGender(person.getGender());
                personDTO.setAlive(person.isAlive());
                personDTO.setDob(person.getDob());
                personDTO.setDod(person.getDod());
                personDTO.setDetails(person.getDetails());
                personDTO.setParents(convertRelationships(person.getParents()));
                personDTO.setSiblings(convertRelationships(person.getSiblings()));
                personDTO.setSpouses(convertRelationships(person.getSpouses()));
                personDTO.setChildren(convertRelationships(person.getChildren()));
                personDTOs.add(personDTO);
            }

            return ResponseEntity.ok(personDTOs);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private List<RelationshipDTO> convertRelationships(List<?> relationships) {
        List<RelationshipDTO> relationshipDTOs = new ArrayList<>();
        for (Object relationship : relationships) {
            RelationshipDTO relationshipDTO = new RelationshipDTO();
            if (relationship instanceof Parent) {
                relationshipDTO.setId(((Parent) relationship).getParent().getIdPerson());
                relationshipDTO.setName(((Parent) relationship).getParent().getName());
                relationshipDTO.setType(((Parent) relationship).getType());
            } else if (relationship instanceof Sibling) {
                relationshipDTO.setId(((Sibling) relationship).getSibling().getIdPerson());
                relationshipDTO.setName(((Sibling) relationship).getSibling().getName());
                relationshipDTO.setType(((Sibling) relationship).getType());
            } else if (relationship instanceof Spouse) {
                relationshipDTO.setId(((Spouse) relationship).getSpouse().getIdPerson());
                relationshipDTO.setName(((Spouse) relationship).getSpouse().getName());
                relationshipDTO.setType(((Spouse) relationship).getType());
            } else if (relationship instanceof Child) {
                relationshipDTO.setId(((Child) relationship).getChild().getIdPerson());
                relationshipDTO.setName(((Child) relationship).getChild().getName());
                relationshipDTO.setType(((Child) relationship).getType());
            }
            relationshipDTOs.add(relationshipDTO);
        }
        return relationshipDTOs;
    }

    @PostMapping("/addFamily")
    public ResponseEntity<?> addFamily(@RequestBody FamilyRequestDTO familyRequestDTO) {
        System.out.println(familyRequestDTO);
        // Lấy thông tin người dùng đã xác thực từ SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginedUser = (UserDetailsImpl) authentication.getPrincipal();

        // Tìm người dùng từ database bằng ID đã xác thực
        AppUser user = userRepo.findById(loginedUser.getId()).orElse(null);

        if (user != null) {
            // Tạo mới đối tượng Family từ thông tin trong FamilyRequestDTO và người dùng
            // hiện tại
            Family family = new Family();
            family.setFamilyName(familyRequestDTO.getFamilyName());
            family.setAddress(familyRequestDTO.getAddress());
            family.setOwner(user);
            // Lưu family vào database
            familyRepo.save(family);
            Person person = new Person();
            person.setName("Root");
            person.setGender(Gender.MALE);
            person.setDetails("something");
            person.setFamily(family);
            personRepo.save(person);

            FamilyDTO res = new FamilyDTO(family.getFamilyName(), family.getAddress(), family.getIdFamily(), 1);

            // Trả về thông báo thành công nếu muốn
            return ResponseEntity.ok(res);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/editFamily/{familyId}")
    public ResponseEntity<String> editFamily(@PathVariable Long familyId,
            @RequestBody FamilyRequestDTO familyRequestDTO) {
        // Lấy thông tin người dùng đã xác thực từ SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginedUser = (UserDetailsImpl) authentication.getPrincipal();

        // Tìm người dùng từ database bằng ID đã xác thực
        AppUser user = userRepo.findById(loginedUser.getId()).orElse(null);

        if (user != null) {
            // Tìm family cần chỉnh sửa bằng ID
            Optional<Family> optionalFamily = familyRepo.findById(familyId);
            if (optionalFamily.isPresent()) {
                Family family = optionalFamily.get();

                // Kiểm tra xem người dùng hiện tại có quyền chỉnh sửa family hay không
                if (family.getOwner().equals(user)) {
                    // Cập nhật thông tin family từ FamilyRequestDTO
                    family.setFamilyName(familyRequestDTO.getFamilyName());
                    family.setAddress(familyRequestDTO.getAddress());

                    // Lưu family đã chỉnh sửa vào database
                    familyRepo.save(family);

                    // Trả về thông báo thành công nếu muốn
                    return ResponseEntity.ok("Family edited successfully.");
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("You don't have permission to edit this family.");
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/deleteFamily/{familyId}")
    public ResponseEntity<String> deleteFamily(@PathVariable Long familyId) {
        // Lấy thông tin người dùng đã xác thực từ SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginedUser = (UserDetailsImpl) authentication.getPrincipal();

        // Tìm người dùng từ database bằng ID đã xác thực
        AppUser user = userRepo.findById(loginedUser.getId()).orElse(null);

        if (user != null) {
            // Tìm family cần xóa bằng ID
            Optional<Family> optionalFamily = familyRepo.findById(familyId);
            if (optionalFamily.isPresent()) {
                Family family = optionalFamily.get();

                // Kiểm tra xem người dùng hiện tại có quyền xóa family hay không
                if (family.getOwner().equals(user)) {
                    // Xóa family khỏi database
                    familyRepo.delete(family);

                    // Trả về thông báo thành công nếu muốn
                    return ResponseEntity.ok("Family deleted successfully.");
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("You don't have permission to delete this family.");
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
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
class PersonDTO {
    private Long id;
    private String name;
    private Gender gender;
    private Date dob;
    private Date dod;
    private boolean alive;
    private String details;
    private List<RelationshipDTO> parents;
    private List<RelationshipDTO> siblings;
    private List<RelationshipDTO> spouses;
    private List<RelationshipDTO> children;

}