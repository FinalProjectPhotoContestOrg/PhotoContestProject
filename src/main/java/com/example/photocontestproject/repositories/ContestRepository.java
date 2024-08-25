package com.example.photocontestproject.repositories;

import com.example.photocontestproject.models.Contest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestRepository extends JpaRepository<Contest, Integer> {
}
