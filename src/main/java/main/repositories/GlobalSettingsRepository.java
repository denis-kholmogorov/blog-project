package main.repositories;

import main.model.GlobalSettings;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GlobalSettingsRepository extends CrudRepository<GlobalSettings, Integer>
{
    @Query(value = "SELECT gs FROM GlobalSettings gs")
    List<GlobalSettings> findAllSettings();
}
