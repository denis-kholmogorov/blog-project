package main.repositories;

import main.model.GlobalSettings;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@Repository
public interface GlobalSettingsRepository extends CrudRepository<GlobalSettings, Integer>
{

    Optional<GlobalSettings> findSettingByCode(String code);

    @Query(value = "SELECT gs FROM GlobalSettings gs")
    List<GlobalSettings> findAllSettings();

    @Cacheable("findOnlyCodeAndValue")
    default Map<String, Boolean> findOnlyCodeAndValue(){
        return findAllSettings().stream().collect(Collectors.toMap(
                GlobalSettings::getCode,
                GlobalSettings::isValue
        ));
    }

    Optional<GlobalSettings> findByCode(String code);
}
