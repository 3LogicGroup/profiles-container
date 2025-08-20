package ru.graviton.profiles.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "path")
public class Path {
    private String path;
    private Path parentPath;
    private String directory;
    private String relativePath;
    @JsonIgnore
    private final Set<Path> children = new HashSet<>();


    public Path(String path, Path parentPath, String directory) {
        this.path = path;
        this.parentPath = parentPath;
        this.directory = directory;
        if (parentPath != null) {
            this.relativePath = path.replaceFirst(parentPath.getPath(), "");
        }

    }

    @Override
    public String toString() {
        return "Path{" +
                "path='" + path + '\'' +
                ", parentPath=" + parentPath +
                ", directory='" + directory + '\'' +
                '}';
    }
}