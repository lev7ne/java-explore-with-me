package ru.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.ewm.dto.EndpointHit;
import ru.ewm.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long>, JpaSpecificationExecutor<ViewStats> {
    @Query("select new ru.ewm.dto.ViewStats(" +
            "hits.app, hits.uri, COUNT(hits.ip)) " +
            "from EndpointHit hits " +
            "where hits.timestamp between ?1 and ?2 " +
            "group by hits.app, hits.uri " +
            "order by COUNT(hits.ip) desc")
    List<ViewStats> readAllStats(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.ewm.dto.ViewStats(" +
            "hits.app, hits.uri, COUNT(hits.ip)) " +
            "from EndpointHit hits " +
            "where hits.timestamp between ?1 and ?2 " +
            "and hits.uri in ?3 " +
            "group by hits.app, hits.uri " +
            "order by COUNT(hits.ip) desc")
    List<ViewStats> readStatsWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.ewm.dto.ViewStats(" +
            "hits.app, hits.uri, COUNT(distinct hits.ip)) " +
            "from EndpointHit hits " +
            "where hits.timestamp between ?1 and ?2 " +
            "group by hits.app, hits.uri " +
            "order by COUNT(distinct hits.ip) desc ")
    List<ViewStats> readStatsWithUniqueViews(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.ewm.dto.ViewStats(" +
            "hits.app, hits.uri, COUNT(distinct hits.ip)) " +
            "from EndpointHit hits " +
            "where hits.timestamp between ?1 and ?2 " +
            "and hits.uri IN ?3 " +
            "group by hits.app, hits.uri " +
            "order by COUNT(distinct hits.ip) desc")
    List<ViewStats> readStatsWithUrisAndUniqueViews(LocalDateTime start, LocalDateTime end, List<String> uris);
}
