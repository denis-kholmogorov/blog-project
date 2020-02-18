package main.services.ApiGeneralSevice;

import main.DTOEntity.InitDto;
import main.DTOEntity.TagDto;

import java.util.List;

public interface ApiGeneralService
{
    InitDto init();

    List<TagDto> tagBySearch(String query);
}
