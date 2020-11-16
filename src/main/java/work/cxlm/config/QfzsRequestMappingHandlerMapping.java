package work.cxlm.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import work.cxlm.model.support.QfzsConst;
import work.cxlm.utils.QfzsUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * created 2020/11/15 13:46
 *
 * @author ryanwang
 * @author Chiru
 */
@Slf4j
public class QfzsRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    private final Set<String> blackPatterns = new HashSet<>();

    private final PathMatcher pathMatcher;

    private final QfzsProperties qfzsProperties;

    public QfzsRequestMappingHandlerMapping(QfzsProperties qfzsProperties) {
        this.qfzsProperties = qfzsProperties;
        initBlackPatterns();
        pathMatcher = new AntPathMatcher();
    }

    private void initBlackPatterns() {
        String uploadUrlPattern = QfzsUtils.ensureBoth(qfzsProperties.getUploadUrlPrefix(), QfzsConst.URL_SEPARATOR) + "**";

        blackPatterns.add("/font/js/**");
        blackPatterns.add("/font/images/**");
        blackPatterns.add("/font/fonts/**");
        blackPatterns.add("/font/css/**");
        blackPatterns.add("/font/assets/**");
        blackPatterns.add("/font/csrf");
        blackPatterns.add("/swagger-ui.html");
        blackPatterns.add("/webjars/**");
        blackPatterns.add(uploadUrlPattern);
        // blackPatterns.add(adminPathPattern);
    }
}