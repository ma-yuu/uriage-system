package com.example.uriage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.uriage.model.Uriage;

public interface UriageRepository extends JpaRepository<Uriage, Long> {

    // 日付でソート
    List<Uriage> findAllByOrderByDateAsc();
    List<Uriage> findAllByOrderByDateDesc();

    // 単価でソート
    List<Uriage> findAllByOrderByPriceAsc();
    List<Uriage> findAllByOrderByPriceDesc();

    // 数量でソート
    List<Uriage> findAllByOrderByQuantityAsc();
    List<Uriage> findAllByOrderByQuantityDesc();
}

