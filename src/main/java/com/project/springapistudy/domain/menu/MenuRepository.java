package com.project.springapistudy.domain.menu;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    Optional<Menu> findByMenuName(String menuName);
}
