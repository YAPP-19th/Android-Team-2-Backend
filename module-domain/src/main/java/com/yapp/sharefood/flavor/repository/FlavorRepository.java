package com.yapp.sharefood.flavor.repository;

import com.yapp.sharefood.flavor.domain.Flavor;
import com.yapp.sharefood.flavor.domain.FlavorType;
import com.yapp.sharefood.flavor.repository.query.FlavorQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlavorRepository extends JpaRepository<Flavor, Long>, FlavorQueryRepository {
    List<Flavor> findByFlavorTypeIsIn(List<FlavorType> flavorTypes);
}
