package com.example.resourceops.recommendation.repository;

import com.example.resourceops.recommendation.entity.ResourceRecommendation;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRecommendationRepository extends JpaRepository<ResourceRecommendation, Long> {

}