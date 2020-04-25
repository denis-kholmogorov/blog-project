package main.DTOEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettingsDto
{
     private boolean MULTIUSER_MODE;

     private boolean POST_PREMODERATION;

     private boolean STATISTICS_IS_PUBLIC;
 }
