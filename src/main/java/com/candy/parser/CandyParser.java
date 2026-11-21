package com.candy.parser;

import com.candy.model.Sweet;
import java.nio.file.Path;
import java.util.List;

public interface CandyParser {

    String getName();

    List<Sweet> parse(Path xmlFile);
}