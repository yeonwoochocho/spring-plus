package org.example.expert.domain.todo.repository;


import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long>, TodoRepositoryCustom {

    // weather, startDate, endDate 조건을 반영한 JPQL 쿼리
    @Query("SELECT t FROM Todo t WHERE " +
            "(COALESCE(:weather, null) IS NULL OR t.weather = :weather) AND " +
            "(COALESCE(:startDate, null) IS NULL OR t.modifiedAt >= :startDate) AND " +
            "(COALESCE(:endDate, null) IS NULL OR t.modifiedAt <= :endDate) " +
            "ORDER BY t.modifiedAt DESC")
    Page<Todo> findByConditions(@Param("weather") String weather,
                                @Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate,
                                PageRequest pageRequest);

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user WHERE t.id = :id")
    Optional<Todo> findByIdWithUser(long id);
}
