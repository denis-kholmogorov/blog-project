package main.models;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaptchaCodesRepository extends CrudRepository<CaptchaCodes, Integer> {
}
