package com.viltgroup.statusmetro.linestatus.service;

import com.viltgroup.statusmetro.linestatus.client.dto.CcrAsset;
import com.viltgroup.statusmetro.linestatus.client.dto.CcrConcessao;
import com.viltgroup.statusmetro.linestatus.client.dto.CcrEnvelope;
import com.viltgroup.statusmetro.linestatus.client.dto.CcrLinha;
import com.viltgroup.statusmetro.linestatus.client.dto.CcrStatusLinha;
import com.viltgroup.statusmetro.linestatus.config.LineStatusProperties;
import com.viltgroup.statusmetro.linestatus.domain.Line;
import com.viltgroup.statusmetro.linestatus.domain.LineStatus;
import com.viltgroup.statusmetro.linestatus.domain.Operator;
import com.viltgroup.statusmetro.linestatus.domain.StatusSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Transforms a successful upstream {@link CcrEnvelope} into the normalized {@link StatusSnapshot}.
 * Drops malformed lines (and flags the snapshot {@code partial}), validates colors, resolves asset
 * URLs, classifies status, and orders lines disrupted-first.
 */
@Service
public class NormalizationService {

    private static final Logger log = LoggerFactory.getLogger(NormalizationService.class);
    private static final Pattern HEX_COLOR = Pattern.compile("^#[0-9A-Fa-f]{6}$");

    private final StatusCategoryMapper categoryMapper;
    private final LineStatusProperties properties;

    public NormalizationService(StatusCategoryMapper categoryMapper, LineStatusProperties properties) {
        this.categoryMapper = categoryMapper;
        this.properties = properties;
    }

    public StatusSnapshot normalize(CcrEnvelope envelope, LocalDateTime fetchedAt) {
        boolean[] partial = {false};
        List<Operator> operators = new ArrayList<>();
        List<Line> allLines = new ArrayList<>();

        List<CcrConcessao> concessoes = envelope.data().concessoes();
        if (concessoes != null) {
            for (CcrConcessao concessao : concessoes) {
                List<Line> lines = new ArrayList<>();
                List<CcrLinha> linhas = concessao.linhas() == null ? List.of() : concessao.linhas();
                for (CcrLinha linha : linhas) {
                    Line line = toLine(linha, concessao.uid(), partial);
                    if (line != null) {
                        lines.add(line);
                        allLines.add(line);
                    }
                }
                lines.sort((a, b) -> Integer.compare(numberValue(a.number()), numberValue(b.number())));
                operators.add(new Operator(
                        concessao.uid(),
                        concessao.nome(),
                        concessao.estados(),
                        resolveAsset(concessao.logo()),
                        lines
                ));
            }
        }

        LocalDateTime lastUpdated = parseTimestamp(envelope.data().dataAtualizacao());
        boolean available = !allLines.isEmpty();
        List<Line> ordered = LineOrdering.order(allLines);

        return new StatusSnapshot(lastUpdated, fetchedAt, false, available, partial[0], operators, ordered);
    }

    private Line toLine(CcrLinha linha, String operatorUid, boolean[] partial) {
        if (linha == null || isBlank(linha.uid()) || isBlank(linha.numero()) || isBlank(linha.nome())) {
            log.debug("Dropping malformed line for operator {}: {}", operatorUid, linha);
            partial[0] = true;
            return null;
        }
        CcrStatusLinha s = linha.statusLinha();
        String code = s == null ? null : s.codigo();
        String label = s == null ? null : s.status();
        String description = s == null ? null : emptyToNull(s.descricao());
        LineStatus status = new LineStatus(code, label, description, categoryMapper.map(code));
        return new Line(
                linha.uid(),
                linha.numero(),
                linha.nome(),
                validColor(linha.corRgb()),
                resolveAsset(linha.icone()),
                status,
                operatorUid
        );
    }

    private String validColor(String corRgb) {
        if (corRgb != null && HEX_COLOR.matcher(corRgb.trim()).matches()) {
            return corRgb.trim();
        }
        return null;
    }

    private String resolveAsset(CcrAsset asset) {
        if (asset == null || isBlank(asset.path())) {
            return null;
        }
        String path = asset.path().trim();
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return path;
        }
        String base = properties.assetBaseUrl();
        if (isBlank(base)) {
            return path;
        }
        String normalizedBase = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        return normalizedBase + normalizedPath;
    }

    private LocalDateTime parseTimestamp(String value) {
        if (isBlank(value)) {
            return null;
        }
        try {
            return LocalDateTime.parse(value.trim());
        } catch (DateTimeParseException ex) {
            log.warn("Unparseable dataAtualizacao '{}'", value);
            return null;
        }
    }

    private static int numberValue(String number) {
        try {
            return Integer.parseInt(number.trim());
        } catch (RuntimeException ex) {
            return Integer.MAX_VALUE;
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String emptyToNull(String value) {
        return isBlank(value) ? null : value;
    }
}
