package com.example.demo.repository;
import com.example.demo.entity.ListaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListaJpaRepository extends JpaRepository<ListaEntity, Long> {}
