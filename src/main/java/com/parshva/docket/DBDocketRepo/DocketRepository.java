package com.parshva.docket.DBDocketRepo;

import com.parshva.docket.DBDocket.Docket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocketRepository extends JpaRepository<Docket, Long> {
}

