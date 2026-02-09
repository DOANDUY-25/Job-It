package vn.duy.jobIT.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.duy.jobIT.domain.Skill;
import vn.duy.jobIT.domain.res.ResultPaginationResponse;
import vn.duy.jobIT.repository.SkillRepository;
import vn.duy.jobIT.util.error.DuplicateResourceException;
import vn.duy.jobIT.util.error.ResourceNotFoundException;
import vn.duy.jobIT.util.response.FormatResultPagaination;

@RequiredArgsConstructor
@Service
@Transactional
public class SkillService {
    private final SkillRepository skillRepository;

    public Skill create(Skill skill){
        if(this.skillRepository.existsByName(skill.getName())){
            throw new DuplicateResourceException("Skill", "name", skill.getName());
        }
        return this.skillRepository.save(skill);
    }

    public Skill fetchSkillById(Long id) {
        return this.skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", "id", id));
    }

    public Skill update(Skill skill){
        Skill currentSkill = this.fetchSkillById(skill.getId());
        
        // Check if new name already exists (and it's not the current skill's name)
        if(skill.getName() != null && 
           !skill.getName().equals(currentSkill.getName()) && 
           this.skillRepository.existsByName(skill.getName())){
            throw new DuplicateResourceException("Skill", "name", skill.getName());
        }
        
        currentSkill.setName(skill.getName());
        return this.skillRepository.save(currentSkill);
    }

    public ResultPaginationResponse fetchAllSkill(Specification<Skill> spec, Pageable pageable){
        Page<Skill> skillPage = this.skillRepository.findAll(spec, pageable);
        return FormatResultPagaination.createPaginationResponse(skillPage);
    }

    public void deleteSkill(Long id) {
        Skill currentSkill = this.fetchSkillById(id);
        
        // Remove skill from all jobs
        currentSkill.getJobs().forEach(
                job -> job.getSkills().remove(currentSkill)
        );
        
        // Remove skill from all subscribers
        currentSkill.getSubscribers().forEach(
                subs -> subs.getSkills().remove(currentSkill)
        );
        
        this.skillRepository.delete(currentSkill);
    }
}
