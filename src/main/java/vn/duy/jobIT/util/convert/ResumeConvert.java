package vn.duy.jobIT.util.convert;

import vn.duy.jobIT.domain.Job;
import vn.duy.jobIT.domain.Resume;
import vn.duy.jobIT.domain.Skill;
import vn.duy.jobIT.domain.res.resume.CreatedResumeResponse;
import vn.duy.jobIT.domain.res.resume.FetchResumeResponse;
import vn.duy.jobIT.domain.res.resume.UpdatedResumeResponse;

import java.util.List;
import java.util.stream.Collectors;

public class ResumeConvert {
    public static CreatedResumeResponse convertToResCreatedResumeRes(Resume resume){
        CreatedResumeResponse res = new CreatedResumeResponse();

        res.setId(resume.getId());
        res.setCreatedDate(resume.getCreatedDate());
        res.setCreatedBy(resume.getCreatedBy());

        return res;
    }

    public static UpdatedResumeResponse convertToResUpdatedResumeRes(Resume resume){
        UpdatedResumeResponse res = new UpdatedResumeResponse();

        res.setLastModifiedDate(resume.getLastModifiedDate());
        res.setLastModifiedBy(resume.getLastModifiedBy());

        return res;
    }

    public static FetchResumeResponse convertToResFetchResumeRes(Resume resume){
        FetchResumeResponse res = new FetchResumeResponse();

        res.setId(resume.getId());
        res.setEmail(resume.getEmail());
        res.setUrl(resume.getUrl());
        res.setStatus(resume.getStatus());
        res.setLastModifiedDate(resume.getLastModifiedDate());
        res.setLastModifiedBy(resume.getLastModifiedBy());
        res.setCreatedDate(resume.getCreatedDate());
        res.setCreatedBy(resume.getCreatedBy());

        if(resume.getJob() != null){
            res.setCompanyName(resume.getJob().getCompany().getName());
        }

        res.setUser(new FetchResumeResponse.UserResume(resume.getUser().getId(), resume.getUser().getName()));
        res.setJob(new FetchResumeResponse.JobResume(resume.getJob().getId(), resume.getJob().getName()));
        return res;
    }
}
