package com.yapp.sharefood.flavor.repository;

import com.yapp.sharefood.flavor.domain.Flavor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlavorRepository extends JpaRepository<Flavor, Long> {
}
