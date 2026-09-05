package com.example.shortlink.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.shortlink.model.ShortLinkEntity;

public interface ShortLinkJpaRepository
          extends JpaRepository<ShortLinkEntity, Long> {

      Optional<ShortLinkEntity> findByCode(String code);
  }
