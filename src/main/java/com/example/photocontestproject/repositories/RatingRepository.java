package com.example.photocontestproject.repositories;

import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.models.Rating;
import com.example.photocontestproject.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface RatingRepository extends JpaRepository<Rating, Integer> {

    Set<Rating> findByEntryId(int entryId);

    Set<Rating> findAll(Specification<Rating> specification, Pageable pageable);

    Optional<Rating> findByJurorAndEntry(User juror, Entry entry);
}
