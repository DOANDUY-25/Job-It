package vn.duy.jobIT.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.duy.jobIT.domain.Company;
import vn.duy.jobIT.domain.Job;
import vn.duy.jobIT.domain.Skill;
import vn.duy.jobIT.domain.res.ResultPaginationResponse;
import vn.duy.jobIT.domain.res.job.CreatedJobResponse;
import vn.duy.jobIT.domain.res.job.UpdatedJobResponse;
import vn.duy.jobIT.repository.CompanyRepository;
import vn.duy.jobIT.repository.JobRepository;
import vn.duy.jobIT.repository.SkillRepository;
import vn.duy.jobIT.util.convert.JobConvert;
import vn.duy.jobIT.util.error.InvalidRequestException;
import vn.duy.jobIT.util.error.ResourceNotFoundException;
import vn.duy.jobIT.util.response.FormatResultPagaination;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;

    public CreatedJobResponse create(Job job){
        if(job.getSkills() != null){
            List<Long> reqSkills = job.getSkills()
                    .stream().map(Skill::getId)
                    .collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            job.setSkills(dbSkills);
        }

        if(job.getCompany() != null){
            Optional<Company> companyOptional = this.companyRepository.findById(job.getCompany().getId());
            if(companyOptional.isEmpty()) {
                throw new ResourceNotFoundException("Company", "id", job.getCompany().getId());
            }
            job.setCompany(companyOptional.get());
        }

        Job currentJob = this.jobRepository.save(job);
        return JobConvert.convertToResCreatedJobRes(currentJob);
    }

    public UpdatedJobResponse update(Job job){
        if(job.getId() == null){
            throw new InvalidRequestException("Job ID is required");
        }
        Job jobInDB = this.fetchJobById(job.getId());

        // Update basic fields
        jobInDB.setName(job.getName());
        jobInDB.setLocation(job.getLocation());
        jobInDB.setSalary(job.getSalary());
        jobInDB.setQuantity(job.getQuantity());
        jobInDB.setLevel(job.getLevel());
        jobInDB.setDescription(job.getDescription());
        jobInDB.setStartDate(job.getStartDate());
        jobInDB.setEndDate(job.getEndDate());
        jobInDB.setActive(job.isActive());

        if(job.getSkills() != null){
            List<Long> reqSkills = job.getSkills()
                    .stream().map(Skill::getId)
                    .collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            jobInDB.setSkills(dbSkills);
        }

        if(job.getCompany() != null){
            Optional<Company> companyOptional = this.companyRepository.findById(job.getCompany().getId());
            if(companyOptional.isEmpty()) {
                throw new ResourceNotFoundException("Company", "id", job.getCompany().getId());
            }
            jobInDB.setCompany(companyOptional.get());
        }

        Job currentJob = this.jobRepository.save(jobInDB);
        return JobConvert.convertToResUpdatedJobRes(currentJob);
    }

    public void delete(Long id){
        if(!this.jobRepository.existsById(id)){
            throw new ResourceNotFoundException("Job", "id", id);
        }
        this.jobRepository.deleteById(id);
    }

    public Job fetchJobById(Long id){
        return this.jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", id));
    }

    public ResultPaginationResponse fetchAllJob(Specification<Job> spec, Pageable pageable){
        Page<Job> jobPage = this.jobRepository.findAll(spec, pageable);
        return FormatResultPagaination.createPaginationResponse(jobPage);
    }
}
