package com.pfplaybackend.api.common.config.swagger;

import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.ErrorType;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * {@link ApiErrorCodes} 어노테이션을 읽어 Swagger 에러 응답을 자동 생성하는 커스터마이저.
 * <p>
 * DomainException enum의 errorCode, message, errorType을 기반으로
 * HTTP 상태코드별 그룹핑 → ApiResponse 생성 → Example(드롭다운) 자동 등록한다.
 * 에러 코드가 추가/수정되면 enum만 변경하면 Swagger 문서가 자동 반영된다.
 * </p>
 */
@Component
public class ApiErrorCodeCustomizer implements OperationCustomizer {

    private static final Map<ErrorType, Integer> STATUS_MAP = Map.of(
            ErrorType.BAD_REQUEST, 400,
            ErrorType.UNAUTHORIZED, 401,
            ErrorType.FORBIDDEN, 403,
            ErrorType.NOT_FOUND, 404,
            ErrorType.CONFLICT, 409
    );

    private static final Map<ErrorType, String> STATUS_DESCRIPTION = Map.of(
            ErrorType.BAD_REQUEST, "잘못된 요청",
            ErrorType.UNAUTHORIZED, "인증 실패",
            ErrorType.FORBIDDEN, "권한 부족 또는 접근 제한",
            ErrorType.NOT_FOUND, "리소스를 찾을 수 없음",
            ErrorType.CONFLICT, "리소스 충돌"
    );

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        ApiErrorCodes annotation = handlerMethod.getMethodAnnotation(ApiErrorCodes.class);
        if (annotation == null) {
            return operation;
        }

        List<DomainException> allErrors = Arrays.stream(annotation.value())
                .filter(Class::isEnum)
                .flatMap(clazz -> Arrays.stream(clazz.getEnumConstants()))
                .collect(Collectors.toList());

        // ErrorType(HTTP 상태)별 그룹핑
        Map<ErrorType, List<DomainException>> grouped = allErrors.stream()
                .collect(Collectors.groupingBy(DomainException::getErrorType, LinkedHashMap::new, Collectors.toList()));

        ApiResponses responses = operation.getResponses();
        if (responses == null) {
            responses = new ApiResponses();
            operation.setResponses(responses);
        }

        for (Map.Entry<ErrorType, List<DomainException>> entry : grouped.entrySet()) {
            ErrorType errorType = entry.getKey();
            List<DomainException> errors = entry.getValue();
            int statusCode = STATUS_MAP.get(errorType);
            String statusKey = String.valueOf(statusCode);

            // description: 에러코드 목록
            String description = STATUS_DESCRIPTION.get(errorType) + " — " +
                    errors.stream()
                            .map(e -> e.getErrorCode() + " (" + e.getMessage() + ")")
                            .collect(Collectors.joining(" | "));

            ApiResponse apiResponse = new ApiResponse().description(description);

            // content + examples
            MediaType mediaType = new MediaType();
            mediaType.schema(new Schema<>().$ref("#/components/schemas/ApiErrorResponse"));

            if (errors.size() == 1) {
                DomainException error = errors.get(0);
                Example example = createExample(statusCode, error);
                mediaType.addExamples(error.getErrorCode(), example);
            } else {
                for (DomainException error : errors) {
                    Example example = createExample(statusCode, error);
                    mediaType.addExamples(error.getErrorCode(), example);
                }
            }

            Content content = new Content();
            content.addMediaType("application/json", mediaType);
            apiResponse.setContent(content);

            responses.addApiResponse(statusKey, apiResponse);
        }

        return operation;
    }

    private Example createExample(int statusCode, DomainException error) {
        Map<String, Object> exampleValue = new LinkedHashMap<>();
        exampleValue.put("status", statusCode);
        exampleValue.put("errorCode", error.getErrorCode());
        exampleValue.put("message", error.getMessage());

        Example example = new Example();
        example.setSummary(error.getErrorCode());
        example.setDescription(error.getMessage());
        example.setValue(exampleValue);
        return example;
    }
}
