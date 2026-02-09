package vn.duy.jobIT.service;

import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.duy.jobIT.domain.Job;
import vn.duy.jobIT.domain.Resume;
import vn.duy.jobIT.domain.User;
import vn.duy.jobIT.domain.res.ResultPaginationResponse;
import vn.duy.jobIT.domain.res.resume.CreatedResumeResponse;
import vn.duy.jobIT.domain.res.resume.UpdatedResumeResponse;
import vn.duy.jobIT.repository.JobRepository;
import vn.duy.jobIT.repository.ResumeRepository;
import vn.duy.jobIT.repository.UserRepository;
import vn.duy.jobIT.util.convert.ResumeConvert;
import vn.duy.jobIT.util.error.InvalidRequestException;
import vn.duy.jobIT.util.error.ResourceNotFoundException;
import vn.duy.jobIT.util.response.FormatResultPagaination;
import vn.duy.jobIT.util.security.SecurityUtils;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final FilterParser filterParser;
    private final FilterSpecificationConverter filterSpecificationConverter;

    public CreatedResumeResponse create(Resume resume){
        // Validate user exists
        if(resume.getUser() == null || resume.getUser().getId() == null){
            throw new InvalidRequestException("User ID is required");
        }
        User user = this.userRepository.findById(resume.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", resume.getUser().getId()));
        
        // Validate job exists
        if(resume.getJob() == null || resume.getJob().getId() == null){
            throw new InvalidRequestException("Job ID is required");
        }
        Job job = this.jobRepository.findById(resume.getJob().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", resume.getJob().getId()));
        
        resume.setUser(user);
        resume.setJob(job);
        
        return ResumeConvert.convertToResCreatedResumeRes(this.resumeRepository.save(resume));
    }

    public Resume fetchResumeById(Long id) {
        return this.resumeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume", "id", id));
    }

    public UpdatedResumeResponse update(Resume resume){
        Resume currentResume = this.fetchResumeById(resume.getId());
        currentResume.setStatus(resume.getStatus());
        return ResumeConvert.convertToResUpdatedResumeRes(this.resumeRepository.save(currentResume));
    }

    public void delete(Long id) {
        Resume currentResume = this.fetchResumeById(id);
        this.resumeRepository.deleteById(currentResume.getId());
    }

    public ResultPaginationResponse fetchAllResume(Specification<Resume> spec, Pageable pageable){
        Page<Resume> resumePage = this.resumeRepository.findAll(spec, pageable);
        return FormatResultPagaination.createPaginateResumeRes(resumePage);
    }

    public ResultPaginationResponse fetchResumeByUser(Pageable pageable){
        String email = SecurityUtils.getCurrentUserLogin().isPresent()
                ? SecurityUtils.getCurrentUserLogin().get()
                : "";
        FilterNode node = filterParser.parse("email='" + email + "'");
        FilterSpecification<Resume> spec = filterSpecificationConverter.convert(node);

        Page<Resume> resumePage = this.resumeRepository.findAll(spec, pageable);
        return FormatResultPagaination.createPaginateResumeRes(resumePage);
    }
}
