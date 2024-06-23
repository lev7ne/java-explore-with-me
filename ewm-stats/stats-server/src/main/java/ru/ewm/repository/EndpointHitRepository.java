package ru.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.ewm.dto.ViewStats;
import ru.ewm.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {
    @Query("select new ru.ewm.dto.ViewStats(" +
            "hits.app, hits.uri, " +
            "CASE WHEN ?4 = true THEN COUNT(distinct hits.ip) ELSE COUNT(hits.ip) END) " +
            "from EndpointHit hits " +
            "where hits.timestamp between ?1 and ?2 " +
            "and (?3 is null or hits.uri in ?3) " +
            "group by hits.app, hits.uri " +
            "order by CASE WHEN ?4 = true THEN COUNT(distinct hits.ip) ELSE COUNT(hits.ip) END desc")
    List<ViewStats> readViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);

    @Query("select new ru.ewm.dto.ViewStats(" +
            "hits.app, hits.uri, " +
            "CASE WHEN ?2 = true THEN COUNT(distinct hits.ip) ELSE COUNT(hits.ip) END) " +
            "from EndpointHit hits " +
            "where (?1 is null or hits.uri in ?1) " +
            "group by hits.app, hits.uri " +
            "order by CASE WHEN ?2 = true THEN COUNT(distinct hits.ip) ELSE COUNT(hits.ip) END desc")
    List<ViewStats> readViewStats(List<String> uris, boolean unique);

//    @Query("select new ru.ewm.dto.ViewStats(" +
//            "hits.app, hits.uri, " +
//            "CASE WHEN ?4 = true THEN COUNT(distinct hits.ip) ELSE COUNT(hits.ip) END) " +
//            "from EndpointHit hits " +
//            "where (?1 is null or hits.timestamp >= ?1) " +
//            "and (?2 is null or hits.timestamp <= ?2) " +
//            "and (?3 is null or hits.uri in ?3) " +
//            "group by hits.app, hits.uri " +
//            "order by CASE WHEN ?4 = true THEN COUNT(distinct hits.ip) ELSE COUNT(hits.ip) END desc")
//    List<ViewStats> readViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
