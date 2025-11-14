package com.greeceri.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.greeceri.store.models.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{

}
