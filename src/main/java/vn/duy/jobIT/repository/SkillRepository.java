package vn.duy.jobIT.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.duy.jobIT.domain.Skill;

import java.util.List;

@Repository
public interface SkillRepository extends
        JpaRepository<Skill, Long>,
        JpaSpecificationExecutor<Skill> {
    boolean existsByName(String name);
    List<Skill> findByIdIn(List<Long> id);
}
