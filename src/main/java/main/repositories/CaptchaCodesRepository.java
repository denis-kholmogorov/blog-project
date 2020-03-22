package main.repositories;

import main.model.CaptchaCodes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface CaptchaCodesRepository extends CrudRepository<CaptchaCodes, Integer>
{
    @Transactional(readOnly = true)
    @Query(value = "Select * from captcha_codes c where adddate(c.time, interval :countTime hour) < now()", nativeQuery = true)
    List<CaptchaCodes> findAllOlderCodes(String countTime);

    //@Query("SELECT c FROM CaptchaCodes c where c.code = :code")
    Optional<CaptchaCodes> findByCode(String code);



}
