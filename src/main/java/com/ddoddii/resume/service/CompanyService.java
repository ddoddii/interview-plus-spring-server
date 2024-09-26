package com.ddoddii.resume.service;

import com.ddoddii.resume.dto.company.CompanyDeptDTO;
import com.ddoddii.resume.dto.company.CompanyJobAndDeptDTO;
import com.ddoddii.resume.dto.company.CompanyJobDTO;
import com.ddoddii.resume.dto.company.CompanyNameDTO;
import com.ddoddii.resume.error.errorcode.CompanyErrorCode;
import com.ddoddii.resume.error.exception.CompanyNotFoundException;
import com.ddoddii.resume.model.company.CompanyDept;
import com.ddoddii.resume.model.company.CompanyJob;
import com.ddoddii.resume.model.company.CompanyName;
import com.ddoddii.resume.repository.CompanyDeptRepository;
import com.ddoddii.resume.repository.CompanyJobRepository;
import com.ddoddii.resume.repository.CompanyNameRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompanyService {
    // 기업 이름
    private final CompanyNameRepository companyNameRepository;
    // 직무
    private final CompanyDeptRepository companyDeptRepository;
    // 직군
    private final CompanyJobRepository companyJobRepository;

    private final StorageService storageService;

    public List<CompanyNameDTO> getAllCompanies() {
        List<CompanyName> companyNames = companyNameRepository.findAll();
        return companyNames.stream()
                .map(companyName -> new CompanyNameDTO(companyName.getId(), companyName.getName(),
                        companyName.getImageUrl()))
                .collect(Collectors.toList());
    }


    public List<CompanyDeptDTO> getCompanyDepts() {
        List<CompanyDept> companyDepts = companyDeptRepository.findAll();
        return companyDepts.stream()
                .map(companyDept -> new CompanyDeptDTO(companyDept.getId(), companyDept.getDept()))
                .collect(Collectors.toList());
    }

    public List<CompanyJobDTO> getCompanyJobs(long departmentId) {
        CompanyDept dept = companyDeptRepository.findById(departmentId);
        List<CompanyJob> companyJobs = companyJobRepository.findByDepartment(dept.getDept());
        return companyJobs.stream()
                .map(companyJob -> new CompanyJobDTO(companyJob.getId(), companyJob.getJob()))
                .collect(Collectors.toList());
    }

    public List<CompanyJobAndDeptDTO> getCompanyDeptAndJobs() {
        List<CompanyJob> companyJobs = companyJobRepository.findAll();

        Map<Integer, Map.Entry<String, List<CompanyJobDTO>>> deptIdToJobsMap = companyJobs.stream()
                .collect(Collectors.groupingBy(
                        CompanyJob::getDeptId,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                jobs -> {
                                    String departmentName = jobs.get(0).getDepartment();
                                    List<CompanyJobDTO> jobDTOs = jobs.stream()
                                            .map(companyJob -> new CompanyJobDTO(companyJob.getId(),
                                                    companyJob.getJob()))
                                            .collect(Collectors.toList());
                                    return Map.entry(departmentName, jobDTOs);
                                }
                        )
                ));

        return deptIdToJobsMap.entrySet().stream()
                .map(entry -> new CompanyJobAndDeptDTO(entry.getKey(), entry.getValue().getKey(),
                        entry.getValue().getValue()))
                .collect(Collectors.toList());

    }

    public CompanyNameDTO getCompanyImage(long companyId) {
        CompanyName company = companyNameRepository.findFirstById(companyId);
        if (company == null) {
            throw new CompanyNotFoundException(CompanyErrorCode.COMPANY_NOT_FOUND);
        }

        String imageUrl = company.getImageUrl();
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            imageUrl = storageService.getImageUrl(company.getId() + ".png");
        }

        return new CompanyNameDTO(company.getId(), company.getName(), imageUrl);
    }
}
