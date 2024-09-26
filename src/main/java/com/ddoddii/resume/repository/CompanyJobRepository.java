package com.ddoddii.resume.repository;

import com.ddoddii.resume.model.company.CompanyJob;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyJobRepository extends JpaRepository<CompanyJob, Long> {
    CompanyJob findCompanyJobById(long id);

    List<CompanyJob> findByDepartment(String department);


}
