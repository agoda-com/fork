package com.agoda.fork.teamcity;

import com.agoda.fork.stat.StatLoader;
import com.agoda.fork.stat.StatLoaderProvider;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class TeamCityStatLoaderProvider implements StatLoaderProvider {
    @Override
    public StatLoader create(String path) throws FileNotFoundException {
        TeamCityConfig config = parseConfig(path);
        return new TeamCityStatLoader(new TeamCityClient(config).teamCityService(), config);
    }

    private TeamCityConfig parseConfig(String path) throws FileNotFoundException {
        Yaml yaml = new Yaml();
        return yaml.loadAs(new FileReader(path), TeamCityConfig.class);
    }
}
